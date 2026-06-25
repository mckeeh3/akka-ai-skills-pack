#!/usr/bin/env python3
"""Validate runtime-completion evidence for done generated-SaaS tasks.

This is intentionally conservative and text-based. It does not prove semantic
correctness; it prevents the most common drift where a feature-bearing task is
marked done with only unit/contract/typecheck evidence and no stated real
runtime/API/UI smoke path.
"""
from __future__ import annotations

import argparse
import re
import sys
from dataclasses import dataclass
from pathlib import Path

TASK_RE = re.compile(r"^###\s+(TASK[^:\n]*:.*)$", re.MULTILINE)
STATUS_RE = re.compile(r"^-\s*status:\s*([^\n]+)", re.MULTILINE | re.IGNORECASE)

EXEMPT_RE = re.compile(
    r"\b(docs-only|planning-only|non-runtime|internal-only|foundation-only|cross-cutting|planning scaffold|gap contract|scope decision|verification-only|readiness contract|decide billing|decision task)\b",
    re.IGNORECASE,
)
RUNTIME_HINT_RE = re.compile(
    r"\b(frontend|browser|ui|surface|workstream|api|endpoint|route|http|sse|websocket|auth|jwt|invitation|email|resend|workos|admin|agent|workflow|timer|consumer|view|entity|provider|audit|trace)\b",
    re.IGNORECASE,
)
IMPLEMENTATION_HINT_RE = re.compile(
    r"\b(src/|frontend/|backend|code|component|endpoint|api|browser|ui|surface|action|mvn|npm|test|typecheck|build|runtime|smoke)\b",
    re.IGNORECASE,
)
REAL_PATH_RE = re.compile(
    r"\b(real|intended|local|runtime|akka-hosted|protected|browser|api|endpoint|route|workstream|surface action|manual smoke|browser smoke|api smoke|app-run|running app)\b",
    re.IGNORECASE,
)
VALIDATION_RE = re.compile(
    r"\b(mvn|npm|curl|httpie|playwright|cypress|vitest|test|typecheck|build|manual smoke|browser smoke|api smoke|runtime smoke|smoke path|checks?:|validation|passed|failed|blocked)\b",
    re.IGNORECASE,
)
INSUFFICIENT_ONLY_RE = re.compile(
    r"\b(contract test|typecheck|unit test|service test|snapshot|fixture-only|frontend-only|mock-only|demo-only)\b",
    re.IGNORECASE,
)
BLOCKER_RE = re.compile(r"\b(blocked|not run|unable to run|deferred|missing credential|missing config)\b", re.IGNORECASE)
READINESS_RE = re.compile(r"\b(described|surface-ready|backend-ready|frontend-rendered|api-smoked|browser-smoked|manual-ready|runtime-ready|readiness level|runtime evidence)\b", re.IGNORECASE)
AUTH_SCOPE_RE = re.compile(r"\b(role|authcontext|auth context|tenant|customer|organization|caller|actor|selected context)\b", re.IGNORECASE)
DENIAL_RE = re.compile(r"\b(denial|denied|forbidden|unauthorized|401|403|hidden|not-found|not found)\b", re.IGNORECASE)
TRACE_RE = re.compile(r"\b(trace|audit|correlation)\b", re.IGNORECASE)
PROVIDER_STATUS_RE = re.compile(r"\b(configured|smoked|fail-closed|fail closed|missing config|missing credential|blocked until|provider smoke|setup)\b", re.IGNORECASE)


@dataclass
class Task:
    title: str
    body: str
    status: str


def parse_tasks(text: str) -> list[Task]:
    matches = list(TASK_RE.finditer(text))
    tasks: list[Task] = []
    for i, match in enumerate(matches):
        start = match.start()
        end = matches[i + 1].start() if i + 1 < len(matches) else len(text)
        body = text[start:end]
        status_match = STATUS_RE.search(body)
        status = status_match.group(1).strip().lower() if status_match else "pending"
        tasks.append(Task(match.group(1).strip(), body, status))
    return tasks


def is_feature_bearing(task: Task) -> bool:
    if EXEMPT_RE.search(task.body):
        return False
    return bool(RUNTIME_HINT_RE.search(task.body) and IMPLEMENTATION_HINT_RE.search(task.body))


def validate_task(task: Task) -> list[str]:
    body = task.body
    missing: list[str] = []
    if not READINESS_RE.search(body):
        missing.append("readiness level or runtime evidence label")
    if not REAL_PATH_RE.search(body):
        missing.append("intended real local runtime/API/UI path evidence")
    if not VALIDATION_RE.search(body):
        missing.append("validation command/result or explicit manual smoke result")
    if re.search(r"\b(auth|admin|workstream|surface|api|endpoint|tenant|organization|user|membership|role)\b", body, re.IGNORECASE):
        if not AUTH_SCOPE_RE.search(body):
            missing.append("role/AuthContext/tenant or selected-scope evidence")
        if not DENIAL_RE.search(body):
            missing.append("authorization denial/forbidden/hidden evidence")
    if re.search(r"\b(audit|trace|correlation)\b", body, re.IGNORECASE) and not TRACE_RE.search(body):
        missing.append("audit/trace/correlation evidence")
    if re.search(r"\b(provider|workos|resend|model|llm|credential|secret)\b", body, re.IGNORECASE) and not PROVIDER_STATUS_RE.search(body):
        missing.append("provider configured or fail-closed evidence")

    has_runtime_smoke = bool(re.search(r"\b(runtime smoke|browser smoke|api smoke|manual smoke|app-run|akka-hosted|curl|httpie|playwright|cypress|api-smoked|browser-smoked|manual-ready|runtime-ready)\b", body, re.IGNORECASE))
    has_only_weak = bool(INSUFFICIENT_ONLY_RE.search(body)) and not has_runtime_smoke
    if has_only_weak:
        missing.append("evidence beyond unit/service/contract/typecheck-only checks")

    if BLOCKER_RE.search(body) and re.search(r"\b(required|local|manual|browser|api|runtime)\b[^\n]*(blocked|not run|unable to run)", body, re.IGNORECASE) and not re.search(r"\b(outside named scope|explicitly non-runtime|live .* remains blocked|provider smoke remains blocked)\b", body, re.IGNORECASE):
        missing.append("done status conflicts with blocked/deferred/unrun runtime evidence")
    return missing


def main() -> int:
    parser = argparse.ArgumentParser(description="Validate done runtime tasks have runtime completion evidence.")
    parser.add_argument("queue", nargs="?", default="specs/pending-tasks.md")
    parser.add_argument("--strict", action="store_true", help="Also warn when no done feature-bearing tasks are found.")
    args = parser.parse_args()

    path = Path(args.queue)
    if not path.is_file():
        print(f"[validate-runtime-completion-evidence][error] Queue file not found: {path}", file=sys.stderr)
        return 1

    tasks = parse_tasks(path.read_text(encoding="utf-8"))
    failures = 0
    checked = 0
    for task in tasks:
        if task.status != "done" or not is_feature_bearing(task):
            continue
        checked += 1
        missing = validate_task(task)
        if missing:
            failures += 1
            print(f"[fail] {task.title}\n  missing: {', '.join(missing)}", file=sys.stderr)

    if failures:
        print(
            f"\n{failures} done feature-bearing task(s) lack required runtime completion evidence. "
            "Repair task notes/status before claiming runtime-ready.",
            file=sys.stderr,
        )
        return 1

    if args.strict and checked == 0:
        print("[validate-runtime-completion-evidence][warn] no done feature-bearing tasks found", file=sys.stderr)

    print(f"[validate-runtime-completion-evidence] pass: {path} ({checked} done runtime task(s) checked)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
