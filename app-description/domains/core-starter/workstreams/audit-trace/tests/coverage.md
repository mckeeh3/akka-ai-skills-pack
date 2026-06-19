# Tests: Audit/Trace

## Acceptance

- Given an authorized caller with selected `AuthContext`, when they open Audit/Trace, then the dashboard and allowed surfaces render only scoped data and expose only authorized actions.
- Given an allowed action, when it is submitted with valid input, then capability `audit-and-trace-investigation` returns the expected structured result and emits required traces.
- Surface/action coverage includes dashboard refresh, search, detail, timeline, failure evidence, trace-gap lifecycle review, investigation guide, redacted export request, export approval/download-expiry handling, investigation note version append/supersede/redaction, summary task start/read, summary review open, summary accept, and summary reject.
- Summary-worker coverage includes accepted/queued/running/completed, review-not-ready, provider/model/runtime/tool-boundary fail-closed, retention-purged, partial-data, malformed source scope, hidden/not-found source, real model-backed advisory summary retention, and no model-less acceptable summary.

## Security and negative

- Disabled users, inactive memberships, role/capability denials, and cross-tenant/customer requests are denied without protected-data leakage.
- Agent/tool calls cannot exceed the governed tool boundary or approval policy.
- Browser payloads never expose provider secrets, raw export bundles beyond authorized short-lived download content, raw trace internals, hidden source ids, or hidden authority state.

## Idempotency and observability

- Repeated export requests, investigation note appends/supersessions, summary starts, summary reads, and summary accept/reject decisions do not duplicate effects and retain correlation/trace evidence.
- Denials, approval-required outcomes, provider fail-closed states, tool-boundary denials, retention/redaction/export-expiry outcomes, trace-gap terminal outcomes, stale/conflict results, hidden/not-found results, and trace emissions are verifiable through local Akka/API/UI tests or readiness evidence.
