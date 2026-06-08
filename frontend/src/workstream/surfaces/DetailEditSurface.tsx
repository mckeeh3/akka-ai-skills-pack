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
      window.localStorage.setItem('seed-ui-theme', value);
    }
    onFieldValueChange?.(fieldId, value, envelope.surfaceId);
  }

  const isMyAccountSurface = envelope.surfaceId === 'surface-my-profile' || envelope.surfaceId === 'surface-my-settings' || envelope.surfaceId === 'surface-my-context';

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
      {isMyAccountSurface && <MyAccountDetailOverview envelope={envelope} fieldValues={fieldValues} />}
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
        <form className="surface-detail-edit-form" data-my-account-detail={isMyAccountSurface ? 'true' : undefined} aria-label={`${envelope.title} edit form`}>
          <input type="hidden" name="recordId" value={recordId} readOnly />
          {fields.map((field) => {
            const inputId = `${envelope.surfaceId}-${field.fieldId}`;
            const describedBy = field.disabledReason ? `${inputId}-reason` : undefined;
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
                {!field.editable && field.disabledReason && <p id={describedBy} className="form-error denied-reason">{field.disabledReason}</p>}
              </div>
            );
          })}
        </form>
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
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} actionInput={editableActionInput} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

function MyAccountDetailOverview({ envelope, fieldValues }: { envelope: SurfaceEnvelope<DetailEditSurfaceData>; fieldValues: Record<string, string> }) {
  if (envelope.surfaceId === 'surface-my-profile') {
    return (
      <section className="my-account-detail-overview" aria-label="Profile self-service boundary">
        <article className="authority-context-panel">
          <h4>Identity boundary</h4>
          <p>{envelope.data.providerBoundarySummary ?? 'Authentication/provider-backed facts are browser-safe and read-only here.'}</p>
          <dl>
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
          <p className="form-status">Previewing: {selectedTheme ?? 'backend selected theme'} · no light/dark/system mode is exposed.</p>
          {envelope.data.availableThemes && envelope.data.availableThemes.length > 0 && <ul className="named-theme-list">{envelope.data.availableThemes.map((theme) => <li key={theme.value ?? theme.themeId ?? theme.id}><strong>{theme.label ?? theme.name}</strong><span>{theme.value ?? theme.themeId ?? theme.id}</span></li>)}</ul>}
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
            <div><dt>Tenant</dt><dd>{String(envelope.data.selectedContext?.tenantId ?? envelope.authContext.tenantId)}</dd></div>
            <div><dt>Customer</dt><dd>{String(envelope.data.selectedContext?.customerId ?? envelope.authContext.customerId ?? 'Tenant scope')}</dd></div>
            <div><dt>Visible capabilities</dt><dd>{String(envelope.data.visibleCapabilitySummary?.count ?? envelope.authContext.visibleCapabilityIds.length)}</dd></div>
          </dl>
        </article>
        {envelope.data.availableContexts && envelope.data.availableContexts.length > 0 && (
          <section className="available-context-grid" aria-label="Authorized context switch targets">
            {envelope.data.availableContexts.map((context, index) => <article key={String(context.selectedContextId ?? index)} className="surface-row-card"><p><span>{String(context.status ?? 'active')}</span><strong>{String(context.tenantId ?? 'Authorized tenant')}</strong></p><p>{String(context.customerId ?? 'Tenant scope')} · {Array.isArray(context.roleIds) ? context.roleIds.join(', ') : 'roles redacted'}</p></article>)}
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

function isNamedThemeId(value: string) {
  return ['aurora-light', 'cobalt-light', 'obsidian-dark', 'midnight-dark', 'dark-night'].includes(value);
}
