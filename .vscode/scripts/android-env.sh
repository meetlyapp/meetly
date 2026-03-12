#!/usr/bin/env bash

# Resolves adb path from local.properties first, then ANDROID_HOME.
resolve_adb() {
  local sdk
  sdk=$(grep '^sdk.dir=' local.properties | cut -d= -f2 | tail -n 1)
  sdk=${sdk//\\/:}
  if [ -z "$sdk" ]; then
    sdk="$ANDROID_HOME"
  fi

  local adb_path="$sdk/platform-tools/adb"
  if [ ! -x "$adb_path" ]; then
    echo "adb not found at: $adb_path"
    return 127
  fi

  echo "$adb_path"
}

# Returns the first connected device serial.
first_device_serial() {
  local adb_path="$1"
  "$adb_path" devices | awk 'NR>1 && $2=="device" {print $1; exit}'
}
