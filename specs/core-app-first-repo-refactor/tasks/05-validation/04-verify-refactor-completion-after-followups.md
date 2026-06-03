# Task Brief: Verify Core App First Refactor Completion After Follow-ups

## Objective

Run terminal verification again after root stale-reference repair and active-spec stale-reference classification/repair.

## Required reads

- `AGENTS.md`
- `skills-pack/skills/README.md`
- `specs/core-app-first-repo-refactor/README.md`
- `specs/core-app-first-repo-refactor/conversation-capture.md`
- `specs/core-app-first-repo-refactor/pending-tasks.md`
- `specs/core-app-first-repo-refactor/sprints/*.md`
- `specs/core-app-first-repo-refactor/backlog/*.md`
- `specs/core-app-first-repo-refactor/tasks/**/*.md`
- `specs/core-app-first-repo-refactor/target-layout-and-path-map.md`
- `specs/core-app-first-repo-refactor/asset-migration-inventory.md`
- latest stale-reference classification/verification notes under `specs/core-app-first-repo-refactor/`

## In scope

- Compare all completed work and follow-up results against the README done state.
- Validate root backend and frontend commands selected by the migrated docs.
- Validate skills-pack install/package commands or document a precise blocker.
- Verify no unclassified stale canonical full-app template/scaffold guidance remains.
- Append more bounded tasks before a new terminal verification task if material gaps remain.
- Update `specs/core-app-first-repo-refactor/pending-tasks.md`.

## Out of scope

- Whole-repository review unrelated to the core-app-first refactor.
- Implementing newly found gaps in the same verification task unless they are tiny queue-note fixes.

## Expected outputs

- Updated `specs/core-app-first-repo-refactor/pending-tasks.md`.
- Completion summary, verification notes, or newly appended follow-up tasks.

## Required checks

- `git diff --check`
- `mvn test`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- `npm --prefix frontend run build`
- `./install.sh --location project --project /tmp/akka-install-dry-run --dry-run`
- `bash skills-pack/tools/build-pack.sh --github-repo example/repo --output-dir /tmp/akka-pack-build-check --clean --no-archive`
- `bash skills-pack/tools/verify-opinionated-ai-first-saas-pack.sh`
- stale-reference search proof for `templates/ai-first-saas-starter`, scaffold-first claims, and full-app template claims

## Done criteria

- Current task group and overall mini-project done state are checked.
- If complete, completion is recorded with no new required work.
- If incomplete, new bounded tasks are appended before another terminal verification task.
- Changes and queue update are committed with message `layout: verify core app first refactor followups`.
