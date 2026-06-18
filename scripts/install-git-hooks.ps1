#Requires -Version 5.1
# Installe le hook Git pre-push (vérification compile avant push)
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $Root

git config core.hooksPath .githooks
Write-Host "Hooks Git activés: .githooks/pre-push lancera verify.ps1 -Quick avant chaque push." -ForegroundColor Green
Write-Host "Pour désactiver: git config --unset core.hooksPath"
