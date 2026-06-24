# TASK-020: Replacement terminal verification after optional-security repair

## Scope

Repeat terminal verification after TASK-019. Close the mini-project only if the done state and required checks are satisfied; otherwise append focused follow-up tasks plus another replacement terminal verification task.

## Required reads

- `specs/skills-pack-worker-tool-lifecycle-realignment/README.md`
- `specs/skills-pack-worker-tool-lifecycle-realignment/target-architecture.md`
- `specs/skills-pack-worker-tool-lifecycle-realignment/migration-strategy.md`
- `specs/skills-pack-worker-tool-lifecycle-realignment/verification-notes.md`
- `specs/skills-pack-worker-tool-lifecycle-realignment/pending-tasks.md`
- Completed follow-up task notes/commits since TASK-018.

## Expected outputs

- Updated `specs/skills-pack-worker-tool-lifecycle-realignment/verification-notes.md`.
- Queue closure if verified, or appended focused follow-up tasks plus a replacement terminal verification task if blockers remain.

## Done criteria

- Re-checks the README done state against the target architecture and completed follow-up work.
- Confirms TASK-018/TASK-019 blockers are resolved or explicitly re-queued.
- Runs required pack checks.
- Does not silently close the mini-project with known material gaps.

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- `bash skills-pack/pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh`
