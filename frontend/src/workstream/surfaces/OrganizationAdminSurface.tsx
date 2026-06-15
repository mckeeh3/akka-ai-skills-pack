import { useMemo, useState, type FormEvent } from 'react';
import type { OrganizationAdminSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceStateFrame } from './SurfaceStateFrame';

const boundaryFallback = 'Organization administration manages the Tenant lifecycle boundary only; it does not grant tenant/customer application-data access, support access, provider secret access, or billing-derived authority.';
const directorySurfaceId = 'surface-user-admin-organization-directory';
const detailSurfaceId = 'surface-user-admin-organization-detail';
const createSurfaceId = 'surface-user-admin-organization-create';
const renameSurfaceId = 'surface-user-admin-organization-rename';
const suspendSurfaceId = 'surface-user-admin-organization-suspend-confirmation';
const reactivateSurfaceId = 'surface-user-admin-organization-reactivate-confirmation';

type Props = {
  envelope: SurfaceEnvelope<OrganizationAdminSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

type OrganizationLike = NonNullable<OrganizationAdminSurfaceData['organizations']>[number];

const organizationBranchRootSurfaceId = 'surface-user-admin-organization-directory';
const organizationBranchReturnActionId = 'action-user-admin-show-organizations';

export function OrganizationAdminSurface({ envelope, onAction }: Props) {
  const data = envelope.data;
  const detail = data.organizationDetail;
  const selectedOrganization = organizationFromDetail(detail) ?? data.organizations?.[0];
  const isDirectory = envelope.surfaceId === directorySurfaceId || data.surfaceContract === 'user_admin.organization_directory.v1';
  const isDetail = envelope.surfaceId === detailSurfaceId || data.surfaceContract === 'user_admin.organization_detail.v1';
  const isCreate = envelope.surfaceId === createSurfaceId || data.surfaceContract === 'user_admin.organization_create.v1';
  const isRename = envelope.surfaceId === renameSurfaceId || data.surfaceContract === 'user_admin.organization_rename.v1';
  const isSuspend = envelope.surfaceId === suspendSurfaceId || data.surfaceContract === 'user_admin.organization_suspend_confirmation.v1';
  const isReactivate = envelope.surfaceId === reactivateSurfaceId || data.surfaceContract === 'user_admin.organization_reactivate_confirmation.v1';

  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="organization-admin-surface" aria-labelledby={`${envelope.surfaceId}-heading`}>
        <OrganizationHeader envelope={envelope} />
        <OrganizationState data={data} actions={envelope.actions} />
        {isDirectory && <OrganizationDirectory envelope={envelope} onAction={onAction} />}
        {isDetail && <OrganizationDetail envelope={envelope} organization={selectedOrganization} onAction={onAction} />}
        {isCreate && <OrganizationCreateForm envelope={envelope} onAction={onAction} />}
        {isRename && <OrganizationRenameForm envelope={envelope} organization={selectedOrganization} onAction={onAction} />}
        {isSuspend && <OrganizationLifecycleConfirmation envelope={envelope} organization={selectedOrganization} actionId="action-organization-suspend" title="Suspend Organization" submitLabel="Suspend" consequence="Suspending changes the Organization/Tenant lifecycle boundary only; it does not expose or mutate tenant application data." onAction={onAction} />}
        {isReactivate && <OrganizationLifecycleConfirmation envelope={envelope} organization={selectedOrganization} actionId="action-organization-reactivate" title="Reactivate Organization" submitLabel="Reactivate" consequence="Reactivation restores the Organization/Tenant lifecycle boundary and remains subject to backend authorization and audit." onAction={onAction} />}
        {data.redaction && <p className="redaction-note">Browser redaction: {renderValue(data.redaction)}. Hidden counts, provider secrets, billing details, support-access internals, and tenant application data are omitted.</p>}
      </section>
    </SurfaceStateFrame>
  );
}

function OrganizationHeader({ envelope }: { envelope: SurfaceEnvelope<OrganizationAdminSurfaceData> }) {
  const data = envelope.data;
  return (
    <div className="organization-admin-header">
      <div>
        <p className="eyebrow">SaaS Owner · Organization Admin</p>
        <h3 id={`${envelope.surfaceId}-heading`}>{envelope.title || 'Organizations'}</h3>
        <p>{data.boundaryNotice ?? data.safeBoundaryNotice ?? boundaryFallback}</p>
      </div>
      <div className="organization-admin-scope" aria-label="Authority basis">
        <strong>{data.scopeLabel ?? 'SaaS Owner scope'}</strong>
        <span>{data.authorityBasis ?? 'Backend checks selected AuthContext and saas_owner.organization.list/read.'}</span>
      </div>
    </div>
  );
}

function OrganizationState({ data, actions }: { data: OrganizationAdminSurfaceData; actions: SurfaceAction[] }) {
  const forbidden = data.systemStates?.includes('forbidden') || actions.every((action) => action.disabled?.reasonCode === 'forbidden');
  return (
    <>
      {forbidden && <p className="surface-state-inline forbidden" role="status">{data.forbiddenMessage ?? 'Organization Admin is unavailable for this selected context. Tenant Admin and Customer Admin contexts cannot gain SaaS Owner authority from browser state.'}</p>}
      {data.lastResult && <p className={`surface-state-inline ${data.lastResult.status}`} role="status">{data.lastResult.message} {data.lastResult.correlationId ? `Correlation ${data.lastResult.correlationId}.` : ''}</p>}
    </>
  );
}

function OrganizationDirectory({ envelope, onAction }: Props) {
  const data = envelope.data;
  const organizations = data.organizations ?? [];
  const [query, setQuery] = useState(String(data.filters?.query ?? ''));
  const [status, setStatus] = useState(String(data.filters?.status ?? ''));
  const visibleOrganizations = useMemo(() => organizations, [organizations]);

  function submitSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    run(envelope, onAction, 'action-organization-list', { query, status });
  }

  return (
    <div className="organization-admin-grid">
      <section className="user-admin-list-panel" aria-labelledby={`${envelope.surfaceId}-list-heading`}>
        <form className="surface-search-form organization-admin-search" role="search" onSubmit={submitSearch}>
          <label htmlFor={`${envelope.surfaceId}-query`}>Search Organizations</label>
          <input className="designed-control surface-search-control" id={`${envelope.surfaceId}-query`} name="query" value={query} onChange={(event) => setQuery(event.currentTarget.value)} placeholder="Organization name or safe id" />
          <label htmlFor={`${envelope.surfaceId}-status`}>Status</label>
          <select className="designed-control" id={`${envelope.surfaceId}-status`} name="status" value={status} onChange={(event) => setStatus(event.currentTarget.value)}>
            <option value="">Any status</option>
            <option value="active">Active</option>
            <option value="suspended">Suspended</option>
          </select>
          <button className="surface-action-link secondary" type="submit">Refresh list</button>
        </form>
        <div className="surface-section-heading compact"><div><p className="eyebrow">{visibleOrganizations.length} visible</p><h4 id={`${envelope.surfaceId}-list-heading`}>Organization directory</h4></div><p>Filtering, row visibility, and counts are backend-authored; local query text is submitted through the governed refresh action only.</p></div>
        {visibleOrganizations.length === 0 ? <p className="surface-empty-copy">{data.emptyMessage ?? 'No Organizations are visible for this safe filter.'}</p> : (
          <div className="user-admin-clean-list" role="list">
            {visibleOrganizations.map((organization) => (
              <button key={organization.organizationId} type="button" role="listitem" className="user-admin-clean-row organization-admin-row" onClick={() => run(envelope, onAction, 'action-organization-read', { organizationId: organization.organizationId })}>
                <span className="user-admin-person"><strong>{organization.organizationName}</strong><small>{organization.organizationId}</small></span>
                <span>{organization.safeLifecycleSummary ?? 'Tenant boundary only'}</span>
                <span className={`status-pill ${organization.status === 'active' ? 'success' : 'danger'}`}>{organization.status}</span>
              </button>
            ))}
          </div>
        )}
      </section>
      <section className="user-admin-list-panel" aria-labelledby={`${envelope.surfaceId}-tasks-heading`}>
        <div className="surface-section-heading compact"><div><p className="eyebrow">Things I can do</p><h4 id={`${envelope.surfaceId}-tasks-heading`}>Organization tasks</h4></div></div>
        {envelope.actions.some((action) => action.actionId === 'action-open-organization-create' && !action.disabled) ? <button className="surface-action-link primary" type="button" onClick={() => run(envelope, onAction, 'action-open-organization-create', {})}>Create Organization</button> : <p className="surface-empty-copy">No backend-authorized create action is available in this selected context.</p>}
        <p className="surface-empty-copy">Create, edit, suspend, and reactivate work is delegated to dedicated task surfaces. Directory rows open safe detail first.</p>
      </section>
    </div>
  );
}

function OrganizationDetail({ envelope, organization, onAction }: Props & { organization?: OrganizationLike }) {
  const detail = envelope.data.organizationDetail;
  const renameAction = envelope.actions.find((action) => action.actionId === 'action-open-organization-rename');
  const suspendAction = envelope.actions.find((action) => action.actionId === 'action-open-organization-suspend');
  const reactivateAction = envelope.actions.find((action) => action.actionId === 'action-open-organization-reactivate');
  const visibleActions = detail?.visibleActions ?? organization?.actionAvailability ?? [];
  return (
    <section className="user-admin-list-panel" aria-labelledby={`${envelope.surfaceId}-detail-heading`}>
      <div className="surface-section-heading compact"><div><p className="eyebrow">Safe detail</p><h4 id={`${envelope.surfaceId}-detail-heading`}>{organization?.organizationName ?? 'Select an Organization'}</h4></div></div>
      <p className="surface-empty-copy">{detail?.safeBoundaryNotice ?? envelope.data.boundaryNotice ?? boundaryFallback}</p>
      {organization && <dl className="organization-admin-detail"><div><dt>Status</dt><dd>{organization.status}</dd></div><div><dt>Trace refs</dt><dd>{(organization.traceRefs ?? detail?.traceRefs ?? []).join(' · ') || 'Trace available after backend action'}</dd></div></dl>}
      <div className="organization-admin-lifecycle-actions" aria-label="Organization task entry points">
        {organization && <button className="surface-action-link secondary" type="button" disabled={!renameAction || Boolean(renameAction.disabled)} aria-disabled={!renameAction || Boolean(renameAction.disabled)} title={organizationActionUnavailableMessage(renameAction)} onClick={() => runAction(envelope, onAction, renameAction, { organizationId: organization.organizationId, recordId: organization.organizationId })}>Rename selected Organization</button>}
        {organization && visibleActions.includes('suspend') && <button className="surface-action-link danger" type="button" disabled={!suspendAction || Boolean(suspendAction.disabled)} aria-disabled={!suspendAction || Boolean(suspendAction.disabled)} title={organizationActionUnavailableMessage(suspendAction)} onClick={() => runAction(envelope, onAction, suspendAction, { organizationId: organization.organizationId, recordId: organization.organizationId })}>Open suspend confirmation</button>}
        {organization && visibleActions.includes('reactivate') && <button className="surface-action-link secondary" type="button" disabled={!reactivateAction || Boolean(reactivateAction.disabled)} aria-disabled={!reactivateAction || Boolean(reactivateAction.disabled)} title={organizationActionUnavailableMessage(reactivateAction)} onClick={() => runAction(envelope, onAction, reactivateAction, { organizationId: organization.organizationId, recordId: organization.organizationId })}>Open reactivate confirmation</button>}
        <OrganizationBranchReturn envelope={envelope} onAction={onAction} />
      </div>
      {organization && !renameAction && <p className="surface-empty-copy" role="status">Rename is unavailable because the backend did not include an authorized Organization rename task action for this detail surface.</p>}
    </section>
  );
}

function OrganizationCreateForm({ envelope, onAction }: Props) {
  const [createName, setCreateName] = useState('');
  const [reason, setReason] = useState('');
  const [validationError, setValidationError] = useState<string>();
  function submitCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!createName.trim()) return setValidationError('Organization name is required.');
    setValidationError(undefined);
    run(envelope, onAction, 'action-organization-create', { organizationName: createName.trim(), reason: reason.trim(), idempotencyKey: idempotencyKey('create', createName) });
  }
  return (
    <form className="organization-admin-command-form" aria-label="Create Organization" onSubmit={submitCreate}>
      <h4>Create Organization</h4>
      {validationError && <p className="surface-state-inline validation-error" role="alert">{validationError}</p>}
      <p className="surface-empty-copy">This single-purpose surface creates an active Organization/Tenant boundary through the protected Admin API.</p>
      <label>Name<input className="designed-control" value={createName} onChange={(event) => setCreateName(event.currentTarget.value)} required /></label>
      <label>Reason<input className="designed-control" value={reason} onChange={(event) => setReason(event.currentTarget.value)} placeholder="Reason for audit/work trace" /></label>
      <div className="organization-admin-lifecycle-actions">
        <button className="surface-action-link primary" type="submit">Create Organization</button>
        <OrganizationBranchReturn envelope={envelope} onAction={onAction} compact />
      </div>
    </form>
  );
}

function OrganizationRenameForm({ envelope, organization, onAction }: Props & { organization?: OrganizationLike }) {
  const [renameName, setRenameName] = useState(organization?.organizationName ?? '');
  const [reason, setReason] = useState('');
  const [validationError, setValidationError] = useState<string>();
  function submitRename(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!organization?.organizationId) return setValidationError('Choose an Organization before renaming.');
    if (!renameName.trim()) return setValidationError('Organization name is required.');
    setValidationError(undefined);
    run(envelope, onAction, 'action-organization-rename', { organizationId: organization.organizationId, organizationName: renameName.trim(), reason: reason.trim(), idempotencyKey: idempotencyKey('rename', organization.organizationId) });
  }
  return (
    <form className="organization-admin-command-form" aria-label="Rename Organization" onSubmit={submitRename}>
      <h4>Rename selected Organization</h4>
      {validationError && <p className="surface-state-inline validation-error" role="alert">{validationError}</p>}
      <p className="surface-empty-copy">Rename updates the Organization display label only and returns to safe detail.</p>
      <label>New Organization name<input className="designed-control" value={renameName} onChange={(event) => setRenameName(event.currentTarget.value)} required /></label>
      <label>Reason<input className="designed-control" value={reason} onChange={(event) => setReason(event.currentTarget.value)} placeholder="Reason for audit/work trace" /></label>
      <div className="organization-admin-lifecycle-actions">
        <button className="surface-action-link secondary" type="submit">Rename</button>
        <OrganizationBranchReturn envelope={envelope} onAction={onAction} compact />
      </div>
    </form>
  );
}

function OrganizationLifecycleConfirmation({ envelope, organization, actionId, title, submitLabel, consequence, onAction }: Props & { organization?: OrganizationLike; actionId: 'action-organization-suspend' | 'action-organization-reactivate'; title: string; submitLabel: string; consequence: string }) {
  const [reason, setReason] = useState('');
  const [validationError, setValidationError] = useState<string>();
  function submitLifecycle() {
    if (!organization?.organizationId) return setValidationError('Choose an Organization before changing lifecycle state.');
    if (!reason.trim()) return setValidationError('Reason is required for Organization lifecycle changes.');
    setValidationError(undefined);
    run(envelope, onAction, actionId, { organizationId: organization.organizationId, reason: reason.trim(), idempotencyKey: idempotencyKey(actionId, organization.organizationId) });
  }
  return (
    <section className="organization-admin-command-form" aria-label={title}>
      <h4>{title}</h4>
      {validationError && <p className="surface-state-inline validation-error" role="alert">{validationError}</p>}
      <p className="surface-empty-copy">{consequence}</p>
      <dl className="organization-admin-detail"><div><dt>Organization</dt><dd>{organization?.organizationName ?? 'No Organization selected'}</dd></div><div><dt>Status</dt><dd>{organization?.status ?? 'unknown'}</dd></div></dl>
      <label>Reason<input className="designed-control" value={reason} onChange={(event) => setReason(event.currentTarget.value)} placeholder="Required reason for audit/work trace" required /></label>
      <div className="organization-admin-lifecycle-actions">
        <button className={`surface-action-link ${actionId === 'action-organization-suspend' ? 'danger' : 'secondary'}`} type="button" onClick={submitLifecycle}>{submitLabel}</button>
        <OrganizationBranchReturn envelope={envelope} onAction={onAction} compact />
      </div>
    </section>
  );
}

function organizationFromDetail(detail: OrganizationAdminSurfaceData['organizationDetail']): OrganizationLike | undefined {
  if (!detail?.organizationId || !detail.organizationName || !detail.status) return undefined;
  return { organizationId: detail.organizationId, organizationName: detail.organizationName, status: detail.status, traceRefs: detail.traceRefs, actionAvailability: detail.visibleActions };
}

function OrganizationBranchReturn({ envelope, onAction, compact = false }: Props & { compact?: boolean }) {
  const action = organizationBranchReturnAction(envelope.actions);
  if (!action) return null;
  return (
    <nav className={compact ? 'organization-admin-branch-return compact' : 'organization-admin-branch-return'} aria-label="Organization branch navigation">
      <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(action, envelope.surfaceId, organizationBranchReturnInput(envelope))}>{organizationBranchReturnLabel(envelope, action)}</button>
      {!compact && <p className="capability-basis">{organizationBranchCapability(envelope, action)} · safe filters: {organizationSafeFilterPreservation(envelope)}</p>}
    </nav>
  );
}

function organizationBranchReturnAction(actions: SurfaceAction[]): SurfaceAction | undefined {
  return actions.find((action) => action.actionId === organizationBranchReturnActionId) ?? actions.find((action) => action.actionId === 'action-organization-list');
}

function organizationBranchReturnLabel(envelope: SurfaceEnvelope<OrganizationAdminSurfaceData>, action: SurfaceAction): string {
  const data = envelope.data as OrganizationAdminSurfaceData & { branchNavigation?: { branchReturnLabel?: string }; branchReturnLabel?: string };
  return data.branchNavigation?.branchReturnLabel ?? data.branchReturnLabel ?? (action.actionId === organizationBranchReturnActionId ? action.label : 'Back to organizations');
}

function organizationBranchReturnInput(envelope: SurfaceEnvelope<OrganizationAdminSurfaceData>): Record<string, string> {
  const data = envelope.data as OrganizationAdminSurfaceData & { branchNavigation?: { branchRootSurfaceId?: string; branchReturnActionId?: string; safeFilterPreservation?: string; correlationId?: string }; branchRootSurfaceId?: string; branchReturnActionId?: string; safeFilterPreservation?: string };
  const branch = data.branchNavigation;
  return {
    branchRootSurfaceId: branch?.branchRootSurfaceId ?? data.branchRootSurfaceId ?? organizationBranchRootSurfaceId,
    branchReturnActionId: branch?.branchReturnActionId ?? data.branchReturnActionId ?? organizationBranchReturnActionId,
    safeFilterPreservation: branch?.safeFilterPreservation ?? data.safeFilterPreservation ?? 'backend-authored-only',
    correlationId: branch?.correlationId ?? envelope.correlationId
  };
}

function organizationBranchCapability(envelope: SurfaceEnvelope<OrganizationAdminSurfaceData>, action: SurfaceAction): string {
  const data = envelope.data as OrganizationAdminSurfaceData & { branchNavigation?: { capabilityId?: string } };
  return data.branchNavigation?.capabilityId ?? action.capabilityId ?? 'saas_owner.organization.list';
}

function organizationSafeFilterPreservation(envelope: SurfaceEnvelope<OrganizationAdminSurfaceData>): string {
  const data = envelope.data as OrganizationAdminSurfaceData & { branchNavigation?: { safeFilterPreservation?: string }; safeFilterPreservation?: string };
  return data.branchNavigation?.safeFilterPreservation ?? data.safeFilterPreservation ?? 'backend-authored-only';
}

function run(envelope: SurfaceEnvelope<OrganizationAdminSurfaceData>, onAction: Props['onAction'], actionId: string, input: Record<string, string>) {
  const action = envelope.actions.find((candidate) => candidate.actionId === actionId);
  runAction(envelope, onAction, action, input);
}

function runAction(envelope: SurfaceEnvelope<OrganizationAdminSurfaceData>, onAction: Props['onAction'], action: SurfaceAction | undefined, input: Record<string, string>) {
  if (!action || action.disabled) return;
  onAction?.(action, envelope.surfaceId, input);
}

function organizationActionUnavailableMessage(action: SurfaceAction | undefined): string | undefined {
  if (!action) return 'The backend did not authorize this Organization task action for the current detail surface.';
  return action.disabled?.message;
}

function idempotencyKey(prefix: string, seed: string) {
  return `ui-${prefix}-${seed.trim().toLowerCase().replace(/[^a-z0-9]+/g, '-') || Date.now()}`;
}

function renderValue(value: unknown): string {
  if (value == null) return 'none';
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') return String(value);
  if (Array.isArray(value)) return value.map(renderValue).join(' · ');
  return Object.entries(value as Record<string, unknown>).map(([key, entry]) => `${key}: ${renderValue(entry)}`).join(' · ');
}
