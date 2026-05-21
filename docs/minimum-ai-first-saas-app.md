# Minimum AI-First SaaS App

## Status and purpose

This is the canonical doctrine for the smallest generated AI-first SaaS starter that this skills pack may treat as a valid first implementation slice.

The minimum app is **not a generic chatbot**. It is a bootstrap-authorized **User Admin workstream v0** inside the agent workstream application model. Its first renderable response surface is `markdown_response`, usually model-authored markdown rendered as sanitized HTML.

Use this doctrine when routing prompts such as “minimum AI-first app,” “starter app,” “basic app,” “initial chatbot,” or “smallest useful generated SaaS app.” The correct interpretation is a minimal User Admin workstream, not a casual chat application and not a full production-ready core SaaS foundation.

## Minimum starter shape

The minimum starter must include these parts together:

```text
bootstrap-authorized human user
+ selected AuthContext
+ role-authorized User Admin workstream v0
+ UserAdminAgent with bounded bootstrap authority
+ request/response timeline
+ markdown_response structured surface
+ durable workstream log
+ capability boundary for backend actions and agent tools
+ audit/work trace substrate
```

The app shell may look chat-like: a left rail, main workstream timeline, and persistent composer. That visual shape does not make the app a chatbot. The left rail exposes role-authorized workstreams; the main panel renders durable requests, responses, capability results, traces, and structured surfaces; the composer is an input channel for the selected functional agent.

## First workstream: User Admin v0

The first functional agent is **User Admin Agent**, scoped to bootstrap administration of the generated SaaS foundation. In v0 it may be intentionally narrow, but it must still be a real functional-agent workstream.

Minimum responsibilities:

- answer bootstrap user-administration questions within its known scope;
- explain current bootstrap access, selected AuthContext, roles, and available capabilities;
- guide the operator through safe next steps for completing User Admin, Agent Admin, Audit/Trace, invitations, and full security readiness;
- expose only established backend capabilities or read-only evidence tools;
- deny or defer actions that require full-core features not yet implemented;
- emit durable workstream entries and audit/work traces for requests, responses, tool use, capability checks, and denials.

Minimum authority:

- bootstrap-only access, not public self-registration;
- explicit role/capability checks for the bootstrap user;
- no autonomous privilege expansion;
- no silent creation of tenant/customer users outside accepted bootstrap policy;
- no prompt- or frontend-only authorization.

## `markdown_response` as the first surface

The minimum starter's first structured surface is `markdown_response`.

It is a real surface contract, not an informal chat blob. At minimum it needs:

- stable surface type: `markdown_response`;
- versioned payload containing markdown content and optional title/summary metadata;
- rendering as sanitized HTML with unsafe HTML, scripts, event handlers, and untrusted links blocked or transformed according to the UI security policy;
- association with workstream entry id, agent id, AuthContext, correlation id, and trace ids;
- explicit loading, success, error, forbidden, and empty states;
- accessibility expectations for headings, links, lists, code blocks, focus behavior, and screen-reader flow;
- tests for sanitization, safe rendering, forbidden responses, trace links, and redaction.

Richer app behavior should add typed surfaces such as tables, forms, decision cards, audit timelines, settings cards, access-review queues, and workflow status cards. Those richer surfaces grow from the same workstream/surface/capability model rather than replacing it with page-first CRUD.

## Required backend semantics

A minimum starter remains a secure SaaS slice. It may defer complete full-core features, but it must not defer the backend security model.

Required from the first slice:

- authenticated or bootstrap-authorized identity basis;
- local `AuthContext` with account/user identity, selected scope, roles, capabilities, and actor metadata;
- backend authorization checks for protected workstream, surface, API, component, and tool actions;
- tenant/customer boundary model where applicable, even if only a bootstrap tenant exists initially;
- durable workstream log for requests, responses, tool/capability results, denials, and trace references;
- audit/work trace substrate for identity, authorization, agent prompt/skill/tool use, capability checks, data access, and denials;
- capability-first backend modeling before exposing browser actions, agent tools, workflows, timers, consumers, or APIs;
- tests for allowed bootstrap access, forbidden access, disabled or missing authority where modeled, trace creation, markdown sanitization, and frontend secret boundaries.

Prompt instructions, loaded skill text, hidden UI state, and route names cannot grant authority. The backend capability contract remains authoritative.

## Readiness levels

Do not collapse these states.

| Readiness state | Meaning | Required follow-up |
|---|---|---|
| Minimum starter ready | User Admin workstream v0 works for bootstrap-authorized users with `markdown_response`, selected AuthContext, backend capability boundary, durable workstream log, and audit/work trace substrate. | Complete full User Admin, Agent Admin, Audit/Trace UI, invitations/onboarding, governed agent documents, and security coverage. |
| Full core SaaS ready | The secure generated-app foundation is complete: WorkOS/AuthKit, local authorization, tenant/customer boundaries, `/api/me`, invitations with Resend/outbox, complete User Admin, Agent Admin, governed runtime agent artifacts, audit/trace search, support access, billing boundary where needed, and full security tests. | Add app-specific domain workstreams, capabilities, surfaces, and outcomes. |
| App-specific ready | Full core SaaS foundation plus product/domain functional agents, capabilities, components, UI surfaces, tests, and operational reviews. | Continue iterative feature delivery and governed improvement. |

Minimum starter readiness is intentionally useful but narrower than full-core SaaS readiness. Full-core readiness remains stricter and must not be weakened to match the starter.

## Growth path

Use this sequence for iterative generation:

```text
User Admin workstream v0
  bootstrap auth + markdown_response + workstream log + trace substrate
→ fuller User Admin structured surfaces and admin capabilities
→ Agent Admin workstream and governed behavior documents
→ Audit/Trace workstream UI and search surfaces
→ invitations/onboarding, support access, security completeness
→ app-specific functional agents, surfaces, capabilities, and outcomes
```

Each step must preserve capability-first backend semantics, backend authorization, audit/work traces, and tenant/customer isolation.

## Routing implications

When a user asks for the smallest useful AI-first SaaS app, route as follows:

1. Apply secure AI-first SaaS interpretation.
2. Apply this minimum app doctrine when the request is for a starter/minimum/basic app or an initial chatbot-like shell.
3. Treat the first vertical as User Admin workstream v0 with `markdown_response`.
4. Model capabilities before selecting Akka components or exposing agent tools/browser actions.
5. Record follow-up tasks for full-core SaaS readiness instead of pretending the minimum starter is complete production foundation.

Do not generate a standalone chatbot, public unauthenticated assistant, or page-first CRUD admin app unless the user explicitly asks for non-SaaS reference material outside this generated-app default.
