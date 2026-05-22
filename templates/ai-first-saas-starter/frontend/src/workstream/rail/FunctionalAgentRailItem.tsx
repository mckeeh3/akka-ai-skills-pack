import type { FunctionalAgentRailEntry } from '../types';
import { agentDisabledReason, isAgentSelectable } from './railState';

type FunctionalAgentRailItemProps = {
  entry: FunctionalAgentRailEntry;
  collapsed?: boolean;
  onSelect?: (functionalAgentId: string) => void;
};

const severityLabel = (severity: string) => `${severity} attention`;
const iconGlyph = (icon?: string, label?: string) => {
  const icons: Record<string, string> = {
    'user-circle': 'P',
    'my-account': 'M',
    profile: 'P',
    users: 'U',
    bot: 'B',
    'bot-off': 'B',
    timeline: 'T',
    shield: 'G',
    'credit-card': '$',
    'life-ring': 'S'
  };
  return icon ? icons[icon] ?? icon.slice(0, 1).toUpperCase() : label?.slice(0, 1).toUpperCase() ?? 'W';
};

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
        <span className="nav-icon" aria-hidden="true">{iconGlyph(entry.icon, entry.label)}</span>
        <span className="workstream-rail-copy">
          <span id={labelId}>{entry.label}</span>
        </span>
        {entry.attention && (
          <span className={`status-pill ${entry.attention.severity === 'critical' ? 'danger' : entry.attention.severity}`} aria-label={`${entry.attention.count} ${severityLabel(entry.attention.severity)}`}>
            {entry.attention.count}
          </span>
        )}
      </button>
      <span id={reasonId} className="sr-only">{disabledReason}</span>
    </li>
  );
}
