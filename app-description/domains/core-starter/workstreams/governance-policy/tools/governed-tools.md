# Tools: Governance/Policy

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Allowed governed tools: list-policy-proposals, draft-policy-proposal, simulate-policy-change, approve-activate-or-rollback-policy, record-policy-outcome-note, start-policy-impact-analysis, read-policy-impact-analysis, cancel-policy-impact-analysis, accept-policy-impact-result, reject-policy-impact-result, request-policy-impact-changes.

Tools are exposed as browser, agent, or internal tools only as stated by the linked capability. Side-effecting or high-impact tools require idempotency, correlation, authorization, approval policy, and audit/work traces. Denied tool calls are traced and return safe feedback.

Impact-analysis tools are advisory task tools. They may create, read, cancel, or disposition an impact-analysis task/result, but they do not approve, activate, roll back, weaken security, expand authority, or mutate policy state except by linking evidence/disposition metadata to the proposal.
