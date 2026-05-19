import type { DetailEditSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type DetailEditSurfaceProps = {
  envelope: SurfaceEnvelope<DetailEditSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

export function DetailEditSurface({ envelope, onAction }: DetailEditSurfaceProps) {
  const permissionState = envelope.data.permissionState;
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="detail-heading" aria-label="Detail record context">
        <div>
          <p className="eyebrow">{envelope.data.recordKind ?? 'detail'} surface</p>
          <h3>{envelope.data.recordLabel ?? envelope.data.recordId}</h3>
          {envelope.data.summary && <p>{envelope.data.summary}</p>}
        </div>
        <span className="version-chip">Version {envelope.data.version}</span>
      </section>
      {permissionState && (
        <p className={permissionState.canEdit ? 'form-status' : 'form-status conflict'}>
          Edit authority: {permissionState.authoritativeCapabilityId}. {permissionState.reason}
        </p>
      )}
      <form className="surface-detail-edit-form" aria-label={`${envelope.title} edit form`}>
        <input type="hidden" name="recordId" value={envelope.data.recordId} readOnly />
        {envelope.data.fields.map((field) => {
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
