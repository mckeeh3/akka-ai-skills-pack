# TASK-WDA-02-002: Add shell surface alias resolver

## Objective

Add backend-authoritative resolution for common prompt-entered surface/workstream requests beyond `show dashboard`, returning structured surfaces or safe system-message denials.

## Required reads

- mini-project README, conversation capture, sprint 02, backlog, queue entry, and this task brief
- `app-description/70-traceability/workstream-id-map.md`
- `app-description/70-traceability/surface-to-capability-map.md`
- `app-description/55-ui/routes-and-deep-links.md`
- `app-description/55-ui/workstream-panel-and-composer.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`
- `frontend/src/main.tsx`
- `frontend/src/workstream/composer/**`

## Skills

- `agent-workstream-apps`
- `akka-http-endpoint-request-context`
- `akka-web-ui-api-client`
- `akka-web-ui-forms-validation`

## In scope

- Support a bounded alias set for v1, such as `show users`, `open users`, `show audit timeline`, `show agent catalog`, `show governance policies`, and `show notifications` where current surfaces exist.
- Normalize aliases into typed shell requests with canonical prompt feedback and origin metadata.
- Preserve backend authorization, not-found-or-forbidden semantics, selected AuthContext, traces, and target-workstream-only request rendering.
- Add tests for accepted aliases, ambiguous/unknown aliases, and forbidden targets.

## Out of scope

- LLM-based router for arbitrary natural language.
- New surfaces not already present or explicitly needed for alias targets.

## Expected outputs

- Backend resolver or shared resolver path for shell surface aliases.
- Frontend composer integration if needed.
- Tests.

## Required checks

- `git diff --check`
- `mvn test -Dtest=WorkstreamServiceTest`
- targeted frontend composer/workstream tests and `npm --prefix frontend run typecheck` if TypeScript changed

## Done criteria

- Common workstream-local prompt aliases resolve through backend-authoritative shell semantics.
- Unsafe/unauthorized aliases do not leak hidden workstream/surface existence.
- Changes and queue update are committed.

## Commit message

`workstream-align: add shell alias resolver`
