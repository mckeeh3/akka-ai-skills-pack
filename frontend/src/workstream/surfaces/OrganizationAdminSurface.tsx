import { useMemo, useState, type FormEvent } from 'react';
import type { OrganizationAdminSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceStateFrame } from './SurfaceStateFrame';

const boundaryFallback = 'Organization administration manages the Tenant lifecycle boundary only; it does not grant tenant/customer application-data access, support access, provider secret access, or billing-derived authority.';

type Props = {
  envelope: SurfaceEnvelope<OrganizationAdminSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function OrganizationAdminSurface({ envelope, onAction }: Props) {
  const data = envelope.data;
  const organizations = data.organizations ?? [];
  const detail = data.organizationDetail;
  const [query, setQuery] = useState(String(data.filters?.query ?? ''));
  const [status, setStatus] = useState(String(data.filters?.status ?? ''));
  const [createName, setCreateName] = useState('');
  const [renameName, setRenameName] = useState(detail?.organizationName ?? organizations[0]?.organizationName ?? '');
  const [reason, setReason] = useState('');
  const [validationError, setValidationError] = useState<string>();
  const selectedOrganization = detail ?? organizations[0];
  const filteredOrganizations = useMemo(() => organizations.filter((organization) => {
    const queryMatch = !query.trim() || organization.organizationName.toLowerCase().includes(query.trim().toLowerCase()) || organization.organizationId.toLowerCase().includes(query.trim().toLowerCase());
    const statusMatch = !status || organization.status === status;
    return queryMatch && statusMatch;
  }), [organizations, query, status]);

  function submitSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setValidationError(undefined);
    run('action-organization-list', { query, status });
  }

  function submitCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!createName.trim()) return setValidationError('Organization name is required.');
    setValidationError(undefined);
    run('action-organization-create', { organizationName: createName.trim(), reason: reason.trim(), idempotencyKey: idempotencyKey('create', createName) });
  }

  function submitRename(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selectedOrganization?.organizationId) return setValidationError('Choose an Organization before renaming.');
    if (!renameName.trim()) return setValidationError('Organization name is required.');
    setValidationError(undefined);
    run('action-organization-rename', { organizationId: selectedOrganization.organizationId, organizationName: renameName.trim(), reason: reason.trim(), idempotencyKey: idempotencyKey('rename', selectedOrganization.organizationId) });
  }

  function lifecycle(actionId: 'action-organization-suspend' | 'action-organization-reactivate') {
    if (!selectedOrganization?.organizationId) return setValidationError('Choose an Organization before changing lifecycle state.');
    if (!reason.trim()) return setValidationError('Reason is required for Organization lifecycle changes.');
    setValidationError(undefined);
    run(actionId, { organizationId: selectedOrganization.organizationId, reason: reason.trim(), idempotencyKey: idempotencyKey(actionId, selectedOrganization.organizationId) });
  }

  function run(actionId: string, input: Record<string, string>) {
    const action = envelope.actions.find((candidate) => candidate.actionId === actionId);
    if (action?.disabled) return setValidationError(action.disabled.message);
    if (action) onAction?.(action, envelope.surfaceId, input);
  }

  const forbidden = data.systemStates?.includes('forbidden') || envelope.actions.every((action) => action.disabled?.reasonCode === 'forbidden');
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="organization-admin-surface" aria-labelledby={`${envelope.surfaceId}-heading`}>
        <div className="organization-admin-header">
          <div>
            <p className="eyebrow">SaaS Owner · Organization Admin</p>
            <h3 id={`${envelope.surfaceId}-heading`}>Organizations</h3>
            <p>{data.boundaryNotice ?? data.safeBoundaryNotice ?? boundaryFallback}</p>
          </div>
          <div className="organization-admin-scope" aria-label="Authority basis">
            <strong>{data.scopeLabel ?? 'SaaS Owner scope'}</strong>
            <span>{data.authorityBasis ?? 'Backend checks selected AuthContext and saas_owner.tenant.read/manage.'}</span>
          </div>
        </div>

        {forbidden && <p className="surface-state-inline forbidden" role="status">{data.forbiddenMessage ?? 'Organization Admin is unavailable for this selected context. Tenant Admin and Customer Admin contexts cannot gain SaaS Owner authority from browser state.'}</p>}
        {validationError && <p className="surface-state-inline validation-error" role="alert">{validationError}</p>}
        {data.lastResult && <p className={`surface-state-inline ${data.lastResult.status}`} role="status">{data.lastResult.message} {data.lastResult.correlationId ? `Correlation ${data.lastResult.correlationId}.` : ''}</p>}

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

        <div className="organization-admin-grid">
          <section className="user-admin-list-panel" aria-labelledby={`${envelope.surfaceId}-list-heading`}>
            <div className="surface-section-heading compact"><div><p className="eyebrow">{filteredOrganizations.length} visible</p><h4 id={`${envelope.surfaceId}-list-heading`}>Organization list</h4></div></div>
            {filteredOrganizations.length === 0 ? <p className="surface-empty-copy">{data.emptyMessage ?? 'No Organizations are visible for this safe filter.'}</p> : (
              <div className="user-admin-clean-list" role="list">
                {filteredOrganizations.map((organization) => (
                  <button key={organization.organizationId} type="button" role="listitem" className="user-admin-clean-row organization-admin-row" onClick={() => run('action-organization-read', { organizationId: organization.organizationId })}>
                    <span className="user-admin-person"><strong>{organization.organizationName}</strong><small>{organization.organizationId}</small></span>
                    <span>{organization.safeLifecycleSummary ?? 'Tenant boundary only'}</span>
                    <span className={`status-pill ${organization.status === 'active' ? 'success' : 'danger'}`}>{organization.status}</span>
                  </button>
                ))}
              </div>
            )}
          </section>

          <section className="user-admin-list-panel" aria-labelledby={`${envelope.surfaceId}-detail-heading`}>
            <div className="surface-section-heading compact"><div><p className="eyebrow">Safe detail</p><h4 id={`${envelope.surfaceId}-detail-heading`}>{selectedOrganization?.organizationName ?? 'Select an Organization'}</h4></div></div>
            <p className="surface-empty-copy">{detail?.safeBoundaryNotice ?? data.boundaryNotice ?? boundaryFallback}</p>
            {selectedOrganization && <dl className="organization-admin-detail"><div><dt>Status</dt><dd>{selectedOrganization.status}</dd></div><div><dt>Trace refs</dt><dd>{(selectedOrganization.traceRefs ?? detail?.traceRefs ?? []).join(' · ') || 'Trace available after backend action'}</dd></div></dl>}

            <form className="organization-admin-command-form" aria-label="Create Organization" onSubmit={submitCreate}>
              <h5>Create Organization</h5>
              <label>Name<input className="designed-control" value={createName} onChange={(event) => setCreateName(event.currentTarget.value)} required /></label>
              <label>Reason<input className="designed-control" value={reason} onChange={(event) => setReason(event.currentTarget.value)} placeholder="Reason for audit/work trace" /></label>
              <button className="surface-action-link primary" type="submit">Create Organization</button>
            </form>

            <form className="organization-admin-command-form" aria-label="Rename Organization" onSubmit={submitRename}>
              <h5>Rename selected Organization</h5>
              <label>New Organization name<input className="designed-control" value={renameName} onChange={(event) => setRenameName(event.currentTarget.value)} required /></label>
              <button className="surface-action-link secondary" type="submit">Rename</button>
            </form>

            <div className="organization-admin-lifecycle-actions" aria-label="Organization lifecycle actions">
              <button className="surface-action-link danger" type="button" onClick={() => lifecycle('action-organization-suspend')}>Suspend</button>
              <button className="surface-action-link secondary" type="button" onClick={() => lifecycle('action-organization-reactivate')}>Reactivate</button>
            </div>
          </section>
        </div>

        {data.redaction && <p className="redaction-note">Browser redaction: {renderValue(data.redaction)}. Hidden counts, provider secrets, billing details, support-access internals, and tenant application data are omitted.</p>}
      </section>
    </SurfaceStateFrame>
  );
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
