# User Admin Production Runtime Hardening Mini-Project

## Purpose

Advance User Admin beyond starter-scope deterministic surfaces and smoke tests by hardening the production runtime paths for:

1. provider-backed invitation delivery;
2. production identity exception recovery workflows;
3. model-backed access-review automation.

This mini-project is root app realization work. It covers app-description/spec alignment, backend Akka components/services, workstream surfaces/actions, provider fail-closed behavior, frontend surfaces as needed, tests, smoke documentation, and verification.

## Current intent

The User Admin structured surface graph and browser/workstream smoke path are complete at starter scope. The next production-readiness increment is to replace or complete remaining deterministic/starter-only seams with governed runtime paths:

- invitations should use the real Resend-backed email/outbox path when configured, expose actionable delivery state, and fail closed when provider configuration is missing;
- identity exceptions should have durable request/review/approval/completion workflows instead of read-only placeholder review panels;
- access review should invoke a governed model-backed User Admin/Access Review agent path when configured, with tool boundaries, prompt/skill/reference loading, durable task progress/results, and fail-closed provider/model behavior.

## Done state

This mini-project is complete when:

1. app-description/User Admin specs explicitly distinguish starter deterministic behavior from production runtime behavior for invitation delivery, identity recovery, and model-backed access review;
2. invitation delivery uses the supported Resend email service/outbox path with retry/failure/recovery surfaces and tests, while missing config returns actionable fail-closed states without fake success;
3. identity exception recovery has durable backend workflow/state for request, review, approval/denial, completion, no-op/replay, and audit, with User Admin surfaces/actions wired to it;
4. access-review automation invokes a concrete governed Akka Agent path when model configuration is active, including model policy, tool boundaries, authorized skills/references where applicable, work traces, and durable access-review task results;
5. provider/model missing or denied states fail closed with typed workstream/system-message surfaces and audit/work traces;
6. frontend/workstream surfaces expose production-safe status, review, approval, and recovery paths without secrets, raw provider ids, raw prompts, or hidden cross-scope data;
7. integration tests cover configured-success and missing-config/denial paths at the smallest reliable local scope, with real-provider smoke tests documented/skipped unless credentials are explicitly present;
8. terminal verification compares completed work against this README, app-description, task criteria, and command evidence, appending follow-up tasks plus a new terminal verification task when gaps remain.

## Non-goals

- Reworking the already-completed User Admin surface conformance or browser smoke projects except where production runtime paths require narrow updates.
- Replacing WorkOS or Resend with another provider.
- Requiring external provider credentials for default CI/local tests.
- Allowing model-less normal runtime behavior for production access-review automation.
- Exposing provider secrets, invitation tokens, raw WorkOS ids, prompts, model provider internals, or hidden tenant/customer facts to ordinary browser users.
- Editing `skills-pack/**` or installed `.agents/**` assets.

## Primary source artifacts

- `app-description/domains/core-starter/workstreams/user-admin/**`
- `app-description/domains/core-starter/capabilities/user-and-access-administration.md`
- `src/main/java/ai/first/application/foundation/email/**`
- `src/main/java/ai/first/application/foundation/invitation/**`
- `src/main/java/ai/first/application/foundation/identity/**`
- `src/main/java/ai/first/application/foundation/agent/**`
- `src/main/java/ai/first/application/coreapp/useradmin/**`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`
- `src/test/java/ai/first/application/coreapp/useradmin/**`
- `src/test/java/ai/first/application/coreapp/workstream/**`
- `frontend/src/workstream/surfaces/**`
- `specs/archive/user-admin-surface-conformance-cleanup/**`
- `specs/user-admin-browser-workstream-smoke/**`

## Task execution rules

Use `specs/user-admin-production-runtime-hardening/pending-tasks.md`. Execute one task per fresh harness context, update task status before implementation edits, run the task's checks, and commit each completed task with the queue update.
