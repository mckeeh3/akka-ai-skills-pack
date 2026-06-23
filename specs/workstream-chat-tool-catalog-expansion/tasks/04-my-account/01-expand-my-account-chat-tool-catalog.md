# TASK-WCTC-04-001: Expand My Account chat tool catalog

## Purpose

Add safe confirmed chat tool coverage for My Account actions selected by the inventory.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-catalog-expansion/README.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-inventory.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-coverage-map.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `app-description/domains/core-starter/workstreams/my-account/**`
- relevant frontend My Account surfaces/tests

## Skills

- `capability-first-backend`
- `akka-web-ui-forms-validation`
- `akka-agent-work-trace`

## Expected outputs

- expanded My Account chat tool catalog entries and prompt examples
- backend execution/proposal behavior and tests
- frontend contract updates if needed
- queue update

## Required checks

- `git diff --check`
- targeted backend My Account chat tool tests
- `npm --prefix frontend test -- --run` if frontend contracts change
- `npm --prefix frontend run typecheck` if frontend contracts change

## Done criteria

- My Account expanded paths require exact confirmation and selected AuthContext validation.
- Profile/settings/preference changes remain scoped to the current account and never expose secrets.
- Changes and queue update are committed.
