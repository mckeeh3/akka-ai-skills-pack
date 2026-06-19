# Tools: Agent Admin

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Allowed governed tools: list-agent-catalog, read-agent-behavior-detail, draft-agent-behavior-proposal, submit-agent-behavior-proposal, approve-agent-behavior-proposal, reject-agent-behavior-proposal, defer-agent-behavior-proposal, cancel-agent-behavior-proposal, activate-agent-behavior-version, rollback-agent-behavior-version, deactivate-agent-behavior-version, start-agent-prompt-risk-review, read-agent-prompt-risk-review, accept-agent-prompt-risk-review, reject-agent-prompt-risk-review, cancel-agent-prompt-risk-review, prepare-agent-seed-import, start-agent-seed-import, cancel-agent-seed-import, readSkill, readReferenceDoc.

Tools are exposed as browser, agent, or internal tools only as stated by the linked capability. Side-effecting or high-impact tools require idempotency, correlation, authorization, approval policy, and audit/work traces. Denied tool calls are traced and return safe feedback.
