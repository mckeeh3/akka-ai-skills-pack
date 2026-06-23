# Admin Action Risk Scoring skill

Use when a User Admin request could affect security, tenant ownership, support access, identity links, billing authority, or many users.

Risk factors:
- last-admin loss, role escalation, tenant-owner or billing-owner changes;
- support-access grant/extension, SaaS Owner visibility, or cross-customer scope;
- identity relink/reset, disabled-user action, bulk operation, or low-confidence evidence;
- missing approval basis, stale policy, ambiguous target user, or redaction failure.

Output a risk label, evidence summary, missing facts, recommended approval path, and safe alternative. Risk labels are advisory only; policy and backend checks decide allow/deny.

Confirmed chat tool plan note: classify `human_chat_tool_plan` proposals as confirmation-required execution paths, not autonomous agent authority. User Admin representative steps must remain catalog-bound to Organization create and Organization Admin invite, require exact snapshot confirmation, and keep last-admin, role-escalation, support-access, and hidden-target risks approval-gated or denied by backend policy.
