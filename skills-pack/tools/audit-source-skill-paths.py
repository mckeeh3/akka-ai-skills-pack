#!/usr/bin/env python3
"""Audit source skill/reference file path references using the installed layout.

This is a lightweight hygiene check for references in skills/*/SKILL.md and
skills/references/*.md that should resolve after installation under
.agents/skills. It intentionally ignores target-project paths such as specs/*,
app-description/*, frontend/*, src/*, and external top-level akka-context/*
references.
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
    "../../../frontend/",
    "src/",
    "../src/",
    "../../src/",
    "../../../src/",
    "../../../specs/",
    "../../../app-description/",
    "../../../AGENTS.md",
    "../../../pom.xml",
    "../src/main/resources/static-resources",
    "../../../akka-context/",
    "../../pom.xml",
)
SKIP_MARKERS = ("://", "*", "<", ">", "...")
INSTALLED_TOP_LEVEL_FILES = {"README.md"}
INSTALLED_ASSET_DIRS = {"docs", "examples", "templates", "tools", "references"}


def is_candidate(ref: str) -> bool:
    if not ref or any(marker in ref for marker in SKIP_MARKERS):
        return False
    if ref.startswith(PROJECT_PLACEHOLDER_PREFIXES):
        return False
    return ref.startswith(LOCAL_PREFIXES)


def source_to_installed_path(source_file: Path, repo_root: Path) -> Path:
    """Map a source checkout file path to its installed .agents/skills path."""
    rel = source_file.relative_to(repo_root)
    if rel.parts[0] == "skills" and len(rel.parts) >= 2:
        if rel.parts[1] == "references":
            return repo_root / "references" / Path(*rel.parts[2:])
        return repo_root / Path(*rel.parts[1:])
    return repo_root / rel


def resolve_ref(source_file: Path, ref: str, repo_root: Path) -> Path:
    path = ref.split("#", 1)[0]
    installed_file = source_to_installed_path(source_file, repo_root)
    if path.startswith(("docs/", "examples/", "templates/", "tools/", "references/")) or path in {
        "pom.xml",
        "AGENTS.md",
        "install-skills.sh",
    }:
        return repo_root / path
    if path.startswith("skills/"):
        return source_to_installed_path(repo_root / path, repo_root)
    return (installed_file.parent / path).resolve()


def installed_layout_exists(installed_target: Path, repo_root: Path) -> bool:
    """Check an installed-layout target against the source checkout."""
    try:
        rel = installed_target.relative_to(repo_root)
    except ValueError:
        return installed_target.exists()
    if not rel.parts:
        return repo_root.exists()
    first = rel.parts[0]
    if first == "references":
        return (repo_root / "skills" / rel).exists()
    if first in INSTALLED_ASSET_DIRS or first in INSTALLED_TOP_LEVEL_FILES:
        return (repo_root / rel).exists()
    if first in {"pom.xml", "AGENTS.md", "install-skills.sh"}:
        return (repo_root / rel).exists()
    return (repo_root / "skills" / rel).exists()


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--repo-root", default=Path(__file__).resolve().parents[1])
    parser.add_argument("--quiet", action="store_true")
    args = parser.parse_args()

    repo_root = Path(args.repo_root).resolve()
    reference_files = sorted((repo_root / "skills" / "references").glob("*.md"))
    skill_files = sorted((repo_root / "skills").glob("*/SKILL.md"))
    audit_files = skill_files + reference_files
    if not skill_files:
        print(f"skill_files=0 reference_files=0 checked_refs=0 broken_refs=0")
        print(f"error: no skill files found under {repo_root / 'skills'}", file=sys.stderr)
        return 1
    checked = 0
    broken: list[tuple[Path, str, Path]] = []

    for audit_file in audit_files:
        for match in BACKTICK_RE.finditer(audit_file.read_text()):
            ref = match.group(1).strip()
            if not is_candidate(ref):
                continue
            target = resolve_ref(audit_file, ref, repo_root)
            checked += 1
            if not installed_layout_exists(target, repo_root):
                broken.append((audit_file.relative_to(repo_root), ref, target.relative_to(repo_root) if target.is_relative_to(repo_root) else target))

    print(f"skill_files={len(skill_files)} reference_files={len(reference_files)} checked_refs={checked} broken_refs={len(broken)}")
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
