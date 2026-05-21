---
name: akka-agent-seed-documents
description: Seed implementation-developed default AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, and ToolPermissionBoundary records into application-managed storage on first install or tenant bootstrap, with idempotency, provenance, activation, upgrade, and tests.
---

# Akka Agent Seed Documents

Use this skill when an AI-first SaaS app stores agent system prompts, runtime skills, manifests, tool boundaries, or default agent definitions inside the application and needs to load the implementation-developed first versions when the app is installed, upgraded, or a tenant is created.

This skill complements `akka-agent-governed-documents`, `akka-agent-prompt-governance`, `akka-agent-skill-governance`, and `akka-agent-behavior-profiles`. It covers the **initial loading path** for governed agent behavior artifacts; it does not replace runtime editing, review, approval, or `readSkill(skillId)` authorization. Generated apps must seed each managed agent with its own default skill manifest; for example, `UserAdminAgent` and `AgentAdminAgent` start with different skill ids, names, descriptions, and when-to-use hints.

Reference example:
- `../../src/main/java/com/example/domain/agentfoundation/ReferenceAgentBehaviorSeedManifest.java`
- `../../src/main/java/com/example/domain/agentfoundation/ReferenceSeededAgentBehaviorState.java`
- `../../src/main/java/com/example/application/agentfoundation/ReferenceAgentBehaviorSeedLoader.java`
- `../../src/main/resources/agent-behavior-seeds/reference-v1/manifest.properties`
- `../../src/main/resources/agent-behavior-seeds/reference-v1/user-admin-system.md`
- `../../src/test/java/com/example/application/agentfoundation/ReferenceAgentBehaviorSeedLoaderTest.java`

## Use when the request mentions

- default prompts or skills created during implementation
- first install, tenant bootstrap, environment bootstrap, starter content, seed data, or initial app documents
- loading default `PromptDocument`, `SkillDocument`, `AgentSkillManifest`, `ToolPermissionBoundary`, or `AgentDefinition` records
- app-managed prompt/skill documents that should have version `1` before admins edit them
- upgrading packaged default prompts/skills without overwriting tenant-customized active versions

## Core pattern

Treat implementation-developed behavior content as a packaged **seed bundle** that is imported into governed application state.

```text
packaged seed bundle in app artifact
→ AgentBehaviorSeedManifest validates ids, checksums, dependencies, and target scope
→ install/tenant-bootstrap workflow imports missing records idempotently
→ governed document/version entities store v1 snapshots with seed provenance
→ AgentDefinition references active prompt, per-agent active skill manifest, tool boundary, and model config refs
→ audit events record seeded documents, skipped existing records, and failed validations
→ later edits use normal draft/review/approval flow
```

Do not let runtime agents read seed files directly. After bootstrap, runtime must resolve `AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, and `ToolPermissionBoundary` from application-managed state.

## Seed bundle contents

A generated app that includes managed runtime agents should package a seed bundle such as:

```text
src/main/resources/agent-behavior-seeds/
  manifest.yaml
  agents/user-admin-agent.yaml
  prompts/user-admin-system.md
  skills/user-admin/access-review.md
  skills/user-admin/admin-risk-scoring.md
  skills/user-admin/invitation-drafting.md
  skills/user-admin/role-recommendation.md
  skills/user-admin/support-access-review.md
  skills/user-admin/audit-summary.md
  skills/agent-admin/agent-definition-review.md
  skills/agent-admin/prompt-diff-review.md
  skills/agent-admin/skill-manifest-review.md
  skills/agent-admin/tool-boundary-review.md
  skills/agent-admin/behavior-test-analysis.md
  manifests/user-admin-agent-manifest.yaml
  manifests/agent-admin-agent-manifest.yaml
  tool-boundaries/user-admin-agent-tools.yaml
  tool-boundaries/agent-admin-agent-tools.yaml
```

Manifest fields should include:

```text
seedBundleId
appVersion or schemaVersion
contentVersion
createdBy: implementation | migration | operator
agentDefinitions[]
promptDocuments[]
skillDocuments[]
skillManifests[] with per-agent entries containing skill id, display name, purpose/description, when-to-use hint, version ref, and assignment target
toolPermissionBoundaries[]
modelConfigRefs[] or requiredModelPolicies[]
checksums
activationPolicy: activate-if-missing | create-draft-if-existing | require-review
```

Use stable ids/slugs. Do not use filesystem paths as runtime ids.

## Install and tenant bootstrap rules

1. On app install or first tenant bootstrap, run a privileged internal bootstrap workflow/action, not a model-visible tool.
2. Validate the seed manifest before creating any governed records: schema version, ids, checksums, required prompts/skills/manifests/tool boundaries, model config refs, token limits, and secret-like content.
3. Create tenant-scoped copies of default `PromptDocument`, `PromptVersion`, `SkillDocument`, `SkillVersion`, `AgentSkillManifest`, `ToolPermissionBoundary`, and `AgentDefinition` records when they do not already exist.
4. Mark initial versions as approved and active only when the seed bundle was produced by the implementation/deployment pipeline and the product accepts packaged defaults as the initial approved baseline.
5. Store seed provenance on every document/version: seed bundle id, source path or resource id, checksum, app version, import time, importer actor, and import correlation id.
6. Idempotency key must include tenant id, seed bundle id, artifact id, and seed content version. Re-running bootstrap must not duplicate documents or versions.
7. Emit `AdminAuditEvent`/work trace facts for created, activated, skipped, unchanged, and failed seed imports.
8. Do not overwrite tenant-edited active behavior during later app upgrades. If packaged defaults changed and the tenant already customized the artifact, create a draft/proposal or upgrade recommendation for review.
9. Disabled/archived tenant agents stay disabled/archived after seed re-run unless an explicit migration approval reactivates them.
10. Runtime invocation remains unchanged: resolve only application-managed active records, assemble only the active agent's compact assigned-skill list into the system prompt, register `readSkill(skillId)` as an Akka tool, and trace prompt assembly/skill loads.

## Upgrade behavior

When a new app version ships changed default prompts or skills:

- if the tenant artifact still matches the prior seed checksum, the migration may create and activate the new approved seed version according to product policy;
- if the tenant artifact diverged from the prior seed checksum, create a draft/proposed version with a diff against tenant active content and route it to review;
- if a new required skill is added to a default manifest, treat the manifest change as governance-impacting and require approval when it expands behavior, data, or tool authority;
- if a tool boundary expands side effects, data scope, tenant scope, or approval authority, route to decision-card approval before activation;
- record upgrade provenance and preserve rollback to the prior approved version.

## Akka substrate routing

Use:

- `akka-workflows` when install or tenant bootstrap spans multiple records, approval gates, retries, or failure recovery;
- `akka-event-sourced-entities` for `AgentDefinition`, `PromptDocument`, `SkillDocument`, and consequential `AgentSkillManifest` lifecycle commands;
- `akka-key-value-entities` for immutable `PromptVersion`/`SkillVersion` seed snapshots when that is the chosen snapshot carrier;
- `akka-consumers` when seed import emits audit/projection events or reacts to tenant-created events;
- `akka-views` for seed import status, version provenance, and admin review queues;
- `akka-http-endpoints` only for protected admin/retry/status surfaces, not for public bootstrap;
- `akka-agent-testing` plus entity/workflow tests for idempotency, tenant isolation, and upgrade behavior.

## Tests to plan

- fresh tenant bootstrap creates default AgentDefinition, prompt v1, skill v1 records, manifest, tool boundary, and active references;
- seed import is idempotent when run twice with the same tenant, bundle id, artifact ids, and content version;
- missing required seed resource, checksum mismatch, invalid manifest reference, oversized content, or secret-like content fails before partial activation;
- runtime resolves seeded records from application state and never reads packaged seed files directly;
- tenant A cannot see or load tenant B seeded prompt/skill records;
- disabled agent remains disabled after seed re-run;
- upgrade activates changed packaged defaults only when the tenant active version still matches prior seed provenance;
- tenant-customized prompt/skill receives a proposed draft/diff rather than being overwritten;
- manifest/tool-boundary authority expansion requires approval and emits audit/decision traces;
- seed provenance appears in history/diff/audit UI without exposing provider secrets.

## Review checklist

Before finishing, verify:

- default prompt/skill content is packaged as implementation seed material and imported into governed storage;
- seed import creates the first approved/active document versions needed for initial app behavior;
- seed import is tenant-scoped, idempotent, audited, and safe to retry;
- upgrade behavior preserves tenant customizations and uses proposal/review flows for divergent or authority-expanding changes;
- runtime prompt assembly, compact manifests, `readSkill(skillId)`, tool boundaries, and traces all use governed records after bootstrap;
- tests cover first install, duplicate import, validation failure, tenant isolation, customization-preserving upgrades, and authority-expansion approval.
