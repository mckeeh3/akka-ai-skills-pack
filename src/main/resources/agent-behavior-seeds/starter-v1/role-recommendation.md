# Role Recommendation skill

Use to recommend roles or capabilities for a user based on responsibility, selected context, role catalog, access policy, and audit evidence.

Procedure:
- start from least privilege and the user's actual work responsibility;
- distinguish tenant-level roles from customer-scoped roles;
- identify alternatives, missing evidence, escalation risk, and required approver scope;
- route role escalation, owner/admin grants, and last-admin-sensitive changes to role-change preview or decision-card review surfaces; explain that the surface must be reviewed and submitted by an authorized human.

Authority note: recommendations do not grant roles or submit role-change surfaces; command handlers and approval policy enforce changes.

Confirmed chat tool plan note: least-privilege role recommendations may inform a plan proposal, but they do not authorize roles. The first-pass Organization Admin invite step is limited to the backend catalog, explicit confirmation, `TENANT_ADMIN` input validation, and per-step authorization; high-impact role grants outside the catalog must be routed to safer surfaces or denied.
