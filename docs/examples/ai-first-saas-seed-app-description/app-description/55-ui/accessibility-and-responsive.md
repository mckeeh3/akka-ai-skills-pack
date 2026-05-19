# Accessibility and Responsive

## Accessibility requirements

- keyboard navigation required for:
  - functional-agent rail and route/deep-link controls
  - AI command strip and suggested prompt chips
  - forms
  - modals/drawers
  - tables/card lists
  - decision-card actions
  - policy controls
  - audit trace filters and results
- decision cards must preserve semantic headings and readable evidence/risk/policy sections
- color must not be the only risk/status indicator; pair status color with label text and/or icon
- focus ring must be visible in light and dark mode using the selected style-guide focus token
- text and meaningful controls must meet WCAG AA contrast in light and dark modes
- form validation errors must be programmatically associated with fields where applicable
- destructive or high-impact actions must name the object and consequence before confirmation
- decorative motion must respect `prefers-reduced-motion`

## First-five-seconds comprehension target

On mission-control and decision surfaces, a user should immediately understand:

1. what objective or operational area is active;
2. what agents are doing;
3. what needs human attention;
4. what authority boundary or policy applies;
5. where to inspect evidence, trace, or outcome context.

## Responsive baseline

- desktop:
  - persistent sidebar
  - full command center dashboard
  - multi-column card/grid layout
- tablet:
  - compact or icon-only sidebar is acceptable
  - review queues and decision cards remain fully usable
  - KPI cards collapse to two columns
- mobile:
  - functional-agent rail becomes a drawer/menu
  - approvals, alerts, and basic admin review are usable
  - decision/action regions appear before charts and lower-priority reports
  - KPI cards stack
  - data tables convert to cards when practical, otherwise horizontal scroll must preserve labels and primary actions

## Keyboard/focus behavior

- workstream load focus starts at the selected functional-agent heading, first actionable surface, or skip link target
- skip link allows bypassing the functional-agent rail to main workstream content
- after validation failure, focus moves to the first invalid field or form-level error summary
- after opening a modal/drawer, focus moves into it and is trapped until closed
- after closing a modal/drawer, focus returns to the invoking control
- after approving/rejecting a decision, focus moves to the next actionable item or a confirmation region

## Light/dark validation

- all core seed UI surfaces must be reviewed in light and dark mode:
  - app shell
  - mission control / briefing
  - decision queue
  - decision card detail
  - governance center
  - audit trace explorer
- mode switching must not remove focus visibility, status labels, or action affordances
