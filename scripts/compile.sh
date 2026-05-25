#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
OUT_DIR="$ROOT_DIR/out"
SRC_DIR="$ROOT_DIR/src"

rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

TMP_SOURCES="$(mktemp)"
trap 'rm -f "$TMP_SOURCES"' EXIT

find "$SRC_DIR" -name '*.java' | grep -v '/TaknsApplet.java$' | sort > "$TMP_SOURCES"

if javac --help-extra 2>/dev/null | grep -q -- '--release'; then
  xargs -d '\n' javac --release 8 -encoding windows-1252 -d "$OUT_DIR" < "$TMP_SOURCES"
else
  xargs -d '\n' javac -source 1.8 -target 1.8 -encoding windows-1252 -d "$OUT_DIR" < "$TMP_SOURCES"
fi

echo "Compiled classes to: $OUT_DIR"
