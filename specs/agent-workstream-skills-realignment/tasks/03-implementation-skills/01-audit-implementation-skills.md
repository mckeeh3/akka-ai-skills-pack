# TASK-AWSR-03-001: Audit implementation skills for input-contract drift

## Goal

Audit web UI, agent, endpoint, component, and testing skills to find where implementation can start without a functional-agent/surface/capability contract.

## Required reads

- Sprint 02 review output
- `specs/agent-workstream-skills-realignment/sprints/03-implementation-skills-sprint.md`
- `skills/akka-web-ui-*/SKILL.md`
- `skills/akka-agents/SKILL.md`
- `skills/akka-agent-*/SKILL.md`
- `skills/akka-http-*/SKILL.md`
- `skills/akka-grpc-*/SKILL.md`
- `skills/akka-mcp-*/SKILL.md`
- entity/workflow/view/consumer/timed-action skills
- `docs/agent-workstream-design-review-checklist.md`

## Work

1. Create `specs/agent-workstream-skills-realignment/implementation-skill-gap-matrix.md`.
2. Classify gaps by web UI, agents, endpoints, components, and tests.
3. Identify which skills need the standard input contract added.
4. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`

## Done criteria

- Implementation skill gap matrix exists and prioritizes follow-up updates.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Audit workstream implementation skills`
