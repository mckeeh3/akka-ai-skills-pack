---
name: ai-first-saas-object-model
description: Select durable AI-first SaaS substrate objects such as goals, plans, policies, decisions, traces, and outcomes, then route them to Akka entity, workflow, view, consumer, timer, and endpoint skills.
---

# AI-First SaaS Object Model

Use this companion after `ai-first-saas` when a product or feature needs durable agentic operating objects before app-description, decomposition, or implementation work.

This is a modeling and routing skill. It does not replace domain modeling inside Event Sourced Entity, Key Value Entity, Workflow, View, or endpoint skills.

## Required reading

Read first:
- `../../../docs/ai-first-saas-application-architecture.md`
- `../ai-first-saas/SKILL.md`

Then load only the downstream component skills needed by the selected object types.

## Use when

Use for tasks that mention or imply:
- goals, objectives, missions, plans, tasks, campaigns, or cases
- policies, clauses, guardrails, examples, thresholds, or permissions
- recommendations, decisions, approval requests, exceptions, or escalations
- evidence, confidence, risk, impact, alternatives, or rationale
- audit events, work traces, decision traces, tool calls, or data access
- feedback, precedents, learned rules, simulations, outcome metrics, or rollback records

## Object selection rule

Create only objects needed for behavior, authorization, explainability, learning, audit, or outcome validation.

Do not generate every canonical AI-first object by default.

## Durable object categories

### Intent objects

Typical objects:
- `Goal`, `Objective`, `SuccessCriterion`, `Constraint`
- `PolicyDocument`, `PolicyClause`, `Guardrail`, `ReferenceExample`

Use when the app must preserve what the human wants, what limits apply, or what rules agents must cite.

Route to:
- audit-grade goals, policies, clauses, and rule changes → `akka-event-sourced-entities`
- simple current preferences or non-audit configuration → `akka-key-value-entities`
- goal launch, approval gates, and plan activation → `akka-workflows`
- goal lists, policy catalogs, and owner views → `akka-views`

### Execution objects

Typical objects:
- `AgentDefinition`, `AgentSkillVersion`, `ExecutionPlan`, `Task`
- `ToolInvocation`, `DataAccessEvent`, `WorkResult`

Use when delegated work must be inspectable, resumable, recoverable, or explainable.

Route to:
- plan lifecycle and task orchestration → `akka-workflows`
- bounded model behavior and tool use → `akka-agents`
- tool/data access history requiring audit → `akka-event-sourced-entities` or `akka-consumers`
- command-center read models → `akka-views`
- deadlines, reminders, retries, or periodic rechecks → `akka-timed-actions`

### Judgment objects

Typical objects:
- `Recommendation`, `Decision`, `ApprovalRequest`, `Exception`
- `Escalation`, `Counterproposal`, `Precedent`

Use when the app must separate machine recommendation from human or policy-authorized decision.

Route to:
- consequential decision history → `akka-event-sourced-entities`
- approval/deviation workflows → `akka-workflows`
- bounded recommendation/explanation generation → `akka-agents`
- review queues and decision cards → `akka-views` plus web UI skills

### Evidence and risk objects

Typical objects:
- `EvidenceItem`, `ConfidenceScore`, `RiskScore`, `ImpactEstimate`
- `StakesEstimate`, `AlternativeConsidered`, `ReasoningFactor`

Use when humans or policies need facts, confidence, risk, and alternatives before action.

Route to:
- persisted evidence bundles and cited rationale → `akka-event-sourced-entities`
- current evidence cache only → `akka-key-value-entities`
- risk/evidence enrichment from events → `akka-consumers`
- sortable decision queues by risk or confidence → `akka-views`

### Governance and learning objects

Typical objects:
- `HumanFeedback`, `LearnedRule`, `SkillProposal`, `PolicyProposal`
- `PolicyCommit`, `ReplayResult`, `SimulationResult`

Use when human corrections should change future behavior through governed commits, not silent prompt drift.

Route to:
- versioned proposals and commits → `akka-event-sourced-entities`
- simulation/replay orchestration → `akka-workflows` plus `akka-timed-actions` if scheduled
- evaluator or proposal-drafting behavior → `akka-agents`
- governance-center views → `akka-views`

### Accountability and outcome objects

Typical objects:
- `AuditEvent`, `WorkTrace`, `DecisionTrace`, `PolicyInvocation`
- `OutcomeMetric`, `OutcomeLink`, `RollbackRecord`

Use when the product must explain what happened, who or what authorized it, and whether it worked.

Route to:
- trace/audit facts → `akka-event-sourced-entities` or append-only topic/consumer flows
- trace enrichment and downstream publication → `akka-consumers`
- audit search, digest, and outcome dashboards → `akka-views`
- periodic outcome measurement or digest generation → `akka-timed-actions`

## Akka substrate choice hints

- Use `akka-event-sourced-entities` for temporal facts, auditable state, decisions, policies, traces, and precedent.
- Use `akka-key-value-entities` for replaceable current state with no audit-grade history requirement.
- Use `akka-workflows` for long-running goals, plans, approvals, compensations, retries, and agent-team orchestration.
- Use `akka-agents` only for bounded model responsibilities after authority and tools are known.
- Use `akka-views` for command centers, queues, policy catalogs, audit search, and outcomes.
- Use `akka-consumers` for event-driven trace enrichment, notifications, publication, and integrations.
- Use `akka-timed-actions` for deadlines, scheduled checks, digests, expiry, and replay/simulation schedules.

## Output expectations

Produce a compact object model with:
- selected objects and why each is durable
- owning role or component for each object
- event-history requirement: audit-grade, current-state only, or derived/read-model only
- main relationships, such as goal → plan → tasks → decisions → traces → outcomes
- downstream skill routing for implementation
- open questions only where authority, risk, retention, or outcome semantics would otherwise be guessed

## Review checklist

Before moving downstream, verify:
- selected objects are justified by behavior, authorization, explainability, learning, audit, or outcomes
- decisions and approvals are distinct from recommendations
- policies, prompts, skills, thresholds, and permissions can be versioned when they affect behavior
- traces can connect goals, plans, agents, tools, policies, data access, approvals, and outcomes
- the model is not just CRUD plus chat
