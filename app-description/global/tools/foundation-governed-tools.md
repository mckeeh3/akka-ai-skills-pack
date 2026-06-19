# Global Tools: Foundation governed tools

Reusable governed-tool ids used by core starter capability and workstream bindings. Workstream files define local exposure and authorization details.

## My Account

- `read-current-account-context`
- `update-own-profile-settings`
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

## Agent/Admin behavior governance

- `list-agent-catalog`
- `read-agent-behavior-detail`
- `draft-agent-behavior-proposal`
- `submit-agent-behavior-proposal`
- `approve-agent-behavior-proposal`
- `reject-agent-behavior-proposal`
- `defer-agent-behavior-proposal`
- `cancel-agent-behavior-proposal`
- `activate-agent-behavior-version`
- `rollback-agent-behavior-version`
- `deactivate-agent-behavior-version`
- `start-agent-prompt-risk-review`
- `read-agent-prompt-risk-review`
- `accept-agent-prompt-risk-review`
- `reject-agent-prompt-risk-review`
- `cancel-agent-prompt-risk-review`
- `prepare-agent-seed-import`
- `start-agent-seed-import`
- `cancel-agent-seed-import`
- `readSkill`
- `readReferenceDoc`

## Audit and governance policy

- `read-audit-trace-dashboard`
- `search-audit-traces`
- `read-trace-detail`
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
- `list-policy-proposals`
- `draft-policy-proposal`
- `simulate-policy-change`
- `approve-activate-or-rollback-policy`
- `record-policy-outcome-note`
- `start-policy-impact-analysis`
- `read-policy-impact-analysis`
- `cancel-policy-impact-analysis`
- `accept-policy-impact-result`
- `reject-policy-impact-result`
- `request-policy-impact-changes`

Exposure labels are `browser-tool`, `agent-tool`, `internal-tool`, or `onboarding-tool`; prompt content cannot add exposure or authority. Side-effecting tools require backend authorization, idempotency/correlation handling, audit/work traces, and approval/confirmation when policy marks the action risky. `onboarding-tool` exposure is reserved for deterministic invitee/system flows such as invitation acceptance and is not agent authority.
