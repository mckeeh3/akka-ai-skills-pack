# Style Guide

## Selection

- selected style: `ai-first-workstream-enterprise`
- style name: AI-first Workstream Enterprise
- source reference:
  - `skills-pack/docs/web-ui-style-guide.md`
  - `skills-pack/examples/web-ui/ai-first-workstream-enterprise/README.md`
  - `skills-pack/examples/web-ui/ai-first-workstream-enterprise/*.html`
- status: selected for the runnable SaaS Foundation App and generated-app baseline

## Theme model

- user-facing model: named theme selection, not dark/light/system mode
- default theme id: `aurora-light`
- available themes:
  - `aurora-light`: Aurora Light; tone light for contrast testing only; calm off-white enterprise workspace with indigo accent
  - `cobalt-light`: Cobalt Light; tone light for contrast testing only; cool pale-blue workspace with stronger blue accent
  - `obsidian-dark`: Obsidian Dark; tone dark for contrast testing only; deep neutral command-desk workspace with indigo/violet accent
  - `midnight-dark`: Midnight Dark; tone dark for contrast testing only; blue-black operations workspace with crisp cyan-blue accent
  - `dark-night`: Dark Night; tone dark for contrast testing only; near-black high-contrast workspace with hot red primary accents, gold warning/ranking cues, and teal success/AI accents
- My Account behavior: users select one available named theme, the UI previews the selected theme immediately on field change, and Save/Confirm persists the selected theme through the governed settings action
- persistence scope: backend user settings; local browser storage may provide immediate preview/cache but is not durable truth
- future theme rule: add color-token bundles and available-theme metadata without changing workstream shell anatomy, surface contracts, component structure, design language, spacing scale, typography scale, route behavior, capability mapping, authorization, audit, or tests
- mode rule: there is no dark/light/system mode; users choose named themes. Theme tone is metadata for contrast testing only. Changing theme changes colors, not layout, component anatomy, style/look, or behavior.

## Visual direction

- aesthetic point of view: enterprise command desk for supervising delegated agent work; calm, dense, traceable, high-confidence, and more operational than generic dashboard/CRM chrome
- tone: refined AI supervision cockpit with strong hierarchy, precise borders, controlled glow, functional color, and designed controls
- memorable motif: layered mission-control surfaces, bright but sparse status accents, visible authority/trace rails, durable decision cards, and AI command strips that launch governed work
- differentiation target: the UI should be remembered as an operations-grade AI supervision cockpit, not as a generic SaaS dashboard; authority, risk, evidence, and traceability should be visible design features
- craft principles: commit to this aesthetic before coding; use typography, color tokens, borders, depth, texture, and purposeful motion to reinforce hierarchy; avoid timid evenly distributed palettes and undifferentiated cards
- forbidden generic patterns: purple-gradient chatbot shells, generic CRUD dashboards, CRM/admin style galleries, undifferentiated card grids, decorative chat bubbles for consequential work, raw browser-default controls, and equal visual weight across FYI, blocked, autonomous, and approval-needed states

## Brand adaptation

- app/product name: SaaS Foundation App unless forked downstream
- logo/icon treatment: compact product mark in the functional-agent rail; may use brand accent but must preserve contrast and not replace authority/status semantics
- copied-demo-content rule: do not copy product names, people, account names, metrics, fleet/customer examples, logos, or screenshots from reference material
- custom brand overrides: limited to token bundles, product name, logo/icon treatment, safe accent tuning, and documented font choices unless the user provides a custom design system

## Design tokens

- typography:
  - display: `--font-display`, used for high-level headings and product-grade hierarchy
  - body: `--font-sans`, used for UI copy and controls
  - mono: `--font-mono`, reserved for trace ids, code-like labels, technical metadata, and dense numeric/queue metrics
  - tabular numerals for KPIs, counts, percentages, money, time, and queue metrics
- spacing: stable scale from `--space-1` through `--space-8`, plus documented workstream composer clearance
- radius: `--radius-sm`, `--radius-md`, `--radius-lg`, `--radius-xl`, `--radius-pill`
- elevation: hairline borders first, then restrained shadows/glow for raised cards, command strips, decision urgency, and AI surfaces
- colors: semantic CSS variables cover canvas, surfaces, text, borders, primary/accent, AI accent, status, chart palette, focus, shadows, and subtle texture; components consume semantic tokens only

## Component style rules

- workstream shell / functional-agent rail: persistent left rail on desktop with role-authorized functional agents and user/notification region near the bottom; selected workstream uses soft accent fill and border
- context/authority bar: shows tenant/customer, role/capability basis, support access, trace links, pending approvals, and stale/sync state in compact bordered fields
- AI command strip / persistent composer: visually distinct raised surface with AI mark, prompt input affordance, suggested prompt chips, send/action button, and subtle AI glow/border; consequential results become durable workstream items/surfaces
- KPI summary cards: strong numbers, label, trend text, status icon/color, optional sparkline; avoid equal visual weight with decision queues
- decision/exception cards: subject, originating agent/system, risk/policy badge, recommendation, evidence summary, confidence/impact when available, primary action, secondary details/evidence action, visible trace link when available
- governance/trust controls: threshold/authority, enabled/verified state, edit/history/simulation links, approval-required state when applicable
- forms: semantic labels, helper/error text, tokenized control backgrounds/borders/focus/disabled/validation states; raw browser-default controls are unacceptable
- tables/lists: default ordering and filters, clear row primary action, status text plus badge, narrow-screen card transformation or intentional technical scroll
- loading/empty/error/success states: structured surfaces with user-safe copy, recovery actions, and trace/correlation ids where visible
- motion/transition rules: state-driven transition for append/update/approval/stale/reconnect; a small number of meaningful high-impact transitions; respects reduced motion
- background/texture/elevation rules: subtle depth fields, grain, radial glow, atmospheric panels, and hairline grids are allowed only when readability, contrast, and focus remain strong

## Accessibility and responsive constraints

- contrast: text, controls, badges, chart legends, focus rings, and status indicators meet contrast expectations for each named theme
- focus: visible focus ring using `--color-focus` across all surfaces in every named theme
- keyboard: functional-agent rail, persistent composer, queues, decision actions, forms, drawers, and modals must be keyboard-reachable in logical order
- status semantics: status uses text plus color/icon treatment; color is never the only status channel
- narrow-screen layout: preserve selected-agent context and primary decision/action first; stack KPI cards and queues by priority
- reduced motion: disable or simplify decorative animation when `prefers-reduced-motion` is active

## Implementation notes

- CSS variable prefix: semantic CSS variables matching the design spec
- files expected to apply this guide: `frontend/src/styles/**`, `frontend/src/workstream/**`, generated static resources after build, and My Account settings surfaces
- tests/manual checks:
  - all five named themes exist as token bundles
  - My Account named-theme selection previews immediately and persists only through governed save/confirm path
  - no dark/light/system mode selector is exposed as the primary preference
  - switching themes changes color tokens only, not shell/surface anatomy, spacing, typography scale, route behavior, or capability mapping
  - keyboard focus and status labels work in every theme tone
