import type { FunctionalAgentRailEntry, WorkstreamIconDescriptor } from '../types';
import { agentDisabledReason, isAgentSelectable } from './railState';
import { WorkstreamIcon } from './WorkstreamIcon';

type FunctionalAgentRailItemProps = {
  entry: FunctionalAgentRailEntry;
  collapsed?: boolean;
  onSelect?: (functionalAgentId: string) => void;
};

const severityLabel = (severity: string) => `${severity} backend-derived actionable attention`;
const attentionSeverityClass = (severity: string) => (severity === 'critical' || severity === 'urgent' || severity === 'blocked' ? 'danger' : severity);

export function FunctionalAgentRailItem({ entry, collapsed = false, onSelect }: FunctionalAgentRailItemProps) {
  const selectable = isAgentSelectable(entry);
  const disabledReason = agentDisabledReason(entry);
  const labelId = `rail-agent-${entry.functionalAgentId}-label`;
  const reasonId = `rail-agent-${entry.functionalAgentId}-reason`;
  const tooltipId = `rail-agent-${entry.functionalAgentId}-tooltip`;
  const fallbackIcon: WorkstreamIconDescriptor = {
    workstreamId: entry.functionalAgentId,
    displayName: entry.label,
    iconId: entry.icon ?? 'workstream',
    visualHint: entry.icon ?? 'workstream',
    accentColorToken: 'accent-workstream',
    tooltip: `Open ${entry.label} workstream`,
    ariaLabel: `Open ${entry.label} workstream`
  };
  const workstreamIcon = entry.workstreamIcon ?? fallbackIcon;
  const describedBy = [workstreamIcon.tooltip ? tooltipId : undefined, disabledReason ? reasonId : undefined].filter(Boolean).join(' ') || undefined;
  const unseenResponseCount = entry.railAttention?.unseenResponseCount ?? 0;
  const unseenResponseLabel = unseenResponseCount === 1 ? '1 unseen response' : `${unseenResponseCount} unseen responses`;

  return (
    <li className={`workstream-rail-item ${entry.isSelected ? 'selected' : ''} ${entry.availability}`}>
      <button
        type="button"
        className={`nav-item workstream-agent-button ${entry.isSelected ? 'active' : ''}`.trim()}
        aria-current={entry.isSelected ? 'page' : undefined}
        aria-label={workstreamIcon.ariaLabel}
        aria-describedby={describedBy}
        disabled={!selectable}
        onClick={() => selectable && onSelect?.(entry.functionalAgentId)}
      >
        <span
          className="nav-icon workstream-icon"
          aria-hidden="true"
          data-workstream-icon-id={workstreamIcon.iconId}
          data-accent-color-token={workstreamIcon.accentColorToken}
        >
          <WorkstreamIcon descriptor={workstreamIcon} />
        </span>
        <span id={tooltipId} className="workstream-icon-tooltip" role="tooltip">{workstreamIcon.tooltip}</span>
        <span className="workstream-rail-copy">
          <span id={labelId}>{entry.label}</span>
        </span>
        {entry.attention && (
          <span className={`status-pill ${attentionSeverityClass(entry.attention.severity)}`} aria-label={`${entry.attention.count} ${severityLabel(entry.attention.severity)}`} data-attention-kind="backend-actionable" data-attention-source={entry.attention.source ?? 'attention.list_rail_summaries'}>
            {entry.attention.count}
          </span>
        )}
        {entry.railAttention && unseenResponseCount > 0 && (
          <span className={`rail-unseen-response-badge ${attentionSeverityClass(entry.railAttention.severity)}`} aria-label={unseenResponseLabel} data-attention-kind={entry.railAttention.kind}>
            {unseenResponseCount > 9 ? '9+' : unseenResponseCount}
          </span>
        )}
      </button>
      <span id={reasonId} className="sr-only">{disabledReason}</span>
    </li>
  );
}
