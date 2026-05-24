import type { FunctionalAgentSummary } from '../types';

export const foundationFunctionalAgents: FunctionalAgentSummary[] = [
  {
    functionalAgentId: 'agent-my-account',
    label: 'My Account',
    purpose: 'Review signed-in account, selected context, profile, settings, sign-out action, and authority basis.',
    icon: 'my-account',
    defaultSurfaceType: 'markdown_response',
    requiredCapabilityIds: ['profile.read'],
    availability: 'visible'
  },
  {
    functionalAgentId: 'agent-user-admin',
    label: 'User Admin',
    purpose: 'Manage invitations, memberships, roles, and access review.',
    icon: 'users',
    defaultSurfaceType: 'markdown_response',
    requiredCapabilityIds: ['secure-tenant-user-foundation', 'admin.users.dashboard.read', 'admin.users.search', 'admin.users.detail.read', 'admin.invitations.create', 'admin.audit.read'],
    attention: { count: 2, severity: 'warning' },
    availability: 'visible'
  },
  {
    functionalAgentId: 'agent-agent-admin',
    label: 'Agent Admin',
    purpose: 'Govern agent definitions, prompts, skill manifests, and tool boundaries.',
    icon: 'bot',
    defaultSurfaceType: 'markdown_response',
    requiredCapabilityIds: ['agent.definitions.manage', 'agent.prompts.govern', 'agent.skills.govern', 'agent.tool_boundaries.manage', 'agent.models.read', 'agent.runtime.test'],
    attention: { count: 4, severity: 'critical' },
    availability: 'visible'
  },
  {
    functionalAgentId: 'agent-audit-trace',
    label: 'Audit/Trace',
    purpose: 'Inspect admin audit events, work traces, decisions, and evidence links.',
    icon: 'timeline',
    defaultSurfaceType: 'markdown_response',
    requiredCapabilityIds: ['audit.trace.read'],
    availability: 'visible'
  },
  {
    functionalAgentId: 'agent-governance-policy',
    label: 'Governance/Policy',
    purpose: 'Review policy proposals, approval gates, simulations, and diffs.',
    icon: 'shield',
    defaultSurfaceType: 'markdown_response',
    requiredCapabilityIds: ['governance.policy.read'],
    attention: { count: 1, severity: 'critical' },
    availability: 'visible'
  },
  {
    functionalAgentId: 'agent-billing',
    label: 'Billing',
    purpose: 'Review subscription status and billing-owner-only administration.',
    icon: 'credit-card',
    defaultSurfaceType: 'dashboard',
    requiredCapabilityIds: ['billing.read'],
    availability: 'hidden'
  },
  {
    functionalAgentId: 'agent-support-access',
    label: 'Support Access',
    purpose: 'Inspect active break-glass support access and expiry.',
    icon: 'life-ring',
    defaultSurfaceType: 'detail-edit',
    requiredCapabilityIds: ['support.access.read'],
    availability: 'disabled',
    deniedReason: 'Support access is not active for this tenant.'
  }
];

export const deniedFunctionalAgentExample: FunctionalAgentSummary = {
  functionalAgentId: 'agent-agent-admin-denied-example',
  label: 'Agent Admin denied example',
  purpose: 'Demonstrate safe denied rail state for agent governance when capability scope is absent; not part of the default five core v0 rail.',
  icon: 'bot-off',
  defaultSurfaceType: 'governance-diff',
  requiredCapabilityIds: ['agent.definitions.manage'],
  availability: 'denied',
  deniedReason: 'Agent governance requires tenant-scoped agent.definitions.manage capability.'
};
export const hiddenFunctionalAgentExample = foundationFunctionalAgents.find((agent) => agent.availability === 'hidden')!;
