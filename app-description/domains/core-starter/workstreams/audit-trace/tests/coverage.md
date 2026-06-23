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


## `human_chat_tool_plan` coverage

- Given deterministic surface routing can safely open or prefill a surface, when a high-confidence no-mutation prompt is submitted, then the router returns that surface first and `human_chat_tool_plan` is not used.
- Given the representative prompt **append investigation note "provider blocked; retry after config" to this trace** and an authorized selected `AuthContext`, when the chat request is classified as `human_chat_tool_plan`, then the response is a no-mutation plan proposal surface that lists actions `action-audit-trace-append-investigation-note`, governed tools `draft-investigation-note`, capabilities `audit.trace.investigation_note.append`, validated input schema `schema.audit-trace.investigation-note.v1` with visible `traceId`/`correlationId`, `noteText`, selected scope, and idempotency key, side effects, approval gates, idempotency, result surfaces `surface-audit-trace-investigation-note`, and trace refs.
- Given a proposed plan has not been explicitly confirmed, when the request completes, then no surface action, governed tool, external provider side effect, state mutation, invitation/email/outbox send, policy/agent lifecycle change, trace note append, or settings update has occurred.
- Given the human confirms the exact `planId` and `planSnapshotId`, when backend authorization, lifecycle, tool-boundary, validation, approval, tenant/customer ownership, and idempotency checks pass for every step, then each step executes as an independent transaction boundary and returns the declared result or recovery surface.
- Given a modified, stale, expired, cross-context, cross-tenant/customer, missing-confirmation, out-of-catalog, unsupported-field, hidden-target, provider/runtime/tool-boundary blocked, or unauthorized plan is confirmed, then execution is denied with `chat_tool_plan.system_message.v1`, `noDirectMutation=true`, safe recovery, no hidden-target enumeration, and trace refs.
- Given the same proposal or confirmed step is replayed with the same idempotency key, then the backend returns the existing proposal/result and does not duplicate side effects, traces, notifications, provider calls, or attention items.
- Given a later dependent step fails after an earlier step commits, then the plan result reports completed, failed, skipped, and recovery steps without rolling back committed work unless a cataloged compensating action exists.
- Given provider/model/runtime configuration is missing for model-backed proposal generation, then the workstream returns a typed plan-unavailable/system-message state and trace evidence instead of fake/model-less planning success.
- Given any agent, prompt, skill, reference, frontend state, route, or visible rail item suggests broader authority than the catalog grants, then the backend rejects the plan or step and records a denial trace.
