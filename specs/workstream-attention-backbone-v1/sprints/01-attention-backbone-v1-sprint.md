# Sprint 01: Attention Backbone v1

## Objective

Implement a shared, backend-owned workstream attention backbone in the AI-first SaaS starter template and route existing My Account, workstream dashboard, and rail attention use cases through it.

## Scope

- Define v1 contracts for attention items, summaries, lifecycle actions, and source references.
- Implement backend starter domain/service/repository support for shared attention.
- Wire My Account and core workstream dashboard surfaces to read from the shared backbone.
- Expose API/frontend data needed for left-rail and My Account attention.
- Add tests proving authorization, tenant isolation/redaction, lifecycle, aggregation, and surface rendering behavior.

## Source context

- `docs/ai-first-saas-application-architecture.md`
- `docs/capability-first-backend-architecture.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/workstream-dashboard-attention-event-backbone-wip.md` as design provenance, not normative replacement
- `templates/ai-first-saas-starter/backend/**`
- `templates/ai-first-saas-starter/frontend/**`

## Acceptance criteria

- Existing hard-coded My Account personal attention is replaced by a scoped backend attention service/projection path.
- Workstream dashboards can include attention items from the shared backbone while preserving workstream-local semantics.
- Left-rail summaries are backend-derived or have a clear API contract and tests blocking frontend-only substitution.
- Attention item lifecycle actions are authorized, audited/traced, idempotent where applicable, and tenant-safe.
- Starter tests cover success, denial/redaction, tenant isolation, and rendering contracts.

## Handoff notes

Keep v1 intentionally narrow. Advanced event consumers, timed expiry, AutonomousAgent notifications, personal digests, and realtime streams are follow-up extensions unless the verification task appends bounded work.
