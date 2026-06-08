# Classification and Package Map: Java Foundation/Coreapp/Business Partition

## Status and scope

This inventory covers the current root app Java sources under `src/main/java/ai/first/**` and tests under `src/test/java/ai/first/**` before any production package move. It is the migration contract for the follow-on package-refactor tasks.

No production Java files are moved by this task.

## Classification rules

- **foundation**: reusable SaaS platform, security, identity, tenancy, authorization, invitation/onboarding, audit, notification, attention, governed-agent runtime, prompt/skill/reference governance, model-provider boundary, and durable repository infrastructure that future business domains may use.
- **coreapp**: the built-in AI-first SaaS core app workstreams and operational behavior shipped with the starter: My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy, their shell/surface orchestration, dashboards, workstream messages, internal autonomous-worker tasks, and endpoint surfaces.
- **business seam**: no current production business-domain code exists; future code should use `*.business.<business-area>.*` packages.
- **layer docs/static**: `package-info.java` files documenting the standard Akka `api` / `application` / `domain` layers may stay at the layer roots and can be updated later to point at `foundation`, `coreapp`, and `business` children.
- **test-only**: test fixtures and tests should follow the production package they validate, with test doubles remaining test-only.

## Current package inventory

| Source set | Package | Count | Classification | Target handling |
| --- | ---: | ---: | --- | --- |
| main | `ai.first.api` | 1 | layer docs/static | keep `ai.first.api` package-info; update docs text later |
| main | `ai.first.api.admin` | 1 | coreapp | move to `ai.first.api.coreapp.admin` |
| main | `ai.first.api.security` | 1 | foundation | move to `ai.first.api.foundation.security` |
| main | `ai.first.api.workstream` | 2 | coreapp | move to `ai.first.api.coreapp.workstream` |
| main | `ai.first.application` | 1 | layer docs/static | keep `ai.first.application` package-info; update docs text later |
| main | `ai.first.application.agentfoundation` | 78 | mixed foundation/coreapp | split by class map below |
| main | `ai.first.application.security` | 74 | mixed foundation/coreapp | split by class map below |
| main | `ai.first.domain` | 1 | layer docs/static | keep `ai.first.domain` package-info; update docs text later |
| main | `ai.first.domain.agentfoundation` | 20 | mixed foundation/coreapp | split by class map below |
| main | `ai.first.domain.security` | 59 | mixed foundation/coreapp | split by class map below |
| test | `ai.first` | 1 | test layer docs/static | keep/update with root layer docs if needed |
| test | `ai.first.application.agentfoundation` | 27 | test-only | move with validated production package or keep as test fixture under matching target package |
| test | `ai.first.application.security` | 45 | test-only | move with validated production package or keep as test fixture under matching target package |

## Production old-to-new package map

### API layer

| Current class | Classification | Target package | Notes |
| --- | --- | --- | --- |
| `ai.first.api.package-info` | layer docs/static | `ai.first.api` | Update package comment after migrations. |
| `ai.first.api.admin.AdminEndpoint` | coreapp | `ai.first.api.coreapp.admin` | Protected User Admin, support access, SSO/SCIM, digest, and audit API surface for built-in core app workstreams. |
| `ai.first.api.security.MeEndpoint` | foundation | `ai.first.api.foundation.security` | Browser bootstrap endpoint for selected local `AuthContext`; reusable foundation API. |
| `ai.first.api.workstream.StarterFrontendEndpoint` | coreapp | `ai.first.api.coreapp.workstream` | Built-in starter frontend hosting endpoint. |
| `ai.first.api.workstream.WorkstreamEndpoint` | coreapp | `ai.first.api.coreapp.workstream` | Built-in workstream shell, functional-agent, action, message, invitation acceptance, and SSE surface API. |

### Domain layer: governed agent foundation

Move these reusable governed-agent records and contracts from `ai.first.domain.agentfoundation` to `ai.first.domain.foundation.agent`:

- `AgentBehaviorRepositoryState`
- `AgentDefinition`
- `AgentLifecycleStatus`
- `AgentReferenceManifest`
- `AgentRuntimeTrace`
- `AgentSkillManifest`
- `BehaviorChangeProposal`
- `GovernedArtifactLifecycleFact`
- `ModelConfigRef`
- `ModelPolicy`
- `PromptDocument`
- `PromptVersion`
- `ReferenceDocument`
- `ReferenceVersion`
- `SeedProvenance`
- `SkillDocument`
- `SkillVersion`
- `ToolCatalogEntry`
- `ToolPermissionBoundary`

Move this core-app Agent Admin worker-task record from `ai.first.domain.agentfoundation` to `ai.first.domain.coreapp.agentadmin`:

- `PromptRiskReviewTask`

### Domain layer: identity, tenancy, authorization, audit, invitation, notification, attention, governance, workstream infrastructure

Move these foundation records from `ai.first.domain.security` to the indicated `ai.first.domain.foundation.*` packages:

| Target package | Classes |
| --- | --- |
| `ai.first.domain.foundation.identity` | `Account`, `AccountStatus`, `AuthContext`, `Customer`, `EnterpriseIdentityProviderStatus`, `FoundationRole`, `IdentityRepositoryState`, `Membership`, `MembershipStatus`, `ScopeType`, `ScimProvisioningRequest`, `ScimProvisioningResult`, `SsoConfigurationValidation`, `Tenant`, `UserProfile`, `UserSettings`, `WorkosIdentity` |
| `ai.first.domain.foundation.audit` | `AdminAuditEvent` |
| `ai.first.domain.foundation.attention` | `AttentionCategory`, `AttentionItem`, `AttentionItemStatus`, `AttentionRedactionLevel`, `AttentionSeverity`, `AttentionSourceRef`, `AttentionSurfaceRef` |
| `ai.first.domain.foundation.email` | `EmailDeliveryStatus`, `EmailNotificationDelivery`, `EmailNotificationDeliveryStatus`, `EmailNotificationPreference`, `EmailOutboxMessage` |
| `ai.first.domain.foundation.invitation` | `Invitation`, `InvitationLifecycleFact`, `InvitationRepositoryState`, `InvitationStatus` |
| `ai.first.domain.foundation.notification` | `NotificationCategory`, `NotificationChannel`, `NotificationChannelRegistryEntry`, `NotificationChannelStatus`, `NotificationDeliveryAttempt`, `NotificationDeliveryAttemptStatus`, `NotificationExternalOutboxMessage`, `NotificationItem`, `NotificationLifecycleStatus`, `NotificationPreference`, `NotificationPriority`, `NotificationProjectionInput`, `NotificationRedactionLevel`, `NotificationSourceRef`, `NotificationSurfaceRef` |
| `ai.first.domain.foundation.governance` | `GovernancePolicyProposal`, `GovernancePolicySimulationResult` |
| `ai.first.domain.foundation.workstream` | `WorkstreamEventEnvelope`, `WorkstreamEventSourceRef` |

Move these built-in core-app worker/surface records from `ai.first.domain.security` to the indicated `ai.first.domain.coreapp.*` packages:

| Target package | Classes | Reason |
| --- | --- | --- |
| `ai.first.domain.coreapp.useradmin` | `AccessReviewTask` | User Admin access-review worker task for the built-in core app. |
| `ai.first.domain.coreapp.audit` | `AuditTraceSummaryTask` | Audit/Trace summary worker task. |
| `ai.first.domain.coreapp.governance` | `GovernancePolicyImpactTask` | Governance/Policy impact-review worker task. |
| `ai.first.domain.coreapp.myaccount` | `DigestExportRequest`, `MyAccountNotificationCenter`, `MyAccountPersonalAttentionDigestTask` | My Account digest/export and notification-center surface state. |

### Application layer: governed agent foundation

Move these reusable governed-agent runtime classes from `ai.first.application.agentfoundation` to `ai.first.application.foundation.agent`:

- `AgentBehaviorRepository`
- `AgentBehaviorSeedLoader`
- `AgentDefinitionEntity`
- `AgentDefinitionView`
- `AgentReferenceManifestEntity`
- `AgentReferenceManifestView`
- `AgentRuntimeLoaderTools`
- `AgentRuntimeService`
- `AgentRuntimeToolResolver`
- `AgentRuntimeTraceEntity`
- `AgentRuntimeTraceSink`
- `AgentRuntimeTraceView`
- `AgentSkillManifestEntity`
- `AgentSkillManifestView`
- `AkkaAgentBehaviorRepository`
- `AkkaAgentRuntimeTraceSink`
- `DefaultWorkstreamAgentRuntimeInvoker`
- `DurableAgentBehaviorRepositoryEntity`
- `FailClosedWorkstreamAgentRuntimeInvoker`
- `ModelProviderClient`
- `OpenAiModelProviderClient`
- `PromptDocumentEntity`
- `PromptDocumentView`
- `ReferenceDocumentEntity`
- `ReferenceDocumentView`
- `SeedImportResult`
- `SkillDocumentEntity`
- `SkillDocumentView`
- `ToolBoundaryGrantView`
- `ToolPermissionBoundaryEntity`
- `ToolRegistry`
- `WorkstreamAgentRuntimeInvoker`
- `WorkstreamRuntimeAgent`

Move these built-in core-app autonomous-agent, evidence-tool, and task classes from `ai.first.application.agentfoundation` to the indicated `ai.first.application.coreapp.*` packages:

| Target package | Classes |
| --- | --- |
| `ai.first.application.coreapp.useradmin` | `AccessReviewAutonomousAgentResult`, `AccessReviewAutonomousAgentResultRule`, `ComponentClientAccessReviewAutonomousAgentRuntime`, `UserAdminAccessReviewAutonomousAgent`, `UserAdminAccessReviewTasks`, `UserAdminAccessReviewWorker`, `UserAdminEvidenceTools` |
| `ai.first.application.coreapp.agentadmin` | `AgentAdminEvidenceTools`, `AgentAdminPromptRiskAutonomousAgent`, `AgentAdminPromptRiskReviewService`, `AgentAdminPromptRiskTasks`, `AgentAdminService`, `AgentMarketplaceGovernanceService`, `AkkaPromptRiskReviewTaskRepository`, `DurablePromptRiskReviewTaskRepositoryEntity`, `ComponentClientPromptRiskAutonomousAgentRuntime`, `FailClosedPromptRiskAutonomousAgentRuntime`, `PromptRiskAutonomousAgentResult`, `PromptRiskAutonomousAgentResultRule`, `PromptRiskAutonomousAgentRuntime`, `PromptRiskReviewTaskRepository` |
| `ai.first.application.coreapp.audit` | `AuditTraceEvidenceTools`, `AuditTraceSummaryAutonomousAgent`, `AuditTraceSummaryAutonomousAgentRuntime`, `AuditTraceSummaryResult`, `AuditTraceSummaryResultRule`, `AuditTraceSummaryTasks`, `ComponentClientAuditTraceSummaryAutonomousAgentRuntime`, `FailClosedAuditTraceSummaryAutonomousAgentRuntime` |
| `ai.first.application.coreapp.governance` | `GovernancePolicyEvidenceTools`, `GovernancePolicyImpactAutonomousAgent`, `GovernancePolicyImpactAutonomousAgentResult`, `GovernancePolicyImpactAutonomousAgentResultRule`, `GovernancePolicyImpactAutonomousAgentRuntime`, `GovernancePolicyImpactTasks`, `ComponentClientGovernancePolicyImpactAutonomousAgentRuntime`, `FailClosedGovernancePolicyImpactAutonomousAgentRuntime` |
| `ai.first.application.coreapp.myaccount` | `MyAccountEvidenceTools`, `MyAccountPersonalAttentionDigestAutonomousAgent`, `MyAccountPersonalAttentionDigestAutonomousAgentRuntime`, `MyAccountPersonalAttentionDigestResult`, `MyAccountPersonalAttentionDigestResultRule`, `MyAccountPersonalAttentionDigestTasks`, `ComponentClientMyAccountPersonalAttentionDigestAutonomousAgentRuntime`, `FailClosedMyAccountPersonalAttentionDigestAutonomousAgentRuntime` |

### Application layer: foundation services, repositories, and bootstrap

Move these reusable foundation classes from `ai.first.application.security` to the indicated `ai.first.application.foundation.*` packages:

| Target package | Classes |
| --- | --- |
| `ai.first.application.foundation.identity` | `AkkaIdentityRepository`, `AuthContextResolver`, `AuthorizationException`, `BootstrapAdminSeeder`, `DurableIdentityRepositoryEntity`, `EnterpriseIdentityAdminService`, `FailClosedFoundationRuntime`, `IdentityRepository`, `MeResponse`, `MeService`, `StarterSecurityComponents`, `StarterServiceSetup`, `WorkosIdentityResolver` |
| `ai.first.application.foundation.audit` | `AdminAuditView`, `AkkaAuditTraceRepository`, `AuditTraceRepository`, `AuditTraceService` |
| `ai.first.application.foundation.attention` | `AkkaAttentionRepository`, `AttentionProducerService`, `AttentionRepository`, `AttentionRepositoryState`, `AttentionService`, `DurableAttentionRepositoryEntity` |
| `ai.first.application.foundation.email` | `EmailNotificationService`, `ResendEmailService` |
| `ai.first.application.foundation.invitation` | `AkkaInvitationRepository`, `DurableInvitationRepositoryEntity`, `InvitationLifecycleHistoryEntity`, `InvitationRepository`, `InvitationService`, `InvitationView` |
| `ai.first.application.foundation.notification` | `AkkaNotificationRepository`, `DurableNotificationRepositoryEntity`, `NotificationRepository`, `NotificationRepositoryState`, `NotificationService` |
| `ai.first.application.foundation.governance` | `AkkaGovernancePolicyRepository`, `DurableGovernancePolicyRepositoryEntity`, `GovernancePolicyRepository`, `GovernancePolicyService` |
| `ai.first.application.foundation.workstream` | `AkkaWorkstreamEventRepository`, `AkkaWorkstreamLogRepository`, `DurableWorkstreamEventRepositoryEntity`, `DurableWorkstreamLogEntity`, `WorkstreamEventAttentionConsumer`, `WorkstreamEventPublisher`, `WorkstreamEventRepository`, `WorkstreamEventRepositoryState`, `WorkstreamLogRepository`, `WorkstreamLogState` |

### Application layer: built-in core app services and worker repositories

Move these built-in workstream and operational classes from `ai.first.application.security` to the indicated `ai.first.application.coreapp.*` packages:

| Target package | Classes |
| --- | --- |
| `ai.first.application.coreapp.useradmin` | `AccessReviewAutonomousAgentRuntime`, `AccessReviewTaskRepository`, `AccessReviewWorker`, `AkkaAccessReviewTaskRepository`, `DurableAccessReviewTaskRepositoryEntity`, `FailClosedAccessReviewAutonomousAgentRuntime`, `UserAdminAccessReviewService`, `UserAdminService`, `UserDirectoryView` |
| `ai.first.application.coreapp.audit` | `AkkaAuditTraceSummaryTaskRepository`, `AuditTraceSummaryService`, `AuditTraceSummaryTaskRepository`, `DurableAuditTraceSummaryTaskRepositoryEntity` |
| `ai.first.application.coreapp.governance` | `AkkaGovernancePolicyImpactTaskRepository`, `DurableGovernancePolicyImpactTaskRepositoryEntity`, `GovernancePolicyImpactService`, `GovernancePolicyImpactTaskRepository` |
| `ai.first.application.coreapp.myaccount` | `AkkaMyAccountPersonalAttentionDigestTaskRepository`, `DigestExportService`, `DurableMyAccountPersonalAttentionDigestTaskRepositoryEntity`, `MyAccountPersonalAttentionDigestService`, `MyAccountPersonalAttentionDigestTaskRepository`, `MyAccountService` |
| `ai.first.application.coreapp.workstream` | `WorkstreamService` |

## Test package map

Tests should move after or with the production class they validate. Test-only fakes should land beside the corresponding test package so package-private access remains available where currently used.

### Tests currently under `ai.first.application.agentfoundation`

| Target test package | Classes |
| --- | --- |
| `ai.first.application.foundation.agent` | `AgentBehaviorSeedLoaderTest`, `AgentDefinitionEntityTest`, `AgentDefinitionViewIntegrationTest`, `AgentRuntimeServiceTest`, `AgentRuntimeToolResolverTest`, `AgentRuntimeTraceEntityTest`, `AgentRuntimeTraceSinkTest`, `AgentRuntimeTraceViewTest`, `DurableAgentBehaviorRepositoryStateTest`, `FakeModelProviderClient`, `GovernedDocumentEntityTest`, `GovernedDocumentViewTest`, `LocalDemoAgentBehaviorRepository`, `LocalDemoAgentRuntimeTraceSink`, `ManifestBoundaryEntityTest`, `ManifestBoundaryViewTest`, `OpenAiModelProviderClientTest`, `WorkstreamRuntimeAgentTest` |
| `ai.first.application.coreapp.agentadmin` | `AgentAdminPromptRiskAutonomousAgentTest`, `AgentAdminPromptRiskReviewServiceTest`, `AgentMarketplaceGovernanceServiceTest`, `DurablePromptRiskReviewTaskRepositoryEntityTest`, `LocalDemoPromptRiskReviewTaskRepository` |
| `ai.first.application.coreapp.audit` | `AuditTraceSummaryAutonomousAgentTest` |
| `ai.first.application.coreapp.myaccount` | `MyAccountPersonalAttentionDigestAutonomousAgentTest` |
| `ai.first.application.coreapp.useradmin` | `UserAdminAccessReviewAutonomousAgentTest`, `UserAdminAccessReviewWorkerTest` |

### Tests currently under `ai.first.application.security`

| Target test package | Classes |
| --- | --- |
| `ai.first.application.foundation.identity` | `DurableIdentityRepositoryEntityTest`, `EnterpriseIdentityAdminServiceTest`, `FailClosedFoundationRuntime`, `FailClosedIdentityRepository`, `FoundationRuntimeDurabilityBoundaryTest`, `LocalDemoIdentityRepository`, `MeServiceTest`, `RealModelProviderSmokeTest`, `WorkosIdentityResolverTest` |
| `ai.first.application.foundation.audit` | `AdminAuditViewTest`, `FailClosedAuditTraceRepository`, `LocalDemoAuditTraceRepository` |
| `ai.first.application.foundation.attention` | `AttentionProducerServiceTest`, `AttentionServiceTest`, `DurableAttentionRepositoryEntityTest`, `LocalDemoAttentionRepository` |
| `ai.first.application.foundation.email` | `EmailNotificationServiceTest` |
| `ai.first.application.foundation.invitation` | `DurableInvitationRepositoryEntityTest`, `InvitationAndUserAdminServiceTest`, `InvitationLifecycleHistoryEntityTest`, `LocalDemoInvitationRepository` |
| `ai.first.application.foundation.notification` | `DurableNotificationRepositoryEntityTest`, `LocalDemoNotificationRepository`, `NotificationServiceTest` |
| `ai.first.application.foundation.governance` | `DurableGovernancePolicyRepositoryEntityTest`, `FailClosedGovernancePolicyRepository`, `GovernancePolicyServiceTest`, `LocalDemoGovernancePolicyRepository` |
| `ai.first.application.foundation.workstream` | `DurableWorkstreamLogEntityTest`, `FailClosedWorkstreamLogRepository`, `LocalDemoWorkstreamEventRepository`, `LocalDemoWorkstreamLogRepository`, `WorkstreamEventBackboneServiceTest` |
| `ai.first.application.coreapp.useradmin` | `AdminEndpointIntegrationTest`, `DurableAccessReviewTaskRepositoryEntityTest`, `FailClosedAccessReviewTaskRepository`, `InvitationAndUserAdminServiceTest` if split becomes useful, `LocalDemoAccessReviewTaskRepository`, `UserAdminAccessReviewServiceTest` |
| `ai.first.application.coreapp.audit` | `AuditTraceSummaryServiceTest`, `LocalDemoAuditTraceSummaryTaskRepository` |
| `ai.first.application.coreapp.governance` | `GovernancePolicyImpactServiceTest`, `LocalDemoGovernancePolicyImpactTaskRepository` |
| `ai.first.application.coreapp.myaccount` | `DigestExportServiceTest`, `LocalDemoMyAccountPersonalAttentionDigestTaskRepository`, `MyAccountPersonalAttentionDigestServiceTest` |
| `ai.first.application.coreapp.workstream` | `WorkstreamServiceTest` |

`InvitationAndUserAdminServiceTest` currently spans foundation invitation onboarding and coreapp user-admin behavior. Prefer either moving it to `ai.first.application.coreapp.useradmin` for the migration, or splitting it later if the boundary check requires package-local test clarity.

## Recommended migration order

1. Move domain foundation records first, preserving old class names and updating all imports.
2. Move application foundation classes and `MeEndpoint`, because coreapp classes depend on foundation identity, auth, audit, invitation, notification, attention, agent runtime, and workstream-event infrastructure.
3. Move coreapp workstream/task/domain/application/API classes.
4. Add package-info docs and lightweight boundary checks after imports stabilize.

## Ambiguities and bounded recommendations

The current split is feasible without a blocking question, but these classes deserve special attention during migration:

- `GovernancePolicyProposal` and `GovernancePolicySimulationResult`: classified as foundation because policy governance is a reusable SaaS capability. The impact-review task/service/agent classes are coreapp because they implement the built-in Governance/Policy workstream.
- `AdminAuditView`: classified as foundation because audit browsing is reusable platform read-model behavior, even though it is first exposed by coreapp Admin/Audit surfaces.
- `WorkstreamEvent*` and `WorkstreamLog*` repositories/entities: classified as foundation infrastructure because future business workstreams should reuse the event/log backbone. `WorkstreamService`, endpoints, and functional-agent surface orchestration remain coreapp.
- `MyAccountNotificationCenter`: classified as coreapp because it is a My Account surface aggregate. Generic `Notification*` records and services remain foundation.
- `StarterSecurityComponents`: classified as foundation bootstrap/composition for now even though it wires coreapp services. If a later architecture check cannot tolerate foundation composition importing coreapp classes, split it into `FoundationComponents` plus `CoreAppComponents` during the boundary-docs/checks task.
- `InvitationAndUserAdminServiceTest`: spans the foundation invitation capability and coreapp User Admin workflow. Splitting is optional unless boundary checks require it.

## Stale-reference search patterns for migration tasks

Use these searches after each move, scoped to the packages moved by that task:

```bash
rg "package ai\.first\.(api\.(admin|security|workstream)|application\.(security|agentfoundation)|domain\.(security|agentfoundation))" src/main/java src/test/java
rg "import ai\.first\.(api\.(admin|security|workstream)|application\.(security|agentfoundation)|domain\.(security|agentfoundation))" src/main/java src/test/java
rg "ai\.first\.(api\.(admin|security|workstream)|application\.(security|agentfoundation)|domain\.(security|agentfoundation))" app-description docs specs README.md AGENTS.md skills-pack 2>/dev/null
```

Historical/provenance text in this mini-project may still mention old package names when documenting the migration source state. Production package declarations and imports should not.

## Business extension seam

No current class maps to `*.business.*`. Future business-specific code should be additive under:

```text
src/main/java/ai/first/api/business/<business-area>/
src/main/java/ai/first/application/business/<business-area>/
src/main/java/ai/first/domain/business/<business-area>/
src/test/java/ai/first/business/<business-area>/
```

Business packages may depend on stable foundation contracts and approved coreapp extension hooks. Foundation must not depend on coreapp or business. Coreapp must not depend on business.
