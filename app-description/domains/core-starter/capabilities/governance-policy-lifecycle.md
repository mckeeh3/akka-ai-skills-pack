# Capability: Governance policy lifecycle

## Purpose

Let authorized SaaS owners and tenant admins manage simple governance policy defaults, tenant overrides, effective-policy reads, reset-to-default behavior, policy-change history, and runtime policy-decision evidence for the core starter.

## Actors and scope

- SaaS owner: manages default values in a SaaS-owner/defaults context.
- Tenant admin: manages tenant business-governance overrides in authorized tenant/customer/account scope.
- Auditor: reads authorized policy settings, history, and runtime-decision evidence.
- Support: reads authorized policy state/history for scoped tenant support.
- Governance/Policy functional agent: explains, searches, summarizes, and drafts simple policy changes under bounded authority.

## Governed tools and exposure

- `governance.policy.list` (`browser-tool`, `agent-tool` read): searchable policy catalog with supported scopes, value type, effective value, and overridden indicators.
- `governance.policy.read_effective` (`browser-tool`, `agent-tool` read): scoped default/override/effective-value detail and decision explanation.
- `governance.policy.set_default` (`browser-tool` command): SaaS-owner-only update of a default boolean or counter value with required reason.
- `governance.policy.set_override` (`browser-tool`, confirmed `human_chat_tool_plan` command): tenant-admin update of a business-governance override with required reason.
- `governance.policy.reset_override` (`browser-tool`, confirmed `human_chat_tool_plan` command): tenant-admin reset of an override back to inherited/default behavior with required reason.
- `governance.policy.read_history` (`browser-tool`, `agent-tool` read): authorized direct change history and practical runtime outcome links.

## Authorization and denials

Tenant admins may override tenant business-governance policy values and decide tenant behavior. SaaS owners may update defaults but must not overwrite tenant overrides. Auditors and support users may read only when backend authorization grants scoped access.

Hard platform security controls are not overrideable through this capability. Non-overridable controls include tenant isolation, backend authorization, secret/JWT/provider-key protection, raw prompt/model/provider payload protection, redaction boundaries, audit trace integrity, and platform integrity checks.

Denied writes and reads return safe system messages with no hidden scope enumeration and emit trace evidence.

## Capability contract

Inputs for side-effecting tools include selected `AuthContext`, actor id, policy id, value type, target scope, requested value for set operations, idempotency key, correlation id, current version/freshness token when available, and required reason. Browser payloads never carry tenant/customer/account authority as trusted input; backend-selected context is authoritative.

Outputs are typed surfaces or safe `system-message` responses with status, default value, override value when visible, effective value, overridden indicator, winning-scope explanation, validation failures, allowed/disabled actions, redaction metadata, and trace refs. Raw prompts, provider secrets, hidden authority state, JWTs, raw tool payloads, cross-tenant evidence, raw correlation ids, and idempotency internals are never returned.

Supported policy value types are `boolean` and `counter`/`limit`. Additional types require future app-description updates and must remain SMB-simple.

Supported scopes may include tenant, agent, workstream, action/tool, role, and customer/account. When multiple scopes apply, the finer-grained/more specific matching scope wins.

Repeated side-effecting commands with the same idempotency key return the existing result and do not duplicate default changes, overrides, resets, history entries, traces, notifications, or outcome links. Stale version, missing reason, validation, forbidden, hard-platform-security, and conflict outcomes return structured blockers; they do not render as success.

## Outcomes

In scope: all-policy inventory, effective-policy detail, default management, tenant override management, reset-to-default, required change reasons, history, runtime policy-decision traces, overridden indicators, and simple search/filter.

Out of scope: complex policy scripting, simulations, legal compliance workflows, approval workflows for policy edits, notifications by default, enterprise role delegation, autonomous policy commits by prompt, and tenant override of hard platform security controls.

## Linked graph nodes

- Workstream: `../workstreams/governance-policy/workstream.md`
- Tests: `../workstreams/governance-policy/tests/coverage.md`
- Traces: `../workstreams/governance-policy/traces/work-traces.md`
