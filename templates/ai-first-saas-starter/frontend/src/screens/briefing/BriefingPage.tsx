import React from 'react';
import type { ApiClient, DecisionSummary, GoalSummary, PolicySummary, RealtimeClient, RealtimeConnectionState, RealtimeEvent, TraceSummary } from '../../api';
import { Button, Card, CommandStrip, DataState, KpiCard, StatusPill } from '../../design-system';

type BriefingData = {
  goals: GoalSummary[];
  decisions: DecisionSummary[];
  policies: PolicySummary[];
  traces: TraceSummary[];
};

type RemoteBriefing =
  | { status: 'loading' }
  | { status: 'ready'; value: BriefingData }
  | { status: 'error'; error: { code: string; message: string; correlationId: string } };

type ActivityItem = {
  id: string;
  label: string;
  detail: string;
  status: string;
  tone: 'success' | 'warning' | 'danger' | 'info' | 'neutral';
  version?: number;
};

export function BriefingPage({ apiClient, realtimeClient }: { apiClient: ApiClient; realtimeClient: RealtimeClient }) {
  const [state, setState] = React.useState<RemoteBriefing>({ status: 'loading' });
  const [connectionState, setConnectionState] = React.useState<RealtimeConnectionState>('idle');
  const [events, setEvents] = React.useState<ActivityItem[]>([]);
  const [commandResult, setCommandResult] = React.useState('No command preview has been run. High-impact actions require a separate review screen.');

  const loadBriefing = React.useCallback(async () => {
    setState({ status: 'loading' });
    const [goalsResult, decisionsResult, policiesResult, tracesResult] = await Promise.all([
      apiClient.goals.listGoals(),
      apiClient.decisions.listDecisions(),
      apiClient.governance.listPolicies(),
      apiClient.audit.searchTraces()
    ]);

    const firstError = [goalsResult, decisionsResult, policiesResult, tracesResult].find((result) => !result.ok);
    if (firstError && !firstError.ok) {
      setState({ status: 'error', error: firstError.error });
      return;
    }

    setState({
      status: 'ready',
      value: {
        goals: goalsResult.ok ? goalsResult.value.items : [],
        decisions: decisionsResult.ok ? decisionsResult.value.items : [],
        policies: policiesResult.ok ? policiesResult.value.items : [],
        traces: tracesResult.ok ? tracesResult.value.items : []
      }
    });
  }, [apiClient]);

  React.useEffect(() => {
    void loadBriefing();
  }, [loadBriefing]);

  React.useEffect(() => {
    const stateSubscription = realtimeClient.onState(setConnectionState);
    const eventSubscription = realtimeClient.onEvent((event) => {
      setEvents((current) => mergeActivity(current, activityFromEvent(event)));
      if (event.type === 'goal.updated') {
        setState((current) => current.status === 'ready'
          ? {
              status: 'ready',
              value: {
                ...current.value,
                goals: current.value.goals.map((goal) => goal.goalId === event.subjectId ? { ...goal, updatedAt: event.occurredAt } : goal)
              }
            }
          : current);
      }
    });
    const connectSubscription = realtimeClient.connect(['decisions', 'goals']);

    return () => {
      connectSubscription.unsubscribe();
      eventSubscription.unsubscribe();
      stateSubscription.unsubscribe();
    };
  }, [realtimeClient]);

  return (
    <section className="mission-control" aria-label="Mission Control validation screen">
      <CommandStrip
        title="Mission Control"
        description="Ask for a supervisory summary or policy explanation. This fixture only prepares safe previews; it never executes high-impact commands."
        prompts={['Summarize active work', 'Explain top policy trigger', 'List decisions due soon']}
        sendLabel="Run safe command preview"
        onSend={() => setCommandResult('Safe preview prepared from fixture data. To approve, launch, or commit policy changes, open the relevant decision card or goal gate.')}
      />
      <div className="command-result" role="status" aria-live="polite">{commandResult}</div>

      <RealtimeBanner state={connectionState} eventCount={events.length} />

      <DataState
        state={state.status === 'loading' ? { status: 'loading' } : state.status === 'error' ? { status: 'error', error: state.error } : { status: 'ready', value: state.value }}
        loadingLabel="Loading Mission Control fixture panels…"
        emptyTitle="No active work"
        emptyDetail="No goals, decisions, or policy signals are present in the fixture dataset."
        onRetry={loadBriefing}
      >
        {(data) => <MissionControlContent data={data} events={events} connectionState={connectionState} />}
      </DataState>
    </section>
  );
}

function MissionControlContent({ data, events, connectionState }: { data: BriefingData; events: ActivityItem[]; connectionState: RealtimeConnectionState }) {
  const openDecisions = data.decisions.filter((decision) => decision.status === 'open');
  const waitingGoals = data.goals.filter((goal) => goal.status === 'waiting_for_human' || goal.status === 'blocked');
  const activeGoals = data.goals.filter((goal) => goal.status === 'active' || goal.status === 'waiting_for_human');
  const topDecision = [...openDecisions].sort(prioritySort)[0];

  return (
    <>
      <MissionKpiBand data={data} connectionState={connectionState} />
      <div className="mission-grid">
        <NeedsAttentionPanel decisions={openDecisions} waitingGoals={waitingGoals} topDecision={topDecision} />
        <AgentActivityTimeline traces={data.traces} realtimeEvents={events} />
        <AgentTeamsPanel activeGoals={activeGoals} />
        <TrustControlsPanel policies={data.policies} />
        <UpcomingActionsPanel decisions={openDecisions} />
      </div>
    </>
  );
}

function MissionKpiBand({ data, connectionState }: { data: BriefingData; connectionState: RealtimeConnectionState }) {
  const openDecisions = data.decisions.filter((decision) => decision.status === 'open');
  const urgent = openDecisions.filter((decision) => decision.priority === 'urgent' || decision.priority === 'high');
  const activeGoals = data.goals.filter((goal) => goal.status === 'active' || goal.status === 'waiting_for_human');
  return (
    <div className="mission-kpi-band" aria-label="Mission Control KPI band">
      <KpiCard label="Autonomous work" value={activeGoals.length} detail="Active or waiting goals in fixture context." status="Ready · active work" statusTone="success" icon="◎" />
      <KpiCard label="Needs attention" value={openDecisions.length} detail={`${urgent.length} high-priority review${urgent.length === 1 ? '' : 's'} surfaced first.`} status="Review · human gate" statusTone="warning" icon="◇" />
      <KpiCard label="Policy posture" value={data.policies.length} detail="Active policy controls with approval gates and proposal counts." status="Guarded · policy active" statusTone="info" icon="◈" />
      <KpiCard label="Realtime" value={connectionLabel(connectionState)} detail="Fixture SSE simulates duplicate events, stale state, and recovery." status={realtimeStatusText(connectionState)} statusTone={connectionState === 'stale' ? 'warning' : connectionState === 'connected' ? 'success' : 'neutral'} icon="⌁" />
    </div>
  );
}

function NeedsAttentionPanel({ decisions, waitingGoals, topDecision }: { decisions: DecisionSummary[]; waitingGoals: GoalSummary[]; topDecision?: DecisionSummary }) {
  return (
    <Card className="needs-attention-panel primary-panel" title="Needs attention" subtitle="Highest-priority human reviews stay before secondary panels on narrow screens.">
      {decisions.length === 0 && waitingGoals.length === 0 ? (
        <p>No pending attention. Autonomous work remains inside policy limits.</p>
      ) : (
        <div className="attention-list">
          {topDecision && <AttentionDecision decision={topDecision} primary />}
          {decisions.filter((decision) => decision.decisionId !== topDecision?.decisionId).map((decision) => <AttentionDecision key={decision.decisionId} decision={decision} />)}
          {waitingGoals.map((goal) => (
            <article key={goal.goalId} className="attention-row">
              <div>
                <StatusPill tone="warning">{`${goal.priority} · ${goal.status.replace(/_/g, ' ')}`}</StatusPill>
                <h3>{goal.objective}</h3>
                <p>{goal.pendingDecisionCount} linked decision{goal.pendingDecisionCount === 1 ? '' : 's'} awaiting review.</p>
              </div>
              <Button tone="secondary" onClick={() => announceSafeAction('Goal review opened in fixture mode')}>Review gate</Button>
            </article>
          ))}
        </div>
      )}
    </Card>
  );
}

function AttentionDecision({ decision, primary = false }: { decision: DecisionSummary; primary?: boolean }) {
  return (
    <article className={primary ? 'attention-row primary' : 'attention-row'}>
      <div>
        <StatusPill tone={priorityTone(decision.priority)}>{`${decision.priority} · ${decision.type.replace(/_/g, ' ')}`}</StatusPill>
        <h3>{decision.title}</h3>
        <p>{decision.impactEstimate ?? 'Impact pending'} · risk {decision.riskScore ?? 'n/a'} · confidence {decision.confidenceScore ?? 'n/a'}</p>
        <p className="policy-trigger">Policy: {decision.policyTriggers.join(', ') || 'No trigger recorded'}</p>
      </div>
      <div className="attention-actions">
        <Button tone="primary" onClick={() => announceSafeAction('Decision card opened in fixture mode')}>Open review</Button>
        <Button tone="ghost" onClick={() => announceSafeAction('Trace link prepared in fixture mode')}>Trace</Button>
      </div>
    </article>
  );
}

function AgentActivityTimeline({ traces, realtimeEvents }: { traces: TraceSummary[]; realtimeEvents: ActivityItem[] }) {
  const items: ActivityItem[] = [
    ...realtimeEvents,
    ...traces.map((trace) => ({ id: trace.traceId, label: trace.action, detail: `${trace.actorLabel} · ${trace.authorizationBasis} · ${trace.correlationId}`, status: trace.category, tone: 'info' as const }))
  ].slice(0, 6);

  return (
    <Card className="agent-activity-panel" title="Agent activity" subtitle="Fixture trace and realtime events are deduplicated by event id and version.">
      <ol className="timeline-list">
        {items.map((item) => (
          <li key={item.id}>
            <StatusPill tone={item.tone}>{item.status}</StatusPill>
            <div><strong>{item.label}</strong><p>{item.detail}</p></div>
          </li>
        ))}
      </ol>
    </Card>
  );
}

function AgentTeamsPanel({ activeGoals }: { activeGoals: GoalSummary[] }) {
  return (
    <Card className="agent-teams-panel" title="Agent teams" subtitle="Bounded teams show supervision and current operating limits.">
      <div className="team-list">
        <TeamRow name="Planning team" status="Active" detail={`${activeGoals.length} goal${activeGoals.length === 1 ? '' : 's'} with plan supervision.`} />
        <TeamRow name="Evidence team" status="Monitoring" detail="Collects trace-backed evidence before recommendations." />
        <TeamRow name="Policy evaluator" status="Guarded" detail="Flags approval gates and deviation risks before action." />
      </div>
    </Card>
  );
}

function TeamRow({ name, status, detail }: { name: string; status: string; detail: string }) {
  return <div className="team-row"><StatusPill tone="info">{status}</StatusPill><div><strong>{name}</strong><p>{detail}</p></div></div>;
}

function TrustControlsPanel({ policies }: { policies: PolicySummary[] }) {
  return (
    <Card className="trust-controls-panel" title="Trust controls" subtitle="Policy gates are visible but cannot be bypassed from Mission Control.">
      <div className="policy-list">
        {policies.map((policy) => (
          <article key={policy.policyId} className="policy-row">
            <div>
              <h3>{policy.name}</h3>
              <p>{policy.activeVersion} · owner role {policy.ownerRole}</p>
            </div>
            <StatusPill tone={policy.openProposalCount > 0 ? 'warning' : 'success'}>{`${policy.approvalGateCount} gates · ${policy.openProposalCount} proposals`}</StatusPill>
          </article>
        ))}
      </div>
    </Card>
  );
}

function UpcomingActionsPanel({ decisions }: { decisions: DecisionSummary[] }) {
  const due = decisions.filter((decision) => decision.dueAt).slice(0, 3);
  return (
    <Card className="upcoming-actions-panel" title="Upcoming actions" subtitle="Safe next steps only; no high-impact command executes directly.">
      {due.length === 0 ? <p>No dated reviews in the fixture queue.</p> : (
        <ul className="upcoming-list">
          {due.map((decision) => <li key={decision.decisionId}><strong>{decision.title}</strong><span>{formatDue(decision.dueAt)}</span></li>)}
        </ul>
      )}
      <Button tone="secondary" onClick={() => announceSafeAction('Queue refresh requested in fixture mode')}>Refresh queue</Button>
    </Card>
  );
}

function RealtimeBanner({ state, eventCount }: { state: RealtimeConnectionState; eventCount: number }) {
  return (
    <div className={`realtime-banner ${state}`} role="status" aria-live="polite">
      <StatusPill tone={state === 'stale' ? 'warning' : state === 'connected' ? 'success' : 'neutral'}>{realtimeStatusText(state)}</StatusPill>
      <span>{state === 'stale' ? 'Realtime updates are stale. Reconnecting to fixture stream…' : 'Realtime fixture stream is monitored for stale and recovered states.'}</span>
      <span>{eventCount} unique realtime event{eventCount === 1 ? '' : 's'}</span>
    </div>
  );
}

function activityFromEvent(event: RealtimeEvent): ActivityItem {
  return {
    id: event.eventId,
    label: event.type.replace('.', ' '),
    detail: `${event.topic} · ${event.subjectId} · version ${event.version}`,
    status: event.type.includes('decision') ? 'Decision event' : 'Goal event',
    tone: event.type.includes('decision') ? 'warning' : 'success',
    version: event.version
  };
}

function mergeActivity(current: ActivityItem[], incoming: ActivityItem) {
  const existing = current.findIndex((item) => item.id === incoming.id);
  if (existing < 0) return [incoming, ...current].slice(0, 8);
  if ((incoming.version ?? 0) < (current[existing].version ?? 0)) return current;
  const next = [...current];
  next[existing] = incoming;
  return next;
}

function prioritySort(a: DecisionSummary, b: DecisionSummary) {
  return priorityRank(b.priority) - priorityRank(a.priority);
}

function priorityRank(priority: DecisionSummary['priority']) {
  return { urgent: 4, high: 3, normal: 2, low: 1 }[priority];
}

function priorityTone(priority: DecisionSummary['priority']) {
  return priority === 'urgent' || priority === 'high' ? 'warning' as const : 'info' as const;
}

function connectionLabel(state: RealtimeConnectionState) {
  return state === 'connected' ? 'Live' : state === 'stale' ? 'Stale' : state === 'connecting' ? 'Connecting' : 'Idle';
}

function realtimeStatusText(state: RealtimeConnectionState) {
  return state === 'connected' ? 'Connected · realtime' : state === 'stale' ? 'Stale · reconnecting' : state === 'connecting' ? 'Connecting · realtime' : `${state} · realtime`;
}

function formatDue(dueAt?: string) {
  if (!dueAt) return 'No due date';
  return new Intl.DateTimeFormat(undefined, { hour: 'numeric', minute: '2-digit' }).format(new Date(dueAt));
}

function announceSafeAction(message: string) {
  window.dispatchEvent(new CustomEvent('seed-safe-action', { detail: message }));
}
