# Style Guide

## Selection

- selected style: `atlas-ops-supervisory-console`
- style name: Atlas Ops supervisory console
- source reference: `docs/web-ui-style-guide.md` canonical AI-first style system
- mode policy: system with explicit light and dark token sets
- status: selected for seed app validation

## Visual direction

- aesthetic point of view: operational mission-control console with readable sans UI typography plus monospace technical accents, warm near-black charcoal backgrounds, layered glass-like panels, orange primary controls/icons, muted coral AI/highlight accents, restrained localized glow, and audit-grade information density
- tone: calm, precise, supervisory, and trust-oriented rather than decorative or playful
- memorable motif: "signal over noise" — subtle grid/scanline atmosphere, compact command surfaces, and semantic accent rails for work needing human attention
- forbidden generic patterns: purple-gradient chatbot shells, undifferentiated white-card dashboards, decorative chat bubbles for consequential work, and equal visual weight across FYI, blocked, autonomous, and approval-needed states

## Brand adaptation

- app/product name: AI-First SaaS Seed
- logo/icon treatment: simple generated product mark; do not copy Atlas Ops logo or mockup names
- copied-demo-content rule: do not copy demo product names, people, account names, metrics, or fleet/customer examples from reference images
- custom brand overrides: none for the seed validation pass

## Visual intent

- calm operational SaaS interface for supervising delegated agent work
- prioritizes decisions, exceptions, policy boundaries, auditability, and outcome visibility over decorative chrome
- feels intentionally designed rather than like a generic admin dashboard, CRM, or chat app
- makes autonomous activity visible without hiding consequential work in chat transcripts
- supports light, dark, and system mode with equivalent hierarchy and contrast

## Design tokens

- typography:
  - `--font-sans: Inter, "Instrument Sans", ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif`
  - `--font-mono: "JetBrains Mono", "IBM Plex Mono", "SFMono-Regular", "SF Mono", Consolas, ui-monospace, monospace`
  - use sans for general UI copy and monospace for trace ids, code-like labels, technical metadata, and dense numeric/queue metrics
  - tabular numerals for KPIs, counts, percentages, money, time, and queue metrics
- spacing: stable 4px-based scale from `--space-1` through `--space-8`
- radius: 8px controls, 12px standard cards, 16px prominent panels, pill badges
- elevation: light mode uses layered card/panel shadows plus hairline borders; dark mode relies on borders, restrained warm glow, and depth from subtle surface contrast
- colors:
  - dark mode uses a warm green-black/charcoal foundation rather than a cool blue-black foundation; page, sidebar, and card surfaces should remain close in value but still have enough surface/border separation to read as premium cards
  - primary controls, icon chips, active rail states, focus accents, and major call-to-action buttons use warm orange tokens similar to the operations-dashboard reference
  - AI/highlight accents use muted coral/pink, while status and chart colors remain semantic and tokenized
  - surfaces, text, borders, primary, AI accent, status, chart, focus, shadows, texture, and motion tokens are defined by the `atlas-ops-supervisory-console` tokens in `docs/web-ui-style-guide.md`
  - lightweight style overrides may change only color and font tokens

## Component style rules

- workstream shell / functional-agent rail:
  - persistent left rail on desktop with role-authorized functional agents and user/notification region near the bottom
  - active functional agent and current structured surface use soft primary surface plus primary icon/text
  - mobile collapses the functional-agent rail into a drawer or menu
- persistent composer:
  - placed at the bottom of the active workstream with visible tenant/customer and authority context
  - includes agent icon, command input/prompt, suggested prompt chips, and send/action button
  - composer-triggered consequential work must resolve into durable goals, decisions, approvals, policy proposals, or traceable actions
- cards/panels:
  - use clear headings, metadata subtitles, 1px borders, and consistent padding
  - decision and exception cards use left semantic accent plus status badge and action column
- buttons/actions:
  - primary actions use solid primary color
  - AI actions may use AI accent
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
  - use tokenized localized radial glow, very subtle dotted texture or near-invisible texture, hairline borders, and panel shadows to create depth
  - avoid prominent diagonal striping; texture should recede behind cards rather than become a visible wallpaper
  - never let texture, glow, or blur reduce text contrast, focus visibility, or structured-surface readability

## Accessibility and responsive constraints

- contrast: WCAG AA for text and meaningful controls in both modes
- focus: visible focus ring using `--color-focus` across all surfaces
- keyboard: functional-agent rail, persistent composer, queues, decision actions, forms, drawers, and modals must be keyboard-reachable in logical order
- status semantics: status uses text plus color/icon treatment
- narrow-screen layout: preserve primary decision/action first; stack KPI cards and queues by priority
- reduced motion: disable or simplify decorative animation when `prefers-reduced-motion` is active

## Implementation notes

- CSS variable prefix: use semantic CSS variables matching the design spec
- files expected to apply this guide: generated React/Vite frontend CSS, workstream shell components, structured surface components, decision-card components, governance surfaces, audit trace surfaces
- tests/manual checks: render core shell and primary Mission Control surface in light and dark mode; verify keyboard focus and status labels
