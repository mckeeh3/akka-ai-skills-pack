#!/usr/bin/env python3
"""Validate the curated akka-components example index.

The examples tree is intentionally a compact, non-buildable reference snapshot. This
check prevents silent drift where files are added/removed without updating the
index that explains why each file remains in the pack.
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
EXAMPLES = ROOT / "examples" / "akka-components"
INDEX = EXAMPLES / "REFERENCE-INDEX.md"


def fail(message: str) -> None:
    print(f"curated_example_index=error: {message}", file=sys.stderr)
    sys.exit(1)


def main() -> None:
    if not INDEX.exists():
        fail(f"missing {INDEX.relative_to(ROOT)}")

    actual = {
        str(path.relative_to(EXAMPLES))
        for path in (EXAMPLES / "src").rglob("*")
        if path.is_file()
    }
    text = INDEX.read_text()
    listed = {
        value
        for value in re.findall(r"`(src/[^`]+)`", text)
        if "*" not in value
    }

    missing_from_index = sorted(actual - listed)
    stale_in_index = sorted(listed - actual)

    if missing_from_index or stale_in_index:
        for path in missing_from_index:
            print(f"missing_from_index: {path}", file=sys.stderr)
        for path in stale_in_index:
            print(f"stale_in_index: {path}", file=sys.stderr)
        fail("REFERENCE-INDEX.md does not match examples/akka-components/src")

    if len(actual) > 120:
        fail(f"curated examples too large ({len(actual)} files); do not mirror the root app")

    print("curated_example_index=ok")


if __name__ == "__main__":
    main()
