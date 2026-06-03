#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
PACK_ROOT="$(cd -- "$SCRIPT_DIR/.." && pwd)"
APP_ROOT="$(cd -- "$PACK_ROOT/.." && pwd)"
DIST_DIR="$PACK_ROOT/dist"
CLEAN=false
NO_ARCHIVE=false
OUTPUT_DIR=""
GITHUB_REPO=""

print_help() {
  cat <<'EOF'
Build a versioned Akka AI skills pack release bundle.

Usage:
  bash tools/build-pack.sh [options]

Options:
  --output-dir <dir>   Output directory for the built bundle. Default: <repo>/dist
  --github-repo <r>    GitHub repo in owner/name form for generated release installer URLs.
                       Default: inferred from remote.origin.url
  --clean              Remove existing output directory contents for this bundle before building
  --no-archive         Keep the expanded bundle directory only; skip tar.gz creation
  --help               Show this help text

Notes:
  - akka-context is intentionally excluded from the bundle
  - the bundle contains install.sh, manifests, repository docs, skills, a pack-facing AGENTS source file, Java reference examples, and frontend workstream reference examples
  - installed skill rewriting still happens at install time via install.sh
  - a versioned GitHub release installer script is generated alongside the archive
EOF
}

log() {
  printf '[build-pack] %s\n' "$*"
}

fail() {
  printf '[build-pack][error] %s\n' "$*" >&2
  exit 1
}

infer_github_repo() {
  local remote_url
  remote_url="$(git -C "$APP_ROOT" config --get remote.origin.url 2>/dev/null || true)"
  [[ -n "$remote_url" ]] || return 1

  case "$remote_url" in
    https://github.com/*.git)
      printf '%s\n' "${remote_url#https://github.com/}" | sed 's/\.git$//'
      ;;
    https://github.com/*)
      printf '%s\n' "${remote_url#https://github.com/}"
      ;;
    git@github.com:*.git)
      printf '%s\n' "${remote_url#git@github.com:}" | sed 's/\.git$//'
      ;;
    git@github.com:*)
      printf '%s\n' "${remote_url#git@github.com:}"
      ;;
    *)
      return 1
      ;;
  esac
}

write_bundle_readme() {
  cat > "$STAGE_DIR/BUNDLE-README.md" <<EOF
# ${PACK_NAME} ${PACK_VERSION}

This is a build artifact for the Akka AI skills pack.

## Included
- install.sh
- pack manifests
- repository docs under docs/
- pack-facing AGENTS guidance source under pack/AGENTS.md
- example-set README source under pack/EXAMPLES-README.md
- repository skills under skills/
- Akka SDK Java reference examples exported from src/
- React/Vite workstream UI reference examples exported from frontend/
- repository pom.xml and example-set README
- frontend workstream UI reference source under frontend/

## Excluded
- akka-context/

The bundle no longer includes a duplicate full-app source copy; downstream core app work should use the repository root fork-and-extend model.

The akka-context directory is intentionally excluded from this bundle. Installed skills are rewritten
at install time so they point to installed examples and generic official Akka SDK documentation
notes instead of repo-local akka-context paths.

## Install into a project with the release installer

After publishing these files as GitHub release assets:

\`\`\`bash
curl -fsSL https://github.com/${GITHUB_REPO}/releases/download/${RELEASE_TAG}/install-${PACK_NAME}-${PACK_VERSION}.sh | bash -s -- --target-dir /path/to/project
\`\`\`

If \`--target-dir\` is omitted, the current directory is used.

## Install from the unpacked bundle

The bundled installer uses cross-harness locations:
- project mode: \`<project-root>/.agents\`
- global mode: \`~/.agents\`

From inside the unpacked bundle:

\`\`\`bash
bash install.sh --location project --project /path/to/project
\`\`\`

Or:

\`\`\`bash
bash install.sh --location global
\`\`\`

If \`--location\` is omitted, the installer prompts interactively.
If project mode is selected, the current directory is used as the project root unless \`--project\` is provided.

## Install contents

The built archive always contains the full packaged skill library, references, and examples.
There is no bundle selection during install.
EOF
}

write_build_info() {
  cat > "$STAGE_DIR/BUILD-INFO.txt" <<EOF
pack_name=${PACK_NAME}
pack_version=${PACK_VERSION}
release_tag=${RELEASE_TAG}
github_repo=${GITHUB_REPO}
built_at_utc=$(date -u +%Y-%m-%dT%H:%M:%SZ)
source_repo=${APP_ROOT}
pack_source=${PACK_ROOT}
archive_path=${ARCHIVE_PATH}
installer_path=${INSTALLER_PATH}
external_docs_bundled=false
install_profile=full
EOF
}

write_release_installer() {
  python3 - "$INSTALLER_TEMPLATE" "$INSTALLER_PATH" "$PACK_NAME" "$PACK_VERSION" "$GITHUB_REPO" <<'PY'
from pathlib import Path
import sys

template_path = Path(sys.argv[1])
output_path = Path(sys.argv[2])
pack_name = sys.argv[3]
pack_version = sys.argv[4]
github_repo = sys.argv[5]

text = template_path.read_text()
text = text.replace("__PACK_NAME__", pack_name)
text = text.replace("__PACK_VERSION__", pack_version)
text = text.replace("__GITHUB_REPO__", github_repo)
output_path.write_text(text)
PY
  chmod +x "$INSTALLER_PATH"
}

# Docs are copied dynamically from the repository docs/ tree during bundle build.
# Keep the source tree validation focused on top-level required paths so newly
# added docs are included automatically instead of requiring a hardcoded list.
validate_source_tree() {
  local required_paths=(
    "$PACK_ROOT/skills/README.md"
    "$PACK_ROOT/skills/references/akka-entity-comparison.md"
    "$APP_ROOT/pom.xml"
    "$APP_ROOT/README.md"
    "$APP_ROOT/LICENSE"
    "$PACK_ROOT/pack/README.md"
    "$PACK_ROOT/pack/AGENTS.md"
    "$PACK_ROOT/pack/EXAMPLES-README.md"
    "$PACK_ROOT/pack/manifest.schema.yaml"
    "$PACK_ROOT/examples/akka-components/README.md"
    "$PACK_ROOT/examples/akka-components/src/main"
    "$PACK_ROOT/examples/akka-components/src/test"
    "$APP_ROOT/frontend"
    "$INSTALLER_TEMPLATE"
  )

  for path in "${required_paths[@]}"; do
    [[ -e "$path" ]] || fail "Required source path not found: $path"
  done

  while IFS= read -r skill_dir; do
    [[ -n "$skill_dir" ]] || continue
    [[ -f "$skill_dir/SKILL.md" ]] || fail "Skill directory missing SKILL.md: $skill_dir"
  done < <(find "$PACK_ROOT/skills" -mindepth 1 -maxdepth 1 -type d ! -name references | sort)
}

copy_tree() {
  local src="$1"
  local dest="$2"
  mkdir -p "$(dirname "$dest")"
  cp -R "$src" "$dest"
}

copy_frontend_reference() {
  local src="$1"
  local dest="$2"
  mkdir -p "$dest"
  cp "$src/README.md" "$dest/README.md"
  cp "$src/package.json" "$dest/package.json"
  cp "$src/package-lock.json" "$dest/package-lock.json"
  cp "$src/tsconfig.json" "$dest/tsconfig.json"
  cp "$src/vite.config.ts" "$dest/vite.config.ts"
  cp "$src/index.html" "$dest/index.html"
  cp "$src/.env.example" "$dest/.env.example"
  cp -R "$src/public" "$dest/public"
  cp -R "$src/src" "$dest/src"
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --output-dir)
      [[ $# -ge 2 ]] || fail "Missing value for --output-dir"
      OUTPUT_DIR="$2"
      shift 2
      ;;
    --github-repo)
      [[ $# -ge 2 ]] || fail "Missing value for --github-repo"
      GITHUB_REPO="$2"
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

[[ -f "$PACK_ROOT/pack/manifest.yaml" ]] || fail "Missing pack/manifest.yaml"
[[ -f "$PACK_ROOT/install.sh" ]] || fail "Missing skills-pack/install.sh"
[[ -d "$PACK_ROOT/skills" ]] || fail "Missing skills"
[[ -d "$PACK_ROOT/examples/akka-components/src" ]] || fail "Missing examples/akka-components/src"

if [[ -z "$GITHUB_REPO" ]]; then
  GITHUB_REPO="$(infer_github_repo || true)"
fi
[[ -n "$GITHUB_REPO" ]] || fail "Could not determine GitHub repo. Use --github-repo owner/name"
[[ "$GITHUB_REPO" == */* ]] || fail "Invalid --github-repo value: $GITHUB_REPO"

if [[ -n "$OUTPUT_DIR" ]]; then
  DIST_DIR="$OUTPUT_DIR"
fi
mkdir -p "$DIST_DIR"

PACK_NAME="$(awk '
  $0 ~ /^metadata:/ { in_metadata=1; next }
  in_metadata && $0 ~ /^  name:/ { print $2; exit }
' "$PACK_ROOT/pack/manifest.yaml")"

PACK_VERSION="$(awk '
  $0 ~ /^metadata:/ { in_metadata=1; next }
  in_metadata && $0 ~ /^  version:/ { print $2; exit }
' "$PACK_ROOT/pack/manifest.yaml")"

[[ -n "$PACK_NAME" ]] || fail "Could not read metadata.name from pack/manifest.yaml"
[[ -n "$PACK_VERSION" ]] || fail "Could not read metadata.version from pack/manifest.yaml"

RELEASE_TAG="v${PACK_VERSION}"
BUNDLE_DIR_NAME="${PACK_NAME}-${PACK_VERSION}"
STAGE_DIR="$DIST_DIR/$BUNDLE_DIR_NAME"
ARCHIVE_PATH="$DIST_DIR/${BUNDLE_DIR_NAME}.tar.gz"
INSTALLER_TEMPLATE="$PACK_ROOT/tools/install-release-template.sh"
INSTALLER_PATH="$DIST_DIR/install-${PACK_NAME}-${PACK_VERSION}.sh"

if [[ "$CLEAN" == true ]]; then
  log "Cleaning previous outputs for $BUNDLE_DIR_NAME"
  rm -rf "$STAGE_DIR" "$ARCHIVE_PATH" "$INSTALLER_PATH"
fi

[[ ! -e "$STAGE_DIR" ]] || fail "Stage directory already exists: $STAGE_DIR (use --clean to replace it)"
[[ ! -e "$ARCHIVE_PATH" ]] || fail "Archive already exists: $ARCHIVE_PATH (use --clean to replace it)"
[[ ! -e "$INSTALLER_PATH" ]] || fail "Release installer already exists: $INSTALLER_PATH (use --clean to replace it)"

validate_source_tree

log "Building $BUNDLE_DIR_NAME"
log "GitHub repo: $GITHUB_REPO"
mkdir -p "$STAGE_DIR"

copy_tree "$PACK_ROOT/skills" "$STAGE_DIR/skills"
copy_tree "$PACK_ROOT/pack" "$STAGE_DIR/pack"
copy_tree "$PACK_ROOT/examples" "$STAGE_DIR/examples"
copy_frontend_reference "$APP_ROOT/frontend" "$STAGE_DIR/frontend"
mkdir -p "$STAGE_DIR/tools"
cp "$PACK_ROOT/install.sh" "$STAGE_DIR/install.sh"
cp "$APP_ROOT/pom.xml" "$STAGE_DIR/pom.xml"
cp "$APP_ROOT/README.md" "$STAGE_DIR/README.md"
cp "$APP_ROOT/LICENSE" "$STAGE_DIR/LICENSE"

copy_tree "$PACK_ROOT/docs" "$STAGE_DIR/docs"

rm -rf "$STAGE_DIR/akka-context"

write_bundle_readme
write_build_info
write_release_installer
chmod +x "$STAGE_DIR/install.sh"

if [[ "$NO_ARCHIVE" == false ]]; then
  log "Creating archive $ARCHIVE_PATH"
  tar -C "$DIST_DIR" -czf "$ARCHIVE_PATH" "$BUNDLE_DIR_NAME"
fi

log "Bundle directory:  $STAGE_DIR"
if [[ "$NO_ARCHIVE" == false ]]; then
  log "Bundle archive:    $ARCHIVE_PATH"
fi
log "Release installer: $INSTALLER_PATH"
log "Done"
