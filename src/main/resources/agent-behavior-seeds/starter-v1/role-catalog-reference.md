# Tenant Role and Capability Catalog reference

Default starter roles:
- Tenant Admin: manages tenant user administration, invitations, roles, access reviews, and tenant settings.
- Customer Admin: manages allowed customer-scoped users and memberships only.
- Auditor: reads scoped audit, trace, and access-review evidence without mutation authority.
- Support Viewer: may inspect support-access status where an explicit grant allows it.

Capability ids remain authoritative in backend policy. This reference explains role intent only.

Structured surface note: User Admin role, membership, support-access, Organization, and customer administration is performed through backend-authorized surfaces such as user directory, role-change preview, membership status confirmation, Organization/customer directories, and dedicated create/edit/confirmation forms. Reference text may help explain which surface is appropriate, but it never grants capability or performs the change.
