# Task Brief: Policy Simulation Platform

## Objective

Implement a bounded policy simulation platform beyond the current Governance/Policy impact-analysis worker.

## Required reads

- `specs/core-app-feature-completion/README.md`
- `specs/core-app-feature-completion/sprints/03-workers-governance-sprint.md`
- `specs/core-app-full-stack-readiness/governance-policy-core-module-slice.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/capability-first-backend-architecture.md`
- `skills/ai-first-saas-policy-governance/SKILL.md`
- `skills/ai-first-saas-decision-cards/SKILL.md`
- `skills/akka-workflows/SKILL.md`

## In scope

- Simulation request/result records, scenario inputs, evidence snapshots, risk/impact findings, approval-required recommendations, and result-review surfaces.
- Optional governed worker path only if model-backed behavior uses real `AutonomousAgent` runtime and fail-closed provider behavior.
- Tests for no authority expansion, retained human approval, tenant isolation, evidence redaction, and activation denial before approval.

## Checks

- `git diff --check`
- focused backend governance/simulation tests
- frontend tests/typecheck/build for decision/simulation surfaces
- `tools/validate-ai-first-saas-starter-fullstack.sh`

## Done criteria

- Policy simulation can produce reviewable evidence/results, but cannot activate or mutate authority without explicit backend approval paths.
