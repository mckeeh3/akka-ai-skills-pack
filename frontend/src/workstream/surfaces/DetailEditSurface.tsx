import { useEffect, useMemo, useState } from 'react';
import type { DetailEditSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type DetailEditSurfaceProps = {
  envelope: SurfaceEnvelope<DetailEditSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
  onFieldValueChange?: (fieldId: string, value: string, surfaceId: string) => void;
};

function stringifySafe(value: unknown): string {
  if (Array.isArray(value)) return value.join(', ');
  if (value && typeof value === 'object') return JSON.stringify(value);
  return String(value ?? '');
}

export function DetailEditSurface({ envelope, onAction, onFieldValueChange }: DetailEditSurfaceProps) {
  const permissionState = envelope.data.permissionState;
  const fields = envelope.data.fields ?? [];
  const recordId = envelope.data.recordId ?? envelope.data.traceId ?? envelope.data.category ?? envelope.surfaceId;
  const recordLabel = envelope.data.recordLabel ?? envelope.data.eventKind ?? envelope.data.category ?? recordId;
  const isAuditEvidence = fields.length === 0 && (envelope.surfaceId.includes('audit-trace') || envelope.data.traceId || envelope.data.safeReason || envelope.data.redactedEvidence);

  const initialFieldValues = useMemo(() => Object.fromEntries(fields.map((field) => [field.fieldId, field.value])), [fields]);
  const [fieldValues, setFieldValues] = useState<Record<string, string>>(initialFieldValues);
  const editableActionInput = useMemo(
    () => Object.fromEntries(fields.filter((field) => field.editable).map((field) => [field.fieldId, fieldValues[field.fieldId] ?? field.value])),
    [fields, fieldValues]
  );

  useEffect(() => {
    setFieldValues(initialFieldValues);
  }, [initialFieldValues]);

  function updateFieldValue(fieldId: string, value: string) {
    setFieldValues((current) => ({ ...current, [fieldId]: value }));
    if (envelope.surfaceId === 'surface-my-settings' && fieldId === 'preferredThemeId' && isNamedThemeId(value)) {
      document.documentElement.dataset.theme = value;
    }
    onFieldValueChange?.(fieldId, value, envelope.surfaceId);
  }

  const isMyAccountSurface = envelope.surfaceId === 'surface-my-profile' || envelope.surfaceId === 'surface-my-settings' || envelope.surfaceId === 'surface-my-context';

  if (isAgentAdminSurface(envelope)) {
    return <AgentAdminInspectionDetail envelope={envelope} onAction={onAction} />;
  }

  if (isUserAdminSurface(envelope) && (envelope.data.recordKind === 'account' || envelope.data.recordKind === 'invitation')) {
    return <UserAdminCleanDetail envelope={envelope} fieldValues={fieldValues} onAction={onAction} />;
  }

  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className={isMyAccountSurface ? 'my-account-detail-hero detail-heading' : 'detail-heading'} aria-label="Detail record context">
        <div>
          <p className="eyebrow">{myAccountEyebrow(envelope.surfaceId, envelope.data.recordKind ?? (isAuditEvidence ? 'audit evidence' : 'detail'))}</p>
          <h3>{recordLabel}</h3>
          {envelope.data.summary && <p>{envelope.data.summary}</p>}
        </div>
        {envelope.data.version !== undefined && <span className="version-chip sr-only">Version {envelope.data.version}</span>}
      </section>
      {isMyAccountSurface && <MyAccountDetailOverview envelope={envelope} fieldValues={fieldValues} onAction={onAction} />}
      {permissionState && (
        <p className={permissionState.canEdit ? 'form-status' : 'form-status conflict'}>
          Edit authority: {permissionState.authoritativeCapabilityId}. {permissionState.reason}
        </p>
      )}
      {isUserAdminSurface(envelope) && <UserAdminDetailOverview envelope={envelope} />}
      {envelope.data.accessManagement && (
        <section className="access-management-evidence" aria-label="User Admin access-management evidence">
          <p className="capability-basis">{envelope.data.accessManagement.advisoryNotice}</p>
          {envelope.data.accessManagement.memberStatus && (
            <article className="access-management-card">
              <h4>Member status authority</h4>
              <dl>
                <dt>Account status</dt><dd>{envelope.data.accessManagement.memberStatus.accountStatus}</dd>
                <dt>Membership status</dt><dd>{envelope.data.accessManagement.memberStatus.membershipStatus}</dd>
                <dt>Action ids</dt><dd>{envelope.data.accessManagement.memberStatus.statusActionIds.join(', ')}</dd>
                {envelope.data.accessManagement.memberStatus.noOpMessage && <><dt>No-op/idempotency</dt><dd>{envelope.data.accessManagement.memberStatus.noOpMessage}</dd></>}
                {envelope.data.accessManagement.memberStatus.idempotencyKeySource && <><dt>Idempotency key source</dt><dd>{envelope.data.accessManagement.memberStatus.idempotencyKeySource}</dd></>}
              </dl>
              <ul>{envelope.data.accessManagement.memberStatus.denialHints.map((hint) => <li key={hint}>{hint}</li>)}</ul>
              <section className="trace-link-list" aria-label="Member status trace links">{envelope.data.accessManagement.memberStatus.traceLinks.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-trace-timeline#${encodeURIComponent(traceId)}`}>{traceId}</a>)}</section>
            </article>
          )}
          {envelope.data.accessManagement.supportAccess && (
            <article className="access-management-card support-access-preview">
              <h4>Support access boundary</h4>
              <dl>
                <dt>Status</dt><dd>{envelope.data.accessManagement.supportAccess.status ?? (envelope.data.accessManagement.supportAccess.supportAccess ? 'active' : 'not active')}</dd>
                {envelope.data.accessManagement.supportAccess.expiresAt && <><dt>Expires</dt><dd>{envelope.data.accessManagement.supportAccess.expiresAt}</dd></>}
                {envelope.data.accessManagement.supportAccess.actionIds && <><dt>Action ids</dt><dd>{envelope.data.accessManagement.supportAccess.actionIds.join(', ')}</dd></>}
              </dl>
              {envelope.data.accessManagement.supportAccess.denialHints && <ul>{envelope.data.accessManagement.supportAccess.denialHints.map((hint) => <li key={hint}>{hint}</li>)}</ul>}
              {envelope.data.accessManagement.supportAccess.traceLinks && <section className="trace-link-list" aria-label="Support access trace links">{envelope.data.accessManagement.supportAccess.traceLinks.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-trace-timeline#${encodeURIComponent(traceId)}`}>{traceId}</a>)}</section>}
            </article>
          )}
          {envelope.data.accessManagement.roleChangePreview && (
            <article className="access-management-card role-change-preview" data-surface-contract={envelope.data.accessManagement.roleChangePreview.surfaceContract}>
              <h4>Role/capability preview</h4>
              <dl>
                <dt>Current roles</dt><dd>{envelope.data.accessManagement.roleChangePreview.currentRoles.join(', ')}</dd>
                <dt>Proposed roles</dt><dd>{envelope.data.accessManagement.roleChangePreview.proposedRoles.join(', ')}</dd>
                <dt>Capability delta</dt><dd>Added {envelope.data.accessManagement.roleChangePreview.capabilityDelta.added.join(', ') || 'none'}; removed {envelope.data.accessManagement.roleChangePreview.capabilityDelta.removed.join(', ') || 'none'}</dd>
                <dt>Affected workstreams</dt><dd>{envelope.data.accessManagement.roleChangePreview.affectedWorkstreams.join(', ')}</dd>
                <dt>Policy hints</dt><dd>{envelope.data.accessManagement.roleChangePreview.policyHints.join('; ')}</dd>
                <dt>Last-admin impact</dt><dd>{envelope.data.accessManagement.roleChangePreview.lastAdminImpact}</dd>
                <dt>Approval required</dt><dd>{envelope.data.accessManagement.roleChangePreview.approvalRequired ? 'yes' : 'no'}</dd>
                <dt>No-op</dt><dd>{envelope.data.accessManagement.roleChangePreview.noOp ? 'yes' : 'no'}</dd>
              </dl>
              <section className="trace-link-list" aria-label="Role-change preview trace links">{envelope.data.accessManagement.roleChangePreview.traceLinks.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-trace-timeline#${encodeURIComponent(traceId)}`}>{traceId}</a>)}</section>
            </article>
          )}
        </section>
      )}
      {fields.length > 0 ? (
        <section className="detail-edit-form-section" aria-labelledby={`${envelope.surfaceId}-form-heading`}>
          <div className="surface-section-heading">
            <div>
              <p className="eyebrow">Governed form</p>
              <h4 id={`${envelope.surfaceId}-form-heading`}>Review editable fields</h4>
            </div>
            <p>Field edits are held locally until you use the governed action below; backend validation, idempotency, authorization, and audit remain authoritative.</p>
          </div>
          <form className="surface-detail-edit-form" data-my-account-detail={isMyAccountSurface ? 'true' : undefined} aria-label={`${envelope.title} edit form`} onSubmit={(event) => event.preventDefault()}>
            <input type="hidden" name="recordId" value={recordId} readOnly />
            {fields.map((field) => {
              const inputId = `${envelope.surfaceId}-${field.fieldId}`;
              const helpId = `${inputId}-help`;
              const reasonId = field.disabledReason ? `${inputId}-reason` : undefined;
              const describedBy = [helpId, reasonId].filter(Boolean).join(' ');
              return (
                <div key={field.fieldId} className="surface-detail-field">
                  <label htmlFor={inputId}>{field.label}</label>
                  {field.inputType === 'textarea' ? (
                    <textarea className="designed-control surface-detail-control" id={inputId} name={field.fieldId} value={fieldValues[field.fieldId] ?? field.value} onChange={(event) => updateFieldValue(field.fieldId, event.currentTarget.value)} readOnly={!field.editable} aria-readonly={!field.editable} aria-describedby={describedBy} />
                  ) : field.inputType === 'select' && field.options ? (
                    <select className="designed-control surface-detail-control" id={inputId} name={field.fieldId} value={fieldValues[field.fieldId] ?? field.options.find((option) => option.label === field.value)?.value ?? field.value} onChange={(event) => updateFieldValue(field.fieldId, event.currentTarget.value)} disabled={!field.editable} aria-describedby={describedBy}>
                      {field.options.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
                    </select>
                  ) : (
                    <input className="designed-control surface-detail-control" id={inputId} name={field.fieldId} type={field.inputType ?? 'text'} value={fieldValues[field.fieldId] ?? field.value} onChange={(event) => updateFieldValue(field.fieldId, event.currentTarget.value)} readOnly={!field.editable} aria-readonly={!field.editable} aria-describedby={describedBy} />
                  )}
                  <p id={helpId} className="field-helper">{field.editable ? 'Editable in this selected context; save with the governed action below.' : 'Read-only in this selected context.'}</p>
                  {!field.editable && field.disabledReason && <p id={reasonId} className="form-error denied-reason">{field.disabledReason}</p>}
                </div>
              );
            })}
          </form>
        </section>
      ) : (
        <article className="audit-evidence-panel" aria-label="Denial/provider/tool evidence">
          {envelope.data.safeReason && <p className="safe-reason">{envelope.data.safeReason}</p>}
          {envelope.data.redactedEvidence && <p>{envelope.data.redactedEvidence}</p>}
          {envelope.data.decision === 'not_found_or_redacted' && <p className="surface-state-inline forbidden" role="status">not_found_or_redacted: hidden or cross-scope evidence is not enumerated.</p>}
          <dl>
            {envelope.data.traceId && <><dt>Trace id</dt><dd>{envelope.data.traceId}</dd></>}
            {envelope.data.eventKind && <><dt>Event kind</dt><dd>{envelope.data.eventKind}</dd></>}
            {envelope.data.timestamp && <><dt>Timestamp</dt><dd>{envelope.data.timestamp}</dd></>}
            {envelope.data.actor && <><dt>Actor</dt><dd>{envelope.data.actor}</dd></>}
            {envelope.data.source && <><dt>Source</dt><dd>{envelope.data.source}</dd></>}
            {envelope.data.authorizationBasis && <><dt>Authorization basis</dt><dd>{envelope.data.authorizationBasis}</dd></>}
            {envelope.data.decision && <><dt>Decision</dt><dd>{envelope.data.decision}</dd></>}
            {envelope.data.redactionMetadata && <><dt>Redaction metadata</dt><dd>{stringifySafe(envelope.data.redactionMetadata)}</dd></>}
          </dl>
          {envelope.data.userActionableNextSteps && <section><h4>Safe next steps</h4><ul>{envelope.data.userActionableNextSteps.map((step) => <li key={step}>{step}</li>)}</ul></section>}
          {envelope.data.policyRefs && <p className="capability-basis">Policy/capability refs: {envelope.data.policyRefs.join(', ')}</p>}
          {envelope.data.redactedDetails && <section className="redaction-note"><h4>Redacted details</h4><ul>{Object.entries(envelope.data.redactedDetails).map(([key, value]) => <li key={key}><strong>{key}</strong>: {value}</li>)}</ul></section>}
          {envelope.data.relatedEvents && envelope.data.relatedEvents.length > 0 && (
            <section className="surface-card-list" aria-label="Related provider tool model worker events">
              <h4>Related failure evidence</h4>
              {envelope.data.relatedEvents.map((event) => (
                <article key={event.traceId} className={`surface-row-card ${event.status ?? 'event'}`}>
                  <p><span>traceId</span><strong><a href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(event.traceId)}`}>{event.traceId}</a></strong></p>
                  {event.eventKind && <p><span>eventKind</span><strong>{event.eventKind}</strong></p>}
                  {event.status && <p><span>status</span><strong>{event.status}</strong></p>}
                  {event.correlationId && <p><span>correlation</span><strong><a href={`/ui?surfaceId=surface-audit-trace-timeline&correlationId=${encodeURIComponent(event.correlationId)}`}>{event.correlationId}</a></strong></p>}
                  {event.summary && <p><span>summary</span><strong>{event.summary}</strong></p>}
                </article>
              ))}
            </section>
          )}
          {envelope.data.traceLinks && <section className="trace-link-list" aria-label="Audit evidence trace links">{envelope.data.traceLinks.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-trace-timeline#${encodeURIComponent(traceId)}`}>{traceId}</a>)}</section>}
        </article>
      )}
      {envelope.data.audit && (
        <section className="trace-link-list" aria-label="Detail audit trace affordances">
          <strong>Audit affordance: {envelope.data.audit.lastEventType}</strong>
          <span>Last actor: {envelope.data.audit.lastActor}</span>
          {envelope.data.audit.traceIds.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-timeline#${traceId}`}>{traceId}</a>)}
        </section>
      )}
      {isUserAdminSurface(envelope) && <UserAdminBranchReturn envelope={envelope} onAction={onAction} />}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} actionInput={editableActionInput} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

function AgentAdminInspectionDetail({ envelope, onAction }: { envelope: SurfaceEnvelope<DetailEditSurfaceData>; onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void }) {
  const fields = envelope.data.fields ?? [];
  const taskEntryActions = envelope.actions;
  const provider = envelope.data.providerReadiness as { status?: string; safeReason?: string; secretVisibility?: string } | undefined;
  const relatedArtifacts = envelope.data.relatedArtifacts as Array<Record<string, unknown>> | undefined;
  const actionContext = { ...(envelope.data.actionContext ?? {}), recordId: envelope.data.recordId ?? '', correlationId: envelope.correlationId };
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="user-admin-detail-clean agent-admin-detail-clean" aria-label="Agent Admin readiness and behavior inspection">
        <div className="user-admin-detail-clean-header">
          <div>
            <p className="eyebrow">Managed agent inspection</p>
            <h3>{envelope.data.recordLabel ?? envelope.title}</h3>
            {envelope.data.summary && <p>{envelope.data.summary}</p>}
          </div>
        </div>

        <section className="user-admin-detail-overview" aria-label="Agent Admin default readiness summary">
          <article className="authority-context-panel">
            <h4>Readiness and authority boundary</h4>
            <p>{envelope.data.permissionState?.reason ?? 'This inspection is read-only. Behavior and lifecycle changes open dedicated proposal, decision, confirmation, workflow, or system-message surfaces.'}</p>
            <dl>
              <div><dt>Agent state</dt><dd>{fieldValue(fields, 'status') ?? 'backend selected'}</dd></div>
              <div><dt>Authority tier</dt><dd>{fieldValue(fields, 'authorityLevel') ?? 'approval required'}</dd></div>
              <div><dt>Provider readiness</dt><dd>{provider?.status ?? 'not reported'}{provider?.secretVisibility ? ` · secrets ${provider.secretVisibility}` : ''}</dd></div>
              <div><dt>Mutation model</dt><dd>No direct mutation from this detail surface</dd></div>
            </dl>
          </article>
        </section>

        {relatedArtifacts && relatedArtifacts.length > 0 && (
          <section className="user-admin-list-panel" aria-label="Behavior artifacts and governance objects">
            <div className="surface-section-heading compact"><div><p className="eyebrow">Behavior artifacts</p><h4>Prompt, manifest, model, and tool-boundary readiness</h4></div><p>Artifact ids and capability details are diagnostic; use task entry points for changes.</p></div>
            <div className="user-admin-clean-list" role="list">
              {relatedArtifacts.map((artifact) => <article key={String(artifact.artifactId)} role="listitem" className="user-admin-clean-row agent-admin-artifact-card"><span className="user-admin-person"><strong>{agentAdminArtifactTitle(artifact)}</strong><small>{agentAdminArtifactSummary(artifact)}</small></span><span className={`status-pill ${statusTone(String(artifact.status ?? 'ready'))}`}>{humanize(String(artifact.status ?? 'ready'))}</span></article>)}
            </div>
          </section>
        )}

        <section className="user-admin-context-actions" aria-label="Agent Admin dedicated task surfaces">
          <div className="surface-section-heading compact">
            <div><p className="eyebrow">Dedicated task surfaces</p><h4>Govern behavior through proposals, decisions, tests, and traces</h4></div>
            <p>Activation, deactivation, rollback, prompt/model/tool/manifest changes, and seed imports are not inline edits. Backend authorization returns the next safe surface.</p>
          </div>
          <SurfaceActionBar actions={taskEntryActions} surfaceId={envelope.surfaceId} actionInput={actionContext} onAction={onAction} />
        </section>

        <details className="dashboard-evidence-drawer">
          <summary>Role-gated diagnostics</summary>
          <div className="user-admin-readable-fields">
            {fields.map((field) => <p key={field.fieldId}><span>{field.label}</span><strong>{field.value}</strong></p>)}
          </div>
          <p>Surface contract: {envelope.data.surfaceContract ?? 'agent_admin.definition.v1'}</p>
          <p>Redaction: {renderSurfaceValue(envelope.data.redaction ?? envelope.data.redactionMetadata) ?? envelope.redaction.profile}</p>
          {envelope.data.audit && <section className="trace-link-list" aria-label="Agent Admin diagnostic trace links">{envelope.data.audit.traceIds.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-agent-admin-trace&traceId=${encodeURIComponent(traceId)}`}>{traceId}</a>)}</section>}
        </details>
      </section>
    </SurfaceStateFrame>
  );
}

function UserAdminCleanDetail({ envelope, onAction }: { envelope: SurfaceEnvelope<DetailEditSurfaceData>; fieldValues: Record<string, string>; onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void }) {
  const isInvitation = envelope.data.recordKind === 'invitation';
  const backAction = userAdminShowUsersAction(envelope.actions);
  const taskEntryActions = envelope.actions.filter((action) => !isUserAdminShowUsersAction(action));
  const fields = envelope.data.fields ?? [];
  const email = fields.find((field) => field.fieldId === 'email')?.value;
  const actionContext = { ...(envelope.data.actionContext ?? {}), ...Object.fromEntries(fields.map((field) => [field.fieldId, field.value])) };

  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="user-admin-detail-clean" aria-label={isInvitation ? 'Invitation detail' : 'User detail'}>
        <div className="user-admin-detail-clean-header">
          <div>
            <p className="eyebrow">{isInvitation ? 'Invitation inspection' : 'User inspection'}</p>
            <h3>{envelope.data.recordLabel ?? email ?? envelope.title}</h3>
            {email && <p>{email}</p>}
          </div>
          {backAction && <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(backAction, envelope.surfaceId, userAdminBranchReturnInput(envelope))}>{userAdminBranchReturnLabel(envelope, backAction)}</button>}
        </div>

        <section className="detail-edit-form-section" aria-labelledby={`${envelope.surfaceId}-inspection-heading`}>
          <div className="surface-section-heading compact">
            <div><p className="eyebrow">Read-only inspection</p><h4 id={`${envelope.surfaceId}-inspection-heading`}>{isInvitation ? 'Invitation information' : 'User information'}</h4></div>
            <p>Detail surfaces do not mutate access inline. Use task entry points to open dedicated forms, confirmations, decisions, workflows, or system messages returned by the backend.</p>
          </div>
          <div className="user-admin-readable-fields">
            {fields.map((field) => <p key={field.fieldId}><span>{field.label}</span><strong>{field.value}</strong></p>)}
          </div>
        </section>

        {isInvitation && <InvitationDeliveryStatusPanel envelope={envelope} />}

        {taskEntryActions.length > 0 && (
          <section className="user-admin-context-actions" aria-label={isInvitation ? 'Invitation task entry points' : 'User task entry points'}>
            <div className="surface-section-heading compact">
              <div><p className="eyebrow">Dedicated task surfaces</p><h4>{isInvitation ? 'Manage invitation through tasks' : 'Manage access through tasks'}</h4></div>
              <p>Each action is backend-authored and reauthorized before returning a task, decision, result, or safe system message surface.</p>
            </div>
            <SurfaceActionBar actions={taskEntryActions} surfaceId={envelope.surfaceId} actionInput={actionContext} onAction={onAction} />
          </section>
        )}

        {envelope.data.audit && (
          <details className="dashboard-evidence-drawer">
            <summary>Audit details</summary>
            <section className="trace-link-list" aria-label="Audit trace links">
              <span>{envelope.data.audit.lastEventType}</span>
              {envelope.data.audit.traceIds.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-timeline#${traceId}`}>{traceId}</a>)}
            </section>
          </details>
        )}
      </section>
    </SurfaceStateFrame>
  );
}

function InvitationDeliveryStatusPanel({ envelope }: { envelope: SurfaceEnvelope<DetailEditSurfaceData> }) {
  const delivery = envelope.data.deliveryState;
  if (!delivery) return null;
  const recoverySteps = envelope.data.recoverySteps ?? [];
  return (
    <section className="access-management-card invitation-delivery-state" aria-label="Invitation delivery status">
      <div className="surface-section-heading compact">
        <div><p className="eyebrow">Provider-backed delivery</p><h4>Delivery status and recovery</h4></div>
        <p>Delivery state is backend-authored and redacted; raw tokens, Resend payloads, email bodies, provider message ids, and secrets are not shown.</p>
      </div>
      <dl>
        <div><dt>Status</dt><dd>{renderSurfaceValue(delivery.currentStatus) ?? 'not reported'}</dd></div>
        <div><dt>Attempts</dt><dd>{renderSurfaceValue(delivery.attempts) ?? '0'}</dd></div>
        <div><dt>Retry eligible</dt><dd>{delivery.retryEligible ? 'yes' : 'no'}</dd></div>
        <div><dt>Provider readiness</dt><dd>{renderSurfaceValue(delivery.providerReadiness) ?? 'backend-derived'}</dd></div>
        {Boolean(delivery.lastSafeError) && <div><dt>Safe error</dt><dd>{renderSurfaceValue(delivery.lastSafeError)}</dd></div>}
      </dl>
      {envelope.data.noFakeSuccess && <p className="surface-state-inline forbidden">Provider/outbox delivery failed closed. This surface is a typed system-message-ready recovery state, not a fake success.</p>}
      {recoverySteps.length > 0 && <ol>{recoverySteps.map((step) => <li key={step}>{step}</li>)}</ol>}
      <p className="capability-basis">{renderSurfaceValue(delivery.providerBoundary) ?? 'Provider boundary remains redacted.'}</p>
    </section>
  );
}

function UserAdminBranchReturn({ envelope, onAction }: { envelope: SurfaceEnvelope<DetailEditSurfaceData>; onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void }) {
  const backAction = userAdminShowUsersAction(envelope.actions);
  if (!backAction) return null;
  const branch = envelope.data.branchNavigation;
  return (
    <nav className="user-admin-branch-return" aria-label="User Admin branch navigation">
      <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(backAction, envelope.surfaceId, userAdminBranchReturnInput(envelope))}>{userAdminBranchReturnLabel(envelope, backAction)}</button>
      <p className="capability-basis">{branch?.capabilityId ?? envelope.data.branchReturnActionId ?? backAction.capabilityId} · safe filters: {branch?.safeFilterPreservation ?? envelope.data.safeFilterPreservation ?? 'backend-authored-only'}</p>
    </nav>
  );
}

function userAdminShowUsersAction(actions: SurfaceAction[]): SurfaceAction | undefined {
  return actions.find((action) => action.actionId === 'action-user-admin-show-users') ?? actions.find((action) => action.actionId === 'action-display-user-list');
}

function isUserAdminShowUsersAction(action: SurfaceAction): boolean {
  return action.actionId === 'action-user-admin-show-users' || action.actionId === 'action-display-user-list';
}

function userAdminBranchReturnLabel(envelope: SurfaceEnvelope<DetailEditSurfaceData>, action: SurfaceAction): string {
  return envelope.data.branchNavigation?.branchReturnLabel ?? envelope.data.branchReturnLabel ?? (action.actionId === 'action-user-admin-show-users' ? action.label : 'Show users');
}

function userAdminBranchReturnInput(envelope: SurfaceEnvelope<DetailEditSurfaceData>): Record<string, string> {
  const branch = envelope.data.branchNavigation;
  return {
    branchRootSurfaceId: branch?.branchRootSurfaceId ?? envelope.data.branchRootSurfaceId ?? 'surface-user-admin-users',
    branchReturnActionId: branch?.branchReturnActionId ?? envelope.data.branchReturnActionId ?? 'action-user-admin-show-users',
    safeFilterPreservation: branch?.safeFilterPreservation ?? envelope.data.safeFilterPreservation ?? 'backend-authored-only',
    correlationId: branch?.correlationId ?? envelope.correlationId
  };
}

function statusValue(value: string | undefined) {
  const normalized = (value ?? 'ACTIVE').toUpperCase().replace(/[\[\]"-]+/g, '_').replace(/\s+/g, '_');
  if (normalized.includes('REMOVED') || normalized.includes('DEACTIVATED') || normalized.includes('DELETED')) return 'REMOVED';
  if (normalized.includes('SUSPENDED') || normalized.includes('DISABLED') || normalized.includes('INACTIVE')) return 'REMOVED';
  return 'ACTIVE';
}

function roleValue(value: string | undefined) {
  const normalized = (value ?? 'TENANT_EMPLOYEE').toUpperCase().replace(/[\[\]"-]+/g, '_').replace(/\s+/g, '_');
  if (normalized.includes('TENANT_ADMIN')) return 'TENANT_ADMIN';
  if (normalized.includes('AUDITOR')) return 'AUDITOR';
  return 'TENANT_EMPLOYEE';
}

function humanize(value: string) {
  return value.replace(/[\[\]_"]|-/g, ' ').replace(/\s+/g, ' ').trim().toLowerCase().replace(/(^|\s)\w/g, (letter) => letter.toUpperCase());
}

function agentAdminArtifactTitle(artifact: Record<string, unknown>) {
  const kind = String(artifact.artifactKind ?? 'artifact');
  if (kind.includes('prompt')) return 'Prompt behavior';
  if (kind.includes('skill') || kind.includes('reference') || kind.includes('manifest')) return 'Skill and reference access';
  if (kind.includes('tool')) return 'Tool boundary';
  if (kind.includes('model')) return 'Model readiness';
  return humanize(kind);
}

function agentAdminArtifactSummary(artifact: Record<string, unknown>) {
  const status = humanize(String(artifact.status ?? 'backend state'));
  const kind = String(artifact.artifactKind ?? 'artifact');
  if (kind.includes('prompt')) return `${status}. Review proposed wording, risk, and redacted previews through prompt governance.`;
  if (kind.includes('skill') || kind.includes('reference') || kind.includes('manifest')) return `${status}. Full document loads remain governed and trace-linked.`;
  if (kind.includes('tool')) return `${status}. Side-effecting tool expansion requires simulation and approval.`;
  if (kind.includes('model')) return `${status}. Provider aliases are visible; credentials remain backend-only.`;
  return `${status}. Open a dedicated task surface for changes.`;
}

function statusTone(value: string) {
  const status = value.toLowerCase();
  if (status.includes('active') || status.includes('ready')) return 'success';
  if (status.includes('review') || status.includes('pending') || status.includes('draft')) return 'warning';
  if (status.includes('blocked') || status.includes('denied') || status.includes('disabled')) return 'danger';
  return 'info';
}

function UserAdminDetailOverview({ envelope }: { envelope: SurfaceEnvelope<DetailEditSurfaceData> }) {
  const data = envelope.data;
  const isRolePreview = data.surfaceContract === 'user_admin.role_change_preview.v1' || envelope.surfaceId === 'surface-user-admin-role-change-preview';
  return (
    <section className="user-admin-detail-overview" aria-label="User Admin capability and policy evidence">
      <article className="authority-context-panel">
        <h4>{isRolePreview ? 'Role-change preview boundary' : 'User Admin detail boundary'}</h4>
        <p>{data.message ?? 'Frontend controls are advisory only; selected AuthContext, backend authorization, policy, idempotency, and audit/work traces remain authoritative.'}</p>
        <dl>
          <div><dt>Surface contract</dt><dd>{data.surfaceContract ?? 'user_admin.user_detail.v1'}</dd></div>
          <div><dt>Status</dt><dd>{data.status ?? 'ready'}</dd></div>
          <div><dt>Correlation</dt><dd>{envelope.correlationId}</dd></div>
          <div><dt>Redaction</dt><dd>{renderSurfaceValue(data.redaction) ?? envelope.redaction.profile}</dd></div>
        </dl>
      </article>
      {isRolePreview && (
        <article className="access-management-card role-change-preview">
          <h4>Capability delta and affected workstreams</h4>
          <dl>
            <dt>Added</dt><dd>{data.capabilityDelta?.added?.join(', ') || 'none'}</dd>
            <dt>Removed</dt><dd>{data.capabilityDelta?.removed?.join(', ') || 'none'}</dd>
            <dt>Unchanged</dt><dd>{data.capabilityDelta?.unchanged?.join(', ') || 'none'}</dd>
            <dt>Affected workstreams</dt><dd>{data.affectedWorkstreams?.join(', ') || 'not reported'}</dd>
            <dt>Last-admin impact</dt><dd>{data.lastAdminImpact ?? 'not reported'}</dd>
          </dl>
          {data.policyHints && <ul>{data.policyHints.map((hint) => <li key={hint}>{hint}</li>)}</ul>}
        </article>
      )}
    </section>
  );
}

function isAgentAdminSurface(envelope: SurfaceEnvelope<DetailEditSurfaceData>) {
  return envelope.ownerFunctionalAgentId === 'agent-admin-agent' || envelope.surfaceId.startsWith('surface-agent-admin-') || envelope.data.surfaceContract?.startsWith('agent_admin.');
}

function fieldValue(fields: NonNullable<DetailEditSurfaceData['fields']>, fieldId: string): string | undefined {
  return fields.find((field) => field.fieldId === fieldId)?.value;
}

function isUserAdminSurface(envelope: SurfaceEnvelope<DetailEditSurfaceData>) {
  return envelope.ownerFunctionalAgentId === 'user-admin-agent' || envelope.surfaceId.startsWith('surface-user-admin-') || envelope.data.surfaceContract?.startsWith('user_admin.');
}

function MyAccountDetailOverview({ envelope, fieldValues, onAction }: { envelope: SurfaceEnvelope<DetailEditSurfaceData>; fieldValues: Record<string, string>; onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void }) {
  if (envelope.surfaceId === 'surface-my-profile') {
    return (
      <section className="my-account-detail-overview" aria-label="Profile self-service boundary">
        <article className="authority-context-panel">
          <h4>Identity boundary</h4>
          <p>{envelope.data.providerBoundarySummary ?? 'Authentication/provider-backed facts are browser-safe and read-only here.'}</p>
          <dl>
            <div><dt>Account</dt><dd>{String(envelope.data.profileSummary?.email ?? envelope.data.recordLabel ?? 'current signed-in account')}</dd></div>
            <div><dt>Status</dt><dd>{String(envelope.data.profileSummary?.accountStatus ?? 'browser-safe status')}</dd></div>
            <div><dt>Selected context</dt><dd>{String(envelope.data.profileSummary?.selectedContextLabel ?? envelope.authContext.tenantId)}</dd></div>
            <div><dt>Editable authority</dt><dd>{envelope.data.permissionState?.authoritativeCapabilityId ?? 'my_account.update_profile_settings'}</dd></div>
            <div><dt>Unsupported changes</dt><dd>Roles, capabilities, account status, provider secrets</dd></div>
          </dl>
        </article>
      </section>
    );
  }
  if (envelope.surfaceId === 'surface-my-settings') {
    const selectedTheme = fieldValues.preferredThemeId ?? envelope.data.preferredThemeId;
    return (
      <section className="my-account-detail-overview" aria-label="Personal preference and named theme boundary">
        <article className="authority-context-panel named-theme-preview-panel">
          <h4>Named theme selection</h4>
          <p>Theme changes preview immediately in this browser by switching documented theme tokens. Save/Confirm persists through the governed backend settings action.</p>
          <p className="form-status" role="status" aria-live="polite">Preview unsaved: {selectedTheme ?? 'backend selected theme'} · no light/dark/system mode is exposed.</p>
          <p className="field-helper">Save/Confirm is required before this browser preview becomes your persisted preference. If save fails, keep this preview local and retry or select the backend theme again.</p>
          {envelope.data.availableThemes && envelope.data.availableThemes.length > 0 && <ul className="named-theme-list" aria-label="Available named themes">{envelope.data.availableThemes.map((theme) => {
            const themeId = theme.value ?? theme.themeId ?? theme.id ?? '';
            return <li key={themeId} className={themeId === selectedTheme ? 'selected' : undefined}><strong>{theme.label ?? theme.name}</strong><span>{themeId === selectedTheme ? 'Selected preview' : 'Available choice'}</span>{themeId === selectedTheme && <em>previewing</em>}</li>;
          })}</ul>}
        </article>
      </section>
    );
  }
  if (envelope.surfaceId === 'surface-my-context') {
    return (
      <section className="my-account-detail-overview" aria-label="Selected AuthContext authority">
        <article className="authority-context-panel">
          <h4>Backend-selected authority</h4>
          <p>Changing context reboots the shell authority basis through /api/me or protected workstream APIs; the browser cannot grant roles or capabilities by editing this surface.</p>
          <dl>
            <div><dt>Organization</dt><dd>Current organization</dd></div>
            <div><dt>Scope</dt><dd>{envelope.authContext.customerId ? 'Selected customer' : 'Tenant scope'}</dd></div>
            <div><dt>Visible permissions</dt><dd>{String(envelope.data.visibleCapabilitySummary?.count ?? envelope.authContext.visibleCapabilityIds.length)}</dd></div>
          </dl>
        </article>
        <p className="surface-state-inline stale">Switching context refreshes the shell authority basis, workstream counters, traces, notifications, and any open structured surfaces.</p>
        {envelope.data.availableContexts && envelope.data.availableContexts.length > 0 && (
          <section className="available-context-grid" aria-label="Authorized context switch targets">
            {envelope.data.availableContexts.map((context, index) => {
              const action = envelope.actions.find((candidate) => candidate.actionId === String(context.actionId ?? 'action-select-my-context'));
              const selectedContextId = String(context.selectedContextId ?? context.contextId ?? '');
              const selected = context.selected === true || selectedContextId === envelope.authContext.selectedContextId;
              return (
                <article key={String(context.selectedContextId ?? index)} className={`surface-row-card ${selected ? 'selected' : ''}`}>
                  <p><span>{String(context.status ?? context.membershipStatus ?? 'active')}{selected ? ' · selected' : ''}</span><strong>{String(context.displayLabel ?? (selected ? 'Current organization' : 'Authorized organization'))}</strong></p>
                  <p>{context.customerId ? 'Selected customer' : 'Tenant scope'} · {Array.isArray(context.roleLabels) ? context.roleLabels.join(', ') : Array.isArray(context.roleIds) ? context.roleIds.join(', ') : 'roles redacted'}</p>
                  {Boolean(context.staleImpact) && <p className="field-helper">{String(context.staleImpact)}</p>}
                  {action && !selected && context.selectable !== false ? <button type="button" className="surface-action-link" onClick={() => onAction?.(action, envelope.surfaceId, { selectedContextId, correlationId: envelope.correlationId })}>Switch to this context</button> : <p className="form-status">{selected ? 'Current context' : 'Context switch is not available'}</p>}
                </article>
              );
            })}
          </section>
        )}
      </section>
    );
  }
  return null;
}

function myAccountEyebrow(surfaceId: string, fallback: string) {
  if (surfaceId === 'surface-my-profile') return 'profile self-service';
  if (surfaceId === 'surface-my-settings') return 'personal preferences';
  if (surfaceId === 'surface-my-context') return 'context and authority';
  return fallback;
}

function renderSurfaceValue(value: unknown): string | undefined {
  if (value == null) return undefined;
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') return String(value);
  if (Array.isArray(value)) return value.map(renderSurfaceValue).filter(Boolean).join(' · ');
  if (typeof value === 'object') return Object.entries(value as Record<string, unknown>).map(([key, entry]) => `${key}: ${renderSurfaceValue(entry) ?? 'n/a'}`).join(' · ');
  return String(value);
}

function isNamedThemeId(value: string) {
  return /^[a-z][a-z0-9-]*-(light|dark|theme)$/.test(value);
}
