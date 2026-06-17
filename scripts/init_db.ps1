# Verify PostgreSQL connectivity using DATABASE_URL from .env.

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $ProjectRoot

if (-not (Test-Path ".env")) {
    Write-Host "ERROR: .env file not found. Copy .env.example to .env first."
    exit 1
}

Write-Host "Checking PostgreSQL connection..."

$python = Join-Path $ProjectRoot ".venv\Scripts\python.exe"
if (-not (Test-Path $python)) {
    $python = "python"
}

& $python (Join-Path $PSScriptRoot "check_db.py")
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "Database check completed successfully."
