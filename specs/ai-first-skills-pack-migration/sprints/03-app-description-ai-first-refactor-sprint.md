# Sprint 3: App-Description AI-First Refactor

## Sprint goal

Refactor the description-first path so maintained app descriptions represent AI-first SaaS applications as agentic operating systems, not primarily CRUD/domain-screen inventories.

## Dependencies

- Sprint 1 doctrine complete.
- Sprint 2 AI-first routing skill family available.

## Scope

Update app-description guidance and examples so high-level product intent is captured as:

- durable goals and objectives
- agent roles and responsibilities
- policies, clauses, guardrails, and approval gates
- decisions, exceptions, recommendations, and evidence
- work traces and audit events
- learning, replay/simulation, and governance flows
- outcomes and metrics
- supervision, decision, teaching, digest, and audit UI surfaces

## Primary files likely affected

- `skills/app-descriptions/SKILL.md`
- `skills/app-description-bootstrap/SKILL.md`
- `skills/app-description-intake-router/SKILL.md`
- `skills/app-description-capability-modeling/SKILL.md`
- `skills/app-description-behavior-specification/SKILL.md`
- `skills/app-description-test-specification/SKILL.md`
- `skills/app-description-auth-security/SKILL.md`
- `skills/app-description-observability/SKILL.md`
- `skills/app-description-ui/SKILL.md`
- `docs/internal-app-description-architecture.md`
- `docs/app-description-maintenance-flow.md`

## Acceptance behavior

- New app-description trees created by the pack include AI-first sections when the app has agentic operations.
- Change requests are interpreted against agentic substrate objects before direct CRUD-like scope changes.
- UI descriptions prioritize supervision/governance surfaces when agents perform work.

## Done criteria

- App-description routing and layer guidance consistently mention AI-first substrate objects.
- Existing purchase-request example is not force-fit unless intentionally updated by a separate example task.
- Generated description guidance remains concise and agent-optimized.
