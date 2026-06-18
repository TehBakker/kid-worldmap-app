#!/usr/bin/env bash
# Vérification locale (Git Bash / Linux / macOS)
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
QUICK=""
if [[ "${1:-}" == "--quick" || "${1:-}" == "-Quick" ]]; then
  QUICK="-Quick"
fi

python3 scripts/validate_assets.py

if [[ ! -f gradle/wrapper/gradle-wrapper.jar ]]; then
  echo "Bootstrapping gradle-wrapper.jar..."
  curl -fsSL -o gradle/wrapper/gradle-wrapper.jar \
    "https://github.com/gradle/gradle/raw/v8.13.0/gradle/wrapper/gradle-wrapper.jar"
fi
chmod +x gradlew

if [[ -n "$QUICK" ]]; then
  ./gradlew compileDebugKotlin testDebugUnitTest --stacktrace --no-daemon
else
  ./gradlew compileDebugKotlin testDebugUnitTest assembleDebug --stacktrace --no-daemon
fi
