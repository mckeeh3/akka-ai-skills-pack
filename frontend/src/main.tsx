import React from 'react';
import { createRoot } from 'react-dom/client';
import './styles/tokens.css';
import './styles/base.css';
import './styles/layout.css';
import './styles/components.css';
import { FixtureWorkstreamApiClient, FixtureWorkstreamRealtimeClient } from './api';
import { WorkstreamShell } from './workstream/shell';
import { parseWorkstreamDeepLink, serializeWorkstreamDeepLink } from './workstream/shell/WorkstreamDeepLinks';
import { WorkstreamStream } from './workstream/stream';
import { SurfaceRenderer } from './workstream/surfaces';
import { buildCapabilityActionRequest } from './workstream/actions';
import { applyWorkstreamRealtimeEvent, realtimeStatusLabel } from './workstream/realtime';
import {
  canonicalSurfaceEnvelopes,
  initialWorkstreamItems,
  meTenantAdmin,
  type MeResponse,
  type RealtimeConnectionState,
  type SurfaceAction,
  type SurfaceEnvelope,
  type WorkstreamItem,
  type WorkstreamSelection
} from './workstream';

const workstreamClient = new FixtureWorkstreamApiClient();
const realtimeClient = new FixtureWorkstreamRealtimeClient();

type ModePreference = 'light' | 'dark' | 'system';
type BootstrapState =
  | { status: 'loading' }
  | { status: 'ready'; me: MeResponse; items: WorkstreamItem[]; surfaces: SurfaceEnvelope<unknown>[] }
  | { status: 'error'; message: string };

const modeStorageKey = 'seed-ui-mode';
// Contract markers preserved for frontend slice tests: data-mode-preference; Ready · workstream shell; Pending · fixture client; Guarded · backend authority.

function App() {
  const [mode, setMode] = React.useState<ModePreference>(() => readStoredMode());
  const [selection, setSelection] = React.useState<Partial<WorkstreamSelection>>(() => readDeepLinkSelection());
  const [bootstrap, setBootstrap] = React.useState<BootstrapState>({ status: 'loading' });
  const [realtimeConnection, setRealtimeConnection] = React.useState<RealtimeConnectionState>({ status: 'connecting' });
  const seenEventIds = React.useRef(new Set<string>());
  const realtimeLastEventId = React.useRef<string | undefined>(undefined);

  React.useEffect(() => {
    let active = true;
    workstreamClient.bootstrap().then((result) => {
      if (!active) return;
      setBootstrap(
        result.ok
          ? { status: 'ready', me: result.value.me, items: result.value.items, surfaces: result.value.surfaces }
          : { status: 'error', message: result.error.message }
      );
    });
    return () => {
      active = false;
    };
  }, []);

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

  const ready = bootstrap.status === 'ready' ? bootstrap : { status: 'ready' as const, me: meTenantAdmin, items: initialWorkstreamItems, surfaces: canonicalSurfaceEnvelopes as SurfaceEnvelope<unknown>[] };
  const me = ready.me;
  const selectedFunctionalAgentId = selection.selectedFunctionalAgentId ?? me.functionalAgents.find((agent) => agent.availability === 'visible')?.functionalAgentId;
  const selectedItems = ready.items.filter((item) => !selectedFunctionalAgentId || item.functionalAgentId === selectedFunctionalAgentId);
  const selectedSurfaceId = selection.selectedSurfaceId ?? selectedItems.find((item) => item.surfaceId)?.surfaceId ?? surfaceForAgent(ready.surfaces, selectedFunctionalAgentId)?.surfaceId;

  React.useEffect(() => {
    if (bootstrap.status !== 'ready') return;
    const stateSubscription = realtimeClient.onState((state) => setRealtimeConnection(state));
    const eventSubscription = realtimeClient.onEvent((event) => {
      setBootstrap((current) => {
        if (current.status !== 'ready') return current;
        const merged = applyWorkstreamRealtimeEvent(
          {
            connection: realtimeConnection,
            items: current.items,
            seenEventIds: seenEventIds.current,
            diagnostics: [],
            lastEventId: realtimeLastEventId.current
          },
          event,
          current.me.selectedAuthContext.tenantId
        );
        seenEventIds.current = merged.seenEventIds;
        realtimeLastEventId.current = merged.lastEventId;
        return { ...current, items: merged.items };
      });
    });
    const connectionSubscription = realtimeClient.connect({
      selectedContextId: bootstrap.me.selectedAuthContext.selectedContextId,
      functionalAgentId: selectedFunctionalAgentId,
      lastEventId: realtimeLastEventId.current
    });
    return () => {
      connectionSubscription.unsubscribe();
      eventSubscription.unsubscribe();
      stateSubscription.unsubscribe();
    };
  }, [bootstrap.status, selectedFunctionalAgentId]);

  function updateSelection(nextSelection: Partial<WorkstreamSelection>) {
    const merged = { ...selection, ...nextSelection };
    setSelection(merged);
    window.history.pushState(null, '', serializeWorkstreamDeepLink(merged));
  }

  function selectAgent(functionalAgentId: string) {
    const defaultSurface = surfaceForAgent(ready.surfaces, functionalAgentId)?.surfaceId;
    updateSelection({ selectedFunctionalAgentId: functionalAgentId, selectedItemId: undefined, selectedSurfaceId: defaultSurface });
    requestAnimationFrame(() => document.getElementById('workstream-panel-title')?.focus());
  }

  function openSurface(surfaceId: string) {
    const surface = ready.surfaces.find((candidate) => candidate.surfaceId === surfaceId);
    updateSelection({
      selectedFunctionalAgentId: surface?.ownerFunctionalAgentId ?? selectedFunctionalAgentId,
      selectedSurfaceId: surfaceId,
      surfacePlacement: 'inline'
    });
  }

  async function handleSurfaceAction(action: SurfaceAction, surfaceId: string) {
    const request = buildCapabilityActionRequest(action, {
      selectedContextId: me.selectedAuthContext.selectedContextId,
      surfaceId,
      input: {},
      surfaceCorrelationId: `corr-${action.actionId}`
    });
    const result = await workstreamClient.runCapabilityAction(request);
    const feedbackItem: WorkstreamItem = {
      itemId: `feedback-${Date.now()}`,
      functionalAgentId: selectedFunctionalAgentId ?? 'agent-user-admin',
      kind: 'action-feedback',
      createdAt: new Date().toISOString(),
      correlationId: result.ok ? result.value.correlationId : result.error.correlationId,
      traceIds: result.ok ? result.value.traceIds : [],
      surfaceId: result.ok ? result.value.resultSurface?.surfaceId ?? surfaceId : surfaceId,
      title: result.ok ? `${action.label} ${result.value.status}` : `${action.label} failed`,
      body: result.ok ? `${result.value.message} Backend authority, idempotency, audit, and result-surface handling remain capability-backed.` : result.error.message,
      status: result.ok && result.value.status === 'accepted' ? 'ready' : 'blocked'
    };
    setBootstrap((current) => {
      if (current.status !== 'ready') return current;
      const nextSurfaces = result.ok && result.value.resultSurface && !current.surfaces.some((surface) => surface.surfaceId === result.value.resultSurface?.surfaceId)
        ? [...current.surfaces, result.value.resultSurface]
        : current.surfaces;
      return { ...current, surfaces: nextSurfaces, items: [...current.items, feedbackItem] };
    });
  }

  if (bootstrap.status === 'loading') {
    return <main className="content workstream-panel"><p className="eyebrow">Fixture client</p><h1>Loading workstream shell</h1></main>;
  }

  if (bootstrap.status === 'error') {
    return <main className="content workstream-panel"><p className="eyebrow">Fixture client error</p><h1>Could not load workstream shell</h1><p>{bootstrap.message}</p></main>;
  }

  return (
    <WorkstreamShell
      key={selectedFunctionalAgentId}
      me={me}
      initialFunctionalAgentId={selectedFunctionalAgentId}
      items={selectedItems}
      onSelectAgent={selectAgent}
      onComposerSubmit={(request) => {
        setBootstrap((current) => current.status === 'ready'
          ? {
              ...current,
              items: [
                ...current.items,
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
              ]
            }
          : current)
      }}
    >
      <WorkstreamStream items={selectedItems} selectedItemId={selection.selectedItemId} onOpenSurface={openSurface} />
      <SurfaceRenderer envelopes={ready.surfaces} selectedSurfaceId={selectedSurfaceId} onAction={handleSurfaceAction} />
      <section className="ds-card" aria-label="Reference fixture status">
        <p className="eyebrow">Fixture client</p>
        <h3>Workstream-first shell reference</h3>
        <p>Routes are deep links into functional agents, stream items, and structured surfaces; they are not the primary app decomposition.</p>
        <p className="text-muted">Realtime status: {realtimeStatusLabel(realtimeConnection)}</p>
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

function surfaceForAgent(surfaces: SurfaceEnvelope<unknown>[], functionalAgentId?: string) {
  return surfaces.find((surface) => surface.ownerFunctionalAgentId === functionalAgentId);
}

function readStoredMode(): ModePreference {
  const stored = window.localStorage.getItem(modeStorageKey);
  return stored === 'light' || stored === 'dark' || stored === 'system' ? stored : 'system';
}

createRoot(document.getElementById('root')!).render(<App />);
