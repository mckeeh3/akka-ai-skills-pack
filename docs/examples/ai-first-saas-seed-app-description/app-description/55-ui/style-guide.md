# Style Guide

## Selection

- selected style: `ai-first-workstream-enterprise`
- style name: AI-first workstream enterprise
- source reference: `docs/web-ui-style-guide.md` canonical AI-first style system
- status: selected for seed app validation

## Theme model

- user-facing model: named theme selection
- default theme id: `aurora-light`
- available themes:
  - `aurora-light`: Aurora Light; light; calm off-white enterprise workspace with indigo accent
  - `cobalt-light`: Cobalt Light; light; cool pale-blue workspace with stronger blue accent
  - `obsidian-dark`: Obsidian Dark; dark; deep neutral dark workspace with indigo/violet accent
  - `midnight-dark`: Midnight Dark; dark; darker blue-black workspace with crisp cyan-blue accent
- My Account behavior: users select one available named theme and the UI applies that theme
- persistence scope: backend user settings for generated-app runtime; local browser storage is acceptable only for pre-runtime visual prototypes and must be labeled as such
- future theme rule: add token bundles and available-theme metadata without changing workstream shell anatomy, surface contracts, component structure, route behavior, capability mapping, authorization, audit, or tests

## Visual direction

- aesthetic point of view: calm enterprise data-product workspace for supervising delegated agent work, with neutral layered surfaces, strong numerical hierarchy, hairline borders, sparse functional color, blue/indigo AI and action accents, and semantic status colors
- tone: calm, precise, supervisory, trustworthy, and outcome-oriented rather than decorative or playful
- memorable motif: "clear delegated-work control" — decisions, evidence, authority, policy, trace, and outcome context are visually easier to find than decorative chrome
- forbidden generic patterns: purple-gradient chatbot shells, generic CRUD dashboards, CRM/admin style galleries, undifferentiated card grids, decorative chat bubbles for consequential work, and equal visual weight across FYI, blocked, autonomous, and approval-needed states

## Brand adaptation

- app/product name: AI-First SaaS Seed
- logo/icon treatment: simple generated product mark; do not copy demo product names, logos, user names, account names, or metrics from reference material
- copied-demo-content rule: do not copy product names, people, account names, metrics, fleet/customer examples, logos, or screenshots from reference material
- custom brand overrides: none for the seed validation pass

## Visual intent

- calm operational SaaS interface for supervising delegated agent work
- prioritizes decisions, exceptions, policy boundaries, auditability, trace links, and outcome visibility over decorative chrome
- feels intentionally designed rather than like a generic admin dashboard, CRM, project-management board, or chat app
- makes autonomous activity visible through structured surfaces, recommendation cards, confidence/risk indicators, timelines, and trust controls without hiding consequential work in chat transcripts
- supports four named themes with equivalent component anatomy, hierarchy, contrast, focus treatment, and status semantics

## Design tokens

- typography:
  - `--font-sans: Inter, "Instrument Sans", ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif`
  - `--font-mono: "JetBrains Mono", "IBM Plex Mono", "SFMono-Regular", "SF Mono", Consolas, ui-monospace, monospace`
  - use sans for general UI copy and monospace for trace ids, code-like labels, technical metadata, and dense numeric/queue metrics
  - tabular numerals for KPIs, counts, percentages, money, time, and queue metrics
- spacing: stable 4px-based scale from `--space-1` through `--space-8`
- radius: 8px controls, 12px standard cards, 16px prominent panels, pill badges
- elevation: light themes use layered cards/panels plus hairline borders and subtle shadows; dark themes rely on layered charcoal/navy/slate surfaces, borders, and restrained local glow
- colors:
  - `aurora-light`: off-white canvas, white cards, neutral slate text, indigo accent, accessible semantic colors
  - `cobalt-light`: cool pale-blue canvas, white or blue-tinted cards, stronger blue accent, slightly cooler status tints
  - `obsidian-dark`: near-black charcoal canvas, subtle slate cards, indigo/violet accent, status colors tuned for dark contrast
  - `midnight-dark`: deeper blue-black canvas, navy/slate cards, cyan-blue accent, status colors tuned for dark contrast
  - color tokens cover canvas, surfaces, text, borders, primary/accent, AI accent, status, chart palette, focus, shadows, and subtle texture
  - components consume semantic CSS variables only; TypeScript may toggle documented named theme ids and must not hard-code visual values
  - lightweight style overrides may change only named theme tokens, color tokens, font-family tokens, product name, logo/icon treatment, and safe brand accents

## Component style rules

- workstream shell / functional-agent rail:
  - persistent left rail on desktop with role-authorized functional agents and user/notification region near the bottom
  - active functional agent and current structured surface use soft accent surface plus accent icon/text
  - mobile collapses the functional-agent rail into a drawer or menu
- persistent composer:
  - placed at the bottom of the active workstream with visible tenant/customer and authority context
  - includes agent icon, command input/prompt, suggested prompt chips, and send/action button
  - composer-triggered consequential work must resolve into durable goals, decisions, approvals, policy proposals, or traceable actions
- cards/panels:
  - use clear headings, metadata subtitles, 1px borders, and consistent padding
  - decision and exception cards use semantic accent plus status badge and action column
- buttons/actions:
  - primary actions use the selected theme's accent token
  - AI actions may use AI accent tokens
  - destructive/high-impact actions require confirmation or a decision-card workflow
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
  - success messages name the completed action
- motion/transition rules:
  - state-driven transitions may polish surface append/update, composer focus, hover, stale/reconnect, approval result, and denial/recovery feedback
  - reduced-motion mode disables decorative transform/animation while preserving visible state changes
- background/texture/elevation rules:
  - use subtle layered depth, hairline borders, panel shadows, and component-local glow to create hierarchy
  - avoid prominent diagonal striping, large page-corner gradients, or visible wallpaper textures
  - never let texture, glow, or blur reduce text contrast, focus visibility, or structured-surface readability

## Accessibility and responsive constraints

- contrast: WCAG AA for text and meaningful controls in all four initial named themes
- focus: visible focus ring using `--color-focus` across all surfaces in every named theme
- keyboard: functional-agent rail, persistent composer, queues, decision actions, forms, drawers, and modals must be keyboard-reachable in logical order
- status semantics: status uses text plus color/icon treatment
- narrow-screen layout: preserve primary decision/action first; stack KPI cards and queues by priority
- reduced motion: disable or simplify decorative animation when `prefers-reduced-motion` is active

## Implementation notes

- CSS variable prefix: use semantic CSS variables matching the design spec
- files expected to apply this guide: generated React/Vite frontend CSS, workstream shell components, structured surface components, decision-card components, governance surfaces, audit trace surfaces, and My Account settings surfaces
- tests/manual checks: render core shell and primary Mission Control surface in `aurora-light`, `cobalt-light`, `obsidian-dark`, and `midnight-dark`; verify My Account named-theme selection applies the selected theme id; verify keyboard focus and status labels in each theme tone
