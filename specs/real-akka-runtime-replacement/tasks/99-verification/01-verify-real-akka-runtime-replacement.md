# Task: Verify real Akka runtime replacement

## Objective

Verify that the mini-project's stricter done state is met: normal generated runtime uses real Akka components for workstream/foundation features, and all substitutes are test-only.

## Required reads

- AGENTS.md
- skills/README.md
- specs/real-akka-runtime-replacement/README.md
- specs/real-akka-runtime-replacement/conversation-capture.md
- specs/real-akka-runtime-replacement/pending-tasks.md
- specs/real-akka-runtime-replacement/sprints/01-real-akka-runtime-replacement-sprint.md
- specs/real-akka-runtime-replacement/backlog/01-real-akka-runtime-replacement-build-backlog.md
- specs/real-akka-runtime-replacement/tasks/**/*.md
- specs/real-akka-runtime-replacement/non-akka-runtime-seam-map.md

## Skills

- none; repository verification task

## In scope

- Compare completed work against README done state, conversation decisions, sprint/backlog goals, and task done criteria.
- Run source scans and broad validation.
- Append bounded follow-up tasks before a new terminal verification task if any gap remains.
- Record verification notes.

## Out of scope

- Do not expand into unrelated whole-repository cleanup.

## Expected outputs

- `specs/real-akka-runtime-replacement/verification-notes.md`
- updated `specs/real-akka-runtime-replacement/pending-tasks.md`
- optional new follow-up task briefs if verification finds gaps

## Required checks

- `git diff --check`
- `tools/validate-ai-first-saas-starter-fullstack.sh`
- template frontend tests/typecheck/build
- root frontend tests/typecheck/build if root mirror changed
- scans proving no production runtime substitute paths remain:
  - `rg -n "LocalDemo|Substitute|AI_FIRST_SAAS_LOCAL_DEMO|fixtureWorkstream|FixtureWorkstream|FixtureApiClient|FixtureRealtimeClient|model-less successful|canned guidance" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/frontend/src templates/ai-first-saas-starter/README.md --glob '!**/*.test.mjs' --glob '!**/node_modules/**' --glob '!**/target/**'`
  - `find templates/ai-first-saas-starter/backend/src/main/java -type f | rg -i "LocalDemo|Substitute|FailClosed.*Repository|FailClosed.*Sink"`

## Done criteria

- Mini-project goals and stricter user decision have been verified.
- If incomplete, new bounded tasks and a new terminal verification task are appended.
- If complete, verification notes record checks and no required follow-up work.
- Changes and queue update are committed.

## Commit message

`runtime: verify real Akka runtime replacement`
