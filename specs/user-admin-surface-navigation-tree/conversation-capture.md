# Conversation Capture: User Admin Surface Navigation Tree

## Source discussion

The User Admin workstream surface model needs an explicit navigation tree rather than a flat set of surfaces. The accepted design is:

```text
User Admin Dashboard = trunk
├── User Directory = first-level branch
│   ├── User Detail / Access Inspection
│   ├── Invitation Create
│   ├── Invitation Detail
│   ├── Invitation Resend Confirmation
│   ├── Invitation Revoke Confirmation
│   ├── Membership / Account Status Confirmation
│   ├── Role Change Preview
│   ├── Support Access Grant / Extend
│   ├── Support Access Revoke Confirmation
│   ├── Access Review Task
│   └── Identity Exception Review
└── Organization Directory = first-level branch, SaaS Owner/App Admin only
    ├── Organization Detail
    ├── Organization Create
    ├── Organization Rename
    ├── Organization Suspend Confirmation
    └── Organization Reactivate Confirmation
```

The Organization Directory must be accessible from the User Admin Dashboard when the selected `AuthContext` has backend authorization such as `saas_owner.organization.list`. It must be omitted or safely denied for Tenant Admin/Customer Admin contexts without that capability.

Surfaces above the directories must provide navigation back to the appropriate directory:

- User branch descendants expose a **Show users** / **Back to users** action returning to `surface-user-admin-users` with the relevant backend-shaped filter/context preserved where safe.
- Organization branch descendants expose a **Show organizations** / **Back to organizations** action returning to `surface-user-admin-organization-directory` with the relevant backend-shaped filter/context preserved where safe.

The current app-description, backend/workstream payloads, frontend surfaces, and tests need to be revised to conform to this tree. Missing surfaces need to be implemented. Existing User Admin or Organization Admin surfaces must first be surveyed and classified as usable as-is, revise, remove/deprecate, or missing/new.

## Accepted decisions

- Use a task-oriented mini-project because the work spans survey, app-description, backend/API payload shape, frontend implementation, and validation.
- Start with a survey/inventory task before implementation to avoid duplicating or patching obsolete surfaces.
- Preserve the SaaS Foundation App runtime doctrine: complete implementation requires the real local backend/API/UI path at the stated scope, not fixture-only behavior.
- Preserve backend-authoritative authorization, tenant/customer scoping, audit/work traces, stale/reconnect handling, and frontend secret boundaries.

## Non-decisions / constraints

- This mini-project does not redesign all User Admin capabilities; it repairs the surface graph/navigation and implements missing branch surfaces at the current foundation scope.
- This mini-project does not make SaaS Owner authority a shortcut to tenant application data. Organization Directory remains platform-safe and Tenant data access requires separate scoped authority/support access.
