# Agent-First DCA App Description Scaffold

This scaffold follows `docs/internal-app-description-architecture.md`.
It is intentionally incomplete until Sprint 6 follow-on tasks fill the layer files.

Layer ownership for this example:
- `00-system/`: app identity, readiness, and generation policy.
- `10-capabilities/`: lifecycle, telemetry, supplies, service, billing, contract, customer-success, inventory, onboarding, and offboarding capabilities.
- `15-operating-model/`: AI-first goals, delegated work, retained human authority, policies, decisions, traces, and outcomes.
- `20-behavior/`: customer/device/DCA collector state models, workflows, and rules.
- `30-tests/`: acceptance, regression, negative, and operational verification expectations.
- `40-auth-security/`: identity, tenant/account authorization, agent permissions, data protection, and trust boundaries.
- `50-observability/`: audit events, traces, metrics, health, alerts, and diagnosability.
- `55-ui/`: supervision, decision, governance, digest, trace, and lifecycle command surfaces.
- `60-generation/`: realization scope and future Akka/React output mapping.
- `70-traceability/`: cross-layer maps.
- `80-review/`: derived review summaries only.
