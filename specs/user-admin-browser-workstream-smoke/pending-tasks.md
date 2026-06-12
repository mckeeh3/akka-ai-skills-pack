# Pending Tasks: User Admin Browser Workstream Smoke

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, conversation capture, selected backlog, selected task entry, and task brief before editing.
- Mark exactly one selected task `in-progress` before implementation edits.
- Update this file before finishing the harness response.
- Each completed task must make one focused git commit including that task's intended changes and queue-status update.
- Commit message format: `user-admin-browser-smoke: <short task title>`.

## Tasks

### TASK-UABWS-00-001: Create User Admin browser smoke planning scaffold

- status: done
- source: user requested a new scoped mini-project to go beyond starter scope after completing User Admin surface conformance cleanup
- task brief: specs/user-admin-browser-workstream-smoke/tasks/00-planning/00-create-browser-smoke-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - specs/user-admin-browser-workstream-smoke/README.md
  - specs/user-admin-browser-workstream-smoke/conversation-capture.md
  - specs/user-admin-browser-workstream-smoke/backlog/01-user-admin-browser-smoke-build-backlog.md
  - specs/user-admin-browser-workstream-smoke/tasks/00-planning/00-create-browser-smoke-queue.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/user-admin-browser-workstream-smoke/README.md
  - specs/user-admin-browser-workstream-smoke/conversation-capture.md
  - specs/user-admin-browser-workstream-smoke/sprints/01-browser-smoke-sprint.md
  - specs/user-admin-browser-workstream-smoke/backlog/01-user-admin-browser-smoke-build-backlog.md
  - specs/user-admin-browser-workstream-smoke/tasks/**/*.md
  - specs/user-admin-browser-workstream-smoke/pending-tasks.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project has captured rationale, current intent, done state, backlog, task briefs, and pending queue
  - first non-done task is runnable without guessing
- notes:
  - vertical contract: docs/planning only for User Admin browser/workstream smoke automation; no runtime mutation

### TASK-UABWS-01-001: Survey smoke tooling and choose implementation approach

- status: done
- source: specs/user-admin-browser-workstream-smoke/backlog/01-user-admin-browser-smoke-build-backlog.md
- task brief: specs/user-admin-browser-workstream-smoke/tasks/01-scope/01-survey-smoke-tooling.md
- depends on:
  - TASK-UABWS-00-001
- required reads:
  - AGENTS.md
  - specs/user-admin-browser-workstream-smoke/README.md
  - specs/user-admin-browser-workstream-smoke/conversation-capture.md
  - specs/user-admin-browser-workstream-smoke/backlog/01-user-admin-browser-smoke-build-backlog.md
  - specs/user-admin-browser-workstream-smoke/tasks/01-scope/01-survey-smoke-tooling.md
  - frontend/package.json
  - frontend/vite.config.ts
  - pom.xml
  - src/main/java/ai/first/api/coreapp/workstream/StarterFrontendEndpoint.java
  - src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java
  - frontend/src/main.tsx
  - frontend/src/api/**
- skills:
  - akka-web-ui-testing
  - akka-http-endpoint-testing
  - akka-web-ui-frontend-project
- expected outputs:
  - specs/user-admin-browser-workstream-smoke/smoke-tooling-survey.md
  - updated pending queue only if task order or blockers change
- required checks:
  - `git diff --check`
- done criteria:
  - survey identifies existing scripts/dependencies and local app-run options
  - survey chooses automated browser, DOM, HTTP+static, or staged smoke approach with rationale
  - next task can implement deterministic local smoke setup without guessing
- notes:
  - vertical contract: User Admin / `agent-user-admin`; tooling survey for hosted frontend/workstream API smoke traversal; planned dashboard/list/detail/task/system-message surfaces; User Admin capability and selected AuthContext requirements; docs-only validation by diff check
  - completed: wrote `specs/user-admin-browser-workstream-smoke/smoke-tooling-survey.md`; selected staged Akka HTTP preflight + deterministic authorized setup + later browser/manual smoke fallback approach; check passed: `git diff --check`; commit message: `user-admin-browser-smoke: survey smoke tooling`

### TASK-UABWS-02-001: Implement deterministic local smoke setup

- status: pending
- source: specs/user-admin-browser-workstream-smoke/backlog/01-user-admin-browser-smoke-build-backlog.md
- task brief: specs/user-admin-browser-workstream-smoke/tasks/02-backend-fixtures/01-deterministic-local-smoke-setup.md
- depends on:
  - TASK-UABWS-01-001
- required reads:
  - AGENTS.md
  - specs/user-admin-browser-workstream-smoke/README.md
  - specs/user-admin-browser-workstream-smoke/smoke-tooling-survey.md
  - specs/user-admin-browser-workstream-smoke/tasks/02-backend-fixtures/01-deterministic-local-smoke-setup.md
  - relevant backend/frontend files identified by the survey
  - src/main/java/ai/first/application/foundation/identity/**
  - src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java
  - src/test/java/ai/first/**
- skills:
  - akka-basic-user-admin
  - akka-http-endpoint-testing
  - akka-workos-user-auth
- expected outputs:
  - test-only seed/config/helper code or documented no-code setup
  - focused tests proving setup is fail-closed outside test mode if code changes are needed
- required checks:
  - `git diff --check`
  - `env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest test`
- done criteria:
  - smoke setup can produce an authorized User Admin context locally without external WorkOS/Resend/model credentials
  - setup does not allow Tenant/Customer admin production bootstrap through `ADMIN_USERS` or expose hidden data
  - next browser smoke task can run against deterministic data
- notes:
  - vertical contract: User Admin / `agent-user-admin`; test-only setup for browser smoke, no production attention item; deterministic selected AuthContext and tenant/customer authorization preserved; backend workstream/API substrate; audit/trace and redaction remain active

### TASK-UABWS-03-001: Implement User Admin browser/workstream smoke tests

- status: pending
- source: specs/user-admin-browser-workstream-smoke/backlog/01-user-admin-browser-smoke-build-backlog.md
- task brief: specs/user-admin-browser-workstream-smoke/tasks/03-browser-smoke/01-implement-user-admin-browser-smoke.md
- depends on:
  - TASK-UABWS-02-001
- required reads:
  - AGENTS.md
  - specs/user-admin-browser-workstream-smoke/README.md
  - specs/user-admin-browser-workstream-smoke/smoke-tooling-survey.md
  - specs/user-admin-browser-workstream-smoke/tasks/03-browser-smoke/01-implement-user-admin-browser-smoke.md
  - files changed by TASK-UABWS-02-001
  - frontend/src/workstream/surfaces/**
  - frontend/src/main.tsx
  - src/main/java/ai/first/api/coreapp/workstream/**
- skills:
  - akka-web-ui-testing
  - akka-http-endpoint-testing
  - akka-web-ui-accessibility-responsive
- expected outputs:
  - browser/workstream smoke test code or script
  - smoke assertions for User Admin dashboard/list/detail/task/system-message flows
- required checks:
  - smoke command selected by survey
  - `git diff --check`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
- done criteria:
  - smoke loads hosted app/workstream shell, opens User Admin and User Directory, opens read-only detail, opens at least one task surface, validates typed system-message denied/blocked path, and asserts no visible raw secrets/tokens/provider ids
- notes:
  - vertical contract: User Admin / `agent-user-admin`; browser smoke for dashboard attention/action routing and task outcomes; dashboard -> users -> row detail -> task/system-message; workstream browser-tool action path; deterministic AuthContext; hosted frontend/workstream endpoint substrate; trace/redaction checks

### TASK-UABWS-04-001: Document and integrate User Admin smoke command

- status: pending
- source: specs/user-admin-browser-workstream-smoke/backlog/01-user-admin-browser-smoke-build-backlog.md
- task brief: specs/user-admin-browser-workstream-smoke/tasks/04-ci-docs/01-document-and-integrate-smoke-command.md
- depends on:
  - TASK-UABWS-03-001
- required reads:
  - AGENTS.md
  - specs/user-admin-browser-workstream-smoke/README.md
  - specs/user-admin-browser-workstream-smoke/smoke-tooling-survey.md
  - specs/user-admin-browser-workstream-smoke/tasks/04-ci-docs/01-document-and-integrate-smoke-command.md
  - smoke implementation from TASK-UABWS-03-001
  - frontend/package.json
  - README.md or relevant docs if touched by prior tasks
- skills:
  - akka-web-ui-testing
  - akka-web-ui-frontend-project
- expected outputs:
  - script/package command or repo tool entry for smoke test
  - documentation for prerequisites, command, expected artifacts, and troubleshooting
- required checks:
  - smoke command from TASK-UABWS-03-001
  - `git diff --check`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
- done criteria:
  - future fresh harness session can run smoke without rediscovering setup; docs explain `ADMIN_USERS` caveat and provider credential non-requirements; smoke command is not conflated with fixture-only tests
- notes:
  - vertical contract: User Admin / `agent-user-admin`; docs/integration for smoke validation; local UI/API smoke command; deterministic smoke AuthContext; expected trace/redaction assertions documented

### TASK-UABWS-99-001: Verify User Admin browser workstream smoke mini-project

- status: pending
- source: specs/user-admin-browser-workstream-smoke/backlog/01-user-admin-browser-smoke-build-backlog.md
- task brief: specs/user-admin-browser-workstream-smoke/tasks/99-verification/01-verify-user-admin-browser-smoke.md
- depends on:
  - TASK-UABWS-04-001
- required reads:
  - AGENTS.md
  - specs/user-admin-browser-workstream-smoke/README.md
  - specs/user-admin-browser-workstream-smoke/conversation-capture.md
  - specs/user-admin-browser-workstream-smoke/pending-tasks.md
  - specs/user-admin-browser-workstream-smoke/backlog/01-user-admin-browser-smoke-build-backlog.md
  - specs/user-admin-browser-workstream-smoke/tasks/99-verification/01-verify-user-admin-browser-smoke.md
  - specs/archive/user-admin-surface-conformance-cleanup/conformance-verification.md
  - smoke docs/outputs produced by prior tasks
- skills:
  - akka-web-ui-testing
  - akka-http-endpoint-testing
  - app-description-readiness-assessment
- expected outputs:
  - specs/user-admin-browser-workstream-smoke/browser-smoke-verification.md
  - updated pending queue with done status if complete, or appended follow-up tasks plus a new terminal verification task if gaps remain
- required checks:
  - smoke command established by prior tasks
  - `git diff --check`
  - `env -u ADMIN_USERS mvn test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `npm --prefix frontend run build`
- done criteria:
  - verification compares completed work against README done state, backlog, task criteria, and app-description/conformance evidence; command evidence is recorded; mini-project complete only if no material gaps remain
- notes:
  - vertical contract: User Admin / `agent-user-admin`; terminal verification of browser smoke coverage for dashboard/list/detail/task/system-message graph; governed browser-tool/workstream action path; deterministic AuthContext and hidden-target denial; hosted frontend/workstream endpoint; safe trace/redaction assertions
