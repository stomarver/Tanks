#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
OUT_DIR="$ROOT_DIR/out"
SRC_DIR="$ROOT_DIR/src"

rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# Compile all project sources with Java 8 compatibility.
find "$SRC_DIR" -name '*.java' -print0 | xargs -0 javac -source 1.8 -target 1.8 -encoding windows-1252 -d "$OUT_DIR"

echo "Compiled classes to: $OUT_DIR"
