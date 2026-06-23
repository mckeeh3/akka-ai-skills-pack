# Tools: Governance/Policy

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Allowed governed tools: list-policy-proposals, draft-policy-proposal, simulate-policy-change, approve-activate-or-rollback-policy, record-policy-outcome-note, start-policy-impact-analysis, read-policy-impact-analysis, cancel-policy-impact-analysis, accept-policy-impact-result, reject-policy-impact-result, request-policy-impact-changes.

Tools are exposed as browser, agent, or internal tools only as stated by the linked capability. Side-effecting or high-impact tools require idempotency, correlation, authorization, approval policy, and audit/work traces. Denied tool calls are traced and return safe feedback.

Impact-analysis tools are advisory task tools. They may create, read, cancel, or disposition an impact-analysis task/result, but they do not approve, activate, roll back, weaken security, expand authority, or mutate policy state except by linking evidence/disposition metadata to the proposal.


## `human_chat_tool_plan` shared adapter catalog

The first-pass chat executable catalog for Governance/Policy reuses the same backend-governed tool ids as the corresponding surface actions. Exposure channel `human_chat_tool_plan` is proposal-and-confirmation only: the initial chat request can return a plan proposal surface, but no mutation occurs until explicit human confirmation and backend authorization succeed.

| Adapter exposure | Representative prompt | Surface action ids | Shared governed tool ids | Capability ids | Input schema and validation | Result surface(s) |
|---|---|---|---|---|---|---|
| `human_chat_tool_plan` | `draft a policy proposal to require approval before redacted exports` | `action-governance-policy-draft-proposal` | `governance.policy.propose` | `governance.policy.propose` | `schema.governance-policy.proposal.draft.v1` with title, rationale, browser-safe proposed change summary, affected capabilities, and idempotency key | `surface-governance-policy-proposal` |

Execution requirements:

- proposal step validation rejects out-of-catalog action/tool/capability combinations, hidden targets, unsafe output bindings, missing provider/runtime/tool-boundary readiness, and unsupported input fields;
- confirmation must bind `planId`, `planSnapshotId`, selected `AuthContext`, requestedBy, confirmedBy, step hashes, idempotency root, correlation id, and visible side-effect acknowledgements;
- every confirmed step recomputes backend authorization and approval policy, uses its own idempotency key and transaction boundary, emits trace evidence, and returns the declared typed result surface or safe system message;
- idempotent replay returns the prior proposal/result without duplicating side effects; partial failure reports completed, failed, skipped, and recovery steps;
- no workstream agent, prompt, frontend route, disabled/visible control, or tool description grants autonomous mutation authority.
