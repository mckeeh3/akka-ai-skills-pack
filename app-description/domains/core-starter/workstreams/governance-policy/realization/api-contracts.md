# Realization: API contracts for Governance/Policy

Capability: `governance-policy-lifecycle`.

## Browser/API evidence

| Tool / action | Exposure | Candidate API evidence | Contract obligations |
|---|---|---|---|
| `governance.policy.list` | `surface_action`, `api_call`, bounded `agent_tool_call` read | `WorkstreamEndpoint.java`, `AdminEndpoint.java`, governance policy service/repository | Scoped all-policy list with supported scopes, value type, default, override indicator, effective value, and trace links. |
| `governance.policy.read_effective` | `surface_action`, `api_call`, bounded `agent_tool_call` read/evaluate | governance policy service/repository | Returns SaaS default, visible tenant override, effective value, winning scope, precedence explanation, and redacted policy-decision trace refs. |
| `governance.policy.set_default` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan` command | admin/workstream endpoint plus governance service/repository | SaaS-owner/defaults-context write for boolean/counter defaults; requires reason, idempotency, auth, history, and trace; must not overwrite tenant overrides. |
| `governance.policy.set_override` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan` command | workstream/admin endpoint plus governance service/repository | Tenant-admin write for authorized business-governance scopes; requires reason, idempotency, supported policy type/scope, active-immediate result, history, and trace. |
| `governance.policy.reset_override` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan` command | workstream/admin endpoint plus governance service/repository | Removes tenant override, recomputes effective value, requires reason/idempotency, and records history/trace. |
| `governance.policy.read_history` | `surface_action`, `api_call`, bounded `agent_tool_call` read | governance service/repository, audit/workstream trace services | Scoped direct change history plus practical runtime outcome links where available. |
| Workstream messages/actions/events | `surface_action`, `api_call` | `WorkstreamEndpoint.java`, `frontend/src/api/HttpWorkstreamApiClient.ts` | Typed inventory, effective-detail, edit, history, and `system_message` surfaces with correlation ids, idempotency keys, validation blockers, and denials. |

## Validation evidence

- `src/test/java/ai/first/application/foundation/governance/GovernancePolicyServiceTest.java`
- `frontend/src/workstream-governance-policy-vertical.contract.test.mjs`
- `frontend/src/workstream-actions.contract.test.mjs`

## Gaps / caveats

- Existing implementation may still reflect older proposal/simulation/approval semantics and must be reviewed before claiming alignment.
- Request DTOs must preserve the updated app-description distinction between SaaS defaults, tenant overrides, effective-policy reads, reset-to-default, history, runtime-decision traces, and hard-platform-security denials.
