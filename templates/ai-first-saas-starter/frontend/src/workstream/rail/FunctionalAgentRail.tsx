import type { AccountStatus, FunctionalAgentRailAttentionStore, FunctionalAgentSummary } from '../types';
import { CollapsedRailToggle } from './CollapsedRailToggle';
import { FunctionalAgentRailItem } from './FunctionalAgentRailItem';
import { visibleRailEntries } from './railState';

type FunctionalAgentRailProps = {
  agents: FunctionalAgentSummary[];
  selectedFunctionalAgentId?: string;
  visibleCapabilityIds: string[];
  accountStatus?: AccountStatus;
  collapsed?: boolean;
  mobileOpen?: boolean;
  appName?: string;
  userDisplayName: string;
  railAttentionByAgentId?: FunctionalAgentRailAttentionStore;
  onSelectAgent?: (functionalAgentId: string) => void;
  onToggleCollapsed?: (collapsed: boolean) => void;
  onSignOut?: () => void;
};

const myAccountFunctionalAgentId = 'agent-my-account';

export function FunctionalAgentRail({
  agents,
  selectedFunctionalAgentId,
  visibleCapabilityIds,
  accountStatus = 'active',
  collapsed = false,
  mobileOpen = false,
  appName = 'Workstream',
  userDisplayName,
  railAttentionByAgentId = {},
  onSelectAgent,
  onToggleCollapsed
}: FunctionalAgentRailProps) {
  const visibleEntries = visibleRailEntries(agents, selectedFunctionalAgentId, visibleCapabilityIds, accountStatus, railAttentionByAgentId);
  const entries = visibleEntries.filter((entry) => entry.functionalAgentId !== myAccountFunctionalAgentId);
  const myAccountEntry = visibleEntries.find((entry) => entry.functionalAgentId === myAccountFunctionalAgentId);
  const myAccountSelected = selectedFunctionalAgentId === myAccountFunctionalAgentId;

  function openMyAccount() {
    if (myAccountEntry) onSelectAgent?.(myAccountFunctionalAgentId);
  }

  return (
    <aside id="workstream-functional-agent-rail" className={`sidebar workstream-functional-agent-rail ${collapsed ? 'collapsed' : 'expanded'} ${mobileOpen ? 'open' : ''}`.trim()} aria-label="Functional agents">
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
        <button
          type="button"
          className={`rail-user-button ${myAccountSelected ? 'active' : ''}`.trim()}
          aria-current={myAccountSelected ? 'page' : undefined}
          aria-label={`Open My Account workstream for ${userDisplayName}`}
          disabled={!myAccountEntry}
          onClick={openMyAccount}
        >
          <span className="user-avatar" aria-hidden="true">{userDisplayName.slice(0, 1).toUpperCase()}</span>
          {!collapsed && <span>{userDisplayName}</span>}
        </button>
      </div>
    </aside>
  );
}
