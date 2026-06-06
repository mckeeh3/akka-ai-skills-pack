---
name: app-description-intake-router
description: Classify flexible user input into description-maintenance or generation intent, extract candidate app-description deltas, and route to the smallest next app-description skill without forcing the user to know internal taxonomy.
---

# App Description Intake Router

Use this skill when the user gives flexible product input and the harness must decide whether to update app-description artifacts, assess readiness, generate code, ask questions, or route to planning/implementation.

## Required reading

- `../docs/description-first-application-doctrine.md`
- `../docs/app-description-maintenance-flow.md`
- `../docs/internal-app-description-architecture.md`
- `../docs/app-description-skill-output-contracts.md`
- `../docs/generated-saas-canonical-doctrine.md`
- current target `app-description/**`, `specs/**`, and pending question/task queues when present

## Classification

Classify the input as one or more:

- new app/bootstrap request
- capability/behavior/test/security/observability/UI change
- functional-agent/workstream/surface change
- readiness/review request
- generation/implementation request
- revised PRD/change reconciliation
- pending question answer or new uncertainty
- repository-maintenance-only request

Preserve the user's language, domain terms, and constraints. Do not invent missing requirements; ask or queue questions when guessing would affect security, authorization, data scope, agent authority, approval gates, runtime completion, or tests.

## Routing

| Intent | Next skill |
|---|---|
| Normalize ambiguous input | `app-description-input-normalization` |
| Bootstrap new description tree | `app-description-bootstrap` |
| Capability scope/outcomes | `app-description-capability-modeling` |
| Business rules/state transitions/edge cases | `app-description-behavior-specification` |
| Tests/acceptance/negative cases | `app-description-test-specification` |
| Auth/security/trust boundaries | `app-description-auth-security` |
| Observability/audit/metrics/traces | `app-description-observability` |
| Workstream agents | `app-description-functional-agent-modeling` |
| Structured surfaces | `app-description-surface-modeling` |
| Frontend/UI contracts | `app-description-ui` |
| Impact of a revision | `app-description-change-impact` |
| Readiness review | `app-description-readiness-assessment` or `app-description-readiness-summary` |
| Realization/generation | `app-generate-app` after readiness/scope is clear |
| PRD/spec/backlog planning | `akka-prd-to-specs-backlog`, `akka-solution-decomposition`, or queue skills |

## Output contract

Return a compact routing note with:

- interpreted intent and confidence
- affected app-description layers/files
- candidate deltas in neutral terms
- assumptions and unresolved questions
- recommended next skill(s), smallest first
- whether implementation/generation is safe now or blocked pending description work

## Guardrails

- For generated AI-first SaaS, apply secure SaaS foundation and runtime-completion doctrine unless explicitly out of scope.
- Do not treat frontend-only mockups, deterministic agent stand-ins, or fixture-only behavior as generated-app readiness.
- Do not generate a parallel app when the repository root app should be extended.
- Use `domain-specific` or the user's actual domain name; avoid historical placeholder domains.
