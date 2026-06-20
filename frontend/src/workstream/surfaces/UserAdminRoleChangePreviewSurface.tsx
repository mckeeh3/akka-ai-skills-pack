import type { DetailEditSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type Props = {
  envelope: SurfaceEnvelope<DetailEditSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function isUserAdminRoleChangePreviewSurface(envelope: SurfaceEnvelope<unknown>) {
  const data = envelope.data as { surfaceContract?: string } | undefined;
  return envelope.surfaceId === 'surface-user-admin-role-change-preview' || data?.surfaceContract === 'user_admin.role_change_preview.v1';
}

export function UserAdminRoleChangePreviewSurface({ envelope, onAction }: Props) {
  const data = envelope.data;
  const branch = data.branchNavigation;
  const returnAction = envelope.actions.find((action) => action.actionId === 'action-user-admin-show-users')
    ?? envelope.actions.find((action) => action.actionId === 'action-display-user-list');
  const taskActions = envelope.actions.filter((action) => action !== returnAction);
  const delta = data.capabilityDelta ?? {};

  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="user-admin-task-surface role-change-preview" aria-labelledby={`${envelope.surfaceId}-heading`}>
        <div className="user-admin-task-header">
          <div>
            <p className="eyebrow">User Admin · role review</p>
            <h3 id={`${envelope.surfaceId}-heading`}>{envelope.title}</h3>
            <p>{data.message ?? 'Review the role and capability impact before any backend-authorized role change is submitted.'}</p>
          </div>
          <span className={`status-pill ${statusTone(data.status)}`}>{formatStatus(data.status ?? 'ready')}</span>
        </div>

        <section className="access-management-card role-change-preview" aria-label="Role and capability delta">
          <div className="surface-section-heading compact">
            <div><p className="eyebrow">Decision evidence</p><h4>Capability delta</h4></div>
            <p>Commit actions are backend-authorized, idempotent, audit-traced, and may return approval, no-op, denial, or refreshed user detail views.</p>
          </div>
          <dl>
            <div><dt>Added capabilities</dt><dd>{listOrNone(delta.added)}</dd></div>
            <div><dt>Removed capabilities</dt><dd>{listOrNone(delta.removed)}</dd></div>
            <div><dt>Unchanged capabilities</dt><dd>{listOrNone(delta.unchanged)}</dd></div>
            <div><dt>Affected workstreams</dt><dd>{listOrNone(data.affectedWorkstreams)}</dd></div>
            <div><dt>Last-admin impact</dt><dd>{data.lastAdminImpact ?? 'No impact reported by backend policy.'}</dd></div>
          </dl>
          {(data.policyHints ?? []).length > 0 && <ul aria-label="Policy hints">{data.policyHints!.map((hint) => <li key={hint}>{hint}</li>)}</ul>}
        </section>

        {(data.traceLinks ?? []).length > 0 && (
          <details className="dashboard-evidence-drawer">
            <summary>Audit evidence</summary>
            <section className="trace-link-list" aria-label="Role-change preview trace links">
              {data.traceLinks!.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-trace-timeline#${encodeURIComponent(traceId)}`}>Open trace evidence</a>)}
            </section>
          </details>
        )}

        {taskActions.length > 0 && <SurfaceActionBar actions={taskActions} surfaceId={envelope.surfaceId} onAction={onAction} />}
        {returnAction && (
          <nav className="user-admin-branch-return" aria-label="User Admin branch navigation">
            <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(returnAction, envelope.surfaceId, {
              branchRootSurfaceId: branch?.branchRootSurfaceId ?? data.branchRootSurfaceId ?? 'surface-user-admin-users',
              branchReturnActionId: branch?.branchReturnActionId ?? data.branchReturnActionId ?? 'action-user-admin-show-users',
              safeFilterPreservation: branch?.safeFilterPreservation ?? data.safeFilterPreservation ?? 'backend-authored-only',
              correlationId: branch?.correlationId ?? envelope.correlationId
            })}>{branch?.branchReturnLabel ?? data.branchReturnLabel ?? returnAction.label}</button>
            <p className="capability-basis">Return uses backend-authored filters and server-side authorization.</p>
          </nav>
        )}
      </section>
    </SurfaceStateFrame>
  );
}

function listOrNone(values?: string[]) {
  return values && values.length > 0 ? values.join(', ') : 'none';
}

function formatStatus(value: string) {
  return value.replace(/[-_]/g, ' ');
}

function statusTone(value?: string) {
  if (value === 'denied') return 'danger';
  if (value === 'no-op') return 'info';
  return 'warning';
}
