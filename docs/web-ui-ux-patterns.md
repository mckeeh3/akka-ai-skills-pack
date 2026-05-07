# Web UI UX patterns

Use this doc when an Akka-hosted browser UI must be more than technically correct. It defines UX patterns that future agents should apply before writing frontend code.

Pair with:
- `docs/web-ui-frontend-decomposition.md`
- `docs/web-ui-style-guide.md`
- `docs/web-ui-quality-checklist.md`
- `skills/akka-web-ui-ux-design/SKILL.md`

## UX principles

1. **One clear purpose per screen.** A user should understand the screen's job in the first five seconds.
2. **Primary action dominance.** The main action should be visible, specific, and visually stronger than secondary actions.
3. **Progressive disclosure.** Show summary and decision-driving data first; defer diagnostics and rare actions.
4. **Recoverability.** Users should know how to fix validation errors, retry failed loads, undo/cancel where possible, and return to their task.
5. **State completeness.** Loading, empty, error, success, submitting, unauthorized, and stale states are part of the UI, not edge cases.
6. **Accessible by default.** Semantics, labels, keyboard flow, focus, contrast, and non-color status are required UX quality, not polish.
7. **Responsive task preservation.** Narrow screens may change layout, but must not hide or break the primary task.

## Screen pattern

For each screen, define:

```text
Screen: <name>
Route/UI path:
User goal:
Primary action:
Secondary actions:
Most important data:
Supporting data:
Entry points:
Exit paths:
Loading state:
Empty state:
Error state and recovery:
Success feedback:
Responsive strategy:
Keyboard/focus path:
```

## Information hierarchy

Use this order unless the product context says otherwise:

1. page title and concise purpose
2. current status or summary metric, when relevant
3. primary action
4. decision-driving content
5. filters/search/sort for dense data
6. secondary actions
7. details, diagnostics, metadata, audit/history

Avoid:
- putting rare admin actions beside primary user actions
- showing raw IDs, timestamps, or internal states before user-meaningful labels
- using equal visual weight for everything
- making users read tables before telling them what needs attention

## Action patterns

### Primary action

- Use a specific verb phrase: `Create request`, `Approve`, `Save changes`.
- Place consistently near the screen title or task region.
- Disable only with an explanation or visible unmet requirement.

### Secondary action

- Use lower visual emphasis.
- Keep near the relevant object or section.
- Do not compete with the primary action.

### Destructive action

- Use explicit labels: `Delete project`, not `Delete` when ambiguity is possible.
- Require confirmation for irreversible operations.
- Confirmation copy must name the object and consequence.
- Prefer recovery/undo when feasible.

## State patterns

### Loading

Good loading states tell users what is happening:
- use skeletons for structured content
- use progress text for actions that take time
- keep layout stable to avoid jumps

Avoid blank pages and indefinite spinners without context.

### Empty

Good empty states include:
- what is missing
- why it may be missing
- what the user can do next

Example:

```text
No purchase requests yet.
Create the first request to start an approval workflow.
[Create request]
```

### Error

Good error states include:
- what failed
- whether user work is preserved
- what the user can do next
- a retry or navigation path when appropriate

Example:

```text
Could not load approvals.
Your filters are still selected. Retry loading the list or clear filters.
[Retry] [Clear filters]
```

### Success

Good success states confirm the concrete outcome and next step:

```text
Request submitted for approval.
Track status from the request detail page.
[View request]
```

## Forms

A high-quality form:
- groups related fields
- labels every field
- explains unusual inputs with helper text
- validates before submit for simple format/required errors
- preserves input after failure
- maps backend validation to fields when possible
- focuses the first invalid field after validation failure
- disables submit while submitting and explains progress
- confirms success with what changed

Avoid forms that only show a generic top-level error.

## Dense data

For tables, lists, queues, and dashboards, define:
- default sort order
- filters/search needed for realistic data volume
- row/card primary action
- status and priority treatment
- empty filtered state vs truly empty state
- narrow-screen transformation: cards, stacked rows, or reduced columns

Use tables for comparison across columns. Use cards/lists when each item has a primary action or status summary.

## Navigation

Navigation should answer:
- where am I?
- what can I do here?
- how do I return to the previous task?
- what changed after my action?

For multi-screen apps:
- show active navigation state
- use stable labels matching user language
- avoid exposing backend component names as navigation labels
- define not-found behavior for unknown routes

## UX copy patterns

Use human, concrete language:

| Situation | Prefer | Avoid |
| --- | --- | --- |
| Primary button | `Submit request` | `Submit` |
| Save success | `Changes saved` | `Success` |
| Required field | `Enter a request title` | `Invalid input` |
| Load failure | `Could not load requests. Retry.` | `Error occurred` |
| Empty list | `No requests match these filters` | `No data` |
| Permission | `You do not have permission to approve this request` | `Forbidden` |

## Responsive behavior

For narrow screens:
- preserve primary action visibility
- stack secondary content below primary content
- convert wide tables to cards or reduce columns intentionally
- maintain touch target size
- avoid horizontal scrolling except for intentionally dense data views

## UX implementation handoff

Before coding, the agent should be able to state:
- what the user sees first
- what action is primary
- what happens when there is no data
- what happens when loading is slow or fails
- what happens when validation fails
- what success looks like
- how mobile and keyboard users complete the same task
