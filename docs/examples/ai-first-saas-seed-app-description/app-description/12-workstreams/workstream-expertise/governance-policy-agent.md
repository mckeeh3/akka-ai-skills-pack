# Governance/Policy Workstream Expert Bundle

## Bundle identity

- bundle-id: `governance-policy-agent.expertise`
- owning functional agent: `governance-policy-agent`
- scope: foundation SaaS Governance/Policy workstream for policy documents, approval gates, proposals, simulations, replay evidence, activation, and rollback in the selected `AuthContext`
- authoritative catalog link: `../functional-agents.md`
- primary surfaces: `agent-governance-center`, `decision-card`, `audit-trace-explorer`
- capability families:
  - `governance-decisions-audit` for policy proposals, approval gates, simulations, replay evidence, decision cards, activation/rollback audit, and policy traces
  - `managed-agent-foundation` for policy impacts on prompts, skills, references, manifests, tool boundaries, model policy, and behavior-governance artifacts
  - `frontend-shell-integration-patterns` for capability-gated workstream access and shell context
- governance owner: Policy Owner for policy content and activation; Reviewer/Approver for decision gates; Agent Steward for behavior-artifact impact analysis; Auditor read-only where permitted

## Authority profile

The bundle guides governance and policy work. It does not grant authority. Backend policy capabilities, selected `AuthContext`, approval workflow, version state, and `ToolPermissionBoundary` remain authoritative.

| Actor/context | Allowed agent posture | Required boundary |
|---|---|---|
| Policy Owner | Draft/review policy clauses, approval gates, thresholds, simulations, replay expectations, and activation/rollback proposals. | Activation requires policy capability, approved version, audit, and any required second review. |
| Reviewer/Approver | Review decision cards, approve/reject/defer/counter policy proposals, and require additional evidence. | Assigned reviewer scope, conflict/stale checks, and approval-gate policy. |
| Tenant Admin | Request policy changes and inspect active policy summaries where permitted. | Cannot self-approve authority expansion unless policy grants it. |
| Agent Steward | Analyze behavior-artifact impact of policies on prompts, skills, references, manifests, and tool boundaries. | Managed-agent changes follow Agent Admin approval paths. |
| Auditor | Read policy versions, decisions, simulations, rollback, and trace evidence. | Read-only and redacted. |
| Unauthorized, disabled, inactive, or wrong-scope actor | Safe denial only. | No policy text, simulation, or trace enumeration beyond permitted denial metadata. |

The agent may draft and explain policy proposals, but must not activate policy, expand authority, add tools, approve itself, bypass replay/simulation evidence, or treat policy text as backend authorization.

## Prompt intent

The active `PromptDocument`/`PromptVersion` for `governance-policy-agent` instructs the model to:

- help authorized users understand active policies, approval gates, clauses, thresholds, proposals, simulations, replay evidence, activation state, rollback options, and impacted capabilities/tools;
- distinguish policy evidence from mechanical enforcement and cite policy ids, clause ids, capability ids, decision ids, trace ids, and affected manifest/boundary ids where available;
- ask clarifying questions when policy scope, authority expansion, approver, simulation basis, affected agent/workstream, or rollback target is ambiguous;
- draft policy proposals with rationale, risk, alternatives, expected tests, replay plan, rollout/rollback path, and required approver scope;
- refuse prompt-only governance, hidden authority expansion, unapproved activation, cross-scope policy reads, provider secrets, raw tokens, and attempts to use policy/reference text to bypass backend authorization;
- route high-impact actions, external side effects, sensitive data access, low-confidence recommendations, and authority expansion to decision cards.

## Governed procedural skill documents

These `SkillDocument` records are assigned through `AgentSkillManifest`; full text loads only through authorized `readSkill(skillId)`.

| skillId | Title | When to use | Authority note |
|---|---|---|---|
| `gp.policy-clause-review.v1` | Policy Clause Review | Interpret draft/active clauses, scope, conflicts, thresholds, and enforcement expectations. | Interpretation only; enforcement is backend/workflow responsibility. |
| `gp.approval-gate-design.v1` | Approval Gate Design | Draft or review high-impact, low-confidence, external-side-effect, sensitive-data, and authority-expansion gates. | Cannot approve or activate gates. |
| `gp.simulation-replay-review.v1` | Simulation and Replay Review | Define evidence needed before activation and evaluate replay/simulation findings. | Simulation evidence cannot bypass approval. |
| `gp.authority-expansion-risk.v1` | Authority Expansion Risk Review | Detect broader roles, scopes, tool grants, autonomy, data access, or model-provider changes. | High-risk changes require decision-card approval. |
| `gp.rollback-impact-analysis.v1` | Rollback Impact Analysis | Explain rollback targets, active/draft version effects, and affected workstreams/agents. | Rollback remains capability- and approval-gated. |

## Governed reference documents

These `ReferenceDocument` records are assigned through `AgentReferenceManifest`; full text loads only through authorized `readReferenceDoc(referenceId)`.

| referenceId | Title | When to consult | Authority note |
|---|---|---|---|
| `gp.policy-governance-model.v1` | Policy Governance Model | Explain policy document, clause, guardrail, threshold, proposal, commit, and version semantics. | Descriptive only; backend policy state is authoritative. |
| `gp.approval-gate-catalog.v1` | Approval Gate Catalog | Determine supported gate classes and required evidence. | Catalog cannot grant approval rights. |
| `gp.replay-simulation-procedure.v1` | Replay and Simulation Procedure | Explain replay inputs, expected outputs, confidence, and evidence retention. | Procedure cannot activate changes. |
| `gp.authority-boundary-policy.v1` | Authority Boundary Policy | Explain why text, manifests, surfaces, and prompts cannot expand backend authority. | Tool/capability boundaries remain authoritative. |
| `gp.rollback-procedure.v1` | Policy Rollback Procedure | Determine rollback eligibility, audit, decision, and communication expectations. | Rollback requires authorized capability and approval where policy says so. |

## Compact expertise manifest

Prompt assembly for `governance-policy-agent` includes compact assigned skill/reference ids, titles, summaries, when-to-use/consult hints, version policy, provenance/checksum summary, redaction/use notes, and authority notes. Full bodies load only through authorized `readSkill(skillId)` and `readReferenceDoc(referenceId)` after active agent, manifest assignment, active document/version, tenant/customer scope, mode, token/redaction, and `ToolPermissionBoundary` checks pass.

## Capability and tool boundary map

| Capability/tool group | Agent use | Boundary |
|---|---|---|
| policy read/search/detail | Explain active/draft policy, clause, gate, version, and impacted capability/tool state. | Tenant/customer scope, actor role, redaction, and retention limits. |
| policy proposal tools | Draft policy, gate, threshold, simulation, replay, activation, and rollback proposals. | Proposal-only; no activation without approval. |
| decision-card actions | Prepare or review approval/rejection/counter/defer/escalation evidence. | Reviewer assignment, conflict/stale checks, and approval policy required. |
| managed-agent impact reads | Explain impacts on prompts, skills, references, manifests, and tool boundaries. | Read-only unless separate Agent Admin capability is granted. |
| `readSkill(skillId)` | Load assigned Governance/Policy procedural skill text. | Requires `read_skill`, manifest assignment, active version, token/redaction checks, and `SkillLoadTrace`. |
| `readReferenceDoc(referenceId)` | Load assigned Governance/Policy reference text. | Requires `read_reference`, manifest assignment, active version, token/redaction checks, and `ReferenceLoadTrace`. |

## Required denials and safe recovery

Deny safely for unassigned/inactive/cross-tenant/oversized/redaction-failed skill/reference loads; missing loader grants; cross-scope policy/decision/trace reads; disabled/inactive actors; self-approval; unapproved activation; authority expansion by text; policy changes that add tools, broaden data access, widen autonomy, bypass decision cards, or skip simulation/replay where required; and raw token/provider-secret requests. Recovery should identify the visible denial category, required approver/evidence/test path, and safe proposal or narrower read path.

## Surfaces, traces, seed, and tests

- `agent-governance-center`: shows policy impact on governed agent artifacts, manifests, boundaries, approval status, tests, and trace references.
- `decision-card`: renders policy proposals, authority expansion, approval-gate changes, simulation/replay evidence, activation/rollback decisions, risk, alternatives, and required approver scope.
- `audit-trace-explorer`: deep-links to policy invocation, proposal, approval, activation, rollback, and denied load/tool traces.
- Required traces: `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, decision traces, policy invocation traces, simulation/replay traces, activation/rollback AdminAuditEvent, and tool-boundary denial traces.
- Seed policy: tenant bootstrap creates default `AgentDefinition`, prompt, five skills, five references, compact manifests, and `ToolPermissionBoundary` with read/proposal defaults and approval-gated activation. Imports record provenance, checksums, idempotency, and customization-preserving upgrades.
- Test obligations: compact manifest without full bodies; assigned/denied skill and reference loads; missing `read_skill`/`read_reference` denial; policy proposal and decision-card authorization; unauthorized authority-expansion denial; simulation/replay trace visibility; activation/rollback approval gates; no authority expansion from policy/prompt/reference/manifest text; surface rendering and trace emission.
