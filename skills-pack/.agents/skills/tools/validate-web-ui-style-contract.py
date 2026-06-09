#!/usr/bin/env python3
"""Lightweight source check for AI-first SaaS web UI style contract drift.

This is intentionally conservative: it reports common weak-output signals rather
than proving visual quality. Use with human review and the canonical static
reference mockups under skills-pack/examples/web-ui/ai-first-workstream-enterprise/.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

THEME_IDS = ["aurora-light", "cobalt-light", "obsidian-dark", "midnight-dark", "dark-night"]
REQUIRED_TOKENS = [
    "--color-canvas",
    "--color-surface",
    "--color-text",
    "--color-border",
    "--color-accent",
    "--color-ai",
    "--color-success",
    "--color-warning",
    "--color-danger",
    "--color-focus",
]
HEX_RE = re.compile(r"#[0-9a-fA-F]{3,8}\b")
MODE_SELECTOR_RE = re.compile(
    r"(value|id|name)=[{\"']?(system|light|dark)[}\"']?|>\s*(System|Light|Dark)\s*<|label:\s*['\"](?:System|Light|Dark)['\"]"
)
THEME_BLOCK_RE = re.compile(r"\[data-theme=\"[^\"]+\"\]\s*\{(?P<body>.*?)\}", re.DOTALL)
THEME_LAYOUT_PROPERTIES = [
    "display:",
    "grid-template",
    "flex-direction",
    "position:",
    "width:",
    "height:",
    "padding:",
    "margin:",
    "border-radius:",
    "font-size:",
]
DEMO_STRINGS = [
    "Atlas Ops",
    "Hugh Morris",
    "Globex",
    "Northwind",
    "Sarah Patel",
    "Sofia Liu",
    "Jon Reyes",
    "Amina Patel",
    "Theo King",
]


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        return ""


def find_frontend(root: Path) -> Path | None:
    candidate = root / "frontend" / "src"
    return candidate if candidate.exists() else None


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("root", nargs="?", default=".", help="target project root, default current directory")
    parser.add_argument("--warn-only", action="store_true", help="print findings but exit 0")
    args = parser.parse_args()

    root = Path(args.root).resolve()
    findings: list[str] = []

    cross_cutting = root / "specs" / "cross-cutting"
    style_paths = [root / "app-description" / "55-ui" / "style-guide.md"]
    if cross_cutting.exists():
        style_paths.extend(sorted(cross_cutting.glob("*ui-style-guide*.md")))
    if not any(path.exists() for path in style_paths):
        findings.append("missing authoritative UI style guide: app-description/55-ui/style-guide.md or specs/cross-cutting/*ui-style-guide*.md")

    frontend = find_frontend(root)
    if frontend is None:
        findings.append("frontend/src not found; skip frontend style source checks")
    else:
        css_files = sorted(frontend.glob("**/*.css"))
        tsx_files = sorted(frontend.glob("**/*.*"))
        css_text = "\n".join(read_text(path) for path in css_files)
        all_text_by_file = {path: read_text(path) for path in tsx_files if path.suffix in {".ts", ".tsx", ".js", ".jsx", ".css", ".html"}}
        all_text = "\n".join(all_text_by_file.values())

        for theme_id in THEME_IDS:
            if theme_id not in all_text:
                findings.append(f"missing named theme id in frontend source: {theme_id}")
        for token in REQUIRED_TOKENS:
            if token not in css_text:
                findings.append(f"missing required CSS token: {token}")
        if ":focus-visible" not in css_text:
            findings.append("missing :focus-visible styling")
        if "prefers-reduced-motion" not in css_text:
            findings.append("missing prefers-reduced-motion handling")
        if "prefers-color-scheme" in all_text:
            findings.append("uses prefers-color-scheme; generated SaaS should use named themes, not dark/light/system mode")

        mode_selector_locations: list[str] = []
        for path, text in all_text_by_file.items():
            if path.suffix not in {".ts", ".tsx", ".js", ".jsx", ".html"} or "__tests__" in path.parts:
                continue
            if MODE_SELECTOR_RE.search(text):
                mode_selector_locations.append(str(path.relative_to(root)))
        if mode_selector_locations:
            findings.append(f"possible dark/light/system mode selector instead of named themes: {', '.join(mode_selector_locations[:8])}")

        theme_layout_locations: list[str] = []
        for path, text in all_text_by_file.items():
            if path.suffix != ".css":
                continue
            for match in THEME_BLOCK_RE.finditer(text):
                body = match.group("body")
                if any(prop in body for prop in THEME_LAYOUT_PROPERTIES):
                    theme_layout_locations.append(str(path.relative_to(root)))
                    break
        if theme_layout_locations:
            findings.append(f"theme blocks appear to change layout/typography, not only color tokens: {', '.join(theme_layout_locations[:8])}")

        unstyled_controls = False
        for path, text in all_text_by_file.items():
            if path.suffix not in {".tsx", ".jsx", ".html"} or "screens" in path.parts:
                continue
            if re.search(r"<(select|textarea)\b(?![^>]*(className|class)=)|<input\b(?![^>]*type=[\"']hidden[\"'])(?![^>]*(className|class)=)", text):
                findings.append(f"possible unstyled form control without class/className: {path.relative_to(root)}")
                unstyled_controls = True
                break
        if not unstyled_controls and not re.search(r"(input|select|textarea)", css_text):
            findings.append("no visible CSS rules for input/select/textarea controls")

        hex_locations: list[str] = []
        for path, text in all_text_by_file.items():
            if path.suffix == ".css" and ("token" in path.name or "theme" in path.name):
                continue
            if HEX_RE.search(text):
                hex_locations.append(str(path.relative_to(root)))
        if hex_locations:
            shown = ", ".join(hex_locations[:8])
            findings.append(f"hard-coded hex colors outside token/theme files: {shown}")

        copied_demo_locations: list[str] = []
        for path, text in all_text_by_file.items():
            if "__tests__" in path.parts:
                continue
            if any(demo in text for demo in DEMO_STRINGS):
                copied_demo_locations.append(str(path.relative_to(root)))
        if copied_demo_locations:
            findings.append(f"possible copied reference/demo content in generated frontend source: {', '.join(copied_demo_locations[:8])}")

        if (frontend / "screens").exists():
            findings.append("legacy frontend/src/screens exists; canonical generated UI should use frontend/src/workstream/**")

    if findings:
        for finding in findings:
            print(f"web-ui-style-contract: {finding}", file=sys.stderr)
        return 0 if args.warn_only else 1
    print("web-ui-style-contract: ok")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
