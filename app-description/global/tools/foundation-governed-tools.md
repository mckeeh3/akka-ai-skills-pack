# Global Tools: Foundation governed tools

Reusable governed-tool ids used by core starter capability and workstream bindings. Workstream files define local exposure and authorization details.

## My Account

- `read-current-account-context`
- `update-own-profile-settings` (legacy/self-service id retained for existing realization references)
- `my_account.update_profile_settings` (current namespaced My Account profile/settings tool id used by surface/chat-plan mappings)
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

## User Admin

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

## Agent Admin doc editing

- `list-agent-doc-agents`
- `read-agent-doc-agent`
- `update-agent-name-purpose`
- `read-agent-prompt-doc`
- `read-agent-skill-doc`
- `read-agent-skill-reference-doc`
- `draft-agent-doc-edit`
- `revise-agent-doc-edit`
- `save-agent-doc-edit`
- `cancel-agent-doc-edit`
- `read-agent-doc-version-history`
- `read-agent-doc-version-diff`
- `restore-agent-doc-version`
- `create-agent-skill`
- `delete-agent-skill`
- `create-agent-skill-reference-doc`
- `delete-agent-skill-reference-doc`
- `read-agent-doc-runtime-traces`
- `readSkill`
- `readReferenceDoc`

## Audit and governance policy

- `read-audit-trace-dashboard`
- `search-audit-traces`
- `read-trace-detail`
- `read-trace-tool-call-detail`
- `read-audit-retention-setting`
- `update-audit-retention-setting`
- `read-trace-timeline`
- `read-trace-failure-evidence`
- `read-investigation-guide`
- `request-redacted-export`
- `draft-investigation-note`
- `start-audit-summary-task`
- `read-audit-summary-task`
- `review-audit-summary-task`
- `accept-audit-summary-task`
- `reject-audit-summary-task`
- `governance.policy.list`
- `governance.policy.read_effective`
- `governance.policy.set_default`
- `governance.policy.set_override`
- `governance.policy.reset_override`
- `governance.policy.read_history`

Exposure labels are `browser-tool`, `agent-tool`, `internal-tool`, or `onboarding-tool`; prompt content cannot add exposure or authority. Side-effecting tools require backend authorization, idempotency/correlation handling, audit/work traces, and approval/confirmation when policy marks the action risky. `onboarding-tool` exposure is reserved for deterministic invitee/system flows such as invitation acceptance and is not agent authority.
