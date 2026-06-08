#!/usr/bin/env python3
"""Validate concrete Repository example class references in skills.

Only sections explicitly titled "Repository example(s)" are checked. Sections that
are conceptual or aspirational should be titled "Pattern reference(s)" instead.
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SKILLS = ROOT / "skills"
EXAMPLES = ROOT / "examples" / "akka-components"

# SDK/protobuf/helper types that may appear inside concrete repository-example
# descriptions but are not expected to be classes in the examples snapshot.
ALLOWED_NON_EXAMPLE_TYPES = {
    "CommandException",
    "ComponentClient",
    "Done",
    "EvaluationResult",
    "HttpClientProvider",
    "INVALID_ARGUMENT",
    "OR",
    "PromptTemplate",
    "QueryStreamEffect",
    "SessionMemoryEntity",
    "SseRouteTester",
    "String",
    "TestKitSupport",
    "TextGuardrail",
}

TOKEN_RE = re.compile(r"`([A-Z][A-Za-z0-9_]*(?:#[A-Za-z0-9_]+)?)`")


def example_classes() -> set[str]:
    if not EXAMPLES.exists():
        return set()
    return {path.stem for path in EXAMPLES.rglob("*.java")}


def repository_example_ranges(lines: list[str]) -> list[tuple[int, int, int]]:
    ranges: list[tuple[int, int, int]] = []
    heading_idx: int | None = None
    start: int | None = None
    for idx, line in enumerate(lines):
        stripped = line.strip()
        if stripped in {"## Repository example", "## Repository examples"} or stripped.startswith(
            "Repository example"
        ):
            if start is not None and heading_idx is not None:
                ranges.append((heading_idx, start, idx))
            heading_idx = idx
            start = idx + 1
            continue
        if start is not None and (line.startswith("## ") or line.startswith("### ")):
            ranges.append((heading_idx or idx, start, idx))
            heading_idx = None
            start = None
    if start is not None and heading_idx is not None:
        ranges.append((heading_idx, start, len(lines)))
    return ranges


def main() -> int:
    classes = example_classes()
    failures: list[str] = []

    for skill in sorted(SKILLS.glob("*/SKILL.md")):
        lines = skill.read_text(encoding="utf-8").splitlines()
        for _heading, start, end in repository_example_ranges(lines):
            section_text = "\n".join(lines[start:end])
            for token in TOKEN_RE.findall(section_text):
                class_name = token.split("#", 1)[0]
                if class_name in ALLOWED_NON_EXAMPLE_TYPES:
                    continue
                if class_name not in classes:
                    line_no = next(
                        (idx + 1 for idx in range(start, end) if f"`{token}`" in lines[idx]),
                        start + 1,
                    )
                    failures.append(f"{skill.relative_to(ROOT)}:{line_no}: missing repository example `{token}`")

    if failures:
        print("Repository example references must point to Java classes in skills-pack/examples/akka-components.", file=sys.stderr)
        print("Use 'Pattern reference(s)' for conceptual/non-snapshot examples.", file=sys.stderr)
        for failure in failures:
            print(failure, file=sys.stderr)
        return 1
    print("repository_example_references=ok")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
