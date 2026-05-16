# Web UI Style Guide

## Selection

- selected style: `atlas-ops-supervisory-console`
- style name: Atlas Ops supervisory console
- source reference: `docs/web-ui-style-guide.md` canonical AI-first style system
- mode policy: system with explicit light and dark token sets
- status: selected

## Brand adaptation

- app/product name: AI-first DCA supplies autopilot reference UI
- logo/icon treatment: simple text or neutral operations icon; do not copy Atlas Ops or other demo marks
- copied-demo-content rule: do not copy demo product names, logos, user names, metrics, or chart data from reference material
- custom brand overrides: use supplies-autopilot terminology, decision-card labels, policy/evidence/risk language, and trace-oriented navigation from this app description

## Design tokens

- typography: Inter/system sans for UI text; Roboto Mono/SFMono-style monospace for ids, trace snippets, timestamps where useful; tabular numerals for queue counts, percentages, costs, times, and outcome metrics
- spacing: stable 4px-based scale from `--space-1` through `--space-8` as defined by `docs/web-ui-style-guide.md`
- radius: 8px controls, 12px standard cards, 16px prominent supervision panels, pill badges
- elevation: subtle card shadows in light mode; border-led depth and restrained AI glow in dark mode
- colors:
  - surfaces: use canonical Atlas Ops light/dark `--color-bg`, `--color-sidebar`, `--color-surface`, `--color-surface-raised`, `--color-surface-soft`, and `--color-surface-accent`
  - text: use canonical `--color-text`, `--color-text-soft`, `--color-muted`, and `--color-inverse-text`
  - borders: use canonical `--color-border` and `--color-border-strong`
  - primary/accent: use canonical blue primary tokens for primary UI actions
  - AI accent: use canonical violet `--color-ai` and `--color-ai-soft` for agent command/explanation affordances
  - status: success, warning, danger, and info tokens must also use labels/icons, not color alone
  - charts: use canonical chart tokens only
  - focus: use canonical `--color-focus` as a visible focus ring with sufficient offset

## Component style rules

- shell/navigation:
  - persistent left sidebar on desktop with primary operational navigation first, then Intelligence/Governance/Audit groupings
  - active section uses soft primary surface plus primary icon/text
  - user/account and notification region stays visible in the shell
  - mobile collapses navigation into a drawer/menu while preserving current context and primary decision actions
- AI command strip:
  - placed near the top of command-center, decision, governance, and audit work surfaces
  - includes AI icon, command input/prompt, suggested prompt chips, and send/action button
  - AI-commanded consequential work must resolve into durable goals, decisions, approvals, policy proposals, or traceable actions
- KPI summary cards:
  - show active goals, waiting decisions, exceptions, outcome deltas, and policy/trace health where relevant
  - include label, value, trend/status text, and optional icon/sparkline
- decision/exception cards:
  - show subject, originating agent/system, policy/risk badge, recommendation, evidence summary, impact/confidence, primary action, and secondary trace/evidence action
  - approval/rejection/suppression actions must keep evidence, policy, risk/confidence, and trace context visible before submission
- agent activity timeline/cards:
  - show agent/team name, recent action, automation/review/escalation badge, timestamp, active/blocked/requires-input state, and detail affordance
- governance/trust controls:
  - show policy/control name, configured threshold/authority, enabled/verified status, and links to policy history or guardrail editing
- cards/panels:
  - use clear headings, metadata subtitles, 1px borders, and consistent padding
  - high-attention panels use semantic accent treatment plus accessible labels
- buttons/actions:
  - primary actions use solid primary color
  - AI actions may use AI accent
  - destructive/high-impact actions require explicit labels and confirmation/context where appropriate
- forms:
  - labels are always visible
  - helper text explains constraints and authority boundaries
  - validation maps to field and form-level messages
- tables/lists:
  - use readable row spacing and persistent labels for status, agent, policy trigger, risk, and due time
  - dense rows may become cards on narrow screens
- charts/data visualization:
  - chart colors use tokenized chart palette
  - legends and text labels are required; color is never the only status channel
- loading/empty/error/success states:
  - skeletons for dashboard cards and queues
  - empty states explain what is absent and what the user can do next
  - errors include recovery action and retry where appropriate
  - success messages name the completed action and preserve decision/trace context

## Accessibility and responsive constraints

- contrast: meet WCAG AA for text and interactive elements in both light and dark modes
- focus: all interactive controls must have visible keyboard focus using `--color-focus`
- keyboard: decision queues, detail cards, filters, command strip, and approval/rejection/suppression forms must be keyboard navigable
- status semantics: status uses text plus color/icon treatment
- narrow-screen layout: stack KPI cards and decision panels to one column; preserve decision evidence and trace links before action controls
- reduced motion: avoid essential animation; respect `prefers-reduced-motion` for transitions and chart animation

## Implementation notes

- CSS variable prefix: use global CSS custom properties with `--color-*`, `--space-*`, `--radius-*`, `--shadow-*`, and `--font-*` names unless a frontend framework requires a wrapper
- files expected to apply this guide: supplies command-center UI, decision-card detail UI, API state rendering, static hosting assets, and frontend smoke tests generated for `TASK-08-006`
- tests/manual checks: verify selected style tokens are centralized in CSS; check light/dark/system behavior; confirm approval/rejection/suppression UI keeps evidence, policy, risk/confidence, and trace context visible; verify keyboard focus and responsive layout
