# Web UI style guide and default themes

Use this document whenever an app includes a browser UI. The visual style is part of the app's maintained specification, not an implementation detail that frontend generation may invent later.

## Authoritative locations

Record the selected style in the smallest authoritative artifact available:

- description-first apps: `app-description/55-ui/style-guide.md`
- specs/backlog apps: `specs/cross-cutting/NN-ui-style-guide.md` or an equivalent UI slice spec referenced by every web UI task

A generated web UI should read from that style guide before producing HTML, CSS, TypeScript, or JavaScript. If a browser UI is in scope and no style has been selected, add a durable `category: ui` question to `specs/pending-questions.md` before creating or executing web UI implementation tasks.

## Required style-guide fields

Every app UI style guide should define:

- selected theme id and name, or `unselected`
- source reference: one of the default theme images, a custom design reference, or a user-provided brand brief
- light/dark/system mode policy
- brand adaptations: app name, logo/icon treatment, product-specific accent allowances, forbidden copied demo names/logos
- layout shell: sidebar/topbar/footer presence, max width, grid density, card density, navigation style
- typography: font family, scale, weights, line heights, numeric/table conventions
- color tokens: CSS variables for surfaces, text, borders, primary/accent colors, status colors, chart colors, focus rings, and shadows
- spacing/radius/elevation tokens
- component rules: cards, buttons, forms, tables/lists, charts, badges, empty/error/loading states, toasts/modals where applicable
- accessibility constraints: contrast, focus visibility, color-not-alone status semantics, reduced motion expectations
- generated asset expectations: `index.html` uses semantic landmarks, `app.css` defines tokens as CSS variables, TypeScript toggles only documented state/classes rather than hard-coded styling decisions

## Style guide artifact template

```md
# Web UI Style Guide

## Selection
- selected theme: <theme-id | custom | unselected>
- theme name: <name>
- source reference: <docs/images/... | custom brief path | user answer>
- mode policy: <light-only | dark-only | system with both light/dark tokens>
- status: <selected | pending-question | deferred-with-default>

## Brand adaptation
- app/product name:
- logo/icon treatment:
- copied-demo-content rule: do not copy demo product names, logos, user names, or metrics from reference images
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
  - status:
  - charts:
  - focus:

## Component style rules
- shell/navigation:
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
- narrow-screen layout:
- reduced motion:

## Implementation notes
- CSS variable prefix:
- files expected to apply this guide:
- tests/manual checks:
```

## CSS token baseline

Generated CSS should express the selected style with plain CSS variables. Prefer a token layer like:

```css
:root {
  --font-sans: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
  --space-1: 0.25rem;
  --space-2: 0.5rem;
  --space-3: 0.75rem;
  --space-4: 1rem;
  --space-6: 1.5rem;
  --radius-sm: 0.5rem;
  --radius-md: 0.75rem;
  --radius-lg: 1rem;
  --shadow-card: 0 12px 32px rgb(15 23 42 / 0.10);
  --color-bg: #f8fafc;
  --color-surface: #ffffff;
  --color-text: #0f172a;
  --color-muted: #64748b;
  --color-border: #e2e8f0;
  --color-primary: #2563eb;
  --color-focus: #2563eb;
  --color-success: #16a34a;
  --color-warning: #f59e0b;
  --color-danger: #ef4444;
}
```

Adapt names as needed, but keep style decisions centralized in CSS instead of scattered through TypeScript.

## Default theme selection question

Use this durable pending-question shape when style is missing:

```md
### Q-<next>: Select web UI style guide theme

- status: pending
- priority: blocking
- category: ui
- depends on: []
- blocks:
  - web UI implementation and generation tasks only
- source:
  - app-description/55-ui/style-guide.md or specs/cross-cutting/NN-ui-style-guide.md is missing or unselected
- question: >
    Which default visual theme should generated web UI content use?
- why it matters: >
    The selected theme drives generated HTML structure, CSS variables, spacing, typography, chart colors, component states, and accessibility review. Without it the harness would have to invent visual style during implementation.
- options:
  - A: theme-1-northpeak-analytics — clean blue SaaS analytics dashboard
  - B: theme-2-promanage-violet — violet project-management workspace
  - C: theme-3-nordic-crm-teal — teal CRM/data pipeline console
  - D: theme-4-finflow-emerald — emerald finance/invoicing operations UI
  - E: theme-5-acme-admin-blue — general blue admin/ops dashboard
  - F: custom — user will provide a custom style brief or reference
- default if deferred: none for production generation; theme-1-northpeak-analytics may be accepted explicitly for early evaluation only
- answer: none
- decision: pending
- decision impact: pending
- reconciled into:
  - none
```

When answered, reconcile the decision into `app-description/55-ui/style-guide.md` and/or the relevant `specs/cross-cutting/*ui-style-guide*.md` before marking the question `resolved`.

## Default themes

The images under `docs/images/` are style references, not source assets to copy. Do not copy their app names, logos, people, data, or exact mock content into generated apps.

### theme-1-northpeak-analytics

- source image: `docs/images/web-ui-theme-1.png`
- best fit: SaaS analytics, executive reporting, customer/revenue dashboards
- visual character: polished neutral dashboard with crisp cards, strong blue primary, multicolor metric accents, calm information hierarchy
- mode policy: system mode with equivalent light and dark palettes
- typography: Inter/system sans; 14px body, 12px labels, 20-24px page headings, 28-32px KPI numerals; font weights 500/600/700 for labels/headings/KPIs
- layout:
  - left sidebar around 224-248px on desktop with icon + label nav
  - topbar with global search and compact account/actions area
  - 12-column dashboard grid; KPI cards in 4-up desktop, 2-up tablet, 1-up mobile
  - cards use airy 16-24px padding and rounded corners
- light tokens:
  - `--color-bg: #f6f8fb`
  - `--color-sidebar: #ffffff`
  - `--color-surface: #ffffff`
  - `--color-surface-raised: #ffffff`
  - `--color-text: #0f172a`
  - `--color-muted: #64748b`
  - `--color-border: #e2e8f0`
  - `--color-primary: #2563eb`
  - `--color-primary-strong: #1d4ed8`
  - `--color-primary-soft: #dbeafe`
- dark tokens:
  - `--color-bg: #07111f`
  - `--color-sidebar: #0b1626`
  - `--color-surface: #111f2f`
  - `--color-surface-raised: #142438`
  - `--color-text: #f8fafc`
  - `--color-muted: #94a3b8`
  - `--color-border: #26384d`
  - `--color-primary: #3b82f6`
  - `--color-primary-strong: #60a5fa`
  - `--color-primary-soft: #1e3a8a`
- accents/charts: blue `#3b82f6`, violet `#8b5cf6`, emerald `#34d399`, orange `#fb923c`, cyan `#22d3ee`
- radius/elevation: 12px cards, 10px controls, subtle 1px borders, light shadow `0 16px 40px rgb(15 23 42 / 0.08)`; dark mode relies more on borders/glow than heavy shadows
- component rules:
  - KPI cards include icon chip, label, numeric value, delta badge, and optional compact sparkline
  - charts use thin grid lines and clear legends
  - tables are clean with separated rows and restrained borders
  - primary buttons are solid blue with white text; secondary buttons are outlined/ghost

### theme-2-promanage-violet

- source image: `docs/images/web-ui-theme-2.png`
- best fit: project management, task boards, planning portals, team workload dashboards
- visual character: collaborative workspace with violet emphasis, soft card surfaces, kanban/timeline affordances, friendly status colors
- mode policy: system mode with light and dark palettes
- typography: Inter/system sans; 14px body, 12px metadata, 18-22px section headings, compact task-card labels; medium weights for navigational labels
- layout:
  - fixed left project/navigation sidebar around 240px
  - task board columns scroll horizontally when needed
  - dashboard cards use tighter 12-16px padding than analytics themes
  - right-side support panels for workload/progress are acceptable on wide screens
- light tokens:
  - `--color-bg: #f7f7fb`
  - `--color-sidebar: #ffffff`
  - `--color-surface: #ffffff`
  - `--color-surface-alt: #f5f3ff`
  - `--color-text: #111827`
  - `--color-muted: #6b7280`
  - `--color-border: #e5e7eb`
  - `--color-primary: #6d5dfc`
  - `--color-primary-strong: #5846e8`
  - `--color-primary-soft: #ede9fe`
- dark tokens:
  - `--color-bg: #0b1020`
  - `--color-sidebar: #111827`
  - `--color-surface: #1a2333`
  - `--color-surface-alt: #241f3d`
  - `--color-text: #f9fafb`
  - `--color-muted: #a1a1aa`
  - `--color-border: #2f3b52`
  - `--color-primary: #8b7cff`
  - `--color-primary-strong: #a78bfa`
  - `--color-primary-soft: #312e81`
- accents/charts/status: violet `#8b5cf6`, blue `#3b82f6`, green `#34d399`, amber `#f59e0b`, rose `#f43f5e`
- radius/elevation: 10-14px cards, pill badges, soft shadows in light mode, low-contrast dark panels with clear column borders
- component rules:
  - kanban cards show title, priority chip, due date, assignee avatars, and status color
  - timeline bars use translucent category colors
  - workload bars use violet primary with muted tracks
  - primary action buttons use violet gradient or solid violet; keep destructive actions red/rose

### theme-3-nordic-crm-teal

- source image: `docs/images/web-ui-theme-3.png`
- best fit: CRM, sales pipeline, contacts/accounts, data-dense business consoles
- visual character: crisp teal/cyan identity with denser tables, pipeline visuals, professional sales dashboard tone
- mode policy: system mode with light and dark palettes
- typography: Inter/system sans; 14px body, 12px table labels, 22-28px page headings, prominent money/KPI numerals; table text should stay highly legible
- layout:
  - sidebar around 220px, topbar search prominent
  - content cards can be more data-dense with 16px padding
  - pipeline/funnel panels pair with adjacent tables/lists
  - data tables should remain readable on narrow screens through card-list fallback or horizontal scroll with sticky first column when appropriate
- light tokens:
  - `--color-bg: #f7fafc`
  - `--color-sidebar: #ffffff`
  - `--color-surface: #ffffff`
  - `--color-surface-alt: #ecfeff`
  - `--color-text: #0f172a`
  - `--color-muted: #64748b`
  - `--color-border: #dbe4ee`
  - `--color-primary: #0891b2`
  - `--color-primary-strong: #0e7490`
  - `--color-primary-soft: #cffafe`
- dark tokens:
  - `--color-bg: #07131d`
  - `--color-sidebar: #0b1722`
  - `--color-surface: #142333`
  - `--color-surface-alt: #0f2a34`
  - `--color-text: #f8fafc`
  - `--color-muted: #94a3b8`
  - `--color-border: #2a3f50`
  - `--color-primary: #22d3ee`
  - `--color-primary-strong: #67e8f9`
  - `--color-primary-soft: #164e63`
- accents/charts: cyan `#22d3ee`, blue `#3b82f6`, green `#22c55e`, violet `#8b5cf6`, amber `#f59e0b`
- radius/elevation: 10px cards, 999px compact badges, restrained shadows, strong border clarity
- component rules:
  - use clear table headers, aligned numeric columns, and subtle row dividers
  - entity avatars/initials use soft pastel tokens
  - pipeline charts use teal-to-blue gradients with labels outside chart shapes
  - upcoming task lists use colored icon chips and compact metadata

### theme-4-finflow-emerald

- source image: `docs/images/web-ui-theme-4.png`
- best fit: finance, invoicing, payments, cashflow, subscription/billing admin
- visual character: trustworthy operations dashboard with emerald/teal primary, clear financial status semantics, compact quick actions
- mode policy: system mode with light and dark palettes
- typography: Inter/system sans; 14px body, 12px metadata/table labels, tabular numerals for currency, 24-32px KPI values; use `font-variant-numeric: tabular-nums` for financial data
- layout:
  - sidebar around 240px with business/account switcher region
  - topbar can include product section label plus search/actions
  - KPI row supports 4-up desktop and readable currency emphasis
  - tables and quick actions use compact row height but generous touch targets
- light tokens:
  - `--color-bg: #f8fafc`
  - `--color-sidebar: #ffffff`
  - `--color-surface: #ffffff`
  - `--color-surface-alt: #ecfdf5`
  - `--color-text: #111827`
  - `--color-muted: #64748b`
  - `--color-border: #e2e8f0`
  - `--color-primary: #10b981`
  - `--color-primary-strong: #059669`
  - `--color-primary-soft: #d1fae5`
- dark tokens:
  - `--color-bg: #07131c`
  - `--color-sidebar: #091622`
  - `--color-surface: #142634`
  - `--color-surface-alt: #123528`
  - `--color-text: #f8fafc`
  - `--color-muted: #9ca3af`
  - `--color-border: #2a3b4a`
  - `--color-primary: #34d399`
  - `--color-primary-strong: #6ee7b7`
  - `--color-primary-soft: #064e3b`
- accents/status: paid/success `#22c55e`, sent/info `#3b82f6`, overdue/danger `#ef4444`, draft/neutral `#94a3b8`, warning/orange `#f97316`, teal `#14b8a6`
- radius/elevation: 10-12px panels, financial tables with low-shadow bordered cards, buttons 8-10px radius
- component rules:
  - financial values right-align and use tabular numerals
  - positive values use green with directional icons; negative/overdue use red with text labels, not color alone
  - charts should distinguish cash in, cash out, and net flow with both color and legend labels
  - quick actions use icon chips and compact button groups

### theme-5-acme-admin-blue

- source image: `docs/images/web-ui-theme-5.png`
- best fit: general admin portals, operations dashboards, customer/product/order back offices
- visual character: versatile blue admin dashboard with balanced cards, strong primary actions, readable tables, friendly multicolor icon accents
- mode policy: system mode with light and dark palettes
- typography: Inter/system sans; 14px body, 12px metadata, 22-28px page headings, 28px KPI numerals; headings use 650-700 weight
- layout:
  - sidebar around 248px with bottom help/user region
  - header supports search, notification, user, and primary/export actions
  - dashboard grid favors one large chart, side activity/action cards, and full-width tables
  - mobile collapses sidebar into menu and stacks cards by priority
- light tokens:
  - `--color-bg: #f7f9fc`
  - `--color-sidebar: #ffffff`
  - `--color-surface: #ffffff`
  - `--color-surface-alt: #eff6ff`
  - `--color-text: #111827`
  - `--color-muted: #667085`
  - `--color-border: #e5e7eb`
  - `--color-primary: #2563eb`
  - `--color-primary-strong: #1d4ed8`
  - `--color-primary-soft: #dbeafe`
- dark tokens:
  - `--color-bg: #0a1320`
  - `--color-sidebar: #0d1a29`
  - `--color-surface: #182536`
  - `--color-surface-alt: #10213a`
  - `--color-text: #f9fafb`
  - `--color-muted: #9ca3af`
  - `--color-border: #2d3c4f`
  - `--color-primary: #3b82f6`
  - `--color-primary-strong: #60a5fa`
  - `--color-primary-soft: #1e3a8a`
- accents/status: blue `#3b82f6`, violet `#8b5cf6`, green `#22c55e`, orange `#fb923c`, cyan `#22d3ee`, danger `#ef4444`
- radius/elevation: 12px cards, 10px buttons, soft light shadows, dark mode border-led depth
- component rules:
  - primary CTA buttons are solid blue; secondary action buttons are outlined with icons
  - activity feeds use circular icon chips with semantic colors
  - status pills use colored backgrounds plus readable text labels
  - tables use clear hover/focus states and avoid low-contrast muted text for important values

## Applying a default theme safely

1. Replace demo names, logos, users, and metrics with the target app's domain.
2. Keep the chosen theme's visual system, not its literal content.
3. Define tokens once in `app.css`; TypeScript may toggle theme classes such as `theme-dark` only when the style guide permits mode switching.
4. Preserve accessibility even when adapting brand colors: contrast and focus tokens override decorative brand fidelity.
5. If users request custom styling later, update the authoritative style guide first, then regenerate affected web UI assets.
