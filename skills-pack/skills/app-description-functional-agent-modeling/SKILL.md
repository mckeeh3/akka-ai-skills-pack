---
name: app-description-functional-agent-modeling
description: Model user-facing role-authorized functional/context-area agents in an app-description workstream layer, including authority, prompt intent, skills, tools, surfaces, capabilities, traces, and tests.
---

# App Description Functional-Agent Modeling

Use this skill when maintaining the app-description `12-workstreams/functional-agents.md` layer or equivalent artifacts for generated full-stack AI-first SaaS apps.

Functional agents are the user-facing vertical application areas in the authenticated workstream shell. They are not Akka components, chat sessions, pages, or generic assistants. Each functional agent owns or reuses structured surfaces, starts from role-specific dashboard surfaces, maintains a human surface graph, and invokes governed backend capabilities through explicit authority rules. Normalize UI nouns such as dashboard, portal, admin screen, command center, work queue, approval area, or agent/chat space into functional-agent ownership plus reusable surface contracts before route/page details.

## Required reading

Read these first if present:
- target project path: AGENTS.md
- `../README.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/requirements-to-workstream-development-process.md` for workstream → attention → dashboard → surfaces/actions → capabilities → autonomous task/notification/projection/trace semantics
- `../docs/internal-app-description-architecture.md`
- `../docs/app-description-maintenance-flow.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/capability-first-backend-architecture.md`
- `../app-descriptions/SKILL.md`
- `../agent-workstream-apps/SKILL.md`
- `../app-description-capability-modeling/SKILL.md`
- `../app-description-surface-modeling/SKILL.md`
- `../app-description-ui/SKILL.md`
- existing `app-description/12-workstreams/**`
- existing `app-description/70-traceability/functional-agent-to-capability-map.md`

## Use this skill when

The task asks to:
- add, remove, split, or revise a user-facing agent, context-area agent, role workspace, command center, or functional work area;
- describe which agents appear in the left rail of the authenticated shell;
- define role-specific dashboard surfaces, attention categories, surface graph nodes/edges, or the internal workstream agent graph for a workstream;
- define what a functional agent can do, which surfaces it renders, or which capabilities it can call;
- capture workstream expertise skills/references, prompt intent, governed prompt documents, governed-tool permissions, or behavior boundaries for a user-facing agent;
- update app-description artifacts before implementation of agent workstream UI or Akka agents.

Do not use this as the main skill for internal-only classifier/summarizer/evaluator agents. Model those in `12-workstreams/internal-agents.md` and route to agent implementation skills only after capability and authority semantics are clear.

## Artifact targets

Prefer these app-description artifacts:

```text
app-description/12-workstreams/
  functional-agents.md
  attention-and-dashboards.md
  workstreams-and-retention.md
  internal-agents.md
  surfaces-index.md
  workstream-expertise/
    README.md
    <functional-agent-id>.md

app-description/55-ui/
  workstream-shell.md
  functional-agent-rail.md
  workstream-panel-and-composer.md

app-description/70-traceability/
  functional-agent-to-capability-map.md
  surface-to-capability-map.md
```

Create or update only the smallest files needed. Keep `12-workstreams/functional-agents.md` authoritative for the functional-agent catalog, and keep `12-workstreams/workstream-expertise/<functional-agent-id>.md` authoritative for that functional agent's workstream expert bundle when LLM behavior is in scope.

## Functional-agent contract

Use `../docs/app-description-skill-output-contracts.md` for the detailed output contract. Return only the actionable summary, affected artifacts/layers, required edits or queue changes, assumptions/questions, and next step. Preserve secure SaaS foundation, generated-SaaS runtime completion, tenant/customer scoping, backend authorization, governed agents/tools, traces, and tests when in scope.

## Modeling rules

1. **Functional agents are verticals.** Model each as a role-authorized work area with attention needs, role-specific dashboard surfaces, a surface graph, capabilities/governed-tools, workstream expertise, internal workstream agent graph candidates, and workstream icon semantics. Do not model it as an Akka `Agent` class first.
2. **Attention is first-class.** Each workstream must answer `what needs my attention?` for authorized users and define how counts/projections feed the dashboard, left rail, and My Account before UI badges or routes are described.
3. **Backend capabilities remain authoritative.** A functional agent can call or expose capabilities/governed-tools, but prompt text, rail visibility, workstream expertise text, and tool descriptions never authorize work.
4. **Surfaces are structured artifacts.** Prefer dashboards, forms, tables, charts, decision cards, diffs, audit timelines, detail cards, approvals, workflow status, evidence bundles, autonomous task progress/result cards, version cards, and outcome panels over free-text-only responses.
5. **Autonomous task candidates stay governed.** Durable model-driven investigation, review, summary, monitoring/remediation, or specialist follow-up should be modeled as internal-agent/autonomous task candidates with lifecycle notifications and capability boundaries; request-based Akka `Agent` remains the default for immediate user-facing turns.
6. **Foundation agents are built in.** SaaS Foundation App scope includes the foundation-domain functional agents — My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy — with role authority, structured surfaces, durable workstream logs, trace links, capability/tool boundaries, and denial behavior. Business-domain agents extend this set rather than replacing it. If a task intentionally defers or removes a foundation behavior, record that scope explicitly.
7. **Keep internal agents separate.** Classifiers, summarizers, evaluators, proposal drafters, and governance reviewers may support a functional agent, but they do not become left-rail work areas unless they represent a user-facing responsibility boundary.
8. **Record tool boundaries as governed behavior.** Side-effecting tools should default to proposal or approval flows unless accepted policy grants bounded autonomous authority.
9. **Link tests immediately.** A functional agent is incomplete without authorization, surface, capability, prompt/tool-boundary, and trace tests.

## Change handling

When a functional-agent change adds or changes:

- a callable operation/query, update `10-capabilities/` via `app-description-capability-modeling`;
- surfaces, browser actions, or controls that open surfaces/workstreams, update `12-workstreams/surfaces-index.md` and `surface-contracts/**` via `app-description-surface-modeling`, plus `55-ui/**` and surface-to-capability traceability; treat buttons, links, rows, cards, and icons that open protected workstreams/surfaces as surface-request actions such as `open_workstream`;
- model binding, provider alias, fallback, or model policy, update `15-operating-model/governed-runtime-agents.md`, model-governance artifacts, readiness state, tests, and trace expectations;
- prompt intent, skills, tools, or behavior documents, update `15-operating-model/governed-runtime-agents.md` and relevant agent governance artifacts;
- approval, policy, escalation, or autonomy, update operating-model, behavior, auth/security, tests, and observability layers;
- roles, permissions, or scope, update auth/security and `/api/me`/capability exposure expectations;
- traces or audits, update observability and traceability artifacts.

## Handoff rules

Route onward as needed:

- to `app-description-capability-modeling` for capability contracts behind callable actions, tools, skill/reference loaders, or expertise manifest entries;
- to `app-description-ui` for rail behavior, workstream panel behavior, composer behavior, and structured surface rendering;
- to `app-description-auth-security` for roles, permissions, scope, forbidden states, and backend authorization;
- to `app-description-observability` for audit/work traces and diagnosability;
- to `app-description-test-specification` for authorization, surface, tool-boundary, tenant-isolation, and audit tests;
- to `akka-web-ui-apps` only when realization of the workstream shell is requested;
- to `akka-agents` and focused agent skills only when implementation of governed functional or internal agent behavior is requested.

## Anti-patterns

Avoid:
- treating functional agents as chatbot personas bolted onto pages;
- making page/screen hierarchy the primary app-description structure;
- omitting role-specific dashboards, surface graph edges, internal workstream agent graph candidates, or workstream expertise when the workstream needs them;
- granting authority through prompt instructions, hidden UI state, rail visibility, or expertise text;
- listing tools without linking them to governed capabilities and governed-tool exposure channels;
- omitting tenant/customer scope or AuthContext;
- leaving LLM-backed workstream agents with implicit, prompt-selected, or provider-secret-bearing model bindings;
- treating a SaaS Foundation App as a generic chatbot or single-workstream admin slice instead of the SaaS Foundation App domain;
- leaving User Admin or Agent Admin out of SaaS Foundation App scope without an explicit deferral;
- allowing a functional agent to perform side effects without approval, policy, idempotency, and audit semantics.

## Final review checklist

Before finishing a functional-agent update, verify:

- [ ] each user-facing functional agent has purpose, authority, workstream icon metadata, surfaces, capabilities, and when LLM-enabled, a workstream expert bundle with prompt intent, explicit `ModelConfigRef`/`ModelPolicy` or inherited governed default model binding, skills, references, compact manifest, tool boundary/loaders, traces, and tests;
- [ ] functional agents are distinguished from internal agents;
- [ ] every side-effecting action maps to a governed capability and backend authorization rule;
- [ ] surfaces are typed and linked to capability-backed actions;
- [ ] traceability maps link functional agents to capabilities, surfaces, UI, tests, and observability;
- [ ] no page-first or chatbot-bolt-on structure became the primary model.
