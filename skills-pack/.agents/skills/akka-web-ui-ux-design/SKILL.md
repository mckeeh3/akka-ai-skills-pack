---
name: akka-web-ui-ux-design
description: Design excellent user experience for Akka-hosted browser apps before frontend implementation, including agent workstream shell intent, structured surface intent, information hierarchy, interaction quality, UX copy, feedback, recovery, accessibility, and responsive behavior.
---

# Akka Web UI UX Design

Use this skill before implementing any non-trivial browser app, dashboard, admin UI, console, portal, workstream shell, or workflow UI hosted by Akka.

For generated full-stack AI-first SaaS, apply this to the agent workstream shell before conventional screens: left-rail functional agents, main workstream panel, bottom composer, context/authority indicators, and typed structured surfaces.

Use this skill before implementing details with `akka-web-ui-frontend-project` so the full web app has an explicit UX contract.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md`, `../docs/app-worker-tool-model.md`, and `../docs/app-description-to-code-compile-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the responsible worker, execution harness, actor adapter, governed tool, capability, AuthContext/scope, DTO, side-effect/idempotency policy, trace/result surface, selected implementation path, and tests are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## AI-first UX role

For AI-first SaaS surfaces, design around supervision, judgment, teaching, and accountability before conventional record management. The first five seconds should answer: which objective or plan is active, what the agent/system is doing, what needs human attention, what authority the human retains, and how to inspect evidence or trace history.

UX design also owns visual intentionality. Do not hand off a vague "modern SaaS" direction. Commit to a recorded aesthetic point of view, memorable motif, and hierarchy strategy before implementation. For the canonical pack style this should remain within the AI-first workstream enterprise language: command desk, governance cockpit, trust-and-outcomes workspace, dense operational panels, authority/trace rails, decision cards, designed controls, and named color themes. Avoid yesterday's passive CRUD SaaS dashboard look: AI-first dashboards should feel like cutting-edge command surfaces where tapping a visible work object can initiate or continue significant governed work.

Include these AI-first workstream/surface patterns for generated full-stack AI-first SaaS:
- **Functional-agent rail:** role-authorized work areas, attention indicators, hidden/denied agent recovery, and selected-agent context.
- **Main workstream:** user intent, primary result surfaces, capability results, workflow progress, decisions, traces, and follow-up actions in one continuous timeline; do not duplicate a prompt/action as both a generic activity-detail card and the actual typed result surface.
- **Persistent composer:** contextual natural-language requests, command shortcuts, uploads where allowed, disabled/forbidden explanations, deterministic surface routing with editable prefill/no-mutation copy, and, where modeled, confirmed chat tool-plan review with explicit plan-bound confirmation before consequential execution.
- **Role-specific dashboard / command center surface:** a dashboard is an action router, not a report. Its ready state must show **things that need my attention** first and **things I can do** second. Aside from section labels, control labels, and minimal explanatory microcopy, visible dashboard content must be actionable/clickable indicators: cards, rows, counters, badges, chart segments, task/progress panels, shortcuts, icons, and buttons that support click and keyboard activation, append a request-like workstream item, and append/open the detail, decision, progress, evidence/trace, result, updated dashboard, or typed `system_message` surface where the user can inspect details and act. Passive FYI metrics, inert charts, decorative card grids, and status tiles that make a user ask “so what can I do with this?” are not acceptable dashboard content; move them to report/detail/analytics surfaces or turn them into governed drilldowns. The common rectangular tile/card with the thing's name and a large count is itself the button, including when the count is `0` and opens an empty queue, detail, explanation, setup, or history surface; `0 things need your attention` is better than hiding the indicator. Do not rely on separate "clickable buttons" while rendering the work objects themselves as inert. All dashboards with attention counters must use the same attention-card counter strip style, placed above lower-priority details/lists. Labels must be slightly larger and semibold/bold, with deliberate vertical spacing between label, number, and badge/status so the card does not feel cramped.
- **Decision card / deviation review surface:** recommendation, evidence, risk, confidence, impact, policy trigger, alternatives, and approve/reject/defer/escalate actions.
- **Policy/governance center surface:** policy versions, proposals, simulations, human-authorized commits, examples, thresholds, and rollback context.
- **Async digest surface:** compressed routine activity with material events, pending decisions, outcome deltas, and trace links.
- **Audit/work trace surface:** who/what/when/why/how-authorized, tool/data access, policy invocations, approvals, overrides, and outcomes.

Do not hide consequential AI behavior behind generic dashboards, page-first CRUD screens, or chat transcripts. Conversation may help intake, explanation, or confirmed human chat tool planning, but the UX handoff should resolve consequential work into durable goals, proposed plans, explicit confirmations, decisions, approvals, traces, policies, outcomes, and structured review/result surfaces backed by governed capabilities.

## Required reading

- `../docs/web-ui-frontend-decomposition.md`
- `../docs/web-ui-ux-patterns.md`
- `../docs/web-ui-style-guide.md`
- `../docs/web-ui-component-catalog.md`
- `../docs/web-ui-quality-checklist.md`
- `../examples/web-ui/ai-first-workstream-enterprise/README.md`, `../examples/web-ui/ai-first-workstream-enterprise/component-catalog.html`, and the static mockups there when using the canonical style
- relevant app-description UI files under `app-description/55-ui/**` or specs under `specs/**` when present

## Use this skill when

- a UI brief needs to become implementable functional agents, workstream shell regions, or structured surfaces
- the user asks for a dashboard, portal, admin console, or browser workflow; normalize generated SaaS work into functional agents and surfaces
- a structured surface has forms, decisions, multi-step flows, dense data, status, or errors
- UX quality matters, not just route/static asset delivery
- existing UI work needs review for clarity, polish, accessibility, or task efficiency

Do not use this for static file serving or a single documentation page unless the page has a real user journey.

## UX plan required before implementation

For each workstream shell region, structured surface, or major region, define:

1. **User goal** — what the user is trying to accomplish.
2. **Primary decision/action** — the one thing that must be obvious.
3. **Secondary actions** — useful but visually subordinate actions.
4. **Information hierarchy** — most important data first; supporting detail later.
5. **Entry and exit paths** — how the user arrives, completes, cancels, or moves on.
6. **Loading experience** — skeleton, placeholder, spinner, or progress text.
7. **Empty state** — why there is no data and what the user can do next.
8. **Error recovery** — actionable recovery text and retry/correction path.
9. **Success feedback** — what changed and what the user can do next.
10. **Validation behavior** — field-level messages, focus movement, input preservation.
11. **Destructive/irreversible actions** — confirmation and recovery expectations.
12. **Responsive behavior** — how the primary task survives on narrow screens.
13. **Keyboard/focus path** — how a keyboard-only user completes the primary flow.
14. **UX copy** — labels, button text, helper text, empty/error/success messages in SaaS user language, not internal component/tool/policy terminology.
15. **User-visible vs internal metadata** — default-visible fields, progressive drilldowns, role-gated audit/support/developer diagnostics, and internal-only metadata that must never be rendered to ordinary users.
16. **Style guide and component catalog application** — how selected style tokens, named themes, reusable component catalog anatomy, reference mockup anatomy, visual hierarchy, typography, texture/depth, and purposeful motion support feedback, structured-surface form controls, and My Account preference behavior when in scope.
17. **Browser-tool/capability mapping** — which browser-tool invokes which governed-tool/backend capability for each consequential surface action and how forbidden/denied states are shown without leaking raw ids or hidden facts.
18. **Confirmed chat tool-plan mapping when allowed** — which governed-tool ids may be invoked from chat after plan review, what details the plan must show, how confirmation is bound, how changed plans are reconfirmed, and which result/partial-failure/trace surfaces appear.

## UX copy rules

Prefer specific, actionable copy:

- Buttons use verbs: `Submit request`, `Approve`, `Save changes`, `Retry`.
- Empty states explain the condition and next action.
- Validation messages say how to fix the input.
- Error messages explain what failed and what to try next.
- Success messages confirm the concrete outcome.
- Destructive confirmations name the object and consequence.

Avoid generic or internal copy:

- `Submit` when a more specific action exists
- `Error occurred`
- `Invalid input`
- `Success`
- unexplained disabled controls
- raw capability/tool/policy ids, backend component names, provider/model details, prompt internals, trace ids, or correlation ids as primary copy for ordinary users

## Interaction quality rules

- Each structured surface has one dominant purpose.
- The primary action is visually obvious and reachable without scrolling on common layouts when practical.
- Secondary/destructive actions do not compete with the primary action.
- Dense data has search, filter, sort, grouping, or progressive disclosure when needed.
- Status is visible through text plus visual treatment, not color alone.
- Slow backend behavior is represented with loading/progress states.
- Users can recover from common mistakes without losing work.
- Navigation shows current location and supports returning to the prior task.

## Output format

Produce a concise UX handoff that downstream implementation can follow:

```text
## UX handoff: <app/shell region/surface>

Functional agent context:
Surface type/version:
Surface graph role: <dashboard trunk | branch | result | system-message | deep-link target>
User goal:
Primary action:
Secondary actions:
Browser-tool/governed-tool/capability mapping:
Confirmed human chat tool-plan mapping (if allowed):
Attention source and freshness (for dashboards/queues):
Information hierarchy:
States:
- loading:
- empty:
- ready:
- validation failure:
- API failure:
- forbidden/denied:
- success:
- stale/reconnecting:
UX copy:
Responsive behavior:
Keyboard/focus behavior:
Deep-link behavior:
Visual craft:
Aesthetic point of view:
Memorable motif:
Hierarchy strategy:
Typography/token strategy:
Texture/depth/motion notes:
Forbidden generic patterns:
Implementation notes:
```

For AI-first structured surfaces, add:

```text
AI-first surface type:
Human authority shown:
Agent/system activity shown:
Evidence/risk/policy shown:
Approval/exception actions:
Trace/outcome links:
User-visible vs internal metadata:
Realtime/stale behavior:
```

## Review checklist

Before coding or accepting UI work, verify:

- the first five seconds communicate selected functional agent, active tenant/customer context, what matters in the workstream, and what the user can do
- every shell region and surface has a clear primary action or clear read-only purpose
- empty/error/success states are useful, not placeholders
- forms preserve user input and focus the first problem after validation failure
- structured-surface form controls, including detail-edit inputs/selects/textareas, look like designed tokenized controls rather than browser defaults
- named-theme selection in My Account previews the selected theme immediately while Save/Confirm remains the governed persistence action
- destructive actions are hard to trigger accidentally
- mobile layout preserves the main task
- keyboard-only flow reaches and completes primary actions
- selected style guide tokens, named-theme bundles, typography, texture/depth choices, and canonical reference mockup anatomy are used to reinforce hierarchy, focus, and status without relying on color alone
- dashboard KPI/attention cards and next-action work objects use the shared attention-card style across dashboards, appear above lower-priority details/lists, have readable semibold/bold labels plus enough gap between label, value, and status badge for fast scanning, and are clickable/keyboard-operable when they represent attention or available work; the whole rectangular tile/card/counter is the control, including valid zero-count states
- dashboard ready states contain only actionable/clickable indicators plus labels and minimal microcopy; passive FYI metrics, inert charts, and decorative card grids are rejected unless converted into governed drilldowns or moved to report/detail/analytics surfaces
- left rail, workstream panel, composer, and surface actions remain usable by keyboard and at narrow widths
- AI-first surfaces show delegated work, retained authority, evidence, policy triggers, trace links, and outcome context when those concepts are in scope, using user-readable summaries by default and role-gated raw audit/support detail only when appropriate
- the UI avoids generic AI/SaaS visual cliches and has a clearly recorded memorable motif
- consequential surface actions and dashboard object interactions map to browser-tool exposures backed by governed-tools and backend capabilities, but those internal mappings are not exposed as normal user copy; UI gating is not treated as authorization, and forbidden dashboard targets are normally omitted from ready dashboard payloads rather than shown as disabled work objects
