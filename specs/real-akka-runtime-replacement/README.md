# Real Akka Runtime Replacement

## Purpose

Replace the remaining local-demo, fail-closed, fixture, and in-memory-style normal runtime seams in the AI-first SaaS starter with real Akka components. The stricter user decision is: **all workstream implementation state and behavior must use real Akka runtime components; in-memory/default/mock/fixture substitutes are allowed only in tests.**

## Trigger

A review of workstream implementation guidance and starter code found that earlier remediation renamed or gated in-memory defaults but still retained `LocalDemo*`, `FailClosed*`, fixture runtime clients, and default constructors that can bypass real Akka component-backed behavior. The user explicitly rejected that compromise and asked to replace these defaults with real Akka components.

## Scope

Affected repository areas:

- `templates/ai-first-saas-starter/backend/src/main/java/**`
- `templates/ai-first-saas-starter/backend/src/test/java/**`
- `templates/ai-first-saas-starter/frontend/src/**`
- mirrored root `frontend/src/**` where template/root sync is required
- `templates/ai-first-saas-starter/README.md`
- skills/docs that mention local/demo/default adapters for generated workstream runtime
- existing durability remediation docs when they now understate the stricter bar

## Non-goals

- Do not remove test doubles from test source when they are clearly test-only.
- Do not replace Akka `TestModelProvider` or deterministic unit-test fixtures used only for tests.
- Do not create a broad enterprise persistence platform beyond the starter's stated workstream/foundation scope.
- Do not preserve local/demo runtime switches for generated-app features claimed as implemented.

## Execution model

Execute one task per fresh harness session. Each task must update the queue and create one focused commit before being marked done.

## Read order for future task sessions

1. `AGENTS.md`
2. `skills/README.md`
3. this `README.md`
4. `conversation-capture.md`
5. selected sprint, backlog, queue entry, and task brief
6. the smallest listed source files and skills for the selected task

## Sprint sequence

1. **Runtime replacement planning and source map** — identify every remaining non-Akka normal-runtime seam and convert the stricter decision into bounded implementation tasks.
2. **Backend Akka component replacement** — implement or bind real Akka entities/views/workflows/consumers for identity, invitations, workstream logs, audit traces, governance policy, access-review tasks, agent behavior, and agent traces; remove normal-runtime local/demo/fail-closed repositories.
3. **Frontend fixture quarantine** — move fixture clients/data out of production runtime paths or make them test-only imports; normal UI uses real HTTP/realtime clients.
4. **Doctrine/readiness cleanup and verification** — update docs/skills/templates and prove no normal generated runtime path uses in-memory/default/mock/fixture substitutes.

## Done state

This mini-project is complete when:

- no `LocalDemo*`, `InMemory*`, fixture, mock, or fake adapter is wired or importable as a normal generated-app runtime path;
- every starter backend repository/service used by workstream features is backed by Akka components in normal runtime;
- workstream message logs, audit/work traces, agent behavior records, identity/membership state, invitations/outbox, access-review tasks, and governance policy records survive through Akka component state at the stated scope;
- frontend fixture clients/data exist only in tests or explicitly test-only files and cannot be selected by runtime query/env switches;
- docs/skills/readiness guidance says replacement with real Akka components is mandatory, not optional production hardening;
- rendered starter validation, targeted backend tests, frontend tests/typecheck/build, and source scans pass.

## Open concerns

Some currently present Akka component seams may be only current-state Key Value Entities. Future tasks should decide per capability whether event history is required, and use Event Sourced Entities where audit-grade lifecycle history is part of the feature contract.
