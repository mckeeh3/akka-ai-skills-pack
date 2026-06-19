# Tools: Audit/Trace

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Allowed governed tools: read-audit-trace-dashboard, search-audit-traces, read-trace-detail, read-trace-timeline, read-trace-failure-evidence, read-investigation-guide, request-redacted-export, draft-investigation-note, start-audit-summary-task, read-audit-summary-task, review-audit-summary-task, accept-audit-summary-task, and reject-audit-summary-task.

Action-to-tool aliases are canonical: `action-audit-trace-dashboard` -> `read-audit-trace-dashboard`; `action-audit-trace-search` -> `search-audit-traces`; `action-audit-trace-detail` -> `read-trace-detail`; `action-audit-trace-timeline` -> `read-trace-timeline`; `action-audit-trace-failure-evidence` -> `read-trace-failure-evidence`; `action-audit-trace-investigation-guide` -> `read-investigation-guide`; `action-audit-trace-request-redacted-export` -> `request-redacted-export`; `action-audit-trace-append-investigation-note` -> `draft-investigation-note`; `action-audit-trace-summary-task-start` -> `start-audit-summary-task`; `action-audit-trace-summary-task-read` -> `read-audit-summary-task`; `action-audit-trace-summary-review` -> `review-audit-summary-task`; `action-audit-trace-summary-accept` -> `accept-audit-summary-task`; and `action-audit-trace-summary-reject` -> `reject-audit-summary-task`.

Tools are exposed as browser, agent, or internal tools only as stated by the linked capability. Side-effecting or high-impact tools require idempotency, correlation, authorization, approval policy, and audit/work traces. Denied tool calls are traced and return safe feedback.
