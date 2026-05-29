# Task: Implement User Admin backend dashboard and invitation action foundation

## Objective

Implement the backend/runtime half of the first User Admin directory and invitation dashboard foundation in the AI-first SaaS starter.

This task should make the workstream runtime path expose backend-derived User Admin dashboard/list/invitation action behavior with deterministic authorization, tenant filtering, idempotency/no-op handling, audit/trace evidence, and provider/outbox fail-closed signals. It must not implement member status/role mutation expansion, UserAdminAgent normal runtime, or access-review worker runtime beyond existing fail-closed behavior.

## Required reads

- `AGENTS.md`
- `specs/full-core-smb-user-admin/README.md`
- `specs/full-core-smb-user-admin/conversation-capture.md`
- `specs/full-core-smb-user-admin/sprints/01-user-admin-vertical-contract-sprint.md`
- `specs/full-core-smb-user-admin/backlog/01-user-admin-vertical-contract-backlog.md`
- `specs/full-core-smb-user-admin/user-admin-vertical-contracts.md`
- `specs/full-core-smb-user-admin/source-boundary-notes.md`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/InvitationService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/UserAdminService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/UserDirectoryView.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/InvitationView.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/ResendEmailService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/api/workstream/WorkstreamEndpoint.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/api/admin/AdminEndpoint.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/StarterSecurityComponents.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/InvitationAndUserAdminServiceTest.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/AdminEndpointIntegrationTest.java`

## Skills

- none; focused repository source-edit task

## In scope

- Align first-slice backend User Admin workstream surface ids/version semantics with the contract (`user_admin.dashboard.v1`, invitation panel/list semantics, typed system-message denial surfaces where needed).
- Ensure User Admin dashboard/list data is backend-derived from `UserDirectoryView`/`InvitationView` and scoped by selected `AuthContext`.
- Add or complete workstream capability actions for invite, resend, and revoke invitation using deterministic `InvitationService` methods.
- Preserve idempotency requirements for commands and no-op behavior for duplicate/open/revoked/terminal invitations.
- Preserve provider/outbox behavior: local capture is explicitly local/test, production missing config fails closed with safe provider/outbox status and audit/trace evidence.
- Preserve current model-backed `UserAdminAgent` boundary: request/response messages must continue through governed Akka Agent runtime path; do not add canned/model-less success responses.
- Add/adjust backend tests for authorized dashboard/list, invite/resend/revoke, tenant isolation, missing capability/disabled actor where existing test helpers permit, idempotency/no-op, audit/trace refs, and provider/outbox fail-closed behavior.
- If touching `/api/admin` page routes, fix the `AdminEndpoint`/client path mismatch only as far as needed for this backend slice and test it.

## Out of scope

- Frontend renderer/layout changes, except if a minimal backend DTO rename requires a companion contract string update that cannot wait.
- Member disable/reactivate, role preview/change expansion beyond preserving existing behavior.
- UserAdminAgent normal runtime enhancements.
- Durable access-review `AutonomousAgent` implementation.

## Expected outputs

- Updated backend source under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/` and/or `api/`.
- Updated backend tests under `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/`.
- Updated `specs/full-core-smb-user-admin/pending-tasks.md` marking this task done when complete.

## Required checks

- `git diff --check`
- `cd templates/ai-first-saas-starter/backend && mvn test -Dtest=InvitationAndUserAdminServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest`
- `rg -n "USERADMIN_(VIEW_OVERVIEW|LIST_INVITATIONS|SEND_INVITATION|RESEND_INVITATION|REVOKE_INVITATION|LIST_MEMBERS)|user_admin\.dashboard\.v1|user_admin\.invitation_panel\.v1|system_message|blocked_provider_or_runtime|trace-useradmin|/api/workstream/actions|/api/admin/invitations" templates/ai-first-saas-starter/backend templates/ai-first-saas-starter/frontend/src --glob '!**/node_modules/**'`
- `tools/validate-ai-first-saas-starter-fullstack.sh` if source changes claim local API/UI runtime readiness or touch cross-stack bootstrap/action behavior broadly

## Done criteria

- Backend workstream runtime can load an authorized User Admin dashboard/list/invitation foundation without fixture-only data substitution.
- Invitation create/resend/revoke actions route through deterministic services with backend authorization, idempotency/no-op semantics, audit/trace ids, tenant isolation, and safe provider/outbox failure behavior.
- Denials and missing prerequisites produce typed safe system-message or equivalent structured failure surfaces and do not leak tenant/customer data.
- Existing request/response UserAdminAgent behavior remains governed by Akka Agent runtime and provider fail-closed doctrine.
- Required checks pass, or any skipped broad fullstack check is justified in the queue notes with a narrower passing validation set.
- Task changes and queue update are committed.

## Commit message

`full-core-smb: implement user admin backend invitation foundation`
