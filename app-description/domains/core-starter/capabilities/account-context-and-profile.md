# Capability: Account context and profile

## Purpose

Let an authenticated member inspect and maintain browser-safe account, profile, settings, selected `AuthContext`, memberships, attention summary, and personal notification/digest preferences for the SaaS Foundation App.

## Actors and scope

- Primary actor: authenticated member in an active membership.
- Supporting callers: My Account functional agent and workstream shell.
- Scope: selected tenant/customer context from backend-owned authorization state.

## Governed tools and exposure

- `read-current-account-context` (`browser-tool`, `agent-tool`): returns `/api/me`-safe account, profile, settings, memberships, capabilities, and selected context.
- `update-own-profile-settings` (`browser-tool`): updates user-experience profile/settings only; it never grants permissions.
- `request-personal-digest-export` (`browser-tool`, `agent-tool`): creates a redaction-aware digest/export request subject to policy.

## Authorization and denials

Backend authorization is authoritative. Disabled users, inactive memberships, missing selected context, cross-tenant reads, or unsupported permission changes are denied and traced.

## Outcomes

In scope: browser-safe self-service, personal command-center dashboard, context/authority visibility, named theme and personal preference persistence, personal attention aggregation, in-app notification triage/preferences, governed digest/export progress/result/blocked flows, authorized sibling-workstream/source opening, and safe open-denied recovery.

Out of scope: role assignment, account privilege escalation, tenant/customer administration, tenant-wide branding, external notification provider administration, fake/model-less digest success in normal runtime, hidden workstream enumeration, and provider secret exposure.

## Linked graph nodes

- Workstream: `../workstreams/my-account/workstream.md`
- Tests: `../workstreams/my-account/tests/coverage.md`
- Traces: `../workstreams/my-account/traces/work-traces.md`
