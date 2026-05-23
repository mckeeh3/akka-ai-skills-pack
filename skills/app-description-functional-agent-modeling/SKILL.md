---
name: app-description-functional-agent-modeling
description: Model user-facing role-authorized functional/context-area agents in an app-description workstream layer, including authority, prompt intent, skills, tools, surfaces, capabilities, traces, and tests.
---

# App Description Functional-Agent Modeling

Use this skill when maintaining the app-description `12-workstreams/functional-agents.md` layer or equivalent artifacts for generated full-stack AI-first SaaS apps.

Functional agents are the user-facing vertical application areas in the authenticated workstream shell. They are not Akka components, chat sessions, pages, or generic assistants. Each functional agent owns or reuses structured surfaces and invokes governed backend capabilities through explicit authority rules. Normalize UI nouns such as dashboard, portal, admin screen, command center, work queue, approval area, or agent/chat space into functional-agent ownership plus reusable surface contracts before route/page details.

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/agent-workstream-application-architecture.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../../docs/ai-first-saas-application-architecture.md`
- `../../docs/capability-first-backend-architecture.md`
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
- define what a functional agent can do, which surfaces it renders, or which capabilities it can call;
- capture prompt intent, governed prompt/skill documents, tool permissions, or behavior boundaries for a user-facing agent;
- update app-description artifacts before implementation of agent workstream UI or Akka agents.

Do not use this as the main skill for internal-only classifier/summarizer/evaluator agents. Model those in `12-workstreams/internal-agents.md` and route to agent implementation skills only after capability and authority semantics are clear.

## Artifact targets

Prefer these app-description artifacts:

```text
app-description/12-workstreams/
  functional-agents.md
  workstreams-and-retention.md
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

For each functional agent, capture:

- stable agent id and display name;
- purpose and business responsibility;
- whether it is required foundation scope or app-specific domain scope;
- tenant/customer scope and selected `AuthContext` assumptions;
- authorized roles, permissions, scopes, or named capability grants;
- default workstream entry behavior: briefing, dashboard, attention queue, recent activity, or empty state;
- durable workstream semantics: retention, replay, summarization, attachments, follow-ups, and trace links;
- prompt intent when LLM behavior is involved: what the agent should help with, what it must refuse, and when it must ask for confirmation;
- workstream expert bundle reference under `workstream-expertise/<functional-agent-id>.md` or an explicit readiness deferral;
- governed behavior artifacts: `AgentDefinition`, `PromptDocument`/`PromptVersion`, `ModelConfigRef`/`ModelPolicy` or an explicit inherited governed default model binding, `SkillDocument`/`SkillVersion`, reference documents or constrained reference records, compact `AgentSkillManifest`/expertise manifest entries, and `ToolPermissionBoundary` references when applicable;
- model binding notes for LLM-backed agents: allowed runtime/test/replay/evaluation modes, fallback or `noFallback` policy, provider secret boundary, and model-use trace facts;
- allowed tools and tool-boundary notes, including authorized `readSkill(skillId)` and reference loaders, expressed as capability exposure surfaces rather than primary backend objects;
- owned and reusable structured surfaces;
- callable backend capabilities and whether each is read-only, proposal-only, approval-gated, or bounded autonomous;
- approval, escalation, denial, exception, and safe recovery behavior;
- audit and work-trace obligations, including `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace`, data-access traces, decision traces, and AdminAuditEvent links where relevant;
- tests for authorization, tenant isolation, disabled users, surface rendering, capability invocation, denial behavior, approval/escalation, prompt/tool boundaries, and audit/trace emission.

## Standard output shape

Use this shape when adding or revising a functional agent:

```md
# Functional-Agent Modeling Update

## Requested change
- ...

## Functional agent
- id / name:
- type: foundation | domain
- purpose / responsibility:
- user-facing shell placement:

## Authority
- AuthContext / tenant/customer scope:
- roles / permissions / named capabilities:
- denial and safe recovery:

## Workstream behavior
- default entry surface:
- workstream retention / replay / summary:
- composer intent and accepted inputs:
- follow-up and handoff behavior:

## Workstream expertise
- expert bundle artifact:
- prompt intent:
- governed agent definition / prompt references:
- model binding (`ModelConfigRef`/`ModelPolicy` or inherited governed default):
- skill documents:
- reference documents:
- compact expertise manifest entries:
- tool permission boundary and loaders:
- autonomous vs proposal vs approval-gated actions:

## Surfaces and capabilities
- owned surfaces:
- reusable surfaces:
- callable capabilities:
- surface action-to-capability links:

## Supervision, audit, and traces
- approval / escalation / exception behavior:
- audit events:
- work traces:
- visible trace links:

## Tests
- authorization / tenant isolation:
- surface rendering:
- capability invocation:
- prompt/skill/tool boundary:
- audit/trace:

## Linked layers
- capabilities:
- operating model:
- behavior:
- tests:
- auth/security:
- observability:
- UI:
- traceability:
```

## Modeling rules

1. **Functional agents are verticals.** Model each as a role-authorized work area with surfaces and capabilities. Do not model it as an Akka `Agent` class first.
2. **Backend capabilities remain authoritative.** A functional agent can call or expose capabilities, but prompt text, rail visibility, and tool descriptions never authorize work.
3. **Surfaces are structured artifacts.** Prefer dashboards, forms, tables, charts, decision cards, diffs, audit timelines, detail cards, approvals, workflow status, evidence bundles, version cards, and outcome panels over free-text-only responses.
4. **Foundation agents are scope-sensitive.** Minimum starter scope must include User Admin workstream v0 as a real role-authorized functional agent with bootstrap authority, `markdown_response`, a durable workstream log, trace links, capability/tool boundaries, denial behavior, and follow-up gaps to full core. Full generated core apps must include full User Admin and Agent Admin functional agents, plus access/profile, audit/trace, and governance/policy coverage as justified by scope. If deferred, record the narrower scope explicitly and never label it full-core ready.
5. **Keep internal agents separate.** Classifiers, summarizers, evaluators, proposal drafters, and governance reviewers may support a functional agent, but they do not become left-rail work areas unless they represent a user-facing responsibility boundary.
6. **Record tool boundaries as governed behavior.** Side-effecting tools should default to proposal or approval flows unless accepted policy grants bounded autonomous authority.
7. **Link tests immediately.** A functional agent is incomplete without authorization, surface, capability, prompt/tool-boundary, and trace tests.

## Change handling

When a functional-agent change adds or changes:

- a callable operation/query, update `10-capabilities/` via `app-description-capability-modeling`;
- surfaces or browser actions, update `12-workstreams/surfaces-index.md` and `surface-contracts/**` via `app-description-surface-modeling`, plus `55-ui/**` and surface-to-capability traceability;
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
- granting authority through prompt instructions, hidden UI state, or rail visibility;
- listing tools without linking them to governed capabilities;
- omitting tenant/customer scope or AuthContext;
- leaving LLM-backed workstream agents with implicit, prompt-selected, or provider-secret-bearing model bindings;
- treating a minimum starter as a generic chatbot instead of User Admin workstream v0;
- leaving User Admin or Agent Admin out of full core SaaS scope without an explicit deferral;
- allowing a functional agent to perform side effects without approval, policy, idempotency, and audit semantics.

## Final review checklist

Before finishing a functional-agent update, verify:

- [ ] each user-facing functional agent has purpose, authority, surfaces, capabilities, and when LLM-enabled, a workstream expert bundle with prompt intent, explicit `ModelConfigRef`/`ModelPolicy` or inherited governed default model binding, skills, references, compact manifest, tool boundary/loaders, traces, and tests;
- [ ] functional agents are distinguished from internal agents;
- [ ] every side-effecting action maps to a governed capability and backend authorization rule;
- [ ] surfaces are typed and linked to capability-backed actions;
- [ ] traceability maps link functional agents to capabilities, surfaces, UI, tests, and observability;
- [ ] no page-first or chatbot-bolt-on structure became the primary model.
