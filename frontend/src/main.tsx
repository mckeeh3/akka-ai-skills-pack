import React from 'react';
import { createRoot } from 'react-dom/client';
import './styles/tokens.css';
import './styles/base.css';
import './styles/layout.css';
import './styles/components.css';

type ModePreference = 'light' | 'dark' | 'system';

const modeStorageKey = 'seed-ui-mode';

function App() {
  const [mode, setMode] = React.useState<ModePreference>(() => readStoredMode());

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

  return <SeedFoundation mode={mode} onModeChange={setMode} />;
}

function SeedFoundation({ mode, onModeChange }: { mode: ModePreference; onModeChange: (mode: ModePreference) => void }) {
  return (
    <div className="foundation-shell">
      <a className="skip-link" href="#main-content">Skip to main content</a>
      <aside className="foundation-sidebar" aria-label="Seed app navigation preview">
        <div className="brand-lockup" aria-label="AI-First SaaS Seed">
          <span className="brand-mark" aria-hidden="true">✦</span>
          <div>
            <strong>AI-First SaaS Seed</strong>
            <span>Design foundation</span>
          </div>
        </div>

        <nav className="nav-list" aria-label="Planned app sections">
          {['Briefing', 'Goals', 'Decisions', 'Governance', 'Audit', 'Admin'].map((item, index) => (
            <button key={item} type="button" className={index === 0 ? 'nav-item active' : 'nav-item'} aria-current={index === 0 ? 'page' : undefined}>
              <span className="nav-icon" aria-hidden="true">{index === 0 ? '⌁' : '○'}</span>
              <span>{item}</span>
            </button>
          ))}
        </nav>

        <p className="sidebar-note">
          Slice 1 validates tokens, mode switching, layout, and focus behavior before app screens are implemented.
        </p>
      </aside>

      <main id="main-content" className="foundation-main" tabIndex={-1}>
        <header className="page-header">
          <p className="eyebrow">Localized frontend slice 1</p>
          <h1>Atlas Ops supervisory console foundation</h1>
          <p>
            Tokenized React/Vite/TypeScript foundation for the seed app. This page proves light, dark, and system mode behavior without implementing product screens yet.
          </p>
        </header>

        <section className="command-strip" aria-labelledby="command-strip-heading">
          <div className="ai-mark" aria-hidden="true">✦</div>
          <div>
            <h2 id="command-strip-heading">AI command strip preview</h2>
            <p>Ask the seed app to summarize agent work, explain risks, or route to a durable decision.</p>
            <div className="prompt-row" aria-label="Example prompts">
              <button type="button" className="prompt-chip">Summarize blockers</button>
              <button type="button" className="prompt-chip">Review exceptions</button>
              <button type="button" className="prompt-chip">Explain policy triggers</button>
            </div>
          </div>
          <button type="button" className="icon-button" aria-label="Send command preview">➤</button>
        </section>

        <section className="card-grid" aria-label="Token validation cards">
          <article className="kpi-card">
            <span className="status-pill success">Success · text label</span>
            <h2>Light/dark parity</h2>
            <p>Semantic tokens preserve hierarchy and contrast across modes.</p>
          </article>
          <article className="kpi-card">
            <span className="status-pill warning">Warning · text label</span>
            <h2>Color not alone</h2>
            <p>Status uses labels and icons in addition to color treatment.</p>
          </article>
          <article className="kpi-card">
            <span className="status-pill danger">Risk · text label</span>
            <h2>Visible focus</h2>
            <p>Keyboard users can see focus on navigation, chips, buttons, and links.</p>
          </article>
        </section>

        <section className="panel" aria-labelledby="mode-heading">
          <div>
            <p className="eyebrow">Theme mode</p>
            <h2 id="mode-heading">Choose display mode</h2>
            <p>The root element stores <code>data-mode</code> and <code>data-mode-preference</code>. Future themes may override only color and font tokens.</p>
          </div>
          <fieldset className="mode-options">
            <legend>Display mode</legend>
            {(['light', 'dark', 'system'] as const).map((option) => (
              <label key={option} className="mode-option">
                <input type="radio" name="mode" value={option} checked={mode === option} onChange={() => onModeChange(option)} />
                <span>{option}</span>
              </label>
            ))}
          </fieldset>
        </section>
      </main>
    </div>
  );
}

function readStoredMode(): ModePreference {
  const stored = window.localStorage.getItem(modeStorageKey);
  return stored === 'light' || stored === 'dark' || stored === 'system' ? stored : 'system';
}

createRoot(document.getElementById('root')!).render(<App />);
