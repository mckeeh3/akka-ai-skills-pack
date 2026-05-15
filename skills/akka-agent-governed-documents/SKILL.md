---
name: akka-agent-governed-documents
description: Model prompts, skills, rubrics, policies, examples, and other behavior-shaping agent artifacts as tenant-scoped governed versioned documents with review, activation, diff/history, immutable snapshots, audit, and protected admin surfaces.
---

# Akka Agent Governed Documents

Use this skill when agent behavior is shaped by runtime-managed documents that need lifecycle, version history, review, approval, activation, deprecation, immutable snapshots, diff UI, or audit.

This skill models governed artifacts. It does not replace focused prompt, skill, policy, evaluation, entity, view, endpoint, or web UI implementation skills.

## Required reading

Read these first if present:
- `../../docs/ai-first-saas-application-architecture.md`
- `../../docs/agent-coverage-matrix.md`
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

- prompt governance, skill governance, rubric governance, or behavior documents
- prompt/skill/policy/rubric/example version history
- diff UI, history UI, review UI, or approval UI for agent behavior changes
- activation, deprecation, archival, rollback, or immutable snapshots
- approved prompt version, approved skill version, policy version, rubric version, or reference-example version
- content checksum, provenance, author/reviewer, secret scanning, or change rationale

## Core pattern

Use a two-entity pattern when document changes need durable history and immutable executable snapshots:

```text
BehaviorDocumentEntity(docId)
- Event Sourced Entity owns canonical current state
- validates lifecycle and governance commands
- emits lifecycle, edit, review, approval, activation, deprecation, and archive events

BehaviorDocumentVersionEntity(docId:version)
- Key Value Entity stores immutable version snapshot
- populated by Consumer from document events
- supports runtime version pinning, history, rollback, and diff UI
```

Concrete document families can share this pattern:

- `PromptDocument` / `PromptVersion`
- `SkillDocument` / `SkillVersion`
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

## Rules

1. Governed documents are tenant-scoped. Include `tenantId` in commands, state, events, views, endpoints, and runtime lookups.
2. Versions are append-only in practice. Do not mutate a version after approval or activation.
3. Activation requires an approved version unless the product explicitly chooses a simplified single-admin flow.
4. Capture content checksums for every version used by runtime agents.
5. Block or flag secret-like content before approval or activation.
6. Lifecycle changes and runtime use of active versions are audited or work-traced.
7. Diff/history views are tenant-scoped and authorization-protected.
8. Prompt, skill, policy, and rubric content is behavior guidance, not a security boundary.
9. Runtime flows must still enforce data/tool permissions mechanically.
10. Rollback is an explicit audited activation of a prior approved version, not an invisible state edit.

## Runtime lookup contract

When an agent runtime flow needs governed behavior, resolve an effective version by tenant and authorized context:

```text
ResolveBehaviorDocumentVersion
- tenantId
- documentId
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

## Admin UI surfaces

Provide protected surfaces for:
- document catalog by type/status/steward/tag
- editor with validation and secret-like content warnings
- version history and side-by-side diff
- review queue with approval/rejection rationale
- active version and rollback controls
- runtime usage trace links
- impacted agent definitions and workflows

## Implementation order

1. Confirm tenant scope, document types, and governance capabilities with `core-saas-foundation`.
2. Define lifecycle statuses, command permissions, and approval/activation rules.
3. Model `BehaviorDocumentEntity` events and replay rules.
4. Add immutable `BehaviorDocumentVersionEntity` snapshots populated from document events.
5. Add views for catalogs, history, active lookup, review queues, and diff inputs.
6. Add protected endpoints and web UI surfaces.
7. Emit audit/work traces for lifecycle changes and runtime version usage.
8. Integrate selected active versions with agent behavior profiles, prompt assembly, skill manifests, policy checks, or evaluation runs.

## Review checklist

Before finishing, verify:
- tenant/customer isolation is enforced for all commands, queries, and runtime lookups
- lifecycle transitions prevent unapproved runtime activation
- immutable version snapshots are created and checksumed
- secret-like content is blocked or flagged before activation
- diff/history/review surfaces are authorization-protected
- activation, deprecation, rollback, and archive actions emit audit events
- runtime agents pin or resolve explicit versions rather than reading mutable draft content
- governed document text does not grant tool/data permissions by itself
