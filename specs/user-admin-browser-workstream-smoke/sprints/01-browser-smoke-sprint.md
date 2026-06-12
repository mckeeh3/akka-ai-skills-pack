# Sprint 01: User Admin Browser Workstream Smoke

## Goal

Add a repeatable local smoke path that validates representative User Admin structured-surface flows through the hosted frontend/workstream API path.

## Ordered sequence

1. Survey existing frontend/backend test and local app-run capabilities; choose the smallest reliable smoke approach.
2. Define deterministic smoke data/config requirements and implement safe test-only support if needed.
3. Implement browser/workstream smoke automation or an explicit scriptable smoke harness.
4. Document commands and integrate checks into package/Maven scripts where appropriate.
5. Verify the smoke suite against README done state and append follow-up tasks if automation is incomplete.

## Expected validation commands

Likely commands include a subset of:

```bash
env -u ADMIN_USERS mvn test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
git diff --check
```

The selected implementation task may add a new command such as:

```bash
npm --prefix frontend run smoke:user-admin
```

or a repo-level script under `tools/`.
