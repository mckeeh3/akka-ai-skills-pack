#!/usr/bin/env python3
"""Audit source skill file path references resolved from each skill directory.

This is a lightweight hygiene check for references in skills/*/SKILL.md that
look like source-repository docs/examples/resources. It intentionally ignores
workspace-template paths such as specs/*, app-description/*, and frontend/*
because those are often target-project paths in generated-app guidance.
"""
from __future__ import annotations

from pathlib import Path
import argparse
import re
import sys

BACKTICK_RE = re.compile(r"`([^`]+)`")
LOCAL_PREFIXES = (
    "./",
    "../",
    "../../",
    "../../../",
    "docs/",
    "src/",
    "skills/",
    "pom.xml",
    "AGENTS.md",
    "install-skills.sh",
)
# Paths commonly used as target-project placeholders rather than source-repo refs.
PROJECT_PLACEHOLDER_PREFIXES = (
    "specs/",
    "../../specs/",
    "app-description/",
    "../../app-description/",
    "frontend/",
    "../../frontend/",
    "../src/main/resources/static-resources",
)
SKIP_MARKERS = ("://", "*", "<", ">", "...")


def is_candidate(ref: str) -> bool:
    if not ref or any(marker in ref for marker in SKIP_MARKERS):
        return False
    if ref.startswith(PROJECT_PLACEHOLDER_PREFIXES):
        return False
    return ref.startswith(LOCAL_PREFIXES)


def resolve_ref(skill_file: Path, ref: str, repo_root: Path) -> Path:
    path = ref.split("#", 1)[0]
    if path.startswith(("docs/", "src/", "skills/")) or path in {
        "pom.xml",
        "AGENTS.md",
        "install-skills.sh",
    }:
        return repo_root / path
    return skill_file.parent / path


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--repo-root", default=".")
    parser.add_argument("--quiet", action="store_true")
    args = parser.parse_args()

    repo_root = Path(args.repo_root).resolve()
    skill_files = sorted((repo_root / "skills").glob("*/SKILL.md"))
    checked = 0
    broken: list[tuple[Path, str, Path]] = []

    for skill_file in skill_files:
        for match in BACKTICK_RE.finditer(skill_file.read_text()):
            ref = match.group(1).strip()
            if not is_candidate(ref):
                continue
            target = resolve_ref(skill_file, ref, repo_root)
            checked += 1
            if not target.exists():
                broken.append((skill_file.relative_to(repo_root), ref, target.relative_to(repo_root) if target.is_relative_to(repo_root) else target))

    print(f"skill_files={len(skill_files)} checked_refs={checked} broken_refs={len(broken)}")
    if broken and not args.quiet:
        current = None
        for skill_file, ref, target in broken:
            if skill_file != current:
                print(f"\n{skill_file}")
                current = skill_file
            print(f"  {ref} -> {target}")
    return 1 if broken else 0


if __name__ == "__main__":
    sys.exit(main())
