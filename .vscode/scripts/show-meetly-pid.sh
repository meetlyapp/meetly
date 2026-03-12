#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=/dev/null
source "$script_dir/android-env.sh"

adb_path="$(resolve_adb)"
serial="$(first_device_serial "$adb_path")"
if [ -z "$serial" ]; then
  echo "No connected Android device found"
  exit 1
fi

echo "Device: $serial"
"$adb_path" -s "$serial" shell pidof -s dev.lisek.meetly
