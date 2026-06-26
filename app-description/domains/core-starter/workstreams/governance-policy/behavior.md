# Behavior: Governance/Policy

## Current-state behavior

Governance/Policy manages simple policy settings for SaaS defaults and tenant overrides. It starts from an all-policy searchable list, shows effective values and overridden indicators, supports tenant-admin override changes, supports reset-to-default, and records policy-change history and runtime policy-decision evidence.

## Agent behavior

`governance-policy-agent` may explain effective policy, summarize defaults and overrides, help find policies, draft simple boolean/counter changes, and prepare reason text. It cannot grant authority through prompt text, bypass backend authorization, bypass tenant isolation, expose secrets, override hard platform security controls, or invent policy types outside the catalog.

Model-backed turns use governed runtime configuration or fail closed.

## Policy catalog behavior

The policy catalog is not fixed up front. App-description changes for agents, workstreams, governed tools/actions, roles, and customer/account behavior may introduce new simple policy definitions. Each definition declares its id/name, value type, SaaS default, allowed scopes, effective-value calculation, and trace/history requirements.

Initial allowed value types are:

- `boolean`;
- `counter` / `limit`.

Additional value types require explicit future app-description intent and must remain SMB-simple.

## Defaults, overrides, and precedence

SaaS owner default values apply where no more specific tenant override exists. Tenant admins can set tenant-owned overrides at supported scopes. Tenant overrides become active immediately after a successful backend-authorized write.

When multiple settings match a runtime decision, the finer-grained/more specific matching scope wins. Resetting an override removes the tenant value and recomputes the effective value from SaaS defaults or less-specific overrides. SaaS owner default changes do not overwrite tenant overrides.

## Change behavior

Every policy write requires a human-entered reason. Policy changes do not require additional confirmation beyond the normal committed action/request and do not notify anyone by default.

Every write records history including actor, selected `AuthContext`, old value, new value, effective timestamp, scope, affected policy id, affected agents/workstreams/tools/roles/customers/accounts where applicable, reason, idempotency key, and trace reference.

## Runtime decision behavior

Runtime policy checks return an effective decision with a human-readable explanation. The trace should identify whether the value came from SaaS default or tenant override, which scope won, who last changed the winning override when applicable, when it changed, and the recorded reason.

Example: one agent/action can be allowed to send email immediately while another agent/action is governed differently because a more specific tenant override applies.

## Edge cases

Repeated writes with the same idempotency key return the existing result and must not duplicate history or traces. Unsupported policy ids, unsupported scopes, unsupported value types, missing reasons, stale versions, inactive users/memberships, missing selected context, and unauthorized cross-tenant/customer attempts return safe validation/denial states and emit traces.

Attempts to override hard platform security controls, such as tenant isolation, secret protection, backend authorization, redaction boundaries, and platform integrity controls, are denied even for tenant admins.

Unsupported business-domain requests are routed to extension guidance rather than silently adding complex policy machinery.
