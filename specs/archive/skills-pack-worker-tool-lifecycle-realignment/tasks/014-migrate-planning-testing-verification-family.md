# TASK-014: Migrate planning, queue, testing, and verification skills

## Scope

Migrate planning/backlog/pending-task/change-reconciliation skills and testing/runtime-verification skills to the three-phase lifecycle and compile/manual-test contracts.

## Required reads

- `skills-pack/docs/app-development-lifecycle.md`
- `skills-pack/docs/app-description-to-code-compile-contract.md`
- `skills-pack/docs/manual-test-reconciliation.md`
- `skills-pack/docs/pending-task-queue.md`
- `skills-pack/skills/akka-prd-to-specs-backlog/SKILL.md`
- `skills-pack/skills/akka-solution-decomposition/SKILL.md`
- `skills-pack/skills/akka-slice-spec-to-backlog/SKILL.md`
- `skills-pack/skills/akka-backlog-to-pending-tasks/SKILL.md`
- `skills-pack/skills/akka-backlog-item-to-task-brief/SKILL.md`
- `skills-pack/skills/akka-do-next-pending-task/SKILL.md`
- `skills-pack/skills/akka-change-request-to-spec-update/SKILL.md`
- `skills-pack/skills/akka-revised-prd-reconciliation/SKILL.md`
- `skills-pack/skills/akka-runtime-feature-verification/SKILL.md`
- `skills-pack/skills/akka-manual-failure-reconciliation/SKILL.md`
- `skills-pack/skills/akka-*-testing/SKILL.md`

## Expected outputs

- Updated planning, queue, testing, and verification skills.
- Queue/task-brief guidance that includes worker/adapter/tool/capability context for feature-bearing tasks.

## Done criteria

- Planning skills produce task briefs/pending tasks with the compile contract where relevant.
- Manual test findings route back to app-description/spec/task reconciliation.
- Testing skills preserve component-specific testing mechanics and do not over-expand into unrelated lifecycle work.
- Runtime verification remains stricter than docs/build/test-only evidence.

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
