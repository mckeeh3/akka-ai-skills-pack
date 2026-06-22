import type { SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type AgentAdminLifecycleData = Record<string, unknown> & {
  surfaceContract?: string;
  recordId?: string;
  recordLabel?: string;
  lifecycleAction?: 'activate' | 'deactivate' | 'rollback' | string;
  currentStatus?: string;
  proposedStatus?: string;
  impactSummary?: string;
  approvalState?: string;
  policyBasis?: string;
  idempotencyKeyHint?: string;
  disabledReason?: string;
  evidenceRefs?: string[];
  traceRefs?: string[];
  actionContext?: Record<string, string>;
  approvalSummary?: { outstandingBlockers?: string[]; providerRuntimeReadiness?: string; noFakeSuccess?: boolean };
  confirmationState?: { requiredAcknowledgementText?: string; providerFailClosedState?: string; nextSafeRecoverySurfaceId?: string };
  safeRedactionSummary?: Record<string, string>;
};

type Props = {
  envelope: SurfaceEnvelope<AgentAdminLifecycleData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

const lifecycleSurfaceIds = new Set([
  'surface-agent-activation-confirmation',
  'surface-agent-deactivation-confirmation',
  'surface-agent-rollback-confirmation',
  'surface-agent-definition-activation-confirmation',
  'surface-agent-definition-deactivation-confirmation'
]);

const lifecycleContracts = new Set([
  'agent_admin.activation_confirmation.v1',
  'agent_admin.deactivation_confirmation.v1',
  'agent_admin.rollback_confirmation.v1',
  'agent_admin.definition_activation_confirmation.v1',
  'agent_admin.definition_deactivation_confirmation.v1'
]);

export function AgentAdminTaskSurface({ envelope, onAction }: Props) {
  const data = envelope.data;
  const action = primaryLifecycleAction(envelope);
  const renderedActions = envelope.actions.map((candidate) => candidate.actionId === action?.actionId && data.disabledReason ? withDisabledReason(candidate, String(data.disabledReason)) : candidate);
  const actionInput = {
    ...(data.actionContext ?? {}),
    agentDefinitionId: String(data.recordId ?? ''),
    lifecycleAction: String(data.lifecycleAction ?? ''),
    idempotencyKey: `${envelope.surfaceId}:${data.recordId ?? envelope.correlationId}`,
    correlationId: envelope.correlationId
  };
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="user-admin-task-surface agent-admin-lifecycle-surface" aria-labelledby={`${envelope.surfaceId}-heading`}>
        <div className="user-admin-task-header">
          <div>
            <p className="eyebrow">Agent Admin · lifecycle confirmation</p>
            <h3 id={`${envelope.surfaceId}-heading`}>{envelope.title}</h3>
            <p>{String(data.impactSummary ?? 'Confirm the managed-agent lifecycle action after backend policy, approval, idempotency, and trace checks.')}</p>
          </div>
        </div>
        <dl className="authority-summary-grid">
          <div><dt>Target</dt><dd>{String(data.recordLabel ?? data.recordId ?? 'Managed agent')}</dd></div>
          <div><dt>Change</dt><dd>{String(data.currentStatus ?? 'current')} → {String(data.proposedStatus ?? data.lifecycleAction ?? 'proposed')}</dd></div>
          <div><dt>Approval</dt><dd>{String(data.approvalState ?? 'backend policy required')}</dd></div>
          <div><dt>Idempotency</dt><dd>{String(data.idempotencyKeyHint ?? 'client-generated key required')}</dd></div>
        </dl>
        {data.policyBasis && <p className="capability-basis">Policy basis: {String(data.policyBasis)}</p>}
        {data.disabledReason && <p className="surface-state-inline forbidden" role="status">{String(data.disabledReason)}</p>}
        {data.approvalSummary?.outstandingBlockers && data.approvalSummary.outstandingBlockers.length > 0 && (
          <section className="evidence-ref-list" aria-label="Activation blockers"><h4>Activation blockers</h4><ul>{data.approvalSummary.outstandingBlockers.map((blocker) => <li key={blocker}>{blocker}</li>)}</ul></section>
        )}
        {data.confirmationState?.requiredAcknowledgementText && <p className="capability-basis">Acknowledgement required: {data.confirmationState.requiredAcknowledgementText}</p>}
        {data.confirmationState?.providerFailClosedState && <p className="capability-basis">Provider/runtime state: {data.confirmationState.providerFailClosedState}</p>}
        {data.evidenceRefs && data.evidenceRefs.length > 0 && <section className="evidence-ref-list" aria-label="Lifecycle evidence"><h4>Evidence</h4><ul>{data.evidenceRefs.map((ref) => <li key={ref}>{ref}</li>)}</ul></section>}
        {data.safeRedactionSummary && <p className="capability-basis">Browser-safe redaction: raw prompts, skills, references, provider credentials, bearer tokens, hidden scopes, and raw trace evidence are omitted or role-gated.</p>}
        {data.traceRefs && data.traceRefs.length > 0 && <section className="trace-link-list" aria-label="Lifecycle trace links">{data.traceRefs.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-agent-admin-trace&traceId=${encodeURIComponent(traceId)}`}>{traceId}</a>)}</section>}
        <SurfaceActionBar actions={renderedActions} surfaceId={envelope.surfaceId} actionInput={actionInput} onAction={onAction} />
        {renderedActions.length === 0 && <p className="form-error">No backend-authorized lifecycle action is available in this selected context.</p>}
      </section>
    </SurfaceStateFrame>
  );
}

export function isAgentAdminTaskSurface(envelope: SurfaceEnvelope<unknown>) {
  const data = envelope.data as { surfaceContract?: string } | undefined;
  return lifecycleSurfaceIds.has(envelope.surfaceId) || lifecycleContracts.has(data?.surfaceContract ?? '');
}

function withDisabledReason(action: SurfaceAction, message: string): SurfaceAction {
  return { ...action, disabled: action.disabled ?? { reasonCode: 'BACKEND_PREREQUISITE_REQUIRED', message } };
}

function primaryLifecycleAction(envelope: SurfaceEnvelope<AgentAdminLifecycleData>): SurfaceAction | undefined {
  const lifecycleAction = String(envelope.data.lifecycleAction ?? '');
  if (lifecycleAction === 'activate') return envelope.actions.find((action) => action.actionId === 'action-agent-activation-confirm' || action.actionId === 'action-activate-behavior-change' || action.actionId === 'action-activate-agent-definition');
  if (lifecycleAction === 'deactivate') return envelope.actions.find((action) => action.actionId === 'action-agent-deactivation-confirm' || action.actionId === 'action-deactivate-agent-definition' || action.actionId === 'action-cancel-behavior-change');
  if (lifecycleAction === 'rollback') return envelope.actions.find((action) => action.actionId === 'action-agent-rollback-confirm' || action.actionId === 'action-rollback-behavior-change');
  return envelope.actions[0];
}
