# Core Starter Capabilities

Capability nodes describe product/backend capability contracts, actors, outcomes, authorization, governed tools, traces, tests, and realization references for the built-in core starter domain.

## Shared capability node convention

Each capability file should preserve accepted product semantics while exposing the current graph links needed by workstream refresh tasks:

- stable capability id/name and capability class (`read/evidence`, `command`, `proposal`, `approval`, `workflow`, `policy/governance`, `trace/audit`, `scheduled`, or `reactive`);
- owning workstream(s) and shared workers from `../../../global/workers/foundation-workers.md` or local worker bindings;
- governed tool ids from `../../../global/tools/foundation-governed-tools.md`, including canonical namespaced ids and any legacy family aliases;
- actor adapters (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `api_call`, `workflow_step`, `timer_invocation`, `consumer_reaction`, `internal_call`, future explicit `mcp_tool_call`) allowed for each tool;
- selected `AuthContext`, Organization/Tenant/Customer/account scope, support-access posture, roles/capability grants, policy/approval/confirmation rules, denial behavior, redaction, and tenant/customer isolation;
- inputs/outputs, safe DTO/evidence limits, idempotency key source, transaction boundary, no-op/replay/conflict behavior, partial-failure behavior, result/system-message surfaces, side effects, events, and projection/attention effects;
- linked data-state nodes, implementation realization files, source-alignment entries, tests, traces, and runtime-validation scenario ids or explicit scenario gaps.

Capability nodes remain shared product contracts. Workstream-local files bind role-specific dashboards, surfaces, action edges, functional-agent tools, API paths, frontend routes, and validation evidence.

## Current capability nodes

- `account-context-and-profile.md`
- `user-and-access-administration.md`
- `agent-doc-administration.md`
- `audit-and-trace-investigation.md`
- `governance-policy-lifecycle.md`
