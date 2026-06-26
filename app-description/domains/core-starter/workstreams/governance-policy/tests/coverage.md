# Tests: Governance/Policy

## Acceptance

- Given an authorized caller with selected `AuthContext`, when they open Governance/Policy, then the all-policy inventory renders only scoped data, supported policy scopes, effective values, overridden indicators, and authorized actions.
- Given a SaaS owner in defaults context changes a default boolean or counter policy with a reason and idempotency key, then the default changes, history is recorded, effective values are recomputed for tenants without overrides, and existing tenant overrides are not overwritten.
- Given a tenant admin changes a supported tenant business-governance policy override with a reason and idempotency key, then the override becomes active immediately, effective policy reflects the override, history is recorded, and no default notification is sent.
- Given a tenant admin resets an override, then the override is removed, the effective value falls back to the SaaS default or less-specific inherited value, and history/trace evidence is recorded.
- Given multiple policy scopes match a runtime decision, then the more specific/finer-grained policy wins and the effective-policy explanation identifies the winning scope.
- Given a runtime action checks policy, then the policy-decision trace records the effective value, source, scope, actor/action context, and last-change reason/source where applicable.
- Given an auditor or support user with authorized access opens policy history, then direct changes and practical runtime outcome links are visible without exposing hidden tenant/customer facts or raw sensitive payloads.

## UI and surface coverage

- Inventory/search supports filters for policy name, workstream, agent, tool/action, and role.
- Inventory and detail surfaces clearly show default value, tenant override value when present, effective value, value type, scope, overridden indicator, and reset availability.
- Edit surfaces require a reason and validate boolean/counter values without complex policy scripting.
- SaaS owner default-management uses the same workstream with SaaS-owner/defaults context selected.
- Related agent/workstream/customer/account pages may deep-link into policy edit/detail surfaces, but Governance/Policy remains the central history and effective-policy view.

## Security and negative

- Disabled users, inactive memberships, missing selected context, missing capability, and cross-tenant/customer requests are denied without protected-data leakage.
- Tenant admins cannot change SaaS defaults.
- SaaS owners changing defaults cannot overwrite tenant overrides.
- Tenant admins cannot override hard platform controls: tenant isolation, backend authorization, secret/JWT/provider-key protection, raw prompt/model/provider payload protection, redaction boundaries, audit trace integrity, or platform integrity checks.
- Unsupported policy ids, unsupported scopes, unsupported value types, missing reasons, stale versions, malformed counters, and hidden customer/account targets return safe validation/denial/`system_message` outcomes and emit traces.
- Browser payloads never expose provider secrets, raw prompts, hidden authority state, JWTs, raw tool payloads, raw correlation/idempotency internals, or cross-tenant evidence.
- The Governance/Policy agent may explain, search, summarize, and draft simple changes, but cannot autonomously mutate policies or expand authority.

## Idempotency and observability

- Repeating any side-effecting default, override, or reset action with the same idempotency key returns the existing result and does not duplicate history, traces, or runtime outcome links.
- Denials, validation errors, effective-policy decisions, policy changes, reset actions, default updates, stale/conflict outcomes, and hard-platform-security override attempts are verifiable through local Akka/API/UI tests or readiness evidence.

## `human_chat_tool_plan` coverage

- Given deterministic surface routing can safely open or prefill a surface for policy reads/searches/effective-detail requests, when a high-confidence no-mutation prompt is submitted, then the router returns that surface first and `human_chat_tool_plan` is not used.
- Given a representative command prompt such as **allow SalesAgent to send emails immediately**, **reset this policy to default**, or **set the SaaS default for this policy to false** and an authorized selected `AuthContext`, when the chat request is classified as `human_chat_tool_plan`, then the response is a no-mutation plan proposal surface that lists the relevant action, governed tool, capability `governance-policy-lifecycle`, schema, required reason, idempotency, side effects, result surface, and trace refs.
- Given a proposed plan has not been explicitly confirmed, when the request completes, then no policy/default/override state mutation has occurred.
- Given the human confirms the exact `planId` and `planSnapshotId`, when backend authorization, catalog, validation, selected scope, required reason, hard-platform-security, and idempotency checks pass, then each step executes as an independent transaction boundary and returns the declared result or recovery surface.
- Given a modified, stale, expired, cross-context, cross-tenant/customer, missing-confirmation, out-of-catalog, unsupported-field, hidden-target, missing-reason, hard-platform-security, or unauthorized plan is confirmed, then execution is denied with a safe `system_message`, `noDirectMutation=true`, no hidden-target enumeration, and trace refs.
