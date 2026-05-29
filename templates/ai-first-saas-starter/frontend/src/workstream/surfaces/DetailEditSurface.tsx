import type { DetailEditSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type DetailEditSurfaceProps = {
  envelope: SurfaceEnvelope<DetailEditSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

function stringifySafe(value: unknown): string {
  if (Array.isArray(value)) return value.join(', ');
  if (value && typeof value === 'object') return JSON.stringify(value);
  return String(value ?? '');
}

export function DetailEditSurface({ envelope, onAction }: DetailEditSurfaceProps) {
  const permissionState = envelope.data.permissionState;
  const fields = envelope.data.fields ?? [];
  const recordId = envelope.data.recordId ?? envelope.data.traceId ?? envelope.data.category ?? envelope.surfaceId;
  const recordLabel = envelope.data.recordLabel ?? envelope.data.eventKind ?? envelope.data.category ?? recordId;
  const isAuditEvidence = fields.length === 0 && (envelope.surfaceId.includes('audit-trace') || envelope.data.traceId || envelope.data.safeReason || envelope.data.redactedEvidence);

  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="detail-heading" aria-label="Detail record context">
        <div>
          <p className="eyebrow">{envelope.data.recordKind ?? (isAuditEvidence ? 'audit evidence' : 'detail')} surface</p>
          <h3>{recordLabel}</h3>
          {envelope.data.summary && <p>{envelope.data.summary}</p>}
        </div>
        {envelope.data.version !== undefined && <span className="version-chip">Version {envelope.data.version}</span>}
      </section>
      {permissionState && (
        <p className={permissionState.canEdit ? 'form-status' : 'form-status conflict'}>
          Edit authority: {permissionState.authoritativeCapabilityId}. {permissionState.reason}
        </p>
      )}
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
        <form className="surface-detail-edit-form" aria-label={`${envelope.title} edit form`}>
          <input type="hidden" name="recordId" value={recordId} readOnly />
          {fields.map((field) => {
            const inputId = `${envelope.surfaceId}-${field.fieldId}`;
            const describedBy = field.disabledReason ? `${inputId}-reason` : undefined;
            return (
              <div key={field.fieldId} className="surface-detail-field">
                <label htmlFor={inputId}>{field.label}</label>
                {field.inputType === 'textarea' ? (
                  <textarea id={inputId} name={field.fieldId} defaultValue={field.value} readOnly={!field.editable} aria-readonly={!field.editable} aria-describedby={describedBy} />
                ) : field.inputType === 'select' && field.options ? (
                  <select id={inputId} name={field.fieldId} defaultValue={field.options.find((option) => option.label === field.value)?.value ?? field.value} disabled={!field.editable} aria-describedby={describedBy}>
                    {field.options.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
                  </select>
                ) : (
                  <input id={inputId} name={field.fieldId} type={field.inputType ?? 'text'} defaultValue={field.value} readOnly={!field.editable} aria-readonly={!field.editable} aria-describedby={describedBy} />
                )}
                {!field.editable && field.disabledReason && <p id={describedBy} className="form-error denied-reason">{field.disabledReason}</p>}
              </div>
            );
          })}
        </form>
      ) : (
        <article className="audit-evidence-panel" aria-label="Denial/provider/tool evidence">
          {envelope.data.safeReason && <p className="safe-reason">{envelope.data.safeReason}</p>}
          {envelope.data.redactedEvidence && <p>{envelope.data.redactedEvidence}</p>}
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
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}
