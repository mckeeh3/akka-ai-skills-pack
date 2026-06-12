# Pending Tasks: Operational Deployment Readiness

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, conversation capture, selected backlog, selected task entry, and task brief before editing.
- Mark exactly one selected task `in-progress` before implementation edits.
- Update this file before finishing the harness response.
- Each completed task must make one focused git commit including that task's intended changes and queue-status update.
- Commit message format: `deployment-readiness: <short task title>`.

## Tasks

### TASK-ODR-00-001: Create operational deployment readiness scaffold

- status: done
- source: user requested archiving production runtime hardening, running release confidence checks, and starting the recommended operational deployment readiness mini-project
- task brief: specs/operational-deployment-readiness/tasks/00-planning/00-create-deployment-readiness-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - specs/operational-deployment-readiness/README.md
  - specs/operational-deployment-readiness/conversation-capture.md
  - specs/operational-deployment-readiness/backlog/01-operational-deployment-readiness-build-backlog.md
  - specs/operational-deployment-readiness/tasks/00-planning/00-create-deployment-readiness-queue.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/operational-deployment-readiness/README.md
  - specs/operational-deployment-readiness/conversation-capture.md
  - specs/operational-deployment-readiness/sprints/01-deployment-readiness-sprint.md
  - specs/operational-deployment-readiness/backlog/01-operational-deployment-readiness-build-backlog.md
  - specs/operational-deployment-readiness/tasks/**/*.md
  - specs/operational-deployment-readiness/pending-tasks.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project has captured rationale, current intent, done state, backlog, task briefs, and pending queue
  - first non-done task is runnable without guessing
- notes:
  - vertical contract: cross-cutting operational readiness planning; no runtime mutation

### TASK-ODR-01-001: Inventory environment, config, docs, and scripts

- status: done
- source: specs/operational-deployment-readiness/backlog/01-operational-deployment-readiness-build-backlog.md
- task brief: specs/operational-deployment-readiness/tasks/01-env-config/01-inventory-env-config-docs-scripts.md
- depends on:
  - TASK-ODR-00-001
- required reads:
  - AGENTS.md
  - specs/operational-deployment-readiness/README.md
  - specs/operational-deployment-readiness/conversation-capture.md
  - pom.xml
  - frontend/package.json
  - README.md
  - docs/**
  - src/main/resources/**
  - src/main/java/ai/first/**
  - frontend/src/**
- skills:
  - akka-web-ui-frontend-project
  - akka-workos-user-auth
  - akka-resend-email-service
  - akka-agent-model-governance
- expected outputs:
  - specs/operational-deployment-readiness/env-config-inventory.md
  - queue updates if blockers/order changes
- required checks:
  - `git diff --check`
- done criteria:
  - inventory captures env vars, scripts, provider/model config, static asset behavior, and known gaps
  - next task can document/validate config without rediscovery
- notes:
  - vertical contract: cross-cutting docs/survey; no runtime behavior change; inventory preserves auth/provider boundaries; validation by diff check
  - completed: created `specs/operational-deployment-readiness/env-config-inventory.md` covering env vars, scripts, provider/model config, static asset behavior, and known follow-up gaps
  - checks: `git diff --check`

### TASK-ODR-02-001: Document environment/secret configuration and add validation

- status: pending
- source: specs/operational-deployment-readiness/backlog/01-operational-deployment-readiness-build-backlog.md
- task brief: specs/operational-deployment-readiness/tasks/01-env-config/02-document-env-secret-config-validation.md
- depends on:
  - TASK-ODR-01-001
- required reads:
  - AGENTS.md
  - specs/operational-deployment-readiness/README.md
  - specs/operational-deployment-readiness/env-config-inventory.md
  - files identified by inventory
- skills:
  - akka-workos-user-auth
  - akka-resend-email-service
  - akka-agent-model-governance
  - akka-web-ui-frontend-project
- expected outputs:
  - docs under docs/ or specs/operational-deployment-readiness/ for env/secrets
  - optional validation script/test
- required checks:
  - `git diff --check`
  - `npm --prefix frontend test -- --run`
- done criteria:
  - required/optional env vars are documented with local/test/prod behavior; frontend-public vs backend-secret boundary is explicit; ADMIN_USERS SaaS Owner bootstrap caveat and fail-closed behavior are documented
- notes:
  - vertical contract: cross-cutting operational docs/validation; documents auth/tenant bootstrap boundaries; docs/scripts/tests substrate

### TASK-ODR-03-001: Document or implement health/readiness diagnostics

- status: pending
- source: specs/operational-deployment-readiness/backlog/01-operational-deployment-readiness-build-backlog.md
- task brief: specs/operational-deployment-readiness/tasks/02-health-observability/01-health-readiness-diagnostics.md
- depends on:
  - TASK-ODR-02-001
- required reads:
  - AGENTS.md
  - specs/operational-deployment-readiness/README.md
  - specs/operational-deployment-readiness/env-config-inventory.md
  - env/secret docs from TASK-ODR-02-001
  - src/main/java/ai/first/api/**
  - src/main/java/ai/first/application/foundation/**
  - src/test/java/ai/first/**
- skills:
  - akka-http-endpoints
  - akka-http-endpoint-testing
  - akka-agent-model-governance
  - akka-resend-email-service
- expected outputs:
  - health/readiness diagnostic docs and optional endpoint/test changes
- required checks:
  - `git diff --check`
  - `env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest test`
- done criteria:
  - startup/frontend/workstream/auth/email/model/audit readiness checks are documented or implemented; missing external config is distinguishable from healthy configured readiness; diagnostics avoid leaking secrets or hidden tenant/customer data
- notes:
  - vertical contract: cross-cutting operational diagnostics; must not bypass auth or reveal hidden data; docs and optional HTTP endpoint/tests

### TASK-ODR-04-001: Create deployment runbook and smoke checklist

- status: pending
- source: specs/operational-deployment-readiness/backlog/01-operational-deployment-readiness-build-backlog.md
- task brief: specs/operational-deployment-readiness/tasks/03-runbooks/01-deployment-runbook-smoke-checklist.md
- depends on:
  - TASK-ODR-03-001
- required reads:
  - AGENTS.md
  - specs/operational-deployment-readiness/README.md
  - env docs and readiness docs from prior tasks
  - specs/user-admin-browser-workstream-smoke/smoke-command.md
  - specs/archive/user-admin-production-runtime-hardening/production-runtime-verification.md
- skills:
  - akka-web-ui-testing
  - akka-http-endpoint-testing
  - akka-resend-email-service
  - akka-agent-model-governance
- expected outputs:
  - deployment runbook and smoke checklist under docs/ or mini-project docs
  - optional README links
- required checks:
  - `git diff --check`
  - `npm --prefix frontend run smoke:user-admin-workstream`
- done criteria:
  - runbook explains env setup, startup, smoke commands, expected pass/fail states, provider/model credential checks, static frontend build, rollback/troubleshooting; commands include ADMIN_USERS caveat
- notes:
  - vertical contract: deployment docs/runbook; smoke references User Admin and readiness surfaces; validation by smoke command and diff check

### TASK-ODR-99-001: Verify operational deployment readiness

- status: pending
- source: specs/operational-deployment-readiness/backlog/01-operational-deployment-readiness-build-backlog.md
- task brief: specs/operational-deployment-readiness/tasks/99-verification/01-verify-operational-deployment-readiness.md
- depends on:
  - TASK-ODR-04-001
- required reads:
  - AGENTS.md
  - specs/operational-deployment-readiness/README.md
  - specs/operational-deployment-readiness/conversation-capture.md
  - specs/operational-deployment-readiness/pending-tasks.md
  - outputs from prior tasks
  - deployment/env/readiness docs created by prior tasks
- skills:
  - app-description-readiness-assessment
  - akka-http-endpoint-testing
  - akka-web-ui-testing
- expected outputs:
  - specs/operational-deployment-readiness/deployment-readiness-verification.md
  - updated queue with done status or follow-up tasks and new terminal verification task
- required checks:
  - `git diff --check`
  - `env -u ADMIN_USERS mvn test`
  - `npm --prefix frontend run smoke:user-admin-workstream`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `npm --prefix frontend run build`
- done criteria:
  - verification compares completed work against README done state, backlog, and task criteria; required checks pass or blockers are recorded; no secrets are introduced; follow-up tasks are appended if material gaps remain
- notes:
  - vertical contract: cross-cutting operational readiness verification; docs/scripts/tests/build validation
