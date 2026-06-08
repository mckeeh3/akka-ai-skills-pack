# Realization: Akka components for My Account

Capability: `account-context-and-profile`.

This map is docs-only. It points to current implementation evidence and does not change runtime behavior.

## Component and service evidence

| Intent binding | Akka / Java evidence | Notes |
|---|---|---|
| Browser-safe current account context and selected tenant/customer `AuthContext` | `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`, `src/main/java/ai/first/application/foundation/identity/**` | Protected `/api/me` is the authoritative account/context edge. It must not expose provider secrets or trust frontend-selected scope. |
| Profile/settings and personal account panels | `src/main/java/ai/first/application/coreapp/myaccount/MyAccountService.java` | Service-level mapping for account profile/settings and self-service summaries. |
| Personal attention digest/export request | `src/main/java/ai/first/application/coreapp/myaccount/DigestExportService.java`, `MyAccountPersonalAttentionDigestService.java`, `MyAccountPersonalAttentionDigestTasks.java` | Digest/export is governed and redaction-aware; it is not a permission-management path. |
| Model-backed personal attention digest worker | `MyAccountPersonalAttentionDigestAutonomousAgent.java`, `ComponentClientMyAccountPersonalAttentionDigestAutonomousAgentRuntime.java`, `FailClosedMyAccountPersonalAttentionDigestAutonomousAgentRuntime.java` | Runtime must use governed model/provider configuration or fail closed; test doubles do not prove normal model-backed runtime. |
| Durable digest task state | `DurableMyAccountPersonalAttentionDigestTaskRepositoryEntity.java`, `AkkaMyAccountPersonalAttentionDigestTaskRepository.java` | Durable task repository evidence for personal digest work. |
| Evidence tools and workstream traces | `MyAccountEvidenceTools.java`, `src/main/java/ai/first/application/foundation/workstream/**`, `src/main/java/ai/first/application/foundation/audit/**` | Tool/data access and denials must create work/audit trace evidence. |

## Validation evidence

- `src/test/java/ai/first/application/coreapp/myaccount/DigestExportServiceTest.java`
- `src/test/java/ai/first/application/coreapp/myaccount/MyAccountPersonalAttentionDigestServiceTest.java`
- `src/test/java/ai/first/application/coreapp/myaccount/MyAccountPersonalAttentionDigestAutonomousAgentTest.java`
- `frontend/src/workstream-my-account-vertical.contract.test.mjs`
- Shared shell tests: `frontend/src/workstream-shell.contract.test.mjs`, `frontend/src/workstream-attention-backbone.contract.test.mjs`

## Gaps / caveats

- Live provider validation is external-configuration dependent; missing model/provider configuration must fail closed.
- `LocalDemoMyAccountPersonalAttentionDigestTaskRepository.java` is test/demo support only, not normal runtime proof.
