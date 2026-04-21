#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd -- "$SCRIPT_DIR/.." && pwd)"
README_PATH="$REPO_ROOT/README.md"
PACK_README_PATH="$REPO_ROOT/pack/README.md"
MANIFEST_PATH="$REPO_ROOT/pack/manifest.yaml"

fail() {
  printf '[check-version][error] %s\n' "$*" >&2
  exit 1
}

[[ -f "$README_PATH" ]] || fail "Missing README.md"
[[ -f "$PACK_README_PATH" ]] || fail "Missing pack/README.md"
[[ -f "$MANIFEST_PATH" ]] || fail "Missing pack/manifest.yaml"

PACK_NAME="$(awk '
  $0 ~ /^metadata:/ { in_metadata=1; next }
  in_metadata && $0 ~ /^  name:/ { print $2; exit }
' "$MANIFEST_PATH")"

PACK_VERSION="$(awk '
  $0 ~ /^metadata:/ { in_metadata=1; next }
  in_metadata && $0 ~ /^  version:/ { print $2; exit }
' "$MANIFEST_PATH")"

[[ -n "$PACK_NAME" ]] || fail "Could not read metadata.name from pack/manifest.yaml"
[[ -n "$PACK_VERSION" ]] || fail "Could not read metadata.version from pack/manifest.yaml"

python3 - "$README_PATH" "$PACK_README_PATH" "$PACK_NAME" "$PACK_VERSION" <<'PY'
from pathlib import Path
import re
import sys

readme_path = Path(sys.argv[1])
pack_readme_path = Path(sys.argv[2])
pack_name = sys.argv[3]
pack_version = sys.argv[4]


def check_doc(label: str, path: Path, *, require_current_manifest_section: bool = False) -> list[str]:
    text = path.read_text()
    errors: list[str] = []

    if require_current_manifest_section and f"Current manifest version:\n- `{pack_version}`" not in text:
        errors.append(
            f"{label} does not show the current manifest version in the Distribution model section"
        )

    pack_refs = sorted(set(re.findall(rf"{re.escape(pack_name)}-(\d+\.\d+\.\d+)", text)))
    for version in pack_refs:
        if version != pack_version:
            errors.append(
                f"{label} contains pack artifact reference {pack_name}-{version}, expected version {pack_version}"
            )

    release_tag_refs = sorted(set(re.findall(r"/download/v(\d+\.\d+\.\d+)/", text)))
    for version in release_tag_refs:
        if version != pack_version:
            errors.append(
                f"{label} contains GitHub release download tag v{version}, expected v{pack_version}"
            )

    release_text_refs = sorted(
        set(re.findall(r"release tag `v(\d+\.\d+\.\d+)`", text, flags=re.IGNORECASE))
    )
    for version in release_text_refs:
        if version != pack_version:
            errors.append(f"{label} contains release tag v{version}, expected v{pack_version}")

    return errors

errors: list[str] = []
errors.extend(check_doc("README.md", readme_path, require_current_manifest_section=True))
errors.extend(check_doc("pack/README.md", pack_readme_path))

if errors:
    print("[check-version][error] Version consistency check failed:", file=sys.stderr)
    for error in errors:
        print(f"- {error}", file=sys.stderr)
    sys.exit(1)

print(
    f"[check-version] README.md and pack/README.md version references match manifest version {pack_version}"
)
PY
