import { ReactNode, useEffect, useMemo, useState } from 'react';
import type { ComposerRequest, FunctionalAgentRailAttentionStore, MeResponse, WorkstreamItem, WorkstreamShellRequest } from '../types';
import { WorkstreamComposer } from '../composer';
import { FunctionalAgentRail } from '../rail';
import { defaultSelectableAgentId } from '../rail';
import { buildShellRequestItem, normalizePromptToShellRequest, selectedFunctionalAgent, selectionFromShellRequest } from './shellState';
import { WorkstreamPanel } from './WorkstreamPanel';

type WorkstreamShellProps = {
  me: MeResponse;
  initialFunctionalAgentId?: string;
  items?: WorkstreamItem[];
  children?: ReactNode;
  appName?: string;
  onSelectAgent?: (functionalAgentId: string) => void;
  onComposerSubmit?: (request: ComposerRequest) => void | Promise<boolean | void>;
  onShellRequest?: (request: WorkstreamShellRequest, requestItem: WorkstreamItem) => void | Promise<boolean | void>;
  submittingFunctionalAgentId?: string;
  railAttentionByAgentId?: FunctionalAgentRailAttentionStore;
  onSignOut?: () => void;
};

export function WorkstreamShell({ me, initialFunctionalAgentId, items = [], children, appName, onSelectAgent, onComposerSubmit, onShellRequest, submittingFunctionalAgentId, railAttentionByAgentId, onSignOut }: WorkstreamShellProps) {
  const initialAgentId = initialFunctionalAgentId ?? defaultSelectableAgentId(me.functionalAgents, me.visibleCapabilityIds, me.account.status) ?? me.functionalAgents[0]?.functionalAgentId;
  const [selectedFunctionalAgentId, setSelectedFunctionalAgentId] = useState(initialAgentId);
  const [railCollapsed, setRailCollapsed] = useState(false);
  const selectedAgent = useMemo(() => selectedFunctionalAgent(me.functionalAgents, selectedFunctionalAgentId ?? ''), [me.functionalAgents, selectedFunctionalAgentId]);

  useEffect(() => {
    setSelectedFunctionalAgentId(initialFunctionalAgentId ?? initialAgentId);
  }, [initialFunctionalAgentId, initialAgentId]);

  function selectAgent(functionalAgentId: string) {
    const request: WorkstreamShellRequest = {
      requestType: 'open_workstream',
      origin: functionalAgentId === 'agent-my-account' ? 'my_account_panel' : 'surface_action',
      displayText: `Show workstream ${functionalAgentId}`,
      canonicalPrompt: `show workstream ${functionalAgentId}`,
      targetFunctionalAgentId: functionalAgentId,
      scope: 'authorized_cross_workstream',
      correlationId: `shell:${Date.now()}`
    };
    handleShellRequest(request);
  }

  async function submitComposerRequest(request: ComposerRequest) {
    const shellRequest = normalizePromptToShellRequest(request.prompt, request.functionalAgentId, request.idempotencyKey);
    if (shellRequest) {
      handleShellRequest(shellRequest);
      return true;
    }
    return onComposerSubmit?.(request);
  }

  function handleShellRequest(request: WorkstreamShellRequest) {
    const nextSelection = selectionFromShellRequest({ selectedFunctionalAgentId: selectedFunctionalAgentId ?? '' }, request);
    setSelectedFunctionalAgentId(nextSelection.selectedFunctionalAgentId);
    onSelectAgent?.(nextSelection.selectedFunctionalAgentId);
    onShellRequest?.(request, buildShellRequestItem(request));
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
          <WorkstreamComposer me={me} authContext={me.selectedAuthContext} selectedAgent={selectedAgent} isSubmitting={submittingFunctionalAgentId === selectedFunctionalAgentId} onSubmit={submitComposerRequest} />
        </footer>
      </div>
    </div>
  );
}
