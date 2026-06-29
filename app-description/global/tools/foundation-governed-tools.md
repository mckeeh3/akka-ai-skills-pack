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

Canonical Agent Admin ids are namespaced by governed artifact. The legacy `agent-doc-administration` capability artifact name and `*-agent-doc-*` tool ids remain source-alignment aliases only; they do not create separate authority from the canonical managed-agent governance tools.

Current canonical ids:

- `agent-definition.catalog.read`
- `agent-definition.detail.read`
- `agent-behavior-profile.history.read`
- `agent-behavior-profile.proposal.create`
- `agent-behavior-profile.version.activate`
- `prompt-document.read`
- `prompt-document.proposal.create`
- `prompt-version.activate`
- `skill-document.catalog.read`
- `skill-document.proposal.create`
- `skill-version.activate`
- `reference-document.catalog.read`
- `reference-document.proposal.create`
- `reference-version.activate`
- `agent-skill-manifest.assign`
- `agent-reference-manifest.assign`
- `model-policy.select`
- `tool-permission-boundary.assign`
- `agent-test-console.run`
- `agent-runtime-trace.read`
- `readSkill`
- `readReferenceDoc`

Legacy aliases retained for existing realization evidence include `list-agent-doc-agents`, `read-agent-doc-agent`, `inspect-agent-runtime-profile`, `draft-agent-doc-edit`, `revise-agent-doc-edit`, `save-agent-doc-edit`, `submit-agent-doc-proposal-for-review`, `approve-agent-doc-proposal`, `reject-agent-doc-proposal`, `activate-agent-doc-version`, `cancel-agent-doc-edit`, `read-agent-doc-version-history`, `read-agent-doc-version-diff`, `restore-agent-doc-version`, `assign-agent-skills`, `assign-agent-generated-tools`, `list-agent-skill-library`, `create-agent-skill`, `delete-agent-skill`, `create-agent-skill-reference-doc`, `delete-agent-skill-reference-doc`, and `read-agent-doc-runtime-traces`. Current intent maps those aliases to the canonical ids in `../../domains/core-starter/workstreams/agent-admin/tools/governed-tools.md`.

`readSkill` and `readReferenceDoc` are runtime loader tools, not open filesystem access. They require manifest membership, tool boundary allowance, active document status, selected `AuthContext`, redaction checks, `SkillLoadTrace`/`ReferenceLoadTrace` evidence, and fail-closed denial.

## Audit/Trace current investigation tool ids

Current Audit/Trace ids are governed investigation tools for tenant-admin and scoped SaaS-support audit/work trace search, detail, correlation, denial investigation, support-access review, redacted export request handling, runtime-validation evidence links, and trace-gap diagnostics:

- `search-audit-traces`
- `search-work-traces`
- `read-audit-trace-detail`
- `read-work-trace-detail`
- `lookup-trace-correlation`
- `investigate-denied-trace-access`
- `summarize-investigation-evidence`
- `request-redacted-trace-export`
- `review-support-access-traces`
- trace ingestion/projection/retention/gap internals
- runtime-validation evidence link internals

Audit/Trace browser actions use `surface_action` plus protected `api_call`. Read-only investigation may also use confirmed `human_chat_tool_plan` and bounded `agent_tool_call` adapters where `../../domains/core-starter/workstreams/audit-trace/tools/governed-tools.md` grants the exact tool and redacted/model-safe result. Export and support-access-sensitive operations remain surface/API/internal paths with approval gates where policy requires them; the agent cannot approve support access or exports. Raw sensitive export, trace editing/deletion, full-payload keyword search, support-access self-approval, and prompt-based authority expansion are not current authority.

Legacy tenant-admin activity-log ids such as `read-audit-trace-dashboard`, `read-trace-detail`, `read-trace-tool-call-detail`, `read-audit-retention-setting`, `update-audit-retention-setting`, `read-trace-timeline`, `read-trace-failure-evidence`, and `read-investigation-guide` are compatibility labels only when a source-alignment entry maps them to the canonical investigation ids above. Retention and trace-visibility policy changes are governed through Governance/Policy and Audit/Trace policy gates rather than an unscoped chat/router mutation.

## Governance/Policy current tool ids

- `governance.policy.search`
- `governance.policy.read`
- `governance.policy.draft`
- `governance.policy.simulate`
- `governance.policy.submit_for_approval`
- `governance.policy.approve`
- `governance.policy.activate`
- `governance.policy.rollback`
- `governance.policy.review_exception`
- `governance.policy.read_history`

Legacy simple-settings ids `governance.policy.list`, `governance.policy.read_effective`, `governance.policy.set_default`, `governance.policy.set_override`, and `governance.policy.reset_override` are aliases only when mapped into the lifecycle model as search/read/draft/simulate/approval/activation operations. They are not separate direct-commit tools.

Governance/Policy read and draft/simulation-assist tools may be exposed to the functional agent through bounded `agent_tool_call` where the tool boundary grants them. Side-effecting lifecycle tools execute through backend-authorized `surface_action`, protected `api_call`, confirmed `human_chat_tool_plan`, `workflow_step`, or `internal_call` only as declared by the workstream; `governance-policy-agent` has no autonomous approval, activation, rollback, or exception-commit authority.

## Shared exposure and result semantics

Exposure labels are descriptive only:

- `browser-tool` = human `surface_action` through protected shell/API plus backend authorization.
- `agent-tool` = AI `agent_tool_call` through a resolved agent profile and explicit tool boundary.
- `internal-tool` = workflow/timer/consumer/internal adapter with provenance.
- `onboarding-tool` = deterministic invitee/system onboarding path.

Side-effecting tools require backend authorization, idempotency/correlation handling, audit/work traces, and approval/confirmation when policy marks the action risky. Read/evidence tools return scoped, redacted evidence DTOs rather than raw state dumps. Denials, stale/conflict/no-op replays, provider/outbox/model blockers, partial failures, and validation failures return typed result/system-message surfaces and trace evidence.
