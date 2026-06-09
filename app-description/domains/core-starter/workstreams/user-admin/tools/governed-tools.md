# Tools: User Admin

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Allowed governed tools:

- `search-user-directory` (`browser-tool`, `agent-tool` read): scoped dashboard, member directory, invitation status, support-access, access-review, and audit-evidence views.
- `create-or-resend-invitation` (`browser-tool`; human-confirmed agent preparation only): invitation create/resend/revoke and delivery/outbox visibility.
- `change-membership-role-or-status` (`browser-tool`; approval/decision-card when risky): membership status changes and role preview/change.
- `grant-or-revoke-support-access` (`browser-tool`; expiry/purpose/approval required): support-access lifecycle.
- `run-access-review` (`agent-tool`, `internal-tool`): start/read/cancel/review durable access-review recommendations.
- `readSkill` / `readReferenceDoc` (`agent-tool` only when assigned and boundary-granted): load active User Admin expertise documents after manifest, scope, status, token, and redaction checks.

## Boundaries

Tools are exposed as browser, agent, or internal tools only as stated by the linked capability. Side-effecting or high-impact tools require idempotency, correlation, authorization, confirmation or approval policy, and audit/work traces. Denied tool calls are traced and return safe feedback.

The agent may prepare payloads and explain tool outcomes, but cannot autonomously send invitations, change roles, disable/reactivate users, alter support access, resolve reviews, or expand authority. Tool output must be browser-safe and tenant/customer scoped.
