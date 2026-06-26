# Capability: Account context and profile

## Purpose

Let an authenticated member inspect and maintain browser-safe account, profile, settings, selected `AuthContext`, memberships, attention summary, and personal notification/digest preferences for the SaaS Foundation App.

## Actors and scope

- Primary actor: authenticated member in an active membership.
- Supporting callers: signed-in member human worker, My Account functional-agent worker, deterministic My Account system worker, and workstream shell.
- Scope: selected tenant/customer context from backend-owned authorization state.

## Governed tools and exposure

- `read-current-account-context` (`browser-tool`, `agent-tool`): returns `/api/me`-safe account, profile, settings, memberships, capabilities, and selected context.
- `update-own-profile-settings` / `my_account.update_profile_settings` (`browser-tool`, bounded `human_chat_tool_plan`; functional-agent proposal only): updates user-experience profile/settings only; it never grants permissions. The namespaced id is used by current surface/chat-plan mappings while the legacy id remains linked for older realization evidence.
- `request-personal-digest-export` (`browser-tool`, `agent-tool`): starts, reads, cancels, and records human review for redaction-aware personal digest/export tasks subject to provider/runtime readiness and policy.
- `notification.list_my_account_center` (`browser-tool`, `agent-tool` read): reads personal in-app notification lanes for the signed-in account and selected context.
- `notification.mark_read`, `notification.dismiss`, `notification.archive`, `notification.snooze`, and `notification.update_preferences` (`browser-tool`, bounded `human_chat_tool_plan`; functional-agent drafting/proposal only): update personal notification read/lifecycle/preference state without resolving source work or granting authority.
- `my_account.open_authorized_workstream` and `attention.open_attention_item` (`browser-tool`, `agent-tool` prepare/read): reauthorize sibling-workstream or source-attention openings and return a target surface or safe no-enumeration denial.
- `my_account.view_own_trace_refs` (`browser-tool`, `agent-tool` read): opens browser-safe trace/evidence summaries visible to the signed-in account in the selected context.

## Authorization and denials

Backend authorization is authoritative for every worker and actor adapter. Human surface availability does not grant agent tool authority; agent tool-boundary availability does not create human affordances. Disabled accounts return `/api/me` safe `disabled_account` / no-selected-context recovery and cannot authorize workstream actions until an authorized account-reactivation flow succeeds. Inactive, disabled, suspended, removed, or archived-parent memberships are omitted from selected-context eligibility or returned as safe non-authorizing context summaries where policy permits. Zero-membership/orphaned accounts return `no_active_membership` recovery without deleting the account or enumerating hidden memberships. Disabled users, inactive memberships, missing selected context, cross-tenant reads, hidden source-workstream targets, unsupported notification actions, provider/runtime digest blockers, or unsupported permission changes are denied and traced. Notification and source-opening tools never mutate source attention, tasks, roles, memberships, or provider state; they only update personal lifecycle/read state or reauthorize a destination surface.

## Outcomes

In scope: browser-safe self-service, zero-membership/no-selected-context recovery, personal command-center dashboard, context/authority visibility, named theme and personal preference persistence, durable personal attention aggregation, in-app notification triage/preferences, governed digest/export progress/result/blocked flows, authorized sibling-workstream/source opening, and safe open-denied recovery.

Out of scope: role assignment, account privilege escalation, tenant/customer administration, tenant-wide branding, external notification provider administration, source-work mutation through notification actions, fake/model-less digest success in normal runtime, hidden workstream enumeration, public self-registration, account deletion/anonymization, and provider secret exposure.

## Linked graph nodes

- Workstream: `../workstreams/my-account/workstream.md`
- Tests: `../workstreams/my-account/tests/coverage.md`
- Traces: `../workstreams/my-account/traces/work-traces.md`
