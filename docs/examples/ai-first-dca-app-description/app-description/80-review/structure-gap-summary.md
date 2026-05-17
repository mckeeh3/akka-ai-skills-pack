# Structure Gap Summary

## Purpose

This derived review summary maps the current DCA app-description tree against the current internal app-description architecture and the canonical secure AI-first SaaS seed example.

Use it to route later refresh tasks. Authoritative meaning remains in the layer files themselves.

## Baseline interpretation

- Canonical foundation/structure reference: `docs/examples/ai-first-saas-seed-app-description/app-description/`.
- DCA role: domain-rich vertical extension for office-device/DCA lifecycle automation.
- DCA status: non-runnable reference material; not a generated application and not the canonical seed template.
- Refresh policy: preserve useful DCA domain semantics while aligning foundation, capability, security, tests, UI, observability, traceability, and generation files with current app-description expectations.

## Gap map

| Layer | Current DCA shape | Intentional vertical-example omissions | Refresh blockers / gaps | Scheduled by |
|---|---|---|---|---|
| `00-system/` | Has manifest, readiness status, generation policy, and README. | Keeps non-runnable reference status rather than runnable seed posture. | None for Sprint 1. Later tasks may update readiness as deeper gaps are closed. | Complete for current positioning pass. |
| `10-capabilities/` | Has a high-level domain capability index for lifecycle, telemetry, supplies, service, billing, onboarding, offboarding, governance, command center, and audit/outcomes. | DCA-specific capability files are intentionally not present yet; the tree currently acts as source material, not a generation-ready contract set. | Missing first-class `01-secure-tenant-user-foundation.md`; capability index is not yet in current capability-first table shape; DCA capabilities lack actors/callers, AuthContext, schemas, idempotency, policy/approval, audit/trace, exposure surfaces, and tests. | Sprint 2 foundation tasks; Sprint 3 capability-first refactor tasks. |
| `15-operating-model/` | Contains DCA goals, agent authority, agent-team design, policies/gates, and decisions/evidence. | Uses a richer DCA-specific `agent-team-design.md` file in addition to the seed-like authority file. | Missing seed-aligned `audit-trace-and-outcomes.md` in this layer; outcome/audit semantics currently live mostly under `50-observability/`. Cross-links back to capability ids will need refresh after capability refactor. | Sprint 3 and Sprint 5 traceability/review tasks. |
| `20-behavior/` | Contains DCA lifecycle state model, supplies flow, lifecycle/exception flows, and approval/fail-safe rules. | Does not mirror seed file names because DCA behavior is vertical-domain-specific. | Behavior files are not yet fully linked to capability-first contracts or concrete test specs; secure foundation behavior such as tenant/user access lifecycle is not represented at generation-ready detail. | Sprint 2 foundation alignment; Sprint 3 behavior/capability mapping; Sprint 4 tests. |
| `30-tests/` | Has a test index plus concrete acceptance, negative, regression, and operational description-level specs for `CAP-00` and `CAP-03`. | Executable test code and provider/domain fixtures remain deferred while DCA remains reference-only. | Future realization still needs concrete telemetry, inventory, fulfillment, contract, WorkOS/JWT, email/outbox, policy-threshold, and deterministic agent/tool fixtures. | Sprint 4 test refresh complete for description-level specs; fixture work belongs to future realization. |
| `40-auth-security/` | Has identity/trust, authorization, agent permissions, data protection, boundary/surface rules, and README. | Keeps DCA-specific agent permission file as a vertical concern. | Needs current foundation terminology: WorkOS authenticates; Akka-owned local state authorizes; Invitation lifecycle; Account/UserProfile/UserSettings; Membership/Role/Permission; AuthContext; `/api/me`; AdminAuditEvent; support access; billing boundary; backend enforcement for every route, command, query, workflow action, stream, timer, consumer side effect, and agent/tool call. | Sprint 2 auth/security tasks. |
| `50-observability/` | Has combined audit/trace/outcomes guidance and README. | Collapses seed's separate logs, metrics, traces, and health files to a vertical summary for now. | Needs seed-aligned observability split or equivalent coverage for logs/audit, metrics, traces/correlation, health/alerts, retention/redaction, support-access audit, billing-boundary audit, and AI/tool activity evidence. | Sprint 5 observability and review tasks. |
| `55-ui/` | Has UI surfaces and style guide. | Uses compact DCA UI surface guidance rather than seed's full UI layer file set. | Needs current foundation UI surfaces and contracts: sign-in, context selection, profile/settings, Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings, supervision, decisions, governance, audit/traces, outcomes, frontend API contracts, realtime states, accessibility, responsive behavior, and secret-boundary constraints. | Sprint 4/Sprint 5 UI and readiness tasks. |
| `60-generation/` | Has implementation slices and README. | Keeps future realization as planning/reference material instead of runnable generation instructions. | Missing current seed-style `realization-scope.md`, `regeneration-map.md`, and `output-surfaces.md`; implementation slices still need reconciliation after foundation/capability/security/test refresh. | Sprint 5 generation/readiness tasks. |
| `70-traceability/` | Has AI-first coverage map and README. | Uses a high-level AI-first coverage map as domain planning source material. | Missing current traceability maps: `capability-to-behavior-map.md`, `operating-model-to-behavior-map.md`, `behavior-to-tests-map.md`, and `change-impact-map.md`; existing map depends on capability ids that will be refactored. | Sprint 3 and Sprint 5 traceability tasks. |
| `80-review/` | Has README and this derived structure gap summary. | Review files remain non-authoritative. | Missing `latest-change-summary.md` and `latest-readiness-summary.md`; create/update only after substantive refresh passes. | Sprint 5 review/readiness tasks. |

## Immediate blockers before runnable generation

1. Secure tenant/user foundation capability is absent from `10-capabilities/`.
2. Auth/security layer does not yet fully express current mandatory SaaS foundation semantics.
3. DCA capabilities are not yet capability-first contracts.
4. UI and observability layers are compact vertical summaries, not generation-ready contracts.
5. Test specs are now concrete at description level, but executable fixtures and provider/domain payloads remain undefined.
6. Traceability and generation layers do not yet match current seed control-map expectations.

## Intentional non-blockers for a vertical reference

- DCA-specific lifecycle, telemetry, supplies, service, billing, onboarding, and offboarding scope may remain richer than the seed.
- File names may differ when they preserve useful domain semantics, provided equivalent layer responsibilities are explicit.
- Runnable code, integration adapters, numeric policy thresholds, model/provider choices, and production fixtures may stay deferred until a bounded realization slice is requested.
- `80-review/` summaries may remain derived and incomplete while authoritative layer refresh tasks are still pending.
