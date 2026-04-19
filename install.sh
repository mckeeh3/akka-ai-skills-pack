#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$SCRIPT_DIR"
PROJECT_DIR="$(pwd)"
BUNDLE="entities-core"
DRY_RUN=false
FORCE=false
LIST_BUNDLES=false

print_help() {
  cat <<'EOF'
Install the Akka AI resource pack into a target project.

Usage:
  ./install.sh [options]

Options:
  --project <dir>     Target project directory. Default: current directory
  --bundle <name>     Bundle to install. Default: entities-core
  --force             Replace existing pack-owned files for the selected bundle
  --dry-run           Show planned actions without writing files
  --list-bundles      Print supported bundles and exit
  --help              Show this help text

Current bundles:
  all
  entities-core
  ese-core
  kve-core

Notes:
  - This first draft installs project-local resources under .pi/
  - akka-context is intentionally NOT installed
  - installed skill files are rewritten so they point to installed examples rather than repo-only src paths
EOF
}

log() {
  printf '[install] %s\n' "$*"
}

warn() {
  printf '[install][warn] %s\n' "$*" >&2
}

fail() {
  printf '[install][error] %s\n' "$*" >&2
  exit 1
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --project)
      [[ $# -ge 2 ]] || fail "Missing value for --project"
      PROJECT_DIR="$2"
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
    --help|-h)
      print_help
      exit 0
      ;;
    *)
      fail "Unknown option: $1"
      ;;
  esac
done

if [[ "$LIST_BUNDLES" == true ]]; then
  cat <<'EOF'
all
entities-core
ese-core
kve-core
EOF
  exit 0
fi

[[ -d "$REPO_ROOT/.pi/skills" ]] || fail "Expected skills at $REPO_ROOT/.pi/skills"
[[ -f "$REPO_ROOT/pack/manifest.yaml" ]] || fail "Expected manifest at $REPO_ROOT/pack/manifest.yaml"
[[ -d "$REPO_ROOT/src" ]] || fail "Expected examples under $REPO_ROOT/src"

mkdir -p "$PROJECT_DIR"
PROJECT_DIR="$(cd "$PROJECT_DIR" && pwd)"
PI_DIR="$PROJECT_DIR/.pi"
SKILLS_DIR="$PI_DIR/skills"
MANIFESTS_DIR="$PI_DIR/manifests"
EXAMPLES_DIR="$PI_DIR/resources/examples/java"
PACK_MANIFEST_TARGET="$MANIFESTS_DIR/akka-ai-pack.yaml"

bundle_skills() {
  case "$1" in
    all|entities-core)
      cat <<'EOF'
akka-entity-type-selection
akka-event-sourced-entities
akka-ese-domain-modeling
akka-ese-application-entity
akka-ese-edge-and-flow-patterns
akka-ese-doc-snippets
akka-ese-unit-testing
akka-ese-integration-testing
akka-ese-notifications
akka-ese-replication
akka-ese-ttl
akka-key-value-entities
akka-kve-domain-modeling
akka-kve-application-entity
akka-kve-edge-and-flow-patterns
akka-kve-doc-snippets
akka-kve-unit-testing
akka-kve-integration-testing
akka-kve-notifications
akka-kve-replication
akka-kve-ttl
EOF
      ;;
    ese-core)
      cat <<'EOF'
akka-entity-type-selection
akka-event-sourced-entities
akka-ese-domain-modeling
akka-ese-application-entity
akka-ese-edge-and-flow-patterns
akka-ese-doc-snippets
akka-ese-unit-testing
akka-ese-integration-testing
akka-ese-notifications
akka-ese-replication
akka-ese-ttl
EOF
      ;;
    kve-core)
      cat <<'EOF'
akka-entity-type-selection
akka-key-value-entities
akka-kve-domain-modeling
akka-kve-application-entity
akka-kve-edge-and-flow-patterns
akka-kve-doc-snippets
akka-kve-unit-testing
akka-kve-integration-testing
akka-kve-notifications
akka-kve-replication
akka-kve-ttl
EOF
      ;;
    *)
      fail "Unsupported bundle: $1"
      ;;
  esac
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

ensure_dir() {
  run_cmd mkdir -p "$1"
}

copy_file() {
  local src="$1"
  local dest="$2"
  ensure_dir "$(dirname "$dest")"
  run_cmd cp "$src" "$dest"
}

copy_dir_replace() {
  local src="$1"
  local dest="$2"
  if [[ -e "$dest" ]]; then
    if [[ "$FORCE" == true ]]; then
      run_cmd rm -rf "$dest"
    else
      run_cmd rm -rf "$dest"
    fi
  fi
  ensure_dir "$(dirname "$dest")"
  run_cmd cp -R "$src" "$dest"
}

rewrite_installed_skills() {
  if [[ "$DRY_RUN" == true ]]; then
    log "Would rewrite installed SKILL.md files under $SKILLS_DIR"
    return
  fi

  python3 - "$SKILLS_DIR" <<'PY'
from pathlib import Path
import re
import sys

skills_dir = Path(sys.argv[1])
example_rewrites = [
    ("../../../src/", "../../resources/examples/java/src/"),
]

def normalize_akka_context_reference(line: str) -> str:
    if "akka-context/" not in line:
        return line
    indent = line[: len(line) - len(line.lstrip())]
    stripped = line.strip()
    if stripped.startswith("- `") and stripped.endswith("`"):
        raw = stripped[3:-1]
        topic = Path(raw).name.replace(".html.md", "").replace('.md', '')
        topic = topic.replace('-', ' ')
        topic = ' '.join(word.capitalize() for word in topic.split())
        return f"{indent}- Official Akka SDK docs: {topic} (not bundled with this pack)"
    return f"{indent}Official Akka SDK docs for this topic (not bundled with this pack)"

for skill_file in skills_dir.glob("*/SKILL.md"):
    text = skill_file.read_text()
    for source, target in example_rewrites:
        text = text.replace(source, target)
    lines = [normalize_akka_context_reference(line) for line in text.splitlines()]
    skill_file.write_text("\n".join(lines) + "\n")
PY
}

selected_skills="$(bundle_skills "$BUNDLE")"

log "Installing bundle '$BUNDLE' into $PROJECT_DIR"
log "Pack source: $REPO_ROOT"

ensure_dir "$SKILLS_DIR"
ensure_dir "$MANIFESTS_DIR"
ensure_dir "$EXAMPLES_DIR"

copy_file "$REPO_ROOT/pack/manifest.yaml" "$PACK_MANIFEST_TARGET"
copy_file "$REPO_ROOT/.pi/skills/README.md" "$SKILLS_DIR/README.md"
copy_dir_replace "$REPO_ROOT/.pi/skills/references" "$SKILLS_DIR/references"

while IFS= read -r skill; do
  [[ -n "$skill" ]] || continue
  [[ -d "$REPO_ROOT/.pi/skills/$skill" ]] || fail "Missing skill directory: $skill"
  copy_dir_replace "$REPO_ROOT/.pi/skills/$skill" "$SKILLS_DIR/$skill"
done <<< "$selected_skills"

copy_file "$REPO_ROOT/pom.xml" "$EXAMPLES_DIR/pom.xml"
copy_file "$REPO_ROOT/README.md" "$EXAMPLES_DIR/README.md"
copy_dir_replace "$REPO_ROOT/src/main" "$EXAMPLES_DIR/src/main"
copy_dir_replace "$REPO_ROOT/src/test" "$EXAMPLES_DIR/src/test"

rewrite_installed_skills

if [[ "$DRY_RUN" == true ]]; then
  log "Dry run complete"
else
  log "Install complete"
  log "Installed manifest: $PACK_MANIFEST_TARGET"
  log "Installed skills:   $SKILLS_DIR"
  log "Installed examples: $EXAMPLES_DIR"
  warn "akka-context was not installed. Installed skills now refer to official Akka docs rather than local akka-context files."
fi
