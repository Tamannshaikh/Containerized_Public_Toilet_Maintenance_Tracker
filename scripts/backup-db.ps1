<#
.SYNOPSIS
    Automated Database Backup Script for PostgreSQL Container.
.DESCRIPTION
    Task 14: Infrastructure Provisioning & Reliability.
    Generates timestamped PostgreSQL backups from the public-toilet-db container
    using pg_dump without hardcoding sensitive credentials.
#>

param(
    [string]$BackupDir = "backups",
    [string]$ContainerName = "public-toilet-db",
    [string]$DbName = $env:POSTGRES_DB,
    [string]$DbUser = $env:POSTGRES_USER
)

# Apply defaults if environment variables are not set
if (-not $DbName) { $DbName = "toilet_tracker_db" }
if (-not $DbUser) { $DbUser = "tracker_user" }

$ErrorActionPreference = "Stop"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "TASK 14: POSTGRESQL DATABASE BACKUP AUTOMATION" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Verify container is running and healthy
$containerStatus = docker inspect $ContainerName --format "{{.State.Status}}:{{.State.Health.Status}}" 2>$null
if ($containerStatus -notmatch "running") {
    Write-Error "Container '$ContainerName' is not running (Status: $containerStatus). Cannot proceed with backup."
}
Write-Host "[1/5] Verified container '$ContainerName' is active ($containerStatus)." -ForegroundColor Green

# 2. Ensure backup destination directory exists
$targetDir = Join-Path -Path $PSScriptRoot -ChildPath "..\"
if (-not (Test-Path $BackupDir)) {
    New-Item -ItemType Directory -Path $BackupDir -Force | Out-Null
    Write-Host "[2/5] Created backup directory: $BackupDir" -ForegroundColor Green
} else {
    Write-Host "[2/5] Backup directory exists: $BackupDir" -ForegroundColor Green
}

# 3. Generate timestamped backup file name
$timestamp = (Get-Date).ToString("yyyyMMdd_HHmmss")
$backupFilename = "toilet_tracker_db_backup_$timestamp.sql"
$backupPath = Join-Path -Path $BackupDir -ChildPath $backupFilename
$containerTemp = "/tmp/$backupFilename"

# 4. Execute pg_dump inside container and copy to host
Write-Host "[3/5] Executing pg_dump inside container for database '$DbName' as user '$DbUser'..." -ForegroundColor Yellow
docker exec $ContainerName pg_dump -U $DbUser -d $DbName --clean --if-exists -f $containerTemp
if ($LASTEXITCODE -ne 0) {
    Write-Error "pg_dump execution failed inside container '$ContainerName'."
}

Write-Host "[4/5] Copying backup archive from container to host: $backupPath" -ForegroundColor Yellow
docker cp "${ContainerName}:${containerTemp}" $backupPath
docker exec $ContainerName rm -f $containerTemp

# 5. Verify backup file integrity
if (Test-Path $backupPath) {
    $fileInfo = Get-Item $backupPath
    $fileSizeKB = [math]::Round($fileInfo.Length / 1KB, 2)
    Write-Host "[5/5] Backup successfully generated!" -ForegroundColor Green
    Write-Host "      Archive File: $backupFilename" -ForegroundColor Cyan
    Write-Host "      File Size:    $fileSizeKB KB ($($fileInfo.Length) bytes)" -ForegroundColor Cyan
    Write-Host "      Full Path:    $($fileInfo.FullName)" -ForegroundColor Cyan

    # Verify key data exists in dump
    $hasTicket = Select-String -Path $backupPath -Pattern "TKT-20261019-9F7207" -SimpleMatch
    if ($hasTicket) {
        Write-Host "      Data Verification: Persisted ticket 'TKT-20261019-9F7207' verified in backup archive." -ForegroundColor Green
    } else {
        Write-Warning "Ticket TKT-20261019-9F7207 was not found in the backup archive."
    }
} else {
    Write-Error "Backup file was not created at destination: $backupPath"
}

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "BACKUP OPERATION COMPLETED SUCCESSFULLY" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
