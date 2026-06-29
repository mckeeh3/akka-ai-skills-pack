# Access: My Account

## Authorized roles

authenticated-member.

## Authorized workers

- `workers/signed-in-member-human.md` is the primary human worker for browser `surface_action`, `api_call`, and exact-confirmed `human_chat_tool_plan` adapters.
- `workers/my-account-functional-agent-worker.md` is the AI-backed workstream assistant for explanation, read/advisory tool use, and no-mutation plan proposals only.
- `workers/my-account-system-worker.md` is the deterministic backend/API/projection/workflow worker for context resolution, surface assembly, reauthorization, idempotency, and trace emission.

## Scope rules

All reads, writes, surface actions, confirmed chat plans, streams, agent turns, API calls, workflow/timer/consumer/internal paths, and governed-tool invocations use backend-owned selected `AuthContext`, tenant/customer ids, membership status, role/capability grants, and approval policy. Human surface availability does not grant agent authority; agent tool-boundary availability does not create a human affordance.

## AuthContext / Organization scope

The selected `AuthContext` is the only authority source for My Account. It is assembled from the authenticated account, active membership, selected Tenant-backed Organization/customer context, visible roles/capabilities, support-access state where visible, and frontend-safe `/api/me` bootstrap data. Browser route parameters, hidden form fields, prompt text, trace refs, and client-selected context ids are advisory inputs only; the backend must re-resolve and reauthorize them before any read, update, source opening, trace opening, or chat-plan confirmation.

Account/profile reads and profile/settings self-service updates are scoped to the signed-in member's own account. My Account may explain membership/Organization status and selected context, but it cannot create membership, grant roles/capabilities, modify account status, reveal hidden Organizations/customers, or administer tenant/customer settings. Disabled accounts, inactive memberships, and missing selected context render recovery surfaces only.

## Denials

Disabled users, inactive memberships, missing selected context, cross-tenant/customer access, unsupported authority expansion, and hidden/unauthorized actions are denied server-side, produce safe `system-message` feedback where user-facing, and emit required traces without exposing protected data.
