#Requires -Version 5.1
<#
  Hook Cursor (événement stop) : vérifie la compilation après une session agent.
  En cas d'échec, propose une correction automatique via followup_message.
#>
$ErrorActionPreference = "Continue"
$Root = Resolve-Path (Join-Path $PSScriptRoot "..\..")
Set-Location $Root

# Ne lance pas la vérif si aucun fichier Kotlin/Java n'a changé
$changed = git diff --name-only HEAD 2>$null
$staged = git diff --cached --name-only 2>$null
$all = @($changed + $staged) | Where-Object { $_ -match '\.(kt|kts)$' }
if (-not $all) {
    exit 0
}

$log = Join-Path $env:TEMP "kid-worldmap-verify.log"
& (Join-Path $Root "scripts\verify.ps1") -Quick *> $log
$code = $LASTEXITCODE

if ($code -eq 0) {
    exit 0
}

$tail = Get-Content $log -Tail 40 -ErrorAction SilentlyContinue
$summary = ($tail -join "`n").Substring(0, [Math]::Min(1500, ($tail -join "`n").Length))

@{
    followup_message = @"
La vérification automatique a échoué (compileDebugKotlin / tests).
Corrige les erreurs ci-dessous puis relance verify.ps1 -Quick.

$summary
"@
} | ConvertTo-Json -Compress

exit 0
