# AI-First SaaS Seed App Description Example

This directory is the **preferred current generated-SaaS app-description reference** for a secure AI-first SaaS app. It is an authoritative description/reference asset for the skills pack, not the primary runnable starter implementation.

Use the runnable starter template under `../../../templates/ai-first-saas-starter/` when the goal is to scaffold and execute a local Akka Java + React/Vite/TypeScript application. Use this seed app-description when the goal is to understand, review, or maintain the canonical description-layer shape before realization.

Purpose:
- define the canonical description-layer seed shape before implementation
- provide a reusable source asset for the skills pack
- describe secure multi-tenant SaaS foundations shared by generated apps
- describe the agent workstream application model: role-authorized functional agents, durable workstreams, structured surfaces, governed capabilities, and horizontal Akka implementation maps
- anchor the requirements-to-workstream target architecture: broad input is processed through workstreams, attention categories, dashboard contracts, surface actions, governed capabilities/APIs, selected Akka substrate, request-based Agent turns, AutonomousAgent task candidates for durable internal/background work, events/notifications/projections, and audit/work traces before implementation tasks are considered ready
- describe a functional AI-first operating-model baseline: goals, plans, governed runtime agents, managed prompts/skills/manifests/tool boundaries, decisions, policy gates, traces, and outcomes
- map the seed app description to Akka Java components and frontend/backend integration patterns without replacing the runnable starter template

Example root:

```text
docs/examples/ai-first-saas-seed-app-description/app-description/
```

Repository/use distinction:
- in this repository, this is a reference example and future seed-generation source asset for the skills pack
- in a target project, the equivalent `app-description/` tree would be maintained in that project workspace
- for runnable local execution, scaffold from `templates/ai-first-saas-starter/` rather than treating this docs example as an executable project

Primary realization target described by this reference:
- backend: Akka Java SDK
- frontend: React + Vite + TypeScript
- app class: secure multi-tenant AI-first SaaS agent workstream shell with modular functional-agent extension points

Runnable baseline:
- canonical runnable scaffold source: `../../../templates/ai-first-saas-starter/README.md`
- scaffold output owns executable backend/frontend files after placeholders are rendered
- this seed app-description remains a description/reference contract for structure, semantics, style guide, and readiness review

Frontend implementation reference:
- canonical architecture: `../../workstream-ui-reference-architecture.md`
- reusable source modules in this repository: `../../../frontend/src/workstream/**`
- API/realtime seams to bind to real generated backend endpoints: `../../../frontend/src/api/WorkstreamApiClient.ts` and `../../../frontend/src/api/WorkstreamRealtimeClient.ts`
- User Admin contract/test reference vertical: `../../../frontend/src/workstream/fixtures/**` and `../../../frontend/src/workstream-user-admin-vertical.contract.test.mjs`

Fixture-backed frontend paths are reference/test assets only. Generated user-facing runtime must use real or fail-closed local backend contracts and cannot count fixture-only behavior as implemented.

## Minimum-first growth path

For “minimum app”, “starter app”, “basic app”, or chatbot-like generated SaaS requests, use `../../minimum-ai-first-saas-app.md` and the runnable starter template before treating this seed as full-core ready. The first runnable implementation target is the **five core workstream v0 set**: bootstrap-authorized user, selected `AuthContext`, bounded My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy functional agents, durable request/response timelines, `markdown_response`, backend capability boundaries, and audit/work trace substrate.

That first runnable target is intentionally narrower than this full-core seed description. It must record explicit follow-up work in this order:

```text
five core workstream v0 starter
→ fuller User Admin structured capabilities
→ fuller Agent Admin and governed behavior document lifecycle
→ Audit/Trace search and investigation UI
→ Governance/Policy workflows and review surfaces
→ invitations/onboarding, support access, and security completeness
→ app-specific functional agents, surfaces, capabilities, and outcomes
```

Do not use this seed README to justify a generic public chatbot, unauthenticated assistant, page-first CRUD console, or app-specific domain workstream before the User Admin v0 minimum slice and full-core progression gates are visible.

The seed app UI docs below describe functional agents, workstreams, structured surfaces, and deep links. They are not a page-first route/screen template. Older page/screen examples in the repository are legacy or mechanics references only.

For a compact planning example that starts from input/PRD text and walks through workstreams, attention, dashboards, surfaces/actions, capabilities, AutonomousAgent candidates, events/notifications, projections, and traces, read `../requirements-to-workstream-mini-example.md` before using conventional mechanics examples.

Use `../../agent-workstream-design-review-checklist.md` when checking whether another example, spec, or generated output still matches this canonical seed direction.
