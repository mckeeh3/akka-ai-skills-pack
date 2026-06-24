# Sprint 03: User Admin Proof

## Goal

Prove the end-to-end confirmed chat tool execution path with the motivating User Admin request.

## Target request

```text
create org "Org 1", and invite mckee.hugh@gmail.com as an org admin
```

## Required behavior

- No Organization or invitation is created when the initial chat request is submitted.
- The workstream returns a detailed plan proposal/confirmation surface.
- The plan is bound to the selected User Admin workstream, selected AuthContext, governed tool ids, inputs, idempotency keys, and correlation id.
- Human confirmation executes Organization creation first, then Organization Admin invitation second.
- Each step uses existing backend-authorized capability/action paths where possible.
- Results report created records, skipped steps, failed steps, and recovery surfaces without exposing secrets or hidden scope data.

## Completion signal

Sprint 03 is complete when backend tests prove no pre-confirmation mutation, authorized post-confirmation execution, safe denial, idempotent retry, partial-failure reporting, and audit/work trace evidence for the User Admin proof path.
