# User Admin Vertical Contracts

## Purpose

This document turns the Full-Core SMB User Admin child plan into implementation-ready vertical slices for the AI-first SaaS starter. It is a planning contract for future source-edit tasks, not a claim that runtime behavior is already implemented.

## Vertical slice order

1. **Directory and invitation dashboard foundation**: backend capability contracts, typed overview/member/invitation surfaces, deterministic invitation and membership projections, audit/trace fields, and local API/UI validation.
2. **Member status and role-change actions**: disable/reactivate and role/capability preview/change with last-admin, self-disable, disabled-user, tenant/customer, policy, no-op, and idempotency guardrails.
3. **UserAdminAgent request/response guidance**: governed Akka `Agent` runtime invocation for summaries, blocked-invitation explanations, role-change guidance, and provider fail-closed surfaces/traces.
4. **Access review worker candidate**: `AutonomousAgent` access-review investigation only after deterministic directory, role, status, trace, and dashboard foundations exist.

The first source-edit task should implement slice 1 only.

## Capability inventory

| Capability id | Type | Required authority and scope | Deterministic responsibility | Surface/API exposure |
|---|---|---|---|---|
| `user_admin.view_overview` | protected read | active account, non-disabled user, selected AuthContext, tenant/customer membership, role/capability allowing User Admin | tenant filtering, disabled-user denial, attention counts, invitation/member/role/recent-change projection, trace redaction | dashboard load, shell open, UserAdminAgent evidence read |
| `user_admin.list_members` | protected read | same as overview plus directory-read capability | scoped member query, status/role/capability summaries, pagination/filter validation, no hidden tenant/customer leakage | member directory surface, member picker, access-review evidence |
| `user_admin.invite_user` | command | User Admin write authority for selected tenant/customer; invite target must be valid for SMB role set | email normalization, duplicate/open-invite check, role validation, idempotency key, outbox/Resend enqueue, audit event | invitation form action, UserAdminAgent guided action proposal only |
| `user_admin.resend_invitation` | command | write authority; invitation belongs to selected tenant/customer and is resendable | resend eligibility, idempotency, outbox enqueue, status update, audit | invitation attention/action surface |
| `user_admin.revoke_invitation` | command | write authority; invitation belongs to selected tenant/customer and is open/revokable | no-op handling, revoke state transition, audit | invitation detail/action surface |
| `user_admin.acceptance_status.read` | protected read | directory-read authority for selected tenant/customer | invitation expiry, accepted, failed-delivery, bounced, pending, and recovery evidence shaping | invitation panel and dashboard attention |
| `user_admin.update_member_status` | command | User Admin write authority; actor cannot bypass own authority or tenant/customer scope | disable/reactivate validation, last-admin and self-disable guardrails, disabled-user effects, no-op/idempotency, audit | member detail confirmation surface |
| `user_admin.preview_role_change` | protected read/proposal | role-management authority; target member in selected tenant/customer | role/capability diff, policy and approval requirement preview, last-admin impact, audit read trace | role-change preview surface |
| `user_admin.change_member_roles` | command | role-management authority and any required policy approval | role mutation, approval gate, idempotency, last-admin preservation, audit | role-change commit action |
| `user_admin.ask_agent` | request/response agent turn | workstream access plus scoped evidence-read capability for any cited data | provider config check, prompt/skill/reference/tool boundary assembly, scoped tool calls, prompt/model/tool traces, fail-closed system message | persistent composer and dashboard ask actions |
| `user_admin.access_review.start` | internal worker start | access-review authority; deterministic foundations present | start task record, scope validation, idempotency, provider readiness gate | access-review queue start action |
| `user_admin.access_review.read` | internal worker read | access-review read authority for task scope | task progress/result shaping, redaction, trace links | access-review queue/progress surface |
| `user_admin.access_review.cancel` | internal worker command | access-review manage authority | lifecycle transition, cancellation trace, idempotency | task progress action |
| `user_admin.access_review.accept_result` | human decision | access-review manage authority; result completed | result acceptance trace; any membership/role changes must route through deterministic User Admin capabilities | review result action |
| `user_admin.access_review.reject_result` | human decision | access-review manage authority | rejection reason validation, audit, no state mutation beyond result lifecycle | review result action |

## Authority and denial rules

- Every capability checks selected `AuthContext`, account status, non-disabled user status, tenant/customer membership, role/capability grants, and resource tenant/customer ownership.
- Disabled actors receive a typed `system_message` denial before protected reads or writes reveal User Admin data.
- Users without User Admin workstream authority do not see the rail item; direct shell/API/deep-link requests still receive backend denials with trace ids.
- Invitation, member, role, and access-review resources must never leak across tenant/customer boundaries, including counts, names, emails, role names, trace links, and denial wording.
- Last-admin protection applies to disable, role removal, and any action that would remove the final effective admin for the selected tenant/customer.
- Self-disable and self-admin-role-removal are denied unless a later explicit policy creates a safe handoff flow; no such flow is in this SMB first slice.
- UI action visibility is advisory only; backend denial remains authoritative for manually submitted requests.

## Structured surfaces and actions

### `user_admin.dashboard.v1`

Purpose: attention-first User Admin workstream home.

Required payload groups:
- readiness/status summary: directory health, invitation health, role/capability coverage, provider/outbox readiness;
- attention cards: failed invitation delivery, expired/stale invitations, users needing review, recent admin changes, access-review tasks needing human review;
- authorized next actions: invite user, view failed invitations, open member directory, start access review only when foundations exist;
- recent consequential activity with trace ids;
- first-run and empty states that teach the SMB owner/operator how to invite the first team member safely.

Actions map to `user_admin.view_overview`, `user_admin.invite_user`, `user_admin.list_members`, `user_admin.acceptance_status.read`, `user_admin.access_review.start`, and `audit.trace.detail.read` where authorized.

### `user_admin.member_directory.v1`

Purpose: scoped member table/detail surface, not a generic CRUD page.

Fields include member id, display name, email, account status, membership status, role/capability summary, invitation status where applicable, last activity/change evidence, attention flags, redaction state, and trace refs.

Actions map to `user_admin.list_members`, `user_admin.update_member_status`, `user_admin.preview_role_change`, `user_admin.change_member_roles`, and trace-open requests.

### `user_admin.invitation_panel.v1`

Purpose: invite/resend/revoke/acceptance and delivery visibility.

Fields include invite id, normalized email, requested role, status, expiry, accepted timestamp, outbox delivery state, provider failure summary, idempotency/correlation refs, and trace refs.

Actions map to `user_admin.invite_user`, `user_admin.resend_invitation`, `user_admin.revoke_invitation`, `user_admin.acceptance_status.read`, and trace-open requests.

### `user_admin.role_change_preview.v1`

Purpose: show capability delta, last-admin impact, policy gate, affected workstreams, and audit consequence before mutation.

Actions map to `user_admin.preview_role_change`, `user_admin.change_member_roles`, approval/open-governance requests where later introduced, and trace-open requests.

### `user_admin.access_review_task.v1`

Purpose: durable worker progress/result surface for access-review investigation once queued.

Fields include task id, status, initiating capability, selected AuthContext, tenant/customer scope, progress, blockers, evidence references, recommendations, risk/confidence, result review state, provider failures, and trace ids.

Actions map to access-review start/read/cancel/accept/reject capabilities. Worker output cannot directly mutate memberships or roles.

### `system_message` requirements

Use typed system-message surfaces for forbidden access, missing context, disabled actor, validation failure, last-admin protection, self-disable denial, no-op mutation, stale/outbox/provider failure, agent provider missing, worker provider blocked, tenant isolation denial, and recovery guidance.

## Deterministic services

Future source tasks should keep these responsibilities out of the model-backed UserAdminAgent and out of worker output:

- `UserAdminAuthorizationService`: capability check, selected AuthContext, membership, disabled-user, role/capability, tenant/customer scope, last-admin policy, support-access visibility where in scope.
- `UserAdminInvitationService`: invite/resend/revoke state transitions, normalized email validation, duplicate/open-invite handling, expiry, idempotency, outbox enqueue/status shaping, audit events.
- `UserAdminMembershipService`: member directory projection inputs, status transitions, self-disable/last-admin guardrails, reactivation validation, no-op/idempotent results, audit events.
- `UserAdminRoleService`: SMB role/capability matrix, preview delta, policy/approval requirement calculation, last-admin preservation, mutation idempotency, audit events.
- `UserAdminProjectionService`: overview, directory, invitation, access-review attention, recent-change, and trace-link browser DTOs.
- `UserAdminTraceService`: trace normalization, correlation ids, redaction, denial/event/result recording, audit-work trace links.
- `ProviderReadinessService`: provider/outbox/model readiness diagnostics that fail closed with actionable system messages without exposing secrets.

## UserAdminAgent boundary

`UserAdminAgent` is request/response guidance through the governed Akka `Agent` runtime path.

Allowed behavior:
- explain why invitations are blocked or failing using authorized evidence;
- summarize access risk, stale invites, recent role/status changes, or access-review results;
- help draft safe invite, disable/reactivate, or role-change next steps;
- recommend that an admin open a deterministic surface/action;
- return structured surfaces or sanitized markdown with trace ids.

Forbidden behavior:
- no direct membership, role, invitation, authorization, tenant filtering, policy, outbox, idempotency, or audit mutation;
- no deterministic/model-less normal response standing in for the Akka Agent runtime;
- no direct provider calls bypassing governed prompt/skill/reference/tool-boundary assembly;
- no hidden cross-tenant or unauthorized evidence in prompts or responses.

Missing provider/model configuration must return a blocked `system_message` surface and trace provider-readiness failure. It must not return a canned successful guidance response.

## Access-review worker contract

The access-review investigation worker is justified after directory/invitation/status/role/trace foundations exist because it needs durable task lifecycle, progress, evidence collection, model-assisted summarization, human result review, and possible cancellation/retry.

Worker capabilities:
- `user_admin.access_review.start`
- `user_admin.access_review.read`
- `user_admin.access_review.cancel`
- `user_admin.access_review.accept_result`
- `user_admin.access_review.reject_result`

Worker evidence may include scoped member summaries, role/capability summaries, inactive/stale accounts, invitation failures, recent admin changes, denial traces, and role-change preview evidence.

Worker results may recommend follow-up, but actual disable/reactivate, invite revoke/resend, and role changes must execute through deterministic User Admin capabilities with human authorization.

## Audit and work traces

Every protected read, denial, command, no-op, validation error, provider failure, outbox event, agent prompt assembly, skill/reference load, tool call, model call, worker lifecycle event, worker result decision, and trace-open action needs durable trace data.

Minimum trace fields:
- trace id and correlation/request id;
- actor account id and selected AuthContext;
- tenant/customer scope;
- capability id;
- resource id/type where safe;
- outcome: allowed, denied, validation_error, no_op, queued, completed, failed, provider_blocked;
- denial or validation reason code;
- deterministic service or agent/worker component name;
- prompt/skill/reference/model/tool ids for model-backed turns;
- redaction state and evidence references safe for the browser.

## First implementation slice: directory and invitation dashboard foundation

### Objective

Implement the minimum vertical runtime path that opens User Admin, loads an authorized dashboard, displays scoped member/invitation attention, and supports invitation read/send/resend/revoke surfaces with deterministic backend behavior and traces.

### Bounded source areas to discover/edit

- `templates/ai-first-saas-starter/` backend User Admin/domain/API/surface files.
- `templates/ai-first-saas-starter/` frontend workstream shell/surface renderer files.
- root `frontend/` only if starter frontend synchronization is required by repository convention.
- targeted tests for backend authorization/invitation behavior, surface rendering, and secret-boundary/runtime checks.

### Acceptance

- User Admin rail/workstream open is authorized by backend state and produces a typed dashboard or typed denial.
- Dashboard displays backend-derived invitation/member/role/recent-change/attention data, with honest first-run and empty states.
- Invitation panel supports invite, resend, revoke, delivery/expiry/acceptance status, validation errors, idempotency/no-op, outbox failure surfaces, and trace links at the stated implemented scope.
- Tenant isolation, disabled-user, missing capability, invalid input, duplicate/open invite, stale invite, and provider/outbox failure paths are tested.
- Local API/UI path works at the stated scope; broad starter changes use `tools/validate-ai-first-saas-starter-fullstack.sh` when feasible.

## Runtime validation map

For every source-edit task, name exact commands after source discovery. The expected validation categories are:

- backend unit/integration tests for authorization, tenant isolation, disabled-user denial, invitation lifecycle, idempotency/no-op, audit/trace emission, and browser DTO redaction;
- frontend tests for `user_admin.dashboard.v1`, `user_admin.member_directory.v1`, `user_admin.invitation_panel.v1`, `system_message` states, accessibility labels, keyboard/focus basics, and responsive stacking;
- API/UI smoke through the local Akka starter path for the implemented User Admin slice;
- provider/outbox missing-configuration smoke that proves fail-closed surfaces/traces, not canned success;
- frontend secret-boundary scan and build/typecheck for touched UI code;
- `tools/validate-ai-first-saas-starter-fullstack.sh` for broad starter behavior changes or when the task explicitly changes generated fullstack runtime.

## Follow-up implementation tasks to append

1. Inspect current starter source boundaries and implement the directory/invitation dashboard foundation as a bounded vertical slice.
2. Implement member disable/reactivate and role-change preview/change after the directory/invitation foundation is validated.
3. Implement `UserAdminAgent` request/response guidance only after deterministic evidence reads and provider fail-closed traces exist.
4. Implement access-review worker only after deterministic User Admin capabilities, trace substrate, and typed dashboard/task surfaces are validated.
