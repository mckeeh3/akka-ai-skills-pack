# Pending Tasks: Autonomous Agents Integration

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, conversation capture, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Treat this repository as the skills-pack source; most changes target `docs/`, `skills/`, `src/`, `templates/`, `pack/`, and supporting `specs/` files.
- Request-based Akka `Agent` remains the default for bounded user-facing workstream turns; Akka `AutonomousAgent` becomes the default for durable task-oriented internal/background agent work when its semantics fit.
- Preserve governed runtime requirements: tenant isolation, backend authorization, model policy, provider-secret boundary, ToolPermissionBoundary, approval gates, and audit/work traces.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes.
- Commit message format: `autonomous-agents: <short task title>`.

## Tasks

### TASK-AUTO-00-001: Create autonomous agents integration queue

- status: done
- source: user-confirmed discussion about integrating newly released Akka Autonomous Agents into this skills pack
- task brief: specs/autonomous-agents-integration/tasks/00-planning/00-create-autonomous-agents-integration-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - docs/pending-question-queue.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/autonomous-agents-integration/README.md
  - specs/autonomous-agents-integration/conversation-capture.md
  - specs/autonomous-agents-integration/pending-tasks.md
  - specs/autonomous-agents-integration/sprints/01-autonomous-agents-first-pass-sprint.md
  - specs/autonomous-agents-integration/backlog/01-autonomous-agents-first-pass-backlog.md
  - specs/autonomous-agents-integration/tasks/**/*.md
- required checks:
  - `git diff --check`
  - `rg -n "TASK-AUTO|AutonomousAgent|Autonomous Agents|request-based" specs/autonomous-agents-integration`
- done criteria:
  - mini-project has captured rationale, sprint sequence, backlog, task briefs, and pending queue
  - future tasks require one focused git commit before being marked done
  - planning scaffold is committed
- notes:
  - commit message: `autonomous-agents: add integration queue`

### TASK-AUTO-01-001: Create deep Autonomous Agents research notes

- status: done
- source: specs/autonomous-agents-integration/backlog/01-autonomous-agents-first-pass-backlog.md
- task brief: specs/autonomous-agents-integration/tasks/01-research/01-deep-autonomous-agents-research-notes.md
- depends on: [TASK-AUTO-00-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/autonomous-agents-integration/README.md
  - specs/autonomous-agents-integration/conversation-capture.md
  - specs/autonomous-agents-integration/sprints/01-autonomous-agents-first-pass-sprint.md
  - specs/autonomous-agents-integration/backlog/01-autonomous-agents-first-pass-backlog.md
  - akka-context/sdk/autonomous-agents.html.md
  - akka-context/sdk/autonomous-agents/defining.html.md
  - akka-context/sdk/autonomous-agents/tasks.html.md
  - akka-context/sdk/autonomous-agents/coordination.html.md
  - akka-context/sdk/autonomous-agents/capabilities.html.md
  - akka-context/sdk/autonomous-agents/client.html.md
  - akka-context/sdk/autonomous-agents/notifications.html.md
  - akka-context/sdk/autonomous-agents/testing.html.md
  - akka-context/sdk/use-cases/autonomous-agents.html.md
- skills:
  - none; repository research task
- expected outputs:
  - specs/autonomous-agents-integration/research-notes.md
- required checks:
  - `git diff --check`
  - `rg -n "AutonomousAgent|TaskAcceptance|TaskTemplate|forAutonomousAgent|AutonomousAgentTools|Agent vs AutonomousAgent|Workflow" specs/autonomous-agents-integration/research-notes.md`
- done criteria:
  - notes are source-linked, accurate, and agent-optimized
  - notes call out Akka autonomous `AgentDefinition` vs governed managed-agent `AgentDefinition`
  - focused git commit exists
- notes:
  - commit message: `autonomous-agents: add research notes`
  - completed with research notes in `specs/autonomous-agents-integration/research-notes.md`

### TASK-AUTO-02-001: Update doctrine and routing for Autonomous Agents

- status: done
- source: specs/autonomous-agents-integration/backlog/01-autonomous-agents-first-pass-backlog.md
- task brief: specs/autonomous-agents-integration/tasks/02-doctrine/01-update-autonomous-agent-doctrine-routing.md
- depends on: [TASK-AUTO-01-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - docs/agent-workstream-application-architecture.md
  - docs/agent-coverage-matrix.md
  - specs/autonomous-agents-integration/research-notes.md
- skills:
  - ai-first-saas
  - capability-first-backend
- expected outputs:
  - doctrine/routing updates in docs and skills/README.md
  - optional focused decision-guide doc
- required checks:
  - `git diff --check`
  - `rg -n "AutonomousAgent|Autonomous Agent|request-based Agent|Workflow|background|internal agent|AgentDefinition" docs skills/README.md`
- done criteria:
  - internal/background-agent default is clear
  - workstream request/response routing remains explicit
  - focused git commit exists
- notes:
  - commit message: `autonomous-agents: update doctrine routing`
  - added `docs/agent-component-selection-guide.md` and routed core doctrine to request-based Agent vs AutonomousAgent vs Workflow selection

### TASK-AUTO-02-002: Align starter and generated-app background-agent guidance

- status: pending
- source: specs/autonomous-agents-integration/backlog/01-autonomous-agents-first-pass-backlog.md
- task brief: specs/autonomous-agents-integration/tasks/02-doctrine/02-align-starter-background-agent-guidance.md
- depends on: [TASK-AUTO-02-001, TASK-AUTO-03-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - templates/ai-first-saas-starter/README.md
  - docs/minimum-ai-first-saas-app.md
  - docs/core-ai-first-saas-foundation.md
  - docs/agent-workstream-application-architecture.md
  - docs/ai-first-saas-application-architecture.md
  - specs/autonomous-agents-integration/research-notes.md
  - skills/akka-autonomous-agents/SKILL.md
- skills:
  - akka-autonomous-agents
  - ai-first-saas
- expected outputs:
  - starter/generated-app guidance updates
- required checks:
  - `git diff --check`
  - `rg -n "AutonomousAgent|Autonomous Agent|background|internal agent|workstream|request-based" templates/ai-first-saas-starter docs skills/README.md`
- done criteria:
  - generated-app/starter background-agent guidance uses Autonomous Agents where appropriate
  - workstream request/response guidance remains intact
  - focused git commit exists
- notes:
  - commit message: `autonomous-agents: align starter guidance`

### TASK-AUTO-03-001: Add Autonomous Agent skill family

- status: pending
- source: specs/autonomous-agents-integration/backlog/01-autonomous-agents-first-pass-backlog.md
- task brief: specs/autonomous-agents-integration/tasks/03-skills/01-add-autonomous-agent-skill-family.md
- depends on: [TASK-AUTO-02-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - pack/manifest.yaml
  - specs/autonomous-agents-integration/research-notes.md
  - docs/agent-coverage-matrix.md
  - skills/akka-agents/SKILL.md
  - skills/akka-agent-component/SKILL.md
  - skills/akka-agent-testing/SKILL.md
  - skills/akka-agent-orchestration/SKILL.md
  - skills/akka-agent-tools/SKILL.md
  - skills/akka-agent-tool-boundaries/SKILL.md
- skills:
  - none; creating new installable skills
- expected outputs:
  - skills/akka-autonomous-agents/SKILL.md
  - skills/akka-autonomous-agent-tasks/SKILL.md
  - skills/akka-autonomous-agent-coordination/SKILL.md
  - skills/akka-autonomous-agent-testing/SKILL.md
  - skills/akka-autonomous-agent-governance/SKILL.md
  - routing/manifest updates
- required checks:
  - `git diff --check`
  - `rg -n "akka-autonomous-agent|AutonomousAgent|TaskAcceptance|forAutonomousAgent|AutonomousAgentTools" skills pack/manifest.yaml skills/README.md`
- done criteria:
  - skills are focused, low-token, and exported/discoverable according to repo convention
  - skills state when not to use Autonomous Agents
  - focused git commit exists
- notes:
  - commit message: `autonomous-agents: add skill family`

### TASK-AUTO-03-002: Update agent guidance and coverage matrix

- status: pending
- source: specs/autonomous-agents-integration/backlog/01-autonomous-agents-first-pass-backlog.md
- task brief: specs/autonomous-agents-integration/tasks/03-skills/02-update-agent-guidance-coverage.md
- depends on: [TASK-AUTO-03-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/agent-coverage-matrix.md
  - specs/autonomous-agents-integration/research-notes.md
  - skills/akka-agents/SKILL.md
  - skills/akka-agent-component/SKILL.md
  - skills/akka-agent-orchestration/SKILL.md
  - skills/akka-agent-testing/SKILL.md
  - skills/akka-autonomous-agents/SKILL.md
  - skills/akka-autonomous-agent-tasks/SKILL.md
  - skills/akka-autonomous-agent-coordination/SKILL.md
  - skills/akka-autonomous-agent-testing/SKILL.md
- skills:
  - akka-autonomous-agents
  - akka-autonomous-agent-testing
- expected outputs:
  - docs/agent-coverage-matrix.md updates
  - cross-links in existing agent/workflow/testing skills as needed
- required checks:
  - `git diff --check`
  - `rg -n "Autonomous Agent|AutonomousAgent|autonomous-agents|request-based|Workflow" docs/agent-coverage-matrix.md skills`
- done criteria:
  - coverage matrix accurately records new coverage/gaps
  - existing guidance no longer implies request-based Agents are the only Akka agent component
  - focused git commit exists
- notes:
  - commit message: `autonomous-agents: update guidance coverage`

### TASK-AUTO-04-001: Add single Autonomous Agent executable example

- status: pending
- source: specs/autonomous-agents-integration/backlog/01-autonomous-agents-first-pass-backlog.md
- task brief: specs/autonomous-agents-integration/tasks/04-examples/01-add-single-autonomous-agent-example.md
- depends on: [TASK-AUTO-03-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/autonomous-agents-integration/research-notes.md
  - skills/akka-autonomous-agents/SKILL.md
  - skills/akka-autonomous-agent-tasks/SKILL.md
  - skills/akka-autonomous-agent-testing/SKILL.md
  - akka-context/sdk/autonomous-agents.html.md
  - akka-context/sdk/autonomous-agents/defining.html.md
  - akka-context/sdk/autonomous-agents/tasks.html.md
  - akka-context/sdk/autonomous-agents/client.html.md
  - akka-context/sdk/autonomous-agents/testing.html.md
- skills:
  - akka-autonomous-agents
  - akka-autonomous-agent-tasks
  - akka-autonomous-agent-testing
- expected outputs:
  - src/main/java/com/example/... Autonomous Agent task/result/example files
  - src/test/java/com/example/... tests
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "extends AutonomousAgent|TaskAcceptance|forAutonomousAgent|AutonomousAgentTools|completeTask" src/main/java src/test/java`
- done criteria:
  - test proves typed task completion through Akka Autonomous Agent path
  - no normal-runtime fake is introduced
  - focused git commit exists
- notes:
  - commit message: `autonomous-agents: add single agent example`

### TASK-AUTO-04-002: Add Autonomous Agent coordination executable example

- status: pending
- source: specs/autonomous-agents-integration/backlog/01-autonomous-agents-first-pass-backlog.md
- task brief: specs/autonomous-agents-integration/tasks/04-examples/02-add-autonomous-agent-coordination-example.md
- depends on: [TASK-AUTO-04-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/autonomous-agents-integration/research-notes.md
  - skills/akka-autonomous-agent-coordination/SKILL.md
  - skills/akka-autonomous-agent-tasks/SKILL.md
  - skills/akka-autonomous-agent-testing/SKILL.md
  - akka-context/sdk/autonomous-agents/coordination.html.md
  - akka-context/sdk/autonomous-agents/capabilities.html.md
  - akka-context/sdk/autonomous-agents/testing.html.md
- skills:
  - akka-autonomous-agent-coordination
  - akka-autonomous-agent-tasks
  - akka-autonomous-agent-testing
- expected outputs:
  - src/main/java/com/example/... coordination example files
  - src/test/java/com/example/... coordination tests
  - docs/agent-coverage-matrix.md update if status changes
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "Delegation|delegateTo|AutonomousAgent|TaskAcceptance|completeTask" src/main/java src/test/java docs/agent-coverage-matrix.md`
- done criteria:
  - test proves coordinator delegates to worker and returns typed result
  - focused git commit exists
- notes:
  - commit message: `autonomous-agents: add coordination example`

### TASK-AUTO-05-001: Verify first-pass Autonomous Agents migration

- status: pending
- source: specs/autonomous-agents-integration/backlog/01-autonomous-agents-first-pass-backlog.md
- task brief: specs/autonomous-agents-integration/tasks/05-review/01-verify-first-pass-migration.md
- depends on: [TASK-AUTO-02-002, TASK-AUTO-03-002, TASK-AUTO-04-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/autonomous-agents-integration/README.md
  - specs/autonomous-agents-integration/conversation-capture.md
  - specs/autonomous-agents-integration/pending-tasks.md
  - specs/autonomous-agents-integration/research-notes.md
  - docs/agent-coverage-matrix.md
- skills:
  - akka-autonomous-agents
  - akka-autonomous-agent-testing
- expected outputs:
  - verification notes or queue follow-up tasks
  - another verification task if follow-up implementation tasks are appended
- required checks:
  - `git diff --check`
  - `rg -n "AutonomousAgent|Autonomous Agent|request-based Agent|Workflow|AgentDefinition|TaskAcceptance|forAutonomousAgent" docs skills src templates specs/autonomous-agents-integration`
- done criteria:
  - review findings are recorded
  - follow-up tasks and another verify task are appended if gaps remain
  - focused git commit exists
- notes:
  - commit message: `autonomous-agents: verify first pass`

### TASK-AUTO-05-002: Define additional Autonomous Agent example tasks

- status: pending
- source: user request to add a final task after verification that defines necessary tasks for additional examples
- task brief: specs/autonomous-agents-integration/tasks/05-review/02-define-additional-example-tasks.md
- depends on: [TASK-AUTO-05-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/autonomous-agents-integration/README.md
  - specs/autonomous-agents-integration/pending-tasks.md
  - specs/autonomous-agents-integration/verification-notes.md if present
  - specs/autonomous-agents-integration/research-notes.md
  - docs/agent-coverage-matrix.md
- skills:
  - akka-autonomous-agents
  - akka-autonomous-agent-coordination
  - akka-autonomous-agent-testing
- expected outputs:
  - updated pending-tasks.md with additional executable example tasks
  - optional specs/autonomous-agents-integration/additional-examples-plan.md
- required checks:
  - `git diff --check`
  - `rg -n "handoff|task dependencies|approval|notification|TaskRule|TeamLeadership|Moderation|ToolPermissionBoundary" specs/autonomous-agents-integration docs/agent-coverage-matrix.md`
- done criteria:
  - additional example work is captured as self-contained future tasks
  - another verify task is appended if new implementation tasks are added
  - focused git commit exists
- notes:
  - commit message: `autonomous-agents: plan additional examples`
