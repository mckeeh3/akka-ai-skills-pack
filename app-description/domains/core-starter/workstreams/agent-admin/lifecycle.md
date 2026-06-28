# Agent Admin lifecycle

Workstream id: `agent-admin`
Owning domain: `core-starter`
Current readiness: `compile-ready`
Ready-to-build assessment: 2026-06-26 — see `../ready-to-build-status.md`; normalized to current lifecycle term `compile-ready` after behavior-profile realization.
Automated alignment readiness: `partially-aligned` for the Agent Admin behavior-profile slice verified by AABP-05-003; manual browser/API acceptance and real external provider smoke remain residual checks.
Implementation alignment: `partially-aligned`
Source alignment: `realization/source-alignment.md`
Last description change: 2026-06-27 — current app-description prioritizes SaaS-admin-only behavior-profile proposal/review/activation over stale direct document-save and governance-console flows.
Last alignment review: 2026-06-27 — AABP-05-003 terminal verification passed backend/frontend/typecheck/build/diff checks after AABP-05-002 repaired the full-suite seed-count blocker.
Last compile: 2026-06-27 — Agent Admin behavior-profile realization mini-project closed at `api-smoked/frontend-rendered`.
Last manual runtime test: unknown

## Current alignment posture

This workstream is partially aligned for the implemented Agent Admin behavior-profile realization scope. Evidence now spans protected WorkstreamEndpoint/API routing, SaaS Owner/Admin authorization and safe non-admin denials, proposal-first save/activation, stale/high-risk denial behavior, restore proposals, skill/reference lifecycle, behavior-profile versions and assignments, active-profile runtime loader/tool-boundary traces, model-backed editing-agent tests with fail-closed missing-runtime behavior, and frontend current surface contracts.

Overall runtime readiness is still not claimed by this lifecycle record. Manual browser acceptance, real WorkOS/AuthKit production login smoke, real external provider-backed editing-agent smoke, and broader UI/operator acceptance remain residual checks outside the completed automated mini-project.

## Slice status map

| Slice | Current status | Evidence level | Next validation owner |
| --- | --- | --- | --- |
| SaaS-admin access and denial | `partially-aligned` | AABP-05-003 `api-smoked` evidence through `/api/workstream/*`, selected `AuthContext`, SaaS Owner/Admin allow path, and browser-safe non-admin denial. | Manual browser login/context smoke only. |
| Catalog/detail/profile inspection | `partially-aligned` | AABP-05-003 protected workstream/API and frontend contract evidence for generated-agent behavior-profile inspection and stale whole-agent mutation de-exposure. | Manual catalog/detail UX acceptance only. |
| Proposal-first prompt/doc editing | `partially-aligned` | AABP-05-003 service/API smoke for draft proposal, approve/activate, stale activation denial, traces, and active-version update only after activation. | Manual editor workflow acceptance and real provider smoke. |
| Risk and authority-expansion handling | `partially-aligned` | AABP-05-003 service-level high-risk/authority-expansion direct activation denial and browser stale proposal recovery evidence. | Broader browser/API high-risk review-route smoke if required. |
| Restore and version history | `partially-aligned` | AABP-05-003 backend evidence that restore creates proposal and historical versions remain read-oriented. | Dedicated browser restore smoke if required. |
| Skill/reference lifecycle | `partially-aligned` | AABP-05-003 backend evidence for proposal-first create/deprecate semantics and loader access removal. | Browser create/deprecate UX smoke if required. |
| Behavior-profile versions/assignments | `partially-aligned` | AABP-05-003 API/frontend evidence for model config ref, skill assignment, generated tool assignment, tenant/global profile version summaries, and no generated tool code mutation. | Manual assignment workflow smoke only. |
| Runtime loader and traces | `partially-aligned` | AABP-05-003 runtime service/tool resolver/trace tests plus browser runtime trace surface smoke for active-profile docs, assigned skill/reference reads, generated-tool decisions, and tool-boundary denials. | Real provider/model invocation and manual trace investigation smoke. |
| Editing-agent model path | `partially-aligned-provider-success-config-blocked` | AABP-05-003 Akka `AgentAdminDocEditingAgent` tests with `TestModelProvider` plus fail-closed missing-runtime behavior; no fake normal provider success claimed. | Real external provider configuration smoke. |
| Frontend current surfaces | `partially-aligned` | AABP-05-003 frontend tests, typecheck, and build for current Agent Admin surfaces/contracts and secret-boundary checks. | Manual responsive/accessibility browser review. |

## Blockers and assumptions

- File-level source alignment records automated `api-smoked/frontend-rendered` evidence for the behavior-profile realization mini-project, not full runtime readiness.
- `compile-ready` remains the app-description lifecycle term; `partially-aligned` records implementation/test evidence for the completed mini-project scope.
- Runtime-ready/manual-ready is not claimed because manual browser/API acceptance and real external provider-backed editing-agent success were not exercised.
- The service-internal generated-agent profile update seam is de-exposed from the current product/API path but has not been physically removed.
- Future workstream-specific validation must classify any new app-description or implementation changes as aligned, stale-description-changed, stale-code-changed, partially-aligned, blocked, or intentionally description-only.

## Next recommended action

Run a separate Agent Admin runtime-ready smoke mini-project if manual/browser/API/provider evidence is required beyond the completed `api-smoked/frontend-rendered` behavior-profile realization scope.
