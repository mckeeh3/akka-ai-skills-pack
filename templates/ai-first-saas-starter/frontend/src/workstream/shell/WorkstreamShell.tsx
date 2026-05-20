import { ReactNode, useMemo, useState } from 'react';
import type { ComposerRequest, MeResponse, WorkstreamItem } from '../types';
import { WorkstreamComposer } from '../composer';
import { FunctionalAgentRail } from '../rail';
import { ContextAuthorityBar } from './ContextAuthorityBar';
import { selectedFunctionalAgent } from './shellState';
import { WorkstreamPanel } from './WorkstreamPanel';

type WorkstreamShellProps = {
  me: MeResponse;
  initialFunctionalAgentId?: string;
  items?: WorkstreamItem[];
  children?: ReactNode;
  onSelectAgent?: (functionalAgentId: string) => void;
  onComposerSubmit?: (request: ComposerRequest) => void;
};

export function WorkstreamShell({ me, initialFunctionalAgentId, items = [], children, onSelectAgent, onComposerSubmit }: WorkstreamShellProps) {
  const initialAgentId = initialFunctionalAgentId ?? me.functionalAgents.find((agent) => agent.availability === 'visible')?.functionalAgentId ?? me.functionalAgents[0]?.functionalAgentId;
  const [selectedFunctionalAgentId, setSelectedFunctionalAgentId] = useState(initialAgentId);
  const [railCollapsed, setRailCollapsed] = useState(false);
  const selectedAgent = useMemo(() => selectedFunctionalAgent(me.functionalAgents, selectedFunctionalAgentId ?? ''), [me.functionalAgents, selectedFunctionalAgentId]);

  function selectAgent(functionalAgentId: string) {
    setSelectedFunctionalAgentId(functionalAgentId);
    onSelectAgent?.(functionalAgentId);
  }

  return (
    <div className="app-shell workstream-shell" data-selected-functional-agent={selectedFunctionalAgentId}>
      <a className="skip-link" href="#main-content">Skip to main workstream</a>
      <FunctionalAgentRail
        agents={me.functionalAgents}
        selectedFunctionalAgentId={selectedFunctionalAgentId}
        visibleCapabilityIds={me.visibleCapabilityIds}
        accountStatus={me.account.status}
        collapsed={railCollapsed}
        onSelectAgent={selectAgent}
        onToggleCollapsed={setRailCollapsed}
      />
      <div className="main-column workstream-main-column">
        <ContextAuthorityBar me={me} authContext={me.selectedAuthContext} pendingApprovalCount={2} />
        <WorkstreamPanel selectedAgent={selectedAgent} items={items}>{children}</WorkstreamPanel>
        <footer className="workstream-composer-region" aria-label="Persistent composer region">
          <WorkstreamComposer me={me} authContext={me.selectedAuthContext} selectedAgent={selectedAgent} onSubmit={onComposerSubmit} />
        </footer>
      </div>
    </div>
  );
}
