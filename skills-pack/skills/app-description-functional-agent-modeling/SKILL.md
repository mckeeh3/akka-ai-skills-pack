---
name: app-description-functional-agent-modeling
description: Model user-facing role-authorized functional/context-area agents as workstream agent bindings in the app-description current-intent graph, including authority, prompt intent, skills, tools, surfaces, capabilities, traces, and tests.
---

# App Description Functional-Agent Modeling

Use this skill when maintaining app-description workstream agent bindings such as `domains/<domain>/workstreams/<workstream>/agents/<agent-binding>.md` plus related global agent definitions for generated full-stack AI-first SaaS apps.

Functional agents are the user-facing vertical application areas in the authenticated workstream shell. They are not Akka components, chat sessions, pages, or generic assistants. Each functional agent owns or reuses structured surfaces, starts from role-specific dashboard surfaces, maintains a human surface graph, and invokes governed backend capabilities through explicit authority rules. Normalize UI nouns such as dashboard, portal, admin screen, command center, work queue, approval area, or agent/chat space into functional-agent ownership plus reusable surface contracts before route/page details.

## Required reading

Read these first if present:
- target project path: AGENTS.md
- `../README.md`
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/app-description-skill-output-contracts.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/workstream-contract.md` for compact workstream fields, type-vs-instance terminology, ownership/reuse rules, id taxonomy, and readiness labels
- `../docs/workstream-surface-intent-routing.md` for deterministic composer-to-surface routing and prefill-only behavior
- `../docs/workstream-manifest-schema.md` for any target-project machine-readable workstream index when present
- `../docs/minimum-implementable-workstream-slice.md` for one-slice implementation/task briefs
- `../docs/workstream-attention-contracts.md` for attention item, producer, lifecycle, aggregation, and tests
- `../docs/requirements-to-workstream-development-process.md` for workstream → attention → dashboard → surfaces/actions → capabilities → autonomous task/notification/projection/trace semantics
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/capability-first-backend-architecture.md`
- `../app-descriptions/SKILL.md`
- `../agent-workstream-apps/SKILL.md`
- `../app-description-capability-modeling/SKILL.md`
- `../app-description-surface-modeling/SKILL.md`
- `../app-description-ui/SKILL.md`
- existing `app-description/domains/<domain>/workstreams/<workstream>/**`
- existing `app-description/global/agents/**`, `global/tools/**`, `global/surfaces/**`, and `global/traces/**`
- existing realization and traceability files when present

## Use this skill when

The task asks to:
- add, remove, split, or revise a user-facing agent, context-area agent, role workspace, command center, or functional work area;
- describe which agents appear in the left rail of the authenticated shell;
- define role-specific dashboard surfaces, attention categories, surface graph nodes/edges, or the internal workstream agent graph for a workstream;
- define what a functional agent can do, which surfaces it renders, or which capabilities it can call;
- capture workstream expertise skills/references, prompt intent, governed prompt documents, governed-tool permissions, or behavior boundaries for a user-facing agent;
- update app-description artifacts before implementation of agent workstream UI or Akka agents.

Do not use this as the main skill for internal-only classifier/summarizer/evaluator agents. Model those as local/internal agent bindings under the supporting workstream, or as reusable global agent definitions plus explicit internal workstream bindings, and route to agent implementation skills only after capability and authority semantics are clear.

## Artifact targets

Prefer these app-description artifacts:

```text
app-description/global/agents/<agent-definition>.md
app-description/global/tools/<tool-definition>.md
app-description/global/surfaces/<surface-pattern>.md
app-description/global/traces/<trace-pattern>.md
app-description/domains/<domain>/domain.md
app-description/domains/<domain>/capabilities/<capability>.md
app-description/domains/<domain>/workstreams/<workstream>/workstream.md
app-description/domains/<domain>/workstreams/<workstream>/access.md
app-description/domains/<domain>/workstreams/<workstream>/agents/<agent-binding-or-local-agent>.md
app-description/domains/<domain>/workstreams/<workstream>/surfaces/<surface-binding>.md
app-description/domains/<domain>/workstreams/<workstream>/tools/<tool-binding>.md
app-description/domains/<domain>/workstreams/<workstream>/traces/<trace-binding>.md
app-description/domains/<domain>/workstreams/<workstream>/tests/<test-expectation>.md
app-description/domains/<domain>/workstreams/<workstream>/realization/frontend-routes.md
```

Create or update only the smallest files needed. Global agent definitions answer what an agent is; workstream agent bindings answer why and how that agent is used in a specific workstream. Keep each workstream's agent binding authoritative for its expert bundle when LLM behavior is in scope.

## Functional-agent contract

Use `../docs/app-description-skill-output-contracts.md` for the detailed output contract. Return only the actionable summary, affected graph nodes/artifacts, required edits or queue changes, assumptions/questions, and next step. Preserve secure SaaS foundation, generated-SaaS runtime completion, tenant/customer scoping, backend authorization, governed agents/tools, traces, and tests when in scope.

## Modeling rules

1. **Functional agents are verticals.** Model each as a role-authorized work area with workstream id/responsibility, exactly-one functional-agent ownership, workstream definition vs runtime instance semantics, readiness level, attention needs, role-specific dashboard surfaces, a surface graph, deterministic surface intent routes for composer-enabled work, a bounded workstream tool catalog, capabilities/governed-tools, workstream expertise, internal workstream agent graph candidates, and workstream icon semantics. Do not model it as an Akka `Agent` class first.
2. **Attention is first-class.** Each workstream must answer `what needs my attention?` for authorized users and define backend-owned attention items, producer idempotency, lifecycle, source/evidence refs, redaction, traces, stale/recompute behavior, and how counts/projections feed the dashboard, left rail, and My Account before UI badges or routes are described.
3. **Backend capabilities remain authoritative.** A functional agent can call or expose capabilities/governed-tools, but prompt text, rail visibility, workstream expertise text, and tool descriptions never authorize work.
4. **Model actor adapters, not duplicate operations.** For every consequential workstream tool, record the shared governed tool id once and bind each allowed adapter separately: surface action/browser-tool for human surface use, `human_chat_tool_plan` for confirmed human chat execution, AI agent-tool when the ToolPermissionBoundary allows model-facing use, and workflow/API/MCP/internal exposures when present. The adapters may differ in input mediation, confirmation UX, approval policy, and trace source; they must not duplicate business semantics or bypass capability authorization.
5. **Surfaces are structured artifacts.** Prefer dashboards, forms, tables, charts, decision cards, diffs, audit timelines, detail cards, approvals, workflow status, evidence bundles, autonomous task progress/result cards, version cards, and outcome panels over free-text-only responses. Each composer-enabled functional agent should know its workstream surface catalog so high-confidence user prompts can open or prepopulate surfaces before model fallback, without submitting commands; if the workstream also supports confirmed human chat tool plans, model the proposed-plan, confirmation binding, per-tool idempotent transaction boundaries, result/partial-failure surfaces, and trace source separately.
6. **Autonomous task candidates stay governed.** Durable model-driven investigation, review, summary, monitoring/remediation, or specialist follow-up should be modeled as internal-agent/autonomous task candidates with lifecycle notifications and capability boundaries; request-based Akka `Agent` remains the default for immediate user-facing turns.
7. **Foundation agents are built in.** SaaS Foundation App scope includes the foundation-domain functional agents — My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy — with role authority, structured surfaces, durable workstream logs, trace links, capability/tool boundaries, and denial behavior. Business-domain agents extend this set rather than replacing it. If a task intentionally defers or removes a foundation behavior, record that scope explicitly.
8. **Keep internal agents separate.** Classifiers, summarizers, evaluators, proposal drafters, and governance reviewers may support a functional agent, but they do not become left-rail work areas unless they represent a user-facing responsibility boundary.
9. **Record tool boundaries as governed behavior.** Side-effecting tools should default to proposal or approval flows unless accepted policy grants bounded autonomous authority.
10. **Link tests immediately.** A functional agent is incomplete without authorization, surface, capability, prompt/tool-boundary, and trace tests.

## Change handling

When a functional-agent change adds or changes:

- a callable operation/query, update `domains/<domain>/capabilities/**` via `app-description-capability-modeling`;
- surfaces, browser actions, controls, or composer surface-intent routes that open/prepopulate surfaces/workstreams, update `domains/<domain>/workstreams/<workstream>/surfaces/**` via `app-description-surface-modeling`, plus workstream realization files and surface-to-capability traceability; treat buttons, links, rows, cards, icons, and high-confidence prompt aliases that open protected workstreams/surfaces as surface-request actions such as `open_workstream` or `show_surface`;
- model binding, provider alias, fallback, or model policy, update the relevant global agent/model policy definition and workstream agent binding, readiness state, tests, and trace expectations;
- prompt intent, skills, tools, or behavior documents, update global governed-document definitions and relevant workstream agent/tool bindings, preserving global tool definitions plus workstream-specific adapter bindings;
- approval, policy, escalation, or autonomy, update policy definitions/bindings, behavior, auth/security, tests, and observability graph nodes;
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
- omitting role-specific dashboards, surface graph edges, deterministic surface intent routes, internal workstream agent graph candidates, or workstream expertise when the workstream needs them;
- granting authority through prompt instructions, hidden UI state, rail visibility, or expertise text;
- listing tools without linking them to governed capabilities and governed-tool exposure channels;
- omitting tenant/customer scope or AuthContext;
- leaving LLM-backed workstream agents with implicit, prompt-selected, or provider-secret-bearing model bindings;
- treating a SaaS Foundation App as a generic chatbot or single-workstream admin slice instead of the SaaS Foundation App domain;
- leaving User Admin or Agent Admin out of SaaS Foundation App scope without an explicit deferral;
- allowing a functional agent to perform side effects without approval, policy, idempotency, and audit semantics.

## Final review checklist

Before finishing a functional-agent update, verify:

- [ ] each user-facing functional agent has workstream id/responsibility, required managed-agent definition id, definition-vs-instance semantics, readiness level, purpose, authority, workstream icon metadata with tooltip, attention/dashboard contract, surfaces, capabilities, and when LLM-enabled, a workstream expert bundle with prompt intent, explicit `ModelConfigRef`/`ModelPolicy` or inherited governed default model binding, skills, references, compact manifest, tool boundary/loaders, traces, and tests;
- [ ] functional agents are distinguished from internal agents;
- [ ] every side-effecting action maps to a governed capability and backend authorization rule;
- [ ] every allowed surface action, confirmed human chat tool plan, and AI agent-tool adapter points to the same governed tool id when it performs the same operation, with adapter-specific confirmation/approval, idempotency, result surface, and trace semantics;
- [ ] surfaces are typed and linked to capability-backed actions and deterministic no-mutation composer routes where applicable;
- [ ] traceability maps link functional agents to capabilities, surfaces, UI, tests, and observability;
- [ ] `tools/validate-workstream-contracts.sh <app-description-dir>` would pass for app-description trees claiming workstream-contract completeness;
- [ ] no page-first or chatbot-bolt-on structure became the primary model.
