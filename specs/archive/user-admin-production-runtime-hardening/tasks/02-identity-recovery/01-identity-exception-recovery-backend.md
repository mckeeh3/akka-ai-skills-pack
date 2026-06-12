# TASK-UAPRH-03-001: Implement durable identity exception recovery backend

## Intent

Implement production identity exception recovery state/workflow for relink request, review, approval/denial, completion, no-op/replay, authorization, and audit.

## Required reads

- `AGENTS.md`
- `specs/user-admin-production-runtime-hardening/README.md`
- production runtime contract from `TASK-UAPRH-01-001`
- `src/main/java/ai/first/application/foundation/identity/**`
- `src/main/java/ai/first/application/coreapp/useradmin/**`
- `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`
- relevant tests under `src/test/java/ai/first/**`

## Skills

- `akka-basic-user-admin`
- `akka-workos-user-auth`
- `akka-workflows`
- `akka-kve-application-entity`
- `akka-http-endpoint-testing`

## Expected outputs

- Durable identity recovery entity/workflow/service changes.
- Admin/workstream action routing where needed.
- Backend tests.

## Required checks

```bash
git diff --check
env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest,InvitationAndUserAdminServiceTest test
```

## Done criteria

- Identity exception recovery supports request, review, approve/deny, complete, no-op/replay, and hidden/cross-scope denials.
- WorkOS/provider identifiers remain redacted in browser-safe payloads.
- Audit/work traces are emitted for allow/deny/no-op/failure.

## Vertical workstream contract

- Workstream: User Admin / `agent-user-admin`.
- Attention: identity exception/relink review.
- Surfaces: identity exception review/status/system-message.
- Tool/capability: identity relink review/recovery browser/internal tools, `user_admin.identity_relink.*`.
- AuthContext: selected tenant/customer and role-specific authority.
- Substrate: entity/workflow/service plus admin/workstream API.
- Validation: focused Maven tests and diff check.
