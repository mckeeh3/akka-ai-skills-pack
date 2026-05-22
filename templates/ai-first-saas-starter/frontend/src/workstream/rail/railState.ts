import type { AccountStatus, FunctionalAgentRailEntry, FunctionalAgentSummary } from '../types';

export const myAccountAgentId = 'agent-my-account';

export function hasRequiredCapabilities(agent: FunctionalAgentSummary, visibleCapabilityIds: string[]): boolean {
  return agent.requiredCapabilityIds.every((capabilityId) => visibleCapabilityIds.includes(capabilityId));
}

export function toRailEntry(agent: FunctionalAgentSummary, selectedFunctionalAgentId: string | undefined, visibleCapabilityIds: string[], accountStatus: AccountStatus = 'active'): FunctionalAgentRailEntry {
  const hasCapabilities = hasRequiredCapabilities(agent, visibleCapabilityIds);
  const visibilityReason: FunctionalAgentRailEntry['visibilityReason'] =
    accountStatus === 'disabled'
      ? 'disabled-account'
      : agent.availability === 'hidden'
        ? 'hidden-by-policy'
        : hasCapabilities
          ? 'has-capability'
          : 'missing-capability';

  return {
    ...agent,
    isSelected: agent.functionalAgentId === selectedFunctionalAgentId,
    visibilityReason
  };
}

export function visibleRailEntries(agents: FunctionalAgentSummary[], selectedFunctionalAgentId: string | undefined, visibleCapabilityIds: string[], accountStatus: AccountStatus = 'active'): FunctionalAgentRailEntry[] {
  return agents
    .map((agent) => toRailEntry(agent, selectedFunctionalAgentId, visibleCapabilityIds, accountStatus))
    .filter((entry) => entry.availability === 'visible' && entry.visibilityReason === 'has-capability');
}

export function isAgentSelectable(entry: FunctionalAgentRailEntry): boolean {
  return entry.availability === 'visible' && entry.visibilityReason === 'has-capability';
}

export function agentDisabledReason(entry: FunctionalAgentRailEntry): string | undefined {
  if (isAgentSelectable(entry)) return undefined;
  if (entry.deniedReason) return entry.deniedReason;
  if (entry.availability === 'disabled' || entry.visibilityReason === 'disabled-account') return 'This functional agent is disabled for the current account or context.';
  if (entry.availability === 'denied' || entry.visibilityReason === 'missing-capability') return `Missing required capability: ${entry.requiredCapabilityIds.join(', ')}`;
  if (entry.visibilityReason === 'hidden-by-policy') return 'Hidden by tenant policy.';
  return 'Unavailable in the selected context.';
}

export function defaultSelectableAgentId(agents: FunctionalAgentSummary[], visibleCapabilityIds: string[], accountStatus: AccountStatus = 'active'): string | undefined {
  const selectableEntries = visibleRailEntries(agents, undefined, visibleCapabilityIds, accountStatus).filter(isAgentSelectable);
  return selectableEntries.find((entry) => entry.functionalAgentId !== myAccountAgentId)?.functionalAgentId ?? selectableEntries[0]?.functionalAgentId;
}
