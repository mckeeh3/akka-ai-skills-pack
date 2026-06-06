# TASK-WCC-01-004: Run focused workstream contract consistency sweep

## Objective

Search for and repair remaining drift caused by the workstream contract cleanup across skills-pack docs, examples, templates, and tools.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/workstream-contract-cleanup/README.md`
- `specs/workstream-contract-cleanup/conversation-capture.md`
- `specs/workstream-contract-cleanup/sprints/01-workstream-contract-cleanup-sprint.md`
- `specs/workstream-contract-cleanup/backlog/01-workstream-contract-cleanup-build-backlog.md`
- `specs/workstream-contract-cleanup/tasks/01-contract-schema/02-run-workstream-contract-consistency-sweep.md`
- Changed files from TASK-WCC-01-001 through TASK-WCC-01-003

## Skills

- none; focused consistency/review task

## In scope

- Search for stale mentions of missing/optional `managedAgentDefinitionId`, icon tooltip drift, old attention severity `critical`, source-layout reference guidance, string-only `internalWorkers`, and missing mapping/evidence language.
- Repair focused docs/examples/templates that directly conflict with the new contract.
- Record non-blocking broader recommendations in notes rather than expanding scope.

## Out of scope

- Rewriting unrelated app-description doctrine.
- Implementing new runtime features.
- Broad whole-repository style editing.

## Expected outputs

- Focused consistency edits under `skills-pack/**`.
- Optional notes in `specs/workstream-contract-cleanup/pending-tasks.md` if follow-up work should be appended during verification.

## Required checks

```bash
git diff --check
python3 skills-pack/tools/validate-workstream-manifest.py skills-pack/templates/ai-first-saas-core-app/app-description
bash skills-pack/tools/validate-workstream-contracts.sh skills-pack/templates/ai-first-saas-core-app/app-description
./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --prune
./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check
```

Run pack verification when the edits touch verification tooling or broad docs:

```bash
bash skills-pack/pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh
```

## Done criteria

- No obvious direct drift remains for the accepted decisions.
- Required checks pass or blockers are recorded.
- The queue is updated and the task changes are committed.

## Commit message

```text
skills-pack: sweep workstream contract consistency
```
