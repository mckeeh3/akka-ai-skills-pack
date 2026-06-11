# Web UI Style Guide

## Selection

- selected style: `ai-first-workstream-enterprise`
- style name: AI-first Workstream Enterprise
- source reference:
  - `skills-pack/docs/web-ui-style-guide.md`
  - `skills-pack/examples/web-ui/ai-first-workstream-enterprise/README.md`
  - `skills-pack/examples/web-ui/ai-first-workstream-enterprise/*.html`
- status: selected

## Theme model

- user-facing model: named theme selection
- default theme id: `aurora-light`
- available themes:
  - `aurora-light`: Aurora Light; tone light for contrast testing only; calm off-white enterprise workspace with indigo accent
  - `cobalt-light`: Cobalt Light; tone light for contrast testing only; cool pale-blue workspace with stronger blue accent
  - `obsidian-dark`: Obsidian Dark; tone dark for contrast testing only; deep neutral command-desk workspace with indigo/violet accent
  - `midnight-dark`: Midnight Dark; tone dark for contrast testing only; blue-black operations workspace with crisp cyan-blue accent
  - `dark-night`: Dark Night; tone dark for contrast testing only; near-black high-contrast workspace with hot red primary accents, gold warning/ranking cues, and teal success/AI accents
- My Account behavior: users select one available named theme, the UI previews the selected theme immediately on field change, and Save/Confirm persists the selected theme through the governed settings action
- persistence scope: backend user settings, with local immediate preview before durable save completes
- future theme rule: add color-token bundles without changing workstream shell anatomy, surface contracts, component structure, design language, spacing scale, typography scale, icon/status semantics, route behavior, capability mapping, authorization, audit behavior, or tests
- mode rule: there is no dark/light/system mode; users choose named themes. Theme tone is metadata for contrast testing only. Changing theme changes colors, not layout, component anatomy, style/look, or behavior.

## Visual direction

- aesthetic point of view: dark-capable enterprise command desk for supervising delegated agent work; calm, dense, traceable, and high-confidence rather than generic dashboard/CRM chrome
- tone: refined operational cockpit with strong hierarchy, precise borders, controlled glow, functional color, and designed controls
- memorable motif: layered navy/charcoal mission-control surfaces, bright but sparse status accents, visible authority/trace rails, durable decision cards, and AI command strips that launch governed work
- differentiation target: the UI should be remembered as an operations-grade AI supervision cockpit, not as a generic SaaS dashboard; authority, risk, evidence, and traceability should be visible design features
- craft principles: commit to this aesthetic before coding; use typography, color tokens, borders, depth, texture, and purposeful motion to reinforce hierarchy; avoid timid evenly distributed palettes and undifferentiated cards
- forbidden generic patterns:
  - purple gradient blobs on white as the primary style
  - equal-weight dashboard cards everywhere
  - raw browser-default input/select/textarea controls
  - chat transcript as the only record of consequential work
  - generic `Submit`, `Success`, `Error occurred`, or `No data` copy for important states
  - copied demo logos, users, product names, or metrics from reference assets

## Brand adaptation

- app/product name: target app name from app description
- logo/icon treatment: compact product mark in the functional-agent rail; may use brand accent but must preserve contrast and not replace authority/status semantics
- copied-demo-content rule: do not copy demo product names, logos, user names, organization names, screenshots, or metrics from reference material
- custom brand overrides: limited to token bundles, product name, logo/icon treatment, safe accent tuning, and documented font choices unless the user provides a custom design system

## Design tokens

- typography:
  - display: distinctive readable sans such as `Sora`, `Aptos Display`, or product-approved display family with accessible fallback
  - body: refined UI sans such as `Geist`, `Aptos`, `SF Pro Text`, or system fallback
  - mono: trace ids, code, technical labels, and dense metrics only
  - numeric data uses tabular numerals where supported
- spacing: shared scale from `--space-1` through `--space-10`; no one-off spacing for generated surfaces unless recorded
- radius: `--radius-sm`, `--radius-md`, `--radius-lg`, `--radius-xl`, `--radius-pill`
- elevation: hairline borders first, then restrained shadows/glow for raised cards, command strips, decision urgency, and AI surfaces
- colors:
  - canvas/surfaces: layered neutral off-white for light themes and navy/charcoal/near-black for dark themes
  - text: high contrast body, softer secondary, muted metadata, disabled state
  - primary/accent: blue/indigo family by default, except `dark-night` red-accent variant
  - AI accent: distinct token separate from primary action where possible
  - status: green success, amber warning/review, red danger/critical, blue info; every status has text/icon semantics, not color alone
  - charts: tokenized categorical colors only
  - focus: visible 3px or equivalent focus ring with sufficient contrast in every named theme

## Component style rules

- workstream shell and functional-agent rail: left rail on desktop, selected workstream uses soft accent fill and border, attention badges are prominent but sparse, user/context controls pinned near the bottom when appropriate
- context/authority bar: shows organization/customer context, role/capability basis, support access, trace links, pending approvals, and stale/sync state in compact bordered fields
- AI command strip: visually distinct raised surface with AI mark, prompt input affordance, suggested prompt chips, send/action button, and subtle AI glow/border; results must become durable workstream items/surfaces
- KPI summary cards: strong numbers, label, trend text, status icon/color, optional sparkline; avoid equal visual weight with decision queues
- decision/exception cards: subject, originating agent/system, risk/policy badge, recommendation, evidence summary, confidence/impact when available, primary action, secondary details/evidence action, visible trace link when available
- agent activity timeline/cards: timestamp, agent, action summary, automation/review/escalation badge, semantic icon, detail affordance; use as collapsed/audit detail, not duplicate sibling surfaces beside the primary typed result
- governance/trust controls: threshold/authority, enabled/verified state, edit/history/simulation links, approval-required state when applicable
- cards/panels: layered surface tokens, hairline borders, purposeful density, no undifferentiated card grids
- buttons/actions: specific verb labels; primary action dominance; destructive/approval actions visually distinct but not overpowering; disabled controls include visible reason when not obvious
- forms: semantic labels, helper/error text, tokenized control backgrounds/borders/focus/disabled/validation states; raw browser-default controls are unacceptable
- tables/lists: default ordering and filters, clear row primary action, status text plus badge, narrow-screen card transformation or intentional technical scroll
- charts/data visualization: chart tokens only, legends for multi-series/donut/map, tabular numerals for critical metrics
- loading/empty/error/success states: structured surfaces with user-safe copy, recovery actions, and trace/correlation ids where visible
- motion/transition rules: state-driven transition for append/update/approval/stale/reconnect; a small number of meaningful high-impact transitions; respects reduced motion
- background/texture/elevation rules: subtle depth fields, grain, radial glow, atmospheric panels, and hairline grids are allowed only when readability, contrast, and focus remain strong

## Accessibility and responsive constraints

- contrast: all text, controls, badges, chart legends, focus rings, and status indicators meet contrast expectations for each named theme
- focus: every interactive element has visible focus; focus moves intentionally after validation failure, modal open/close, route/deep-link change, major surface change, and workstream switch
- keyboard: rail, composer, surfaces, decision actions, forms, traces, and My Account theme settings are keyboard-operable
- status semantics: status never depends on color alone; include text and/or icons
- narrow-screen layout: selected-agent context and primary action remain visible; dense tables transform to cards or intentionally reduce columns; decision/exception queues stay above low-priority reports
- reduced motion: transitions are disabled or minimized under `prefers-reduced-motion`

## Implementation notes

- CSS variable prefix: semantic CSS custom properties such as `--color-canvas`, `--color-surface`, `--color-text`, `--color-accent`, `--color-ai`, `--color-success`, `--color-warning`, `--color-danger`, `--color-info`, `--color-focus`, `--shadow-card`
- files expected to apply this guide:
  - `frontend/src/styles/tokens.css`
  - `frontend/src/styles/base.css`
  - `frontend/src/styles/layout.css`
  - `frontend/src/styles/components.css`
  - reusable workstream components under `frontend/src/workstream/**`
- reference mockups: `skills-pack/examples/web-ui/ai-first-workstream-enterprise/*.html` show the intended visual craft; copy anatomy and token roles, not demo content
- tests/manual checks:
  - selected style guide exists and is linked by UI tasks
  - all named themes have color-token bundles
  - no dark/light/system mode selector is exposed as the primary preference
  - My Account theme picker previews immediately and persists only through governed save/confirm path
  - important structured-surface form controls are visibly styled
  - no hard-coded color drift outside token/theme files unless justified
  - loading, empty, forbidden, error, approval-needed, stale/reconnect, no-op, and success states are visible and actionable
