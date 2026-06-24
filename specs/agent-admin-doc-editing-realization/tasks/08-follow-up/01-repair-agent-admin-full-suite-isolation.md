# Task AADE-08-001: Repair Agent Admin full-suite smoke isolation

## Scope

Repair the remaining terminal verification blocker found by `AADE-07-001`: `mvn test` fails only in the full-suite run at `AgentAdminBrowserWorkstreamSmokeTest.protectedWorkstreamApiWiresCurrentAgentAdminDocEditingActions:100` because `action-agent-admin-show-agents` returns an empty `rows` list for the `User Admin` filter. The same smoke test passed when run alone, so treat this as an order-dependent state/isolation/runtime seeding gap unless investigation proves a different bounded root cause.

## Required reads

- `specs/agent-admin-doc-editing-realization/verification-notes.md`
- `specs/agent-admin-doc-editing-realization/pending-tasks.md`
- `src/test/java/ai/first/application/coreapp/workstream/AgentAdminBrowserWorkstreamSmokeTest.java`
- affected Agent Admin doc-admin service/workstream action/runtime seeding paths used by `list-agent-doc-agents`
- app-description Agent Admin files only as needed to confirm current SaaS-admin-only doc-editing intent

## Skills

- `akka-runtime-feature-verification`
- `akka-agent-testing`

## Implementation requirements

- Reproduce or isolate why full-suite execution leaves the Agent Admin protected workstream smoke with no `User Admin` rows while isolated execution passes.
- Repair the root cause without broadening Agent Admin access beyond SaaS Owner/Admin and without reintroducing stale governance-console or tenant-scoped Agent Admin behavior.
- Preserve the current protected API smoke coverage for SaaS Owner/Admin list/detail/prompt/edit/save/history/diff/restore/create/delete/trace actions and non-SaaS-admin denial.
- If the root cause is test data pollution, make the smoke setup deterministic and isolated enough for full-suite execution.
- If investigation proves the smoke assertion is too brittle while runtime behavior is correct, update the assertion to a current-intent contract with equivalent or stronger runtime evidence.

## Required checks

```bash
mvn -Dtest='AgentAdminBrowserWorkstreamSmokeTest' test
mvn test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
git diff --check
```

## Done criteria

- `AgentAdminBrowserWorkstreamSmokeTest` passes both isolated and within `mvn test`.
- Full Maven suite no longer fails on the Agent Admin protected workstream smoke.
- Frontend checks remain green if shared contracts or generated static assets are touched.
- Queue update and implementation/test changes are committed together.
