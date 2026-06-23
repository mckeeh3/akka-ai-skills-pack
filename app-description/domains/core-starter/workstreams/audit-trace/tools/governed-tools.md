# Tools: Audit/Trace

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Allowed governed tools: read-audit-trace-dashboard, search-audit-traces, read-trace-detail, read-trace-timeline, read-trace-failure-evidence, read-investigation-guide, request-redacted-export, draft-investigation-note, start-audit-summary-task, read-audit-summary-task, review-audit-summary-task, accept-audit-summary-task, and reject-audit-summary-task.

Action-to-tool aliases are canonical: `action-audit-trace-dashboard` -> `read-audit-trace-dashboard`; `action-audit-trace-search` -> `search-audit-traces`; `action-audit-trace-detail` -> `read-trace-detail`; `action-audit-trace-timeline` -> `read-trace-timeline`; `action-audit-trace-failure-evidence` -> `read-trace-failure-evidence`; `action-audit-trace-investigation-guide` -> `read-investigation-guide`; `action-audit-trace-request-redacted-export` -> `request-redacted-export`; `action-audit-trace-append-investigation-note` -> `draft-investigation-note`; `action-audit-trace-summary-task-start` -> `start-audit-summary-task`; `action-audit-trace-summary-task-read` -> `read-audit-summary-task`; `action-audit-trace-summary-review` -> `review-audit-summary-task`; `action-audit-trace-summary-accept` -> `accept-audit-summary-task`; and `action-audit-trace-summary-reject` -> `reject-audit-summary-task`.

Tools are exposed as browser, agent, or internal tools only as stated by the linked capability. Side-effecting or high-impact tools require idempotency, correlation, authorization, approval policy, and audit/work traces. Denied tool calls are traced and return safe feedback.


## `human_chat_tool_plan` expanded current-intent catalog

This catalog records current intent for later runtime expansion. It reuses the same governed tool ids as browser surface actions and does not by itself change runtime behavior. Exposure channel `human_chat_tool_plan` remains proposal-and-confirmation only: deterministic no-mutation surface routing runs first, the initial execution-oriented chat request may only return a no-mutation plan proposal, and no state changes until exact human plan-snapshot confirmation and backend authorization succeed.

Allowed expanded entries:

| Classification | Representative prompts | Surface action ids | Shared governed tool ids | Capability ids | Input schema and validation | Result surface(s) |
|---|---|---|---|---|---|---|
| `chat-executable-now` | `search traces for provider failures`; `show detail for this trace`; `show timeline for this correlation`; `show failure evidence`; `guide this investigation` | `action-audit-trace-search`; `action-audit-trace-detail`; `action-audit-trace-timeline`; `action-audit-trace-failure-evidence`; `action-audit-trace-investigation-guide` | `search-audit-traces`; `read-trace-detail`; `read-trace-timeline`; `read-trace-failure-evidence`; `read-investigation-guide` | `audit.trace.search`; `audit.trace.detail.read`; `audit.trace.timeline.read`; `audit.trace.failureEvidence.read`; `audit.trace.investigationGuide.read` | `schema.audit-trace.*`; require selected scope, backend-visible trace/correlation/filter binding, redacted output, no hidden-target enumeration, and trace refs | Audit search/detail/timeline/failure/guide surfaces |
| `chat-executable-now` | `append investigation note "provider blocked; retry after config" to this trace` | `action-audit-trace-append-investigation-note` | `draft-investigation-note` | `audit.trace.investigation_note.append` | `schema.audit-trace.investigation-note.v1`; require visible trace/correlation id, browser-safe note text, selected scope, and idempotency key | `surface-audit-trace-investigation-note` |

Expanded classification and blocked/surface-only rationale:

| Classification | Action groups | Rationale and boundary |
|---|---|---|
| `router-only` | Audit/Trace dashboard open | Deterministic no-mutation route is sufficient and avoids hiding scoped counts behind plan confirmation. |
| `surface-only` | Summary read/review/accept/reject and export request surface review | Dedicated surfaces carry evidence review, redaction choices, and human disposition; chat should not compress these steps. |
| `approval-gated` | Redacted export request; audit summary task start | Export delivery and model-backed summaries require approval/redaction or provider fail-closed semantics beyond chat confirmation. |
| `blocked-pending-design` | Unredacted/raw browser export, hidden evidence delivery, escalation workflows | Need explicit export/redaction/approval design and tests before any chat execution. |
| `internal-only` | Trace ingestion, retention/projection gap detection, provider/raw event normalization, summary workers | Service/background evidence paths are not direct chat catalog steps. |
| `out-of-scope` | Business-domain audit actions outside foundation trace investigation | Outside the five foundation workstreams for this catalog expansion. |

Execution requirements for every accepted Audit/Trace entry:

- proposal step validation rejects out-of-catalog action/tool/capability combinations, hidden traces/correlations, unsafe output bindings, missing provider/runtime/tool-boundary readiness, unsupported input fields, unredacted export attempts, and raw provider/prompt/tool payload exposure;
- confirmation must bind `planId`, `planSnapshotId`, selected `AuthContext`, requestedBy, confirmedBy, step hashes, idempotency root, correlation id, and visible side-effect acknowledgements;
- every confirmed step recomputes backend authorization, selected tenant/customer scope, redaction policy, idempotency, and trace emission, then returns only browser-safe result surfaces or safe system messages;
- idempotent replay returns prior read/note results without duplicate notes, exports, provider calls, or traces; partial failure reports completed, failed, skipped, and recovery steps;
- no workstream agent, prompt, frontend route, disabled/visible control, or tool description grants autonomous mutation authority.
