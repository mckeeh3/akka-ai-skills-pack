# Task: Verify User Admin readiness

## Objective

Verify that the User Admin child mini-project has enough contract and queued implementation detail to proceed without guessing.

## Required reads

- `AGENTS.md`
- all files under `specs/full-core-smb-user-admin/`
- umbrella baseline files under `specs/full-core-smb-saas-hardening/` named by the queue

## Expected outputs

- updated `specs/full-core-smb-user-admin/pending-tasks.md`
- verification notes or newly appended bounded tasks plus a new terminal verification task

## Checks

- `git diff --check`
- targeted checks needed to prove User Admin contracts and first implementation tasks are ready

## Done criteria

- User Admin child goals are compared against completed work.
- If ready, completion is recorded with no new required planning work.
- If incomplete, bounded follow-up tasks are appended before a new terminal verification task.
- Changes and queue update are committed.
