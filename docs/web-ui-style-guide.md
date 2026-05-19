# Web UI style guide and AI-first style selection

Use this document whenever an app includes a browser UI. The visual style is part of the maintained app specification, not an implementation detail that frontend generation may invent later.

## Policy

Generated full-stack SaaS apps must use an AI-first supervision-oriented UI system by default. Do not select older dashboard/CRM/project-management visual styles for new generated AI-first SaaS apps; those patterns overfit conventional CRUD/analytics pages and do not make delegated work, authority, policy, evidence, decisions, traces, and outcomes prominent enough.

Style selection is intentionally narrow:

1. **Use the canonical AI-first style system** unless the user provides a custom brand brief or design system.
2. **Use `custom`** only when the user supplies enough tokens/component rules to preserve the AI-first UX anatomy.
3. **Do not offer or invent generic dashboard/CRM/project-management style choices**; those are outside the AI-first SaaS UI model.

## Authoritative locations

Record the selected style in the smallest authoritative artifact available:

- description-first apps: `app-description/55-ui/style-guide.md`
- specs/backlog apps: `specs/cross-cutting/NN-ui-style-guide.md` or an equivalent UI slice spec referenced by every web UI task

A generated web UI must read from that style guide before producing HTML, CSS, TypeScript, or JavaScript. For generated full-stack AI-first SaaS, the browser UI is mandatory; if no style has been selected, add a durable `category: ui` question to `specs/pending-questions.md` before creating or executing web UI implementation tasks.

## Required style-guide fields

Every app UI style guide should define:

- selected AI-first style id and name, or `unselected`
- source reference: this document, a custom design reference, or a user-provided brand brief
- light/dark/system mode policy
- brand adaptations: app name, logo/icon treatment, product-specific accent allowances, forbidden copied demo names/logos
- layout shell: sidebar/topbar/footer presence, max width, grid density, card density, navigation style
- typography: font family, scale, weights, line heights, numeric/table conventions
- color tokens: CSS variables for surfaces, text, borders, primary/accent colors, status colors, chart colors, focus rings, and shadows
- spacing/radius/elevation tokens
- component rules: command strip, decision cards, cards, buttons, forms, tables/lists, charts, badges, empty/error/loading states, toasts/modals where applicable
- accessibility constraints: contrast, focus visibility, color-not-alone status semantics, reduced motion expectations
- generated asset expectations: `index.html` uses semantic landmarks, `app.css` defines tokens as CSS variables, TypeScript toggles only documented state/classes rather than hard-coded styling decisions

## Style guide artifact template

```md
# Web UI Style Guide

## Selection
- selected style: <atlas-ops-supervisory-console | custom | unselected>
- style name: <name>
- source reference: <docs/web-ui-style-guide.md | custom brief path | user answer>
- mode policy: <system with light/dark tokens | light-only | dark-only>
- status: <selected | pending-question | deferred-with-default>

## Brand adaptation
- app/product name:
- logo/icon treatment:
- copied-demo-content rule: do not copy demo product names, logos, user names, or metrics from reference material
- custom brand overrides:

## Design tokens
- typography:
- spacing:
- radius:
- elevation:
- colors:
  - surfaces:
  - text:
  - primary/accent:
  - AI accent:
  - status:
  - charts:
  - focus:

## Component style rules
- shell/navigation:
- AI command strip:
- KPI summary cards:
- decision/exception cards:
- agent activity timeline/cards:
- governance/trust controls:
- cards/panels:
- buttons/actions:
- forms:
- tables/lists:
- charts/data visualization:
- loading/empty/error/success states:

## Accessibility and responsive constraints
- contrast:
- focus:
- keyboard:
- status semantics:
- narrow-screen layout:
- reduced motion:

## Implementation notes
- CSS variable prefix:
- files expected to apply this guide:
- tests/manual checks:
```

## Default style selection question

Use this durable pending-question shape when style is missing:

```md
### Q-<next>: Select web UI style guide

- status: pending
- priority: blocking
- category: ui
- depends on: []
- blocks:
  - web UI implementation and generation tasks only
- source:
  - app-description/55-ui/style-guide.md or specs/cross-cutting/NN-ui-style-guide.md is missing or unselected
- question: >
    Which visual style should generated web UI content use?
- why it matters: >
    The selected style drives generated HTML structure, CSS variables, spacing, typography, chart colors, component states, accessibility review, and regeneration consistency. Without it the harness would have to invent visual style during implementation.
- options:
  - A: atlas-ops-supervisory-console — recommended canonical AI-first SaaS supervision UI
  - B: custom — user will provide a custom style brief or reference that preserves AI-first supervision, decision, governance, audit, and outcome surfaces
- default if deferred: none for production generation; atlas-ops-supervisory-console may be accepted explicitly for early evaluation only
- answer: none
- decision: pending
- decision impact: pending
- reconciled into:
  - none
```

When answered, reconcile the decision into `app-description/55-ui/style-guide.md` and/or the relevant `specs/cross-cutting/*ui-style-guide*.md` before marking the question `resolved`.

## Canonical AI-first style system: `atlas-ops-supervisory-console`

Use this as the default choice for generated AI-first SaaS apps. It is an operational supervision interface for delegated agent work, not a generic dashboard skin.

### Visual intent

- calm operational SaaS interface for supervising delegated agent work
- prioritizes decisions, exceptions, policy boundaries, auditability, and outcome visibility over decorative chrome
- makes autonomous activity visible without hiding consequential work in chat transcripts
- supports light, dark, and system mode with equivalent hierarchy and contrast

### Style customization scope

Lightweight style customization may change only:

- color tokens
- font-family tokens
- product name, logo/icon treatment, and safe brand accents

It must not change:

- surface inventory
- navigation structure
- grid behavior
- card anatomy
- component sizes
- spacing scale
- border radii
- icon/status semantics
- UX copy rules
- accessibility constraints

### Font tokens

```css
:root {
  --font-sans: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
  --font-mono: "Roboto Mono", "SFMono-Regular", Consolas, monospace;
}
```

### Shared non-style tokens

```css
:root {
  --space-1: 0.25rem;
  --space-2: 0.5rem;
  --space-3: 0.75rem;
  --space-4: 1rem;
  --space-5: 1.25rem;
  --space-6: 1.5rem;
  --space-8: 2rem;

  --radius-sm: 0.5rem;
  --radius-md: 0.75rem;
  --radius-lg: 1rem;
  --radius-xl: 1.25rem;
  --radius-pill: 999px;

  --shell-sidebar-width: 16rem;
  --content-max-width: 96rem;
  --card-padding: 1rem;
  --control-height: 2.5rem;
}
```

### Light mode color tokens

```css
:root,
[data-mode="light"] {
  --color-bg: #f8fafc;
  --color-bg-subtle: #f1f5f9;
  --color-sidebar: #ffffff;
  --color-surface: #ffffff;
  --color-surface-raised: #ffffff;
  --color-surface-soft: #f8fafc;
  --color-surface-accent: #f5f3ff;

  --color-text: #0f172a;
  --color-text-soft: #334155;
  --color-muted: #64748b;
  --color-inverse-text: #ffffff;

  --color-border: #e2e8f0;
  --color-border-strong: #cbd5e1;

  --color-primary: #2563eb;
  --color-primary-strong: #1d4ed8;
  --color-primary-soft: #dbeafe;
  --color-ai: #7c3aed;
  --color-ai-soft: #ede9fe;

  --color-success: #16a34a;
  --color-success-soft: #dcfce7;
  --color-warning: #f59e0b;
  --color-warning-soft: #fef3c7;
  --color-danger: #ef4444;
  --color-danger-soft: #fee2e2;
  --color-info: #0ea5e9;
  --color-info-soft: #e0f2fe;

  --color-chart-blue: #2563eb;
  --color-chart-violet: #8b5cf6;
  --color-chart-green: #22c55e;
  --color-chart-amber: #f59e0b;
  --color-chart-red: #ef4444;
  --color-chart-cyan: #06b6d4;

  --color-focus: #2563eb;
  --shadow-card: 0 16px 40px rgb(15 23 42 / 0.08);
  --shadow-glow-ai: 0 0 0 1px rgb(124 58 237 / 0.18), 0 20px 60px rgb(124 58 237 / 0.10);
}
```

### Dark mode color tokens

```css
[data-mode="dark"] {
  --color-bg: #07111f;
  --color-bg-subtle: #0b1626;
  --color-sidebar: #070d18;
  --color-surface: #101a28;
  --color-surface-raised: #142033;
  --color-surface-soft: #0d1826;
  --color-surface-accent: #17152d;

  --color-text: #f8fafc;
  --color-text-soft: #cbd5e1;
  --color-muted: #94a3b8;
  --color-inverse-text: #ffffff;

  --color-border: #243247;
  --color-border-strong: #38506d;

  --color-primary: #3b82f6;
  --color-primary-strong: #60a5fa;
  --color-primary-soft: #1e3a8a;
  --color-ai: #a855f7;
  --color-ai-soft: #2e185c;

  --color-success: #4ade80;
  --color-success-soft: #123524;
  --color-warning: #fbbf24;
  --color-warning-soft: #3a2a08;
  --color-danger: #f87171;
  --color-danger-soft: #3b1218;
  --color-info: #38bdf8;
  --color-info-soft: #0b3145;

  --color-chart-blue: #3b82f6;
  --color-chart-violet: #a855f7;
  --color-chart-green: #4ade80;
  --color-chart-amber: #fbbf24;
  --color-chart-red: #f87171;
  --color-chart-cyan: #22d3ee;

  --color-focus: #93c5fd;
  --shadow-card: 0 18px 50px rgb(0 0 0 / 0.28);
  --shadow-glow-ai: 0 0 0 1px rgb(168 85 247 / 0.30), 0 20px 70px rgb(76 29 149 / 0.30);
}
```

## Required AI-first component anatomy

### App shell

- left sidebar on desktop, approximately `--shell-sidebar-width`
- product logo at top
- primary nav items first, then grouped sections such as Intelligence and Governance
- notifications and current user profile pinned near the bottom
- active item uses soft primary background plus primary icon/text color
- unavailable sections are hidden or disabled based on capabilities; backend authorization remains authoritative

### Main content

- responsive content grid with maximum width token
- surface title and subtitle above the AI command strip
- dashboard regions use a 12-column grid on wide screens
- cards stack by priority on narrow screens
- decision/exception queues remain above lower-priority reports on narrow screens

### AI command strip

Purpose: let users ask for summaries, risk explanations, and safe actions without making chat the source of truth.

Required elements:

- AI icon or mark
- command input/prompt adapted to the app name
- suggested prompt chips
- send/action button
- visual distinction through AI accent token and subtle glow/border

Consequential command results must become durable goals, decisions, approvals, policy proposals, or traceable actions.

### KPI summary cards

Required elements:

- label
- current value
- trend/delta with direction and text
- optional icon or sparkline
- status color plus text, not color alone

### Decision and exception cards

Required elements:

- subject/entity name
- originating agent or system
- status/risk/policy badge
- recommendation
- reason/evidence summary
- impact or confidence when available
- primary action
- secondary evidence/details action

### Agent activity timeline

Required elements:

- timestamp
- agent name
- action summary
- automation/review/escalation badge
- icon with semantic color
- detail affordance

### Agent/team cards

Required elements:

- agent/team name
- autonomy or trust metric when relevant
- trend
- recent action count
- success or quality metric
- active/blocked/requires-input state

### Governance/trust controls

Required elements:

- policy or control name
- configured threshold/authority
- enabled/verified status
- link to edit policy guardrails or inspect policy history

### Data visualization

- charts use selected style chart tokens only
- legends are required for donut, map, and multi-series charts
- status meaning must be represented with label text and/or icons, not color alone
- critical metrics use tabular numerals

## Required surface patterns

Generated AI-first SaaS UIs should favor these surfaces over generic CRUD dashboards:

- **Mission Control / Briefing:** surface framing, AI command strip, operational KPI band, agent execution timeline, needs-your-attention queue, agent teams/trust summary, trust controls, upcoming autonomous actions.
- **Goal Workbench:** objective form, success criteria, constraints, proposed execution plan, agent/team assignment, tool/data permissions, approval gates, launch simulation/review action.
- **Decision Queue and Decision Detail:** filters by priority/policy/agent/due time, recommendation summary, evidence/risk, alternatives, approve/reject/counter/defer/escalate actions, trace and outcome links.
- **Governance Center:** policy list and versions, thresholds, authority boundaries, proposed changes, simulations/replays, human-authorized commit flow, rollback and audit links.
- **Audit Trace Explorer:** search/filter by goal, agent, decision, tool, user, policy, time; chronological trace entries; evidence/tool/data-access details; authorization and policy invocation details; outcome links.

## Applying the style safely

1. Replace demo names, logos, users, and metrics with the target app's domain.
2. Keep the AI-first component anatomy, not literal mockup content.
3. Define tokens once in `app.css`; TypeScript may toggle mode classes/attributes only when the style guide permits mode switching.
4. Preserve accessibility even when adapting brand colors: contrast and focus tokens override decorative brand fidelity.
5. If users request custom styling later, update the authoritative style guide first, then regenerate affected web UI assets.

## Removed generic style-gallery choices

The skills pack no longer exposes multiple generic dashboard/CRM/admin style choices for generated AI-first SaaS apps. Treat any old gallery-style id as obsolete source history, not as an available planning option. When encountered in an existing artifact, migrate the authoritative style guide to `atlas-ops-supervisory-console` or a user-supplied `custom` style guide that preserves the required AI-first component anatomy.
