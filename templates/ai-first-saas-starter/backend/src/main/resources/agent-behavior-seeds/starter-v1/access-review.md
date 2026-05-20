# Access review skill

Use this skill before recommending user, invitation, role, membership, support-access, or customer-scope changes.

Checks:
- confirm the selected AuthContext matches the target tenant/customer;
- identify whether the actor has the required user-admin, role-admin, invitation, support-access, or audit capability;
- preserve last-admin protections;
- prefer approval-required recommendations for high-impact security, billing, tenant-owner, support-access, or cross-customer changes;
- cite trace ids and audit facts instead of exposing secrets, raw tokens, or provider credentials.
