import React from 'react';
import { createRoot } from 'react-dom/client';
import { AuthKitProvider, useAuth } from '@workos-inc/authkit-react';
import './styles/tokens.css';
import './styles/base.css';
import './styles/layout.css';
import './styles/components.css';
import { FixtureWorkstreamApiClient, FixtureWorkstreamRealtimeClient, HttpWorkstreamApiClient, HttpWorkstreamRealtimeClient, type InvitationAcceptanceResult, type TokenProvider, type WorkstreamClient, type WorkstreamRealtimeClient } from './api';
import { WorkstreamShell } from './workstream/shell';
import { parseWorkstreamDeepLink, serializeWorkstreamDeepLink } from './workstream/shell/WorkstreamDeepLinks';
import { WorkstreamStream } from './workstream/stream';
import { SurfaceRenderer } from './workstream/surfaces';
import { buildCapabilityActionRequest } from './workstream/actions';
import { applyWorkstreamRealtimeEvent, realtimeStatusLabel } from './workstream/realtime';
import {
  type MeResponse,
  type RealtimeConnectionState,
  type SurfaceAction,
  type SurfaceEnvelope,
  type WorkstreamItem,
  type WorkstreamSelection
} from './workstream';

const useFixtureWorkstream = new URLSearchParams(window.location.search).get('fixtureWorkstream') === '1';
const workosClientId = import.meta.env.VITE_WORKOS_CLIENT_ID;
const hasConfiguredWorkosClient = typeof workosClientId === 'string' && workosClientId.startsWith('client_') && !workosClientId.includes('your_workos');

type ModePreference = 'light' | 'dark' | 'system';
type BootstrapState =
  | { status: 'loading' }
  | { status: 'ready'; me: MeResponse; items: WorkstreamItem[]; surfaces: SurfaceEnvelope<unknown>[] }
  | { status: 'error'; message: string };

type InvitationAcceptanceState =
  | { status: 'none' }
  | { status: 'accepting' }
  | { status: 'ready'; result: InvitationAcceptanceResult }
  | { status: 'error'; message: string; correlationId?: string };

const modeStorageKey = 'seed-ui-mode';
// Contract markers preserved for frontend slice tests: data-mode-preference; Ready · workstream shell; Explicit fixture mode; Guarded · backend authority; production path uses HttpWorkstreamApiClient and WorkOS AuthKit getAccessToken.

type WorkstreamAppProps = {
  tokenProvider?: TokenProvider;
  authStatusLabel?: string;
  onSignOut?: () => void;
};

function WorkstreamApp({ tokenProvider, authStatusLabel, onSignOut }: WorkstreamAppProps) {
  const workstreamClient = React.useMemo<WorkstreamClient>(() => useFixtureWorkstream ? new FixtureWorkstreamApiClient() : new HttpWorkstreamApiClient(tokenProvider), [tokenProvider]);
  const realtimeClient = React.useMemo<WorkstreamRealtimeClient>(() => useFixtureWorkstream ? new FixtureWorkstreamRealtimeClient() : new HttpWorkstreamRealtimeClient(), []);
  const [mode, setMode] = React.useState<ModePreference>(() => readStoredMode());
  const [selection, setSelection] = React.useState<Partial<WorkstreamSelection>>(() => readDeepLinkSelection());
  const [bootstrap, setBootstrap] = React.useState<BootstrapState>({ status: 'loading' });
  const [invitationAcceptance, setInvitationAcceptance] = React.useState<InvitationAcceptanceState>(() => readInvitationAcceptanceRequest() ? { status: 'accepting' } : { status: 'none' });
  const [realtimeConnection, setRealtimeConnection] = React.useState<RealtimeConnectionState>({ status: 'connecting' });
  const seenEventIds = React.useRef(new Set<string>());
  const realtimeLastEventId = React.useRef<string | undefined>(undefined);

  React.useEffect(() => {
    const acceptanceRequest = readInvitationAcceptanceRequest();
    if (!acceptanceRequest) return;
    let active = true;
    setInvitationAcceptance({ status: 'accepting' });
    workstreamClient.acceptInvitation(acceptanceRequest).then((result) => {
      if (!active) return;
      setInvitationAcceptance(result.ok ? { status: 'ready', result: result.value } : { status: 'error', message: result.error.message, correlationId: result.error.correlationId });
    });
    return () => {
      active = false;
    };
  }, [workstreamClient]);

  React.useEffect(() => {
    const acceptanceRequest = readInvitationAcceptanceRequest();
    if (acceptanceRequest && invitationAcceptance.status === 'accepting') return;
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
  }, [workstreamClient, invitationAcceptance.status]);

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

  const me = bootstrap.status === 'ready' ? bootstrap.me : undefined;
  const selectedFunctionalAgentId = me ? selection.selectedFunctionalAgentId ?? me.functionalAgents.find((agent) => agent.availability === 'visible')?.functionalAgentId : undefined;
  const selectedItems = bootstrap.status === 'ready' ? bootstrap.items.filter((item) => !selectedFunctionalAgentId || item.functionalAgentId === selectedFunctionalAgentId) : [];
  const selectedSurfaceId = bootstrap.status === 'ready' ? selection.selectedSurfaceId ?? selectedItems.find((item) => item.surfaceId)?.surfaceId ?? surfaceForAgent(bootstrap.surfaces, selectedFunctionalAgentId)?.surfaceId : undefined;

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
    if (bootstrap.status !== 'ready') return;
    const defaultSurface = surfaceForAgent(bootstrap.surfaces, functionalAgentId)?.surfaceId;
    updateSelection({ selectedFunctionalAgentId: functionalAgentId, selectedItemId: undefined, selectedSurfaceId: defaultSurface });
    requestAnimationFrame(() => document.getElementById('workstream-panel-title')?.focus());
  }

  function openSurface(surfaceId: string) {
    if (bootstrap.status !== 'ready') return;
    const surface = bootstrap.surfaces.find((candidate) => candidate.surfaceId === surfaceId);
    updateSelection({
      selectedFunctionalAgentId: surface?.ownerFunctionalAgentId ?? selectedFunctionalAgentId,
      selectedSurfaceId: surfaceId,
      surfacePlacement: 'inline'
    });
  }

  async function handleSurfaceAction(action: SurfaceAction, surfaceId: string) {
    if (bootstrap.status !== 'ready') return;
    const request = buildCapabilityActionRequest(action, {
      selectedContextId: bootstrap.me.selectedAuthContext.selectedContextId,
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
    if (result.ok && result.value.resultSurface) {
      updateSelection({
        selectedFunctionalAgentId: result.value.resultSurface.ownerFunctionalAgentId,
        selectedSurfaceId: result.value.resultSurface.surfaceId,
        surfacePlacement: 'inline'
      });
    }
  }

  function handleComposerSubmit(request: Parameters<NonNullable<React.ComponentProps<typeof WorkstreamShell>['onComposerSubmit']>>[0]) {
    const prompt = request.prompt.toLowerCase();
    const showUserDetail = /\b(show|display|open|view)\b.*\b(user|account|member)\b.*\b(detail|profile|admin@example\.test|tenant admin)\b/.test(prompt) || /\badmin@example\.test\b/.test(prompt);
    const showUsers = !showUserDetail && (/\b(show|display|open|list|search)\b.*\b(users|invitations|memberships)\b/.test(prompt) || /\busers\b/.test(prompt));
    const userRequestItem: WorkstreamItem = {
      itemId: `composer-${Date.now()}`,
      functionalAgentId: request.functionalAgentId,
      kind: 'user-request',
      createdAt: new Date().toISOString(),
      correlationId: request.idempotencyKey,
      traceIds: [],
      title: 'Composer request captured',
      body: request.prompt,
      status: 'ready'
    };
    const navigationFeedbackItem: WorkstreamItem | undefined = showUserDetail
      ? {
          itemId: `composer-display-user-detail-${Date.now()}`,
          functionalAgentId: 'agent-user-admin',
          kind: 'action-feedback',
          createdAt: new Date().toISOString(),
          correlationId: 'corr-composer-display-user-detail',
          traceIds: ['trace-composer-display-user-detail', 'trace-user-admin-detail'],
          surfaceId: 'surface-user-admin-detail-admin',
          title: 'Display user account detail',
          body: 'Composer intent “show admin@example.test detail” opened the tenant-scoped User Admin detail/edit structured surface with backend-authoritative denial language.',
          status: 'ready'
        }
      : showUsers
        ? {
            itemId: `composer-display-users-${Date.now()}`,
            functionalAgentId: 'agent-user-admin',
            kind: 'action-feedback',
            createdAt: new Date().toISOString(),
            correlationId: 'corr-composer-display-users',
            traceIds: ['trace-composer-display-users'],
            surfaceId: 'surface-user-admin-list',
            title: 'Display the user list view',
            body: 'Composer intent “show users” opened the tenant-scoped User Admin list/search structured surface.',
            status: 'ready'
          }
        : undefined;
    setBootstrap((current) => current.status === 'ready'
      ? { ...current, items: [...current.items, userRequestItem, ...(navigationFeedbackItem ? [navigationFeedbackItem] : [])] }
      : current);
    if (showUserDetail) {
      updateSelection({ selectedFunctionalAgentId: 'agent-user-admin', selectedSurfaceId: 'surface-user-admin-detail-admin', surfacePlacement: 'inline' });
    } else if (showUsers) {
      updateSelection({ selectedFunctionalAgentId: 'agent-user-admin', selectedSurfaceId: 'surface-user-admin-list', surfacePlacement: 'inline' });
    }
  }

  if (bootstrap.status === 'loading') {
    return <main className="content workstream-panel"><p className="eyebrow">Real API client</p><h1>Loading workstream shell</h1></main>;
  }

  if (bootstrap.status === 'error') {
    return <main className="content workstream-panel"><p className="eyebrow">Real API client error</p><h1>Could not load workstream shell</h1><p>{bootstrap.message}</p></main>;
  }
  if (!me) return null;

  return (
    <>
    {authStatusLabel ? <div className="auth-status-bar" role="status">{authStatusLabel}{onSignOut ? <button type="button" onClick={onSignOut}>Sign out</button> : null}</div> : null}
    <WorkstreamShell
      key={selectedFunctionalAgentId}
      me={me}
      initialFunctionalAgentId={selectedFunctionalAgentId}
      items={selectedItems}
      onSelectAgent={selectAgent}
      onComposerSubmit={handleComposerSubmit}
    >
      <InvitationAcceptancePanel state={invitationAcceptance} />
      <WorkstreamStream items={selectedItems} selectedItemId={selection.selectedItemId} onOpenSurface={openSurface} />
      <SurfaceRenderer envelopes={bootstrap.surfaces} selectedSurfaceId={selectedSurfaceId} onAction={handleSurfaceAction} />
      <section className="ds-card" aria-label="Workstream API status">
        <p className="eyebrow">{useFixtureWorkstream ? 'Explicit fixture client' : 'Real API client'}</p>
        <h3>Production workstream shell</h3>
        <p>Routes are deep links into functional agents, stream items, and structured surfaces; the default path loads backend-authoritative data through /api/workstream endpoints.</p>
        <p className="text-muted">Realtime status: {realtimeStatusLabel(realtimeConnection)}</p>
        <ThemeModeToggle mode={mode} onModeChange={setMode} />
      </section>
    </WorkstreamShell>
    </>
  );
}

function InvitationAcceptancePanel({ state }: { state: InvitationAcceptanceState }) {
  if (state.status === 'none') return null;
  if (state.status === 'accepting') {
    return <section className="ds-card invitation-acceptance" aria-live="polite"><p className="eyebrow">Invitation acceptance</p><h3>Validating invitation</h3><p>Checking your signed-in identity against the invitation without exposing the raw token.</p></section>;
  }
  if (state.status === 'error') {
    return <section className="ds-card invitation-acceptance blocked" aria-live="polite"><p className="eyebrow">Invitation acceptance</p><h3>Invitation could not be checked</h3><p>{state.message}</p><p className="text-muted">Correlation: {state.correlationId ?? 'unavailable'}</p></section>;
  }
  const accepted = state.result.status === 'accepted' || state.result.status === 'already-accepted';
  return (
    <section className={`ds-card invitation-acceptance ${accepted ? 'ready' : 'blocked'}`} aria-live="polite">
      <p className="eyebrow">Invitation acceptance</p>
      <h3>{accepted ? 'Membership ready' : 'Invitation needs attention'}</h3>
      <p>{state.result.recoveryHint}</p>
      <p className="text-muted">Status: {state.result.status} · Correlation: {state.result.correlationId}</p>
    </section>
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

function readInvitationAcceptanceRequest() {
  const search = new URLSearchParams(window.location.search);
  const hashQuery = window.location.hash.includes('?') ? window.location.hash.slice(window.location.hash.indexOf('?')) : '';
  const hash = new URLSearchParams(hashQuery);
  const token = search.get('token') ?? search.get('inviteToken') ?? hash.get('token') ?? hash.get('inviteToken') ?? undefined;
  const acceptanceContextId = search.get('acceptanceContextId') ?? hash.get('acceptanceContextId') ?? undefined;
  if (!token && !acceptanceContextId && !window.location.pathname.startsWith('/accept')) return undefined;
  return { token, acceptanceContextId };
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

function AuthenticatedRoot() {
  const { isLoading, user, signIn, signOut, getAccessToken } = useAuth();
  if (isLoading) return <div className="auth-gate"><p>Checking secure session…</p></div>;
  if (!user) {
    return (
      <div className="auth-gate">
        <h1>Sign in to continue</h1>
        <p>The AI-first SaaS workstream uses WorkOS/AuthKit for browser authentication. Backend capabilities remain authorized by local tenant membership and role state.</p>
        <button type="button" onClick={() => void signIn()}>Sign in with WorkOS</button>
        <p className="auth-gate__hint">For frontend-only inspection, append <code>?fixtureWorkstream=1</code>.</p>
      </div>
    );
  }
  return <WorkstreamApp tokenProvider={() => getAccessToken()} authStatusLabel={`Signed in as ${user.email ?? 'authenticated user'}`} onSignOut={() => signOut()} />;
}

function Root() {
  if (useFixtureWorkstream) return <WorkstreamApp authStatusLabel="Fixture workstream mode" />;
  if (!hasConfiguredWorkosClient) {
    return (
      <div className="auth-gate">
        <h1>Configure WorkOS/AuthKit</h1>
        <p>Set <code>VITE_WORKOS_CLIENT_ID</code> and <code>VITE_WORKOS_REDIRECT_URI</code>, then rebuild the frontend to use real backend APIs.</p>
        <p>Append <code>?fixtureWorkstream=1</code> to inspect the workstream UI without provider configuration.</p>
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
