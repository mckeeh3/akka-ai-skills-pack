# Sprint 02: Doctrine Consolidation

## Objective

Promote the WIP dashboard/attention/event-backbone/autonomous-task concepts into canonical doctrine or clearly linked canonical sections so intake and planning skills have an authoritative process model.

## Scope

Likely affected docs:
- `docs/workstream-dashboard-attention-event-backbone-wip.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/capability-first-backend-architecture.md`
- `docs/agent-component-selection-guide.md`
- `docs/workstream-ui-reference-architecture.md`

## Work areas

1. Decide whether to create a new canonical process doc or rename/split the WIP.
2. Add dashboard scoping and My Account aggregate exception to canonical workstream doctrine.
3. Add attention-item and attention-summary semantics to canonical surface/UI/capability guidance.
4. Add autonomous task progress/result and notification-to-surface semantics.
5. Add request-based Agent vs AutonomousAgent vs Workflow selection references where requirements processing needs them.

## Acceptance criteria

- Canonical docs clearly describe input → workstreams → attention → dashboards → surfaces → capabilities → Akka components → autonomous tasks/events/traces.
- WIP status is either preserved intentionally or superseded by canonical docs.
- Existing secure SaaS, workstream, surface, capability-first, and autonomous-agent semantics remain consistent.
