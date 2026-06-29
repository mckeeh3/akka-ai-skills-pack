# Realization: API contracts for My Account

Capability: `account-context-and-profile`.

## Browser/API evidence

| Tool / action | Exposure | API evidence | Contract obligations |
|---|---|---|---|
| `read-current-account-context` | `browser-tool`, `agent-tool` | `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`; frontend `frontend/src/api/HttpApiClient.ts`, `frontend/src/api/types.ts` | JWT/request-context backed account resolution, backend-owned tenant/customer scope, browser-safe memberships/capabilities, no secrets. |
| `update-own-profile-settings` | `browser-tool` | `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`; `src/main/java/ai/first/application/coreapp/myaccount/MyAccountService.java` | Self-service settings only; cannot grant roles/capabilities; validation errors and denials are explicit and traced. |
| `request-personal-digest-export` | `browser-tool`, `agent-tool` | `WorkstreamEndpoint.java`; digest task services/entities | Creates a redaction-aware request with correlation id, trace link, policy denial behavior, provider/runtime fail-closed blocked state, workflow progress, and advisory result review. |
| Notification center actions | `browser-tool` | `WorkstreamEndpoint.java`; notification projection/services | Lists only current user's authorized in-app notification center, lifecycle mutations affect notification state only, and source opening reauthorizes target surfaces. |
| Workstream messages/actions/events | `browser-tool` | `WorkstreamEndpoint.java`, `frontend/src/api/HttpWorkstreamApiClient.ts`, `frontend/src/api/WorkstreamRealtimeClient.ts` | Responses are typed workstream items/surfaces/events and preserve auth/tenant scope server-side. |

## Validation evidence

- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`
- `frontend/src/workstream-actions.contract.test.mjs`
- `frontend/src/workstream-composer-message-api.contract.test.mjs`
- `frontend/src/workstream-attention-update-delivery.contract.test.mjs`

## Runtime-validation ownership

My Account owns workstream-level runtime-validation references for `/api/me` bootstrap evidence where the evidence proves My Account selected-context rendering, profile/settings/context surfaces, open-denied recovery, trace refs, and frontend secret boundaries. Shared auth foundation may separately own provider login/session mechanics. The My Account validation path must exercise protected `/api/me` and workstream API mappings through signed-in member scope; fixture-only, client-selected context, or model-less substitute paths do not satisfy alignment for this workstream.

## Rebuilt surface contract obligations

- Dashboard API payloads must support a personal command center: context/authority summary, top attention counters, `Needs me` items, control-panel summaries, authorized workstream links, redaction, and trace/correlation fields.
- Profile/settings APIs must distinguish editable self-service fields from immutable/provider-backed facts and must reject unsupported role, capability, account-status, provider-secret, theme-injection, or tenant-wide settings changes before mutation.
- Settings APIs must persist named theme ids through governed self-service settings. Browser-local theme preview is not persistence and must not be reported as saved until the backend action succeeds.
- Context APIs must source selected context from backend-owned `/api/me`/protected shell APIs, list only authorized contexts, and mark affected surfaces stale after context switching.
- Digest/export APIs must expose progress, result, and blocked surfaces without fake/model-less normal-runtime success and without mutating source attention.
- Denied/open-unavailable APIs must return safe system-message surfaces without leaking hidden workstream/context/source existence.

## Gaps / caveats

- API descriptions do not prove runtime readiness; feature-bearing changes must validate the local Akka/API/UI path.
