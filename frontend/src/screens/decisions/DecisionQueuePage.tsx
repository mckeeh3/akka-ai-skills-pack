import React from 'react';
import type { ApiClient, ApiError, DecisionAction, DecisionActionResponse, DecisionDetailResponse, DecisionSummary } from '../../api';
import { Button, Card, DataState, SelectField, StatusPill, TextArea } from '../../design-system';

type RemoteData<T> =
  | { status: 'loading' }
  | { status: 'empty' }
  | { status: 'ready'; value: T }
  | { status: 'error'; error: ApiError };

type ActionForm = {
  action: DecisionAction;
  comment: string;
  counterproposal: string;
  acknowledgement: boolean;
  useStaleVersion: boolean;
};

type FieldErrors = Partial<Record<keyof ActionForm, string>>;

const initialActionForm: ActionForm = {
  action: 'approve',
  comment: '',
  counterproposal: '',
  acknowledgement: false,
  useStaleVersion: false
};

export function DecisionQueuePage({ apiClient }: { apiClient: ApiClient }) {
  const [decisionsState, setDecisionsState] = React.useState<RemoteData<DecisionSummary[]>>({ status: 'loading' });
  const [selectedDecisionId, setSelectedDecisionId] = React.useState<string>();
  const [detailState, setDetailState] = React.useState<RemoteData<DecisionDetailResponse> | { status: 'idle' }>({ status: 'idle' });
  const [form, setForm] = React.useState<ActionForm>(initialActionForm);
  const [errors, setErrors] = React.useState<FieldErrors>({});
  const [submitting, setSubmitting] = React.useState(false);
  const [result, setResult] = React.useState<DecisionActionResponse>();
  const [message, setMessage] = React.useState<string>();
  const acknowledgementRef = React.useRef<HTMLInputElement>(null);
  const counterRef = React.useRef<HTMLTextAreaElement>(null);
  const selectedButtonRef = React.useRef<HTMLButtonElement>(null);

  const loadDecisions = React.useCallback(async () => {
    setDecisionsState({ status: 'loading' });
    const result = await apiClient.decisions.listDecisions();
    if (!result.ok) {
      setDecisionsState({ status: 'error', error: result.error });
      return;
    }
    const openFirst = [...result.value.items].sort(prioritySort)[0];
    if (result.value.items.length === 0) {
      setDecisionsState({ status: 'empty' });
      return;
    }
    setDecisionsState({ status: 'ready', value: result.value.items });
    setSelectedDecisionId((current) => current ?? openFirst?.decisionId);
  }, [apiClient]);

  const loadDetail = React.useCallback(async (decisionId: string) => {
    setDetailState({ status: 'loading' });
    const result = await apiClient.decisions.getDecision(decisionId);
    setDetailState(result.ok ? { status: 'ready', value: result.value } : { status: 'error', error: result.error });
  }, [apiClient]);

  React.useEffect(() => {
    void loadDecisions();
  }, [loadDecisions]);

  React.useEffect(() => {
    if (selectedDecisionId) void loadDetail(selectedDecisionId);
  }, [selectedDecisionId, loadDetail]);

  React.useEffect(() => {
    setResult(undefined);
    setMessage(undefined);
    setErrors({});
    setForm(initialActionForm);
  }, [selectedDecisionId]);

  async function submitDecisionAction(detail: DecisionDetailResponse, event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setResult(undefined);
    setMessage(undefined);
    const clientErrors = validateActionForm(form, detail);
    setErrors(clientErrors);
    if (clientErrors.acknowledgement) acknowledgementRef.current?.focus();
    else if (clientErrors.counterproposal) counterRef.current?.focus();
    if (Object.keys(clientErrors).length > 0) return;

    setSubmitting(true);
    const expectedVersion = form.useStaleVersion ? detail.version - 1 : detail.version;
    const apiResult = await apiClient.decisions.actOnDecision(detail.decisionId, form.action, {
      expectedVersion,
      comment: form.comment || undefined,
      counterproposal: form.counterproposal || undefined,
      acknowledgement: form.acknowledgement,
      idempotencyKey: `decision-${detail.decisionId}-${form.action}-${Date.now()}`
    });
    setSubmitting(false);

    if (!apiResult.ok) {
      setErrors(mapActionErrors(apiResult.error));
      setMessage(apiResult.error.code === 'conflict' ? `Stale conflict: ${apiResult.error.message}` : apiResult.error.message);
      if (apiResult.error.fieldErrors?.acknowledgement) acknowledgementRef.current?.focus();
      return;
    }

    setResult(apiResult.value);
    setMessage(`${labelForAction(form.action)} completed. Focus can move to the next actionable decision.`);
    setForm(initialActionForm);
    await loadDecisions();
    await loadDetail(detail.decisionId);
    requestAnimationFrame(() => selectedButtonRef.current?.focus());
  }

  return (
    <section className="decision-queue" aria-label="Decision queue and decision card detail flow">
      <div className="slice-intro">
        <p className="eyebrow">Slice 6</p>
        <h2>Decision Queue</h2>
        <p>Review recommendations with evidence, risk, confidence, impact, policy triggers, allowed actions, trace links, and stale-conflict handling.</p>
      </div>
      <div className="decision-layout two-column-flow">
        <DecisionList state={decisionsState} selectedDecisionId={selectedDecisionId} selectedButtonRef={selectedButtonRef} onSelect={setSelectedDecisionId} onRetry={loadDecisions} />
        <div className="flow-stack">
          {message && <div className={message.startsWith('Stale conflict') ? 'form-status conflict' : 'form-status'} role="status" aria-live="polite">{message}</div>}
          <DataState<DecisionDetailResponse>
            state={detailState.status === 'idle' ? { status: 'loading' } : detailState.status === 'loading' ? { status: 'loading' } : detailState.status === 'empty' ? { status: 'empty' } : detailState.status === 'error' ? { status: 'error', error: detailState.error } : { status: 'ready', value: detailState.value }}
            loadingLabel="Loading selected decision card…"
            emptyTitle="No selected decision"
            emptyDetail="Select a decision to inspect its recommendation and allowed actions."
            onRetry={() => selectedDecisionId && loadDetail(selectedDecisionId)}
          >
            {(detail) => (
              <DecisionDetailPanel
                detail={detail}
                form={form}
                errors={errors}
                submitting={submitting}
                result={result}
                acknowledgementRef={acknowledgementRef}
                counterRef={counterRef}
                onFormChange={setForm}
                onSubmit={(event) => submitDecisionAction(detail, event)}
              />
            )}
          </DataState>
        </div>
      </div>
    </section>
  );
}

function DecisionList({ state, selectedDecisionId, selectedButtonRef, onSelect, onRetry }: { state: RemoteData<DecisionSummary[]>; selectedDecisionId?: string; selectedButtonRef: React.RefObject<HTMLButtonElement | null>; onSelect: (decisionId: string) => void; onRetry: () => void }) {
  return (
    <DataState
      state={state.status === 'loading' ? { status: 'loading' } : state.status === 'empty' ? { status: 'empty' } : state.status === 'error' ? { status: 'error', error: state.error } : { status: 'ready', value: state.value }}
      loadingLabel="Loading decision queue…"
      emptyTitle="No open decisions"
      emptyDetail="There are no recommendations, exceptions, or deviations requiring human action."
      onRetry={onRetry}
    >
      {(decisions) => (
        <Card title="Risk-ranked queue" subtitle="Open high-priority decisions appear first. Status is always textual, not color-only.">
          <div className="queue-count" role="status">{decisions.filter((decision) => decision.status === 'open').length} open decisions</div>
          <div className="selectable-list" role="list">
            {[...decisions].sort(prioritySort).map((decision) => (
              <button key={decision.decisionId} ref={decision.decisionId === selectedDecisionId ? selectedButtonRef : undefined} type="button" className={decision.decisionId === selectedDecisionId ? 'selectable-row selected' : 'selectable-row'} onClick={() => onSelect(decision.decisionId)}>
                <span><strong>{decision.title}</strong><small>{decision.type.replace(/_/g, ' ')} · risk {decision.riskScore ?? 'n/a'} · confidence {decision.confidenceScore ?? 'n/a'}</small></span>
                <StatusPill tone={priorityTone(decision.priority)}>{`${decision.priority} · ${decision.status}`}</StatusPill>
              </button>
            ))}
          </div>
        </Card>
      )}
    </DataState>
  );
}

function DecisionDetailPanel({ detail, form, errors, submitting, result, acknowledgementRef, counterRef, onFormChange, onSubmit }: { detail: DecisionDetailResponse; form: ActionForm; errors: FieldErrors; submitting: boolean; result?: DecisionActionResponse; acknowledgementRef: React.RefObject<HTMLInputElement | null>; counterRef: React.RefObject<HTMLTextAreaElement | null>; onFormChange: (form: ActionForm) => void; onSubmit: (event: React.FormEvent<HTMLFormElement>) => void }) {
  return (
    <Card className="decision-detail-card" title="Decision card detail" subtitle="Approve, reject, request changes, escalate, or counter only through explicit allowed actions.">
      <div className="detail-heading">
        <div>
          <StatusPill tone={priorityTone(detail.priority)}>{`${detail.priority} · ${detail.type.replace(/_/g, ' ')}`}</StatusPill>
          <h3>{detail.title}</h3>
          <p>{detail.recommendation}</p>
        </div>
        <span className="version-chip">v{detail.version}</span>
      </div>
      <div className="decision-facts">
        <Fact label="Risk" value={String(detail.riskScore ?? 'n/a')} />
        <Fact label="Confidence" value={String(detail.confidenceScore ?? 'n/a')} />
        <Fact label="Impact" value={detail.impactEstimate ?? 'Impact pending'} />
        <Fact label="Policy" value={detail.policyTriggers.join(', ') || 'No trigger recorded'} />
      </div>
      <section className="evidence-panel" aria-labelledby="evidence-heading">
        <h3 id="evidence-heading">Evidence</h3>
        <div className="evidence-list">
          {detail.evidenceItems.map((item) => <article key={item.evidenceId} className="evidence-row"><strong>{item.label}</strong><p>{item.summary}</p><small>{item.sourceType}</small></article>)}
        </div>
      </section>
      <section aria-labelledby="alternatives-heading">
        <h3 id="alternatives-heading">Alternatives considered</h3>
        <ul className="compact-list">{detail.alternativesConsidered.map((item) => <li key={item}>{item}</li>)}</ul>
      </section>
      <DecisionActionForm detail={detail} form={form} errors={errors} submitting={submitting} acknowledgementRef={acknowledgementRef} counterRef={counterRef} onFormChange={onFormChange} onSubmit={onSubmit} />
      {result && <p className="success-note" role="status">{labelForAction(result.action)} produced {result.status}; trace {result.traceId}; correlation {result.correlationId}.</p>}
      <section className="trace-link-list" aria-label="Decision trace links">
        <h3>Trace links</h3>
        {detail.traceLinks.map((trace) => <a key={trace.traceId} href={trace.href}>{trace.label}</a>)}
      </section>
    </Card>
  );
}

function DecisionActionForm({ detail, form, errors, submitting, acknowledgementRef, counterRef, onFormChange, onSubmit }: { detail: DecisionDetailResponse; form: ActionForm; errors: FieldErrors; submitting: boolean; acknowledgementRef: React.RefObject<HTMLInputElement | null>; counterRef: React.RefObject<HTMLTextAreaElement | null>; onFormChange: (form: ActionForm) => void; onSubmit: (event: React.FormEvent<HTMLFormElement>) => void }) {
  const actionOptions = detail.allowedActions;
  return (
    <form className="stacked-form action-form" onSubmit={onSubmit} noValidate>
      <SelectField id="decision-action" label="Decision action" value={form.action} onChange={(event) => onFormChange({ ...form, action: event.target.value as DecisionAction })}>
        {actionOptions.map((action) => <option key={action} value={action}>{labelForAction(action)}</option>)}
      </SelectField>
      <TextArea id="decision-comment" label="Reviewer comment" helper="Optional rationale saved with the trace when real APIs are connected." value={form.comment} onChange={(event) => onFormChange({ ...form, comment: event.target.value })} />
      {form.action === 'counter' && <TextArea id="decision-counterproposal" ref={counterRef} label="Counterproposal" helper="Required for counter actions." error={errors.counterproposal} value={form.counterproposal} onChange={(event) => onFormChange({ ...form, counterproposal: event.target.value })} />}
      <label className="checkbox-row">
        <input ref={acknowledgementRef} type="checkbox" checked={form.acknowledgement} aria-invalid={errors.acknowledgement ? 'true' : undefined} aria-describedby={errors.acknowledgement ? 'decision-ack-error' : undefined} onChange={(event) => onFormChange({ ...form, acknowledgement: event.target.checked })} />
        <span>I reviewed the evidence, risk, confidence, impact, policy trigger, and trace context for this action.</span>
      </label>
      {errors.acknowledgement && <p id="decision-ack-error" className="field-error" role="alert">{errors.acknowledgement}</p>}
      <label className="checkbox-row subtle">
        <input type="checkbox" checked={form.useStaleVersion} onChange={(event) => onFormChange({ ...form, useStaleVersion: event.target.checked })} />
        <span>Simulate stale version conflict on submit.</span>
      </label>
      <Button type="submit" disabled={submitting}>{submitting ? 'Submitting decision action…' : labelForAction(form.action)}</Button>
    </form>
  );
}

function Fact({ label, value }: { label: string; value: string }) {
  return <div className="fact"><span>{label}</span><strong>{value}</strong></div>;
}

function validateActionForm(form: ActionForm, detail: DecisionDetailResponse): FieldErrors {
  const errors: FieldErrors = {};
  if (form.action === 'approve' && detail.priority === 'high' && !form.acknowledgement) errors.acknowledgement = 'High-impact approval requires explicit acknowledgement.';
  if (form.action === 'counter' && form.counterproposal.trim().length < 8) errors.counterproposal = 'Describe the counterproposal before submitting.';
  return errors;
}

function mapActionErrors(error: ApiError): FieldErrors {
  return {
    acknowledgement: error.fieldErrors?.acknowledgement?.[0],
    counterproposal: error.fieldErrors?.counterproposal?.[0]
  };
}

function prioritySort(a: DecisionSummary, b: DecisionSummary) {
  return priorityRank(b.priority) - priorityRank(a.priority);
}

function priorityRank(priority: DecisionSummary['priority']) {
  return { low: 0, normal: 1, high: 2, urgent: 3 }[priority];
}

function priorityTone(priority: DecisionSummary['priority']) {
  return priority === 'urgent' || priority === 'high' ? 'warning' : priority === 'normal' ? 'info' : 'neutral';
}

function labelForAction(action: DecisionAction) {
  return {
    approve: 'Approve decision',
    reject: 'Reject decision',
    request_changes: 'Request changes',
    escalate: 'Escalate decision',
    counter: 'Submit counterproposal',
    convert_to_policy_proposal: 'Convert to policy proposal'
  }[action];
}
