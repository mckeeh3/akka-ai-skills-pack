# Task: Verify User Admin readiness after typecheck fix

## Objective

Re-run the User Admin child mini-project verification after the fullstack typecheck blockers are fixed.

## Required reads

- `AGENTS.md`
- all files under `specs/full-core-smb-user-admin/`
- umbrella baseline files under `specs/full-core-smb-saas-hardening/` named by the queue

## Expected outputs

- updated `specs/full-core-smb-user-admin/pending-tasks.md`
- verification notes or newly appended bounded tasks plus a new terminal verification task

## Checks

- `git diff --check`
- `tools/validate-ai-first-saas-starter-fullstack.sh`
- targeted `rg` checks needed to prove User Admin contracts and first implementation tasks are ready

## Done criteria

- User Admin child goals are compared against completed work after the validation-blocker task.
- If ready, completion is recorded with no new required planning or validation work.
- If incomplete, bounded follow-up tasks are appended before a new terminal verification task.
- Changes and queue update are committed.

## Commit message

`full-core-smb: verify user admin readiness after typecheck fix`
