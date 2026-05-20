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
- governed runtime agent records, seed import, deterministic prompt assembly, authorized `readSkill(skillId)`, behavior-change proposal semantics, and trace records;
- workstream API services for Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy surface payloads;
- service tests that can run after scaffold placeholder rendering.

The current scaffold is backend-first. The validated React/Vite workstream frontend is installed as a reference under `.agents/resources/examples/frontend/`; materializing it into the scaffolded app remains an explicit extension step until `frontend/` is added to this template.

## Local environment

The scaffold renders `.env.example` into the target project. Copy it to `.env` and fill in provider values before local manual testing that needs real WorkOS/AuthKit, Resend, or model-backed agent calls:

```bash
cp .env.example .env
set -a
source .env
set +a
```

Important variables:

- backend-only: `WORKOS_API_KEY`, `WORKOS_API_BASE_URL`, `WORKOS_JWT_ISSUER`, `WORKOS_JWT_AUDIENCE`, `ADMIN_USERS`, `RESEND_API_KEY`, `RESEND_FROM_EMAIL`, `INVITE_EMAIL_FROM`, `INVITE_EMAIL_SUBJECT`, `RESEND_API_BASE_URL`, `OPENAI_API_KEY`;
- browser-public if/when the frontend is materialized: `VITE_WORKOS_CLIENT_ID`, `VITE_WORKOS_REDIRECT_URI`.

Never put backend secrets into frontend env files or built static assets.
