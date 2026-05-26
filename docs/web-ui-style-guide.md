# Web UI style guide and AI-first style selection

Use this document whenever an app includes a browser UI. The visual style is part of the maintained app specification, not an implementation detail that frontend generation may invent later.

## Policy

Generated full-stack SaaS apps must use an AI-first supervision-oriented UI system by default. Do not select older dashboard/CRM/project-management visual styles for new generated AI-first SaaS apps; those patterns overfit conventional CRUD/analytics pages and do not make delegated work, authority, policy, evidence, decisions, traces, and outcomes prominent enough.

Visual design is a cosmetic realization layer only. It must make the existing workstream shell, structured surfaces, capability-backed actions, authority state, audit/trace links, accessibility, and responsive behavior clearer and more polished; it must not add, remove, rename, or reinterpret functional agents, surfaces, capabilities, authorization rules, routes, API contracts, tests, or readiness semantics.

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
- visual direction: concise aesthetic point of view, tone, memorable motif, and forbidden generic patterns
- light/dark/system mode policy
- brand adaptations: app name, logo/icon treatment, product-specific accent allowances, forbidden copied demo names/logos
- layout shell: sidebar/topbar/footer presence, max width, grid density, card density, navigation style
- typography: font family, scale, weights, line heights, numeric/table conventions, and fallback strategy
- color tokens: CSS variables for surfaces, text, borders, primary/accent colors, status colors, chart colors, focus rings, and shadows
- spacing/radius/elevation tokens
- component rules: command strip, decision cards, cards, buttons, forms, tables/lists, charts, badges, empty/error/loading states, toasts/modals where applicable
- motion and texture: purposeful transition rules, reduced-motion behavior, background depth, borders, shadows, and decorative texture limits
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

## Visual direction
- aesthetic point of view:
- tone:
- memorable motif:
- forbidden generic patterns:

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
- motion/transition rules:
- background/texture/elevation rules:

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
- warm dark-console foundation by default: near-black green/charcoal backgrounds, layered low-contrast panels, orange primary controls/icons, muted coral AI/highlight accents, and semantic status colors
- prioritizes decisions, exceptions, policy boundaries, auditability, and outcome visibility over decorative chrome
- feels intentionally designed rather than like a generic admin dashboard, CRM, or chat app
- makes autonomous activity visible without hiding consequential work in chat transcripts
- supports light, dark, and system mode with equivalent hierarchy and contrast

### Cosmetic craft rules

Use these rules to improve visual quality without changing product behavior:

- Choose a clear aesthetic direction for the app or brand adaptation, such as operational mission control, editorial command desk, industrial trust console, or refined governance cockpit.
- Avoid generic AI UI clichés: purple gradients on white, undifferentiated cards, equal visual weight everywhere, decorative chat bubbles for consequential work, and styling that could belong to any SaaS dashboard.
- Typography should feel intentional. Use a readable warm sans for most UI copy and reserve monospace for code, trace ids, dense metrics, and technical labels. Custom styles may replace the default font tokens when the app records accessible fallbacks, readable line heights, and tabular/numeric conventions for dense operational data.
- Use color, borders, shadows, texture, and depth to clarify hierarchy: human-needed work, policy-blocked work, autonomous progress, trace/history, and FYI activity should not look interchangeable.
- Motion should be purposeful and state-driven: surface append/update, agent-working, approval result, stale/reconnect, expansion, and denial/recovery transitions may be polished, but must preserve reduced-motion support and never obscure state or audit evidence.
- Background treatments, grain, gradients, dotted textures, patterns, and glow effects are acceptable only when implemented through documented tokens/classes and when contrast, focus visibility, and surface readability remain intact. Prefer a very subtle dotted field over visible diagonal striping.
- Dark mode should avoid pure black and avoid default cool blue-black dashboard styling; prefer warm near-black/charcoal surfaces with subtle orange/coral glow and hairline borders.

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
  --font-sans: Inter, "Instrument Sans", ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
  --font-mono: "JetBrains Mono", "IBM Plex Mono", "SFMono-Regular", "SF Mono", Consolas, ui-monospace, monospace;
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
  --color-bg: #fbf7ef;
  --color-bg-subtle: #f4ecdf;
  --color-sidebar: #fffaf2;
  --color-surface: #fffdf8;
  --color-surface-raised: #ffffff;
  --color-surface-soft: #fbf5eb;
  --color-surface-accent: #fff0d6;

  --color-text: #1f1712;
  --color-text-soft: #4b3a31;
  --color-muted: #7a6a60;
  --color-inverse-text: #120f0b;

  --color-border: #eadfce;
  --color-border-strong: #d7c4aa;

  --color-primary: #ff9f1c;
  --color-primary-strong: #d97706;
  --color-primary-soft: #fff0d6;
  --color-ai: #c75a6f;
  --color-ai-soft: #fde8ee;

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
  --color-chart-amber: #ff9f1c;
  --color-chart-red: #ef4444;
  --color-chart-cyan: #06b6d4;

  --color-focus: #d97706;
  --shadow-card: 0 16px 40px rgb(50 32 12 / 0.08);
  --shadow-glow-ai: 0 0 0 1px rgb(199 90 111 / 0.16), 0 18px 50px rgb(199 90 111 / 0.12);
}
```

### Dark mode color tokens

```css
[data-mode="dark"] {
  --color-bg: #050a08;
  --color-bg-subtle: #0a100d;
  --color-sidebar: #070c0a;
  --color-surface: #111914;
  --color-surface-raised: #17211b;
  --color-surface-soft: #0c1410;
  --color-surface-accent: #241c12;

  --color-text: #f4eee6;
  --color-text-soft: #d4c7ba;
  --color-muted: #8d9a91;
  --color-inverse-text: #120f0b;

  --color-border: #2a3930;
  --color-border-strong: #46584c;

  --color-primary: #ff9f1c;
  --color-primary-strong: #ffb547;
  --color-primary-soft: #3a2710;
  --color-ai: #d65f73;
  --color-ai-soft: #341820;

  --color-success: #4ade80;
  --color-success-soft: #123524;
  --color-warning: #fbbf24;
  --color-warning-soft: #3a2a08;
  --color-danger: #f87171;
  --color-danger-soft: #3b1218;
  --color-info: #38bdf8;
  --color-info-soft: #0b3145;

  --color-chart-blue: #60a5fa;
  --color-chart-violet: #b47cff;
  --color-chart-green: #4ade80;
  --color-chart-amber: #ff9f1c;
  --color-chart-red: #f87171;
  --color-chart-cyan: #22d3ee;

  --color-focus: #ffb547;
  --shadow-card: 0 18px 50px rgb(0 0 0 / 0.34);
  --shadow-glow-ai: 0 0 0 1px rgb(214 95 115 / 0.28), 0 22px 76px rgb(83 30 38 / 0.30);
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
