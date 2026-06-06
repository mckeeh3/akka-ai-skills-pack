#!/usr/bin/env python3
"""Validate skill-relative references in an installed .agents/skills layout.

The skills-pack source intentionally uses references such as ../docs/foo.md from
skills/<skill>/SKILL.md. Those references are not source-layout relative; they
are installed-layout relative after skills, docs, examples, templates, tools,
and references are lifted into one .agents/skills directory.
"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

REFERENCE_RE = re.compile(
    r"(?<![\w/])\.\./(?:docs|references|examples|templates|tools)/[^\s)`\]\\\"<>]+"
)
TRAILING_PUNCTUATION = "\u201d\u2019'\".,;:"
SKIP_MARKERS = ("*", "{", "}", "<", ">")


def normalize_reference(raw: str) -> str:
    reference = raw.rstrip(TRAILING_PUNCTUATION)
    if "#" in reference:
        reference = reference.split("#", 1)[0]
    return reference


def is_intended_file_reference(reference: str) -> bool:
    return bool(reference) and not any(marker in reference for marker in SKIP_MARKERS)


def iter_skill_files(installed_skills_dir: Path):
    for child in sorted(installed_skills_dir.iterdir()):
        if not child.is_dir():
            continue
        skill_file = child / "SKILL.md"
        if skill_file.is_file():
            yield skill_file


def validate(installed_skills_dir: Path) -> list[str]:
    failures: list[str] = []
    if not installed_skills_dir.is_dir():
        return [f"installed skills directory does not exist: {installed_skills_dir}"]

    for skill_file in iter_skill_files(installed_skills_dir):
        text = skill_file.read_text(encoding="utf-8")
        for line_number, line in enumerate(text.splitlines(), start=1):
            for match in REFERENCE_RE.finditer(line):
                reference = normalize_reference(match.group(0))
                if not is_intended_file_reference(reference):
                    continue
                resolved = (skill_file.parent / reference).resolve()
                try:
                    resolved.relative_to(installed_skills_dir.resolve())
                except ValueError:
                    failures.append(
                        f"{skill_file.relative_to(installed_skills_dir)}:{line_number}: "
                        f"reference escapes installed skills directory: {reference}"
                    )
                    continue
                if not resolved.exists():
                    failures.append(
                        f"{skill_file.relative_to(installed_skills_dir)}:{line_number}: "
                        f"missing installed-layout reference {reference} -> {resolved}"
                    )
    return failures


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(
        description="Validate ../docs, ../references, ../examples, ../templates, and ../tools references from installed SKILL.md files."
    )
    parser.add_argument(
        "installed_skills_dir",
        type=Path,
        help="Installed .agents/skills directory, not skills-pack/skills source directory.",
    )
    args = parser.parse_args(argv)

    failures = validate(args.installed_skills_dir)
    if failures:
        for failure in failures:
            print(f"[validate-installed-skill-references][error] {failure}", file=sys.stderr)
        return 1

    print(f"[validate-installed-skill-references] installed skill references passed: {args.installed_skills_dir}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
