# Capability: User and access administration

## Purpose

Let authorized administrators manage users, memberships, roles/capabilities, invitations, support access, access review, identity relink review, and admin audit evidence within selected tenant/customer scope without weakening isolation, approval policy, provider boundaries, or traceability.

## Actors and scope

- Tenant admins manage tenant employees and tenant-owned customer administration within authorized tenant/customer scope.
- Customer admins manage customer users, invitations, customer roles, and customer access-review evidence within their customer scope.
- Auditors read scoped evidence without mutating access.
- SaaS Owner support roles may inspect or explain tenant/customer user data only through active tenant-created support access.
- User Admin functional agent may recommend, draft, summarize, or prepare decision cards but cannot expand authority or mutate access without backend capability authorization and human/approval gates.

## Capability inventory

| Capability id | Type | Responsibility | Surface/API exposure |
|---|---|---|---|
| `user_admin.view_overview` | protected read | Scoped dashboard projection, attention counts, provider/outbox/model readiness, recent admin activity, redaction. | `surface-user-admin-dashboard`; agent evidence read. |
| `user_admin.list_members` | protected read | Scoped directory, membership, invitation, support-access, and access-review rows with pagination/filter validation. | `surface-user-admin-member-directory`. |
| `user_admin.read_user_account` | protected read | Scoped account/member/invitation/support/review/audit detail by backend-authorized target. | `surface-user-admin-user-account`. |
| `user_admin.invite_user` | command | Email normalization, duplicate/open-invite check, role validation, idempotency, outbox/Resend enqueue, audit. | Invitation panel/form and dashboard actions. |
| `user_admin.resend_invitation` | command | Resend eligibility, idempotency, outbox enqueue/status update, audit. | Invitation panel and queue row actions. |
| `user_admin.revoke_invitation` | command | Open/revokable invitation transition, no-op handling, audit. | Invitation panel and queue row actions. |
| `user_admin.acceptance_status.read` | protected read | Invitation expiry, accepted, failed-delivery, bounced, pending, and recovery evidence shaping. | Dashboard, invitation panel, directory badges. |
| `user_admin.update_member_status` | command | Disable/reactivate/suspend/remove validation, self-disable and last-admin guardrails, no-op/idempotency, audit. | Directory/detail confirmation and system messages. |
| `user_admin.preview_role_change` | protected read/proposal | Role/capability diff, policy/approval requirement, affected workstreams, last-admin impact, trace evidence. | Role-change preview/decision card. |
| `user_admin.change_member_roles` | command | Role mutation after authorization/approval, idempotency, last-admin preservation, audit. | Role-change preview commit result. |
| `user_admin.support_access.read` | protected read | Scoped support grant state, expiry, purpose, visibility limits, audit evidence. | Dashboard, directory, detail. |
| `user_admin.support_access.grant_revoke_extend` | command | Grant/revoke/extend eligibility, expiry/purpose capture, approval, idempotency, audit. | Detail/action panel and decision card. |
| `user_admin.identity_relink.review` | proposal/command gated | Exceptional identity link/relink review, policy gate, audit. | User account detail and decision card. |
| `user_admin.ask_agent` | request/response agent turn | Governed prompt/skill/reference/tool assembly, scoped evidence use, provider/model readiness, fail-closed trace. | Composer, dashboard ask actions, markdown/structured surfaces. |
| `user_admin.access_review.start` | internal worker start | Scope validation, idempotent task start, provider/model readiness gate. | Access-review task surface. |
| `user_admin.access_review.read` | protected read | Task progress/result shaping, evidence refs, redaction, trace links. | Access-review task surface. |
| `user_admin.access_review.cancel` | command | Lifecycle cancellation and trace. | Access-review task surface. |
| `user_admin.access_review.accept_result` | human decision | Record acceptance of advisory result only. | Access-review result decision. |
| `user_admin.access_review.reject_result` | human decision | Reason-validated rejection of advisory result. | Access-review result decision. |
| `admin.audit.read` | protected read | Scoped admin audit and work-trace evidence reauthorization. | Audit/Trace surfaces and trace links. |

## Governed tools and exposure

- `search-user-directory` (`browser-tool`, `agent-tool` read): scoped overview, directory, detail, invitation, support, access-review, and audit evidence views.
- `create-or-resend-invitation` (`browser-tool`; agent may prepare human-confirmed payloads): invitation lifecycle and Resend/captured-outbox delivery boundary.
- `change-membership-role-or-status` (`browser-tool` with approval when risky): membership role/status preview and mutation with idempotency and audit.
- `grant-or-revoke-support-access` (`browser-tool` with expiry/purpose/approval): scoped support access changes.
- `run-access-review` (`agent-tool`, `internal-tool`): durable access-review recommendations and review decisions; no autonomous access mutation.
- `readSkill` / `readReferenceDoc` (`agent-tool` where assigned): governed User Admin expertise loaders with manifest/boundary/status/redaction checks.

## Authorization and denials

Every command/query is scoped by selected `AuthContext`, active account, non-disabled actor status, tenant/customer ids, resource ownership, membership status, role/capability grants, support-access state, and approval policy. Cross-tenant/customer access, disabled users, inactive memberships, email-only authorization, prompt-only privilege grants, hidden-target enumeration, raw token/secret access, unredacted audit export, unsupported bulk side effects, last-admin loss, self-disable/self-admin-role-removal, role escalation, and provider/model/outbox fake success are forbidden.

## Outcomes

In scope: governed user/admin operations, invitation lifecycle, member status/role changes, support-access lifecycle, access-review recommendations, decision cards for risky changes, admin audit evidence, scoped User Admin agent guidance, and safe local/test email outbox behavior.

Out of scope: public self-registration, app-specific customer billing, autonomous high-impact access changes, client-side authorization, deterministic/model-less normal agent success, and fixture/mock normal runtime data.

## Linked graph nodes

- Workstream: `../workstreams/user-admin/workstream.md`
- Surfaces: `../workstreams/user-admin/surfaces/surfaces.md`
- Agent binding: `../workstreams/user-admin/agents/functional-agent.md`
- Tools: `../workstreams/user-admin/tools/governed-tools.md`
- Tests: `../workstreams/user-admin/tests/coverage.md`
- Traces: `../workstreams/user-admin/traces/work-traces.md`
