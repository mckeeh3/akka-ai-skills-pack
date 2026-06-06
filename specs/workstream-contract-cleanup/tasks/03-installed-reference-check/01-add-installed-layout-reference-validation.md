# TASK-WCC-01-003: Add installed-layout reference validation

## Objective

Document and validate that skill intra-doc references are intended to resolve in the installed `.agents/skills` layout, preserving references such as `../docs/...`, `../references/...`, `../examples/...`, and `../templates/...`.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/workstream-contract-cleanup/README.md`
- `specs/workstream-contract-cleanup/conversation-capture.md`
- `specs/workstream-contract-cleanup/sprints/01-workstream-contract-cleanup-sprint.md`
- `specs/workstream-contract-cleanup/backlog/01-workstream-contract-cleanup-build-backlog.md`
- `specs/workstream-contract-cleanup/tasks/03-installed-reference-check/01-add-installed-layout-reference-validation.md`
- `skills-pack/install-skills.sh`
- `skills-pack/pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh`
- representative `skills-pack/skills/*/SKILL.md` references

## Skills

- none; skills-pack installer/tooling maintenance task

## In scope

- Add or update documentation stating that skill references are validated against installed `.agents/skills` layout.
- Add or update a lightweight check that installs or stages the pack and verifies intended relative references from `SKILL.md` files resolve after install.
- Integrate the check into the appropriate pack verification script if practical.
- Avoid false positives for prose examples that are not intended as file references.

## Out of scope

- Rewriting skill references to source-layout paths.
- Changing the installer layout.
- Validating external URLs or root app file references unless clearly in scope.

## Expected outputs

- Updated docs and/or tooling under `skills-pack/**`.
- Queue status update in `specs/workstream-contract-cleanup/pending-tasks.md`.

## Required checks

```bash
git diff --check
./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --prune
./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check
```

If integrated, also run:

```bash
bash skills-pack/pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh
```

## Done criteria

- Accepted decisions 7, 8, and 9 are reflected in docs/tooling.
- The check validates installed-layout references without asking maintainers to rewrite correct `../docs/...` references.
- The queue is updated and the task changes are committed.

## Commit message

```text
skills-pack: validate installed skill references
```
