---
name: ai-first-saas
description: Interpret high-level product intent as an AI-first SaaS operating model, then route to app-description, Akka decomposition, or focused implementation skills without replacing them.
---

# AI-First SaaS

Use this as the top-level interpretation skill when a product, PRD, feature request, or architecture prompt involves delegated operational work, autonomous or semi-autonomous decisions, agent teams, human supervision, policy controls, approvals, exceptions, audit traces, or outcome accountability.

This is a routing and framing skill. It does not replace `core-saas-foundation`, `agent-workstream-apps`, app-description skills, Akka solution decomposition, web UI skills, or focused component implementation skills. For every generated SaaS app, preserve this handoff order: secure AI-first SaaS interpretation → `agent-workstream-apps` for functional agents, workstreams, and structured surfaces → `core-saas-foundation` for mandatory foundation verticals inside that workstream model → `capability-first-backend` for governed operation/query contracts → the selected description, decomposition, PRD/backlog, or focused implementation path.

## Goal

Before decomposing into CRUD screens or isolated Akka components, identify whether the work should be modeled as:

```text
human objective
→ secure SaaS foundation
→ functional/context-area agents
→ continuous workstreams
→ structured surfaces and surface actions
→ governed backend capabilities
→ horizontal Akka components
→ bounded agent or agent-team execution
→ policy, permission, evidence, and approval controls
→ human supervision and exception handling
→ traceable outcomes and learning loops
```

Then choose the smallest downstream path that can implement or maintain that model. For generated full-stack SaaS, the default handoff is `agent-workstream-apps` before capability inventory, Akka decomposition, or component implementation.

## Required reading

Read these first when using this skill:
- `../../../AGENTS.md`
- `../README.md`
- `../core-saas-foundation/SKILL.md`
- `../agent-workstream-apps/SKILL.md` for generated full-stack SaaS app routing
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/agent-workstream-application-architecture.md` for generated full-stack SaaS app routing
- `../docs/structured-surface-contracts.md` when surfaces or surface actions need implementation-ready contracts

For minimum, core app, basic, basic-chatbot, smallest-useful-app, or initial chatbot-like generated SaaS requests, also read `../docs/minimum-ai-first-saas-app.md` before applying the anti-chatbot and core app baseline rule.

For description-first work, also read the app-description docs named by `skills/README.md`.
For generated SaaS foundations, also load `../ai-first-saas-admin-agents/SKILL.md` for mandatory AI-assisted admin offload. For direct Akka implementation, load only the focused Stage 3 skills after the functional-agent/workstream/surface model and capability contracts have selected the substrate components.

## Use when

Use this skill for inputs that mention or imply:
- agents doing operational work for users or teams
- goals, objectives, plans, missions, campaigns, cases, or managed outcomes
- approvals, exceptions, escalations, deviations, reviews, or recommendations
- policy-bound automation, permissions, thresholds, or guardrails
- human supervision, command centers, work queues, digests, or audit trails
- evidence, confidence, risk, impact, alternatives, or decision rationale
- learning from feedback, precedents, policy updates, simulations, or evaluations

Also use it for broad product prompts where the app may be AI-first even if the prompt uses ordinary SaaS language.

## Do not use when

Do not force every AI-first pattern when the task is clearly:
- a narrow Akka component implementation request with settled architecture
- a secure SaaS feature with no delegated work, decisions, governance, or outcome loop beyond the mandatory foundation
- a documentation or repo maintenance task unrelated to generated application architecture
- a pure public static asset task with no protected API or agentic operating model implications

Even when this skill is used, apply only the AI-first patterns justified by the product intent, while keeping the `core-saas-foundation` mandatory for generated SaaS apps.

## Anti-chatbot and core app baseline rule

Do not treat an AI-first app as a chatbot bolted onto CRUD.

Conversation can collect intent or explain status, but consequential work should resolve into durable, inspectable objects such as goals, plans, tasks, policies, decisions, approvals, traces, and outcomes.

When the user asks for a “minimum AI-first app,” “core app,” “basic app,” “basic chatbot,” “smallest useful app,” or initial chatbot-like generated SaaS, load `../docs/minimum-ai-first-saas-app.md` and route to a bootstrap-authorized **five core workstream core app domain** shell: My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy, each with the `markdown_response` structured surface. Do not start with a generic public chatbot, unauthenticated assistant, or page-first CRUD console unless the user explicitly asks for non-SaaS reference material. Preserve capability-first backend modeling before choosing Akka components or exposing browser actions/agent tools.

## Interpretation workflow

### 1. Identify the human objective

Extract:
- objective owner and affected stakeholders
- desired outcome and success criteria
- constraints, limits, preferences, and definitions of done
- what the human delegates versus retains

If the objective is too vague to bound authority, ask or queue a clarification before designing autonomous action.

### 2. Identify durable substrate objects

Look for durable objects in these categories:
- intent: goals, objectives, constraints, policies, guardrails, examples
- execution: plans, tasks, agents, agent teams, tool calls, work results
- judgment: recommendations, decisions, approval requests, exceptions, escalations
- evidence and risk: evidence items, confidence, risk, impact, alternatives
- governance: feedback, policy proposals, learned rules, simulations, commits
- accountability: audit events, work traces, decision traces, outcome links

Do not create all categories by default. Select only objects needed for behavior, authorization, explainability, learning, or audit.

### 3. Bound agent responsibility and authority

For each agent or agent team, define:
- responsibility and non-responsibility
- tools and data it may use
- decisions it may make autonomously
- decisions requiring approval or escalation
- confidence, risk, or impact thresholds
- memory/session expectations and trace requirements

Route implementation to `akka-agents` only after these boundaries are clear enough for code.

### 4. Capture governance and decision needs

Identify:
- policies, clauses, prompts, skills, thresholds, and permission rules that affect behavior
- approval gates and exception paths
- evidence required for human review
- audit records required for decisions and actions
- how feedback can become a precedent, example, policy proposal, or governed commit

Prefer mechanically enforced permissions and versioned policy records over prompt-only control.

### 5. Choose the downstream operating path

For generated full-stack SaaS apps, load or apply `agent-workstream-apps` before downstream description, decomposition, PRD/backlog, capability, or implementation work so the handoff contains an explicit inventory of functional agents, any internal agents, initial workstreams, structured surfaces, surface action-to-capability candidates, and downstream skills to load. Load `core-saas-foundation` alongside that model to ensure Account/Profile/Settings/Membership/Tenant/Customer/admin/audit baseline work is represented as mandatory foundation verticals rather than as optional object lists. For minimum/core app/basic/chatbot-like generated SaaS requests, that inventory starts with the five core workstream core app domain—My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy—with bounded bootstrap authority, `markdown_response`, durable workstream log, audit/work trace substrate, and follow-up gates for full-core readiness. When core user administration is in scope for a generated SaaS app, load `ai-first-saas-admin-agents` so AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, AdminAuditSummaryAgent, decision cards, and approval boundaries are planned before domain work. Then route based on what the user is asking for:

- Use `app-descriptions` when the user wants to describe, review, revise, or maintain the authoritative application description before generation, preserving functional agents, surfaces, capabilities, and horizontal Akka implementation notes.
- Use `capability-first-backend` when surface actions, governed-tools, agent-tools, browser-tools, workflow steps, APIs, timers, consumers, or internal operations need governed backend contracts.
- Use `akka-solution-decomposition` when the user wants a direct Akka solution shape from the accepted workstream/capability model and the component set is not yet known.
- Use `akka-prd-to-specs-backlog` when the user wants repo-ready specs, backlogs, and pending-task artifacts that preserve the functional-agent/workstream/surface/capability structure.
- Use focused Stage 3 component skills only when the secure foundation, AI-first operating model, workstream model, capability contracts, and component scope are already clear enough to implement.

## Akka substrate routing

Map AI-first concepts to Akka implementation families only after the secure foundation, functional agents, structured surfaces, and governed capabilities are clear:

- durable audit-grade goals, policies, decisions, traces, and consequential facts → `akka-event-sourced-entities`
- simple current-state records, preferences, and ephemeral operational state → `akka-key-value-entities`
- long-running plans, approvals, retries, compensation, and agent orchestration → `akka-workflows`
- bounded LLM responsibilities, tool use, planning, recommendation, evaluation, and explanation → `akka-agents`
- command centers, queues, dashboards, audit search, and outcome reporting → `akka-views`
- async trace enrichment, notifications, publishing, integrations, and downstream reactions → `akka-consumers`
- deadlines, reminders, expiries, periodic digests, rechecks, and simulations → `akka-timed-actions`
- browser APIs, service APIs, streams, and AI-client tools/resources → HTTP, gRPC, and MCP endpoint skills
- supervision, decision, governance, digest, and trace interfaces → `akka-web-ui-apps` plus focused web UI companion skills

## Output expectations

When this skill is the entry point, produce or feed downstream work with:
- AI-first interpretation: objective, delegated work, retained human authority, and outcome loop
- selected durable objects and why each is needed
- generated SaaS handoff: functional agents, role-specific dashboards, human surface graph nodes/actions, internal workstream agent graph nodes/delegations/results when applicable, initial workstreams, structured surfaces, governed-tools, and surface action-to-capability mappings
- agent/team responsibilities and authority boundaries, including mandatory foundation admin agents, any app-specific agents, internal worker agents, and the governed-tool exposure labels they may use
- policy, approval, exception, audit, and outcome implications
- recommended downstream path and exact skills to load next, always including `core-saas-foundation` and normally `agent-workstream-apps` before capability or Akka component routing for generated SaaS apps
- open questions only where implementation would otherwise guess authority, risk, policy, surface/action contract, or outcome semantics

## Minimal readiness checklist

Before moving to code, verify:
- goals or delegated work are durable enough to inspect
- agent authority is explicit and bounded
- policy and permission controls are enforceable, not only suggested in prompts
- approval and exception flows include evidence, risk, confidence, impact, and alternatives when consequential
- traces connect actions and decisions to goals, policies, tools, data access, approvals, and outcomes
- UI surfaces prioritize supervision, decisions, governance, digests, and audit where those needs exist
- the selected Akka component set is the minimal substrate for the requested scope
