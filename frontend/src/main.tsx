import React from 'react';
import { createRoot } from 'react-dom/client';
import { AuthKitProvider, useAuth } from '@workos-inc/authkit-react';
import './styles.css';

type RemoteData<T> =
  | { status: 'idle' | 'loading' }
  | { status: 'ready'; value: T }
  | { status: 'error'; error: ApiError };

type ApiErrorKind = 'unauthorized' | 'forbidden' | 'disabled' | 'notFound' | 'server' | 'network';

type ApiError = {
  kind: ApiErrorKind;
  message: string;
  status?: number;
};

type Scope = {
  role: string;
  tenantId?: string | null;
  customerId?: string | null;
};

type MeResponse = {
  userId: string;
  email: string;
  displayName: string;
  status: 'ACTIVE' | 'INVITED' | 'DISABLED' | string;
  roles: string[];
  scopes: Scope[];
  capabilities: string[];
};

type RoleAssignment = {
  role: string;
  tenantId?: string | null;
  customerId?: string | null;
};

type UserResponse = {
  userId: string;
  email: string;
  displayName: string;
  status: string;
  roles: RoleAssignment[];
};

type UserActionResponse = {
  user: UserResponse;
  auditId: string;
};

type Page = 'overview' | 'supplies' | 'admin' | 'tenants' | 'audit' | 'profile';

const clientId = browserEnvValue('WORKOS_CLIENT_ID');
const redirectUri = browserEnvValue('WORKOS_REDIRECT_URI') || window.location.origin;

function App() {
  if (!clientId) {
    return <MissingConfig />;
  }

  return (
    <AuthKitProvider clientId={clientId} redirectUri={redirectUri}>
      <AuthenticatedShell />
    </AuthKitProvider>
  );
}

function AuthenticatedShell() {
  const auth = useAuth();
  const [meState, setMeState] = React.useState<RemoteData<MeResponse>>({ status: 'idle' });
  const [page, setPage] = React.useState<Page>('overview');

  React.useEffect(() => {
    let cancelled = false;
    async function loadMe() {
      if (!auth.user) {
        setMeState({ status: 'idle' });
        return;
      }
      setMeState({ status: 'loading' });
      const result = await getMe(() => auth.getAccessToken());
      if (cancelled) return;
      setMeState(result.ok ? { status: 'ready', value: result.value } : { status: 'error', error: result.error });
    }
    loadMe();
    return () => {
      cancelled = true;
    };
  }, [auth.user?.id]);

  if (auth.isLoading) {
    return <CenteredState title="Loading secure session" detail="Checking WorkOS AuthKit session state…" busy />;
  }

  if (!auth.user) {
    return <SignedOutState onSignIn={() => auth.signIn()} />;
  }

  if (meState.status === 'loading' || meState.status === 'idle') {
    return <CenteredState title="Loading local DCA account" detail="Authenticated with WorkOS. Loading Akka-owned roles, scopes, and capabilities from /api/me…" busy />;
  }

  if (meState.status === 'error') {
    return <AccessProblem error={meState.error} onSignOut={() => auth.signOut()} />;
  }

  const me = meState.value;
  const navigation = navItemsFor(me);
  const currentPage = navigation.some((item) => item.page === page) ? page : 'overview';

  return (
    <div className="app-shell">
      <aside className="sidebar" aria-label="Application navigation">
        <div className="brand-lockup" aria-label="AI-first DCA Seed Console">
          <span className="brand-mark" aria-hidden="true">D</span>
          <div>
            <strong>AI-first DCA</strong>
            <span>Seed console</span>
          </div>
        </div>
        <nav aria-label="Primary navigation" className="nav-list">
          {navigation.map((item) => (
            <button
              key={item.page}
              type="button"
              className={currentPage === item.page ? 'nav-item active' : 'nav-item'}
              onClick={() => setPage(item.page)}
              aria-current={currentPage === item.page ? 'page' : undefined}
            >
              <span aria-hidden="true">{item.icon}</span>
              <span>{item.label}</span>
            </button>
          ))}
        </nav>
        <div className="auth-note" role="note">
          Navigation reflects <code>/api/me</code> roles only. Backend APIs still enforce authorization for every protected action.
        </div>
      </aside>

      <div className="main-column">
        <header className="topbar">
          <div>
            <p className="eyebrow">Authenticated Akka-hosted app shell</p>
            <h1>{pageTitle(currentPage)}</h1>
          </div>
          <div className="account-chip" aria-label="Signed in account">
            <div>
              <strong>{me.displayName || me.email}</strong>
              <span>{me.status} · {me.roles.join(', ') || 'no roles'}</span>
            </div>
            <button type="button" className="secondary" onClick={() => auth.signOut()}>Sign out</button>
          </div>
        </header>
        <main id="main-content" className="content" tabIndex={-1}>
          <PageContent page={currentPage} me={me} />
        </main>
      </div>
    </div>
  );
}

function PageContent({ page, me }: { page: Page; me: MeResponse }) {
  switch (page) {
    case 'supplies':
      return <SuppliesSurface me={me} />;
    case 'admin':
      return <AdminSurface me={me} />;
    case 'tenants':
      return <TenantSurface me={me} />;
    case 'audit':
      return <AuditSurface me={me} />;
    case 'profile':
      return <ProfileSurface me={me} />;
    default:
      return <OverviewSurface me={me} />;
  }
}

function OverviewSurface({ me }: { me: MeResponse }) {
  return (
    <section aria-labelledby="overview-heading">
      <div className="section-heading">
        <p className="eyebrow">Mission control</p>
        <h2 id="overview-heading">DCA seed foundation</h2>
        <p>The shell combines WorkOS authentication, Akka-owned authorization state, and AI-first supervision entry points.</p>
      </div>
      <div className="metric-grid">
        <InfoCard title="Local account" value={me.status} detail={me.email} status={me.status === 'ACTIVE' ? 'success' : 'warning'} />
        <InfoCard title="Roles" value={`${me.roles.length}`} detail={me.roles.join(', ') || 'No application roles assigned'} status="info" />
        <InfoCard title="Scopes" value={`${me.scopes.length}`} detail={scopeSummary(me.scopes)} status="info" />
        <InfoCard title="Backend authority" value="Server enforced" detail="Frontend role-aware navigation is UX only." status="success" />
      </div>
      <div className="panel emphasis">
        <h3>Human authority remains explicit</h3>
        <p>
          Supplies approvals, user administration, tenant/customer changes, and audit access must still pass backend authorization and workflow/entity gates.
        </p>
      </div>
    </section>
  );
}

function SuppliesSurface({ me }: { me: MeResponse }) {
  const canReview = hasAnyCapability(me, ['SUPERVISE_OPERATIONS', 'REVIEW_DECISIONS']);
  return (
    <section aria-labelledby="supplies-heading">
      <div className="section-heading">
        <p className="eyebrow">Delegated operations</p>
        <h2 id="supplies-heading">Supplies autopilot</h2>
        <p>Open the existing supplies command center for decision-card evidence, policy triggers, trace IDs, and outcome links.</p>
      </div>
      <div className="panel action-panel">
        <div>
          <h3>{canReview ? 'Review decisions and exceptions' : 'Limited operational visibility'}</h3>
          <p>
            This shell exposes the entry point based on roles from <code>/api/me</code>. Backend supplies APIs must enforce reviewer authority independently.
          </p>
        </div>
        <a className="primary-link" href="/ui/supplies">Open supplies command center</a>
      </div>
    </section>
  );
}

function AdminSurface({ me }: { me: MeResponse }) {
  const auth = useAuth();
  const [email, setEmail] = React.useState('');
  const [displayName, setDisplayName] = React.useState('');
  const [role, setRole] = React.useState('USER');
  const [tenantId, setTenantId] = React.useState('');
  const [customerId, setCustomerId] = React.useState('');
  const [result, setResult] = React.useState<RemoteData<UserActionResponse>>({ status: 'idle' });
  const canAdminUsers = hasAnyCapability(me, ['ADMIN_USERS', 'ADMIN_CUSTOMER_USERS']);

  async function submitInvite(event: React.FormEvent) {
    event.preventDefault();
    setResult({ status: 'loading' });
    const assignment: RoleAssignment = {
      role,
      tenantId: role === 'APP_ADMIN' ? null : tenantId.trim(),
      customerId: role === 'CUSTOMER_ADMIN' || role === 'USER' ? customerId.trim() : null
    };
    const response = await inviteUser(() => auth.getAccessToken(), {
      email: email.trim(),
      displayName: displayName.trim(),
      roles: [assignment]
    });
    setResult(response.ok ? { status: 'ready', value: response.value } : { status: 'error', error: response.error });
    if (response.ok) {
      setEmail('');
      setDisplayName('');
    }
  }

  return (
    <section aria-labelledby="users-and-roles-heading">
      <div className="section-heading">
        <p className="eyebrow">Administration</p>
        <h2 id="users-and-roles-heading">Invite users and assign roles</h2>
        <p>Create a local invited account, assign its server-side role/scope, and trigger the backend invitation email. The user signs in with WorkOS and <code>/api/me</code> links the identity to the invited account.</p>
      </div>
      {!canAdminUsers && <div className="callout warning" role="note">Your current role does not grant user administration; backend APIs will reject forbidden calls.</div>}
      <form className="panel form-grid" onSubmit={submitInvite} aria-busy={result.status === 'loading'}>
        <label>
          Email
          <input value={email} onChange={(event) => setEmail(event.target.value)} type="email" required placeholder="new.user@example.com" />
        </label>
        <label>
          Display name
          <input value={displayName} onChange={(event) => setDisplayName(event.target.value)} placeholder="New User" />
        </label>
        <label>
          Role
          <select value={role} onChange={(event) => setRole(event.target.value)}>
            {['USER', 'CUSTOMER_ADMIN', 'OPERATIONS_SUPERVISOR', 'DEALER_OWNER', 'POLICY_OWNER', 'AUDITOR', 'APP_ADMIN'].map((value) => <option key={value} value={value}>{value}</option>)}
          </select>
        </label>
        {role !== 'APP_ADMIN' && <label>
          Tenant ID
          <input value={tenantId} onChange={(event) => setTenantId(event.target.value)} required placeholder="tenant-a" />
        </label>}
        {(role === 'USER' || role === 'CUSTOMER_ADMIN') && <label>
          Customer ID
          <input value={customerId} onChange={(event) => setCustomerId(event.target.value)} required placeholder="customer-a" />
        </label>}
        <div className="form-actions">
          <button type="submit" disabled={!canAdminUsers || result.status === 'loading'}>{result.status === 'loading' ? 'Sending invite…' : 'Invite user'}</button>
        </div>
      </form>
      {result.status === 'ready' && <div className="callout success" role="status">Invited <strong>{result.value.user.email}</strong> as {result.value.user.roles.map((item) => item.role).join(', ')}. Audit ID: <code>{result.value.auditId}</code></div>}
      {result.status === 'error' && <div className="callout warning" role="alert">{result.error.message}</div>}
    </section>
  );
}

function TenantSurface({ me }: { me: MeResponse }) {
  return (
    <PlaceholderSurface
      eyebrow="Administration"
      title="Tenants and customers"
      body="Set up dealer tenants and customer scopes for future DCA lifecycle and supplies slices."
      warning={!hasCapability(me, 'ADMIN_TENANTS') ? 'Tenant/customer visibility is scoped. UI visibility is not a permission grant.' : undefined}
    />
  );
}

function AuditSurface({ me }: { me: MeResponse }) {
  return (
    <PlaceholderSurface
      eyebrow="Accountability"
      title="Security and work audit"
      body="Review admin audit entries, policy/decision traces, tool/data access, and outcome links as future slices are attached."
      warning={!hasCapability(me, 'VIEW_AUDIT') ? 'Audit access must be authorized by the backend before records are returned.' : undefined}
    />
  );
}

function ProfileSurface({ me }: { me: MeResponse }) {
  return (
    <section aria-labelledby="profile-heading">
      <div className="section-heading">
        <p className="eyebrow">Account</p>
        <h2 id="profile-heading">My local Akka profile</h2>
      </div>
      <dl className="details panel">
        <dt>User ID</dt><dd>{me.userId}</dd>
        <dt>Email</dt><dd>{me.email}</dd>
        <dt>Display name</dt><dd>{me.displayName || 'Not provided'}</dd>
        <dt>Status</dt><dd><StatusBadge label={me.status} tone={me.status === 'ACTIVE' ? 'success' : 'warning'} /></dd>
        <dt>Capabilities</dt><dd>{me.capabilities.join(', ') || 'None'}</dd>
        <dt>Scopes</dt><dd>{scopeSummary(me.scopes)}</dd>
      </dl>
    </section>
  );
}

function PlaceholderSurface({ eyebrow, title, body, warning }: { eyebrow: string; title: string; body: string; warning?: string }) {
  return (
    <section aria-labelledby={`${title}-heading`.replaceAll(' ', '-').toLowerCase()}>
      <div className="section-heading">
        <p className="eyebrow">{eyebrow}</p>
        <h2 id={`${title}-heading`.replaceAll(' ', '-').toLowerCase()}>{title}</h2>
        <p>{body}</p>
      </div>
      {warning && <div className="callout warning" role="note">{warning}</div>}
      <div className="panel empty-state">
        <h3>Ready for the next implementation slice</h3>
        <p>Frontend state and API clients are structured so protected actions can be added without weakening backend authorization.</p>
      </div>
    </section>
  );
}

function InfoCard({ title, value, detail, status }: { title: string; value: string; detail: string; status: 'success' | 'warning' | 'info' }) {
  return (
    <article className="info-card">
      <span className={`status-dot ${status}`} aria-hidden="true" />
      <h3>{title}</h3>
      <div className="info-value">{value}</div>
      <p>{detail}</p>
    </article>
  );
}

function SignedOutState({ onSignIn }: { onSignIn: () => void }) {
  return (
    <main className="center-card">
      <p className="eyebrow">Public app shell</p>
      <h1>Sign in to AI-first DCA</h1>
      <p>Static frontend assets are public. Protected <code>/api/...</code> routes require a WorkOS bearer token and Akka-owned authorization.</p>
      <button type="button" onClick={onSignIn}>Sign in with WorkOS</button>
    </main>
  );
}

function MissingConfig() {
  return (
    <main className="center-card" role="alert">
      <p className="eyebrow">Configuration needed</p>
      <h1>WorkOS AuthKit client ID is missing</h1>
      <p>Set public <code>VITE_WORKOS_CLIENT_ID</code> and optional <code>VITE_WORKOS_REDIRECT_URI</code>. Do not put backend secrets in frontend env files.</p>
    </main>
  );
}

function CenteredState({ title, detail, busy }: { title: string; detail: string; busy?: boolean }) {
  return (
    <main className="center-card" aria-busy={busy ? 'true' : undefined}>
      <p className="eyebrow">Please wait</p>
      <h1>{title}</h1>
      <p>{detail}</p>
    </main>
  );
}

function AccessProblem({ error, onSignOut }: { error: ApiError; onSignOut: () => void }) {
  const copy: Record<ApiErrorKind, { title: string; detail: string }> = {
    unauthorized: { title: 'Session token was not accepted', detail: 'Sign in again so the browser can send a fresh bearer token to /api/me.' },
    forbidden: { title: 'Local DCA account is not authorized', detail: 'WorkOS authenticated you, but Akka authorization rejected this account or scope.' },
    disabled: { title: 'Account is disabled', detail: 'A disabled local account cannot use protected APIs even with a valid WorkOS session.' },
    notFound: { title: 'No invited local account was found', detail: 'Ask an administrator to invite and scope this WorkOS user before continuing.' },
    server: { title: 'The backend could not load account access', detail: 'Try again or ask an administrator to check service logs and audit records.' },
    network: { title: 'Could not reach the Akka backend', detail: 'Check that the frontend is served by Akka or that Vite is proxying /api to Akka.' }
  };
  const selected = copy[error.kind];
  return (
    <main className="center-card" role="alert">
      <p className="eyebrow">Access problem</p>
      <h1>{selected.title}</h1>
      <p>{selected.detail}</p>
      <pre className="error-box">{error.message}</pre>
      <button type="button" onClick={onSignOut}>Sign out</button>
    </main>
  );
}

function StatusBadge({ label, tone }: { label: string; tone: 'success' | 'warning' | 'info' }) {
  return <span className={`badge ${tone}`}>{label}</span>;
}

function navItemsFor(me: MeResponse): Array<{ page: Page; label: string; icon: string }> {
  const items: Array<{ page: Page; label: string; icon: string; show: boolean }> = [
    { page: 'overview', label: 'Mission control', icon: '⌁', show: true },
    { page: 'supplies', label: 'Supplies autopilot', icon: '◇', show: hasAnyCapability(me, ['SUPERVISE_OPERATIONS', 'REVIEW_DECISIONS', 'USE_APP']) },
    { page: 'admin', label: 'Users and roles', icon: '◎', show: hasAnyCapability(me, ['ADMIN_USERS', 'ADMIN_CUSTOMER_USERS']) },
    { page: 'tenants', label: 'Tenants/customers', icon: '▦', show: hasCapability(me, 'ADMIN_TENANTS') || hasAnyRole(me, ['DEALER_OWNER', 'CUSTOMER_ADMIN']) },
    { page: 'audit', label: 'Audit and traces', icon: '☷', show: hasCapability(me, 'VIEW_AUDIT') },
    { page: 'profile', label: 'My access', icon: '◉', show: true }
  ];
  return items.filter((item) => item.show).map(({ page, label, icon }) => ({ page, label, icon }));
}

function pageTitle(page: Page) {
  return {
    overview: 'Mission control',
    supplies: 'Supplies autopilot',
    admin: 'Users and roles',
    tenants: 'Tenants and customers',
    audit: 'Audit and traces',
    profile: 'My access'
  }[page];
}

function browserEnvValue(name: string) {
  const env = (import.meta as unknown as { env?: Record<string, string | undefined> }).env ?? {};
  return env[`VITE_${name}`] ?? '';
}

function hasCapability(me: MeResponse, capability: string) {
  return me.capabilities.includes(capability);
}

function hasAnyCapability(me: MeResponse, capabilities: string[]) {
  return capabilities.some((capability) => hasCapability(me, capability));
}

function hasAnyRole(me: MeResponse, roles: string[]) {
  return roles.some((role) => me.roles.includes(role));
}

function scopeSummary(scopes: Scope[]) {
  if (!scopes.length) return 'No tenant or customer scopes assigned';
  return scopes
    .map((scope) => [scope.role, scope.tenantId, scope.customerId].filter(Boolean).join(' / '))
    .join('; ');
}

async function getMe(getAccessToken: () => Promise<string | null | undefined>): Promise<{ ok: true; value: MeResponse } | { ok: false; error: ApiError }> {
  try {
    const token = await getAccessToken();
    if (!token) {
      return { ok: false, error: { kind: 'unauthorized', message: 'No WorkOS access token was available.' } };
    }
    const response = await fetch('/api/me', {
      headers: { Authorization: `Bearer ${token}` }
    });
    if (!response.ok) {
      return { ok: false, error: await mapApiError(response) };
    }
    return { ok: true, value: await response.json() as MeResponse };
  } catch (error) {
    return { ok: false, error: { kind: 'network', message: error instanceof Error ? error.message : String(error) } };
  }
}

async function inviteUser(
  getAccessToken: () => Promise<string | null | undefined>,
  request: { email: string; displayName: string; roles: RoleAssignment[] }
): Promise<{ ok: true; value: UserActionResponse } | { ok: false; error: ApiError }> {
  try {
    const token = await getAccessToken();
    if (!token) {
      return { ok: false, error: { kind: 'unauthorized', message: 'No WorkOS access token was available.' } };
    }
    const response = await fetch('/api/admin/users/invite', {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
      body: JSON.stringify(request)
    });
    if (!response.ok) {
      return { ok: false, error: await mapApiError(response) };
    }
    return { ok: true, value: await response.json() as UserActionResponse };
  } catch (error) {
    return { ok: false, error: { kind: 'network', message: error instanceof Error ? error.message : String(error) } };
  }
}

async function mapApiError(response: Response): Promise<ApiError> {
  const text = await response.text();
  const message = text || `HTTP ${response.status}`;
  if (response.status === 401) return { kind: 'unauthorized', message, status: response.status };
  if (response.status === 403) {
    return { kind: message.toLowerCase().includes('disabled') ? 'disabled' : 'forbidden', message, status: response.status };
  }
  if (response.status === 404) return { kind: 'notFound', message, status: response.status };
  return { kind: 'server', message, status: response.status };
}

createRoot(document.getElementById('root')!).render(<App />);
