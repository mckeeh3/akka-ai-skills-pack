#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd -- "$SCRIPT_DIR/.." && pwd)"
MANIFEST_PATH="$REPO_ROOT/pack/manifest.yaml"
DIST_DIR="$REPO_ROOT/dist"

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
  bash tools/release.sh

Flow:
  1. require a clean git working tree
  2. show the current manifest version and latest release tag
  3. prompt for the next semver version
  4. replace hardcoded current-version references in tracked text files
  5. run version checks and Maven verification
  6. build release assets under dist/
  7. commit the version changes
  8. create an annotated git tag
  9. optionally push the release commit/tag
 10. if gh is installed, optionally create or update a draft GitHub release
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
  status="$(git -C "$REPO_ROOT" status --porcelain)"
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

  python3 - "$REPO_ROOT" "$current_version" "$next_version" <<'PY'
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
  git -C "$REPO_ROOT" symbolic-ref --quiet --short HEAD || true
}

ensure_tag_available() {
  local tag="$1"
  local status

  log "Checking local tag availability: $tag"
  git -C "$REPO_ROOT" rev-parse "refs/tags/$tag" >/dev/null 2>&1 && fail "Local tag already exists: $tag"

  log "Checking origin tag availability: $tag"
  set +e
  if command -v timeout >/dev/null 2>&1; then
    GIT_TERMINAL_PROMPT=0 timeout 20 git -C "$REPO_ROOT" ls-remote --exit-code --tags origin "refs/tags/$tag" >/dev/null 2>&1
  else
    GIT_TERMINAL_PROMPT=0 git -C "$REPO_ROOT" ls-remote --exit-code --tags origin "refs/tags/$tag" >/dev/null 2>&1
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

create_or_update_github_release() {
  local tag="$1"
  local archive_path="$2"
  local installer_path="$3"

  [[ -f "$archive_path" ]] || fail "Missing archive asset: $archive_path"
  [[ -f "$installer_path" ]] || fail "Missing installer asset: $installer_path"

  if gh release view "$tag" >/dev/null 2>&1; then
    log "GitHub release already exists for $tag; uploading assets with --clobber"
    gh release upload "$tag" "$archive_path" "$installer_path" --clobber
  else
    log "Creating draft GitHub release for $tag"
    if ! gh release create "$tag" "$archive_path" "$installer_path" \
      --draft \
      --generate-notes \
      --title "$tag"; then
      warn "Release creation failed; checking whether a release appeared concurrently"
      gh release view "$tag" >/dev/null 2>&1 || return 1
      gh release upload "$tag" "$archive_path" "$installer_path" --clobber
    fi
  fi
}

main() {
  if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
    print_help
    exit 0
  fi
  [[ $# -eq 0 ]] || fail "Unknown option: $1"

  command -v git >/dev/null 2>&1 || fail "git is required"
  command -v python3 >/dev/null 2>&1 || fail "python3 is required"
  command -v mvn >/dev/null 2>&1 || fail "mvn is required"
  [[ -f "$MANIFEST_PATH" ]] || fail "Missing pack/manifest.yaml"

  cd "$REPO_ROOT"
  require_clean_tree

  local pack_name current_version current_tag latest_tag default_next next_version next_tag branch
  pack_name="$(read_manifest_field name)"
  current_version="$(read_manifest_field version)"
  [[ -n "$pack_name" ]] || fail "Could not read metadata.name from pack/manifest.yaml"
  [[ -n "$current_version" ]] || fail "Could not read metadata.version from pack/manifest.yaml"
  validate_semver "$current_version"

  current_tag="v$current_version"
  latest_tag="$(git -C "$REPO_ROOT" describe --tags --abbrev=0 2>/dev/null || true)"
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
- run mvn verify
- build release assets under dist/
- commit version changes as: Release $next_tag
- create annotated git tag: $next_tag
EOF

  confirm "Continue?" no || fail "Release cancelled"

  replace_version_references "$current_version" "$next_version"

  if git -C "$REPO_ROOT" diff --quiet; then
    fail "No version references changed. Refusing to continue."
  fi

  log "Changed files:"
  git -C "$REPO_ROOT" diff --name-only | sed 's/^/[release] - /'

  log "Checking version consistency"
  bash "$REPO_ROOT/tools/check-version-consistency.sh"

  log "Running Maven verification"
  mvn verify --no-transfer-progress

  log "Building release assets"
  bash "$REPO_ROOT/tools/build-pack.sh" --clean

  local archive_path installer_path pushed=false
  archive_path="$DIST_DIR/${pack_name}-${next_version}.tar.gz"
  installer_path="$DIST_DIR/install-${pack_name}-${next_version}.sh"

  [[ -f "$archive_path" ]] || fail "Expected archive was not built: $archive_path"
  [[ -f "$installer_path" ]] || fail "Expected installer was not built: $installer_path"

  log "Committing version changes"
  git -C "$REPO_ROOT" add -u
  git -C "$REPO_ROOT" commit -m "Release $next_tag"

  log "Creating annotated tag $next_tag"
  git -C "$REPO_ROOT" tag -a "$next_tag" -m "Release $next_tag"

  branch="$(current_branch)"
  if confirm "Push release commit and tag to origin?" no; then
    [[ -n "$branch" ]] || fail "Cannot push automatically from a detached HEAD"
    log "Pushing branch $branch"
    git -C "$REPO_ROOT" push origin "$branch"
    log "Pushing tag $next_tag"
    git -C "$REPO_ROOT" push origin "refs/tags/$next_tag"
    pushed=true
  else
    warn "Release commit and tag were created locally but not pushed."
  fi

  if command -v gh >/dev/null 2>&1; then
    if [[ "$pushed" == true ]]; then
      if confirm "Create or update a draft GitHub release with gh?" yes; then
        create_or_update_github_release "$next_tag" "$archive_path" "$installer_path"
      fi
    else
      warn "gh is installed, but GitHub release creation was skipped because the tag was not pushed."
    fi
  else
    warn "gh is not installed; skipping GitHub release creation."
  fi

  cat <<EOF

Release complete.

Version:           $next_version
Tag:               $next_tag
Archive:           $archive_path
Release installer: $installer_path

If you did not push from this script, push manually with:
  git push origin ${branch:-<branch>}
  git push origin refs/tags/$next_tag
EOF
}

main "$@"
