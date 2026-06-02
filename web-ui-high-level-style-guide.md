# Atlas Ops UI Style Guide

A design system extracted from the reviewed agent-first SaaS dashboards, intended as input for frontend design skills.

## Theming & Color

The system supports **simple light and dark themes**. The two themes are intended to differ **primarily in background and surface colors** — everything else (the accent, the semantic status ramp, typography, spacing, borders, component shapes) stays constant across themes. A theme is therefore defined by a small set of background tokens rather than a full re-palette.

Define theming through layered surface tokens, swapped per theme:

- **Canvas** (app background): the deepest layer. Dark theme is a near-black, cool-toned base — anywhere from a true black/very-dark-navy to a desaturated charcoal. Light theme is a soft off-white / very pale gray. The system should tolerate either end of the dark range as a drop-in swap.
- **Surface / card**: one step off the canvas. Dark theme cards are a marginally lighter, slightly cooler panel; light theme cards are pure white or a faintly tinted near-white. Card-vs-canvas separation stays low-contrast in both themes.
- **Elevated / nested surface**: a further subtle step for sub-panels (e.g. an agent-recommendation block inside a card).
- **Hairline border**: a low-contrast divider/border token. In both themes, structure is carried by these thin borders plus minimal elevation rather than heavy drop shadows.
- **Text tokens**: primary (high-contrast), secondary/muted, and disabled — each defined per theme but mapped to the same roles.

Accent and semantic colors are **theme-independent** (the same hues read correctly on both light and dark canvases, with at most minor tint/opacity adjustment for fills):

Color is used **sparingly and functionally**, never decoratively. The bulk of every screen is neutral. Accent appears only to encode meaning:

- A single brand accent (blue/indigo, occasionally violet) for primary actions, active nav state, links, and the agent/AI affordance.
- A semantic status ramp: green (positive/healthy/auto-approved/on-track), amber/yellow (medium severity/warning/needs review), red (high severity/at-risk/critical), neutral gray for unknown.
- Status is reinforced with small colored dots, pill badges, and tinted text — not large color fields.
- Positive/negative metric deltas in green/red, paired with small directional arrows.

Because only backgrounds change between themes, semantic fills (status pill backgrounds, tinted alert-row backgrounds) should be expressed as a translucent tint of the semantic hue layered over the surface, so they adapt automatically to the underlying canvas.

## Typography

A clean grotesque/geometric **sans-serif** throughout. Strong typographic hierarchy carries the layout:

- Large page titles (semibold, ~24–28px) with a lighter, smaller muted subtitle directly beneath.
- Section/card headers in medium weight, small-to-medium size.
- Big numeric KPIs set very large and semibold — the visual anchors of metric strips.
- Body and table text small, regular weight, in muted gray for secondary content.
- Eyebrow/label text (section kickers, metric labels) small, uppercase, letter-spaced, low-contrast.

Numerals are heavily emphasized; the design is data-forward and lets figures dominate.

## Iconography

Thin, consistent **line icons** (outline, ~1.5px stroke) at small sizes — in the left nav, inline with rows, and as status/type glyphs. Agents get a small avatar treatment (rounded squares with a tinted glyph). Confidence/utilization shown as a small circular ring/donut gauge.

## Layout — Overall Shell

A persistent three-zone structure:

1. **Left sidebar nav** (fixed, ~220px, full height, rendered on the canvas or a near-canvas surface). Logo/wordmark lockup at top, then nav items grouped under small uppercase section labels (MANAGE, INTELLIGENCE, GOVERNANCE, ADMIN). Active item highlighted with a tinted-accent background block. Items pair a line icon with a label; some carry a small "New" or count badge. A collapse control pinned at the bottom alongside the user identity chip (avatar + name/role) and a notification bell with count.

2. **Top bar / header zone** (full width of the content area). Holds a contextual breadcrumb/title beside the product name and right-aligned utilities. Two observed variants: a classic bar with a centered global search (⌘K affordance) plus notification + user cluster; and a chat-forward variant where a prominent "Ask Atlas…" input with suggested-prompt chips sits as the first content element, with a gradient/accent-tinted leading icon.

3. **Main content area** — generously padded, scrollable, on the canvas color.

## Layout — Content Patterns

Content is built from **bordered rounded cards** (medium corner radius, ~8–12px) on a responsive multi-column grid. Recurring structures:

- **KPI metric strip**: a full-width band near the top, divided into 4–6 equal cells (one bordered container with internal dividers, or separate cards). Each cell: uppercase label, one oversized value, a small delta and/or inline sparkline. Often the leftmost cell states the standing "objective" in prose instead of a number.

- **Primary work zone**: below the strip, a two- or three-column card arrangement. A common pattern is a left "prioritized list / queue" column paired with a wider right "detail / recommendation" panel — a master-detail split. Lists use ranked rows: icon, primary label, secondary descriptor line, right-aligned value, severity pill.

- **Agent-execution timeline**: a vertical timestamped activity feed — each entry has a left time gutter with a node dot, an agent glyph, a primary action line, a muted context line, a status pill (Auto / Review / Escalated), and a chevron affordance.

- **"Needs your attention" / exceptions column**: stacked alert cards, each with a left accent border colored by severity, a title + status pill, a short reasoning block, and a vertical stack of action buttons (one filled primary + outlined secondaries).

- **Detail/recommendation panel**: a larger card — header with status pills, supporting context body, an explicit nested "agent recommendation" sub-panel with a confidence gauge, and a row of action buttons (one filled primary, rest outlined). A horizontal underline-tab strip (Details / Evidence / Notes / Audit trail) switches panel content.

- **Right rail**: a narrow column of stacked summary cards — at-a-glance counts, a donut/ring breakdown with legend, a small map/heatmap with node markers, a ranked at-risk list.

- **Bottom band**: a full-width row of small equal cards summarizing background/agent activity — each a labeled tile with a status chip, a couple of metric lines, and a muted "view activity" link. Often paired with a "trust controls" card (policy rows each ending in a green check) and an "upcoming actions" list.

- **Approval rows**: list items ending in a tight button pair (filled "Approve" + outlined "Details"), with inline supporting metrics (ETA, savings, confidence) in small muted text.

## Components & Affordances

- **Buttons**: pill-to-rounded-rectangle; one filled accent primary per action cluster, others outlined/ghost. Split buttons (button + dropdown caret) for primary commit/export actions.
- **Pills/badges**: small rounded-full chips, tinted background + matching text, for status, severity, priority, and counts. Used heavily and consistently. Their tint should derive from the semantic hue over the current surface so they work in both themes.
- **Tabs**: underline-style horizontal tabs, active marked by an accent underline.
- **Filter controls**: compact dropdown selects and a date-range/segment control, right-aligned above lists.
- **Segmented category bar**: a row of selectable count-tiles ("All / Service risk / Supply risk …") acting as a filter switcher, active one tinted.
- **Inline data viz**: small sparklines in metric cells, donut/ring charts for composition, horizontal progress bars for coverage, node-on-map markers for geographic risk, small circular gauges for confidence/utilization — all rendered minimally with the semantic palette.
- **Tables/lists**: borderless, generous row padding, light horizontal dividers; numeric columns right-aligned.

## Overall Aesthetic

Restrained, dense-but-breathable, **enterprise data product**. Personality comes from disciplined spacing, strong numeric hierarchy, and functional color rather than ornament. Whitespace and hairline borders do the structural work. The agentic/AI layer is signaled through dedicated recommendation panels, confidence gauges, agent avatars, status pills, and an optional conversational "ask" input — always integrated into the same calm, neutral system. Switching between light and dark is a **background/surface swap only**: the same components, accent, and semantics render unchanged on either canvas.
