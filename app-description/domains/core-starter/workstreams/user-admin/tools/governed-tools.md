# Tools: User Admin

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Allowed governed tools:

- `search-user-directory` (`browser-tool`, `agent-tool` read): scoped dashboard, users directory, user detail inspection, invitation status, support-access state, access-review state, and audit-evidence views. Directory output is discovery-only; row/card activation opens inspection surfaces.
- `create-or-resend-invitation` (`browser-tool`; human-confirmed agent preparation only): invitation create, detail read, resend confirmation, revoke confirmation, and delivery/outbox visibility.
- `change-membership-role-or-status` (`browser-tool`; approval/decision-card when risky): membership/account lifecycle confirmations and role preview/change commits through dedicated task surfaces.
- `grant-or-revoke-support-access` (`browser-tool`; expiry/purpose/approval required): support-access grant/extend forms and revoke confirmations.
- `run-access-review` (`agent-tool`, `internal-tool`): start/read/cancel/review durable access-review recommendations; recommendations route follow-up access changes through deterministic User Admin task surfaces.
- `readSkill` / `readReferenceDoc` (`agent-tool` only when assigned and boundary-granted): load active User Admin expertise documents after manifest, scope, status, token, and redaction checks.

## Boundaries

Tools are exposed as browser, agent, or internal tools only as stated by the linked capability. Side-effecting or high-impact tools require idempotency, correlation, authorization, confirmation or approval policy, and audit/work traces. Denied tool calls are traced and return safe feedback.

The agent may prepare payloads and explain tool outcomes, but cannot autonomously send invitations, change roles, disable/reactivate users, alter support access, resolve reviews, or expand authority. Tool output must be browser-safe and tenant/customer scoped.
