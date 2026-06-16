# Foundation Surface Completion Tracker

## Harness instructions

This document is the durable status board for foundation dashboard/surface completion. Use it together with `pending-tasks.md`.

For the next pending sub-task:

1. Select the first `pending` entry in `pending-tasks.md` whose dependencies are satisfied.
2. Mark only that task `in-progress` before edits.
3. Complete exactly one objective for exactly one dashboard/surface.
4. Update the matching row/objective below with status and evidence.
5. Run the required checks or record a blocker.
6. Mark the task `done` only when the objective's criteria are met; otherwise mark `blocked` with a precise blocker.
7. Commit the task changes and queue/tracker update together when possible.

## Objective definitions

### fully-specified

Goal: prove or repair the app-description contract so a developer/generator can implement and review the surface without inventing product meaning.

Evidence must cover: surface identity, owner functional agent, placement, purpose, payload schema, redaction, data source, actions/events, governed capability/tool mapping, authority/AuthContext/tenant rules, audit/work trace, UI states, accessibility/responsive expectations, style/catalog binding, and tests. If the description is already sufficient, record the evidence path and mark `done`. If not, update app-description or mark `blocked` with the missing decision.

### fully-implemented

Goal: prove or implement the real runtime path for the surface at the stated scope.

Evidence must cover: browser surface/action or non-UI trigger, frontend component/client path, protected API/workstream endpoint path, backend Akka component/service/substrate path, authorization and tenant scoping, side effects/projections, typed result/system-message handling, audit/work trace, provider configured or fail-closed behavior, and no normal fixture/demo/mock runtime path. Implementation may be marked `done` only when runtime code exists and aligns with the app-description objective.

### fully-tested

Goal: prove the implemented surface through tests and/or recorded manual/API/browser smoke evidence.

Evidence must cover: success path, validation/error path, forbidden/denied/hidden path where applicable, tenant/customer isolation, stale/conflict/idempotency where applicable, audit/work trace/correlation, provider fail-closed behavior, frontend secret boundary, and local commands/manual-smoke results. Unit, service, contract, typecheck, and build checks support but do not alone prove user-visible runtime readiness.

## Status values

- `pending` — not yet assessed or completed.
- `in-progress` — currently being worked.
- `blocked` — missing prerequisite, decision, code, provider config, seed data, or validation path.
- `done` — objective complete with evidence.
- `deferred` — intentionally postponed.
- `superseded` — replaced by newer intent/spec.

## Tracker

## My Account

### `surface-my-account-dashboard`

- Workstream: My Account
- Type: `dashboard`
- Contract: `my_account.personal_command_center.v1`
- Purpose: Personal command center for attention, authority, settings, notifications, and digest/export work.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | done | App-description contract verified and completed in `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`: includes surface identity/owner/placement/purpose, primary `attentionCounters[]` and secondary `controlPanels[]` payload schema, forbidden payload/redaction boundaries, governed action-to-capability mappings, selected AuthContext/tenant authorization rules, trace/audit contract, UI states, accessibility/responsive expectations, style/catalog bindings, and acceptance/security/observability regression coverage. Sufficiency review says the dashboard is implementable without invented fields/actions/states/auth/traces/tests. | 2026-06-16 |
| fully-implemented | done | Runtime implementation verified for `surface-my-account-dashboard`: protected `/api/workstream/surfaces/{surfaceId}` and `/api/workstream/actions` in `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java` resolve JWT identity plus `X-Selected-Context-Id`; `WorkstreamService#myAccountDashboardSurface` returns the backend-owned `my_account.personal_command_center.v1` envelope with `attentionCounters`, `controlPanels`, selected AuthContext/account context, redaction, trace refs, notification and digest summaries, and governed action edges; `MyAccountService` enforces `my_account.view_summary`, `my_account.view_context`, `my_account.list_personal_attention`, and `my_account.open_authorized_workstream`; the React `DashboardSurface` renders the My Account command center from the backend surface envelope and routes counter/control-panel actions through `SurfaceActionBar`/capability action requests rather than frontend-only authority. Existing tests prove backend retrieval, safe workstream open/denial, trace/context data, and frontend typed rendering. Checks passed: focused Maven `WorkstreamServiceTest` invocation (1 My Account runtime test executed), `npm --prefix frontend test -- --run src/workstream-my-account-vertical.contract.test.mjs` (154 node contract tests passed), focused `rg` evidence for surface id/contract/counters/actions/auth/traces, and `git diff --check`. | 2026-06-16 |
| fully-tested | done | Added and passed `src/test/java/ai/first/application/coreapp/workstream/MyAccountBrowserWorkstreamSmokeTest.java`, a TestKit/httpClient hosted-shell and protected workstream API smoke that loads `/ui`, bootstraps `/api/workstream/bootstrap` with JWT and `X-Selected-Context-Id`, fetches `surface-my-account-dashboard`, verifies backend-owned `my_account.personal_command_center.v1` payload/actions/trace/correlation/secret-boundary evidence, starts the personal digest action and observes fail-closed `surface-my-account-personal-attention-digest-blocked` with `noFakeSuccess`/`noDirectMutation`, opens an authorized sibling workstream, verifies regular-member hidden-workstream denial as `not_found_or_redacted`, and confirms missing bearer access is rejected. Supporting frontend contract/typecheck evidence: `npm --prefix frontend test -- --run src/workstream-my-account-vertical.contract.test.mjs` and `npm --prefix frontend run typecheck`. Runtime readiness level: `runtime-ready` for the dashboard testing scope via Akka-hosted UI shell + protected API/action path. | 2026-06-16 |

### `surface-my-profile`

- Workstream: My Account
- Type: `detail-edit`
- Contract: `my_account.profile.self_service.v1`
- Purpose: Browser-safe identity/profile self-service with clear immutable/provider-backed fields.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-my-settings`

- Workstream: My Account
- Type: `detail-edit`
- Contract: `my_account.preferences.self_service.v1`
- Purpose: Personal preferences, named theme selection, locale/timezone, and preference save state.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-my-context`

- Workstream: My Account
- Type: `detail-edit / authority panel`
- Contract: `my_account.context_authority.v1`
- Purpose: Selected AuthContext, active membership, role/capability basis, and context-switch targets.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-my-account-notification-center`

- Workstream: My Account
- Type: `notification-center`
- Contract: `my_account.notification_center.v1`
- Purpose: Personal in-app triage for authorized notifications.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-my-account-personal-attention-digest-progress`

- Workstream: My Account
- Type: `workflow-status`
- Contract: `my_account.personal_attention_digest.progress.v1`
- Purpose: Autonomous personal briefing/digest task progress.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-my-account-personal-attention-digest-result`

- Workstream: My Account
- Type: `outcome-panel`
- Contract: `my_account.personal_attention_digest.result.v1`
- Purpose: Advisory digest/export result review with evidence, omissions, and accept/reject actions.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-my-account-personal-attention-digest-blocked`

- Workstream: My Account
- Type: `system-message`
- Contract: `my_account.personal_attention_digest.blocked.v1`
- Purpose: Provider/runtime fail-closed explanation and recovery.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-my-account-open-denied`

- Workstream: My Account
- Type: `system-message`
- Contract: `my_account.open_denied.v1`
- Purpose: Safe not-found/redacted/unavailable workstream recovery.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

## User Admin

### `surface-user-admin-dashboard`

- Workstream: User Admin
- Type: `dashboard`
- Contract: `user_admin.dashboard.v1`
- Purpose: Attention-first User Admin command center for SaaS Owner Admin, Organization, Organization Admin, directory, invitation, role, support, review, provider, and audit health.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-saas-owner-admins`

- Workstream: User Admin
- Type: `list-search`
- Contract: `user_admin.saas_owner_admins.v1`
- Purpose: SaaS Owner scoped directory for app-owner/admin users and invitations.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-saas-owner-admin-invitation-create`

- Workstream: User Admin
- Type: `create-form`
- Contract: `user_admin.saas_owner_admin_invitation_create.v1`
- Purpose: Invitation form for another SaaS Owner Admin with role validation, idempotency, outbox boundary, and audit.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-users`

- Workstream: User Admin
- Type: `list-search`
- Contract: `user_admin.users.v1`
- Purpose: Scoped searchable directory for users/memberships.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-user-detail`

- Workstream: User Admin
- Type: `show-inspection`
- Contract: `user_admin.user_detail.v1`
- Purpose: Scoped account, membership, invitation, support-access, access-review, identity, and audit inspection.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-invitation-create`

- Workstream: User Admin
- Type: `create-form`
- Contract: `user_admin.invitation_create.v1`
- Purpose: Single-purpose invitation creation form.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-invitation-detail`

- Workstream: User Admin
- Type: `show-inspection / workflow-status`
- Contract: `user_admin.invitation_detail.v1`
- Purpose: Lifecycle-aware invitation inspection.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-invitation-resend-confirmation`

- Workstream: User Admin
- Type: `lifecycle-confirmation`
- Contract: `user_admin.invitation_resend_confirmation.v1`
- Purpose: Single-purpose resend confirmation.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-invitation-revoke-confirmation`

- Workstream: User Admin
- Type: `destructive-lifecycle-confirmation`
- Contract: `user_admin.invitation_revoke_confirmation.v1`
- Purpose: Single-purpose invitation revoke confirmation.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-membership-status-confirmation`

- Workstream: User Admin
- Type: `destructive-lifecycle-confirmation`
- Contract: `user_admin.membership_status_confirmation.v1`
- Purpose: Disable/suspend/reactivate/remove membership or account confirmation.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-role-change-preview`

- Workstream: User Admin
- Type: `decision-card / diff`
- Contract: `user_admin.role_change_preview.v1`
- Purpose: Capability delta and approval preview before role mutation.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-support-access-grant`

- Workstream: User Admin
- Type: `create-form`
- Contract: `user_admin.support_access_grant.v1`
- Purpose: Support-access grant/extend form.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-support-access-revoke-confirmation`

- Workstream: User Admin
- Type: `destructive-lifecycle-confirmation`
- Contract: `user_admin.support_access_revoke_confirmation.v1`
- Purpose: Support-access revoke confirmation.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-access-review-task`

- Workstream: User Admin
- Type: `workflow-status / outcome-panel`
- Contract: `user_admin.access_review_task.v1`
- Purpose: Durable access-review task progress, result, blockers, and human review.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-identity-exception-review`

- Workstream: User Admin
- Type: `decision-card / workflow-status`
- Contract: `user_admin.identity_exception_review.v1`
- Purpose: Identity-link/relink exception review and approved recovery routing.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-directory`

- Workstream: User Admin
- Type: `list-search`
- Contract: `user_admin.organization_directory.v1`
- Purpose: SaaS Owner Organization directory.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-detail`

- Workstream: User Admin
- Type: `show-inspection`
- Contract: `user_admin.organization_detail.v1`
- Purpose: Lifecycle-aware Organization inspection.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-admins`

- Workstream: User Admin
- Type: `list-search`
- Contract: `user_admin.organization_admins.v1`
- Purpose: Directory of Organization Admin users/invitations for one selected Organization/Tenant.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-admin-invitation-create`

- Workstream: User Admin
- Type: `create-form`
- Contract: `user_admin.organization_admin_invitation_create.v1`
- Purpose: Bootstrap/invite form for a TENANT_ADMIN.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-admin-detail`

- Workstream: User Admin
- Type: `show-inspection`
- Contract: `user_admin.organization_admin_detail.v1`
- Purpose: Shows one Organization Admin membership/invitation.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-create`

- Workstream: User Admin
- Type: `create-form`
- Contract: `user_admin.organization_create.v1`
- Purpose: Organization creation form.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-rename`

- Workstream: User Admin
- Type: `edit-form`
- Contract: `user_admin.organization_rename.v1`
- Purpose: Organization display-name edit surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-suspend-confirmation`

- Workstream: User Admin
- Type: `destructive-lifecycle-confirmation`
- Contract: `user_admin.organization_suspend_confirmation.v1`
- Purpose: Organization suspension confirmation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-reactivate-confirmation`

- Workstream: User Admin
- Type: `lifecycle-confirmation`
- Contract: `user_admin.organization_reactivate_confirmation.v1`
- Purpose: Organization reactivation confirmation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-directory`

- Workstream: User Admin
- Type: `list-search`
- Contract: `user_admin.customer_directory.v1`
- Purpose: Organization Admin Customer directory.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-detail`

- Workstream: User Admin
- Type: `show-inspection`
- Contract: `user_admin.customer_detail.v1`
- Purpose: Lifecycle-aware Customer inspection surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-admins`

- Workstream: User Admin
- Type: `list-search`
- Contract: `user_admin.customer_admins.v1`
- Purpose: Customer Admin users/invitations for one selected Customer.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-admin-invitation-create`

- Workstream: User Admin
- Type: `create-form`
- Contract: `user_admin.customer_admin_invitation_create.v1`
- Purpose: Bootstrap/invite form for a CUSTOMER_ADMIN.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-admin-detail`

- Workstream: User Admin
- Type: `show-inspection`
- Contract: `user_admin.customer_admin_detail.v1`
- Purpose: Shows one Customer Admin membership/invitation.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-create`

- Workstream: User Admin
- Type: `create-form`
- Contract: `user_admin.customer_create.v1`
- Purpose: Customer creation form.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-rename`

- Workstream: User Admin
- Type: `edit-form`
- Contract: `user_admin.customer_rename.v1`
- Purpose: Customer display-name/profile edit surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-suspend-confirmation`

- Workstream: User Admin
- Type: `destructive-lifecycle-confirmation`
- Contract: `user_admin.customer_suspend_confirmation.v1`
- Purpose: Customer suspension/archive confirmation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-reactivate-confirmation`

- Workstream: User Admin
- Type: `lifecycle-confirmation`
- Contract: `user_admin.customer_reactivate_confirmation.v1`
- Purpose: Customer reactivation confirmation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-system-message`

- Workstream: User Admin
- Type: `system-message`
- Contract: `user_admin.system_message.v1`
- Purpose: Safe denial, validation, provider/outbox/model blocked, stale, conflict, and no-op recovery.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

## Agent Admin

### `surface-agent-admin-dashboard`

- Workstream: Agent Admin
- Type: `dashboard`
- Contract: `agent_admin.dashboard.v1`
- Purpose: Agent Admin command center.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-admin-catalog`

- Workstream: Agent Admin
- Type: `list-search`
- Contract: `agent_admin.catalog.v1`
- Purpose: Managed agent catalog.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-admin-detail`

- Workstream: Agent Admin
- Type: `show-inspection`
- Contract: `agent_admin.detail.v1`
- Purpose: Agent readiness/behavior inspection.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-prompt-governance`

- Workstream: Agent Admin
- Type: `governance-diff / show-inspection`
- Contract: `agent_admin.prompt_governance.v1`
- Purpose: Prompt governance and behavior artifact review.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-skill-manifest-diff`

- Workstream: Agent Admin
- Type: `governance-diff / show-inspection`
- Contract: `agent_admin.skill_manifest_diff.v1`
- Purpose: Skill manifest diff/review.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-tool-boundary-diff`

- Workstream: Agent Admin
- Type: `governance-diff / show-inspection`
- Contract: `agent_admin.tool_boundary_diff.v1`
- Purpose: Tool-boundary simulation and review.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-model-refs`

- Workstream: Agent Admin
- Type: `governance-diff / show-inspection`
- Contract: `agent_admin.model_refs.v1`
- Purpose: Model reference proposal/review.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-test-console`

- Workstream: Agent Admin
- Type: `workflow-status`
- Contract: `agent_admin.test_console.v1`
- Purpose: No-side-effect runtime test surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-activation-confirmation`

- Workstream: Agent Admin
- Type: `lifecycle-confirmation`
- Contract: `agent_admin.activation_confirmation.v1`
- Purpose: Activation confirmation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-deactivation-confirmation`

- Workstream: Agent Admin
- Type: `lifecycle-confirmation`
- Contract: `agent_admin.deactivation_confirmation.v1`
- Purpose: Deactivation confirmation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-rollback-confirmation`

- Workstream: Agent Admin
- Type: `lifecycle-confirmation`
- Contract: `agent_admin.rollback_confirmation.v1`
- Purpose: Rollback confirmation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-behavior-proposal`

- Workstream: Agent Admin
- Type: `decision-card / decision`
- Contract: `agent_admin.behavior_proposal.v1`
- Purpose: Behavior proposal decision card.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-admin-prompt-risk-review`

- Workstream: Agent Admin
- Type: `workflow-status`
- Contract: `agent_admin.prompt_risk_review.v1`
- Purpose: Prompt-risk autonomous review result.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-admin-trace`

- Workstream: Agent Admin
- Type: `audit-timeline`
- Contract: `agent_admin.trace.v1`
- Purpose: Agent Admin trace timeline.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-seed-material`

- Workstream: Agent Admin
- Type: `list-search / workflow-status`
- Contract: `agent_admin.seed_material.v1`
- Purpose: Seed material discovery/import workflow surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

## Audit Trace

### `surface-audit-trace-dashboard`

- Workstream: Audit Trace
- Type: `dashboard`
- Contract: `audit.trace.dashboard.v1`
- Purpose: Investigation command center.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-search`

- Workstream: Audit Trace
- Type: `list-search`
- Contract: `audit.trace.search.v1`
- Purpose: Scoped trace search.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-detail`

- Workstream: Audit Trace
- Type: `detail-edit as read-only evidence`
- Contract: `audit.trace.detail.v1`
- Purpose: Browser-safe trace/event evidence detail.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-timeline`

- Workstream: Audit Trace
- Type: `audit-timeline`
- Contract: `audit.trace.timeline.v1`
- Purpose: Correlation timeline.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-failure-evidence`

- Workstream: Audit Trace
- Type: `detail-edit as read-only evidence`
- Contract: `audit.trace.failureEvidence.v1`
- Purpose: Denial/provider/tool/model/runtime failure evidence.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-investigation-guide`

- Workstream: Audit Trace
- Type: `decision-card`
- Contract: `audit.trace.investigationGuide.v1`
- Purpose: Human investigation guidance.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-export-request`

- Workstream: Audit Trace
- Type: `decision-card`
- Contract: `audit.trace.exportRequest.v1`
- Purpose: Policy-gated scoped redacted export request.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-investigation-note`

- Workstream: Audit Trace
- Type: `system-message`
- Contract: `audit.trace.investigationNote.v1`
- Purpose: Human investigation note append result.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-summary-progress`

- Workstream: Audit Trace
- Type: `workflow-status`
- Contract: `audit.trace.summaryProgress.v1`
- Purpose: Audit summary worker progress or fail-closed blocker.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-summary-review`

- Workstream: Audit Trace
- Type: `decision-card`
- Contract: `audit.trace.summaryReview.v1`
- Purpose: Human review of redacted advisory summary.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

## Governance Policy

### `surface-governance-policy-dashboard`

- Workstream: Governance Policy
- Type: `dashboard`
- Contract: `governance.policy.dashboard.v1`
- Purpose: Governance/Policy dashboard.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-inventory`

- Workstream: Governance Policy
- Type: `list-search`
- Contract: `governance.policy.inventory.v1`
- Purpose: Policy/proposal inventory and queue.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-proposal`

- Workstream: Governance Policy
- Type: `governance-diff`
- Contract: `governance.policy.proposal.v1`
- Purpose: Policy proposal lifecycle/diff surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-simulation`

- Workstream: Governance Policy
- Type: `governance-diff`
- Contract: `governance.policy.simulation.v1`
- Purpose: Advisory simulation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-decision`

- Workstream: Governance Policy
- Type: `decision-card`
- Contract: `governance.policy.decision.v1`
- Purpose: Policy decision/activation/rollback card.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-outcome`

- Workstream: Governance Policy
- Type: `outcome-panel`
- Contract: `governance.policy.outcome.v1`
- Purpose: Policy outcome notes/metrics/evidence.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-impact-analysis-task`

- Workstream: Governance Policy
- Type: `workflow-status`
- Contract: `governance.policy.impact_analysis.task.v1`
- Purpose: Impact-analysis task progress/status.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-impact-analysis-result`

- Workstream: Governance Policy
- Type: `decision-card`
- Contract: `governance.policy.impact_analysis.result.v1`
- Purpose: Impact-analysis advisory result review.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-system-message`

- Workstream: Governance Policy
- Type: `system-message`
- Contract: `governance.policy.system_message.v1`
- Purpose: Governance/Policy safe system message.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |
