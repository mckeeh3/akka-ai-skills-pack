---
name: app-description-intake-router
description: Classify flexible user input as incremental intent, identify affected current-intent graph nodes, and route to the smallest safe compiler skill without forcing the user to know internal taxonomy.
---

# App Description Intake Router

Use this skill when the user gives flexible product input and the harness must decide whether to normalize, update current-intent artifacts, assess impact/readiness, plan realization, generate code, ask questions, or report a review.

This skill is an **intake/router** in the intent compiler. It should route incremental human intent into current app/domain/workstream/global artifacts and downstream realization paths.

## Lifecycle classification

- Phase: interview.
- Kind: intake/router.
- Family: app-description.
- Living-graph contract: route requests by affected app-description current-intent graph nodes, including workers, execution harnesses, actor adapters, governed tools, capabilities, traces, tests, readiness, and realization implications.
- Build/compile handoff: route planning, generation, code, tests, and validation requests through `../docs/app-description-to-code-compile-contract.md` after current-intent graph sufficiency is clear.

## Required reading

Read only what is needed for the requested route:

- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/app-development-lifecycle.md` when routing between interview/current-intent, build/compile, and manual/runtime-validation concerns
- `../docs/app-worker-tool-model.md` and `../docs/app-description-component-graph.md` when workers, harnesses, tools, capabilities, agents, surfaces, system triggers, or implementation readiness are in scope
- `../docs/app-description-to-code-compile-contract.md` when routing planning, generation, code, tests, or validation requests
- current target `app-description/**`, `specs/**`, and pending question/task queues when present and relevant
- focused companion skill docs only after the route is selected

For generated AI-first SaaS or implementation/generation requests, also preserve the runtime-completion and secure-foundation guidance from the target project's `AGENTS.md` and the focused realization/planning skills.

## Classification

Classify the user increment by intent kind and operation:

- app objective or bootstrap
- domain, workstream, worker, execution-harness, actor-adapter, governed-tool, capability, data/state, behavior, surface, agent, tool, policy, trace, test, or realization change
- auth/security, observability, readiness, review, planning, generation, or code-change request
- revised PRD/change reconciliation
- pending question answer or new ambiguity
- repository-maintenance-only request

Also identify whether the operation is add, refine, replace, remove, reconcile, validate, or repair.

Preserve the user's domain terms and constraints. Do not invent missing requirements; ask or queue questions when guessing would affect tenant/customer scope, authorization, worker responsibility, data scope, actor-adapter selection, agent/tool authority, approval gates, runtime completion, traces, or tests.

## Routing

Prefer the smallest route that can safely compile the increment:

| Intent | Next skill |
|---|---|
| Broad, mixed, or ambiguous input | `app-description-input-normalization` |
| New current-intent graph | `app-description-bootstrap` |
| Workstream, worker, or functional-agent binding | `app-description-functional-agent-modeling` |
| Structured surface/action binding or human surface harness | `app-description-surface-modeling` |
| Capability scope/outcomes/API/governed-tool contract | `app-description-capability-modeling` |
| Business rules/state transitions/edge cases | `app-description-behavior-specification` |
| Tests/acceptance/negative cases | `app-description-test-specification` |
| Auth/security/trust boundaries | `app-description-auth-security` |
| Observability/audit/metrics/traces | `app-description-observability` |
| Frontend/UI routes and rendering contracts | `app-description-ui` |
| Impact of a revision or drift | `app-description-change-impact` |
| Readiness review | `app-description-readiness-assessment` or `app-description-readiness-summary` |
| Realization/generation | `app-generate-app` after current intent and readiness/scope are clear |
| PRD/spec/backlog planning | `akka-prd-to-specs-backlog`, `akka-change-request-to-spec-update`, `akka-solution-decomposition`, or queue skills |

When a reusable global artifact is mentioned, route workstream usage as a binding under `domains/<domain>/workstreams/<workstream>/**` rather than duplicating or changing the global definition unless the global contract itself changes.

## Output contract

Return a compact routing note with:

- interpreted user increment and confidence
- intent kind and operation
- affected app/global/domain/workstream graph nodes or likely paths
- candidate current-state delta, avoiding conversation chronology
- auth/security, policy, trace, test, and realization implications
- worker/tool graph implications: responsible workers, execution harnesses, actor adapters, governed tools, capabilities, and any missing links
- assumptions and unresolved ambiguities, including whether they block action
- recommended next skill or route, smallest first
- whether implementation/generation is safe now or blocked pending current-intent work

## Guardrails

- Current app-description artifacts describe the intended system now; do not preserve superseded alternatives or historical clutter in canonical files.
- Preserve traceability from app objective to domain, workstream, worker, execution harness, actor adapter, governed tool, capability, surface/agent/tool/policy/trace, API/component/frontend route, test, and runtime evidence.
- For generated AI-first SaaS, apply secure SaaS foundation and runtime-completion doctrine unless explicitly out of scope.
- Do not treat frontend-only mockups, deterministic agent stand-ins, fixture-only behavior, or page/endpoint/`agent_tool_call`-only descriptions as generated-app readiness.
- Do not generate a parallel app when the repository root app should be extended.
- Use `domain-specific` or the user's actual domain name; avoid historical placeholder domains.
