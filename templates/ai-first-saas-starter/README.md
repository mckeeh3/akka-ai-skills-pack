# AI-First SaaS Starter Template

This template is the isolated scaffold source for a full-core secure AI-first SaaS application generated with the skills pack.

It is template source, not a runnable project until placeholders are rendered by the scaffold command.

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
- governed runtime agent records, seed import, deterministic prompt assembly, authorized `readSkill(skillId)`, behavior-change proposal semantics, and trace records;
- workstream API services for Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy surface payloads;
- service tests that can run after scaffold placeholder rendering.

Current durability coverage:

- durable/component seam present: invitation current state and captured email outbox through `DurableInvitationRepositoryEntity` plus entity tests for lookup, tenant-scoped duplicate detection, and idempotent outbox enqueue;
- local/demo fallback retained: `StarterSecurityComponents` still wires `InMemoryIdentityRepository` and `InMemoryInvitationRepository` so a clean scaffold runs without dependency injection setup;
- remaining slices: Account/Profile/Settings/Membership, Tenant/Customer, AdminAuditEvent history, event-sourced invitation lifecycle history, InvitationView/UserDirectoryView/AdminAuditView projections, and endpoint-level binding to Akka-backed repositories.

The scaffold includes the validated React/Vite workstream frontend under `frontend/`. Its production build writes Akka static resources to `src/main/resources/static-resources/`, and `StarterFrontendEndpoint` serves `/`, `/ui`, `/workstream`, `/favicon.ico`, and `/assets/**` while protected APIs remain under `/api/...`.

## Fullstack scaffold validation

From the skills-pack source repository, validate the rendered starter with one command:

```bash
tools/validate-ai-first-saas-starter-fullstack.sh
```

The validation command scaffolds this template into a temporary target, verifies rendered backend/frontend paths, runs `mvn test`, runs `npm install`, `npm test -- --run`, `npm run typecheck`, and `npm run build`, verifies the frontend build wrote Akka static resources under `src/main/resources/static-resources/`, and scans the built static assets for obvious backend secret markers. Use `--keep` to retain the generated target for inspection.

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
4. Put backend-only WorkOS values in `.env`: `WORKOS_API_KEY`, `WORKOS_JWT_ISSUER`, and `WORKOS_JWT_AUDIENCE` from the same WorkOS environment.
5. Set `ADMIN_USERS="your.email@example.com:TENANT_ADMIN:tenant-starter"` before running the backend for real local AuthKit testing.

Important variables:

- backend-only: `WORKOS_API_KEY`, `WORKOS_API_BASE_URL`, `WORKOS_JWT_ISSUER`, `WORKOS_JWT_AUDIENCE`, `ADMIN_USERS`, `RESEND_API_KEY`, `RESEND_FROM_EMAIL`, `INVITE_EMAIL_FROM`, `INVITE_EMAIL_SUBJECT`, `RESEND_API_BASE_URL`, `OPENAI_API_KEY`;
- browser-public: `VITE_WORKOS_CLIENT_ID`, `VITE_WORKOS_REDIRECT_URI`.

First-admin semantics are intentionally closed:

- there is no open self-registration and no silent privileged account creation from `/api/me`;
- `ADMIN_USERS` is an explicit first-admin allowlist for the clean local scaffold, currently limited to `email:TENANT_ADMIN:tenant-starter` entries;
- if `ADMIN_USERS` is unset, only deterministic `admin@example.test` / `member@example.test` demo records are seeded for tests and fixture inspection;
- production-ready projects must replace the in-memory starter repository with durable local authorization state and an audited bootstrap/import flow before external use.

Never put backend secrets into frontend env files or built static assets.
