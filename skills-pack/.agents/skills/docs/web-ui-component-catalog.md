# Web UI component catalog

Use this catalog with `./web-ui-style-guide.md`, `./web-ui-ux-patterns.md`, and the static visual reference page at `../examples/web-ui/ai-first-workstream-enterprise/component-catalog.html`.

This catalog defines reusable AI-first SaaS UI components for generated Akka-hosted browser apps. Components are structured-surface building blocks: they clarify dashboard attention, decisions, authority, traces, state feedback, and governed actions. They are not authorization controls, route contracts, or proof that a runtime app works.

## Rules

- Reuse these component anatomies before inventing new dashboard/card/list patterns.
- Keep capability ids, governed-tool ids, AuthContext, authorization, DTOs, trace/audit semantics, and tests authoritative in app-description/specs/backend code.
- Replace demo names, counts, users, and labels with target-domain content.
- Use selected style tokens and named themes; switching theme changes color tokens only, not component anatomy or behavior.
- Every consequential action must map to a governed backend capability. UI visibility, disabled controls, prompt text, and route names are not authorization.
- Component content is for SaaS users first. Translate internal capability, governed-tool, policy, trace, provider/model, prompt, event, and correlation/idempotency details into user-safe labels, summaries, recovery text, and role-gated drilldowns.

## Components

### Attention counter card

Use when a dashboard answers “what needs my attention?” with clickable counters.

Anatomy:
- short semibold/bold label
- large numeric count
- concise badge/status/action hint below the count

Placement:
- render as a counter strip near the top of role-specific dashboards, command centers, and My Account dashboards
- place before lower-priority profile/settings/details, queues, lists, or explanatory panels
- clicking opens the relevant governed list/detail/surface, not an unprotected route shortcut

Do not:
- compress label, count, and status into a cramped inline cluster
- make each dashboard invent a different counter shape
- treat the card as authorization

Accessibility:
- use link/button semantics for clickable cards
- expose the full attention category and action in accessible text
- include status text, not color alone

### AI command strip

Use when a functional agent accepts contextual requests such as summaries, risk explanations, or safe action preparation.

Anatomy:
- AI mark/icon
- contextual prompt text or input
- optional suggested prompt chips
- specific send/action button

Behavior:
- consequential command results become durable goals, decisions, approvals, proposals, traces, or system-message surfaces
- chat text is not the source of truth for protected actions

### Decision card

Use when a human must review a recommendation, exception, approval, or policy deviation.

Anatomy:
- subject/entity name
- recommendation
- evidence/rationale summary
- risk, confidence, impact, and policy trigger where available
- primary decision action plus subordinate alternatives
- trace/outcome links

Behavior:
- approve/reject/counter/defer/escalate actions map to governed capabilities
- show no-op, approval-required, denial, and success result surfaces

### Queue row/card

Use for sorted work needing selection before detail review.

Anatomy:
- subject and context
- value/impact/due information
- risk/status badge
- trace/evidence readiness
- primary open/review action

Rules:
- sort by urgency, risk, freshness, due time, or business priority
- keep rows dense but readable
- transform into cards on narrow screens when needed

### System-message card

Use for action results that do not naturally navigate to another structured surface.

Anatomy:
- severity and user-safe title
- concise body explaining what happened
- recovery or next action
- related surface/action/capability ids only in role-appropriate diagnostic contexts, otherwise user-safe labels
- trace/correlation id only when useful and visible to the current role

States:
- success
- warning/stale/reconnect
- validation failure
- forbidden/denied
- approval required
- no-op
- provider/runtime blocked

### Authority/context panel

Use when users need to understand organization/customer scope, role/capability basis, support access, or retained human authority.

Anatomy:
- selected organization/customer or account context
- role/capability summary
- support-access or elevated-access state when applicable
- safe context-switch or request-access actions

Rules:
- show user-safe denials without leaking hidden workstreams or cross-tenant facts
- backend authorization remains authoritative

### Trace/evidence block

Use inside decision, audit, queue, or detail surfaces where evidence and provenance matter.

Anatomy:
- evidence summary
- policy/prompt/model/tool/data references where relevant and authorized, summarized in user language by default
- trace ids or citation links only when the current role needs them; raw ids stay subordinate to readable labels
- copy/open trace affordance

Rules:
- redact sensitive facts according to AuthContext
- never expose backend secrets, prompt internals, raw provider/model data, hidden tenant data, or internal-only implementation metadata

### Named theme selector

Use in My Account settings when named-theme preference is in scope.

Anatomy:
- label and select control listing user-facing theme names
- helper text explaining immediate preview
- Save/Confirm button for governed persistence
- save failure recovery state

Rules:
- use stable theme ids internally
- do not present `system`, `light`, or `dark` as the primary preference model
- preview changes color tokens only; it does not prove persistence succeeded

### Empty/error/forbidden state card

Use when a surface lacks data, fails to load, or denies an action.

Anatomy:
- specific state title
- user-safe explanation
- preserved-work note where relevant
- retry/recover/request-access action when allowed
- trace/correlation id only where useful for the current role; ordinary users should get a readable support/reference label instead

Rules:
- avoid generic `No data`, `Error occurred`, or `Forbidden` copy without recovery guidance
- keep hidden workstream existence and privileged facts private
