# Pending Tasks: AI-First SaaS Starter App Template

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Update task status before finishing the harness response.
- Each task must make one git commit before being marked `done`; the commit should include only that task's intended changes and its queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- This queue is for the starter app template migration, rooted at `specs/ai-first-saas-starter-app-template/`.

## Tasks

### TASK-STARTER-00-001: Create starter app template planning scaffold

- status: done
- source: user request to plan an end-to-end full-core starter app included with the skills pack
- task brief: specs/ai-first-saas-starter-app-template/tasks/00-planning-scaffold/00-create-starter-app-template-plan.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - docs/workstream-ui-reference-architecture.md
  - specs/core-app-full-stack-readiness/README.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/ai-first-saas-starter-app-template/README.md
  - specs/ai-first-saas-starter-app-template/conversation-capture.md
  - specs/ai-first-saas-starter-app-template/pending-tasks.md
  - specs/ai-first-saas-starter-app-template/sprints/*.md
  - specs/ai-first-saas-starter-app-template/backlog/*.md
  - specs/ai-first-saas-starter-app-template/tasks/**/*.md
- required checks:
  - git diff --check
- done criteria:
  - migration has captured rationale, sprint sequence, backlogs, and pending queue
  - task changes and queue update are committed
- notes:
  - commit message: `Add starter app template migration plan`

### TASK-STARTER-01-001: Define starter app scope and acceptance criteria

- status: done
- source: specs/ai-first-saas-starter-app-template/backlog/01-template-scope-inventory-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/01-template-scope-inventory/01-define-starter-app-scope.md
- depends on: [TASK-STARTER-00-001]
- required reads:
  - specs/ai-first-saas-starter-app-template/README.md
  - specs/ai-first-saas-starter-app-template/conversation-capture.md
  - specs/ai-first-saas-starter-app-template/sprints/01-template-scope-inventory-sprint.md
  - specs/ai-first-saas-starter-app-template/backlog/01-template-scope-inventory-build-backlog.md
  - specs/core-app-full-stack-readiness/full-core-realization-map.md
  - specs/core-app-full-stack-readiness/full-core-acceptance-test-matrix.md
- skills:
  - core-saas-foundation
  - agent-workstream-apps
  - capability-first-backend
- expected outputs:
  - specs/ai-first-saas-starter-app-template/starter-app-scope-and-acceptance.md
- required checks:
  - git diff --check
- done criteria:
  - starter full-core scope, non-goals, install modes, and acceptance criteria are explicit
  - task changes and queue update are committed
- notes:
  - commit message: `Define starter app scope and acceptance`

### TASK-STARTER-01-002: Inventory legacy and reusable assets

- status: done
- source: specs/ai-first-saas-starter-app-template/backlog/01-template-scope-inventory-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/01-template-scope-inventory/02-inventory-legacy-and-reusable-assets.md
- depends on: [TASK-STARTER-01-001]
- required reads:
  - specs/ai-first-saas-starter-app-template/starter-app-scope-and-acceptance.md
  - docs/ai-first-examples-and-tests-gap-list.md
  - docs/web-ui-pattern-selection.md
  - docs/workstream-ui-reference-architecture.md
  - frontend/src/workstream/**
  - src/main/java/com/example/**
  - src/main/resources/static-resources/**
  - docs/examples/**
- skills:
  - none; repository inventory task
- expected outputs:
  - specs/ai-first-saas-starter-app-template/legacy-and-reusable-asset-inventory.md
- required checks:
  - git diff --check
- done criteria:
  - each major existing code/example asset is classified as reuse, migrate, mechanics-only, quarantine, archive, or delete-later
  - task changes and queue update are committed
- notes:
  - commit message: `Inventory starter reusable assets`

### TASK-STARTER-01-003: Choose template source layout and extension workflow

- status: done
- source: specs/ai-first-saas-starter-app-template/backlog/01-template-scope-inventory-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/01-template-scope-inventory/03-choose-template-layout-extension-workflow.md
- depends on: [TASK-STARTER-01-002]
- required reads:
  - specs/ai-first-saas-starter-app-template/starter-app-scope-and-acceptance.md
  - specs/ai-first-saas-starter-app-template/legacy-and-reusable-asset-inventory.md
  - pack/AGENTS.md
  - install.sh
  - pack/**
  - docs/skills-pack-user-guide.md
  - docs/skills-pack-developer-guide.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/ai-first-saas-starter-app-template/template-layout-and-extension-workflow.md
- required checks:
  - git diff --check
- done criteria:
  - canonical template source path, installed-pack resource path, scaffold behavior, and extension workflow are documented
  - task changes and queue update are committed
- notes:
  - commit message: `Choose starter template layout workflow`

### TASK-STARTER-02-001: Create starter backend skeleton and package policy

- status: done
- source: specs/ai-first-saas-starter-app-template/backlog/02-foundation-backend-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/02-foundation-backend/01-create-starter-backend-skeleton.md
- depends on: [TASK-STARTER-01-003]
- required reads:
  - specs/ai-first-saas-starter-app-template/template-layout-and-extension-workflow.md
  - specs/core-app-full-stack-readiness/full-core-realization-map.md
  - skills/README.md
  - skills/core-saas-foundation/SKILL.md
  - pom.xml
  - src/main/java/com/example/**
- skills:
  - core-saas-foundation
  - akka-http-endpoints
  - akka-entities
  - akka-views
- expected outputs:
  - starter app template project skeleton in the chosen template source path
  - package/base-package policy recorded in template docs
- required checks:
  - git diff --check
  - relevant Maven compile/test command if skeleton is executable
- done criteria:
  - skeleton is isolated from legacy examples and ready for foundation components
  - task changes and queue update are committed
- notes:
  - commit message: `Create starter backend skeleton`

### TASK-STARTER-02-002: Implement `/api/me`, AuthContext, membership, role, and audit foundation

- status: done
- source: specs/ai-first-saas-starter-app-template/backlog/02-foundation-backend-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/02-foundation-backend/02-implement-me-authcontext-admin-audit-foundation.md
- depends on: [TASK-STARTER-02-001]
- required reads:
  - specs/core-app-full-stack-readiness/user-admin-reference-slice.md
  - docs/core-saas-identity-tenancy-admin.md
  - skills/core-saas-foundation/SKILL.md
  - skills/akka-workos-user-auth/SKILL.md
  - skills/akka-basic-user-admin/SKILL.md
  - skills/akka-http-endpoint-jwt/SKILL.md
  - skills/akka-http-endpoint-request-context/SKILL.md
- skills:
  - core-saas-foundation
  - akka-workos-user-auth
  - akka-basic-user-admin
  - akka-http-endpoint-jwt
  - akka-http-endpoint-request-context
- expected outputs:
  - Account/Profile/Settings/Tenant/Customer/Membership/Role/Capability/AuthContext/AdminAudit foundation code
  - `/api/me` endpoint and tests
- required checks:
  - git diff --check
  - Maven tests for changed backend slice
- done criteria:
  - `/api/me` returns browser-safe context/capabilities and denies disabled/no-membership/forbidden states correctly
  - task changes and queue update are committed
- notes:
  - commit message: `Implement starter auth context me foundation`

### TASK-STARTER-02-003: Implement invitation onboarding and user admin backend slice

- status: done
- source: specs/ai-first-saas-starter-app-template/backlog/02-foundation-backend-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/02-foundation-backend/03-implement-invitation-user-admin-backend.md
- depends on: [TASK-STARTER-02-002]
- required reads:
  - specs/core-app-full-stack-readiness/invitation-onboarding-reference-slice.md
  - specs/core-app-full-stack-readiness/user-admin-reference-slice.md
  - skills/akka-saas-invitation-onboarding/SKILL.md
  - skills/akka-resend-email-service/SKILL.md
  - skills/akka-workflows/SKILL.md
  - skills/akka-timed-actions/SKILL.md
  - skills/akka-consumers/SKILL.md
  - skills/akka-views/SKILL.md
- skills:
  - akka-saas-invitation-onboarding
  - akka-resend-email-service
  - akka-workflows
  - akka-timed-actions
  - akka-consumers
  - akka-views
- expected outputs:
  - Invitation entity/workflow/views/endpoints/outbox/email seam
  - User Admin backend capabilities/views/endpoints
  - invite/user-admin backend tests
- required checks:
  - git diff --check
  - Maven tests for invitation and user-admin slice
- done criteria:
  - invite lifecycle and user admin are executable with audit, idempotency, and tenant-isolation coverage
  - task changes and queue update are committed
- notes:
  - commit message: `Implement starter invitation user admin backend`
  - rendered-template Maven test command passed with `{{JAVA_BASE_PACKAGE}}=ai.first` and `{{JAVA_PACKAGE_PATH}}=ai/first`.

### TASK-STARTER-03-001: Define real workstream browser API contracts

- status: done
- source: specs/ai-first-saas-starter-app-template/backlog/03-workstream-api-frontend-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/03-workstream-api-frontend/01-define-real-workstream-api-contracts.md
- depends on: [TASK-STARTER-02-003]
- required reads:
  - docs/workstream-ui-reference-architecture.md
  - docs/structured-surface-contracts.md
  - docs/web-ui-api-contract-patterns.md
  - specs/core-app-full-stack-readiness/core-workstream-api-contracts.md
  - frontend/src/api/WorkstreamApiClient.ts
  - frontend/src/workstream/types/**
- skills:
  - akka-web-ui-apps
  - akka-web-ui-api-client
  - akka-http-endpoints
- expected outputs:
  - specs/ai-first-saas-starter-app-template/starter-workstream-api-contracts.md
  - updated frontend/backend DTO contract notes if needed
- required checks:
  - git diff --check
- done criteria:
  - real endpoint/DTO contracts are implementation-ready for workstream bootstrap, surfaces, actions, and events
  - task changes and queue update are committed
- notes:
  - commit message: `Define starter workstream API contracts`

### TASK-STARTER-03-002: Wire User Admin workstream UI to real endpoints

- status: done
- source: specs/ai-first-saas-starter-app-template/backlog/03-workstream-api-frontend-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/03-workstream-api-frontend/02-wire-user-admin-workstream-ui-real-endpoints.md
- depends on: [TASK-STARTER-03-001]
- required reads:
  - specs/ai-first-saas-starter-app-template/starter-workstream-api-contracts.md
  - frontend/src/workstream-user-admin-vertical.contract.test.mjs
  - frontend/src/api/**
  - frontend/src/workstream/**
  - skills/akka-web-ui-apps/SKILL.md
  - skills/akka-web-ui-frontend-project/SKILL.md
  - skills/akka-web-ui-testing/SKILL.md
- skills:
  - akka-web-ui-apps
  - akka-web-ui-frontend-project
  - akka-web-ui-api-client
  - akka-web-ui-state-rendering
  - akka-web-ui-testing
- expected outputs:
  - real production frontend client wiring for User Admin vertical
  - endpoint/frontend tests for packaged UI and User Admin API behavior
- required checks:
  - git diff --check
  - `cd frontend && npm test -- --run && npm run typecheck && npm run build`
  - relevant Maven endpoint tests
- done criteria:
  - User Admin dashboard/list/detail/edit uses real starter backend APIs in production path
  - task changes and queue update are committed
- notes:
  - commit message: `Wire starter user admin workstream APIs`
  - frontend checks passed: `cd frontend && npm test -- --run && npm run typecheck && npm run build`
  - rendered-template Maven test command passed with `{{JAVA_BASE_PACKAGE}}=ai.first` and `{{JAVA_PACKAGE_PATH}}=ai/first`.

### TASK-STARTER-03-003: Wire remaining core workstream surfaces and realtime/stale behavior

- status: done
- source: specs/ai-first-saas-starter-app-template/backlog/03-workstream-api-frontend-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/03-workstream-api-frontend/03-wire-core-surfaces-realtime.md
- depends on: [TASK-STARTER-03-002]
- required reads:
  - specs/ai-first-saas-starter-app-template/starter-workstream-api-contracts.md
  - docs/workstream-ui-reference-architecture.md
  - skills/akka-web-ui-realtime/SKILL.md
  - skills/akka-http-endpoint-sse/SKILL.md
  - frontend/src/workstream/**
- skills:
  - akka-web-ui-realtime
  - akka-http-endpoint-sse
  - akka-web-ui-state-rendering
  - akka-web-ui-testing
- expected outputs:
  - Access/Profile, Audit/Trace, Governance/Policy, and placeholder Agent Admin surfaces wired to real endpoints
  - realtime or stale-state endpoint/client behavior and tests
- required checks:
  - git diff --check
  - frontend tests/typecheck/build
  - relevant Maven endpoint tests
- done criteria:
  - core workstream shell operates against backend APIs with visible stale/reconnect/forbidden behavior
  - task changes and queue update are committed
- notes:
  - commit message: `Wire starter core workstream surfaces realtime`
  - frontend checks passed: `cd frontend && npm test -- --run && npm run typecheck && npm run build`
  - rendered-template Maven test command passed with `{{JAVA_BASE_PACKAGE}}=ai.first` and `{{JAVA_PACKAGE_PATH}}=ai/first`.

### TASK-STARTER-04-001: Implement governed agent records and seed import

- status: done
- source: specs/ai-first-saas-starter-app-template/backlog/04-agent-governance-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/04-agent-governance/01-implement-agent-records-seed-import.md
- depends on: [TASK-STARTER-02-003]
- required reads:
  - specs/core-app-full-stack-readiness/agent-admin-component-api-slice.md
  - specs/governed-runtime-agent-foundation/minimal-governed-runtime-agent-reference-slice.md
  - docs/agent-runtime-invocation-pattern.md
  - skills/akka-agent-seed-documents/SKILL.md
  - skills/akka-agent-behavior-profiles/SKILL.md
  - skills/akka-agent-prompt-governance/SKILL.md
  - skills/akka-agent-skill-governance/SKILL.md
  - skills/akka-agent-tool-boundaries/SKILL.md
- skills:
  - akka-agent-seed-documents
  - akka-agent-behavior-profiles
  - akka-agent-prompt-governance
  - akka-agent-skill-governance
  - akka-agent-tool-boundaries
- expected outputs:
  - governed agent records/components/views
  - seed bundle/import code and tests
- required checks:
  - git diff --check
  - Maven tests for governed agent seed slice
- done criteria:
  - default governed agent behavior imports idempotently and preserves tenant customization semantics
  - task changes and queue update are committed
- notes:
  - commit message: `Implement starter governed agent seed import`
  - rendered-template Maven test command passed with `{{JAVA_BASE_PACKAGE}}=ai.first` and `{{JAVA_PACKAGE_PATH}}=ai/first`.

### TASK-STARTER-04-002: Implement prompt assembly, readSkill, and behavior-editing flow

- status: done
- source: specs/ai-first-saas-starter-app-template/backlog/04-agent-governance-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/04-agent-governance/02-implement-prompt-assembly-readskill-behavior-editing.md
- depends on: [TASK-STARTER-04-001]
- required reads:
  - docs/agent-runtime-invocation-pattern.md
  - specs/core-app-full-stack-readiness/hybrid-akka-agent-runtime-contract.md
  - skills/akka-agent-runtime-state/SKILL.md
  - skills/akka-agent-tools/SKILL.md
  - skills/akka-agent-testing/SKILL.md
- skills:
  - akka-agent-runtime-state
  - akka-agent-tools
  - akka-agent-testing
- expected outputs:
  - deterministic prompt assembly service/capability
  - authorized `readSkill(skillId)` capability
  - behavior editing proposal/review/activation slice
  - tests for denial, tracing, and approval boundaries
- required checks:
  - git diff --check
  - Maven tests for agent runtime/governance slice
- done criteria:
  - runtime behavior guidance is governed by backend records and traces, not prompt text alone
  - task changes and queue update are committed
- notes:
  - commit message: `Implement starter agent runtime governance`
  - rendered-template Maven test command passed with `{{JAVA_BASE_PACKAGE}}=ai.first` and `{{JAVA_PACKAGE_PATH}}=ai/first`.

### TASK-STARTER-04-003: Wire Agent Admin and Governance/Policy UI to real capabilities

- status: done
- source: specs/ai-first-saas-starter-app-template/backlog/04-agent-governance-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/04-agent-governance/03-wire-agent-admin-governance-ui.md
- depends on: [TASK-STARTER-04-002, TASK-STARTER-03-003]
- required reads:
  - specs/core-app-full-stack-readiness/agent-admin-component-api-slice.md
  - specs/core-app-full-stack-readiness/governance-policy-core-module-slice.md
  - frontend/src/workstream-agent-admin-vertical.contract.test.mjs
  - frontend/src/workstream/**
  - skills/akka-web-ui-apps/SKILL.md
- skills:
  - akka-web-ui-apps
  - akka-web-ui-api-client
  - akka-web-ui-forms-validation
  - akka-web-ui-testing
- expected outputs:
  - Agent Admin and Governance/Policy real API/frontend wiring
  - tests for governed edits, approvals, denials, and traces through UI/API contracts
- required checks:
  - git diff --check
  - frontend tests/typecheck/build
  - relevant Maven tests
- done criteria:
  - governed agent and policy surfaces are real starter capabilities, not only fixtures
  - task changes and queue update are committed
- notes:
  - commit message: `Wire starter agent admin governance UI`
  - frontend checks passed: `cd frontend && npm test -- --run && npm run typecheck && npm run build`
  - rendered-template Maven test command passed with `{{JAVA_BASE_PACKAGE}}=ai.first`, `{{JAVA_PACKAGE_PATH}}=ai/first`, `{{MAVEN_GROUP_ID}}=ai.first`, `{{APP_SLUG}}=ai-first-saas-starter`, and `{{APP_NAME}}=AI First SaaS Starter`.

### TASK-STARTER-05-001: Add starter scaffold packaging mode

- status: pending
- source: specs/ai-first-saas-starter-app-template/backlog/05-packaging-install-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/05-packaging-install/01-add-starter-scaffold-packaging-mode.md
- depends on: [TASK-STARTER-04-003]
- required reads:
  - specs/ai-first-saas-starter-app-template/template-layout-and-extension-workflow.md
  - install.sh
  - pack/**
  - docs/skills-pack-user-guide.md
  - docs/skills-pack-developer-guide.md
  - skills/README.md
- skills:
  - none; packaging/install task
- expected outputs:
  - pack resource/template updates
  - install/init scaffold option or documented command
  - overwrite-safety behavior
  - packaging/scaffold tests or validation script where practical
- required checks:
  - git diff --check
  - packaging validation command if available
- done criteria:
  - skills-only and starter-app scaffold modes are clearly separated and safe
  - task changes and queue update are committed
- notes: []

### TASK-STARTER-05-002: Update extension workflow docs and routing

- status: pending
- source: specs/ai-first-saas-starter-app-template/backlog/05-packaging-install-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/05-packaging-install/02-update-extension-workflow-docs-routing.md
- depends on: [TASK-STARTER-05-001]
- required reads:
  - docs/skills-pack-user-guide.md
  - docs/skills-pack-developer-guide.md
  - skills/README.md
  - skills/app-generate-app/SKILL.md
  - skills/akka-prd-to-specs-backlog/SKILL.md
  - skills/akka-do-next-pending-task/SKILL.md
- skills:
  - app-descriptions
  - akka-prd-to-specs-backlog
- expected outputs:
  - user/developer docs updated for scaffold-then-extend workflow
  - routing guidance updated to prefer starter extension for new apps when scaffolded
- required checks:
  - git diff --check
- done criteria:
  - downstream users know how to install/scaffold the starter and extend it safely
  - task changes and queue update are committed
- notes: []

### TASK-STARTER-06-001: Apply legacy asset quarantine and canonical routing cleanup

- status: pending
- source: specs/ai-first-saas-starter-app-template/backlog/06-legacy-cleanup-review-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/06-legacy-cleanup-review/01-quarantine-legacy-assets-routing-cleanup.md
- depends on: [TASK-STARTER-05-002]
- required reads:
  - specs/ai-first-saas-starter-app-template/legacy-and-reusable-asset-inventory.md
  - docs/ai-first-examples-and-tests-gap-list.md
  - docs/web-ui-pattern-selection.md
  - docs/workstream-ui-reference-architecture.md
  - skills/README.md
  - skills/akka-web-ui-apps/SKILL.md
- skills:
  - none; cleanup/review task
- expected outputs:
  - docs/skills updated to stop promoting stale DCA/static seed assets as canonical
  - legacy assets marked mechanics-only, archived, or removed according to inventory
- required checks:
  - git diff --check
  - relevant frontend/backend tests affected by cleanup
- done criteria:
  - only the starter app remains canonical for full-core implementation guidance
  - task changes and queue update are committed
- notes: []

### TASK-STARTER-06-002: Run final starter acceptance and publish completion summary

- status: pending
- source: specs/ai-first-saas-starter-app-template/backlog/06-legacy-cleanup-review-build-backlog.md
- task brief: specs/ai-first-saas-starter-app-template/tasks/06-legacy-cleanup-review/02-final-acceptance-completion-summary.md
- depends on: [TASK-STARTER-06-001]
- required reads:
  - specs/ai-first-saas-starter-app-template/README.md
  - specs/ai-first-saas-starter-app-template/starter-app-scope-and-acceptance.md
  - specs/core-app-full-stack-readiness/full-core-acceptance-test-matrix.md
  - docs/ai-first-examples-and-tests-gap-list.md
- skills:
  - none; final review task
- expected outputs:
  - specs/ai-first-saas-starter-app-template/final-acceptance-review.md
  - specs/ai-first-saas-starter-app-template/migration-completion-summary.md
  - updated gap list/docs if needed
- required checks:
  - git diff --check
  - full available frontend tests/typecheck/build
  - full available Maven tests or documented subset with rationale
  - packaging/scaffold validation
- done criteria:
  - final review proves starter scaffold can be installed, built, tested, and used as extension base
  - task changes and queue update are committed
- notes: []
