export type AgentAvailability = 'visible' | 'hidden' | 'denied' | 'disabled';
export type AttentionSeverity = 'info' | 'warning' | 'urgent' | 'blocked' | 'critical';

export type FunctionalAgentAttention = {
  count: number;
  severity: AttentionSeverity;
  /** Backend governed-tool that produced this actionable attention summary, e.g. attention.list_rail_summaries. */
  source?: 'attention.list_rail_summaries' | string;
};

export type FunctionalAgentRailAttentionKind = 'background-response' | 'background-activity';

export type FunctionalAgentRailAttention = {
  unseenResponseCount: number;
  severity: AttentionSeverity;
  kind: FunctionalAgentRailAttentionKind;
  lastItemId?: string;
  lastUpdatedAt?: string;
};

export type FunctionalAgentRailAttentionStore = Record<string, FunctionalAgentRailAttention | undefined>;

export type WorkstreamIconDescriptor = {
  workstreamId: string;
  displayName: string;
  iconId: string;
  visualHint: string;
  accentColorToken: string;
  tooltip: string;
  ariaLabel: string;
  assetRef?: string;
};

export type FunctionalAgentSummary = {
  functionalAgentId: string;
  label: string;
  purpose: string;
  /** @deprecated Use workstreamIcon.iconId plus descriptor metadata for shell rendering. */
  icon?: string;
  workstreamIcon: WorkstreamIconDescriptor;
  defaultSurfaceType: string;
  requiredCapabilityIds: string[];
  attention?: FunctionalAgentAttention;
  availability: AgentAvailability;
  deniedReason?: string;
};

export type FunctionalAgentRailEntry = FunctionalAgentSummary & {
  isSelected: boolean;
  visibilityReason: 'has-capability' | 'missing-capability' | 'hidden-by-policy' | 'disabled-account';
  railAttention?: FunctionalAgentRailAttention;
};
