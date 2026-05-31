# Final Verification: Requirements Intake Alignment Cleanup

## Result

Status: incomplete; one bounded follow-up repair task is required before terminal verification can close the mini-project.

The completed task group substantially satisfies the sprint goals: active intake, app-description, PRD/spec/backlog, pending queue, docs/examples/UI, and package-reference paths were inventoried, rewritten, demoted, and checked. The final stale-term pass found mostly acceptable hits: explicit anti-pattern language, mechanics-only legacy references, component-level substrate examples, canonical seed surface ids such as `user-admin-user-list`, and template/test fixture strings.

However, final inspection found a small active-guidance drift batch that should be repaired before declaring the mini-project complete.

## Current task group assessment

- Sprint 01 inventory and criteria: complete. `content-inventory.md` covers high-priority files from the conversation capture, and `prune-and-rewrite-criteria.md` defines keep/rewrite/remove/demote rules plus stale-term searches.
- Sprint 02 app-description intake skills: mostly complete. Bootstrap, normalization, router, readiness, generation, and orchestration paths are aligned to five-core starter, workstream/surface/capability, managed runtime, `55-ui`, and runtime validation. One companion capability-modeling skill still prefers purchase-request capability examples and needs repair.
- Sprint 03 PRD/spec/backlog/queue skills: complete for planned scope. Planning and pending flows preserve workstream/surface/capability/AuthContext/audit/runtime context and block stale page/component-only task shapes.
- Sprint 04 docs/examples/UI guidance: complete for planned scope. Purchase-request examples are demoted in main example indexes and active flow docs; UI/API docs are surface-first and workstream-shell oriented.
- Sprint 05 trim/package consistency: mostly complete. Package references do not point to removed files. Remaining material drift is a small active-guidance repair batch listed below.

## Done-state assessment

The mini-project done state is not yet fully achieved because final verification found active guidance that can still mislead future agents:

1. `skills/app-description-capability-modeling/SKILL.md` says `Prefer these example references when present:` and lists purchase-request capability files. This violates the mechanics-only rule for legacy examples. It should prefer AI-first seed capability references and label purchase-request links as conventional mechanics only.
2. `docs/examples/ai-first-saas-seed-app-description/README.md` contains `before the User Admin v0 minimum slice`, which can reintroduce User-Admin-only starter drift. It should say five-core workstream v0 starter/minimum scope.
3. `skills/README.md` lists `../docs/examples/purchase-request-pending-tasks.md` under pending-task execution references without a local mechanics-only label. Broader README context demotes purchase-request examples, but this local reference should be explicit.

No unresolved user question blocks this repair. The required work is bounded enough for one fresh harness task.

## Search commands recorded

```bash
rg -n "User Admin workstream v0|User Admin-only|generic chatbot|chatbot shell|page tree|screen tree|screens first|page-first|screen-first|CRUD-first|CRUD screen|CRUD module|resource-first|/api/<resource>|nav bar|navigation bar|search page|help page|list/detail|list/detail/edit|user-list|user-edit|frontend/src/screens|shopping-cart|purchase-request" skills docs pack templates --glob '!docs/examples/purchase-request-app-description/**'

rg -n "app-description-skills-plan-backlog|purchase-request-prd|purchase-request-app-description|purchase-request-solution-plan|purchase-request-pending-tasks" . --glob '!specs/requirements-intake-alignment-cleanup/**'

rg -n "User Admin workstream v0|User Admin-only" skills docs pack templates
```

## Search disposition summary

- Exact `User Admin workstream v0` / `User Admin-only`: no matches in active searched paths.
- Purchase-request hits: mostly mechanics-only labels or actual legacy example files; one active capability-modeling skill still uses preferred wording and is queued for repair.
- Page/CRUD/chatbot hits: mostly canonical anti-pattern warnings or explicit generated-app blockers.
- `frontend/src/screens` hits: explicit legacy/mechanics quarantine or readiness/generation blockers.
- `shopping-cart` hits: component/substrate mechanics examples or explicit mechanics-only labels.
- `user-admin-user-list` / `user-list` hits: mostly canonical seed surface ids and template/test fixture ids, not stale page taxonomy; the seed README wording about `User Admin v0 minimum slice` is queued for repair.
- Removed file reference check: `app-description-skills-plan-backlog` remains absent outside this RIAC spec; purchase-request references remain only as existing example files, mechanics-only links, historical specs, or the queued ambiguous README/capability skill references.

## Follow-up queue changes

Appended:

- `TASK-RIAC-06-001`: repair the final active-guidance drift batch.
- `TASK-RIAC-99-002`: rerun terminal verification after the repair.
