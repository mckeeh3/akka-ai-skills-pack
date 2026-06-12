import { useMemo, useState, type FormEvent } from 'react';
import type { SurfaceAction, SurfaceEnvelope, UserAdminBranchNavigation } from '../types';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type UserAdminTaskSurfaceData = Record<string, unknown> & {
  surfaceContract?: string;
  branchNavigation?: UserAdminBranchNavigation;
  branchRootSurfaceId?: string;
  branchReturnActionId?: string;
  branchReturnLabel?: string;
  safeFilterPreservation?: string;
  recordId?: string;
  recordLabel?: string;
  recordKind?: string;
  status?: string;
  summary?: string;
  currentStatus?: string;
  proposedStatus?: string;
  consequenceCopy?: string;
  confirmationCopy?: string;
  reasonRequired?: boolean;
  purposeRequired?: boolean;
  confirmationRequired?: boolean;
  noDirectMutation?: boolean;
  idempotencyKeyHint?: string;
  validationMessages?: string[];
  traceRefs?: string[];
  correlationId?: string;
  redaction?: unknown;
  actionContext?: Record<string, string>;
  draft?: Record<string, unknown>;
  targetScope?: Record<string, unknown>;
};

type Props = {
  envelope: SurfaceEnvelope<UserAdminTaskSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

const requiredUserTaskSurfaceIds = new Set([
  'surface-user-admin-invitation-create',
  'surface-user-admin-invitation-resend-confirmation',
  'surface-user-admin-invitation-revoke-confirmation',
  'surface-user-admin-membership-status-confirmation',
  'surface-user-admin-support-access-grant',
  'surface-user-admin-support-access-revoke-confirmation',
  'surface-user-admin-identity-exception-review'
]);

const requiredUserTaskContracts = new Set([
  'user_admin.invitation_create.v1',
  'user_admin.invitation_resend_confirmation.v1',
  'user_admin.invitation_revoke_confirmation.v1',
  'user_admin.membership_status_confirmation.v1',
  'user_admin.support_access_grant.v1',
  'user_admin.support_access_revoke_confirmation.v1',
  'user_admin.identity_exception_review.v1'
]);

export function UserAdminTaskSurface({ envelope, onAction }: Props) {
  const data = envelope.data;
  const isInvitationCreate = matches(envelope, 'surface-user-admin-invitation-create', 'user_admin.invitation_create.v1');
  const isInvitationResend = matches(envelope, 'surface-user-admin-invitation-resend-confirmation', 'user_admin.invitation_resend_confirmation.v1');
  const isInvitationRevoke = matches(envelope, 'surface-user-admin-invitation-revoke-confirmation', 'user_admin.invitation_revoke_confirmation.v1');
  const isMembershipStatus = matches(envelope, 'surface-user-admin-membership-status-confirmation', 'user_admin.membership_status_confirmation.v1');
  const isSupportGrant = matches(envelope, 'surface-user-admin-support-access-grant', 'user_admin.support_access_grant.v1');
  const isSupportRevoke = matches(envelope, 'surface-user-admin-support-access-revoke-confirmation', 'user_admin.support_access_revoke_confirmation.v1');
  const isIdentityException = matches(envelope, 'surface-user-admin-identity-exception-review', 'user_admin.identity_exception_review.v1');

  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="user-admin-task-surface" aria-labelledby={`${envelope.surfaceId}-heading`}>
        <UserAdminTaskHeader envelope={envelope} />
        <UserAdminTaskState envelope={envelope} />
        {isInvitationCreate && <InvitationCreateTask envelope={envelope} onAction={onAction} />}
        {isInvitationResend && <InvitationConfirmationTask envelope={envelope} onAction={onAction} actionId="action-useradmin-resend-invitation" verb="Resend invitation" />}
        {isInvitationRevoke && <InvitationConfirmationTask envelope={envelope} onAction={onAction} actionId="action-useradmin-revoke-invitation" verb="Revoke invitation" requireReason />}
        {isMembershipStatus && <MembershipStatusTask envelope={envelope} onAction={onAction} />}
        {isSupportGrant && <SupportAccessGrantTask envelope={envelope} onAction={onAction} />}
        {isSupportRevoke && <SupportAccessRevokeTask envelope={envelope} onAction={onAction} />}
        {isIdentityException && <IdentityExceptionReview envelope={envelope} onAction={onAction} />}
        <TraceAndRedaction data={data} />
        <UserAdminBranchReturn envelope={envelope} onAction={onAction} />
      </section>
    </SurfaceStateFrame>
  );
}

export function isUserAdminTaskSurface(envelope: SurfaceEnvelope<unknown>) {
  const data = envelope.data as { surfaceContract?: string } | undefined;
  return requiredUserTaskSurfaceIds.has(envelope.surfaceId) || requiredUserTaskContracts.has(data?.surfaceContract ?? '');
}

function UserAdminTaskHeader({ envelope }: { envelope: SurfaceEnvelope<UserAdminTaskSurfaceData> }) {
  return (
    <div className="user-admin-task-header">
      <div>
        <p className="eyebrow">User Admin task surface</p>
        <h3 id={`${envelope.surfaceId}-heading`}>{envelope.title}</h3>
        <p>{envelope.data.summary ?? taskPurpose(envelope)}</p>
      </div>
      <div className="user-admin-task-authority" aria-label="Backend authority">
        <strong>{envelope.data.surfaceContract ?? envelope.surfaceId}</strong>
        <span>Selected AuthContext, backend authorization, idempotency, and audit/work traces remain authoritative.</span>
      </div>
    </div>
  );
}

function UserAdminTaskState({ envelope }: { envelope: SurfaceEnvelope<UserAdminTaskSurfaceData> }) {
  const status = String(envelope.data.status ?? 'ready');
  return (
    <section className="user-admin-task-state" aria-label="Task state and target">
      <dl>
        <div><dt>Status</dt><dd>{formatStatus(status)}</dd></div>
        <div><dt>Target</dt><dd>{String(envelope.data.recordLabel ?? envelope.data.recordId ?? 'selected scope')}</dd></div>
        <div><dt>Correlation</dt><dd>{envelope.data.correlationId ?? envelope.correlationId}</dd></div>
        <div><dt>Idempotency</dt><dd>{String(envelope.data.idempotencyKeyHint ?? 'backend-required when commanded')}</dd></div>
      </dl>
      {envelope.data.validationMessages && envelope.data.validationMessages.length > 0 && <ul className="form-error" aria-label="Server validation messages">{envelope.data.validationMessages.map((message) => <li key={message}>{message}</li>)}</ul>}
    </section>
  );
}

function InvitationCreateTask({ envelope, onAction }: Props) {
  const action = findAction(envelope.actions, 'action-invite-user');
  const [email, setEmail] = useState(String(envelope.data.draft?.email ?? ''));
  const [displayName, setDisplayName] = useState(String(envelope.data.draft?.displayName ?? ''));
  const [role, setRole] = useState(firstRole(envelope.data.draft?.roles));
  const [error, setError] = useState<string>();
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!email.trim() || !email.includes('@')) return setError('Enter a valid email address before creating an invitation.');
    setError(undefined);
    runAction(envelope, onAction, action, { email: email.trim(), displayName: displayName.trim(), roles: role, idempotencyKey: idempotencyKey('invite', email), ...stringRecord(envelope.data.actionContext) });
  }
  return (
    <form className="user-admin-task-form" aria-label="Create invitation" onSubmit={submit}>
      <h4>Create scoped invitation</h4>
      {error && <p className="surface-state-inline validation-error" role="alert">{error}</p>}
      <p className="surface-empty-copy">This form captures browser-safe invitation inputs only. Tokens, provider payloads, raw JWTs, and full email bodies are never rendered.</p>
      <label>Email<input className="designed-control" type="email" value={email} onChange={(event) => setEmail(event.currentTarget.value)} required /></label>
      <label>Display name<input className="designed-control" value={displayName} onChange={(event) => setDisplayName(event.currentTarget.value)} /></label>
      <label>Requested role<select className="designed-control" value={role} onChange={(event) => setRole(event.currentTarget.value)}><option value="TENANT_EMPLOYEE">Employee</option><option value="TENANT_ADMIN">Tenant admin</option><option value="AUDITOR">Auditor</option></select></label>
      <button className="surface-action-link primary" type="submit" disabled={!action || Boolean(action.disabled)}>Create invitation</button>
      {action?.disabled && <p className="form-error">{action.disabled.message}</p>}
      <ScopeSummary scope={envelope.data.targetScope} />
    </form>
  );
}

function InvitationConfirmationTask({ envelope, onAction, actionId, verb, requireReason = false }: Props & { actionId: string; verb: string; requireReason?: boolean }) {
  const action = findAction(envelope.actions, actionId);
  const [reason, setReason] = useState('');
  const [error, setError] = useState<string>();
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if ((requireReason || envelope.data.reasonRequired) && !reason.trim()) return setError('Reason is required for this invitation lifecycle action.');
    setError(undefined);
    runAction(envelope, onAction, action, { ...stringRecord(envelope.data.actionContext), reason: reason.trim(), idempotencyKey: idempotencyKey(actionId, String(envelope.data.recordId ?? envelope.correlationId)) });
  }
  return (
    <form className="user-admin-task-form" aria-label={verb} onSubmit={submit}>
      <h4>{verb}</h4>
      {error && <p className="surface-state-inline validation-error" role="alert">{error}</p>}
      <p>{String(envelope.data.confirmationCopy ?? envelope.data.consequenceCopy ?? 'Backend authorization decides whether this invitation lifecycle action can proceed.')}</p>
      <label>Reason<textarea className="designed-control" value={reason} onChange={(event) => setReason(event.currentTarget.value)} required={requireReason || Boolean(envelope.data.reasonRequired)} /></label>
      <button className={actionId.includes('revoke') ? 'surface-action-link danger' : 'surface-action-link secondary'} type="submit" disabled={!action || Boolean(action.disabled)}>{verb}</button>
      {action?.disabled && <p className="form-error">{action.disabled.message}</p>}
    </form>
  );
}

function MembershipStatusTask({ envelope, onAction }: Props) {
  const suspendAction = findAction(envelope.actions, 'action-useradmin-disable-member') ?? findAction(envelope.actions, 'action-disable-account');
  const reactivateAction = findAction(envelope.actions, 'action-useradmin-reactivate-member') ?? findAction(envelope.actions, 'action-reactivate-account');
  const [status, setStatus] = useState(String(envelope.data.proposedStatus ?? 'removed'));
  const [reason, setReason] = useState('');
  const [error, setError] = useState<string>();
  const action = status.toLowerCase().includes('active') ? reactivateAction : suspendAction;
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!reason.trim()) return setError('Reason is required for membership/account lifecycle confirmation.');
    setError(undefined);
    runAction(envelope, onAction, action, { ...stringRecord(envelope.data.actionContext), status, reason: reason.trim(), idempotencyKey: idempotencyKey('membership-status', String(envelope.data.recordId ?? envelope.correlationId)) });
  }
  return (
    <form className="user-admin-task-form" aria-label="Membership status confirmation" onSubmit={submit}>
      <h4>Confirm membership/account lifecycle change</h4>
      {error && <p className="surface-state-inline validation-error" role="alert">{error}</p>}
      <p>{String(envelope.data.consequenceCopy ?? 'Lifecycle changes enforce self-action, last-admin, scope, idempotency, and audit guardrails on the backend.')}</p>
      <label>Proposed status<select className="designed-control" value={status} onChange={(event) => setStatus(event.currentTarget.value)}><option value="removed">Disable/remove</option><option value="active">Reactivate</option></select></label>
      <label>Reason<textarea className="designed-control" value={reason} onChange={(event) => setReason(event.currentTarget.value)} required /></label>
      <button className={status === 'active' ? 'surface-action-link secondary' : 'surface-action-link danger'} type="submit" disabled={!action || Boolean(action.disabled)}>Confirm status change</button>
      {action?.disabled && <p className="form-error">{action.disabled.message}</p>}
      <p className="capability-basis">Current status: {String(envelope.data.currentStatus ?? 'not reported')} · confirmation required: {envelope.data.confirmationRequired ? 'yes' : 'backend policy'}</p>
    </form>
  );
}

function SupportAccessGrantTask({ envelope, onAction }: Props) {
  const grantAction = findAction(envelope.actions, 'action-grant-support-access') ?? findAction(envelope.actions, 'action-extend-support-access');
  const [purpose, setPurpose] = useState('');
  const [expiryHours, setExpiryHours] = useState('24');
  const [error, setError] = useState<string>();
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!purpose.trim()) return setError('Purpose is required for support-access grant or extension.');
    setError(undefined);
    runAction(envelope, onAction, grantAction, { ...stringRecord(envelope.data.actionContext), purpose: purpose.trim(), expiryHours, idempotencyKey: idempotencyKey('support-grant', String(envelope.data.recordId ?? envelope.correlationId)) });
  }
  return (
    <form className="user-admin-task-form" aria-label="Grant support access" onSubmit={submit}>
      <h4>Grant or extend support access</h4>
      {error && <p className="surface-state-inline validation-error" role="alert">{error}</p>}
      <p>{String(envelope.data.summary ?? 'Grant/extend time-boxed support access through backend policy and audit.')}</p>
      <label>Purpose<textarea className="designed-control" value={purpose} onChange={(event) => setPurpose(event.currentTarget.value)} required /></label>
      <label>Expiry<select className="designed-control" value={expiryHours} onChange={(event) => setExpiryHours(event.currentTarget.value)}><option value="4">4 hours</option><option value="24">24 hours</option><option value="72">72 hours</option></select></label>
      <button className="surface-action-link primary" type="submit" disabled={!grantAction || Boolean(grantAction.disabled)}>Request support access grant</button>
      {grantAction?.disabled && <p className="form-error">{grantAction.disabled.message}</p>}
    </form>
  );
}

function SupportAccessRevokeTask({ envelope, onAction }: Props) {
  const action = findAction(envelope.actions, 'action-revoke-support-access');
  const [reason, setReason] = useState('');
  const [error, setError] = useState<string>();
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!reason.trim()) return setError('Reason is required to revoke support access.');
    setError(undefined);
    runAction(envelope, onAction, action, { ...stringRecord(envelope.data.actionContext), reason: reason.trim(), idempotencyKey: idempotencyKey('support-revoke', String(envelope.data.recordId ?? envelope.correlationId)) });
  }
  return (
    <form className="user-admin-task-form" aria-label="Revoke support access" onSubmit={submit}>
      <h4>Revoke support access</h4>
      {error && <p className="surface-state-inline validation-error" role="alert">{error}</p>}
      <p>{String(envelope.data.consequenceCopy ?? 'Revoking support access removes the time-boxed support grant only.')}</p>
      <label>Reason<textarea className="designed-control" value={reason} onChange={(event) => setReason(event.currentTarget.value)} required /></label>
      <button className="surface-action-link danger" type="submit" disabled={!action || Boolean(action.disabled)}>Revoke support access</button>
      {action?.disabled && <p className="form-error">{action.disabled.message}</p>}
    </form>
  );
}

function IdentityExceptionReview({ envelope, onAction }: Props) {
  const detailAction = findAction(envelope.actions, 'action-display-user-detail');
  return (
    <section className="user-admin-task-decision" aria-label="Identity exception review">
      <h4>Identity exception review</h4>
      <p>{String(envelope.data.summary ?? 'Review identity-link/relink evidence without exposing provider internals.')}</p>
      <dl>
        <div><dt>Risk</dt><dd>{String(envelope.data.risk ?? 'provider-boundary')}</dd></div>
        <div><dt>Direct mutation</dt><dd>{envelope.data.noDirectMutation ? 'Not allowed' : 'Not reported'}</dd></div>
        <div><dt>Provider boundary</dt><dd>{String(envelope.data.providerBoundary ?? 'Provider identifiers and payloads are redacted.')}</dd></div>
      </dl>
      <p className="surface-state-inline forbidden">No direct mutation: recovery must route to deterministic backend approval/status flows or safe user detail.</p>
      {detailAction && <button type="button" className="surface-action-link secondary" disabled={Boolean(detailAction.disabled)} onClick={() => runAction(envelope, onAction, detailAction, stringRecord(envelope.data.actionContext))}>Open user detail</button>}
    </section>
  );
}

function TraceAndRedaction({ data }: { data: UserAdminTaskSurfaceData }) {
  const traceRefs = data.traceRefs ?? data.branchNavigation?.traceRefs ?? [];
  return (
    <section className="user-admin-task-evidence" aria-label="Trace and redaction evidence">
      {traceRefs.length > 0 && <div className="trace-link-list" aria-label="User Admin task trace links">{traceRefs.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-timeline#${encodeURIComponent(traceId)}`}>{traceId}</a>)}</div>}
      {data.redaction !== undefined && <p className="redaction-note">Browser redaction: {renderValue(data.redaction)}. Hidden users, cross-scope facts, raw JWTs, invitation tokens, provider payloads, and secrets are omitted.</p>}
    </section>
  );
}

function ScopeSummary({ scope }: { scope: Record<string, unknown> | undefined }) {
  if (!scope) return null;
  return <p className="capability-basis">Target scope: {renderValue(scope)}</p>;
}

function UserAdminBranchReturn({ envelope, onAction }: Props) {
  const action = findAction(envelope.actions, 'action-user-admin-show-users') ?? findAction(envelope.actions, 'action-display-user-list');
  if (!action) return null;
  const branch = envelope.data.branchNavigation;
  return (
    <nav className="user-admin-branch-return" aria-label="User Admin branch navigation">
      <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(action, envelope.surfaceId, {
        branchRootSurfaceId: branch?.branchRootSurfaceId ?? envelope.data.branchRootSurfaceId ?? 'surface-user-admin-users',
        branchReturnActionId: branch?.branchReturnActionId ?? envelope.data.branchReturnActionId ?? 'action-user-admin-show-users',
        safeFilterPreservation: branch?.safeFilterPreservation ?? envelope.data.safeFilterPreservation ?? 'backend-authored-only',
        correlationId: branch?.correlationId ?? envelope.correlationId
      })}>{branch?.branchReturnLabel ?? envelope.data.branchReturnLabel ?? action.label}</button>
      <p className="capability-basis">{branch?.capabilityId ?? action.capabilityId} · safe filters: {branch?.safeFilterPreservation ?? envelope.data.safeFilterPreservation ?? 'backend-authored-only'}</p>
    </nav>
  );
}

function matches(envelope: SurfaceEnvelope<UserAdminTaskSurfaceData>, surfaceId: string, surfaceContract: string) {
  return envelope.surfaceId === surfaceId || envelope.data.surfaceContract === surfaceContract;
}

function findAction(actions: SurfaceAction[], actionId: string) {
  return actions.find((action) => action.actionId === actionId);
}

function runAction(envelope: SurfaceEnvelope<UserAdminTaskSurfaceData>, onAction: Props['onAction'], action: SurfaceAction | undefined, input: Record<string, string>) {
  if (!action || action.disabled) return;
  onAction?.(action, envelope.surfaceId, input);
}

function stringRecord(value: unknown): Record<string, string> {
  if (!value || typeof value !== 'object') return {};
  return Object.fromEntries(Object.entries(value as Record<string, unknown>).map(([key, entry]) => [key, String(entry ?? '')]));
}

function firstRole(value: unknown) {
  if (Array.isArray(value) && value.length > 0) return String(value[0]);
  if (typeof value === 'string' && value) return value;
  return 'TENANT_EMPLOYEE';
}

function idempotencyKey(prefix: string, seed: string) {
  return `ui-${prefix}-${seed.trim().toLowerCase().replace(/[^a-z0-9]+/g, '-') || Date.now()}`;
}

function taskPurpose(envelope: SurfaceEnvelope<UserAdminTaskSurfaceData>) {
  if (envelope.surfaceId.includes('invitation-create')) return 'Create one scoped invitation and let the backend own validation, outbox, provider, audit, and result routing.';
  if (envelope.surfaceId.includes('invitation-resend')) return 'Confirm invitation resend through backend outbox/provider policy.';
  if (envelope.surfaceId.includes('invitation-revoke')) return 'Confirm invitation revocation without changing existing accounts or memberships.';
  if (envelope.surfaceId.includes('membership-status')) return 'Confirm a membership/account lifecycle change with last-admin and self-action guardrails.';
  if (envelope.surfaceId.includes('support-access-grant')) return 'Grant or extend time-boxed support access through a governed backend path.';
  if (envelope.surfaceId.includes('support-access-revoke')) return 'Revoke time-boxed support access without changing ordinary membership or roles.';
  return 'Review User Admin exception evidence without exposing provider internals or performing direct mutation.';
}

function formatStatus(value: string) {
  return value.replace(/[-_]/g, ' ');
}

function renderValue(value: unknown): string {
  if (value == null) return 'none';
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') return String(value);
  if (Array.isArray(value)) return value.map(renderValue).join(' · ');
  return Object.entries(value as Record<string, unknown>).map(([key, entry]) => `${key}: ${renderValue(entry)}`).join(' · ');
}

export type { UserAdminTaskSurfaceData };
