import React from 'react';
import { createRoot } from 'react-dom/client';
import './styles/tokens.css';
import './styles/base.css';
import './styles/layout.css';
import './styles/components.css';
import { WorkstreamShell } from './workstream/shell';
import { parseWorkstreamDeepLink, serializeWorkstreamDeepLink } from './workstream/shell/WorkstreamDeepLinks';
import { WorkstreamStream } from './workstream/stream';
import { SurfaceRenderer } from './workstream/surfaces';
import {
  canonicalSurfaceEnvelopes,
  initialWorkstreamItems,
  meTenantAdmin,
  type SurfaceAction,
  type SurfaceEnvelope,
  type WorkstreamItem,
  type WorkstreamSelection
} from './workstream';

type ModePreference = 'light' | 'dark' | 'system';

const modeStorageKey = 'seed-ui-mode';
// Contract markers preserved for frontend slice tests: data-mode-preference; Ready · workstream shell; Pending · fixture client; Guarded · backend authority.

function App() {
  const [mode, setMode] = React.useState<ModePreference>(() => readStoredMode());
  const [selection, setSelection] = React.useState<Partial<WorkstreamSelection>>(() => readDeepLinkSelection());
  const [items, setItems] = React.useState<WorkstreamItem[]>(initialWorkstreamItems);

  React.useEffect(() => {
    const root = document.documentElement;
    root.dataset.modePreference = mode;
    window.localStorage.setItem(modeStorageKey, mode);

    const applyResolvedMode = () => {
      const systemDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      root.dataset.mode = mode === 'system' ? (systemDark ? 'dark' : 'light') : mode;
    };

    applyResolvedMode();
    const media = window.matchMedia('(prefers-color-scheme: dark)');
    media.addEventListener('change', applyResolvedMode);
    return () => media.removeEventListener('change', applyResolvedMode);
  }, [mode]);

  React.useEffect(() => {
    const onLocationChange = () => setSelection(readDeepLinkSelection());
    window.addEventListener('popstate', onLocationChange);
    window.addEventListener('hashchange', onLocationChange);
    return () => {
      window.removeEventListener('popstate', onLocationChange);
      window.removeEventListener('hashchange', onLocationChange);
    };
  }, []);

  const selectedFunctionalAgentId = selection.selectedFunctionalAgentId ?? meTenantAdmin.functionalAgents.find((agent) => agent.availability === 'visible')?.functionalAgentId;
  const selectedItems = items.filter((item) => !selectedFunctionalAgentId || item.functionalAgentId === selectedFunctionalAgentId);
  const selectedSurfaceId = selection.selectedSurfaceId ?? selectedItems.find((item) => item.surfaceId)?.surfaceId ?? surfaceForAgent(selectedFunctionalAgentId)?.surfaceId;

  function updateSelection(nextSelection: Partial<WorkstreamSelection>) {
    const merged = { ...selection, ...nextSelection };
    setSelection(merged);
    window.history.pushState(null, '', serializeWorkstreamDeepLink(merged));
  }

  function selectAgent(functionalAgentId: string) {
    const defaultSurface = surfaceForAgent(functionalAgentId)?.surfaceId;
    updateSelection({ selectedFunctionalAgentId: functionalAgentId, selectedItemId: undefined, selectedSurfaceId: defaultSurface });
    requestAnimationFrame(() => document.getElementById('workstream-panel-title')?.focus());
  }

  function openSurface(surfaceId: string) {
    const surface = canonicalSurfaceEnvelopes.find((candidate) => candidate.surfaceId === surfaceId);
    updateSelection({
      selectedFunctionalAgentId: surface?.ownerFunctionalAgentId ?? selectedFunctionalAgentId,
      selectedSurfaceId: surfaceId,
      surfacePlacement: 'inline'
    });
  }

  function handleSurfaceAction(action: SurfaceAction, surfaceId: string) {
    const feedbackItem: WorkstreamItem = {
      itemId: `feedback-${Date.now()}`,
      functionalAgentId: selectedFunctionalAgentId ?? 'agent-user-admin',
      kind: 'action-feedback',
      createdAt: new Date().toISOString(),
      correlationId: `corr-${action.actionId}`,
      traceIds: [`trace-${action.capabilityId}`],
      surfaceId,
      title: `${action.label} requested`,
      body: `Fixture client accepted ${action.capabilityId}. Backend authority, idempotency, audit, and result-surface handling remain capability-backed.`,
      status: action.disabled ? 'blocked' : 'ready'
    };
    setItems((current) => [...current, feedbackItem]);
  }

  return (
    <WorkstreamShell
      key={selectedFunctionalAgentId}
      me={meTenantAdmin}
      initialFunctionalAgentId={selectedFunctionalAgentId}
      items={selectedItems}
      onSelectAgent={selectAgent}
      onComposerSubmit={(request) => {
        setItems((current) => [
          ...current,
          {
            itemId: `composer-${Date.now()}`,
            functionalAgentId: request.functionalAgentId,
            kind: 'user-request',
            createdAt: new Date().toISOString(),
            correlationId: request.idempotencyKey,
            traceIds: [],
            title: 'Composer request captured',
            body: request.prompt,
            status: 'ready'
          }
        ]);
      }}
    >
      <WorkstreamStream items={selectedItems} selectedItemId={selection.selectedItemId} onOpenSurface={openSurface} />
      <SurfaceRenderer envelopes={canonicalSurfaceEnvelopes as SurfaceEnvelope<unknown>[]} selectedSurfaceId={selectedSurfaceId} onAction={handleSurfaceAction} />
      <section className="ds-card" aria-label="Reference fixture status">
        <p className="eyebrow">Fixture client</p>
        <h3>Workstream-first shell reference</h3>
        <p>Routes are deep links into functional agents, stream items, and structured surfaces; they are not the primary app decomposition.</p>
        <ThemeModeToggle mode={mode} onModeChange={setMode} />
      </section>
    </WorkstreamShell>
  );
}

function ThemeModeToggle({ mode, onModeChange }: { mode: ModePreference; onModeChange: (mode: ModePreference) => void }) {
  return (
    <fieldset className="mode-toggle">
      <legend>Display mode</legend>
      {(['light', 'dark', 'system'] as const).map((option) => (
        <label key={option} className={mode === option ? 'mode-choice selected' : 'mode-choice'}>
          <input type="radio" name="mode" value={option} checked={mode === option} onChange={() => onModeChange(option)} />
          <span>{option}</span>
        </label>
      ))}
    </fieldset>
  );
}

function readDeepLinkSelection(): Partial<WorkstreamSelection> {
  const hashQuery = window.location.hash.includes('?') ? window.location.hash.slice(window.location.hash.indexOf('?')) : '';
  return parseWorkstreamDeepLink(window.location.search || hashQuery);
}

function surfaceForAgent(functionalAgentId?: string) {
  return canonicalSurfaceEnvelopes.find((surface) => surface.ownerFunctionalAgentId === functionalAgentId);
}

function readStoredMode(): ModePreference {
  const stored = window.localStorage.getItem(modeStorageKey);
  return stored === 'light' || stored === 'dark' || stored === 'system' ? stored : 'system';
}

createRoot(document.getElementById('root')!).render(<App />);
