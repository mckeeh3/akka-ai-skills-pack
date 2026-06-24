# TASK-016: Terminal verification and follow-up queue decision

## Scope

Verify whether the mini-project done state has been achieved. Append follow-up tasks and a new terminal verification task if material gaps remain.

## Required reads

- `specs/skills-pack-worker-tool-lifecycle-realignment/README.md`
- `specs/skills-pack-worker-tool-lifecycle-realignment/conversation-capture.md`
- `specs/skills-pack-worker-tool-lifecycle-realignment/target-architecture.md`
- `specs/skills-pack-worker-tool-lifecycle-realignment/migration-strategy.md`
- `specs/skills-pack-worker-tool-lifecycle-realignment/pending-tasks.md`
- All completed task notes/commits from this mini-project.
- `skills-pack/docs/app-development-lifecycle.md`
- `skills-pack/docs/app-worker-tool-model.md`
- `skills-pack/docs/app-description-component-graph.md`
- `skills-pack/docs/app-description-to-code-compile-contract.md`
- `skills-pack/docs/manual-test-reconciliation.md`
- `skills-pack/skills/README.md`
- `skills-pack/pack/manifest.yaml`

## Expected outputs

- Verification notes in `specs/skills-pack-worker-tool-lifecycle-realignment/verification-notes.md`.
- Queue updates marking this task done only if the mini-project done state is satisfied.
- If gaps remain: appended bounded follow-up tasks and a replacement terminal verification task.

## Done criteria

- Compares completed work against the README done state and target architecture.
- Audits terminology consistency for lifecycle, workers, governed tools, actor adapters, capabilities, surfaces, and Akka components.
- Confirms representative and family migrations are complete or explicitly queued.
- Runs required pack checks.
- Does not silently close the mini-project with known material gaps.

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- `bash skills-pack/pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh` when practical; if not practical, record why and what replacement evidence was used.
