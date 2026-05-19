# TASK-CORE-05-001: Specify Audit/Trace core module

## Purpose

Create concrete capability, component, API, view, UI, redaction, and test contracts for full-core Audit/Trace.

## Required reads

- `docs/ai-first-saas-application-architecture.md`
- `docs/examples/core-ai-first-saas-input/08-module-audit-work-trace-prd.md`
- `skills/ai-first-saas-audit-trace/SKILL.md`
- `skills/akka-agent-work-trace/SKILL.md`
- `specs/core-app-full-stack-readiness/full-core-realization-map.md`

## Expected outputs

- `specs/core-app-full-stack-readiness/audit-trace-core-module-slice.md`

## Required checks

- Slice covers admin audit, prompt assembly, skill load, tool invocation, model use, decision/approval, data access, redaction, search/list/detail/timeline, export/stream if included, and denial audit.
- `git diff --check`

## Done criteria

- Audit/Trace is generation-ready as a core module.
- Queue status and changes are committed.
