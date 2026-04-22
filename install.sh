#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$SCRIPT_DIR"
PROJECT_ROOT="$(pwd)"
LOCATION=""
DRY_RUN=false
FORCE=false

print_help() {
  cat <<'EOF'
Install the Akka AI skills pack into a cross-harness agents directory.

Usage:
  ./install.sh [options]

Options:
  --location <mode>   Install location: project or global
                      project -> <project-root>/.agents
                      global  -> ~/.agents
                      If omitted, the installer prompts interactively.
  --project <dir>     Project root used for project mode. Default: current directory
  --force             Replace existing pack-owned files
  --dry-run           Show planned actions without writing files
  --help              Show this help text

Notes:
  - This installer always installs the full packaged skill library, references, and examples
  - This installer uses cross-harness locations, not project-local .pi directories
  - project mode installs into <project-root>/.agents
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
    --location)
      [[ $# -ge 2 ]] || fail "Missing value for --location"
      LOCATION="$2"
      shift 2
      ;;
    --project)
      [[ $# -ge 2 ]] || fail "Missing value for --project"
      PROJECT_ROOT="$2"
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
    --help|-h)
      print_help
      exit 0
      ;;
    *)
      fail "Unknown option: $1"
      ;;
  esac
done

[[ -d "$REPO_ROOT/skills" ]] || fail "Expected skills at $REPO_ROOT/skills"
[[ -f "$REPO_ROOT/pack/manifest.yaml" ]] || fail "Expected manifest at $REPO_ROOT/pack/manifest.yaml"
[[ -d "$REPO_ROOT/src" ]] || fail "Expected examples under $REPO_ROOT/src"
command -v python3 >/dev/null 2>&1 || fail "python3 is required"

resolve_location() {
  case "$1" in
    project)
      printf '%s\n' "$PROJECT_ROOT/.agents"
      ;;
    global)
      printf '%s\n' "$HOME/.agents"
      ;;
    *)
      fail "Unsupported --location '$1'. Use project or global."
      ;;
  esac
}

prompt_for_location() {
  if [[ ! -t 0 ]]; then
    fail "No --location provided and interactive input is unavailable. Use --location project or --location global."
  fi

  while true; do
    cat <<EOF
Choose install location:
  1) project ($PROJECT_ROOT/.agents)
  2) global  ($HOME/.agents)
EOF
    read -r -p "Enter choice [1-2]: " choice
    case "$choice" in
      1)
        LOCATION="project"
        return
        ;;
      2)
        LOCATION="global"
        return
        ;;
      *)
        warn "Invalid choice: $choice"
        ;;
    esac
  done
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

[[ -n "$LOCATION" ]] || prompt_for_location
if [[ "$LOCATION" == "project" ]]; then
  mkdir -p "$PROJECT_ROOT"
  PROJECT_ROOT="$(cd "$PROJECT_ROOT" && pwd)"
fi
AGENTS_ROOT="$(resolve_location "$LOCATION")"
SKILLS_DIR="$AGENTS_ROOT/skills"
MANIFESTS_DIR="$AGENTS_ROOT/manifests"
EXAMPLES_DIR="$AGENTS_ROOT/resources/examples/java"
PACK_MANIFEST_TARGET="$MANIFESTS_DIR/akka-ai-skills-pack.yaml"

log "Installing full pack content"
log "Install mode:      $LOCATION"
if [[ "$LOCATION" == "project" ]]; then
  log "Project root:      $PROJECT_ROOT"
fi
log "Install root:      $AGENTS_ROOT"
log "Pack source:       $REPO_ROOT"

ensure_dir "$SKILLS_DIR"
ensure_dir "$MANIFESTS_DIR"
ensure_dir "$EXAMPLES_DIR"

copy_file "$REPO_ROOT/pack/manifest.yaml" "$PACK_MANIFEST_TARGET"
copy_file "$REPO_ROOT/skills/README.md" "$SKILLS_DIR/README.md"
copy_dir_replace "$REPO_ROOT/skills/references" "$SKILLS_DIR/references"

while IFS= read -r skill_dir; do
  [[ -n "$skill_dir" ]] || continue
  skill_name="$(basename "$skill_dir")"
  copy_dir_replace "$skill_dir" "$SKILLS_DIR/$skill_name"
done < <(find "$REPO_ROOT/skills" -mindepth 1 -maxdepth 1 -type d ! -name references | sort)

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
