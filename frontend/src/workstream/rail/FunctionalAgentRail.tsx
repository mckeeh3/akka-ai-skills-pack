import { useState } from 'react';
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
  appName?: string;
  userDisplayName: string;
  onSelectAgent?: (functionalAgentId: string) => void;
  onToggleCollapsed?: (collapsed: boolean) => void;
  onSignOut?: () => void;
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
  onToggleCollapsed,
  onSignOut
}: FunctionalAgentRailProps) {
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const entries = visibleRailEntries(agents, selectedFunctionalAgentId, visibleCapabilityIds, accountStatus);

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
          {entries.map((entry) => (
            <FunctionalAgentRailItem key={entry.functionalAgentId} entry={entry} collapsed={collapsed} onSelect={onSelectAgent} />
          ))}
        </ul>
      </nav>
      <div className="sidebar-bottom rail-user-region">
        <button type="button" className="rail-user-button" aria-haspopup="menu" aria-expanded={userMenuOpen} onClick={() => setUserMenuOpen((open) => !open)}>
          <span className="user-avatar" aria-hidden="true">{userDisplayName.slice(0, 1).toUpperCase()}</span>
          {!collapsed && <span>{userDisplayName}</span>}
        </button>
        {userMenuOpen && (
          <div className="rail-user-menu" role="menu">
            <button type="button" role="menuitem">Profile</button>
            <button type="button" role="menuitem">Settings</button>
            <button type="button" role="menuitem" onClick={onSignOut}>Sign out</button>
          </div>
        )}
      </div>
    </aside>
  );
}
