# TASK-FSFR-10-001: Reconcile My Account direct-action validation contract failures

## Purpose

Resolve the residual `mvn test` failures found by `TASK-FSFR-99-001` after the original full-suite failure inventory was mostly remediated.

## Residual failure evidence

Terminal verification command `mvn test` failed with 431 tests run, 2 failures, 0 errors, 2 skipped:

- `WorkstreamServiceTest.myAccountRejectsUnsupportedSelfServiceFieldsBeforeMutation:3261` expected `AuthorizationException`, but no exception was thrown.
- `WorkstreamServiceTest.myAccountSettingsRejectInvalidTimezoneBeforeMutation:3296` expected `AuthorizationException`, but no exception was thrown.

These failures appear related to the `TASK-FSFR-07-001` change that converted My Account unsupported direct-action fields into browser-safe validation-error result surfaces through the protected workstream action path. Reconcile the service-level assertions with the accepted browser-safe runtime contract without weakening authorization or allowing unsupported self-service mutations.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/full-suite-failure-remediation/README.md`
- `specs/full-suite-failure-remediation/conversation-capture.md`
- `specs/full-suite-failure-remediation/pending-tasks.md`
- `specs/full-suite-failure-remediation/failure-inventory.md`
- `specs/full-suite-failure-remediation/verification-notes.md`
- this task brief
- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- My Account source/test files directly named by the failure details if further inspection proves they are involved

## Skills

- `akka-runtime-feature-verification`
- `akka-web-ui-testing`
- `akka-pending-task-queue-maintenance`

## Expected outputs

- Focused implementation, test, and/or current-intent repair for the two residual My Account direct-action validation failures.
- Queue update for this task.
- Commit with message: `full-suite-remediation: reconcile my account validation contract`.

## Required checks

- `git diff --check`
- `mvn -Dtest=WorkstreamServiceTest#myAccountRejectsUnsupportedSelfServiceFieldsBeforeMutation+myAccountSettingsRejectInvalidTimezoneBeforeMutation test`
- Related My Account browser smoke tests if the protected workstream action surface behavior changes.
- `mvn test` if the focused repair is small enough to re-run before terminal verification; otherwise record the exact rationale and leave the full run to `TASK-FSFR-99-002`.

## Done criteria

- Both residual `WorkstreamServiceTest` methods pass.
- Unsupported direct-action fields and invalid My Account settings remain fail-closed: no forbidden field is mutated, browser/API output is safe, and authorization/validation semantics are explicit.
- Browser-safe validation result surfaces introduced for My Account remain compatible with the protected workstream runtime path, or current-intent/test evidence is updated to the accepted behavior.
- Changes and queue update are committed.
