# Tools: Governance/Policy

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Allowed governed tools: list-policy-proposals, draft-policy-proposal, simulate-policy-change, approve-activate-or-rollback-policy, record-policy-outcome-note.

Tools are exposed as browser, agent, or internal tools only as stated by the linked capability. Side-effecting or high-impact tools require idempotency, correlation, authorization, approval policy, and audit/work traces. Denied tool calls are traced and return safe feedback.
