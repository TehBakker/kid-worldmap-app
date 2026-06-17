# Load .env and start FastAPI in development mode with auto-reload.

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $ProjectRoot

if (Test-Path ".env") {
    Get-Content ".env" | ForEach-Object {
        if ($_ -match '^\s*#' -or $_ -match '^\s*$') { return }
        $parts = $_ -split '=', 2
        if ($parts.Count -eq 2) {
            Set-Item -Path "env:$($parts[0].Trim())" -Value $parts[1].Trim()
        }
    }
    Write-Host "Loaded environment from .env"
} else {
    Write-Host "Warning: .env file not found. Copy .env.example to .env first."
}

if (-not $env:PORT) { $env:PORT = "8080" }
if (-not $env:ENVIRONMENT) { $env:ENVIRONMENT = "local" }

$uvicorn = Join-Path $ProjectRoot ".venv\Scripts\uvicorn.exe"
if (-not (Test-Path $uvicorn)) {
    $uvicorn = "uvicorn"
}

Write-Host "Starting FastAPI on http://127.0.0.1:$($env:PORT) (ENVIRONMENT=$($env:ENVIRONMENT))"
& $uvicorn app.main:app --host 127.0.0.1 --port $env:PORT --reload
