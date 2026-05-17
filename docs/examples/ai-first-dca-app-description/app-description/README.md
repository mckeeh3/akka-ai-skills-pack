# AI-First DCA App Description

This reference app-description follows `docs/internal-app-description-architecture.md` as a DCA-specific vertical extension of the canonical secure AI-first SaaS seed app-description.

Use `docs/examples/ai-first-saas-seed-app-description/app-description/` as the canonical baseline for foundation structure and reusable generated-app starting points. Use this tree as domain-rich source material for office-device/DCA lifecycle automation, telemetry-driven delegated work, policy-gated decisions, traces, and outcome loops.

This tree is intentionally non-runnable. Future work should use it as source material for executable reference slices rather than treating it as this repository's business app or as the canonical structural template.

Layer ownership for this vertical example:
- `00-system/`: app identity, readiness, and generation policy.
- `10-capabilities/`: DCA/lifecycle, telemetry, supplies, service, billing, contract, customer-success, inventory, onboarding, and offboarding capabilities layered on top of the mandatory seed/foundation capability.
- `15-operating-model/`: AI-first goals, delegated work, retained human authority, policies, decisions, traces, and outcomes.
- `20-behavior/`: customer/device/DCA collector state models, workflows, and rules.
- `30-tests/`: acceptance, regression, negative, and operational verification expectations.
- `40-auth-security/`: identity, tenant/account authorization, agent permissions, data protection, and trust boundaries.
- `50-observability/`: audit events, traces, metrics, health, alerts, and diagnosability.
- `55-ui/`: supervision, decision, governance, digest, trace, and lifecycle command surfaces.
- `60-generation/`: non-runnable realization scope and future Akka/React output mapping.
- `70-traceability/`: cross-layer maps.
- `80-review/`: derived review summaries only.
