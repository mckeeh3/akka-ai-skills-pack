# Governance/Policy Workstream Migration Plan

## Scope

Refresh `app-description/domains/core-starter/workstreams/governance-policy/**` to the current skills-pack app-description graph contract.

## Primary intent

Admins and policy operators govern policy lifecycle, approval gates, exceptions, simulations, decision evidence, and policy-bound operations across the SaaS foundation.

## Required graph coverage

- Workstream purpose and lifecycle/alignment state.
- Governance policy human operators, functional agent, and system worker bindings.
- Surfaces for policy catalog, policy detail, draft/edit/review, simulation, approval/denial, exception handling, decision cards, and policy audit evidence.
- Governed tools for policy read/search, draft creation, policy proposal, simulation, approval, activation, rollback, exception review, and trace reads.
- Actor adapters: surface actions, confirmed human chat plans, agent tool calls where bounded, workflow/internal/API calls.
- Capability links to governance-policy lifecycle and related cross-workstream policy enforcement.
- Approval, authority expansion, idempotency, tenant scope, denial, rollback, and versioning semantics.
- Trace obligations for policy changes, simulations, decisions, exceptions, denials, and downstream enforcement evidence.
- Tests and runtime-validation scenarios for policy proposal/review, simulation evidence, denial, activation/rollback, and audit trace evidence.
- Realization files and source-alignment entries.

## Specific refresh questions for the task

- Which policy types are mandatory for the foundation vs examples/placeholders?
- Which policy decisions require human approval before activation?
- Which downstream workstreams must cite governance-policy enforcement traces?

## Expected task output

The task should update only Governance/Policy workstream files plus narrow shared references if required, then mark lifecycle/source-alignment to reflect description changes and implementation alignment.
