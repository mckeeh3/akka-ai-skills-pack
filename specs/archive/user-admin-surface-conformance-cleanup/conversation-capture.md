# Conversation Capture: User Admin Surface Conformance Cleanup

## Source discussion

The user asked to review User Admin workstream surfaces against the workstream and structured-surface concepts. The review concluded that the implementation partially conforms but still includes broad CRUD/admin behavior and generic renderer behavior that conflicts with the app-description concepts.

The user then asked to "fix everything" identified in that review, choosing conformance and cleanup whenever a this-or-that decision is required, and to retire/remove legacy content rather than preserve stale behavior. Because the scope touches app-description, backend workstream payloads/actions, frontend rendering, legacy route cleanup, and tests, it is better handled as a bounded mini-project rather than a single session implementation.

## Decisions

- Treat this as root app realization, not skills-pack maintenance.
- Prefer conformance with `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md` and structured-surface concepts over backwards compatibility with legacy CRUD screens.
- Revise, absorb, retire, or remove legacy content/code that conflicts with structured User Admin surfaces.
- Use Organization Admin's split collection-object pattern as the implementation model for the remaining User Admin surfaces.
- Do not invent new authority. Preserve backend authorization, selected `AuthContext`, tenant/customer isolation, audit/work traces, idempotency, and frontend secret boundaries.
- If a role-specific dashboard id/contract is retained, document it explicitly as a variant of the dashboard trunk; otherwise normalize to `surface-user-admin-dashboard` semantics.
- Default UI should use business language; technical ids and raw diagnostics move to role-gated audit/support/developer drilldowns.

## Review findings to address

1. Legacy `AdminUsersPage` combines invite, list, and role assignment and should be retired/absorbed.
2. User Detail currently performs inline status/role edits; it must become inspection plus task-entry routing only.
3. Canonical User Admin surface types are inconsistently emitted/rendered.
4. Dashboard trunk vs role-specific dashboard variants need normalization or explicit modeling.
5. Frontend derives dashboard queues from cards/sections; backend should author them.
6. Default UI exposes too much implementation metadata.
7. Row routing has frontend inference fallbacks; backend must author target action/surface for normal runtime.
8. User Admin forms hardcode roles/expiry choices; backend should provide options/policy.
9. Denials/stale/hidden/not-found/no-op/provider/outbox/model blocked results need typed `system_message` surfaces.
10. Access review and identity exception surfaces need starter-scope durable task/decision semantics and fail-closed behavior.
11. Functional-agent ids need normalization or explicit aliasing.
12. User, invitation, membership, role, support access, access review, and identity exception surfaces should follow canonical collection-object progression.

## Recommended execution shape

Create bounded fresh-session tasks:

1. app-description/spec alignment;
2. backend canonical surface envelopes and backend-authored dashboard/row payloads;
3. backend inspection/task-router and typed system-message result behavior;
4. frontend surface rendering cleanup and legacy page retirement;
5. full-stack regression tests;
6. terminal verification with follow-up task append if gaps remain.
