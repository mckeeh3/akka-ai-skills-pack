# TASK-WCTC-05-001: Expand User Admin chat tool catalog

## Purpose

Add safe confirmed chat tool coverage for more User Admin actions selected by the inventory.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-catalog-expansion/README.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-inventory.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-coverage-map.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `app-description/domains/core-starter/workstreams/user-admin/**`
- relevant User Admin service/surface/tests

## Skills

- `capability-first-backend`
- `akka-saas-invitation-onboarding`
- `akka-agent-tool-boundaries`
- `akka-agent-work-trace`

## Expected outputs

- expanded User Admin chat tool entries for safe invitation, organization/customer, membership, support-access, or access-review paths selected by inventory
- explicit blocked/approval-gated handling for high-risk paths
- backend/frontend tests as needed
- queue update

## Required checks

- `git diff --check`
- targeted backend User Admin chat tool tests
- frontend tests/typecheck if frontend contracts change

## Done criteria

- No role grant/removal, account lifecycle, support access, last-admin-risk, or destructive action executes unless fully confirmation/approval modeled.
- Invitation and organization/customer actions preserve tenant/customer scope, idempotency, provider/outbox fail-closed behavior, and traces.
- Changes and queue update are committed.
