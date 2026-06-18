#Requires -Version 5.1
# Télécharge gradle-wrapper.jar si absent (nécessaire pour gradlew en ligne de commande / CI)
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$dest = Join-Path $Root "gradle\wrapper\gradle-wrapper.jar"
$props = Join-Path $Root "gradle\wrapper\gradle-wrapper.properties"

if (Test-Path $dest) {
    Write-Host "gradle-wrapper.jar déjà présent."
    exit 0
}

if (-not (Test-Path $props)) {
    throw "gradle-wrapper.properties introuvable"
}

$url = "https://github.com/gradle/gradle/raw/v8.13.0/gradle/wrapper/gradle-wrapper.jar"
Write-Host "Téléchargement de gradle-wrapper.jar..."
Invoke-WebRequest -Uri $url -OutFile $dest -UseBasicParsing
Write-Host "OK: $dest"
