# Minimum AI-First SaaS App

## Status and purpose

This is the canonical doctrine for the smallest generated AI-first SaaS starter that this skills pack may treat as a valid first implementation slice.

The minimum app is **not a generic chatbot**. It is a bootstrap-authorized **five core workstream v0 starter** inside the agent workstream application model. Each core workstream starts with the same first renderable response surface, `markdown_response`, usually model-authored markdown rendered as sanitized HTML.

Use this doctrine when routing prompts such as “minimum AI-first app,” “starter app,” “basic app,” “initial chatbot,” or “smallest useful generated SaaS app.” The correct interpretation is a minimal five-core workstream shell, not a casual chat application and not a full production-ready core SaaS foundation.

## Minimum starter shape

The minimum starter must include these parts together:

```text
bootstrap-authorized human user
+ selected AuthContext
+ role-authorized five core workstream v0 set
+ bounded functional agents for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy
+ request/response timeline for each visible core workstream
+ markdown_response structured surface
+ durable workstream log
+ capability boundary for backend actions and agent tools
+ audit/work trace substrate
```

The app shell may look chat-like: a left rail, main workstream timeline, and persistent composer. That visual shape does not make the app a chatbot. The left rail exposes role-authorized workstreams; the main panel renders durable requests, responses, capability results, traces, and structured surfaces; the composer is an input channel for the selected functional agent.

## First workstreams: five core v0 set

The first functional agents are the **five core v0 workstream agents**, scoped to bootstrap operation of the generated SaaS foundation:

| Workstream | v0 responsibility |
|---|---|
| My Account | Explain current account, selected AuthContext, profile/settings scope, sign-out path, and safe self-service next steps. |
| User Admin | Explain bootstrap user administration, current access, roles/capabilities, invitation/readiness gaps, and safe next steps. |
| Agent Admin | Explain governed agent definitions, prompts, skills, manifests, model refs, tool boundaries, and deferred full-core behavior-management work. |
| Audit/Trace | Explain available audit/work trace substrate, trace links, correlation ids, denial traces, and deferred search/investigation surfaces. |
| Governance/Policy | Explain policy/permission concepts, approval/governance boundaries, behavior-change controls, and deferred full-core governance surfaces. |

In v0 each workstream may be intentionally narrow, but each must still be a real functional-agent workstream. If the workstream claims model-backed behavior, message submission must run through the governed prompt/runtime path, invoke a concrete Akka `Agent` component for the selected workstream, and use a configured provider boundary; service-only provider calls that bypass the Akka Agent component are not complete workstream-agent runtime. Missing provider configuration must produce a safe blocked/error surface and trace, not a deterministic canned reply. Deterministic/demo/mock/model-less workstream replies are acceptable only as explicitly named test doubles or fixture-mode development aids, never as the normal runtime path used to call the starter functional.

Minimum responsibilities across the five core v0 agents:

- answer bootstrap questions within the selected workstream's known scope;
- explain current bootstrap access, selected AuthContext, roles, visible capabilities, and unavailable/deferred capabilities;
- guide the operator through safe next steps for completing full User Admin, Agent Admin, Audit/Trace, Governance/Policy, invitations, and full security readiness;
- expose only established backend capabilities or read-only evidence tools;
- deny or defer actions that require full-core features not yet implemented;
- emit durable workstream entries and audit/work traces for requests, responses, tool use, capability checks, and denials.

Minimum authority:

- bootstrap-only access, not public self-registration;
- explicit role/capability checks for the bootstrap user and selected core workstream;
- no autonomous privilege expansion;
- no silent creation of tenant/customer users outside accepted bootstrap policy;
- no prompt- or frontend-only authorization.

## `markdown_response` as the first surface

The minimum starter's first structured surface is `markdown_response` for **each** of the five core v0 workstreams.

It is a real surface contract, not an informal chat blob. At minimum it needs:

- stable surface type: `markdown_response`;
- versioned payload containing markdown content and optional title/summary metadata;
- rendering as sanitized HTML with unsafe HTML, scripts, event handlers, and untrusted links blocked or transformed according to the UI security policy;
- association with workstream entry id, agent id, AuthContext, correlation id, and trace ids;
- explicit loading, success, error, forbidden, and empty states;
- accessibility expectations for headings, links, lists, code blocks, focus behavior, and screen-reader flow;
- tests for sanitization, safe rendering, forbidden responses, trace links, and redaction.

Richer app behavior should add typed surfaces such as tables, forms, decision cards, audit timelines, settings cards, access-review queues, policy diffs, behavior-test results, and workflow status cards. Those richer surfaces grow from the same workstream/surface/capability model rather than replacing it with page-first CRUD.

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
- tests for allowed bootstrap access, forbidden access, disabled or missing authority where modeled, trace creation, markdown sanitization, five-core workstream visibility, and frontend secret boundaries.

Akka local execution is the production-like validation path for the starter. Minimum starter readiness requires local backend/API/UI smoke or manual validation that exercises the real runtime behavior, not only fixture rendering or mocked unit tests. Test fixtures and fakes should be named as such and isolated to tests.

Prompt instructions, loaded skill text, hidden UI state, and route names cannot grant authority. The backend capability contract remains authoritative.

## Readiness levels

Do not collapse these states.

| Readiness state | Meaning | Required follow-up |
|---|---|---|
| Minimum starter ready | Five core workstream v0 set works for bootstrap-authorized users with `markdown_response`, selected AuthContext, backend capability boundary, durable workstream log, and audit/work trace substrate. | Complete richer User Admin, Agent Admin, Audit/Trace, Governance/Policy, and My Account surfaces; invitations/onboarding; governed agent documents; and security coverage. |
| Full core SaaS ready | The secure generated-app foundation is complete: WorkOS/AuthKit, local authorization, tenant/customer boundaries, `/api/me`, invitations with Resend/outbox, complete User Admin, Agent Admin, Audit/Trace, Governance/Policy, My Account, governed runtime agent artifacts, audit/trace search, support access, billing boundary where needed, and full security tests. | Add app-specific domain workstreams, capabilities, surfaces, and outcomes. |
| App-specific ready | Full core SaaS foundation plus product/domain functional agents, capabilities, components, UI surfaces, tests, and operational reviews. | Continue iterative feature delivery and governed improvement. |

Minimum starter readiness is intentionally useful but narrower than full-core SaaS readiness. Full-core readiness remains stricter and must not be weakened to match the starter.

## Growth path

Use this sequence for iterative generation:

```text
five core workstream v0 set
  bootstrap auth + markdown_response + workstream log + trace substrate
→ fuller My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy structured surfaces and capabilities
→ invitations/onboarding, support access, security completeness
→ app-specific functional agents, surfaces, capabilities, and outcomes
```

Each step must preserve capability-first backend semantics, backend authorization, audit/work traces, and tenant/customer isolation.

A new app-specific workstream follows the same extension loop:

```text
new PRD
→ new functional agent/workstream v0
→ markdown_response shell
→ prompt/skills/model refs
→ governed capabilities
→ richer structured surfaces
→ Akka components/tests
```

## Routing implications

When a user asks for the smallest useful AI-first SaaS app, route as follows:

1. Apply secure AI-first SaaS interpretation.
2. Apply this minimum app doctrine when the request is for a starter/minimum/basic app or an initial chatbot-like shell.
3. Treat the first runnable starter as the five core v0 workstream set with `markdown_response`: My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
4. Model capabilities before selecting Akka components or exposing agent tools/browser actions.
5. Record follow-up tasks for full-core SaaS readiness instead of pretending the minimum starter is complete production foundation.

Do not generate a standalone chatbot, public unauthenticated assistant, or page-first CRUD admin app unless the user explicitly asks for non-SaaS reference material outside this generated-app default.
