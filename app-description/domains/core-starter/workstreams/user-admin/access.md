# Access: User Admin

## Authorized roles

- `tenant-admin`: manage tenant employees and tenant-owned customer administration within authorized tenant/customer scope.
- `customer-admin`: manage only selected customer users, invitations, customer roles, and customer access-review evidence permitted by customer policy.
- `auditor`: read scoped evidence and audit/access-review traces where permitted; no mutation.
- `saas-owner-support`: may view or explain tenant/customer user data only through an active tenant-created support-access grant and audited selected context.

Role names are UI/intent labels only. Backend capability grants, selected `AuthContext`, membership state, approval policy, and resource ownership are authoritative.

## Scope rules

All reads, writes, surface actions, streams, agent turns, skill/reference loads, internal worker tasks, and governed-tool invocations use backend-owned selected `AuthContext`, tenant/customer ids, account status, membership status, role/capability grants, support-access state, resource ownership, and approval policy.

Frontend rail visibility, disabled buttons, hidden actions, prompt text, expertise text, and client filters never grant authority. Direct API/deep-link requests still receive backend authorization and safe denials.

## Required denials

Server-side denials include disabled actor, inactive membership, missing selected context, missing capability, Customer Admin tenant-level action, SaaS Owner without support grant, cross-tenant/customer target, hidden/not-found target, role escalation, last-admin loss, self-disable/self-admin-role-removal, support-access policy violation, identity relink policy denial, unsupported bulk side effect, unredacted audit export, provider/model/outbox unavailable, missing tool-boundary grant, and denied skill/reference load.

Denials produce safe `system-message` feedback where user-facing, emit audit/work traces, and must not reveal protected identities, hidden counts, cross-scope existence, raw provider ids, raw invitation tokens, secrets, or private evidence.

## Approval and confirmation

Invitation send/resend/revoke, membership status changes, role changes, support-access grants/revocations/extensions, identity relink, and access-review resolution require explicit human confirmation and/or decision-card approval according to policy. Last-admin loss, role escalation, support-access expansion, identity relink/reset, bulk operations, and low-confidence recommendations are risky by default.
