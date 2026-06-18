#Requires -Version 5.1
<#
.SYNOPSIS
  Vérifie que le projet Android compile et que les tests passent.

.USAGE
  .\scripts\verify.ps1           # compilation + tests + APK debug
  .\scripts\verify.ps1 -Quick    # compilation Kotlin + tests seulement (plus rapide)
#>
param(
    [switch]$Quick
)

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $Root

function Ensure-GradleWrapper {
    $jar = Join-Path $Root "gradle\wrapper\gradle-wrapper.jar"
    if (Test-Path $jar) { return }

    Write-Host "gradle-wrapper.jar absent — bootstrap..." -ForegroundColor Yellow
    $bootstrap = Join-Path $Root "scripts\bootstrap-gradle-wrapper.ps1"
    if (Test-Path $bootstrap) {
        & $bootstrap
    } else {
        throw "gradle-wrapper.jar manquant. Ouvre le projet dans Android Studio (Sync) ou lance scripts\bootstrap-gradle-wrapper.ps1"
    }
}

function Get-GradleCommand {
    Ensure-GradleWrapper
    $gradlew = Join-Path $Root "gradlew.bat"
    if (Test-Path $gradlew) { return $gradlew }

    $cached = Get-ChildItem "$env:USERPROFILE\.gradle\wrapper\dists" -Recurse -Filter "gradle.bat" -ErrorAction SilentlyContinue |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1
    if ($cached) { return $cached.FullName }

    throw "Gradle introuvable. Installe Android Studio ou lance Sync Gradle."
}

Write-Host "==> Validation JSON assets" -ForegroundColor Cyan
python (Join-Path $Root "scripts\validate_assets.py")
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$gradle = Get-GradleCommand
$tasks = if ($Quick) {
    @("compileDebugKotlin", "testDebugUnitTest")
} else {
    @("compileDebugKotlin", "testDebugUnitTest", "assembleDebug")
}

Write-Host "==> Gradle: $($tasks -join ', ')" -ForegroundColor Cyan
& $gradle @tasks --stacktrace --no-daemon
exit $LASTEXITCODE
