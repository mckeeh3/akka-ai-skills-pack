#!/usr/bin/env bash
# Load local backend environment variables from .env.
#
# Usage:
#   source ./set-env.sh          # exports variables into your current shell
#   ./set-env.sh <command> ...   # runs a command with variables loaded
#   ./set-env.sh                 # validates/loads in a subshell and prints guidance
#
# This script never writes .env or frontend/.env.local.

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="${ENV_FILE:-$ROOT_DIR/.env}"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Missing env file: $ENV_FILE" >&2
  echo "Create it from .env.example first, then rerun this script." >&2
  return 1 2>/dev/null || exit 1
fi

# Export all variable assignments while sourcing .env.
set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a

if [[ $# -gt 0 ]]; then
  exec "$@"
fi

if [[ "${BASH_SOURCE[0]}" == "$0" ]]; then
  cat <<EOF
Loaded ${ENV_FILE#$ROOT_DIR/} in this subprocess only.
To set variables in your current shell, run:
  source ./set-env.sh

Or run a command with the variables loaded:
  ./set-env.sh mvn test
EOF
else
  echo "Loaded ${ENV_FILE#$ROOT_DIR/}"
fi
