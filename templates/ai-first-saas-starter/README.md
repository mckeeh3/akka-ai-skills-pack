# AI-First SaaS Starter Template

This template is the isolated scaffold source for a secure AI-first SaaS application generated with the skills pack.

It is the canonical runnable implementation baseline after placeholders are rendered by the scaffold command. Before rendering, it is template source rather than a directly runnable project.

Reference distinction:
- `docs/examples/ai-first-saas-seed-app-description/` is the canonical description-layer/reference asset for structure, semantics, UI style guide, readiness review, and future generation input.
- this template is the scaffold source for local executable backend/frontend behavior.
- when docs and template both describe the same concept, preserve the seed app-description as the meaning contract and this template as its runnable starter realization.

## Minimum-first scaffold path

Natural-language requests for a “minimum AI-first app,” “starter app,” “basic app,” “smallest useful app,” or chatbot-like initial SaaS must start from the canonical minimum doctrine in `docs/minimum-ai-first-saas-app.md`: the **five core v0 workstream set**, not a generic chatbot or a single User Admin-only slice.

The first runnable starter target is intentionally narrower than full-core readiness. It must provide bootstrap authorization, selected `AuthContext`, bounded AI-first managed functional agents, durable workstream log entries, real model-backed `markdown_response` v1 surfaces produced through a concrete Akka `Agent` component, active runtime configuration resolution, governed loader tools, `ToolPermissionBoundary` enforcement, `effects().tools(runtimeTools)` registration, backend capability boundaries, provider/configuration failure handling, and audit/work trace substrate for these five core v0 workstreams. My Account is launched only by clicking the signed-in user tile/email at the bottom of the left rail; it is not listed with the other top-rail workstreams:

1. My Account
2. User Admin
3. Agent Admin
4. Audit/Trace
5. Governance/Policy

The scaffold must record follow-up work that distinguishes five-core-v0 readiness from full-core readiness: richer structured surfaces, complete invitation onboarding, support access, full governed agent document lifecycle, searchable audit/trace views, policy/governance workflows, security hardening, and only then app-specific domain workstreams.

This template may contain broader full-core scaffold assets, but generated-app guidance must not claim five-core-v0 readiness unless normal workstream message submission uses the governed runtime path, resolves active configuration from managed records, invokes the `WorkstreamRuntimeAgent` Akka Agent component with `effects().tools(runtimeTools)`, and uses a configured backend model provider, and must not claim full-core readiness unless those follow-up gates are satisfied and tested. Direct `ModelProviderClient` or service-only provider calls are support seams, not a substitute for the user-facing Akka Agent runtime path.

## Base package policy

The scaffold flow must ask before materializing Java files:

> What Java base package should I use for generated code? Press Enter to use `ai.first`.

If the user accepts or defers, use `ai.first`.

Rendering rules:

- `{{JAVA_BASE_PACKAGE}}` becomes the selected Java package, for example `ai.first`.
- `{{JAVA_PACKAGE_PATH}}` becomes the matching source path, for example `ai/first`.
- `{{MAVEN_GROUP_ID}}` defaults to the selected Java package.
- `com.example` is not used by this starter unless the user explicitly requests it as the selected package.

## Backend skeleton

The backend skeleton starts at `backend/` and renders into the target project root as:

```text
pom.xml
src/main/java/<selected package>/api
src/main/java/<selected package>/application
src/main/java/<selected package>/domain
src/main/resources
src/test/java/<selected package>
```

The package layout follows the skills-pack convention:

- `domain` contains pure records, validation, command/event decisions, and shared foundation types.
- `application` contains Akka components: entities, views, workflows, consumers, timed actions, and agents.
- `api` contains HTTP/gRPC/MCP endpoints and API DTOs.

The scaffolded backend foundation includes:

- canonical Account/Profile/Settings/Tenant/Customer/Membership/Role/AuthContext/AdminAudit domain records;
- local AuthContext resolution from WorkOS JWT identity plus Akka-owned account and membership state;
- JWT-protected `GET /api/me` returning browser-safe account, profile, settings, selected context, memberships, capabilities, functional-agent availability, and audit correlation;
- backend denial paths for disabled accounts, missing memberships, forbidden selected contexts, and tenant/customer mismatch;
- invitation onboarding and user administration services with captured-outbox/Resend boundary, idempotency, and audit behavior;
- a first durable Akka invitation repository seam: `DurableInvitationRepositoryEntity` stores current invitation/outbox state behind the existing `InvitationRepository` contract through `AkkaInvitationRepository` while preserving the in-memory adapter as the default local/demo fallback;
- a first durable Akka governed-agent repository seam: `DurableAgentBehaviorRepositoryEntity` stores current AgentDefinition, PromptDocument, SkillDocument, ReferenceDocument, AgentSkillManifest, AgentReferenceManifest, and ToolPermissionBoundary records behind `AgentBehaviorRepository` through `AkkaAgentBehaviorRepository` while preserving the in-memory adapter as the default local/demo fallback;
- governed runtime agent records, seed import, deterministic prompt assembly with compact skill/reference manifests, authorized `readSkill(skillId)` and `readReferenceDoc(referenceId)`, behavior-change proposal semantics, and trace records;
- workstream API services for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy surface payloads;
- service tests that can run after scaffold placeholder rendering.

Current durability coverage:

- durable/component seam present: invitation current state and captured email outbox through `DurableInvitationRepositoryEntity` plus entity tests for lookup, tenant-scoped duplicate detection, and idempotent outbox enqueue;
- durable/component seam present: governed-agent current state through `DurableAgentBehaviorRepositoryEntity` and `AgentBehaviorRepositoryState`, covering seeded AgentDefinition, active prompt, active skill, active reference, compact skill/reference manifests, and tool boundary records used by prompt assembly, `readSkill(skillId)`, and `readReferenceDoc(referenceId)`;
- local/demo fallback retained: `StarterSecurityComponents` still wires `InMemoryIdentityRepository`, `InMemoryInvitationRepository`, and `InMemoryAgentBehaviorRepository` so a clean scaffold runs without dependency injection setup;
- remaining slices: Account/Profile/Settings/Membership, Tenant/Customer, AdminAuditEvent history, event-sourced invitation lifecycle history, event-sourced PromptDocument/SkillDocument/ReferenceDocument/manifest/ToolPermissionBoundary lifecycle history, InvitationView/UserDirectoryView/AdminAuditView/governed-agent projections, and endpoint-level binding to Akka-backed repositories.

## Internal/background agent guidance

The starter's five core v0 workstreams remain user-facing request/response workstreams backed by a concrete request-based Akka `Agent` component. Do not migrate `WorkstreamRuntimeAgent`, the persistent composer path, or normal `markdown_response` generation to `AutonomousAgent` by default; those turns must keep the governed runtime path with active managed configuration resolution, governed loader tools, `ToolPermissionBoundary` enforcement, `effects().tools(runtimeTools)`, provider fail-closed behavior, and trace emission.

When extending the starter with durable internal/background agent work, default to Akka `AutonomousAgent` when the work has typed task lifecycle, model-driven iteration, dependencies, cancellation/failure, snapshots, notifications, delegation, handoff, team coordination, moderation, or independent long-running execution. Good starter growth examples include access-review investigations, admin-risk review, invitation-drafting queues, support-access review, audit-summary batches, governance replay/evaluation loops, policy-change analysis, digest generation, monitoring/remediation processors, and specialist agent work that continues after a user-facing turn.

Generated-app extensions must expose those background tasks through governed capabilities before wiring APIs or UI: authorize task start/query/result/cancel/notification operations, scope task and agent instance ids by tenant/customer/AuthContext, enforce model policy and provider-secret boundaries, enforce `ToolPermissionBoundary` before registering tools, record `PromptAssemblyTrace`/`SkillLoadTrace`/`ReferenceLoadTrace`/`AgentWorkTrace` plus task lifecycle traces, and route side-effecting or authority-expanding actions to approval/decision-card surfaces. Akka autonomous `AgentDefinition` means the SDK definition returned by `AutonomousAgent.definition()` or supplied through `AgentSetup`; qualify it separately from this starter's governed managed-agent `AgentDefinition` records.

The scaffold includes the validated React/Vite workstream frontend under `frontend/`. Its production build writes Akka static resources to `src/main/resources/static-resources/`, and `StarterFrontendEndpoint` serves `/`, `/ui`, `/workstream`, `/favicon.ico`, and `/assets/**` while protected APIs remain under `/api/...`.

## Fullstack scaffold validation

From the skills-pack source repository, validate the rendered starter with one command:

```bash
tools/validate-ai-first-saas-starter-fullstack.sh
```

The validation command scaffolds this template into a temporary target, verifies rendered backend/frontend paths, checks that `WorkstreamRuntimeAgent` still registers runtime tools with `effects().tools(runtimeTools)`, runs `mvn test` including governed agent seed/runtime tests and Akka Agent runtime guards for the five core v0 workstreams, runs `npm install`, `npm test -- --run`, `npm run typecheck`, and `npm run build`, verifies the frontend build wrote Akka static resources under `src/main/resources/static-resources/`, scans the built static assets for obvious backend secret markers, and reports the optional provider smoke state. If `OPENAI_API_KEY` is absent, the provider smoke is skipped loudly and validation still passes; if backend model-provider env is present, it runs a targeted real model smoke through one message in each five-core v0 workstream, backend workstream message submission, the `ComponentClient`-backed `WorkstreamRuntimeAgent`, runtime tool registration, trace ids, and secret-boundary checks. Use `--keep` to retain the generated target for inspection.

Focused workstream icon proof from the skills-pack source repository:

```bash
tools/prove-workstream-icons-v0.sh
```

This scaffolds the starter into a temporary target without network access and verifies that User Admin, Agent Admin, Audit/Trace, and Governance/Policy expose descriptor-backed left rail icon affordances while My Account remains launched only from the lower-left signed-in user tile.

## Local build and manual-test commands

From a scaffolded project:

```bash
mvn test
cd frontend
npm install
npm test -- --run
npm run typecheck
npm run build
cd ..
mvn compile exec:java
```

Optional provider smoke from the skills-pack source repository:

```bash
# Skip mode is safe for CI with no provider secrets:
env -u OPENAI_API_KEY tools/smoke-ai-first-saas-starter-real-model.sh

# Real mode runs a targeted JUnit smoke in a rendered scaffold and submits one
# prompt through each five-core v0 workstream via backend WorkstreamService:
export OPENAI_API_KEY="sk-..."
export OPENAI_MODEL_ID="gpt-4o-mini" # optional; defaults to gpt-4o-mini
export OPENAI_API_BASE_URL="https://api.openai.com/v1" # optional default
export OPENAI_REQUEST_TIMEOUT_SECONDS="30" # optional default
tools/smoke-ai-first-saas-starter-real-model.sh
```

The starter's Akka model-provider config intentionally keeps `temperature` and `top-p` at OpenAI default-compatible values because some supported OpenAI models reject non-default sampling overrides. Keep any future sampling overrides model-specific.

Then open the Akka-hosted frontend at `http://localhost:9000/` or `http://localhost:9000/ui` depending on the local Akka port. Use `?fixtureWorkstream=1` only to inspect the frontend fixture mode; normal starter testing should exercise `/api/workstream/...` backend APIs.

## Local environment and AuthKit bootstrap

The scaffold renders `.env.example` into the target project. Copy it to `.env` and fill in provider values before local manual testing that needs real WorkOS/AuthKit, Resend, or model-backed agent calls. Provider settings for workstream agents are backend-only; missing or blank model-provider settings must block message submission with an actionable error, not silently fall back to deterministic response text:

```bash
cp .env.example .env
set -a
source .env
set +a
```

For local WorkOS/AuthKit sign-in:

1. Create/select a WorkOS application for local development.
2. Add `http://localhost:9000` as the AuthKit redirect/callback URI.
3. Put the public client id in `frontend/.env.local` as `VITE_WORKOS_CLIENT_ID`; keep `VITE_WORKOS_REDIRECT_URI=http://localhost:9000`.
4. Put backend-only WorkOS values in `.env`: `WORKOS_API_KEY`, `WORKOS_JWT_ISSUER`, and `WORKOS_JWT_AUDIENCE` from the same WorkOS environment, plus `APP_PUBLIC_BASE_URL=http://localhost:9000`.
5. Set `ADMIN_USERS="your.email@example.com:SAAS_OWNER_ADMIN:OWNER"` before running the backend for real local AuthKit testing. Add tenant admins as `email:TENANT_ADMIN:tenant-starter` when needed.
6. Set `RESEND_API_KEY` and `INVITE_EMAIL_FROM` or `RESEND_FROM_EMAIL` before testing production invitation email delivery; local/dev/test may use the captured outbox adapter.

AuthKit access tokens may contain `sub` without `email`. The backend resolves `/api/me` identity through `WorkosIdentityResolver`: it uses email-like token claims when present and otherwise calls WorkOS user-management server-side with backend-only `WORKOS_API_KEY`. This lookup does not authorize users by itself; local Akka `ADMIN_USERS`, account, membership, and invitation state remains authoritative.

Important variables:

- backend-only: `WORKOS_API_KEY`, `WORKOS_API_BASE_URL`, `WORKOS_JWT_ISSUER`, `WORKOS_JWT_AUDIENCE`, `ADMIN_USERS`, `RESEND_API_KEY`, `RESEND_FROM_EMAIL`, `INVITE_EMAIL_FROM`, `INVITE_EMAIL_SUBJECT`, `RESEND_API_BASE_URL`, `OPENAI_API_KEY`, model id, provider endpoint, and provider timeout;
- browser-public: `VITE_WORKOS_CLIENT_ID`, `VITE_WORKOS_REDIRECT_URI`.

First-admin semantics are intentionally closed:

- there is no open self-registration and no silent privileged account creation from `/api/me`;
- `ADMIN_USERS` is an explicit first-admin allowlist for the clean local scaffold using `email:ROLE:scope` entries, such as `email:SAAS_OWNER_ADMIN:OWNER`, `email:TENANT_ADMIN:tenant-starter`, or `email:CUSTOMER_ADMIN:tenant-starter/customer-123`;
- if `ADMIN_USERS` is unset, only deterministic `admin@example.test` / `member@example.test` demo records are seeded for tests and fixture inspection;
- production-ready projects must replace the in-memory starter repository with durable local authorization state and an audited bootstrap/import flow before external use.

Never put backend secrets into frontend env files or built static assets.

Generated backend code that loads required environment values must treat missing, empty, and blank values as unset. Startup/readiness or blocked-operation logs must include the exact missing env var name, for example `Required backend environment variable [WORKOS_API_KEY] is not set or is blank`, and must never log secret values.

## Manual real-model smoke checklist

Run this after the workstream-agent runtime is implemented and before calling the five core v0 starter functional. The automated provider smoke is `tools/smoke-ai-first-saas-starter-real-model.sh`; it skips when `OPENAI_API_KEY` is absent and, when enabled, verifies a `ComponentClient` → `WorkstreamRuntimeAgent` Akka Agent invocation, provider-backed `markdown_response`, PromptAssemblyTrace/MODEL_INVOCATION/AgentWorkTrace shape, trace ids, and provider-secret redaction:

1. Load `.env` with backend-only WorkOS/AuthKit, JWT, admin-bootstrap, and model-provider variables such as `OPENAI_API_KEY`; keep provider secrets out of `frontend/.env*`.
2. Run `mvn test`, frontend tests/typecheck/build, and `mvn compile exec:java` from the rendered project root where `pom.xml` and `src/` live.
3. Sign in through AuthKit as a configured `ADMIN_USERS` account.
4. Open the workstream UI and submit one short prompt in each workstream: open My Account from the signed-in user tile/email at the bottom of the left rail, then use the top-rail entries for User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
5. Verify each response is an Akka Agent-backed, provider-backed `markdown_response` and that prompt/model/work trace metadata is present with secrets redacted.
6. Inspect `/api/me`, workstream payloads, trace surfaces, `frontend/.env*`, and built static resources for secret-boundary violations; no `OPENAI_API_KEY` value or backend secret should appear.
7. Restart without model-provider variables and verify message submission is blocked with actionable provider-configuration copy instead of deterministic fallback output.
