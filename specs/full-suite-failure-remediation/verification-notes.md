# Full Suite Failure Remediation Verification Notes

Task: `TASK-FSFR-99-001`  
Verifier: terminal verification worker  
Date: 2026-06-23  
Result: blocked / queue extended. Frontend checks are clean, but `mvn test` still has two backend failures in My Account direct-action contract tests.

## Scope and completed-work review

Reviewed the completed task notes in `pending-tasks.md` and the changed-file lists for the remediation commits:

| Task | Commit | Summary | Changed files |
|---|---|---|---|
| `TASK-FSFR-01-001` | `21e72296` | Reproduced and classified the current failures. | `specs/full-suite-failure-remediation/failure-inventory.md`, `specs/full-suite-failure-remediation/pending-tasks.md` |
| `TASK-FSFR-02-001` | `f38821f7` | Repaired frontend surface routing contract evidence. | `frontend/src/workstream-surface-intent-routing.contract.test.mjs`, `specs/full-suite-failure-remediation/pending-tasks.md` |
| `TASK-FSFR-03-001` | `fda6d5e6` | Repaired Governance/Policy lifecycle and browser/runtime surfaces. | `app-description/domains/core-starter/workstreams/governance-policy/tests/coverage.md`, `src/main/java/ai/first/application/foundation/governance/GovernancePolicyService.java`, `src/main/java/ai/first/domain/foundation/governance/GovernancePolicyProposal.java`, `specs/full-suite-failure-remediation/pending-tasks.md` |
| `TASK-FSFR-04-001` | `87ff8213` | Reconciled Agent Admin artifact read/redaction coverage. | `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`, `specs/full-suite-failure-remediation/pending-tasks.md` |
| `TASK-FSFR-05-001` | `ec5ba719` | Repaired User Admin status/support-access browser smoke behavior. | `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `specs/full-suite-failure-remediation/pending-tasks.md` |
| `TASK-FSFR-06-001` | `ad58e7d9` | Repaired `/api/me` bootstrap audit capability mapping. | `src/main/java/ai/first/application/foundation/identity/MeResponse.java`, `src/test/java/ai/first/application/foundation/identity/MeServiceTest.java`, `specs/full-suite-failure-remediation/pending-tasks.md` |
| `TASK-FSFR-07-001` | `a57d2f60` | Repaired runtime seam, autonomous fail-closed, and My Account browser denial surfaces. | `src/main/java/ai/first/application/coreapp/audit/AuditTraceSummaryService.java`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`, `specs/full-suite-failure-remediation/pending-tasks.md` |
| `TASK-FSFR-08-001` | `f6c996dd` | Repaired remaining User Admin browser smoke safety assertion. | `src/test/java/ai/first/application/coreapp/workstream/UserAdminBrowserWorkstreamSmokeTest.java`, `specs/full-suite-failure-remediation/pending-tasks.md` |
| `TASK-FSFR-09-001` | `f66addae` | Re-verified Governance submit attention producer after lifecycle repair. | `specs/full-suite-failure-remediation/pending-tasks.md` |

## Required command evidence

| Command | Result | Evidence summary |
|---|---|---|
| `git diff --check` | passed | No whitespace errors before writing this verification note; repeated after verification notes/queue/task-brief edits and still passed. |
| `npm --prefix frontend test -- --run` | passed | 177 tests passed, 0 failed. |
| `npm --prefix frontend run typecheck` | passed | `tsc --noEmit` completed with no TypeScript errors. |
| `mvn test` | failed | 431 tests run, 2 failures, 0 errors, 2 skipped. Remaining failures are `WorkstreamServiceTest.myAccountRejectsUnsupportedSelfServiceFieldsBeforeMutation` and `WorkstreamServiceTest.myAccountSettingsRejectInvalidTimezoneBeforeMutation`. |

## README done-state comparison

| README done-state bullet | Verification | Status |
|---|---|---|
| Current full-suite failures are reproduced and classified in `failure-inventory.md`. | `TASK-FSFR-01-001` created the inventory with frontend/typecheck/backend command evidence and mapped every reproduced failure to a task. | satisfied |
| Each failure is resolved by either fixing implementation, fixing a stale/incorrect test, or updating current intent plus tests when implementation discovery changes accepted behavior. | All original inventory failures now have targeted repair notes. Terminal `mvn test` shows none of the original inventory failures remain, but two My Account direct-action contract failures remain outside the original inventory and must be reconciled before the mini-project can close. | blocked |
| `npm --prefix frontend test -- --run` passes. | Terminal command passed with 177/177 tests. | satisfied |
| `npm --prefix frontend run typecheck` passes. | Terminal command passed. | satisfied |
| `mvn test` passes, or any remaining failures are explicitly moved to new bounded follow-up tasks with accepted blockers. | Terminal command failed with two My Account `WorkstreamServiceTest` failures. Bounded follow-up task `TASK-FSFR-10-001` and replacement terminal verification `TASK-FSFR-99-002` are appended in the queue. | extended |
| Targeted tests for each repaired cluster pass. | Completed task notes record passing targeted checks for frontend routing, Governance/Policy service/browser/attention, Agent Admin artifact reads, User Admin status/browser smoke, MeService capability mapping, runtime seam/autonomous/My Account browser smoke, remaining browser smoke, and attention producer. | satisfied |
| Security and runtime invariants remain intact: tenant/customer scoping, backend authorization, audit/work traces, provider fail-closed behavior, frontend secret boundaries, deterministic surface routing, and confirmed chat-tool semantics. | Frontend secret-boundary and deterministic routing contracts pass. Backend full-suite no longer reports the original auth/tenant/governance/attention/browser-smoke failures, but the two residual My Account test failures are security-relevant because they disagree on whether unsupported/invalid self-service fields should throw authorization exceptions or return browser-safe validation result surfaces. | blocked |
| Terminal verification records the final command evidence and closes the queue or appends bounded follow-up tasks plus a new terminal verification task. | This note records command evidence and the queue is extended because `mvn test` is not clean. | satisfied |

## Failure-inventory item comparison

| Inventory item | Original failure | Completed-work / terminal evidence | Status |
|---|---|---|---|
| `FSFR-FE-001` | Frontend destructive/approval-gated surface prompt contract drift. | `TASK-FSFR-02-001` updated the stale contract evidence. Terminal frontend suite passed 177/177 including `surface intent router keeps destructive and approval-gated asks on safe fallback`. | resolved |
| `FSFR-BE-001` | Governance/Policy submit lifecycle expected `in_review` but returned `simulation-required`. | `TASK-FSFR-03-001` repaired proposal submit lifecycle. Terminal `mvn test` reports no GovernancePolicyService lifecycle failures. | resolved |
| `FSFR-BE-002` | Governance/Policy simulation decision lifecycle expected `in_review` but returned `simulation-required`. | `TASK-FSFR-03-001` repaired simulation/lifecycle alignment. Terminal `mvn test` reports no GovernancePolicyService simulation lifecycle failure. | resolved |
| `FSFR-BE-003` | Governance/Policy request-changes action expected accepted but returned approval-required. | `TASK-FSFR-03-001` preserved retained-authority action semantics. Terminal `mvn test` reports no request-changes failure. | resolved |
| `FSFR-BE-004` | Governance/Policy rejection action expected accepted but returned approval-required. | `TASK-FSFR-03-001` preserved rejection/activation blocking semantics. Terminal `mvn test` reports no rejection lifecycle failure. | resolved |
| `FSFR-BE-005` | Governance/Policy workstream action surface did not include expected submit state. | `TASK-FSFR-03-001` repaired workstream surfaces and targeted `WorkstreamServiceTest` governance coverage. Terminal `mvn test` reports no governance workstream-surface failure. | resolved |
| `FSFR-BE-006` | Governance/Policy proposal browser runtime expected empty/new draft but received draft. | `TASK-FSFR-03-001` restored browser-safe direct new-draft proposal previews. Terminal `mvn test` reports no GovernancePolicyBrowserWorkstreamSmokeTest proposal failure. | resolved |
| `FSFR-BE-007` | Governance/Policy simulation browser runtime expected `in_review` but returned `simulation-required`. | `TASK-FSFR-03-001` repaired the simulation runtime path. Terminal `mvn test` reports no related browser-smoke failure. | resolved |
| `FSFR-BE-008` | Governance/Policy dashboard browser runtime expected empty/new draft but received draft. | `TASK-FSFR-03-001` aligned dashboard surface state. Terminal `mvn test` reports no related browser-smoke failure. | resolved |
| `FSFR-BE-009` | Governance/Policy decision browser runtime expected `in_review` but returned `simulation-required`. | `TASK-FSFR-03-001` aligned decision runtime path. Terminal `mvn test` reports no related browser-smoke failure. | resolved |
| `FSFR-BE-010` | Governance/Policy outcome browser runtime expected `in_review` but returned `simulation-required`. | `TASK-FSFR-03-001` aligned outcome runtime path. Terminal `mvn test` reports no related browser-smoke failure. | resolved |
| `FSFR-BE-011` | Agent Admin artifact read returned skill manifest diff instead of manifest surface. | `TASK-FSFR-04-001` reconciled artifact-read surface coverage while retaining backend-authoritative redaction. Terminal `mvn test` reports no Agent Admin artifact read failure. | resolved |
| `FSFR-BE-012` | User Admin disable-member action returned no-op instead of accepted. | `TASK-FSFR-05-001` repaired canonical disable/reactivate semantics. Terminal `mvn test` reports no User Admin status-action failure. | resolved |
| `FSFR-BE-013` | User Admin system-message browser safety assertion saw unsafe content. | `TASK-FSFR-05-001` and `TASK-FSFR-08-001` repaired browser safety handling for backend-authored identifiers. Terminal `mvn test` reports no UserAdminBrowserWorkstreamSmokeTest system-message failure. | resolved |
| `FSFR-BE-014` | Hidden support-access grant target was not denied. | `TASK-FSFR-05-001` denied mismatched/hidden target pairs before payload return. Terminal `mvn test` reports no support-access grant browser-smoke failure. | resolved |
| `FSFR-BE-015` | Bootstrap SaaS owner audit capability id mismatch. | `TASK-FSFR-06-001` selected scoped audit capability ids while retaining backend hints. Terminal `mvn test` reports no MeService capability failure. | resolved |
| `FSFR-BE-016` | Production WorkstreamRuntimeAgent seam appeared fake. | `TASK-FSFR-07-001` tightened seam regression to distinguish fail-closed fields from fake runtime evidence. Terminal `mvn test` reports no runtime-seam failure. | resolved |
| `FSFR-BE-017` | Audit/Trace summary worker errored on missing durable Akka binding. | `TASK-FSFR-07-001` returned browser-safe `blocked_provider_or_runtime` when the durable task repository is unbound. Terminal `mvn test` reports no Audit/Trace summary worker failure. | resolved |
| `FSFR-BE-018` | My Account profile browser runtime path errored after unsupported `roleIds` denial. | `TASK-FSFR-07-001` converted unsupported direct-action fields to validation-error result surfaces through the protected path. Terminal `mvn test` reports no MyAccountBrowserWorkstreamSmokeTest profile runtime error, but a related lower-level `WorkstreamServiceTest` now fails because it still expects an `AuthorizationException`. | partially resolved / follow-up required |
| `FSFR-BE-019` | My Account settings browser runtime path errored after unsupported `providerSecret` denial. | `TASK-FSFR-07-001` converted unsupported direct-action fields to validation-error result surfaces through the protected path. Terminal `mvn test` reports no MyAccountBrowserWorkstreamSmokeTest settings runtime error, but a related lower-level invalid-timezone test now fails because it still expects an `AuthorizationException`. | partially resolved / follow-up required |
| `FSFR-BE-020` | Governance submit attention lookup found no value. | `TASK-FSFR-09-001` re-verified submit attention production/resolution after lifecycle repairs. Terminal `mvn test` reports no AttentionProducerService failure. | resolved |

## Residual failures and queued follow-up

`mvn test` still fails with:

1. `WorkstreamServiceTest.myAccountRejectsUnsupportedSelfServiceFieldsBeforeMutation:3261` — expected `AuthorizationException` for unsupported self-service fields, but no exception was thrown.
2. `WorkstreamServiceTest.myAccountSettingsRejectInvalidTimezoneBeforeMutation:3296` — expected `AuthorizationException` for invalid timezone, but no exception was thrown.

These failures appear related to the `TASK-FSFR-07-001` My Account browser-smoke repair that converted unsupported direct-action fields into browser-safe validation result surfaces through the protected action path. The correct accepted behavior needs a bounded reconciliation between service-level assertions and the browser-safe workstream result-surface contract; this verifier did not implement that repair.

Queue changes:

- `TASK-FSFR-99-001` is blocked, not done, because the README done state is not achieved.
- `TASK-FSFR-10-001` is appended for the My Account direct-action validation contract failures.
- `TASK-FSFR-99-002` is appended as the replacement terminal full-suite verification task.

## Result

- Readiness level: `api-smoked` for frontend/typecheck and most backend/runtime paths; not `runtime-ready` for the mini-project because `mvn test` is not clean.
- Mini-project status: blocked/extended.
- Next runnable task: `TASK-FSFR-10-001: Reconcile My Account direct-action validation contract failures`.
