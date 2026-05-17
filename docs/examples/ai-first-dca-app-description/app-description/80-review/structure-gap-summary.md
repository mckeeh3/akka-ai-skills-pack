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
| `10-capabilities/` | Has `CAP-00` secure foundation, a current capability-first index, a detailed `CAP-03` Supplies Autopilot contract, and lightweight DCA contracts for remaining vertical capabilities. | Most DCA-specific capabilities intentionally remain routing contracts while DCA is a vertical reference rather than a whole-app realization target. | Future realization must expand non-`CAP-03` DCA contracts with exact schemas, integration payloads, thresholds, idempotency details, approval rules, and tests before implementing those verticals. | Sprint 2 foundation and Sprint 3 capability-first refresh complete; future realization detail remains. |
| `15-operating-model/` | Contains DCA goals, agent authority, agent-team design, policies/gates, and decisions/evidence. | Uses a richer DCA-specific `agent-team-design.md` file in addition to the seed-like authority file. | Missing seed-aligned `audit-trace-and-outcomes.md` in this layer; outcome/audit semantics currently live mostly under `50-observability/`. Cross-links back to capability ids will need refresh after capability refactor. | Sprint 3 and Sprint 5 traceability/review tasks. |
| `20-behavior/` | Contains DCA lifecycle state model, supplies flow, lifecycle/exception flows, and approval/fail-safe rules. | Does not mirror seed file names because DCA behavior is vertical-domain-specific. | Behavior files are not yet fully linked to capability-first contracts or concrete test specs; secure foundation behavior such as tenant/user access lifecycle is not represented at generation-ready detail. | Sprint 2 foundation alignment; Sprint 3 behavior/capability mapping; Sprint 4 tests. |
| `30-tests/` | Has a test index plus concrete acceptance, negative, regression, and operational description-level specs for `CAP-00` and `CAP-03`. | Executable test code and provider/domain fixtures remain deferred while DCA remains reference-only. | Future realization still needs concrete telemetry, inventory, fulfillment, contract, WorkOS/JWT, email/outbox, policy-threshold, and deterministic agent/tool fixtures. | Sprint 4 test refresh complete for description-level specs; fixture work belongs to future realization. |
| `40-auth-security/` | Has refreshed identity/trust, authorization, agent permissions, data protection, boundary/surface rules, and README aligned to `CAP-00`. | Keeps DCA-specific agent permission file as a vertical concern. | Future runnable implementation must bind these description-level rules to concrete WorkOS/JWT modes, invitation/email adapters, backend authorization code, support-access workflows, and generated security tests. | Sprint 2 auth/security refresh complete; future implementation detail remains. |
| `50-observability/` | Has README plus seed-aligned observability files for logs/audit, metrics, traces/correlation, health/alerts, and expanded audit/trace/outcomes. | Keeps DCA-specific trace and outcome examples richer than the seed because they are vertical reference material. | Description-level observability now covers foundation security events and DCA work/decision/policy/tool/data-access/outcome traces; runnable realization still needs concrete provider payloads, thresholds, retention periods, adapters, and executable validation fixtures. | Sprint 4 observability refresh complete; Sprint 4 readiness and Sprint 5 review/generation handoff remain. |
| `55-ui/` | Has refreshed UI surfaces, selected style guide, mandatory foundation administration surfaces, DCA operational surfaces, API/realtime needs, tests, accessibility, responsive behavior, and secret-boundary constraints. | Uses compact DCA UI surface guidance rather than seed's full UI layer file set. | Future runnable realization still needs exact endpoint schemas, generated frontend components, CSS token implementation, realtime topic wiring, and executable UI tests. | Sprint 4 UI refresh complete; Sprint 5 realization handoff remains. |
| `60-generation/` | Has implementation slices and README. | Keeps future realization as planning/reference material instead of runnable generation instructions. | Missing current seed-style `realization-scope.md`, `regeneration-map.md`, and `output-surfaces.md`; implementation slices still need reconciliation after foundation/capability/security/test refresh. | Sprint 5 generation/readiness tasks. |
| `70-traceability/` | Has AI-first coverage map plus `capability-to-layer-map.md` linking foundation and DCA capabilities to behavior, tests, auth/security, UI, observability, generation, and review layers. | Uses a high-level AI-first coverage map as domain planning source material rather than mirroring every seed traceability file. | Future final review may add narrower maps only if needed; current blocker is Sprint 5 realization handoff, not capability traceability discovery. | Sprint 3 traceability refresh complete; Sprint 5 review remains. |
| `80-review/` | Has README, this derived structure gap summary, and latest readiness summary. | Review files remain non-authoritative. | Missing `latest-change-summary.md`; final review should update derived summaries if Sprint 5 finds consistency issues. | Sprint 4 readiness summary complete; Sprint 5 review remains. |

## Immediate blockers before runnable generation

1. Non-`CAP-03` DCA capabilities remain lightweight routing contracts and need expansion before those verticals are implemented.
2. Executable provider/domain fixtures remain undefined for telemetry, inventory/fulfillment, contracts, WorkOS/JWT, email/outbox, policies, and deterministic agents/tools.
3. Exact UI/API schemas, realtime topics, generated frontend components, CSS token implementation, and executable UI tests remain future realization work.
4. Numeric thresholds, retention/redaction policy, alert thresholds, integration adapters, and generated validation/evaluation fixtures remain undefined.
5. Generation handoff files still need Sprint 5 reconciliation before any executable slice starts.

## Intentional non-blockers for a vertical reference

- DCA-specific lifecycle, telemetry, supplies, service, billing, onboarding, and offboarding scope may remain richer than the seed.
- File names may differ when they preserve useful domain semantics, provided equivalent layer responsibilities are explicit.
- Runnable code, integration adapters, numeric policy thresholds, model/provider choices, and production fixtures may stay deferred until a bounded realization slice is requested.
- `80-review/` summaries may remain derived and incomplete while authoritative layer refresh tasks are still pending.
