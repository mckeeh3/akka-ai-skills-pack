# Pending Tasks: Core App Full-Stack Readiness

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Update task status before finishing the harness response.
- Each task must make one git commit before being marked `done`; the commit should include only that task's intended changes and its queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- This queue is for the core app full-stack readiness migration, rooted at `specs/core-app-full-stack-readiness/`.

## Tasks

### TASK-CORE-00-001: Create core app full-stack readiness planning scaffold

- status: done
- source: user request to plan full-core app readiness work as one-session committed tasks
- task brief: specs/core-app-full-stack-readiness/tasks/00-planning-scaffold/00-create-core-app-full-stack-readiness-plan.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md
  - specs/workstream-ui-implementation-migration/README.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/core-app-full-stack-readiness/README.md
  - specs/core-app-full-stack-readiness/conversation-capture.md
  - specs/core-app-full-stack-readiness/pending-tasks.md
  - specs/core-app-full-stack-readiness/sprints/*.md
  - specs/core-app-full-stack-readiness/backlog/*.md
  - specs/core-app-full-stack-readiness/tasks/**/*.md
- required checks:
  - verify git status contains only migration planning scaffold files before commit
  - git diff --check
- done criteria:
  - migration has captured rationale, sprint sequence, backlogs, and pending queue
  - task changes and queue update are committed
- notes:
  - commit message: `Add core app full-stack readiness plan`

### TASK-CORE-01-001: Create full-core realization map

- status: done
- source: specs/core-app-full-stack-readiness/backlog/01-scope-and-generation-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/01-scope-and-generation/01-create-full-core-realization-map.md
- depends on: [TASK-CORE-00-001]
- required reads:
  - specs/core-app-full-stack-readiness/README.md
  - specs/core-app-full-stack-readiness/conversation-capture.md
  - specs/core-app-full-stack-readiness/sprints/01-scope-and-generation-sprint.md
  - specs/core-app-full-stack-readiness/backlog/01-scope-and-generation-build-backlog.md
  - docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md
  - docs/examples/core-ai-first-saas-input/01-core-seed-progression-plan.md
  - docs/examples/ai-first-saas-seed-app-description/README.md
  - docs/capability-first-backend-architecture.md
  - skills/core-saas-foundation/SKILL.md
  - skills/akka-solution-decomposition/SKILL.md
- skills:
  - core-saas-foundation
  - capability-first-backend
  - akka-solution-decomposition
- expected outputs:
  - specs/core-app-full-stack-readiness/full-core-realization-map.md
- required checks:
  - git diff --check
- done criteria:
  - future tasks can use the map as implementation contract
  - task changes and queue update are committed
- notes:
  - commit message: `Add full-core realization map`

### TASK-CORE-01-002: Harden readiness and generation gates

- status: done
- source: specs/core-app-full-stack-readiness/backlog/01-scope-and-generation-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/01-scope-and-generation/02-harden-readiness-and-generation-gates.md
- depends on: [TASK-CORE-01-001]
- required reads:
  - specs/core-app-full-stack-readiness/full-core-realization-map.md
  - skills/app-description-readiness-assessment/SKILL.md
  - skills/app-generate-app/SKILL.md
  - skills/app-description-bootstrap/SKILL.md
  - skills/akka-prd-to-specs-backlog/SKILL.md
  - skills/akka-solution-decomposition/SKILL.md
- skills:
  - app-description-readiness-assessment
  - app-generate-app
  - akka-prd-to-specs-backlog
  - akka-solution-decomposition
- expected outputs:
  - readiness/generation/planning skill updates
- required checks:
  - git diff --check
- done criteria:
  - full-core omissions are blocked or explicitly labeled as narrower scope
  - task changes and queue update are committed
- notes:
  - commit message: `Harden full-core readiness gates`

### TASK-CORE-01-003: Create golden-path generation walkthrough

- status: done
- source: specs/core-app-full-stack-readiness/backlog/01-scope-and-generation-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/01-scope-and-generation/03-create-golden-path-generation-walkthrough.md
- depends on: [TASK-CORE-01-002]
- required reads:
  - specs/core-app-full-stack-readiness/full-core-realization-map.md
  - docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md
  - docs/module-sprint-planning.md
  - docs/pending-task-queue.md
  - skills/akka-prd-to-specs-backlog/SKILL.md
  - skills/akka-backlog-to-pending-tasks/SKILL.md
- skills:
  - akka-prd-to-specs-backlog
  - akka-backlog-to-pending-tasks
- expected outputs:
  - specs/core-app-full-stack-readiness/golden-path-generation-walkthrough.md
- required checks:
  - git diff --check
- done criteria:
  - a fresh harness can follow the documented generation path
  - task changes and queue update are committed
- notes:
  - commit message: `Add golden-path generation walkthrough`

### TASK-CORE-02-001: Inventory auth and user admin reference gaps

- status: done
- source: specs/core-app-full-stack-readiness/backlog/02-auth-user-admin-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/02-auth-user-admin/01-inventory-auth-user-admin-reference-gaps.md
- depends on: [TASK-CORE-01-001]
- required reads:
  - specs/core-app-full-stack-readiness/full-core-realization-map.md
  - docs/core-saas-identity-tenancy-admin.md
  - skills/akka-workos-user-auth/SKILL.md
  - skills/akka-basic-user-admin/SKILL.md
  - skills/akka-saas-invitation-onboarding/SKILL.md
  - src/main/java/com/example/domain/security/**
  - src/main/java/com/example/application/security/**
  - src/main/java/com/example/api/security/**
- skills:
  - akka-workos-user-auth
  - akka-basic-user-admin
  - akka-saas-invitation-onboarding
- expected outputs:
  - specs/core-app-full-stack-readiness/auth-user-admin-gap-inventory.md
- required checks:
  - git diff --check
- done criteria:
  - gaps are concrete and ordered for follow-up tasks
  - task changes and queue update are committed
- notes:
  - commit message: `Inventory auth user admin gaps`

### TASK-CORE-02-002: Specify invitation onboarding reference slice

- status: done
- source: specs/core-app-full-stack-readiness/backlog/02-auth-user-admin-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/02-auth-user-admin/02-spec-invitation-onboarding-reference-slice.md
- depends on: [TASK-CORE-02-001]
- required reads:
  - specs/core-app-full-stack-readiness/auth-user-admin-gap-inventory.md
  - skills/akka-saas-invitation-onboarding/SKILL.md
  - skills/akka-workflows/SKILL.md
  - skills/akka-consumers/SKILL.md
  - skills/akka-timed-actions/SKILL.md
  - skills/akka-views/SKILL.md
  - docs/core-saas-identity-tenancy-admin.md
- skills:
  - akka-saas-invitation-onboarding
  - akka-workflows
  - akka-consumers
  - akka-timed-actions
  - akka-views
- expected outputs:
  - specs/core-app-full-stack-readiness/invitation-onboarding-reference-slice.md
- required checks:
  - git diff --check
- done criteria:
  - future code task can implement the reference slice without architecture decisions
  - task changes and queue update are committed
- notes:
  - commit message: `Specify invitation onboarding reference slice`

### TASK-CORE-02-003: Specify full user administration reference slice

- status: done
- source: specs/core-app-full-stack-readiness/backlog/02-auth-user-admin-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/02-auth-user-admin/03-spec-full-user-admin-reference-slice.md
- depends on: [TASK-CORE-02-002]
- required reads:
  - specs/core-app-full-stack-readiness/auth-user-admin-gap-inventory.md
  - specs/core-app-full-stack-readiness/invitation-onboarding-reference-slice.md
  - skills/akka-basic-user-admin/SKILL.md
  - docs/core-saas-identity-tenancy-admin.md
  - docs/core-ai-first-saas-foundation.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/01-secure-tenant-user-foundation.md
- skills:
  - akka-basic-user-admin
  - core-saas-foundation
  - akka-views
  - akka-http-endpoints
- expected outputs:
  - specs/core-app-full-stack-readiness/user-admin-reference-slice.md
- required checks:
  - git diff --check
- done criteria:
  - component/API/view/test contracts are ready for future implementation tasks
  - task changes and queue update are committed
- notes:
  - commit message: `Specify user admin reference slice`

### TASK-CORE-03-001: Inventory Agent Admin and hybrid runtime gaps

- status: done
- source: specs/core-app-full-stack-readiness/backlog/03-agent-admin-runtime-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/03-agent-admin-runtime/01-inventory-agent-admin-runtime-gaps.md
- depends on: [TASK-CORE-01-001]
- required reads:
  - docs/agent-coverage-matrix.md
  - docs/agent-runtime-invocation-pattern.md
  - skills/akka-agent-behavior-profiles/SKILL.md
  - skills/akka-agent-prompt-governance/SKILL.md
  - skills/akka-agent-skill-governance/SKILL.md
  - skills/akka-agent-tool-boundaries/SKILL.md
  - skills/akka-agent-model-governance/SKILL.md
  - src/main/java/com/example/domain/agentfoundation/**
  - src/main/java/com/example/application/agentfoundation/**
- skills:
  - akka-agent-behavior-profiles
  - akka-agent-prompt-governance
  - akka-agent-skill-governance
  - akka-agent-tool-boundaries
  - akka-agent-model-governance
- expected outputs:
  - specs/core-app-full-stack-readiness/agent-admin-runtime-gap-inventory.md
- required checks:
  - git diff --check
- done criteria:
  - gaps are concrete and ordered for follow-up tasks
  - task changes and queue update are committed
- notes:
  - commit message: `Inventory agent admin runtime gaps`

### TASK-CORE-03-002: Specify Agent Admin component and API slice

- status: done
- source: specs/core-app-full-stack-readiness/backlog/03-agent-admin-runtime-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/03-agent-admin-runtime/02-spec-agent-admin-component-api-slice.md
- depends on: [TASK-CORE-03-001]
- required reads:
  - specs/core-app-full-stack-readiness/agent-admin-runtime-gap-inventory.md
  - docs/agent-runtime-invocation-pattern.md
  - skills/akka-agent-behavior-profiles/SKILL.md
  - skills/akka-agent-governed-documents/SKILL.md
  - skills/akka-agent-prompt-governance/SKILL.md
  - skills/akka-agent-skill-governance/SKILL.md
  - skills/akka-agent-seed-documents/SKILL.md
  - skills/akka-agent-model-governance/SKILL.md
- skills:
  - akka-agent-behavior-profiles
  - akka-agent-governed-documents
  - akka-agent-prompt-governance
  - akka-agent-skill-governance
  - akka-agent-seed-documents
  - akka-agent-model-governance
- expected outputs:
  - specs/core-app-full-stack-readiness/agent-admin-component-api-slice.md
- required checks:
  - git diff --check
- done criteria:
  - Agent Admin components/APIs are specified enough for code generation
  - task changes and queue update are committed
- notes:
  - commit message: `Specify agent admin component API slice`

### TASK-CORE-03-003: Harden hybrid Akka agent runtime contract

- status: done
- source: specs/core-app-full-stack-readiness/backlog/03-agent-admin-runtime-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/03-agent-admin-runtime/03-harden-hybrid-akka-agent-runtime-contract.md
- depends on: [TASK-CORE-03-002]
- required reads:
  - specs/core-app-full-stack-readiness/agent-admin-runtime-gap-inventory.md
  - docs/agent-runtime-invocation-pattern.md
  - skills/akka-agent-component/SKILL.md
  - skills/akka-agent-behavior-profiles/SKILL.md
  - skills/akka-agent-skill-governance/SKILL.md
  - skills/akka-agent-tool-boundaries/SKILL.md
  - skills/akka-agent-testing/SKILL.md
  - src/main/java/com/example/application/agentfoundation/ReferenceAgentRuntimeResolver.java
- skills:
  - akka-agent-component
  - akka-agent-behavior-profiles
  - akka-agent-skill-governance
  - akka-agent-tool-boundaries
  - akka-agent-testing
- expected outputs:
  - specs/core-app-full-stack-readiness/hybrid-akka-agent-runtime-contract.md
- required checks:
  - git diff --check
- done criteria:
  - static Java Agent code and governed behavior records have a clear implementation handoff
  - task changes and queue update are committed
- notes:
  - commit message: `Harden hybrid agent runtime contract`

### TASK-CORE-04-001: Align core workstream API contracts

- status: done
- source: specs/core-app-full-stack-readiness/backlog/04-workstream-ui-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/04-workstream-ui/01-align-core-workstream-api-contracts.md
- depends on: [TASK-CORE-02-003, TASK-CORE-03-002]
- required reads:
  - docs/workstream-ui-reference-architecture.md
  - docs/structured-surface-contracts.md
  - frontend/src/workstream/**
  - specs/core-app-full-stack-readiness/user-admin-reference-slice.md
  - specs/core-app-full-stack-readiness/agent-admin-component-api-slice.md
- skills:
  - akka-web-ui-api-client
  - akka-web-ui-apps
  - akka-web-ui-testing
- expected outputs:
  - updated frontend fixture/API contracts or specs/core-app-full-stack-readiness/core-workstream-api-contracts.md
- required checks:
  - frontend checks/build if frontend code changes
  - git diff --check
- done criteria:
  - workstream UI has realistic full-core contracts
  - task changes and queue update are committed
- notes:
  - commit message: `Align core workstream API contracts`

### TASK-CORE-04-002: Add Agent Admin workstream reference

- status: done
- source: specs/core-app-full-stack-readiness/backlog/04-workstream-ui-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/04-workstream-ui/02-add-agent-admin-workstream-reference.md
- depends on: [TASK-CORE-04-001]
- required reads:
  - specs/core-app-full-stack-readiness/core-workstream-api-contracts.md
  - docs/workstream-ui-reference-architecture.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/agent-catalog-and-detail.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/prompt-and-skill-governance.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/skill-manifests-and-tool-permissions.md
  - frontend/src/workstream/**
- skills:
  - akka-web-ui-apps
  - akka-web-ui-state-rendering
  - akka-web-ui-testing
- expected outputs:
  - Agent Admin frontend fixtures/components/tests or docs updates
- required checks:
  - frontend checks/build if frontend code changes
  - git diff --check
- done criteria:
  - Agent Admin UI is concrete enough for future generated apps
  - task changes and queue update are committed
- notes:
  - commit message: `Add agent admin workstream reference`

### TASK-CORE-05-001: Specify Audit/Trace core module

- status: done
- source: specs/core-app-full-stack-readiness/backlog/05-audit-governance-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/05-audit-governance/01-spec-audit-trace-core-module.md
- depends on: [TASK-CORE-03-003]
- required reads:
  - docs/ai-first-saas-application-architecture.md
  - docs/examples/core-ai-first-saas-input/08-module-audit-work-trace-prd.md
  - skills/ai-first-saas-audit-trace/SKILL.md
  - skills/akka-agent-work-trace/SKILL.md
  - specs/core-app-full-stack-readiness/full-core-realization-map.md
- skills:
  - ai-first-saas-audit-trace
  - akka-agent-work-trace
- expected outputs:
  - specs/core-app-full-stack-readiness/audit-trace-core-module-slice.md
- required checks:
  - git diff --check
- done criteria:
  - Audit/Trace is generation-ready as a core module
  - task changes and queue update are committed
- notes:
  - commit message: `Specify audit trace core module`

### TASK-CORE-05-002: Specify Governance/Policy core module

- status: done
- source: specs/core-app-full-stack-readiness/backlog/05-audit-governance-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/05-audit-governance/02-spec-governance-policy-core-module.md
- depends on: [TASK-CORE-05-001]
- required reads:
  - docs/examples/core-ai-first-saas-input/09-module-evaluation-closed-loop-improvement-prd.md
  - skills/ai-first-saas-policy-governance/SKILL.md
  - skills/ai-first-saas-decision-cards/SKILL.md
  - skills/akka-agent-closed-loop-improvement/SKILL.md
  - specs/core-app-full-stack-readiness/agent-admin-component-api-slice.md
  - specs/core-app-full-stack-readiness/audit-trace-core-module-slice.md
- skills:
  - ai-first-saas-policy-governance
  - ai-first-saas-decision-cards
  - akka-agent-closed-loop-improvement
- expected outputs:
  - specs/core-app-full-stack-readiness/governance-policy-core-module-slice.md
- required checks:
  - git diff --check
- done criteria:
  - Governance/Policy is generation-ready as a core module
  - task changes and queue update are committed
- notes:
  - commit message: `Specify governance policy core module`

### TASK-CORE-06-001: Create full-core acceptance and security test matrix

- status: done
- source: specs/core-app-full-stack-readiness/backlog/06-e2e-generation-review-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/06-e2e-generation-review/01-create-full-core-acceptance-test-matrix.md
- depends on: [TASK-CORE-04-002, TASK-CORE-05-002]
- required reads:
  - docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md
  - specs/core-app-full-stack-readiness/full-core-realization-map.md
  - specs/core-app-full-stack-readiness/*-slice.md
  - docs/agent-coverage-matrix.md
  - docs/web-ui-quality-checklist.md
- skills:
  - app-description-test-specification
  - akka-agent-testing
  - akka-web-ui-testing
- expected outputs:
  - specs/core-app-full-stack-readiness/full-core-acceptance-test-matrix.md
- required checks:
  - git diff --check
- done criteria:
  - full-core readiness has one authoritative test checklist
  - task changes and queue update are committed
- notes:
  - commit message: `Add full-core acceptance test matrix`

### TASK-CORE-06-002: Final consistency review

- status: done
- source: specs/core-app-full-stack-readiness/backlog/06-e2e-generation-review-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/06-e2e-generation-review/02-final-consistency-review.md
- depends on: [TASK-CORE-06-001]
- required reads:
  - specs/core-app-full-stack-readiness/README.md
  - specs/core-app-full-stack-readiness/pending-tasks.md
  - all files produced by completed tasks in this migration
  - skills/README.md
  - docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md
  - docs/examples/ai-first-saas-seed-app-description/README.md
- skills:
  - none; repository review task
- expected outputs:
  - specs/core-app-full-stack-readiness/final-consistency-review.md
- required checks:
  - git diff --check
- done criteria:
  - final review records pass/fail and follow-up tasks
  - task changes and queue update are committed
- notes:
  - commit message: `Add final consistency review`

### TASK-CORE-06-003: Write migration completion summary

- status: pending
- source: specs/core-app-full-stack-readiness/backlog/06-e2e-generation-review-build-backlog.md
- task brief: specs/core-app-full-stack-readiness/tasks/06-e2e-generation-review/03-write-migration-completion-summary.md
- depends on: [TASK-CORE-06-002]
- required reads:
  - specs/core-app-full-stack-readiness/final-consistency-review.md
  - specs/core-app-full-stack-readiness/pending-tasks.md
  - git log --oneline -- specs/core-app-full-stack-readiness
- skills:
  - none; repository review task
- expected outputs:
  - specs/core-app-full-stack-readiness/migration-completion-summary.md
- required checks:
  - git diff --check
- done criteria:
  - migration has a durable completion summary
  - task changes and queue update are committed
