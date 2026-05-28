# Autonomous Agents First-Pass Verification Notes

## Scope

Verified the first-pass Autonomous Agents migration after completed TASK-AUTO work through TASK-AUTO-04-002.

Reviewed:
- queue, README, conversation capture, sprint, backlog, and task brief;
- research notes and coverage matrix;
- doctrine/routing files changed by the migration;
- new Autonomous Agent skills and manifest entries;
- starter/generated-app guidance;
- executable single-agent and delegation examples/tests.

## Findings

### Routing and doctrine

- `skills/README.md` routes durable internal/background agent work to Akka `AutonomousAgent` while keeping request-based Akka `Agent` as the default for user-facing workstream request/response turns.
- `docs/agent-component-selection-guide.md` gives clear selection rules for request-based `Agent`, `AutonomousAgent`, `Workflow`, `Workflow + Agent`, and `Workflow + AutonomousAgent`.
- AI-first SaaS, capability-first, agent-workstream, minimum-app, core-foundation, and starter guidance preserve the request-based workstream-agent runtime path and route durable internal/background work to Autonomous Agents when task lifecycle semantics fit.
- The Akka autonomous `AgentDefinition` vs governed managed-agent `AgentDefinition` terminology guardrail is present in core doctrine, selector guidance, starter guidance, research notes, and Autonomous Agent skills.

### Skill and manifest coverage

- New installable skills exist for Autonomous Agents, tasks, coordination, testing, and governance.
- `pack/manifest.yaml` exports the new skill family and includes it in the skill id list.
- Existing request-based agent, orchestration, testing, and workflow skills cross-link to Autonomous Agent guidance where durable task lifecycle or model-driven coordination is the better fit.
- The skill family preserves generated-app governance requirements: capability contracts, backend authorization, tenant/customer scope, model policy, provider-secret boundary, `ToolPermissionBoundary`, approval gates, traces, and fail-closed behavior.

### Executable examples and tests

- `AnswerQuestionAutonomousAgent` demonstrates a minimal typed task via `TaskAcceptance` and `runSingleTask` through `AutonomousQuestionEndpoint`.
- `AnswerQuestionAutonomousAgentIntegrationTest` proves endpoint-started typed task completion through the Akka Autonomous Agent task path using `TestModelProvider.AutonomousAgentTools.completeTask` as test infrastructure.
- `ResearchCoordinatorAutonomousAgent` and `ResearchWorkerAutonomousAgent` demonstrate delegation via `Delegation.to(...)` and typed top-level/worker tasks.
- `ResearchCoordinatorAutonomousAgentIntegrationTest` proves endpoint-started delegation to a worker and typed final completion.
- The examples use real Akka SDK components and test-only deterministic model providers; no normal-runtime deterministic substitute was introduced by this migration.

### Coverage matrix

- `docs/agent-coverage-matrix.md` marks single-agent Autonomous Agent task coverage as complete.
- Delegation coordination is test-backed, while handoff, team, moderation, approval, notification, and dependency/external-input examples remain explicitly partial/guidance-only.
- The current cleanup backlog already points to additional Autonomous Agent executable examples if verification finds them necessary.

## Follow-up decision

No blocking first-pass migration gap requires immediate implementation before the existing next planning task.

The remaining uncovered example areas are real but already match the purpose of `TASK-AUTO-05-002`: define additional executable example tasks for handoff, task dependencies/external input, approval, notifications, teams, moderation, or related governance-heavy scenarios. Therefore this verification does not append duplicate implementation tasks or another verification task.

## Checks

Required checks for this verification task:

```bash
git diff --check
rg -n "AutonomousAgent|Autonomous Agent|request-based Agent|Workflow|AgentDefinition|TaskAcceptance|forAutonomousAgent" docs skills src templates specs/autonomous-agents-integration
```

No code was changed, so no Maven test rerun was required by this review task.
