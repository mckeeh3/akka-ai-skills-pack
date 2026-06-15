import type { ListSearchSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type ListSearchSurfaceProps = {
  envelope: SurfaceEnvelope<ListSearchSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function ListSearchSurface({ envelope, onAction }: ListSearchSurfaceProps) {
  const isUserAdmin = envelope.surfaceId === 'surface-user-admin-users' || envelope.data.surfaceContract === 'user_admin.users.v1' || envelope.data.surfaceContracts?.some((contract) => contract.startsWith('user_admin.'));
  const isAgentAdminCatalog = envelope.surfaceId === 'surface-agent-admin-catalog' || envelope.data.surfaceContract === 'agent_admin.catalog.v1';
  const columns = Array.from(new Set(envelope.data.rows.flatMap((row) => Object.keys(row))));
  const queryValue = typeof envelope.data.query === 'string' ? envelope.data.query : JSON.stringify(envelope.data.query);
  return (
    <SurfaceStateFrame envelope={envelope}>
      {isAgentAdminCatalog ? <AgentAdminCatalogView envelope={envelope} onAction={onAction} /> : isAgentAdminSeedMaterial(envelope) ? <AgentAdminSeedMaterialView envelope={envelope} onAction={onAction} /> : isUserAdmin ? <UserAdminUsersView envelope={envelope} onAction={onAction} /> : (
        <>
          <form className="surface-search-form" role="search">
            <label htmlFor={`${envelope.surfaceId}-query`}>Search</label>
            <input className="designed-control surface-search-control" id={`${envelope.surfaceId}-query`} name="query" defaultValue={queryValue} />
          </form>
          {envelope.data.partial && <p className="surface-state-inline partial" role="status">Partial results: unauthorized or redacted evidence is omitted.</p>}
          {envelope.data.redaction && <p className="redaction-note">Redaction: {renderSurfaceValue(envelope.data.redaction)}</p>}
          {envelope.data.rows.length === 0 ? <p>{envelope.data.emptyMessage ?? 'No results match the current search.'}</p> : (
            <table>
              <caption>{envelope.title} results</caption>
              <thead><tr>{columns.map((column) => <th key={column} scope="col">{column}</th>)}</tr></thead>
              <tbody>{envelope.data.rows.map((row, index) => <tr key={String(row.id ?? row.userId ?? index)}>{columns.map((column) => <td key={column}>{String(row[column] ?? '')}</td>)}</tr>)}</tbody>
            </table>
          )}
          <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
        </>
      )}
    </SurfaceStateFrame>
  );
}


function AgentAdminSeedMaterialView({ envelope, onAction }: ListSearchSurfaceProps) {
  const rows = envelope.data.rows;
  const actionById = new Map(envelope.actions.map((action) => [action.actionId, action]));
  const refreshAction = actionById.get('action-list-agent-seed-material');
  const importAction = actionById.get('action-import-agent-seed-defaults');
  const traceAction = actionById.get('action-open-agent-trace');
  return (
    <section className="user-admin-users-surface agent-admin-seed-surface" aria-label="Agent Admin seed provenance">
      <div className="user-admin-users-header">
        <div>
          <p className="eyebrow">Agent Admin · seed provenance</p>
          <h3>Seed defaults and tenant customizations</h3>
          <p>Review starter defaults, import readiness, and tenant override preservation without exposing raw prompt, skill, reference, or provider-secret material.</p>
        </div>
        <div className="user-admin-users-header-actions">
          {refreshAction && <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(refreshAction, envelope.surfaceId, safeDirectoryInput(envelope))}>Refresh seed material</button>}
          {importAction && <button type="button" className="surface-action-link primary" onClick={() => onAction?.(importAction, envelope.surfaceId, safeDirectoryInput(envelope))}>Import missing defaults</button>}
          {traceAction && <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(traceAction, envelope.surfaceId)}>Open seed traces</button>}
        </div>
      </div>
      {envelope.data.partial && <p className="surface-state-inline partial" role="status">Partial seed state: unauthorized or redacted seed evidence is omitted.</p>}
      {rows.length === 0 ? <p className="surface-empty-copy">{envelope.data.emptyMessage ?? 'No seed material is visible in this selected scope.'}</p> : (
        <div className="user-admin-clean-list agent-admin-seed-list" role="list" aria-label="Seed material cards">
          {rows.map((row, index) => <article key={String(row.id ?? index)} role="listitem" className="user-admin-clean-row agent-admin-seed-row">
            <span className="user-admin-person"><strong>{seedMaterialTitle(row)}</strong><small>{seedMaterialSummary(row)}</small></span>
            <span className={`status-pill ${statusTone(String(row.status ?? 'ready'))}`}>{formatStatus(String(row.status ?? 'ready'))}</span>
            <span className="status-pill info">{row.tenantCustomized ? 'tenant customized' : 'starter default'}</span>
          </article>)}
        </div>
      )}
      <details className="dashboard-evidence-drawer">
        <summary>Role-gated seed diagnostics</summary>
        <p>Surface contract: {envelope.data.surfaceContract ?? 'agent_admin.seed_material.v1'}</p>
        <p>Redaction: {renderSurfaceValue(envelope.data.redaction) ?? 'raw behavior material and provider secrets omitted'}</p>
        <ul>{rows.map((row, index) => <li key={String(row.id ?? index)}>{renderSurfaceValue({ artifactId: row.artifactId, seedBundleId: row.seedBundleId, checksum: row.checksum, traceId: row.traceId })}</li>)}</ul>
      </details>
    </section>
  );
}

function isAgentAdminSeedMaterial(envelope: SurfaceEnvelope<ListSearchSurfaceData>) {
  return envelope.surfaceId === 'surface-agent-seed-material' || envelope.data.surfaceContract === 'agent_admin.seed_material.v1';
}

function seedMaterialTitle(row: ListSearchSurfaceData['rows'][number]) {
  return String(row.artifactKind ?? 'Seed artifact').replace(/_/g, ' ');
}

function seedMaterialSummary(row: ListSearchSurfaceData['rows'][number]) {
  const customized = row.tenantCustomized ? 'Tenant customization is preserved' : 'Starter default is active';
  return `${customized}. ${String(row.seedBundleId ?? 'Seed bundle')} provenance is trace-linked; raw content is available only through governed loaders.`;
}

function AgentAdminCatalogView({ envelope, onAction }: ListSearchSurfaceProps) {
  const rows = envelope.data.rows;
  const actionById = new Map(envelope.actions.map((action) => [action.actionId, action]));
  const refreshAction = actionById.get('action-display-agent-catalog');
  const traceAction = actionById.get('action-open-agent-trace');
  return (
    <section className="user-admin-users-surface agent-admin-catalog-surface" aria-label="Agent Admin managed agent catalog">
      <div className="user-admin-users-header">
        <div>
          <p className="eyebrow">Agent Admin · managed agent catalog</p>
          <h3>Managed agents</h3>
          <p>Each card opens backend-authored readiness inspection. The catalog never mutates agent lifecycle, prompts, tools, models, or seeds inline.</p>
        </div>
        <div className="user-admin-users-header-actions">
          {refreshAction && <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(refreshAction, envelope.surfaceId, safeDirectoryInput(envelope))}>Refresh catalog</button>}
          {traceAction && <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(traceAction, envelope.surfaceId)}>Open traces</button>}
        </div>
      </div>
      <form className="surface-search-form user-admin-clean-search" role="search" onSubmit={(event) => { event.preventDefault(); const query = new FormData(event.currentTarget).get('query'); if (refreshAction) onAction?.(refreshAction, envelope.surfaceId, stringRecord({ ...safeDirectoryInput(envelope), query: typeof query === 'string' ? query : '' })); }}>
        <label htmlFor={`${envelope.surfaceId}-query`}>Search managed agents</label>
        <input className="designed-control surface-search-control" id={`${envelope.surfaceId}-query`} name="query" defaultValue={typeof envelope.data.query === 'string' ? envelope.data.query : ''} />
        <button type="submit" className="surface-action-link secondary" disabled={!refreshAction}>Search</button>
      </form>
      {envelope.data.partial && <p className="surface-state-inline partial" role="status">Partial results: unauthorized or redacted agent evidence is omitted.</p>}
      {envelope.data.redaction && <details className="dashboard-evidence-drawer"><summary>Catalog redaction and diagnostics</summary><p>{renderSurfaceValue(envelope.data.redaction)}</p></details>}
      {rows.length === 0 ? <p className="surface-empty-copy">{envelope.data.emptyMessage ?? 'No managed agents are visible in this scope.'}</p> : (
        <div className="user-admin-clean-list agent-admin-catalog-list" role="list" aria-label="Managed AgentDefinition cards">
          {rows.map((row, index) => <AgentAdminCatalogRow key={String(row.id ?? index)} row={row} actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />)}
        </div>
      )}
      <details className="dashboard-evidence-drawer"><summary>Role-gated catalog diagnostics</summary><p>Surface contract: {envelope.data.surfaceContract ?? 'agent_admin.catalog.v1'}</p><p>Trace links: {renderSurfaceValue((envelope.data as { traceLinks?: unknown }).traceLinks) ?? 'none'}</p></details>
    </section>
  );
}

function AgentAdminCatalogRow({ row, actions, surfaceId, onAction }: { row: ListSearchSurfaceData['rows'][number]; actions: SurfaceAction[]; surfaceId: string; onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void }) {
  const action = userAdminRowAction(row, actions);
  const label = String(row.displayName ?? row.id ?? 'Managed agent');
  const status = String(row.providerStatus ?? row.status ?? 'ready');
  return (
    <button type="button" role="listitem" className="user-admin-clean-row agent-admin-agent-row" disabled={!action || Boolean(action.disabled)} onClick={() => action && onAction?.(action, surfaceId, backendRowInput(row))} aria-label={`Open readiness inspection for ${label}`}>
      <span className="user-admin-person"><strong>{label}</strong><small>{String(row.readinessSummary ?? 'Backend-authored readiness summary')}</small></span>
      <span className="user-admin-role">{formatRole(row.authorityLevel ?? row.rowType)}</span>
      <span className={`status-pill ${statusTone(status)}`}>{formatStatus(status)}</span>
      <span className="status-pill info">{String(row.attentionSummary ?? row.seedStatus ?? 'No attention summary')}</span>
    </button>
  );
}

// Legacy contract marker: previous broad row control copy was "View/edit user" and helper userDetailInput(row);
// current rows open backend-authored inspection/task surfaces only.
function UserAdminUsersView({ envelope, onAction }: ListSearchSurfaceProps) {
  const rows = envelope.data.rows;
  const inviteSurfaceAction = envelope.actions.find((action) => action.actionId === 'action-open-useradmin-invitation-create')
    ?? envelope.actions.find((action) => action.resultSurface?.updateSurfaceId === 'surface-user-admin-invitation-create' || action.shellRequest?.targetSurfaceId === 'surface-user-admin-invitation-create');
  const auditAction = envelope.actions.find((action) => action.actionId === 'action-open-audit-trace');
  const refreshAction = envelope.actions.find((action) => action.actionId === 'action-user-admin-show-users') ?? envelope.actions.find((action) => action.actionId === 'action-display-user-list');
  return (
    <section className="user-admin-users-surface" aria-label="User Admin users, invitations, support, review, and identity queues">
      <div className="user-admin-users-header">
        <div>
          <p className="eyebrow">User Admin · backend-routed directory</p>
          <h3>Users and access objects</h3>
          <p>Every row opens the backend-declared target surface. The browser does not infer authority, row routing, hidden counts, role choices, or lifecycle eligibility from labels or status.</p>
        </div>
        <div className="user-admin-users-header-actions">
          {inviteSurfaceAction && <button type="button" className="surface-action-link primary" onClick={() => onAction?.(inviteSurfaceAction, envelope.surfaceId)}>Invite user</button>}
          {refreshAction && <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(refreshAction, envelope.surfaceId, safeDirectoryInput(envelope))}>Refresh directory</button>}
          {auditAction && <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(auditAction, envelope.surfaceId)}>View audit trail</button>}
        </div>
      </div>

      <form className="surface-search-form user-admin-clean-search" role="search" onSubmit={(event) => { event.preventDefault(); const query = new FormData(event.currentTarget).get('query'); if (refreshAction && !refreshAction.disabled) onAction?.(refreshAction, envelope.surfaceId, stringRecord({ ...safeDirectoryInput(envelope), query: typeof query === 'string' ? query : '' })); }}>
        <label htmlFor={`${envelope.surfaceId}-query`}>Search users, invitations, or access objects</label>
        <input className="designed-control surface-search-control" id={`${envelope.surfaceId}-query`} name="query" placeholder="Search visible users and invitations in this selected scope" defaultValue={typeof envelope.data.query === 'string' ? envelope.data.query : ''} />
        <button type="submit" className="surface-action-link secondary" disabled={!refreshAction || Boolean(refreshAction.disabled)}>Search</button>
        {refreshAction?.disabled && <p className="form-error">{refreshAction.disabled.message}</p>}
      </form>

      {envelope.data.partial && <p className="surface-state-inline partial" role="status">Partial results: unauthorized or redacted evidence is omitted.</p>}
      {envelope.data.redaction && <p className="redaction-note">Redaction: {renderSurfaceValue(envelope.data.redaction)}</p>}

      <section className="user-admin-list-panel" aria-labelledby={`${envelope.surfaceId}-backend-routed-heading`}>
        <div className="surface-section-heading compact">
          <div><p className="eyebrow">{rows.length} visible</p><h4 id={`${envelope.surfaceId}-backend-routed-heading`}>Backend-authored rows</h4></div>
          <p>Rows may open user detail, invitation detail, role preview, access-review task, identity-exception review, support-access task, audit evidence, or a safe system message.</p>
        </div>
        {rows.length === 0 ? <p className="surface-empty-copy">{envelope.data.emptyMessage ?? 'No access-administration rows are visible in this scope.'}</p> : (
          <div className="user-admin-clean-list" role="list">
            {rows.map((row, index) => <UserAdminDirectoryRow key={String(row.id ?? row.userId ?? row.invitationId ?? index)} row={row} actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />)}
          </div>
        )}
      </section>
    </section>
  );
}

function UserAdminDirectoryRow({ row, actions, surfaceId, onAction }: { row: ListSearchSurfaceData['rows'][number]; actions: SurfaceAction[]; surfaceId: string; onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void }) {
  const rowAction = userAdminRowAction(row, actions);
  const disabledReason = !rowAction ? 'Backend did not provide an authorized row action for this object.' : rowAction.disabled?.message;
  const status = String(row.status ?? row.delivery ?? row.redactionState ?? 'ready');
  const label = displayName(row);
  const targetSurfaceId = String(row.targetSurfaceId ?? 'system-message');
  return (
    <button type="button" role="listitem" className="user-admin-clean-row" disabled={!rowAction || Boolean(rowAction.disabled)} onClick={() => rowAction && onAction?.(rowAction, surfaceId, backendRowInput(row))} aria-label={`Open ${label} through backend-authored ${String(row.openActionId ?? rowAction?.actionId ?? 'missing row action')} to ${targetSurfaceId}`}>
      <span className="user-admin-person">
        <strong>{label}</strong>
        <small>{String(row.email ?? row.targetObjectType ?? row.rowType ?? '')}</small>
      </span>
      <span className="user-admin-role">{formatRole(row.role ?? row.targetObjectType ?? row.rowType)}</span>
      <span className={`status-pill ${statusTone(status)}`}>{formatStatus(status)}</span>
      <span className="status-pill info">{targetSurfaceId.replace('surface-user-admin-', '').replace(/-/g, ' ')}</span>
      {disabledReason && <span className="form-error denied-reason">{disabledReason}</span>}
    </button>
  );
}

function userAdminRowAction(row: ListSearchSurfaceData['rows'][number], actions: SurfaceAction[]): SurfaceAction | undefined {
  const explicitActionId = typeof row.openActionId === 'string' ? row.openActionId : undefined;
  const targetSurfaceId = typeof row.targetSurfaceId === 'string' ? row.targetSurfaceId : undefined;
  if (explicitActionId) return actions.find((action) => action.actionId === explicitActionId);
  if (targetSurfaceId) return actions.find((action) => action.resultSurface?.updateSurfaceId === targetSurfaceId || action.shellRequest?.targetSurfaceId === targetSurfaceId);
  return undefined;
}

function backendRowInput(row: ListSearchSurfaceData['rows'][number]): Record<string, string> {
  const activation = typeof row.activation === 'object' && row.activation ? row.activation as Record<string, unknown> : undefined;
  const safeActionContext = typeof row.safeActionContext === 'object' && row.safeActionContext ? row.safeActionContext as Record<string, unknown> : undefined;
  const activationContext = typeof activation?.safeActionContext === 'object' && activation.safeActionContext ? activation.safeActionContext as Record<string, unknown> : undefined;
  return stringRecord({ ...safeActionContext, ...activationContext, accountId: row.accountId ?? row.userId, membershipId: row.membershipId, invitationId: row.invitationId, targetObjectType: row.targetObjectType, targetSurfaceId: row.targetSurfaceId });
}

function safeDirectoryInput(envelope: SurfaceEnvelope<ListSearchSurfaceData>): Record<string, string> {
  const filters = typeof envelope.data.filters === 'object' && envelope.data.filters ? envelope.data.filters as Record<string, unknown> : {};
  return stringRecord({ query: typeof envelope.data.query === 'string' ? envelope.data.query : '', ...filters, ...(envelope.data.filterState ?? {}) });
}

function displayName(row: ListSearchSurfaceData['rows'][number]) {
  const name = String(row.displayName ?? row.email ?? row.id ?? row.targetObjectType ?? 'Access object');
  if (name.includes('@') && row.email && name === row.email) return String(row.email).split('@')[0];
  return name;
}

function formatRole(value: unknown) {
  return String(value ?? 'Access object').replace(/[\[\]]/g, '').replace(/TENANT_/g, '').replace(/_/g, ' ').toLowerCase().replace(/(^|,\s*)(\w)/g, (match) => match.toUpperCase());
}

function formatStatus(value: string) {
  return value.replace(/[-_]/g, ' ');
}

function statusTone(value: string) {
  const status = value.toLowerCase();
  if (status.includes('active') || status.includes('sent') || status.includes('accepted') || status.includes('ready')) return 'success';
  if (status.includes('pending') || status.includes('queued') || status.includes('invited') || status.includes('review')) return 'warning';
  if (status.includes('failed') || status.includes('revoked') || status.includes('expired') || status.includes('suspended') || status.includes('disabled') || status.includes('forbidden') || status.includes('hidden')) return 'danger';
  return 'info';
}

function stringRecord(value: Record<string, unknown>): Record<string, string> {
  return Object.fromEntries(Object.entries(value).filter(([, entry]) => entry !== undefined && entry !== null).map(([key, entry]) => [key, String(entry)]));
}

function renderSurfaceValue(value: unknown): string | undefined {
  if (value == null) return undefined;
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') return String(value);
  if (Array.isArray(value)) return value.map(renderSurfaceValue).filter(Boolean).join(' · ');
  if (typeof value === 'object') return Object.entries(value as Record<string, unknown>).map(([key, entry]) => `${key}: ${renderSurfaceValue(entry) ?? 'n/a'}`).join(' · ');
  return String(value);
}
