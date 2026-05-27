# TASK-WSI-02-001: Add icon metadata contracts and fixtures to starter/reference frontend

## Goal

Add typed workstream icon metadata to reference and starter frontend contracts and fixtures so `/api/me`/bootstrap data carries icon descriptors for core workstreams.

## Required reads

- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `frontend/src/workstream/types/agents.ts`
- `frontend/src/workstream/types/auth.ts`
- `frontend/src/workstream/fixtures/agents.ts`
- `frontend/src/workstream/fixtures/me.ts`
- `frontend/src/workstream.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream/types/agents.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/types/auth.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/agents.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/me.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream.contract.test.mjs`

## Expected edits

- Add `WorkstreamIconDescriptor` to shared workstream/agent types.
- Add `workstreamIcon: WorkstreamIconDescriptor` to `FunctionalAgentSummary` or an equivalent typed property.
- Keep `icon?: string` only as temporary compatibility if needed; prefer descriptor-based rendering in later tasks.
- Seed descriptors for at least:
  - User Admin — users/person/admin visual hint
  - Agent Admin — bot/spark/agent visual hint
  - Audit/Trace — timeline/search visual hint
  - Governance/Policy — shield/checklist visual hint
- Seed descriptors for hidden/disabled examples and My Account where useful, while preserving the rule that My Account is launched from the user tile.
- Update contract tests to assert descriptor fields exist.

## Required checks

```bash
cd frontend && npm test -- --run src/workstream.contract.test.mjs src/workstream-shell.contract.test.mjs
cd templates/ai-first-saas-starter/frontend && npm test -- --run src/workstream.contract.test.mjs src/workstream-shell.contract.test.mjs
git diff --check
rg -n "WorkstreamIconDescriptor|workstreamIcon|iconId|accentColorToken|ariaLabel" frontend/src templates/ai-first-saas-starter/frontend/src
```

## Done criteria

- Type and fixture contracts expose icon metadata for the four top-rail core v0 workstreams.
- Pending task is marked done with a completion note.
- Commit message: `workstream-icons: add icon contracts and fixtures`.
