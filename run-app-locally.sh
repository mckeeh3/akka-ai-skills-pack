#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "Building frontend..."
npm --prefix frontend run build

echo "Starting Akka app on 0.0.0.0..."
exec mvn clean compile exec:java -Dakka.runtime.http-interface=0.0.0.0
