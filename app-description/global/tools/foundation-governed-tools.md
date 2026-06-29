# Global Tools: Foundation governed tools

Reusable governed-tool ids used by core starter capability and workstream bindings. A governed tool is a semantic app operation or governed evidence read. It is not a UI button, raw endpoint, Akka component method, prompt phrase, or model SDK tool. Workstream files define local exposure, inputs, surfaces, tests, and realization evidence.

## Shared governed-tool node contract

Every current or future tool binding must record:

- stable governed tool id, display name, tool type, owning capability id, and current/deferred status;
- allowed worker types and exact actor adapters: `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `api_call`, `workflow_step`, `timer_invocation`, `consumer_reaction`, `internal_call`, or future explicit `mcp_tool_call`;
- exposure labels when useful for implementation mapping: `browser-tool` maps to `surface_action` plus protected API; `agent-tool` maps to `agent_tool_call`; `internal-tool` maps to workflow/timer/consumer/internal paths; `onboarding-tool` maps to deterministic invitation/onboarding `api_call`/`internal_call` only;
- selected `AuthContext`, Organization/Tenant/Customer/account scope, role/capability checks, support-access posture, policy/approval requirements, and denial behavior;
- input/output schemas, validation, redaction, safe defaults, browser-safe evidence limits, and raw-secret/raw-token/raw-provider-payload exclusions;
- confirmation, approval, autonomy, idempotency key source, transaction boundary, no-op/replay behavior, partial-failure behavior, side effects, result surfaces/events, and attention/projection updates;
- audit/work trace requirements, including adapter source, correlation id, requestedBy for AI-mediated human requests, confirmedBy and confirmation id for confirmed chat-plan execution, and denial category;
- implementation mapping references to capability, Akka/API/frontend/agent/workflow/timer/consumer paths, automated tests, and runtime-validation scenario id or explicit scenario gap.

Side-effecting tools default to human confirmation or approval when consequential/risky. AI-backed workers do not inherit authority from human surface availability. Prompt text, route visibility, hidden fields, frontend state, and model output never grant tool authority.

## Canonical id and alias convention

Namespaced capability-operation ids are canonical for new bindings. Broad legacy ids remain governed tool family aliases for existing realization references and should map to the canonical operation ids rather than creating duplicate operations.

| Legacy/family id | Canonical current ids / scope |
| --- | --- |
| `update-own-profile-settings` | Alias for `my_account.update_profile_settings`; retained for existing realization evidence only. |
| `manage-organizations` | Family alias for `saas_owner.organization.list/read/create/rename/suspend/archive/reactivate`. |
| `manage-saas-owner-admins` | Family alias for `saas_owner.admin.list/invite/manage`. |
| `manage-organization-admins` | Family alias for `saas_owner.organization_admin.list/invite/manage`. |
| `manage-customers` | Family alias for `tenant.customer.list/read/create/rename/suspend/archive/reactivate`. |
| `manage-customer-admins` | Family alias for `tenant.customer_admin.list/invite/manage`. |
| `search-user-directory` | Family alias for `user_admin.view_overview`, `user_admin.list_members`, `user_admin.read_user_account`, acceptance/support/access-review read tools, and authorized `admin.audit.read` links. |
| `create-or-resend-invitation` | Family alias for `user_admin.invite_user`, `user_admin.resend_invitation`, `user_admin.revoke_invitation`, and `user_admin.acceptance_status.read`; invitee acceptance remains `user_admin.accept_invitation` / `accept-invitation`. |
| `change-membership-role-or-status` | Family alias for `user_admin.preview_role_change`, `user_admin.change_member_roles`, and `user_admin.update_member_status`. |
| `grant-or-revoke-support-access` | Family alias for `user_admin.support_access.read` and `user_admin.support_access.grant_revoke_extend`. |
| `run-access-review` | Family alias for `user_admin.access_review.start/read/cancel/accept_result/reject_result`; advisory only. |

Per-workstream tasks should bind canonical ids where local files already use namespaced ids and may keep family ids only as compatibility aliases/evidence references.

## My Account current tool ids

- `read-current-account-context`
- `update-own-profile-settings` (legacy/self-service family alias retained for existing realization references)
- `my_account.update_profile_settings` (canonical namespaced profile/settings tool id used by current surface/chat-plan mappings)
- `request-personal-digest-export`
- `notification.list_my_account_center`
- `notification.mark_read`
- `notification.dismiss`
- `notification.archive`
- `notification.snooze`
- `notification.update_preferences`
- `my_account.open_authorized_workstream`
- `attention.open_attention_item`
- `my_account.view_own_trace_refs`

## User Admin current tool ids

Family aliases retained for existing realization references:

- `manage-organizations`
- `manage-saas-owner-admins`
- `manage-organization-admins`
- `manage-customers`
- `manage-customer-admins`
- `search-user-directory`
- `create-or-resend-invitation`
- `accept-invitation`
- `change-membership-role-or-status`
- `grant-or-revoke-support-access`
- `run-access-review`

Canonical namespaced operations live in `../../domains/core-starter/capabilities/user-and-access-administration.md` and include the `saas_owner.*`, `tenant.customer.*`, `tenant.customer_admin.*`, `user_admin.*`, and `admin.audit.read` ids listed there. `accept-invitation` / `user_admin.accept_invitation` is an `onboarding-tool` only and is not agent authority.

## Agent Admin current tool ids

- `list-agent-doc-agents`
- `read-agent-doc-agent`
- `update-agent-model-config-ref`
- `read-agent-behavior-profile-history`
- `restore-agent-behavior-profile-version`
- `assign-agent-skills`
- `assign-agent-generated-tools`
- `list-agent-skill-library`
- `read-agent-prompt-doc`
- `read-agent-skill-doc`
- `read-agent-skill-reference-doc`
- `inspect-agent-runtime-profile`
- `draft-agent-doc-edit`
- `revise-agent-doc-edit`
- `save-agent-doc-edit` (creates a non-active draft/proposal; activation is separate unless a later bounded implementation explicitly aliases low-risk review-and-activate)
- `submit-agent-doc-proposal-for-review`
- `approve-agent-doc-proposal`
- `reject-agent-doc-proposal`
- `activate-agent-doc-version`
- `cancel-agent-doc-edit`
- `read-agent-doc-version-history`
- `read-agent-doc-version-diff`
- `restore-agent-doc-version` (creates a restore proposal; activation is separate)
- `create-agent-skill`
- `delete-agent-skill`
- `create-agent-skill-reference-doc`
- `delete-agent-skill-reference-doc`
- `read-agent-doc-runtime-traces`
- `readSkill`
- `readReferenceDoc`

`readSkill` and `readReferenceDoc` are runtime loader tools, not open filesystem access. They require manifest membership, tool boundary allowance, active document status, selected `AuthContext`, redaction checks, `SkillLoadTrace`/reference-load trace evidence, and fail-closed denial.

## Audit/Trace current tenant-admin activity-log tool ids

Current in-scope tenant-admin activity-log tools:

- `read-audit-trace-dashboard`
- `search-audit-traces`
- `read-trace-detail`
- `read-trace-tool-call-detail`
- `read-audit-retention-setting`
- `update-audit-retention-setting`
- `read-trace-timeline`
- `read-trace-failure-evidence`
- `read-investigation-guide`

The current Audit/Trace tenant-admin activity-log capability grants only `surface_action` and protected `api_call` reads/mutation where declared by `../../domains/core-starter/capabilities/audit-and-trace-investigation.md`. It grants no `agent_tool_call` or `human_chat_tool_plan` authority for trace search, full-payload detail, payload read, retention mutation, export, investigation notes, or summaries.

Deferred/non-current Audit/Trace ids retained only as future-scope placeholders until a later accepted current-intent change defines capability, AuthContext, approvals, redaction, tests, and runtime-validation contracts:

- `request-redacted-export`
- `draft-investigation-note`
- `start-audit-summary-task`
- `read-audit-summary-task`
- `review-audit-summary-task`
- `accept-audit-summary-task`
- `reject-audit-summary-task`

Per-workstream refresh must not bind these deferred ids as current authority.

## Governance/Policy current tool ids

- `governance.policy.list`
- `governance.policy.read_effective`
- `governance.policy.set_default`
- `governance.policy.set_override`
- `governance.policy.reset_override`
- `governance.policy.read_history`

Governance/Policy side-effecting tools are exposed through `surface_action`, protected `api_call`, and confirmed `human_chat_tool_plan` only where local binding declares confirmation and backend authorization. They are not autonomous `agent_tool_call`s.

## Shared exposure and result semantics

Exposure labels are descriptive only:

- `browser-tool` = human `surface_action` through protected shell/API plus backend authorization.
- `agent-tool` = AI `agent_tool_call` through a resolved agent profile and explicit tool boundary.
- `internal-tool` = workflow/timer/consumer/internal adapter with provenance.
- `onboarding-tool` = deterministic invitee/system onboarding path.

Side-effecting tools require backend authorization, idempotency/correlation handling, audit/work traces, and approval/confirmation when policy marks the action risky. Read/evidence tools return scoped, redacted evidence DTOs rather than raw state dumps. Denials, stale/conflict/no-op replays, provider/outbox/model blockers, partial failures, and validation failures return typed result/system-message surfaces and trace evidence.
