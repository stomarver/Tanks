#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
OUT_DIR="$ROOT_DIR/out"

if [[ ! -d "$OUT_DIR" ]]; then
  "$ROOT_DIR/scripts/compile.sh"
fi

exec java -cp "$OUT_DIR" com.mojang.takns.Takns "$@"
