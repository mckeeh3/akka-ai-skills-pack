#!/usr/bin/env bash
set -euo pipefail

PACK_NAME="__PACK_NAME__"
PACK_VERSION="__PACK_VERSION__"
GITHUB_REPO="__GITHUB_REPO__"
RELEASE_TAG="v${PACK_VERSION}"
TARGET_DIR="$(pwd)"
BUNDLE="all"
ARCHIVE_URL=""
DRY_RUN=false
FORCE=false
KEEP_TEMP=false
LIST_BUNDLES=false

print_help() {
  cat <<EOF
Download Akka AI pack release ${PACK_VERSION} from GitHub and install it into a target project directory.

Usage:
  bash $(basename "$0") [options]

Options:
  --target-dir <dir>  Project directory that will receive .agents/. Default: current directory
  --bundle <name>     Bundle to install. Default: all
  --force             Forward --force to the bundled installer
  --dry-run           Forward --dry-run to the bundled installer
  --list-bundles      Download the bundle and print supported bundle ids
  --archive-url <url> Override the GitHub release archive URL (useful for testing)
  --keep-temp         Keep the temporary download/extract directory
  --help              Show this help text

Defaults:
  GitHub repo:  ${GITHUB_REPO}
  Release tag:  ${RELEASE_TAG}
  Archive URL:  https://github.com/${GITHUB_REPO}/releases/download/${RELEASE_TAG}/${PACK_NAME}-${PACK_VERSION}.tar.gz

Example:
  curl -fsSL https://github.com/${GITHUB_REPO}/releases/download/${RELEASE_TAG}/install-${PACK_NAME}-${PACK_VERSION}.sh | bash -s -- --target-dir /path/to/project --bundle ${BUNDLE}

Tip:
  Use --list-bundles to inspect the bundle ids defined by the packaged manifest.
EOF
}

log() {
  printf '[release-install] %s\n' "$*"
}

fail() {
  printf '[release-install][error] %s\n' "$*" >&2
  exit 1
}

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || fail "Required command not found: $1"
}

run_cmd() {
  if [[ "$DRY_RUN" == true ]]; then
    printf '[dry-run]'
    for arg in "$@"; do
      printf ' %q' "$arg"
    done
    printf '\n'
  else
    "$@"
  fi
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --target-dir)
      [[ $# -ge 2 ]] || fail "Missing value for --target-dir"
      TARGET_DIR="$2"
      shift 2
      ;;
    --bundle)
      [[ $# -ge 2 ]] || fail "Missing value for --bundle"
      BUNDLE="$2"
      shift 2
      ;;
    --force)
      FORCE=true
      shift
      ;;
    --dry-run)
      DRY_RUN=true
      shift
      ;;
    --list-bundles)
      LIST_BUNDLES=true
      shift
      ;;
    --archive-url)
      [[ $# -ge 2 ]] || fail "Missing value for --archive-url"
      ARCHIVE_URL="$2"
      shift 2
      ;;
    --keep-temp)
      KEEP_TEMP=true
      shift
      ;;
    --help|-h)
      print_help
      exit 0
      ;;
    *)
      fail "Unknown option: $1"
      ;;
  esac
done

require_cmd bash
require_cmd curl
require_cmd tar
require_cmd python3

mkdir -p "$TARGET_DIR"
TARGET_DIR="$(cd "$TARGET_DIR" && pwd)"

if [[ -z "$ARCHIVE_URL" ]]; then
  ARCHIVE_URL="https://github.com/${GITHUB_REPO}/releases/download/${RELEASE_TAG}/${PACK_NAME}-${PACK_VERSION}.tar.gz"
fi

TMP_DIR="$(mktemp -d)"
ARCHIVE_PATH="$TMP_DIR/${PACK_NAME}-${PACK_VERSION}.tar.gz"
EXTRACTED_DIR="$TMP_DIR/${PACK_NAME}-${PACK_VERSION}"

cleanup() {
  if [[ "$KEEP_TEMP" == true ]]; then
    log "Keeping temporary directory: $TMP_DIR"
    return
  fi
  rm -rf "$TMP_DIR"
}
trap cleanup EXIT

log "Target project: $TARGET_DIR"
log "Bundle:         $BUNDLE"
log "Archive URL:    $ARCHIVE_URL"

run_cmd curl -fsSL "$ARCHIVE_URL" -o "$ARCHIVE_PATH"
run_cmd tar -xzf "$ARCHIVE_PATH" -C "$TMP_DIR"

[[ -f "$EXTRACTED_DIR/install.sh" ]] || fail "Downloaded archive did not contain ${PACK_NAME}-${PACK_VERSION}/install.sh"

if [[ "$LIST_BUNDLES" == true ]]; then
  run_cmd bash "$EXTRACTED_DIR/install.sh" --list-bundles
  exit 0
fi

installer_cmd=(
  bash "$EXTRACTED_DIR/install.sh"
  --location project
  --project "$TARGET_DIR"
  --bundle "$BUNDLE"
)

if [[ "$FORCE" == true ]]; then
  installer_cmd+=(--force)
fi
if [[ "$DRY_RUN" == true ]]; then
  installer_cmd+=(--dry-run)
fi

run_cmd "${installer_cmd[@]}"

if [[ "$DRY_RUN" == false ]]; then
  log "Install complete in $TARGET_DIR/.agents"
fi
