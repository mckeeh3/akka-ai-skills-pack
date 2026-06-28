---
name: app-description-surface-modeling
description: Model structured workstream surfaces in app descriptions, including the mandatory collection-object list/show/create/edit/destructive-lifecycle progression, typed payloads, reusable functional-agent placement, capability-backed actions, rendering states, auth, traces, and tests.
---

# App Description Surface Modeling

Use this skill to update authoritative app-description surface definitions and workstream surface bindings for agent workstream apps. A surface is a typed, backend-backed user interaction unit, not a static mockup.

## Lifecycle classification

- Phase: interview.
- Kind: focused current-intent capture/editor.
- Family: app-description.
- Living-graph contract: surface modeling maintains human execution-harness nodes in the app-description current-intent graph and must preserve surface-to-worker, actor-adapter, governed-tool, capability, trace, test, and realization links.
- Build/compile handoff: when a surface contract becomes implementation, planning, code, tests, or validation work, hand off through `../docs/app-description-to-code-compile-contract.md` so browser realization compiles the existing graph instead of inventing app meaning.

## Required reading

- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/app-development-lifecycle.md` when surface work crosses interview, build/compile, or runtime-validation readiness
- `../docs/app-worker-tool-model.md` and `../docs/app-description-component-graph.md`
- `../docs/app-description-skill-output-contracts.md`
- `../docs/structured-surface-contracts.md`
- `../docs/workstream-surface-intent-routing.md`
- `../docs/workstream-contract.md`
- `../docs/workstream-attention-contracts.md`
- `../docs/workstream-ui-reference-architecture.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/web-ui-style-guide.md`
- `../docs/web-ui-component-catalog.md`
- `../docs/web-ui-quality-checklist.md`
- current `app-description/global/surfaces/**`, `global/tools/**`, `global/traces/**`, domain capability files, and workstream surface/access/tool/trace/test/realization bindings

## Surface contract fields

For each surface, define:

- whether this is a reusable global surface pattern or a workstream-specific surface binding
- the human worker(s) the surface serves, the surface's role as a human-worker execution harness, and any adjacent AI/system workers that produce evidence, proposals, attention items, or result payloads for it
- for durable collection objects, the surface's role in the canonical collection-object progression from `../docs/structured-surface-contracts.md`: domain list/search, lifecycle-aware show/inspection, create, edit, destructive lifecycle confirmation, or domain-specific single-action surface
- stable domain-semantic id, type, version, owning workstream definition, exactly one owning functional agent, reusable functional agents/workstreams if any, and purpose
- actor roles/scopes and selected `AuthContext` requirements
- user goal, primary decision/action, and user-facing success outcome in normal SaaS product language
- payload schema summary with frontend-safe fields only, split by visibility: default user-visible fields, on-demand drilldown fields, admin/support/auditor-only fields, and internal-only implementation metadata
- loading, empty, ready, submitting, success, validation-error, forbidden, conflict, stale/reconnect, partial-data, and failure states as applicable
- visible and hidden/denied actions
- stable `actionId`, browser action/tool id, shared governed backend capability/tool id, actor adapter/exposure channel, idempotency/correlation behavior, result surface, confirmation or approval gate, and trace source for each consequential action; model the human `surface_action` adapter separately from matching `human_chat_tool_plan`, `agent_tool_call`, workflow, timer, consumer, API, MCP, or internal adapters; keep ids and implementation names out of normal user copy unless the actor is in a support, auditor, admin, or developer-facing diagnostic context
- deterministic surface intent routes for composer-enabled workstreams: prompt examples, target surface, safe editable prefill fields, ambiguity behavior, required capability, no-mutation guarantee, denial/system-message result, and route trace
- target/result surface or typed `system_message`
- trace/audit/work-trace links, redaction rules, and which trace/evidence details are summarized for users versus exposed only through role-gated drilldowns
- accessibility/responsive expectations
- acceptance, regression, security, negative, idempotency, observability, and user-UX usefulness tests

## Modeling rules

- Model SaaS user intent before internal implementation detail. A surface is a human-worker execution harness and reviewable contract, not the product authority, capability, endpoint, or component. A surface contract must distinguish user-visible UX content from implementation/support metadata. Default SaaS users should see task-relevant outcomes, decisions, next actions, status, confidence/risk summaries, and useful explanations; they should not see internal policy ids, raw trace/event ids, governed-tool ids, backend component names, provider/model internals, prompt internals, correlation/idempotency mechanics, or authorization plumbing unless the surface is explicitly for admins, support, auditors, compliance reviewers, or developers.
- For each visible field, action, badge, chart, trace link, or evidence block, ask: “Does this help the current actor decide, act, recover, or understand a business outcome?” If not, omit it, translate it into user language, or move it to a role-gated drilldown/audit/support surface.
- Use progressive disclosure for diagnostics and governance details: default view = user-safe summary; drilldown = role-authorized evidence/audit detail; internal-only = stored implementation metadata never rendered to ordinary users.
- Translate internal states and denials into SaaS product language. Prefer “You don’t have permission to archive this customer. Ask an organization admin for access.” over “Capability `customer.archive.v2` denied by `PolicyClause#TENANT_ROLE_014`.”
- Audit/trace surfaces still need detailed evidence, but they must also split user-readable trace summaries from privileged raw audit detail and apply role-based redaction by default.
- Model dashboard attention and surface graph edges before conventional route/page details.
- For any durable collection of domain things such as users, customers, orders, policies, agents, invitations, or governed documents, use the canonical collection-object surface progression in `../docs/structured-surface-contracts.md` by default. Name surfaces domain-semantically, not generically: use names such as `customerDirectory`, `customerProfile`, `newCustomerIntake`, `customerEditForm`, or `customerArchiveConfirmation` instead of `thing.list/show/create/edit/delete`.
- Treat collection-object discovery as delegated progression: list/search surfaces discover and select; every listed row/card is clickable and keyboard-operable; selection opens the object's lifecycle-appropriate show/inspection surface; show/inspection surfaces expose task entry points; separate create, edit, destructive lifecycle, and domain-specific single-action surfaces perform consequential mutations.
- For each create/edit/task surface in a composer-enabled workstream, define whether high-confidence natural-language requests may open the surface with prefilled fields. Prefill is advisory visible state only: the deterministic surface router must not submit commands, approve decisions, send invitations/emails, activate behavior, revoke/archive records, or otherwise mutate state. This router rule is not a global ban on separately modeled confirmed human chat tool plans; those belong in the workstream tool catalog and capability map, require explicit confirmation bound to the proposed plan, and return result or partial-failure surfaces with traces.
- Keep each surface single-purpose. Do not combine list, show, create, edit, and destructive lifecycle behavior into one broad surface. Use separate edit and delete/archive/revoke/deactivate/cancel confirmation surfaces, and prefer domain lifecycle language over physical delete unless true deletion is required.
- Treat every workstream dashboard as an action router, not a report: ready dashboard content must be ordered top-to-bottom as **things that need my attention** followed by **things I can do**.
- Aside from section labels, control labels, and minimal explanatory microcopy, model only actionable/clickable dashboard indicators. Cards, rows, counters, badges, chart segments, task/progress panels, shortcuts, icons, and buttons must declare the surface/action they open, the governed capability/browser-tool if protected, and the request/result surfaces they append. Passive FYI metrics, inert charts, decorative status tiles, and content that does not answer “what can I do with this?” belong in report/detail/analytics surfaces or must become governed drilldowns.
- A dense labeled counter or metric tile is itself the interaction target, including valid `0` states that open an empty queue, explanation, setup, history, or validation surface. Use `none` with a recorded reason only for explicit non-actionable exceptions; ready dashboards should normally omit forbidden targets instead of displaying disabled work objects.
- When creating or revising a workstream dashboard, command center, queue, decision, audit, workflow/progress, form, table, detail, or other browser-rendered surface contract, include an explicit surface-description sufficiency review question for human app developers: "Is this surface definition sufficiently unambiguous that a developer or generator can implement and review the surface without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics?" If the answer is no, make another pass on the surface description before creating surface implementation tasks.
- Every browser-rendered surface must link to the selected web UI style guide, named-theme contract, and component-catalog anatomy before implementation. Surface contracts own purpose/authority/capability semantics; frontend realization owns visual/style mechanics and must not invent application meaning.
- If the app lacks a selected UI style/named-theme contract, route to `app-description-ui` or queue a blocking `category: ui` question before creating surface implementation tasks.
- Every consequential read/query/mutation/action maps to a backend capability and governed tool id, is authorized server-side, and records whether the surface action is the human `surface_action`/browser-tool adapter versus a separate `human_chat_tool_plan`, AI `agent_tool_call`, API, workflow, timer, consumer, MCP, or internal adapter. Surface availability never grants AI or system-worker authority; those adapters must be separately modeled in the workstream tool/capability graph.
- Denials, approval-required results, validation failures, stale/reconnect, no-op, and background-work states are explicit structured outcomes.
- Do not expose secrets, raw provider data, hidden roles, cross-tenant/customer identifiers, privileged evidence, prompt internals, backend component names, governed-tool ids, policy implementation ids, raw event ids, or correlation/idempotency mechanics in normal browser payloads. If a support/audit/admin/developer surface needs a technical identifier, label it as diagnostic metadata, gate it by role/scope, and keep it visually subordinate to user-meaningful content.
- Do not describe fixture/static/mock surfaces as normal generated-app runtime.
- If a surface needs missing capability, governed-tool, action identity, authority, or result-surface semantics, ask or queue a blocking question instead of inventing stable implementation ids. Template examples may propose candidate ids only when clearly marked provisional.
- Process/template baselines may list deferred typed surfaces and first-slice fallbacks; app-level implementation cleanup must replace consequential deferred surfaces before claiming capability readiness.

## Output contract

Update or propose updates to the app-description with:

- new/changed surface contracts
- worker/harness/actor-adapter mappings for each surface, including which human worker uses the surface and which AI or system workers may produce evidence, proposals, attention, or result payloads for it
- surface action-to-capability mappings that reuse shared governed tool ids instead of redefining operations already exposed through human chat tool plans, agent tool calls, APIs, workflows, timers, consumers, MCP, or internal adapters
- surface intent routing catalog entries for composer-enabled workstreams, including target surfaces, prefill mapping, ambiguity handling, no-mutation guarantees, and tests
- for each durable collection object in scope, the domain-semantic list/show/create/edit/destructive-lifecycle surface progression, including lifecycle-state-dependent show/task routing and any explicit override from the canonical pattern
- a surface-description sufficiency review result for each new or substantially changed browser-rendered surface, including whether another description pass is required before code generation and whether the default view avoids exposing internal implementation details that do not help the target SaaS user
- affected capability/security/test/observability links
- traceability map or graph-link changes
- assumptions and open questions
- generation impact: localized UI/API change, backend capability change, broader workstream redesign, UI style/catalog realization prerequisite, or separate app-level surface implementation cleanup

If a surface requires data, worker responsibility, authority, governed-tool identity, actor-adapter behavior, style/catalog binding, or tests not yet described, queue or ask the blocking question instead of inventing it. Do not treat the generated UI as the only review artifact; the app developer should also be able to review and refine the surface description itself, then use that refined description to revise or repair surface-related code.
