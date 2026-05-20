import React from 'react';
import type { ApiClient, ApiError, TraceDetailResponse, TraceSummary } from '../../api';
import { Button, Card, DataState, SelectField, StatusPill, TextInput } from '../../design-system';

type RemoteData<T> =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'empty' }
  | { status: 'ready'; value: T }
  | { status: 'error'; error: ApiError };

type TraceFilters = {
  goal: string;
  agent: string;
  decision: string;
  policy: string;
  tool: string;
  actor: string;
  time: string;
};

const initialFilters: TraceFilters = { goal: '', agent: '', decision: '', policy: '', tool: '', actor: '', time: '24h' };

export function AuditTraceExplorerPage({ apiClient }: { apiClient: ApiClient }) {
  const [filters, setFilters] = React.useState<TraceFilters>(initialFilters);
  const [resultsState, setResultsState] = React.useState<RemoteData<TraceSummary[]>>({ status: 'idle' });
  const [selectedTraceId, setSelectedTraceId] = React.useState<string>();
  const [detailState, setDetailState] = React.useState<RemoteData<TraceDetailResponse>>({ status: 'idle' });
  const [message, setMessage] = React.useState<string>();

  const search = React.useCallback(async (query: TraceFilters) => {
    setResultsState({ status: 'loading' });
    setMessage(undefined);
    const result = await apiClient.audit.searchTraces(query);
    if (!result.ok) {
      setResultsState({ status: 'error', error: result.error });
      return;
    }
    const filtered = applyFixtureFilter(result.value.items, query);
    if (filtered.length === 0) {
      setResultsState({ status: 'empty' });
      setSelectedTraceId(undefined);
      setDetailState({ status: 'idle' });
      return;
    }
    setResultsState({ status: 'ready', value: filtered });
    setSelectedTraceId((current) => current ?? filtered[0]?.traceId);
  }, [apiClient]);

  const loadDetail = React.useCallback(async (traceId: string) => {
    setDetailState({ status: 'loading' });
    const result = await apiClient.audit.getTrace(traceId);
    setDetailState(result.ok ? { status: 'ready', value: result.value } : { status: 'error', error: result.error });
  }, [apiClient]);

  React.useEffect(() => {
    void search(initialFilters);
  }, [search]);

  React.useEffect(() => {
    if (selectedTraceId) void loadDetail(selectedTraceId);
  }, [selectedTraceId, loadDetail]);

  function submitSearch(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSelectedTraceId(undefined);
    setDetailState({ status: 'idle' });
    void search(filters);
  }

  function forbiddenExport() {
    setMessage('Forbidden export: trace exports require auditor export permission and backend redaction. The fixture keeps this action blocked.');
  }

  return (
    <section className="audit-trace-explorer" aria-label="Audit Trace Explorer">
      <div className="slice-intro">
        <p className="eyebrow">Slice 7</p>
        <h2>Audit Trace Explorer</h2>
        <p>Search work, decision, policy, tool, actor, and time context; inspect authorization basis and correlation IDs before export.</p>
      </div>
      <div className="two-column-flow audit-layout">
        <Card className="form-card" title="Trace filters" subtitle="Filters cover goal, agent, decision, policy, tool, actor, and time window.">
          <form className="stacked-form" onSubmit={submitSearch}>
            <TextInput id="trace-goal" label="Goal" value={filters.goal} onChange={(event) => setFilters({ ...filters, goal: event.target.value })} />
            <TextInput id="trace-agent" label="Agent" value={filters.agent} onChange={(event) => setFilters({ ...filters, agent: event.target.value })} />
            <TextInput id="trace-decision" label="Decision" value={filters.decision} onChange={(event) => setFilters({ ...filters, decision: event.target.value })} />
            <TextInput id="trace-policy" label="Policy" value={filters.policy} onChange={(event) => setFilters({ ...filters, policy: event.target.value })} />
            <TextInput id="trace-tool" label="Tool" value={filters.tool} onChange={(event) => setFilters({ ...filters, tool: event.target.value })} />
            <TextInput id="trace-actor" label="Actor" value={filters.actor} onChange={(event) => setFilters({ ...filters, actor: event.target.value })} />
            <SelectField id="trace-time" label="Time window" value={filters.time} onChange={(event) => setFilters({ ...filters, time: event.target.value })}>
              <option value="1h">Past hour</option>
              <option value="24h">Past 24 hours</option>
              <option value="7d">Past 7 days</option>
              <option value="none">No results fixture window</option>
            </SelectField>
            <div className="page-actions">
              <Button type="submit">Search traces</Button>
              <Button type="button" tone="secondary" onClick={() => { setFilters(initialFilters); void search(initialFilters); }}>Reset filters</Button>
            </div>
          </form>
        </Card>
        <div className="flow-stack">
          {message && <div className="form-status conflict" role="status" aria-live="polite">{message}</div>}
          <TraceResults state={resultsState} selectedTraceId={selectedTraceId} onSelect={setSelectedTraceId} onRetry={() => search(filters)} onExport={forbiddenExport} />
          <TraceDetail state={detailState} onRetry={() => selectedTraceId && loadDetail(selectedTraceId)} />
        </div>
      </div>
    </section>
  );
}

function TraceResults({ state, selectedTraceId, onSelect, onRetry, onExport }: { state: RemoteData<TraceSummary[]>; selectedTraceId?: string; onSelect: (traceId: string) => void; onRetry: () => void; onExport: () => void }) {
  return (
    <DataState
      state={state.status === 'idle' ? { status: 'empty' } : state.status === 'loading' ? { status: 'loading' } : state.status === 'empty' ? { status: 'empty' } : state.status === 'error' ? { status: 'error', error: state.error } : { status: 'ready', value: state.value }}
      loadingLabel="Searching traces…"
      emptyTitle="No trace results"
      emptyDetail="Change filters or reset to recent trace activity. Empty search and no-results states are explicit."
      onRetry={onRetry}
    >
      {(traces) => (
        <Card title="Trace results" subtitle="Rows include authorization basis and correlation IDs for audit review.">
          <div className="page-actions"><Button tone="secondary" onClick={onExport}>Export traces</Button></div>
          <div className="trace-result-list" role="list">
            {traces.map((trace) => (
              <button key={trace.traceId} type="button" className={selectedTraceId === trace.traceId ? 'trace-row selected' : 'trace-row'} onClick={() => onSelect(trace.traceId)}>
                <span><StatusPill tone={trace.category === 'decision' ? 'warning' : 'info'}>{trace.category.replace(/_/g, ' ')}</StatusPill><strong>{trace.action}</strong><small>{trace.actorLabel} → {trace.targetLabel}</small></span>
                <span><small>Authorized by {trace.authorizationBasis}</small><small>Correlation {trace.correlationId}</small></span>
              </button>
            ))}
          </div>
        </Card>
      )}
    </DataState>
  );
}

function TraceDetail({ state, onRetry }: { state: RemoteData<TraceDetailResponse>; onRetry: () => void }) {
  return (
    <DataState
      state={state.status === 'idle' ? { status: 'empty' } : state.status === 'loading' ? { status: 'loading' } : state.status === 'empty' ? { status: 'empty' } : state.status === 'error' ? { status: 'error', error: state.error } : { status: 'ready', value: state.value }}
      loadingLabel="Loading trace detail…"
      emptyTitle="No trace selected"
      emptyDetail="Select a trace row to inspect policy references, safe details, and related links."
      onRetry={onRetry}
    >
      {(trace) => (
        <Card className="trace-detail-panel" title="Trace detail" subtitle="Safe details avoid embedding sensitive data in static assets.">
          <div className="detail-heading">
            <div>
              <StatusPill tone="info">{trace.category.replace(/_/g, ' ')}</StatusPill>
              <h3>{trace.action}</h3>
              <p>{trace.actorLabel} acted on {trace.targetLabel}; authorization basis: {trace.authorizationBasis}.</p>
            </div>
            <span className="version-chip">{trace.traceId}</span>
          </div>
          <div className="decision-facts">
            <div className="fact"><span>Tenant</span><strong>{trace.tenantId}</strong></div>
            <div className="fact"><span>Correlation</span><strong>{trace.correlationId}</strong></div>
            <div className="fact"><span>Timestamp</span><strong>{new Date(trace.timestamp).toLocaleString()}</strong></div>
            <div className="fact"><span>Policies</span><strong>{trace.policyReferences.length}</strong></div>
          </div>
          <section className="trace-link-list" aria-label="Policy references">
            <h3>Policy references</h3>
            {trace.policyReferences.map((policy) => <p key={`${policy.policyId}-${policy.version}-${policy.clauseId}`}>{policy.policyId} {policy.version} {policy.clauseId ?? ''}</p>)}
          </section>
        </Card>
      )}
    </DataState>
  );
}

function applyFixtureFilter(items: TraceSummary[], filters: TraceFilters) {
  if (filters.time === 'none') return [];
  const tokens = [filters.goal, filters.agent, filters.decision, filters.policy, filters.tool, filters.actor].map((value) => value.trim().toLowerCase()).filter(Boolean);
  if (tokens.length === 0) return items;
  return items.filter((trace) => {
    const haystack = `${trace.traceId} ${trace.category} ${trace.actorLabel} ${trace.action} ${trace.targetLabel} ${trace.authorizationBasis} ${trace.correlationId}`.toLowerCase();
    return tokens.every((token) => haystack.includes(token));
  });
}
