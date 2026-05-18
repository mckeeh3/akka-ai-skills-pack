---
name: ai-first-saas-agent-team-design
description: Design bounded AI-first SaaS agent teams with explicit coordinator/specialist responsibilities, authority limits, tools, escalation rules, traces, and routing to Akka agent and workflow skills.
---

# AI-First SaaS Agent Team Design

Use this companion after `ai-first-saas` when a product or feature needs one or more agents to perform delegated operational work under human and policy control.

This is an operating-model and routing skill. It does not replace `akka-agents`, `akka-agent-orchestration`, or `akka-workflows` implementation guidance.

## Required reading

Read first:
- `../../docs/ai-first-saas-application-architecture.md`
- `../ai-first-saas/SKILL.md`

For implementation, route to:
- `akka-agents`
- `akka-agent-component`
- `akka-agent-tools` or `akka-agent-component-tools` when tools are needed
- `akka-agent-structured-responses` when outputs must become typed records
- `akka-agent-memory` when session history matters
- `akka-agent-orchestration` and `akka-workflows` for durable multi-agent execution
- `akka-agent-guardrails` and `akka-agent-evaluation` when runtime safety or judging is in scope

## Use when

Use for tasks that mention or imply:
- an agent, copilot, worker, analyst, reviewer, planner, evaluator, or agent team
- delegated operational work that spans multiple steps or responsibilities
- autonomous or semi-autonomous action with approval, exception, or escalation paths
- tool use, data access, recommendations, summarization, planning, classification, or evaluation
- human supervision of active work, team progress, risk, exceptions, and outcomes

## Do not use when

Do not design an agent team when:
- one deterministic workflow or rule can do the work safely
- a single bounded request/reply agent is enough
- authority, tools, policy, or escalation boundaries are too vague to implement safely
- the request is only to add a chatbot interface without durable delegated work

## One-agent vs agent-team decision guide

Before naming agent classes, decide whether the work is safest as one governed skilled agent, multiple specialized agents, a workflow-supervised team, or a separate evaluator.

Use a **single governed skilled agent** when:
- responsibilities share the same authority level, lifecycle, owner/steward, model config policy, memory/session rules, tool boundary, approval thresholds, and audit requirements;
- the agent can keep one clear primary responsibility while using focused governed skills through an `AgentSkillManifest`;
- skills differ by task guidance, not by data/tool authority or risk class;
- one admin/review surface can safely manage the agent definition, prompt, skills, model, tool boundary, and traces.

Use **multiple specialized agents** when any responsibility needs:
- different authority, tenant/customer scope, tool boundary, model config, memory, lifecycle, owner/steward, scaling profile, prompt governance cadence, or approval policy;
- a materially different risk class, trace retention, redaction rule, or audit review path;
- independent enable/disable, rollout, evaluation, replay, or rollback;
- separation of duties, such as drafter vs approver, operator vs auditor, or worker vs reviewer.

Use a **workflow-supervised agent team** when:
- the objective spans durable multi-step execution, retries, pauses, deadlines, compensation, or human approval gates;
- separate agents produce handoff artifacts that must survive restarts or be inspected by supervisors;
- policy, confidence, risk, or side-effect checks must occur between model calls;
- humans need a command-center view of progress, exceptions, and pending decisions.

Use a **reviewer/evaluator agent** when:
- output quality, policy fit, completeness, risk, or hallucination detection must be judged separately from the worker that produced the output;
- evaluator authority is advisory by default and its structured result gates automation or human review;
- high-impact actions need an independent confidence/risk signal before approval.

Unsafe over-consolidation signals:
- one agent would need broad tools only some tasks justify;
- prompt/skill text is expected to substitute for backend authorization;
- unrelated responsibilities would share memory, traces, or model settings by convenience;
- disabling a risky function would also disable unrelated low-risk work.

Unnecessary sprawl signals:
- proposed specialized agents share the same authority, tools, model config, lifecycle, steward, memory, approval, and audit needs;
- differences are only prompt sections or governed skills;
- coordination would add no durable state, retry, approval, or trace value.

## Team shape patterns

### Single bounded agent

Use when one model responsibility produces one bounded output.

Examples:
- classify an incoming case
- draft a recommendation with cited evidence
- summarize activity for a digest
- evaluate an answer against a rubric

Route to:
- `akka-agents`
- `akka-agent-component`
- `akka-agent-structured-responses` when the output drives state or UI
- `akka-agent-testing`

### Coordinator plus specialists

Use when one objective requires separate planning, data gathering, analysis, drafting, review, or evaluation responsibilities.

Coordinator responsibility:
- interpret the durable goal and plan
- assign bounded tasks
- maintain progress and handoff context
- enforce approval/escalation gates through workflow state
- consolidate results for human review or downstream action

Specialist responsibility:
- perform one narrow job with explicit tools, inputs, and output schema
- avoid changing plan authority unless specifically allowed
- emit traceable evidence, confidence, risk, and limitations

Route to:
- `akka-workflows` for durable plan/team orchestration
- `akka-agent-orchestration` for workflow-supervised calls
- `akka-agents` plus focused agent companion skills for each specialist
- `akka-views` for mission-control and work-queue surfaces

### Reviewer/evaluator agent

Use when the app needs a model to judge quality, policy fit, completeness, or risk before human review or automation.

Rules:
- evaluator output should be structured
- evaluator is advisory unless product policy grants it specific authority
- high-impact or uncertain cases escalate to humans

Route to:
- `akka-agent-evaluation`
- `akka-agent-structured-responses`
- `akka-workflows` for gating and escalation

### Human-supervised agent team

Use when agents can progress work but humans retain authority for risky, high-impact, novel, or policy-bound actions.

Required elements:
- approval gates and exception states
- decision cards with evidence, risk, confidence, impact, and alternatives
- audit trace for prompts, tools, policy invocations, and decisions
- command-center views for active work and pending human input

Route to:
- `akka-workflows` and `akka-workflow-pausing`
- `akka-views`
- `akka-web-ui-apps` plus focused web UI companion skills
- `akka-event-sourced-entities` for decisions, approvals, and traces when audit-grade history is required

## Agent contract template

For each agent, define:

```text
Agent name:
Single responsibility:
Non-responsibilities:
Inputs:
Output type: plain text | structured | streamed
Allowed tools/data:
Forbidden tools/data:
Autonomous decisions allowed:
Requires approval when:
Escalates when:
Confidence/risk/impact thresholds:
Memory/session behavior:
Trace records emitted:
Evaluation or guardrails:
Implementation routing:
```

## Authority boundary rules

- Agents may recommend, draft, classify, summarize, evaluate, or execute only within explicit product authority.
- Tool permissions must be mechanically enforced; do not rely only on prompt instructions.
- Policy-changing, permission-expanding, high-impact, or uncertain actions need human governance unless the product explicitly defines a safe autonomous boundary.
- Record when an agent used data, invoked a tool, cited a policy, made a recommendation, or triggered escalation.

## Akka substrate routing

- Durable multi-step agent execution → `akka-workflows` plus `akka-agent-orchestration`.
- Single model call → `akka-agents` plus `akka-agent-component`.
- Typed recommendations, decisions, risk reviews, or evaluator outputs → `akka-agent-structured-responses`.
- Local function tools → `akka-agent-tools`.
- Akka components as tools → `akka-agent-component-tools`.
- Remote MCP tools → `akka-agent-mcp-tools`.
- Memory/session design → `akka-agent-memory`.
- Runtime safety checks → `akka-agent-guardrails`.
- LLM-as-judge → `akka-agent-evaluation`.
- Team state, approvals, and retries → `akka-workflows`.
- Team activity, queues, and supervision views → `akka-views`.
- Deadlines, rechecks, and digests → `akka-timed-actions`.

## Output expectations

Produce a concise team design with:
- selected team shape and why it is needed
- agent contracts for each agent
- coordinator/workflow responsibilities versus specialist responsibilities
- human approval and exception points
- tools, data access, memory, guardrail, and evaluation needs
- trace and audit requirements
- downstream Akka skills to load next
- unresolved authority questions, if any

## Review checklist

Before implementation, verify:
- each agent has one responsibility and explicit non-responsibilities
- every autonomous action has a bounded authority rule
- tools and data permissions are explicit and enforceable
- workflow orchestration is used for durable multi-agent progress instead of fragile agent-to-agent chaining
- human approvals and exceptions are represented as durable states when consequential
- tests can replace real model calls with deterministic model providers
