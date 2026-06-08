# Secure AI-first SaaS Core Starter Content Review

## Purpose

This review identifies repository content related to the **secure multi-tenant AI-first SaaS core starter**. The root `src/` directory contains this app's generated/runnable code. The repo also contains app-description, specs, docs, and skills-pack content that are specifically related to the starter app.

## High-level finding

This repository intentionally serves two roles:

1. the runnable **Secure AI-first SMB SaaS Core App**; and
2. the source for the Akka AI skills pack that teaches agents how to maintain and extend that app.

The actual starter app runtime lives in root app paths, especially:

- `src/**`
- `frontend/**`
- `app-description/**`
- `specs/**`
- root `docs/**`

The skills-pack content under `skills-pack/**` and the installed mirror under `.agents/skills/**` is guidance/reference material for maintaining or extending the starter, not runtime app source.

## Canonical app/runtime content

### Root app manifest/docs

Key starter identity files:

- `README.md`
  - Defines this as the **Secure AI-first SMB SaaS Core App**.
  - States the five built-in workstreams:
    - My Account
    - User Admin
    - Agent Admin
    - Audit/Trace
    - Governance/Policy
  - Defines root app vs `skills-pack/` boundaries.

- `AGENTS.md`
  - Confirms root app work should improve the runnable core app.
  - Says `skills-pack/**` is for skills maintenance only.
  - Defines extension zones under `business.<domain>` packages.

- `docs/domain-extension-guide.md`
  - Explains how downstream business-specific domains should extend the core starter.

- `docs/java-package-boundaries.md`
  - Defines canonical package ownership:
    - `ai.first.*.foundation`
    - `ai.first.*.coreapp`
    - `ai.first.*.business.<area>`

- `docs/upstream-merge-guide.md`
  - App-fork maintenance guidance.

## Generated/runtime code in `src/`

`src/main/java/ai/first/**` is the runnable core app source.

Current rough shape:

- `src/main/java/ai/first/domain/foundation/**`
  - Identity, tenancy, invitations, audit, governance, notifications, agent governance, workstream events.
- `src/main/java/ai/first/application/foundation/**`
  - Repositories/services/entities/views for reusable SaaS foundation.
- `src/main/java/ai/first/api/foundation/**`
  - Foundation HTTP APIs, especially `/api/me`.
- `src/main/java/ai/first/domain/coreapp/**`
  - Built-in five-workstream domain objects.
- `src/main/java/ai/first/application/coreapp/**`
  - Workstream services and AI-assisted admin/offload components.
- `src/main/java/ai/first/api/coreapp/**`
  - Admin/workstream/frontend APIs.

Important runtime files:

- `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`
  - JWT-protected `/api/me`.
  - Resolves WorkOS identity and selected context.

- `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`
  - `/api/workstream`.
  - Bootstrap, functional agents, items, surfaces, actions, shell requests, messages, invitation accept, bounded SSE events.

- `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`
  - `/api/admin`.
  - User Admin dashboards, users, invitations, audit, role/support/admin related APIs.

- `src/main/java/ai/first/application/foundation/identity/StarterServiceSetup.java`
  - Akka startup bootstrap for the SaaS foundation.

- `src/main/resources/agent-behavior-seeds/**`
  - Starter governed agent/prompt/skill/reference seed material.

- `src/main/resources/static-resources/**`
  - Built frontend output. Do not hand-edit.

Test coverage is substantial under `src/test/java/ai/first/**`, including identity, invitations, managed agents, workstream, frontend/API integration, and full-core readiness support tests.

## Frontend core starter content

`frontend/**` is the source for the React/Vite workstream UI.

Important paths:

- `frontend/src/workstream/**`
  - Canonical workstream shell, rail, composer, stream, structured surfaces, realtime hooks, visual-session state.

- `frontend/src/api/**`
  - Typed frontend API clients and DTOs.

- `frontend/src/screens/**`
  - Older/legacy page-style surfaces. Some app-description files call these reference/legacy rather than primary architecture.

- `frontend/src/*contract.test.mjs`
  - Contract tests for shell, workstreams, actions, user admin, agent admin, audit/trace, governance, auth runtime boundary, design system, and visual sessions.

## Authoritative app description

`app-description/**` is specifically related to the secure multi-tenant AI-first SaaS core starter.

Key files:

- `app-description/README.md`
  - Identifies this directory as the authoritative current-intent graph.
  - Distinguishes active current intent from migration/historical material.

- `app-description/app.md`
  - Identifies the secure multi-tenant AI-first SaaS core starter objective.
  - Names the five built-in workstreams.
  - Defines operating model, foundation references, tenant/security posture, validation posture, and non-goals.

- `app-description/global/**`
  - Captures foundation actors, roles, policies, surfaces, functional agents, governed tools, and trace patterns for this starter.

- `app-description/domains/core-starter/capabilities/**`
  - Captures account/profile, user/access administration, managed-agent governance, audit/trace investigation, and governance/policy lifecycle capabilities.

- `app-description/domains/core-starter/workstreams/**`
  - Captures My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy workstream access, behavior, surfaces, agents, tools, policies, traces, tests, and realization mappings.

- `app-description/domains/core-starter/realization/traceability.md`
  - Maps current-intent areas to backend, frontend, API, test, provider, and readiness evidence/gaps.

This current-intent graph is the clearest starter app source-of-truth area.

## Specs and planning content

Active starter-related specs include:

- `specs/full-core-saas-readiness/**`
  - Current readiness mini-project.
  - Runtime smoke evidence.
  - Resend/model/WorkOS provider smoke docs.
  - Auth boundary, managed agent, invitation, user admin, audit/governance validations.

- `specs/web-ui-design/**`
  - AI-first SaaS web UI design and starter frontend slices.

- `specs/workstream-visual-sessions/**`
  - Visual-session workstream planning/remediation.

Potentially stale/problematic active specs:

- `specs/workstream-visual-sessions/**` still references nonexistent `templates/ai-first-saas-starter/**`.
- `specs/java-source-cleanup/**` appears stale: it references old template/source layouts and examples that no longer match current root app structure.
- Many useful historical starter initiatives are under `specs/archive/**`; these are context/history, not active source of truth.

## Skills-pack content specifically related to this app

Under `skills-pack/**`, the starter-relevant guidance is extensive.

Most important docs:

- `skills-pack/docs/generated-saas-canonical-doctrine.md`
  - States root is the runnable secure SaaS Foundation App.
  - Generated apps use `ai.first`, WorkOS/AuthKit, `/api/me`, tenant/customer scoping, roles, invitations, Resend, audit, governed runtime agents, workstream UI.

- `skills-pack/docs/full-core-foundation-readiness.md`
  - Defines built-in SaaS Foundation App scope and extension readiness.

- `skills-pack/docs/core-ai-first-saas-foundation.md`
  - Defines foundation vocabulary, three-level operating model, SaaS Owner/Tenant/Customer boundaries, AI-first expectations.

- `skills-pack/docs/core-saas-identity-tenancy-admin.md`
- `skills-pack/docs/security-workos-auth-and-admin.md`
- `skills-pack/docs/security-review-checklist.md`
- `skills-pack/docs/workstream-ui-reference-architecture.md`
- `skills-pack/docs/web-ui-style-guide.md`
- `skills-pack/docs/workstream-contract.md`
- `skills-pack/docs/workstream-attention-contracts.md`

Most relevant skills:

- `skills-pack/skills/core-saas-foundation/SKILL.md`
- `skills-pack/skills/ai-first-saas/SKILL.md`
- `skills-pack/skills/agent-workstream-apps/SKILL.md`
- `skills-pack/skills/akka-basic-user-admin/SKILL.md`
- `skills-pack/skills/akka-workos-user-auth/SKILL.md`
- `skills-pack/skills/akka-saas-invitation-onboarding/SKILL.md`
- `skills-pack/skills/akka-agents/SKILL.md`
- `skills-pack/skills/akka-agent-behavior-profiles/SKILL.md`
- `skills-pack/skills/akka-agent-prompt-governance/SKILL.md`
- `skills-pack/skills/akka-agent-skill-governance/SKILL.md`
- `skills-pack/skills/akka-agent-tool-boundaries/SKILL.md`
- `skills-pack/skills/akka-web-ui-apps/SKILL.md`

Examples/templates:

- `skills-pack/docs/examples/ai-first-saas-core-app-domain/**`
  - Domain PRD/reference content for the foundation app.

- `skills-pack/templates/ai-first-saas-core-app/**`
  - App-description template fragments.

- `skills-pack/examples/akka-components/src/main/java/ai/first/**`
  - Reference Akka component examples for the core app patterns.

`.agents/skills/**` is an installed/mirrored copy of the skills-pack and should be treated as harness support content, not canonical editable app source.

## Notable cleanup/alignment observations

1. The current architecture is now **root-app first**, not “generate a separate starter template”.
2. Some active specs still mention the old `templates/ai-first-saas-starter/**` path, which does not exist.
3. `specs/java-source-cleanup/src-main-java-inventory.md` appears materially stale relative to current `src/main/java/ai/first/**`.
4. Root `src/**`, `frontend/**`, and `app-description/**` are the canonical starter app. `skills-pack/**` teaches or references it.
5. `src/main/resources/static-resources/**` is generated frontend build output and should not be edited manually.
