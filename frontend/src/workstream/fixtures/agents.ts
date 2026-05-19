import type { FunctionalAgentSummary } from '../types';

export const foundationFunctionalAgents: FunctionalAgentSummary[] = [
  {
    functionalAgentId: 'agent-access-profile',
    label: 'Access/Profile',
    purpose: 'Review signed-in account, selected context, settings, and authority basis.',
    icon: 'user-circle',
    defaultSurfaceType: 'detail-edit',
    requiredCapabilityIds: ['profile.read'],
    availability: 'visible'
  },
  {
    functionalAgentId: 'agent-user-admin',
    label: 'User Admin',
    purpose: 'Manage invitations, memberships, roles, and access review.',
    icon: 'users',
    defaultSurfaceType: 'dashboard',
    requiredCapabilityIds: ['secure-tenant-user-foundation'],
    attention: { count: 2, severity: 'warning' },
    availability: 'visible'
  },
  {
    functionalAgentId: 'agent-agent-admin',
    label: 'Agent Admin',
    purpose: 'Govern agent definitions, prompts, skill manifests, and tool boundaries.',
    icon: 'bot',
    defaultSurfaceType: 'dashboard',
    requiredCapabilityIds: ['agent.definitions.manage', 'agent.prompts.govern', 'agent.skills.govern', 'agent.tool_boundaries.manage', 'agent.models.read', 'agent.runtime.test'],
    attention: { count: 4, severity: 'critical' },
    availability: 'visible'
  },
  {
    functionalAgentId: 'agent-agent-admin-denied-example',
    label: 'Agent Admin denied example',
    purpose: 'Demonstrate safe denied rail state for agent governance when capability scope is absent.',
    icon: 'bot-off',
    defaultSurfaceType: 'governance-diff',
    requiredCapabilityIds: ['agent.definitions.manage'],
    availability: 'denied',
    deniedReason: 'Agent governance requires tenant-scoped agent.definitions.manage capability.'
  },
  {
    functionalAgentId: 'agent-audit-trace',
    label: 'Audit/Trace',
    purpose: 'Inspect admin audit events, work traces, decisions, and evidence links.',
    icon: 'timeline',
    defaultSurfaceType: 'audit-timeline',
    requiredCapabilityIds: ['audit.trace.read'],
    availability: 'visible'
  },
  {
    functionalAgentId: 'agent-governance-policy',
    label: 'Governance/Policy',
    purpose: 'Review policy proposals, approval gates, simulations, and diffs.',
    icon: 'shield',
    defaultSurfaceType: 'governance-diff',
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

export const deniedFunctionalAgentExample = foundationFunctionalAgents.find((agent) => agent.availability === 'denied')!;
export const hiddenFunctionalAgentExample = foundationFunctionalAgents.find((agent) => agent.availability === 'hidden')!;
