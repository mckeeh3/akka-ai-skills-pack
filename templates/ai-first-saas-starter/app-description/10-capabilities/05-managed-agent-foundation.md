# Capability: Managed Agent Foundation

This capability makes runtime agent behavior governable as first-class tenant-scoped application data. It is mandatory for generated AI-first SaaS foundations that include any agent-assisted administration, work management, policy governance, or audit/tracing surface.

## Capability definition

- capability-id: `managed-agent-foundation`
- class:
  - command
  - read/evidence
  - policy/governance
  - approval
  - trace/audit
- purpose:
  - manage `AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `ReferenceDocument`/`ReferenceVersion`, `AgentSkillManifest`, `AgentReferenceManifest`, `ToolPermissionBoundary`, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, and `AgentWorkTrace` as governed runtime assets rather than static implementation-only text
- business outcome:
  - every agent invocation uses an approved behavior profile, active prompt version, compact approved skill/reference manifests, authorized `readSkill(skillId)` and `readReferenceDoc(referenceId)` loads, explicit tool/data boundaries, and durable traces that explain what behavior guidance/evidence was assembled, what skills/references were loaded or denied, and what work occurred

## In-scope outcomes

- Tenant/customer-scoped `AgentDefinition` lifecycle: draft, active, disabled, archived, owner/steward, model reference, prompt reference, skill manifest reference, tool permission boundary reference, and authority level.
- Governed prompt lifecycle: `PromptDocument`, `PromptVersion`, draft/proposed diff, review, approval, activation, rollback, checksum, status, and assembly preview.
- Governed skill lifecycle: `SkillDocument`, `SkillVersion`, compact manifest hint, full skill text, review, approval, activation/deprecation, and tenant/customer visibility.
- Governed reference lifecycle: `ReferenceDocument`, `ReferenceVersion`, compact manifest hint, full reference text, review, approval, activation/deprecation, and tenant/customer visibility.
- First-install/tenant-bootstrap default document loading: implementation-developed default `AgentDefinition`, prompt, skill, reference, manifest, and tool-boundary starter resources are validated and imported into governed storage as the initial approved/active versions with checksums, provenance, idempotency, and audit.
- `AgentSkillManifest` and `AgentReferenceManifest` assignments that expose only approved compact skill/reference metadata to an agent until `readSkill(skillId)` or `readReferenceDoc(referenceId)` is authorized.
- `ToolPermissionBoundary` lifecycle for scoped tool/data permissions, policy citations, denial reasons, and approval-required authority expansion.
- Bounded marketplace prompt import governance: backend-owned catalog entries expose browser-safe metadata, provenance, checksums, proposal-only imports, checksum mismatch denial, and authority-expansion denial.
- Tenant-managed stable-tool-id binding requests: tenants may request only backend-owned registry tool ids, requests create approval-required `ToolPermissionBoundary` proposals, unapproved/arbitrary ids are denied, and high-impact/external-side-effect tools remain separately blocked until provider-specific approval scope exists.
- Runtime assembly contract: resolve active `AgentDefinition`, assemble active governed prompt, include compact skill and reference manifests, authorize every `readSkill(skillId)` and `readReferenceDoc(referenceId)`, and create `PromptAssemblyTrace`, `SkillLoadTrace`, and `ReferenceLoadTrace` records.
- `AgentBehaviorEditorAgent` or equivalent editing-agent responsibilities: interpret behavior-change requests, identify affected documents, draft proposed diffs, explain rationale, flag risk, create draft versions, and route for review/approval.
- `AgentWorkTrace` records for consequential recommendations, decisions, tool/data access, approvals, denials, and trace links.

## Out-of-scope outcomes

- Prompt or skill content granting data, tool, tenant, role, or approval authority by itself.
- Hidden static prompts or bundled skills that bypass governed document/version records for generated AI-first foundations.
- Runtime agents reading packaged seed files directly after bootstrap instead of resolving active governed records.
- Unreviewed direct text activation unless a tenant explicitly enables a separate restricted admin path with audit and approval controls.
- Cross-tenant skill, prompt, manifest, tool-boundary, or trace visibility without SaaS Owner/support-access authority and audit.

## Actors and callers

- SaaS Owner Admin, Tenant Admin, delegated Agent Steward, Policy Owner, Reviewer/Approver, Auditor.
- `AgentBehaviorEditorAgent` and bounded admin/work agents that request behavior edits or skill loads.
- Runtime agent invocation service, workflow steps, supervised agent tools, governance UI, and audit/trace views.

## Authority and contract

- AuthContext / scope:
  - every document, version, manifest, tool boundary, and trace is tenant/customer scoped unless explicitly platform-owned
  - agent invocations require an active selected tenant/customer context, active caller authority, active `AgentDefinition`, and non-disabled prompt/skill/tool-boundary references
- permissions / named capability grants:
  - `tenant.agent.read`, `tenant.agent.manage`, `tenant.agent.disable`, `tenant.prompt.read`, `tenant.prompt.propose`, `tenant.prompt.approve`, `tenant.skill.read`, `tenant.skill.propose`, `tenant.skill.approve`, `tenant.manifest.manage`, `tenant.tool_boundary.manage`, `tenant.agent_trace.read`, `agent_admin.marketplace_prompts.read`, `agent_admin.marketplace_prompt.import`, `agent_admin.tool_binding.request`
- inputs / validation / idempotency:
  - commands include tenant/customer id, document or agent ids, expected version, idempotency key, correlation id, requested authority change, rationale, risk classification where applicable, and seed bundle/content version when importing implementation defaults
  - activation rejects stale expected versions, disabled owners, missing approval, checksum mismatch, unassigned skills, and tool-boundary expansion without approval
- outputs / redaction / denial shape:
  - browser and agent responses expose behavior metadata, compact skill/reference hints, effective grants, review status, trace ids, and redacted diffs
  - full skill, reference, and prompt text is returned only to authorized reviewers/stewards or through authorized `readSkill(skillId)` / `readReferenceDoc(referenceId)` runtime reads
  - denials use stable forbidden/not-found shapes and do not reveal cross-tenant artifact existence
- data access:
  - `AgentDefinition`, `PromptDocument`, `PromptVersion`, `SkillDocument`, `SkillVersion`, `ReferenceDocument`, `ReferenceVersion`, `AgentSkillManifest`, `AgentReferenceManifest`, `ToolPermissionBoundary`, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, decision cards, and AdminAuditEvent
- side effects:
  - import missing default seed documents on install/tenant bootstrap, create draft/proposed versions, route approvals, activate/rollback versions, disable/enable agents, assign manifests/tool boundaries, emit audit events, and persist traces
- exposure surfaces:
  - browser UI for agent catalog/detail, prompt governance, skill governance, manifest and tool-boundary management, editing-agent proposals, and traces
  - protected HTTP APIs for governed document/version/manifest/boundary management and trace queries
  - internal runtime `readSkill(skillId)` surface authorized by `AgentSkillManifest` and `ToolPermissionBoundary`
  - internal runtime `readReferenceDoc(referenceId)` surface authorized by `AgentReferenceManifest` and `ToolPermissionBoundary`
  - workflow/decision-card surfaces for approval-required authority expansion
  - internal/backend governance facade for marketplace prompt catalog/import and tenant stable-tool-id binding requests; browser/API surfaces may expose it only through the named protected capabilities above

## Policy, approval, and autonomy

- Default autonomy level:
  - editing agents may draft proposed diffs and rationale but cannot activate prompt, skill, manifest, or tool-boundary changes
  - low-risk wording edits may be proposed automatically; authority expansion, new tools, broader data access, cross-tenant visibility, or agent enable/disable changes require human review/approval
- Approval gates:
  - any change that expands tool/data/authorization authority requires reviewer approval, policy citation, and trace/audit record
  - marketplace prompt imports and tenant stable-tool-id binding requests remain proposal-only until explicit backend review/approval and cannot mutate active prompt or tool-boundary state directly
  - disabled-agent reactivation requires steward/admin approval
  - direct text editing, if enabled, remains an audited restricted admin path and does not bypass review when activation is policy-gated
- Escalation:
  - ambiguous requested authority, manifest/tool mismatch, unassigned skill requests, tenant-boundary conflicts, or high-risk behavior changes create decision cards

## Audit and trace requirements

- Audit events:
  - seed bundle import, seeded document/version create, proposed diff, approval/rejection, activation, rollback, manifest assignment, tool-boundary change, agent disable/reactivate, authorized/denied `readSkill`, authorized/denied `readReferenceDoc`, and runtime assembly
- Work-trace fields:
  - agent id, prompt document/version, skill document/version ids, reference document/version ids, manifest ids, tool-boundary id, caller AuthContext, tenant/customer id, policy citations, approval ids, correlation id, redaction marker, and checksum/version facts
- Trace records:
  - `PromptAssemblyTrace` records selected prompt versions, compact skill/reference manifests included, policy context, and redaction/assembly checksum
  - `SkillLoadTrace` records allowed and denied `readSkill(skillId)` attempts with manifest/boundary reason
  - `ReferenceLoadTrace` records allowed and denied `readReferenceDoc(referenceId)` attempts with manifest/boundary reason
  - `AgentWorkTrace` records consequential recommendations, tool/data access, decisions, approvals, and outcome links

## Required tests

- success:
  - first tenant bootstrap imports implementation-developed default AgentDefinition, prompt v1, skill v1 records, reference v1 records, AgentSkillManifest, AgentReferenceManifest, and ToolPermissionBoundary into governed storage; authorized steward can create draft prompt/skill/reference versions, assign approved skill/reference manifests, set a `ToolPermissionBoundary`, activate approved versions, invoke an active agent, and find `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, and `AgentWorkTrace`
- validation:
  - stale versions, seed checksum mismatch, marketplace prompt checksum mismatch, missing starter resources, missing references, invalid manifest entries, unknown skill/reference ids, unapproved stable tool ids, arbitrary class/tool names, and missing idempotency keys are rejected safely
- forbidden and tenant isolation:
  - cross-tenant artifact reads, unauthorized prompt/skill/reference/tool changes, disabled-agent invocation, unassigned skill/reference reads, and tool-boundary violations are denied without leaking artifact existence
- approval:
  - authority expansion, new tool/data access, cross-scope manifest additions, marketplace prompt imports, tenant stable-tool-id binding requests, and reactivation of disabled agents require decision-card approval before activation
- audit/trace:
  - edit proposals, diff review, approval/rejection, activation/rollback, authorized/denied `readSkill`, authorized/denied `readReferenceDoc`, prompt assembly, and consequential agent work create durable audit/trace records
- regression:
  - prompt, skill, reference, or marketplace prompt wording changes alone never grant new tool/data/authorization authority; tenant-managed tool-binding requests cannot reference arbitrary Java classes or unregistered tool names; manifest and tool-boundary enforcement remains authoritative after retries, replay, projection rebuilds, and seed re-imports; tenant-customized active prompt/skill/reference content is not overwritten by app upgrade seed imports

## Linked layers

- operating model:
  - `../15-operating-model/agent-roles-and-authority.md`
  - `../15-operating-model/policies-and-approval-gates.md`
  - `../15-operating-model/audit-trace-and-outcomes.md`
- behavior:
  - `../20-behavior/flows/04-managed-agent-foundation-flow.md`
  - `../20-behavior/rules/02-agent-authority-rules.md`
- tests:
  - `../30-tests/acceptance/01-core-app-acceptance.md`
  - `../30-tests/negative/01-forbidden-actions.md`
  - `../30-tests/regression/01-tenant-isolation-and-idempotency.md`
  - `../30-tests/operational/01-observability-and-audit.md`
- auth/security:
  - `../40-auth-security/authorization-rules.md`
  - `../40-auth-security/boundary-and-surface-rules.md`
  - `../40-auth-security/data-protection.md`
- observability:
  - `../50-observability/logs-and-audit.md`
  - `../50-observability/traces-and-correlation.md`
- UI:
  - `../55-ui/agent-catalog-and-detail.md`
  - `../55-ui/prompt-and-skill-governance.md`
  - `../55-ui/skill-manifests-and-tool-permissions.md`
  - `../55-ui/edit-agent-proposals-and-traces.md`
