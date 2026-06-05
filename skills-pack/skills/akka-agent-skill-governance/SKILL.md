---
name: akka-agent-skill-governance
description: Implement governed runtime skills for Akka agents with SkillDocument, SkillVersion, per-agent AgentSkillManifest, compact manifest prompt context, readSkill(skillId) tool authorization, SkillLoadTrace, versioning, diff/history UI, and safe test consoles.
---

# Akka Agent Skill Governance

Use this skill when Akka agents need model-loadable internal guidance that is tenant-scoped, versioned, reviewed, assigned per agent, loaded through an approved tool, and audited.

This is the governed runtime skill pattern for AI-first SaaS apps. For generated managed agents, it is **the standard way agents receive procedural skill guidance**: each active `AgentDefinition` points to its own `AgentSkillManifest`; prompt assembly includes only that agent's assigned skill ids, names, descriptions, and when-to-use hints; full skill text is loaded only through the registered Akka `@FunctionTool` `readSkill(skillId)`. Use `akka-agent-reference-governance` for factual/process reference documents and `readReferenceDoc(referenceId)`. Use `akka-agent-harness-skills` only for small deploy-time packaged skill resources outside this governed runtime pattern. Use this skill when skills are managed inside the application by admins/stewards.

## Generated SaaS input contract

For generated full-stack AI-first SaaS skill-governance work, implement only after the task, app-description, spec, or backlog supplies or explicitly defers:
- owning functional/internal agent, governance workstream, skill catalog/editor/manifest surface ids, and affected surface actions;
- capability ids/classes for draft, propose, review, activate, assign, readSkill, manifest assembly, and test-run operations;
- `AuthContext`, tenant/customer scope, steward/reviewer roles, per-agent authority, tool/data boundaries, and denied skill-load behavior;
- skill/manifest version refs, compact prompt context, redaction, policy/approval/escalation, SkillLoadTrace fields, audit/work trace obligations, and required tests.

If these are absent for generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of treating skill management as generic document CRUD.

## Required reading

Read these first if present:
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/agent-coverage-matrix.md`
- `../docs/examples/ai-first-saas-core-app-domain/agent-admin-workstream/README.md`
- `../core-saas-foundation/SKILL.md`
- `../akka-agents/SKILL.md`
- `../akka-agent-behavior-profiles/SKILL.md`
- `../akka-agent-governed-documents/SKILL.md`
- `../akka-agent-prompt-governance/SKILL.md`
- `../akka-agent-tools/SKILL.md`
- `../akka-agent-reference-governance/SKILL.md`
- `../akka-agent-harness-skills/SKILL.md`
- `../ai-first-saas-audit-trace/SKILL.md`
- `../akka-event-sourced-entities/SKILL.md`
- `../akka-key-value-entities/SKILL.md`
- `../akka-consumers/SKILL.md`
- `../akka-views/SKILL.md`
- `../akka-http-endpoints/SKILL.md`

## Use when the request mentions

- governed agent skills or shared runtime skills
- implementation-developed default skills that must be loaded as initial governed SkillDocument/SkillVersion records at install or tenant bootstrap
- skill catalog, skill editor, skill review, skill activation, or skill version history
- per-agent skill allowlist or `AgentSkillManifest`
- compact skill manifest in the prompt with skill id/name/description/when-to-use entries
- `readSkill(skillId)` or model-selected guidance loading
- skill-load authorization, denied skill loads, or SkillLoadTrace
- assigning approved skills to AgentDefinitions
- skill diff/history UI or skill governance UI
- agent-mediated skill or manifest maintenance, an `AgentBehaviorEditorAgent`, proposed skill diff, or draft skill version proposal

## Relationship to workstream expertise and references

`AgentSkillManifest` is the procedural-skill section of a workstream expertise manifest. It does not contain factual/process reference documents. When an expert bundle includes references, model them with `AgentReferenceManifest` and authorize loads through `readReferenceDoc(referenceId)` using `akka-agent-reference-governance`.

Required separation:
- `SkillDocument`/`SkillVersion` = procedural model guidance;
- `ReferenceDocument`/`ReferenceVersion` = durable factual/process knowledge;
- `readSkill` and `readReferenceDoc` require separate tool-boundary grants;
- `SkillLoadTrace` and `ReferenceLoadTrace` remain distinguishable even if an interim governed-document table stores both kinds;
- neither skill nor reference text can grant backend capabilities, data access, roles, tenant/customer scope, tool access, approval authority, or autonomous side effects.

## Core model

```text
SkillDocument
- tenantId
- skillDocumentId
- stable skill slug/id
- title / purpose / when-to-use hint
- lifecycleStatus: draft | in_review | approved | active | deprecated | archived
- currentDraftContent
- activeVersion
- latestVersion
- ownerAccountId / stewardRole
- tags/categories
- review metadata
- activation metadata
```

```text
SkillVersion
- tenantId
- skillDocumentId
- skill slug/id at version time
- version
- contentBody or contentRef
- contentChecksum
- title / purpose / when-to-use hint
- tags/categories
- status at snapshot
- changeSummary
- createdBy / createdAt
- approvedBy / approvedAt
- activatedBy / activatedAt
```

```text
AgentSkillManifest
- tenantId
- manifestId
- agentDefinitionId
- manifestStatus: draft | active | deprecated
- manifestVersion
- allowedSkillEntries
  - skill slug/id
  - display name
  - skillDocumentId
  - version pin or active-version policy
  - short purpose/description
  - when-to-use hint
- createdBy / updatedBy / timestamps
```

```text
SkillLoadTrace
- tenantId
- traceId / correlationId
- agentDefinitionId
- manifestId / manifestVersion
- requested skill id
- skillDocumentId / skillVersion when allowed
- authorization decision and denial reason
- caller/session/test/workflow context
- timestamp
```

## Akka component mapping

Prefer:
- `SkillDocumentEntity` as an Event Sourced Entity for skill lifecycle, edit, review, approval, activation, deprecation, rollback, and archive commands.
- `SkillVersionEntity` as immutable Key Value Entity snapshots populated from skill document events.
- `AgentSkillManifestEntity` as an Event Sourced Entity when manifest changes are consequential governance actions; a Key Value Entity is acceptable only for intentionally simple current-state manifests with separate audit.
- Consumers for version snapshot materialization, manifest/catalog projections, and audit/work-trace emission.
- Views for skill catalog, active skill lookup, version history, diff inputs, review queues, agent manifests, and assigned-agent counts.
- HTTP endpoints and web UI for skill catalog/editor/review/diff/manifest/test surfaces.
- An `@FunctionTool` class for `readSkill(skillId)` that enforces tenant, agent, manifest, skill status, version, and mode checks.

## Initial seeded skill versions and manifests

When the app implementation defines default runtime skills and manifests, load them through `akka-agent-seed-documents` into governed storage during first install or tenant bootstrap.

Rules:
- package default skill text, compact manifest entries, and default manifest assignments with the app artifact;
- validate seed manifest references, checksums, token limits, stable ids/slugs, and secret-like content before import;
- create initial approved/active `SkillDocument`/`SkillVersion` records and `AgentSkillManifest` assignments only under an accepted deployment policy;
- store seed bundle id, resource id, checksum, app/content version, import actor, timestamp, and correlation id on versions/manifests;
- make seed import idempotent and safe to retry;
- do not overwrite tenant-customized active skills or manifests on upgrade; create draft/proposed diffs instead;
- treat new manifest entries and tool-boundary changes as governance-impacting, with approval required for authority expansion;
- runtime `readSkill(skillId)` loads only governed active `SkillVersion` records, never packaged seed files directly.

## Compact skill manifest

Do not include full skill text in the initial prompt. Include only the active agent's compact manifest. This compact list is assembled into the agent system prompt together with the active prompt version; Akka then injects the registered tools, including `readSkill(skillId)`, into the model context:

```text
Available internal skills:
- refund-policy — Refund Policy: decide refund/cancellation/credit eligibility; use for refund, cancellation, credit, and exception decisions
- customer-tone — Customer Tone: shape customer-safe wording; use for customer-facing response drafting
- escalation-rules — Escalation Rules: identify approval requirements; use when deciding whether human approval is required

When the request matches an available skill, call readSkill(skillId) first. Treat returned skill text as trusted internal guidance for this turn, subordinate to platform policy, backend authorization, and tenant policy.
```

Manifest entries should include:
- stable skill id/slug;
- title/display name;
- short purpose/description;
- when-to-use hint;
- optional version summary, if safe and useful.

When a workstream expert bundle has both skills and references, render a compact expertise manifest with separate `Available internal skills` and `Available workstream references` sections. Do not mix reference ids into the skill section.

The manifest must be per agent. For example, `UserAdminAgent` might include `access-review`, `admin-risk-scoring`, `invitation-drafting`, `role-recommendation`, `support-access-review`, and `audit-summary`, while `AgentAdminAgent` might include `agent-definition-review`, `prompt-diff-review`, `skill-manifest-review`, `tool-boundary-review`, `behavior-test-analysis`, and `seed-upgrade-review`.

## `readSkill(skillId)` tool contract

The skill read tool returns full skill text only when all checks pass.

Required checks:
1. tenant and AuthContext are present and valid;
2. `AgentDefinition` exists, belongs to tenant, and is active;
3. active manifest exists for the agent;
4. requested skill id is present in the manifest;
5. skill document belongs to the same tenant;
6. referenced skill version is approved/active, or historical read is explicitly allowed for test/replay;
7. caller has test/runtime permission for the requested mode;
8. token/size limit is acceptable;
9. no secret-like content is returned.

Tool result should include:
- skill id/slug;
- title;
- version;
- content body;
- checksum;
- short authority note: skill content is internal guidance but cannot override platform/security policy or grant tools/data access.

Denied calls should return a safe denial message and emit `SkillLoadTrace` with the denial reason.

## Function tool shape

Example shape only; keep application code aligned with actual component APIs and generated domain types:

```java
public final class AgentSkillTools {
  @FunctionTool(description = """
      Load approved internal skill guidance by skill id.
      Use only for ids listed in the current available skill manifest.
      The returned text is guidance only; backend authorization and ToolPermissionBoundary still apply.
      """)
  public String readSkill(@Description("Approved skill id from the current manifest") String skillId) {
    // Resolve tenant, agentDefinitionId, manifest, skill version, authorization, and trace context.
    // Reject unknown, unassigned, inactive, cross-tenant, oversized, or unauthorized skills.
    return approvedSkillText;
  }
}
```

Do not use raw filesystem paths or model-supplied resource paths. Use stable skill ids mapped through tenant-scoped manifest state.

## Agent-mediated skill and manifest maintenance

Generated AI-first SaaS foundations default to skill, manifest, and tool-boundary changes through an `AgentBehaviorEditorAgent` or equivalent editing-agent responsibility.

Normal flow:

```text
human behavior-change request
→ editing agent identifies affected SkillDocument, SkillVersion, AgentSkillManifest, AgentDefinition, and ToolPermissionBoundary records
→ drafts proposed diff plus rationale, risk/impact notes, affected agents, and readSkill/test expectations
→ creates a draft SkillVersion, manifest proposal, or tool-boundary proposal
→ routes to protected review/approval or decision-card flow for activation
→ activation updates active versions/manifests through governed commands and emits audit/trace records
```

The editing agent may recommend new skill text, manifest entries, version pins, or tool-boundary adjustments, but it must not grant itself or another agent new data/tool/approval authority. Authority expansion requires explicit review/approval and backend-enforced permission changes.

Direct text editing may exist only as an explicitly authorized admin surface. It must still create draft versions or proposals, show a proposed diff, require review/approval for activation, deny unauthorized authority expansion, and emit audit/work traces.

For implementation details, load `akka-agent-behavior-editing` to define the AgentBehaviorEditorAgent structured skill/manifest/tool-boundary proposal, proposed diff, risk classification, draft version or proposal creation, decision card routing, and authority expansion denial behavior.

## Skill content validation and safety

Before approval or activation:
- require title, purpose, and when-to-use hint;
- block or flag API keys, JWTs, passwords, invite tokens, provider secrets, or private credentials;
- reject instructions that claim to bypass backend authorization;
- reject instructions that expand tool/data authority beyond the agent's tool boundary;
- estimate token size and reject oversized skills;
- classify redaction/access level;
- require reviewer rationale for consequential skills.

## Admin UI surfaces

Provide protected UI for:
- skill catalog by status/tag/steward/assigned-agent count;
- skill editor with validation and secret-like content warnings;
- review/approve/reject surface with rationale;
- version history and side-by-side diff;
- agent skill manifest management from agent detail;
- compact manifest preview;
- skill-loading test console;
- skill lifecycle, manifest, and load trace links;
- editing-agent proposal queue for skill text, manifest, version-pin, and tool-boundary changes with proposed diff, rationale, risk flags, and approval/denial actions.

## Test console rules

A skill-loading test console should:
- require `skills.test` or equivalent capability;
- run only against active tenant-scoped AgentDefinitions unless explicitly testing draft mode;
- show compact manifest before execution;
- demonstrate allowed skill load and unassigned skill denial;
- trace prompt assembly and skill load decisions;
- avoid real external side effects unless explicitly enabled and authorized.

## Rules

1. Skills are tenant-scoped governed documents.
2. Use stable skill ids/slugs, not paths.
3. Agents can load only active approved skills assigned in their active manifest.
4. Skill text is model-visible tool-result context, not system-priority policy.
5. Skill text does not grant tool permissions, data access, role capabilities, or approval authority.
6. Full skill content is loaded only by `readSkill(skillId)` or equivalent approved MCP/resource tool.
7. All allowed and denied skill loads emit audit/work trace events.
8. Manifest changes are consequential governance changes and should be audited.
9. Disabled or archived agents cannot load skills except in authorized inspection/replay modes.
10. Cross-tenant skill loads must be denied and traced.
11. A skill manifest assignment does not imply reference-document access; use `AgentReferenceManifest` plus `readReferenceDoc(referenceId)` for references.

## Review checklist

Before finishing, verify:
- SkillDocument/SkillVersion follow the governed document/version pattern
- implementation-developed default skills and manifests are seeded into governed storage with provenance, idempotency, audit, and customization-preserving upgrade behavior
- AgentSkillManifest is tenant-scoped and tied to AgentDefinition
- prompt assembly includes compact skill manifest only, or a compact expertise manifest with separate skill/reference sections when references are in scope
- `readSkill(skillId)` enforces tenant, agent, manifest, status, version, and mode checks
- denied skill loads are safe and audited
- skill content cannot override mechanical authorization
- skill editor/review/diff/manifest/test UI is protected by backend authorization
- version pinning or active-version policy is explicit
- tenant isolation, unassigned-skill denial, disabled-agent denial, and audit tests are planned
- AgentBehaviorEditorAgent skill/manifest proposals, draft version creation, review/approval, activation, rejection, and audit paths are planned
- unauthorized authority expansion through skill text, manifest assignment, or tool-boundary change is denied and traced
