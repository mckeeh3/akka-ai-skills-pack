import type { AccountStatus, FunctionalAgentSummary } from '../types';
import { CollapsedRailToggle } from './CollapsedRailToggle';
import { FunctionalAgentRailItem } from './FunctionalAgentRailItem';
import { myAccountAgentId, toRailEntry, visibleRailEntries } from './railState';

type FunctionalAgentRailProps = {
  agents: FunctionalAgentSummary[];
  selectedFunctionalAgentId?: string;
  visibleCapabilityIds: string[];
  accountStatus?: AccountStatus;
  collapsed?: boolean;
  appName?: string;
  userDisplayName: string;
  onSelectAgent?: (functionalAgentId: string) => void;
  onToggleCollapsed?: (collapsed: boolean) => void;
};

export function FunctionalAgentRail({
  agents,
  selectedFunctionalAgentId,
  visibleCapabilityIds,
  accountStatus = 'active',
  collapsed = false,
  appName = 'Workstream',
  userDisplayName,
  onSelectAgent,
  onToggleCollapsed
}: FunctionalAgentRailProps) {
  const entries = visibleRailEntries(agents, selectedFunctionalAgentId, visibleCapabilityIds, accountStatus);
  const myAccountAgent = agents.find((agent) => agent.functionalAgentId === myAccountAgentId);
  const visibleMyAccountEntry = entries.find((entry) => entry.functionalAgentId === myAccountAgentId);
  const myAccountEntry = visibleMyAccountEntry ?? (myAccountAgent ? toRailEntry(myAccountAgent, selectedFunctionalAgentId, visibleCapabilityIds, accountStatus) : undefined);
  const workAreaEntries = entries.filter((entry) => entry.functionalAgentId !== myAccountAgentId);

  return (
    <aside className={`sidebar workstream-functional-agent-rail ${collapsed ? 'collapsed' : 'expanded'}`} aria-label="Functional agents">
      <div className="rail-top">
        <div className="brand-lockup">
          <span className="brand-mark" aria-hidden="true">AI</span>
          {!collapsed && <strong>{appName}</strong>}
        </div>
        <CollapsedRailToggle collapsed={collapsed} onToggle={onToggleCollapsed} />
      </div>
      <nav aria-label="Functional agent work areas">
        <ul id="workstream-functional-agent-rail-list" className="nav-list workstream-agent-list">
          {workAreaEntries.map((entry) => (
            <FunctionalAgentRailItem key={entry.functionalAgentId} entry={entry} collapsed={collapsed} onSelect={onSelectAgent} />
          ))}
        </ul>
      </nav>
      <div className="sidebar-bottom rail-user-region" aria-label="Current user workstream: My Account">
        {myAccountEntry ? (
          <ul className="nav-list workstream-agent-list rail-user-workstream-list">
            <FunctionalAgentRailItem entry={{ ...myAccountEntry, label: userDisplayName }} collapsed={collapsed} onSelect={onSelectAgent} />
          </ul>
        ) : (
          <button type="button" className="rail-user-button" disabled>
            <span className="user-avatar" aria-hidden="true">{userDisplayName.slice(0, 1).toUpperCase()}</span>
            {!collapsed && <span>{userDisplayName}</span>}
          </button>
        )}
      </div>
    </aside>
  );
}
