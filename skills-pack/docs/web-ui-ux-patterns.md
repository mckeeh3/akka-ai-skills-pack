# Web UI UX patterns

Use this doc when an Akka-hosted browser UI must be more than technically correct. For generated full-stack AI-first SaaS, apply UX planning to the agent workstream shell before route or visual-component details.

Pair with:
- `./workstream-ui-reference-architecture.md`
- `./structured-surface-contracts.md`
- `./web-ui-frontend-decomposition.md`
- `./web-ui-style-guide.md`
- `./web-ui-component-catalog.md`
- `./web-ui-quality-checklist.md`
- `../akka-web-ui-ux-design/SKILL.md`

## UX principles

1. **Workstream context first.** In the first five seconds, the user should know the selected functional agent, selected tenant/customer context, authority basis, current workstream state, and what needs attention.
2. **Surfaces are the renderable contract.** Dashboards, decision cards, forms, tables, traces, progress panels, and system messages are structured surfaces with typed state and capability-backed actions.
3. **The dashboard starts the surface graph.** A role-specific dashboard shows attention items, evidence, freshness, and next browser-tools; each attention or next-action work object moves through a human surface graph edge to a result, updated surface, progress surface, decision surface, or `system_message` surface.
4. **Dashboard things are operable.** Cards, rows, counters, badges, chart segments, task/progress panels, shortcuts, icons, and buttons that represent things needing attention or things the user can do next are clickable and keyboard-operable by default. A rectangular tile/card with a work-object name and a large count is itself the button; do not make only a tiny nested button operable. Zero-count tiles may still be operable when they open an empty queue, detail, explanation, setup, or history surface. Activation appends a request-like workstream item and appends/opens the surface where the user can inspect details and take allowed actions. Inert dashboard objects are explicit exceptions with a recorded reason.
5. **Browser-tools are explicit.** Every consequential UI action, read/query action, surface request, deep link, or recovery action maps to a browser-tool exposure backed by a governed-tool inside a backend capability.
6. **Primary action dominance.** The main next action should be visible, specific, and stronger than secondary actions.
7. **Progressive disclosure.** Show decision-driving evidence first; defer diagnostics, raw ids, and rare actions.
8. **Recoverability.** Users should know how to retry, correct validation errors, request approval, recover from stale state, or return to the workstream.
9. **State completeness.** Loading, empty, error, success, submitting, forbidden, denied, approval-needed, no-op, stale, reconnecting, and partial-data states are normal UI states.
10. **Accessible by default.** Semantics, labels, keyboard flow, focus, contrast, status text, and responsive task preservation are required.

## Workstream shell UX contract

For each generated SaaS app, define these shell regions before conventional route details. When My Account or profile/settings surfaces are in scope, include simple named-theme preference behavior: list available theme names from the authoritative UI style guide, allow selecting one theme id, apply it to the UI, and state the persistence scope truthfully.

```text
Functional-agent rail:
  visible/hidden/denied agents, attention badges, selected agent, role/capability basis
Context and authority bar:
  selected tenant/customer, membership, roles/capabilities, support access, pending approvals
Main workstream stream:
  user requests, surface requests, primary result surfaces, action feedback, system messages, traces
  no duplicate generic activity/detail surface when a typed result surface already represents the turn
Persistent composer:
  accepted prompt/action requests, disabled/forbidden states, selected-agent context, refocus behavior
Structured surfaces:
  dashboards, attention counter cards, decision cards, forms, tables, audit timelines, governance diffs, workflow/task progress, outcomes
Realtime/stale region:
  connected/reconnecting/stale indicators and recovery actions
```

Routes and deep links support these regions; they are not the root application model.

## Surface UX template

For each structured surface, define:

```text
Surface: <name>
Owning functional agent:
Surface type/version:
Placement: <inline | side-panel | modal | deep-link | reusable>
User goal:
Primary action:
Secondary actions:
Most important data:
Supporting data:
Capability/action mapping:
Entry points:
Exit paths:
Loading state:
Empty state:
Forbidden/denied state:
Validation state:
Approval-needed state:
Error and recovery:
No-op state:
Stale/reconnect state:
Success/system-message result:
Trace and audit links:
Responsive strategy:
Keyboard/focus path:
```

## Information hierarchy

Use this order unless the product context says otherwise:

1. selected functional agent, tenant/customer context, and concise purpose
2. attention state, pending decision, blocked work, or summary metric, rendered as the first dashboard counter strip when present, with KPI labels large/bold enough to scan and clearly separated from the number/status by deliberate spacing
3. primary action or next decision
4. decision-driving structured surface content
5. filters/search/sort for dense data when needed
6. secondary actions
7. details, diagnostics, metadata, audit/history, and trace links

Avoid:
- rare admin actions beside primary user actions
- internal component names as labels
- raw IDs or timestamps before user-meaningful labels
- equal visual weight for everything
- forcing users to inspect dense data before explaining what needs attention

## Action patterns

### Dashboard work-object interactions

Use for dashboard cards, rows, counters, badges, chart segments, task/progress panels, shortcuts, icons, and buttons that represent attention or next work. Treat the whole tile/card/counter hit area as the interaction target, including common rectangular KPI shapes with a label and large count. Count `0` does not make the object inert by itself; it can open the empty queue, detail, explanation, setup, or history surface for that category. Model each object as a human surface graph edge with source dashboard surface, canonical interaction type, target/result surface, browser-tool name where protected data is involved, governed-tool id, capability id, request/result append behavior, correlation/trace ids, and backend authorization. Ready dashboard payloads should contain authorized work the actor can do; hidden or forbidden targets should normally be omitted rather than shown as disabled dashboard objects. Denial/system-message states still apply for stale payloads, deep links, manual requests, races, or changed authorization.

### Surface request actions

Use for `show_dashboard`, `open_workstream`, `open_attention_item`, `refresh_surface`, row/card detail expansion, and deep-link recovery. Model each as a human surface graph edge with source surface, target or result surface, browser-tool name, governed-tool id, capability id, and backend authorization. They still require capability ids and backend authorization when protected data or cross-workstream state is involved.

### Command and proposal actions

Use specific verb phrases such as `Invite user`, `Save policy`, `Request approval`, or `Start investigation`. Side-effecting actions should display confirmation, approval, policy, or no-op behavior when required by the capability.

### Decision and approval actions

Decision-card actions should show recommendation, evidence, risk, alternatives, confidence/impact where available, and visible trace links before `Approve`, `Reject`, `Counter`, `Defer`, or `Escalate` actions.

### Trace and governance actions

Governance and audit surfaces should make policy versions, proposed diffs, simulations, rollback/commit state, trace ids, and evidence links visible without exposing secrets or privileged hidden facts.

## System-message surfaces

System feedback is a structured surface, not a toast-only string. Browser-tool results that do not navigate to a normal target surface should return or append a `system_message` surface. Define success, warning, validation, forbidden, approval-required, background-work-started, deferred-capability, stale/reconnect, no-op, and failure messages with:
- severity and message code
- user-safe title/body
- related surface/action/capability ids
- allowed recovery actions
- trace/correlation ids when visible
- redaction rules

## AutonomousAgent progress/result surfaces

When durable internal/background agent work is visible, show progress and results through typed surfaces:
- task accepted/queued/running/blocked/waiting-for-human/completed/failed/rejected
- dependency and handoff state
- evidence gathered and decisions needed
- policy/approval requirements
- retry, cancel, escalate, open trace, or open result actions

Do not represent task progress only as a spinner, raw log, or untracked chat text.

## State patterns

### Loading

Use skeletons for structured content and progress text for longer actions. Keep layout stable.

### Empty

Explain what is missing, why it may be missing, and the next allowed action.

### Error

Say what failed, whether user work is preserved, and what can be retried or recovered.

### Forbidden/denied

Explain the denial in user-safe language without leaking hidden workstream existence, privileged facts, prompt text, provider details, or cross-tenant data.

### Success and no-op

Confirm the concrete outcome and next step. If nothing changed, explain why and where to continue.

## Forms and dense data

Forms must define labels, helper text, validation, backend error mapping, submit state, idempotency expectations, success behavior, and focus movement after validation failure.

Dense tables, queues, charts, and cards should define default ordering, filters/search needed for realistic volume, item primary action, status treatment, empty-filtered vs truly-empty states, and narrow-screen transformation. Dashboard attention/KPI cards must not compress label, count, and badge into a cramped cluster; use the shared attention-card style with larger semibold/bold labels and visible vertical rhythm so users can read the attention category before acting on the number. Cards, counters, badges, rows, and task/progress panels that represent attention or next work should look and behave as modern high-tech operable work buttons, with the whole shape clickable/tappable, visible focus, clear hover/active treatment, and enough visual energy to communicate that tapping the dashboard does real capability-backed work. Place these counters above dashboard details, profile/settings panels, queues, and lists; detailed investigation belongs below the counter strip or inside the target workstream dashboard.

## Deep links

The primary app model is the role-authorized functional-agent rail plus continuous workstream. Deep links may select a functional agent, stream item, or surface, but must load protected data through authorized APIs and render unavailable, forbidden, not-found, stale, and recovery states.

## UX copy patterns

Use human, concrete language:

| Situation | Prefer | Avoid |
| --- | --- | --- |
| Primary button | `Request approval` | `Submit` |
| Save success | `Policy changes saved` | `Success` |
| Required field | `Enter a policy name` | `Invalid input` |
| Load failure | `Could not load the audit timeline. Retry.` | `Error occurred` |
| Empty queue | `No decisions need your review` | `No data` |
| Permission | `You do not have permission to change this policy` | `Forbidden` |

## Responsive behavior

For narrow screens:
- preserve selected-agent context and primary action visibility
- stack secondary surfaces below primary work
- transform dense tables into cards or reduce columns intentionally
- maintain touch target size
- avoid horizontal scrolling except for intentionally dense technical evidence

## UX implementation handoff

Before coding, the agent should be able to state:
- which functional agents appear in the rail and which are hidden or denied
- which role-specific dashboard attention and next-action objects appear, which sources/evidence/freshness they use, which target/result surfaces they open, and which browser-tools they offer
- which human surface graph nodes and edges exist, including result and system-message surfaces
- what the user sees first in the selected workstream
- how the composer behaves and when it is disabled
- which structured surfaces can appear and which capabilities back their actions
- what the primary action is and why
- what happens when there is no data
- what happens when loading is slow, failed, forbidden, stale, or reconnecting
- what happens when validation fails
- what success and no-op results look like
- how mobile and keyboard users complete the same task
