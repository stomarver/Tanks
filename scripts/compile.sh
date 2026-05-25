#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
OUT_DIR="$ROOT_DIR/out"
SRC_DIR="$ROOT_DIR/src"

INCLUDE_APPLET=0
if [[ "${1:-}" == "--with-applet" ]]; then
  INCLUDE_APPLET=1
fi

rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

TMP_SOURCES="$(mktemp)"
trap 'rm -f "$TMP_SOURCES"' EXIT

find "$SRC_DIR" -name '*.java' | sort > "$TMP_SOURCES"

if [[ "$INCLUDE_APPLET" -ne 1 ]]; then
  grep -v '/TaknsApplet.java$' "$TMP_SOURCES" > "${TMP_SOURCES}.filtered"
  mv "${TMP_SOURCES}.filtered" "$TMP_SOURCES"
fi

if ! xargs -d '\n' javac --release 8 -encoding windows-1252 -d "$OUT_DIR" < "$TMP_SOURCES"; then
  xargs -d '\n' javac -source 1.8 -target 1.8 -encoding windows-1252 -d "$OUT_DIR" < "$TMP_SOURCES"
fi

if [[ "$INCLUDE_APPLET" -eq 1 ]]; then
  echo "Compiled classes to: $OUT_DIR (including TaknsApplet)"
else
  echo "Compiled classes to: $OUT_DIR (desktop build; TaknsApplet excluded)"
fi
