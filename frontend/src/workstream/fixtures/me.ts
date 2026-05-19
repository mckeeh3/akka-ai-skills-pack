import type { AuthContext, MeResponse } from '../types';
import { foundationFunctionalAgents } from './agents';

const baseTimeZone = 'America/New_York';

export const tenantAdminAuthContext: AuthContext = {
  selectedContextId: 'ctx-tenant-admin-acme',
  tenantId: 'tenant-acme',
  tenantName: 'Acme Tenant',
  customerId: 'customer-northwind',
  customerName: 'Northwind Customer',
  membershipId: 'membership-admin',
  roleIds: ['tenant-admin', 'policy-owner'],
  capabilityIds: ['profile.read', 'profile.update', 'secure-tenant-user-foundation', 'admin.users.read', 'admin.users.invite', 'admin.roles.update', 'audit.trace.read', 'governance.policy.read', 'governance.policy.propose', 'governance-decisions-audit', 'decision.approve', 'workflow.read']
};

export const memberAuthContext: AuthContext = {
  selectedContextId: 'ctx-member-acme',
  tenantId: 'tenant-acme',
  tenantName: 'Acme Tenant',
  customerId: 'customer-northwind',
  customerName: 'Northwind Customer',
  membershipId: 'membership-member',
  roleIds: ['member'],
  capabilityIds: ['profile.read', 'workflow.read', 'decision.read']
};

export const auditorSupportAuthContext: AuthContext = {
  selectedContextId: 'ctx-auditor-support-acme',
  tenantId: 'tenant-acme',
  tenantName: 'Acme Tenant',
  membershipId: 'membership-auditor',
  roleIds: ['auditor', 'support'],
  capabilityIds: ['profile.read', 'audit.trace.read', 'support.access.read'],
  supportAccess: { active: true, reason: 'Customer-approved incident review', expiresAt: '2026-06-01T12:00:00.000Z' }
};

function responseFor(accountId: string, email: string, displayName: string, status: MeResponse['account']['status'], selectedAuthContext: AuthContext, visibleCapabilityIds = selectedAuthContext.capabilityIds): MeResponse {
  return {
    account: { accountId, email, displayName, status },
    profile: { displayName, locale: 'en-US', timeZone: baseTimeZone },
    settings: { preferredColorMode: 'system' },
    memberships: [
      {
        membershipId: selectedAuthContext.membershipId,
        tenantId: selectedAuthContext.tenantId,
        tenantName: selectedAuthContext.tenantName,
        customerId: selectedAuthContext.customerId,
        customerName: selectedAuthContext.customerName,
        status: status === 'disabled' ? 'disabled' : 'active',
        roleIds: selectedAuthContext.roleIds,
        capabilityIds: selectedAuthContext.capabilityIds
      }
    ],
    selectedAuthContext,
    availableAuthContexts: [selectedAuthContext],
    visibleCapabilityIds,
    functionalAgents: foundationFunctionalAgents
  };
}

export const meTenantAdmin = responseFor('acct-admin', 'admin@example.test', 'Tenant Admin', 'active', tenantAdminAuthContext);
export const meRegularMember = responseFor('acct-member', 'member@example.test', 'Regular Member', 'active', memberAuthContext);
export const meAuditorSupport = responseFor('acct-auditor', 'auditor@example.test', 'Auditor Support', 'active', auditorSupportAuthContext);
export const meDisabledUser = responseFor('acct-disabled', 'disabled@example.test', 'Disabled User', 'disabled', { ...memberAuthContext, selectedContextId: 'ctx-disabled-member', membershipId: 'membership-disabled' }, []);

export const meForbiddenNoMembership: MeResponse = {
  account: { accountId: 'acct-nomembership', email: 'nomembership@example.test', displayName: 'No Membership', status: 'active' },
  profile: { displayName: 'No Membership', locale: 'en-US', timeZone: baseTimeZone },
  settings: { preferredColorMode: 'system' },
  memberships: [],
  selectedAuthContext: { selectedContextId: 'ctx-forbidden-none', tenantId: 'tenant-acme', tenantName: 'Acme Tenant', membershipId: 'none', roleIds: [], capabilityIds: [] },
  availableAuthContexts: [],
  visibleCapabilityIds: [],
  functionalAgents: foundationFunctionalAgents.map((agent) => ({ ...agent, availability: agent.availability === 'hidden' ? 'hidden' : 'denied', deniedReason: 'No active membership in this tenant.' }))
};

export const meFixtures = {
  admin: meTenantAdmin,
  member: meRegularMember,
  auditorSupport: meAuditorSupport,
  disabled: meDisabledUser,
  forbiddenNoMembership: meForbiddenNoMembership
};
