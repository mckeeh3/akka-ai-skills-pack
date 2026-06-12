# User Admin Browser Workstream Smoke Mini-Project

## Purpose

Go beyond starter-scope contract/unit validation by adding a scoped browser/workstream smoke automation project for User Admin structured surfaces. The goal is to prove the conformant User Admin surface graph works through the real local app UI/workstream path, not only through backend service tests or static frontend contract checks.

This mini-project covers root app assets: specs, test harness utilities, optional deterministic local smoke seed/config, browser or DOM smoke tests, documentation, and validation notes.

## Current intent

The completed User Admin surface conformance cleanup established the app-description, backend workstream surface envelopes, frontend structured renderers, and focused conformance tests. The next maturity step is a real UI smoke path that exercises representative User Admin flows through the hosted frontend and workstream API boundary.

The browser smoke suite should validate, at minimum:

1. loading the hosted app/workstream shell locally;
2. selecting/opening the User Admin functional agent;
3. opening User Admin dashboard;
4. navigating dashboard -> User Directory;
5. opening user detail as read-only inspection/task-router;
6. opening dedicated invitation create and membership/support/role task surfaces where deterministic data allows;
7. rendering typed `system_message` for representative denied/blocked/hidden paths;
8. verifying no raw secrets/tokens/provider ids are visible in the browser;
9. preserving keyboard-accessible clickable rows/actions and focus recovery at a smoke-test level.

## Done state

This mini-project is complete when:

1. the smoke-test scope and deterministic local runtime assumptions are documented;
2. any necessary local smoke seed/config is explicit, safe, tenant-scoped, and does not weaken production auth/provider fail-closed behavior;
3. browser/workstream smoke tests run locally against the intended app UI/API path and cover the representative flows above;
4. tests are integrated into an explicit command, script, or documented manual fallback if browser automation cannot be installed in this environment;
5. the smoke suite avoids fixture-only claims for normal runtime behavior while allowing deterministic test data in test mode;
6. validation output records command evidence and any environmental prerequisites;
7. terminal verification compares work against this README and app-description/conformance artifacts, appending follow-up tasks plus a new terminal verification task if gaps remain.

## Non-goals

- Reopening User Admin surface conformance work that is already verified.
- Implementing production-grade model-backed access-review automation.
- Implementing real Resend/WorkOS provider smoke flows that require external credentials.
- Broad app-wide browser automation outside User Admin workstream surfaces.
- Editing `skills-pack/**` or installed `.agents/**` assets.

## Primary source artifacts

- `specs/archive/user-admin-surface-conformance-cleanup/**`
- `specs/archive/user-admin-surface-navigation-tree/**`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`
- `frontend/src/workstream/surfaces/**`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-surfaces.contract.test.mjs`
- `frontend/package.json`
- `frontend/vite.config.ts`

## Task execution rules

Use `specs/user-admin-browser-workstream-smoke/pending-tasks.md`. Execute one task per fresh harness context, update task status before implementation edits, run the task's checks, and commit each completed task with the queue update.
