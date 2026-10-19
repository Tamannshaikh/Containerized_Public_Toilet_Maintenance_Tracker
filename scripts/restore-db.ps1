<#
.SYNOPSIS
    Automated Database Safe Restoration and Verification Script.
.DESCRIPTION
    Task 14: Infrastructure Provisioning & Reliability.
    Restores a PostgreSQL backup archive into an isolated verification database
    (toilet_tracker_verify_db) to prove backup recoverability without modifying
    or touching the live application database (toilet_tracker_db).
#>

param(
    [string]$BackupFile,
    [string]$ContainerName = "public-toilet-db",
    [string]$VerifyDbName = "toilet_tracker_verify_db",
    [string]$LiveDbName = "toilet_tracker_db",
    [string]$DbUser = $env:POSTGRES_USER
)

if (-not $DbUser) { $DbUser = "tracker_user" }

$ErrorActionPreference = "Stop"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "TASK 14: SAFE DATABASE RESTORATION & VERIFICATION" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# SAFETY GUARD 1: Prevent restoring into live database
if ($VerifyDbName -eq $LiveDbName) {
    Write-Error "SAFETY VIOLATION: Verification target matches live database '$LiveDbName'! Aborting to prevent data corruption."
}

# 1. Determine backup file to verify
if (-not $BackupFile) {
    $latestBackup = Get-ChildItem -Path "backups" -Filter "*.sql" | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    if (-not $latestBackup) {
        Write-Error "No backup archives found in 'backups/' directory. Run backup-db.ps1 first."
    }
    $BackupFile = $latestBackup.FullName
}

if (-not (Test-Path $BackupFile)) {
    Write-Error "Specified backup archive does not exist: $BackupFile"
}

Write-Host "[1/6] Selected backup archive: $(Split-Path $BackupFile -Leaf)" -ForegroundColor Green

# 2. Verify container is healthy
$containerStatus = docker inspect $ContainerName --format "{{.State.Status}}:{{.State.Health.Status}}" 2>$null
if ($containerStatus -notmatch "running") {
    Write-Error "Container '$ContainerName' is not active ($containerStatus)."
}
Write-Host "[2/6] Verified database container '$ContainerName' is active ($containerStatus)." -ForegroundColor Green

# 3. Copy backup archive into container
$containerTemp = "/tmp/restore_test.sql"
Write-Host "[3/6] Copying backup to container temporary staging: $containerTemp" -ForegroundColor Yellow
docker cp $BackupFile "${ContainerName}:${containerTemp}"

# 4. Create isolated verification database
Write-Host "[4/6] Initializing isolated verification database '$VerifyDbName'..." -ForegroundColor Yellow
docker exec $ContainerName psql -U $DbUser -d $LiveDbName -c "DROP DATABASE IF EXISTS $VerifyDbName;" | Out-Null
docker exec $ContainerName psql -U $DbUser -d $LiveDbName -c "CREATE DATABASE $VerifyDbName;"
if ($LASTEXITCODE -ne 0) {
    docker exec $ContainerName rm -f $containerTemp
    Write-Error "Failed to create verification database '$VerifyDbName'."
}

# 5. Restore backup into isolated verification database
Write-Host "[5/6] Restoring backup archive into '$VerifyDbName'..." -ForegroundColor Yellow
docker exec $ContainerName psql -U $DbUser -d $VerifyDbName -q -f $containerTemp
if ($LASTEXITCODE -ne 0) {
    Write-Warning "psql reported non-zero code during restore. Verifying restored tables..."
}

# 6. Verify restored data integrity
Write-Host "[6/6] Verifying restored schema and data integrity..." -ForegroundColor Yellow
$tablesOutput = docker exec $ContainerName psql -U $DbUser -d $VerifyDbName -c "\dt"
$ticketsOutput = docker exec $ContainerName psql -U $DbUser -d $VerifyDbName -c "SELECT id, ticket_number, title, priority, status FROM maintenance_tickets;"
$facilitiesCount = docker exec $ContainerName psql -U $DbUser -d $VerifyDbName -t -c "SELECT count(*) FROM toilet_facilities;"

Write-Host "`n--- RESTORED TICKETS IN VERIFICATION DATABASE ---" -ForegroundColor Cyan
Write-Host $ticketsOutput
Write-Host "-------------------------------------------------" -ForegroundColor Cyan

# Assert ticket TKT-20261019-9F7207 is present
if ($ticketsOutput -match "TKT-20261019-9F7207") {
    Write-Host "`nSUCCESS: Persisted ticket 'TKT-20261019-9F7207' successfully restored and verified in '$VerifyDbName'!" -ForegroundColor Green
} else {
    Write-Error "Restored data verification failed: Ticket 'TKT-20261019-9F7207' was not found in restored database."
}

# Clean up verification database and container temp file
Write-Host "`nCleaning up isolated verification database '$VerifyDbName'..." -ForegroundColor Yellow
docker exec $ContainerName psql -U $DbUser -d $LiveDbName -c "DROP DATABASE $VerifyDbName;" | Out-Null
docker exec $ContainerName rm -f $containerTemp
Write-Host "Isolated verification database cleanly dropped. Live production database remains untouched." -ForegroundColor Green

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "DATABASE RESTORATION VERIFICATION COMPLETED SUCCESSFULLY" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
