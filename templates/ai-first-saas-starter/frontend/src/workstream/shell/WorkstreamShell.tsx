import { ReactNode, useEffect, useMemo, useState } from 'react';
import type { ComposerRequest, FunctionalAgentRailAttentionStore, MeResponse, WorkstreamItem } from '../types';
import { WorkstreamComposer } from '../composer';
import { FunctionalAgentRail } from '../rail';
import { defaultSelectableAgentId } from '../rail';
import { selectedFunctionalAgent } from './shellState';
import { WorkstreamPanel } from './WorkstreamPanel';

type WorkstreamShellProps = {
  me: MeResponse;
  initialFunctionalAgentId?: string;
  items?: WorkstreamItem[];
  children?: ReactNode;
  appName?: string;
  onSelectAgent?: (functionalAgentId: string) => void;
  onComposerSubmit?: (request: ComposerRequest) => void | Promise<boolean | void>;
  onShowDashboard?: (functionalAgentId: string) => void | Promise<void>;
  submittingFunctionalAgentId?: string;
  railAttentionByAgentId?: FunctionalAgentRailAttentionStore;
  onSignOut?: () => void;
};

export function WorkstreamShell({ me, initialFunctionalAgentId, items = [], children, appName, onSelectAgent, onComposerSubmit, onShowDashboard, submittingFunctionalAgentId, railAttentionByAgentId, onSignOut }: WorkstreamShellProps) {
  const initialAgentId = initialFunctionalAgentId ?? defaultSelectableAgentId(me.functionalAgents, me.visibleCapabilityIds, me.account.status) ?? me.functionalAgents[0]?.functionalAgentId;
  const [selectedFunctionalAgentId, setSelectedFunctionalAgentId] = useState(initialAgentId);
  const [railCollapsed, setRailCollapsed] = useState(false);
  const [mobileRailOpen, setMobileRailOpen] = useState(false);
  const selectedAgent = useMemo(() => selectedFunctionalAgent(me.functionalAgents, selectedFunctionalAgentId ?? ''), [me.functionalAgents, selectedFunctionalAgentId]);

  useEffect(() => {
    setSelectedFunctionalAgentId(initialFunctionalAgentId ?? initialAgentId);
  }, [initialFunctionalAgentId, initialAgentId]);

  useEffect(() => {
    if (!mobileRailOpen) return;
    function closeOnEscape(event: KeyboardEvent) {
      if (event.key === 'Escape') setMobileRailOpen(false);
    }
    document.addEventListener('keydown', closeOnEscape);
    return () => document.removeEventListener('keydown', closeOnEscape);
  }, [mobileRailOpen]);

  function selectAgent(functionalAgentId: string) {
    setSelectedFunctionalAgentId(functionalAgentId);
    setMobileRailOpen(false);
    onSelectAgent?.(functionalAgentId);
  }

  return (
    <div className="app-shell workstream-shell" data-selected-functional-agent={selectedFunctionalAgentId} data-mobile-rail-open={mobileRailOpen ? 'true' : 'false'}>
      <a className="skip-link" href="#main-content">Skip to main workstream</a>
      <button
        type="button"
        className="mobile-menu-button"
        aria-controls="workstream-functional-agent-rail"
        aria-expanded={mobileRailOpen}
        onClick={() => setMobileRailOpen(true)}
      >
        <span aria-hidden="true">☰</span>
        <span>Workstreams</span>
      </button>
      {mobileRailOpen && <button type="button" className="nav-backdrop" aria-label="Close workstream navigation" onClick={() => setMobileRailOpen(false)} />}
      <FunctionalAgentRail
        agents={me.functionalAgents}
        selectedFunctionalAgentId={selectedFunctionalAgentId}
        visibleCapabilityIds={me.visibleCapabilityIds}
        accountStatus={me.account.status}
        collapsed={railCollapsed}
        mobileOpen={mobileRailOpen}
        appName={appName}
        userDisplayName={me.account.displayName}
        railAttentionByAgentId={railAttentionByAgentId}
        onSelectAgent={selectAgent}
        onToggleCollapsed={setRailCollapsed}
        onSignOut={onSignOut}
      />
      <div className="main-column workstream-main-column">
        <WorkstreamPanel selectedAgent={selectedAgent} items={items}>{children}</WorkstreamPanel>
        <footer className="workstream-composer-region" aria-label="Persistent composer region">
          <WorkstreamComposer me={me} authContext={me.selectedAuthContext} selectedAgent={selectedAgent} isSubmitting={submittingFunctionalAgentId === selectedFunctionalAgentId} onSubmit={onComposerSubmit} onShowDashboard={onShowDashboard} />
        </footer>
      </div>
    </div>
  );
}
