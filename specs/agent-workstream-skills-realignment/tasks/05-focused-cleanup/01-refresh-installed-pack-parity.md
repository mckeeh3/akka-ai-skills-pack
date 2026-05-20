# TASK-AWSR-05-001: Refresh installed pack and validate source parity

## Goal

Refresh `.agents/` from source and verify installed-pack dogfood output reflects the realigned source skills. Do not commit `.agents/`.

## Required reads

- `install.sh`
- `.gitignore`
- `skills/README.md`
- `skills/akka-solution-decomposition/SKILL.md`
- `skills/akka-web-ui-api-client/SKILL.md`
- `skills/capability-first-backend/SKILL.md`
- `specs/agent-workstream-skills-realignment/final-realignment-review.md`
- `specs/agent-workstream-skills-realignment/sprints/05-focused-cleanup-sprint.md`

## Work

1. Run `./install.sh --location project --project . --force`.
2. Restore source `AGENTS.md` if the installer replaces it: `git checkout -- AGENTS.md`.
3. Spot-check installed `.agents/skills/**` for source terms:
   - `Agent workstream model` in `.agents/skills/akka-solution-decomposition/SKILL.md`;
   - `Generated SaaS input contract` in `.agents/skills/akka-web-ui-api-client/SKILL.md`;
   - workstream-before-capability language in `.agents/skills/capability-first-backend/SKILL.md`.
4. Create `specs/agent-workstream-skills-realignment/installed-pack-parity-check.md` with commands run, result, and any mismatches.
5. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`
- `git status --short` must not show `.agents/` tracked changes

## Done criteria

- Installed dogfood output is refreshed and spot-checked.
- Parity check doc exists.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Refresh installed pack parity check`
