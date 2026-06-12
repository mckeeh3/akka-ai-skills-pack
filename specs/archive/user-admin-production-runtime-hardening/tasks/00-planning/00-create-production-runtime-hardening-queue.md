# TASK-UAPRH-00-001: Create production runtime hardening planning scaffold

## Intent

Create the mini-project scaffold and queue for User Admin provider-backed invitation delivery, identity recovery workflows, and model-backed access-review automation.

## Required reads

- `AGENTS.md`
- `specs/user-admin-production-runtime-hardening/README.md`
- `specs/user-admin-production-runtime-hardening/conversation-capture.md`
- `specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md`

## Expected outputs

- Mini-project README, capture, sprint, backlog, task briefs, and pending queue.

## Required checks

```bash
git diff --check
```

## Done criteria

- First non-done task is runnable.
- Terminal verification task can append follow-ups.
- Scaffold is committed without staging unrelated work.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `agent-user-admin`.
- Non-attention reason: planning only.
- Surfaces: invitation, identity exception, access review, system-message surfaces.
- Governed tools/capabilities: planned `user_admin.*`, email/outbox, identity relink, access review agent tools.
- AuthContext: preserve tenant/customer scoping and App/Tenant/Customer admin boundaries.
- Substrate: specs only.
- Validation: `git diff --check`.
