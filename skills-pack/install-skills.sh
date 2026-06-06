#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
PACK_ROOT="$SCRIPT_DIR"
SOURCE_SKILLS_DIR="$PACK_ROOT/skills"
SOURCE_REFERENCES_DIR="$PACK_ROOT/references"

# Intentional install boundary:
# skills-pack/ is the source-controlled "brain" that is lifted into the AI
# harness skills directory. Its skills plus associated docs, examples, templates,
# tools, and shared references all serve one purpose: helping the harness build
# and maintain secure AI-first SaaS applications on the runnable Akka SaaS
# Foundation App. Future installer changes should preserve that source-to-harness
# contract: install the support library into .agents/skills/**, but do not create
# a duplicate app baseline or install independently maintained akka-context docs.
INSTALL_ASSET_DIRS=(docs examples templates tools)
PROJECT_ROOT="$(pwd)"
TARGET_DIR=""
LOCATION="project"
MODE="copy"
DRY_RUN=false
FORCE=false
PRUNE=false
UNINSTALL=false
CHECK=false
MANIFEST_NAME=".akka-ai-skills-pack-install-manifest"
PACK_NAME="akka-ai-skills-pack"

log() { printf '[install-skills] %s\n' "$*"; }
warn() { printf '[install-skills][warn] %s\n' "$*" >&2; }
fail() { printf '[install-skills][error] %s\n' "$*" >&2; exit 1; }

print_help() {
  cat <<'EOF'
Install the Akka AI skills-pack source brain into a harness-discoverable skills directory.

The installed library is the harness-level guidance, references, templates, tools,
and examples used to implement and maintain secure AI-first SaaS applications on
Akka. It is not a generated application, duplicate app baseline, or Akka SDK docs
bundle.

Usage:
  ./install-skills.sh [options]

Options:
  --target <dir>       Skills directory to install into. Default: <cwd>/.agents/skills
  --location <mode>    Convenience target: project or global
                       project -> <project-root>/.agents/skills (default)
                       global  -> ~/.agents/skills
  --project <dir>      Project root for --location project. Default: current directory
  --mode <mode>        Install mode: copy or symlink. Default: copy
  --force              Replace existing non-manifest-owned targets
  --prune              Remove previously installed pack-owned skills that no longer exist in source
  --uninstall          Remove all entries owned by this installer manifest
  --check              Verify installed pack content matches current source and report retired managed entries
  --dry-run            Show planned actions without writing files
  --help               Show this help text

Manifest:
  The installer writes <target>/.akka-ai-skills-pack-install-manifest. It records
  pack-owned top-level entries: README.md, shared asset/reference directories,
  and installed skill directories. Prune and uninstall only remove paths recorded
  in that manifest, so unrelated harness skills or local files in the same
  directory are not deleted.

Installed content:
  The installer copies/symlinks skill directories plus curated pack docs,
  examples, templates, tools, and shared references needed by installed skills.
  These asset directories are curated as whole directories, not selected from a
  per-file installer allowlist. It does not install akka-context; standard Akka
  projects keep that independently maintained directory at the project root.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --target)
      [[ $# -ge 2 ]] || fail "Missing value for --target"
      TARGET_DIR="$2"
      shift 2
      ;;
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
    --mode)
      [[ $# -ge 2 ]] || fail "Missing value for --mode"
      MODE="$2"
      shift 2
      ;;
    --force)
      FORCE=true
      shift
      ;;
    --prune)
      PRUNE=true
      shift
      ;;
    --uninstall)
      UNINSTALL=true
      shift
      ;;
    --check)
      CHECK=true
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

[[ -d "$SOURCE_SKILLS_DIR" ]] || fail "Expected skills source at $SOURCE_SKILLS_DIR"
case "$MODE" in copy|symlink) ;; *) fail "Unsupported --mode '$MODE'. Use copy or symlink." ;; esac
case "$LOCATION" in project|global) ;; *) fail "Unsupported --location '$LOCATION'. Use project or global." ;; esac

if [[ -z "$TARGET_DIR" ]]; then
  if [[ "$LOCATION" == "global" ]]; then
    TARGET_DIR="$HOME/.agents/skills"
  else
    if [[ -d "$PROJECT_ROOT" ]]; then
      PROJECT_ROOT="$(cd "$PROJECT_ROOT" && pwd)"
    elif [[ "$DRY_RUN" == true ]]; then
      PROJECT_ROOT="${PROJECT_ROOT%/}"
    else
      mkdir -p "$PROJECT_ROOT"
      PROJECT_ROOT="$(cd "$PROJECT_ROOT" && pwd)"
    fi
    TARGET_DIR="$PROJECT_ROOT/.agents/skills"
  fi
fi

MANIFEST_PATH="$TARGET_DIR/$MANIFEST_NAME"

run_cmd() {
  if [[ "$DRY_RUN" == true ]]; then
    printf '[dry-run]'
    for arg in "$@"; do printf ' %q' "$arg"; done
    printf '\n'
  else
    "$@"
  fi
}

is_safe_rel() {
  local rel="$1"
  [[ -n "$rel" && "$rel" != /* && "$rel" != *'..'* && "$rel" != *$'\t'* ]]
}

is_manifest_owned() {
  local rel="$1"
  [[ -f "$MANIFEST_PATH" ]] || return 1
  awk -F '\t' -v rel="$rel" '$1 == "entry" && $3 == rel { found=1 } END { exit found ? 0 : 1 }' "$MANIFEST_PATH"
}

remove_target() {
  local rel="$1"
  is_safe_rel "$rel" || fail "Refusing unsafe manifest path: $rel"
  local dest="$TARGET_DIR/$rel"
  if [[ -e "$dest" || -L "$dest" ]]; then
    log "Removing managed path: $dest"
    run_cmd rm -rf "$dest"
  fi
}

content_digest() {
  local path="$1"
  if [[ -f "$path" ]]; then
    sha256sum "$path" | awk '{ print $1 }'
  elif [[ -d "$path" ]]; then
    (
      cd "$path"
      find . -type f -print0 \
        | sort -z \
        | while IFS= read -r -d '' file; do sha256sum "$file"; done \
        | sha256sum \
        | awk '{ print $1 }'
    )
  else
    return 1
  fi
}

install_entry() {
  local type="$1"
  local rel="$2"
  local src="$3"
  local dest="$TARGET_DIR/$rel"

  if [[ -e "$dest" || -L "$dest" ]]; then
    if is_manifest_owned "$rel" || [[ "$FORCE" == true ]]; then
      run_cmd rm -rf "$dest"
    else
      fail "Refusing to replace unmanaged target: $dest (use --force if this is intended)"
    fi
  fi

  run_cmd mkdir -p "$(dirname "$dest")"
  if [[ "$MODE" == "symlink" ]]; then
    log "Symlinking $rel"
    run_cmd ln -s "$src" "$dest"
  else
    log "Copying $rel"
    if [[ "$type" == "file" ]]; then
      run_cmd cp "$src" "$dest"
    else
      run_cmd cp -R "$src" "$dest"
    fi
  fi
}

write_manifest() {
  local tmp
  tmp="$(mktemp)"
  {
    printf '# %s managed install manifest\n' "$PACK_NAME"
    printf 'pack\t%s\n' "$PACK_NAME"
    printf 'source\t%s\n' "$PACK_ROOT"
    printf 'mode\t%s\n' "$MODE"
    printf 'installed_at\t%s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)"
    printf 'entry\tfile\tREADME.md\n'
    [[ -d "$SOURCE_REFERENCES_DIR" ]] && printf 'entry\tdir\treferences\n'
    local asset_dir
    for asset_dir in "${INSTALL_ASSET_DIRS[@]}"; do
      [[ -d "$PACK_ROOT/$asset_dir" ]] && printf 'entry\tdir\t%s\n' "$asset_dir"
    done
    while IFS= read -r skill_dir; do
      printf 'entry\tdir\t%s\n' "$(basename "$skill_dir")"
    done < <(find "$SOURCE_SKILLS_DIR" -mindepth 1 -maxdepth 1 -type d ! -name references | sort)

    # Preserve retired manifest-owned entries until --prune or --uninstall removes
    # them, so a warning-only install does not make retired skills unmanageable.
    if [[ "$PRUNE" != true && -f "$MANIFEST_PATH" ]]; then
      local current manifest retired rel type
      current="$(mktemp)"
      manifest="$(mktemp)"
      retired="$(mktemp)"
      current_rel_file "$current"
      awk -F '\t' '$1 == "entry" { print $2 "\t" $3 }' "$MANIFEST_PATH" | sort -k2,2 > "$manifest"
      awk -F '\t' '{ print $2 }' "$manifest" | sort > "$retired.current-manifest"
      comm -23 "$retired.current-manifest" "$current" > "$retired"
      while IFS= read -r rel; do
        [[ -n "$rel" ]] || continue
        type="$(awk -F '\t' -v rel="$rel" '$2 == rel { print $1; exit }' "$manifest")"
        printf 'entry\t%s\t%s\n' "${type:-dir}" "$rel"
      done < "$retired"
      rm -f "$current" "$manifest" "$retired" "$retired.current-manifest"
    fi
  } > "$tmp"

  if [[ "$DRY_RUN" == true ]]; then
    log "Would write manifest: $MANIFEST_PATH"
    sed 's/^/[dry-run][manifest] /' "$tmp"
    rm -f "$tmp"
  else
    mkdir -p "$TARGET_DIR"
    mv "$tmp" "$MANIFEST_PATH"
  fi
}

current_rel_file() {
  local tmp="$1"
  {
    printf 'README.md\n'
    [[ -d "$SOURCE_REFERENCES_DIR" ]] && printf 'references\n'
    local asset_dir
    for asset_dir in "${INSTALL_ASSET_DIRS[@]}"; do
      [[ -d "$PACK_ROOT/$asset_dir" ]] && printf '%s\n' "$asset_dir"
    done
    while IFS= read -r skill_dir; do basename "$skill_dir"; done < <(find "$SOURCE_SKILLS_DIR" -mindepth 1 -maxdepth 1 -type d ! -name references | sort)
  } | sort > "$tmp"
}

manifest_rel_file() {
  local tmp="$1"
  if [[ -f "$MANIFEST_PATH" ]]; then
    while IFS=$'\t' read -r kind type rel; do
      [[ "$kind" == "entry" ]] || continue
      is_safe_rel "$rel" || fail "Refusing unsafe manifest path: $rel"
      printf '%s\n' "$rel"
    done < "$MANIFEST_PATH" | sort > "$tmp"
  else
    : > "$tmp"
  fi
}

validate_source_skills() {
  local missing=false
  while IFS= read -r skill_dir; do
    [[ -f "$skill_dir/SKILL.md" ]] || { warn "Missing SKILL.md in $skill_dir"; missing=true; }
  done < <(find "$SOURCE_SKILLS_DIR" -mindepth 1 -maxdepth 1 -type d ! -name references | sort)
  [[ "$missing" == false ]] || fail "One or more source skill directories are invalid"
}

report_retired_entries() {
  local current manifest retired
  current="$(mktemp)"
  manifest="$(mktemp)"
  retired="$(mktemp)"
  current_rel_file "$current"
  manifest_rel_file "$manifest"
  comm -23 "$manifest" "$current" > "$retired"
  if [[ -s "$retired" ]]; then
    warn "Previously installed pack-owned entries no longer exist in source:"
    sed 's/^/[install-skills][warn] - /' "$retired" >&2
    if [[ "$PRUNE" != true && "$UNINSTALL" != true ]]; then
      warn "Run again with --prune to remove retired pack-owned skills."
    fi
  fi
  rm -f "$current" "$manifest" "$retired"
}

prune_retired_entries() {
  local current manifest retired rel
  current="$(mktemp)"
  manifest="$(mktemp)"
  retired="$(mktemp)"
  current_rel_file "$current"
  manifest_rel_file "$manifest"
  comm -23 "$manifest" "$current" > "$retired"
  while IFS= read -r rel; do
    [[ -n "$rel" ]] && remove_target "$rel"
  done < "$retired"
  rm -f "$current" "$manifest" "$retired"
}

check_install() {
  local failed=false rel dest src source_hash installed_hash
  validate_source_skills
  [[ -f "$MANIFEST_PATH" ]] || { warn "Missing install manifest: $MANIFEST_PATH"; failed=true; }

  check_entry_matches() {
    local rel="$1"
    local src="$2"
    local dest="$TARGET_DIR/$rel"
    if [[ ! -e "$dest" && ! -L "$dest" ]]; then
      warn "Missing installed $rel"
      failed=true
      return
    fi
    if ! source_hash="$(content_digest "$src")"; then
      warn "Could not digest source $src"
      failed=true
      return
    fi
    if ! installed_hash="$(content_digest "$dest")"; then
      warn "Could not digest installed $rel"
      failed=true
      return
    fi
    if [[ "$source_hash" != "$installed_hash" ]]; then
      warn "Installed content differs from current source: $rel"
      failed=true
    fi
  }

  check_entry_matches README.md "$SOURCE_SKILLS_DIR/README.md"
  if [[ -d "$SOURCE_REFERENCES_DIR" ]]; then
    check_entry_matches references "$SOURCE_REFERENCES_DIR"
  fi
  local asset_dir
  for asset_dir in "${INSTALL_ASSET_DIRS[@]}"; do
    if [[ -d "$PACK_ROOT/$asset_dir" ]]; then
      check_entry_matches "$asset_dir" "$PACK_ROOT/$asset_dir"
    fi
  done
  while IFS= read -r src; do
    rel="$(basename "$src")"
    check_entry_matches "$rel" "$src"
  done < <(find "$SOURCE_SKILLS_DIR" -mindepth 1 -maxdepth 1 -type d ! -name references | sort)

  report_retired_entries
  [[ "$failed" == false ]] || fail "Install check failed"
  log "Install check passed: $TARGET_DIR"
}

uninstall_all() {
  [[ -f "$MANIFEST_PATH" ]] || fail "No install manifest found at $MANIFEST_PATH; refusing unmanaged uninstall"
  while IFS=$'\t' read -r kind type rel; do
    [[ "$kind" == "entry" ]] || continue
    remove_target "$rel"
  done < "$MANIFEST_PATH"
  log "Removing manifest: $MANIFEST_PATH"
  run_cmd rm -f "$MANIFEST_PATH"
}

validate_source_skills
log "Source skills: $SOURCE_SKILLS_DIR"
log "Target skills: $TARGET_DIR"

if [[ "$UNINSTALL" == true ]]; then
  uninstall_all
  exit 0
fi

if [[ "$CHECK" == true ]]; then
  check_install
  exit 0
fi

run_cmd mkdir -p "$TARGET_DIR"
if [[ "$PRUNE" == true ]]; then
  prune_retired_entries
else
  report_retired_entries
fi

install_entry file README.md "$SOURCE_SKILLS_DIR/README.md"
if [[ -d "$SOURCE_REFERENCES_DIR" ]]; then
  install_entry dir references "$SOURCE_REFERENCES_DIR"
fi
for asset_dir in "${INSTALL_ASSET_DIRS[@]}"; do
  if [[ -d "$PACK_ROOT/$asset_dir" ]]; then
    install_entry dir "$asset_dir" "$PACK_ROOT/$asset_dir"
  fi
done
while IFS= read -r skill_dir; do
  install_entry dir "$(basename "$skill_dir")" "$skill_dir"
done < <(find "$SOURCE_SKILLS_DIR" -mindepth 1 -maxdepth 1 -type d ! -name references | sort)
write_manifest
if [[ "$DRY_RUN" == true ]]; then
  log "Dry run complete. No files were written. Harness skills directory: $TARGET_DIR"
else
  log "Install complete. Harness skills directory: $TARGET_DIR"
fi
