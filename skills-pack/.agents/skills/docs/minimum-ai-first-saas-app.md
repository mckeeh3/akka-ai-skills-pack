# SaaS Foundation App

## Status and purpose

This is the canonical doctrine for the **SaaS Foundation App** that ships with this repository.

The SaaS Foundation App is not a throwaway starter, a demo, a generic chatbot, or a lesser readiness tier. It is the out-of-the-box runnable secure AI-first SaaS application that users clone or fork and then extend. It contains the built-in SaaS foundation domain with five role-authorized workstreams: My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy.

Keep this file name for compatibility with existing skill references, but treat the current concept as **SaaS Foundation App**, not a legacy staging concept.

## Product model

The repository starts with a fully functional foundation domain. Downstream product work extends that app by adding business-specific domains, workstreams, surfaces, agents, governed capabilities, Akka components, frontend extensions, app-description extensions, specs, docs, and tests. The foundation domain can also be modified when the product needs it; it is simply the domain that comes out of the box.

```text
SaaS Foundation App
  built-in SaaS foundation domain
    My Account workstream
    User Admin workstream
    Agent Admin workstream
    Audit/Trace workstream
    Governance/Policy workstream
  + business-specific domains added by users
    domain workstreams
    surfaces
    agents and shared governed tools
    Akka components
    frontend/app-description/spec/doc/test extensions
```

Users should not generate a separate parallel baseline app. They should extend the runnable repository root or a fork using merge-friendly package and extension zones.

## Workstream shell and surfaces

The app shell may look chat-like: a role-authorized workstream rail, main workstream timeline, and persistent composer. That visual shape does not make the app a chatbot.

A workstream is a durable interaction space owned by a functional/context-area agent. User requests, agent replies, capability results, system messages, forms, tables, decision cards, trace timelines, policy diffs, workflow status cards, and other UI payloads are rendered as **surfaces**. Surface types range from model-authored `markdown_response` to SaaS-style interactive forms, lists, detail screens, and review cards.

My Account is one of the five foundation workstreams, but its launcher is the signed-in user tile/email at the bottom of the rail; it should not be duplicated as a normal top-rail workstream.

## Foundation workstreams

| Workstream | Responsibility |
|---|---|
| My Account | Current account, selected AuthContext, profile/settings, notifications/attention, sign-out, and safe self-service. |
| User Admin | User, membership, role, invitation, access-review, support-access, and admin-audit operations within authority boundaries. |
| Agent Admin | Governed agent definitions, prompts, skills, references, manifests, model refs, tool boundaries, behavior editing, test consoles, and lifecycle operations. |
| Audit/Trace | Searchable/explainable audit and work traces for identity, authz, data access, tool use, decisions, workflows, agents, denials, and investigations. |
| Governance/Policy | Policy/permission concepts, proposals, simulations, approvals, activation/rollback, improvement governance, and outcome evidence. |

Each workstream must remain role-authorized, tenant/customer scoped where applicable, traceable, and backed by backend capability checks. Prompt text, hidden frontend state, and route names cannot grant authority.

## Prescriptive component architecture

The skills pack uses an opinionated component architecture. It is intentionally prescriptive: feature intent is translated into governed capabilities and then into the defined Akka component set. This is **the way** the pack implements apps, rather than one option among many.

Use the eleven Akka component families deliberately:

1. Event Sourced Entity
2. Key Value Entity
3. Workflow
4. request-based Agent
5. Autonomous Agent
6. View
7. Consumer
8. Timed Action
9. HTTP Endpoint
10. gRPC Endpoint
11. MCP Endpoint

Capabilities and governed tools are the bridge between product intent and components. The same components implement business functionality while exposing selected actor adapters such as `surface_action`, confirmed `human_chat_tool_plan`, `agent_tool_call`, `workflow_step`, `timer_invocation`, `consumer_reaction`, `api_call`, `mcp_tool_call`, and `internal_call`. Humans, agents, and systems use governed tools only through explicit, backend-authorized capability/tool boundaries with durable traces; the AI model is not the security boundary.

## Required runtime semantics

The SaaS Foundation App and its extensions must validate through the real local Akka/API/UI path at the stated scope.

Required semantics:

- authenticated or bootstrap-authorized identity basis;
- selected `AuthContext` with account/user identity, selected scope, roles, capabilities, and actor metadata;
- backend authorization checks for protected workstream, surface, API, component, stream, and tool actions;
- tenant/customer boundary model where applicable;
- durable workstream log for requests, responses, tool/capability results, denials, and trace references, while rendering one primary result surface per prompt/action instead of duplicate generic activity/detail surfaces;
- audit/work trace substrate for identity, authorization, agent prompt/skill/reference/model/tool use, capability checks, data access, and denials;
- capability-first backend modeling before exposing browser actions, confirmed human chat tool plans, agent tools, workflows, timers, consumers, or APIs;
- model-backed workstream agents invoke concrete Akka `Agent` components through governed runtime configuration, active prompts/manifests/tool boundaries, registered runtime tools, provider boundary, and traces;
- missing provider or security configuration fails closed with an actionable blocked/error surface, not a deterministic canned success;
- test fixtures and fakes are isolated to tests or explicitly named fixture modes.

## Extension path

Use this sequence for product work:

```text
understand feature intent
→ classify affected domain/workstream/surface/agent/capability
→ define governed capability and tool boundaries plus actor adapters (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, workflow/timer/consumer/API/MCP/internal)
→ select the required Akka component family or families
→ implement backend state, behavior, endpoints/tools, traces, and tests
→ implement or extend workstream surfaces in the shell
→ validate through local Akka/API/UI runtime
```

For a new business domain:

```text
business PRD or prompt
→ business domain model
→ role-authorized workstreams and functional agents
→ initial surface contracts, often markdown_response plus required structured surfaces
→ governed capabilities and tools
→ Akka components and tests
→ frontend workstream shell extensions
→ app-description/spec/doc updates
```

Internal/background model-driven work should not be hidden inside an immediate request/response turn. Keep user-facing workstream turns request-based, and launch durable background work through governed capabilities backed by Akka `AutonomousAgent` when task lifecycle, dependencies, notifications, model-driven investigation, delegation/handoff/team coordination, evaluation/replay, monitoring/remediation, cancellation, or failure state matter.

## Routing implications

When a user asks for a SaaS Foundation App, starter, baseline, minimum app, basic app, or chatbot-like generated SaaS shell, interpret that as the **SaaS Foundation App** unless they explicitly ask for non-SaaS reference material. The correct response is not to create a generic chatbot or parallel blank app; it is to maintain or extend the built-in five-workstream SaaS foundation domain and then add business-specific domains/workstreams as needed.
