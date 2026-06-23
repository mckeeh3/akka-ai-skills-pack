# Tools: Agent Admin

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Allowed governed tools: list-agent-catalog, read-agent-behavior-detail, draft-agent-behavior-proposal, submit-agent-behavior-proposal, approve-agent-behavior-proposal, reject-agent-behavior-proposal, defer-agent-behavior-proposal, cancel-agent-behavior-proposal, activate-agent-behavior-version, rollback-agent-behavior-version, deactivate-agent-behavior-version, start-agent-prompt-risk-review, read-agent-prompt-risk-review, accept-agent-prompt-risk-review, reject-agent-prompt-risk-review, cancel-agent-prompt-risk-review, prepare-agent-seed-import, start-agent-seed-import, cancel-agent-seed-import, readSkill, readReferenceDoc.

Tools are exposed as browser, agent, or internal tools only as stated by the linked capability. Side-effecting or high-impact tools require idempotency, correlation, authorization, approval policy, and audit/work traces. Denied tool calls are traced and return safe feedback.


## `human_chat_tool_plan` expanded current-intent catalog

This catalog records current intent for later runtime expansion. It reuses the same governed tool ids as browser surface actions and does not by itself change runtime behavior. Exposure channel `human_chat_tool_plan` remains proposal-and-confirmation only: deterministic no-mutation surface routing runs first, the initial execution-oriented chat request may only return a no-mutation plan proposal, and no state changes until exact human plan-snapshot confirmation and backend authorization succeed.

Allowed expanded entries:

| Classification | Representative prompts | Surface action ids | Shared governed tool ids | Capability ids | Input schema and validation | Result surface(s) |
|---|---|---|---|---|---|---|
| `approval-gated` | `start prompt risk review for this prompt proposal` | `action-agent-prompt-risk-review-start` | `agent_admin.start_behavior_review_task` | `agent_admin.start_behavior_review_task` | `schema.agent-admin.prompt-risk-review.start.v1`; require visible `agentDefinitionId`, proposal id, redacted artifact deltas, reason, provider/runtime/tool-boundary fail-closed evidence, and idempotency key | `surface-agent-admin-prompt-risk-review` |
| `chat-proposal-only` | `run a no-side-effect test for this agent`; `simulate prompt governance`; `simulate skill manifest`; `simulate tool boundary`; `run model reference readiness test` | `action-agent-detail-run-test`; `action-agent-prompt-governance-simulate`; `action-agent-skill-manifest-simulate`; `action-agent-tool-boundary-simulate`; `action-agent-model-refs-run-test` | agent-admin simulation/test governed tools | `agent_admin.start_behavior_review_task` or specific simulation capabilities | Simulation/run-test schemas under `schema.agent-admin.*`; require visible agent/artifact ids, no-side-effect tool boundary, provider/runtime fail-closed state, redacted outputs, and idempotency key | Test console, governance simulation, model refs, or safe system-message surfaces |
| `chat-proposal-only` | `submit this prompt governance change for review`; `submit skill manifest review`; `submit tool boundary review`; `submit model references for review` | `action-agent-prompt-governance-submit-review`; `action-agent-skill-manifest-submit-review`; `action-agent-tool-boundary-submit-review`; `action-agent-model-refs-submit-review`; `action-propose-prompt-diff`; `action-submit-behavior-change`; `action-agent-behavior-proposal-submit` | `draft-agent-behavior-proposal`; `submit-agent-behavior-proposal` | `agent_admin.draft_behavior_change`; `agent_admin.submit_behavior_change_for_review` | Submit-review schemas under `schema.agent-admin.*submit-review*`; require visible draft/proposal ids, redacted diff summaries, authority-expansion denial, no activation, and idempotency key | `surface-agent-behavior-proposal`; artifact review surfaces |

Expanded classification and blocked/surface-only rationale:

| Classification | Action groups | Rationale and boundary |
|---|---|---|
| `router-only` | Dashboard/catalog/search/filter/detail opens and refreshes | Structured surfaces preserve target visibility and redaction; chat plans must not enumerate hidden agents or raw artifacts. |
| `surface-only` | Target-specific artifact reads, seed material prepare/read, prompt-risk read/source/trace opens, trace drill-ins | The browser surface owns redacted evidence review, target binding, and diagnostics. |
| `chat-proposal-only` | No-side-effect tests/simulations and behavior/prompt/skill/tool/model submit-review paths | These may create advisory evidence or proposal artifacts only; active managed-agent behavior remains unchanged. |
| `approval-gated` | Prompt-risk review start; proposal approve/reject/defer/cancel; prompt/skill/tool/model review decisions; seed import start/cancel; lifecycle activation/deactivation/rollback | These require visible proposal state, provider/tool-boundary readiness, human governance decisions, and lifecycle prerequisites beyond chat confirmation. |
| `blocked-pending-design` | Direct activation/rollback/deactivation without approved proposal metadata; trace export/escalation from Agent Admin; customization-overwriting seed import | High-impact authority or evidence export needs separate approval/redaction/recovery design. |
| `internal-only` | Skill/reference loaders, provider readiness probes, import diff builders, task workers, audit projection consumers | Governed service/model/provider paths are available only through assigned boundaries and not direct chat steps. |
| `out-of-scope` | Business-domain managed agents and non-foundation agent teams | Outside the five foundation workstreams for this catalog expansion. |

Execution requirements for every accepted Agent Admin entry:

- proposal step validation rejects out-of-catalog action/tool/capability combinations, hidden targets, unsafe output bindings, missing provider/runtime/tool-boundary readiness, unsupported input fields, and any prompt/skill/model text that attempts to grant authority;
- confirmation must bind `planId`, `planSnapshotId`, selected `AuthContext`, requestedBy, confirmedBy, step hashes, idempotency root, correlation id, and visible side-effect/approval acknowledgements;
- every confirmed step recomputes backend authorization and approval policy, uses its own idempotency key and transaction boundary, emits trace evidence, and returns the declared typed result surface or safe system message;
- provider/model/runtime unavailable states are `provider_blocked`/`noFakeSuccess` outcomes, not successful analysis;
- no workstream agent, prompt, frontend route, disabled/visible control, or tool description grants autonomous mutation authority.
