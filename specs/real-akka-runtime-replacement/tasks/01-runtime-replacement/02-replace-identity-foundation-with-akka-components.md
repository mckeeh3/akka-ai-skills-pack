# Task: Replace identity foundation runtime with Akka components

## Objective

Replace normal-runtime local-demo/fail-closed identity/account/profile/settings/membership/role/capability state with Akka component-backed state and views in the starter template.

## Required reads

- AGENTS.md
- skills/README.md
- docs/ai-first-saas-application-architecture.md
- docs/capability-first-backend-architecture.md
- specs/real-akka-runtime-replacement/README.md
- specs/real-akka-runtime-replacement/non-akka-runtime-seam-map.md
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/IdentityRepository.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/StarterSecurityComponents.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AuthContextResolver.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/MeService.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/UserAdminService.java

## Skills

- akka-key-value-entity or akka-event-sourced-entity as selected by state/audit needs
- akka-view
- akka-http-endpoints when endpoint wiring changes

## In scope

- Add or complete Akka components for Account/UserProfile/UserSettings/Membership/Role/Capability current state and required views.
- Bind normal runtime `AuthContextResolver`, `/api/me`, and user-admin reads/writes to Akka-backed state.
- Move local-demo identity/bootstrap helpers out of main runtime or make them test-only.
- Preserve first-admin bootstrap as audited Akka state import/command, not in-memory seeding.

## Out of scope

- Do not implement broad external IAM beyond existing WorkOS/AuthKit boundary.
- Do not use local-demo repositories to satisfy normal runtime tests.

## Expected outputs

- backend Akka entity/view/repository source for identity foundation
- updated `StarterSecurityComponents`/endpoint binding
- backend tests proving durable identity/membership `/api/me` and denial behavior
- updated queue notes

## Required checks

- `git diff --check`
- rendered backend tests covering `MeService`, WorkOS resolver, user admin, tenant isolation, disabled user, role/scope denial
- `rg -n "LocalDemoIdentity|FailClosedIdentity|new LocalDemoIdentity|seedLocalDemoMember|AI_FIRST_SAAS_LOCAL_DEMO" templates/ai-first-saas-starter/backend/src/main/java`

## Done criteria

- Normal runtime identity foundation uses Akka components.
- No main-source local-demo/fail-closed identity repository is wired or needed for claimed runtime behavior.
- Tests prove state survives through Akka component paths at the starter scope.
- Changes and queue update are committed.

## Commit message

`runtime: replace identity foundation with Akka components`
