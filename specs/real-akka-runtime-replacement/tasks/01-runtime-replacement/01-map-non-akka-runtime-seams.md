# Task: Map non-Akka normal-runtime seams

## Objective

Inventory every remaining local-demo, fail-closed, fixture, in-memory-style, mock, fake, canned, model-less, or default substitute path that can participate in normal generated-app runtime, then refine/append implementation tasks with exact Akka replacement targets.

## Required reads

- AGENTS.md
- skills/README.md
- specs/real-akka-runtime-replacement/README.md
- specs/real-akka-runtime-replacement/conversation-capture.md
- specs/real-akka-runtime-replacement/sprints/01-real-akka-runtime-replacement-sprint.md
- specs/real-akka-runtime-replacement/backlog/01-real-akka-runtime-replacement-build-backlog.md
- specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md if present
- templates/ai-first-saas-starter/README.md

## In scope

- Scan backend main source, frontend runtime source, template README, and related docs.
- Classify each finding as `replace-with-Akka-component`, `move-to-test-only`, `delete`, or `docs-only wording cleanup`.
- For each `replace-with-Akka-component`, name the suggested Akka substrate and source files.
- Update `pending-tasks.md` if the initial queue needs more granular tasks.

## Out of scope

- Do not implement replacements in this mapping task except queue/doc refinements.

## Expected outputs

- `specs/real-akka-runtime-replacement/non-akka-runtime-seam-map.md`
- updated `specs/real-akka-runtime-replacement/pending-tasks.md` if needed
- optional added task briefs for newly discovered bounded work

## Required checks

- `git diff --check`
- `rg -n "LocalDemo|InMemory|FailClosed|fixture|Fixture|mock|Mock|fake|Fake|canned|model-less|demo|Demo|AI_FIRST_SAAS_LOCAL_DEMO" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/frontend/src templates/ai-first-saas-starter/README.md skills docs --glob '!**/node_modules/**' --glob '!**/target/**'`
- targeted `find` listing backend main-source `LocalDemo*`, `FailClosed*`, and production fixture client files

## Done criteria

- All remaining substitute paths in production-relevant source are classified.
- Each replacement has a named Akka component target or is explicitly test-only/delete/docs cleanup.
- Follow-up tasks are accurate and bounded.
- Changes and queue update are committed.

## Commit message

`runtime: map non-Akka runtime seams`
