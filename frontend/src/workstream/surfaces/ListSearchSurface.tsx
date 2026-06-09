import type { ListSearchSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type ListSearchSurfaceProps = {
  envelope: SurfaceEnvelope<ListSearchSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function ListSearchSurface({ envelope, onAction }: ListSearchSurfaceProps) {
  const isUserAdmin = envelope.surfaceId === 'surface-user-admin-member-directory' || envelope.surfaceId === 'surface-user-admin-invitation-panel' || envelope.data.surfaceContract === 'user_admin.member_directory.v1' || envelope.data.surfaceContract === 'user_admin.invitation_panel.v1' || envelope.data.surfaceContracts?.some((contract) => contract.startsWith('user_admin.'));
  const columns = Array.from(new Set(envelope.data.rows.flatMap((row) => Object.keys(row))));
  const queryValue = typeof envelope.data.query === 'string' ? envelope.data.query : JSON.stringify(envelope.data.query);
  return (
    <SurfaceStateFrame envelope={envelope}>
      {isUserAdmin && <UserAdminListHeader envelope={envelope} />}
      <form className="surface-search-form" role="search">
        <label htmlFor={`${envelope.surfaceId}-query`}>Search</label>
        <input className="designed-control surface-search-control" id={`${envelope.surfaceId}-query`} name="query" defaultValue={queryValue} />
      </form>
      {envelope.data.partial && <p className="surface-state-inline partial" role="status">Partial results: unauthorized or redacted evidence is omitted.</p>}
      {envelope.data.redaction && <p className="redaction-note">Redaction: {renderSurfaceValue(envelope.data.redaction)}</p>}
      {isUserAdmin && envelope.data.capabilityIds && <p className="capability-basis">Capability-backed surface: {envelope.data.capabilityIds.join(', ')}</p>}
      {envelope.data.rows.length === 0 ? <p>{envelope.data.emptyMessage ?? 'No results match the current search.'}</p> : isUserAdmin ? <UserAdminResponsiveRows rows={envelope.data.rows} actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} /> : (
        <table>
          <caption>{envelope.title} results</caption>
          <thead><tr>{columns.map((column) => <th key={column} scope="col">{column}</th>)}</tr></thead>
          <tbody>{envelope.data.rows.map((row, index) => <tr key={String(row.id ?? row.userId ?? index)}>{columns.map((column) => <td key={column}>{String(row[column] ?? '')}</td>)}</tr>)}</tbody>
        </table>
      )}
      {isUserAdmin && envelope.data.systemStates && <p className="sr-only">User Admin states: {envelope.data.systemStates.join(', ')}</p>}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

function UserAdminListHeader({ envelope }: { envelope: SurfaceEnvelope<ListSearchSurfaceData> }) {
  const contracts = envelope.data.surfaceContracts ?? (envelope.data.surfaceContract ? [envelope.data.surfaceContract] : []);
  return (
    <section className="user-admin-list-hero" aria-label="User Admin scoped list authority">
      <div>
        <p className="eyebrow">Scoped access list</p>
        <h4>{envelope.surfaceId === 'surface-user-admin-invitation-panel' ? 'Invitation lifecycle and delivery visibility' : 'Member directory, support access, review flags, and audit excerpts'}</h4>
        <p>Rows are produced by backend views using selected AuthContext. Filters and action visibility are never security boundaries.</p>
      </div>
      {contracts.length > 0 && <p className="form-status">Contracts: {contracts.join(', ')}</p>}
      {envelope.data.mobileFallback && <p className="form-status">Responsive fallback: {envelope.data.mobileFallback}</p>}
    </section>
  );
}

function UserAdminResponsiveRows({ rows, actions, surfaceId, onAction }: { rows: ListSearchSurfaceData['rows']; actions: SurfaceAction[]; surfaceId: string; onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void }) {
  const detailAction = actions.find((action) => action.actionId === 'action-display-user-detail');
  return (
    <div className="user-admin-row-grid" aria-label="User Admin scoped rows with table-to-card fallback">
      {rows.map((row, index) => {
        const id = String(row.id ?? row.userId ?? row.invitationId ?? index);
        return (
          <article key={id} className={`surface-row-card user-admin-row ${String(row.rowType ?? 'record')}`}>
            <div>
              <p className="eyebrow">{String(row.rowType ?? 'record').replace(/[-_]/g, ' ')}</p>
              <h4>{String(row.displayName ?? row.email ?? row.label ?? id)}</h4>
              {row.email && <p>{String(row.email)}</p>}
            </div>
            <dl>
              {Object.entries(row).filter(([key]) => !['id', 'userId', 'invitationId', 'displayName', 'email'].includes(key)).slice(0, 8).map(([key, value]) => <div key={key}><dt>{key}</dt><dd>{String(value ?? '')}</dd></div>)}
            </dl>
            <div className="surface-row-actions">
              {detailAction && isUserRow(row) && <button className="surface-action-link" type="button" onClick={() => onAction?.(detailAction, surfaceId, userDetailInput(row))}>View/edit user</button>}
              {row.traceId && <a className="surface-action-link secondary" href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(String(row.traceId))}`}>Open trace</a>}
            </div>
          </article>
        );
      })}
    </div>
  );
}

function isUserRow(row: ListSearchSurfaceData['rows'][number]) {
  const rowType = String(row.rowType ?? '');
  return rowType === 'user-directory' || rowType === 'membership' || rowType === 'support-access';
}

function userDetailInput(row: ListSearchSurfaceData['rows'][number]): Record<string, string> {
  return {
    accountId: String(row.accountId ?? row.userId ?? row.id ?? ''),
    membershipId: String(row.membershipId ?? row.id ?? ''),
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
