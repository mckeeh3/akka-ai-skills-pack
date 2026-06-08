# Realization: API contracts for My Account

Capability: `account-context-and-profile`.

## Browser/API evidence

| Tool / action | Exposure | API evidence | Contract obligations |
|---|---|---|---|
| `read-current-account-context` | `browser-tool`, `agent-tool` | `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`; frontend `frontend/src/api/HttpApiClient.ts`, `frontend/src/api/types.ts` | JWT/request-context backed account resolution, backend-owned tenant/customer scope, browser-safe memberships/capabilities, no secrets. |
| `update-own-profile-settings` | `browser-tool` | `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`; `src/main/java/ai/first/application/coreapp/myaccount/MyAccountService.java` | Self-service settings only; cannot grant roles/capabilities; validation errors and denials are explicit and traced. |
| `request-personal-digest-export` | `browser-tool`, `agent-tool` | `WorkstreamEndpoint.java`; `DigestExportService.java`; digest task services/entities | Creates a redaction-aware request with correlation id, trace link, and policy denial behavior. |
| Workstream messages/actions/events | `browser-tool` | `WorkstreamEndpoint.java`, `frontend/src/api/HttpWorkstreamApiClient.ts`, `frontend/src/api/WorkstreamRealtimeClient.ts` | Responses are typed workstream items/surfaces/events and preserve auth/tenant scope server-side. |

## Validation evidence

- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`
- `frontend/src/workstream-actions.contract.test.mjs`
- `frontend/src/workstream-composer-message-api.contract.test.mjs`
- `frontend/src/workstream-attention-update-delivery.contract.test.mjs`

## Gaps / caveats

- API descriptions do not prove runtime readiness; feature-bearing changes must validate the local Akka/API/UI path.
