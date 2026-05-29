# Full-Core SMB User Admin Access Management

## Purpose

Implement the next User Admin full-core SMB slice after the dashboard/invitation foundation: practical access management for SMB operators.

This mini-project focuses on member status changes and role/capability management through the AI-first workstream shell, with deterministic backend guardrails, traceable denials/no-ops, attractive member/role surfaces, and runtime/API/UI validation.

## Background

Predecessor mini-projects:

- `specs/full-core-smb-saas-hardening/`
- `specs/full-core-smb-baseline-and-ux/`
- `specs/full-core-smb-user-admin/`

`specs/full-core-smb-user-admin/user-admin-vertical-contracts.md` defines the User Admin slice order:

1. directory and invitation dashboard foundation — completed;
2. member status and role-change actions — this mini-project;
3. UserAdminAgent request/response guidance;
4. access-review worker candidate.

This slice creates the deterministic capability foundation needed before AI guidance or access-review workers can safely recommend actions.

## Scope

- Member directory/detail surface improvements for status and role/capability evidence.
- Disable/reactivate user actions with last-admin, self-disable, disabled-user, tenant/customer, no-op, idempotency, and audit/trace guardrails.
- Role/capability preview and commit surfaces with capability deltas, affected workstreams, policy/approval hints, last-admin preservation, idempotency, and trace links.
- Backend deterministic services or existing service extensions for authorization, status transition validation, role matrix/delta calculation, projection shaping, and trace emission.
- Frontend typed surfaces/actions for member status and role-change workflows inside the User Admin workstream, not page-first CRUD.
- Validation through starter backend/frontend tests and fullstack validation when runtime contracts change broadly.

## Non-goals

- Do not implement UserAdminAgent request/response guidance in this mini-project except to preserve existing behavior and provider fail-closed semantics.
- Do not implement access-review AutonomousAgent worker yet.
- Do not add enterprise SCIM/SSO lifecycle consoles or complex custom role-builder suites.
- Do not allow AI output, prompt text, hidden fields, or frontend affordances to grant access authority.
- Do not mark runtime behavior complete with fixture-only, deterministic model-less, or mock paths.

## Target source areas

Primary executable baseline:

- `templates/ai-first-saas-starter/`

Likely source families:

- backend security/application services under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/`
- backend workstream/API endpoints under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/api/`
- backend tests under `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/`
- frontend workstream API/types/surfaces/actions under `templates/ai-first-saas-starter/frontend/src/`
- root `frontend/` mirror when repository conventions require synchronization

## Execution model

Execute one task per fresh harness session. Start with source-boundary inspection and slice contract refinement, then implement backend and frontend vertical slices, then verify.

## Done state

This mini-project is complete when:

- User Admin has implementation-ready and/or implemented SMB member status and role/capability management slices;
- disable/reactivate and role-change preview/commit behavior has deterministic backend authority/idempotency/audit/trace guardrails;
- frontend User Admin surfaces render member status, role/capability deltas, denials, no-ops, and trace links attractively inside the workstream shell;
- local starter validation passes for the implemented scope or concrete blocker tasks are appended;
- no planning gap remains before the next User Admin AI guidance mini-project.
