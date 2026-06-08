# Agent Binding: user-admin-agent

## Uses

Global definition: `../../../../../global/agents/foundation-functional-agents.md`.

## Authority

The agent operates only through capability `user-and-access-administration` and governed tools `search-user-directory, create-or-resend-invitation, change-membership-role-or-status, grant-or-revoke-support-access, run-access-review` with selected `AuthContext`, backend authorization, tool-boundary checks, approval gates, and durable traces.

## Prompt intent

Guide authorized users through administer users, memberships, invitations, roles, support access, access reviews, identity review, and admin audit summaries within authorized tenant/customer scope. Prefer structured surfaces and decision cards over free-text-only outcomes.

## Tests and traces

See `../tests/coverage.md` and `../traces/work-traces.md`.
