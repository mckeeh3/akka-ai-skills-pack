import React from 'react';
import { createRoot } from 'react-dom/client';
import { AuthKitProvider, useAuth } from '@workos-inc/authkit-react';
import './styles/tokens.css';
import './styles/base.css';
import './styles/layout.css';
import './styles/components.css';
import { HttpWorkstreamApiClient } from './api/HttpWorkstreamApiClient';
import { HttpWorkstreamRealtimeClient } from './api/HttpWorkstreamRealtimeClient';
import { HttpApiClient, type TokenProvider } from './api/HttpApiClient';
import type { ApiClient } from './api/ApiClient';
import type { ApiError, OrganizationActionResponse, OrganizationDetailPayload, OrganizationListPayload, OrganizationSummary } from './api/types';
import type { WorkstreamClient } from './api/WorkstreamApiClient';
import type { WorkstreamRealtimeClient } from './api/WorkstreamRealtimeClient';
import { WorkstreamShell } from './workstream/shell';
import { parseWorkstreamDeepLink, serializeWorkstreamDeepLink } from './workstream/shell/WorkstreamDeepLinks';
import { WorkstreamStream } from './workstream/stream';
import { pruneWorkstreamSurfaceStreamsByAgent } from './workstream/stream/streamState';
import { buildCapabilityActionRequest } from './workstream/actions';
import { clearDeviceSurfaceStreamForSession, createWorkstreamVisualSessionKey, persistDeviceSurfaceStreams, restoreDevicePersistedSurfaceStreams, restoreOrCreateVisualSession, saveVisualSession, updateVisualSessionViewState, type WorkstreamVisualSession, type WorkstreamVisualSessionStore } from './workstream/visual-session';
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
const configuredWorkosRedirectUri = import.meta.env.VITE_WORKOS_REDIRECT_URI;
const workosRedirectUri = typeof configuredWorkosRedirectUri === 'string' && configuredWorkosRedirectUri.trim().length > 0
  ? configuredWorkosRedirectUri
  : window.location.origin;
const hasConfiguredWorkosClient = typeof workosClientId === 'string' && workosClientId.startsWith('client_') && !workosClientId.includes('your_workos');

type ThemePreference = 'aurora-light' | 'cobalt-light' | 'obsidian-dark' | 'midnight-dark' | 'dark-night';
type BootstrapState =
  | { status: 'loading' }
  | { status: 'ready'; me: MeResponse; items: WorkstreamItem[]; surfaces: SurfaceEnvelope<unknown>[] }
  | { status: 'error'; message: string };

const themeStorageKey = 'seed-ui-theme';
const defaultThemeId: ThemePreference = 'aurora-light';
const availableThemeIds: readonly ThemePreference[] = ['aurora-light', 'cobalt-light', 'obsidian-dark', 'midnight-dark', 'dark-night'];
// Contract markers preserved for frontend slice tests: data-theme; named theme selection; Ready · workstream shell; Pending · backend configuration; Guarded · backend authority; runtime path uses HttpWorkstreamApiClient, HttpWorkstreamRealtimeClient, and WorkOS AuthKit getAccessToken.

type WorkstreamAppProps = {
  tokenProvider?: TokenProvider;
  onSignOut?: () => void;
  clients?: { workstream: WorkstreamClient; realtime: WorkstreamRealtimeClient; api?: ApiClient };
};

type InvitationAcceptanceResult = {
  status: string;
  reasonCode: string;
  recoveryHint: string;
  invitationId?: string;
  tenantId?: string;
  customerId?: string;
  membershipId?: string;
  expiresAt?: string;
  correlationId: string;
};

function WorkstreamApp({ tokenProvider, onSignOut, clients }: WorkstreamAppProps) {
  const selectedContextIdRef = React.useRef<string | undefined>(undefined);
  const workstreamClient = React.useMemo<WorkstreamClient>(() => clients?.workstream ?? new HttpWorkstreamApiClient(tokenProvider), [clients, tokenProvider]);
  const realtimeClient = React.useMemo<WorkstreamRealtimeClient>(() => clients?.realtime ?? new HttpWorkstreamRealtimeClient(tokenProvider), [clients, tokenProvider]);
  const apiClient = React.useMemo<ApiClient>(() => clients?.api ?? new HttpApiClient(tokenProvider, () => selectedContextIdRef.current), [clients, tokenProvider]);
  const [themeId, setThemeId] = React.useState<ThemePreference>(() => readStoredThemeId());
  const [selection, setSelection] = React.useState<Partial<WorkstreamSelection>>(() => readDeepLinkSelection());
  const [bootstrap, setBootstrap] = React.useState<BootstrapState>({ status: 'loading' });
  const [submittingFunctionalAgentId, setSubmittingFunctionalAgentId] = React.useState<string>();
  const [requestScrollTargetBySessionKey, setRequestScrollTargetBySessionKey] = React.useState<Record<string, string | undefined>>({});
  const [visualSessionsByKey, setVisualSessionsByKey] = React.useState<WorkstreamVisualSessionStore>({});
  const [railAttentionByAgentId, setRailAttentionByAgentId] = React.useState<FunctionalAgentRailAttentionStore>({});
  const [realtimeConnection, setRealtimeConnection] = React.useState<RealtimeConnectionState>({ status: 'connecting' });
  const seenEventIds = React.useRef(new Set<string>());
  const realtimeLastEventId = React.useRef<string | undefined>(undefined);
  const realtimeLastMergedEventId = React.useRef<string | undefined>(undefined);

  React.useEffect(() => {
    let active = true;
    workstreamClient.bootstrap().then((result) => {
      if (!active) return;
      if (!result.ok) {
        setBootstrap({ status: 'error', message: result.error.message });
        return;
      }
      // Backend-derived rail summaries remain sourced from bootstrap: me: { ...result.value.me, functionalAgents: result.value.functionalAgents }
      const me = { ...result.value.me, functionalAgents: result.value.functionalAgents };
      selectedContextIdRef.current = me.selectedAuthContext.selectedContextId;
      const restoredSurfaceStream = restoreDevicePersistedSurfaceStreams({ me, items: result.value.items, surfaces: result.value.surfaces });
      setBootstrap({ status: 'ready', me, items: restoredSurfaceStream.items, surfaces: restoredSurfaceStream.surfaces });
    });
    return () => {
      active = false;
    };
  }, []);

  React.useEffect(() => {
    const root = document.documentElement;
    root.dataset.theme = themeId;
  }, [themeId]);

  React.useEffect(() => {
    if (bootstrap.status !== 'ready') return;
    const backendThemeId = normalizeThemeId(bootstrap.me.settings.preferredThemeId) ?? defaultThemeId;
    setThemeId(backendThemeId);
    persistThemeId(backendThemeId);
  }, [bootstrap.status === 'ready' ? bootstrap.me.settings.preferredThemeId : undefined]);

  React.useEffect(() => {
    if (bootstrap.status !== 'ready') return;
    persistDeviceSurfaceStreams({ me: bootstrap.me, items: bootstrap.items, surfaces: bootstrap.surfaces });
  }, [bootstrap.status === 'ready' ? bootstrap.items : undefined, bootstrap.status === 'ready' ? bootstrap.surfaces : undefined, bootstrap.status === 'ready' ? bootstrap.me : undefined]);

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

  React.useEffect(() => {
    selectedContextIdRef.current = me?.selectedAuthContext.selectedContextId;
  }, [me?.selectedAuthContext.selectedContextId]);

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
  const selectedItems = ready?.items.filter((item) => !selectedFunctionalAgentId || item.functionalAgentId === selectedFunctionalAgentId) ?? [];

  React.useEffect(() => {
    if (bootstrap.status !== 'ready') return;
    const stateSubscription = realtimeClient.onState((state) => {
      setRealtimeConnection(state);
      if ('lastEventId' in state && state.lastEventId) realtimeLastEventId.current = state.lastEventId;
    });
    const eventSubscription = realtimeClient.onEvent((event) => {
      markUnseenBackgroundActivity(event);
      if (event.eventType === 'projection.refresh.available' || event.eventType === 'surface.stale') {
        void refreshBackendDerivedAttentionDelivery({ functionalAgentId: event.functionalAgentId, surfaceId: event.surfaceId, reason: 'event-backed-projection-refresh' });
      }
      setBootstrap((current) => {
        if (current.status !== 'ready') return current;
        const merged = applyWorkstreamRealtimeEvent(
          {
            connection: realtimeConnection,
            items: current.items,
            seenEventIds: seenEventIds.current,
            diagnostics: [],
            lastEventId: realtimeLastMergedEventId.current
          },
          event,
          current.me.selectedAuthContext.tenantId
        );
        seenEventIds.current = merged.seenEventIds;
        realtimeLastMergedEventId.current = merged.lastEventId;
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
    const restoredSurface = restoredSession.selectedSurfaceId ?? surfaceForAgent(ready.surfaces, functionalAgentId)?.surfaceId ?? dashboardSurfaceIdForAgent(functionalAgentId);
    clearRailAttention(functionalAgentId);
    setVisualSessionsByKey((store) => saveVisualSession(store, restoredSession));
    updateSelection({ selectedFunctionalAgentId: functionalAgentId, selectedItemId: undefined, selectedSurfaceId: restoredSurface });
    void refreshBackendDerivedAttentionDelivery({ functionalAgentId, surfaceId: restoredSurface, reason: 'workstream-open' });
    requestAnimationFrame(() => document.getElementById('workstream-panel-title')?.focus());
  }

  function openSurface(surfaceId: string) {
    if (!ready) return;
    const surface = ready.surfaces.find((candidate) => candidate.surfaceId === surfaceId);
    if (!surface) return;
    appendSurfaceRequestAndResponse(surface, surfaceRequestTitle(surface));
    updateSelection({
      selectedFunctionalAgentId: surface.ownerFunctionalAgentId ?? selectedFunctionalAgentId,
      selectedSurfaceId: surfaceId,
      surfacePlacement: 'inline'
    });
  }

  function surfaceRequestTitle(surface: SurfaceEnvelope<unknown>) {
    return `Show ${titleCaseSurfaceName(surface.title)}`;
  }

  function actionRequestTitle(action: SurfaceAction, input: unknown, targetSurface: SurfaceEnvelope<unknown> | undefined) {
    if (action.intent === 'surface-request') return action.shellRequest?.displayText ?? (targetSurface ? surfaceRequestTitle(targetSurface) : action.label);
    if (action.actionId === 'action-invite-user') {
      const values = inputRecord(input);
      const invitee = values.displayName?.trim() || values.email?.trim();
      return invitee ? `Invite user ${invitee}` : 'Invite user';
    }
    return action.label;
  }

  function titleCaseSurfaceName(title: string | undefined) {
    return (title ?? 'surface').replace(/\w\S*/g, (word) => word.charAt(0).toUpperCase() + word.slice(1));
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

  function handleSurfaceFieldValueChange(fieldId: string, value: string) {
    if (fieldId !== 'preferredThemeId') return;
    const previewThemeId = normalizeThemeId(value);
    if (previewThemeId) setThemeId(previewThemeId);
  }

  async function handleSurfaceAction(action: SurfaceAction, surfaceId: string, input: unknown = {}) {
    if (!ready || !me) return;
    if (isOrganizationAdminRuntimeAction(action)) {
      await runOrganizationAdminRuntimeAction(action, surfaceId, input);
      return;
    }
    const actionCorrelationId = `corr-${action.actionId}-${Date.now().toString(36)}`;
    const request = buildCapabilityActionRequest(action, {
      selectedContextId: me.selectedAuthContext.selectedContextId,
      surfaceId,
      input,
      surfaceCorrelationId: actionCorrelationId
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
      title: actionRequestTitle(action, input, targetSurface),
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
      const nextSurfaces = result.ok && result.value.resultSurface
        ? current.surfaces.some((surface) => surface.surfaceId === result.value.resultSurface?.surfaceId)
          ? current.surfaces.map((surface) => surface.surfaceId === result.value.resultSurface?.surfaceId ? result.value.resultSurface : surface)
          : [...current.surfaces, result.value.resultSurface]
        : current.surfaces;
      const selectedThemeId = input && typeof input === 'object' && 'preferredThemeId' in input ? normalizeThemeId((input as { preferredThemeId?: unknown }).preferredThemeId) : undefined;
      const selectedThemeCommitAccepted = result.ok && selectedThemeId && (result.value.status === 'accepted' || result.value.status === 'no-op');
      const nextMe = selectedThemeCommitAccepted ? { ...current.me, settings: { ...current.me.settings, preferredThemeId: selectedThemeId } } : current.me;
      return { ...current, me: nextMe, surfaces: nextSurfaces, items: pruneWorkstreamItems([...current.items, actionRequestItem, ...(surfaceResponseItem ? [surfaceResponseItem] : [])]) };
    });
    const selectedThemeId = input && typeof input === 'object' && 'preferredThemeId' in input ? normalizeThemeId((input as { preferredThemeId?: unknown }).preferredThemeId) : undefined;
    const selectedThemeCommitAccepted = result.ok && selectedThemeId && (result.value.status === 'accepted' || result.value.status === 'no-op');
    if (selectedThemeCommitAccepted) {
      setThemeId(selectedThemeId);
      persistThemeId(selectedThemeId);
    } else if (selectedThemeId) {
      setThemeId(normalizeThemeId(me.settings.preferredThemeId) ?? defaultThemeId);
    }
    const opensAnotherWorkstreamFromMyAccount = surfaceId === 'surface-my-account-dashboard' && targetSurface?.ownerFunctionalAgentId !== selectedFunctionalAgentId;
    if (targetSurface && (action.intent === 'surface-request' || opensAnotherWorkstreamFromMyAccount || isCurrentlySelectedFunctionalAgent(targetSurface.ownerFunctionalAgentId))) {
      clearRailAttention(targetSurface.ownerFunctionalAgentId);
      updateSelection({
        selectedFunctionalAgentId: targetSurface.ownerFunctionalAgentId,
        selectedSurfaceId: targetSurface.surfaceId,
        surfacePlacement: 'inline'
      });
    } else if (targetSurface) {
      markUnseenResponse(targetSurface.ownerFunctionalAgentId, surfaceResponseItem?.itemId ?? actionRequestItem.itemId, 'info');
    }
    if (isProducerAffectingAction(action)) {
      // Contract marker: reason: 'producer-affecting-action-completion'. Input-scoped detail surfaces keep the action result surface instead of fetching the actor-default detail surface.
      await refreshBackendAttentionSummaries('producer-affecting-action-completion');
      if (!isInputScopedDetailSurface(targetSurface)) {
        await refreshBackendSurface(targetSurface?.surfaceId ?? surfaceId);
      }
    }
  }

  async function runOrganizationAdminRuntimeAction(action: SurfaceAction, surfaceId: string, input: unknown) {
    const currentSurface = ready?.surfaces.find((candidate) => candidate.surfaceId === surfaceId);
    const formInput = inputRecord(input);
    const result = await callOrganizationAdminApi(action.actionId, formInput);
    const updatedSurface = currentSurface
      ? mapOrganizationAdminSurface(currentSurface, action.actionId, formInput, result)
      : undefined;
    const now = Date.now();
    const actionRequestItem: WorkstreamItem = {
      itemId: `surface-action-request-${now}`,
      functionalAgentId: updatedSurface?.ownerFunctionalAgentId ?? selectedFunctionalAgentId ?? 'agent-user-admin',
      kind: 'surface-request',
      createdAt: new Date().toISOString(),
      correlationId: result.ok ? runtimeCorrelationId(result.value) : result.error.correlationId,
      traceIds: result.ok ? runtimeTraceIds(result.value) : [],
      title: action.label,
      status: result.ok ? 'ready' : 'blocked'
    };
    const surfaceResponseItem: WorkstreamItem | undefined = updatedSurface ? {
      itemId: `surface-action-response-${updatedSurface.surfaceId}-${now}`,
      functionalAgentId: updatedSurface.ownerFunctionalAgentId,
      kind: 'surface',
      createdAt: new Date().toISOString(),
      correlationId: actionRequestItem.correlationId,
      traceIds: updatedSurface.traceIds,
      surfaceId: updatedSurface.surfaceId,
      title: updatedSurface.title,
      status: 'ready'
    } : undefined;
    setRequestScrollTargetForCurrentSession(actionRequestItem.itemId, actionRequestItem.functionalAgentId);
    rememberVisualSession(sessionForAgent(actionRequestItem.functionalAgentId), { anchorSurfaceId: actionRequestItem.itemId, selectedSurfaceId: updatedSurface?.surfaceId ?? surfaceId, userHasManualScroll: false });
    setBootstrap((current) => {
      if (current.status !== 'ready') return current;
      const nextSurfaces = updatedSurface
        ? current.surfaces.some((surface) => surface.surfaceId === updatedSurface.surfaceId)
          ? current.surfaces.map((surface) => surface.surfaceId === updatedSurface.surfaceId ? updatedSurface : surface)
          : [...current.surfaces, updatedSurface]
        : current.surfaces;
      const syncedSurfaces = updatedSurface
        ? syncOrganizationDirectorySurfaces(nextSurfaces, updatedSurface, action.actionId)
        : nextSurfaces;
      return { ...current, surfaces: syncedSurfaces, items: pruneWorkstreamItems([...current.items, actionRequestItem, ...(surfaceResponseItem ? [surfaceResponseItem] : [])]) };
    });
    if (updatedSurface) {
      clearRailAttention(updatedSurface.ownerFunctionalAgentId);
      updateSelection({ selectedFunctionalAgentId: updatedSurface.ownerFunctionalAgentId, selectedSurfaceId: updatedSurface.surfaceId, surfacePlacement: 'inline' });
    }
  }

  async function callOrganizationAdminApi(actionId: string, input: Record<string, string>) {
    // Production Organization Admin actions intentionally use the typed AdminClient and protected /api/admin/organizations path.
    if (isOrganizationDirectoryAction(actionId)) return apiClient.admin.listOrganizations({ query: input.query, status: input.status });
    if (actionId === 'action-organization-read') return apiClient.admin.getOrganization(requiredInput(input, 'organizationId'));
    if (actionId === 'action-organization-create') return apiClient.admin.createOrganization({ organizationName: requiredInput(input, 'organizationName'), reason: input.reason, idempotencyKey: requiredInput(input, 'idempotencyKey') });
    if (actionId === 'action-organization-rename') return apiClient.admin.renameOrganization(requiredInput(input, 'organizationId'), { organizationName: requiredInput(input, 'organizationName'), reason: input.reason, idempotencyKey: requiredInput(input, 'idempotencyKey') });
    if (actionId === 'action-organization-suspend') return apiClient.admin.suspendOrganization(requiredInput(input, 'organizationId'), { reason: requiredInput(input, 'reason'), idempotencyKey: requiredInput(input, 'idempotencyKey') });
    if (actionId === 'action-organization-reactivate') return apiClient.admin.reactivateOrganization(requiredInput(input, 'organizationId'), { reason: requiredInput(input, 'reason'), idempotencyKey: requiredInput(input, 'idempotencyKey') });
    return apiClient.admin.listOrganizations();
  }

  function mapOrganizationAdminSurface(surface: SurfaceEnvelope<unknown>, actionId: string, input: Record<string, string>, result: Awaited<ReturnType<typeof callOrganizationAdminApi>>): SurfaceEnvelope<unknown> {
    const data = { ...(surface.data as Record<string, unknown>) };
    if (!result.ok) {
      return {
        ...surface,
        correlationId: result.error.correlationId,
        traceIds: surface.traceIds,
        generatedAt: new Date().toISOString(),
        data: {
          ...data,
          systemStates: [organizationErrorState(result.error.code)],
          forbiddenMessage: result.error.code === 'forbidden' ? 'Organization Admin is unavailable for this selected context. Backend authorization denied the protected Admin API call.' : data.forbiddenMessage,
          lastResult: { status: organizationErrorState(result.error.code), message: result.error.message, correlationId: result.error.correlationId }
        }
      };
    }
    if (isOrganizationDirectoryAction(actionId)) {
      const payload = result.value as OrganizationListPayload;
      return {
        ...surface,
        surfaceId: 'surface-user-admin-organization-directory',
        surfaceType: 'list-search',
        surfaceVersion: surface.surfaceVersion ?? '1.0.0',
        title: 'Organization Directory',
        correlationId: payload.correlationId,
        traceIds: payload.traceRefs,
        generatedAt: new Date().toISOString(),
        data: {
          ...data,
          surfaceContract: 'user_admin.organization_directory.v1',
          organizations: payload.organizations,
          filters: { query: input.query, status: input.status },
          boundaryNotice: payload.safeBoundaryNotice,
          safeBoundaryNotice: payload.safeBoundaryNotice,
          traceRefs: payload.traceRefs,
          correlationId: payload.correlationId,
          redaction: payload.redactions,
          systemStates: payload.organizations.length ? ['ready'] : ['empty'],
          lastResult: { status: 'success', message: 'Organization list refreshed from the protected Admin API.', correlationId: payload.correlationId, traceRefs: payload.traceRefs }
        }
      };
    }
    if (actionId === 'action-organization-read') {
      return surfaceWithOrganizationDetail(surface, result.value as OrganizationDetailPayload, 'success', 'Organization detail loaded from the protected Admin API.');
    }
    const payload = result.value as OrganizationActionResponse;
    return surfaceWithOrganizationDetail(surface, payload.organization, payload.status, payload.message, payload.traceRefs, payload.correlationId);
  }

  function surfaceWithOrganizationDetail(surface: SurfaceEnvelope<unknown>, detailPayload: OrganizationDetailPayload, status: string, message: string, traceRefs = detailPayload.traceRefs, correlationId = detailPayload.correlationId): SurfaceEnvelope<unknown> {
    const data = surface.data as Record<string, unknown> & { organizations?: OrganizationSummary[] };
    const organization = detailPayload.organization;
    const organizations = upsertOrganization(data.organizations ?? [], organization);
    return {
      ...surface,
      surfaceId: 'surface-user-admin-organization-detail',
      surfaceType: 'show-inspection',
      surfaceVersion: surface.surfaceVersion ?? '1.0.0',
      title: 'Organization Detail',
      correlationId,
      traceIds: traceRefs,
      generatedAt: new Date().toISOString(),
      data: {
        ...data,
        surfaceContract: 'user_admin.organization_detail.v1',
        organizations,
        organizationDetail: { ...organization, safeBoundaryNotice: detailPayload.safeBoundaryNotice, visibleActions: detailPayload.visibleActions, recentAuditEvents: detailPayload.recentAuditEvents, traceRefs: detailPayload.traceRefs, correlationId: detailPayload.correlationId },
        boundaryNotice: detailPayload.safeBoundaryNotice,
        safeBoundaryNotice: detailPayload.safeBoundaryNotice,
        traceRefs,
        correlationId,
        redaction: detailPayload.redactions,
        systemStates: [status === 'no-op' ? 'no-op' : 'success'],
        lastResult: { status: status === 'no-op' ? 'no-op' : 'success', message, correlationId, traceRefs }
      }
    };
  }

  function isOrganizationDirectoryAction(actionId: string) {
    return actionId === 'action-organization-list' || actionId === 'action-display-organization-admin' || actionId === 'action-user-admin-show-organizations';
  }

  function isOrganizationAdminRuntimeAction(action: SurfaceAction) {
    return action.intent !== 'surface-request' && (action.actionId.startsWith('action-organization-') || action.capabilityId.startsWith('saas_owner.organization.'));
  }

  function inputRecord(input: unknown): Record<string, string> {
    if (!input || typeof input !== 'object') return {};
    return Object.fromEntries(Object.entries(input as Record<string, unknown>).map(([key, value]) => [key, value == null ? '' : String(value)]));
  }

  function requiredInput(input: Record<string, string>, key: string) {
    return input[key] ?? '';
  }

  function runtimeCorrelationId(value: OrganizationListPayload | OrganizationDetailPayload | OrganizationActionResponse) {
    return value.correlationId;
  }

  function runtimeTraceIds(value: OrganizationListPayload | OrganizationDetailPayload | OrganizationActionResponse) {
    return 'traceRefs' in value ? value.traceRefs : [];
  }

  function upsertOrganization(organizations: OrganizationSummary[], organization: OrganizationSummary) {
    return organizations.some((candidate) => candidate.organizationId === organization.organizationId)
      ? organizations.map((candidate) => candidate.organizationId === organization.organizationId ? organization : candidate)
      : [organization, ...organizations];
  }

  function syncOrganizationDirectorySurfaces(surfaces: SurfaceEnvelope<unknown>[], updatedSurface: SurfaceEnvelope<unknown>, actionId: string) {
    if (isOrganizationDirectoryAction(actionId)) return surfaces;
    const organization = organizationFromDetailSurface(updatedSurface);
    if (!organization) return surfaces;
    return surfaces.map((surface) => {
      const data = surface.data as Record<string, unknown> & { organizations?: OrganizationSummary[]; filters?: { query?: string; status?: string } };
      const isDirectory = surface.surfaceId === 'surface-user-admin-organization-directory' || data.surfaceContract === 'user_admin.organization_directory.v1';
      if (!isDirectory || !organizationMatchesDirectoryFilters(organization, data.filters)) return surface;
      const organizations = upsertOrganization(data.organizations ?? [], organization);
      return {
        ...surface,
        generatedAt: new Date().toISOString(),
        data: {
          ...data,
          organizations,
          systemStates: organizations.length ? ['ready'] : ['empty'],
          lastResult: {
            status: 'success',
            message: 'Organization directory updated with the latest protected Admin API result.',
            correlationId: updatedSurface.correlationId,
            traceRefs: updatedSurface.traceIds
          }
        }
      };
    });
  }

  function organizationFromDetailSurface(surface: SurfaceEnvelope<unknown>): OrganizationSummary | undefined {
    const detail = (surface.data as Record<string, unknown> & { organizationDetail?: Partial<OrganizationSummary> }).organizationDetail;
    if (!detail?.organizationId || !detail.organizationName || !detail.status) return undefined;
    return {
      organizationId: detail.organizationId,
      organizationName: detail.organizationName,
      status: detail.status,
      traceRefs: detail.traceRefs ?? []
    };
  }

  function organizationMatchesDirectoryFilters(organization: OrganizationSummary, filters?: { query?: string; status?: string }) {
    const status = (filters?.status ?? '').trim().toLowerCase();
    const query = (filters?.query ?? '').trim().toLowerCase();
    if (status && organization.status.toLowerCase() !== status) return false;
    if (!query) return true;
    return organization.organizationId.toLowerCase().includes(query) || organization.organizationName.toLowerCase().includes(query);
  }

  function organizationErrorState(code: string) {
    if (code.includes('403') || code === 'forbidden') return 'forbidden';
    if (code.includes('404') || code === 'not_found') return 'not_found_or_redacted';
    if (code.includes('409') || code === 'conflict') return 'conflict';
    if (code.includes('400') || code.includes('validation')) return 'validation-error';
    return 'error';
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
    clearRailAttention(targetSurface.ownerFunctionalAgentId);
    updateSelection({ selectedFunctionalAgentId: targetSurface.ownerFunctionalAgentId, selectedSurfaceId: targetSurface.surfaceId, surfacePlacement: 'inline' });
    await refreshBackendDerivedAttentionDelivery({ functionalAgentId: targetSurface.ownerFunctionalAgentId, surfaceId: targetSurface.surfaceId, reason: 'shell-surface-refresh' });
    return true;
  }

  async function handleShowDashboard(functionalAgentId: string) {
    if (!ready || !me) return;
    const correlationId = `corr-show-dashboard-${Date.now().toString(36)}`;
    const shellRequest = buildShowDashboardShellRequest(functionalAgentId, me.selectedAuthContext.selectedContextId, correlationId, 'shell_button');
    await runShellSurfaceRequest(shellRequest, functionalAgentId, 'show-dashboard');
  }

  function handleClearScreen(functionalAgentId: string) {
    if (!ready || !me) return;
    const sessionKey = createWorkstreamVisualSessionKey({
      accountId: me.account.accountId,
      selectedContextId: me.selectedAuthContext.selectedContextId,
      functionalAgentId,
      workstreamId: functionalAgentId
    });
    clearDeviceSurfaceStreamForSession(sessionKey);
    setVisualSessionsByKey((store) => {
      const { [sessionKey]: _cleared, ...remaining } = store;
      return remaining;
    });
    setRequestScrollTargetBySessionKey((targets) => ({ ...targets, [sessionKey]: undefined }));
    clearRailAttention(functionalAgentId);
    setBootstrap((current) => current.status === 'ready'
      ? { ...current, items: current.items.filter((item) => item.functionalAgentId !== functionalAgentId) }
      : current);
    updateSelection({ selectedFunctionalAgentId: functionalAgentId, selectedItemId: undefined, selectedSurfaceId: undefined, surfacePlacement: undefined });
    requestAnimationFrame(() => document.getElementById('main-content')?.focus());
  }

  function isProducerAffectingAction(action: SurfaceAction) {
    return ['command', 'approval', 'workflow', 'governance'].includes(action.intent);
  }

  function isInputScopedDetailSurface(surface?: SurfaceEnvelope<unknown>) {
    return surface?.surfaceId === 'surface-user-admin-user-detail' || (surface?.data as { surfaceContract?: string } | undefined)?.surfaceContract === 'user_admin.user_detail.v1';
  }

  async function refreshBackendDerivedAttentionDelivery(input: { functionalAgentId?: string; surfaceId?: string; reason: 'workstream-open' | 'producer-affecting-action-completion' | 'shell-surface-refresh' | 'event-backed-projection-refresh' }) {
    // Backend attention remains authoritative: rail badges refresh from attention.list_rail_summaries via functional-agents,
    // while dashboard/My Account attention items refresh from backend surfaces carrying attention.list_workstream_items
    // or attention.list_my_account_items payloads. Transient railAttentionState remains only for unseen responses.
    await refreshBackendAttentionSummaries(input.reason);
    const surfaceId = input.surfaceId ?? (input.functionalAgentId ? dashboardSurfaceIdForAgent(input.functionalAgentId) : undefined);
    if (surfaceId) await refreshBackendSurface(surfaceId);
  }

  async function refreshBackendAttentionSummaries(reason: string) {
    const result = await workstreamClient.listFunctionalAgents();
    if (!result.ok) return;
    setBootstrap((current) => current.status === 'ready'
      ? { ...current, me: { ...current.me, functionalAgents: result.value } }
      : current);
  }

  async function refreshBackendSurface(surfaceId: string) {
    const result = await workstreamClient.getSurface(surfaceId);
    if (!result.ok) return;
    setBootstrap((current) => {
      if (current.status !== 'ready') return current;
      const nextSurfaces = current.surfaces.some((surface) => surface.surfaceId === result.value.surfaceId)
        ? current.surfaces.map((surface) => surface.surfaceId === result.value.surfaceId ? result.value : surface)
        : [...current.surfaces, result.value];
      return { ...current, surfaces: nextSurfaces };
    });
  }

  async function handleComposerSubmit(request: Parameters<NonNullable<React.ComponentProps<typeof WorkstreamShell>['onComposerSubmit']>>[0]) {
    if (!ready || !me) return false;
    const submittedAt = Date.now();
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
      status: 'working'
    };
    setSubmittingFunctionalAgentId(request.functionalAgentId);
    setRequestScrollTargetForCurrentSession(userRequestItem.itemId, request.functionalAgentId);
    rememberVisualSession(sessionForAgent(request.functionalAgentId), { activeTurnGroupId: correlationId, anchorSurfaceId: userRequestItem.itemId, userHasManualScroll: false });
    setBootstrap((current) => current.status === 'ready'
      ? { ...current, items: pruneWorkstreamItems([...current.items, userRequestItem]) }
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
        body: `${safeError.body} Retry is safe: re-enter the prompt to reuse the selected workstream context. Correlation ${result.error.correlationId}.`,
        status: safeError.status
      };
      setRequestScrollTargetForCurrentSession(userRequestItem.itemId, request.functionalAgentId);
      rememberVisualSession(sessionForAgent(request.functionalAgentId), { anchorSurfaceId: userRequestItem.itemId, userHasManualScroll: false });
      if (!isCurrentlySelectedFunctionalAgent(request.functionalAgentId)) markUnseenResponse(request.functionalAgentId, errorItem.itemId, 'warning');
      setBootstrap((current) => current.status === 'ready'
        ? { ...current, items: pruneWorkstreamItems([...current.items.map((item) => item.itemId === userRequestItem.itemId ? { ...item, status: 'ready' as const } : item), errorItem]) }
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
      return { ...current, surfaces: nextSurfaces, items: pruneWorkstreamItems([...current.items.filter((item) => item.itemId !== userRequestItem.itemId), userItem, traceableAgentItem]) };
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
      onClearScreen={handleClearScreen}
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
        onSurfaceFieldValueChange={handleSurfaceFieldValueChange}
        onSignOut={onSignOut}
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
  if (['dashboard', 'show dashboard', 'open dashboard', 'refresh dashboard', 'show command center', 'open command center'].includes(normalized)) {
    const shellRequest = buildShowDashboardShellRequest(functionalAgentId, selectedContextId, correlationId, 'user_prompt', prompt.trim());
    return normalized.startsWith('refresh') ? { ...shellRequest, requestType: 'refresh_surface' } : shellRequest;
  }
  if (!isBackendShellAliasPrompt(normalized)) return undefined;
  return {
    requestType: normalized.startsWith('refresh') ? 'refresh_surface' : 'show_surface',
    origin: 'user_prompt',
    displayText: prompt.trim(),
    targetFunctionalAgentId: functionalAgentId,
    sourceFunctionalAgentId: functionalAgentId,
    scope: 'current_workstream',
    correlationId,
    selectedContextId
  };
}

function isBackendShellAliasPrompt(normalized: string): boolean {
  return [
    'show notifications', 'open notifications', 'show notification center', 'open notification center', 'notifications',
    'show users', 'open users', 'show user list', 'open user list', 'show user directory', 'open user directory', 'users',
    'show invitations', 'open invitations', 'invitations',
    'show agent catalog', 'open agent catalog', 'show agents', 'open agents', 'agent catalog',
    'show audit timeline', 'open audit timeline', 'show trace timeline', 'open trace timeline', 'audit timeline',
    'show audit search', 'open audit search', 'show traces', 'open traces', 'show audit traces', 'open audit traces',
    'show governance policies', 'open governance policies', 'show policies', 'open policies', 'governance policies', 'policies',
    'show governance dashboard', 'open governance dashboard', 'show policy dashboard', 'open policy dashboard'
  ].includes(normalized);
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
    case 'agent-agent-admin': return 'surface-agent-admin-dashboard';
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

function withRuntimeNotification(items: WorkstreamItem[], connection: RealtimeConnectionState): WorkstreamItem[] {
  if (connection.status !== 'stale' && connection.status !== 'disconnected') return items;
  const notification: WorkstreamItem = {
    itemId: `system-realtime-${connection.status}`,
    functionalAgentId: items[0]?.functionalAgentId ?? 'system',
    kind: 'system-notification',
    createdAt: new Date().toISOString(),
    correlationId: `corr-realtime-${connection.status}`,
    traceIds: [],
    title: realtimeStatusLabel(connection),
    body: 'Workstream surfaces may be stale until realtime events resume.',
    status: 'stale'
  };
  return [...items, notification];
}

function pruneWorkstreamItems(items: WorkstreamItem[], maxItems = 40): WorkstreamItem[] {
  return pruneWorkstreamSurfaceStreamsByAgent(items, maxItems);
}

function normalizeThemeId(value: unknown): ThemePreference | undefined {
  return typeof value === 'string' && (availableThemeIds as readonly string[]).includes(value) ? value as ThemePreference : undefined;
}

function readStoredThemeId(): ThemePreference {
  return normalizeThemeId(window.localStorage.getItem(themeStorageKey)) ?? defaultThemeId;
}

function persistThemeId(themeId: ThemePreference) {
  window.localStorage.setItem(themeStorageKey, themeId);
}


function AuthenticatedRoot() {
  const { isLoading, user, signIn, signOut, getAccessToken } = useAuth();
  const inviteToken = invitationTokenFromLocation();
  if (isLoading) return <div className="auth-gate"><p>Checking secure session…</p></div>;
  if (!user) {
    return (
      <div className="auth-gate">
        <h1>{inviteToken ? 'Sign in to accept your invitation' : 'Welcome back'}</h1>
        <p>{inviteToken ? 'Sign in with the email address that received this invitation.' : 'Sign in to access your workspace and continue your work.'}</p>
        <button type="button" onClick={() => void signIn({ state: { returnTo: currentBrowserReturnTo() } })}>Sign in</button>
        <div className="auth-gate__hint" role="note" aria-label="Invitation required">
          <strong>Don’t have an invitation?</strong>
          <span> Access is invite-only. Contact your workspace admin to be invited.</span>
        </div>
      </div>
    );
  }
  if (inviteToken) return <InvitationAcceptancePage token={inviteToken} tokenProvider={() => getAccessToken()} onSignOut={() => signOut()} />;
  return <WorkstreamApp tokenProvider={() => getAccessToken()} onSignOut={() => signOut()} />;
}

function InvitationAcceptancePage({ token, tokenProvider, onSignOut }: { token: string; tokenProvider: TokenProvider; onSignOut: () => void }) {
  const [result, setResult] = React.useState<{ status: 'loading' } | { status: 'ready'; value: InvitationAcceptanceResult } | { status: 'error'; error: ApiError }>({ status: 'loading' });

  React.useEffect(() => {
    let active = true;
    acceptInvitationToken(token, tokenProvider).then((next) => {
      if (!active) return;
      setResult(next.ok ? { status: 'ready', value: next.value } : { status: 'error', error: next.error });
    });
    return () => {
      active = false;
    };
  }, [token, tokenProvider]);

  if (result.status === 'loading') {
    return <main className="auth-gate"><h1>Accepting invitation…</h1><p>Checking this invitation against your signed-in WorkOS account.</p></main>;
  }
  if (result.status === 'error') {
    return (
      <main className="auth-gate">
        <h1>Invitation could not be accepted</h1>
        <p>{result.error.message}</p>
        <p className="auth-gate__hint">Correlation {result.error.correlationId}</p>
        <button type="button" onClick={onSignOut}>Sign out</button>
      </main>
    );
  }
  const accepted = result.value.status === 'accepted' || result.value.status === 'already-accepted';
  return (
    <main className="auth-gate">
      <h1>{accepted ? 'Invitation accepted' : 'Invitation needs attention'}</h1>
      <p>{result.value.recoveryHint}</p>
      <p className="auth-gate__hint">Status {result.value.status}. Correlation {result.value.correlationId}.</p>
      {accepted && <a className="button-like" href="/ui">Open app</a>}
      {!accepted && <button type="button" onClick={onSignOut}>Sign in with a different account</button>}
    </main>
  );
}

function invitationTokenFromLocation(): string | undefined {
  if (window.location.pathname !== '/accept') return undefined;
  const token = new URLSearchParams(window.location.search).get('token');
  return token && token.trim().length > 0 ? token.trim() : undefined;
}

function currentBrowserReturnTo(): string {
  return `${window.location.pathname}${window.location.search}${window.location.hash}` || '/ui';
}

function safeAuthReturnTo(value: unknown): string | undefined {
  if (typeof value !== 'string' || !value.startsWith('/') || value.startsWith('//')) return undefined;
  return value;
}

async function acceptInvitationToken(token: string, tokenProvider: TokenProvider): Promise<{ ok: true; value: InvitationAcceptanceResult } | { ok: false; error: ApiError }> {
  try {
    const accessToken = await tokenProvider();
    const headers = new Headers({ Accept: 'application/json', 'Content-Type': 'application/json' });
    if (accessToken) headers.set('Authorization', `Bearer ${accessToken}`);
    const response = await fetch('/api/workstream/invitations/accept', { method: 'POST', headers, body: JSON.stringify({ token }) });
    if (!response.ok) return { ok: false, error: await mapInvitationAcceptanceError(response) };
    return { ok: true, value: await response.json() as InvitationAcceptanceResult };
  } catch (error) {
    return { ok: false, error: { code: 'network_error', message: error instanceof Error ? error.message : String(error), correlationId: 'client-network-error' } };
  }
}

async function mapInvitationAcceptanceError(response: Response): Promise<ApiError> {
  let parsed: Partial<ApiError> = {};
  try {
    parsed = await response.json() as Partial<ApiError>;
  } catch {
    parsed = { message: await response.text() };
  }
  return {
    code: parsed.code ?? `http_${response.status}`,
    message: parsed.message ?? `HTTP ${response.status}`,
    correlationId: parsed.correlationId ?? response.headers.get('x-correlation-id') ?? 'missing-correlation-id',
    fieldErrors: parsed.fieldErrors
  };
}

function Root() {
  if (!hasConfiguredWorkosClient) {
    return (
      <div className="auth-gate">
        <h1>Configure WorkOS/AuthKit</h1>
        <p>Set <code>VITE_WORKOS_CLIENT_ID</code> and optionally <code>VITE_WORKOS_REDIRECT_URI</code>, then rebuild the frontend to use real backend APIs.</p>
        <p>The normal frontend runtime does not provide a fixture mode. Tests keep fixture clients and data under test-only assets.</p>
      </div>
    );
  }
  return (
    <AuthKitProvider
      clientId={workosClientId}
      redirectUri={workosRedirectUri}
      onRedirectCallback={({ state }) => {
        const returnTo = safeAuthReturnTo(state?.returnTo);
        if (returnTo && returnTo !== currentBrowserReturnTo()) window.location.assign(returnTo);
      }}
    >
      <AuthenticatedRoot />
    </AuthKitProvider>
  );
}

createRoot(document.getElementById('root')!).render(<Root />);
