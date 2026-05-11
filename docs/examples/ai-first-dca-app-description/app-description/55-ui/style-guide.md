# Web UI Style Guide

## Selection

- selected theme: `theme-1-northpeak-analytics`
- theme name: Northpeak Analytics
- source reference: `docs/images/web-ui-theme-1.png` and user answer selecting `theme-1`
- mode policy: system with both light and dark tokens
- status: selected

## Brand adaptation

- app/product name: Agent-first DCA supplies autopilot reference UI
- logo/icon treatment: simple text or neutral operations icon; do not copy the Northpeak logo or demo marks
- copied-demo-content rule: do not copy demo product names, logos, user names, metrics, or chart data from reference images
- custom brand overrides: use supplies-autopilot terminology, decision-card labels, policy/evidence/risk language, and trace-oriented navigation from this app description

## Design tokens

- typography: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; 14px body; 12px labels/metadata; 20-24px page headings; 28-32px KPI numerals; weights 500/600/700 for labels, headings, and KPI values
- spacing: 4px base scale with 8px, 12px, 16px, 24px, and 32px layout steps
- radius: 10px controls; 12px cards and panels; 16px high-emphasis dashboard containers where useful
- elevation: subtle 1px borders plus light shadow `0 16px 40px rgb(15 23 42 / 0.08)`; dark mode should prefer borders/glow over heavy shadows
- colors:
  - surfaces:
    - light: `--color-bg: #f6f8fb`, `--color-sidebar: #ffffff`, `--color-surface: #ffffff`, `--color-surface-raised: #ffffff`
    - dark: `--color-bg: #07111f`, `--color-sidebar: #0b1626`, `--color-surface: #111f2f`, `--color-surface-raised: #142438`
  - text:
    - light: `--color-text: #0f172a`, `--color-muted: #64748b`
    - dark: `--color-text: #f8fafc`, `--color-muted: #94a3b8`
  - borders:
    - light: `--color-border: #e2e8f0`
    - dark: `--color-border: #26384d`
  - primary/accent:
    - light: `--color-primary: #2563eb`, `--color-primary-strong: #1d4ed8`, `--color-primary-soft: #dbeafe`
    - dark: `--color-primary: #3b82f6`, `--color-primary-strong: #60a5fa`, `--color-primary-soft: #1e3a8a`
  - status: success `#16a34a`, warning `#f59e0b`, danger `#ef4444`; status must also use labels/icons, not color alone
  - charts: blue `#3b82f6`, violet `#8b5cf6`, emerald `#34d399`, orange `#fb923c`, cyan `#22d3ee`
  - focus: use the current primary blue as a visible 2px focus ring with sufficient offset

## Component style rules

- shell/navigation: desktop left sidebar around 224-248px with icon plus label navigation; topbar with page context, search/filter affordance, and compact action/account area; collapse to top navigation or drawer on narrow screens
- cards/panels: airy 16-24px padding, rounded corners, restrained borders, clear title/metadata hierarchy; KPI cards may include icon chip, label, value, delta badge, and compact sparkline
- buttons/actions: primary actions use solid blue with white text; secondary actions use outlined or ghost treatment; destructive/reject actions require explicit labels and confirmation/context where appropriate
- forms: labels are always visible; validation messages are placed near controls; action forms for approval/rejection/suppression must show evidence, policy, risk/confidence, and trace context before submission
- tables/lists: clean separated rows, restrained borders, sticky or repeated headers for long queues, and explicit empty/loading/error states
- charts/data visualization: thin grid lines, clear legends, accessible labels, and non-color encodings where risk/status meaning is critical
- loading/empty/error/success states: use skeletons or compact progress indicators for loading, useful empty-state guidance, actionable error copy, and success confirmations that preserve decision/trace context

## Accessibility and responsive constraints

- contrast: meet WCAG AA for text and interactive elements in both light and dark modes
- focus: all interactive controls must have visible keyboard focus using the documented focus token
- keyboard: decision queues, detail cards, filters, and approval/rejection/suppression forms must be keyboard navigable
- narrow-screen layout: stack KPI cards and decision panels to one column on mobile; preserve decision evidence and trace links before action controls
- reduced motion: avoid essential animation; respect `prefers-reduced-motion` for transitions and chart animation

## Implementation notes

- CSS variable prefix: use global CSS custom properties with `--color-*`, `--space-*`, `--radius-*`, and `--shadow-*` names unless a frontend framework requires a wrapper
- files expected to apply this guide: supplies command-center UI, decision-card detail UI, API state rendering, static hosting assets, and frontend smoke tests generated for `TASK-08-006`
- tests/manual checks: verify selected theme tokens are centralized in CSS; check light/dark/system behavior; confirm approval/rejection/suppression UI keeps evidence, policy, risk/confidence, and trace context visible; verify keyboard focus and responsive layout
