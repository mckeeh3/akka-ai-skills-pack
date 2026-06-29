# Implementation Follow-up Queue

This file is populated by workstream alignment tasks and consolidated by `TASK-ADIA-03-001`.

Do not execute entries from this file until they have been converted into runnable pending tasks with required reads, vertical workstream contracts, required checks, and runtime-validation expectations.

## Follow-up tasks

### ADIA-FU-UA-001: Execute User Admin invitation runtime-validation scenario

- source task: `TASK-ADIA-02-002`
- source workstream: User Admin
- type: runtime-validation execution
- status: proposed-for-consolidation
- evidence basis: `app-description/domains/core-starter/workstreams/user-admin/realization/source-alignment.md`, `specs/runtime-validation/scenarios/user-admin/RV-USER-ADMIN-001-invite-user.md`
- gap: `RV-USER-ADMIN-001` is authored but not run. User Admin invitation create/idempotency/provider-state/non-admin denial has source/test/frontend evidence only.
- expected runnable scope: start the local app, authenticate as configured organization-admin/member personas or approved local equivalents, execute invitation create, duplicate invite, and denied member attempt through protected API/UI, and capture result surface plus audit/work trace ids.
- blocker classification if setup is missing: `auth-setup-blocker` for missing WorkOS/AuthKit/local test users; `provider-config-blocker` for live email delivery when Resend credentials are required.

### ADIA-FU-UA-002: Verify User Admin invitee acceptance and selected-context refresh path

- source task: `TASK-ADIA-02-002`
- source workstream: User Admin
- type: auth/runtime-validation gap
- status: proposed-for-consolidation
- evidence basis: `InvitationAcceptanceEndpoint.java`, foundation identity/invitation services, `InvitationAndUserAdminServiceTest.java`, User Admin source-alignment invitation slice
- gap: Source/tests map signed-token invitation acceptance and `/api/me` selected-context refresh, but no current runtime record proves WorkOS/AuthKit invitee identity, token validation, account/membership activation, replay behavior, and safe expired/revoked/mismatched recovery.
- expected runnable scope: run valid acceptance, replay by same account, mismatch/expired/revoked denial, and selected-context refresh through the protected local path; verify no raw tokens, JWT/session values, or provider internals appear in browser payloads/traces.
- blocker classification if setup is missing: `auth-setup-blocker`.

### ADIA-FU-UA-003: Verify User Admin provider fail-closed and configured-provider paths

- source task: `TASK-ADIA-02-002`
- source workstream: User Admin
- type: provider/runtime-validation gap
- status: proposed-for-consolidation
- evidence basis: `ResendEmailService.java`, `RealResendProviderSmokeTest.java`, access-review runtime classes/tests, User Admin source-alignment provider-blocked slices
- gap: Resend invitation delivery and model-backed access-review/User Admin guidance have source/test evidence and fail-closed expectations, but no current configured local provider runtime record. Missing provider/model/outbox configuration must not be counted as normal success.
- expected runnable scope: with provider secrets available, execute Resend invite delivery and access-review model-backed happy path; without provider secrets, execute runtime fail-closed surfaces/messages/traces for invite delivery, chat-plan proposal/model use where applicable, and access-review tasks.
- blocker classification if setup is missing: `provider-config-blocker`.

### ADIA-FU-UA-004: Author or execute User Admin role/status/support/identity/access-review/chat-plan runtime-validation coverage

- source task: `TASK-ADIA-02-002`
- source workstream: User Admin
- type: runtime-validation corpus gap
- status: proposed-for-consolidation
- evidence basis: User Admin `tests/coverage.md`, `traces/work-traces.md`, `realization/source-alignment.md`, backend/frontend tests inventoried by TASK-ADIA-02-002
- gap: Existing runtime-validation corpus covers only the invitation scenario. Role/status/support-access, identity exception, last-admin guard, access-review advisory result, `human_chat_tool_plan` exact confirmation/partial-failure, admin-audit trace, and secret-boundary paths still lack durable scenario/run coverage.
- expected runnable scope: create or execute bounded scenarios for role change success/denial, membership status/last-admin/self-action denial, support-access grant/revoke/expiry, identity exception recovery, access-review provider/fail-closed advisory flow, exact chat-plan proposal/confirmation/denial/partial-failure, and audit/trace reauthorization.
- blocker classification if setup is missing: `auth-setup-blocker`, `provider-config-blocker`, or `runtime-validation-gap` as applicable.
