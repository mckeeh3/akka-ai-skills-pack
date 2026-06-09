---
name: ai-first-saas-policy-governance
description: Model AI-first SaaS policies, clauses, permissions, thresholds, approval gates, proposals, simulations, and human-governed commits, then route implementation to Akka substrate skills.
---

# AI-First SaaS Policy Governance

Use this companion after `ai-first-saas` when agentic behavior depends on policies, permissions, thresholds, guardrails, examples, prompts, skills, or governed behavior changes.

This is a governance modeling and routing skill. It does not replace security, entity, workflow, agent, or testing implementation skills.

## Required reading

Read first:
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../ai-first-saas/SKILL.md`

Then load focused downstream skills only for selected implementation components.

## Use when

Use for tasks that mention or imply:
- policies, policy clauses, rulebooks, playbooks, guardrails, or reference examples
- permissions, authority scopes, thresholds, risk tiers, or approval gates
- prompt, skill, policy, or evaluator versioning
- human feedback becoming durable precedents, learned rules, or policy proposals
- simulation, replay, evaluation, or governed activation of behavior changes
- tenant, role, data-access, or tool-use limits for agents

## Governance object pattern

Select only objects needed for enforceable behavior and audit:

- `PolicyDocument` / `PolicyClause`: versioned rules agents or workflows must cite.
- `PromptDocument` / `SkillDocument` / `EvaluationRubric` / `ReferenceExample`: governed behavior-shaping documents when prompt, skill, rubric, or example changes affect runtime behavior.
- `Guardrail` / `Threshold`: mechanical limits for autonomy, risk, confidence, impact, or data access.
- `PermissionGrant`: who or what may use a tool, dataset, action, or authority scope.
- `ReferenceExample` / `Precedent`: human-approved examples that guide future behavior.
- `PolicyProposal`: drafted change not yet active.
- `SimulationResult` / `ReplayResult`: evidence before activating impactful changes.
- `PolicyCommit`: human-authorized activation record.

## Authority rules

- Treat policies, prompts, skills, thresholds, and permissions as runtime business logic when they affect behavior.
- Version behavior-shaping artifacts and record provenance.
- Enforce permissions mechanically; prompt instructions are not sufficient.
- Agents may draft governance changes, but policy commits and expanded authority require human governance unless the product explicitly defines a safe autonomous boundary.
- High-impact, novel, uncertain, or policy-bound cases should route to approval or exception workflows.

## Akka substrate routing

- Audit-grade policy, clause, proposal, precedent, and commit history → `akka-event-sourced-entities`.
- Current non-audit configuration, feature flags, or tenant settings → `akka-key-value-entities`.
- Approval, exception, simulation, replay, and activation lifecycles → `akka-workflows` plus `akka-workflow-pausing` when human input is required.
- Policy interpretation, proposal drafting, classification, or evaluation → `akka-agents`, `akka-agent-structured-responses`, `akka-agent-guardrails`, and `akka-agent-evaluation`.
- Governed prompts, skills, rubrics, policies, and examples → `akka-agent-governed-documents`, plus `akka-agent-prompt-governance` or `akka-agent-skill-governance` when focused prompt/skill behavior is in scope.
- Closed-loop behavior improvement proposals, replay/simulation, activation, and rollback → `akka-agent-closed-loop-improvement`.
- Policy catalogs, proposal queues, approval queues, and governance center read models → `akka-views`.
- Trace enrichment, notification, and downstream policy publication → `akka-consumers`.
- Scheduled reviews, replays, expirations, or periodic governance checks → `akka-timed-actions`.
- Browser governance APIs and UI → `akka-http-endpoints`, `akka-web-ui-apps`, and focused web UI skills.
- Authentication, roles, JWT, or admin permissions → `akka-workos-user-auth`, `akka-basic-user-admin`, and HTTP/JWT skills for the mandatory secure SaaS foundation.

## Workstream handoff requirements

For generated full-stack SaaS work, every policy-governance output must hand off an implementation-ready workstream contract before component selection:
- owning or reusable functional agent, such as Governance/Policy, Agent Admin, User Admin, Audit/Trace, or a domain agent;
- structured surface id/type where user-facing, such as policy catalog, proposal queue, approval card, diff review, simulation result, or governance dashboard;
- surface action list mapped to capability ids/classes, including propose policy, simulate change, request approval, approve/deny commit, rollback, or view audit evidence;
- `AuthContext`, tenant/customer scope, role/capability rules, approval gates, audit/work-trace fields, and denial behavior;
- downstream Akka, frontend, and test skills needed for the selected exposure channels.

## Output expectations

Produce a compact governance design with:
- governed artifacts and why each must be durable or versioned
- authority scopes, permission boundaries, and threshold rules
- approval and exception gates
- how human feedback becomes precedent, proposal, or commit
- simulation, replay, evaluation, and activation requirements
- audit records emitted by policy use and policy changes
- downstream skills to load next
- unresolved questions only where policy, authority, risk, or security semantics would otherwise be guessed

## Review checklist

Before implementation, verify:
- behavior-shaping prompts, skills, policies, rubrics, examples, and thresholds are addressable and versioned when they affect runtime behavior
- permissions are enforceable outside prompts
- policy changes have provenance and activation authority
- approvals/exceptions are durable when consequential
- tests can cover allowed, denied, threshold, approval, and proposal/commit paths
