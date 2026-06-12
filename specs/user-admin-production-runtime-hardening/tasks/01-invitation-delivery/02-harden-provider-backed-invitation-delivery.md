# TASK-UAPRH-02-001: Harden provider-backed invitation delivery

## Intent

Implement or repair the Resend-backed invitation email/outbox runtime path, including retries, delivery state, fail-closed missing config, idempotency, and audit.

## Required reads

- `AGENTS.md`
- `specs/user-admin-production-runtime-hardening/README.md`
- `specs/user-admin-production-runtime-hardening/production-runtime-contract.md` if present
- `src/main/java/ai/first/application/foundation/email/**`
- `src/main/java/ai/first/application/foundation/invitation/**`
- `src/main/java/ai/first/application/coreapp/useradmin/**`
- `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`
- `src/test/java/ai/first/application/coreapp/useradmin/**`

## Skills

- `akka-resend-email-service`
- `akka-basic-user-admin`
- `akka-consumer-testing`
- `akka-http-endpoint-testing`

## Expected outputs

- Backend invitation delivery/outbox/provider changes.
- Focused tests for configured fake-provider success, missing config, retry/no-op, revoke interaction, and audit/redaction.

## Required checks

```bash
git diff --check
env -u ADMIN_USERS mvn -q -Dtest=InvitationAndUserAdminServiceTest,EmailNotificationServiceTest test
```

## Done criteria

- Invitation create/resend uses provider/outbox runtime path when configured.
- Missing Resend config returns actionable fail-closed state with no fake success.
- Delivery attempts, retries, failures, revokes, no-ops, idempotency, and audit are covered by tests.
- Tokens, email bodies, and provider secrets are never exposed in browser-safe payloads.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `agent-user-admin`.
- Attention: invitation delivery failure/stale/pending queues.
- Surfaces: invitation create/detail/resend/revoke/system-message.
- Governed tools: `create-or-resend-invitation` browser/internal tool.
- Capabilities: `user_admin.invite_user`, `user_admin.resend_invitation`, `user_admin.revoke_invitation`, delivery status read.
- AuthContext: selected tenant/customer scope and role authorization.
- Substrate: email service/outbox, invitation state/history, admin/workstream API.
- Validation: Maven focused tests and diff check.
