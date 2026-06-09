# App-description skill output contracts

Use this shared reference to keep app-description skills short. Focused skills should apply the relevant contract below and avoid restating the full current-intent graph model.

## Bootstrap contract

A bootstrap output creates the smallest authoritative `app-description/**` tree that can be maintained safely. For generated SaaS, it should include or explicitly defer:

- `app-description/app.md` with objective, operating model, tenant/customer assumptions, readiness posture, generation policy, and scope label (`SaaS Foundation App maintenance/extension`, `business-domain extension`, app-specific feature, or named narrower scope);
- `global/actors/**`, `global/roles/**`, `global/policies/**`, `global/surfaces/**`, `global/agents/**`, `global/tools/**`, and `global/traces/**` for reusable definitions;
- `domains/<domain>/domain.md`, `capabilities/**`, and `data-state/**` for domain-owned capability and state contracts;
- `domains/<domain>/workstreams/<workstream>/**` for access, behavior, surface/agent/tool/policy/trace bindings, tests, and realization maps;
- `domains/<domain>/workstreams/<workstream>/realization/{akka-components.md,frontend-routes.md,api-contracts.md}` sufficient for the declared scope;
- readiness status that distinguishes ready, ready-with-assumptions, not-ready, blocked, and explicitly deferred areas.

Use `current-intent-model.md`, `intent-compiler-skill-contracts.md`, templates under `templates/ai-first-saas-core-app/app-description/**`, and the target project's existing `app-description/**` as structure references. If an existing target still uses legacy numbered directories, update it in place only for the selected scope and map the change back to app/global/domain/workstream nodes rather than introducing a parallel tree.

## Readiness assessment contract

A readiness assessment must report:

- scope label: SaaS Foundation App maintenance/extension, business-domain extension, app-specific feature, or named narrower scope;
- overall state: `ready`, `ready-with-assumptions`, `not-ready`, or `blocked`;
- blocking gaps by current-intent graph area: app/global definitions, domain capabilities/data-state, workstream access/behavior/surfaces/agents/tools/policies/traces/tests, and realization maps;
- assumptions that are acceptable for the declared scope;
- unsafe assumptions that must become pending questions or description updates;
- recommended next skill or action.

SaaS Foundation App readiness requires the built-in foundation domain semantics needed for the requested change: My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy, invitation/onboarding, user administration, governed runtime agents, workstream UI, and acceptance/security/agent-governance/frontend tests where those areas are in scope. Narrower scopes must name every omitted area and must not claim omitted behavior is ready.

## Delta modeling contract

For capability, behavior, test, auth/security, observability, UI, surface, functional-agent, readiness, summary, and change-impact skills, write only the affected current-intent graph node deltas and cross-links. Every delta should include:

- requested change and source input;
- affected app/global/domain/workstream nodes and intended file paths;
- whether a reusable global artifact changes or only a workstream binding changes;
- in-scope/out-of-scope behavior;
- authority, AuthContext/scope, capability/tool ids, DTOs or payload contracts where relevant;
- side effects, idempotency/no-op behavior, policy/approval/escalation, denial behavior, redaction, and traces;
- tests and linked graph nodes that must be updated next;
- realization impact for Akka components, frontend routes, API contracts, specs/backlogs/tasks, and runtime validation when relevant;
- open questions and assumptions.

## Intake/normalization contract

Normalize flexible user input into:

- raw summary;
- primary and secondary intents;
- operation: add, refine, replace, remove, reconcile, validate, or repair;
- affected app/global/domain/workstream nodes;
- confirmed deltas vs inferred candidate deltas;
- realization/review request flags;
- constraints/preferences;
- open questions;
- smallest next skill sequence.

Do not edit files during pure intake unless the user explicitly asked to maintain the description now.

## Response style

Be concise. Return the changed/created files, readiness/routing result, blockers, and next step. Do not include large boilerplate layer descriptions when a link to this contract is sufficient.
