# TASK-FSFR-03-001: Repair Governance/Policy lifecycle cluster

## Purpose

Reconcile Governance/Policy lifecycle behavior, tests, browser-smoke expectations, and attention producer behavior.

## Required reads

- `AGENTS.md`
- `specs/full-suite-failure-remediation/README.md`
- `specs/full-suite-failure-remediation/failure-inventory.md`
- Governance/Policy tests and source files named by the inventory
- `app-description/domains/core-starter/workstreams/governance-policy/**`

## Skills

- `ai-first-saas-policy-governance`
- `ai-first-saas-decision-cards`
- `capability-first-backend`
- `akka-workflow-testing`
- `akka-runtime-feature-verification`

## Expected outputs

- implementation/test/current-intent repairs for Governance/Policy lifecycle state transitions and attention behavior
- queue update

## Required checks

- `git diff --check`
- targeted GovernancePolicyService tests
- targeted WorkstreamService Governance/Policy test
- targeted GovernancePolicyBrowserWorkstreamSmokeTest if feasible
- targeted AttentionProducerServiceTest if included in this cluster

## Done criteria

- Accepted governance lifecycle states are consistent across service, workstream surfaces, browser smoke, and attention.
- Policy activation/approval semantics are not weakened.
- Changes and queue update are committed.
