# TASK-WCTE-07-001: Execute confirmed User Admin chat tool plan

## Purpose

Execute the confirmed User Admin two-step plan transaction-by-transaction after explicit human confirmation.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-execution/README.md`
- `specs/workstream-chat-tool-execution/source-and-design-map.md`
- User Admin proposal implementation from `TASK-WCTE-06-001`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/application/coreapp/useradmin/**`
- `src/main/java/ai/first/application/foundation/invitation/**`
- related tests named in the design map

## Skills

- `capability-first-backend`
- `akka-saas-invitation-onboarding`
- `akka-resend-email-service`
- `akka-agent-work-trace`

## Expected outputs

- Confirmation command/path that validates the exact plan snapshot and human confirmation.
- Step 1 Organization creation using existing governed backend path.
- Step 2 Organization Admin invitation using existing governed backend path and created organization output.
- Per-step result, partial failure, retry/idempotency, and recovery surfaces.
- Queue update.

## Required checks

- `git diff --check`
- targeted backend execution tests

## Done criteria

- No execution happens without confirmation.
- Confirmed execution is idempotent and reauthorizes every step.
- Partial failure reports completed/failed/skipped steps and recovery guidance.
- Changes and queue update are committed.
