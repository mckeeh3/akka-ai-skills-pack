import { useState, type FormEvent } from 'react';
import type { SurfaceAction, SurfaceEnvelope, UserAdminBranchNavigation } from '../types';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type ScopedAdminData = Record<string, unknown> & {
  surfaceContract?: string;
  branchNavigation?: UserAdminBranchNavigation;
  branchRootSurfaceId?: string;
  branchReturnActionId?: string;
  branchReturnLabel?: string;
  safeFilterPreservation?: string;
  summary?: string;
  boundaryNotice?: string;
  safeBoundaryNotice?: string;
  emptyMessage?: string;
  redaction?: unknown;
  rows?: Array<Record<string, unknown>>;
  customers?: Array<Record<string, unknown>>;
  filters?: Record<string, unknown>;
  fields?: Array<{ fieldId: string; label: string; value: string; editable?: boolean }>;
  draft?: Record<string, unknown>;
  roleOptions?: Array<{ value?: string; roleId?: string; id?: string; label?: string; name?: string }>;
  allowedRoleOptions?: Array<{ value?: string; roleId?: string; id?: string; label?: string; name?: string }>;
  policyOptions?: { roles?: Array<{ value?: string; roleId?: string; id?: string; label?: string; name?: string }> };
  query?: string;
  status?: string;
  recordId?: string;
  recordLabel?: string;
  recordKind?: string;
  tenantId?: string;
  organizationId?: string;
  organizationName?: string;
  reasonRequired?: boolean;
  confirmationRequired?: boolean;
  traceRefs?: string[];
  correlationId?: string;
};

type Props = {
  envelope: SurfaceEnvelope<ScopedAdminData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

const scopedAdminContracts = new Set([
  'user_admin.saas_owner_admins.v1',
  'user_admin.saas_owner_admin_invitation_create.v1',
  'user_admin.organization_admins.v1',
  'user_admin.organization_admin_invitation_create.v1',
  'user_admin.organization_admin_detail.v1',
  'user_admin.customer_directory.v1',
  'user_admin.customer_detail.v1',
  'user_admin.customer_create.v1',
  'user_admin.customer_rename.v1',
  'user_admin.customer_suspend_confirmation.v1',
  'user_admin.customer_reactivate_confirmation.v1',
  'user_admin.customer_admins.v1',
  'user_admin.customer_admin_invitation_create.v1',
  'user_admin.customer_admin_detail.v1'
]);

export function isUserAdminScopedAdminSurface(envelope: SurfaceEnvelope<unknown>) {
  const data = envelope.data as { surfaceContract?: string } | undefined;
  return scopedAdminContracts.has(data?.surfaceContract ?? '')
    || envelope.surfaceId === 'surface-user-admin-saas-owner-admins'
    || envelope.surfaceId === 'surface-user-admin-saas-owner-admin-invitation-create'
    || envelope.surfaceId.startsWith('surface-user-admin-customer-')
    || envelope.surfaceId === 'surface-user-admin-organization-admins'
    || envelope.surfaceId === 'surface-user-admin-organization-admin-invitation-create'
    || envelope.surfaceId === 'surface-user-admin-organization-admin-detail';
}

export function UserAdminScopedAdminSurface({ envelope, onAction }: Props) {
  const contract = envelope.data.surfaceContract ?? '';
  const isInvite = contract.includes('invitation_create') || envelope.surfaceId.includes('invitation-create');
  const isCustomerTask = contract === 'user_admin.customer_create.v1'
    || contract === 'user_admin.customer_rename.v1'
    || contract === 'user_admin.customer_suspend_confirmation.v1'
    || contract === 'user_admin.customer_reactivate_confirmation.v1';
  const isDetail = envelope.surfaceType === 'show-inspection' || contract.endsWith('_detail.v1');

  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="user-admin-task-surface scoped-admin-surface" aria-labelledby={`${envelope.surfaceId}-heading`}>
        <ScopedHeader envelope={envelope} />
        {isInvite ? <RoleScopedInvitationForm envelope={envelope} onAction={onAction} />
          : isCustomerTask ? <CustomerTaskForm envelope={envelope} onAction={onAction} />
          : isDetail ? <ScopedInspection envelope={envelope} onAction={onAction} />
          : <ScopedDirectory envelope={envelope} onAction={onAction} />}
        <BranchReturn envelope={envelope} onAction={onAction} />
        {Boolean(envelope.data.redaction) && <p className="redaction-note">Browser redaction: {renderValue(envelope.data.redaction)}. Hidden counts, provider payloads, raw invitation tokens, secrets, and cross-scope evidence are omitted.</p>}
      </section>
    </SurfaceStateFrame>
  );
}

function ScopedHeader({ envelope }: { envelope: SurfaceEnvelope<ScopedAdminData> }) {
  return (
    <div className="user-admin-task-header">
      <div>
        <p className="eyebrow">User Admin · {scopeLabel(envelope)}</p>
        <h3 id={`${envelope.surfaceId}-heading`}>{envelope.title}</h3>
        <p>{String(envelope.data.summary ?? envelope.data.boundaryNotice ?? envelope.data.safeBoundaryNotice ?? 'Backend-authored scope, capability, target, redaction, and audit policy govern this surface.')}</p>
      </div>
    </div>
  );
}

function ScopedDirectory({ envelope, onAction }: Props) {
  const rows = directoryRows(envelope);
  const refreshAction = branchReturnAction(envelope.actions) ?? envelope.actions.find((action) => action.intent === 'read');
  const createAction = envelope.actions.find((action) => action.actionId.includes('open') && (action.actionId.includes('Create') || action.actionId.includes('create') || action.actionId.includes('invitation')))
    ?? envelope.actions.find((action) => action.resultSurface?.updateSurfaceId?.includes('create'));
  return (
    <section className="user-admin-list-panel" aria-label="Scoped User Admin directory">
      <div className="surface-section-heading compact">
        <div><p className="eyebrow">{rows.length} visible</p><h4>Backend-authored directory</h4></div>
        <p>Rows and task entry points are backend-authored. The browser does not infer hidden targets, role eligibility, or authority from labels.</p>
      </div>
      <div className="user-admin-users-header-actions">
        {refreshAction && <button type="button" className="surface-action-link secondary" disabled={Boolean(refreshAction.disabled)} onClick={() => onAction?.(refreshAction, envelope.surfaceId, branchReturnInput(envelope))}>Refresh / return</button>}
        {createAction && <button type="button" className="surface-action-link primary" disabled={Boolean(createAction.disabled)} onClick={() => onAction?.(createAction, envelope.surfaceId, branchReturnInput(envelope))}>{createAction.label}</button>}
      </div>
      {rows.length === 0 ? <p className="surface-empty-copy">{String(envelope.data.emptyMessage ?? 'No records are visible for this selected target yet.')}</p> : (
        <div className="user-admin-clean-list" role="list">
          {rows.map((row, index) => <ScopedDirectoryRow key={String(row.id ?? row.customerId ?? row.accountId ?? index)} row={row} actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />)}
        </div>
      )}
    </section>
  );
}

function ScopedDirectoryRow({ row, actions, surfaceId, onAction }: { row: Record<string, unknown>; actions: SurfaceAction[]; surfaceId: string; onAction?: Props['onAction'] }) {
  const action = rowAction(row, actions);
  const label = String(row.displayName ?? row.customerName ?? row.organizationName ?? row.email ?? row.id ?? row.customerId ?? 'Scoped record');
  const status = String(row.status ?? row.deliveryStatus ?? row.redactionState ?? 'ready');
  return (
    <button type="button" role="listitem" className="user-admin-clean-row" disabled={!action || Boolean(action.disabled)} onClick={() => action && onAction?.(action, surfaceId, rowInput(row))} aria-label={`Open ${label} through backend-authored action`}>
      <span className="user-admin-person"><strong>{label}</strong><small>{String(row.email ?? row.customerId ?? row.accountId ?? row.membershipId ?? row.targetObjectType ?? '')}</small></span>
      <span className="user-admin-role">{humanize(String(row.role ?? row.rowType ?? row.targetObjectType ?? 'scoped object'))}</span>
      <span className={`status-pill ${statusTone(status)}`}>{humanize(status)}</span>
      <span className="status-pill info">{String(row.targetSurfaceId ?? 'backend target')}</span>
    </button>
  );
}

function RoleScopedInvitationForm({ envelope, onAction }: Props) {
  const action = envelope.actions.find((candidate) => candidate.actionId === 'action-submit-saas-owner-admin-invitation')
    ?? envelope.actions.find((candidate) => candidate.actionId === 'action-customer-admin-invite')
    ?? envelope.actions.find((candidate) => candidate.actionId === 'action-invite-user')
    ?? envelope.actions.find((candidate) => candidate.intent === 'command');
  const [email, setEmail] = useState(String(envelope.data.draft?.email ?? ''));
  const [displayName, setDisplayName] = useState(String(envelope.data.draft?.displayName ?? ''));
  const roleOptions = roleOptionsFor(envelope);
  const [role, setRole] = useState(roleOptions[0]?.value ?? '');
  const [reason, setReason] = useState('');
  const [error, setError] = useState<string>();
  const requiresOrganizationTarget = envelope.data.surfaceContract === 'user_admin.organization_admin_invitation_create.v1';
  const hasOrganizationTarget = Boolean(String(envelope.data.tenantId ?? envelope.data.organizationId ?? '').trim());
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (requiresOrganizationTarget && !hasOrganizationTarget) return setError('Select an Organization first so the backend can include tenant-scope proof for the Organization Admin invitation.');
    if (!email.trim() || !email.includes('@')) return setError('Enter a valid email address before creating an invitation.');
    if (!role) return setError('Backend did not provide an authorized role option for this target scope.');
    setError(undefined);
    onAction?.(action!, envelope.surfaceId, { ...branchReturnInput(envelope), email: email.trim(), displayName: displayName.trim(), roles: role, reason: reason.trim(), idempotencyKey: idempotencyKey('role-invite', email) });
  }
  return (
    <form className="user-admin-task-form" aria-label="Role-scoped invitation create" onSubmit={submit}>
      <h4>Create scoped invitation</h4>
      {(envelope.data.recordLabel || envelope.data.organizationName) && <p className="capability-basis">Target: {String(envelope.data.recordLabel ?? envelope.data.organizationName)} · backend-authored scope proof included</p>}
      {error && <p className="surface-state-inline validation-error" role="alert">{error}</p>}
      <label>Email<input className="designed-control" type="email" value={email} onChange={(event) => setEmail(event.currentTarget.value)} required /></label>
      <label>Display name<input className="designed-control" value={displayName} onChange={(event) => setDisplayName(event.currentTarget.value)} /></label>
      <label>Requested role<select className="designed-control" value={role} onChange={(event) => setRole(event.currentTarget.value)} disabled={roleOptions.length === 0}>{roleOptions.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}</select></label>
      <label>Reason<input className="designed-control" value={reason} onChange={(event) => setReason(event.currentTarget.value)} placeholder="Reason for audit/work trace" /></label>
      {roleOptions.length === 0 && <p className="form-error">No backend-authorized role option is available for this selected scope.</p>}
      {requiresOrganizationTarget && !hasOrganizationTarget && <p className="form-error">Open this form from an Organization detail row before creating the invitation.</p>}
      <button className="surface-action-link primary" type="submit" disabled={!action || Boolean(action.disabled) || roleOptions.length === 0 || (requiresOrganizationTarget && !hasOrganizationTarget)}>Create invitation</button>
      {action?.disabled && <p className="form-error">{action.disabled.message}</p>}
      <p className="capability-basis">Provider/outbox failures return a typed system message. Raw tokens, email bodies, provider payloads, and secrets are never shown.</p>
    </form>
  );
}

function CustomerTaskForm({ envelope, onAction }: Props) {
  const isRename = envelope.data.surfaceContract === 'user_admin.customer_rename.v1';
  const isSuspend = envelope.data.surfaceContract === 'user_admin.customer_suspend_confirmation.v1';
  const isReactivate = envelope.data.surfaceContract === 'user_admin.customer_reactivate_confirmation.v1';
  const actionId = isRename ? 'action-customer-rename' : isSuspend ? 'action-customer-suspend' : isReactivate ? 'action-customer-reactivate' : 'action-customer-create';
  const action = envelope.actions.find((candidate) => candidate.actionId === actionId) ?? envelope.actions.find((candidate) => candidate.intent === 'command');
  const [customerName, setCustomerName] = useState(String(envelope.data.draft?.customerName ?? envelope.data.recordLabel ?? ''));
  const [reason, setReason] = useState(String(envelope.data.draft?.reason ?? ''));
  const [confirmation, setConfirmation] = useState('');
  const [error, setError] = useState<string>();
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!isSuspend && !isReactivate && !customerName.trim()) return setError('Customer name is required.');
    if ((isSuspend || envelope.data.reasonRequired) && !reason.trim()) return setError('Reason is required for this Customer lifecycle action.');
    if ((isSuspend || isReactivate || envelope.data.confirmationRequired) && !confirmation.trim()) return setError('Type a confirmation before continuing.');
    setError(undefined);
    onAction?.(action!, envelope.surfaceId, { ...branchReturnInput(envelope), customerName: customerName.trim(), reason: reason.trim(), confirmation: confirmation.trim(), idempotencyKey: idempotencyKey(actionId, customerName || envelope.surfaceId) });
  }
  return (
    <form className="user-admin-task-form" aria-label="Customer lifecycle task" onSubmit={submit}>
      <h4>{envelope.title}</h4>
      {error && <p className="surface-state-inline validation-error" role="alert">{error}</p>}
      <p>{String(envelope.data.summary ?? 'Customer lifecycle work is tenant-scoped, backend-authorized, idempotent, and audit traced.')}</p>
      {!isSuspend && !isReactivate && <label>Customer name<input className="designed-control" value={customerName} onChange={(event) => setCustomerName(event.currentTarget.value)} required /></label>}
      <label>Reason<input className="designed-control" value={reason} onChange={(event) => setReason(event.currentTarget.value)} required={isSuspend || Boolean(envelope.data.reasonRequired)} /></label>
      {(isSuspend || isReactivate || envelope.data.confirmationRequired) && <label>Confirmation<input className="designed-control" value={confirmation} onChange={(event) => setConfirmation(event.currentTarget.value)} placeholder="Confirm this Customer lifecycle action" required /></label>}
      <button className={`surface-action-link ${isSuspend ? 'danger' : 'primary'}`} type="submit" disabled={!action || Boolean(action.disabled)}>{action?.label ?? envelope.title}</button>
      {action?.disabled && <p className="form-error">{action.disabled.message}</p>}
    </form>
  );
}

function ScopedInspection({ envelope, onAction }: Props) {
  const taskActions = envelope.actions.filter((action) => !isBranchReturnAction(envelope, action));
  return (
    <section className="user-admin-list-panel" aria-label="Scoped inspection">
      <div className="surface-section-heading compact"><div><p className="eyebrow">Read-only inspection</p><h4>{String(envelope.data.recordLabel ?? envelope.title)}</h4></div><p>Consequential work opens dedicated task, confirmation, decision, workflow, or system-message surfaces. This inspection does not mutate access inline.</p></div>
      {envelope.data.fields && envelope.data.fields.length > 0 ? <div className="user-admin-readable-fields">{envelope.data.fields.map((field) => <p key={field.fieldId}><span>{field.label}</span><strong>{field.value}</strong></p>)}</div> : <p className="surface-empty-copy">{String(envelope.data.summary ?? 'No additional browser-safe fields were provided for this selected target.')}</p>}
      {taskActions.length > 0 && <div className="surface-action-row" aria-label="Scoped inspection task actions">{taskActions.map((action) => <button key={action.actionId} type="button" className="surface-action-link secondary" disabled={Boolean(action.disabled)} onClick={() => onAction?.(action, envelope.surfaceId, branchReturnInput(envelope))}>{action.label}</button>)}</div>}
    </section>
  );
}

function BranchReturn({ envelope, onAction }: Props) {
  const action = branchReturnAction(envelope.actions);
  if (!action) return null;
  return (
    <nav className="user-admin-branch-return" aria-label="Scoped User Admin branch navigation">
      <button type="button" className="surface-action-link secondary" disabled={Boolean(action.disabled)} onClick={() => onAction?.(action, envelope.surfaceId, branchReturnInput(envelope))}>{branchReturnLabel(envelope, action)}</button>
      <p className="capability-basis">{envelope.data.branchNavigation?.capabilityId ?? action.capabilityId} · safe filters: {envelope.data.branchNavigation?.safeFilterPreservation ?? envelope.data.safeFilterPreservation ?? 'backend-authored-only'}</p>
    </nav>
  );
}

function directoryRows(envelope: SurfaceEnvelope<ScopedAdminData>): Array<Record<string, unknown>> {
  if (Array.isArray(envelope.data.rows)) return envelope.data.rows;
  if (Array.isArray(envelope.data.customers)) return envelope.data.customers;
  return [];
}

function roleOptionsFor(envelope: SurfaceEnvelope<ScopedAdminData>): Array<{ value: string; label: string }> {
  const raw = envelope.data.roleOptions ?? envelope.data.allowedRoleOptions ?? envelope.data.policyOptions?.roles ?? [];
  return raw.map((option) => {
    const value = String(option.value ?? option.roleId ?? option.id ?? '');
    return { value, label: String(option.label ?? option.name ?? value.replace(/_/g, ' ')) };
  }).filter((option) => option.value && option.label);
}

function rowAction(row: Record<string, unknown>, actions: SurfaceAction[]): SurfaceAction | undefined {
  const explicit = typeof row.openActionId === 'string' ? row.openActionId : undefined;
  const target = typeof row.targetSurfaceId === 'string' ? row.targetSurfaceId : undefined;
  if (explicit) return actions.find((action) => action.actionId === explicit);
  if (target) return actions.find((action) => action.resultSurface?.updateSurfaceId === target || action.shellRequest?.targetSurfaceId === target);
  return actions.find((action) => action.intent === 'read' && !action.actionId.includes('show-'));
}

function rowInput(row: Record<string, unknown>): Record<string, string> {
  return stringRecord({ accountId: row.accountId, membershipId: row.membershipId, invitationId: row.invitationId, customerId: row.customerId, organizationId: row.organizationId, targetObjectType: row.targetObjectType, targetSurfaceId: row.targetSurfaceId, ...(typeof row.safeActionContext === 'object' && row.safeActionContext ? row.safeActionContext as Record<string, unknown> : {}) });
}

function branchReturnAction(actions: SurfaceAction[]): SurfaceAction | undefined {
  return actions.find((action) => action.actionId === 'action-user-admin-show-saas-owner-admins')
    ?? actions.find((action) => action.actionId === 'action-user-admin-show-organizations')
    ?? actions.find((action) => action.actionId === 'action-user-admin-show-customers')
    ?? actions.find((action) => action.actionId === 'action-user-admin-show-users')
    ?? actions.find((action) => action.actionId === 'action-display-user-list');
}

function isBranchReturnAction(envelope: SurfaceEnvelope<ScopedAdminData>, action: SurfaceAction) {
  return action.actionId === envelope.data.branchNavigation?.branchReturnActionId
    || action.actionId === envelope.data.branchReturnActionId
    || action === branchReturnAction(envelope.actions);
}

function branchReturnLabel(envelope: SurfaceEnvelope<ScopedAdminData>, action: SurfaceAction) {
  return envelope.data.branchNavigation?.branchReturnLabel ?? envelope.data.branchReturnLabel ?? action.label;
}

function branchReturnInput(envelope: SurfaceEnvelope<ScopedAdminData>): Record<string, string> {
  const branch = envelope.data.branchNavigation;
  return {
    branchRootSurfaceId: branch?.branchRootSurfaceId ?? envelope.data.branchRootSurfaceId ?? '',
    branchReturnActionId: branch?.branchReturnActionId ?? envelope.data.branchReturnActionId ?? '',
    safeFilterPreservation: branch?.safeFilterPreservation ?? envelope.data.safeFilterPreservation ?? 'backend-authored-only',
    correlationId: branch?.correlationId ?? envelope.data.correlationId ?? envelope.correlationId,
    query: String(envelope.data.query ?? (typeof envelope.data.filters === 'object' && envelope.data.filters ? (envelope.data.filters as Record<string, unknown>).query : '') ?? ''),
    status: String(envelope.data.status ?? (typeof envelope.data.filters === 'object' && envelope.data.filters ? (envelope.data.filters as Record<string, unknown>).status : '') ?? ''),
    recordId: String(envelope.data.recordId ?? ''),
    tenantId: String(envelope.data.tenantId ?? ''),
    organizationId: String(envelope.data.organizationId ?? envelope.data.tenantId ?? ''),
    organizationName: String(envelope.data.organizationName ?? envelope.data.recordLabel ?? ''),
    customerId: String(envelope.data.customerId ?? (typeof envelope.data.customerDetail === 'object' && envelope.data.customerDetail ? (envelope.data.customerDetail as Record<string, unknown>).customerId : '') ?? ''),
    customerName: String(envelope.data.customerName ?? (typeof envelope.data.customerDetail === 'object' && envelope.data.customerDetail ? (envelope.data.customerDetail as Record<string, unknown>).customerName : '') ?? '')
  };
}

function scopeLabel(envelope: SurfaceEnvelope<ScopedAdminData>) {
  const contract = envelope.data.surfaceContract ?? envelope.surfaceId;
  if (contract.includes('saas_owner')) return 'SaaS Owner Admin';
  if (contract.includes('organization_admin')) return 'Organization Admin';
  if (contract.includes('customer_admin')) return 'Customer Admin';
  if (contract.includes('customer')) return 'Customer';
  return 'scoped admin';
}

function idempotencyKey(prefix: string, seed: string) {
  const normalized = seed.trim().toLowerCase().replace(/[^a-z0-9]+/g, '-') || String(Date.now());
  return `ui-${prefix}-${normalized}-${Date.now().toString(36)}`;
}

function stringRecord(value: Record<string, unknown>): Record<string, string> {
  return Object.fromEntries(Object.entries(value).filter(([, entry]) => entry !== undefined && entry !== null).map(([key, entry]) => [key, String(entry)]));
}

function humanize(value: string) {
  return value.replace(/[-_]/g, ' ').toLowerCase().replace(/(^|\s)\w/g, (letter) => letter.toUpperCase());
}

function statusTone(value: string) {
  const status = value.toLowerCase();
  if (status.includes('active') || status.includes('ready') || status.includes('sent') || status.includes('accepted')) return 'success';
  if (status.includes('pending') || status.includes('review') || status.includes('invited')) return 'warning';
  if (status.includes('suspend') || status.includes('disabled') || status.includes('failed') || status.includes('revoked') || status.includes('hidden') || status.includes('forbidden')) return 'danger';
  return 'info';
}

function renderValue(value: unknown): string {
  if (value == null) return 'none';
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') return String(value);
  if (Array.isArray(value)) return value.map(renderValue).join(' · ');
  return Object.entries(value as Record<string, unknown>).map(([key, entry]) => `${key}: ${renderValue(entry)}`).join(' · ');
}
