# Access: My Account

## Authorized roles

authenticated-member.

## Authorized workers

- `workers/signed-in-member-human.md` is the primary human worker for browser `surface_action`, `api_call`, and exact-confirmed `human_chat_tool_plan` adapters.
- `workers/my-account-functional-agent-worker.md` is the AI-backed workstream assistant for explanation, read/advisory tool use, and no-mutation plan proposals only.
- `workers/my-account-system-worker.md` is the deterministic backend/API/projection/workflow worker for context resolution, surface assembly, reauthorization, idempotency, and trace emission.

## Scope rules

All reads, writes, surface actions, confirmed chat plans, streams, agent turns, API calls, workflow/timer/consumer/internal paths, and governed-tool invocations use backend-owned selected `AuthContext`, tenant/customer ids, membership status, role/capability grants, and approval policy. Human surface availability does not grant agent authority; agent tool-boundary availability does not create a human affordance.

## Denials

Disabled users, inactive memberships, missing selected context, cross-tenant/customer access, unsupported authority expansion, and hidden/unauthorized actions are denied server-side, produce safe `system-message` feedback where user-facing, and emit required traces without exposing protected data.
