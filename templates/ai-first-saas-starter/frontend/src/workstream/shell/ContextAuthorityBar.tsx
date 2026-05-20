import type { AuthContext, MeResponse, TraceLink } from '../types';

type ContextAuthorityBarProps = {
  me: MeResponse;
  authContext?: AuthContext;
  pendingApprovalCount?: number;
  traceLinks?: TraceLink[];
};

export function ContextAuthorityBar({ me, authContext = me.selectedAuthContext, pendingApprovalCount = 0, traceLinks = [] }: ContextAuthorityBarProps) {
  const forbidden = me.memberships.length === 0;
  const disabled = me.account.status === 'disabled';
  const recovery = disabled ? 'Contact an administrator to reactivate this account.' : forbidden ? 'Select or request access to a tenant membership.' : undefined;

  return (
    <header className="topbar workstream-context-authority" aria-label="Selected context and authority">
      <div>
        <p className="eyebrow">Selected AuthContext</p>
        <h1>{authContext.tenantName}{authContext.customerName ? ` / ${authContext.customerName}` : ''}</h1>
        <p>
          Signed in as {me.account.displayName}. Roles: {authContext.roleIds.length ? authContext.roleIds.join(', ') : 'none'}.
          {' '}Browser-safe capabilities: {authContext.capabilityIds.length}.
        </p>
      </div>
      <div className="topbar-actions" aria-label="Authority indicators">
        {authContext.supportAccess?.active && <span className="status-pill warning">Support access active: {authContext.supportAccess.reason}</span>}
        {pendingApprovalCount > 0 && <span className="status-pill info">{pendingApprovalCount} pending approvals</span>}
        {traceLinks.map((link) => <a key={link.traceId} className="ds-button ghost" href={link.href}>{link.label}</a>)}
        {recovery && <span className="status-pill danger">{recovery}</span>}
      </div>
    </header>
  );
}
