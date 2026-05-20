import React from 'react';
import type { ApiClient, ApiError, GoalDetailResponse, GoalSummary, LaunchGoalResponse } from '../../api';
import { Button, Card, DataState, SelectField, StatusPill, TextArea, TextInput } from '../../design-system';

type RemoteData<T> =
  | { status: 'loading' }
  | { status: 'empty' }
  | { status: 'ready'; value: T }
  | { status: 'error'; error: ApiError };

type GoalFormState = {
  objective: string;
  priority: GoalSummary['priority'];
  targetDate: string;
  successCriteria: string;
  constraints: string;
};

type FieldErrors = Partial<Record<keyof GoalFormState | 'acknowledgement', string>>;

const initialGoalForm: GoalFormState = {
  objective: '',
  priority: 'normal',
  targetDate: '',
  successCriteria: '',
  constraints: ''
};

export function GoalWorkbenchPage({ apiClient }: { apiClient: ApiClient }) {
  const [goalsState, setGoalsState] = React.useState<RemoteData<GoalSummary[]>>({ status: 'loading' });
  const [selectedGoalId, setSelectedGoalId] = React.useState<string>();
  const [detailState, setDetailState] = React.useState<RemoteData<GoalDetailResponse> | { status: 'idle' }>({ status: 'idle' });
  const [form, setForm] = React.useState<GoalFormState>(initialGoalForm);
  const [fieldErrors, setFieldErrors] = React.useState<FieldErrors>({});
  const [formMessage, setFormMessage] = React.useState<string>();
  const [submitting, setSubmitting] = React.useState(false);
  const [drafting, setDrafting] = React.useState(false);
  const [launching, setLaunching] = React.useState(false);
  const [launchAck, setLaunchAck] = React.useState(false);
  const [launchResult, setLaunchResult] = React.useState<LaunchGoalResponse>();
  const objectiveRef = React.useRef<HTMLTextAreaElement>(null);
  const successRef = React.useRef<HTMLTextAreaElement>(null);
  const acknowledgementRef = React.useRef<HTMLInputElement>(null);

  const loadGoals = React.useCallback(async () => {
    setGoalsState({ status: 'loading' });
    const result = await apiClient.goals.listGoals();
    if (!result.ok) {
      setGoalsState({ status: 'error', error: result.error });
      return;
    }
    if (result.value.items.length === 0) {
      setGoalsState({ status: 'empty' });
      return;
    }
    setGoalsState({ status: 'ready', value: result.value.items });
    setSelectedGoalId((current) => current ?? result.value.items[0]?.goalId);
  }, [apiClient]);

  const loadGoalDetail = React.useCallback(async (goalId: string) => {
    setDetailState({ status: 'loading' });
    const result = await apiClient.goals.getGoal(goalId);
    setDetailState(result.ok ? { status: 'ready', value: result.value } : { status: 'error', error: result.error });
  }, [apiClient]);

  React.useEffect(() => {
    void loadGoals();
  }, [loadGoals]);

  React.useEffect(() => {
    if (selectedGoalId) void loadGoalDetail(selectedGoalId);
  }, [selectedGoalId, loadGoalDetail]);

  async function createGoal(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setFormMessage(undefined);
    setLaunchResult(undefined);
    const clientErrors = validateGoalForm(form);
    setFieldErrors(clientErrors);
    if (clientErrors.objective) objectiveRef.current?.focus();
    else if (clientErrors.successCriteria) successRef.current?.focus();
    if (Object.keys(clientErrors).length > 0) return;

    setSubmitting(true);
    const result = await apiClient.goals.createGoal({
      objective: form.objective.trim(),
      priority: form.priority,
      targetDate: form.targetDate || undefined,
      successCriteria: lines(form.successCriteria),
      constraints: lines(form.constraints),
      idempotencyKey: `goal-${Date.now()}`
    });
    setSubmitting(false);

    if (!result.ok) {
      setFieldErrors(mapGoalErrors(result.error));
      setFormMessage(result.error.message);
      focusFirstMappedError(result.error, { objective: objectiveRef.current, successCriteria: successRef.current });
      return;
    }

    setForm(initialGoalForm);
    setFieldErrors({});
    setFormMessage(`Goal created. Next action: ${result.value.nextAction.replace(/_/g, ' ')}. Correlation ${result.value.correlationId}.`);
    setSelectedGoalId(result.value.goalId);
    await loadGoals();
  }

  async function draftPlan(detail: GoalDetailResponse) {
    setDrafting(true);
    setFormMessage(undefined);
    const result = await apiClient.goals.draftPlan(detail.goal.goalId, {
      guidance: 'Fixture plan requested from Slice 6 goal detail.',
      expectedGoalVersion: detail.goal.version,
      idempotencyKey: `draft-${detail.goal.goalId}-${Date.now()}`
    });
    setDrafting(false);
    setFormMessage(result.ok ? `Draft plan ${result.value.status}; job ${result.value.planJobId}.` : result.error.message);
  }

  async function launchGoal(detail: GoalDetailResponse) {
    setFieldErrors({});
    setLaunchResult(undefined);
    if (!launchAck) {
      setFieldErrors({ acknowledgement: 'Acknowledge the approval gates before launching.' });
      acknowledgementRef.current?.focus();
      return;
    }
    if (!detail.plan) {
      setFormMessage('Draft a plan before launch.');
      return;
    }
    setLaunching(true);
    const result = await apiClient.goals.launchGoal(detail.goal.goalId, {
      planId: detail.plan.planId,
      expectedGoalVersion: detail.goal.version,
      acknowledgement: launchAck,
      idempotencyKey: `launch-${detail.goal.goalId}-${Date.now()}`
    });
    setLaunching(false);
    if (!result.ok) {
      setFieldErrors(mapGoalErrors(result.error));
      setFormMessage(result.error.message);
      return;
    }
    setLaunchResult(result.value);
    setFormMessage(`Goal launched with trace ${result.value.traceId}.`);
    await loadGoals();
    await loadGoalDetail(detail.goal.goalId);
  }

  return (
    <section className="goal-workbench" aria-label="Goal workbench and detail flow">
      <div className="slice-intro">
        <p className="eyebrow">Slice 6</p>
        <h2>Goal Workbench</h2>
        <p>Define durable goals, request a bounded draft plan, and launch only after visible approval gates are acknowledged.</p>
      </div>
      <div className="goal-layout two-column-flow">
        <GoalForm form={form} errors={fieldErrors} submitting={submitting} objectiveRef={objectiveRef} successRef={successRef} onChange={setForm} onSubmit={createGoal} />
        <div className="flow-stack">
          {formMessage && <div className="form-status" role="status" aria-live="polite">{formMessage}</div>}
          <GoalList state={goalsState} selectedGoalId={selectedGoalId} onSelect={setSelectedGoalId} onRetry={loadGoals} />
          <DataState<GoalDetailResponse>
            state={detailState.status === 'idle' ? { status: 'loading' } : detailState.status === 'loading' ? { status: 'loading' } : detailState.status === 'empty' ? { status: 'empty' } : detailState.status === 'error' ? { status: 'error', error: detailState.error } : { status: 'ready', value: detailState.value }}
            loadingLabel="Loading selected goal detail…"
            emptyTitle="No selected goal"
            emptyDetail="Create or select a goal to review its plan and approval gates."
            onRetry={() => selectedGoalId && loadGoalDetail(selectedGoalId)}
          >
            {(detail) => (
              <GoalDetailPanel
                detail={detail}
                drafting={drafting}
                launching={launching}
                launchAck={launchAck}
                acknowledgementError={fieldErrors.acknowledgement}
                launchResult={launchResult}
                acknowledgementRef={acknowledgementRef}
                onDraftPlan={() => draftPlan(detail)}
                onLaunch={() => launchGoal(detail)}
                onAcknowledgementChange={setLaunchAck}
              />
            )}
          </DataState>
        </div>
      </div>
    </section>
  );
}

function GoalForm({ form, errors, submitting, objectiveRef, successRef, onChange, onSubmit }: { form: GoalFormState; errors: FieldErrors; submitting: boolean; objectiveRef: React.RefObject<HTMLTextAreaElement | null>; successRef: React.RefObject<HTMLTextAreaElement | null>; onChange: (form: GoalFormState) => void; onSubmit: (event: React.FormEvent<HTMLFormElement>) => void }) {
  return (
    <Card className="form-card" title="Create a durable goal" subtitle="Client validation preserves input. Backend validation remains authoritative when real APIs are connected.">
      <form className="stacked-form" onSubmit={onSubmit} noValidate>
        <TextArea id="goal-objective" ref={objectiveRef} label="Objective" helper="Describe the operational outcome, not a UI task." error={errors.objective} value={form.objective} onChange={(event) => onChange({ ...form, objective: event.target.value })} />
        <SelectField id="goal-priority" label="Priority" value={form.priority} onChange={(event) => onChange({ ...form, priority: event.target.value as GoalSummary['priority'] })}>
          <option value="low">Low</option>
          <option value="normal">Normal</option>
          <option value="high">High</option>
          <option value="urgent">Urgent</option>
        </SelectField>
        <TextInput id="goal-target-date" label="Target date" helper="Optional planning target." type="date" value={form.targetDate} onChange={(event) => onChange({ ...form, targetDate: event.target.value })} />
        <TextArea id="goal-success-criteria" ref={successRef} label="Success criteria" helper="One measurable criterion per line." error={errors.successCriteria} value={form.successCriteria} onChange={(event) => onChange({ ...form, successCriteria: event.target.value })} />
        <TextArea id="goal-constraints" label="Constraints" helper="Optional authority, tool, timing, or risk constraints. One per line." value={form.constraints} onChange={(event) => onChange({ ...form, constraints: event.target.value })} />
        <Button type="submit" disabled={submitting}>{submitting ? 'Creating goal…' : 'Create goal'}</Button>
      </form>
    </Card>
  );
}

function GoalList({ state, selectedGoalId, onSelect, onRetry }: { state: RemoteData<GoalSummary[]>; selectedGoalId?: string; onSelect: (goalId: string) => void; onRetry: () => void }) {
  return (
    <DataState
      state={state.status === 'loading' ? { status: 'loading' } : state.status === 'empty' ? { status: 'empty' } : state.status === 'error' ? { status: 'error', error: state.error } : { status: 'ready', value: state.value }}
      loadingLabel="Loading goals…"
      emptyTitle="No goals yet"
      emptyDetail="Create a goal to start the plan review flow."
      onRetry={onRetry}
    >
      {(goals) => (
        <Card title="Goal detail list" subtitle="Select a goal to review plan state, linked decisions, and traceability.">
          <div className="selectable-list" role="list">
            {goals.map((goal) => (
              <button key={goal.goalId} type="button" className={goal.goalId === selectedGoalId ? 'selectable-row selected' : 'selectable-row'} onClick={() => onSelect(goal.goalId)}>
                <span><strong>{goal.objective}</strong><small>{goal.ownerDisplayName} · {goal.pendingDecisionCount} pending decisions</small></span>
                <StatusPill tone={goal.status === 'active' ? 'success' : goal.status === 'waiting_for_human' ? 'warning' : 'info'}>{`${goal.priority} · ${goal.status.replace(/_/g, ' ')}`}</StatusPill>
              </button>
            ))}
          </div>
        </Card>
      )}
    </DataState>
  );
}

function GoalDetailPanel({ detail, drafting, launching, launchAck, acknowledgementError, launchResult, acknowledgementRef, onDraftPlan, onLaunch, onAcknowledgementChange }: { detail: GoalDetailResponse; drafting: boolean; launching: boolean; launchAck: boolean; acknowledgementError?: string; launchResult?: LaunchGoalResponse; acknowledgementRef: React.RefObject<HTMLInputElement | null>; onDraftPlan: () => void; onLaunch: () => void; onAcknowledgementChange: (checked: boolean) => void }) {
  return (
    <Card className="detail-card" title="Selected goal detail" subtitle="Plan review and launch actions stay fixture-backed in this slice.">
      <div className="detail-heading">
        <div>
          <StatusPill tone={detail.goal.status === 'active' ? 'success' : detail.goal.status === 'waiting_for_human' ? 'warning' : 'info'}>{detail.goal.status.replace(/_/g, ' ')}</StatusPill>
          <h3>{detail.goal.objective}</h3>
          <p>{detail.goal.successCriteria.join(' · ')}</p>
        </div>
        <span className="version-chip">v{detail.goal.version}</span>
      </div>
      <section className="plan-panel" aria-labelledby="plan-panel-heading">
        <h3 id="plan-panel-heading">Plan review</h3>
        {detail.plan ? (
          <ol className="step-list">
            {detail.plan.steps.map((step) => (
              <li key={step.stepId}>
                <strong>{step.title}</strong>
                <p>{step.assignedAgent ?? 'Unassigned'} · side effects: {step.expectedSideEffects.length || 0} · gates: {step.requiredApprovalGateIds.join(', ') || 'none'}</p>
              </li>
            ))}
          </ol>
        ) : <p>No plan has been drafted yet.</p>}
        <Button tone="secondary" disabled={drafting} onClick={onDraftPlan}>{drafting ? 'Drafting plan…' : 'Request draft plan'}</Button>
      </section>
      <section className="approval-panel" aria-labelledby="approval-panel-heading">
        <h3 id="approval-panel-heading">Approval gates</h3>
        <div className="gate-list">
          {detail.approvalGates.map((gate) => <div key={gate.gateId} className="gate-row"><StatusPill tone={gate.status === 'required' ? 'warning' : 'success'}>{gate.status.replace(/_/g, ' ')}</StatusPill><span>{gate.name}</span><small>{gate.policyClauseId}</small></div>)}
        </div>
        <label className="checkbox-row">
          <input ref={acknowledgementRef} type="checkbox" checked={launchAck} aria-invalid={acknowledgementError ? 'true' : undefined} aria-describedby={acknowledgementError ? 'launch-ack-error' : undefined} onChange={(event) => onAcknowledgementChange(event.target.checked)} />
          <span>I acknowledge the listed approval gates, evidence expectations, and authority limits before launch.</span>
        </label>
        {acknowledgementError && <p id="launch-ack-error" className="field-error" role="alert">{acknowledgementError}</p>}
        <Button disabled={launching} onClick={onLaunch}>{launching ? 'Launching goal…' : 'Approve launch'}</Button>
        {launchResult && <p className="success-note" role="status">Launched as {launchResult.status}; trace {launchResult.traceId}; correlation {launchResult.correlationId}.</p>}
      </section>
      <section className="trace-link-list" aria-label="Goal trace links">
        <h3>Trace links</h3>
        {detail.traceLinks.map((trace) => <a key={trace.traceId} href={trace.href}>{trace.label}</a>)}
      </section>
    </Card>
  );
}

function validateGoalForm(form: GoalFormState): FieldErrors {
  const errors: FieldErrors = {};
  if (form.objective.trim().length < 10) errors.objective = 'Describe the intended outcome in at least 10 characters.';
  if (lines(form.successCriteria).length === 0) errors.successCriteria = 'Add at least one measurable success criterion.';
  return errors;
}

function mapGoalErrors(error: ApiError): FieldErrors {
  return {
    objective: error.fieldErrors?.objective?.[0],
    successCriteria: error.fieldErrors?.successCriteria?.[0],
    acknowledgement: error.fieldErrors?.acknowledgement?.[0]
  };
}

function focusFirstMappedError(error: ApiError, refs: { objective: HTMLTextAreaElement | null; successCriteria: HTMLTextAreaElement | null }) {
  if (error.fieldErrors?.objective) refs.objective?.focus();
  else if (error.fieldErrors?.successCriteria) refs.successCriteria?.focus();
}

function lines(value: string) {
  return value.split('\n').map((line) => line.trim()).filter(Boolean);
}
