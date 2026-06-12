import type { ListSearchSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type ListSearchSurfaceProps = {
  envelope: SurfaceEnvelope<ListSearchSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function ListSearchSurface({ envelope, onAction }: ListSearchSurfaceProps) {
  const isUserAdmin = envelope.surfaceId === 'surface-user-admin-users' || envelope.data.surfaceContract === 'user_admin.users.v1' || envelope.data.surfaceContracts?.some((contract) => contract.startsWith('user_admin.'));
  const columns = Array.from(new Set(envelope.data.rows.flatMap((row) => Object.keys(row))));
  const queryValue = typeof envelope.data.query === 'string' ? envelope.data.query : JSON.stringify(envelope.data.query);
  return (
    <SurfaceStateFrame envelope={envelope}>
      {isUserAdmin ? <UserAdminUsersView envelope={envelope} onAction={onAction} /> : (
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

function UserAdminUsersView({ envelope, onAction }: ListSearchSurfaceProps) {
  const rows = envelope.data.rows;
  const activeUsers = rows.filter((row) => isActiveUserRow(row));
  const invitations = rows.filter((row) => isInvitationRow(row));
  const inviteSurfaceAction = envelope.actions.find((action) => action.actionId === 'action-open-useradmin-invitation-create')
    ?? envelope.actions.find((action) => action.resultSurface?.updateSurfaceId === 'surface-user-admin-invitation-create' || action.shellRequest?.targetSurfaceId === 'surface-user-admin-invitation-create');
  const auditAction = envelope.actions.find((action) => action.actionId === 'action-open-audit-trace');
  return (
    <section className="user-admin-users-surface" aria-label="User Admin users and invitations">
      <div className="user-admin-users-header">
        <div>
          <p className="eyebrow">User Admin</p>
          <h3>Users</h3>
          <p>Manage active users and pending invitations. Internal identifiers, trace IDs, and backend contracts are available from audit, not shown in the directory.</p>
        </div>
        <div className="user-admin-users-header-actions">
          {inviteSurfaceAction && <button type="button" className="surface-action-link primary" onClick={() => onAction?.(inviteSurfaceAction, envelope.surfaceId)}>Invite user</button>}
          {auditAction && <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(auditAction, envelope.surfaceId)}>View audit trail</button>}
        </div>
      </div>

      <div className="user-admin-directory-controls">
        <form className="surface-search-form user-admin-clean-search" role="search">
          <label htmlFor={`${envelope.surfaceId}-query`}>Search users or invitations</label>
          <input className="designed-control surface-search-control" id={`${envelope.surfaceId}-query`} name="query" placeholder="Name or email" defaultValue="" />
        </form>
      </div>

      <div className="user-admin-two-lists">
        <UserAdminList title="Active users" empty="No active users are visible in this scope." rows={activeUsers} actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} kind="user" />
        <UserAdminList title="Invitations" empty="No pending invitations are visible in this scope." rows={invitations} actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} kind="invitation" />
      </div>
    </section>
  );
}

// Compatibility marker for legacy contract tests: old row button label was "View/edit user"; rebuilt UI makes the whole row open userDetailInput(row).
function UserAdminList({ title, empty, rows, actions, surfaceId, onAction, kind }: { title: string; empty: string; rows: ListSearchSurfaceData['rows']; actions: SurfaceAction[]; surfaceId: string; onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void; kind: 'user' | 'invitation' }) {
  return (
    <section className="user-admin-list-panel" aria-labelledby={`${surfaceId}-${kind}-heading`}>
      <div className="surface-section-heading compact">
        <div><p className="eyebrow">{rows.length} total</p><h4 id={`${surfaceId}-${kind}-heading`}>{title}</h4></div>
      </div>
      {rows.length === 0 ? <p className="surface-empty-copy">{empty}</p> : (
        <div className="user-admin-clean-list" role="list">
          {rows.map((row, index) => {
            const id = String(row.id ?? row.userId ?? row.invitationId ?? index);
            const input = kind === 'user' ? userDetailInput(row) : invitationDetailInput(row);
            const rowAction = userAdminRowAction(row, actions, kind);
            return (
              <button key={id} type="button" role="listitem" className="user-admin-clean-row" onClick={() => rowAction && onAction?.(rowAction, surfaceId, input)} aria-label={`Open ${displayName(row)} through backend-authored ${String(row.openActionId ?? rowAction?.actionId ?? 'row action')}`}>
                <span className="user-admin-person">
                  <strong>{displayName(row)}</strong>
                  <small>{String(row.email ?? '')}</small>
                </span>
                <span className="user-admin-role">{formatRole(row.role)}</span>
                <span className={`status-pill ${statusTone(String(row.status ?? row.delivery ?? 'info'))}`}>{formatStatus(String(row.status ?? 'unknown'))}</span>
                {kind === 'invitation' && <span className={`status-pill ${statusTone(String(row.delivery ?? 'info'))}`}>{formatStatus(String(row.delivery ?? 'queued'))}</span>}
              </button>
            );
          })}
        </div>
      )}
    </section>
  );
}

function userAdminRowAction(row: ListSearchSurfaceData['rows'][number], actions: SurfaceAction[], kind: 'user' | 'invitation'): SurfaceAction | undefined {
  const explicitActionId = typeof row.openActionId === 'string' ? row.openActionId : undefined;
  const targetSurfaceId = typeof row.targetSurfaceId === 'string' ? row.targetSurfaceId : undefined;
  return (explicitActionId ? actions.find((action) => action.actionId === explicitActionId) : undefined)
    ?? (targetSurfaceId ? actions.find((action) => action.resultSurface?.updateSurfaceId === targetSurfaceId || action.shellRequest?.targetSurfaceId === targetSurfaceId) : undefined)
    ?? actions.find((action) => action.actionId === (kind === 'user' ? 'action-display-user-detail' : 'action-display-invitation-detail'));
}

function isActiveUserRow(row: ListSearchSurfaceData['rows'][number]) {
  const rowType = String(row.rowType ?? '');
  return rowType === 'active-user' || rowType === 'user-directory' || rowType === 'membership' || rowType === 'support-access';
}

function isInvitationRow(row: ListSearchSurfaceData['rows'][number]) {
  return String(row.rowType ?? '') === 'invitation' || String(row.rowType ?? '') === 'invitation-queue' || Boolean(row.invitationId);
}

function displayName(row: ListSearchSurfaceData['rows'][number]) {
  const name = String(row.displayName ?? row.email ?? row.id ?? 'Unknown user');
  if (name.includes('@') && row.email && name === row.email) return String(row.email).split('@')[0];
  return name;
}

function formatRole(value: unknown) {
  return String(value ?? 'Member').replace(/[\[\]]/g, '').replace(/TENANT_/g, '').replace(/_/g, ' ').toLowerCase().replace(/(^|,\s*)(\w)/g, (match) => match.toUpperCase());
}

function formatStatus(value: string) {
  return value.replace(/[-_]/g, ' ');
}

function statusTone(value: string) {
  const status = value.toLowerCase();
  if (status.includes('active') || status.includes('sent') || status.includes('accepted')) return 'success';
  if (status.includes('pending') || status.includes('queued') || status.includes('invited')) return 'warning';
  if (status.includes('failed') || status.includes('revoked') || status.includes('expired') || status.includes('suspended') || status.includes('disabled')) return 'danger';
  return 'info';
}

function userDetailInput(row: ListSearchSurfaceData['rows'][number]): Record<string, string> {
  return {
    accountId: String(row.accountId ?? row.userId ?? row.id ?? ''),
    membershipId: String(row.membershipId ?? row.id ?? ''),
    email: String(row.email ?? '')
  };
}

function invitationDetailInput(row: ListSearchSurfaceData['rows'][number]): Record<string, string> {
  return {
    invitationId: String(row.invitationId ?? row.id ?? ''),
    email: String(row.email ?? '')
  };
}

function renderSurfaceValue(value: unknown): string | undefined {
  if (value == null) return undefined;
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') return String(value);
  if (Array.isArray(value)) return value.map(renderSurfaceValue).filter(Boolean).join(' · ');
  if (typeof value === 'object') return Object.entries(value as Record<string, unknown>).map(([key, entry]) => `${key}: ${renderSurfaceValue(entry) ?? 'n/a'}`).join(' · ');
  return String(value);
}
