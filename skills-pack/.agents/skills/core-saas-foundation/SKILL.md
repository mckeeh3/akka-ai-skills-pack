---
name: core-saas-foundation
description: Apply the mandatory secure SaaS foundation for every new AI-first SaaS app, PRD, spec, backlog, app-description, decomposition, and generation flow before app-specific domain work.
---

# Core SaaS Foundation

Use this skill for every new project, app, PRD, spec, backlog, app-description bootstrap, solution decomposition, and generation flow handled by this pack unless the user explicitly asks for repository-maintenance-only work or non-SaaS reference material. In the intent compiler model, this skill seeds and protects the global and foundation-domain current-intent baseline that app-specific work extends.

This skill supplies the secure SaaS baseline. In this repository that baseline is the **SaaS Foundation App**: a runnable out-of-the-box domain with five workstreams that users clone/fork and extend.

## Required reading

Read these first when using this skill:

- target project path: `AGENTS.md`
- `../README.md`
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/minimum-ai-first-saas-app.md`
- `../docs/full-core-foundation-readiness.md`
- `../docs/core-ai-first-saas-foundation.md`
- `../docs/core-saas-identity-tenancy-admin.md`
- `../docs/core-saas-owner-tenant-billing.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/structured-surface-contracts.md`

Then load only the focused downstream skills needed for the selected path, especially `agent-workstream-apps`, `capability-first-backend`, auth/admin skills, governed-agent skills, and the Akka component skills selected by decomposition.

## SaaS Foundation App framing

The SaaS Foundation App is not a demo, throwaway starter, or generic chatbot. It is the built-in foundation domain that comes fully functional with the repository. It can be modified like any other domain, and downstream product work extends it with business-specific domains, workstreams, surfaces, agents, governed capabilities, Akka components, frontend extensions, app-description artifacts, specs, docs, and tests.

Built-in foundation workstreams:

| Workstream | Responsibility |
|---|---|
| My Account | Current account, selected AuthContext, profile/settings, notifications/attention, sign-out, and safe self-service. |
| User Admin | Users, memberships, roles, invitations, access review, support access, last-admin protection, and admin audit. |
| Agent Admin | Governed agent definitions, prompts, skills, references, manifests, model refs, tool boundaries, behavior editing, test consoles, and lifecycle. |
| Audit/Trace | Searchable/explainable audit and work traces for identity, authz, data access, tool use, decisions, agents, denials, and investigations. |
| Governance/Policy | Policy/permission concepts, proposals, simulations, approvals, activation/rollback, improvement governance, and outcome evidence. |

Support Access and Billing may be separate foundation workstreams or surfaces when relevant to the product scope.

## Mandatory foundation concepts

Every generated SaaS app and every SaaS Foundation App extension must preserve or explicitly address:

- WorkOS/AuthKit browser authentication and WorkOS JWT validation.
- Akka-owned local authorization state: Account, UserProfile, UserSettings, Membership, Role, Permission/Capability, selected AuthContext, Tenant, Customer, support access, and billing-safe platform records.
- `/api/me` with browser-safe account, profile, settings, memberships, selected/default context, roles/capabilities, and context switching.
- Invitation lifecycle and email-invite onboarding using Resend for production email and an explicit captured outbox adapter for local/dev/test.
- Backend authorization for every protected route, component command, view query, stream, agent tool, workflow action, consumer side effect, and timer action.
- Tenant/customer scoped commands and queries that mechanically reject cross-scope access.
- AdminAuditEvent and work traces for identity, invitation/email, membership/role, support-access, billing, data access, approval, policy, and consequential AI/tool activity.
- Governed runtime agent foundation: AgentDefinition, PromptDocument/PromptVersion, SkillDocument/SkillVersion, ReferenceDocument/ReferenceVersion, AgentSkillManifest, AgentReferenceManifest, ToolPermissionBoundary, prompt/skill/reference load traces, and AgentWorkTrace.
- Bounded AI-assisted admin offload with decision cards for risky changes; agents may draft/recommend but must not autonomously expand authority.
- Workstream shell surfaces and actions mapped to governed backend capabilities.
- Security tests for tenant isolation, forbidden access, disabled users, role/scope denial, `/api/me`, invitation lifecycle, admin list/search, membership lifecycle, last-admin protection, support access, agent-governance boundaries, audit/trace, surface action authorization, markdown sanitization where used, and frontend secret boundaries.

Prompt text, skill text, hidden UI state, and route names cannot grant authority. Backend authorization and tool/data boundaries remain authoritative.

## Prescriptive implementation architecture

Model foundation work as user-facing workstream verticals before selecting entities, workflows, views, endpoints, or frontend components. Each vertical needs:

1. functional/context-area agent ownership;
2. structured surfaces and surface actions;
3. governed capability and governed-tool contracts;
4. AuthContext, tenant/customer scope, side effects, idempotency, policy/approval, audit/trace, and tests;
5. selected Akka component families from the pack's eleven-component architecture;
6. frontend workstream shell/API/realtime behavior where user-visible.

Do not start from object lists, CRUD screens, or Akka component families alone.

## Route-specific requirements

### App-description paths

Bootstrap and maintain secure SaaS foundation files in the current-intent graph: app/global actors, roles, policies, surfaces, agents, tools, traces, plus foundation domain workstreams, surface contracts, capabilities, behavior, tests, auth/security, observability, and realization files. For SaaS Foundation App work, describe the built-in foundation domain and any modifications in place. For business-specific work, add domain/workstream extensions without replacing foundation semantics.

### Akka solution decomposition

Every solution plan must include a secure SaaS foundation section before app-specific capabilities. It should show affected foundation/business workstreams, structured surfaces, action-to-capability mappings, and then candidate Akka components. Route through `capability-first-backend` before component implementation.

### PRD/spec/backlog planning

Every app PRD or incremental product intent must compile foundation implications into current-intent graph updates and first implementation work that preserves or extends the SaaS Foundation App. Pending tasks should be vertical increments: functional agent, structured surface/action, governed capability, selected Akka substrate, browser/API/realtime work, auth/audit, and tests.

### Generation

Generation must stop or mark the description not-ready when the requested scope lacks required foundation semantics. Do not invent access semantics during code generation; add description/spec gaps instead. Do not create a separate blank baseline when the target is this repository or a downstream fork; extend the SaaS Foundation App.

## Output checklist

Before handing off, verify:

- The target scope is explicit: SaaS Foundation App maintenance/extension, business-domain extension, app-specific feature work, or another named scope.
- Foundation workstreams and structured surfaces are present or intentionally out of scope.
- Capabilities are modeled before Akka component selection.
- WorkOS/AuthKit, Resend/outbox, `/api/me`, invitation/admin lifecycle, backend authorization, tenant/customer isolation, governed-agent runtime, and audit/work traces are preserved for relevant scope.
- User Admin can discover and manage users within authority boundaries when User Admin behavior is in scope.
- Agent Admin, Audit/Trace, and Governance/Policy boundaries are not bypassed by prompts, tools, or frontend state.
- Required negative authorization/security tests are planned or implemented.
- App-specific domain implementation extends the foundation instead of replacing it.
