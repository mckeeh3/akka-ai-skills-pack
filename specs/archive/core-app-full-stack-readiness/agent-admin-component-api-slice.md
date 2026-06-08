# Agent Admin Component and API Slice

## Purpose

Define the implementation-ready Agent Admin slice for generated secure AI-first SaaS applications. This slice closes the gap between the current reference runtime helpers and a production-shaped governed-agent administration module.

This is a specification slice only. Follow-up code tasks should implement the components, APIs, views, seed import, and tests without re-deciding durable state boundaries.

## Scope

Included:

- Tenant-scoped `AgentDefinition` lifecycle, authority, placement, model reference, prompt reference, skill manifest reference, and tool-boundary reference.
- Governed `PromptDocument` / immutable `PromptVersion` lifecycle with review, activation, rollback, diff/history, prompt assembly, and test-console use.
- Governed `SkillDocument` / immutable `SkillVersion` lifecycle with catalog, review, activation, assignment, compact manifest, and `readSkill(skillId)` authorization.
- `AgentSkillManifest` versioning, compact manifest preview, assignment, activation, and runtime lookup semantics.
- `ToolPermissionBoundary` registry-backed grants for read-only, side-effecting, component, MCP, data lookup, and `read_skill` tools.
- Safe `ModelConfigRef` and `ModelPolicy` records with no provider secrets in governed state, DTOs, prompt context, traces, or browser payloads.
- Seed import for implementation-developed default agent definitions, prompts, skills, manifests, tool boundaries, and model-policy references.
- Behavior-edit proposal records for prompt, skill, manifest, tool-boundary, model-policy, and agent-definition changes.
- Agent Admin protected HTTP API contracts, admin views, workstream surface payloads, and deterministic test-console contracts.
- Unit, integration, view, endpoint, seed, runtime-lookup, authorization, tenant-isolation, audit/trace, and frontend-secret-boundary tests.

Excluded and deferred:

- The final static Java `Agent` invocation wrapper and runtime resolver hardening; specified by `hybrid-akka-agent-runtime-contract.md` in the next task.
- Durable Audit/Trace storage/search/redaction implementation beyond events and links emitted by this slice; specified by the Audit/Trace core module.
- Governance/Policy module decision-card implementation beyond proposal/approval hooks required by Agent Admin.
- Concrete React components; this slice defines API and surface contracts for later workstream UI alignment.

## Capability contracts

### `agent.definitions.manage`

- type: command/governance capability
- actors/callers: Tenant Agent Admin, SaaS Owner bootstrap or support actor with scoped support grant, seed bootstrap service, behavior-edit proposal activation handler.
- AuthContext: active account, selected Tenant context, `agent.definitions.manage`, target tenant id, correlation id, and idempotency key for consequential commands.
- side effects: mutate `AgentDefinitionEntity`; emit AdminAuditEvent/work-trace facts; update agent catalog/runtime-lookup views; optionally create activation-readiness findings.
- idempotency: create/update/activate/disable/archive commands carry stable command ids; repeated commands return current state or no-op conflict without duplicate state changes.
- denials: cross-tenant target, missing capability, disabled actor, archived agent mutation, authority expansion without approval, missing active prompt/manifest/tool/model refs on activation.

### `agent.prompts.govern`

- type: governed document command/test capability
- actors/callers: Agent Admin, prompt reviewer/approver, behavior editor agent as drafter, seed bootstrap service, test-console endpoint.
- AuthContext: selected tenant with prompt governance capability; `prompts.test` for test runs; runtime assembly uses active approved prompt only.
- side effects: create/edit/submit/review/activate/deprecate/rollback prompt documents and immutable prompt versions; emit prompt audit and PromptAssemblyTrace facts for allowed and denied assembly/test actions.
- denials: unapproved runtime prompt, secret-like content, prompt text attempting authority expansion, cross-tenant prompt ref, disabled/archived owning agent.

### `agent.skills.govern`

- type: governed document command/read-tool capability
- actors/callers: Agent Admin, skill reviewer/approver, behavior editor agent as drafter, seed bootstrap service, runtime agents through `readSkill(skillId)`, test console.
- AuthContext: selected tenant with skill governance capability for admin commands; runtime `readSkill` receives invocation AuthContext, active agent id, manifest id/version, mode, and correlation id.
- side effects: create/edit/review/activate/deprecate/rollback skills; create immutable versions; mutate manifests; emit SkillLoadTrace for allowed and denied reads.
- denials: unassigned skill, inactive/unapproved skill in runtime mode, oversized content, secret-like content, cross-tenant skill, disabled/archived agent, mode not allowed.

### `agent.tool_boundaries.manage`

- type: governance/policy capability
- actors/callers: Agent Admin, reviewers/approvers, behavior editor agent as proposer, seed bootstrap service, runtime tool enforcer for read-only lookup.
- AuthContext: selected tenant with tool-boundary capability; approval capability for side-effecting, security, billing, role/membership, external-message, or cross-customer expansion.
- side effects: create/review/activate/deprecate tool boundaries and registry grants; emit audit/work traces; return `approval_required` instead of executing high-impact tool use when policy requires approval.
- denials: free-form tool ids, unknown registry binding, cross-scope grant, side-effecting grant without approval/idempotency/trace policy, prompt/skill attempt to self-expand tools.

### `agent.models.read` and `agent.models.manage`

- type: model governance read/command capability
- actors/callers: Agent Admin for tenant-safe model selection; SaaS Owner for platform provider aliases and deployment-owned secret setup; runtime resolver for model-policy checks.
- AuthContext: selected tenant for tenant-managed model refs; SaaS Owner scope for platform model-policy administration.
- side effects: create/update/activate/disable/archive safe `ModelConfigRef` and `ModelPolicy`; emit model-config audit and runtime model-use traces.
- denials: provider secret in payload, disabled model, denied provider alias, forbidden mode, unauthorized agent/capability/authority level, implicit fallback.

### `agent.runtime.test`

- type: deterministic test/evaluation capability
- actors/callers: Agent Admin, prompt/skill reviewers, behavior proposal reviewers.
- AuthContext: selected tenant, `agent.runtime.test`, target agent id, mode `test`, correlation/workTrace id.
- side effects: resolve draft or active records as permitted, assemble prompt, optionally load assigned skills, invoke test model or local deterministic model, emit PromptAssemblyTrace, SkillLoadTrace, AgentWorkTrace, and safe result summary.
- denials: production side effects, disabled/archived agent unless inspection mode permits, unauthorized draft use, tool-boundary violation, provider secret exposure.

## Component contracts

### `AgentDefinitionEntity`

Recommended substrate: Event Sourced Entity.

State fields:

| Field | Notes |
|---|---|
| `tenantId` | Required in state, commands, events, and views. |
| `agentDefinitionId` | Stable id. |
| `displayName`, `description` | Browser-safe metadata. |
| `placement` | `FUNCTIONAL_CONTEXT_AREA` or `INTERNAL_WORKER`. |
| `functionalAreaId` | Required for workstream rail agents; absent for internal workers. |
| `status` | `DRAFT`, `ACTIVE`, `DISABLED`, `ARCHIVED`. |
| `authorityLevel` | `ADVISORY`, `DRAFT_ONLY`, `APPROVAL_REQUIRED`, `BOUNDED_AUTONOMOUS`. |
| `ownerAccountId`, `stewardRole` | Administration responsibility. |
| `promptDocumentId`, `activePromptVersion` | Required for activation. |
| `skillManifestId`, `activeSkillManifestVersion` | Required for runtime prompt compact manifest when skills are available. |
| `toolBoundaryId`, `activeToolBoundaryVersion` | Required and deny-by-default for activation. |
| `modelConfigRefId`, `modelPolicyRefId` | Safe aliases/policies only; no secrets. |
| `policyRefs`, `approvalBoundaryRefs` | Governance hooks. |
| `runtimeClassRef` | Static Java Agent binding or adapter id; no tenant secret. |
| `traceRequirements` | Prompt/skill/tool/work trace obligations. |
| `createdBy`, `updatedBy`, `activatedBy`, `disabledBy`, `archivedBy` | Audit metadata. |
| `createdAt`, `updatedAt`, `activatedAt`, `disabledAt`, `archivedAt` | Timestamps. |
| `version`, `checksum` | Optimistic concurrency and runtime snapshot checks. |

Commands:

- `createDraftAgentDefinition(tenantId, agentDefinitionId, metadata, placement, owner, idempotencyKey)`.
- `updateAgentMetadata(agentDefinitionId, displayName, description, owner, stewardRole, expectedVersion)`.
- `setPlacement(agentDefinitionId, placement, functionalAreaId, expectedVersion)`.
- `setAuthority(agentDefinitionId, authorityLevel, approvalBoundaryRefs, approvedDecisionId, expectedVersion)`.
- `setPromptReference(agentDefinitionId, promptDocumentId, promptVersionPolicy, expectedVersion)`.
- `setSkillManifestReference(agentDefinitionId, manifestId, manifestVersionPolicy, expectedVersion)`.
- `setToolBoundaryReference(agentDefinitionId, boundaryId, boundaryVersionPolicy, expectedVersion)`.
- `setModelConfigReference(agentDefinitionId, modelConfigRefId, modelPolicyRefId, expectedVersion)`.
- `setRuntimeClassReference(agentDefinitionId, runtimeClassRef, expectedVersion)`.
- `activateAgentDefinition(agentDefinitionId, activationReason, readinessSnapshot, expectedVersion)`.
- `disableAgentDefinition(agentDefinitionId, reason, expectedVersion)`.
- `archiveAgentDefinition(agentDefinitionId, reason, expectedVersion)`.

Events:

- `AgentDefinitionCreated`, `AgentDefinitionMetadataUpdated`, `AgentDefinitionPlacementChanged`, `AgentDefinitionAuthorityChanged`, `AgentDefinitionPromptReferenceChanged`, `AgentDefinitionSkillManifestChanged`, `AgentDefinitionToolBoundaryChanged`, `AgentDefinitionModelConfigChanged`, `AgentDefinitionRuntimeClassChanged`, `AgentDefinitionActivated`, `AgentDefinitionDisabled`, `AgentDefinitionArchived`.

Validation rules:

- Tenant id in command must match state tenant id.
- `ACTIVE` requires approved active prompt, active tool boundary, active model config, valid runtime class binding, and non-archived referenced artifacts.
- Authority or tool/model expansion requires approval reference unless the product explicitly labels a narrower single-admin simplification.
- Disabled or archived agents cannot be invoked by runtime flows; archived agents cannot be mutated except authorized restore if a future slice defines it.

### `PromptDocumentEntity` and `PromptVersionEntity`

Recommended substrates: Event Sourced Entity for `PromptDocumentEntity`; immutable Key Value Entity or append-only snapshot for `PromptVersionEntity`.

`PromptDocument` state fields:

- `tenantId`, `promptDocumentId`, `agentDefinitionId`, `title`, `promptType`, `status` (`DRAFT`, `IN_REVIEW`, `APPROVED`, `ACTIVE`, `DEPRECATED`, `ARCHIVED`), `currentDraftContentRefOrBody`, `latestVersion`, `approvedVersion`, `activeVersion`, `ownerAccountId`, `stewardRole`, `validationFindings`, `createdBy`, `updatedBy`, `reviewedBy`, `activatedBy`, timestamps.

`PromptVersion` snapshot fields:

- `tenantId`, `promptDocumentId`, `version`, `agentDefinitionId`, `contentBodyOrRef`, `contentChecksum`, `contentSize`, `tokenEstimate`, `statusAtSnapshot`, `changeSummary`, `provenance`, `reviewDecisionId`, `seedBundleId` when seeded, `createdBy`, `approvedBy`, `activatedBy`, timestamps, `redactionClassification`.

Commands:

- create prompt document, edit draft, validate draft, submit version, approve version, reject version, activate approved version, deprecate version, rollback to approved version, archive document, assemble prompt for authorized test/runtime/replay/evaluation.

Events:

- `PromptDocumentCreated`, `PromptDraftEdited`, `PromptVersionSubmitted`, `PromptVersionApproved`, `PromptVersionRejected`, `PromptVersionActivated`, `PromptVersionDeprecated`, `PromptRolledBack`, `PromptDocumentArchived`, `PromptAssembled`, `PromptTestRunStarted`.

Validation rules:

- Runtime mode may use only approved active versions.
- Draft or rejected content is only available to authorized test/replay modes.
- Secret-like content, unsupported variables, prompt attempts to bypass backend authorization, and tool/data authority expansion must block activation or route to review.
- Prompt assembly includes compact skill manifest references, not full skill text.

### `SkillDocumentEntity` and `SkillVersionEntity`

Recommended substrates: Event Sourced Entity for `SkillDocumentEntity`; immutable Key Value Entity or append-only snapshot for `SkillVersionEntity`.

`SkillDocument` state fields:

- `tenantId`, `skillDocumentId`, `stableSkillId`, `title`, `purpose`, `whenToUse`, `tags`, `status`, `currentDraftContentRefOrBody`, `latestVersion`, `approvedVersion`, `activeVersion`, `ownerAccountId`, `stewardRole`, `validationFindings`, timestamps, audit actor ids.

`SkillVersion` snapshot fields:

- `tenantId`, `skillDocumentId`, `stableSkillId`, `version`, `title`, `purpose`, `whenToUse`, `contentBodyOrRef`, `contentChecksum`, `tokenEstimate`, `statusAtSnapshot`, `changeSummary`, `provenance`, `seedBundleId` when seeded, review/activation metadata, `redactionClassification`.

Commands:

- create skill document, edit draft, submit version, approve/reject version, activate approved version, deprecate, rollback, archive, validate content, test-load assigned skill.

Events:

- `SkillDocumentCreated`, `SkillDraftEdited`, `SkillVersionSubmitted`, `SkillVersionApproved`, `SkillVersionRejected`, `SkillVersionActivated`, `SkillVersionDeprecated`, `SkillRolledBack`, `SkillDocumentArchived`, `SkillLoadTestStarted`.

Validation rules:

- Skill ids are stable slugs, not file paths.
- Runtime `readSkill` may load only assigned active approved versions.
- Skill text cannot grant tools, data, tenant scope, role capabilities, or approval authority.
- Secret-like content and oversized content block activation.

### `AgentSkillManifestEntity`

Recommended substrate: Event Sourced Entity when manifest changes are consequential governance actions.

State fields:

| Field | Notes |
|---|---|
| `tenantId`, `manifestId`, `agentDefinitionId` | Required scope and owner. |
| `status` | `DRAFT`, `IN_REVIEW`, `ACTIVE`, `DEPRECATED`, `ARCHIVED`. |
| `manifestVersion` | Incremented on activation. |
| `entries[]` | Stable skill id, skill document id, version pin or active-version policy, short title, purpose, when-to-use hint. |
| `compactManifestChecksum` | Runtime prompt trace input. |
| `changeSummary`, `riskFlags`, `approvalDecisionId` | Governance evidence. |
| `seedBundleId`, `provenance` | When seeded. |
| `createdBy`, `updatedBy`, `reviewedBy`, `activatedBy`, timestamps | Audit metadata. |

Commands/events:

- `createDraftManifest` / `AgentSkillManifestCreated`.
- `addOrUpdateManifestEntry` / `AgentSkillManifestEntryChanged`.
- `removeManifestEntry` / `AgentSkillManifestEntryRemoved`.
- `submitManifestForReview` / `AgentSkillManifestSubmitted`.
- `approveManifest` / `AgentSkillManifestApproved`.
- `activateManifest` / `AgentSkillManifestActivated`.
- `deprecateManifest` / `AgentSkillManifestDeprecated`.
- `archiveManifest` / `AgentSkillManifestArchived`.

Validation rules:

- Every entry must reference same-tenant skill document and approved active or explicitly pinned approved version.
- Manifest activation requires owning `AgentDefinition` to be draft/active and not archived.
- Adding a skill that expands behavior, data access hints, tool dependence, or authority routes to approval.

### `ToolRegistry` and `ToolPermissionBoundaryEntity`

Recommended substrates: Key Value Entity or static registry plus View for `ToolRegistryEntry` when implementation-owned; Event Sourced Entity for tenant-managed `ToolPermissionBoundaryEntity`.

`ToolRegistryEntry` fields:

- `toolId`, `displayName`, `category` (`LOCAL_FUNCTION`, `COMPONENT`, `MCP`, `READ_SKILL`, `DATA_LOOKUP`, `EXTERNAL_SIDE_EFFECT`), `capabilityId`, `bindingRef`, `inputSchemaSummary`, `outputSchemaSummary`, `redactionRules`, `sideEffectLevel`, `tenantScopeBehavior`, `requiredCallerCapability`, `requiredAgentAuthorityLevel`, `approvalRequiredDefault`, `idempotencyRequired`, `traceLevel`, `owner`, `status`.

`ToolPermissionBoundary` fields:

- `tenantId`, `boundaryId`, `boundaryVersion`, `agentDefinitionId` or reusable boundary ref, `status`, `allowedToolGrants[]`, `allowedDataGrants[]`, `sideEffectPolicy`, `approvalRules`, `policyRefs`, `checksum`, `changeSummary`, `riskFlags`, `approvalDecisionId`, actor ids and timestamps.

`ToolGrant` fields:

- `toolId`, `category`, `capabilityId`, `allowedOperations` (`READ`, `PROPOSE`, `REQUEST_APPROVAL`, `EXECUTE`), `allowedModes`, `tenantScope`, `customerScope`, `dataClassificationLimit`, `requiredCallerCapability`, `requiredAgentAuthorityLevel`, `sideEffectLevel`, `autonomy`, `idempotencyRequired`, `traceLevel`.

Commands/events:

- create draft boundary, update grant, remove grant, submit/review/activate/deprecate/archive boundary, simulate boundary against proposed tool call.
- `ToolBoundaryCreated`, `ToolBoundaryGrantChanged`, `ToolBoundarySubmitted`, `ToolBoundaryApproved`, `ToolBoundaryActivated`, `ToolBoundaryDeprecated`, `ToolBoundaryArchived`, `ToolBoundarySimulationRecorded`.

Validation rules:

- No free-form model-supplied tool ids, URLs, component ids, resource paths, or MCP names.
- Side-effecting grants require idempotency and trace policy.
- Security, billing, membership/role, external email/message, cross-customer, export, delete, and irreversible operations default to approval-required.
- `readSkill(skillId)` is a `READ_SKILL` grant and cannot grant other permissions.

### `ModelConfigRefEntity` and `ModelPolicyEntity`

Recommended substrate: Key Value Entity for current state when platform configuration owns history elsewhere; Event Sourced Entity when tenants manage model policy changes.

`ModelConfigRef` fields:

- `tenantId` or `platformScope`, `modelConfigRefId`, `displayName`, `providerAlias`, `allowedAgentDefinitionIds`, `allowedCapabilityIds`, `allowedModes`, `allowedAuthorityLevels`, `fallbackPolicyRef`, `status` (`DRAFT`, `ACTIVE`, `DISABLED`, `ARCHIVED`), actor ids and timestamps.

`ModelPolicy` fields:

- `policyId`, `tenantId` or `platformScope`, `allowedProviderAliases`, `deniedProviderAliases`, optional cost/latency/data-residency/retention/safety constraints, `fallbackOrder` or `noFallback`, `requiresApprovalForChanges`, `traceLevel`, status, actor ids and timestamps.

Validation rules:

- Provider secrets, API keys, deployment credentials, and secret-bearing URLs are never stored or returned.
- Runtime resolver denies disabled, archived, cross-tenant, forbidden-mode, forbidden-agent, forbidden-capability, forbidden-authority, and denied-provider configs before model invocation.
- Fallback must be explicit and traced; no implicit provider fallback.

### `AgentBehaviorSeedImportWorkflow`

Recommended substrate: Workflow or privileged internal bootstrap action. Use Workflow when import spans many records, retries, and failure recovery.

State fields:

- `tenantId`, `seedBundleId`, `contentVersion`, `idempotencyKey`, `importActor`, `correlationId`, `status`, `validatedManifestChecksum`, `createdRecords[]`, `skippedRecords[]`, `proposedUpgrades[]`, `failedValidationFindings[]`, timestamps.

Steps:

1. Validate seed manifest schema, ids, checksums, references, token limits, secret-like content, activation policy, and model-policy refs.
2. Create missing AgentDefinition draft records.
3. Create missing PromptDocument/PromptVersion approved active seed records when activation policy permits.
4. Create missing SkillDocument/SkillVersion approved active seed records.
5. Create or update-by-proposal AgentSkillManifest and ToolPermissionBoundary records.
6. Create safe ModelConfigRef/ModelPolicy references or validate that deployment-provided refs exist.
7. Activate AgentDefinition only after references pass readiness.
8. Emit AdminAuditEvent/work-trace facts for created, skipped, proposed, failed, and activated records.

Upgrade rules:

- If tenant active content still matches prior seed checksum, import changed packaged default as the next approved/active version according to policy.
- If tenant active content diverged, create a behavior-edit proposal or draft version with a diff; do not overwrite.
- Manifest/tool-boundary authority expansion requires approval before activation.
- Disabled or archived tenant agents remain disabled or archived after seed re-run unless an explicit approved migration says otherwise.

### `BehaviorEditProposalEntity`

Recommended substrate: Event Sourced Entity; follow-up Governance/Policy module may generalize it into platform-wide decision cards.

State fields:

- `tenantId`, `proposalId`, `proposalType` (`PROMPT`, `SKILL`, `MANIFEST`, `TOOL_BOUNDARY`, `MODEL_POLICY`, `AGENT_DEFINITION`), `targetArtifactRefs[]`, `createdByActor`, `createdByAgentDefinitionId` when drafted by behavior editor, `status` (`DRAFT`, `IN_REVIEW`, `APPROVED`, `REJECTED`, `CHANGES_REQUESTED`, `APPLIED`, `SUPERSEDED`), `proposedDiff`, `rationale`, `riskFlags`, `authorityExpansion`, `testPlan`, `traceRefs`, `decisionCardRef`, timestamps.

Commands/events:

- create proposal, attach proposed diff, request changes, approve, reject, escalate, apply approved proposal to target component command, supersede.

Validation rules:

- Behavior editor can draft proposals and draft versions, not silently mutate active runtime artifacts.
- Authority expansion, tool/data scope expansion, model-provider expansion, or approval-boundary expansion requires human approval.
- Applying a proposal calls the same governed component commands as direct admin actions.

## View contracts

All views are tenant-scoped and authorization-filtered at endpoint/query boundary. Sensitive prompt/skill content is returned only by detail endpoints with explicit capability; list views return summaries.

| View | Source | Purpose | Key filters |
|---|---|---|---|
| `AgentDefinitionCatalogView` | AgentDefinition events | list/filter agent definitions | tenant, status, placement, steward, authority, model ref |
| `AgentDefinitionDetailView` | AgentDefinition plus active artifact projections | detail/readiness card | tenant, agentDefinitionId |
| `AgentRuntimeLookupView` | active AgentDefinition, prompt, manifest, tool boundary, model refs | resolver input | tenant, agentDefinitionId, status active |
| `PromptDocumentCatalogView` | PromptDocument events | prompt list by agent/status | tenant, agentDefinitionId, status, steward |
| `PromptVersionHistoryView` | PromptVersion snapshots | history/diff and rollback candidates | tenant, promptDocumentId, version |
| `PromptReviewQueueView` | PromptDocument events | submitted prompts awaiting review | tenant, status in_review, steward |
| `SkillCatalogView` | SkillDocument events | skill catalog and assigned counts | tenant, status, tag, stableSkillId |
| `SkillVersionHistoryView` | SkillVersion snapshots | skill history/diff | tenant, skillDocumentId, version |
| `SkillReviewQueueView` | SkillDocument events | submitted skills awaiting review | tenant, status in_review, steward |
| `AgentSkillManifestView` | Manifest events | manifest detail and compact preview | tenant, agentDefinitionId, manifestId |
| `ToolRegistryView` | registry records | browsable tool catalog | tenant/platform, category, sideEffectLevel, status |
| `ToolBoundaryView` | ToolBoundary events | active/draft boundary detail | tenant, agentDefinitionId, boundaryId, status |
| `ModelConfigCatalogView` | ModelConfigRef/Policy events | safe model choices | tenant/platform, status, allowedMode |
| `SeedImportStatusView` | seed workflow events | import history and retry surface | tenant, seedBundleId, status |
| `BehaviorEditProposalView` | proposal events | proposal/review queue | tenant, status, proposalType, target artifact |
| `AgentAdminAuditLinkView` | audit/work trace facts | trace links from Agent Admin | tenant, artifact ref, correlationId, actor |

## Protected HTTP API contracts

All routes are under `/api/agent-admin/**` and require WorkOS/AuthKit-authenticated browser calls or trusted internal service identity. Every command and query resolves selected `AuthContext`, checks backend capabilities, enforces tenant/customer scope, emits allowed/denied audit as required, and returns browser-safe DTOs.

### Agent definitions

- `GET /api/agent-admin/agents?status=&placement=&steward=&q=` → `AgentDefinitionListResponse`.
- `POST /api/agent-admin/agents` with `CreateAgentDefinitionRequest` → `AgentDefinitionDetailDto`.
- `GET /api/agent-admin/agents/{agentDefinitionId}` → `AgentDefinitionDetailDto` including active refs, readiness, trace links, and redacted runtime binding.
- `PATCH /api/agent-admin/agents/{agentDefinitionId}` with metadata/placement/steward changes → detail.
- `PUT /api/agent-admin/agents/{agentDefinitionId}/authority` → approval-required result or detail.
- `PUT /api/agent-admin/agents/{agentDefinitionId}/refs` to set prompt/manifest/tool/model refs → readiness detail.
- `POST /api/agent-admin/agents/{agentDefinitionId}/activate` → detail or activation readiness errors.
- `POST /api/agent-admin/agents/{agentDefinitionId}/disable` → detail.
- `POST /api/agent-admin/agents/{agentDefinitionId}/archive` → detail.

### Prompts

- `GET /api/agent-admin/prompts?agentDefinitionId=&status=` → prompt catalog.
- `POST /api/agent-admin/prompts` → create prompt document.
- `GET /api/agent-admin/prompts/{promptDocumentId}` → prompt detail with content only when authorized.
- `PATCH /api/agent-admin/prompts/{promptDocumentId}/draft` → edit draft.
- `POST /api/agent-admin/prompts/{promptDocumentId}/submit` → submitted version summary.
- `POST /api/agent-admin/prompts/{promptDocumentId}/review` → approve/reject with rationale.
- `POST /api/agent-admin/prompts/{promptDocumentId}/activate` → active version summary.
- `POST /api/agent-admin/prompts/{promptDocumentId}/rollback` → active version summary.
- `GET /api/agent-admin/prompts/{promptDocumentId}/versions` → version history.
- `GET /api/agent-admin/prompts/{promptDocumentId}/diff?from=&to=` → redacted diff DTO.
- `POST /api/agent-admin/prompts/{promptDocumentId}/test` → prompt test-console run.

### Skills and manifests

- `GET /api/agent-admin/skills?status=&tag=&q=` → skill catalog.
- `POST /api/agent-admin/skills` → create skill document.
- `GET /api/agent-admin/skills/{skillDocumentId}` → skill detail with content only when authorized.
- `PATCH /api/agent-admin/skills/{skillDocumentId}/draft` → edit draft.
- `POST /api/agent-admin/skills/{skillDocumentId}/submit` → submitted version summary.
- `POST /api/agent-admin/skills/{skillDocumentId}/review` → approve/reject.
- `POST /api/agent-admin/skills/{skillDocumentId}/activate` → active version summary.
- `GET /api/agent-admin/skills/{skillDocumentId}/versions` and `/diff` → history/diff.
- `GET /api/agent-admin/agents/{agentDefinitionId}/manifest` → active/draft manifest with compact preview.
- `PATCH /api/agent-admin/agents/{agentDefinitionId}/manifest/draft` → edit entries.
- `POST /api/agent-admin/agents/{agentDefinitionId}/manifest/submit` → submitted manifest.
- `POST /api/agent-admin/agents/{agentDefinitionId}/manifest/review` → approve/reject.
- `POST /api/agent-admin/agents/{agentDefinitionId}/manifest/activate` → active manifest.
- `POST /api/agent-admin/agents/{agentDefinitionId}/read-skill-test` → allowed/denied skill-load test with trace refs.

### Tool boundaries and model refs

- `GET /api/agent-admin/tools/registry?category=&sideEffectLevel=` → safe registry catalog.
- `GET /api/agent-admin/agents/{agentDefinitionId}/tool-boundary` → active/draft boundary.
- `PATCH /api/agent-admin/agents/{agentDefinitionId}/tool-boundary/draft` → edit grants.
- `POST /api/agent-admin/agents/{agentDefinitionId}/tool-boundary/simulate` → allowed/denied/approval-required simulation.
- `POST /api/agent-admin/agents/{agentDefinitionId}/tool-boundary/submit` → submitted boundary.
- `POST /api/agent-admin/agents/{agentDefinitionId}/tool-boundary/review` → approve/reject.
- `POST /api/agent-admin/agents/{agentDefinitionId}/tool-boundary/activate` → active boundary.
- `GET /api/agent-admin/models` → safe `ModelConfigRef` catalog.
- `GET /api/agent-admin/model-policies` → safe model policies.
- `POST/PATCH /api/agent-admin/models` and `/model-policies` only when tenant model management is in scope; otherwise read-only from platform seed/deployment policy.

### Seed import, proposals, test console, traces

- `POST /api/agent-admin/seeds/import` → privileged internal/admin retry only; starts seed import workflow.
- `GET /api/agent-admin/seeds/imports` and `/{seedImportId}` → import status/history.
- `GET /api/agent-admin/proposals?status=&type=&target=` → behavior-edit proposal queue.
- `GET /api/agent-admin/proposals/{proposalId}` → proposed diff and decision metadata.
- `POST /api/agent-admin/proposals/{proposalId}/review` → approve/reject/request changes/escalate.
- `POST /api/agent-admin/proposals/{proposalId}/apply` → applies approved proposal through target component command.
- `POST /api/agent-admin/agents/{agentDefinitionId}/test-console/run` → deterministic test-mode invocation.
- `GET /api/agent-admin/traces?artifactRef=&correlationId=` → trace links only; durable search belongs to Audit/Trace module.

## DTO redaction contract

Browser DTOs may include:

- ids, display names, statuses, statuses of references, checksums, safe provider aliases, content summaries, diffs visible to authorized reviewers, validation findings, trace refs, decision refs, and timestamps.

Browser DTOs must not include:

- provider secrets, API keys, JWTs, invite tokens, raw WorkOS/provider private ids unless specifically authorized, raw secret-like prompt/skill content in list views, raw tool credentials, hidden platform instructions, out-of-scope tenant/customer ids, or model-visible internal policy beyond authorized summaries.

## Workstream surface contract

Later UI work should map the following structured surfaces to the API contracts above:

| Surface | Primary payload | Required actions |
|---|---|---|
| Agent catalog | `AgentDefinitionListResponse` | filter, create draft, open detail, show forbidden/empty/loading states |
| Agent detail/readiness | `AgentDefinitionDetailDto` | edit metadata, set refs, activate, disable, archive, open traces |
| Prompt governance | prompt catalog/detail/version/diff DTOs | edit draft, submit, approve/reject, activate, rollback, test |
| Skill governance | skill catalog/detail/version/diff DTOs | edit draft, submit, approve/reject, activate, open assigned agents |
| Manifest management | manifest detail and compact preview | add/remove skills, submit/review/activate, test readSkill |
| Tool boundary management | registry and boundary DTOs | edit grants, simulate, submit/review/activate |
| Model refs | model catalog/policy DTOs | select safe model ref, inspect allowed modes, no secrets |
| Behavior proposals | proposal detail and proposed diff | approve/reject/request changes/escalate/apply |
| Test console | run request/result with trace refs | run test mode, inspect denials, show no-side-effect banner |
| Trace links | trace link summaries | navigate to Audit/Trace module when present |

## Runtime lookup handoff

This slice must produce state and views consumed by the later runtime resolver contract:

1. Resolve caller selected `AuthContext` and requested `agentDefinitionId`.
2. Query `AgentRuntimeLookupView` or component clients for active `AgentDefinition`.
3. Reject disabled, archived, draft-in-runtime, cross-tenant, or unauthorized agents.
4. Resolve active approved `PromptVersion`.
5. Resolve active `AgentSkillManifest` compact entries.
6. Resolve active `ToolPermissionBoundary` and safe `ModelConfigRef`/`ModelPolicy`.
7. Return enough refs/checksums for deterministic prompt assembly and PromptAssemblyTrace.
8. Enforce `readSkill` and tool calls through the active manifest/boundary, not through prompt/skill text.

The exact Java Agent wrapper, mode matrix, and model invocation steps are deferred to `hybrid-akka-agent-runtime-contract.md`.

## Test plan

### Domain and entity unit tests

- `AgentDefinitionEntity` create/update/activate/disable/archive success paths.
- Activation denial when prompt, manifest, tool boundary, model ref, or runtime class is missing/inactive.
- Authority expansion requires approval decision id.
- Disabled/archived agent cannot be activated or invoked without explicit allowed inspection mode.
- Prompt document lifecycle: edit, submit, approve, activate, rollback, archive.
- Prompt activation denial for secret-like content, unapproved version, unsupported variables, or authority-expanding instruction.
- Skill document lifecycle and activation denial for secret-like/oversized/authority-expanding content.
- Manifest activation denial for cross-tenant, inactive, unapproved, or missing skill refs.
- Tool boundary grant validation for registry-only tool ids, side-effect idempotency, approval-required defaults, and mode constraints.
- Model config denial for secret-bearing payload, disabled status, forbidden provider, unsupported mode, or forbidden agent/capability.

### Seed import tests

- Fresh tenant bootstrap creates default AgentDefinition, PromptDocument/PromptVersion, SkillDocument/SkillVersion, AgentSkillManifest, ToolPermissionBoundary, and ModelConfigRef references.
- Re-running the same seed is idempotent and reports skipped unchanged records.
- Checksum mismatch, missing required seed resource, invalid references, oversized content, or secret-like content fails before partial activation.
- Tenant-customized prompt/skill receives a draft/proposal on upgrade instead of being overwritten.
- Manifest or tool-boundary authority expansion routes to approval and emits audit/decision facts.
- Disabled/archived tenant agent remains disabled/archived after seed re-run.

### View and endpoint integration tests

- Agent catalog/detail views are tenant-scoped and status-filtered.
- Prompt/skill history and diff endpoints return authorized content only.
- Review queue endpoints show only selected-tenant submitted artifacts.
- Tool registry/boundary and model catalog DTOs contain no provider secrets.
- Forbidden mutation returns safe denial and emits audit.
- Cross-tenant ids are denied without leaking existence.
- Endpoint idempotency keys prevent duplicate creates/activations/reviews.
- Behavior proposal apply uses target component commands and cannot bypass validation.

### Runtime-adjacent tests

- Runtime lookup succeeds for active agent with active prompt, manifest, tool boundary, and model ref.
- Runtime lookup denies disabled, archived, draft, cross-tenant, missing-capability, missing-ref, or disabled-model cases before model invocation.
- Prompt assembly trace includes prompt version, manifest version, model ref, tool boundary ref, checksum, mode, caller, and correlation id.
- `readSkill(skillId)` succeeds only for assigned active same-tenant skill in allowed mode and emits `SkillLoadTrace`.
- `readSkill` denies unassigned, inactive, cross-tenant, oversized, unauthorized, or disabled-agent reads with safe result and trace.
- Tool-boundary simulation and enforcement return allowed, denied, or approval-required according to active grants.
- Test console uses test mode, avoids production side effects, and traces prompt/skill/tool decisions.

### Security and frontend-boundary tests

- Tenant isolation for every command, view, endpoint, seed import, proposal, and test-console route.
- Disabled account and role/capability denial for Agent Admin commands and queries.
- Prompt, skill, model, trace, and browser DTOs exclude provider secrets and hidden platform instructions.
- Frontend-visible capability flags derive from `/api/me`/backend authorization and do not authorize actions by themselves.
- AdminAuditEvent/work trace emitted for allowed and denied lifecycle, review, activation, seed, model, tool, and test-console actions.

## Implementation order

1. Implement shared domain records/enums for lifecycle statuses, artifact refs, authority levels, model refs, tool grants, proposal statuses, validation findings, and redaction summaries.
2. Implement `AgentDefinitionEntity` and catalog/runtime lookup views.
3. Implement `ModelConfigRef`/`ModelPolicy` safe registry and model catalog view.
4. Implement `PromptDocumentEntity`, `PromptVersionEntity`, prompt history/review views, and prompt endpoints.
5. Implement `SkillDocumentEntity`, `SkillVersionEntity`, skill catalog/history/review views, and skill endpoints.
6. Implement `AgentSkillManifestEntity`, compact manifest preview, manifest endpoints, and `readSkill` test surface.
7. Implement tool registry, `ToolPermissionBoundaryEntity`, boundary simulation, and boundary endpoints.
8. Implement seed import workflow/bootstrap action with audit/status view.
9. Implement `BehaviorEditProposalEntity` and proposal review/apply endpoints.
10. Implement deterministic test-console endpoint and runtime lookup handoff tests.
11. Wire AdminAuditEvent/work-trace emission and redacted DTO helpers across all endpoints.

## Done handoff for future code generation

A future implementation task can proceed when it preserves these decisions:

- AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, and ToolPermissionBoundary are tenant-scoped governed state with explicit lifecycle transitions.
- PromptVersion and SkillVersion are immutable snapshots.
- ModelConfigRef is secret-free and checked by model policy before invocation.
- Seed import creates governed records and never remains a runtime behavior source.
- Behavior editor proposals draft diffs and route review; they do not directly mutate active behavior.
- Runtime/test-console flows consume active governed records, compact manifests, tool boundaries, and safe model refs through backend authorization and traces.
