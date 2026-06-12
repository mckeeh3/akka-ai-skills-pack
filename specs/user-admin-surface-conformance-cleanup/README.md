# User Admin Surface Conformance Cleanup Mini-Project

## Purpose

Repair the implemented User Admin workstream surfaces so they conform to the current workstream and structured-surface concepts instead of behaving like legacy CRUD/admin pages or generic renderers.

This mini-project covers app-facing root application assets: `app-description/`, backend workstream surface construction and action routing, frontend workstream surface rendering, legacy frontend retirement, contract/runtime tests, and verification notes.

## Current intent

User Admin is an AI-first access operations workstream owned by `user-admin-agent` / `agent-user-admin` aliasing until the implementation is normalized. The browser surfaces must be structured, backend-backed work units:

- dashboard as attention-first action router;
- User Directory and Organization Directory as collection discovery branch roots;
- lifecycle-aware show/inspection surfaces that inspect and route to task surfaces, but do not mutate inline;
- separate create, edit, decision, workflow, lifecycle-confirmation, and destructive-lifecycle-confirmation surfaces for consequential work;
- backend-authored row/card/dashboard target surfaces and action ids;
- safe `system_message` surfaces for denied, stale, hidden/not-found, no-op, provider/outbox/model blocked, validation, and conflict results;
- default user/admin copy in business language, with raw capability/action/correlation/trace diagnostics moved to role-gated drilldowns.

The previous review identified these required cleanup themes:

1. retire or absorb legacy `frontend/src/screens/admin/AdminUsersPage.tsx` behavior;
2. remove inline role/status/support mutations from User Detail;
3. use canonical surface types consistently instead of collapsing User Admin descendants into `detail-edit`;
4. normalize the dashboard trunk and/or explicitly document role-specific dashboard variants;
5. stop deriving User Admin dashboard queues client-side;
6. reduce implementation metadata in default UI;
7. make row routing fully backend-authored;
8. replace hardcoded form options with backend-shaped policy options;
9. route denials/stale/not-found to typed `surface-user-admin-system-message` where possible;
10. complete access review and identity exception task/decision semantics at starter scope;
11. align `user-admin-agent` and `agent-user-admin` naming;
12. apply Organization Admin's collection-object split model to the rest of User Admin.

## Done state

This mini-project is complete when:

1. app-description records the conformance cleanup decisions, including canonical surface-type usage, role-specific dashboard variant policy, functional-agent id normalization/aliasing, and system-message/result-surface semantics;
2. backend User Admin surface envelopes emit canonical surface types or a documented compatibility mapping without losing semantics;
3. dashboard payloads provide backend-authored attention queues/populations/actions so the frontend no longer invents User Admin queues from generic cards/sections;
4. User Detail and Invitation Detail are inspection/task-router surfaces only; consequential role, status, support-access, invitation, access-review, and identity-recovery work routes to dedicated surfaces;
5. User Directory row/card activation is backend-authored, with no frontend status/label inference needed for normal runtime routing;
6. User Admin forms use backend-shaped option/policy payloads rather than hardcoded roles/expiry choices;
7. legacy admin page/screen code is removed, retired, or absorbed into structured workstream surfaces, with routes/tests updated accordingly;
8. default User Admin UI avoids exposing implementation ids/correlation/raw trace/capability details except through role-gated diagnostic/audit drilldowns;
9. denied/stale/hidden/not-found/no-op/provider/outbox/model blocked paths return safe typed surfaces through the intended workstream/API path at the stated scope;
10. tests prove the conformant full-stack path, including backend actions, frontend rendering, authorization denials, idempotency/no-op, audit/trace redaction, and frontend secret boundaries;
11. final verification compares completed work against this README, app-description, prior navigation-tree project, and task done criteria, appending follow-up tasks plus a new terminal verification task when material gaps remain.

## Non-goals

- Redesigning all identity/auth foundation outside the User Admin structured-surface conformance scope.
- Implementing production-grade model-backed access-review or identity-recovery automation beyond safe starter-scope task/decision surfaces and fail-closed behavior.
- Granting SaaS Owner users tenant app-data, provider-secret, support-access, or billing-derived authority.
- Counting fixture-only/demo behavior as normal runtime completion.
- Editing `skills-pack/**` or installed `.agents/**` assets; this is root app realization work.

## Primary source artifacts

- `app-description/domains/core-starter/workstreams/user-admin/workstream.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/realization/traceability.md`
- `specs/user-admin-surface-navigation-tree/**`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/application/coreapp/useradmin/**`
- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`
- `frontend/src/workstream/surfaces/**`
- `frontend/src/workstream/types/surfaces.ts`
- `frontend/src/screens/admin/AdminUsersPage.tsx`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-organization-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-surfaces.contract.test.mjs`

## Task execution rules

Use `specs/user-admin-surface-conformance-cleanup/pending-tasks.md`. Execute one task per fresh harness context, update task status before implementation edits, run the task's checks, and commit each completed task with the queue update.
