# TASK-AWSR-03-003: Align endpoint, component, and test skills

## Goal

Update the highest-priority endpoint, Akka component, and testing skills so they preserve workstream/surface/capability context before coding.

## Required reads

- `specs/agent-workstream-skills-realignment/implementation-skill-gap-matrix.md`
- endpoint/component/test skills identified by the gap matrix
- `docs/capability-first-backend-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/agent-workstream-application-architecture.md`

## Work

1. Add standard input-contract expectations to selected endpoint skills.
2. Add standard input-contract expectations to selected entity/workflow/view/consumer/timed-action skills.
3. Add test expectations for surface rendering, action-to-capability invocation, authorization, tenant isolation, idempotency, audit/trace, and realtime/stale behavior where applicable.
4. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`

## Done criteria

- Focused implementation skills no longer invite component-first implementation for generated SaaS work.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Align endpoint component test skills`
