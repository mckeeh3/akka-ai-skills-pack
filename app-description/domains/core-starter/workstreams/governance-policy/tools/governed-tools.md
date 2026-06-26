# Tools: Governance/Policy

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Allowed governed tools:

- `governance.policy.list`: read the searchable policy catalog and visible scopes.
- `governance.policy.read_effective`: read SaaS default, tenant override, effective value, override indicator, and decision explanation for an authorized policy/scope.
- `governance.policy.set_default`: SaaS-owner-only write of a default boolean or counter value.
- `governance.policy.set_override`: tenant-admin write of a tenant override for an authorized business-governance policy/scope.
- `governance.policy.reset_override`: tenant-admin reset of a tenant override back to the inherited/default value.
- `governance.policy.read_history`: read authorized change history and runtime outcome links.

Tools are exposed as browser tools and bounded agent tools only as stated by the linked capability. Side-effecting tools require selected `AuthContext`, backend authorization, supported policy id/type/scope validation, required reason, idempotency, correlation, and audit/work traces. Denied tool calls are traced and return safe feedback.

## `human_chat_tool_plan` current-intent catalog

Exposure channel `human_chat_tool_plan` remains proposal-and-confirmation only. Deterministic no-mutation surface routing runs first. Initial execution-oriented chat requests may return a no-mutation plan proposal, and no state changes occur until exact human plan-snapshot confirmation and backend authorization succeed.

Allowed entries:

| Classification | Representative prompts | Surface action ids | Shared governed tool ids | Capability ids | Result surface(s) |
|---|---|---|---|---|---|
| `chat-executable-now` | `show policy settings`; `show effective policy for SalesAgent email`; `show overridden policies` | `action-governance-policy-list`; `action-governance-policy-read-effective` | `governance.policy.list`; `governance.policy.read_effective` | `governance.policy.read` | `surface-governance-policy-inventory`; `surface-governance-policy-effective-detail` |
| `chat-proposal-only` | `allow SalesAgent to send emails immediately`; `reset this policy to default`; `set max retries to 3` | `action-governance-policy-set-override`; `action-governance-policy-reset-override`; `action-governance-policy-set-default` | `governance.policy.set_override`; `governance.policy.reset_override`; `governance.policy.set_default` | `governance.policy.override`; `governance.policy.default.manage` | `surface-governance-policy-edit`; `surface-governance-policy-history` |

Blocked or out-of-scope entries:

- complex policy scripts or arbitrary rule expressions;
- policy simulations or impact-analysis tasks;
- legal compliance workflows;
- policy-edit approval workflows;
- default notifications for policy changes;
- enterprise delegation models;
- any request to override hard platform security controls.

Execution requirements for every accepted entry:

- validate catalog membership, supported policy type, supported scope, selected context, actor capability, required reason for writes, idempotency, and trace emission;
- recompute and return effective policy after writes;
- reject hidden scope targets, unsupported value types, cross-tenant/customer scope, missing reasons, stale versions, and hard platform-security overrides;
- idempotent replay returns prior write/read results without duplicate history or traces;
- no workstream agent, prompt, frontend route, visible control, or tool description grants authority beyond backend authorization.
