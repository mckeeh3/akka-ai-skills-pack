#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'USAGE'
Usage: tools/runtime-validation/stop-local.sh [--force]

Stops the script-started local runtime-validation Akka backend recorded in
.runtime-validation/backend.pid. The script is intentionally conservative: it
only stops the process tracked by the runtime-validation pid file.

Options:
  --force  send SIGKILL if the process does not exit after the graceful timeout
USAGE
}

FORCE=false
for arg in "$@"; do
  case "$arg" in
    --force) FORCE=true ;;
    -h|--help) usage; exit 0 ;;
    *) echo "unknown argument: $arg" >&2; usage >&2; exit 2 ;;
  esac
done

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

STATE_DIR=".runtime-validation"
PID_FILE="$STATE_DIR/backend.pid"

if [[ ! -f "$PID_FILE" ]]; then
  echo "No runtime-validation backend pid file found at $PID_FILE"
  exit 0
fi

pid="$(cat "$PID_FILE" || true)"
if [[ -z "$pid" || ! "$pid" =~ ^[0-9]+$ ]]; then
  echo "Invalid runtime-validation backend pid file; removing $PID_FILE" >&2
  rm -f "$PID_FILE"
  exit 0
fi

if ! kill -0 "$pid" 2>/dev/null; then
  echo "Runtime-validation backend pid $pid is not running; removing stale pid file"
  rm -f "$PID_FILE"
  exit 0
fi

echo "Stopping runtime-validation backend pid $pid"
kill "$pid" || true
for _ in {1..30}; do
  if ! kill -0 "$pid" 2>/dev/null; then
    rm -f "$PID_FILE"
    echo "Runtime-validation backend stopped"
    exit 0
  fi
  sleep 1
done

if [[ "$FORCE" == true ]]; then
  echo "Runtime-validation backend pid $pid did not stop gracefully; sending SIGKILL" >&2
  kill -9 "$pid" || true
  rm -f "$PID_FILE"
  echo "Runtime-validation backend stopped"
  exit 0
fi

echo "Runtime-validation backend pid $pid did not stop within 30 seconds. Re-run with --force if needed." >&2
exit 1
