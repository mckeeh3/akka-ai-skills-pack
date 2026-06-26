# Core starter workstream ready-to-build status

Assessment date: 2026-06-26
Scope: app-description readiness for building the existing `core-starter` workstreams.

This status means the current-intent description is sufficiently complete to create focused build/compile implementation tasks for the workstream. It does **not** claim implementation alignment, runtime readiness, passing local Akka/API/UI validation, or manual test completion.

| Workstream | Ready-to-build status | Build scope basis | Required runtime proof before marking complete |
| --- | --- | --- | --- |
| `my-account` | `ready-to-build` | Purpose, functional agent, access, behavior, self-service profile/settings and notification tools, surfaces, tests, traces, and realization mappings are present. | Local Akka/API/UI validation of selected context, profile/settings updates, notification lifecycle actions, denials, traces, and provider/model fail-closed behavior for digest/export paths. |
| `user-admin` | `ready-to-build` | Three admin levels, explicit human/functional-agent/access-review/system worker roster, user/org/customer/invitation/membership/role/support-access/access-review/identity-exception behavior, capability bindings, surface graph, governed tools/actor adapters, tests, traces, and realization mappings are present. | Local Akka/API/UI validation of worker -> adapter -> governed-tool -> capability paths, scoped authorization, idempotent admin mutations, invitation delivery/acceptance boundaries, last-admin/self-action guardrails, audit/work traces, and provider/outbox/model fail-closed behavior. |
| `agent-admin` | `ready-to-build` | SaaS-admin-only managed-agent behavior governance intent, functional agent, access, safe behavior-profile inspection, proposal/review/activation lifecycle, tools, surfaces, tests, traces, and realization mappings are present. | Local Akka/API/UI validation of SaaS-admin authorization, catalog/detail/profile browsing, AI-assisted structured proposals, risk/authority-expansion handling, save-draft/review/approve/reject/activate/cancel/restore-proposal, version history, runtime doc loading, model/tool-boundary fail-closed behavior, and trace visibility. |
| `governance-policy` | `ready-to-build` | Updated SMB-friendly effective-policy settings model has purpose, functional agent, policy model, access, behavior, surfaces, tools, tests, traces, and realization mappings sufficient for focused build tasks. | Local Akka/API/UI validation of policy catalog/effective reads, SaaS defaults, tenant overrides, reset-to-default, history, policy-decision trace evidence, authorization denials, tenant isolation, and non-overridable platform controls. |
| `audit-trace` | `ready-to-build` | Tenant-admin activity-log scope intent, access, behavior, surfaces, tools, tests, traces, retention rules, and realization mappings are present for search, full-payload detail, tool-call linkage, denial evidence, and configurable retention. | Local Akka/API/UI validation of tenant-admin scoped search, detail/full-payload warning, tool-call parent links, retention default/update/bounds/no-op behavior, non-admin/disabled/cross-tenant denials, immutable trace records, and durable audit/work traces. |

## Recommendation

All existing core starter workstreams can now be split into focused build/compile task briefs. Build completion must still be judged per workstream through real local runtime validation, not by this app-description readiness status.
