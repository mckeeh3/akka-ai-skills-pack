# Task AADE-06-002: Repair collateral full-suite authorization smoke blockers

## Scope

Repair the non-Agent-Admin full-suite smoke failures exposed during `AADE-05-001` that prevent terminal validation from closing the mini-project.

## Required reads

- `specs/agent-admin-doc-editing-realization/verification-notes.md`
- `specs/agent-admin-doc-editing-realization/pending-tasks.md`
- affected smoke tests and workstream authorization/runtime paths named in the verification notes
- relevant app-description files for Audit/Trace and My Account only if the repair touches their product behavior

## Skills

- `akka-runtime-feature-verification`
- `akka-web-ui-testing`

## Implementation requirements

- Repair `AuditTraceBrowserWorkstreamSmokeTest.protectedAuditTraceDashboardDeniesUnauthorizedAndDisabledContextsSafelyWhileScopingCustomers` so the SaaS Owner/Admin dashboard scope evidence is correct and browser-safe, or update the test to the accepted current contract if the implementation is already correct.
- Repair `MyAccountBrowserWorkstreamSmokeTest.hostedShellAndProtectedWorkstreamApiExerciseMyAccountDashboardRuntimePath` so My Account workstream actions are not blocked by Agent Admin SaaS-admin authorization changes.
- Verify representative shell/workstream actions still preserve backend authorization and safe denial behavior.
- Do not broaden Agent Admin access beyond SaaS Owner/Admin to make collateral tests pass.

## Required checks

```bash
mvn -Dtest='AuditTraceBrowserWorkstreamSmokeTest,MyAccountBrowserWorkstreamSmokeTest,WorkstreamServiceTest' test
git diff --check
```

Run `mvn test` if the repair changes shared workstream authorization, shell request routing, or bootstrap behavior.

## Done criteria

- The named collateral smoke failures from `AADE-05-001` pass.
- Shared authorization behavior remains fail-closed and browser-safe.
- Targeted checks pass and changes are committed with the queue update.
