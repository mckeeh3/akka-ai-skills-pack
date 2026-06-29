# App-description Refresh Migration Sequence

## Phase 0: Scaffold

Create this mini-project, pending queue, backlog, task briefs, and per-workstream migration plan skeletons.

## Phase 1: Shared foundation refresh

1. Audit current shared app/global/domain artifacts against the current skills-pack graph contract.
2. Refresh shared artifacts before workstream-specific revisions:
   - `app-description/app.md`
   - `app-description/global/**`
   - `app-description/domains/core-starter/domain.md`
   - `app-description/domains/core-starter/capabilities/**`
   - `app-description/domains/core-starter/data-state/**`
3. Record shared naming and graph conventions used by all workstreams.

## Phase 2: Per-workstream revisions

Revise one workstream per task. Each task uses its workstream migration plan and updates only that workstream plus narrowly required shared references.

Recommended order:

1. My Account — smallest authenticated member path and `/api/me`/profile context.
2. User Admin — invitation, membership, role/capability, admin audit foundation.
3. Agent Admin — governed agent behavior/profile/prompt/skill/reference/tool-boundary semantics.
4. Governance/Policy — policy lifecycle, approvals, decision evidence.
5. Audit/Trace — cross-workstream trace investigation and audit evidence.

## Phase 3: Consistency and readiness

Run a cross-workstream consistency pass:

- global artifact reuse vs workstream bindings;
- governed-tool ids and capability ids;
- AuthContext, tenant/organization scope, role/capability vocabulary;
- actor adapters and trace sources;
- frontend/API/realization link consistency;
- runtime-validation scenario coverage;
- lifecycle/source-alignment state.

## Phase 4: Terminal verification

Verify the mini-project done state. If gaps remain, append bounded follow-up tasks and a new terminal verification task instead of declaring completion.
