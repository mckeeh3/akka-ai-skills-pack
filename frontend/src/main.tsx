import React from 'react';
import { createRoot } from 'react-dom/client';
import './styles/tokens.css';
import './styles/base.css';
import './styles/layout.css';
import './styles/components.css';

type ModePreference = 'light' | 'dark' | 'system';
type RouteId = 'briefing' | 'goals' | 'decisions' | 'governance' | 'audit' | 'admin' | 'profile';

const modeStorageKey = 'seed-ui-mode';

const routes: Array<{ id: RouteId; label: string; group: 'Work' | 'Decisions' | 'Governance' | 'Audit' | 'Admin'; icon: string; path: string }> = [
  { id: 'briefing', label: 'Briefing', group: 'Work', icon: '⌁', path: '/ui/briefing' },
  { id: 'goals', label: 'Goals', group: 'Work', icon: '◎', path: '/ui/goals/new' },
  { id: 'decisions', label: 'Decision queue', group: 'Decisions', icon: '◇', path: '/ui/decisions' },
  { id: 'governance', label: 'Policies', group: 'Governance', icon: '◈', path: '/ui/governance/policies' },
  { id: 'audit', label: 'Audit traces', group: 'Audit', icon: '☷', path: '/ui/audit/traces' },
  { id: 'admin', label: 'Users', group: 'Admin', icon: '◉', path: '/ui/admin/users' },
  { id: 'profile', label: 'Profile', group: 'Admin', icon: '☼', path: '/ui/profile' }
];

function App() {
  const [mode, setMode] = React.useState<ModePreference>(() => readStoredMode());
  const [route, setRoute] = React.useState<RouteId>(() => routeFromHash());
  const [navOpen, setNavOpen] = React.useState(false);

  React.useEffect(() => {
    const root = document.documentElement;
    root.dataset.modePreference = mode;
    window.localStorage.setItem(modeStorageKey, mode);

    const applyResolvedMode = () => {
      const systemDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      root.dataset.mode = mode === 'system' ? (systemDark ? 'dark' : 'light') : mode;
    };

    applyResolvedMode();
    const media = window.matchMedia('(prefers-color-scheme: dark)');
    media.addEventListener('change', applyResolvedMode);
    return () => media.removeEventListener('change', applyResolvedMode);
  }, [mode]);

  React.useEffect(() => {
    const onHashChange = () => setRoute(routeFromHash());
    window.addEventListener('hashchange', onHashChange);
    return () => window.removeEventListener('hashchange', onHashChange);
  }, []);

  function navigate(nextRoute: RouteId) {
    setRoute(nextRoute);
    setNavOpen(false);
    window.location.hash = nextRoute;
    requestAnimationFrame(() => document.getElementById('main-content')?.focus());
  }

  return (
    <AppShell
      route={route}
      mode={mode}
      navOpen={navOpen}
      onNavigate={navigate}
      onModeChange={setMode}
      onToggleNav={() => setNavOpen((open) => !open)}
      onCloseNav={() => setNavOpen(false)}
    />
  );
}

function AppShell({
  route,
  mode,
  navOpen,
  onNavigate,
  onModeChange,
  onToggleNav,
  onCloseNav
}: {
  route: RouteId;
  mode: ModePreference;
  navOpen: boolean;
  onNavigate: (route: RouteId) => void;
  onModeChange: (mode: ModePreference) => void;
  onToggleNav: () => void;
  onCloseNav: () => void;
}) {
  const activeRoute = routes.find((item) => item.id === route) ?? routes[0];

  return (
    <div className="app-shell">
      <a className="skip-link" href="#main-content">Skip to main content</a>
      <SidebarNav activeRoute={route} open={navOpen} onNavigate={onNavigate} onClose={onCloseNav} />
      {navOpen && <button type="button" className="nav-backdrop" aria-label="Close navigation" onClick={onCloseNav} />}

      <div className="main-column">
        <header className="topbar">
          <button type="button" className="mobile-menu-button" aria-expanded={navOpen} aria-controls="sidebar-navigation" onClick={onToggleNav}>
            ☰ <span>Menu</span>
          </button>
          <TenantSwitcher />
          <div className="topbar-actions">
            <NotificationsButton />
            <ThemeModeToggle mode={mode} onModeChange={onModeChange} />
            <UserMenu />
          </div>
        </header>

        <main id="main-content" className="content" tabIndex={-1}>
          <PageHeader route={activeRoute} />
          <RouteShell route={route} mode={mode} onModeChange={onModeChange} />
        </main>
      </div>
    </div>
  );
}

function SidebarNav({ activeRoute, open, onNavigate, onClose }: { activeRoute: RouteId; open: boolean; onNavigate: (route: RouteId) => void; onClose: () => void }) {
  const grouped = groupRoutes(routes);
  return (
    <aside id="sidebar-navigation" className={open ? 'sidebar open' : 'sidebar'} aria-label="Seed app navigation">
      <div className="brand-lockup" aria-label="AI-First SaaS Seed">
        <span className="brand-mark" aria-hidden="true">✦</span>
        <div>
          <strong>AI-First SaaS Seed</strong>
          <span>Supervisory console</span>
        </div>
      </div>
      <nav className="nav-groups" aria-label="Primary navigation">
        {Object.entries(grouped).map(([group, items]) => (
          <section key={group} className="nav-group" aria-labelledby={`nav-group-${group}`}>
            <h2 id={`nav-group-${group}`}>{group}</h2>
            <div className="nav-list">
              {items.map((item) => (
                <button key={item.id} type="button" className={activeRoute === item.id ? 'nav-item active' : 'nav-item'} aria-current={activeRoute === item.id ? 'page' : undefined} onClick={() => onNavigate(item.id)}>
                  <span className="nav-icon" aria-hidden="true">{item.icon}</span>
                  <span>{item.label}</span>
                </button>
              ))}
            </div>
          </section>
        ))}
      </nav>
      <div className="sidebar-bottom">
        <div className="notification-summary" aria-label="Notification summary"><span className="notification-dot">3</span> pending reviews</div>
        <button type="button" className="collapse-button" onClick={onClose}>Collapse</button>
      </div>
    </aside>
  );
}

function TenantSwitcher() {
  return (
    <button type="button" className="tenant-switcher" aria-label="Current tenant: Seed tenant. Tenant switching placeholder.">
      <span className="tenant-avatar" aria-hidden="true">ST</span>
      <span><strong>Seed tenant</strong><small>Fixture context</small></span>
    </button>
  );
}

function NotificationsButton() {
  return <button type="button" className="icon-ghost" aria-label="3 notifications requiring attention">🔔<span className="notification-dot">3</span></button>;
}

function ThemeModeToggle({ mode, onModeChange }: { mode: ModePreference; onModeChange: (mode: ModePreference) => void }) {
  return (
    <fieldset className="mode-toggle">
      <legend>Display mode</legend>
      {(['light', 'dark', 'system'] as const).map((option) => (
        <label key={option} className={mode === option ? 'mode-choice selected' : 'mode-choice'}>
          <input type="radio" name="mode" value={option} checked={mode === option} onChange={() => onModeChange(option)} />
          <span>{option}</span>
        </label>
      ))}
    </fieldset>
  );
}

function UserMenu() {
  return (
    <button type="button" className="user-menu" aria-label="User menu for seed supervisor">
      <span className="user-avatar" aria-hidden="true">SS</span>
      <span><strong>Seed Supervisor</strong><small>Supervisor</small></span>
    </button>
  );
}

function PageHeader({ route }: { route: (typeof routes)[number] }) {
  return (
    <header className="page-header">
      <p className="eyebrow">{route.group}</p>
      <h1>{route.label}</h1>
      <p>{pageSubtitle(route.id)}</p>
    </header>
  );
}

function RouteShell({ route, mode, onModeChange }: { route: RouteId; mode: ModePreference; onModeChange: (mode: ModePreference) => void }) {
  if (route === 'profile') {
    return <ProfilePreferences mode={mode} onModeChange={onModeChange} />;
  }

  return (
    <section className="route-shell" aria-labelledby={`${route}-shell-heading`}>
      <div className="command-strip">
        <div className="ai-mark" aria-hidden="true">✦</div>
        <div>
          <h2 id={`${route}-shell-heading`}>{routeShellTitle(route)}</h2>
          <p>{routeShellCopy(route)}</p>
          <div className="prompt-row" aria-label="Example prompts">
            <button type="button" className="prompt-chip">Summarize blockers</button>
            <button type="button" className="prompt-chip">Review exceptions</button>
            <button type="button" className="prompt-chip">Explain policy triggers</button>
          </div>
        </div>
        <button type="button" className="icon-button" aria-label="Send command preview">➤</button>
      </div>

      <div className="card-grid">
        <article className="kpi-card"><span className="status-pill success">Ready · shell route</span><h2>Route mounted</h2><p>This slice validates route shells before product data is wired.</p></article>
        <article className="kpi-card"><span className="status-pill warning">Pending · fixture client</span><h2>Client seam next</h2><p>Slice 3 will add typed clients and fixture-backed data.</p></article>
        <article className="kpi-card"><span className="status-pill danger">Guarded · backend authority</span><h2>UX only</h2><p>Route visibility is not an authorization grant; backend APIs remain authoritative.</p></article>
      </div>
    </section>
  );
}

function ProfilePreferences({ mode, onModeChange }: { mode: ModePreference; onModeChange: (mode: ModePreference) => void }) {
  return (
    <section className="panel" aria-labelledby="profile-preferences-heading">
      <div>
        <p className="eyebrow">Profile</p>
        <h2 id="profile-preferences-heading">Display preferences</h2>
        <p>The root element stores <code>data-mode</code> and <code>data-mode-preference</code>. Future themes may override only color and font tokens.</p>
      </div>
      <ThemeModeToggle mode={mode} onModeChange={onModeChange} />
    </section>
  );
}

function routeFromHash(): RouteId {
  const hash = window.location.hash.replace(/^#/, '');
  return routes.some((item) => item.id === hash) ? hash as RouteId : 'briefing';
}

function readStoredMode(): ModePreference {
  const stored = window.localStorage.getItem(modeStorageKey);
  return stored === 'light' || stored === 'dark' || stored === 'system' ? stored : 'system';
}

function groupRoutes(items: typeof routes) {
  return items.reduce<Record<string, typeof routes>>((groups, item) => {
    groups[item.group] = [...(groups[item.group] ?? []), item];
    return groups;
  }, {});
}

function pageSubtitle(route: RouteId) {
  return {
    briefing: 'Supervise autonomous work, exceptions, policy edges, and strategic decisions.',
    goals: 'Turn intent into durable goals, plan reviews, tool permissions, and launch gates.',
    decisions: 'Review recommendations, evidence, risk, policy triggers, and allowed actions.',
    governance: 'Manage policies, proposals, simulations, commits, and rollback context.',
    audit: 'Search traces by goal, agent, decision, policy, tool, actor, and time.',
    admin: 'Manage users, invitations, roles, and tenant access seams.',
    profile: 'Manage display mode and local user preferences.'
  }[route];
}

function routeShellTitle(route: RouteId) {
  return {
    briefing: 'Mission control shell',
    goals: 'Goal workbench shell',
    decisions: 'Decision review shell',
    governance: 'Governance center shell',
    audit: 'Audit explorer shell',
    admin: 'Admin users shell',
    profile: 'Profile preferences'
  }[route];
}

function routeShellCopy(route: RouteId) {
  return {
    briefing: 'Future panels will show KPI bands, agent activity, attention queues, trust controls, and upcoming actions.',
    goals: 'Future panels will capture objectives, success criteria, constraints, draft plans, and approval gates.',
    decisions: 'Future panels will show decision cards with evidence, risk, confidence, impact, policy triggers, and trace links.',
    governance: 'Future panels will show policy versions, proposals, simulations, human-authorized commits, and audit links.',
    audit: 'Future panels will show trace filters, chronological trace results, authorization basis, and correlation ids.',
    admin: 'Future panels will show invitations, role assignments, validation, and high-impact confirmations.',
    profile: 'Manage local display preferences.'
  }[route];
}

createRoot(document.getElementById('root')!).render(<App />);
