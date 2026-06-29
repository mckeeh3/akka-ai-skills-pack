# Web UI style guide and named-theme selection

Use this document whenever a generated app includes a browser UI. The visual style is part of the maintained app specification, not an implementation detail that frontend generation may invent later.

For the canonical style, also review `./web-ui-component-catalog.md` and the static reference mockups under `../examples/web-ui/ai-first-workstream-enterprise/`, especially `component-catalog.html`. They encode the expected visual craft: distinctive command-desk hierarchy, tokenized controls, functional-agent rail, command strip, reusable attention counters, decision cards, audit traces, and dense-but-readable workstream surfaces. Treat those files as design references, not runnable app completion evidence or content to copy wholesale.

## Policy

Generated full-stack SaaS apps must use the canonical AI-first workstream UI style by default. The style is a calm enterprise data-product interface for supervising delegated work: neutral layered surfaces, sparse functional color, blue/indigo accent, semantic status colors, strong numerical hierarchy, and reusable workstream-surface patterns.

Visual output must be intentionally designed, not merely acceptable. Before writing UI code, commit to the recorded aesthetic direction and execute it precisely through typography, color tokens, spacing, component anatomy, motion, and texture. Avoid generic AI-generated aesthetics: predictable SaaS dashboards, timid evenly distributed palettes, default controls, purple-gradient-on-white cliches, and layouts that could belong to any product.

Do not select older dashboard/CRM/project-management visual styles for new generated AI-first SaaS apps. Those patterns overfit conventional CRUD/analytics pages and do not make delegated work, authority, policy, evidence, decisions, traces, and outcomes prominent enough.

Visual design is a cosmetic realization layer only. It must make the existing workstream shell, structured surfaces, capability-backed actions, authority state, audit/trace links, accessibility, and responsive behavior clearer and more polished; it must not add, remove, rename, or reinterpret functional agents, surfaces, capabilities, authorization rules, routes, API contracts, tests, or readiness semantics.

Style and theme selection is intentionally narrow:

1. **Use the canonical AI-first workstream style system** unless the user provides a custom brand brief or design system.
2. **Use named themes as the user-facing preference model.** Users choose one available theme by name, normally in My Account settings; the app applies that theme to the UI.
3. **Use `custom`** only when the user supplies enough tokens/component rules to preserve the AI-first UX anatomy and matches or exceeds the visual specificity of `../examples/web-ui/ai-first-workstream-enterprise/`.
4. **Do not offer or invent generic dashboard/CRM/project-management style galleries.** Those are outside the AI-first SaaS UI model.

## Authoritative locations

Record the selected style and available named themes in the smallest authoritative artifact available in the current-intent UI/surface realization graph:

- current-intent graph apps: a shared UI/style node such as `app-description/global/surfaces/web-ui-style.md`, a workstream realization file such as `app-description/domains/<domain>/workstreams/<workstream>/realization/frontend-routes.md`, or a linked domain/workstream UI style artifact referenced by every affected surface implementation task
- specs/backlog apps: `specs/cross-cutting/NN-ui-style-guide.md` or an equivalent UI slice spec referenced by every web UI task
- legacy/template compatibility trees: `app-description/55-ui/style-guide.md` only when that file is mapped back to the owning current-intent workstream surfaces

A generated web UI must read from that style guide before producing HTML, CSS, TypeScript, or JavaScript. For generated full-stack AI-first SaaS, the browser UI is mandatory; if no style and named-theme contract has been selected, add a durable `category: ui` question to `specs/pending-questions.md` before creating or executing web UI implementation tasks.

## Required style-guide fields

Every app UI style guide should define:

- selected AI-first style id and name, or `unselected`
- source reference: this document, `./web-ui-component-catalog.md`, `../examples/web-ui/ai-first-workstream-enterprise/`, a custom design reference, or a user-provided brand brief
- visual direction: concise aesthetic point of view, tone, memorable motif, differentiation target, and forbidden generic patterns
- **theme model:** named-theme selection, not dark/light/system mode; available theme ids/names; default theme id; optional tone metadata for contrast testing only; user preference scope and persistence expectations; immediate local preview behavior for My Account theme selection; governed save/confirm path for durable persistence
- brand adaptations: app name, logo/icon treatment, product-specific accent allowances, forbidden copied demo names/logos
- layout shell: functional-agent rail, context/authority bar, main workstream panel, persistent composer, surface grid density, and deep-link support
- typography: font family, scale, weights, line heights, numeric/table conventions, and fallback strategy
- color tokens: CSS variables for canvas, surfaces, text, borders, primary/accent colors, status colors, chart colors, focus rings, and shadows
- spacing/radius/elevation tokens
- component rules: command strip, decision cards, cards, buttons, forms, structured-surface form controls, tables/lists, charts, badges, empty/error/loading states, toasts/modals where applicable
- motion and texture: purposeful transition rules, reduced-motion behavior, background depth, borders, shadows, decorative texture limits, and the one or two memorable visual details that make the UI feel product-specific
- accessibility constraints: contrast, focus visibility, color-not-alone status semantics, reduced motion expectations
- generated asset expectations: `index.html` uses semantic landmarks, `app.css` defines tokens as CSS variables, TypeScript toggles only documented theme ids/classes/attributes rather than hard-coded styling decisions

## Style guide artifact template

```md
# Web UI Style Guide

## Selection
- selected style: <ai-first-workstream-enterprise | custom | unselected>
- style name: <name>
- source reference: <./web-ui-style-guide.md | custom brief path | user answer>
- status: <selected | pending-question | deferred-with-default>

## Theme model
- user-facing model: named theme selection
- default theme id: <aurora-light | ...>
- available themes:
  - <theme-id>: <theme name>; tone <light | dark for contrast testing only>; <short intent>
- My Account behavior: users select one available named theme, the UI previews the selected theme immediately on field change, and Save/Confirm persists the selected theme through the governed settings action
- persistence scope: <backend user settings | local browser setting | deferred-with-follow-up>
- future theme rule: add color-token bundles without changing workstream/component anatomy, design language, spacing, typography scale, or behavior

## Visual direction
- aesthetic point of view:
- tone:
- memorable motif:
- differentiation target: what should feel memorable or specifically designed for this app
- reference mockups: `../examples/web-ui/ai-first-workstream-enterprise/*.html` when using the canonical style
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
  - canvas/surfaces:
  - text:
  - primary/accent:
  - AI accent:
  - status:
  - charts:
  - focus:

## Component style rules
- workstream shell and functional-agent rail:
- AI command strip:
- KPI summary cards: all dashboards with attention counters use the same attention-card style; place the counter strip above lower-priority detail panels/lists, with readable labels, strong numbers, and deliberate vertical spacing between label, number, and status badge
- decision/exception cards:
- agent activity timeline/cards:
- governance/trust controls:
- cards/panels:
- buttons/actions:
- forms: structured-surface inputs/selects/textareas use tokenized designed control classes/states and must not render as browser-default/native controls
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
- tests/runtime-validation checks:
```

## Default style selection question

Use this durable pending-question shape when style or named-theme selection is missing:

```md
### Q-<next>: Select web UI style and theme contract

- status: pending
- priority: blocking
- category: ui
- depends on: []
- blocks:
  - web UI implementation and generation tasks only
- source:
  - the current-intent UI/surface style artifact, legacy/template `app-description/55-ui/style-guide.md`, or specs/cross-cutting/NN-ui-style-guide.md is missing, unselected, or lacks a named-theme contract
- question: >
    Which visual style and named theme contract should generated web UI content use?
- why it matters: >
    The selected style and theme contract drive generated HTML structure, CSS variables, spacing, typography, chart colors, component states, accessibility review, user theme preference behavior, and regeneration consistency. Without them the harness would have to invent visual style during implementation.
- options:
  - A: ai-first-workstream-enterprise — recommended canonical AI-first SaaS workstream UI with four initial named themes
  - B: custom — user will provide a custom style brief or reference that preserves AI-first supervision, decision, governance, audit, outcome surfaces, and named-theme semantics
- default if deferred: none for production generation; ai-first-workstream-enterprise may be accepted explicitly for early evaluation only
- answer: none
- decision: pending
- decision impact: pending
- reconciled into:
  - none
```

When answered, reconcile the decision into the current-intent UI/surface style artifact, a mapped legacy/template `app-description/55-ui/style-guide.md`, and/or the relevant `specs/cross-cutting/*ui-style-guide*.md` before marking the question `resolved`.

## Canonical AI-first style system: `ai-first-workstream-enterprise`

Use this as the default style system for generated AI-first SaaS apps. It is a restrained enterprise workstream interface for delegated agent work, not a generic dashboard skin.

### Visual intent

- calm, data-forward SaaS interface for supervising delegated agent work
- neutral canvas and layered card surfaces; structure comes from whitespace, hairline borders, and minimal elevation rather than heavy shadows
- a single blue/indigo brand accent for primary actions, links, active navigation, and AI affordances
- semantic status colors used sparingly and functionally: green for positive/healthy/on-track, amber for warning/needs-review, red for critical/at-risk, and neutral gray for unknown/inactive
- strong numeric hierarchy in KPI strips, metrics, tables, and dense operational summaries; all dashboard attention counters use a consistent card strip above detail content, with labels large/bold enough to scan and enough spacing between label, number, and status to avoid cramped cards
- decisions, exceptions, policy boundaries, auditability, and outcome visibility are more prominent than decorative chrome
- autonomous activity is visible through recommendation panels, confidence gauges, agent avatars, status pills, and timelines without hiding consequential work in chat transcripts
- there is no dark/light/system mode and no mode-specific layout; named themes preserve the same component anatomy and design language while swapping color token bundles, primarily backgrounds/surfaces plus accent and semantic tuning

### Named-theme contract

Generated AI-first SaaS apps start with these five named themes unless the authoritative style guide records a custom equivalent with multiple named color-token bundles spanning both light-toned and dark-toned options for contrast coverage:

| Theme id | Name | Tone | Intended use |
| --- | --- | --- | --- |
| `aurora-light` | Aurora Light | light | default calm off-white enterprise workspace with indigo accent |
| `cobalt-light` | Cobalt Light | light | cooler pale-blue light workspace with stronger blue accent |
| `obsidian-dark` | Obsidian Dark | dark | default deep neutral dark workspace with indigo/violet accent |
| `midnight-dark` | Midnight Dark | dark | darker blue-black workspace with crisp cyan-blue accent |
| `dark-night` | Dark Night | dark | near-black benchmark-style workspace with charcoal cards, hot red primary accents, gold ranking/warning cues, and teal success/AI accents |

Theme ids are stable implementation values. Theme names are user-facing labels. My Account settings must expose available theme names, store/apply the selected theme id at the documented scope, and must not present `system`, `light`, or `dark` as user-selectable modes. A theme may record `tone: light` or `tone: dark` for contrast testing only. Changing theme changes color tokens, not the design/style/look, workstream anatomy, surface inventory, spacing, typography scale, routes, capability mapping, authorization, or audit behavior.

When the user changes a named-theme field, the UI must preview that selected theme immediately in the current browser session by switching only documented theme ids/classes/attributes. Save/Confirm must still call the governed backend settings action for durable persistence, authorization, audit, and cross-session truth. Immediate preview is not proof that persistence succeeded; failure to save must show a clear recovery path without silently claiming the preference is stored.

Future themes are added by defining new color-token bundles and adding them to the available-theme list. Adding or switching a theme must not change workstream shell anatomy, surface contracts, card structure, spacing scale, typography scale, icon/status semantics, route behavior, capability mapping, authorization, audit behavior, or tests.

### Visual craft doctrine

Use these rules to improve visual quality without changing product behavior. This doctrine folds in external frontend-design principles while constraining them to this pack's canonical AI-first SaaS model: be distinctive and precise, but do not vary the underlying workstream design language per app unless the user supplies a custom design system.

Before coding, the UI implementer must be able to state:

- the app purpose and primary supervision/review job;
- the selected aesthetic point of view, normally enterprise command desk / governance cockpit / trust-and-outcomes workspace;
- the memorable design motif, such as authority rails, trace-lit timelines, decision cards, or dense operational panels;
- the exact token families and component recipes that will carry that motif;
- which visual cliches are forbidden for this app.

Design craft rules:

- Choose a clear aesthetic direction for the app or brand adaptation, such as enterprise command desk, calm governance cockpit, data-product operations center, or trust-and-outcomes workspace. Do not proceed with an unspecified "modern SaaS" look.
- Avoid generic AI UI clichés: purple gradients on white, undifferentiated cards, equal visual weight everywhere, decorative chat bubbles for consequential work, and styling that could belong to any SaaS dashboard.
- Typography should feel intentional. Prefer a distinctive but highly readable display/body pairing over generic default system stacks when the target app can support it. Use weight, size, line height, letter spacing, and tabular numerals deliberately for hierarchy. Reserve monospace for code, trace ids, dense metrics, and technical labels. Custom styles may replace the default font tokens when the app records accessible fallbacks, readable line heights, and tabular/numeric conventions for dense operational data.
- Use color, borders, shadows, texture, and depth to clarify hierarchy: human-needed work, policy-blocked work, autonomous progress, trace/history, and FYI activity should not look interchangeable. Strong accents are better than timid color spread, but accent color must stay functional and tokenized.
- Motion should be purposeful and state-driven: surface append/update, agent-working, approval result, stale/reconnect, expansion, and denial/recovery transitions may be polished, but must preserve reduced-motion support and never obscure state or audit evidence. Prefer a small number of high-impact, meaningful transitions over scattered decorative animation.
- Background treatments, grain, dotted textures, patterns, and component-level glow effects are acceptable only when implemented through documented tokens/classes and when contrast, focus visibility, and surface readability remain intact. Prefer subtle depth fields, atmospheric panels, and precise hairline grids over visible diagonal striping or large page-corner gradients.
- Dark themes should avoid pure black slabs and default generic blue-black dashboard styling; use low-contrast layered near-black, charcoal, navy, or slate surfaces with crisp hairline borders.

### Style customization scope

Lightweight style customization may change only:

- named theme tokens
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

### Theme token template

Each named theme should provide the same semantic token names. Components consume these tokens, never theme-specific hard-coded colors.

```css
[data-theme="aurora-light"] {
  color-scheme: light;
  --color-canvas: #f7f8fb;
  --color-canvas-subtle: #eef2f7;
  --color-sidebar: #ffffff;
  --color-surface: #ffffff;
  --color-surface-raised: #fdfefe;
  --color-surface-nested: #f3f6fb;

  --color-text: #111827;
  --color-text-soft: #374151;
  --color-muted: #6b7280;
  --color-disabled: #9ca3af;
  --color-inverse-text: #ffffff;

  --color-border: #dce3ed;
  --color-border-strong: #b9c4d2;

  --color-accent: #4f46e5;
  --color-accent-strong: #3730a3;
  --color-accent-soft: rgb(79 70 229 / 0.10);
  --color-ai: #6366f1;
  --color-ai-soft: rgb(99 102 241 / 0.12);

  --color-success: #15803d;
  --color-success-soft: rgb(21 128 61 / 0.12);
  --color-warning: #b45309;
  --color-warning-soft: rgb(180 83 9 / 0.14);
  --color-danger: #dc2626;
  --color-danger-soft: rgb(220 38 38 / 0.12);
  --color-info: #2563eb;
  --color-info-soft: rgb(37 99 235 / 0.12);

  --color-chart-blue: #2563eb;
  --color-chart-violet: #7c3aed;
  --color-chart-green: #16a34a;
  --color-chart-amber: #d97706;
  --color-chart-red: #dc2626;
  --color-chart-cyan: #0891b2;

  --color-focus: #4f46e5;
  --shadow-card: 0 14px 36px rgb(15 23 42 / 0.08);
  --shadow-glow-ai: 0 0 0 1px rgb(99 102 241 / 0.18), 0 18px 50px rgb(99 102 241 / 0.10);
}
```

Required aliases may be added for existing SaaS Foundation App code, but the authoritative token roles are canvas/surface/text/border/accent/status/chart/focus/shadow. If legacy CSS uses `--color-bg` or `--color-primary`, map those aliases to `--color-canvas` and `--color-accent` during migration rather than preserving old semantics as a new default.

### Initial theme token direction

- `aurora-light`: off-white canvas, white cards, neutral slate text, indigo accent, accessible semantic colors.
- `cobalt-light`: cool pale-blue canvas, white/blue-tinted cards, stronger blue accent, slightly cooler status tints.
- `obsidian-dark`: near-black charcoal canvas, subtle slate cards, indigo/violet accent, status colors tuned for dark contrast.
- `midnight-dark`: deeper blue-black canvas, navy/slate cards, cyan-blue accent, status colors tuned for dark contrast.
- `dark-night`: pure black canvas, charcoal layered cards, fine gray borders, hot red primary/action accents, gold warning/rank cues, teal success/AI accents, and blue info links.

Theme variation should mainly cover canvas/surface/background colors plus accent and semantic color tuning. Typography, spacing, radii, component anatomy, and UX behavior stay shared.

## Required AI-first component anatomy

### App shell

- left functional-agent rail on desktop, approximately `--shell-sidebar-width`
- product logo at top
- role-authorized functional agents first, then supporting governance/audit/status entries when represented as functional agents or capability-backed surfaces
- attention, notifications, and current user profile/context controls pinned near the bottom where appropriate
- active functional agent uses soft accent background plus accent icon/text color
- unavailable workstreams are hidden or disabled based on capabilities; backend authorization remains authoritative
- conventional routes and deep links select shell state; they are not the primary application model

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

Craft rules:

- The label should be visibly larger and heavier than muted helper copy; use a semibold/bold display/body style rather than tiny gray text.
- Leave clear vertical rhythm between label, number, and badge/status text, typically at least the selected spacing token equivalent of `--space-3` for attention-card stacks.
- Centered KPI cards are acceptable only when the value/label/status cluster has enough breathing room; otherwise use a left-aligned hierarchy with the same spacing discipline.
- In role-specific dashboards and My Account dashboards, put the attention counter strip before lower-priority profile/settings/details, queues, lists, or explanatory panels so users first see what needs attention and then inspect details below.

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

Use agent activity timelines for collapsed/audit detail, progress inspection, or explicitly requested trace views. Do not show them as duplicate sibling cards next to the primary typed result surface for the same prompt/action.

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

### Structured-surface forms

Structured surfaces that collect or edit user input, including `detail-edit` and settings surfaces, must render controls as designed components in the selected AI-first style system.

Required elements:

- semantic `<form>`, `<label>`, `<input>`, `<select>`, `<textarea>`, and `<button>` elements where applicable
- visible or programmatic labels, helper text, validation text, and disabled/submitting states
- tokenized control styling for background, border, text, spacing, radius, focus ring, disabled state, and validation state
- layout rhythm that aligns labels, controls, helper text, and actions with surrounding workstream surface spacing
- browser-default/native-looking controls are unacceptable for generated workstream surfaces, even when the markup is semantically correct
- tests or source checks for important generated surfaces should catch missing structured-surface form control styling when those surfaces are in scope

### Data visualization

- charts use selected style chart tokens only
- legends are required for donut, map, and multi-series charts
- status meaning must be represented with label text and/or icons, not color alone
- critical metrics use tabular numerals

## Required surface patterns

Generated AI-first SaaS UIs should favor these surfaces over generic CRUD dashboards:

- **Mission Control / Briefing:** surface framing, AI command strip, operational KPI band, agent execution timeline, needs-your-attention queue, agent teams/trust summary, trust controls, upcoming autonomous actions, and typed system-message surfaces.
- **Goal Workbench:** objective form, success criteria, constraints, proposed execution plan, agent/team assignment, tool/data permissions, approval gates, launch simulation/review action.
- **Decision Queue and Decision Detail:** filters by priority/policy/agent/due time, recommendation summary, evidence/risk, alternatives, approve/reject/counter/defer/escalate actions, trace and outcome links.
- **Governance Center:** policy list and versions, thresholds, authority boundaries, proposed changes, simulations/replays, human-authorized commit flow, rollback and audit links.
- **Audit Trace Explorer:** search/filter by goal, agent, decision, tool, user, policy, time; chronological trace entries; evidence/tool/data-access details; authorization and policy invocation details; outcome links.
- **AutonomousAgent Progress / Result:** task status, dependency/blocked state, waiting-for-human state, progress evidence, result or rejection summary, retry/cancel/escalate actions, and trace links.

## Applying the style safely

1. Review `../examples/web-ui/ai-first-workstream-enterprise/` before generating canonical-style UI.
2. Replace demo names, logos, users, and metrics with the target app's domain.
3. Keep the AI-first component anatomy, token roles, hierarchy, and state treatment, not literal mockup content.
4. Define tokens once in `app.css` or project-standard token files; TypeScript may toggle only documented named theme ids/classes/attributes.
5. Style structured-surface form controls through reusable tokenized selectors/classes; do not accept raw browser-default input, select, or textarea rendering in workstream surfaces.
6. Preserve accessibility even when adapting brand colors: contrast and focus tokens override decorative brand fidelity.
7. If users request custom styling later, update the authoritative style guide first, then regenerate affected web UI assets.
8. If theme persistence is not implemented at the expected runtime scope, record the limitation and queue follow-up work rather than claiming durable My Account theme selection is complete.

## Removed generic visual catalog choices

The skills pack no longer exposes multiple generic dashboard/CRM/admin visual catalog choices for generated AI-first SaaS apps. Treat any old gallery-style id as obsolete source history, not as an available planning option. When encountered in an existing artifact, migrate the authoritative style guide to `ai-first-workstream-enterprise` or a user-supplied `custom` style guide that preserves the required AI-first component anatomy and named-theme contract.
