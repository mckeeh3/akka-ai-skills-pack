import { useEffect, useMemo, useState, type FormEvent } from 'react';
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
      {isMyAccountSurface && <MyAccountDetailOverview envelope={envelope} fieldValues={fieldValues} />}
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
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} actionInput={editableActionInput} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

function UserAdminCleanDetail({ envelope, fieldValues, onAction }: { envelope: SurfaceEnvelope<DetailEditSurfaceData>; fieldValues: Record<string, string>; onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void }) {
  const isInvitation = envelope.data.recordKind === 'invitation';
  const backAction = envelope.actions.find((action) => action.actionId === 'action-display-user-list');
  const primaryActions = envelope.actions.filter((action) => action.actionId !== 'action-display-user-list' && !action.actionId.includes('access-review') && action.actionId !== 'action-useradmin-change-member-roles');
  const changeRoleAction = envelope.actions.find((action) => action.actionId === 'action-useradmin-change-member-roles');
  const fields = envelope.data.fields ?? [];
  const email = fields.find((field) => field.fieldId === 'email')?.value;
  const status = fields.find((field) => field.fieldId === 'status' || field.fieldId === 'membershipStatus')?.value;
  const role = fields.find((field) => field.fieldId === 'role')?.value;
  const [roleDraft, setRoleDraft] = useState(roleValue(role));
  const actionContext = { ...(envelope.data.actionContext ?? {}), ...Object.fromEntries(fields.map((field) => [field.fieldId, fieldValues[field.fieldId] ?? field.value])) };

  function submitRoleChange(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!changeRoleAction) return;
    onAction?.(changeRoleAction, envelope.surfaceId, { ...actionContext, roles: roleDraft, reason: 'Updated from User Admin detail' });
  }
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="user-admin-detail-clean" aria-label={isInvitation ? 'Invitation detail' : 'User detail'}>
        <div className="user-admin-detail-clean-header">
          <div>
            <p className="eyebrow">{isInvitation ? 'Invitation' : 'User'}</p>
            <h3>{envelope.data.recordLabel ?? email ?? envelope.title}</h3>
            {email && <p>{email}</p>}
          </div>
          {backAction && <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(backAction, envelope.surfaceId)}>Back to users</button>}
        </div>

        <dl className="user-admin-detail-summary">
          {status && <div><dt>Status</dt><dd>{humanize(status)}</dd></div>}
          {role && <div><dt>Role</dt><dd>{humanize(role)}</dd></div>}
          {fields.filter((field) => ['delivery', 'expiresAt'].includes(field.fieldId)).map((field) => <div key={field.fieldId}><dt>{field.label}</dt><dd>{humanize(field.value)}</dd></div>)}
        </dl>

        {!isInvitation && (
          <section className="detail-edit-form-section" aria-labelledby={`${envelope.surfaceId}-form-heading`}>
            <div className="surface-section-heading compact">
              <div><p className="eyebrow">Profile</p><h4 id={`${envelope.surfaceId}-form-heading`}>User information</h4></div>
            </div>
            <div className="user-admin-readable-fields">
              {fields.filter((field) => !['membershipId'].includes(field.fieldId)).map((field) => <p key={field.fieldId}><span>{field.label}</span><strong>{fieldValues[field.fieldId] ?? field.value}</strong></p>)}
            </div>
            {changeRoleAction && (
              <form className="user-admin-edit-form" aria-label="Edit user role" onSubmit={submitRoleChange}>
                <label>Role<select className="designed-control" value={roleDraft} onChange={(event) => setRoleDraft(event.currentTarget.value)}><option value="TENANT_EMPLOYEE">Employee</option><option value="TENANT_ADMIN">Tenant admin</option><option value="AUDITOR">Auditor</option></select></label>
                <button className="surface-action-link primary" type="submit">Save role</button>
              </form>
            )}
          </section>
        )}

        <section className="user-admin-context-actions" aria-label={isInvitation ? 'Invitation actions' : 'User actions'}>
          <div className="surface-section-heading compact">
            <div><p className="eyebrow">Actions</p><h4>{isInvitation ? 'Manage invitation' : 'Manage user'}</h4></div>
            <p>{isInvitation ? 'Resend or revoke this invitation when appropriate.' : 'Role, status, and support-access changes are checked before they are applied.'}</p>
          </div>
          <SurfaceActionBar actions={primaryActions} surfaceId={envelope.surfaceId} actionInput={actionContext} onAction={onAction} />
        </section>

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

function roleValue(value: string | undefined) {
  const normalized = (value ?? 'TENANT_EMPLOYEE').toUpperCase().replace(/\s+/g, '_');
  if (normalized.includes('TENANT_ADMIN')) return 'TENANT_ADMIN';
  if (normalized.includes('AUDITOR')) return 'AUDITOR';
  return 'TENANT_EMPLOYEE';
}

function humanize(value: string) {
  return value.replace(/[\[\]_"]/g, ' ').replace(/\s+/g, ' ').trim().toLowerCase().replace(/(^|\s)\w/g, (letter) => letter.toUpperCase());
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
          <div><dt>Surface contract</dt><dd>{data.surfaceContract ?? 'user_admin.user_account.v1'}</dd></div>
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

function isUserAdminSurface(envelope: SurfaceEnvelope<DetailEditSurfaceData>) {
  return envelope.ownerFunctionalAgentId === 'agent-user-admin' || envelope.surfaceId.startsWith('surface-user-admin-') || envelope.data.surfaceContract?.startsWith('user_admin.');
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
          <p className="form-status" role="status" aria-live="polite">Previewing: {selectedTheme ?? 'backend selected theme'} · no light/dark/system mode is exposed.</p>
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

function renderSurfaceValue(value: unknown): string | undefined {
  if (value == null) return undefined;
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') return String(value);
  if (Array.isArray(value)) return value.map(renderSurfaceValue).filter(Boolean).join(' · ');
  if (typeof value === 'object') return Object.entries(value as Record<string, unknown>).map(([key, entry]) => `${key}: ${renderSurfaceValue(entry) ?? 'n/a'}`).join(' · ');
  return String(value);
}

function isNamedThemeId(value: string) {
  return ['aurora-light', 'cobalt-light', 'obsidian-dark', 'midnight-dark', 'dark-night'].includes(value);
}
