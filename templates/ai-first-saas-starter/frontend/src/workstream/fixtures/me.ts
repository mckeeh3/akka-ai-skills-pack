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
  capabilityIds: ['profile.read', 'profile.update', 'my_account.view_summary', 'my_account.view_context', 'my_account.update_profile_settings', 'my_account.list_next_steps', 'my_account.open_authorized_workstream', 'my_account.ask_agent', 'my_account.view_own_trace_refs', 'secure-tenant-user-foundation', 'admin.users.dashboard.read', 'admin.users.search', 'admin.users.detail.read', 'admin.invitations.create', 'admin.invitations.resend', 'admin.invitations.revoke', 'admin.users.profile.patch', 'admin.roles.replace', 'admin.access_review.resolve', 'admin.audit.read', 'admin.users.read', 'admin.users.invite', 'admin.roles.update', 'audit.trace.read', 'governance.policy.read', 'governance.policy.propose', 'governance-decisions-audit', 'decision.approve', 'workflow.read', 'agent_admin.submit_turn', 'agent_admin.list_definitions', 'agent_admin.get_definition', 'agent_admin.get_prompt_version', 'agent_admin.get_skill_version', 'agent_admin.get_reference_version', 'agent_admin.get_manifest', 'agent_admin.get_model_ref', 'agent_admin.get_tool_boundary', 'agent_admin.simulate_tool_boundary', 'agent_admin.draft_behavior_change', 'agent_admin.submit_behavior_change_for_review', 'agent_admin.approve_behavior_change', 'agent_admin.reject_behavior_change', 'agent_admin.activate_behavior_change', 'agent_admin.cancel_behavior_change', 'agent_admin.list_seed_material', 'agent_admin.reseed_missing_defaults', 'agent_admin.start_behavior_review_task', 'agent_admin.get_behavior_review_task', 'agent_admin.cancel_behavior_review_task']
};

export const memberAuthContext: AuthContext = {
  selectedContextId: 'ctx-member-acme',
  tenantId: 'tenant-acme',
  tenantName: 'Acme Tenant',
  customerId: 'customer-northwind',
  customerName: 'Northwind Customer',
  membershipId: 'membership-member',
  roleIds: ['member'],
  capabilityIds: ['profile.read', 'my_account.view_summary', 'my_account.view_context', 'my_account.update_profile_settings', 'my_account.list_next_steps', 'my_account.open_authorized_workstream', 'my_account.ask_agent', 'my_account.view_own_trace_refs', 'workflow.read', 'decision.read']
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
