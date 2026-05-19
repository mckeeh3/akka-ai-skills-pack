import type { AccountStatus, FunctionalAgentSummary } from '../types';
import { CollapsedRailToggle } from './CollapsedRailToggle';
import { FunctionalAgentRailItem } from './FunctionalAgentRailItem';
import { visibleRailEntries } from './railState';

type FunctionalAgentRailProps = {
  agents: FunctionalAgentSummary[];
  selectedFunctionalAgentId?: string;
  visibleCapabilityIds: string[];
  accountStatus?: AccountStatus;
  collapsed?: boolean;
  onSelectAgent?: (functionalAgentId: string) => void;
  onToggleCollapsed?: (collapsed: boolean) => void;
};

export function FunctionalAgentRail({
  agents,
  selectedFunctionalAgentId,
  visibleCapabilityIds,
  accountStatus = 'active',
  collapsed = false,
  onSelectAgent,
  onToggleCollapsed
}: FunctionalAgentRailProps) {
  const entries = visibleRailEntries(agents, selectedFunctionalAgentId, visibleCapabilityIds, accountStatus);

  return (
    <aside className={`sidebar workstream-functional-agent-rail ${collapsed ? 'collapsed' : 'expanded'}`} aria-label="Functional agents">
      <div className="brand-lockup">
        <span className="brand-mark" aria-hidden="true">AI</span>
        {!collapsed && (
          <span>
            <strong>Workstream</strong>
            <span>Role-authorized agents</span>
          </span>
        )}
      </div>
      <CollapsedRailToggle collapsed={collapsed} onToggle={onToggleCollapsed} />
      <nav aria-label="Functional agent work areas">
        <ul id="workstream-functional-agent-rail-list" className="nav-list workstream-agent-list">
          {entries.map((entry) => (
            <FunctionalAgentRailItem key={entry.functionalAgentId} entry={entry} collapsed={collapsed} onSelect={onSelectAgent} />
          ))}
        </ul>
      </nav>
      {!collapsed && (
        <div className="sidebar-bottom">
          <p className="notification-summary">
            <span className="notification-dot" aria-hidden="true">{entries.filter((entry) => entry.attention).length}</span>
            Attention indicators are scoped by the selected AuthContext and browser-safe capabilities.
          </p>
        </div>
      )}
    </aside>
  );
}
