# Catalog coverage map: expanded `human_chat_tool_plan` current intent

Task: `TASK-WCTC-02-001`

Scope: app-description/current-intent only. This map does not claim runtime catalog expansion, backend execution, frontend rendering, seeds, or tests are implemented. Later tasks must implement and verify the accepted entries.

## Classification key

- `chat-executable-now`: candidate for exact-confirmed chat execution through an existing backend-authorized surface/action path.
- `chat-proposal-only`: may create/read inert proposals, drafts, advisory tasks, simulations, or evidence but must not commit final authority/lifecycle state.
- `approval-gated`: may be proposed or routed only when a separate approval/decision/confirmation surface remains authoritative; chat confirmation alone is insufficient.
- `surface-only`: keep as structured browser surface action because target context, evidence review, validation, or recovery UX must remain authoritative in the surface.
- `router-only`: deterministic no-mutation open/prefill route, not a chat execution step.
- `internal-only`: service/provider/background/support path not directly exposed to chat.
- `blocked-pending-design`: missing prerequisite policy, runtime, approval, redaction, provider, or test design for safe chat exposure.
- `out-of-scope`: outside this five-workstream foundation catalog expansion.

Global invariants for every accepted entry: deterministic surface router runs first; proposal generation is no-mutation; exact `planId + planSnapshotId + selected AuthContext + requestedBy + confirmedBy + stepHashes` confirmation is required; backend reauthorizes every step against selected `AuthContext`, tenant/customer scope, capability, catalog membership, tool boundary, approval policy, lifecycle state, and idempotency; provider/model/runtime unavailable states fail closed; browser-visible traces/results are redacted and never expose secrets, raw provider payloads, JWTs, invitation tokens, hidden targets, or unredacted evidence.

## Accepted expanded current-intent entries

| Workstream | Classification | Action ids | Shared governed tool ids | Capability ids | Input/result contracts | Required evidence in later implementation |
|---|---|---|---|---|---|---|
| My Account | `chat-executable-now` | `action-update-my-profile`; `action-update-my-settings` | `my_account.update_profile_settings` | `my_account.update_profile_settings` | `schema.my-account.profile.update.v1`; `schema.my-account.settings.update.v1`; result `surface-my-profile` / `surface-my-settings` | Exact confirmation; unsupported field denial; invalid theme/timezone/category denial; self-scope only; idempotent replay; trace refs. |
| My Account | `chat-executable-now` | `action-notification-mark-read`; `action-notification-dismiss`; `action-notification-archive`; `action-notification-snooze`; `action-notification-update-preferences` | `notification.mark_read`; `notification.dismiss`; `notification.archive`; `notification.snooze`; `notification.update_preferences` | `notification.manage_own_state`; `notification.update_own_preferences` | `schema.notification.*`; result `surface-my-account-notification-center` | Visible notification/category binding; no source task mutation; bounded snooze; no-op repeated lifecycle; idempotency; trace refs. |
| User Admin | `chat-executable-now` | `action-submit-organization-create`; `action-submit-organization-admin-invitation`; `action-submit-user-admin-invitation`; `action-submit-saas-owner-admin-invitation`; `action-customer-admin-invite` | `manage-organizations`; `manage-organization-admins`; `create-or-resend-invitation`; `manage-saas-owner-admins`; `manage-customer-admins` | `saas_owner.tenant.manage`; `saas_owner.organization_admin.invite`; `user_admin.invite_user`; `saas_owner.admin.invite`; `tenant.customer_admin.invite` | Organization/customer/invitation submit schemas; result Organization/Customer/invitation detail surfaces | Role pinned to authorized options; selected tenant/customer isolation; duplicate/open invite behavior; provider/outbox fail-closed; idempotency; trace refs. |
| User Admin | `chat-executable-now` | `action-useradmin-resend-invitation`; `action-confirm-user-admin-invitation-revoke` | `create-or-resend-invitation` | `user_admin.resend_invitation`; `user_admin.revoke_invitation` | `schema.invitation.resend.v1`; `schema.invitation.revoke.v1`; result invitation detail/system-message surfaces | Visible invitation/status binding; revoke consequence copy; no-op revoked replay; provider/outbox fail-closed; exact confirmation; trace refs. |
| User Admin | `chat-executable-now` | `action-submit-customer-create`; `action-submit-customer-rename`; `action-submit-organization-rename` | `manage-customers`; `manage-organizations` | `tenant.customer.create`; `tenant.customer.rename`; `saas_owner.organization.rename` | Customer/Organization create/rename schemas; result Customer/Organization detail surfaces | Selected Organization/Tenant authority; visible customer/org id for rename; duplicate/no-op/conflict behavior; idempotency; trace refs. |
| Agent Admin | `approval-gated` | `action-agent-prompt-risk-review-start` | `agent_admin.start_behavior_review_task` | `agent_admin.start_behavior_review_task` | `schema.agent-admin.prompt-risk-review.start.v1`; result `surface-agent-admin-prompt-risk-review` | Provider/runtime/tool-boundary fail-closed; no behavior mutation; approval-required display; exact confirmation; trace refs. |
| Agent Admin | `chat-proposal-only` | `action-agent-detail-run-test`; `action-agent-prompt-governance-simulate`; `action-agent-skill-manifest-simulate`; `action-agent-tool-boundary-simulate`; `action-agent-model-refs-run-test` | Agent Admin simulation/test governed tools | `agent_admin.start_behavior_review_task` or specific simulation capabilities | `schema.agent-admin.*simulate/run-test*`; result test/simulation/system-message surfaces | No side-effect tools; no activation; provider missing returns blocked/no fake success; redacted outputs; trace refs. |
| Agent Admin | `chat-proposal-only` | `action-agent-prompt-governance-submit-review`; `action-agent-skill-manifest-submit-review`; `action-agent-tool-boundary-submit-review`; `action-agent-model-refs-submit-review`; `action-propose-prompt-diff`; `action-submit-behavior-change`; `action-agent-behavior-proposal-submit` | `draft-agent-behavior-proposal`; `submit-agent-behavior-proposal` | `agent_admin.draft_behavior_change`; `agent_admin.submit_behavior_change_for_review` | `schema.agent-admin.*submit-review*`; result behavior proposal/artifact review surfaces | Draft/submission idempotency; prompt/skill/model text cannot grant authority; no active behavior change; trace refs. |
| Audit/Trace | `chat-executable-now` | `action-audit-trace-search`; `action-audit-trace-detail`; `action-audit-trace-timeline`; `action-audit-trace-failure-evidence`; `action-audit-trace-investigation-guide` | `search-audit-traces`; `read-trace-detail`; `read-trace-timeline`; `read-trace-failure-evidence`; `read-investigation-guide` | `audit.trace.search`; `audit.trace.detail.read`; `audit.trace.timeline.read`; `audit.trace.failureEvidence.read`; `audit.trace.investigationGuide.read` | `schema.audit-trace.*`; result search/detail/timeline/failure/guide surfaces | Visible trace/correlation/filter binding; redaction; hidden trace denial/no enumeration; tenant/customer isolation; trace refs. |
| Audit/Trace | `chat-executable-now` | `action-audit-trace-append-investigation-note` | `draft-investigation-note` | `audit.trace.investigation_note.append` | `schema.audit-trace.investigation-note.v1`; result `surface-audit-trace-investigation-note` | Exact confirmation; idempotent append; no source evidence/policy mutation; redaction; trace refs. |
| Governance/Policy | `chat-executable-now` | `action-governance-policy-list`; `action-governance-policy-read` | `list-policy-proposals`; `governance.policy.read` | `governance.policy.read` | `schema.governance-policy.inventory.v1`; `schema.governance-policy.detail.v1`; result inventory/detail surfaces | Scoped filters; hidden row no-enumeration; tenant/customer isolation; redaction; trace refs. |
| Governance/Policy | `chat-proposal-only` | `action-governance-policy-draft-proposal`; `action-governance-policy-submit-proposal`; `action-governance-policy-simulate`; `action-governance-policy-start-impact-analysis`; `action-governance-policy-read-impact-analysis` | `draft-policy-proposal`; `simulate-policy-change`; `start-policy-impact-analysis`; `read-policy-impact-analysis` | `governance.policy.propose`; `governance.policy.simulate`; `governance.policy.impact_analysis.start`; `governance.policy.impact_analysis.read` | Proposal/simulation/impact-analysis schemas; result proposal/simulation/task/result surfaces | Draft/submission/advisory only; no activation/rollback; provider blocked no fake success; invalid scope denial; idempotency; trace refs. |

## Non-executable, blocked, and surface/router rationale

| Workstream | Classification | Action groups | Rationale |
|---|---|---|---|
| My Account | `router-only` | Dashboard/profile/settings/context reads and sibling workstream/source opens | Deterministic no-mutation open/prefill is the safe path and avoids hidden workstream enumeration. |
| My Account | `surface-only` | Context switch, sign out, personal digest read/result review | Requires backend-authored context choices, shell/session UX, stale-surface handling, or advisory evidence review. |
| My Account | `approval-gated` | Personal digest start/cancel/accept/reject | Provider/model-backed advisory work and disposition need fail-closed semantics and human review surfaces. |
| My Account | `blocked-pending-design` | External/email notification provider preferences/checks | Needs external-channel provider/outbox policy, redaction, and fail-closed tests. |
| My Account | `internal-only` | Identity/session/account bootstrap, notification producers, attention aggregation | Provider/service/background paths are not direct chat catalog steps. |
| My Account | `out-of-scope` | Business-domain profile extensions and non-foundation notification channels | Outside this foundation catalog expansion. |
| User Admin | `router-only` | Dashboard, directories, branch returns, audit-trace opens | Structured surfaces preserve row visibility and no-enumeration. |
| User Admin | `surface-only` | Open-only confirmation surfaces, support-access read/validate, access-review reads/disposition, identity exception review opens | Browser surfaces carry required target context, backend-authored options, evidence review, and recovery UX. |
| User Admin | `chat-proposal-only` | Role/capability preview, access-review recommendations | Safe as evidence/proposal only; final access changes remain gated. |
| User Admin | `approval-gated` | Suspend/archive/reactivate, role/status/account disable/reactivate/remove, support access, access-review lifecycle, identity relink lifecycle | High-impact authority/lifecycle changes require last-admin/self-action/provider policies and decision-card semantics. |
| User Admin | `blocked-pending-design` | Permanent account removal and provider identity recovery completion | Requires destructive/recovery design, provider redaction, and audit policy. |
| User Admin | `internal-only` | Invitation acceptance, identity linking, expiry/delivery workers, audit projection consumers | Service/provider/background paths only. |
| User Admin | `out-of-scope` | Business-domain permissions outside foundation hierarchy | Outside this foundation catalog expansion. |
| Agent Admin | `router-only` | Dashboard/catalog/search/detail opens | No-mutation route avoids hidden agent/artifact enumeration. |
| Agent Admin | `surface-only` | Redacted artifact reads, seed material prepare/read, prompt-risk read/source/trace opens, trace drill-ins | Browser surfaces own target binding, redaction, and diagnostic evidence review. |
| Agent Admin | `approval-gated` | Review decisions, behavior proposal approve/reject/defer/cancel, lifecycle activation/deactivation/rollback, seed import start/cancel | Managed-agent authority and lifecycle state require separate governance approval/prerequisites. |
| Agent Admin | `blocked-pending-design` | Direct lifecycle changes without approved metadata; trace export/escalation; customization-overwriting seed import | Needs full approval/redaction/recovery design. |
| Agent Admin | `internal-only` | Skill/reference loaders, provider readiness probes, task workers, import diff builders | Governed loader/provider/service paths only. |
| Agent Admin | `out-of-scope` | Business-domain managed-agent teams | Outside this foundation catalog expansion. |
| Audit/Trace | `router-only` | Dashboard open | No-mutation route is sufficient for the command center. |
| Audit/Trace | `surface-only` | Summary read/review/accept/reject and export review surfaces | Evidence review, redaction choices, and human disposition remain structured-surface responsibilities. |
| Audit/Trace | `approval-gated` | Redacted export request and summary task start | Export and model-backed summaries require approval/redaction or provider fail-closed semantics. |
| Audit/Trace | `blocked-pending-design` | Unredacted/raw export, hidden evidence delivery, escalation workflows | Needs export/redaction/approval design and tests. |
| Audit/Trace | `internal-only` | Trace ingestion, retention/projection gap detection, raw event normalization, summary workers | Background evidence pipeline only. |
| Audit/Trace | `out-of-scope` | Business-domain audit actions outside foundation trace investigation | Outside this foundation catalog expansion. |
| Governance/Policy | `router-only` | Dashboard open | No-mutation route keeps queues visible without implying authority. |
| Governance/Policy | `surface-only` | Detail opens without visible binding, outcome note until target binding exists, impact-result disposition | Dedicated surfaces preserve visible proposal binding, evidence review, and human reason capture. |
| Governance/Policy | `approval-gated` | Decision/approve/reject/request changes, activation, rollback, impact disposition/cancel | Live governance decisions and authority changes require lifecycle prerequisites and decision-card semantics. |
| Governance/Policy | `blocked-pending-design` | Direct activation/rollback/threshold weakening without approved proposal and rollback metadata | Needs approval, simulation evidence, rollback, recovery, and trace policy. |
| Governance/Policy | `internal-only` | Policy evaluators, enforcement hooks, scheduled outcome follow-ups, impact-analysis workers | Background/governance service paths only. |
| Governance/Policy | `out-of-scope` | Business-domain policy surfaces outside foundation Governance/Policy | Outside this foundation catalog expansion. |

## App-description files updated

- `app-description/domains/core-starter/workstreams/surface-catalog.md`
- `app-description/domains/core-starter/workstreams/my-account/tools/governed-tools.md`
- `app-description/domains/core-starter/workstreams/user-admin/tools/governed-tools.md`
- `app-description/domains/core-starter/workstreams/agent-admin/tools/governed-tools.md`
- `app-description/domains/core-starter/workstreams/audit-trace/tools/governed-tools.md`
- `app-description/domains/core-starter/workstreams/governance-policy/tools/governed-tools.md`

## Follow-up validation expected in later tasks

Later implementation and regression tasks must add backend/API/frontend evidence for no mutation before confirmation, exact confirmation, out-of-catalog denial, selected-context/capability denial, idempotency, approval-gated behavior, partial failure/recovery, provider fail-closed behavior, redaction/secret boundaries, and durable audit/work traces across the expanded catalog.
