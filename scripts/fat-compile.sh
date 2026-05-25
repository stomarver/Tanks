#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
OUT_DIR="$ROOT_DIR/out"
DIST_DIR="$ROOT_DIR/dist"
JAR_PATH="$DIST_DIR/takns.jar"

"$ROOT_DIR/scripts/compile.sh"

rm -rf "$DIST_DIR"
mkdir -p "$DIST_DIR"

jar cfe "$JAR_PATH" com.mojang.takns.Takns -C "$OUT_DIR" .

echo "Fat JAR created: $JAR_PATH"
echo "Run with: java -jar $JAR_PATH"
