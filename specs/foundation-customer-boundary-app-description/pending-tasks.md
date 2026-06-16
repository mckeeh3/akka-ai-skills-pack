# Pending Tasks: Foundation Customer Boundary App-description

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Mark exactly one selected task `in-progress` before implementation edits.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, conversation capture, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each completed task must make one focused git commit containing the intended changes and queue update.
- Commit message format: `customer-boundary-desc: <short task title>`.

## Tasks

### TASK-FCBAD-00-001: Create foundation customer boundary app-description mini-project

- status: done
- source: user discussion requesting foundation customer boundary app-description queue
- task brief: specs/foundation-customer-boundary-app-description/tasks/00-planning/00-create-mini-project.md
- depends on: []
- required reads:
  - AGENTS.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
  - .agents/skills/docs/current-intent-model.md
  - .agents/skills/docs/intent-to-realization-flow.md
  - .agents/skills/docs/intent-compiler-skill-contracts.md
  - current conversation context
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/foundation-customer-boundary-app-description/README.md
  - specs/foundation-customer-boundary-app-description/conversation-capture.md
  - specs/foundation-customer-boundary-app-description/sprints/01-foundation-customer-boundary-description-sprint.md
  - specs/foundation-customer-boundary-app-description/backlog/01-foundation-customer-boundary-app-description-backlog.md
  - specs/foundation-customer-boundary-app-description/tasks/**
  - specs/foundation-customer-boundary-app-description/pending-tasks.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold and durable task queue exist
  - first non-done task is runnable without guessing
  - terminal verification task can append follow-up tasks and a replacement terminal verification task if ambiguity remains
- notes:
  - vertical contract: docs-only planning scaffold for root app-description current-intent capture; foundation customer boundary scope; no runtime/API/UI behavior change; validation path `git diff --check`

### TASK-FCBAD-01-001: Inventory customer-boundary evidence and graph gaps

- status: done
- source: specs/foundation-customer-boundary-app-description/backlog/01-foundation-customer-boundary-app-description-backlog.md#fcbad-01-inventory-current-customer-boundary-evidence-and-graph-gaps
- task brief: specs/foundation-customer-boundary-app-description/tasks/01-description/01-inventory-evidence-and-gaps.md
- depends on:
  - TASK-FCBAD-00-001
- required reads:
  - AGENTS.md
  - .agents/skills/docs/current-intent-model.md
  - .agents/skills/docs/intent-to-realization-flow.md
  - app-description/AGENTS.md
  - app-description/README.md
  - specs/foundation-customer-boundary-app-description/README.md
  - specs/foundation-customer-boundary-app-description/conversation-capture.md
  - specs/foundation-customer-boundary-app-description/backlog/01-foundation-customer-boundary-app-description-backlog.md
  - specs/foundation-customer-boundary-app-description/tasks/01-description/01-inventory-evidence-and-gaps.md
- skills:
  - app-description-change-impact
  - capability-first-backend
  - ai-first-saas
- expected outputs:
  - specs/foundation-customer-boundary-app-description/customer-boundary-evidence-and-gap-map.md
- required checks:
  - `git diff --check`
  - targeted search proof recorded in notes
- done criteria:
  - evidence/gap map identifies active graph nodes to edit and non-goal runtime work
  - ambiguity/blockers are recorded or the next description task is confirmed runnable
  - task changes and queue update are committed
- notes:
  - vertical contract: docs-only current-intent inventory; foundation customer boundary and User Admin customer lifecycle/Customer Admin branch; no runtime behavior change; validation path `git diff --check` plus search proof
  - completed output: `specs/foundation-customer-boundary-app-description/customer-boundary-evidence-and-gap-map.md`
  - checks: `git diff --check` passed
  - search proof: `rg -n "Customer|customer|tenant\.customer|action-customer|TenantCustomer" app-description src/main/java frontend/src src/test/java --glob '!**/node_modules/**'` found active app-description, backend, frontend, and test evidence
  - blocker assessment: no pending question required; `TASK-FCBAD-01-002` is runnable
  - commit message: `customer-boundary-desc: inventory evidence and gaps`

### TASK-FCBAD-01-002: Capture customer boundary domain, capability, and state intent

- status: done
- source: specs/foundation-customer-boundary-app-description/backlog/01-foundation-customer-boundary-app-description-backlog.md#fcbad-02-capture-domain-capability-and-state-intent
- task brief: specs/foundation-customer-boundary-app-description/tasks/01-description/02-capture-domain-capability-state.md
- depends on:
  - TASK-FCBAD-01-001
- required reads:
  - AGENTS.md
  - .agents/skills/docs/current-intent-model.md
  - .agents/skills/docs/intent-compiler-skill-contracts.md
  - app-description/AGENTS.md
  - specs/foundation-customer-boundary-app-description/README.md
  - specs/foundation-customer-boundary-app-description/conversation-capture.md
  - specs/foundation-customer-boundary-app-description/customer-boundary-evidence-and-gap-map.md
  - specs/foundation-customer-boundary-app-description/tasks/01-description/02-capture-domain-capability-state.md
- skills:
  - app-description-capability-modeling
  - app-description-auth-security
  - app-description-behavior-specification
- expected outputs:
  - active app-description domain/capability/state updates for foundation customer boundary
- required checks:
  - `git diff --check`
  - targeted app-description coverage proof recorded in notes
- done criteria:
  - foundation customer boundary ownership, non-goals, capabilities, state responsibilities, tenant/customer scoping, Customer Admin semantics, and business-domain separation are captured
  - no runtime code is changed
  - task changes and queue update are committed
- notes:
  - vertical contract: docs-only current-intent capture; foundation customer boundary domain/capability/state; `tenant.customer.*` and Customer Admin branch scope; validation path `git diff --check` plus targeted `rg` proof
  - changed app-description nodes: `app-description/app.md`, `app-description/domains/core-starter/domain.md`, `app-description/domains/core-starter/data-state/auth-context-and-membership-state.md`, `app-description/domains/core-starter/capabilities/user-and-access-administration.md`
  - checks: `git diff --check` passed
  - targeted proof: `rg -n 'foundation customer boundary|foundation Customer boundary|business-domain|CRM|tenant\\.customer\\.\\*|tenant/customer scoping|selected AuthContext|Customer Admin' app-description/app.md app-description/domains/core-starter/domain.md app-description/domains/core-starter/data-state/auth-context-and-membership-state.md app-description/domains/core-starter/capabilities/user-and-access-administration.md` found active app-description domain/capability/state coverage
  - runtime impact: no runtime code changed
  - commit message: `customer-boundary-desc: capture domain capability state`

### TASK-FCBAD-01-003: Capture customer boundary workstream, surfaces, agents, and realization bindings

- status: pending
- source: specs/foundation-customer-boundary-app-description/backlog/01-foundation-customer-boundary-app-description-backlog.md#fcbad-03-capture-workstream-surface-agent-tool-policy-trace-test-and-realization-bindings
- task brief: specs/foundation-customer-boundary-app-description/tasks/01-description/03-capture-workstream-surfaces-agents-realization.md
- depends on:
  - TASK-FCBAD-01-002
- required reads:
  - AGENTS.md
  - .agents/skills/docs/current-intent-model.md
  - .agents/skills/docs/intent-to-realization-flow.md
  - .agents/skills/docs/intent-compiler-skill-contracts.md
  - app-description/AGENTS.md
  - specs/foundation-customer-boundary-app-description/README.md
  - specs/foundation-customer-boundary-app-description/conversation-capture.md
  - specs/foundation-customer-boundary-app-description/customer-boundary-evidence-and-gap-map.md
  - specs/foundation-customer-boundary-app-description/tasks/01-description/03-capture-workstream-surfaces-agents-realization.md
- skills:
  - app-description-surface-modeling
  - app-description-functional-agent-modeling
  - app-description-observability
  - app-description-test-specification
- expected outputs:
  - active app-description workstream/surface/agent/tool/policy/trace/test/realization updates for foundation customer boundary
- required checks:
  - `git diff --check`
  - targeted app-description coverage proof recorded in notes
- done criteria:
  - User Admin customer boundary branch, surfaces, actions, functional-agent authority, governed tools, policy/denial behavior, traces/tests, and Akka/API/frontend realization mapping are captured
  - no runtime code is changed
  - task changes and queue update are committed
- notes:
  - vertical contract: User Admin / `user-admin-agent`; docs-only surface/action/governed-tool/current-intent capture; customer directory/detail/lifecycle/Customer Admin branch; `tenant.customer.*` and `tenant.customer_admin.*`; selected tenant/customer AuthContext semantics; validation path `git diff --check` plus targeted `rg` proof

### TASK-FCBAD-01-004: Verify foundation customer boundary app-description sufficiency

- status: pending
- source: specs/foundation-customer-boundary-app-description/backlog/01-foundation-customer-boundary-app-description-backlog.md#fcbad-04-verify-unambiguous-description-and-loop-if-needed
- task brief: specs/foundation-customer-boundary-app-description/tasks/02-verification/04-verify-description-sufficiency.md
- depends on:
  - TASK-FCBAD-01-003
- required reads:
  - AGENTS.md
  - .agents/skills/docs/current-intent-model.md
  - .agents/skills/docs/intent-to-realization-flow.md
  - .agents/skills/docs/intent-compiler-skill-contracts.md
  - app-description/AGENTS.md
  - specs/foundation-customer-boundary-app-description/README.md
  - specs/foundation-customer-boundary-app-description/conversation-capture.md
  - specs/foundation-customer-boundary-app-description/customer-boundary-evidence-and-gap-map.md
  - specs/foundation-customer-boundary-app-description/backlog/01-foundation-customer-boundary-app-description-backlog.md
  - specs/foundation-customer-boundary-app-description/pending-tasks.md
  - specs/foundation-customer-boundary-app-description/tasks/02-verification/04-verify-description-sufficiency.md
- skills:
  - app-description-readiness-assessment
  - app-description-readiness-summary
  - app-description-change-impact
- expected outputs:
  - specs/foundation-customer-boundary-app-description/verification/foundation-customer-boundary-sufficiency-review.md
  - updated pending-tasks.md; append follow-up tasks plus new terminal verification task if gaps remain
- required checks:
  - `git diff --check`
  - targeted app-description coverage proof recorded in review
- done criteria:
  - review explicitly answers whether the foundation customer boundary description is sufficiently unambiguous
  - if yes, mini-project done state is met
  - if no, follow-up bounded tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - vertical contract: docs-only verifier/reviewer for foundation customer boundary current intent; no runtime behavior change; validates tenant/customer scoping, Customer Admin limits, realization mapping, audit/work trace, and test description sufficiency
