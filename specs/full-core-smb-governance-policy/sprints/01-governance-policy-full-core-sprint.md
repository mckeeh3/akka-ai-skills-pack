# Sprint 01: Governance/Policy Full-Core Map and First Implementation Group

## Objective

Define and queue the Governance/Policy SMB full-core implementation path, then implement the first bounded source-edit group once source boundaries are known.

## Source context

User Admin, Agent Admin, and Audit/Trace now provide the evidence and trace substrate needed for governance decisions. This sprint turns those foundations into governed policy posture, proposal, simulation, approval, and guidance surfaces.

## Ordered work areas

1. Define Governance/Policy vertical slice contracts and implementation map.
2. Implement governance dashboard and policy inventory/detail foundations.
3. Implement proposal draft/submit/read lifecycle.
4. Implement simulation/replay evidence and decision-card lifecycle.
5. Implement GovernancePolicyAgent request/response guidance.
6. Implement policy-impact analysis worker only if deterministic foundations and task semantics justify it.
7. Validate runtime/API/UI behavior and verify mini-project readiness.

## Acceptance criteria

- Implementation tasks are bounded by actual source boundaries.
- Governance/Policy surfaces are typed, trace-linked, authority-scoped, redacted, and visually polished.
- Deterministic services own policy evaluation, proposal lifecycle, simulation, approval, activation, rollback, and audit.
- Model-backed guidance/worker behavior uses governed Akka runtime and provider fail-closed semantics.
- Targeted and broad starter validation pass or blockers are queued.
