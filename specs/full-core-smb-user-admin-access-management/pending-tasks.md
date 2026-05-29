# Pending Tasks: Full-Core SMB User Admin Access Management

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `full-core-smb: <short task title>`.

## Tasks

### TASK-FCSMB-UAM-00-001: Create User Admin access management queue

- status: done
- source: user approved next User Admin full-core slice after dashboard/invitation foundation
- task brief: specs/full-core-smb-user-admin-access-management/tasks/00-planning/00-create-user-admin-access-management-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
  - specs/full-core-smb-user-admin/pending-tasks.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/full-core-smb-user-admin-access-management/README.md
  - specs/full-core-smb-user-admin-access-management/conversation-capture.md
  - specs/full-core-smb-user-admin-access-management/pending-tasks.md
  - specs/full-core-smb-user-admin-access-management/sprints/*.md
  - specs/full-core-smb-user-admin-access-management/backlog/*.md
  - specs/full-core-smb-user-admin-access-management/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold exists and is committed
- notes:
  - commit message: `full-core-smb: add user admin access management queue`

### TASK-FCSMB-UAM-01-001: Inspect access-management source boundaries and define implementation map

- status: done
- source: specs/full-core-smb-user-admin-access-management/backlog/01-user-admin-access-management-backlog.md
- task brief: specs/full-core-smb-user-admin-access-management/tasks/01-access-management/01-inspect-access-management-boundaries.md
- depends on: [TASK-FCSMB-UAM-00-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-user-admin-access-management/README.md
  - specs/full-core-smb-user-admin-access-management/conversation-capture.md
  - specs/full-core-smb-user-admin-access-management/sprints/01-user-admin-access-management-sprint.md
  - specs/full-core-smb-user-admin-access-management/backlog/01-user-admin-access-management-backlog.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
  - specs/full-core-smb-user-admin/source-boundary-notes.md
- skills:
  - none; repository source-discovery/planning task
- expected outputs:
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - updated specs/full-core-smb-user-admin-access-management/pending-tasks.md with bounded source-edit tasks
  - task briefs for the next backend/frontend implementation tasks
- required checks:
  - `git diff --check`
  - targeted `find`/`rg` commands proving discovered member/status/role/capability source and test boundaries
  - `rg -n "disable|reactivate|role|capability|last-admin|self-disable|idempotency|trace|system_message|runtime validation" specs/full-core-smb-user-admin-access-management`
- done criteria:
  - backend and frontend source-edit tasks can run without guessing source paths or validation commands
  - member status and role/capability scope is bounded to SMB and preserves deterministic authority
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: map user admin access management`

### TASK-FCSMB-UAM-01-002: Implement backend User Admin access management

- status: done
- source: specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
- task brief: specs/full-core-smb-user-admin-access-management/tasks/01-access-management/02-implement-backend-access-management.md
- depends on: [TASK-FCSMB-UAM-01-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-user-admin-access-management/README.md
  - specs/full-core-smb-user-admin-access-management/conversation-capture.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - specs/full-core-smb-user-admin-access-management/sprints/01-user-admin-access-management-sprint.md
  - specs/full-core-smb-user-admin-access-management/backlog/01-user-admin-access-management-backlog.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
- skills:
  - none; repository source-edit task
- expected outputs:
  - updated backend User Admin source under templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/
  - updated backend tests under templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/
  - updated specs/full-core-smb-user-admin-access-management/pending-tasks.md
- required checks:
  - `cd templates/ai-first-saas-starter/backend && mvn test -Dtest=InvitationAndUserAdminServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest`
  - `rg -n "USERADMIN_UPDATE_MEMBER_STATUS|USERADMIN_PREVIEW_ROLE_CHANGE|USERADMIN_CHANGE_MEMBER_ROLES|user_admin\.role_change_preview\.v1|last-admin|self-disable|idempotency|trace-useradmin|system_message|blocked_provider_or_runtime" templates/ai-first-saas-starter/backend/src templates/ai-first-saas-starter/backend/src/test --glob '!**/target/**'`
  - `git diff --check`
- done criteria:
  - backend member status and role/capability runtime actions are deterministic and tested
  - last-admin, self-disable, disabled-user, tenant/customer, no-op/idempotency, audit, and trace guardrails are covered
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: implement backend access management`
  - validation: direct template Maven command is not executable before scaffolding because template placeholders make `pom.xml` invalid; scaffolded equivalent passed with `env -u ADMIN_USERS`: `tools/scaffold-ai-first-saas-starter.sh --target "$tmp" --template-dir templates/ai-first-saas-starter --app-name "FCSMB UAM Backend Check" --app-slug fcsmb-uam-backend-check --base-package ai.first --maven-group-id ai.first && cd "$tmp" && env -u ADMIN_USERS mvn test -Dtest=InvitationAndUserAdminServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest`

### TASK-FCSMB-UAM-01-003: Implement frontend User Admin access management surfaces

- status: done
- source: specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
- task brief: specs/full-core-smb-user-admin-access-management/tasks/01-access-management/03-implement-frontend-access-management.md
- depends on: [TASK-FCSMB-UAM-01-002]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-user-admin-access-management/README.md
  - specs/full-core-smb-user-admin-access-management/conversation-capture.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - specs/full-core-smb-user-admin-access-management/sprints/01-user-admin-access-management-sprint.md
  - specs/full-core-smb-user-admin-access-management/backlog/01-user-admin-access-management-backlog.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
- skills:
  - none; repository source-edit task
- expected outputs:
  - updated starter frontend workstream surfaces/actions/tests under templates/ai-first-saas-starter/frontend/src/
  - root frontend mirror only if repository conventions require synchronization
  - updated specs/full-core-smb-user-admin-access-management/pending-tasks.md
- required checks:
  - `cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/api.contract.test.mjs`
  - `rg -n "USERADMIN_UPDATE_MEMBER_STATUS|USERADMIN_PREVIEW_ROLE_CHANGE|USERADMIN_CHANGE_MEMBER_ROLES|user_admin\.role_change_preview\.v1|user_admin\.member_directory\.v1|system_message|last-admin|self-disable|idempotency|trace-useradmin|blocked_provider_or_runtime|table-to-card" templates/ai-first-saas-starter/frontend/src --glob '!**/node_modules/**'`
  - `git diff --check`
- done criteria:
  - frontend surfaces render member status, role/capability deltas, denials, no-ops, idempotency, and trace links inside the workstream shell
  - action controls submit governed capability requests and remain advisory only
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: implement frontend access management`
  - validation: `cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/api.contract.test.mjs`; `cd templates/ai-first-saas-starter/frontend && npm run typecheck`; required `rg` proof; `git diff --check`

### TASK-FCSMB-UAM-01-004: Validate User Admin access management runtime

- status: done
- source: specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
- task brief: specs/full-core-smb-user-admin-access-management/tasks/01-access-management/04-validate-access-management-runtime.md
- depends on: [TASK-FCSMB-UAM-01-003]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-user-admin-access-management/README.md
  - specs/full-core-smb-user-admin-access-management/conversation-capture.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - specs/full-core-smb-user-admin-access-management/sprints/01-user-admin-access-management-sprint.md
  - specs/full-core-smb-user-admin-access-management/backlog/01-user-admin-access-management-backlog.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
- skills:
  - none; repository validation task
- expected outputs:
  - updated specs/full-core-smb-user-admin-access-management/pending-tasks.md with validation notes or follow-up tasks
  - optional validation notes if useful for verification handoff
- required checks:
  - `cd templates/ai-first-saas-starter/backend && mvn test -Dtest=InvitationAndUserAdminServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest`
  - `cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/api.contract.test.mjs`
  - `rg -n "USERADMIN_UPDATE_MEMBER_STATUS|USERADMIN_PREVIEW_ROLE_CHANGE|USERADMIN_CHANGE_MEMBER_ROLES|user_admin\.role_change_preview\.v1|system_message|last-admin|self-disable|idempotency|trace-useradmin|blocked_provider_or_runtime|runtime validation" templates/ai-first-saas-starter specs/full-core-smb-user-admin-access-management --glob '!**/node_modules/**' --glob '!**/target/**'`
  - `tools/validate-ai-first-saas-starter-fullstack.sh`
  - `git diff --check`
- done criteria:
  - targeted backend/frontend checks pass or bounded blocker tasks are appended
  - fullstack validation passes or an explicit in-scope blocker is queued
  - queue is ready for terminal verification
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: validate access management runtime`
  - validation: direct template backend Maven command is not executable before scaffolding because template placeholders make `pom.xml` invalid; scaffolded equivalent passed with `env -u ADMIN_USERS`: `tools/scaffold-ai-first-saas-starter.sh --target "$tmp" --template-dir templates/ai-first-saas-starter --app-name "FCSMB UAM Validation" --app-slug fcsmb-uam-validation --base-package ai.first --maven-group-id ai.first && cd "$tmp" && env -u ADMIN_USERS mvn test -Dtest=InvitationAndUserAdminServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest`
  - validation: `cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/api.contract.test.mjs`; required `rg` proof; `tools/validate-ai-first-saas-starter-fullstack.sh`; `git diff --check`

### TASK-FCSMB-UAM-99-001: Verify User Admin access management readiness

- status: done
- source: mini-project verification loop
- task brief: specs/full-core-smb-user-admin-access-management/tasks/99-verification/01-verify-user-admin-access-management.md
- depends on:
  - TASK-FCSMB-UAM-01-001
  - TASK-FCSMB-UAM-01-002
  - TASK-FCSMB-UAM-01-003
  - TASK-FCSMB-UAM-01-004
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/full-core-smb-user-admin-access-management/README.md
  - specs/full-core-smb-user-admin-access-management/conversation-capture.md
  - specs/full-core-smb-user-admin-access-management/pending-tasks.md
  - specs/full-core-smb-user-admin-access-management/sprints/*.md
  - specs/full-core-smb-user-admin-access-management/backlog/*.md
  - specs/full-core-smb-user-admin-access-management/tasks/**/*.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/full-core-smb-user-admin-access-management/pending-tasks.md
  - verification notes or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - targeted checks needed to validate implementation-map/source-edit task readiness
- done criteria:
  - mini-project goals have been compared against completed work
  - if ready, next implementation task is runnable without guessing
  - if incomplete, bounded tasks are appended before a new terminal verification task
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: verify access management readiness`
  - verification: compared mini-project README done state, conversation decisions, sprint/backlog goals, implementation map, and completed task notes; no bounded follow-up tasks are needed for this mini-project.
  - validation: scaffolded backend targeted tests passed with `env -u ADMIN_USERS mvn test -Dtest=InvitationAndUserAdminServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest`; starter frontend targeted contract tests and `npm run typecheck` passed; `tools/validate-ai-first-saas-starter-fullstack.sh` passed; targeted `rg` proof for access-management/runtime terms passed; `git diff --check` passed.
