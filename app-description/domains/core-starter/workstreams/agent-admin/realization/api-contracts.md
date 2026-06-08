# Realization: API contracts for Agent Admin

Capability: `managed-agent-governance`.

## Browser/API evidence

| Tool / action | Exposure | API evidence | Contract obligations |
|---|---|---|---|
| `list-agent-catalog` | `browser-tool`, `agent-tool` | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `WorkstreamEndpoint.java`, `AgentDefinitionView.java`; frontend API clients | Scoped catalog with status, owner/steward, authority profile, and redaction. |
| `read-agent-behavior-detail` | `browser-tool`, `agent-tool` | foundation agent document/entity/view classes; `AgentAdminService.java` | Reads active/draft versions, manifests, boundaries, model refs, and trace links without exposing secrets. |
| `draft-agent-behavior-proposal` | `browser-tool`, `agent-tool` proposal | `AgentAdminService.java`, `AgentMarketplaceGovernanceService.java`, prompt risk review service | Creates reviewable drafts/diffs; authority expansion becomes denial or approval-required proposal. |
| `approve-activate-or-rollback-agent-behavior` | `browser-tool` approval | `AdminEndpoint.java`, behavior repository/entities/services | Human/backend-policy governed commit/rollback with audit and version trace. |
| `readSkill`, `readReferenceDoc` | `agent-tool` loaders | `AgentRuntimeLoaderTools.java`, manifests, boundary entities/views | Manifest- and boundary-authorized loading only; denied loads are traced. |
| Workstream messages/actions/events | `browser-tool` | `WorkstreamEndpoint.java`, `frontend/src/api/HttpWorkstreamApiClient.ts` | Typed surfaces/action results with correlation ids and work traces. |

## Validation evidence

- `src/test/java/ai/first/application/foundation/agent/AgentRuntimeToolResolverTest.java`
- `src/test/java/ai/first/application/foundation/agent/AgentRuntimeTraceSinkTest.java`
- `src/test/java/ai/first/application/coreapp/agentadmin/AgentAdminPromptRiskReviewServiceTest.java`
- `frontend/src/workstream-agent-admin-vertical.contract.test.mjs`

## Gaps / caveats

- Provider secrets are never browser DTOs; live model-provider proof is external-configuration dependent.
