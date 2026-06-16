# TASK-FCBAD-02-002: Repair Customer Admin target propagation and workstream invitation path

## Objective

Fix the runtime drift where User Admin Customer Admin branch surfaces lose the selected `customerId`, and the workstream invite form can use the generic tenant-scoped invitation action instead of a customer-scoped Customer Admin invitation.

## Source findings

- `runtime-audit/foundation-customer-boundary-runtime-drift-audit.md#fcb-rd-03-customer-admin-branch-surfaces-do-not-preserve-selected-customer-target`
- `runtime-audit/foundation-customer-boundary-runtime-drift-audit.md#fcb-rd-04-customer-admin-invitation-from-workstream-can-create-the-wrong-scope`

## Required reads

- `AGENTS.md`
- `specs/foundation-customer-boundary-app-description/runtime-audit/foundation-customer-boundary-runtime-drift-audit.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/user-admin/agents/functional-agent.md`
- `app-description/domains/core-starter/workstreams/user-admin/realization/akka-components.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/application/foundation/invitation/InvitationService.java`
- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`
- `frontend/src/workstream/surfaces/UserAdminScopedAdminSurface.tsx`
- this task brief

## Implementation scope

- Propagate selected Customer identity from Customer detail into Customer Admin list, invitation, and detail branch surfaces.
- Customer Admin branch payloads should include browser-safe `customerId`, display label when available, branch metadata, target scope proof, and redaction.
- Add a dedicated Customer Admin invite action/path, or otherwise make the backend distinguish Customer Admin invite from generic user invite by trusted backend-authored surface/action context.
- Workstream Customer Admin invitation must call `InvitationService.createInvitation` with `ScopeType.CUSTOMER`, selected tenant id, target `customerId`, and Customer Admin-safe roles.
- Frontend `UserAdminScopedAdminSurface` should submit the target `customerId` from backend-authored data/action context, not infer hidden target state.

## Required tests

Add/update focused tests proving:

- Customer detail -> Customer Admins preserves `customerId` and opens `surface-user-admin-customer-admins` for the selected Customer.
- Customer detail -> Invite Customer Admin preserves `customerId` and opens `surface-user-admin-customer-admin-invitation-create`.
- Submitting Customer Admin invitation through the workstream creates a customer-scoped invitation with `ScopeType.CUSTOMER`, selected tenant id, target `customerId`, and `CUSTOMER_ADMIN` role.
- Generic user invitation behavior remains unchanged for ordinary tenant/customer selected contexts.

## Required checks

- `mvn -Dtest=ai.first.application.coreapp.workstream.WorkstreamServiceTest test`
- `npm --prefix frontend test -- --run frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `git diff --check`

## Done criteria

- Customer Admin branch no longer loses target Customer scope.
- Workstream Customer Admin invitation cannot create a tenant-scoped invitation by accident.
- Tests prove backend target propagation and frontend submission payload behavior.
- Queue status and notes are updated and committed with code/test changes.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent`.
- Attention category or non-attention reason: runtime drift repair; no new attention item.
- Role-specific dashboard / surface: `surface-user-admin-customer-detail`, `surface-user-admin-customer-admins`, `surface-user-admin-customer-admin-invitation-create`.
- Surface graph node/action edge: Customer detail to Customer Admin branch and Customer Admin invite submit.
- Governed-tool id and exposure: `manage-customer-admins` browser-tool/workstream action.
- Capability id: `tenant.customer_admin.list`, `tenant.customer_admin.invite`.
- AuthContext / roles / tenant scope: Organization/Tenant Admin selected context plus explicit target Customer proof.
- Akka substrate: WorkstreamService deterministic action routing plus InvitationService call; frontend surface renderer.
- API / frontend / realtime path: `/api/workstream/actions` and scoped admin frontend surface.
- Audit/work trace requirements: invitation audit/outbox traces remain scoped to target Customer; no raw tokens/secrets.
- Local validation path: focused Maven test, frontend contract test, `git diff --check`.
