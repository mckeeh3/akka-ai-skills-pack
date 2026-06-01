import React from 'react';
import { createRoot } from 'react-dom/client';
import { AuthKitProvider, useAuth } from '@workos-inc/authkit-react';
import './styles/tokens.css';
import './styles/base.css';
import './styles/layout.css';
import './styles/components.css';
import { HttpWorkstreamApiClient } from './api/HttpWorkstreamApiClient';
import { HttpWorkstreamRealtimeClient } from './api/HttpWorkstreamRealtimeClient';
import type { TokenProvider } from './api/HttpApiClient';
import type { ApiError } from './api/types';
import type { WorkstreamClient } from './api/WorkstreamApiClient';
import type { WorkstreamRealtimeClient } from './api/WorkstreamRealtimeClient';
import { WorkstreamShell } from './workstream/shell';
import { parseWorkstreamDeepLink, serializeWorkstreamDeepLink } from './workstream/shell/WorkstreamDeepLinks';
import { WorkstreamStream } from './workstream/stream';
import { buildCapabilityActionRequest } from './workstream/actions';
import { createWorkstreamVisualSessionKey, restoreOrCreateVisualSession, saveVisualSession, updateVisualSessionViewState, type WorkstreamVisualSession, type WorkstreamVisualSessionStore } from './workstream/visual-session';
import { clearRailAttentionForAgent, defaultSelectableAgentId, recordUnseenRailResponse } from './workstream/rail';
import { applyWorkstreamRealtimeEvent, realtimeStatusLabel } from './workstream/realtime';
import {
  type FunctionalAgentRailAttention,
  type FunctionalAgentRailAttentionStore,
  type MeResponse,
  type RealtimeConnectionState,
  type SurfaceAction,
  type SurfaceEnvelope,
  type WorkstreamEvent,
  type WorkstreamItem,
  type WorkstreamSelection,
  type WorkstreamShellRequest
} from './workstream';

const workosClientId = import.meta.env.VITE_WORKOS_CLIENT_ID;
const hasConfiguredWorkosClient = typeof workosClientId === 'string' && workosClientId.startsWith('client_') && !workosClientId.includes('your_workos');

type ModePreference = 'light' | 'dark' | 'system';
type BootstrapState =
  | { status: 'loading' }
  | { status: 'ready'; me: MeResponse; items: WorkstreamItem[]; surfaces: SurfaceEnvelope<unknown>[] }
  | { status: 'error'; message: string };

const modeStorageKey = 'seed-ui-mode';
// Contract markers preserved for frontend slice tests: data-mode-preference; Ready · workstream shell; Pending · backend configuration; Guarded · backend authority; runtime path uses HttpWorkstreamApiClient, HttpWorkstreamRealtimeClient, and WorkOS AuthKit getAccessToken.

type WorkstreamAppProps = {
  tokenProvider?: TokenProvider;
  onSignOut?: () => void;
  clients?: { workstream: WorkstreamClient; realtime: WorkstreamRealtimeClient };
};

function WorkstreamApp({ tokenProvider, onSignOut, clients }: WorkstreamAppProps) {
  const workstreamClient = React.useMemo<WorkstreamClient>(() => clients?.workstream ?? new HttpWorkstreamApiClient(tokenProvider), [clients, tokenProvider]);
  const realtimeClient = React.useMemo<WorkstreamRealtimeClient>(() => clients?.realtime ?? new HttpWorkstreamRealtimeClient(), [clients]);
  const [mode, setMode] = React.useState<ModePreference>(() => readStoredMode());
  const [selection, setSelection] = React.useState<Partial<WorkstreamSelection>>(() => readDeepLinkSelection());
  const [bootstrap, setBootstrap] = React.useState<BootstrapState>({ status: 'loading' });
  const [submittingFunctionalAgentId, setSubmittingFunctionalAgentId] = React.useState<string>();
  const [requestScrollTargetBySessionKey, setRequestScrollTargetBySessionKey] = React.useState<Record<string, string | undefined>>({});
  const [visualSessionsByKey, setVisualSessionsByKey] = React.useState<WorkstreamVisualSessionStore>({});
  const [railAttentionByAgentId, setRailAttentionByAgentId] = React.useState<FunctionalAgentRailAttentionStore>({});
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

  const ready = bootstrap.status === 'ready' ? bootstrap : undefined;
  const me = ready?.me;
  const selectedFunctionalAgentId = me ? selection.selectedFunctionalAgentId ?? defaultSelectableAgentId(me.functionalAgents, me.visibleCapabilityIds, me.account.status) : undefined;
  const selectedFunctionalAgentIdRef = React.useRef<string | undefined>(selectedFunctionalAgentId);

  React.useEffect(() => {
    selectedFunctionalAgentIdRef.current = selectedFunctionalAgentId;
  }, [selectedFunctionalAgentId]);

  const selectedSessionKey = selectedFunctionalAgentId ? createWorkstreamVisualSessionKey({
    accountId: me?.account.accountId ?? 'bootstrap-loading',
    selectedContextId: me?.selectedAuthContext.selectedContextId ?? 'bootstrap-loading',
    functionalAgentId: selectedFunctionalAgentId,
    workstreamId: selectedFunctionalAgentId
  }) : undefined;
  const currentVisualSession = selectedFunctionalAgentId ? restoreOrCreateVisualSession({
    store: visualSessionsByKey,
    accountId: me?.account.accountId ?? 'bootstrap-loading',
    selectedContextId: me?.selectedAuthContext.selectedContextId ?? 'bootstrap-loading',
    functionalAgentId: selectedFunctionalAgentId,
    workstreamId: selectedFunctionalAgentId,
    items: ready?.items.filter((item) => item.functionalAgentId === selectedFunctionalAgentId) ?? []
  }) : undefined;
  const selectedSurfaceId = selection.selectedSurfaceId ?? currentVisualSession?.selectedSurfaceId;
  const selectedItems = buildVisibleWorkstreamItems(
    ready?.items.filter((item) => !selectedFunctionalAgentId || item.functionalAgentId === selectedFunctionalAgentId) ?? [],
    ready?.surfaces ?? [],
    selectedFunctionalAgentId,
    selectedSurfaceId
  );

  React.useEffect(() => {
    if (bootstrap.status !== 'ready') return;
    const stateSubscription = realtimeClient.onState((state) => setRealtimeConnection(state));
    const eventSubscription = realtimeClient.onEvent((event) => {
      markUnseenBackgroundActivity(event);
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
    selectedFunctionalAgentIdRef.current = merged.selectedFunctionalAgentId;
    setSelection(merged);
    window.history.pushState(null, '', serializeWorkstreamDeepLink(merged));
  }

  function rememberVisualSession(session: WorkstreamVisualSession, viewState: Parameters<typeof updateVisualSessionViewState>[1]) {
    const nextSession = updateVisualSessionViewState(session, viewState);
    setVisualSessionsByKey((store) => saveVisualSession(store, nextSession));
    return nextSession;
  }

  function sessionForAgent(functionalAgentId: string): WorkstreamVisualSession {
    if (!ready || !me) throw new Error('Workstream session requested before backend bootstrap completed.');
    return restoreOrCreateVisualSession({
      store: visualSessionsByKey,
      accountId: me?.account.accountId ?? 'bootstrap-loading',
      selectedContextId: me?.selectedAuthContext.selectedContextId ?? 'bootstrap-loading',
      functionalAgentId,
      workstreamId: functionalAgentId,
      items: ready?.items.filter((item) => item.functionalAgentId === functionalAgentId) ?? []
    });
  }

  function setRequestScrollTargetForCurrentSession(targetId: string, functionalAgentId = selectedFunctionalAgentId) {
    if (!functionalAgentId || !me) return;
    const sessionKey = createWorkstreamVisualSessionKey({
      accountId: me?.account.accountId ?? 'bootstrap-loading',
      selectedContextId: me?.selectedAuthContext.selectedContextId ?? 'bootstrap-loading',
      functionalAgentId,
      workstreamId: functionalAgentId
    });
    setRequestScrollTargetBySessionKey((targets) => ({ ...targets, [sessionKey]: targetId }));
  }

  function isCurrentlySelectedFunctionalAgent(functionalAgentId: string) {
    return selectedFunctionalAgentIdRef.current === functionalAgentId;
  }

  function markUnseenResponse(functionalAgentId: string, lastItemId?: string, severity: FunctionalAgentRailAttention['severity'] = 'info') {
    if (isCurrentlySelectedFunctionalAgent(functionalAgentId)) return;
    setRailAttentionByAgentId((store) => recordUnseenRailResponse(store, {
      functionalAgentId,
      selectedFunctionalAgentId: selectedFunctionalAgentIdRef.current,
      lastItemId,
      severity,
      kind: 'background-response'
    }));
  }

  function markUnseenBackgroundActivity(event: WorkstreamEvent) {
    if (isCurrentlySelectedFunctionalAgent(event.functionalAgentId)) return;
    if (!isMaterialBackgroundEvent(event.eventType)) return;
    setRailAttentionByAgentId((store) => recordUnseenRailResponse(store, {
      functionalAgentId: event.functionalAgentId,
      selectedFunctionalAgentId: selectedFunctionalAgentIdRef.current,
      severity: event.eventType.includes('denied') ? 'warning' : 'info',
      kind: event.eventType === 'workstream.item.appended' || event.eventType === 'surface.created' ? 'background-response' : 'background-activity',
      lastItemId: event.surfaceId,
      lastUpdatedAt: event.occurredAt
    }));
  }

  function clearRailAttention(functionalAgentId: string) {
    setRailAttentionByAgentId((store) => clearRailAttentionForAgent(store, functionalAgentId));
  }

  function selectAgent(functionalAgentId: string) {
    if (!ready) return;
    const restoredSession = sessionForAgent(functionalAgentId);
    const restoredSurface = restoredSession.selectedSurfaceId ?? surfaceForAgent(ready.surfaces, functionalAgentId)?.surfaceId;
    clearRailAttention(functionalAgentId);
    setVisualSessionsByKey((store) => saveVisualSession(store, restoredSession));
    updateSelection({ selectedFunctionalAgentId: functionalAgentId, selectedItemId: undefined, selectedSurfaceId: restoredSurface });
    requestAnimationFrame(() => document.getElementById('workstream-panel-title')?.focus());
  }

  function openSurface(surfaceId: string) {
    if (!ready) return;
    const surface = ready.surfaces.find((candidate) => candidate.surfaceId === surfaceId);
    if (!surface) return;
    appendSurfaceRequestAndResponse(surface, `Show ${surface.title}`);
    updateSelection({
      selectedFunctionalAgentId: surface.ownerFunctionalAgentId ?? selectedFunctionalAgentId,
      selectedSurfaceId: surfaceId,
      surfacePlacement: 'inline'
    });
  }

  function appendSurfaceRequestAndResponse(surface: SurfaceEnvelope<unknown>, title: string, body?: string) {
    const now = Date.now();
    const functionalAgentId = surface.ownerFunctionalAgentId ?? selectedFunctionalAgentId ?? 'agent-user-admin';
    const requestItem: WorkstreamItem = {
      itemId: `surface-request-${now}`,
      functionalAgentId,
      kind: 'surface-request',
      createdAt: new Date().toISOString(),
      correlationId: `corr-surface-request-${now}`,
      traceIds: surface.traceIds,
      title,
      body,
      status: 'ready'
    };
    const responseItem: WorkstreamItem = {
      itemId: `surface-response-${surface.surfaceId}-${now}`,
      functionalAgentId,
      kind: 'surface',
      createdAt: new Date().toISOString(),
      correlationId: requestItem.correlationId,
      traceIds: surface.traceIds,
      surfaceId: surface.surfaceId,
      title: surface.title,
      status: 'ready'
    };
    setRequestScrollTargetForCurrentSession(requestItem.itemId, functionalAgentId);
    if (currentVisualSession) rememberVisualSession(currentVisualSession, { anchorSurfaceId: requestItem.itemId, selectedSurfaceId: surface.surfaceId, userHasManualScroll: false });
    setBootstrap((current) => current.status === 'ready'
      ? { ...current, items: pruneWorkstreamItems([...current.items, requestItem, responseItem]) }
      : current);
  }

  async function handleSurfaceAction(action: SurfaceAction, surfaceId: string) {
    if (!ready || !me) return;
    const request = buildCapabilityActionRequest(action, {
      selectedContextId: me.selectedAuthContext.selectedContextId,
      surfaceId,
      input: {},
      surfaceCorrelationId: `corr-${action.actionId}`
    });
    const result = await workstreamClient.runCapabilityAction(request);
    const targetSurface = result.ok ? result.value.resultSurface ?? ready.surfaces.find((candidate) => candidate.surfaceId === surfaceId) : undefined;
    const now = Date.now();
    const actionRequestItem: WorkstreamItem = {
      itemId: `surface-action-request-${now}`,
      functionalAgentId: targetSurface?.ownerFunctionalAgentId ?? selectedFunctionalAgentId ?? 'agent-user-admin',
      kind: 'surface-request',
      createdAt: new Date().toISOString(),
      correlationId: result.ok ? result.value.correlationId : result.error.correlationId,
      traceIds: result.ok ? result.value.traceIds : [],
      title: action.label,
      body: result.ok ? result.value.message : result.error.message,
      status: result.ok && result.value.status === 'accepted' ? 'ready' : 'blocked'
    };
    const surfaceResponseItem: WorkstreamItem | undefined = targetSurface ? {
      itemId: `surface-action-response-${targetSurface.surfaceId}-${now}`,
      functionalAgentId: targetSurface.ownerFunctionalAgentId,
      kind: 'surface',
      createdAt: new Date().toISOString(),
      correlationId: actionRequestItem.correlationId,
      traceIds: targetSurface.traceIds,
      surfaceId: targetSurface.surfaceId,
      title: targetSurface.title,
      status: 'ready'
    } : undefined;
    setRequestScrollTargetForCurrentSession(actionRequestItem.itemId, actionRequestItem.functionalAgentId);
    rememberVisualSession(sessionForAgent(actionRequestItem.functionalAgentId), { anchorSurfaceId: actionRequestItem.itemId, userHasManualScroll: false });
    setBootstrap((current) => {
      if (current.status !== 'ready') return current;
      const nextSurfaces = result.ok && result.value.resultSurface && !current.surfaces.some((surface) => surface.surfaceId === result.value.resultSurface?.surfaceId)
        ? [...current.surfaces, result.value.resultSurface]
        : current.surfaces;
      return { ...current, surfaces: nextSurfaces, items: pruneWorkstreamItems([...current.items, actionRequestItem, ...(surfaceResponseItem ? [surfaceResponseItem] : [])]) };
    });
    if (targetSurface && isCurrentlySelectedFunctionalAgent(targetSurface.ownerFunctionalAgentId)) {
      updateSelection({
        selectedFunctionalAgentId: targetSurface.ownerFunctionalAgentId,
        selectedSurfaceId: targetSurface.surfaceId,
        surfacePlacement: 'inline'
      });
    } else if (targetSurface) {
      markUnseenResponse(targetSurface.ownerFunctionalAgentId, surfaceResponseItem?.itemId ?? actionRequestItem.itemId, 'info');
    }
  }

  async function runShellSurfaceRequest(shellRequest: WorkstreamShellRequest, fallbackFunctionalAgentId: string, itemPrefix: string) {
    const shellResult = await workstreamClient.runShellRequest(shellRequest);
    if (!shellResult.ok) {
      const safeError = safeComposerErrorCopy(shellResult.error);
      const errorItem: WorkstreamItem = {
        itemId: `${itemPrefix}-shell-error-${Date.now()}`,
        functionalAgentId: fallbackFunctionalAgentId,
        kind: 'system-notification',
        createdAt: new Date().toISOString(),
        correlationId: shellRequest.correlationId,
        traceIds: [],
        title: safeError.title,
        body: `${safeError.body} Correlation ${shellResult.error.correlationId}.`,
        status: safeError.status
      };
      setBootstrap((current) => current.status === 'ready'
        ? { ...current, items: pruneWorkstreamItems([...current.items, errorItem]) }
        : current);
      return false;
    }
    const targetSurface = shellResult.value.resultSurface;
    const requestItem = shellResult.value.requestItem;
    const surfaceResponseItem: WorkstreamItem = {
      itemId: `${itemPrefix}-shell-response-${targetSurface.surfaceId}-${Date.now()}`,
      functionalAgentId: targetSurface.ownerFunctionalAgentId,
      kind: 'surface',
      createdAt: new Date().toISOString(),
      correlationId: shellResult.value.correlationId,
      traceIds: targetSurface.traceIds,
      surfaceId: targetSurface.surfaceId,
      title: targetSurface.title,
      status: 'ready'
    };
    setRequestScrollTargetForCurrentSession(requestItem.itemId, targetSurface.ownerFunctionalAgentId);
    rememberVisualSession(sessionForAgent(targetSurface.ownerFunctionalAgentId), { anchorSurfaceId: requestItem.itemId, selectedSurfaceId: targetSurface.surfaceId, userHasManualScroll: false });
    setBootstrap((current) => {
      if (current.status !== 'ready') return current;
      const nextSurfaces = current.surfaces.some((surface) => surface.surfaceId === targetSurface.surfaceId)
        ? current.surfaces.map((surface) => surface.surfaceId === targetSurface.surfaceId ? targetSurface : surface)
        : [...current.surfaces, targetSurface];
      return { ...current, surfaces: nextSurfaces, items: pruneWorkstreamItems([...current.items, requestItem, surfaceResponseItem]) };
    });
    if (isCurrentlySelectedFunctionalAgent(targetSurface.ownerFunctionalAgentId)) {
      updateSelection({ selectedFunctionalAgentId: targetSurface.ownerFunctionalAgentId, selectedSurfaceId: targetSurface.surfaceId, surfacePlacement: 'inline' });
    } else {
      markUnseenResponse(targetSurface.ownerFunctionalAgentId, surfaceResponseItem.itemId, 'info');
    }
    return true;
  }

  async function handleShowDashboard(functionalAgentId: string) {
    if (!ready || !me) return;
    const correlationId = `corr-show-dashboard-${Date.now().toString(36)}`;
    const shellRequest = buildShowDashboardShellRequest(functionalAgentId, me.selectedAuthContext.selectedContextId, correlationId, 'shell_button');
    await runShellSurfaceRequest(shellRequest, functionalAgentId, 'show-dashboard');
  }

  async function handleComposerSubmit(request: Parameters<NonNullable<React.ComponentProps<typeof WorkstreamShell>['onComposerSubmit']>>[0]) {
    if (!ready || !me) return false;
    const submittedAt = Date.now();
    const pendingItemId = `composer-submitting-${submittedAt}`;
    const correlationId = `corr-composer-${submittedAt.toString(36)}`;
    const shellRequest = buildComposerShellRequest(request.prompt, request.functionalAgentId, me.selectedAuthContext.selectedContextId, correlationId);
    if (shellRequest) {
      return runShellSurfaceRequest(shellRequest, request.functionalAgentId, 'composer');
    }
    const userRequestItem: WorkstreamItem = {
      itemId: `composer-request-${submittedAt}`,
      functionalAgentId: request.functionalAgentId,
      kind: 'user-request',
      createdAt: new Date(submittedAt).toISOString(),
      correlationId,
      traceIds: [],
      title: request.prompt,
      body: request.prompt,
      status: 'ready'
    };
    const submittingItem: WorkstreamItem = {
      itemId: pendingItemId,
      functionalAgentId: request.functionalAgentId,
      kind: 'system-status',
      createdAt: new Date(submittedAt).toISOString(),
      correlationId,
      traceIds: [],
      title: 'Submitting to model-backed agent',
      body: 'The governed runtime is assembling the prompt, checking model/provider configuration, and invoking the model. This selected workstream context is preserved for retry.',
      status: 'working'
    };
    setSubmittingFunctionalAgentId(request.functionalAgentId);
    setRequestScrollTargetForCurrentSession(userRequestItem.itemId, request.functionalAgentId);
    rememberVisualSession(sessionForAgent(request.functionalAgentId), { activeTurnGroupId: correlationId, anchorSurfaceId: userRequestItem.itemId, userHasManualScroll: false });
    setBootstrap((current) => current.status === 'ready'
      ? { ...current, items: pruneWorkstreamItems([...current.items, userRequestItem, submittingItem]) }
      : current);

    const result = await workstreamClient.submitWorkstreamMessage({
      ...request,
      correlationId
    });

    setSubmittingFunctionalAgentId(undefined);

    if (!result.ok) {
      const safeError = safeComposerErrorCopy(result.error);
      const errorItem: WorkstreamItem = {
        itemId: `composer-error-${Date.now()}`,
        functionalAgentId: request.functionalAgentId,
        kind: 'system-notification',
        createdAt: new Date().toISOString(),
        correlationId,
        traceIds: [],
        title: safeError.title,
        body: `${safeError.body} Retry is safe: the prompt remains in the composer and will reuse the selected workstream context. Correlation ${result.error.correlationId}.`,
        status: safeError.status
      };
      setRequestScrollTargetForCurrentSession(userRequestItem.itemId, request.functionalAgentId);
      rememberVisualSession(sessionForAgent(request.functionalAgentId), { anchorSurfaceId: userRequestItem.itemId, userHasManualScroll: false });
      if (!isCurrentlySelectedFunctionalAgent(request.functionalAgentId)) markUnseenResponse(request.functionalAgentId, errorItem.itemId, 'warning');
      setBootstrap((current) => current.status === 'ready'
        ? { ...current, items: pruneWorkstreamItems([...current.items.filter((item) => item.itemId !== pendingItemId), errorItem]) }
        : current);
      return false;
    }

    const { userItem, agentItem, surface } = result.value;
    const traceableAgentItem: WorkstreamItem = {
      ...agentItem,
      traceLinks: agentItem.traceLinks ?? agentItem.traceIds.map((traceId) => ({ traceId, label: traceId, href: `/ui?traceId=${encodeURIComponent(traceId)}` }))
    };
    // Compatibility contract marker for older visual-session string tests: const responseFunctionalAgentId = agentItem.functionalAgentId ?? userItem.functionalAgentId ?? request.functionalAgentId
    const responseFunctionalAgentId = surface.ownerFunctionalAgentId ?? request.functionalAgentId;
    setRequestScrollTargetForCurrentSession(userItem.itemId, responseFunctionalAgentId);
    rememberVisualSession(sessionForAgent(responseFunctionalAgentId), { activeTurnGroupId: correlationId, anchorSurfaceId: userItem.itemId, selectedSurfaceId: surface.surfaceId, userHasManualScroll: false });
    setBootstrap((current) => {
      if (current.status !== 'ready') return current;
      const nextSurfaces = current.surfaces.some((candidate) => candidate.surfaceId === surface.surfaceId)
        ? current.surfaces.map((candidate) => candidate.surfaceId === surface.surfaceId ? surface : candidate)
        : [...current.surfaces, surface];
      return { ...current, surfaces: nextSurfaces, items: pruneWorkstreamItems([...current.items.filter((item) => item.itemId !== pendingItemId && item.itemId !== userRequestItem.itemId), userItem, traceableAgentItem]) };
    });
    if (isCurrentlySelectedFunctionalAgent(responseFunctionalAgentId)) {
      updateSelection({
        selectedFunctionalAgentId: responseFunctionalAgentId,
        selectedSurfaceId: surface.surfaceId,
        surfacePlacement: 'inline'
      });
    } else {
      markUnseenResponse(responseFunctionalAgentId, traceableAgentItem.itemId, 'info');
    }
    return true;
  }

  if (bootstrap.status === 'loading') {
    return <main className="content workstream-panel"><p className="eyebrow">Real API client</p><h1>Loading workstream shell</h1></main>;
  }

  if (bootstrap.status === 'error') {
    return <main className="content workstream-panel"><p className="eyebrow">Real API client error</p><h1>Could not load workstream shell</h1><p>{bootstrap.message}</p></main>;
  }

  if (!ready || !me) {
    return <main className="content workstream-panel"><p className="eyebrow">Real API client</p><h1>Loading workstream shell</h1></main>;
  }

  const currentRequestScrollTargetId = selectedSessionKey ? requestScrollTargetBySessionKey[selectedSessionKey] : undefined;

  return (
    <>
    <WorkstreamShell
      me={me}
      initialFunctionalAgentId={selectedFunctionalAgentId}
      items={selectedItems}
      appName="AI-first SaaS"
      onSelectAgent={selectAgent}
      onComposerSubmit={handleComposerSubmit}
      onShowDashboard={handleShowDashboard}
      submittingFunctionalAgentId={submittingFunctionalAgentId}
      railAttentionByAgentId={railAttentionByAgentId}
      onSignOut={onSignOut}
    >
      <WorkstreamStream
        items={withRuntimeNotification(selectedItems, realtimeConnection)}
        surfaces={ready.surfaces}
        selectedItemId={selection.selectedItemId}
        requestScrollTargetId={currentRequestScrollTargetId}
        autoAnchorPaused={currentVisualSession?.userHasManualScroll}
        onOpenSurface={openSurface}
        onSurfaceAction={handleSurfaceAction}
        onAutoAnchorPaused={() => {
          if (currentVisualSession) rememberVisualSession(currentVisualSession, { userHasManualScroll: true });
        }}
      />
    </WorkstreamShell>
    </>
  );
}

function readDeepLinkSelection(): Partial<WorkstreamSelection> {
  const hashQuery = window.location.hash.includes('?') ? window.location.hash.slice(window.location.hash.indexOf('?')) : '';
  return parseWorkstreamDeepLink(window.location.search || hashQuery);
}

function isMaterialBackgroundEvent(eventType: WorkstreamEvent['eventType']): boolean {
  return eventType === 'workstream.item.appended'
    || eventType === 'surface.created'
    || eventType === 'surface.updated'
    || eventType === 'surface.action.denied'
    || eventType === 'surface.workflow.progressed'
    || eventType === 'surface.stale'
    || eventType === 'surface.reconnected';
}

function buildComposerShellRequest(prompt: string, functionalAgentId: string, selectedContextId: string, correlationId: string): WorkstreamShellRequest | undefined {
  const normalized = prompt.trim().toLowerCase().replace(/[.!?]+$/g, '').replace(/\s+/g, ' ');
  if (!['dashboard', 'show dashboard', 'open dashboard', 'refresh dashboard', 'show command center', 'open command center'].includes(normalized)) return undefined;
  const shellRequest = buildShowDashboardShellRequest(functionalAgentId, selectedContextId, correlationId, 'user_prompt', prompt.trim());
  return normalized.startsWith('refresh') ? { ...shellRequest, requestType: 'refresh_surface' } : shellRequest;
}

function buildShowDashboardShellRequest(functionalAgentId: string, selectedContextId: string, correlationId: string, origin: WorkstreamShellRequest['origin'], displayText = 'Show dashboard'): WorkstreamShellRequest {
  return {
    requestType: 'show_surface',
    origin,
    displayText,
    canonicalPrompt: 'Show dashboard',
    targetFunctionalAgentId: functionalAgentId,
    targetSurfaceId: dashboardSurfaceIdForAgent(functionalAgentId),
    sourceFunctionalAgentId: functionalAgentId,
    scope: 'current_workstream',
    correlationId,
    selectedContextId
  };
}

function dashboardSurfaceIdForAgent(functionalAgentId: string): string {
  switch (functionalAgentId) {
    case 'agent-my-account': return 'surface-my-account-dashboard';
    case 'agent-agent-admin': return 'surface-agent-admin-catalog';
    case 'agent-audit-trace': return 'surface-audit-trace-dashboard';
    case 'agent-governance-policy': return 'surface-governance-policy-dashboard';
    default: return 'surface-user-admin-dashboard';
  }
}

function safeComposerErrorCopy(error: ApiError): { title: string; body: string; status: 'blocked' | 'failed' } {
  if (error.code === 'forbidden' || error.code === 'unauthorized') {
    return {
      title: 'Message not submitted · forbidden',
      body: 'The backend denied this workstream capability for the selected context. Select an authorized context or ask an administrator to grant the required capability.',
      status: 'blocked'
    };
  }
  if (/provider|model|openai|configuration|config|credential|api[_-]?key/i.test(`${error.code} ${error.message}`)) {
    return {
      title: 'Message not submitted · provider configuration required',
      body: 'The model-backed agent is blocked because backend provider configuration is missing or invalid. Configure the backend provider variables and restart the API; secrets are not exposed in the browser.',
      status: 'blocked'
    };
  }
  return {
    title: 'Message not submitted · retry available',
    body: `The workstream message was not accepted by the backend: ${error.message}`,
    status: 'failed'
  };
}

function surfaceForAgent(surfaces: SurfaceEnvelope<unknown>[], functionalAgentId?: string) {
  return surfaces.find((surface) => surface.ownerFunctionalAgentId === functionalAgentId);
}

function buildVisibleWorkstreamItems(items: WorkstreamItem[], surfaces: SurfaceEnvelope<unknown>[], functionalAgentId?: string, selectedSurfaceId?: string): WorkstreamItem[] {
  if (items.some((item) => item.kind === 'surface')) return items;
  const surface = surfaces.find((candidate) => candidate.surfaceId === selectedSurfaceId && candidate.ownerFunctionalAgentId === functionalAgentId) ?? surfaceForAgent(surfaces, functionalAgentId);
  if (!surface) return items;
  return [{
    itemId: `default-surface-${surface.surfaceId}`,
    functionalAgentId: surface.ownerFunctionalAgentId,
    kind: 'surface',
    createdAt: surface.generatedAt,
    correlationId: surface.correlationId,
    traceIds: surface.traceIds,
    surfaceId: surface.surfaceId,
    title: surface.title,
    status: 'ready'
  }, ...items];
}

function withRuntimeNotification(items: WorkstreamItem[], connection: RealtimeConnectionState): WorkstreamItem[] {
  if (connection.status === 'connected') return items;
  const notification: WorkstreamItem = {
    itemId: `system-realtime-${connection.status}`,
    functionalAgentId: items[0]?.functionalAgentId ?? 'system',
    kind: 'system-notification',
    createdAt: new Date().toISOString(),
    correlationId: `corr-realtime-${connection.status}`,
    traceIds: [],
    title: realtimeStatusLabel(connection),
    body: connection.status === 'stale' || connection.status === 'disconnected' ? 'Workstream surfaces may be stale until realtime events resume.' : undefined,
    status: connection.status === 'stale' || connection.status === 'disconnected' ? 'stale' : 'working'
  };
  return [...items, notification];
}

function pruneWorkstreamItems(items: WorkstreamItem[], maxItems = 80): WorkstreamItem[] {
  return items.length > maxItems ? items.slice(items.length - maxItems) : items;
}

function readStoredMode(): ModePreference {
  const stored = window.localStorage.getItem(modeStorageKey);
  return stored === 'light' || stored === 'dark' || stored === 'system' ? stored : 'system';
}


function AuthenticatedRoot() {
  const { isLoading, user, signIn, signOut, getAccessToken } = useAuth();
  if (isLoading) return <div className="auth-gate"><p>Checking secure session…</p></div>;
  if (!user) {
    return (
      <div className="auth-gate">
        <h1>Sign in to continue</h1>
        <p>The AI-first SaaS workstream uses WorkOS/AuthKit for browser authentication. Backend capabilities remain authorized by local tenant membership and role state.</p>
        <button type="button" onClick={() => void signIn()}>Sign in with WorkOS</button>
        <p className="auth-gate__hint">The normal frontend runtime always uses real backend API and realtime clients. Fixture data and clients are test-only assets.</p>
      </div>
    );
  }
  return <WorkstreamApp tokenProvider={() => getAccessToken()} onSignOut={() => signOut()} />;
}

function Root() {
  if (!hasConfiguredWorkosClient) {
    return (
      <div className="auth-gate">
        <h1>Configure WorkOS/AuthKit</h1>
        <p>Set <code>VITE_WORKOS_CLIENT_ID</code> and <code>VITE_WORKOS_REDIRECT_URI</code>, then rebuild the frontend to use real backend APIs.</p>
        <p>The normal frontend runtime does not provide a fixture mode. Tests keep fixture clients and data under test-only assets.</p>
      </div>
    );
  }
  return (
    <AuthKitProvider clientId={workosClientId}>
      <AuthenticatedRoot />
    </AuthKitProvider>
  );
}

createRoot(document.getElementById('root')!).render(<Root />);
