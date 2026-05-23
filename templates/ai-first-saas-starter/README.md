# AI-First SaaS Starter Template

This template is the isolated scaffold source for a secure AI-first SaaS application generated with the skills pack.

It is template source, not a runnable project until placeholders are rendered by the scaffold command.

## Minimum-first scaffold path

Natural-language requests for a “minimum AI-first app,” “starter app,” “basic app,” “smallest useful app,” or chatbot-like initial SaaS must start from the canonical minimum doctrine in `docs/minimum-ai-first-saas-app.md`: **User Admin workstream v0**, not a generic chatbot.

The first runnable starter slice is intentionally narrower than full-core readiness. It must provide bootstrap authorization, selected `AuthContext`, bounded `UserAdminAgent`, durable workstream log, `markdown_response`, backend capability boundary, and audit/work trace substrate. The scaffold must record follow-up work for fuller User Admin structured surfaces, Agent Admin, Audit/Trace UI/search, invitation onboarding, support access, governed agent documents, security completeness, and only then app-specific workstreams.

This template may contain broader full-core scaffold assets, but generated-app guidance must not claim full-core readiness unless those follow-up gates are satisfied and tested.

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

The scaffold includes the validated React/Vite workstream frontend under `frontend/`. Its production build writes Akka static resources to `src/main/resources/static-resources/`, and `StarterFrontendEndpoint` serves `/`, `/ui`, `/workstream`, `/favicon.ico`, and `/assets/**` while protected APIs remain under `/api/...`.

## Fullstack scaffold validation

From the skills-pack source repository, validate the rendered starter with one command:

```bash
tools/validate-ai-first-saas-starter-fullstack.sh
```

The validation command scaffolds this template into a temporary target, verifies rendered backend/frontend paths, runs `mvn test` including the governed agent seed/runtime tests for User Admin skills and references, runs `npm install`, `npm test -- --run`, `npm run typecheck`, and `npm run build`, verifies the frontend build wrote Akka static resources under `src/main/resources/static-resources/`, and scans the built static assets for obvious backend secret markers. Use `--keep` to retain the generated target for inspection.

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

Then open the Akka-hosted frontend at `http://localhost:9000/` or `http://localhost:9000/ui` depending on the local Akka port. Use `?fixtureWorkstream=1` only to inspect the frontend fixture mode; normal starter testing should exercise `/api/workstream/...` backend APIs.

## Local environment and AuthKit bootstrap

The scaffold renders `.env.example` into the target project. Copy it to `.env` and fill in provider values before local manual testing that needs real WorkOS/AuthKit, Resend, or model-backed agent calls:

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
5. Set `ADMIN_USERS="your.email@example.com:TENANT_ADMIN:tenant-starter"` before running the backend for real local AuthKit testing.
6. Set `RESEND_API_KEY` and `INVITE_EMAIL_FROM` or `RESEND_FROM_EMAIL` before testing production invitation email delivery; local/dev/test may use the captured outbox adapter.

AuthKit access tokens may contain `sub` without `email`. The backend resolves `/api/me` identity through `WorkosIdentityResolver`: it uses email-like token claims when present and otherwise calls WorkOS user-management server-side with backend-only `WORKOS_API_KEY`. This lookup does not authorize users by itself; local Akka `ADMIN_USERS`, account, membership, and invitation state remains authoritative.

Important variables:

- backend-only: `WORKOS_API_KEY`, `WORKOS_API_BASE_URL`, `WORKOS_JWT_ISSUER`, `WORKOS_JWT_AUDIENCE`, `ADMIN_USERS`, `RESEND_API_KEY`, `RESEND_FROM_EMAIL`, `INVITE_EMAIL_FROM`, `INVITE_EMAIL_SUBJECT`, `RESEND_API_BASE_URL`, `OPENAI_API_KEY`;
- browser-public: `VITE_WORKOS_CLIENT_ID`, `VITE_WORKOS_REDIRECT_URI`.

First-admin semantics are intentionally closed:

- there is no open self-registration and no silent privileged account creation from `/api/me`;
- `ADMIN_USERS` is an explicit first-admin allowlist for the clean local scaffold, currently limited to `email:TENANT_ADMIN:tenant-starter` entries;
- if `ADMIN_USERS` is unset, only deterministic `admin@example.test` / `member@example.test` demo records are seeded for tests and fixture inspection;
- production-ready projects must replace the in-memory starter repository with durable local authorization state and an audited bootstrap/import flow before external use.

Never put backend secrets into frontend env files or built static assets.

Generated backend code that loads required environment values must treat missing, empty, and blank values as unset. Startup/readiness or blocked-operation logs must include the exact missing env var name, for example `Required backend environment variable [WORKOS_API_KEY] is not set or is blank`, and must never log secret values.
