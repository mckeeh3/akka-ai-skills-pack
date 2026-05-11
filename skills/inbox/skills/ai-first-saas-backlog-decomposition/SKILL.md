---
name: ai-first-saas-backlog-decomposition
description: Convert ai-first SaaS specs into realistic epics, stories, tasks, sequencing, dependencies, and Akka + React/Vite/TypeScript implementation backlogs.
---

# ai-first-saas-backlog-decomposition

Use this skill when a coding agent must turn PRDs, product specs, ai-first SaaS specs, or outputs from other planning skills into implementation backlogs, sprints, epics, stories, and engineering tasks.

This skill complements general PRD/spec-to-backlog skills. Use those skills for ordinary product decomposition, estimation, acceptance criteria, and project-management formatting. Use this skill to add the ai-first substrate work that conventional backlog decomposition often misses.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical contract for goals, agents, policies, approvals, traces, replay, audit, outcomes, and required UI surfaces.
- `docs/skills-pack-tech-stack.md` defines the target Akka + React/Vite/TypeScript stack and the expected full-stack decomposition.
- Load related skills when their outputs are available: object model, maturity model, agent team design, policy governance, UI surfaces, decision cards, audit trace, runtime orchestration, permission enforcement, replay/simulation, security/privacy, testing/evaluation, outcomes/metrics, and curation/digest.

## Required inputs

Prefer these inputs, but proceed with explicit assumptions if some are missing:

- product objective, domain, users, and maturity target;
- ai-first applicability assessment;
- object model and lifecycle/state-machine definitions;
- agent roster, permissions, tools, data scopes, and authority boundaries;
- policy/skill governance requirements;
- required UI surfaces and decision-card specs;
- audit, trace, replay, security, privacy, and test requirements;
- target release constraints, team shape, and sprint length.

## Decomposition procedure

1. **Confirm maturity scope**
   - Label the backlog as `prototype`, `mvp`, `production`, or `regulated_high_risk`.
   - Exclude advanced substrate work that exceeds the target maturity unless it is a hard requirement.
   - Mark future-hardening tasks explicitly instead of silently overbuilding.

2. **Extract backlog domains**
   Create epics for the ai-first capabilities that must exist:
   - substrate models;
   - agent definitions and compiled runtime context;
   - policy, skill, prompt, and version governance;
   - runtime orchestration and approval pause/resume;
   - permission enforcement and policy evaluation;
   - UI surfaces and typed frontend clients;
   - audit and work trace storage;
   - replay and simulation;
   - security and privacy controls;
   - outcomes, metrics, curation, and digests when in scope;
   - tests, evals, and operational readiness.

3. **Map each capability to the target stack**
   For each epic/story identify:
   - Akka Event Sourced Entities for state requiring event history, auditability, replay, or temporal reasoning.
   - Akka Key Value Entities for simpler current-state records.
   - Akka Workflows for long-running goals, plans, approvals, retries, compensation, and pause/resume flows.
   - Akka Views for CQRS read models, dashboards, queues, traces, governance screens, and metrics.
   - Akka Consumers for reacting to events, updating projections, producing audit records, notifications, curation, or analytics.
   - Timed Actions/timers for deadlines, reminders, expirations, scheduled digests, and retry loops.
   - Akka agents for planning, classification, recommendations, summaries, evaluation, or tool-mediated work.
   - HTTP/gRPC/MCP endpoints for browser/API access and tool/resource exposure.
   - React/Vite/TypeScript screens, typed API clients, UI state, validation, accessibility, and real-time updates via SSE/WebSocket/streaming where needed.

4. **Break epics into vertical slices**
   Favor thin end-to-end slices that create durable objects, commands, events, read models, UI, audit events, and tests together. Avoid building a large opaque agent before permissions, policies, traces, and approval gates exist.

5. **Add acceptance criteria and tests**
   Every story involving agent action must include criteria for authorization, policy clause references, audit events, trace links, human review/approval behavior, and failure handling.

6. **Sequence by dependencies**
   Build foundational durable objects and command paths first, then orchestration, then UI and advanced intelligence. Replay, advanced digest curation, analytics, and governance polish should not precede the event/trace substrate they depend on.

## Recommended epic catalog

Use or adapt these epics.

### Epic: AI-first substrate models

Purpose: create durable, linked objects for goals, objectives, execution plans, agents, policies, approvals, decisions, exceptions, traces, events, and outcomes.

Typical tasks:

- Define entity schemas, IDs, tenant boundaries, timestamps, statuses, and version references.
- Implement command handlers for `Goal`, `ExecutionPlan`, `ApprovalRequest`, `Decision`, `Exception`, `PolicyDocument`, `AgentDefinition`, `WorkTrace`, and `OutcomeMetric`.
- Choose Event Sourced Entities for history-sensitive objects and Key Value Entities for simpler records.
- Create Views for active goals, pending approvals, agent roster, policy inventory, trace lookup, and outcome summaries.
- Add migration/seed data for initial agents, policies, and sample goals.

### Epic: Agent definitions and runtime context

Typical tasks:

- Implement versioned `AgentSystemPrompt`, `AgentSkill`, skill component, and assignment records.
- Build compiled runtime context generation from prompt versions, skills, policies, rules, tools, data scopes, and authority boundaries.
- Snapshot or reference context versions for every agent run.
- Add agent roster read models and UI panels.
- Add tests that prompt/skill/policy version changes do not mutate historical executions.

### Epic: Policy, skill, and governance versioning

Typical tasks:

- Implement proposal, review, commit, reject, rollback, and activation workflows for prompts, skills, policies, rules, thresholds, guardrails, and approval gates.
- Require human authorization before active behavior changes.
- Store stable policy clause IDs and skill rule IDs.
- Build diff, provenance, and recent-changes views.
- Add governance UI for draft/approved/deprecated states and replay-before-commit hooks.

### Epic: Runtime orchestration and agent work

Typical tasks:

- Implement Goal-to-Execution workflow: goal intake, plan generation, plan review, activation, agent task dispatch, completion, and closure.
- Implement approval-gate pause/resume, exception escalation, retry, timeout, and compensation paths.
- Define task-run and tool-invocation lifecycles with idempotency keys.
- Add Akka agents for coordinator and specialists with bounded tools and structured outputs.
- Add Consumers for work progress, notifications, trace updates, and projection refresh.

### Epic: Permission enforcement and policy evaluation

Typical tasks:

- Implement policy evaluator contracts for action disposition: `auto`, `review`, `approval`, `escalate`, `denied`, or `fyi`.
- Enforce tool permissions, data scopes, tenant isolation, approval gates, and side-effect boundaries in backend code.
- Deny forbidden model-requested actions before tool calls or external side effects.
- Record enforcement audit events and denial reasons.
- Add unit and workflow tests proving prompts are not the enforcement boundary.

### Epic: Required UI surfaces

Typical tasks:

- Build Goal-to-Execution Workbench for durable goal creation, generated plan review, permission/approval visibility, and activation.
- Build Command Center/Mission Control for active goals, agents, activity streams, queues, and compressed routine activity.
- Build Decision Card UI with evidence, risk, confidence, policy trigger, alternatives, precedents, actions, and learning option.
- Build Governance/Learning Center for agent roster, prompts, skills, policies, rules, versioning, proposals, and replay results.
- Build Async Digest/Executive Briefing when curation is in scope.
- Build Audit and Work Trace views reachable from every artifact.
- Implement typed TypeScript API clients, loading/empty/error states, accessibility, and real-time updates where appropriate.

### Epic: Audit, trace, replay, and simulation

Typical tasks:

- Emit meaningful audit events from commands, workflows, policy evaluations, tool calls, data access, human decisions, and governance commits.
- Persist work traces with evidence, policy invocations, tool invocations, data access, alternatives, structured rationale, and outcome links.
- Implement replay fixtures and no-side-effect sandbox execution for proposed prompt/skill/policy/threshold changes.
- Create replay result schemas, diff categories, and UI read models.
- Add retention, redaction, export, and trace lookup tasks.

### Epic: Security, privacy, and operations

Typical tasks:

- Define tenant isolation, role-based access control, secrets handling, PII redaction, provider data policies, retention, deletion, and export behavior.
- Add prompt-injection and untrusted-tool-output defenses.
- Require human approval for sensitive, irreversible, or external side effects.
- Add operational metrics for workflow failures, denied actions, approval latency, replay regressions, and agent error rates.
- Add structured logs without treating logs as the audit source of truth.

### Epic: Testing, evaluation, and acceptance

Typical tasks:

- Add backend entity, workflow, consumer, view, endpoint, and policy evaluator tests.
- Add deterministic agent tests with mocked model/tool responses and structured-output validation.
- Add golden trace and replay regression tests.
- Add adversarial prompt-injection, permission denial, tenant isolation, approval gate, and human-in-the-loop tests.
- Add React UI tests for goal creation, command center, decision cards, governance, digest, and trace drill-down.
- Add outcome validation tests and calibration checks where applicable.

## Sequencing and dependency guidance

Default sequence:

1. Intake assumptions, maturity scope, domain entities, ai-first substrate object model.
2. Core commands/entities/events for goals, plans, agents, policies, approvals, decisions, exceptions, traces, and audit events.
3. CQRS Views and HTTP APIs needed by the first frontend slice.
4. Goal-to-Execution Workbench with plan review and activation.
5. Minimal coordinator/specialist agent definitions with compiled context snapshots.
6. Policy evaluator, permission enforcement, and approval-gate workflow before real side effects.
7. Command Center and Decision Card UI backed by real workflows and traces.
8. Governance/versioning for prompts, skills, policies, thresholds, and human-approved activation.
9. Audit trace drill-down and retention/redaction controls.
10. Replay/simulation after enough historical traces and deterministic fixtures exist.
11. Digest, curation, advanced metrics, calibration, and optimization after core execution is reliable.
12. Production hardening: security, privacy, observability, load, failure recovery, and operational runbooks.

Dependency rules:

- Do not build advanced replay UI before traces, policy versions, prompt/skill versions, and replay fixtures exist.
- Do not allow autonomous side effects before permission enforcement, approval gates, audit events, and rollback/reversibility rules exist.
- Do not build a digest as a simple notification list; it depends on material/routine classification and trace links.
- Do not build governance commits without provenance, human authorization, version links, and historical execution preservation.
- Do not treat agent implementation as complete until tests cover denied actions, low-confidence routing, high-risk approvals, and audit trace completeness.

## Epic/story/task output format

Produce backlog output in this structure:

```yaml
backlog:
  product_name: string
  maturity_level: prototype | mvp | production | regulated_high_risk
  planning_assumptions:
    - string
  sequencing_notes:
    - string
  epics:
    - id: EPIC-001
      name: string
      purpose: string
      agent_first_capabilities:
        - substrate_models | agent_definitions | policy_governance | runtime_orchestration | permission_enforcement | ui_surfaces | audit_trace | replay_simulation | security_privacy | outcomes_metrics | testing_evaluation
      stack_mapping:
        akka_entities:
          event_sourced: string[]
          key_value: string[]
        akka_workflows: string[]
        akka_views: string[]
        akka_consumers: string[]
        timed_actions: string[]
        akka_agents: string[]
        endpoints:
          http: string[]
          grpc: string[]
          mcp: string[]
        frontend:
          react_screens: string[]
          typed_clients: string[]
          realtime_patterns: string[]
      dependencies:
        - EPIC-000 or STORY-000
      stories:
        - id: STORY-001
          title: string
          user_or_system_value: string
          primary_human_roles: string[]
          agent_first_objects: string[]
          implementation_tasks:
            backend:
              - string
            frontend:
              - string
            agent_behavior:
              - string
            policy_permissions_audit:
              - string
            tests:
              - string
            operations:
              - string
          acceptance_criteria:
            - string
          audit_events:
            - name: string
              required_fields: string[]
          risks_or_open_questions:
            - string
          estimate: xs | s | m | l | xl | unknown
          release_slice: foundation | mvp | hardening | future
```

## Backlog quality checklist

Before returning a backlog, verify:

- [ ] Durable goals and execution plans are implemented before advanced supervision or replay features.
- [ ] Agent definitions include prompt, skill, policy, tool, data-scope, and authority-boundary work.
- [ ] Platform-enforced permissions and approval gates appear before autonomous side effects.
- [ ] Every decision/approval story includes evidence, policy clause references, alternatives or rationale summary, human action, and audit events.
- [ ] Audit and work trace tasks are not replaced by generic application logging.
- [ ] UI stories map to the six required ai-first surfaces when in scope.
- [ ] Replay/simulation depends on versioned prompts, skills, policies, fixtures, and no-side-effect execution.
- [ ] Security/privacy tasks include tenant isolation, sensitive data, prompt injection, provider data handling, retention, and export/deletion.
- [ ] Testing tasks cover backend components, frontend screens, agent behavior, policy evaluation, permission denial, approval workflows, replay regressions, and adversarial cases.
- [ ] Sequencing notes make tradeoffs explicit and avoid overbuilding beyond the maturity target.
