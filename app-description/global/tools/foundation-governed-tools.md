# Global Tools: Foundation governed tools

Reusable governed-tool ids used by core starter capability and workstream bindings. Workstream files define local exposure and authorization details.

## My Account

- `read-current-account-context`
- `update-own-profile-settings`
- `request-personal-digest-export`

## User Admin

- `search-user-directory`
- `create-or-resend-invitation`
- `change-membership-role-or-status`
- `grant-or-revoke-support-access`
- `run-access-review`

## Agent/Admin behavior governance

- `list-agent-catalog`
- `read-agent-behavior-detail`
- `draft-agent-behavior-proposal`
- `approve-activate-or-rollback-agent-behavior`
- `readSkill`
- `readReferenceDoc`

## Audit and governance policy

- `search-audit-traces`
- `read-trace-detail`
- `request-redacted-export`
- `draft-investigation-note`
- `list-policy-proposals`
- `draft-policy-proposal`
- `simulate-policy-change`
- `approve-activate-or-rollback-policy`
- `record-policy-outcome-note`

Exposure labels are `browser-tool`, `agent-tool`, or `internal-tool`; prompt content cannot add exposure or authority. Side-effecting tools require backend authorization, idempotency/correlation handling, audit/work traces, and approval/confirmation when policy marks the action risky.
