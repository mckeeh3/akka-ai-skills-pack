# Tools: Governance/Policy

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Allowed governed tools: list-policy-proposals, draft-policy-proposal, simulate-policy-change, approve-activate-or-rollback-policy, record-policy-outcome-note, start-policy-impact-analysis, read-policy-impact-analysis, cancel-policy-impact-analysis, accept-policy-impact-result, reject-policy-impact-result, request-policy-impact-changes.

Tools are exposed as browser, agent, or internal tools only as stated by the linked capability. Side-effecting or high-impact tools require idempotency, correlation, authorization, approval policy, and audit/work traces. Denied tool calls are traced and return safe feedback.

Impact-analysis tools are advisory task tools. They may create, read, cancel, or disposition an impact-analysis task/result, but they do not approve, activate, roll back, weaken security, expand authority, or mutate policy state except by linking evidence/disposition metadata to the proposal.


## `human_chat_tool_plan` expanded current-intent catalog

This catalog records current intent for later runtime expansion. It reuses the same governed tool ids as browser surface actions and does not by itself change runtime behavior. Exposure channel `human_chat_tool_plan` remains proposal-and-confirmation only: deterministic no-mutation surface routing runs first, the initial execution-oriented chat request may only return a no-mutation plan proposal, and no state changes until exact human plan-snapshot confirmation and backend authorization succeed.

Allowed expanded entries:

| Classification | Representative prompts | Surface action ids | Shared governed tool ids | Capability ids | Input schema and validation | Result surface(s) |
|---|---|---|---|---|---|---|
| `chat-executable-now` | `list policy proposals`; `show this policy proposal` | `action-governance-policy-list`; `action-governance-policy-read` | `list-policy-proposals`; `governance.policy.read` | `governance.policy.read` | `schema.governance-policy.inventory.v1`; `schema.governance-policy.detail.v1`; require selected scope, visible/filter-bound proposal or policy ref, redacted output, and no hidden-row enumeration | `surface-governance-policy-inventory`; `surface-governance-policy-detail` |
| `chat-proposal-only` | `draft a policy proposal to require approval before redacted exports`; `submit this policy proposal`; `simulate this policy proposal`; `start impact analysis for this proposal`; `read the impact analysis` | `action-governance-policy-draft-proposal`; `action-governance-policy-submit-proposal`; `action-governance-policy-simulate`; `action-governance-policy-start-impact-analysis`; `action-governance-policy-read-impact-analysis` | `draft-policy-proposal`; `simulate-policy-change`; `start-policy-impact-analysis`; `read-policy-impact-analysis` | `governance.policy.propose`; `governance.policy.simulate`; `governance.policy.impact_analysis.start`; `governance.policy.impact_analysis.read` | Proposal/simulation/impact schemas under `schema.governance-policy.*`; require browser-safe proposed change summary, visible proposal refs, provider/runtime fail-closed state when required, no activation/approval, and idempotency key | Proposal, simulation, and impact-analysis task/result surfaces |

Expanded classification and blocked/surface-only rationale:

| Classification | Action groups | Rationale and boundary |
|---|---|---|
| `router-only` | Governance dashboard open | Deterministic no-mutation route keeps policy attention and queues visible without implying lifecycle authority. |
| `surface-only` | Target-specific detail opens without visible binding; outcome note until target binding exists; impact-result disposition review | Dedicated surfaces preserve visible proposal binding, evidence review, and human reason capture. |
| `chat-proposal-only` | Draft/submit proposal, simulation, start/read impact analysis | These create or read inert/advisory artifacts only; they cannot approve, activate, roll back, weaken policy, or expand authority. |
| `approval-gated` | Policy decide/approve/reject/request changes, outcome note when target binding is complete, impact-analysis cancel/accept/reject/request changes, activation, rollback | Human governance decisions and live authority changes require lifecycle prerequisites and dedicated decision-card semantics beyond chat confirmation. |
| `blocked-pending-design` | Direct activation/rollback/threshold weakening without approved proposal and rollback metadata; unmodeled export policy changes | Live policy authority changes need full approval, simulation evidence, rollback, recovery, and trace policy before chat exposure. |
| `internal-only` | Policy evaluators, enforcement hooks, scheduled outcome follow-ups, impact-analysis workers | Background/governance service paths are not direct chat catalog steps. |
| `out-of-scope` | Business-domain policy surfaces outside foundation Governance/Policy | Outside the five foundation workstreams for this catalog expansion. |

Execution requirements for every accepted Governance/Policy entry:

- proposal step validation rejects out-of-catalog action/tool/capability combinations, hidden policy/proposal targets, unsafe output bindings, missing provider/runtime/tool-boundary readiness, unsupported input fields, direct activation/rollback, authority expansion, and policy-weakening shortcuts;
- confirmation must bind `planId`, `planSnapshotId`, selected `AuthContext`, requestedBy, confirmedBy, step hashes, idempotency root, correlation id, and visible side-effect/approval acknowledgements;
- every confirmed step recomputes backend authorization, selected tenant/customer scope, lifecycle prerequisites, provider fail-closed state when applicable, idempotency, and trace emission;
- idempotent replay returns prior proposal/advisory/read results without duplicate proposals, provider runs, activation, rollback, or traces; partial failure reports completed, failed, skipped, and recovery steps;
- no workstream agent, prompt, frontend route, disabled/visible control, or tool description grants autonomous mutation authority.
