# Frontend API Contracts

Canonical workstream UI calls protected backend APIs for data/actions; routes are not authorization.

## User Admin compatibility contracts

- `GET /api/admin/users/dashboard` returns `UserAdminDashboardPayload` with scoped dashboard counters, trace refs, and authorized actions.
- `GET /api/admin/users` returns scoped user/invitation rows.
- `GET /api/admin/users/{accountId}` returns `UserAdminUserAccountPayload` for an authorized account detail.
- `POST /api/admin/invitations` creates an invitation request without exposing raw invitation tokens/token hashes.
- `POST /api/admin/invitations/{invitationId}/resend` resends when authorized.
- `POST /api/admin/invitations/{invitationId}/revoke` revokes when authorized.

## Workstream shell contracts

- `POST /api/workstream/bootstrap` loads `/api/me`-safe shell state and structured surfaces.
- `POST /api/workstream/actions` executes capability-backed surface actions with `X-Selected-Context-Id`.
