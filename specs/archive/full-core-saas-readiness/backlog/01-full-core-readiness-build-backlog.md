# Backlog: Full-Core SaaS Readiness

## Goal

Move the core app from five-core starter alignment toward full-core SaaS readiness by closing readiness gaps in bounded, runtime-validated increments.

## Implementation notes

- Prefer evidence inventory before implementation; do not duplicate already completed work.
- Use app-description readiness docs as the source of remaining gaps, but verify against code/tests/runtime evidence.
- Keep every task bounded to one fresh harness session.
- Preserve tenant/customer scoping, backend authorization, audit/work traces, provider fail-closed behavior, frontend secret boundaries, and workstream-first UI.

## Suggested harness task breakdown

1. Define full-core readiness gap contract and evidence matrix.
2. Validate WorkOS/AuthKit runtime and frontend secret boundaries.
3. Complete invitation onboarding with Resend/captured outbox and lifecycle tests.
4. Complete User Admin structured surface/action coverage for users, invitations, roles/memberships, access review, support access, admin audit, and tenant administration.
5. Implement Tenant Administration foundation follow-up for SaaS Owner Tenant create/maintain and Tenant Admin bootstrap/maintenance if not already covered by User Admin readiness.
6. Complete Agent Admin managed-agent lifecycle coverage.
7. Complete governed prompt/skill/reference/manifest/tool-boundary proposal/approval/trace coverage.
8. Complete Audit/Trace search, detail, timeline, redaction/export, and investigation-note coverage.
9. Complete Governance/Policy proposal, impact/simulation, approval, activation/rollback, and outcome coverage.
10. Run full local runtime smoke and update readiness docs.
11. Verify mini-project completion or append follow-up tasks.

## Dependencies

- Readiness contract precedes implementation tasks.
- Auth/invitation/User Admin readiness should precede full managed-agent/governance smoke because admin capabilities and audit are foundational.
- Audit/Trace and Governance/Policy tasks may proceed after the readiness contract if dependencies are already implemented and documented.

## Required checks

Common checks, selected per task:

```bash
git diff --check
mvn test
mvn test -Dtest=<focused tests>
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
```

Local/manual smoke checks should be captured when a task claims runtime behavior.

## Acceptance criteria

- Every full-core gap is either closed with evidence, blocked with precise blocker, or deferred with accepted scope impact.
- No normal runtime path uses fixtures/demo/model-less substitutes for readiness claims.
- App-description readiness docs stay synchronized with implementation state.
- Final verification either records completion or appends bounded follow-up work plus a new terminal verification task.
