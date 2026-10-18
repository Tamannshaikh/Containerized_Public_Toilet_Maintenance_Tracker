param(
    [int]$Port = 8082,
    [int]$MaxAttempts = 15
)

Write-Output "[HEALTH-CHECK] Probing health on port $Port (Max attempts: $MaxAttempts)..."
$healthy = $false

for ($i = 1; $i -le $MaxAttempts; $i++) {
    Start-Sleep -Seconds 2
    try {
        $resp = Invoke-WebRequest -Uri "http://localhost:$Port/api/v1/health" -UseBasicParsing -TimeoutSec 5
        if ($resp.StatusCode -eq 200) {
            $data = $resp.Content | ConvertFrom-Json
            if ($data.status -eq "UP") {
                Write-Output "[HEALTH-CHECK] >>> SUCCESS: Application is UP and HEALTHY on port $Port (Attempt $i/$MaxAttempts) <<<"
                Write-Output "[HEALTH-CHECK] Service: $($data.service)"
                Write-Output "[HEALTH-CHECK] Version: $($data.version)"
                $healthy = $true
                break
            }
        }
    } catch {
        Write-Output "[HEALTH-CHECK] Waiting for service on port $Port... (Attempt $i/$MaxAttempts)"
    }
}

if ($healthy) {
    exit 0
} else {
    Write-Error "[HEALTH-CHECK] ERROR: Application on port $Port failed to become healthy within timeout!"
    exit 1
}
