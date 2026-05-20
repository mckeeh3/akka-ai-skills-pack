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

The first backend foundation slice includes:

- canonical Account/Profile/Settings/Tenant/Customer/Membership/Role/AuthContext/AdminAudit domain records;
- local AuthContext resolution from WorkOS JWT identity plus Akka-owned account and membership state;
- JWT-protected `GET /api/me` returning browser-safe account, profile, settings, selected context, memberships, capabilities, functional-agent availability, and audit correlation;
- backend denial paths for disabled accounts, missing memberships, forbidden selected contexts, and tenant/customer mismatch;
- service tests that can run after scaffold placeholder rendering.

Follow-up foundation tasks replace the in-memory identity adapter with durable Akka entities/views, add invitation onboarding, support access, governed runtime agent records, complete admin APIs, frontend surfaces, and security tests.
