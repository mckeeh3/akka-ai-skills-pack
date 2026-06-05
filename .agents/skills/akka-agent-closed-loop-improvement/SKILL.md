---
name: akka-agent-closed-loop-improvement
description: Design governed closed-loop agent improvement with EvaluationRubric, EvaluationRun, EvaluationFinding, ImprovementProposal, replay/simulation evidence, human approval, activation, monitoring, rollback, and audit. Use when evaluator agents or analysis produce behavior-change proposals.
---

# Akka Agent Closed-Loop Improvement

Use this skill when agent outputs, traces, prompt tests, or skill tests should feed a governed improvement loop.

This skill covers the SaaS governance lifecycle around evaluation and self-improvement. Use `akka-agent-evaluation` for the focused Java SDK evaluator-agent / `EvaluationResult` implementation mechanics.

## Required reading

Read these first if present:
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/agent-coverage-matrix.md`
- `../docs/examples/core-ai-first-saas-input/09-module-evaluation-closed-loop-improvement-prd.md`
- `../core-saas-foundation/SKILL.md`
- `../ai-first-saas-policy-governance/SKILL.md`
- `../ai-first-saas-audit-trace/SKILL.md`
- `../akka-agents/SKILL.md`
- `../akka-agent-evaluation/SKILL.md`
- `../akka-agent-governed-documents/SKILL.md`
- `../akka-agent-prompt-governance/SKILL.md`
- `../akka-agent-skill-governance/SKILL.md`
- `../akka-agent-work-trace/SKILL.md`
- `../akka-workflows/SKILL.md`
- `../akka-workflow-pausing/SKILL.md`
- `../akka-event-sourced-entities/SKILL.md`
- `../akka-key-value-entities/SKILL.md`
- `../akka-views/SKILL.md`
- `../akka-http-endpoints/SKILL.md`

## Use when the request mentions

- closed-loop improvement or self-improving agents
- evaluator findings becoming prompt, skill, policy, rubric, or agent changes
- improvement proposals, proposal review, or approval workflow
- replay, simulation, regression evaluation, canary, activation, or rollback
- evaluation queue, findings review, outcome monitoring, or learning loop UI
- human-governed activation of agent-authored recommendations

## Core safety rule

Agents may analyze, evaluate, recommend, and draft proposals. They must not directly activate consequential behavior changes unless an explicit bounded auto-approval policy exists and is mechanically enforced and tested.

Prompt text, skill text, evaluator recommendations, and model output do not grant approval authority.

## Core flow

```text
production/test output or WorkTrace
→ EvaluationRun
→ EvaluationFinding
→ ImprovementProposal
→ ReplaySimulationResult / regression evidence
→ human approval or bounded auto-approval
→ activation through prompt/skill/agent/rubric governance
→ OutcomeObservation / monitoring
→ rollback if needed
```

## Durable objects

### EvaluationRubric

A governed document that defines repeatable evaluation criteria.

Fields:
- `rubricId`, `tenantId`, name, purpose
- target type: prompt output, skill usage, agent behavior, trace, safety, quality, policy adherence
- criteria and weights or pass/fail rules
- severity mapping and failure categories
- evaluator instructions
- lifecycle status and version
- created/updated/reviewed metadata

Use `akka-agent-governed-documents` when rubric version history, review, activation, or diff UI matters.

### EvaluationRun

One evaluation execution.

Fields:
- `evaluationRunId`, `tenantId`
- target type/id: agentDefinitionId, promptVersion, skillVersion, workTraceId, test output id
- rubric id/version
- evaluator type: deterministic | evaluator_agent | manual
- status: queued | running | completed | failed | canceled
- input references and redaction summary
- findings summary, score/pass-fail, risk/severity
- created by / started at / completed at
- correlation id

Use a Workflow when the evaluation includes asynchronous evaluator calls, retries, human handoff, or result collection.

### EvaluationFinding

A specific issue, success, or risk.

Fields:
- `findingId`, `evaluationRunId`, `tenantId`
- category: correctness, safety, hallucination, policy, tone, tool_use, data_access, prompt_adherence, skill_use, latency, user_value
- severity and confidence
- evidence references and linked trace event ids
- explanation and recommended action

### ImprovementProposal

A governed proposal to change behavior.

Fields:
- `proposalId`, `tenantId`
- target artifact type/id/version: prompt, skill, agent_definition, rubric, policy
- proposal type: edit, activation, rollback, disable, authority_reduction, tool_boundary_change
- status: draft | in_review | approved | rejected | activated | rolled_back | canceled
- source evaluationRunId/findingIds
- proposed diff or change summary
- evidence summary
- risk/impact assessment
- expected outcome
- replay/simulation result refs
- approver/reviewer metadata
- activation metadata
- rollback target
- correlation id

Use an Event Sourced Entity for proposal lifecycle and approvals.

### ReplaySimulationResult

Evidence from testing a proposed change before activation.

Fields:
- `simulationId`, `tenantId`, proposalId
- input trace/test case refs
- candidate artifact versions/checksums
- baseline result summary
- candidate result summary
- evaluator score comparison
- pass/fail and created timestamp

### OutcomeObservation

Post-activation monitoring signal.

Fields:
- observation id, tenant id
- linked proposal id and activated artifact version
- observation window
- measured value/status
- expected vs actual summary
- rollback recommendation flag

## Akka component mapping

Route to:
- `akka-agent-evaluation` for evaluator agents that return `EvaluationResult`.
- `akka-agent-work-trace` for trace inputs, evidence references, prompt/skill/model/tool context, and correlation.
- `akka-agent-governed-documents` for versioned rubrics and target prompt/skill/policy documents.
- `akka-workflows` for EvaluationRun orchestration, proposal review, replay/simulation, activation, monitoring, and rollback flow.
- `akka-workflow-pausing` when human review/approval is required.
- `akka-event-sourced-entities` for ImprovementProposal and audit-grade lifecycle records.
- `akka-key-value-entities` for findings, simulation results, outcome observations, or immutable snapshots when appropriate.
- `akka-views` for evaluation queues, findings lists, proposal queues, approval queues, and outcome dashboards.
- `akka-http-endpoints` and `akka-web-ui-apps` for Evaluation and Improvement UI.
- `ai-first-saas-audit-trace` for evaluation, proposal, approval, activation, rollback, and outcome trace events.

## Authority and approval rules

- Evaluator agents can create findings and draft proposals.
- Evaluator agents cannot approve, activate, expand authority, grant tool permissions, or weaken policies by default.
- Activation requires the governance capability for the target artifact, such as `prompts.activate`, `skills.activate`, `agents.status.manage`, or policy approval capability.
- Auto-approval must be explicitly modeled with scope, risk tier, allowed proposal types, max impact, required tests, and rollback conditions.
- Authority expansion should require human approval; authority reduction may be eligible for bounded automation if the product accepts it.
- Rollback is an audited activation of a prior approved version or prior profile state.

## UI surfaces

Provide protected UI for:
- evaluation run queue/list and filters;
- run detail with target, rubric, findings, score, severity, evidence, and trace links;
- rubric catalog/editor/version history if in scope;
- findings review and create-proposal action;
- proposal detail with diff, evidence, risk/impact, replay/simulation results, and approval controls;
- activation/canary/rollback controls where supported;
- outcome monitoring summary and rollback recommendation state.

## Test requirements

Plan tests for:
- evaluation run creation and completion;
- evaluator-agent result mapping and validation;
- finding creation from evaluator output;
- proposal creation from findings;
- unauthorized proposal review/activation denial;
- human approval and rejection paths;
- activation through target artifact governance;
- rollback path;
- tenant isolation;
- audit/work trace emission;
- bounded auto-approval denial and allowed cases if auto-approval is included.

## Review checklist

Before finishing, verify:
- self-improvement is proposal-driven, not direct self-modification
- evaluator agents cannot activate consequential changes by default
- rubrics and target artifacts are versioned when repeatability matters
- replay/simulation evidence is captured before activation where risk warrants it
- approval and rollback are durable and audited
- all records are tenant-scoped and authorization-protected
- trace/evidence links support later investigation
- UI exposes findings, proposals, evidence, decisions, and outcomes clearly
