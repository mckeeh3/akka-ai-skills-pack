# Pending Tasks: User Admin Production Runtime Hardening

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, conversation capture, selected backlog, selected task entry, and task brief before editing.
- Mark exactly one selected task `in-progress` before implementation edits.
- Update this file before finishing the harness response.
- Each completed task must make one focused git commit including that task's intended changes and queue-status update.
- Commit message format: `user-admin-runtime-hardening: <short task title>`.

## Tasks

### TASK-UAPRH-00-001: Create production runtime hardening planning scaffold

- status: done
- source: user requested mini-project for provider-backed invitation delivery hardening, production identity exception recovery workflows, and model-backed access-review automation
- task brief: specs/user-admin-production-runtime-hardening/tasks/00-planning/00-create-production-runtime-hardening-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - specs/user-admin-production-runtime-hardening/README.md
  - specs/user-admin-production-runtime-hardening/conversation-capture.md
  - specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md
  - specs/user-admin-production-runtime-hardening/tasks/00-planning/00-create-production-runtime-hardening-queue.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/user-admin-production-runtime-hardening/README.md
  - specs/user-admin-production-runtime-hardening/conversation-capture.md
  - specs/user-admin-production-runtime-hardening/sprints/01-production-runtime-hardening-sprint.md
  - specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md
  - specs/user-admin-production-runtime-hardening/tasks/**/*.md
  - specs/user-admin-production-runtime-hardening/pending-tasks.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project has captured rationale, current intent, done state, backlog, task briefs, and pending queue
  - first non-done task is runnable without guessing
- notes:
  - vertical contract: planning only for User Admin production runtime hardening; no runtime mutation

### TASK-UAPRH-01-001: Align production runtime contracts

- status: done
- source: specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md
- task brief: specs/user-admin-production-runtime-hardening/tasks/01-invitation-delivery/01-align-production-runtime-contracts.md
- depends on:
  - TASK-UAPRH-00-001
- required reads:
  - AGENTS.md
  - specs/user-admin-production-runtime-hardening/README.md
  - specs/user-admin-production-runtime-hardening/conversation-capture.md
  - specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md
  - app-description/domains/core-starter/workstreams/user-admin/**
  - app-description/domains/core-starter/capabilities/user-and-access-administration.md
  - specs/archive/user-admin-surface-conformance-cleanup/conformance-verification.md
- skills:
  - app-description-surface-modeling
  - app-description-behavior-specification
  - app-description-test-specification
  - akka-resend-email-service
  - akka-agent-component
  - akka-agent-tool-boundaries
- expected outputs:
  - updated app-description/spec notes
  - optional specs/user-admin-production-runtime-hardening/production-runtime-contract.md
- required checks:
  - `git diff --check`
  - focused `rg` proof for Resend, identity exception, access-review, model-backed, fail-closed, and system-message requirements
- done criteria:
  - provider/model/workflow behavior, surfaces, denials, traces, and tests are unambiguous for implementation tasks
- notes:
  - vertical contract: User Admin / `agent-user-admin`; invitation delivery failure, identity exception review, access-review blocker/result attention; invitation, identity, access-review, and system-message surfaces; capabilities `user_admin.*`, identity relink, access review, audit/work-trace; selected AuthContext and role/scope behavior; specs-only validation

### TASK-UAPRH-02-001: Harden provider-backed invitation delivery

- status: done
- source: specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md
- task brief: specs/user-admin-production-runtime-hardening/tasks/01-invitation-delivery/02-harden-provider-backed-invitation-delivery.md
- depends on:
  - TASK-UAPRH-01-001
- required reads:
  - AGENTS.md
  - specs/user-admin-production-runtime-hardening/README.md
  - specs/user-admin-production-runtime-hardening/production-runtime-contract.md if present
  - src/main/java/ai/first/application/foundation/email/**
  - src/main/java/ai/first/application/foundation/invitation/**
  - src/main/java/ai/first/application/coreapp/useradmin/**
  - src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java
  - src/test/java/ai/first/application/coreapp/useradmin/**
- skills:
  - akka-resend-email-service
  - akka-basic-user-admin
  - akka-consumer-testing
  - akka-http-endpoint-testing
- expected outputs:
  - backend invitation delivery/outbox/provider changes
  - focused tests
- required checks:
  - `git diff --check`
  - `env -u ADMIN_USERS mvn -q -Dtest=InvitationAndUserAdminServiceTest,EmailNotificationServiceTest test`
- done criteria:
  - invitation create/resend uses provider/outbox runtime path when configured; missing Resend config fails closed; delivery attempts/retries/failures/revokes/no-ops/idempotency/audit are covered; no tokens/secrets exposed
- notes:
  - vertical contract: User Admin / `agent-user-admin`; invitation delivery failure/stale/pending attention; invitation create/detail/resend/revoke/system-message surfaces; `create-or-resend-invitation` tool; `user_admin.invite_user`, resend/revoke/status capabilities; selected tenant/customer scope; email/outbox/invitation state/admin API substrate

### TASK-UAPRH-02-002: Add invitation delivery surfaces, tests, and smoke docs

- status: done
- source: specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md
- task brief: specs/user-admin-production-runtime-hardening/tasks/01-invitation-delivery/03-invitation-delivery-surfaces-tests-smoke.md
- depends on:
  - TASK-UAPRH-02-001
- required reads:
  - prior task changes for TASK-UAPRH-02-001
  - app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
  - src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java
  - frontend/src/workstream/surfaces/**
  - src/test/java/ai/first/application/coreapp/workstream/UserAdminBrowserWorkstreamSmokeTest.java
  - specs/user-admin-browser-workstream-smoke/smoke-command.md
- skills:
  - akka-web-ui-testing
  - akka-http-endpoint-testing
  - akka-resend-email-service
- expected outputs:
  - workstream/frontend delivery-state rendering updates
  - tests/smoke docs for provider-backed invitation delivery states
- required checks:
  - `git diff --check`
  - `env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest,UserAdminBrowserWorkstreamSmokeTest test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
- done criteria:
  - invitation detail/action result surfaces show delivery status/retry/recovery without secrets; provider-blocked states render typed system messages; smoke docs cover real-provider skip behavior
- notes:
  - vertical contract: User Admin / `agent-user-admin`; invitation delivery failure/retry attention; invitation detail/resend/revoke/system-message surfaces; invitation browser-tools/capabilities; scoped no-hidden-target behavior; workstream/frontend/smoke substrate

### TASK-UAPRH-03-001: Implement durable identity exception recovery backend

- status: done
- source: specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md
- task brief: specs/user-admin-production-runtime-hardening/tasks/02-identity-recovery/01-identity-exception-recovery-backend.md
- depends on:
  - TASK-UAPRH-01-001
- required reads:
  - AGENTS.md
  - specs/user-admin-production-runtime-hardening/README.md
  - production runtime contract from TASK-UAPRH-01-001
  - src/main/java/ai/first/application/foundation/identity/**
  - src/main/java/ai/first/application/coreapp/useradmin/**
  - src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java
  - relevant tests under src/test/java/ai/first/**
- skills:
  - akka-basic-user-admin
  - akka-workos-user-auth
  - akka-workflows
  - akka-kve-application-entity
  - akka-http-endpoint-testing
- expected outputs:
  - durable identity recovery entity/workflow/service changes
  - admin/workstream action routing where needed
  - backend tests
- required checks:
  - `git diff --check`
  - `env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest,InvitationAndUserAdminServiceTest test`
- done criteria:
  - identity exception recovery supports request, review, approve/deny, complete, no-op/replay, hidden/cross-scope denials, redaction, and audit/work traces
- notes:
  - vertical contract: User Admin / `agent-user-admin`; identity exception/relink review attention; identity exception review/status/system-message surfaces; `user_admin.identity_relink.*`; selected tenant/customer and role-specific authority; entity/workflow/service plus admin/workstream API substrate

### TASK-UAPRH-03-002: Wire identity recovery surfaces and tests

- status: done
- source: specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md
- task brief: specs/user-admin-production-runtime-hardening/tasks/02-identity-recovery/02-identity-recovery-surfaces-tests.md
- depends on:
  - TASK-UAPRH-03-001
- required reads:
  - prior task changes for TASK-UAPRH-03-001
  - app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
  - src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java
  - frontend/src/workstream/surfaces/**
  - frontend/src/workstream-user-admin-vertical.contract.test.mjs
  - src/test/java/ai/first/application/coreapp/workstream/**
- skills:
  - akka-web-ui-testing
  - akka-http-endpoint-testing
  - akka-basic-user-admin
- expected outputs:
  - workstream/frontend identity recovery surface changes
  - backend/frontend tests
- required checks:
  - `git diff --check`
  - `env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest,UserAdminBrowserWorkstreamSmokeTest test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
- done criteria:
  - identity exception surface shows durable lifecycle/status and actions; denied/hidden/provider-boundary states are safe; tests prove no raw WorkOS/JWT/provider payload exposure
- notes:
  - vertical contract: User Admin / `agent-user-admin`; identity exception review/recovery attention; identity exception review/workflow/status/system-message; role/scope allow/deny; workstream/frontend/smoke validation
  - completed: wired identity recovery request/read/approve/deny/complete actions through durable backend lifecycle surfaces, frontend controls, browser-safe redaction, WorkstreamService and hosted smoke tests
  - checks: `git diff --check`; `env -u ADMIN_USERS mvn -q compile`; `env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest,UserAdminBrowserWorkstreamSmokeTest test`; `npm --prefix frontend test -- --run`; `npm --prefix frontend run typecheck`

### TASK-UAPRH-04-001: Implement model-backed access-review agent runtime path

- status: pending
- source: specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md
- task brief: specs/user-admin-production-runtime-hardening/tasks/03-access-review-agent/01-model-backed-access-review-agent-runtime.md
- depends on:
  - TASK-UAPRH-01-001
- required reads:
  - AGENTS.md
  - production runtime contract from TASK-UAPRH-01-001
  - src/main/java/ai/first/application/foundation/agent/**
  - src/main/java/ai/first/application/coreapp/useradmin/**
  - src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java
  - src/test/java/ai/first/application/coreapp/useradmin/**
- skills:
  - akka-agent-component
  - akka-agent-tools
  - akka-agent-tool-boundaries
  - akka-agent-model-governance
  - akka-agent-work-trace
  - akka-agent-testing
  - akka-basic-user-admin
- expected outputs:
  - access-review agent runtime integration
  - tool boundary/model config/fail-closed handling
  - durable task progress/result updates
  - tests
- required checks:
  - `git diff --check`
  - `env -u ADMIN_USERS mvn -q -Dtest=UserAdminAccessReviewServiceTest,UserAdminAccessReviewAutonomousAgentTest,WorkstreamServiceTest test`
- done criteria:
  - configured access review invokes concrete governed Akka Agent path; missing model/provider/boundary config fails closed; tool/data/policy/model usage emits traces; result does not mutate access without human review
- notes:
  - vertical contract: User Admin / Access Review agent under `agent-user-admin`; access review running/result/blocker attention; access-review task/status/result/system-message surfaces; user/audit evidence tools, model policy/tool boundary; selected-scope evidence authorization; Akka Agent/service/task/workstream substrate

### TASK-UAPRH-04-002: Add access-review agent surfaces, traces, and tests

- status: pending
- source: specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md
- task brief: specs/user-admin-production-runtime-hardening/tasks/03-access-review-agent/02-access-review-agent-surfaces-traces-tests.md
- depends on:
  - TASK-UAPRH-04-001
- required reads:
  - prior task changes for TASK-UAPRH-04-001
  - app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
  - src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java
  - frontend/src/workstream/surfaces/**
  - src/test/java/ai/first/application/coreapp/workstream/**
  - frontend/src/workstream-user-admin-vertical.contract.test.mjs
- skills:
  - akka-agent-work-trace
  - akka-agent-testing
  - akka-web-ui-testing
  - akka-http-endpoint-testing
- expected outputs:
  - workstream/frontend access-review surface updates
  - trace/evidence rendering changes
  - backend/frontend tests
- required checks:
  - `git diff --check`
  - `env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest,UserAdminAccessReviewAutonomousAgentTest,UserAdminBrowserWorkstreamSmokeTest test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
- done criteria:
  - access-review task surface shows model-backed progress/result or typed blocked state; human accept/reject remains explicit; trace links summarize model/tool/data/policy usage safely; tests cover result and blocked states
- notes:
  - vertical contract: User Admin / `agent-user-admin` plus access-review agent; result/blocker attention; access-review workflow/status/outcome/system-message/trace surfaces; access-review and audit/work-trace capabilities; scoped evidence only; workstream/frontend/agent trace validation

### TASK-UAPRH-05-001: Run integrated User Admin production runtime tests and update smoke coverage

- status: pending
- source: specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md
- task brief: specs/user-admin-production-runtime-hardening/tasks/04-integration/01-integrated-production-runtime-tests.md
- depends on:
  - TASK-UAPRH-02-002
  - TASK-UAPRH-03-002
  - TASK-UAPRH-04-002
- required reads:
  - AGENTS.md
  - specs/user-admin-production-runtime-hardening/README.md
  - outputs from prior implementation tasks
  - src/test/java/ai/first/application/coreapp/workstream/UserAdminBrowserWorkstreamSmokeTest.java
  - specs/user-admin-browser-workstream-smoke/smoke-command.md
  - relevant frontend/backend tests
- skills:
  - akka-http-endpoint-testing
  - akka-web-ui-testing
  - akka-agent-testing
- expected outputs:
  - updated integrated tests/smoke coverage
  - documentation notes for optional provider/model credential smoke behavior
- required checks:
  - `git diff --check`
  - `env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest,InvitationAndUserAdminServiceTest,UserAdminAccessReviewAutonomousAgentTest,UserAdminBrowserWorkstreamSmokeTest test`
  - `npm --prefix frontend run smoke:user-admin-workstream`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
- done criteria:
  - integrated tests cover representative success and fail-closed paths for all three production hardening areas; smoke command remains passing and includes safe updated assertions; optional real-provider/model smoke behavior is documented/skipped without credentials
- notes:
  - vertical contract: User Admin / `agent-user-admin`; invitation delivery, identity exception, access review attention; dashboard/list/detail/task/workflow/system-message surfaces; invitation/outbox, identity, access-review, audit/work-trace, model/tool boundaries; selected-scope hidden-target denials; integrated backend/agent/frontend smoke validation

### TASK-UAPRH-99-001: Verify User Admin production runtime hardening

- status: pending
- source: specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md
- task brief: specs/user-admin-production-runtime-hardening/tasks/99-verification/01-verify-production-runtime-hardening.md
- depends on:
  - TASK-UAPRH-05-001
- required reads:
  - AGENTS.md
  - specs/user-admin-production-runtime-hardening/README.md
  - specs/user-admin-production-runtime-hardening/conversation-capture.md
  - specs/user-admin-production-runtime-hardening/pending-tasks.md
  - specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md
  - outputs from prior tasks
  - specs/archive/user-admin-surface-conformance-cleanup/conformance-verification.md
  - specs/user-admin-browser-workstream-smoke/browser-smoke-verification.md
- skills:
  - app-description-readiness-assessment
  - akka-http-endpoint-testing
  - akka-web-ui-testing
  - akka-agent-testing
- expected outputs:
  - specs/user-admin-production-runtime-hardening/production-runtime-verification.md
  - updated pending queue with done status if complete, or appended follow-up tasks plus a new terminal verification task if gaps remain
- required checks:
  - `git diff --check`
  - `env -u ADMIN_USERS mvn test`
  - `npm --prefix frontend run smoke:user-admin-workstream`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `npm --prefix frontend run build`
- done criteria:
  - verification compares completed work against README done state, backlog, app-description, task criteria, and command evidence; no feature is marked complete based on fixture-only/model-less/provider-less normal runtime behavior; follow-up tasks are appended if gaps remain
- notes:
  - vertical contract: User Admin / `agent-user-admin`; terminal verification for invitation delivery, identity exception, access-review blocker/result production paths; all production hardening surfaces; invitation/outbox, identity recovery, access-review agent/model/tool boundary, audit/work trace; App/Tenant/Customer scoped allow/deny; broad Maven/npm/smoke/build validation
