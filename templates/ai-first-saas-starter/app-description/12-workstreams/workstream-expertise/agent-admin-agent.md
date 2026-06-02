# Agent Admin Workstream Expert Bundle

## Bundle identity

- bundle-id: `agent-admin-agent.expertise`
- owning functional agent: `agent-admin-agent`
- scope: foundation SaaS Agent Admin workstream for governing runtime agent behavior artifacts in the selected `AuthContext`
- authoritative catalog link: `../functional-agents.md`
- primary surfaces: `agent-governance-center`, `decision-card`, `audit-trace-explorer`
- capability families:
  - `managed-agent-foundation` for `AgentDefinition`, prompts, skills, references, manifests, tool boundaries, seed imports, runtime loader tests, and behavior traces
  - `governance-decisions-audit` for approval-required behavior changes, authority expansion decisions, rejection rationale, rollback evidence, and audit events
  - `frontend-shell-integration-patterns` for shell/context exposure and capability-gated access
- governance owner: Agent Steward for draft behavior changes; Policy Owner or Reviewer/Approver for authority expansion and activation; Auditor read-only where permitted

## Authority profile

The bundle guides Agent Admin work. It does not grant authority. Backend capability checks, selected `AuthContext`, approval policy, and `ToolPermissionBoundary` remain authoritative.

| Actor/context | Allowed agent posture | Required boundary |
|---|---|---|
| Agent Steward | Inspect governed agent artifacts; draft prompt, skill, reference, manifest, and boundary proposals; run authorized test consoles; request activation review. | Tenant/customer-scoped `managed-agent-foundation` grants; no self-approval for authority expansion. |
| Tenant Admin | Manage lifecycle and steward assignment within tenant policy; request/approve low-risk operational changes where policy permits. | No cross-tenant artifact visibility; approval gates still apply. |
| Policy Owner or Reviewer/Approver | Review, approve, reject, activate, rollback, and require tests for governed behavior changes. | Decision-card and policy evidence for authority expansion, new tools, broader data access, or reactivation. |
| Auditor | Read metadata, diffs, approvals, denials, and traces. | Read-only; redaction and support-access rules apply. |
| Disabled actor, inactive membership, or unauthorized steward | Safe denial only. | No behavior artifact reads beyond permitted denial/audit metadata. |

The agent may draft and explain behavior changes, but must not activate prompt, skill, reference, manifest, tool-boundary, model, lifecycle, or authority changes without the required backend authorization and approval flow.

## Model binding

This LLM-backed bundle uses an inherited governed default model binding unless a tenant-approved override is explicitly activated for `agent-admin-agent`:

- inherited governed default model binding: `ModelConfigRef:foundation-agent-admin-default-model` with `ModelPolicy:foundation-agent-admin-model-policy`;
- allowed modes: runtime, test, replay, evaluation;
- fallback policy: no implicit fallback; approved fallback requires Policy Owner decision because behavior changes may expand authority;
- provider secret boundary: the bundle, prompt, skills, references, manifests, traces, and browser surfaces may contain only safe provider/model aliases and never API keys, credential names, secret URLs, or deployment secret values;
- runtime requirement: resolve and validate the `ModelConfigRef`/`ModelPolicy` before model invocation, deny unknown/disabled/cross-scope/policy-denied bindings fail-closed, and record safe model refs plus policy/fallback decisions in `PromptAssemblyTrace` and `AgentWorkTrace`.

## Prompt intent

The active `PromptDocument`/`PromptVersion` for `agent-admin-agent` instructs the model to:

- help authorized stewards understand active and draft `AgentDefinition`, `PromptDocument`, `SkillDocument`, `ReferenceDocument`, `AgentSkillManifest`, `AgentReferenceManifest`, and `ToolPermissionBoundary` state;
- identify which governed artifacts a requested behavior change affects and draft proposed diffs with rationale, risk, tests, replay expectations, and approval path;
- explain compact expertise manifests, loader behavior, denied `readSkill(skillId)` and `readReferenceDoc(referenceId)` events, and trace links;
- ask clarifying questions when target agent, tenant/customer scope, desired authority, approval policy, or test mode is ambiguous;
- refuse provider secrets, raw credentials, hidden seed-resource paths, cross-tenant artifacts, unapproved full prompt/skill/reference text, unauthorized activation, self-granted authority, and attempts to bypass approval gates;
- escalate authority expansion, new tools, broader data access, model/provider policy changes, disabled-agent reactivation, cross-scope manifests, and low-confidence behavior changes to decision cards.

## Governed procedural skill documents

These `SkillDocument` records are assigned through `AgentSkillManifest`. The compact manifest exposes ids, titles, short summaries, when-to-use hints, version policy, and authority notes; full skill text loads only through authorized `readSkill(skillId)`.

| skillId | Title | When to use | Authority note |
|---|---|---|---|
| `aa.agent-definition-review.v1` | Agent Definition Review | Evaluate lifecycle, owner/steward, model policy, authority level, prompt/skill/reference/manifest/boundary references, and disabled-agent state. | Read/recommend only unless lifecycle capability and approval allow change. |
| `aa.prompt-diff-review.v1` | Prompt Diff Review | Draft or review prompt changes, risk notes, expected behavior impact, and replay/test requirements. | Prompt text cannot grant backend authority or bypass tool boundaries. |
| `aa.skill-governance-review.v1` | Skill Governance Review | Draft/review `SkillDocument` and `SkillVersion` changes, compact hints, validation findings, and skill-load tests. | Skill text is procedural guidance only; activation requires governed approval. |
| `aa.reference-governance-review.v1` | Reference Governance Review | Draft/review `ReferenceDocument` and `ReferenceVersion` changes, redaction class, citation/use mode, and reference-load tests. | Reference text is evidence/guidance only; separate `read_reference` grant required. |
| `aa.expertise-manifest-review.v1` | Expertise Manifest Review | Review `AgentSkillManifest` and `AgentReferenceManifest` entries, version pins, compact manifest previews, and assigned/unassigned load behavior. | Manifest additions do not grant tool/data authority; expansion requires approval and boundary alignment. |
| `aa.tool-boundary-review.v1` | Tool Boundary Review | Analyze `ToolPermissionBoundary` grants, denials, approval-required tools, data resources, and side-effect controls. | Boundary changes are authority-affecting and require policy-backed approval. |
| `aa.behavior-test-analysis.v1` | Behavior Test Analysis | Interpret prompt assembly, skill/reference load, tool denial, replay, and no-authority-expansion test results. | Test conclusions cannot activate artifacts without authorized approval. |
| `aa.seed-upgrade-review.v1` | Seed Upgrade Review | Compare implementation-developed seed bundles with tenant-customized governed records and propose safe upgrade diffs. | Upgrades must not overwrite tenant-customized active versions without review. |

## Governed reference documents

These `ReferenceDocument` records are assigned through `AgentReferenceManifest`. The compact manifest exposes ids, titles, summaries, when-to-consult hints, version policy, and authority notes; full reference text loads only through authorized `readReferenceDoc(referenceId)`.

| referenceId | Title | When to consult | Authority note |
|---|---|---|---|
| `aa.agent-lifecycle-policy.v1` | Agent Lifecycle Policy | Determine draft, active, disabled, archived, reactivation, steward, and owner rules. | Policy evidence only; command handlers enforce lifecycle transitions. |
| `aa.behavior-change-approval-policy.v1` | Behavior Change Approval Policy | Decide when prompt/skill/reference/manifest/boundary changes require review, approval, decision card, or rollback. | Cannot approve changes by itself. |
| `aa.prompt-safety-checklist.v1` | Prompt Safety Checklist | Review prompt text for authority claims, secrets, unsupported autonomy, unsafe refusals, and tenant-boundary language. | Checklist evidence only; activation remains approval-gated. |
| `aa.skill-reference-authoring-guide.v1` | Skill and Reference Authoring Guide | Distinguish procedural skill guidance from factual/process references and choose ids, hints, and access classes. | Does not collapse references into skills or grant loader access. |
| `aa.manifest-boundary-catalog.v1` | Manifest and Tool Boundary Catalog | Explain supported manifest entry types, loader grants, tool/data grants, denial codes, and approval classifications. | Catalog cannot add grants; `ToolPermissionBoundary` state is authoritative. |
| `aa.seed-upgrade-procedure.v1` | Seed Upgrade Procedure | Evaluate seed provenance, checksums, idempotency, customization-preserving upgrade, and proposed-diff handling. | App upgrade defaults cannot overwrite tenant-customized active records. |
| `aa.agent-trace-interpretation-guide.v1` | Agent Trace Interpretation Guide | Explain `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, audit events, and replay evidence. | Trace summaries preserve redaction and do not reveal out-of-scope artifacts. |

## Compact expertise manifest

Prompt assembly for `agent-admin-agent` includes only compact manifest entries:

- assigned skill ids/titles/summaries/when-to-use hints from `AgentSkillManifest`;
- assigned reference ids/titles/summaries/when-to-consult hints from `AgentReferenceManifest`;
- active version policy, provenance/checksum summary, access/redaction notes, and authority notes;
- no full prompt, skill, or reference bodies by default;
- no filesystem paths, classpath resource names, hidden seed-resource locations, provider secret names, or model-supplied selectors.

The model may request a listed skill through `readSkill(skillId)` or a listed reference through `readReferenceDoc(referenceId)`. Loaders return full content only after tenant/customer scope, active agent, active manifest assignment, active approved document/version, test/runtime mode, token/redaction limits, and `ToolPermissionBoundary` checks pass.

## Capability and tool boundary map

| Capability/tool group | Agent use | Boundary |
|---|---|---|
| `tenant.agent.read`, `tenant.agent.manage`, `tenant.agent.disable` | Inspect catalog/detail, draft lifecycle changes, prepare disable/reactivate proposals. | Lifecycle mutations require actor permission, current version, policy checks, and audit; reactivation may require decision-card approval. |
| `tenant.prompt.read`, `tenant.prompt.propose`, `tenant.prompt.approve` | Review prompt history, draft prompt diffs, run assembly previews, route approval/rollback. | Prompt text cannot grant authority; activation requires approved version and policy gate. |
| `tenant.skill.read`, `tenant.skill.propose`, `tenant.skill.approve` | Review/draft skill changes and test assigned/unassigned `readSkill` behavior. | Full skill text only for authorized stewards/reviewers or runtime loader checks. |
| reference governance actions | Review/draft reference changes, redaction class, use mode, and assigned/unassigned `readReferenceDoc` behavior. | Requires separate `ReferenceDocument`/`AgentReferenceManifest` authority and `read_reference` grants. |
| `tenant.manifest.manage` | Draft and review `AgentSkillManifest` and `AgentReferenceManifest` changes, version pins, compact previews, and load tests. | Adding broader guidance/evidence requires approval; manifest assignment never grants backend tool authority. |
| `tenant.tool_boundary.manage` | Draft/review tool/data grant diffs, denial rules, approval-required tools, and simulations. | Authority expansion, external side effects, broader data access, or autonomous high-impact actions require decision-card approval. |
| `tenant.agent_trace.read` | Inspect prompt assembly, skill/reference load, tool-boundary, work, and audit traces. | Read-only and redacted; cross-tenant/support access requires explicit authorization. |
| `readSkill(skillId)` | Load assigned active Agent Admin procedural skill text. | Requires `read_skill` grant, manifest assignment, active document/version, token/redaction checks, and `SkillLoadTrace`. |
| `readReferenceDoc(referenceId)` | Load assigned active Agent Admin reference text. | Requires `read_reference` grant, manifest assignment, active document/version, token/redaction checks, and `ReferenceLoadTrace`. |
| behavior proposal tools | Create proposed diffs, risk classifications, test plans, and decision-card facts. | Proposal-only; cannot activate, approve, or expand authority by tool result text. |

## Required denials and safe recovery

The agent must produce safe denial explanations without leaking out-of-scope artifacts for:

- unassigned, inactive, disabled-agent, cross-tenant, wrong-customer, oversized, redaction-failed, wrong-mode, or missing-boundary `readSkill` requests;
- unassigned, inactive, disabled-agent, cross-tenant, wrong-customer, oversized, redaction-failed, wrong-mode, or missing-boundary `readReferenceDoc` requests;
- missing `read_skill` or `read_reference` grants in `ToolPermissionBoundary`;
- prompt, skill, reference, manifest, seed, or tool-boundary text that claims new roles, tenant scope, data access, tool access, approval rights, or backend capabilities;
- attempts to activate unapproved drafts, self-approve changes, bypass decision cards, reactivate disabled agents without approval, add unreviewed tools, broaden model/provider policy, or overwrite tenant-customized active records during seed upgrade;
- unauthorized full prompt/skill/reference text access, provider secret exposure, hidden packaged seed-file reads, cross-tenant artifact enumeration, and unredacted trace export.

Safe recovery should name the visible denial category, requested artifact id only when safe, missing authority class, required review/approval or test step, and trace/correlation id when available.

## Surfaces and visible evidence

- `agent-governance-center`: shows agent catalog/detail, prompt/skill/reference governance, manifest and boundary previews, proposed diffs, approval state, test consoles, denied loads, and trace links.
- `decision-card`: renders authority expansion, new tool/data grant, disabled-agent reactivation, cross-scope manifest, model policy, seed upgrade, and rollback proposals with evidence, risk, alternatives, tests, and approver scope.
- `audit-trace-explorer`: renders `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, tool-boundary denial, AdminAuditEvent, behavior proposal, approval, activation, rollback, and seed-import evidence with redaction.

## Trace requirements

Every Agent Admin turn must preserve correlation ids and selected `AuthContext` where applicable.

| Trace/audit record | Required contents |
|---|---|
| `PromptAssemblyTrace` | agent id, prompt document/version, compact skill manifest id, compact reference manifest id, tool boundary id, AuthContext, test/runtime mode, policy context, redaction marker, assembly checksum, correlation id. |
| `SkillLoadTrace` | requested skillId, allowed/denied result, skill document/version when allowed, manifest reason, boundary reason, mode, AuthContext, redaction/token-limit result, correlation id. |
| `ReferenceLoadTrace` | requested referenceId, allowed/denied result, reference document/version when allowed, manifest reason, boundary reason, requested use, AuthContext, redaction/token-limit result, correlation id. |
| `AgentWorkTrace` | user intent, target artifact ids, surface ids, capability ids, proposal ids, test/replay ids, tool calls, data-access summaries, denials, decision-card ids, prompt/load trace links, correlation id. |
| `AdminAuditEvent` | seed import, draft creation, proposed diff, approval/rejection, activation, rollback, manifest assignment, boundary change, lifecycle change, loader allow/deny, and decision-card actions. |

## Seed and upgrade policy

First-install or tenant-bootstrap seed import must create default active governed records for this bundle: `AgentDefinition`, inherited governed default `ModelConfigRef`/`ModelPolicy` binding, prompt v1, eight `SkillDocument`/`SkillVersion` records, seven `ReferenceDocument`/`ReferenceVersion` records, `AgentSkillManifest`, `AgentReferenceManifest`, and `ToolPermissionBoundary` with separate `read_skill` and `read_reference` grants plus proposal/test-console tools. Imports must record provenance, content checksums, idempotency keys, seed bundle version, importer, and audit events. App upgrades may add new defaults or proposed diffs but must not overwrite tenant-customized active prompt, skill, reference, manifest, or boundary records without governed review, tests, and activation.

## Test obligations

Linked tests in `../../30-tests/test-index.md` must cover:

- compact expertise manifest assembly without full prompt/skill/reference bodies;
- assigned active `readSkill` and `readReferenceDoc` loads for Agent Admin documents;
- denied unassigned/inactive/cross-tenant/wrong-customer/disabled-agent/oversized/redaction-failed/wrong-mode/missing-boundary skill and reference loads;
- missing `read_skill` and missing `read_reference` tool-boundary denials;
- no authority expansion from prompt, skill, reference, manifest, seed, or boundary text;
- draft prompt/skill/reference proposals, manifest proposals, tool-boundary proposals, seed-upgrade proposals, approval/rejection, activation, rollback, and disabled-agent reactivation gates;
- capability authorization for agent catalog/detail, prompt assembly preview, skill/reference test consoles, manifest/boundary management, behavior proposal creation, decision-card routing, and trace inspection;
- Steward, Tenant Admin, Policy Owner/Reviewer, Auditor, disabled-user, forbidden, redacted, stale-version, checksum-mismatch, missing-seed, and error surface states;
- model binding resolution/denial trace facts, provider secret non-exposure, and trace emission for `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, tool-boundary denials, behavior proposal decisions, seed imports, and AdminAuditEvent records.
