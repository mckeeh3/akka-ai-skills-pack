# Tools: Audit/Trace

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Allowed governed tools: read-audit-trace-dashboard, search-audit-traces, read-trace-detail, read-trace-timeline, read-trace-failure-evidence, read-investigation-guide, request-redacted-export, draft-investigation-note, start-audit-summary-task, read-audit-summary-task, review-audit-summary-task, accept-audit-summary-task, and reject-audit-summary-task.

Action-to-tool aliases are canonical: `action-audit-trace-dashboard` -> `read-audit-trace-dashboard`; `action-audit-trace-search` -> `search-audit-traces`; `action-audit-trace-detail` -> `read-trace-detail`; `action-audit-trace-timeline` -> `read-trace-timeline`; `action-audit-trace-failure-evidence` -> `read-trace-failure-evidence`; `action-audit-trace-investigation-guide` -> `read-investigation-guide`; `action-audit-trace-request-redacted-export` -> `request-redacted-export`; `action-audit-trace-append-investigation-note` -> `draft-investigation-note`; `action-audit-trace-summary-task-start` -> `start-audit-summary-task`; `action-audit-trace-summary-task-read` -> `read-audit-summary-task`; `action-audit-trace-summary-review` -> `review-audit-summary-task`; `action-audit-trace-summary-accept` -> `accept-audit-summary-task`; and `action-audit-trace-summary-reject` -> `reject-audit-summary-task`.

Tools are exposed as browser, agent, or internal tools only as stated by the linked capability. Side-effecting or high-impact tools require idempotency, correlation, authorization, approval policy, and audit/work traces. Denied tool calls are traced and return safe feedback.


## `human_chat_tool_plan` shared adapter catalog

The first-pass chat executable catalog for Audit/Trace reuses the same backend-governed tool ids as the corresponding surface actions. Exposure channel `human_chat_tool_plan` is proposal-and-confirmation only: the initial chat request can return a plan proposal surface, but no mutation occurs until explicit human confirmation and backend authorization succeed.

| Adapter exposure | Representative prompt | Surface action ids | Shared governed tool ids | Capability ids | Input schema and validation | Result surface(s) |
|---|---|---|---|---|---|---|
| `human_chat_tool_plan` | `append investigation note "provider blocked; retry after config" to this trace` | `action-audit-trace-append-investigation-note` | `draft-investigation-note` | `audit.trace.investigation_note.append` | `schema.audit-trace.investigation-note.v1` with visible `traceId`/`correlationId`, `noteText`, selected scope, and idempotency key | `surface-audit-trace-investigation-note` |

Execution requirements:

- proposal step validation rejects out-of-catalog action/tool/capability combinations, hidden targets, unsafe output bindings, missing provider/runtime/tool-boundary readiness, and unsupported input fields;
- confirmation must bind `planId`, `planSnapshotId`, selected `AuthContext`, requestedBy, confirmedBy, step hashes, idempotency root, correlation id, and visible side-effect acknowledgements;
- every confirmed step recomputes backend authorization and approval policy, uses its own idempotency key and transaction boundary, emits trace evidence, and returns the declared typed result surface or safe system message;
- idempotent replay returns the prior proposal/result without duplicating side effects; partial failure reports completed, failed, skipped, and recovery steps;
- no workstream agent, prompt, frontend route, disabled/visible control, or tool description grants autonomous mutation authority.
