---
name: akka-agent-governed-documents
description: Model prompts, skills, rubrics, policies, examples, and other behavior-shaping agent artifacts as tenant-scoped governed versioned documents with review, activation, diff/history, immutable snapshots, audit, and protected admin surfaces.
---

# Akka Agent Governed Documents

Use this skill when agent behavior is shaped by runtime-managed documents that need lifecycle, version history, review, approval, activation, deprecation, immutable snapshots, diff UI, or audit.

This skill models governed artifacts. It does not replace focused prompt, skill, policy, evaluation, entity, view, endpoint, web UI, or default-document setup implementation work. Use it when implementation-developed default documents must exist in governed storage on first install, tenant bootstrap, or app upgrade.

## Required reading

Read these first if present:
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/governed-agent-substrate.md`
- `../docs/agent-coverage-matrix.md`
- `../core-saas-foundation/SKILL.md`
- `../ai-first-saas-audit-trace/SKILL.md`
- `../ai-first-saas-policy-governance/SKILL.md`
- `../akka-agents/SKILL.md`
- `../akka-agent-behavior-profiles/SKILL.md`
- `../akka-event-sourced-entities/SKILL.md`
- `../akka-key-value-entities/SKILL.md`
- `../akka-views/SKILL.md`
- `../akka-consumers/SKILL.md`
- `../akka-http-endpoints/SKILL.md`

## Use when the request mentions

- prompt governance, skill governance, reference governance, rubric governance, or behavior documents
- prompt/skill/reference/policy/rubric/example version history
- diff UI, history UI, review UI, or approval UI for agent behavior changes
- activation, deprecation, archival, rollback, or immutable snapshots
- approved prompt version, approved skill version, policy version, rubric version, or reference-example version
- content checksum, provenance, author/reviewer, secret scanning, or change rationale
- default-content provenance, default first versions, first-install document setup, tenant bootstrap, SaaS Foundation App prompts/skills, or implementation-defined behavior defaults
- agent-mediated prompt, skill, manifest, tool-boundary, policy, rubric, or example maintenance
- an `AgentBehaviorEditorAgent`, editing agent, proposed diff, draft version, or review/approval flow for behavior changes

## Core pattern

Use a two-entity pattern when document changes need durable history and immutable executable snapshots:

```text
BehaviorDocumentEntity(docId)
- Event Sourced Entity owns canonical current state
- validates lifecycle and governance commands
- emits lifecycle, edit, review, approval, activation, deprecation, and archive events

BehaviorDocumentVersionEntity(docId:version)
- Key Value Entity stores immutable version snapshot
- populated by Consumer from document events or default-document setup workflow
- supports runtime version pinning, history, rollback, and diff UI
```

Concrete document families can share this pattern:

- `PromptDocument` / `PromptVersion`
- `SkillDocument` / `SkillVersion`
- `ReferenceDocument` / `ReferenceVersion`
- `EvaluationRubric` / `RubricVersion`
- `PolicyDocument` / `PolicyVersion`
- `ReferenceExample` / `ExampleVersion`

## Minimal document state

```text
BehaviorDocument
- tenantId
- documentId
- documentType
- title / summary
- lifecycleStatus: draft | in_review | approved | active | deprecated | archived
- currentDraftVersion
- approvedVersion
- activeVersion
- ownerAccountId / stewardRole
- tags / applicability
- createdBy / updatedBy / timestamps
```

```text
BehaviorDocumentVersion
- tenantId
- documentId
- version
- documentType
- contentRef or contentBody
- contentChecksum
- createdBy
- createdAt
- reviewStatus
- approvedBy / approvedAt
- activationStatus
- deprecationReason
- provenance / changeSummary
- redactionClassification
```

## Akka component mapping

Route to:
- `akka-event-sourced-entities` for canonical document lifecycle, edits, reviews, approvals, activation, deprecation, rollback, and archive commands.
- `akka-key-value-entities` for immutable version snapshots and runtime-pinned active versions.
- `akka-consumers` to materialize version snapshots from document events, emit audit traces, and update derived catalogs.
- `akka-views` for document lists, active-version lookup, history, diff inputs, review queues, and admin search.
- `akka-http-endpoints` and `akka-web-ui-apps` for protected editor, diff, review, activation, and history surfaces.
- `ai-first-saas-audit-trace` for lifecycle, review, approval, activation, rollback, deprecation, and runtime-use traces.
- `core-saas-foundation` for tenant/customer scope, roles, capabilities, and backend authorization.

## Command and event checklist

Typical commands:
- create document draft
- edit draft content or metadata
- submit version for review
- approve or reject version
- activate approved version
- deprecate active version
- rollback to prior approved version
- archive document
- restore archived document when allowed

Typical events:
- `BehaviorDocumentCreated`
- `BehaviorDocumentDraftEdited`
- `BehaviorDocumentVersionSubmitted`
- `BehaviorDocumentVersionApproved`
- `BehaviorDocumentVersionRejected`
- `BehaviorDocumentVersionActivated`
- `BehaviorDocumentVersionDeprecated`
- `BehaviorDocumentRolledBack`
- `BehaviorDocumentArchived`

## Default document setup flow

Apps that manage agent prompts and skills internally still need an initial source for those records. Treat implementation-developed defaults as governed records in the same document/version entities used by runtime editing.

Normal first-install or tenant-bootstrap flow:

```text
implementation-defined agent behavior defaults
→ validate references, checksums, token limits, and secret-like content
→ create missing tenant-scoped AgentDefinition, PromptDocument/PromptVersion, SkillDocument/SkillVersion, ReferenceDocument/ReferenceVersion, AgentSkillManifest, AgentReferenceManifest, and ToolPermissionBoundary records
→ mark default v1 approved/active only under an accepted deployment policy
→ record default-content provenance and AdminAuditEvent/work trace facts
→ runtime resolves only governed records, never filesystem defaults
```

Upgrade flow:

- if tenant active content still matches the prior governed baseline checksum, the app may create the new implementation default as the next approved/active version according to policy;
- if tenant active content diverged, create a draft/proposal with a diff and require review instead of overwriting tenant behavior;
- manifest or tool-boundary changes that expand authority require approval/decision-card routing before activation.

Use focused prompt/skill/reference/tool-boundary governance guidance for default record shape, review, upgrade, and test expectations.

## Agent-mediated maintenance flow

Generated AI-first SaaS foundations should default to **agent-mediated** behavior-document maintenance instead of assuming admins directly edit prompt or skill text as the primary path.

Normal flow:

```text
human change request
→ AgentBehaviorEditorAgent interprets intent and authority context
→ identifies affected PromptDocument, SkillDocument, ReferenceDocument, AgentSkillManifest, AgentReferenceManifest, ToolPermissionBoundary, policy, rubric, or example records
→ drafts a proposed diff plus rationale, risk flags, and test/replay suggestions
→ creates a draft version or manifest/tool-boundary proposal
→ routes the proposal to protected review/approval or decision-card flow
→ activation happens only through the existing governed document/manifest commands
```

`AgentBehaviorEditorAgent` responsibilities:
- translate natural-language maintenance requests into bounded document changes;
- preserve tenant/customer scope, AuthContext, and caller capability limits;
- produce a proposed diff rather than silently mutating active behavior;
- explain rationale, affected agents/workflows, risk, impact, and authority changes;
- flag attempted authority expansion, data/tool permission expansion, secret-like content, or cross-tenant references;
- create only draft versions or review proposals unless a documented policy grants narrower autonomous authority;
- emit audit/work traces for proposal creation, denial, review, approval, activation, and rejection.

Direct text editing can still exist as a protected admin surface when explicitly allowed, but it is secondary to the default editing-agent proposal path and must use the same review/approval, diff, activation, and audit controls.

For implementation details, load `akka-agent-behavior-editing` to define the AgentBehaviorEditorAgent structured proposal, proposed diff schema, risk classification, draft version creation, decision card routing, and authority expansion denial behavior.

## Rules

1. Governed documents are tenant-scoped. Include `tenantId` in commands, state, events, views, endpoints, and runtime lookups.
2. Versions are append-only in practice. Do not mutate a version after approval or activation.
3. Activation requires an approved version unless the product explicitly chooses a simplified single-admin flow.
4. Capture content checksums for every version used by runtime agents.
5. Block or flag secret-like content before approval or activation.
6. Lifecycle changes and runtime use of active versions are audited or work-traced.
7. Diff/history views are tenant-scoped and authorization-protected.
8. Prompt, skill, policy, and rubric content is behavior guidance, not a security boundary.
9. Filesystem defaults are not runtime behavior sources after bootstrap; runtime uses governed records only.
10. Default-document setup must be idempotent when implemented and must not overwrite tenant-customized active content during upgrades.
11. Runtime flows must still enforce data/tool permissions mechanically.
12. Rollback is an explicit audited activation of a prior approved version, not an invisible state edit.
13. Workstream reference documents should use `akka-agent-reference-governance` when they need per-agent reference manifests, authorized `readReferenceDoc(referenceId)` loading, denied-load semantics, ReferenceLoadTrace, or compact expertise manifest entries.

## Runtime lookup contract

When an agent runtime flow needs governed behavior or reference knowledge, resolve an effective version by tenant and authorized context:

```text
ResolveBehaviorDocumentVersion
- tenantId
- documentId
- documentKind: prompt | skill | reference | policy | rubric | example | other
- requestedVersion or active
- agentDefinitionId when applicable
- purpose: test | runtime | replay | evaluation
- caller AuthContext
```

Reject the lookup when:
- the caller lacks the required capability
- the document belongs to another tenant/customer scope
- the document or version is archived/deprecated for the requested purpose
- runtime use requests an unapproved or inactive version without explicit test/replay authority

## Admin UI, implementation order, and review checklist

Provide protected catalog, editor/validation, diff/history, review, active-version/rollback, runtime-trace, impacted-agent, and editing-agent proposal surfaces.

Implement in this order: confirm tenant scope/capabilities, define lifecycle permissions, model document events and immutable version snapshots, add catalog/history/active/review/diff views, add protected endpoints/UI, emit audit/work traces, then integrate active versions with agent profiles, prompt assembly, skill/reference manifests, policies, or evaluation runs.

Before finishing, verify tenant/customer isolation, approval-before-activation, immutable checksummed versions, secret-like content checks, protected diff/history/review surfaces, editing-agent proposal tests, authority-expansion denial/audit, lifecycle audit events, explicit runtime version resolution, and separation between governed text and mechanical tool/data permissions.
