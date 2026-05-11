# Agent-First SaaS Coding-Agent Framework

## Purpose

This document is the canonical conceptual contract and index for designing SaaS applications where AI agents perform operational work and humans govern, approve, correct, teach, and audit that work.

Use this file for shared thesis, vocabulary, object categories, human roles, required surfaces, and skill selection. Use the focused skills in `skills/` for operational procedures, schemas, implementation checklists, and backlog/test planning.

AI-first SaaS is not a CRUD app with AI features, a dashboard with a chatbot, a workflow builder with generated text, or traditional SaaS plus automation. It is:

> an operating surface for managing AI labor, where agents execute work under explicit human-authored policies, and humans supervise high-value decisions, exceptions, learning loops, outcomes, and accountability.

For visual screen composition, also use [`docs/ai-first-saas-ui-patterns.md`](ai-first-saas-ui-patterns.md). For target implementation architecture, use [`docs/skills-pack-tech-stack.md`](skills-pack-tech-stack.md).

---

## Core thesis

Traditional SaaS assumes humans are the primary workers and software is a tool. AI-first SaaS inverts this: agents are primary operational workers, and humans become directors, supervisors, reviewers, exception handlers, policy owners, teachers, auditors, and outcome owners.

The core design unit changes from a `task` to a `delegation`:

```text
what the agent is allowed to do
+ under what authority
+ using what data/tools
+ constrained by which policies
+ requiring which approval gates
+ producing which traceable outcomes
```

A valid ai-first SaaS design therefore models both domain entities and the agentic substrate: goals, plans, agents, policies, decisions, evidence, approvals, exceptions, traces, feedback, learned rules, replay results, permissions, and outcomes.

---

## Operating loop

All ai-first SaaS products should make this loop explicit and durable:

```text
Human states objective
→ App creates a durable goal object
→ Coordinator or agent creates an execution plan
→ Agents act within policy, tool, data, and authority boundaries
→ System briefs humans on material work and exceptions
→ Humans decide high-stakes or uncertain cases
→ Human decisions become precedents, examples, proposals, or policy updates
→ Future behavior changes through governed prompt/skill/policy versions
→ Outcomes validate whether the automation is working
```

If an implementation lacks durable representations for goals, plans, policies, approvals/decisions, traces, learning, and outcomes, it is probably conventional SaaS with automation rather than ai-first SaaS.

---

## Human roles

Model users by operational role. One person may hold multiple roles.

| Role | Primary responsibility |
|---|---|
| Intent Author | Defines objectives, success criteria, constraints, preferences, thresholds, and definitions of done. |
| Supervisor | Monitors active goals, agents, plans, progress, exceptions, approvals, and material activity. |
| Reviewer / Approver | Makes high-impact, uncertain, risky, or policy-boundary decisions. |
| Exception Handler | Resolves ambiguity, authority limits, data gaps, policy conflicts, and blocked actions. |
| Policy Owner / Coach | Turns corrections into durable policy, examples, thresholds, and governed behavior changes. |
| Auditor | Investigates what happened, who/what authorized it, which evidence/policies/tools were used, and why. |
| Outcome Owner | Determines whether agent labor is producing business value and acceptable risk. |

---

## Shared object vocabulary

Use domain-specific entities as needed, but do not organize ai-first products only around records and forms. Include these substrate object categories where relevant:

- **Intent:** `Goal`, `Objective`, `SuccessCriterion`, `Constraint`, `PolicyDocument`, `PolicyClause`, `Guardrail`, `ReferenceExample`.
- **Execution:** `Agent`, `AgentTeam`, `AgentDefinition`, `AgentSystemPrompt`, `AgentSkill`, `AgentSkillVersion`, `ExecutionPlan`, `Workflow`, `Task`, `ToolInvocation`, `DataAccessEvent`, `WorkResult`.
- **Judgment:** `Recommendation`, `Decision`, `ApprovalRequest`, `Exception`, `Escalation`, `Counterproposal`, `Precedent`.
- **Evidence and risk:** `EvidenceItem`, `ConfidenceScore`, `RiskScore`, `ImpactEstimate`, `StakesEstimate`, `AlternativeConsidered`, `ReasoningFactor`.
- **Learning and governance:** `HumanFeedback`, `LearnedRule`, `SkillProposal`, `SkillCommit`, `PolicyProposal`, `PolicyCommit`, `ReplayResult`, `SimulationResult`.
- **Accountability and outcomes:** `AuditEvent`, `WorkTrace`, `DecisionTrace`, `PolicyInvocation`, `OutcomeMetric`, `OutcomeLink`, `RollbackRecord`.

Detailed object modeling belongs in [`skills/ai-first-saas-object-model/SKILL.md`](../skills/ai-first-saas-object-model/SKILL.md).

---

## Agent and governance principles

- Agents are bounded operational actors, not vague background automation.
- Production systems should usually prefer a coordinated team of specialized agents over one omnipotent app-level agent when risk, scope, data access, or governance complexity justifies specialization.
- Each agent should have a versioned system prompt, assigned versioned skills, platform-enforced tool/data/action permissions, authority boundaries, confidence/risk thresholds, escalation criteria, and audit requirements.
- System prompts, skills, policies, rules, thresholds, approval gates, and permissions are runtime business logic. They require versioning, provenance, testability, replay/simulation where impactful, and human authorization before active behavior changes.
- Prompts and skills instruct models; platform permissions enforce what models can actually do.

Use [`skills/ai-first-saas-agent-team-design/SKILL.md`](../skills/ai-first-saas-agent-team-design/SKILL.md), [`skills/ai-first-saas-policy-governance/SKILL.md`](../skills/ai-first-saas-policy-governance/SKILL.md), and [`skills/ai-first-saas-permission-enforcement/SKILL.md`](../skills/ai-first-saas-permission-enforcement/SKILL.md) for detailed procedures.

---

## Required surfaces

AI-first products may contain conventional tables, forms, and detail pages, but the primary UX should support delegation, supervision, decision, teaching, catch-up, and audit.

| Temporal mode | Human question | Required surface |
|---|---|---|
| Delegating now | What outcome do I want, and how should agents pursue it? | Goal-to-Execution Workbench |
| Attending now | What is happening, and where should I focus? | Command Center / Mission Control |
| Deciding now | This needs judgment; what should I do? | Deviation Review / Decision Card |
| Teaching now | What should good mean from now on? | Agent Skills / Policy / Governance / Learning Center |
| Catching up | What happened while I was away? | Async Digest / Executive Briefing |
| Auditing later | Why did this happen and was it authorized? | Audit and Work Trace |

Use [`skills/ai-first-saas-ui-surfaces/SKILL.md`](../skills/ai-first-saas-ui-surfaces/SKILL.md) for screen specs and [`docs/ai-first-saas-ui-patterns.md`](ai-first-saas-ui-patterns.md) for component/layout patterns.

---

## Architecture contract

The target skills-pack architecture is Akka backend plus React/Vite/TypeScript frontend. Designs should map capabilities to explicit write paths, read paths, asynchronous reactions, long-running workflows, AI-agent responsibilities, API/streaming surfaces, UI components, and tests.

At a high level:

- durable state and behavior live in Akka entities, workflows, consumers, timed actions, views, endpoints, and agents;
- events, audit records, work traces, and outcome links are generated during execution, not reconstructed only from logs;
- UI read models should be shaped for objective-centered supervision, decision queues, digests, traces, and governance screens;
- replay/simulation, policy evaluation, permission checks, approval gates, redaction, and retention should be designed as explicit system capabilities.

Use the runtime, audit, replay, security, outcomes, testing, and backlog skills for implementation detail.

---

## Conversation rule

Do not implement the product as only a chatbot. Conversation is useful for stating intent, asking clarifying questions, querying traces, requesting summaries, or issuing high-level commands, but it must resolve into durable, inspectable objects:

```text
chat command
→ goal / plan / policy / decision / task / query / report object
→ structured UI for inspection and control
→ audit event
```

High-stakes decisions should use structured decision UI with recommendation, evidence, alternatives, risk, policy trigger, precedent, and action controls rather than chat-only interaction.

---

## Skill index

Load the smallest relevant skill set for the current task:

| Need | Skill |
|---|---|
| Decide whether a product should be treated as ai-first SaaS | [`ai-first-saas-intake`](../skills/ai-first-saas-intake/SKILL.md) |
| Scope realistic capability by maturity level | [`ai-first-saas-maturity-model`](../skills/ai-first-saas-maturity-model/SKILL.md) |
| Convert requirements into domain + substrate objects | [`ai-first-saas-object-model`](../skills/ai-first-saas-object-model/SKILL.md) |
| Design coordinator/specialist agent teams and permissions | [`ai-first-saas-agent-team-design`](../skills/ai-first-saas-agent-team-design/SKILL.md) |
| Design prompt, skill, policy, rule, guardrail, and learning governance | [`ai-first-saas-policy-governance`](../skills/ai-first-saas-policy-governance/SKILL.md) |
| Specify the six required UI surfaces | [`ai-first-saas-ui-surfaces`](../skills/ai-first-saas-ui-surfaces/SKILL.md) |
| Specify approvals, exceptions, and decision cards | [`ai-first-saas-decision-cards`](../skills/ai-first-saas-decision-cards/SKILL.md) |
| Specify audit events, work traces, provenance, and event catalogs | [`ai-first-saas-audit-trace`](../skills/ai-first-saas-audit-trace/SKILL.md) |
| Design runtime execution, workflows, agent runs, retries, and failures | [`ai-first-saas-runtime-orchestration`](../skills/ai-first-saas-runtime-orchestration/SKILL.md) |
| Enforce tools, data scopes, actions, tenant boundaries, and approvals | [`ai-first-saas-permission-enforcement`](../skills/ai-first-saas-permission-enforcement/SKILL.md) |
| Replay or simulate prompt, skill, policy, or threshold changes | [`ai-first-saas-replay-simulation`](../skills/ai-first-saas-replay-simulation/SKILL.md) |
| Design security, privacy, abuse resistance, redaction, and retention | [`ai-first-saas-security-privacy`](../skills/ai-first-saas-security-privacy/SKILL.md) |
| Build tests, evals, golden traces, adversarial cases, and acceptance criteria | [`ai-first-saas-testing-evaluation`](../skills/ai-first-saas-testing-evaluation/SKILL.md) |
| Decompose specs into epics, stories, implementation tasks, and sequencing | [`ai-first-saas-backlog-decomposition`](../skills/ai-first-saas-backlog-decomposition/SKILL.md) |
| Reference a compact end-to-end example | [`ai-first-saas-worked-example`](../skills/ai-first-saas-worked-example/SKILL.md) |
| Define outcome metrics, attribution, CQRS views, and validation | [`ai-first-saas-outcomes-metrics`](../skills/ai-first-saas-outcomes-metrics/SKILL.md) |
| Define risk, confidence, impact, stakes, trust, and routing thresholds | [`ai-first-saas-risk-confidence-calibration`](../skills/ai-first-saas-risk-confidence-calibration/SKILL.md) |
| Design material/routine curation and async digest behavior | [`ai-first-saas-curation-digest`](../skills/ai-first-saas-curation-digest/SKILL.md) |
| Convert conversational input into durable inspectable objects | [`ai-first-saas-conversation-to-durable-objects`](../skills/ai-first-saas-conversation-to-durable-objects/SKILL.md) |

---

## Minimal ai-first checklist

Before calling a design ai-first, verify:

- [ ] Goals are durable objects with objectives, success criteria, constraints, owners, and outcomes.
- [ ] Agents have explicit responsibilities, versioned prompts/skills, bounded permissions, thresholds, and traces.
- [ ] Execution plans are inspectable, editable before activation where appropriate, policy-bound, and auditable.
- [ ] Policies and clauses are versioned, addressable, testable, and cited by decisions/actions.
- [ ] Approval, exception, and decision flows include evidence, risk, confidence, impact, alternatives, and learning options.
- [ ] Human corrections can become precedents, examples, proposals, policy changes, or threshold changes through governed commits.
- [ ] Audit events, work traces, tool invocations, data access events, policy invocations, and outcome links are generated during execution.
- [ ] Routine activity can be compressed while remaining audit-complete; material activity is surfaced.
- [ ] Replay/simulation exists for impactful prompt, skill, policy, threshold, or governance changes.
- [ ] The six required surfaces exist or are intentionally deferred based on maturity level.
- [ ] Security, privacy, tenant isolation, redaction, retention, and permission enforcement are mechanical, not prompt-only.
- [ ] Outcomes validate whether automation is useful, safe, and improving.

---

## Glossary

- **AI-first SaaS:** SaaS where agents perform operational work and humans supervise, govern, teach, and audit.
- **Agentic substrate:** The goals, plans, agents, prompts, skills, policies, events, traces, replay, permissions, and outcome infrastructure beneath the UI.
- **Agent skill:** Versioned, app-managed capability package assigned to agents and compiled from structured context, policy bindings, rules, examples, and metadata.
- **Agent system prompt:** Versioned operating identity for an agent, separate from assigned skills.
- **Approval gate:** Policy-defined point where an agent must receive human approval before continuing.
- **Clause:** Stable, addressable unit of policy.
- **CompiledAgentContext:** Runtime context assembled from an agent definition, prompt version, skill versions, policy versions, rules, examples, tool/data scopes, and authority boundaries.
- **Decision card:** Structured UI object for reviewing a recommendation, exception, approval, or policy deviation.
- **Decision provenance:** Stored evidence, policies, scores, alternatives, rationale summary, versions, and actions explaining why a recommendation or action happened.
- **Disposition tag:** Per-action label such as `auto`, `review`, `approval`, `escalate`, or `fyi`.
- **Goal object:** Durable representation of human intent, success criteria, constraints, and ownership.
- **Material event:** Agent activity worth surfacing to humans.
- **Precedent:** Prior human decision and outcome used to calibrate future decisions.
- **Replay:** Re-evaluation of historical decisions under a proposed prompt, skill, policy, or threshold version.
- **Routine activity:** Agent activity recorded for audit but compressed in supervision and digest UI.
- **Surface:** Screen or interaction layer derived from ai-first substrate objects.
- **Trace:** Structured record of agent activity, evidence, tools, policies, decisions, and outcomes.
