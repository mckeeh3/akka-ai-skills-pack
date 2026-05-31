# Sprint 01: Contracts and Shared Runtime

## Objective

Turn full-core intent into implementation-ready workstream/surface/agent/capability contracts, then add shared runtime substrate for rich surfaces and surface actions.

## Source context

- `docs/requirements-to-workstream-development-process.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/capability-first-backend-architecture.md`
- `docs/workstream-expertise-model.md`
- existing five-core v0 starter and production-ready v0 spec

## Work areas

1. Define full-core contract matrix for all five core workstreams.
2. Add or update app-description/reference docs that show the full-core surface graph and capability map.
3. Add shared rich-surface/action envelope support in backend and frontend.
4. Add shell request routing for explicit full-core surface requests/actions while keeping initial v0 bootstrap minimal.

## Acceptance criteria

- Every planned full-core surface/action has workstream placement, functional-agent owner, payload type, governed capability/governed-tool, authority, traces, and tests.
- Shared runtime supports typed rich surfaces without weakening `markdown_response` v0 behavior.
- The first rich surface is reached through an explicit request/action/API path.
