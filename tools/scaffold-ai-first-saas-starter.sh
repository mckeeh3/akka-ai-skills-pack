#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
TARGET_DIR="$(pwd)"
TEMPLATE_DIR=""
APP_NAME="AI First SaaS Starter"
APP_SLUG="ai-first-saas-starter"
BASE_PACKAGE=""
MAVEN_GROUP_ID=""
DRY_RUN=false
FORCE_EMPTY=false
FORCE_OVERWRITE=false
YES=false

print_help() {
  cat <<'EOF'
Scaffold the AI-first SaaS starter app from the installed skills-pack template.

Usage:
  scaffold-ai-first-saas-starter.sh [options]

Options:
  --target <dir>          Target project directory. Default: current directory
  --template-dir <dir>    Template directory override. Default: installed pack resources/templates/ai-first-saas-starter
  --app-name <name>       Human-readable app name. Default: AI First SaaS Starter
  --app-slug <slug>       Lower-kebab app/project slug. Default: ai-first-saas-starter
  --base-package <pkg>    Java base package. If omitted, prompts; Enter uses ai.first
  --maven-group-id <id>   Maven group id. Default: same as base package
  --dry-run               Print planned writes and conflicts without writing files
  --force-empty           Allow safe bootstrap files such as .git, .agents, AGENTS.md, README.md, empty docs/specs/app-description
  --force-overwrite       Allow overwriting conflicting rendered paths after confirmation
  --yes                   Non-interactive confirmation for --force-overwrite
  --help                  Show this help text

Default behavior is fail-closed: existing application files or rendered-path conflicts stop scaffolding.
EOF
}

fail() {
  printf '[scaffold][error] %s\n' "$*" >&2
  exit 1
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --target)
      [[ $# -ge 2 ]] || fail "Missing value for --target"
      TARGET_DIR="$2"
      shift 2
      ;;
    --template-dir)
      [[ $# -ge 2 ]] || fail "Missing value for --template-dir"
      TEMPLATE_DIR="$2"
      shift 2
      ;;
    --app-name)
      [[ $# -ge 2 ]] || fail "Missing value for --app-name"
      APP_NAME="$2"
      shift 2
      ;;
    --app-slug)
      [[ $# -ge 2 ]] || fail "Missing value for --app-slug"
      APP_SLUG="$2"
      shift 2
      ;;
    --base-package)
      [[ $# -ge 2 ]] || fail "Missing value for --base-package"
      BASE_PACKAGE="$2"
      shift 2
      ;;
    --maven-group-id)
      [[ $# -ge 2 ]] || fail "Missing value for --maven-group-id"
      MAVEN_GROUP_ID="$2"
      shift 2
      ;;
    --dry-run)
      DRY_RUN=true
      shift
      ;;
    --force-empty)
      FORCE_EMPTY=true
      shift
      ;;
    --force-overwrite)
      FORCE_OVERWRITE=true
      shift
      ;;
    --yes)
      YES=true
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

if [[ -z "$TEMPLATE_DIR" ]]; then
  if [[ -d "$SCRIPT_DIR/../resources/templates/ai-first-saas-starter" ]]; then
    TEMPLATE_DIR="$SCRIPT_DIR/../resources/templates/ai-first-saas-starter"
  elif [[ -d "$SCRIPT_DIR/../templates/ai-first-saas-starter" ]]; then
    TEMPLATE_DIR="$SCRIPT_DIR/../templates/ai-first-saas-starter"
  else
    fail "Could not locate ai-first-saas-starter template. Use --template-dir."
  fi
fi

[[ -d "$TEMPLATE_DIR" ]] || fail "Template directory not found: $TEMPLATE_DIR"
command -v python3 >/dev/null 2>&1 || fail "python3 is required"

mkdir -p "$TARGET_DIR"
TARGET_DIR="$(cd "$TARGET_DIR" && pwd)"
TEMPLATE_DIR="$(cd "$TEMPLATE_DIR" && pwd)"

if [[ -z "$BASE_PACKAGE" ]]; then
  if [[ -t 0 ]]; then
    read -r -p "What Java base package should I use for generated code? Press Enter to use ai.first: " BASE_PACKAGE
    BASE_PACKAGE="${BASE_PACKAGE:-ai.first}"
  else
    BASE_PACKAGE="ai.first"
    printf '[scaffold] No --base-package provided and input is non-interactive; using deferred default %s\n' "$BASE_PACKAGE"
  fi
fi

if [[ -z "$MAVEN_GROUP_ID" ]]; then
  MAVEN_GROUP_ID="$BASE_PACKAGE"
fi

if [[ "$FORCE_OVERWRITE" == true && "$YES" != true && ! -t 0 ]]; then
  fail "--force-overwrite in non-interactive mode requires --yes"
fi

export TEMPLATE_DIR TARGET_DIR APP_NAME APP_SLUG BASE_PACKAGE MAVEN_GROUP_ID DRY_RUN FORCE_EMPTY FORCE_OVERWRITE YES

python3 <<'PY'
from __future__ import annotations

import os
import re
import shutil
import sys
from datetime import datetime, timezone
from pathlib import Path

TEMPLATE_DIR = Path(os.environ["TEMPLATE_DIR"])
TARGET_DIR = Path(os.environ["TARGET_DIR"])
APP_NAME = os.environ["APP_NAME"]
APP_SLUG = os.environ["APP_SLUG"]
BASE_PACKAGE = os.environ["BASE_PACKAGE"]
MAVEN_GROUP_ID = os.environ["MAVEN_GROUP_ID"]
DRY_RUN = os.environ["DRY_RUN"] == "true"
FORCE_EMPTY = os.environ["FORCE_EMPTY"] == "true"
FORCE_OVERWRITE = os.environ["FORCE_OVERWRITE"] == "true"
YES = os.environ["YES"] == "true"

if not re.fullmatch(r"[a-zA-Z_$][\w$]*(\.[a-zA-Z_$][\w$]*)*", BASE_PACKAGE):
    print(f"[scaffold][error] Invalid Java base package: {BASE_PACKAGE}", file=sys.stderr)
    sys.exit(1)
if not re.fullmatch(r"[a-z0-9]+(-[a-z0-9]+)*", APP_SLUG):
    print(f"[scaffold][error] Invalid app slug: {APP_SLUG}; expected lower-kebab", file=sys.stderr)
    sys.exit(1)

JAVA_PACKAGE_PATH = BASE_PACKAGE.replace(".", "/")
placeholders = {
    "{{APP_NAME}}": APP_NAME,
    "{{APP_SLUG}}": APP_SLUG,
    "{{JAVA_BASE_PACKAGE}}": BASE_PACKAGE,
    "{{JAVA_PACKAGE_PATH}}": JAVA_PACKAGE_PATH,
    "{{MAVEN_GROUP_ID}}": MAVEN_GROUP_ID,
}

allowed_files = {".gitignore", "AGENTS.md", "README.md"}
allowed_dirs = {".git", ".agents", "docs", "specs", "app-description"}
app_markers = {"pom.xml", "build.gradle", "build.gradle.kts", "package.json", "src", "frontend"}

def is_empty_dir(path: Path) -> bool:
    return path.is_dir() and not any(path.iterdir())

def target_has_only_bootstrap_files() -> tuple[bool, list[str]]:
    unsafe: list[str] = []
    for child in TARGET_DIR.iterdir():
        name = child.name
        if name in {".", ".."}:
            continue
        if name in app_markers:
            unsafe.append(name)
            continue
        if child.is_file() and name in allowed_files:
            continue
        if child.is_dir() and name in allowed_dirs:
            if name in {"docs", "specs", "app-description"} and not is_empty_dir(child):
                unsafe.append(f"{name}/ (non-empty)")
            continue
        unsafe.append(name + ("/" if child.is_dir() else ""))
    return (not unsafe, unsafe)

def render_text(value: str) -> str:
    for source, target in placeholders.items():
        value = value.replace(source, target)
    return value

def target_relative_path(source_relative: Path) -> Path | None:
    parts = source_relative.parts
    if parts[0] == "backend":
        return Path(*parts[1:])
    if parts[0] == "frontend":
        return source_relative
    if parts[0] in {"app-description", "specs"}:
        return source_relative
    if parts[0] == "README.md":
        return source_relative
    if parts[0] in {"TEMPLATE-MANIFEST.md", "scaffold-rules.md"}:
        return Path("specs") / "template" / source_relative
    return source_relative

writes: list[tuple[Path, Path]] = []
for source in sorted(TEMPLATE_DIR.rglob("*")):
    if not source.is_file():
        continue
    rel = source.relative_to(TEMPLATE_DIR)
    target_rel = target_relative_path(rel)
    if target_rel is None:
        continue
    rendered_rel = Path(render_text(str(target_rel)))
    writes.append((source, TARGET_DIR / rendered_rel))

safe_target, unsafe_entries = target_has_only_bootstrap_files()
if not safe_target and not FORCE_OVERWRITE:
    print("[scaffold][error] Target contains existing application or non-bootstrap files:", file=sys.stderr)
    for entry in unsafe_entries:
        print(f"  - {entry}", file=sys.stderr)
    print("Use an empty target, remove these files, or pass --force-overwrite deliberately.", file=sys.stderr)
    sys.exit(1)
if unsafe_entries and FORCE_EMPTY and not FORCE_OVERWRITE:
    print("[scaffold][error] --force-empty only allows safe bootstrap files; found unsafe entries:", file=sys.stderr)
    for entry in unsafe_entries:
        print(f"  - {entry}", file=sys.stderr)
    sys.exit(1)

report_path = TARGET_DIR / "specs" / "scaffold-report.md"
conflicts = [target for _, target in writes if target.exists()]
if report_path.exists():
    conflicts.append(report_path)
if conflicts and not FORCE_OVERWRITE:
    print("[scaffold][error] Rendered target paths already exist:", file=sys.stderr)
    for path in conflicts:
        print(f"  - {path.relative_to(TARGET_DIR)}", file=sys.stderr)
    print("Use --dry-run to inspect or --force-overwrite for deliberate replacement.", file=sys.stderr)
    sys.exit(1)

if FORCE_OVERWRITE and conflicts and not YES:
    print("[scaffold] The following paths will be overwritten:")
    for path in conflicts:
        print(f"  - {path.relative_to(TARGET_DIR)}")
    confirm = input("Type 'overwrite' to proceed: ")
    if confirm != "overwrite":
        print("[scaffold][error] Overwrite not confirmed", file=sys.stderr)
        sys.exit(1)

print(f"[scaffold] Template: {TEMPLATE_DIR}")
print(f"[scaffold] Target:   {TARGET_DIR}")
print(f"[scaffold] App:      {APP_NAME} ({APP_SLUG})")
print(f"[scaffold] Package:  {BASE_PACKAGE}")
print(f"[scaffold] Files:    {len(writes)}")

for source, target in writes:
    rel_target = target.relative_to(TARGET_DIR)
    if DRY_RUN:
        action = "overwrite" if target.exists() else "write"
        print(f"[dry-run] {action} {rel_target}")
        continue
    target.parent.mkdir(parents=True, exist_ok=True)
    try:
        text = source.read_text()
        target.write_text(render_text(text))
    except UnicodeDecodeError:
        shutil.copyfile(source, target)

report = report_path
report_text = "\n".join([
    "# Scaffold Report",
    "",
    f"- template: ai-first-saas-starter",
    f"- template source: {TEMPLATE_DIR}",
    f"- scaffolded at UTC: {datetime.now(timezone.utc).strftime('%Y-%m-%dT%H:%M:%SZ')}",
    f"- app name: {APP_NAME}",
    f"- app slug: {APP_SLUG}",
    f"- Java base package: {BASE_PACKAGE}",
    f"- Maven group id: {MAVEN_GROUP_ID}",
    f"- rendered files: {len(writes)}",
    "",
    "## Rendered paths",
    *[f"- `{target.relative_to(TARGET_DIR)}`" for _, target in writes],
    "",
    "## Follow-up checks",
    "",
    "- `mvn test` from the project root once backend dependencies are available.",
    "- frontend checks once the frontend template slice is present.",
    "- review `app-description/` and `specs/` before extending the starter.",
    "",
]) + "\n"

if DRY_RUN:
    print(f"[dry-run] write {report.relative_to(TARGET_DIR)}")
else:
    report.parent.mkdir(parents=True, exist_ok=True)
    if report.exists() and not FORCE_OVERWRITE:
        print(f"[scaffold][error] Report path already exists: {report.relative_to(TARGET_DIR)}", file=sys.stderr)
        sys.exit(1)
    report.write_text(report_text)
    print(f"[scaffold] Wrote {report.relative_to(TARGET_DIR)}")
    print("[scaffold] Complete")
PY
