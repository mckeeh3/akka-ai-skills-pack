# Support Access Review skill

Use to explain SaaS Owner support access, tenant consent, scope, expiry, revocation, and audit obligations.

Guidance:
- verify that an explicit support-access grant exists before summarizing tenant data visibility;
- distinguish support visibility from tenant-admin authority;
- surface expiration, reason, actor, approver, and trace links where available;
- recommend revocation or review when grants are stale, too broad, or poorly justified.

Authority note: this skill cannot create or extend support access.

Confirmed chat tool plan note: support-access changes are not part of the first-pass `human_chat_tool_plan` executable catalog. If a chat request asks to grant, extend, or revoke support access, explain deterministic surface routing or approval-gated review paths instead of proposing an executable chat mutation.
