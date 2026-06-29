# Audit Trace lifecycle

Workstream id: `audit-trace`
Owning domain: `core-starter`
Current readiness: `description-ready`
Implementation alignment: `stale-description-changed`
Source alignment: `realization/source-alignment.md`
Last description change: 2026-06-29 — refreshed Audit/Trace from narrow tenant-admin activity log to the current functional-agent investigation graph covering workers, actor adapters, governed tools, capability links, traces, surfaces, tests, runtime-validation evidence, support-access review, export-if-allowed semantics, and source-alignment posture.
Last alignment review: 2026-06-29 — app-description review only; implementation not revalidated.
Last compile: unknown
Last manual runtime test: unknown

## Current alignment posture

This workstream is not currently aligned. The app-description now describes a broader Audit/Trace investigation graph than the prior tenant-admin-only activity-log slice, so mapped implementation evidence must be reviewed before claiming build, source, or runtime alignment.

## Blockers and assumptions

- This is docs-only current-intent refresh work; no runtime/API/UI source was changed.
- `description-ready` means the graph is coherent enough for focused build/compile or source-alignment tasks; it does not claim implementation or runtime readiness.
- Source alignment remains `stale-description-changed` until a focused review checks worker/adapter/tool/capability realization for Audit/Trace search/detail/timeline/correlation/denial/support-access/export/runtime-validation paths.
- Sensitive/raw export remains approval-gated and may be unavailable unless a later policy grants it explicitly.

## Next recommended action

Run a focused Audit/Trace source-alignment and implementation planning review for search/detail/timeline/correlation, read-only chat plans, bounded agent tool calls, denials/redaction, support-access review, redacted export request workflow, runtime-validation evidence links, trace-gap detection, and frontend/API route mappings before claiming implementation alignment or runtime readiness.
