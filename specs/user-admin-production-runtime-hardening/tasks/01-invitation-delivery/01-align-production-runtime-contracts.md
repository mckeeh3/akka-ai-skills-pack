# TASK-UAPRH-01-001: Align production runtime contracts

## Intent

Update app-description/spec artifacts so invitation delivery, identity recovery, and model-backed access-review production behavior are explicit before code changes.

## Required reads

- `AGENTS.md`
- `specs/user-admin-production-runtime-hardening/README.md`
- `specs/user-admin-production-runtime-hardening/conversation-capture.md`
- `specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md`
- `app-description/domains/core-starter/workstreams/user-admin/**`
- `app-description/domains/core-starter/capabilities/user-and-access-administration.md`
- `specs/archive/user-admin-surface-conformance-cleanup/conformance-verification.md`

## Skills

- `app-description-surface-modeling`
- `app-description-behavior-specification`
- `app-description-test-specification`
- `akka-resend-email-service`
- `akka-agent-component`
- `akka-agent-tool-boundaries`

## Expected outputs

- Updated app-description/spec notes for production runtime contracts.
- Optional `specs/user-admin-production-runtime-hardening/production-runtime-contract.md`.

## Required checks

```bash
git diff --check
rg "Resend|identity exception|access-review|model-backed|fail-closed|system-message" app-description/domains/core-starter/workstreams/user-admin specs/user-admin-production-runtime-hardening
```

## Done criteria

- Required provider/model/workflow behavior, surfaces, denials, traces, and tests are unambiguous for implementation tasks.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `agent-user-admin`.
- Attention: invitation delivery failure, identity exception review, access review result/blocker.
- Surfaces: invitation detail/actions, identity exception review/status, access-review task, system-message.
- Capabilities: `user_admin.invite_user`, resend/revoke, identity relink review/recovery, access review start/read/accept/reject.
- AuthContext: App/Tenant/Customer admin scopes preserved.
- Substrate: app-description/specs only.
- Validation: focused search and diff check.
