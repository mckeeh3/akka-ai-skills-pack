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

### ADIA-FU-AA-001: Execute Agent Admin provider fail-closed test-console runtime-validation scenario

- source task: `TASK-ADIA-02-003`
- source workstream: Agent Admin
- type: runtime-validation execution
- status: proposed-for-consolidation
- evidence basis: `app-description/domains/core-starter/workstreams/agent-admin/realization/source-alignment.md`, `specs/runtime-validation/scenarios/agent-admin/RV-AGENT-ADMIN-001-provider-fail-closed-test-console.md`, `AgentRuntimeServiceTest.java`, `WorkstreamService.java`
- gap: `RV-AGENT-ADMIN-001` is authored but not run. Source/tests cover provider-missing fail-closed and no-side-effect test-console behavior, but no local API/UI/browser run proves the Agent Admin test-console fail-closed surface, loader/tool-boundary denial trace links, idempotent retry, or browser provider-secret boundary.
- expected runnable scope: start the local app with model provider credentials absent or recorded as withheld, authenticate as the SaaS-admin persona, open Agent Admin catalog/detail/test-console, submit a harmless test prompt, verify provider/config blocker and loader/tool-boundary denial copy, capture sanitized network payloads and trace ids, and confirm no provider keys/raw credentials/hidden prompt text appear in browser-visible payloads.
- blocker classification if setup is missing: `auth-setup-blocker` for missing SaaS-admin local mapping; `provider-config-blocker` only if the app cannot be placed in an intentionally missing-provider state; `runtime-validation-gap` until a run record exists.

### ADIA-FU-AA-002: Reconcile Agent Admin canonical governed-tool ids and current surface names

- source task: `TASK-ADIA-02-003`
- source workstream: Agent Admin
- type: source/API/frontend contract alignment gap
- status: proposed-for-consolidation
- evidence basis: Agent Admin `tools/governed-tools.md`, `surfaces/surfaces.md`, `realization/source-alignment.md`, `WorkstreamService.java`, `AgentAdminDocAdministrationService.java`, `workstream-agent-admin-vertical.contract.test.mjs`
- gap: App-description canonical ids (`agent-definition.catalog.read`, `prompt-document.proposal.create`, `agent-test-console.run`, `agent-runtime-trace.read`, etc.) currently coexist with implementation aliases (`list-agent-doc-agents`, `draft-agent-doc-edit`, `activate-agent-doc-version`, `surface-agent-test-console`, etc.). Alignment is partial until aliases are explicitly mapped or implementation/contracts adopt canonical ids and `surface-agent-admin-*` names.
- expected runnable scope: choose a compatibility posture, update API/frontend/app-description mapping consistently, prove each canonical Agent Admin governed-tool id maps to one backend capability/action and one frontend result surface, and preserve legacy aliases only as compatibility wrappers with tests.
- blocker classification if setup is missing: `source-alignment-gap` or `test-gap`; do not mark runtime-ready from alias mapping alone.

### ADIA-FU-AA-003: Implement or verify trace-backed Agent Admin dashboard attention queues

- source task: `TASK-ADIA-02-003`
- source workstream: Agent Admin
- type: implementation/test/runtime-validation gap
- status: proposed-for-consolidation
- evidence basis: Agent Admin `workstream.md`, `behavior.md`, `surfaces/surfaces.md`, `realization/source-alignment.md`, frontend and workstream tests
- gap: Current dashboard source/fixtures exist, but refreshed attention categories for behavior-change proposals, approval-required proposals, provider/config blockers, and denied loader/tool-boundary events are not proven as trace-backed counts. Existing frontend contract still records an empty attention array.
- expected runnable scope: define the backend attention read model and filters, return counts/cards that open proposal/review/test-console/runtime-trace surfaces, test zero and non-zero states, prove SaaS-admin authorization/redaction, and record trace ids without auto-activating behavior.
- blocker classification if setup is missing: `implementation-gap` or `test-gap`.

### ADIA-FU-AA-004: Verify Agent Admin proposal/chat-plan/idempotency/partial-failure runtime path

- source task: `TASK-ADIA-02-003`
- source workstream: Agent Admin
- type: runtime-validation and test gap
- status: proposed-for-consolidation
- evidence basis: `AgentAdminDocAdministrationServiceTest.java`, `AgentAdminBrowserWorkstreamSmokeTest.java`, `WorkstreamServiceTest.java`, Agent Admin `tests/coverage.md`, `tools/governed-tools.md`, `traces/work-traces.md`
- gap: Source/tests cover proposal lifecycle and chat-plan catalog entries, but no current runtime-validation run proves exact `human_chat_tool_plan` confirmation, idempotent no-op retries, stale/high-risk denial recovery, and partial-failure result surfaces through the local UI/API path.
- expected runnable scope: execute or author scenarios for prompt/skill/reference/profile proposal save, review, direct activation denial for authority expansion, low-risk activation, repeated activation no-op/stale recovery, confirmed chat-plan proposal-only execution, and partial-failure reporting with trace links.
- blocker classification if setup is missing: `auth-setup-blocker`, `provider-config-blocker`, or `runtime-validation-gap` as applicable.

### ADIA-FU-AA-005: Verify Agent Admin runtime trace visibility across prompt, skill, reference, provider, and work traces

- source task: `TASK-ADIA-02-003`
- source workstream: Agent Admin
- type: trace/runtime-validation gap
- status: proposed-for-consolidation
- evidence basis: `AgentRuntimeService.java`, `AgentRuntimeLoaderTools.java`, `AgentRuntimeTraceEntity/View/Sink.java`, `AgentAdminDocAdministrationService.runtimeDocReadTraces`, Agent Admin `traces/work-traces.md`, foundation agent tests
- gap: Source/tests prove `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, boundary denial, provider fail-closed, and `AgentWorkTrace` emission in isolated/service paths, but no Agent Admin real UI/API trace drill-in verifies browser-safe metadata, redaction, filters, denied-load categories, and correlation with test-console/provider failures.
- expected runnable scope: run allowed and denied prompt assembly, `readSkill`, `readReferenceDoc`, generated-tool/tool-boundary, provider-missing, and workstream-agent invocations; open Agent Admin runtime trace surfaces; verify rows show metadata/checksums/decision categories and omit full loaded skill/reference bodies and secrets.
- blocker classification if setup is missing: `runtime-validation-gap`, `provider-config-blocker`, or `test-gap`.
