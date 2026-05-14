import React from 'react';
import type { ApiClient, ApiError, PolicySummary } from '../../api';
import { Button, Card, DataState, SelectField, StatusPill, TextArea, TextInput } from '../../design-system';

type RemoteData<T> =
  | { status: 'loading' }
  | { status: 'empty' }
  | { status: 'ready'; value: T }
  | { status: 'error'; error: ApiError };

type ProposalForm = {
  policyId: string;
  title: string;
  proposedChange: string;
  rationale: string;
  expectedImpact: string;
  simulationScope: string;
  acknowledgement: boolean;
};

type ProposalErrors = Partial<Record<keyof ProposalForm, string>>;

type SimulationState =
  | { status: 'idle' }
  | { status: 'queued'; proposalId: string; jobId: string; correlationId: string }
  | { status: 'running'; proposalId: string; jobId: string; correlationId: string }
  | { status: 'completed'; proposalId: string; jobId: string; correlationId: string };

const initialForm: ProposalForm = {
  policyId: '',
  title: '',
  proposedChange: '',
  rationale: '',
  expectedImpact: '',
  simulationScope: 'recent decisions and approval gates',
  acknowledgement: false
};

export function GovernancePoliciesPage({ apiClient }: { apiClient: ApiClient }) {
  const [policiesState, setPoliciesState] = React.useState<RemoteData<PolicySummary[]>>({ status: 'loading' });
  const [form, setForm] = React.useState<ProposalForm>(initialForm);
  const [errors, setErrors] = React.useState<ProposalErrors>({});
  const [message, setMessage] = React.useState<string>();
  const [submitting, setSubmitting] = React.useState(false);
  const [simulation, setSimulation] = React.useState<SimulationState>({ status: 'idle' });
  const [commitReady, setCommitReady] = React.useState(false);
  const titleRef = React.useRef<HTMLInputElement>(null);
  const changeRef = React.useRef<HTMLTextAreaElement>(null);
  const ackRef = React.useRef<HTMLInputElement>(null);

  const loadPolicies = React.useCallback(async () => {
    setPoliciesState({ status: 'loading' });
    const result = await apiClient.governance.listPolicies();
    if (!result.ok) {
      setPoliciesState({ status: 'error', error: result.error });
      return;
    }
    if (result.value.items.length === 0) {
      setPoliciesState({ status: 'empty' });
      return;
    }
    setPoliciesState({ status: 'ready', value: result.value.items });
    setForm((current) => current.policyId ? current : { ...current, policyId: result.value.items[0].policyId });
  }, [apiClient]);

  React.useEffect(() => {
    void loadPolicies();
  }, [loadPolicies]);

  async function submitProposal(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setMessage(undefined);
    setCommitReady(false);
    const clientErrors = validateProposal(form);
    setErrors(clientErrors);
    if (clientErrors.title) titleRef.current?.focus();
    else if (clientErrors.proposedChange) changeRef.current?.focus();
    else if (clientErrors.acknowledgement) ackRef.current?.focus();
    if (Object.keys(clientErrors).length > 0) return;

    setSubmitting(true);
    const createResult = await apiClient.governance.createPolicyProposal({
      policyId: form.policyId,
      title: form.title.trim(),
      proposedChange: form.proposedChange.trim(),
      rationale: form.rationale.trim(),
      expectedImpact: form.expectedImpact.trim(),
      simulationScope: form.simulationScope.trim(),
      idempotencyKey: `proposal-${Date.now()}`
    });
    if (!createResult.ok) {
      setSubmitting(false);
      setMessage(createResult.error.message);
      return;
    }

    const simulationResult = await apiClient.governance.simulatePolicyProposal(createResult.value.proposalId);
    setSubmitting(false);
    if (!simulationResult.ok) {
      setMessage(simulationResult.error.message);
      return;
    }

    setSimulation({ status: 'queued', proposalId: createResult.value.proposalId, jobId: simulationResult.value.simulationJobId, correlationId: simulationResult.value.correlationId });
    setMessage(`Proposal ${createResult.value.status}; simulation queued. Correlation ${createResult.value.correlationId}.`);
    window.setTimeout(() => setSimulation({ status: 'running', proposalId: createResult.value.proposalId, jobId: simulationResult.value.simulationJobId, correlationId: simulationResult.value.correlationId }), 60);
    window.setTimeout(() => {
      setSimulation({ status: 'completed', proposalId: createResult.value.proposalId, jobId: simulationResult.value.simulationJobId, correlationId: simulationResult.value.correlationId });
      setCommitReady(true);
    }, 120);
  }

  function confirmCommit() {
    if (simulation.status !== 'completed') return;
    setMessage(`Authorized commit remains a backend-governed action. Simulation ${simulation.jobId} is complete; policy owner approval is still required before activation.`);
  }

  return (
    <section className="governance-center" aria-label="Governance Center policy proposal flow">
      <div className="slice-intro">
        <p className="eyebrow">Slice 7</p>
        <h2>Governance Center</h2>
        <p>Policy owners can review active policies, draft proposals, run fixture simulations, and see explicit authority-change warnings before any commit.</p>
      </div>
      <div className="two-column-flow governance-layout">
        <PolicyList state={policiesState} selectedPolicyId={form.policyId} onSelect={(policyId) => setForm({ ...form, policyId })} onRetry={loadPolicies} />
        <div className="flow-stack">
          {message && <div className="form-status" role="status" aria-live="polite">{message}</div>}
          <ProposalPanel form={form} errors={errors} policiesState={policiesState} submitting={submitting} titleRef={titleRef} changeRef={changeRef} ackRef={ackRef} onChange={setForm} onSubmit={submitProposal} />
          <SimulationPanel simulation={simulation} commitReady={commitReady} onConfirmCommit={confirmCommit} />
        </div>
      </div>
    </section>
  );
}

function PolicyList({ state, selectedPolicyId, onSelect, onRetry }: { state: RemoteData<PolicySummary[]>; selectedPolicyId: string; onSelect: (policyId: string) => void; onRetry: () => void }) {
  return (
    <DataState
      state={state.status === 'loading' ? { status: 'loading' } : state.status === 'empty' ? { status: 'empty' } : state.status === 'error' ? { status: 'error', error: state.error } : { status: 'ready', value: state.value }}
      loadingLabel="Loading policies…"
      emptyTitle="No active policies"
      emptyDetail="Policy proposals cannot be activated until a policy owner creates the first governed policy."
      onRetry={onRetry}
    >
      {(policies) => (
        <Card title="Active policies" subtitle="Open proposal counts and approval gates are visible before simulation or commit.">
          <div className="selectable-list" role="list">
            {policies.map((policy) => (
              <button key={policy.policyId} type="button" className={selectedPolicyId === policy.policyId ? 'selectable-row selected' : 'selectable-row'} onClick={() => onSelect(policy.policyId)}>
                <span><strong>{policy.name}</strong><small>{policy.ownerRole} · {policy.approvalGateCount} approval gates · version {policy.activeVersion}</small></span>
                <StatusPill tone={policy.openProposalCount > 0 ? 'warning' : 'success'}>{`${policy.openProposalCount} open proposals`}</StatusPill>
              </button>
            ))}
          </div>
        </Card>
      )}
    </DataState>
  );
}

function ProposalPanel({ form, errors, policiesState, submitting, titleRef, changeRef, ackRef, onChange, onSubmit }: { form: ProposalForm; errors: ProposalErrors; policiesState: RemoteData<PolicySummary[]>; submitting: boolean; titleRef: React.RefObject<HTMLInputElement | null>; changeRef: React.RefObject<HTMLTextAreaElement | null>; ackRef: React.RefObject<HTMLInputElement | null>; onChange: (form: ProposalForm) => void; onSubmit: (event: React.FormEvent<HTMLFormElement>) => void }) {
  const policies = policiesState.status === 'ready' ? policiesState.value : [];
  return (
    <Card className="form-card" title="Policy proposal panel" subtitle="Proposal creation and simulation use fixture clients; activation is intentionally blocked without an authorized commit.">
      <form className="stacked-form" onSubmit={onSubmit} noValidate>
        <SelectField id="proposal-policy" label="Policy" value={form.policyId} error={errors.policyId} onChange={(event) => onChange({ ...form, policyId: event.target.value })}>
          {policies.map((policy) => <option key={policy.policyId} value={policy.policyId}>{policy.name}</option>)}
        </SelectField>
        <TextInput id="proposal-title" ref={titleRef} label="Proposal title" error={errors.title} value={form.title} onChange={(event) => onChange({ ...form, title: event.target.value })} />
        <TextArea id="proposal-change" ref={changeRef} label="Proposed change" helper="Describe the changed rule, threshold, prompt, or approval gate." error={errors.proposedChange} value={form.proposedChange} onChange={(event) => onChange({ ...form, proposedChange: event.target.value })} />
        <TextArea id="proposal-rationale" label="Rationale" error={errors.rationale} value={form.rationale} onChange={(event) => onChange({ ...form, rationale: event.target.value })} />
        <TextArea id="proposal-impact" label="Expected impact" error={errors.expectedImpact} value={form.expectedImpact} onChange={(event) => onChange({ ...form, expectedImpact: event.target.value })} />
        <TextInput id="simulation-scope" label="Simulation scope" value={form.simulationScope} onChange={(event) => onChange({ ...form, simulationScope: event.target.value })} />
        <label className="checkbox-row authority-warning">
          <input ref={ackRef} type="checkbox" checked={form.acknowledgement} aria-invalid={errors.acknowledgement ? 'true' : undefined} aria-describedby={errors.acknowledgement ? 'proposal-ack-error' : undefined} onChange={(event) => onChange({ ...form, acknowledgement: event.target.checked })} />
          <span>I understand this proposal may change agent authority, approval gates, or policy thresholds and cannot activate without a human-authorized commit.</span>
        </label>
        {errors.acknowledgement && <p id="proposal-ack-error" className="field-error" role="alert">{errors.acknowledgement}</p>}
        <Button type="submit" disabled={submitting || policies.length === 0}>{submitting ? 'Submitting and simulating…' : 'Create proposal and run simulation'}</Button>
      </form>
    </Card>
  );
}

function SimulationPanel({ simulation, commitReady, onConfirmCommit }: { simulation: SimulationState; commitReady: boolean; onConfirmCommit: () => void }) {
  const statusText = simulation.status === 'idle' ? 'No simulation has been queued.' : `Simulation ${simulation.status} for ${simulation.proposalId}.`;
  return (
    <Card className="simulation-panel" title="Simulation and commit state" subtitle="Fixture simulations demonstrate queued, running, and completed states before commit controls appear.">
      <div className="detail-heading">
        <div>
          <StatusPill tone={simulation.status === 'completed' ? 'success' : simulation.status === 'idle' ? 'neutral' : 'warning'}>{simulation.status}</StatusPill>
          <h3>{statusText}</h3>
          {simulation.status !== 'idle' && <p>Job {simulation.jobId}; correlation {simulation.correlationId}. Review generated audit links before activation.</p>}
        </div>
      </div>
      <div className="audit-link-row" aria-label="Governance audit links">
        <a href="#audit">Open policy invocation traces</a>
        <a href="#audit">Open approval-gate audit trail</a>
      </div>
      <div className="commit-warning">
        <strong>Authority-change warning:</strong> policy commits can expand or restrict agent permissions. The frontend can prepare the request, but backend authorization and policy-owner approval remain mandatory.
      </div>
      <Button tone="danger" disabled={!commitReady} onClick={onConfirmCommit}>Confirm authorized policy commit</Button>
    </Card>
  );
}

function validateProposal(form: ProposalForm): ProposalErrors {
  const errors: ProposalErrors = {};
  if (!form.policyId) errors.policyId = 'Select a policy before proposing a change.';
  if (form.title.trim().length < 6) errors.title = 'Name the policy proposal.';
  if (form.proposedChange.trim().length < 12) errors.proposedChange = 'Describe the proposed policy change.';
  if (form.rationale.trim().length < 8) errors.rationale = 'Explain why this change is needed.';
  if (form.expectedImpact.trim().length < 8) errors.expectedImpact = 'Describe the expected authority, risk, or outcome impact.';
  if (!form.acknowledgement) errors.acknowledgement = 'Acknowledge the authority-change warning before simulation.';
  return errors;
}
