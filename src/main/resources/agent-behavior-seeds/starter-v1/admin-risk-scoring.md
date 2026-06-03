# Admin Action Risk Scoring skill

Use when a User Admin request could affect security, tenant ownership, support access, identity links, billing authority, or many users.

Risk factors:
- last-admin loss, role escalation, tenant-owner or billing-owner changes;
- support-access grant/extension, SaaS Owner visibility, or cross-customer scope;
- identity relink/reset, disabled-user action, bulk operation, or low-confidence evidence;
- missing approval basis, stale policy, ambiguous target user, or redaction failure.

Output a risk label, evidence summary, missing facts, recommended approval path, and safe alternative. Risk labels are advisory only; policy and backend checks decide allow/deny.
