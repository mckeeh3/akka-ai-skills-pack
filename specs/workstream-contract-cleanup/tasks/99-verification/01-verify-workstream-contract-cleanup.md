# TASK-WCC-99-001: Verify workstream contract cleanup completion

## Objective

Verify that the workstream contract cleanup tasks implemented the accepted decisions and that no material gaps remain. Append follow-up tasks before a new terminal verification task if gaps remain.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/workstream-contract-cleanup/README.md`
- `specs/workstream-contract-cleanup/conversation-capture.md`
- `specs/workstream-contract-cleanup/pending-tasks.md`
- `specs/workstream-contract-cleanup/sprints/01-workstream-contract-cleanup-sprint.md`
- `specs/workstream-contract-cleanup/backlog/01-workstream-contract-cleanup-build-backlog.md`
- all task briefs under `specs/workstream-contract-cleanup/tasks/**`
- changed files from completed TASK-WCC tasks

## Skills

- none; repository verification task

## In scope

- Compare completed work against every accepted decision in `conversation-capture.md`.
- Check docs/schema/validator/template consistency for the updated workstream contract.
- Run the named validation commands that are practical locally.
- Append bounded follow-up tasks if material gaps remain, then append a new terminal verification task.
- If complete, record completion in `pending-tasks.md` notes.

## Out of scope

- Whole-repository review unrelated to this mini-project.
- Root app runtime implementation.
- Unbounded polish.

## Expected outputs

- Updated `specs/workstream-contract-cleanup/pending-tasks.md`.
- Optional completion summary or verification notes under `specs/workstream-contract-cleanup/` if useful.
- Follow-up task briefs only if verification finds material gaps.

## Required checks

```bash
git diff --check
python3 skills-pack/tools/validate-workstream-manifest.py skills-pack/templates/ai-first-saas-core-app/app-description
bash skills-pack/tools/validate-workstream-contracts.sh skills-pack/templates/ai-first-saas-core-app/app-description
./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --prune
./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check
bash skills-pack/pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh
```

## Done criteria

- The task group/sprint goals are compared against completed work.
- The mini-project done state in `README.md` is compared against completed work.
- All accepted decisions are either implemented or have explicit follow-up tasks.
- If complete, completion is recorded with no new required work.
- If incomplete, new bounded tasks are appended before a new terminal verification task.
- The queue is updated and the verification changes are committed.

## Commit message

```text
specs: verify workstream contract cleanup
```
