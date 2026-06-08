# TASK-AWSR-03-002: Align web UI and agent implementation skills

## Goal

Update the highest-priority web UI and agent skills so implementation starts from workstream/surface/capability context.

## Required reads

- `specs/agent-workstream-skills-realignment/implementation-skill-gap-matrix.md`
- `skills/akka-web-ui-apps/SKILL.md`
- focused `skills/akka-web-ui-*/SKILL.md` files identified by the gap matrix
- `skills/akka-agents/SKILL.md`
- focused `skills/akka-agent-*/SKILL.md` files identified by the gap matrix
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`

## Work

1. Add or strengthen the standard input contract in the highest-priority web UI companion skills.
2. Add or strengthen functional-agent vs internal-agent distinctions in the highest-priority agent skills.
3. Preserve token efficiency and avoid broad unrelated rewrites.
4. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`
- text search over touched files for `functional agent`, `surface`, `capability`, `AuthContext`, and `trace`

## Done criteria

- Web UI and agent implementation guidance consumes the workstream/surface/capability contract.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Align web UI and agent implementation skills`
