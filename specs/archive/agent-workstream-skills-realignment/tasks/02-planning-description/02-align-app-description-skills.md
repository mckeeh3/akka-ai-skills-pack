# TASK-AWSR-02-002: Align app-description skills with workstream ownership

## Goal

Update app-description skills so `12-workstreams/` is the authoritative application model and `55-ui/` is browser realization.

## Required reads

- `specs/agent-workstream-skills-realignment/planning-description-gap-matrix.md`
- `skills/app-descriptions/SKILL.md`
- `skills/app-description-functional-agent-modeling/SKILL.md`
- `skills/app-description-surface-modeling/SKILL.md`
- `skills/app-description-capability-modeling/SKILL.md`
- `skills/app-description-ui/SKILL.md`
- `docs/internal-app-description-architecture.md`
- `docs/structured-surface-contracts.md`

## Work

1. Apply focused updates to app-description skills/docs identified by the gap matrix.
2. Ensure cross-layer handoffs preserve functional-agent, surface, capability, auth, observability, UI, and test links.
3. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`
- text search over touched files for `12-workstreams`, `55-ui`, `functional agent`, `surface`, and `capability`

## Done criteria

- Description-first guidance clearly preserves workstream/surface ownership.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Align app description workstream ownership`
