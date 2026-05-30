# Sprint 01: Audit/Trace Full-Core Map and First Implementation Group

## Objective

Define and queue the Audit/Trace SMB full-core implementation path, then implement the first bounded source-edit group once source boundaries are known.

## Source context

User Admin and Agent Admin now produce meaningful traces. This sprint makes those traces visible and useful through Audit/Trace dashboards, search, timelines, evidence cards, and governed explanations.

## Ordered work areas

1. Define Audit/Trace vertical slice contracts and implementation map.
2. Implement audit dashboard plus trace search/detail/timeline foundations.
3. Implement redacted evidence/failure cards and cross-workstream trace-link handling.
4. Implement AuditTraceAgent request/response guidance.
5. Implement scheduled audit-summary worker only if deterministic foundations and task semantics justify it.
6. Validate runtime/API/UI behavior and verify mini-project readiness.

## Acceptance criteria

- Implementation tasks are bounded by actual source boundaries.
- Audit/Trace surfaces are typed, trace-linked, authority-scoped, redacted, and visually polished.
- Deterministic services own trace authorization, redaction, correlation, and projection.
- Model-backed guidance/worker behavior uses governed Akka runtime and provider fail-closed semantics.
- Targeted and broad starter validation pass or blockers are queued.
