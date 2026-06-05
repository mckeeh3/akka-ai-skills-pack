#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
PACK_ROOT="$(cd -- "$SCRIPT_DIR/../../.." && pwd)"
APP_ROOT="$(cd -- "$PACK_ROOT/.." && pwd)"
MANIFEST_PATH="$PACK_ROOT/pack/manifest.yaml"

log() {
  printf '[release] %s\n' "$*"
}

warn() {
  printf '[release][warn] %s\n' "$*" >&2
}

fail() {
  printf '[release][error] %s\n' "$*" >&2
  exit 1
}

print_help() {
  cat <<'EOF'
Cut a release for the Akka AI skills pack.

Usage:
  cd skills-pack && bash pack/maintainer/tools/release.sh
  # or, from the repository root:
  bash skills-pack/pack/maintainer/tools/release.sh

Flow:
  1. require a clean git working tree
  2. show the current manifest version and latest release tag
  3. prompt for the next semver version
  4. replace hardcoded current-version references in tracked text files
  5. run lightweight skills-pack validation
  6. commit the version changes
  7. create an annotated git tag
  8. optionally push the release commit/tag

No distribution archive or release-installer asset is built. The repository is the unit
of installation; users install skills from the checked-out/tagged repository.
EOF
}

confirm() {
  local prompt="$1"
  local default="${2:-no}"
  local suffix answer

  case "$default" in
    yes) suffix='[Y/n]' ;;
    no) suffix='[y/N]' ;;
    *) fail "Invalid confirm default: $default" ;;
  esac

  while true; do
    read -r -p "$prompt $suffix " answer
    answer="${answer:-$default}"
    case "$answer" in
      y|Y|yes|YES|Yes) return 0 ;;
      n|N|no|NO|No) return 1 ;;
      *) printf 'Please answer yes or no.\n' ;;
    esac
  done
}

require_clean_tree() {
  local status
  status="$(git -C "$APP_ROOT" status --porcelain)"
  [[ -z "$status" ]] || fail "Working tree is not clean. Commit or stash changes before cutting a release."
}

read_manifest_field() {
  local field="$1"
  awk -v wanted="$field" '
    $0 ~ /^metadata:/ { in_metadata=1; next }
    in_metadata && $0 ~ ("^  " wanted ":") { print $2; exit }
  ' "$MANIFEST_PATH"
}

validate_semver() {
  local version="$1"
  [[ "$version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]] || fail "Version must be MAJOR.MINOR.PATCH, got: $version"
}

next_patch_version() {
  local version="$1"
  IFS=. read -r major minor patch <<< "$version"
  printf '%s.%s.%s\n' "$major" "$minor" "$((patch + 1))"
}

replace_version_references() {
  local current_version="$1"
  local next_version="$2"

  log "Replacing tracked text references: $current_version -> $next_version"

  python3 - "$APP_ROOT" "$current_version" "$next_version" <<'PY'
from pathlib import Path
import subprocess
import sys

repo = Path(sys.argv[1])
current = sys.argv[2]
next_version = sys.argv[3]

tracked = subprocess.check_output(
    ["git", "-C", str(repo), "ls-files", "-z"],
    text=False,
)
changed: list[str] = []
for raw in tracked.split(b"\0"):
    if not raw:
        continue
    rel = raw.decode()
    path = repo / rel
    try:
        data = path.read_bytes()
    except FileNotFoundError:
        continue
    if b"\0" in data:
        continue
    try:
        text = data.decode("utf-8")
    except UnicodeDecodeError:
        continue
    if current not in text:
        continue
    path.write_text(text.replace(current, next_version))
    changed.append(rel)

if changed:
    print("\n".join(changed))
PY
}

current_branch() {
  git -C "$APP_ROOT" symbolic-ref --quiet --short HEAD || true
}

ensure_tag_available() {
  local tag="$1"
  local status

  log "Checking local tag availability: $tag"
  git -C "$APP_ROOT" rev-parse "refs/tags/$tag" >/dev/null 2>&1 && fail "Local tag already exists: $tag"

  log "Checking origin tag availability: $tag"
  set +e
  if command -v timeout >/dev/null 2>&1; then
    GIT_TERMINAL_PROMPT=0 timeout 20 git -C "$APP_ROOT" ls-remote --exit-code --tags origin "refs/tags/$tag" >/dev/null 2>&1
  else
    GIT_TERMINAL_PROMPT=0 git -C "$APP_ROOT" ls-remote --exit-code --tags origin "refs/tags/$tag" >/dev/null 2>&1
  fi
  status=$?
  set -e

  case "$status" in
    0)
      fail "Remote tag already exists on origin: $tag"
      ;;
    2)
      log "Tag is available: $tag"
      ;;
    124)
      fail "Timed out while checking origin for tag $tag"
      ;;
    *)
      fail "Could not check origin for tag $tag. Verify remote access, then try again."
      ;;
  esac
}

main() {
  if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
    print_help
    exit 0
  fi
  [[ $# -eq 0 ]] || fail "Unknown option: $1"

  command -v git >/dev/null 2>&1 || fail "git is required"
  command -v python3 >/dev/null 2>&1 || fail "python3 is required"
  [[ -f "$MANIFEST_PATH" ]] || fail "Missing pack/manifest.yaml"

  cd "$APP_ROOT"
  require_clean_tree

  local pack_name current_version current_tag latest_tag default_next next_version next_tag branch
  pack_name="$(read_manifest_field name)"
  current_version="$(read_manifest_field version)"
  [[ -n "$pack_name" ]] || fail "Could not read metadata.name from pack/manifest.yaml"
  [[ -n "$current_version" ]] || fail "Could not read metadata.version from pack/manifest.yaml"
  validate_semver "$current_version"

  current_tag="v$current_version"
  latest_tag="$(git -C "$APP_ROOT" describe --tags --abbrev=0 2>/dev/null || true)"
  default_next="$(next_patch_version "$current_version")"

  cat <<EOF
Akka AI skills pack release

Current manifest version: $current_version
Current release tag:      $current_tag
Latest local git tag:     ${latest_tag:-none}
Default next version:     $default_next
EOF

  while true; do
    read -r -p "Next release version [$default_next]: " next_version
    next_version="${next_version:-$default_next}"
    next_version="${next_version#v}"
    validate_semver "$next_version"
    [[ "$next_version" != "$current_version" ]] || { printf 'Next version must differ from current version.\n'; continue; }
    break
  done

  next_tag="v$next_version"
  ensure_tag_available "$next_tag"

  cat <<EOF

Release plan:
- update tracked hardcoded references from $current_version to $next_version
- run version consistency check
- run install-skills dry-run and install check
- run git diff --check
- commit version changes as: Release $next_tag
- create annotated git tag: $next_tag

No distribution archive or release-installer asset will be built.
EOF

  confirm "Continue?" no || fail "Release cancelled"

  replace_version_references "$current_version" "$next_version"

  if git -C "$APP_ROOT" diff --quiet; then
    fail "No version references changed. Refusing to continue."
  fi

  log "Changed files:"
  git -C "$APP_ROOT" diff --name-only | sed 's/^/[release] - /'

  log "Checking version consistency"
  bash "$PACK_ROOT/pack/maintainer/tools/check-version-consistency.sh"

  log "Running install-skills dry-run"
  local install_check_dir
  install_check_dir="$(mktemp -d)"
  bash "$PACK_ROOT/install-skills.sh" --target "$install_check_dir/.agents/skills" --dry-run
  bash "$PACK_ROOT/install-skills.sh" --target "$install_check_dir/.agents/skills"
  bash "$PACK_ROOT/install-skills.sh" --target "$install_check_dir/.agents/skills" --check
  rm -rf "$install_check_dir"

  log "Checking whitespace"
  git -C "$APP_ROOT" diff --check

  log "Committing version changes"
  git -C "$APP_ROOT" add -u
  git -C "$APP_ROOT" commit -m "Release $next_tag"

  log "Creating annotated tag $next_tag"
  git -C "$APP_ROOT" tag -a "$next_tag" -m "Release $next_tag"

  branch="$(current_branch)"
  if confirm "Push release commit and tag to origin?" no; then
    [[ -n "$branch" ]] || fail "Cannot push automatically from a detached HEAD"
    log "Pushing branch $branch"
    git -C "$APP_ROOT" push origin "$branch"
    log "Pushing tag $next_tag"
    git -C "$APP_ROOT" push origin "refs/tags/$next_tag"
  else
    warn "Release commit and tag were created locally but not pushed."
  fi

  cat <<EOF

Release complete.

Version: $next_version
Tag:     $next_tag
Install: git checkout $next_tag && ./install-skills.sh --target <harness>/.agents/skills

If you did not push from this script, push manually with:
  git push origin ${branch:-<branch>}
  git push origin refs/tags/$next_tag
EOF
}

main "$@"
