# Realization: API contracts for Agent Admin

Capability: `managed-agent-governance`.

## Browser/API evidence

| Tool / action | Exposure | API evidence | Contract obligations |
|---|---|---|---|
| `list-agent-catalog` | `browser-tool`, `agent-tool` | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `WorkstreamEndpoint.java`, `AgentDefinitionView.java`; frontend API clients | Scoped catalog with status, owner/steward, authority profile, and redaction. |
| `read-agent-behavior-detail` | `browser-tool`, `agent-tool` | foundation agent document/entity/view classes; `AgentAdminService.java` | Reads active/draft versions, manifests, boundaries, model refs, and trace links without exposing secrets. |
| `draft-agent-behavior-proposal` | `browser-tool`, `agent-tool` proposal | `AgentAdminService.java`, `AgentMarketplaceGovernanceService.java`, prompt risk review service | Creates reviewable drafts/diffs; authority expansion becomes denial or approval-required proposal. |
| Proposal decision tools (`submit-agent-behavior-proposal`, `approve-agent-behavior-proposal`, `reject-agent-behavior-proposal`, `defer-agent-behavior-proposal`, `cancel-agent-behavior-proposal`) | `browser-tool`; agent prepares only | `AdminEndpoint.java`, behavior repository/entities/services | Human/backend-policy governed proposal state transitions with reason/acknowledgement validation, idempotency, stale-version handling, audit, and trace evidence; approval does not activate behavior. |
| Lifecycle confirmation tools (`activate-agent-behavior-version`, `rollback-agent-behavior-version`, `deactivate-agent-behavior-version`) | `browser-tool` approval | `AdminEndpoint.java`, behavior repository/entities/services | Explicit activation/rollback/deactivation from legal backend states only, with version, provider/runtime, tool-boundary, policy, idempotency, audit, and trace checks. |
| Prompt-risk review tools (`start-agent-prompt-risk-review`, `read-agent-prompt-risk-review`, `accept-agent-prompt-risk-review`, `reject-agent-prompt-risk-review`, `cancel-agent-prompt-risk-review`) | `browser-tool`, `agent-tool` read/prepare, `internal-tool` worker | prompt risk review service and task state | Real model-backed review lifecycle; blocked/deferred/fixture-only/model-less results cannot be accepted as evidence. |
| Seed-import tools (`prepare-agent-seed-import`, `start-agent-seed-import`, `cancel-agent-seed-import`) | `browser-tool`; agent prepares only | seed material/import services and provenance state | Customization-preserving import planning/execution with conflict, provenance, provider/runtime, idempotency, and trace handling. |
| `readSkill`, `readReferenceDoc` | `agent-tool` loaders | `AgentRuntimeLoaderTools.java`, manifests, boundary entities/views | Manifest- and boundary-authorized loading only; denied loads are traced. |
| Workstream messages/actions/events | `browser-tool` | `WorkstreamEndpoint.java`, `frontend/src/api/HttpWorkstreamApiClient.ts` | Typed surfaces/action results with correlation ids and work traces. |

## Validation evidence

- `src/test/java/ai/first/application/foundation/agent/AgentRuntimeToolResolverTest.java`
- `src/test/java/ai/first/application/foundation/agent/AgentRuntimeTraceSinkTest.java`
- `src/test/java/ai/first/application/coreapp/agentadmin/AgentAdminPromptRiskReviewServiceTest.java`
- `frontend/src/workstream-agent-admin-vertical.contract.test.mjs`

## Gaps / caveats

- Provider secrets are never browser DTOs; live model-provider proof is external-configuration dependent.
