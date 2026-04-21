#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd -- "$SCRIPT_DIR/.." && pwd)"
DIST_DIR="$REPO_ROOT/dist"
CLEAN=false
NO_ARCHIVE=false
OUTPUT_DIR=""

print_help() {
  cat <<'EOF'
Build a versioned Akka AI resource pack release bundle.

Usage:
  bash tools/build-pack.sh [options]

Options:
  --output-dir <dir>  Output directory for the built bundle. Default: <repo>/dist
  --clean             Remove existing output directory contents for this bundle before building
  --no-archive        Keep the expanded bundle directory only; skip tar.gz creation
  --help              Show this help text

Notes:
  - akka-context is intentionally excluded from the bundle
  - the bundle contains install.sh, manifests, skills, and reference examples
  - installed skill rewriting still happens at install time via install.sh
EOF
}

log() {
  printf '[build-pack] %s\n' "$*"
}

fail() {
  printf '[build-pack][error] %s\n' "$*" >&2
  exit 1
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --output-dir)
      [[ $# -ge 2 ]] || fail "Missing value for --output-dir"
      OUTPUT_DIR="$2"
      shift 2
      ;;
    --clean)
      CLEAN=true
      shift
      ;;
    --no-archive)
      NO_ARCHIVE=true
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

[[ -f "$REPO_ROOT/pack/manifest.yaml" ]] || fail "Missing pack/manifest.yaml"
[[ -f "$REPO_ROOT/install.sh" ]] || fail "Missing install.sh"
[[ -d "$REPO_ROOT/skills" ]] || fail "Missing skills"
[[ -d "$REPO_ROOT/src" ]] || fail "Missing src"

if [[ -n "$OUTPUT_DIR" ]]; then
  DIST_DIR="$OUTPUT_DIR"
fi
mkdir -p "$DIST_DIR"

PACK_NAME="$(awk '
  $0 ~ /^metadata:/ { in_metadata=1; next }
  in_metadata && $0 ~ /^  name:/ { print $2; exit }
' "$REPO_ROOT/pack/manifest.yaml")"

PACK_VERSION="$(awk '
  $0 ~ /^metadata:/ { in_metadata=1; next }
  in_metadata && $0 ~ /^  version:/ { print $2; exit }
' "$REPO_ROOT/pack/manifest.yaml")"

[[ -n "$PACK_NAME" ]] || fail "Could not read metadata.name from pack/manifest.yaml"
[[ -n "$PACK_VERSION" ]] || fail "Could not read metadata.version from pack/manifest.yaml"

BUNDLE_DIR_NAME="${PACK_NAME}-${PACK_VERSION}"
STAGE_DIR="$DIST_DIR/$BUNDLE_DIR_NAME"
ARCHIVE_PATH="$DIST_DIR/${BUNDLE_DIR_NAME}.tar.gz"

if [[ "$CLEAN" == true ]]; then
  log "Cleaning previous outputs for $BUNDLE_DIR_NAME"
  rm -rf "$STAGE_DIR" "$ARCHIVE_PATH"
fi

if [[ -e "$STAGE_DIR" ]]; then
  fail "Stage directory already exists: $STAGE_DIR (use --clean to replace it)"
fi

validate_source_tree() {
  local required_paths=(
    "$REPO_ROOT/skills/README.md"
    "$REPO_ROOT/skills/references/akka-entity-comparison.md"
    "$REPO_ROOT/pom.xml"
    "$REPO_ROOT/README.md"
    "$REPO_ROOT/LICENSE"
    "$REPO_ROOT/pack/README.md"
    "$REPO_ROOT/pack/manifest.schema.yaml"
  )

  for path in "${required_paths[@]}"; do
    [[ -e "$path" ]] || fail "Required source path not found: $path"
  done

  while IFS= read -r skill_dir; do
    [[ -n "$skill_dir" ]] || continue
    [[ -f "$skill_dir/SKILL.md" ]] || fail "Skill directory missing SKILL.md: $skill_dir"
  done < <(find "$REPO_ROOT/skills" -mindepth 1 -maxdepth 1 -type d ! -name references | sort)
}

copy_tree() {
  local src="$1"
  local dest="$2"
  mkdir -p "$(dirname "$dest")"
  cp -R "$src" "$dest"
}

write_bundle_readme() {
  cat > "$STAGE_DIR/BUNDLE-README.md" <<EOF
# ${PACK_NAME} ${PACK_VERSION}

This is a build artifact for the Akka AI resource pack.

## Included
- install.sh
- pack manifests
- repository skills under skills/
- Akka SDK Java reference examples exported from src/
- repository pom.xml and README.md for the example set

## Excluded
- akka-context/

The akka-context directory is intentionally excluded from this bundle. Installed skills are rewritten
at install time so they point to installed examples and generic official Akka SDK documentation
notes instead of repo-local akka-context paths.

## Install

The installer uses cross-harness locations:
- project mode: \`<project-root>/.agents\`
- global mode: \`~/.agents\`

From inside the unpacked bundle:

\`\`\`bash
bash install.sh --location project --project /path/to/project --bundle entities-core
\`\`\`

Or:

\`\`\`bash
bash install.sh --location global --bundle entities-core
\`\`\`

If \`--location\` is omitted, the installer prompts interactively.
If project mode is selected, the current directory is used as the project root unless \`--project\` is provided.

## Bundles
- all
- entities-core
- ese-core
- kve-core
EOF
}

write_build_info() {
  cat > "$STAGE_DIR/BUILD-INFO.txt" <<EOF
pack_name=${PACK_NAME}
pack_version=${PACK_VERSION}
built_at_utc=$(date -u +%Y-%m-%dT%H:%M:%SZ)
source_repo=${REPO_ROOT}
archive_path=${ARCHIVE_PATH}
external_docs_bundled=false
EOF
}

validate_source_tree

log "Building $BUNDLE_DIR_NAME"
mkdir -p "$STAGE_DIR"

copy_tree "$REPO_ROOT/skills" "$STAGE_DIR/skills"
copy_tree "$REPO_ROOT/pack" "$STAGE_DIR/pack"
copy_tree "$REPO_ROOT/src" "$STAGE_DIR/src"
cp "$REPO_ROOT/install.sh" "$STAGE_DIR/install.sh"
cp "$REPO_ROOT/pom.xml" "$STAGE_DIR/pom.xml"
cp "$REPO_ROOT/README.md" "$STAGE_DIR/README.md"
cp "$REPO_ROOT/LICENSE" "$STAGE_DIR/LICENSE"

rm -rf "$STAGE_DIR/akka-context"

write_bundle_readme
write_build_info
chmod +x "$STAGE_DIR/install.sh"

if [[ "$NO_ARCHIVE" == false ]]; then
  log "Creating archive $ARCHIVE_PATH"
  tar -C "$DIST_DIR" -czf "$ARCHIVE_PATH" "$BUNDLE_DIR_NAME"
fi

log "Bundle directory: $STAGE_DIR"
if [[ "$NO_ARCHIVE" == false ]]; then
  log "Bundle archive:   $ARCHIVE_PATH"
fi
log "Done"
