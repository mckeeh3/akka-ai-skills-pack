export type AgentAvailability = 'visible' | 'hidden' | 'denied' | 'disabled';
export type AttentionSeverity = 'info' | 'warning' | 'critical';

export type FunctionalAgentAttention = {
  count: number;
  severity: AttentionSeverity;
};

export type FunctionalAgentSummary = {
  functionalAgentId: string;
  label: string;
  purpose: string;
  icon?: string;
  defaultSurfaceType: string;
  requiredCapabilityIds: string[];
  attention?: FunctionalAgentAttention;
  availability: AgentAvailability;
  deniedReason?: string;
};

export type FunctionalAgentRailEntry = FunctionalAgentSummary & {
  isSelected: boolean;
  visibilityReason: 'has-capability' | 'missing-capability' | 'hidden-by-policy' | 'disabled-account';
};
