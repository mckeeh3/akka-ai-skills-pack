import { Fragment, useId, useRef, useState } from 'react';
import type { FormEvent } from 'react';
import type { ChatToolPlanConfirmationRequest, ChatToolPlanExecutionResult, ChatToolPlanStep, ChatToolPlanStepResult, ChatToolPlanSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type ChatToolPlanSurfaceProps = {
  envelope: SurfaceEnvelope<ChatToolPlanSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: ChatToolPlanConfirmationRequest) => void | Promise<void>;
};

type FormState =
  | { status: 'idle' }
  | { status: 'validation'; message: string }
  | { status: 'submitting' }
  | { status: 'submitted'; message: string }
  | { status: 'error'; message: string };

const sensitiveKeyPattern = /secret|token|jwt|credential|password|api[_-]?key|providerPayload|rawPayload|hiddenCapability|invitationToken/i;

export function ChatToolPlanSurface({ envelope, onAction }: ChatToolPlanSurfaceProps) {
  const data = envelope.data;
  const isResult = envelope.surfaceType === 'chat_tool_plan_result' || data.surfaceContract === 'chat_tool_plan.result.v1' || Boolean(data.result);
  const isSystemMessage = envelope.surfaceType === 'chat_tool_plan_system_message' || data.surfaceContract === 'chat_tool_plan.system_message.v1' || Boolean(data.systemMessage && !data.proposal && !data.result);

  return (
    <SurfaceStateFrame envelope={envelope}>
      <article className="chat-tool-plan-surface" aria-label={envelope.title}>
        <BoundaryNotice data={data} />
        {isResult ? <ChatToolPlanResult data={data} /> : isSystemMessage ? <ChatToolPlanSystemMessage data={data} /> : <ChatToolPlanProposal envelope={envelope} onAction={onAction} />}
      </article>
    </SurfaceStateFrame>
  );
}

function ChatToolPlanProposal({ envelope, onAction }: ChatToolPlanSurfaceProps) {
  const data = envelope.data;
  const proposal = data.proposal;
  const snapshot = data.confirmationSnapshot;
  const confirmAction = envelope.actions.find((action) => action.actionId === 'action-confirm-chat-tool-plan');
  const [confirmationText, setConfirmationText] = useState('');
  const [formState, setFormState] = useState<FormState>({ status: 'idle' });
  const confirmationInputId = useId();
  const confirmationInputRef = useRef<HTMLInputElement>(null);

  if (!proposal || !snapshot) {
    return <ChatToolPlanSystemMessage data={{ ...data, systemMessage: data.systemMessage ?? { code: 'plan_unavailable', message: 'The backend did not provide a confirmable plan snapshot.', recoverySteps: ['Ask the workstream agent to re-propose the plan.'], noFakeSuccess: true, traceIds: data.traceRefs }, status: 'plan_unavailable' }} />;
  }

  const confirmableProposal = proposal;
  const confirmableSnapshot = snapshot;
  const expectedConfirmation = `CONFIRM ${confirmableSnapshot.planSnapshotId}`;
  const confirmationMatches = confirmationText === expectedConfirmation;
  const canSubmit = Boolean(confirmAction && confirmationMatches && formState.status !== 'submitting');

  async function submitConfirmation(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!confirmAction) {
      setFormState({ status: 'error', message: 'No backend confirmation action is available for this plan.' });
      return;
    }
    if (!confirmationMatches) {
      setFormState({ status: 'validation', message: `Type ${expectedConfirmation} exactly to confirm this immutable plan snapshot.` });
      confirmationInputRef.current?.focus();
      return;
    }
    setFormState({ status: 'submitting' });
    const request: ChatToolPlanConfirmationRequest = {
      selectedContextId: confirmableSnapshot.selectedContextId,
      planId: confirmableSnapshot.planId,
      planSnapshotId: confirmableSnapshot.planSnapshotId,
      confirmationText,
      stepHashes: confirmableSnapshot.stepHashes,
      idempotencyKey: `${confirmableSnapshot.idempotencyRoot}:confirm:${confirmableSnapshot.planSnapshotId}`,
      correlationId: `corr-chat-tool-plan-confirm-${Date.now().toString(36)}`
    };
    try {
      await onAction?.(confirmAction, envelope.surfaceId, request);
      setFormState({ status: 'submitted', message: 'Confirmation was submitted to the backend. The result surface will show committed, failed, skipped, and recovery states.' });
    } catch (error) {
      setFormState({ status: 'error', message: error instanceof Error ? error.message : 'The confirmation request could not be submitted.' });
    }
  }

  return (
    <>
      <section className="chat-tool-plan-summary" aria-label="Plan summary">
        <p className="surface-state-inline forbidden">No governed tools have executed. The browser cannot edit step inputs, hidden capabilities, tenant/customer scope, provider payloads, or policy gates; ask for a repaired or re-proposed plan instead.</p>
        <p>{confirmableProposal.summary}</p>
        <dl className="authority-summary-grid">
          <div><dt>Status</dt><dd>{formatStatus(confirmableProposal.status)}</dd></div>
          <div><dt>Plan id</dt><dd>{confirmableProposal.planId}</dd></div>
          <div><dt>Snapshot</dt><dd>{confirmableProposal.planSnapshotId}</dd></div>
          <div><dt>Selected AuthContext</dt><dd>{confirmableProposal.selectedContextId}</dd></div>
          <div><dt>Requested by</dt><dd>{confirmableProposal.requestedByAccountId}</dd></div>
          <div><dt>Expires</dt><dd>{formatDateTime(confirmableProposal.expiresAt)}</dd></div>
          <div><dt>Pre-confirmation side effect</dt><dd>{data.sideEffect}</dd></div>
          <div><dt>Execution enabled</dt><dd>{data.executionEnabled ? 'Yes' : 'No, not until exact backend confirmation'}</dd></div>
        </dl>
      </section>
      <section aria-label="Required capabilities and approvals" className="chat-tool-plan-governance">
        <h4>Capabilities, approval, and idempotency</h4>
        <dl>
          <dt>Required capabilities</dt><dd>{confirmableProposal.requiredCapabilities.length ? confirmableProposal.requiredCapabilities.join(' · ') : 'No capability list returned'}</dd>
          <dt>Approval summary</dt><dd>{confirmableProposal.approvalSummary}</dd>
          <dt>Idempotency root</dt><dd>{confirmableProposal.idempotencyRoot}</dd>
          <dt>Confirmation instructions</dt><dd>{confirmableSnapshot.confirmationInstructions}</dd>
        </dl>
      </section>
      <ChatToolPlanSteps steps={confirmableProposal.steps} stepHashes={confirmableSnapshot.stepHashes} />
      <form className="chat-tool-plan-confirmation-form" aria-label="Confirm immutable chat tool plan snapshot" onSubmit={submitConfirmation} noValidate>
        <div className="surface-detail-field">
          <label htmlFor={confirmationInputId}>Type the exact confirmation phrase</label>
          <input
            ref={confirmationInputRef}
            className="designed-control"
            id={confirmationInputId}
            name="confirmationText"
            autoComplete="off"
            value={confirmationText}
            onChange={(event) => {
              setConfirmationText(event.currentTarget.value);
              if (formState.status === 'validation') setFormState({ status: 'idle' });
            }}
            aria-describedby={`${confirmationInputId}-hint ${formState.status === 'validation' || formState.status === 'error' ? `${confirmationInputId}-error` : ''}`.trim()}
            aria-invalid={formState.status === 'validation' || undefined}
          />
          <p id={`${confirmationInputId}-hint`} className="form-status">Required phrase: <code>{expectedConfirmation}</code>. This confirms only plan id <code>{confirmableSnapshot.planId}</code> and snapshot <code>{confirmableSnapshot.planSnapshotId}</code>.</p>
          {(formState.status === 'validation' || formState.status === 'error') && <p id={`${confirmationInputId}-error`} className="form-error" role="alert">{formState.message}</p>}
          {formState.status === 'submitted' && <p className="form-status" role="status">{formState.message}</p>}
        </div>
        <button type="submit" className="ds-button primary" disabled={!canSubmit} aria-disabled={!canSubmit}>
          {formState.status === 'submitting' ? 'Submitting confirmation…' : confirmAction?.label ?? 'Confirm this plan'}
        </button>
        {!confirmationMatches && <p className="form-status">Confirmation remains disabled until the exact snapshot phrase is entered. There is no automatic or background execution.</p>}
      </form>
      <TraceReferences traceIds={confirmableSnapshot.traceIds.length ? confirmableSnapshot.traceIds : data.traceRefs} label="Plan trace references" />
    </>
  );
}

function ChatToolPlanSteps({ steps, stepHashes }: { steps: ChatToolPlanStep[]; stepHashes?: Record<string, string> }) {
  if (!steps.length) return <p>No executable steps are available for this plan.</p>;
  return (
    <section className="chat-tool-plan-steps" aria-label="Proposed governed tool steps">
      <h4>Proposed steps</h4>
      <ol>
        {steps.map((step) => (
          <li key={step.stepId} className="chat-tool-plan-step">
            <div className="chat-tool-plan-step-header"><strong>{step.sequence}. {step.label}</strong><span className="status-pill neutral">{step.transactionBoundary}</span></div>
            <dl className="chat-tool-plan-step-grid">
              <div><dt>Action</dt><dd>{step.actionId}</dd></div>
              <div><dt>Browser tool</dt><dd>{step.browserToolId}</dd></div>
              <div><dt>Governed tool</dt><dd>{step.governedToolId}</dd></div>
              <div><dt>Capability</dt><dd>{step.capabilityId}</dd></div>
              <div><dt>Input schema</dt><dd>{step.inputSchemaRef}</dd></div>
              <div><dt>Idempotency</dt><dd>{step.idempotencyKey}</dd></div>
              <div><dt>Confirmation</dt><dd>{step.requiresConfirmation ? 'Required' : 'Not reported'}</dd></div>
              <div><dt>Approval</dt><dd>{step.requiresApproval ? 'Separate approval required' : 'No separate approval reported'}</dd></div>
              <div><dt>Step hash</dt><dd>{stepHashes?.[step.stepId] ?? 'Not supplied'}</dd></div>
            </dl>
            <StepInputSummary input={step.input} />
            {step.dependsOnStepIds.length > 0 && <p className="form-status">Depends on: {step.dependsOnStepIds.join(' · ')}</p>}
            {Object.keys(step.outputBindings).length > 0 && <KeyValueList title="Output bindings" values={step.outputBindings} />}
            {step.traceRequirements.length > 0 && <p className="form-status">Trace requirements: {step.traceRequirements.join(' · ')}</p>}
          </li>
        ))}
      </ol>
    </section>
  );
}

function ChatToolPlanResult({ data }: { data: ChatToolPlanSurfaceData }) {
  const result = data.result;
  if (!result) return <ChatToolPlanSystemMessage data={{ ...data, systemMessage: data.systemMessage ?? { code: 'result_unavailable', message: 'The backend did not return an execution result.', recoverySteps: ['Open the plan trace or retry with the same confirmation idempotency key.'], noFakeSuccess: true, traceIds: data.traceRefs } }} />;
  return (
    <>
      <section className="chat-tool-plan-result-summary" aria-label="Execution result summary">
        <p role="status">Plan {result.planId} completed with status <strong>{formatStatus(result.status)}</strong>.</p>
        {data.systemMessage && <p className="surface-state-inline forbidden">{data.systemMessage.message}</p>}
        <dl className="authority-summary-grid">
          <div><dt>Plan snapshot</dt><dd>{result.planSnapshotId}</dd></div>
          <div><dt>Completed</dt><dd>{result.completedSteps.length}</dd></div>
          <div><dt>Failed</dt><dd>{result.failedSteps.length}</dd></div>
          <div><dt>Skipped</dt><dd>{result.skippedSteps.length}</dd></div>
          <div><dt>Correlation</dt><dd>{result.correlationId}</dd></div>
        </dl>
      </section>
      <StepResultSection title="Completed steps" steps={result.completedSteps} empty="No steps completed." />
      <StepResultSection title="Failed steps" steps={result.failedSteps} empty="No failed steps." />
      <StepResultSection title="Skipped steps" steps={result.skippedSteps} empty="No skipped steps." />
      <RecoverySection result={result} systemMessage={data.systemMessage ?? undefined} />
      {result.resultSurfaceIds.length > 0 && <section aria-label="Result surfaces"><h4>Result surfaces</h4><ul>{result.resultSurfaceIds.map((surfaceId) => <li key={surfaceId}>{surfaceId}</li>)}</ul></section>}
      <TraceReferences traceIds={result.traceIds.length ? result.traceIds : data.traceRefs} label="Execution trace references" />
    </>
  );
}

function StepResultSection({ title, steps, empty }: { title: string; steps: ChatToolPlanStepResult[]; empty: string }) {
  return (
    <section className="chat-tool-plan-step-results" aria-label={title}>
      <h4>{title}</h4>
      {steps.length === 0 ? <p>{empty}</p> : <ol>{steps.map((step) => <li key={`${title}-${step.stepId}`} className={step.status}><strong>{step.stepId}</strong>: {step.message}<dl><dt>Status</dt><dd>{formatStatus(step.status)}</dd><dt>Action</dt><dd>{step.actionId}</dd><dt>Governed tool</dt><dd>{step.governedToolId}</dd><dt>Capability</dt><dd>{step.capabilityId}</dd>{step.resultSurfaceId && <><dt>Result surface</dt><dd>{step.resultSurfaceId}</dd></>}{step.errorCode && <><dt>Error code</dt><dd>{step.errorCode}</dd></>}</dl><TraceReferences traceIds={step.traceIds} label={`${step.stepId} trace references`} /></li>)}</ol>}
    </section>
  );
}

function RecoverySection({ result, systemMessage }: { result: ChatToolPlanExecutionResult; systemMessage?: { recoverySteps: string[] } }) {
  const recoverySteps = result.recoverySteps.length ? result.recoverySteps : systemMessage?.recoverySteps ?? [];
  return (
    <section className="chat-tool-plan-recovery" aria-label="Recovery states">
      <h4>Recovery</h4>
      {recoverySteps.length > 0 ? <ol>{recoverySteps.map((step) => <li key={step}>{step}</li>)}</ol> : <p>No recovery steps were returned.</p>}
      <p className="form-status">Completed steps remain committed unless a backend-governed compensating action is separately exposed.</p>
    </section>
  );
}

function ChatToolPlanSystemMessage({ data }: { data: ChatToolPlanSurfaceData }) {
  const message = data.systemMessage;
  if (!message) return <p role="status">Chat tool plan state is unavailable.</p>;
  return (
    <section className="chat-tool-plan-system-message" role="alert" aria-label="Chat tool plan unavailable">
      <span className="status-pill danger">{formatStatus(message.code)}</span>
      <p>{message.message}</p>
      {message.noFakeSuccess && <p className="form-status">No fake success: no model-less planning success or tool execution is shown.</p>}
      <section aria-label="Recovery steps"><h4>Recovery steps</h4><ol>{message.recoverySteps.map((step) => <li key={step}>{step}</li>)}</ol></section>
      <TraceReferences traceIds={message.traceIds.length ? message.traceIds : data.traceRefs} label="Unavailable plan trace references" />
    </section>
  );
}

function BoundaryNotice({ data }: { data: ChatToolPlanSurfaceData }) {
  return (
    <div className="chat-tool-plan-boundary" role="note" aria-label="Chat tool plan authority boundary">
      <p>{data.noMutation ? 'Proposal only: no state changed before confirmation.' : 'Result: state may have changed only after explicit backend confirmation.'}</p>
      {data.noDirectMutation && <p>Browser UI cannot execute tools directly, edit plan steps, expand capabilities, reveal hidden scope, or expose provider secrets/payloads.</p>}
    </div>
  );
}

function StepInputSummary({ input }: { input: Record<string, unknown> }) {
  const entries = Object.entries(input ?? {});
  if (entries.length === 0) return <p className="form-status">No browser-visible input summary was returned.</p>;
  return <KeyValueList title="Browser-safe input summary" values={Object.fromEntries(entries.map(([key, value]) => [key, safeDisplayValue(key, value)]))} />;
}

function KeyValueList({ title, values }: { title: string; values: Record<string, unknown> }) {
  const entries = Object.entries(values);
  if (entries.length === 0) return null;
  return (
    <section className="chat-tool-plan-key-values" aria-label={title}>
      <h5>{title}</h5>
      <dl>{entries.map(([key, value]) => <Fragment key={key}><dt>{humanizeKey(key)}</dt><dd>{renderDisplayValue(value)}</dd></Fragment>)}</dl>
    </section>
  );
}

function TraceReferences({ traceIds, label }: { traceIds: string[]; label: string }) {
  if (!traceIds.length) return null;
  return (
    <details className="chat-tool-plan-traces trace-link-list">
      <summary>{label}</summary>
      <ul>{traceIds.map((traceId, index) => <li key={`${traceId}-${index}`}><a href={`/ui?traceId=${encodeURIComponent(traceId)}`}>Trace reference {index + 1}</a></li>)}</ul>
    </details>
  );
}

function safeDisplayValue(key: string, value: unknown): unknown {
  if (sensitiveKeyPattern.test(key)) return '[redacted by browser surface]';
  if (Array.isArray(value)) return value.map((item) => safeDisplayValue(key, item));
  if (value && typeof value === 'object') {
    return Object.fromEntries(Object.entries(value as Record<string, unknown>).map(([nestedKey, nestedValue]) => [nestedKey, safeDisplayValue(nestedKey, nestedValue)]));
  }
  return value;
}

function renderDisplayValue(value: unknown): string {
  if (value === null || value === undefined || value === '') return 'Not provided';
  if (Array.isArray(value)) return value.map(renderDisplayValue).join(' · ');
  if (typeof value === 'object') return Object.entries(value as Record<string, unknown>).map(([key, nested]) => `${humanizeKey(key)}: ${renderDisplayValue(nested)}`).join('; ');
  return String(value);
}

function humanizeKey(value: string): string {
  return value.replace(/([a-z])([A-Z])/g, '$1 $2').replace(/[_-]/g, ' ');
}

function formatStatus(value: string | undefined): string {
  return (value ?? 'unknown').replace(/[-_]/g, ' ');
}

function formatDateTime(value: string | undefined): string {
  if (!value) return 'Not provided';
  const date = new Date(value);
  return Number.isNaN(date.valueOf()) ? value : date.toLocaleString();
}
