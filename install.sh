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
  - This installer always installs the full packaged skill library, selected pack-facing docs, examples, and pack-facing AGENTS.md guidance
  - This installer uses cross-harness locations, not project-local .pi directories
  - project mode installs into <project-root>/.agents
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

    if stripped.startswith("- `../../../") and "repo mental model and session bootstrap" in stripped:
        return None
    if "../../../AGENTS.md" in stripped:
        return f"{indent}- `../../AGENTS.md` for installed-pack usage guidance and routing expectations"
    if "../../../skills/README.md" in stripped:
        return f"{indent}- `../README.md` for routing across the installed skill library"
    return line

for skill_file in skills_dir.glob("*/SKILL.md"):
    text = skill_file.read_text()
    for source, target in example_rewrites:
        text = text.replace(source, target)
    normalized_lines = []
    for line in text.splitlines():
        line = normalize_akka_context_reference(line)
        line = normalize_repo_internal_reference(line)
        if line is not None:
            normalized_lines.append(line)
    skill_file.write_text("\n".join(normalized_lines) + "\n")
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
EXAMPLES_DIR="$AGENTS_ROOT/resources/examples/java"
PACK_MANIFEST_TARGET="$MANIFESTS_DIR/akka-ai-skills-pack.yaml"

PACK_DOC_FILES=(
  docs/app-description-end-to-end-workflow-example.md
  docs/app-description-maintenance-flow.md
  docs/app-description-skills-plan-backlog.md
  docs/description-first-application-doctrine.md
  docs/internal-app-description-architecture.md
  docs/agent-coverage-matrix.md
  docs/agent-runtime-state-reference.md
  docs/consumer-reference.md
  docs/examples/purchase-request-app-description/README.md
  docs/examples/purchase-request-app-description/app-description/00-system/app-manifest.md
  docs/examples/purchase-request-app-description/app-description/00-system/generation-policy.md
  docs/examples/purchase-request-app-description/app-description/00-system/readiness-status.md
  docs/examples/purchase-request-app-description/app-description/10-capabilities/01-submit-and-approve-purchase-requests.md
  docs/examples/purchase-request-app-description/app-description/10-capabilities/capabilities-index.md
  docs/examples/purchase-request-app-description/app-description/20-behavior/behavior-index.md
  docs/examples/purchase-request-app-description/app-description/20-behavior/flows/01-submission-and-approval-flow.md
  docs/examples/purchase-request-app-description/app-description/20-behavior/rules/01-edit-and-approval-rules.md
  docs/examples/purchase-request-app-description/app-description/20-behavior/state-models/01-purchase-request-lifecycle.md
  docs/examples/purchase-request-app-description/app-description/30-tests/acceptance/01-purchase-request-acceptance.md
  docs/examples/purchase-request-app-description/app-description/30-tests/negative/01-forbidden-actions.md
  docs/examples/purchase-request-app-description/app-description/30-tests/operational/01-audit-and-diagnosability.md
  docs/examples/purchase-request-app-description/app-description/30-tests/regression/01-repeat-actions.md
  docs/examples/purchase-request-app-description/app-description/30-tests/test-index.md
  docs/examples/purchase-request-app-description/app-description/40-auth-security/data-protection.md
  docs/examples/purchase-request-app-description/app-description/40-auth-security/identity-and-authorization.md
  docs/examples/purchase-request-app-description/app-description/50-observability/logs-metrics-traces-and-alerts.md
  docs/examples/purchase-request-app-description/app-description/60-generation/output-surfaces.md
  docs/examples/purchase-request-app-description/app-description/60-generation/realization-scope.md
  docs/examples/purchase-request-app-description/app-description/60-generation/regeneration-map.md
  docs/examples/purchase-request-app-description/app-description/70-traceability/behavior-to-tests-map.md
  docs/examples/purchase-request-app-description/app-description/70-traceability/capability-to-behavior-map.md
  docs/examples/purchase-request-app-description/app-description/70-traceability/change-impact-map.md
  docs/examples/purchase-request-app-description/app-description/80-review/latest-change-summary.md
  docs/examples/purchase-request-app-description/app-description/80-review/latest-readiness-summary.md
  docs/examples/purchase-request-app-description/normalized-input-example.md
  docs/examples/purchase-request-prd.md
  docs/examples/purchase-request-solution-plan.md
  docs/examples/purchase-request-pending-tasks.md
  docs/intent-driven-usage-flow.md
  docs/pending-task-queue.md
  docs/prd-to-akka-flow.md
  docs/service-to-service-consumers.md
  docs/service-to-service-views.md
  docs/solution-plan-to-implementation-queue.md
  docs/timer-pattern-selection.md
  docs/web-ui-pattern-selection.md
  docs/workflow-endpoint-pattern.md
)

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
ensure_dir "$EXAMPLES_DIR"

copy_file "$REPO_ROOT/pack/manifest.yaml" "$PACK_MANIFEST_TARGET"
copy_file "$REPO_ROOT/pack/AGENTS.md" "$AGENTS_ROOT/AGENTS.md"
copy_file "$REPO_ROOT/skills/README.md" "$SKILLS_DIR/README.md"
copy_dir_replace "$REPO_ROOT/skills/references" "$SKILLS_DIR/references"

while IFS= read -r skill_dir; do
  [[ -n "$skill_dir" ]] || continue
  skill_name="$(basename "$skill_dir")"
  copy_dir_replace "$skill_dir" "$SKILLS_DIR/$skill_name"
done < <(find "$REPO_ROOT/skills" -mindepth 1 -maxdepth 1 -type d ! -name references | sort)

for doc_file in "${PACK_DOC_FILES[@]}"; do
  copy_file "$REPO_ROOT/$doc_file" "$AGENTS_ROOT/$doc_file"
done

copy_file "$REPO_ROOT/pom.xml" "$EXAMPLES_DIR/pom.xml"
copy_file "$REPO_ROOT/pack/EXAMPLES-README.md" "$EXAMPLES_DIR/README.md"
copy_dir_replace "$REPO_ROOT/src/main" "$EXAMPLES_DIR/src/main"
copy_dir_replace "$REPO_ROOT/src/test" "$EXAMPLES_DIR/src/test"

rewrite_installed_docs
rewrite_installed_skills

if [[ "$DRY_RUN" == true ]]; then
  log "Dry run complete"
else
  log "Install complete"
  log "Installed guidance: $AGENTS_ROOT/AGENTS.md"
  log "Installed docs:     $DOCS_DIR"
  log "Installed manifest: $PACK_MANIFEST_TARGET"
  log "Installed skills:   $SKILLS_DIR"
  log "Installed examples: $EXAMPLES_DIR"
fi
