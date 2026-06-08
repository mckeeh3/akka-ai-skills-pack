# Access: My Account

## Authorized roles

authenticated-member.

## Scope rules

All reads, writes, surface actions, streams, agent turns, and governed-tool invocations use backend-owned selected `AuthContext`, tenant/customer ids, membership status, role/capability grants, and approval policy.

## Denials

Disabled users, inactive memberships, missing selected context, cross-tenant/customer access, unsupported authority expansion, and hidden/unauthorized actions are denied server-side, produce safe `system-message` feedback where user-facing, and emit required traces without exposing protected data.
