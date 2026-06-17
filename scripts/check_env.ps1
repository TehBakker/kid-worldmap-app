# Verify required environment variables before running or deploying.

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
}

$errors = 0

Write-Host "Checking required environment variables..."

if ([string]::IsNullOrWhiteSpace($env:DATABASE_URL)) {
    Write-Host "ERROR: DATABASE_URL is missing or empty."
    $errors++
} else {
    Write-Host "OK: DATABASE_URL is set."
    if ($env:DATABASE_URL -match "YOUR_PASSWORD") {
        Write-Host "WARNING: Replace YOUR_PASSWORD in .env with your real Supabase password."
    }
    if ($env:DATABASE_URL -notmatch "^postgresql\+psycopg://") {
        Write-Host "WARNING: DATABASE_URL should use postgresql+psycopg:// for psycopg driver."
    }
    if ($env:DATABASE_URL -notmatch "sslmode=require") {
        Write-Host "WARNING: Supabase requires sslmode=require in DATABASE_URL."
    }
}

Write-Host ""
$portDisplay = if ($env:PORT) { $env:PORT } else { "8080 (default)" }
$envDisplay = if ($env:ENVIRONMENT) { $env:ENVIRONMENT } else { "local (default)" }
Write-Host "Optional variables (defaults applied if missing):"
Write-Host "  PORT=$portDisplay"
Write-Host "  ENVIRONMENT=$envDisplay"

if ($errors -gt 0) {
    Write-Host ""
    Write-Host "$errors required variable(s) missing. Copy .env.example to .env and fill in values."
    exit 1
}

Write-Host ""
Write-Host "Environment check passed."
