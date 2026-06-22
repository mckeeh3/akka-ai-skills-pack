# Access: Audit/Trace

## Authorized roles

- `saas-owner-admin`: may inspect app-owner/platform-scoped audit/work trace evidence and Organization administration traces visible from a SaaS Owner selected `AuthContext`; this does not grant tenant/customer application-data trace visibility without an explicit authorized support or tenant/customer context.
- `tenant-admin`: may inspect Organization/Tenant-scoped audit/work trace evidence for the selected Organization, including customer-boundary administration evidence within that Organization.
- `customer-admin`: may inspect Customer-scoped audit/work trace evidence for the selected Customer only; sibling-customer and tenant-wide evidence is omitted or redacted.
- `auditor`: may inspect scoped evidence where explicitly granted.
- `policy-owner-approver`: may inspect policy/decision trace evidence where authorized for the selected scope.

## Scope rules

All reads, writes, surface actions, streams, agent turns, and governed-tool invocations use backend-owned selected `AuthContext`, tenant/customer ids, membership status, role/capability grants, and approval policy. Trace visibility follows the admin level of the selected context: SaaS Owner for platform/app-owner evidence, Organization Admin for organization evidence, and Customer Admin for customer evidence. Cross-level drilldowns reauthorize through Audit/Trace before revealing evidence.

## Denials

Disabled users, inactive memberships, missing selected context, cross-tenant/customer access, unsupported authority expansion, and hidden/unauthorized actions are denied server-side, produce safe `system-message` feedback where user-facing, and emit required traces without exposing protected data.
