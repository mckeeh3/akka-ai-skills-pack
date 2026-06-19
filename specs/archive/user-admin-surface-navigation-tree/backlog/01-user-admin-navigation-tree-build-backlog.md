# Backlog: User Admin Navigation Tree

## Implementation order

1. Survey current surfaces and classify usable/revise/remove/missing.
2. Revise app-description to define the tree graph and branch-return action contract.
3. Update backend/workstream payloads and action handling for navigation metadata.
4. Implement dashboard and User Directory branch frontend behavior.
5. Implement Organization Directory branch frontend behavior and missing surfaces.
6. Add integration/contract tests for tree traversal and authorization.
7. Verify the sprint and mini-project done state; append follow-up tasks if needed.

## Surface groups

### Dashboard trunk

- `surface-user-admin-dashboard`
- Required branch actions:
  - Open users -> `surface-user-admin-users`
  - Open organizations -> `surface-user-admin-organization-directory` when authorized

### User branch

- Branch root: `surface-user-admin-users`
- Descendants:
  - `surface-user-admin-user-detail`
  - `surface-user-admin-invitation-create`
  - `surface-user-admin-invitation-detail`
  - `surface-user-admin-invitation-resend-confirmation`
  - `surface-user-admin-invitation-revoke-confirmation`
  - `surface-user-admin-membership-status-confirmation`
  - `surface-user-admin-role-change-preview`
  - `surface-user-admin-support-access-grant`
  - `surface-user-admin-support-access-revoke-confirmation`
  - `surface-user-admin-access-review-task`
  - `surface-user-admin-identity-exception-review`
- Required branch-return action: show/back to users -> `surface-user-admin-users`.

### Organization branch

- Branch root: `surface-user-admin-organization-directory`
- Descendants:
  - `surface-user-admin-organization-detail`
  - `surface-user-admin-organization-create`
  - `surface-user-admin-organization-rename`
  - `surface-user-admin-organization-suspend-confirmation`
  - `surface-user-admin-organization-reactivate-confirmation`
- Required branch-return action: show/back to organizations -> `surface-user-admin-organization-directory`.

## Design notes

- Direct deep links or stale payload actions must be reauthorized server-side and return safe `surface-user-admin-system-message` denials when unauthorized.
- Back-to-directory actions must preserve safe filters/context only when backend marks them safe for the selected `AuthContext`.
- Frontend action visibility is a UX hint only; backend capability checks remain authoritative.
- Organization surfaces use customer-facing **Organization** language while preserving internal Tenant isolation in backend code/audit.
