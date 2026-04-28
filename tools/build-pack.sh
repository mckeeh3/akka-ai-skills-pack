#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd -- "$SCRIPT_DIR/.." && pwd)"
DIST_DIR="$REPO_ROOT/dist"
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
  - the bundle contains install.sh, manifests, selected pack-facing docs, skills, a pack-facing AGENTS source file, and reference examples
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
  remote_url="$(git -C "$REPO_ROOT" config --get remote.origin.url 2>/dev/null || true)"
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
- selected pack-facing docs under docs/
- pack-facing AGENTS guidance source under pack/AGENTS.md
- example-set README source under pack/EXAMPLES-README.md
- repository skills under skills/
- Akka SDK Java reference examples exported from src/
- repository pom.xml and example-set README

## Excluded
- akka-context/

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
source_repo=${REPO_ROOT}
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
  docs/pending-question-queue.md
  docs/pending-task-queue.md
  docs/prd-to-akka-flow.md
  docs/service-to-service-consumers.md
  docs/service-to-service-views.md
  docs/solution-plan-to-implementation-queue.md
  docs/timer-pattern-selection.md
  docs/web-ui-api-contract-patterns.md
  docs/web-ui-frontend-decomposition.md
  docs/web-ui-style-guide.md
  docs/images/web-ui-theme-1.png
  docs/images/web-ui-theme-2.png
  docs/images/web-ui-theme-3.png
  docs/images/web-ui-theme-4.png
  docs/images/web-ui-theme-5.png
  docs/web-ui-lightweight-typescript-architecture.md
  docs/web-ui-pattern-selection.md
  docs/web-ui-quality-checklist.md
  docs/workflow-endpoint-pattern.md
)

validate_source_tree() {
  local required_paths=(
    "$REPO_ROOT/skills/README.md"
    "$REPO_ROOT/skills/references/akka-entity-comparison.md"
    "$REPO_ROOT/pom.xml"
    "$REPO_ROOT/package.json"
    "$REPO_ROOT/tsconfig.web-ui.json"
    "$REPO_ROOT/README.md"
    "$REPO_ROOT/LICENSE"
    "$REPO_ROOT/pack/README.md"
    "$REPO_ROOT/pack/AGENTS.md"
    "$REPO_ROOT/pack/EXAMPLES-README.md"
    "$REPO_ROOT/pack/manifest.schema.yaml"
    "$INSTALLER_TEMPLATE"
  )

  for path in "${required_paths[@]}"; do
    [[ -e "$path" ]] || fail "Required source path not found: $path"
  done

  while IFS= read -r skill_dir; do
    [[ -n "$skill_dir" ]] || continue
    [[ -f "$skill_dir/SKILL.md" ]] || fail "Skill directory missing SKILL.md: $skill_dir"
  done < <(find "$REPO_ROOT/skills" -mindepth 1 -maxdepth 1 -type d ! -name references | sort)

  for path in "${PACK_DOC_FILES[@]}"; do
    [[ -f "$REPO_ROOT/$path" ]] || fail "Required pack doc not found: $REPO_ROOT/$path"
  done
}

copy_tree() {
  local src="$1"
  local dest="$2"
  mkdir -p "$(dirname "$dest")"
  cp -R "$src" "$dest"
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

[[ -f "$REPO_ROOT/pack/manifest.yaml" ]] || fail "Missing pack/manifest.yaml"
[[ -f "$REPO_ROOT/install.sh" ]] || fail "Missing install.sh"
[[ -d "$REPO_ROOT/skills" ]] || fail "Missing skills"
[[ -d "$REPO_ROOT/src" ]] || fail "Missing src"

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
' "$REPO_ROOT/pack/manifest.yaml")"

PACK_VERSION="$(awk '
  $0 ~ /^metadata:/ { in_metadata=1; next }
  in_metadata && $0 ~ /^  version:/ { print $2; exit }
' "$REPO_ROOT/pack/manifest.yaml")"

[[ -n "$PACK_NAME" ]] || fail "Could not read metadata.name from pack/manifest.yaml"
[[ -n "$PACK_VERSION" ]] || fail "Could not read metadata.version from pack/manifest.yaml"

RELEASE_TAG="v${PACK_VERSION}"
BUNDLE_DIR_NAME="${PACK_NAME}-${PACK_VERSION}"
STAGE_DIR="$DIST_DIR/$BUNDLE_DIR_NAME"
ARCHIVE_PATH="$DIST_DIR/${BUNDLE_DIR_NAME}.tar.gz"
INSTALLER_TEMPLATE="$REPO_ROOT/tools/install-release-template.sh"
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

copy_tree "$REPO_ROOT/skills" "$STAGE_DIR/skills"
copy_tree "$REPO_ROOT/pack" "$STAGE_DIR/pack"
copy_tree "$REPO_ROOT/src" "$STAGE_DIR/src"
cp "$REPO_ROOT/install.sh" "$STAGE_DIR/install.sh"
cp "$REPO_ROOT/pom.xml" "$STAGE_DIR/pom.xml"
cp "$REPO_ROOT/package.json" "$STAGE_DIR/package.json"
cp "$REPO_ROOT/tsconfig.web-ui.json" "$STAGE_DIR/tsconfig.web-ui.json"
cp "$REPO_ROOT/README.md" "$STAGE_DIR/README.md"
cp "$REPO_ROOT/LICENSE" "$STAGE_DIR/LICENSE"

mkdir -p "$STAGE_DIR/docs"
for path in "${PACK_DOC_FILES[@]}"; do
  mkdir -p "$STAGE_DIR/$(dirname "$path")"
  cp "$REPO_ROOT/$path" "$STAGE_DIR/$path"
done

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
