# Requirements-to-workstream mini example

Use this compact example as the quickest installed-pack-facing pattern for turning a broad PRD fragment into the required generated-SaaS planning chain. For target architecture, use this with `../requirements-to-workstream-development-process.md` and `ai-first-saas-seed-app-description/README.md`. The purchase-request files remain a mechanics reference for conventional planning shape, not the generated SaaS target architecture.

## Input fragment

```text
Build an AI-first SaaS app where tenant admins can manage users and delegated agents can help investigate access risks, summarize pending invitations, and propose safe fixes. Admins need to know what needs their attention each day.
```

## Workstream inventory

| Workstream | Backing functional agent | Responsibility | Scope |
|---|---|---|---|
| `my_account` | My Account Agent | current user's context, profile/settings, and cross-workstream attention | mandatory core foundation |
| `user_admin` | User Admin Agent | users, memberships, invitations, access review, support access, admin audit | mandatory core foundation |
| `audit_trace` | Audit/Trace Agent | searchable audit/work evidence and investigation traces | mandatory core foundation |

Do not start by listing pages, database tables, CRUD endpoints, or Akka components. Those appear only after surfaces and capabilities are clear.

## Attention and dashboard breakdown

| Workstream | Attention category | Dashboard/default surface | Left rail / My Account behavior |
|---|---|---|---|
| `my_account` | personal action, forbidden-context recovery, profile/settings issue | `surface.my_account.dashboard.v1` | aggregates the current user's attention across visible workstreams |
| `user_admin` | pending invitation, failed delivery, disabled account, access-review risk, last-admin risk | `surface.user_admin.dashboard.v1` | count reflects authorized unresolved user-admin attention items |
| `audit_trace` | sensitive-read anomaly, denied action spike, export approval, trace investigation result | `surface.audit.dashboard.v1` | count reflects audit items the current user may inspect or approve |

## Surfaces and actions

| Surface/action | Governed capability/API | Notes |
|---|---|---|
| `action.user_admin.refresh_dashboard` | `user_admin.dashboard.summary` | read-only dashboard query with AuthContext and tenant/customer scope |
| `action.user_admin.open_invitation_queue` | `user_admin.invitations.search` | surface-request action; opens filtered list from dashboard queue |
| `action.user_admin.resend_invitation` | `user_admin.invitations.resend` | command with idempotency key, permission check, Resend/captured-outbox path, audit |
| `action.user_admin.start_access_review` | `user_admin.access_review.start` | starts deterministic review workflow and may launch internal investigation task |
| `action.audit.open_trace` | `audit.traces.view` | redacted evidence query with sensitive-read audit when required |

Every browser button, workstream-agent tool, and system-message suggestion maps to one of these capabilities or to a declared follow-up capability.

## Akka substrate selection after capability contracts

| Capability / need | Selected substrate |
|---|---|
| invitation lifecycle with delivery, expiry, resend/revoke, acceptance | Event Sourced Entity + Workflow + Consumer + Timed Action |
| dashboard, queue, and My Account attention reads | Views exposed by HTTP endpoints |
| immediate admin explanation or guided search | request-based Akka `Agent` for the User Admin workstream |
| durable access-risk investigation, evidence gathering, or periodic review summary | Akka `AutonomousAgent` task candidate when typed lifecycle, progress snapshots, result review, notification, cancellation, or handoff is required |
| risky resolution or last-admin protection | Workflow approval/decision card plus audit trace |

Request-based `Agent` remains the default for immediate user-facing workstream turns. `AutonomousAgent` is evaluated for durable internal/background model-driven work; it does not bypass capability authority.

## Events, notifications, projections, and traces

- Invitation commands emit invitation lifecycle events, delivery outbox events, AdminAuditEvent records, and dashboard/attention projection updates.
- Access-review workflow emits review-started, evidence-collected, decision-required, resolved, and expired/escalated events.
- AutonomousAgent task snapshots/results feed progress surfaces and attention only when blocked, failed, rejected, risky, or decision-producing.
- My Account and left rail counts come from governed backend projections, not frontend-only badge logic.
- AgentWorkTrace, PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, ToolPermissionBoundary decisions, and AdminAuditEvent links are part of done criteria.

## Implementation-task shape

A fresh-session task generated from this example should preserve at least:

```text
workstream: user_admin
attention category: failed invitation delivery
surface/action: surface.user_admin.dashboard.v1 / action.user_admin.resend_invitation
capability id: user_admin.invitations.resend
AuthContext/scope: Tenant Admin in selected tenant; deny disabled users and cross-tenant targets
substrate: Invitation ESE + InvitationWorkflow + Resend outbox Consumer + InvitationView + HTTP endpoint + React surface action
events/projections/traces: InvitationResendRequested, EmailDeliveryQueued/Failed/Sent, dashboard attention update, AdminAuditEvent
tests/local validation: idempotent resend, forbidden tenant, captured local outbox, dashboard count update, audit trace link, UI action smoke
```

If a generated task lacks the workstream, attention/dashboard, surface action, capability id, substrate, events/projections, traces, and validation path, repair the task before implementation.
