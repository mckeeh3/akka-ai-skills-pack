# Sprint 05: Terminal Verification

## Goal

Verify that full-suite failures are resolved or narrowed to explicitly queued blockers.

## Required verification behavior

The terminal verification task must:

1. compare completed work against `failure-inventory.md` and this README done state;
2. run frontend tests and typecheck;
3. run `mvn test` or document why an equivalent targeted/full run is required as a follow-up;
4. record exact remaining failures, if any;
5. append bounded follow-up tasks plus a new terminal verification task when material failures remain.

## Completion signal

The mini-project closes only when the normal full-suite path is clean or any remaining failures are explicitly accepted blockers with queued remediation.
