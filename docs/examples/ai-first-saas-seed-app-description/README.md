# AI-First SaaS Seed App Description Example

This directory is the **preferred current generated-SaaS app-description reference** for a runnable **Akka Java + React/Vite/TypeScript AI-first SaaS seed app**.

Purpose:
- define the canonical seed app before implementation
- provide a reusable source asset for the skills pack
- describe secure multi-tenant SaaS foundations shared by generated apps
- describe the agent workstream application model: role-authorized functional agents, durable workstreams, structured surfaces, governed capabilities, and horizontal Akka implementation maps
- describe a functional AI-first operating-model baseline: goals, plans, governed runtime agents, managed prompts/skills/manifests/tool boundaries, decisions, policy gates, traces, and outcomes
- map the seed app to Akka Java components and frontend/backend integration patterns

Example root:

```text
docs/examples/ai-first-saas-seed-app-description/app-description/
```

Repository/use distinction:
- in this repository, this is a reference example and future seed-generation source asset for the skills pack
- in a target project, the equivalent `app-description/` tree would be maintained in that project workspace

Primary implementation target:
- backend: Akka Java SDK
- frontend: React + Vite + TypeScript
- app class: secure multi-tenant AI-first SaaS agent workstream shell with modular functional-agent extension points

Frontend implementation reference:
- canonical architecture: `../../workstream-ui-reference-architecture.md`
- reusable source modules in this repository: `../../../frontend/src/workstream/**`
- fixture API/realtime seams: `../../../frontend/src/api/WorkstreamApiClient.ts` and `../../../frontend/src/api/WorkstreamRealtimeClient.ts`
- User Admin reference vertical: `../../../frontend/src/workstream/fixtures/**` and `../../../frontend/src/workstream-user-admin-vertical.contract.test.mjs`

## Minimum-first growth path

For “minimum app”, “starter app”, “basic app”, or chatbot-like generated SaaS requests, use `../../minimum-ai-first-saas-app.md` before treating this seed as full-core ready. The first implementation slice is **User Admin workstream v0**: bootstrap-authorized user, selected `AuthContext`, bounded `UserAdminAgent`, durable request/response timeline, `markdown_response`, backend capability boundary, and audit/work trace substrate.

That first slice is intentionally narrower than this full-core seed. It must record explicit follow-up work in this order:

```text
User Admin workstream v0
→ fuller User Admin structured capabilities
→ Agent Admin and governed behavior documents
→ Audit/Trace search and investigation UI
→ invitations/onboarding, support access, and security completeness
→ app-specific functional agents, surfaces, capabilities, and outcomes
```

Do not use this seed README to justify a generic public chatbot, unauthenticated assistant, page-first CRUD console, or app-specific domain workstream before the User Admin v0 minimum slice and full-core progression gates are visible.

The seed app UI docs below describe functional agents, workstreams, structured surfaces, and deep links. They are not a page-first route/screen template. Older page/screen examples in the repository are legacy or mechanics references only.

Use `../../agent-workstream-design-review-checklist.md` when checking whether another example, spec, or generated output still matches this canonical seed direction.
