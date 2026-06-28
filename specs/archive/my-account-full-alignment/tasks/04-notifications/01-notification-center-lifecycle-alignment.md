# MAFA-04-001: Notification center lifecycle alignment

## Goal

Prove and repair My Account notification center backend/frontend automated behavior against the revised `my_account.notification_center.v1` contract.

## Required reads

- `specs/my-account-full-alignment/README.md`
- `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md` notification center section
- `app-description/domains/core-starter/workstreams/my-account/tools/governed-tools.md`
- `src/main/java/ai/first/application/foundation/notification/**`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `frontend/src/workstream/surfaces/NotificationCenterSurface.tsx`
- existing notification/My Account tests

## Skills

- `akka-web-ui-testing`
- `akka-http-endpoint-testing`
- `capability-first-backend`

## Implementation requirements

- Test refresh and empty authorized center.
- Test mark-read, dismiss, archive, snooze, repeated/no-op lifecycle actions, and bounded snooze validation.
- Test in-app preference update for visible categories only.
- Test open-source reauthorization success and safe denial.
- Prove lifecycle actions mutate notification state only and do not resolve source attention/tasks/events.
- Prove hidden/cross-tenant notifications/categories/sources are omitted or safely denied.
- Prove frontend does not render external channel/provider/outbox/delivery controls or secrets.
- Repair backend/frontend drift discovered by tests.

## Vertical workstream contract

- Lifecycle / readiness target: build-compile to backend-ready/frontend-rendered by automated tests.
- Workstream / functional agent: My Account / `my-account-agent`.
- Surface graph/action edge: `surface-my-account-notification-center`; notification lifecycle `surface_action` edges; open source to target surface or `surface-my-account-open-denied`.
- Governed-tool id and exposure: `notification.list_my_account_center`, `notification.mark_read`, `notification.dismiss`, `notification.archive`, `notification.snooze`, `notification.update_preferences`, `attention.open_attention_item`; actor adapter `surface_action`, API/internal.
- Confirmation / approval / transaction: lifecycle actions require idempotency/correlation where implemented; no source-work mutation; no approval gate.
- Capability id: notification capability ids and `account-context-and-profile`.
- AuthContext / tenant scope: current account and selected tenant/customer; hidden source denial without enumeration.
- Akka substrate: service/workstream tests plus frontend contract/type tests.
- Audit/work trace requirements: lifecycle/read/open-source traces verified directly or via MAFA-03 evidence.
- Local validation path: Maven targeted tests, frontend tests/typecheck, `git diff --check`.

## Required checks

```bash
mvn -Dtest='WorkstreamServiceTest,*Notification*Test' test
npm --prefix frontend test -- --run frontend/src/workstream-my-account-vertical.contract.test.mjs
npm --prefix frontend run typecheck
git diff --check
```

## Done criteria

- Notification center automated behavior matches current app-description or residual gaps are queued.
- Source-alignment notification entry is updated.
- Queue status is updated and changes are committed.
