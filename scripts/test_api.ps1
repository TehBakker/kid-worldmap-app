# Smoke-test the local API endpoints.

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

$port = if ($env:PORT) { $env:PORT } else { "8080" }
$baseUrl = if ($env:BASE_URL) { $env:BASE_URL } else { "http://127.0.0.1:$port" }

Write-Host "Testing API at $baseUrl"
Write-Host ""

Write-Host "==> GET /health"
Invoke-RestMethod -Uri "$baseUrl/health" | ConvertTo-Json -Compress
Write-Host ""

Write-Host "==> GET /db-check"
Invoke-RestMethod -Uri "$baseUrl/db-check" | ConvertTo-Json -Compress
Write-Host ""

Write-Host "==> POST /notes"
$body = @{ title = "Ma note"; content = "Contenu" } | ConvertTo-Json
Invoke-RestMethod -Uri "$baseUrl/notes" -Method Post -ContentType "application/json" -Body $body | ConvertTo-Json -Compress
Write-Host ""

Write-Host "==> GET /notes"
Invoke-RestMethod -Uri "$baseUrl/notes" | ConvertTo-Json -Compress
Write-Host ""

Write-Host "All API smoke tests passed."
