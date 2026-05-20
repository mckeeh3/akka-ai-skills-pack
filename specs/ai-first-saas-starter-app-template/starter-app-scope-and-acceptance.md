# Starter App Scope and Acceptance Criteria

## Purpose

This document defines the canonical AI-first SaaS starter app that will be shipped as an explicit scaffoldable template with the skills pack. It is the scope contract for subsequent implementation, packaging, and cleanup tasks under `specs/ai-first-saas-starter-app-template/`.

## Scope label

- **Scope:** `full core`
- **Audience:** downstream users starting a new or empty Akka + React/Vite/TypeScript AI-first SaaS application.
- **Role in the pack:** a runnable starter scaffold that demonstrates the mandatory secure SaaS foundation, governed runtime agent foundation, workstream UI, capability-first backend, and extension seams.

Any generated or scaffolded variant that omits User Admin, Agent Admin, invitation onboarding, governed prompts/skills/manifests/tool boundaries, Audit/Trace, Governance/Policy, or the workstream UI must be labeled as a narrower scope such as `Module 1-only / not full core`.

## Installation and scaffold stance

The skills pack must continue to support two distinct installation modes:

1. **Skills-only install**
   - Default for global installs and existing projects.
   - Installs guidance, skills, docs, and pack assets without materializing starter app code into the target project.
   - Must not overwrite or mix into an existing application unless the user explicitly chooses a scaffold/init flow.

2. **Explicit starter scaffold/init mode**
   - Intended for empty or new target projects.
   - Copies the canonical starter app template into the target workspace only after an explicit user action or accepted init prompt.
   - Produces a runnable Akka backend plus React/Vite/TypeScript frontend foundation that users extend through app-description, capability, UI, agent, and Akka component tasks.

The starter is a product foundation and reference implementation, not a replacement for the skills-only pack.

## In-scope starter app capabilities

### Secure SaaS foundation

The starter must include:

- WorkOS/AuthKit browser authentication seam and JWT request-context validation.
- Akka-owned local authorization state: `Account`, `UserProfile`, `UserSettings`, `Tenant`, `Customer`, `Membership`, `Role`, `Permission`/`Capability`, selected `AuthContext`, support-access membership, and subscription/billing-safe SaaS Owner boundary.
- `/api/me` returning browser-safe account, profile, settings, active memberships, selected/default context, available contexts, and capability hints.
- Complete email-invite onboarding with `Invitation`, resend, revoke/cancel, expiry, acceptance, delivery status/attempts, idempotency, audit, Resend production email boundary, and local/dev/test captured outbox.
- User administration for directory/search, invitation management, membership lifecycle, role/capability assignment, account disable/reactivate, support access, access review, last-admin protection, and scoped admin audit.
- Backend authorization for every protected endpoint, component command, view query, stream, workflow action, consumer side effect, timer action, internal agent operation, and agent tool.
- Tenant/customer isolation, redaction, safe denial shapes, correlation ids, and durable audit/trace facts.

### Governed runtime agent foundation

The starter must include tenant-scoped governed behavior records and runtime contracts:

- `AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, `ToolPermissionBoundary`, `ModelConfigurationRef` or equivalent safe model alias records.
- First-install or tenant-bootstrap seed import of implementation-developed default agent definitions, prompts, skills, manifests, model refs, and tool boundaries.
- Idempotent seed behavior with provenance, checksums, validation, audit, and customization-preserving upgrades.
- Deterministic prompt assembly that resolves active `AgentDefinition`, active prompt version, compact skill manifest, model ref, and tool boundary without exposing full skill text or provider secrets.
- Authorized `readSkill(skillId)` that checks tenant, active agent, manifest assignment, skill version/status, mode, `AuthContext`, and tool boundary before returning skill text.
- `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace` for allowed and denied runtime/test/replay activity.
- AI-assisted admin offload responsibilities such as access review, admin risk scoring, invitation drafting, role recommendation, support-access review, admin audit summaries, and policy proposal drafting where enabled. These responsibilities may recommend or draft but must not autonomously expand high-risk authority.

### Agent workstream UI

The starter must include the canonical authenticated workstream shell:

- sign-in/access recovery states;
- role-authorized functional-agent rail;
- continuous main workstream panel;
- persistent composer;
- selected tenant/customer context and authority indicators;
- structured surfaces with typed payloads, actions, loading/empty/error/forbidden/stale/success states, accessibility, and responsive behavior.

Required foundation functional agents:

1. **Access/Profile** — identity/context card, profile/settings form, membership/context selector, capability summary, no-access/disabled/forbidden recovery.
2. **User Admin** — user directory, invitations, member detail, role/capability assignment, disabled/revoked status, access review, support access, admin audit timeline.
3. **Agent Admin** — agent catalog/detail, lifecycle cards, prompt editor/review/diff/history/test, skill catalog/manifest/test, tool-boundary editor/review, safe model config ref, deterministic test console.
4. **Audit/Trace** — audit landing/search/list/detail, correlated work timeline, redacted details, optional stream/export when implemented.
5. **Governance/Policy** — policy/rubric overview, evaluation findings, improvement proposal decision cards, approval/rejection workflow status, activation/rollback/outcome review where included.

### Capability-first backend inventory

The starter must provide implementation seams for the full-core capability families described by the realization map:

- `core.access.me`
- `core.profile.update`
- `core.invitations.manage`
- `core.memberships.manage`
- `core.access_review.commit`
- `agent.definitions.manage`
- `agent.prompts.govern`
- `agent.skills.govern`
- `agent.tool_boundaries.manage`
- `agent.runtime.test`
- `audit.trace.search`
- `governance.proposals.review`
- `frontend.workstream.shell`

Each capability must preserve actors/callers, selected `AuthContext`, tenant/customer scope, typed inputs/outputs, validation, side effects, idempotency, policy/approval rules, audit/trace obligations, selected exposure surfaces, and tests.

### Expected Akka and frontend substrates

The starter may evolve module by module, but full-core completion must include these substrate families where called for by the capability shape:

- Key Value Entities for current-state profile, settings, model refs, and non-history-critical configuration.
- Event Sourced Entities for invitation lifecycle, audit-grade admin/governance records, agent definitions, prompt/skill documents, tool boundaries, proposals, decisions, and traces where history matters.
- Workflows for invitation orchestration, review/approval, seed import or activation, governance proposal, rollback, and other long-running or approval-gated flows.
- Views for user directory, memberships, invitations, admin audit, access review, agent catalog, prompt/skill history, trace search, and governance queues.
- Consumers for email/outbox delivery, trace normalization/enrichment, version snapshot creation, and other asynchronous reactions.
- Timed actions for invitation expiry/reminders, support-access expiry, and scheduled governance or trace-retention behavior where included.
- Akka Agents for bounded admin assistance, behavior editing, evaluation, trace summarization, and deterministic test-console flows.
- HTTP APIs and optional SSE/WebSocket routes for browser calls, workstream updates, and trace streams.
- React/Vite/TypeScript frontend with typed clients, structured surface renderers, forms, state/realtime handling, accessibility, responsive layout, tests, and production build/static asset serving.

## Explicit non-goals

The starter app must not:

- materialize automatically during global installs or default skills-only installs;
- overwrite existing target-project code without explicit user acceptance;
- become an app-specific CRM, DCA, procurement, finance, or other domain example;
- treat old DCA/static seed assets as canonical starter paths without inventory and migration decisions;
- defer security, web UI, user administration, agent governance, audit/trace, or tenant-isolation tests while still claiming `full core`;
- authorize by frontend navigation, prompt text, email address alone, hidden fields, or tool descriptions;
- store or expose WorkOS, Resend, model-provider, raw JWT/session, raw invitation token, token-hash, or backend-only policy secrets in browser DTOs, traces, fixtures, or built assets;
- make SaaS Owner a tenant data super-admin without explicit support-access authority;
- let prompt/skill content grant backend authority, tool permissions, data scope, approvals, or roles;
- require every downstream application to use every example agent behavior beyond the mandatory foundation; downstream apps extend the scaffold through accepted requirements and capability contracts.

## Acceptance criteria

### Backend foundation acceptance

- `/api/me` supports active, unauthenticated, disabled, no-access, forbidden, and invalid selected-context cases with safe browser DTOs and denial audit where applicable.
- Context selection persists only for authorized active memberships and denies cross-tenant/customer contexts without leakage.
- Invitation create/resend/revoke/expire/accept flows are idempotent, auditable, tenant-scoped, and backed by captured local/dev/test outbox plus Resend production boundary.
- User/admin APIs cover directory/search, membership lifecycle, role/capability changes, account status changes, support-access lifecycle, access review, and last-admin protection.
- Every protected route, command, query, stream, workflow action, consumer, timer, tool, and internal-agent operation resolves and enforces account, selected `AuthContext`, tenant/customer scope, status, role/capability, and correlation id server-side.

### Agent governance acceptance

- Seed import creates default governed agent, prompt, skill, manifest, model ref, and tool-boundary records exactly once per tenant/bootstrap context and preserves tenant customizations on upgrade.
- AgentDefinition, prompt, skill, manifest, tool-boundary, model ref, and governance lifecycle APIs enforce tenant scope, capability checks, readiness rules, immutable version snapshots, diff/history, and audit.
- Prompt assembly emits `PromptAssemblyTrace` without leaking prompt bodies into trace metadata.
- `readSkill(skillId)` allows only assigned active same-tenant skill reads in permitted mode and emits `SkillLoadTrace` for both allow and deny decisions.
- Tool-boundary enforcement denies unknown tools, ungranted tools, cross-scope inputs, side effects under read-only grants, and approval-required side effects before mutation.
- Deterministic test-console/runtime tests invoke governed agents without production side effects and emit `AgentWorkTrace`.

### Frontend/workstream acceptance

- The authenticated shell renders the functional-agent rail, main workstream, persistent composer, selected context/authority indicators, and default foundation surfaces.
- Functional-agent rail visibility and surface action availability are derived from backend capabilities but backend authorization remains authoritative.
- Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy surfaces cover loading, empty, populated, validation, success, server-error, forbidden, stale/reconnect, keyboard/focus, and responsive states.
- Typed API clients and structured-surface renderers use browser-safe DTOs only.
- Production-like frontend build and static asset serving tests prove route/API references, asset availability, style-guide inclusion, and absence of frontend secrets.

### Audit, trace, and governance acceptance

- AdminAuditEvent, AuditTraceEvent, WorkTrace, PromptAssemblyTrace, SkillLoadTrace, and AgentWorkTrace capture allowed, denied, no-op, idempotent, failed, approval-required, sensitive-read, and governance lifecycle events as applicable.
- Trace search/detail/timeline queries are tenant-scoped, redacted by default, stable ordered, and audited for denied or sensitive access.
- Governance proposals preserve evidence, risk, confidence, impact, alternatives, target artifact/version/checksum, decision history, activation/rollback path, and outcome notes.
- Authority expansion for tools, data, tenant/customer scope, autonomy, approvals, security, billing, role, membership, email sending, or external side effects requires backend-enforced human approval unless a narrow autonomous policy is explicitly documented and tested.

### Security acceptance

- Cross-tenant/customer access to users, invitations, memberships, roles, agents, prompts, skills, manifests, traces, proposals, outcomes, APIs, views, streams, tools, workflows, timers, and UI actions is denied without existence leakage.
- Disabled accounts/memberships and callers missing capabilities fail closed across all protected backend and frontend action paths.
- Client-supplied tenant/customer ids are never trusted as authority and mismatches are denied and audited.
- Redaction prevents frontend bundles, static assets, API payloads, fixtures, traces, and exports from containing provider secrets, auth tokens, raw invite tokens, token hashes, backend-only policy internals, or private provider ids.
- A security-review checklist blocks full-core readiness for missing authorization placement, tenant isolation, redaction, provider-secret boundaries, frontend authorization assumptions, approval bypasses, or audit/trace gaps.

### Packaging and extension acceptance

- The canonical template source path and installed-pack resource path are documented before implementation begins.
- Skills-only install remains safe for global and existing-project use.
- Explicit scaffold/init mode can copy the starter into an empty project and leave the user with a runnable backend/frontend foundation.
- Package/base-package policy is recorded and applied consistently to group id, Java packages, imports, tests, frontend API paths, and generated documentation.
- Extension workflow is documented as: scaffold starter → update app-description/specs → model governed capabilities → extend Akka components/UI/tests → run acceptance/security checks.
- Legacy examples are inventoried before reuse, migration, quarantine, archival, or deletion from canonical paths.

### Required test families

Full-core completion requires automated coverage across:

1. domain/component unit tests for foundation, governed agent, audit/trace, and governance objects;
2. workflow tests for invitation, review/approval, activation, rollback, seed import, and evaluation/proposal flows;
3. consumer and timed-action tests for email/outbox, trace enrichment/projections, version snapshots, invitation expiry/reminders, support-access expiry, and scheduled governance behavior;
4. view tests for scoped, paginated, redacted admin, invitation, membership, agent, prompt, skill, manifest, trace, and governance projections;
5. HTTP/stream endpoint integration tests for authenticated, unauthenticated, forbidden, disabled, invalid-context, validation, conflict/idempotent, and cross-tenant cases;
6. governed agent runtime tests with deterministic model providers for runtime resolution, prompt assembly, `readSkill`, tool-boundary enforcement, behavior-edit proposal flow, evaluator flow, traces, and pre-model denials;
7. frontend tests/build for typed clients, forms, state transitions, workstream shell, structured surface rendering, realtime handling, accessibility, responsive behavior, static asset serving, and secret boundary;
8. security-review checks for backend authorization coverage, tenant/customer isolation, redaction, secret-never-store rules, provider-secret boundaries, frontend authorization reliance, approval bypasses, and audit/trace completeness.

## Implementation handoff rules

- Subsequent starter tasks must copy relevant capability ids, AuthContext/scope rules, exposure surfaces, Akka substrate choices, and tests from this document and the full-core realization map instead of re-deciding full-core scope.
- Narrower implementation slices are allowed as intermediate milestones, but they must not be described as full core until all required foundation functional agents, governed runtime records, UI surfaces, authorization, audit/trace, packaging, and tests are complete.
- Missing foundation semantics should create explicit spec gaps or pending tasks, not silent assumptions in code generation.
