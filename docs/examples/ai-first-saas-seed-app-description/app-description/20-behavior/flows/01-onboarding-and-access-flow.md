# Flow: Onboarding and Access

1. Tenant admin invites a user to a tenant with one or more roles.
2. System creates a pending invitation and emits an audit event.
3. Invited user authenticates through configured identity flow.
4. System verifies invitation token, tenant status, and role assignment authority.
5. Membership becomes active after acceptance.
6. User can switch among active tenant memberships.
7. Removed/suspended memberships immediately lose tenant access.
8. Expired invitations cannot be accepted and are marked by timed action.
