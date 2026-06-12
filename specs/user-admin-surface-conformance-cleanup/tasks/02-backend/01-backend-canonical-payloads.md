# TASK-UASCC-02-001: Repair backend canonical surface envelopes and authored payloads

## Intent

Update backend User Admin workstream surface construction so normal runtime envelopes carry canonical surface semantics and backend-authored dashboard/list payloads instead of relying on frontend invention.

## Required reads

- `AGENTS.md`
- `specs/user-admin-surface-conformance-cleanup/README.md`
- `specs/user-admin-surface-conformance-cleanup/conversation-capture.md`
- `specs/user-admin-surface-conformance-cleanup/backlog/01-user-admin-surface-conformance-build-backlog.md`
- `specs/user-admin-surface-conformance-cleanup/tasks/02-backend/01-backend-canonical-payloads.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/application/coreapp/useradmin/**`
- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`

## Skills

- `akka-basic-user-admin`
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-testing`

## Expected outputs

- Backend `WorkstreamService` and related DTO/payload changes.
- Focused backend tests for canonical payloads.

## Required checks

```bash
git diff --check
mvn -q -Dtest=WorkstreamServiceTest test
```

## Done criteria

- User Admin surfaces emit canonical surface types or documented compatibility metadata without losing semantics.
- Dashboard payload includes backend-authored attention queues/counts, administered populations, authorized actions, branch actions, trace/redaction info, and no hidden counts.
- User Directory rows include complete backend-authored `targetSurfaceId`, target type/object, `openActionId`, eligibility/redaction state, and safe action context.
- Task/form payloads include backend-shaped role/support-expiry/policy options instead of requiring frontend hardcoding.
- Default browser payload separates user-safe content from diagnostic metadata.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `agent-user-admin` runtime surface graph.
- Attention category or non-attention reason: dashboard attention queues for invitations, support access, access review, denied actions, provider/outbox/model blockers.
- Role-specific dashboard / surface: User Admin dashboard variants and `surface-user-admin-users`.
- Surface graph node/action edge: dashboard -> users/organizations; users rows -> detail/invitation/role/access-review/identity/system-message.
- Governed-tool id and exposure: browser-tools such as `search-user-directory`, `create-or-resend-invitation`, `change-membership-role-or-status`, `grant-or-revoke-support-access`.
- Capability id: `user_admin.list_members`, `user_admin.invite_user`, `user_admin.preview_role_change`, `user_admin.support_access.*`, `user_admin.access_review.*`, `admin.audit.read`, `saas_owner.organization.*`.
- AuthContext / roles / tenant scope: backend-selected App Admin/Tenant Admin/Customer Admin/Auditor scope with hidden-target denial expectations.
- Akka substrate: workstream service/API payloads plus views/services used by User Admin.
- API / frontend / realtime path: `/api/workstream` surface/action path consumed by frontend workstream shell.
- Audit/work trace requirements: allow/deny/no-op/failure traces and browser-safe redaction.
- Local validation path: focused Maven test plus diff check.
