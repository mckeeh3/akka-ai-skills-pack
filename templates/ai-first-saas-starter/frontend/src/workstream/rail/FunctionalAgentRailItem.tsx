import type { FunctionalAgentRailEntry } from '../types';
import { agentDisabledReason, isAgentSelectable } from './railState';

type FunctionalAgentRailItemProps = {
  entry: FunctionalAgentRailEntry;
  collapsed?: boolean;
  onSelect?: (functionalAgentId: string) => void;
};

const severityLabel = (severity: string) => `${severity} attention`;

export function FunctionalAgentRailItem({ entry, collapsed = false, onSelect }: FunctionalAgentRailItemProps) {
  const selectable = isAgentSelectable(entry);
  const disabledReason = agentDisabledReason(entry);
  const labelId = `rail-agent-${entry.functionalAgentId}-label`;
  const reasonId = `rail-agent-${entry.functionalAgentId}-reason`;

  return (
    <li className={`workstream-rail-item ${entry.isSelected ? 'selected' : ''} ${entry.availability}`}>
      <button
        type="button"
        className={`nav-item workstream-agent-button ${entry.isSelected ? 'active' : ''}`.trim()}
        aria-current={entry.isSelected ? 'page' : undefined}
        aria-labelledby={labelId}
        aria-describedby={disabledReason ? reasonId : undefined}
        disabled={!selectable}
        onClick={() => selectable && onSelect?.(entry.functionalAgentId)}
      >
        <span className="nav-icon" aria-hidden="true">{entry.icon ?? 'agent'}</span>
        {!collapsed && (
          <span className="workstream-rail-copy">
            <span id={labelId}>{entry.label}</span>
            <small>{entry.purpose}</small>
          </span>
        )}
        {entry.attention && (
          <span className={`status-pill ${entry.attention.severity === 'critical' ? 'danger' : entry.attention.severity}`} aria-label={`${entry.attention.count} ${severityLabel(entry.attention.severity)}`}>
            {entry.attention.count}
          </span>
        )}
      </button>
      {!collapsed && disabledReason && <p id={reasonId} className="fixture-defer-note">{disabledReason}</p>}
    </li>
  );
}
