param(
    [int]$Port = 8082
)

Write-Output "[DEPLOY] Preparing staging port $Port..."

# Safety Guard: Never stop 8080 (Jenkins) or 8081 (Preserved app)
if ($Port -ne 8081 -and $Port -ne 8080) {
    Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue | ForEach-Object {
        $procId = $_.OwningProcess
        Write-Output "[DEPLOY] Stopping previous staging process PID: $procId on port $Port"
        Stop-Process -Id $procId -Force -ErrorAction SilentlyContinue
    }
    Start-Sleep -Seconds 1
}

Write-Output "[DEPLOY] Launching newly built JAR on port $Port..."
$jarPath = (Resolve-Path "deploy\staging\app.jar").Path
$cmd = "java -jar `"$jarPath`" --server.port=$Port"
$res = Invoke-CimMethod -ClassName Win32_Process -MethodName Create -Arguments @{ CommandLine = $cmd }
Write-Output "[DEPLOY] Staging process launched with ReturnValue: $($res.ReturnValue), PID: $($res.ProcessId) on port $Port"
