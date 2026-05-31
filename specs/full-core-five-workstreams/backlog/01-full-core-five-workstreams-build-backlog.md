# Backlog: Full-Core Five Workstreams

## Goal

Move from the five-core v0 `markdown_response` starter to explicit full-core implementations while preserving the skills-pack decomposition chain:

```text
requirements → workstreams → attention → dashboards → surface graph → internal workstream agent graph → governed-tools → capabilities → Akka substrate → UI/API → traces/tests/local validation
```

## Suggested harness task breakdown

1. **Planning scaffold** — create this mini-project and queue.
2. **Full-core contract matrix** — document all five workstream contracts before coding.
3. **Shared rich surface runtime** — typed rich surface/action envelope and shell request path.
4. **My Account vertical** — profile/settings/context/personal attention surfaces and capabilities.
5. **User Admin vertical** — users, invitations, memberships, roles/scopes, access review.
6. **Agent Admin vertical** — agent definitions, prompt/skill/reference manifests, tool boundaries, behavior test/diff surfaces.
7. **Audit/Trace vertical** — searchable audit/work trace timeline and details.
8. **Governance/Policy vertical** — policy registry, simulations, proposals, decisions/approvals.
9. **Validation and handoff** — full-stack checks, docs, scaffold sync, readiness summary.

## Implementation notes

- Keep initial bootstrap surfaces minimal: five v0 `markdown_response` surfaces only.
- Rich surfaces must be opened by explicit shell requests, surface actions, or full-core APIs.
- Every action, including read/surface-request actions, requires `browserToolId`, `governedToolId`, `capabilityId`, backend auth, audit, and denial shape.
- User-facing workstream turns use request-based Akka `Agent`; durable background/investigation/replay/review work should evaluate Akka `AutonomousAgent`.
- Managed-agent behavior uses governed `AgentDefinition`, prompts, skills, references, manifests, loader tools, `ToolPermissionBoundary`, and traces.
- Completion requires real local runtime/API/UI behavior at task scope.

## Required validation themes

- backend tests: authorization, tenant isolation, idempotency, audit/work traces, projection reads, capability denials;
- frontend tests: typed surface rendering, actions, forbidden/error/stale states, secret boundaries, accessibility where practical;
- full-stack/scaffold checks: starter validates after template rendering;
- optional real-provider smoke where model-backed agent behavior is in scope and provider env is present.
