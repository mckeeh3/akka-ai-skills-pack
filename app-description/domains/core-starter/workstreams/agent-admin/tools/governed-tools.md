# Tools: Agent Admin

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Allowed governed tools: list-agent-catalog, read-agent-behavior-detail, draft-agent-behavior-proposal, submit-agent-behavior-proposal, approve-agent-behavior-proposal, reject-agent-behavior-proposal, defer-agent-behavior-proposal, cancel-agent-behavior-proposal, activate-agent-behavior-version, rollback-agent-behavior-version, deactivate-agent-behavior-version, start-agent-prompt-risk-review, read-agent-prompt-risk-review, accept-agent-prompt-risk-review, reject-agent-prompt-risk-review, cancel-agent-prompt-risk-review, prepare-agent-seed-import, start-agent-seed-import, cancel-agent-seed-import, readSkill, readReferenceDoc.

Tools are exposed as browser, agent, or internal tools only as stated by the linked capability. Side-effecting or high-impact tools require idempotency, correlation, authorization, approval policy, and audit/work traces. Denied tool calls are traced and return safe feedback.


## `human_chat_tool_plan` shared adapter catalog

The first-pass chat executable catalog for Agent Admin reuses the same backend-governed tool ids as the corresponding surface actions. Exposure channel `human_chat_tool_plan` is proposal-and-confirmation only: the initial chat request can return a plan proposal surface, but no mutation occurs until explicit human confirmation and backend authorization succeed.

| Adapter exposure | Representative prompt | Surface action ids | Shared governed tool ids | Capability ids | Input schema and validation | Result surface(s) |
|---|---|---|---|---|---|---|
| `human_chat_tool_plan` | `start prompt risk review for the Agent Admin prompt proposal` | `action-agent-prompt-risk-review-start` | `agent_admin.start_behavior_review_task` | `agent_admin.start_behavior_review_task` | `schema.agent-admin.prompt-risk-review.start.v1` with visible `agentDefinitionId`, `proposalId`, redacted `artifactDeltas`, reason, and idempotency key | `surface-agent-admin-prompt-risk-review` |

Execution requirements:

- proposal step validation rejects out-of-catalog action/tool/capability combinations, hidden targets, unsafe output bindings, missing provider/runtime/tool-boundary readiness, and unsupported input fields;
- confirmation must bind `planId`, `planSnapshotId`, selected `AuthContext`, requestedBy, confirmedBy, step hashes, idempotency root, correlation id, and visible side-effect acknowledgements;
- every confirmed step recomputes backend authorization and approval policy, uses its own idempotency key and transaction boundary, emits trace evidence, and returns the declared typed result surface or safe system message;
- idempotent replay returns the prior proposal/result without duplicating side effects; partial failure reports completed, failed, skipped, and recovery steps;
- no workstream agent, prompt, frontend route, disabled/visible control, or tool description grants autonomous mutation authority.
