# Tenant Role and Capability Catalog reference

Default starter roles:
- Tenant Admin: manages tenant user administration, invitations, roles, access reviews, and tenant settings.
- Customer Admin: manages allowed customer-scoped users and memberships only.
- Auditor: reads scoped audit, trace, and access-review evidence without mutation authority.
- Support Viewer: may inspect support-access status where an explicit grant allows it.

Capability ids remain authoritative in backend policy. This reference explains role intent only.

Structured surface note: User Admin role, membership, support-access, Organization, and customer administration is performed through backend-authorized surfaces such as user directory, role-change preview, membership status confirmation, Organization/customer directories, and dedicated create/edit/confirmation forms. Reference text may help explain which surface is appropriate, but it never grants capability or performs the change.

Confirmed chat tool plan reference: the first-pass User Admin chat plan may mention `roles=[TENANT_ADMIN]` only for the Organization Admin invitation step bound to a newly created Organization. Role text remains explanatory; backend capability `saas_owner.organization_admin.invite`, confirmation, selected AuthContext, and command handlers decide whether the invitation can be created.
