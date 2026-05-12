import React from 'react';
import { createRoot } from 'react-dom/client';
import { AuthKitProvider, useAuth } from '@workos-inc/authkit-react';
import './styles.css';

type UserProfile = {
  firstName: string;
  lastName: string;
  displayName: string;
  avatarUrl: string;
  phone: string;
  locale: string;
  timezone: string;
};

type UserAccount = {
  userId: string;
  email: string;
  status: string;
  profile: UserProfile;
  roles: Array<{ role: string; tenantId?: string; customerId?: string }>;
};

type Page = 'dashboard' | 'profile' | 'users' | 'tenants' | 'customers' | 'audit' | 'settings';

const clientId = import.meta.env.VITE_WORKOS_CLIENT_ID ?? '';
const redirectUri = import.meta.env.VITE_WORKOS_REDIRECT_URI ?? window.location.origin;

function App() {
  if (!clientId) {
    return <MissingConfig />;
  }

  return (
    <AuthKitProvider clientId={clientId} redirectUri={redirectUri}>
      <SecureShell />
    </AuthKitProvider>
  );
}

function SecureShell() {
  const auth = useAuth();
  const [me, setMe] = React.useState<UserAccount | null>(null);
  const [error, setError] = React.useState<string>('');
  const [page, setPage] = React.useState<Page>('dashboard');

  React.useEffect(() => {
    async function loadMe() {
      if (!auth.user) return;
      try {
        const token = await auth.getAccessToken();
        const response = await fetch('/api/me', {
          headers: { Authorization: `Bearer ${token}` }
        });
        if (!response.ok) throw new Error(await response.text());
        setMe(await response.json());
      } catch (e) {
        setError(e instanceof Error ? e.message : String(e));
      }
    }
    loadMe();
  }, [auth.user?.id]);

  if (auth.isLoading) return <main className="center-card" aria-busy="true">Loading account access…</main>;

  if (!auth.user) {
    return (
      <main className="center-card">
        <h1>Akka Secure App</h1>
        <p>Sign in with WorkOS to continue.</p>
        <button onClick={() => auth.signIn()}>Sign in</button>
      </main>
    );
  }

  const isAppAdmin = hasRole(me, 'APP_ADMIN');
  const isTenantAdmin = isAppAdmin || hasRole(me, 'TENANT_ADMIN');
  const isCustomerAdmin = isTenantAdmin || hasRole(me, 'CUSTOMER_ADMIN');

  const navItems: Array<{ page: Page; label: string; enabled: boolean }> = [
    { page: 'dashboard', label: 'Dashboard', enabled: true },
    { page: 'profile', label: 'My Profile', enabled: true },
    { page: 'users', label: 'User Administration', enabled: isCustomerAdmin },
    { page: 'tenants', label: 'Tenants', enabled: isAppAdmin },
    { page: 'customers', label: 'Customers', enabled: isTenantAdmin },
    { page: 'audit', label: 'Audit Log', enabled: isAppAdmin },
    { page: 'settings', label: 'Settings', enabled: isAppAdmin }
  ];

  return (
    <div className="app-shell">
      <TopBar user={me} workosUser={auth.user} onSignOut={() => auth.signOut()} />
      <aside className="sidebar">
        <div className="sidebar-title">Navigation</div>
        <nav aria-label="Primary navigation">
          {navItems.filter(item => item.enabled).map(item => (
            <button
              key={item.page}
              className={page === item.page ? 'nav-item active' : 'nav-item'}
              onClick={() => setPage(item.page)}
              aria-current={page === item.page ? 'page' : undefined}
            >
              {item.label}
            </button>
          ))}
        </nav>
      </aside>
      <main className="content">
        {error && <pre className="error">{error}</pre>}
        {me ? <PageContent page={page} me={me} /> : <LoadingProfile user={auth.user} />}
      </main>
    </div>
  );
}

function TopBar({ user, workosUser, onSignOut }: { user: UserAccount | null; workosUser: any; onSignOut: () => void }) {
  return (
    <header className="topbar">
      <div>
        <div className="brand">Akka Secure App</div>
        <div className="subtitle">Secure access console served by Akka</div>
      </div>
      <div className="topbar-user">
        <div className="user-name">{user?.profile?.displayName || user?.email || workosUser.email}</div>
        <div className="user-status">{user?.status ?? 'Signed in'}</div>
        <button onClick={onSignOut}>Sign out</button>
      </div>
    </header>
  );
}

function PageContent({ page, me }: { page: Page; me: UserAccount }) {
  switch (page) {
    case 'profile':
      return <ProfilePage me={me} />;
    case 'users':
      return <PlaceholderPage title="User Administration" description="Create, activate, disable, delete, role-manage, and impersonate users from here." />;
    case 'tenants':
      return <PlaceholderPage title="Tenants" description="Manage tenant/account records. Available to APP_ADMIN users." />;
    case 'customers':
      return <PlaceholderPage title="Customers" description="Manage customers under tenant accounts." />;
    case 'audit':
      return <PlaceholderPage title="Audit Log" description="Review admin actions, role changes, impersonation events, and account changes." />;
    case 'settings':
      return <PlaceholderPage title="Settings" description="Application-wide settings and security configuration." />;
    default:
      return <DashboardPage me={me} />;
  }
}

function DashboardPage({ me }: { me: UserAccount }) {
  return (
    <section>
      <div className="section-heading">
        <p className="eyebrow">Security overview</p>
        <h1>Dashboard</h1>
      </div>
      <div className="grid">
        <InfoCard title="Account" value={me.email} detail={`Status: ${me.status}`} />
        <InfoCard title="Roles" value={`${me.roles?.length ?? 0}`} detail={roleSummary(me)} />
        <InfoCard title="Security" value="WorkOS" detail="Authentication, MFA, email verification, and social login" />
      </div>
    </section>
  );
}

function ProfilePage({ me }: { me: UserAccount }) {
  return (
    <section>
      <div className="section-heading">
        <p className="eyebrow">Account</p>
        <h1>My Profile</h1>
      </div>
      <div className="panel">
        <dl className="details">
          <dt>Email</dt><dd>{me.email}</dd>
          <dt>Display name</dt><dd>{me.profile?.displayName || 'Not provided'}</dd>
          <dt>First name</dt><dd>{me.profile?.firstName || 'Not provided'}</dd>
          <dt>Last name</dt><dd>{me.profile?.lastName || 'Not provided'}</dd>
          <dt>Locale</dt><dd>{me.profile?.locale || 'Not provided'}</dd>
          <dt>Timezone</dt><dd>{me.profile?.timezone || 'Not provided'}</dd>
        </dl>
      </div>
    </section>
  );
}

function PlaceholderPage({ title, description }: { title: string; description: string }) {
  return (
    <section>
      <div className="section-heading">
        <p className="eyebrow">Administration</p>
        <h1>{title}</h1>
      </div>
      <div className="panel">
        <p>{description}</p>
        <p className="muted">This section is ready for the next implementation step.</p>
      </div>
    </section>
  );
}

function InfoCard({ title, value, detail }: { title: string; value: string; detail: string }) {
  return (
    <article className="info-card">
      <h3>{title}</h3>
      <div className="info-value">{value}</div>
      <p>{detail}</p>
    </article>
  );
}

function LoadingProfile({ user }: { user: any }) {
  return (
    <section>
      <div className="section-heading">
        <p className="eyebrow">Loading profile</p>
        <h1>{user.firstName ?? user.email}</h1>
      </div>
      <div className="panel">
        <p>Signed in with WorkOS. Loading the Akka application profile…</p>
        <p>If this does not complete, the local Akka user account may not have been invited/activated yet.</p>
      </div>
    </section>
  );
}

function MissingConfig() {
  return (
    <main className="center-card">
      <h1>Akka Secure App</h1>
      <p>Set <code>VITE_WORKOS_CLIENT_ID</code> before running or building the frontend.</p>
    </main>
  );
}

function hasRole(me: UserAccount | null, role: string) {
  return (me?.roles ?? []).some(assignment => assignment.role === role);
}

function roleSummary(me: UserAccount) {
  if (!me.roles?.length) return 'No application roles assigned';
  return me.roles.map(role => role.role).join(', ');
}

createRoot(document.getElementById('root')!).render(<App />);
