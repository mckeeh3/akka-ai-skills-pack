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

Installed scaffold command:
  .agents/bin/scaffold-ai-first-saas-starter.sh --target /path/to/empty-project --app-name "My App" --base-package ai.first

Notes:
  - This installer always installs the full packaged skill library, repository docs, Java examples, frontend workstream reference examples, starter template resources, and pack-facing AGENTS.md guidance
  - This installer uses cross-harness locations, not project-local .pi directories
  - project mode installs into <project-root>/.agents and offers to install pack guidance at <project-root>/AGENTS.md
  - if <project-root>/AGENTS.md exists, choose replace, append, or skip; --force replaces it
  - akka-context is intentionally NOT installed
  - repo-internal maintainer guidance files are NOT installed
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
[[ -f "$REPO_ROOT/pack/AGENTS.md" ]] || fail "Expected pack-facing AGENTS at $REPO_ROOT/pack/AGENTS.md"
[[ -f "$REPO_ROOT/pack/EXAMPLES-README.md" ]] || fail "Expected examples README at $REPO_ROOT/pack/EXAMPLES-README.md"
[[ -d "$REPO_ROOT/docs" ]] || fail "Expected docs at $REPO_ROOT/docs"
[[ -d "$REPO_ROOT/src" ]] || fail "Expected Java examples under $REPO_ROOT/src"
[[ -d "$REPO_ROOT/frontend" ]] || fail "Expected frontend examples under $REPO_ROOT/frontend"
[[ -d "$REPO_ROOT/templates/ai-first-saas-starter" ]] || fail "Expected starter template under $REPO_ROOT/templates/ai-first-saas-starter"
[[ -f "$REPO_ROOT/tools/scaffold-ai-first-saas-starter.sh" ]] || fail "Expected starter scaffold script at $REPO_ROOT/tools/scaffold-ai-first-saas-starter.sh"
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

copy_frontend_reference() {
  local src="$1"
  local dest="$2"
  if [[ -e "$dest" ]]; then
    run_cmd rm -rf "$dest"
  fi
  ensure_dir "$dest"
  copy_file "$src/README.md" "$dest/README.md"
  copy_file "$src/package.json" "$dest/package.json"
  copy_file "$src/package-lock.json" "$dest/package-lock.json"
  copy_file "$src/tsconfig.json" "$dest/tsconfig.json"
  copy_file "$src/vite.config.ts" "$dest/vite.config.ts"
  copy_file "$src/index.html" "$dest/index.html"
  copy_file "$src/.env.example" "$dest/.env.example"
  copy_dir_replace "$src/public" "$dest/public"
  copy_dir_replace "$src/src" "$dest/src"
}

prompt_project_agents_action() {
  local dest="$1"

  if [[ "$FORCE" == true ]]; then
    printf 'replace\n'
    return
  fi

  if [[ ! -t 0 ]]; then
    fail "$dest already exists and interactive input is unavailable. Re-run with --force to replace it, or run interactively to choose replace, append, or skip."
  fi

  while true; do
    cat >&2 <<EOF
Project guidance file already exists:
  $dest

Choose how to install Akka AI skills pack project guidance:
  1) replace existing AGENTS.md
  2) append pack guidance to existing AGENTS.md
  3) skip / do not copy project AGENTS.md
EOF
    read -r -p "Enter choice [1-3]: " choice
    case "$choice" in
      1|r|R|replace)
        printf 'replace\n'
        return
        ;;
      2|a|A|append)
        printf 'append\n'
        return
        ;;
      3|s|S|skip)
        printf 'skip\n'
        return
        ;;
      *)
        warn "Invalid choice: $choice"
        ;;
    esac
  done
}

install_project_agents_guidance() {
  [[ "$LOCATION" == "project" ]] || return

  local src="$AGENTS_ROOT/AGENTS.md"
  local dest="$PROJECT_ROOT/AGENTS.md"
  local action="replace"

  if [[ -e "$dest" ]]; then
    action="$(prompt_project_agents_action "$dest")"
  fi

  case "$action" in
    replace)
      log "Installing project guidance: $dest"
      copy_file "$src" "$dest"
      ;;
    append)
      log "Appending project guidance: $dest"
      if [[ "$DRY_RUN" == true ]]; then
        printf '[dry-run] append %q to %q\n' "$src" "$dest"
      else
        {
          printf '\n\n---\n\n'
          printf '<!-- Begin Akka AI skills pack guidance -->\n\n'
          cat "$src"
          printf '\n<!-- End Akka AI skills pack guidance -->\n'
        } >> "$dest"
      fi
      ;;
    skip)
      log "Skipping project guidance: $dest"
      ;;
    *)
      fail "Unsupported project guidance action: $action"
      ;;
  esac
}

rewrite_installed_docs() {
  if [[ "$DRY_RUN" == true ]]; then
    log "Would rewrite installed docs under $DOCS_DIR"
    return
  fi

  python3 - "$DOCS_DIR" <<'PY'
from pathlib import Path
import os
import re
import sys


def topic_note(raw: str, indent: str) -> str:
    topic = Path(raw).name.replace('.html.md', '').replace('.md', '')
    topic = topic.replace('-', ' ')
    topic = ' '.join(word.capitalize() for word in topic.split())
    return f"{indent}- Official Akka SDK docs: {topic} (not bundled with this pack)"


docs_dir = Path(sys.argv[1])
for doc_file in docs_dir.rglob('*.md'):
    rel_parent = doc_file.parent.relative_to(docs_dir)
    depth = len(rel_parent.parts)
    up = '/'.join(['..'] * (depth + 1))
    examples_prefix = f"{up}/resources/examples/java/"
    frontend_prefix = f"{up}/resources/examples/frontend/"
    skills_prefix = f"{up}/skills/"

    def replace_doc_backtick(match: re.Match) -> str:
        target = match.group(1)
        target_path = docs_dir / target
        return f"`{Path(os.path.relpath(target_path, doc_file.parent))}`"

    lines = []
    for line in doc_file.read_text().splitlines():
        stripped = line.strip()
        indent = line[: len(line) - len(line.lstrip())]

        if 'akka-context/' in line:
            if stripped.startswith('- `') and stripped.endswith('`'):
                raw = stripped[3:-1]
                line = topic_note(raw, indent)
            else:
                line = f"{indent}Official Akka SDK docs for this topic (not bundled with this pack)"

        line = re.sub(r'`(?:\.\./)?docs/([^`]+)`', replace_doc_backtick, line)
        line = line.replace('`src/', f'`{examples_prefix}src/')
        line = line.replace(' src/', f' {examples_prefix}src/')
        line = re.sub(r'`(?:\.\./)+frontend/([^`]+)`', lambda m: f'`{frontend_prefix}{m.group(1)}`', line)
        line = re.sub(r'`frontend/([^`]+)`', lambda m: f'`{frontend_prefix}{m.group(1)}`', line)
        line = line.replace('`skills/', f'`{skills_prefix}')
        line = line.replace(' skills/', f' {skills_prefix}')

        lines.append(line)

    doc_file.write_text('\n'.join(lines) + '\n')
PY
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
    ("../../src/", "../../resources/examples/java/src/"),
    ("../../../frontend/", "../../resources/examples/frontend/"),
    ("../../frontend/", "../../resources/examples/frontend/"),
    ("../../../docs/", "../../docs/"),
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

def normalize_repo_internal_reference(line: str):
    stripped = line.strip()
    indent = line[: len(line) - len(line.lstrip())]

    if (
        stripped.startswith("- `../../../") or stripped.startswith("- `../../")
    ) and "repo mental model and session bootstrap" in stripped:
        return None
    if "../../../AGENTS.md" in stripped or "../../AGENTS.md" in stripped:
        return f"{indent}- `../../AGENTS.md` for installed-pack usage guidance and routing expectations"
    if "../../../skills/README.md" in stripped or "../README.md" in stripped:
        return f"{indent}- `../README.md` for routing across the installed skill library"
    return line

def rewrite_skill_text(path: Path, frontend_prefix: str) -> None:
    text = path.read_text()
    for source, target in example_rewrites:
        text = text.replace(source, target)
    text = text.replace('`../frontend/', f'`{frontend_prefix}')
    text = text.replace('`frontend/', f'`{frontend_prefix}')
    normalized_lines = []
    for line in text.splitlines():
        line = normalize_akka_context_reference(line)
        line = normalize_repo_internal_reference(line)
        if line is not None:
            normalized_lines.append(line)
    path.write_text("\n".join(normalized_lines) + "\n")

for skill_file in skills_dir.glob("*/SKILL.md"):
    rewrite_skill_text(skill_file, '../../resources/examples/frontend/')

readme = skills_dir / 'README.md'
if readme.exists():
    rewrite_skill_text(readme, '../resources/examples/frontend/')
PY
}

[[ -n "$LOCATION" ]] || prompt_for_location
if [[ "$LOCATION" == "project" ]]; then
  mkdir -p "$PROJECT_ROOT"
  PROJECT_ROOT="$(cd "$PROJECT_ROOT" && pwd)"
fi
AGENTS_ROOT="$(resolve_location "$LOCATION")"
SKILLS_DIR="$AGENTS_ROOT/skills"
DOCS_DIR="$AGENTS_ROOT/docs"
MANIFESTS_DIR="$AGENTS_ROOT/manifests"
BIN_DIR="$AGENTS_ROOT/bin"
JAVA_EXAMPLES_DIR="$AGENTS_ROOT/resources/examples/java"
FRONTEND_EXAMPLES_DIR="$AGENTS_ROOT/resources/examples/frontend"
TEMPLATE_RESOURCES_DIR="$AGENTS_ROOT/resources/templates"
STARTER_TEMPLATE_DIR="$TEMPLATE_RESOURCES_DIR/ai-first-saas-starter"
PACK_MANIFEST_TARGET="$MANIFESTS_DIR/akka-ai-skills-pack.yaml"

# Install the complete docs tree dynamically so newly added docs are bundled
# and docs removed from the source repository are removed from existing installs.
log "Installing full pack content"
log "Install mode:      $LOCATION"
if [[ "$LOCATION" == "project" ]]; then
  log "Project root:      $PROJECT_ROOT"
fi
log "Install root:      $AGENTS_ROOT"
log "Pack source:       $REPO_ROOT"

ensure_dir "$SKILLS_DIR"
ensure_dir "$DOCS_DIR"
ensure_dir "$MANIFESTS_DIR"
ensure_dir "$BIN_DIR"
ensure_dir "$JAVA_EXAMPLES_DIR"
ensure_dir "$FRONTEND_EXAMPLES_DIR"
ensure_dir "$TEMPLATE_RESOURCES_DIR"

copy_file "$REPO_ROOT/pack/manifest.yaml" "$PACK_MANIFEST_TARGET"
copy_file "$REPO_ROOT/pack/AGENTS.md" "$AGENTS_ROOT/AGENTS.md"
copy_file "$REPO_ROOT/tools/scaffold-ai-first-saas-starter.sh" "$BIN_DIR/scaffold-ai-first-saas-starter.sh"
if [[ "$DRY_RUN" == true ]]; then
  printf '[dry-run] chmod +x %q\n' "$BIN_DIR/scaffold-ai-first-saas-starter.sh"
else
  chmod +x "$BIN_DIR/scaffold-ai-first-saas-starter.sh"
fi
install_project_agents_guidance
copy_file "$REPO_ROOT/skills/README.md" "$SKILLS_DIR/README.md"
copy_dir_replace "$REPO_ROOT/skills/references" "$SKILLS_DIR/references"

while IFS= read -r skill_dir; do
  [[ -n "$skill_dir" ]] || continue
  skill_name="$(basename "$skill_dir")"
  copy_dir_replace "$skill_dir" "$SKILLS_DIR/$skill_name"
done < <(find "$REPO_ROOT/skills" -mindepth 1 -maxdepth 1 -type d ! -name references | sort)

copy_dir_replace "$REPO_ROOT/docs" "$DOCS_DIR"

copy_file "$REPO_ROOT/pom.xml" "$JAVA_EXAMPLES_DIR/pom.xml"
copy_file "$REPO_ROOT/pack/EXAMPLES-README.md" "$JAVA_EXAMPLES_DIR/README.md"
copy_dir_replace "$REPO_ROOT/src/main" "$JAVA_EXAMPLES_DIR/src/main"
copy_dir_replace "$REPO_ROOT/src/test" "$JAVA_EXAMPLES_DIR/src/test"
copy_frontend_reference "$REPO_ROOT/frontend" "$FRONTEND_EXAMPLES_DIR"
copy_dir_replace "$REPO_ROOT/templates/ai-first-saas-starter" "$STARTER_TEMPLATE_DIR"

rewrite_installed_docs
rewrite_installed_skills

if [[ "$DRY_RUN" == true ]]; then
  log "Dry run complete"
else
  log "Install complete"
  log "Installed pack guidance:    $AGENTS_ROOT/AGENTS.md"
  if [[ "$LOCATION" == "project" ]]; then
    log "Project guidance target: $PROJECT_ROOT/AGENTS.md"
  fi
  log "Installed docs:             $DOCS_DIR"
  log "Installed manifest: $PACK_MANIFEST_TARGET"
  log "Installed skills:   $SKILLS_DIR"
  log "Installed Java examples:      $JAVA_EXAMPLES_DIR"
  log "Installed frontend examples:  $FRONTEND_EXAMPLES_DIR"
  log "Installed starter template:    $STARTER_TEMPLATE_DIR"
  log "Installed scaffold command:    $BIN_DIR/scaffold-ai-first-saas-starter.sh"
fi
