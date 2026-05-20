import React from 'react';
import type { AdminUser, ApiClient, ApiError } from '../../api';
import { Button, Card, DataState, SelectField, StatusPill, TextArea, TextInput } from '../../design-system';

type RemoteData<T> =
  | { status: 'loading' }
  | { status: 'empty' }
  | { status: 'ready'; value: T }
  | { status: 'error'; error: ApiError };

type InviteForm = { email: string; displayName: string; roleId: string; message: string };
type RoleForm = { roleId: string; reason: string; confirmation: boolean };
type InviteErrors = Partial<Record<keyof InviteForm, string>>;
type RoleErrors = Partial<Record<keyof RoleForm, string>>;

const roleOptions = ['supervisor', 'reviewer', 'auditor', 'policy-owner', 'tenant-admin'];
const initialInvite: InviteForm = { email: '', displayName: '', roleId: '', message: '' };
const initialRole: RoleForm = { roleId: 'reviewer', reason: '', confirmation: false };

export function AdminUsersPage({ apiClient }: { apiClient: ApiClient }) {
  const [usersState, setUsersState] = React.useState<RemoteData<AdminUser[]>>({ status: 'loading' });
  const [selectedUserId, setSelectedUserId] = React.useState<string>();
  const [invite, setInvite] = React.useState<InviteForm>(initialInvite);
  const [inviteErrors, setInviteErrors] = React.useState<InviteErrors>({});
  const [roleForm, setRoleForm] = React.useState<RoleForm>(initialRole);
  const [roleErrors, setRoleErrors] = React.useState<RoleErrors>({});
  const [submittingInvite, setSubmittingInvite] = React.useState(false);
  const [submittingRole, setSubmittingRole] = React.useState(false);
  const [message, setMessage] = React.useState<string>();
  const emailRef = React.useRef<HTMLInputElement>(null);
  const roleSelectRef = React.useRef<HTMLSelectElement>(null);
  const reasonRef = React.useRef<HTMLTextAreaElement>(null);
  const confirmationRef = React.useRef<HTMLInputElement>(null);

  const loadUsers = React.useCallback(async () => {
    setUsersState({ status: 'loading' });
    const result = await apiClient.admin.listUsers();
    if (!result.ok) {
      setUsersState({ status: 'error', error: result.error });
      return;
    }
    if (result.value.items.length === 0) {
      setUsersState({ status: 'empty' });
      return;
    }
    setUsersState({ status: 'ready', value: result.value.items });
    setSelectedUserId((current) => current ?? result.value.items[0]?.userId);
  }, [apiClient]);

  React.useEffect(() => {
    void loadUsers();
  }, [loadUsers]);

  const selectedUser = usersState.status === 'ready' ? usersState.value.find((user) => user.userId === selectedUserId) : undefined;

  async function submitInvite(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setMessage(undefined);
    const clientErrors = validateInvite(invite);
    setInviteErrors(clientErrors);
    if (clientErrors.email) emailRef.current?.focus();
    else if (clientErrors.roleId) roleSelectRef.current?.focus();
    if (Object.keys(clientErrors).length > 0) return;

    setSubmittingInvite(true);
    const result = await apiClient.admin.inviteUser({
      email: invite.email.trim(),
      displayName: invite.displayName.trim() || undefined,
      roleIds: [invite.roleId],
      message: invite.message.trim() || undefined,
      idempotencyKey: `invite-${Date.now()}`
    });
    setSubmittingInvite(false);
    if (!result.ok) {
      setInviteErrors({ email: result.error.fieldErrors?.email?.[0], roleId: result.error.fieldErrors?.roleIds?.[0] });
      setMessage(result.error.message);
      if (result.error.fieldErrors?.email) emailRef.current?.focus();
      return;
    }

    setInvite(initialInvite);
    setInviteErrors({});
    setMessage(result.value.status === 'resent' ? `Duplicate invitation handled: resent invitation to ${result.value.invitedEmail}.` : `Invitation sent to ${result.value.invitedEmail}.`);
    await loadUsers();
  }

  async function submitRole(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selectedUser) return;
    setMessage(undefined);
    const clientErrors = validateRole(roleForm);
    setRoleErrors(clientErrors);
    if (clientErrors.reason) reasonRef.current?.focus();
    else if (clientErrors.confirmation) confirmationRef.current?.focus();
    if (Object.keys(clientErrors).length > 0) return;

    setSubmittingRole(true);
    const result = await apiClient.admin.updateRoles(selectedUser.userId, {
      roleIds: [roleForm.roleId],
      reason: roleForm.reason.trim() || undefined,
      expectedVersion: selectedUser.version
    });
    setSubmittingRole(false);
    if (!result.ok) {
      setRoleErrors({ reason: result.error.fieldErrors?.reason?.[0] });
      setMessage(result.error.message);
      reasonRef.current?.focus();
      return;
    }

    setMessage(`Roles updated for ${selectedUser.email}; audit trace ${result.value.auditTraceId}.`);
    setRoleForm(initialRole);
    setRoleErrors({});
    await loadUsers();
  }

  return (
    <section className="admin-users" aria-label="Admin Users and Invitations">
      <div className="slice-intro">
        <p className="eyebrow">Slice 7</p>
        <h2>Admin Users and Invitations</h2>
        <p>Invite users and demonstrate role assignment validation. Frontend role visibility is UX only; backend authorization remains authoritative.</p>
      </div>
      <div className="two-column-flow admin-layout">
        <InviteUserForm form={invite} errors={inviteErrors} submitting={submittingInvite} emailRef={emailRef} roleSelectRef={roleSelectRef} onChange={setInvite} onSubmit={submitInvite} />
        <div className="flow-stack">
          <div className="commit-warning"><strong>UX-only visibility:</strong> showing admin controls in the frontend does not grant permission. Real APIs must enforce tenant admin and elevated-role authorization.</div>
          {message && <div className="form-status" role="status" aria-live="polite">{message}</div>}
          <UserList state={usersState} selectedUserId={selectedUserId} onSelect={(user) => { setSelectedUserId(user.userId); setRoleForm({ ...initialRole, roleId: user.roles[0] ?? 'reviewer' }); }} onRetry={loadUsers} />
          <RoleAssignmentForm user={selectedUser} form={roleForm} errors={roleErrors} submitting={submittingRole} reasonRef={reasonRef} confirmationRef={confirmationRef} onChange={setRoleForm} onSubmit={submitRole} />
        </div>
      </div>
    </section>
  );
}

function InviteUserForm({ form, errors, submitting, emailRef, roleSelectRef, onChange, onSubmit }: { form: InviteForm; errors: InviteErrors; submitting: boolean; emailRef: React.RefObject<HTMLInputElement | null>; roleSelectRef: React.RefObject<HTMLSelectElement | null>; onChange: (form: InviteForm) => void; onSubmit: (event: React.FormEvent<HTMLFormElement>) => void }) {
  return (
    <Card className="form-card" title="Invite user" subtitle="Validation covers email, role selection, duplicate invitations, and server error mapping.">
      <form className="stacked-form" onSubmit={onSubmit} noValidate>
        <TextInput id="invite-email" ref={emailRef} label="Email" type="email" error={errors.email} value={form.email} onChange={(event) => onChange({ ...form, email: event.target.value })} />
        <TextInput id="invite-name" label="Display name" value={form.displayName} onChange={(event) => onChange({ ...form, displayName: event.target.value })} />
        <SelectField id="invite-role" ref={roleSelectRef} label="Initial role" error={errors.roleId} value={form.roleId} onChange={(event) => onChange({ ...form, roleId: event.target.value })}>
          <option value="">Select role</option>
          {roleOptions.map((role) => <option key={role} value={role}>{role}</option>)}
        </SelectField>
        <TextArea id="invite-message" label="Invite message" helper="Optional fixture-only message." value={form.message} onChange={(event) => onChange({ ...form, message: event.target.value })} />
        <Button type="submit" disabled={submitting}>{submitting ? 'Sending invitation…' : 'Invite user'}</Button>
      </form>
    </Card>
  );
}

function UserList({ state, selectedUserId, onSelect, onRetry }: { state: RemoteData<AdminUser[]>; selectedUserId?: string; onSelect: (user: AdminUser) => void; onRetry: () => void }) {
  return (
    <DataState
      state={state.status === 'loading' ? { status: 'loading' } : state.status === 'empty' ? { status: 'empty' } : state.status === 'error' ? { status: 'error', error: state.error } : { status: 'ready', value: state.value }}
      loadingLabel="Loading users and invitations…"
      emptyTitle="No users or invitations"
      emptyDetail="Invite the first tenant user to demonstrate administration flows."
      onRetry={onRetry}
    >
      {(users) => (
        <Card title="Users and invitations" subtitle="Membership status and roles are text-visible, not color-only.">
          <div className="selectable-list" role="list">
            {users.map((user) => (
              <button key={user.userId} type="button" className={selectedUserId === user.userId ? 'selectable-row selected' : 'selectable-row'} onClick={() => onSelect(user)}>
                <span><strong>{user.displayName ?? user.email}</strong><small>{user.email} · version {user.version}</small></span>
                <StatusPill tone={user.membershipStatus === 'active' ? 'success' : user.membershipStatus === 'invited' ? 'warning' : 'danger'}>{`${user.membershipStatus} · ${user.roles.join(', ')}`}</StatusPill>
              </button>
            ))}
          </div>
        </Card>
      )}
    </DataState>
  );
}

function RoleAssignmentForm({ user, form, errors, submitting, reasonRef, confirmationRef, onChange, onSubmit }: { user?: AdminUser; form: RoleForm; errors: RoleErrors; submitting: boolean; reasonRef: React.RefObject<HTMLTextAreaElement | null>; confirmationRef: React.RefObject<HTMLInputElement | null>; onChange: (form: RoleForm) => void; onSubmit: (event: React.FormEvent<HTMLFormElement>) => void }) {
  const elevated = isElevated(form.roleId);
  return (
    <Card className="role-assignment-card" title="Role assignment" subtitle="Elevated roles require a reason and explicit confirmation before fixture submission.">
      {!user ? <p>Select a user before changing roles.</p> : (
        <form className="stacked-form" onSubmit={onSubmit} noValidate>
          <p>Selected user: <strong>{user.email}</strong></p>
          <SelectField id="role-assignment" label="Role" value={form.roleId} onChange={(event) => onChange({ ...form, roleId: event.target.value })}>
            {roleOptions.map((role) => <option key={role} value={role}>{role}</option>)}
          </SelectField>
          {elevated && <TextArea id="role-reason" ref={reasonRef} label="Elevated-role reason" helper="Required for tenant-admin or policy-owner grants." error={errors.reason} value={form.reason} onChange={(event) => onChange({ ...form, reason: event.target.value })} />}
          {elevated && <label className="checkbox-row authority-warning"><input ref={confirmationRef} type="checkbox" checked={form.confirmation} aria-invalid={errors.confirmation ? 'true' : undefined} aria-describedby={errors.confirmation ? 'role-confirm-error' : undefined} onChange={(event) => onChange({ ...form, confirmation: event.target.checked })} /><span>I confirm this elevated role can change tenant administration or policy authority.</span></label>}
          {errors.confirmation && <p id="role-confirm-error" className="field-error" role="alert">{errors.confirmation}</p>}
          <Button type="submit" disabled={submitting}>{submitting ? 'Updating roles…' : 'Update role assignment'}</Button>
        </form>
      )}
    </Card>
  );
}

function validateInvite(form: InviteForm): InviteErrors {
  const errors: InviteErrors = {};
  if (!/^\S+@\S+\.\S+$/.test(form.email.trim())) errors.email = 'Enter a valid email address.';
  if (!form.roleId) errors.roleId = 'Select an initial role.';
  return errors;
}

function validateRole(form: RoleForm): RoleErrors {
  const errors: RoleErrors = {};
  if (isElevated(form.roleId) && form.reason.trim().length < 8) errors.reason = 'Explain why the elevated role is required.';
  if (isElevated(form.roleId) && !form.confirmation) errors.confirmation = 'Confirm the elevated authority change before updating roles.';
  return errors;
}

function isElevated(role: string) {
  return role === 'tenant-admin' || role === 'policy-owner';
}
