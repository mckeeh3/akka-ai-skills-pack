# App-description skill output contracts

Use this shared reference to keep app-description skills short. Focused skills should apply the relevant contract below and avoid restating the full layer model.

## Bootstrap contract

A bootstrap output creates the smallest authoritative `app-description/**` tree that can be maintained safely. For generated SaaS, it should include or explicitly defer:

- `00-system/app-manifest.md`, `readiness-status.md`, and `generation-policy.md` with scope label (`core app baseline`, `full core`, `Module 1-only / not full core`, or named narrower scope);
- `10-capabilities/**` with secure SaaS foundation plus primary app capabilities;
- `12-workstreams/**` with functional agents, attention/dashboard model, internal-agent candidates, surfaces index, and surface contracts;
- `15-operating-model/**` for goals, authority, policies, decisions, traces, and outcomes when AI-first operation is in scope;
- behavior, tests, auth/security, observability, UI, realization map, and traceability layers sufficient for the declared scope;
- readiness status that distinguishes ready, ready-with-assumptions, not-ready, blocked, and explicitly deferred areas.

Use `templates/ai-first-saas-core-app/app-description/**` and the target project's existing `app-description/**` as structure references.

## Readiness assessment contract

A readiness assessment must report:

- scope label and whether full-core gates apply;
- overall state: `ready`, `ready-with-assumptions`, `not-ready`, or `blocked`;
- blocking gaps by layer: foundation/security, workstreams/surfaces, capabilities/tools, behavior, tests, observability/traces, UI, realization map;
- assumptions that are acceptable for the declared scope;
- unsafe assumptions that must become pending questions or description updates;
- recommended next skill or action.

Full-core readiness is blocked unless the description covers My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy, invitation onboarding, user administration, governed runtime agents, workstream UI, and acceptance/security/agent-governance/frontend tests. Narrower scopes must name every omitted full-core area.

## Delta modeling contract

For capability, behavior, test, auth/security, observability, UI, surface, functional-agent, and change-impact skills, write only the affected layer deltas and cross-links. Every delta should include:

- requested change and source input;
- in-scope/out-of-scope behavior;
- authority, AuthContext/scope, capability/tool ids, DTOs or payload contracts where relevant;
- side effects, idempotency/no-op behavior, policy/approval/escalation, denial behavior, redaction, and traces;
- tests and linked layers that must be updated next;
- open questions and assumptions.

## Intake/normalization contract

Normalize flexible user input into:

- raw summary;
- primary and secondary intents;
- confirmed deltas vs inferred candidate deltas;
- realization/review request flags;
- constraints/preferences;
- open questions;
- smallest next skill sequence.

Do not edit files during pure intake unless the user explicitly asked to maintain the description now.

## Response style

Be concise. Return the changed/created files, readiness/routing result, blockers, and next step. Do not include large boilerplate layer descriptions when a link to this contract is sufficient.
