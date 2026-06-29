# Cross-workstream consistency and readiness review

Task: `TASK-ADR-03-001`
Date: 2026-06-29
Scope: description/readiness review for refreshed shared `app-description/` artifacts and the five core-starter workstreams: My Account, User Admin, Agent Admin, Governance/Policy, and Audit/Trace.

## Readiness outcome

Overall state: `not-ready` for terminal app-description refresh verification until the bounded follow-up `TASK-ADR-03-002` reconciles shared capability/global-tool/surface-catalog drift.

The five refreshed workstream directories now generally preserve the intended chain:

```text
worker -> execution harness -> actor adapter -> governed tool -> capability
  -> realization/source-alignment -> tests/runtime-validation references -> traces
```

However, several shared artifacts that the workstreams depend on still describe pre-refresh scope for Governance/Policy, Audit/Trace, and Agent Admin aliases. Those contradictions are material because downstream generation or terminal verification would otherwise choose between inconsistent current-intent authorities.

## Concrete findings

| Severity | Finding | Evidence | Required disposition |
| --- | --- | --- | --- |
| High | Audit/Trace capability still defines a narrow tenant-admin activity-log scope and explicitly denies agent/chat/export/summary authority, while refreshed Audit/Trace workstream defines a broader investigation graph with read-only chat plans, bounded agent tool calls, support-access review, runtime-validation evidence, trace-gap detection, summaries, and redacted export-if-allowed behavior. | `app-description/domains/core-starter/capabilities/audit-and-trace-investigation.md`; `app-description/domains/core-starter/workstreams/audit-trace/workstream.md`; `app-description/domains/core-starter/workstreams/audit-trace/tools/governed-tools.md`; `app-description/domains/core-starter/workstreams/audit-trace/access.md`; `app-description/domains/core-starter/workstreams/audit-trace/realization/source-alignment.md`. | Reconcile the capability node and global tool inventory to the refreshed workstream or explicitly narrow/defer the conflicting workstream exposure. Queued as `TASK-ADR-03-002`. |
| High | Governance/Policy capability and global tool inventory still describe the older simple default/override model (`governance.policy.list`, `read_effective`, `set_default`, `set_override`, `reset_override`) while refreshed workstream files use lifecycle/decision ids (`search`, `read`, `draft`, `simulate`, `submit_for_approval`, `approve`, `activate`, `rollback`, `review_exception`, `read_history`) and decision-card approval semantics. | `app-description/domains/core-starter/capabilities/governance-policy-lifecycle.md`; `app-description/global/tools/foundation-governed-tools.md`; `app-description/domains/core-starter/workstreams/governance-policy/tools/governed-tools.md`; `app-description/domains/core-starter/workstreams/governance-policy/workstream.md`; `app-description/domains/core-starter/workstreams/governance-policy/realization/source-alignment.md`. | Reconcile capability/global tool ids, exposure, approval/idempotency semantics, and old-id alias policy. Queued as `TASK-ADR-03-002`. |
| High | Shared surface catalog and ready-to-build summary still route Audit/Trace and Governance/Policy using the pre-refresh activity-log/simple-policy model and state that Audit/Trace has no `human_chat_tool_plan` execution, conflicting with the refreshed workstream graph and shared adapter catalog. | `app-description/domains/core-starter/workstreams/surface-catalog.md`; `app-description/domains/core-starter/workstreams/ready-to-build-status.md`; `app-description/domains/core-starter/workstreams/audit-trace/surfaces/surfaces.md`; `app-description/domains/core-starter/workstreams/governance-policy/surfaces/surfaces.md`; `app-description/domains/core-starter/workstreams/audit-trace/agents/functional-agent.md`. | Refresh shared catalog/status text or mark old rows as legacy/compatibility-only so generated routing does not infer obsolete authority. Queued as `TASK-ADR-03-002`. |
| Medium | Agent Admin local workstream uses canonical managed-agent governance tool ids while global/capability artifacts still primarily expose the legacy `agent-doc-administration` and `*-agent-doc-*` id vocabulary. Local files document the alias posture, but shared artifacts do not yet provide the same canonical/alias mapping. | `app-description/domains/core-starter/workstreams/agent-admin/tools/governed-tools.md`; `app-description/domains/core-starter/workstreams/agent-admin/workstream.md`; `app-description/domains/core-starter/capabilities/agent-doc-administration.md`; `app-description/global/tools/foundation-governed-tools.md`; `app-description/domains/core-starter/workstreams/agent-admin/lifecycle.md`. | Add or confirm bounded canonical/legacy alias mapping in shared artifacts without renaming runtime code. Queued as part of `TASK-ADR-03-002`. |

## Cross-workstream graph proof

| Workstream | Worker/adapter/tool/capability proof | Lifecycle/source-alignment posture | Runtime-validation posture | Readiness note |
| --- | --- | --- | --- | --- |
| My Account | Workstream tools bind signed-in human, functional agent, and system worker adapters to `account-context-and-profile`, including `surface_action`, `api_call`, bounded `human_chat_tool_plan`, read/advisory `agent_tool_call`, notification tools, authorized opens, and trace refs. | `lifecycle.md` and `realization/source-alignment.md` both mark `stale-description-changed` after TASK-ADR-02-001. | `tests/coverage.md` and source-alignment graph proof name `/api/me`, profile/context, denial, agent-assistance, and trace validation refs. | Locally coherent for description/build planning; no material cross-workstream blocker found in this pass. |
| User Admin | Workstream graph binds human admins, functional/access-review agents, invitation/onboarding/system workers, surface/chat/agent/API/workflow/timer/consumer/internal adapters, User Admin governed tools, and `user-and-access-administration`. | `lifecycle.md` and `realization/source-alignment.md` both mark `stale-description-changed`. | Tests/source-alignment require invitation, list/detail, role/status/support/access-review, denial, trace, and provider/outbox/model fail-closed validation. | Locally coherent for description/build planning; no material cross-workstream blocker found in this pass. |
| Agent Admin | Workstream graph binds SaaS admin human, functional agent, behavior-editor internal agent, runtime system worker, canonical governed tools, `readSkill`/`readReferenceDoc`, loader/test-console traces, and legacy alias notes. | `lifecycle.md` and `realization/source-alignment.md` both mark `stale-description-changed`; lifecycle explicitly notes legacy capability naming. | Tests/source-alignment require SaaS-admin auth, proposal lifecycle, provider fail-closed test console, loader/tool-boundary denial, and Prompt/Skill/Reference/Agent work traces. | Locally coherent, but shared global/capability alias map needs reconciliation before terminal verification. |
| Governance/Policy | Refreshed local graph binds human operators, functional/system workers, `surface_action`, confirmed `human_chat_tool_plan`, bounded `agent_tool_call`, `api_call`, `workflow_step`, `internal_call`, lifecycle governed tools, and `governance-policy-lifecycle`. | `lifecycle.md` and `realization/source-alignment.md` both mark `stale-description-changed`. | Tests/source-alignment require draft/simulation/approval/activation/rollback/exception/denial/partial-failure/trace validation. | Local graph is coherent, but shared capability/global-tool/surface-catalog artifacts contradict the refreshed ids and approval model. |
| Audit/Trace | Refreshed local graph binds tenant admin, SaaS support, functional agent, system worker, read/search/correlation/denial/support/export/runtime-validation tools, and `audit-and-trace-investigation`. | `lifecycle.md` and `realization/source-alignment.md` both mark `stale-description-changed`. | Tests/source-alignment require search/detail, denied read, redaction, chat-plan, agent-tool boundary, support access, export, correlation, trace-gap, provider/config fail-closed, and source-alignment evidence validation. | Local graph is coherent, but shared capability/global-tool/surface-catalog artifacts still describe the old activity-log-only authority. |

## Consistency checklist result

- Shared global definitions versus workstream bindings: not ready; Governance/Policy and Audit/Trace shared definitions diverge from refreshed workstream bindings.
- Actor adapters: local workstreams consistently use `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `api_call`, workflow/timer/consumer/internal vocabulary; shared catalog/capability text must be reconciled before terminal verification.
- Governed tool ids to capability ids: locally present in all five workstreams; Governance/Policy, Audit/Trace, and Agent Admin shared/global mappings need bounded reconciliation.
- AuthContext, roles, tenant/Organization/Customer scope, denials, and frontend secret boundaries: consistently emphasized in refreshed workstreams; shared stale capability text creates conflicting authority for Audit/Trace and Governance/Policy.
- Lifecycle/source-alignment: all five workstreams honestly mark `stale-description-changed`; no runtime-ready claim was found.
- Runtime-validation references: each refreshed workstream names scenario refs or scenario expectations; no real runtime validation was run in this docs-only task.

## Impact and queued follow-up

The impact is description/readiness-only. No runtime/API/UI code is changed or implied. Terminal verification should not run next because it would inherit unresolved shared-current-intent contradictions.

Queued follow-up: `TASK-ADR-03-002` should reconcile shared capability/global-tool/surface-catalog/current-status drift for Governance/Policy, Audit/Trace, and Agent Admin alias mappings, then return the queue to terminal verification.

## Validation

Required commands for this task:

- `rg -n "stale-description-changed|source-alignment|runtime-validation|actor adapter|governed tool|capability" app-description/domains/core-starter/workstreams`
- `git diff --check`
