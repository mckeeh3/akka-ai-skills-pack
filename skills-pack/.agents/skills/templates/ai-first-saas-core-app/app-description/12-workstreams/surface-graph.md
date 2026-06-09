# Foundation Surface Graph

This file makes the template surface graph explicit for process guidance. It is a surface-ready baseline only: concrete app implementations must complete typed payloads, action mappings, capability contracts, governed-tool ids, and runtime validation before claiming `capability-ready` or higher.

## Graph node rules

- Every surface has exactly one owning functional agent.
- Reuse is explicit through `reusableByFunctionalAgentIds`/contract text; reuse does not transfer authority.
- Every edge is a surface action, surface-request, prompt-entered request, deep link, realtime refresh, workflow/AutonomousAgent progress event, or typed system result.
- Each edge should produce at most one primary browser-visible result surface for the same content. Activity records, tool-call traces, and response metadata remain collapsed/audit-only unless the edge explicitly targets a visible progress/detail surface.
- Browser-visible edges are advisory. Backend capability checks decide authorization, scope, redaction, approval, idempotency, side effects, audit, and denial.
- Deferred typed result surfaces are listed in `deferred-typed-surfaces.md` and may use `markdown_response`/`system_message` only as first-slice fallbacks, not as duplicate siblings of internal activity/detail surfaces.

## Foundation graph nodes

| surfaceId | ownerFunctionalAgentId | Reusable by | Role in graph | Contract |
| --- | --- | --- | --- | --- |
| `my-account-dashboard` | `my-account-agent` | opens authorized target workstreams/surfaces | My Account aggregate trunk | `surface-contracts/01-access-profile-dashboard.md` |
| `user-admin-dashboard` | `user-admin-agent` | My Account attention summaries | User Admin trunk | `surface-contracts/02-user-admin-dashboard.md` |
| `user-admin-user-list` | `user-admin-agent` | dashboard queues and prompt/deep-link requests | User Admin table/search node | `surface-contracts/03-user-admin-user-list.md` |
| `user-admin-user-account` | `user-admin-agent` | list rows, attention items, audit drill-ins | User Admin detail/action node | `surface-contracts/04-user-admin-user-account.md` |
| `decision-card` | `governance-policy-agent` | User Admin, Agent Admin, Audit/Trace, domain-specific reviewer workstreams | Approval/exception gate | `surface-contracts/05-decision-card.md` |
| `audit-trace-explorer` | `audit-trace-agent` | all foundation and domain-specific workstreams with scoped trace access | Evidence/timeline drill-in node | `surface-contracts/06-audit-trace-explorer.md` |
| `agent-governance-center` | `agent-admin-agent` | Governance/Policy | Managed-agent governance workspace | `surface-contracts/07-agent-governance-center.md` |
| `markdown_response` | producing functional agent | all foundation/domain workstreams | Structured explanatory/result surface; sole visible markdown result for the turn | shared contract in `docs/structured-surface-contracts.md` |
| `system_message` | producing functional agent | all foundation/domain workstreams | Structured feedback/denial/status surface; sole visible status result for the event | shared contract in `docs/structured-surface-contracts.md` |

## Foundation graph edges

| edgeId | Source surface | Action/request | Target/result surface | Capability family | Notes |
| --- | --- | --- | --- | --- | --- |
| `my-account.refresh-dashboard` | `my-account-dashboard` | refresh dashboard | `my-account-dashboard` | `secure-tenant-user-foundation` | Backend-produced account/context/attention projection. |
| `my-account.select-context` | `my-account-dashboard` | select AuthContext | `my-account-dashboard` or `system_message` | `secure-tenant-user-foundation` | Denial cannot leak hidden tenant/customer existence. |
| `my-account.open-profile` | `my-account-dashboard` | open profile | deferred `my-account-profile-card` or fallback | `secure-tenant-user-foundation` | Deferred typed surface. |
| `my-account.open-settings` | `my-account-dashboard` | open settings | deferred `my-account-settings-card` or fallback | `secure-tenant-user-foundation` | Deferred typed surface. |
| `my-account.open-attention-item` | `my-account-dashboard` | open attention item | target owning workstream surface or `system_message` | `frontend-shell-integration-patterns` | Target workstream authorization is authoritative. |
| `user-admin.refresh-dashboard` | `user-admin-dashboard` | refresh dashboard | `user-admin-dashboard` | `secure-tenant-user-foundation` | Counts come from backend views/projections. |
| `user-admin.open-invitation-queue` | `user-admin-dashboard` | open invitation queue | `user-admin-user-list` | `secure-tenant-user-foundation` | Filter state preserves dashboard origin metadata. |
| `user-admin.open-access-review` | `user-admin-dashboard` | open access review queue | `user-admin-user-list` or `decision-card` | `secure-tenant-user-foundation` | Risky items may route to approval. |
| `user-admin.start-investigation` | `user-admin-dashboard` | start access-risk investigation | deferred `task-progress-surface` or `system_message` | `secure-tenant-user-foundation` / `managed-agent-foundation` | Internal worker cannot grant authority. |
| `user-admin.search-users` | `user-admin-user-list` | search/list users | `user-admin-user-list` | `secure-tenant-user-foundation` | Pagination/filter metadata is backend scoped. |
| `user-admin.open-user-account` | `user-admin-user-list` | open user account | `user-admin-user-account` | `secure-tenant-user-foundation` | Hidden-not-found for forbidden cross-scope ids. |
| `user-admin.mutate-user-access` | `user-admin-user-list` or `user-admin-user-account` | invite/membership/role/account/support-access mutation | source update, `decision-card`, or `system_message` | `secure-tenant-user-foundation` / `governance-decisions-audit` | Idempotent, audited, policy-gated where risky. |
| `decision.review` | `decision-card` | approve/reject/request changes/defer/escalate | source update, `system_message`, or target queue | `governance-decisions-audit` | Reviewer authority enforced by backend. |
| `decision.request-evidence` | `decision-card` | request more evidence | deferred `task-progress-surface` or `system_message` | `governance-decisions-audit` | May trigger internal worker with bounded authority. |
| `audit.search` | `audit-trace-explorer` | search/open/filter traces | `audit-trace-explorer` | `governance-decisions-audit` | Search counts are scoped and redacted. |
| `audit.open-source-surface` | `audit-trace-explorer` | open source surface | source surface or `system_message` | `frontend-shell-integration-patterns` | Source workstream authorization remains authoritative. |
| `audit.export` | `audit-trace-explorer` | export scoped traces | deferred `trace-export-status`, `decision-card`, or `system_message` | `governance-decisions-audit` | Export may require approval. |
| `agent-governance.search` | `agent-governance-center` | search/list managed agents | `agent-governance-center` | `managed-agent-foundation` | Read only governed records visible in scope. |
| `agent-governance.open-detail` | `agent-governance-center` | open agent/detail/version | deferred `agent-detail-card` or `agent-version-card` | `managed-agent-foundation` | Deferred typed surfaces. |
| `agent-governance.propose-edit` | `agent-governance-center` | propose behavior edit | deferred `behavior-diff-review` or `decision-card` | `managed-agent-foundation` | Authority expansion denied or routed to policy. |
| `agent-governance.review-proposal` | `agent-governance-center` | review/approve/reject proposal | `decision-card` or `system_message` | `governance-decisions-audit` | Human authority retained. |
| `agent-governance.safe-test` | `agent-governance-center` | run safe test console | deferred `safe-test-console-result` or `system_message` | `managed-agent-foundation` | Never bypasses tool boundaries. |

## Domain-specific graph edge template

| edgeId | Source surface | Action/request | Target/result surface | Capability id | Governed-tool id | Exposure | Auth basis | Idempotency | Trace required | Notes |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `<domain>.<verb>` | `<domain-dashboard-or-surface>` | `<surface action or prompt request>` | `<surface-id or system_message>` | `<capability-id>` | `<governed-tool-id>` | `browser-tool|agent-tool|surface-request|workflow-tool|internal-tool` | `<role/AuthContext/scope>` | `<key strategy>` | `true|false` | `<result, stale, denial, approval behavior>` |

Do not invent stable action, capability, or governed-tool ids during implementation when the product authority is unclear. Ask or queue the blocking question; template examples may propose candidate ids only when clearly marked provisional.
