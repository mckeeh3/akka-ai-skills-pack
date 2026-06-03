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
  const selectedAgent = useMemo(() => selectedFunctionalAgent(me.functionalAgents, selectedFunctionalAgentId ?? ''), [me.functionalAgents, selectedFunctionalAgentId]);

  useEffect(() => {
    setSelectedFunctionalAgentId(initialFunctionalAgentId ?? initialAgentId);
  }, [initialFunctionalAgentId, initialAgentId]);

  function selectAgent(functionalAgentId: string) {
    setSelectedFunctionalAgentId(functionalAgentId);
    onSelectAgent?.(functionalAgentId);
  }

  return (
    <>
      <a className="skip-link" href="#main-content">Skip to main workstream</a>
      <div className="app-shell workstream-shell" data-selected-functional-agent={selectedFunctionalAgentId}>
        <FunctionalAgentRail
          agents={me.functionalAgents}
          selectedFunctionalAgentId={selectedFunctionalAgentId}
          visibleCapabilityIds={me.visibleCapabilityIds}
          accountStatus={me.account.status}
          collapsed={railCollapsed}
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
    </>
  );
}
