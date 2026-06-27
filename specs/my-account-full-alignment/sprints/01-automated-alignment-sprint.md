# Sprint 01: My Account Automated Alignment

## Goal

Complete all non-manual alignment work needed to move My Account from partially aligned to automated-aligned/manual-ready, without claiming manual browser or provider-backed runtime evidence that has not been exercised.

## Work order

1. Split source-alignment and lifecycle tracking.
2. Strengthen backend/API action path tests and repair drift.
3. Verify durable trace/audit semantics.
4. Verify notification center lifecycle and frontend contracts.
5. Verify `human_chat_tool_plan` proposal/confirmation/denial/idempotency.
6. Verify digest fail-closed/provider-runtime behavior.
7. Strengthen frontend automated rendering/secret-boundary tests.
8. Run terminal verification and update readiness.

## Acceptance

- Every task is completed or blocked with a precise reason.
- Terminal verification records whether automated alignment is complete.
- Any remaining items are manual-only or provider-config-only, or are appended as bounded follow-up tasks with a new terminal verification task.
