import type { FunctionalAgentSummary } from '../types';

export const foundationFunctionalAgents: FunctionalAgentSummary[] = [
  {
    functionalAgentId: 'agent-my-account',
    label: 'My Account',
    purpose: 'Review signed-in account, selected context, profile, settings, sign-out action, and authority basis.',
    icon: 'my-account',
    workstreamIcon: {
      workstreamId: 'workstream-my-account',
      displayName: 'My Account',
      iconId: 'my-account',
      visualHint: 'user profile settings',
      accentColorToken: 'accent-account',
      tooltip: 'Open My Account from the signed-in user tile',
      ariaLabel: 'Open My Account workstream from the signed-in user tile'
    },
    defaultSurfaceType: 'markdown_response',
    requiredCapabilityIds: ['profile.read'],
    availability: 'visible'
  },
  {
    functionalAgentId: 'agent-user-admin',
    label: 'User Admin',
    purpose: 'Manage invitations, memberships, roles, and access review.',
    icon: 'users',
    workstreamIcon: {
      workstreamId: 'workstream-user-admin',
      displayName: 'User Admin',
      iconId: 'users-admin',
      visualHint: 'users person admin',
      accentColorToken: 'accent-users',
      tooltip: 'Open User Admin workstream',
      ariaLabel: 'Open User Admin workstream'
    },
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
    workstreamIcon: {
      workstreamId: 'workstream-agent-admin',
      displayName: 'Agent Admin',
      iconId: 'bot-spark',
      visualHint: 'bot spark agent',
      accentColorToken: 'accent-agents',
      tooltip: 'Open Agent Admin workstream',
      ariaLabel: 'Open Agent Admin workstream'
    },
    defaultSurfaceType: 'markdown_response',
    requiredCapabilityIds: ['agent_admin.submit_turn', 'agent_admin.list_definitions', 'agent_admin.get_definition', 'agent_admin.draft_behavior_change', 'agent_admin.simulate_tool_boundary', 'agent_admin.get_model_ref'],
    attention: { count: 4, severity: 'critical' },
    availability: 'visible'
  },
  {
    functionalAgentId: 'agent-audit-trace',
    label: 'Audit/Trace',
    purpose: 'Inspect admin audit events, work traces, decisions, and evidence links.',
    icon: 'timeline',
    workstreamIcon: {
      workstreamId: 'workstream-audit-trace',
      displayName: 'Audit/Trace',
      iconId: 'timeline-search',
      visualHint: 'timeline search trace',
      accentColorToken: 'accent-audit',
      tooltip: 'Open Audit/Trace workstream',
      ariaLabel: 'Open Audit/Trace workstream'
    },
    defaultSurfaceType: 'markdown_response',
    requiredCapabilityIds: ['audit.trace.read'],
    availability: 'visible'
  },
  {
    functionalAgentId: 'agent-governance-policy',
    label: 'Governance/Policy',
    purpose: 'Review policy proposals, approval gates, simulations, and diffs.',
    icon: 'shield',
    workstreamIcon: {
      workstreamId: 'workstream-governance-policy',
      displayName: 'Governance/Policy',
      iconId: 'shield-checklist',
      visualHint: 'shield checklist policy',
      accentColorToken: 'accent-governance',
      tooltip: 'Open Governance/Policy workstream',
      ariaLabel: 'Open Governance/Policy workstream'
    },
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
    workstreamIcon: {
      workstreamId: 'workstream-billing',
      displayName: 'Billing',
      iconId: 'credit-card-hidden',
      visualHint: 'credit card billing hidden',
      accentColorToken: 'accent-billing',
      tooltip: 'Billing workstream hidden until billing authority is granted',
      ariaLabel: 'Billing workstream unavailable'
    },
    defaultSurfaceType: 'dashboard',
    requiredCapabilityIds: ['billing.read'],
    availability: 'hidden'
  },
  {
    functionalAgentId: 'agent-support-access',
    label: 'Support Access',
    purpose: 'Inspect active break-glass support access and expiry.',
    icon: 'life-ring',
    workstreamIcon: {
      workstreamId: 'workstream-support-access',
      displayName: 'Support Access',
      iconId: 'life-ring-disabled',
      visualHint: 'life ring support disabled',
      accentColorToken: 'accent-support',
      tooltip: 'Support Access workstream disabled for this tenant',
      ariaLabel: 'Support Access workstream disabled'
    },
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
  workstreamIcon: {
    workstreamId: 'workstream-agent-admin-denied-example',
    displayName: 'Agent Admin denied example',
    iconId: 'bot-off-denied',
    visualHint: 'bot denied agent',
    accentColorToken: 'accent-denied',
    tooltip: 'Agent Admin workstream denied without agent governance capability',
    ariaLabel: 'Agent Admin workstream denied'
  },
  defaultSurfaceType: 'governance-diff',
  requiredCapabilityIds: ['agent_admin.list_definitions'],
  availability: 'denied',
  deniedReason: 'Agent governance requires tenant-scoped agent_admin.list_definitions capability.'
};
export const hiddenFunctionalAgentExample = foundationFunctionalAgents.find((agent) => agent.availability === 'hidden')!;
