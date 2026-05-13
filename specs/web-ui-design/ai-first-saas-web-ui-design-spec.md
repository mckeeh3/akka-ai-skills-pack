# AI-first SaaS Web UI Design Spec

## Status

- status: draft for seed app validation
- source mockups: `specs/web-ui-design/images/ai-first-saas-web-ui-01.png` through `ai-first-saas-web-ui-08.png`
- first target: `docs/examples/ai-first-saas-seed-app-description/`
- later target: integrate the validated design system into skills-pack guidance and generation assets

## Design intent

The mockups define an AI-first operational SaaS interface for supervising delegated agent work. The UI should make it clear, within the first five seconds:

1. what objective or operational area is active;
2. what agents are doing autonomously;
3. what needs human attention now;
4. which policy or approval boundaries control agent action;
5. where the user can inspect evidence, trace history, and outcomes.

The interface is not a generic analytics dashboard with a chat box. The assistant prompt is a command/explanation affordance, while consequential work is represented as durable goals, queues, decisions, approvals, policy controls, traces, and agent activity.

## Mockup-derived UI principles

- **Persistent supervision shell:** left navigation, user/account region, notifications, and collapsible navigation are stable across screens.
- **Page-local mission framing:** every page has a title, short explanatory subtitle, and optional primary page action.
- **AI command strip:** a prominent assistant strip appears near the top of work surfaces with suggested prompts and a send/action control.
- **KPI summary band:** important operational facts are summarized immediately below the command strip using cards or segmented panels.
- **Decision-first hierarchy:** exception and approval queues receive strong placement and clear action controls.
- **Agent activity visibility:** autonomous work is visible as timelines, agent cards, upcoming actions, and status summaries.
- **Governance visibility:** policy/trust controls are first-class UI regions, not hidden settings.
- **Traceable evidence path:** every recommendation, exception, and decision card should link to details, reasoning, evidence, or audit trace.
- **Mode parity:** light and dark mode must preserve layout, hierarchy, status semantics, and contrast.
- **Theme restraint:** lightweight themes may change only colors and fonts; layout, component anatomy, spacing scale, and interaction rules remain stable.

## Supported modes and themes

### Mode policy

- The design must support `light`, `dark`, and `system` color mode selection.
- Both light and dark token sets are required for every theme.
- Mode switching must be token-driven through CSS variables, not hard-coded component branches.
- Generated frontend code may toggle a documented theme/mode class or `data-theme` attribute only.

### Lightweight theme scope

Themes are limited to:

- color tokens;
- font-family tokens.

Themes must not change:

- screen inventory;
- navigation structure;
- grid behavior;
- card anatomy;
- component sizes;
- spacing scale;
- border radii;
- icon semantics;
- UX copy rules;
- accessibility constraints.

## Seed theme: Atlas Ops supervisory console

This is the initial theme to test against the seed app.

### Font tokens

```css
:root {
  --font-sans: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
  --font-mono: "Roboto Mono", "SFMono-Regular", Consolas, monospace;
}
```

### Shared non-theme tokens

These tokens are stable across lightweight themes.

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
  --color-bg: #f8fafc;
  --color-bg-subtle: #f1f5f9;
  --color-sidebar: #ffffff;
  --color-surface: #ffffff;
  --color-surface-raised: #ffffff;
  --color-surface-soft: #f8fafc;
  --color-surface-accent: #f5f3ff;

  --color-text: #0f172a;
  --color-text-soft: #334155;
  --color-muted: #64748b;
  --color-inverse-text: #ffffff;

  --color-border: #e2e8f0;
  --color-border-strong: #cbd5e1;

  --color-primary: #2563eb;
  --color-primary-strong: #1d4ed8;
  --color-primary-soft: #dbeafe;
  --color-ai: #7c3aed;
  --color-ai-soft: #ede9fe;

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
  --color-chart-amber: #f59e0b;
  --color-chart-red: #ef4444;
  --color-chart-cyan: #06b6d4;

  --color-focus: #2563eb;
  --shadow-card: 0 16px 40px rgb(15 23 42 / 0.08);
  --shadow-glow-ai: 0 0 0 1px rgb(124 58 237 / 0.18), 0 20px 60px rgb(124 58 237 / 0.10);
}
```

### Dark mode color tokens

```css
[data-mode="dark"] {
  --color-bg: #07111f;
  --color-bg-subtle: #0b1626;
  --color-sidebar: #070d18;
  --color-surface: #101a28;
  --color-surface-raised: #142033;
  --color-surface-soft: #0d1826;
  --color-surface-accent: #17152d;

  --color-text: #f8fafc;
  --color-text-soft: #cbd5e1;
  --color-muted: #94a3b8;
  --color-inverse-text: #ffffff;

  --color-border: #243247;
  --color-border-strong: #38506d;

  --color-primary: #3b82f6;
  --color-primary-strong: #60a5fa;
  --color-primary-soft: #1e3a8a;
  --color-ai: #a855f7;
  --color-ai-soft: #2e185c;

  --color-success: #4ade80;
  --color-success-soft: #123524;
  --color-warning: #fbbf24;
  --color-warning-soft: #3a2a08;
  --color-danger: #f87171;
  --color-danger-soft: #3b1218;
  --color-info: #38bdf8;
  --color-info-soft: #0b3145;

  --color-chart-blue: #3b82f6;
  --color-chart-violet: #a855f7;
  --color-chart-green: #4ade80;
  --color-chart-amber: #fbbf24;
  --color-chart-red: #f87171;
  --color-chart-cyan: #22d3ee;

  --color-focus: #93c5fd;
  --shadow-card: 0 18px 50px rgb(0 0 0 / 0.28);
  --shadow-glow-ai: 0 0 0 1px rgb(168 85 247 / 0.30), 0 20px 70px rgb(76 29 149 / 0.30);
}
```

## Layout system

### App shell

- left sidebar on desktop, approximately `--shell-sidebar-width`;
- product logo at top;
- primary nav items first, then grouped sections such as Intelligence and Governance;
- notifications and current user profile pinned near the bottom;
- collapse control at bottom;
- active item uses soft primary background plus primary icon/text color;
- unavailable sections are hidden or disabled based on permissions, with backend authorization remaining authoritative.

### Main content

- content region uses a responsive grid with a maximum width token;
- page title and subtitle sit above the AI command strip;
- dashboard regions use a 12-column grid on wide screens;
- cards stack by priority on narrow screens;
- decision/exception queues must remain above lower-priority reports on narrow screens.

### Responsive behavior

- desktop: persistent sidebar and multi-column dashboard grid;
- tablet: narrower sidebar or icon-only sidebar, 2-column card groups;
- mobile: sidebar becomes a menu/drawer, command strip and KPI cards stack, tables become cards or horizontally scroll only when card conversion would lose meaning;
- primary decision actions stay visible without requiring horizontal scroll.

## Core components

### AI command strip

Purpose: let users ask for summaries, risk explanations, and safe actions without making chat the source of truth.

Required elements:

- AI icon or mark;
- prompt/input text such as `Ask Atlas to summarize work, explain risks, or take action...` adapted to the app name;
- suggested prompt chips;
- send/action button;
- visual distinction through AI accent token and subtle glow/border.

Rules:

- suggested prompts use verbs and operational language;
- command results that affect work must become durable objects or actions;
- risky actions route to decision cards, approvals, or confirmation flows.

### KPI summary cards

Required elements:

- label;
- current value;
- trend/delta with direction and text;
- optional icon or sparkline;
- status color plus text, not color alone.

### Decision and exception cards

Required elements:

- subject/entity name;
- originating agent or system;
- status/risk/policy badge;
- recommendation;
- reason/evidence summary;
- impact or confidence when available;
- primary action;
- secondary evidence/details action.

Primary actions may include `Approve`, `Review plan`, `Dispatch`, `Counter`, `Edit message`, `Reschedule`, `See reasoning`, or domain-specific equivalents.

### Agent activity timeline

Required elements:

- timestamp;
- agent name;
- action summary;
- automation/review/escalation badge;
- icon with semantic color;
- detail affordance.

### Agent/team cards

Required elements:

- agent/team name;
- autonomy or trust metric when relevant;
- trend;
- recent action count;
- success or quality metric;
- active/blocked/requires-input state.

### Governance/trust controls

Required elements:

- policy or control name;
- configured threshold/authority;
- enabled/verified status;
- link to edit policy guardrails or inspect policy history.

### Data visualization

- Charts use theme chart tokens only.
- Legends are required for donut, map, and multi-series charts.
- Status meaning must be represented with label text and/or icons, not color alone.
- Critical metrics use tabular numerals.

## Screen patterns for seed validation

Map the mockup system onto the seed app screens as follows.

### Mission Control / Briefing

AI-first surface type: command center and async digest.

Required regions:

1. page title/subtitle explaining supervised autonomous work;
2. AI command strip for summaries, risk explanations, and action intake;
3. operational KPI band: active goals, waiting decisions, exceptions, outcome delta;
4. agent execution timeline;
5. needs-your-attention queue;
6. agent teams/trust summary;
7. trust controls/policy guardrails;
8. upcoming autonomous actions.

### Goal Workbench

AI-first surface type: goal-to-execution workbench.

Required regions:

1. goal/objective form;
2. success criteria and constraints;
3. proposed execution plan;
4. agent/team assignment;
5. tool/data permissions;
6. approval gates;
7. launch simulation or review action.

### Decision Queue and Decision Detail

AI-first surface type: decision card / deviation review.

Required regions:

1. queue filters by priority, policy trigger, agent, and due time;
2. recommendation summary;
3. evidence and risk;
4. alternatives considered;
5. approve/reject/counter/defer/escalate actions;
6. trace link and outcome feedback link.

### Governance Center

AI-first surface type: policy/governance/learning center.

Required regions:

1. policy list and versions;
2. approval thresholds and authority boundaries;
3. proposed policy changes;
4. simulation/replay results;
5. human-authorized commit flow;
6. rollback and audit links.

### Audit Trace Explorer

AI-first surface type: audit/work trace.

Required regions:

1. search/filter by goal, agent, decision, tool, user, policy, time;
2. chronological trace entries;
3. evidence/tool/data-access details;
4. authorization and policy invocation details;
5. outcome links.

## Accessibility requirements

- Meet WCAG AA contrast for text and meaningful UI controls in both modes.
- Focus rings use `--color-focus` and must be visible against all surfaces.
- Keyboard users can reach nav, command strip, cards, filters, tables, and primary actions in logical order.
- Status must not depend on color alone; include labels, icons, and/or text.
- Motion is subtle and disabled or reduced under `prefers-reduced-motion`.
- Form validation errors identify the field and how to fix it.
- Action confirmations name the object and consequence.

## Implementation expectations for seed app test

Authoritative seed app UI files to update during validation:

- `docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/style-guide.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/screens-and-navigation.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/states-and-realtime.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/accessibility-and-responsive.md`

Expected generated frontend shape when realization is requested:

- React + Vite + TypeScript frontend;
- tokenized CSS variables for theme and mode;
- no copied demo names, users, logos, or metrics from mockups;
- app-specific names and data from the seed app description;
- components consume tokens and semantic props rather than hard-coded colors;
- tests or checks verify both light and dark mode render without inaccessible contrast regressions where practical.

## Acceptance checklist

- [ ] Light and dark modes are both specified and visually equivalent in hierarchy.
- [ ] Theme overrides are limited to color and font tokens.
- [ ] App shell, command strip, KPI band, decision cards, agent activity, governance controls, and audit paths are represented.
- [ ] AI-first surfaces expose human authority, agent activity, evidence/risk/policy, trace links, and outcome context.
- [ ] Responsive behavior preserves the primary decision/action on narrow screens.
- [ ] Accessibility rules cover contrast, focus, keyboard, color-not-alone semantics, and reduced motion.
- [ ] Seed app UI description can reference this spec without copying mockup demo content.
