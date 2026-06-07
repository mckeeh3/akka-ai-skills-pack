# AI-first Workstream Enterprise UI reference mockups

These static mockups are source-reference assets for generated AI-first SaaS UI style. They are **not** runtime completion evidence and must not be copied wholesale into a target app.

Use them to understand the expected visual craft for the canonical `ai-first-workstream-enterprise` style:

- distinctive dark command-desk aesthetic without generic dashboard chrome;
- role-authorized functional-agent rail;
- context/authority bars that make authorization, support access, and traces visible;
- AI command strips that launch durable work rather than hiding outcomes in chat;
- decision, audit, governance, and admin surfaces with strong hierarchy;
- tokenized controls and named-theme CSS variables;
- named color themes; there is no dark/light/system mode, only named themes whose tone may be light or dark for contrast testing;
- responsive layouts that preserve the primary supervision/review task.

## Files

- `tokens.css` — canonical example token bundles and component style primitives for the static references.
- `mission-control.html` — dark-toned briefing/dashboard surface with command strip, KPI band, agent activity, decisions, trust controls, and autonomous action queues.
- `mission-control-light.html` — light-toned variant with the same anatomy and design language; only named-theme color tokens change.
- `attention-dashboard-flow.html` — canonical “what needs my attention?” flow: dashboard count cards → sorted list/table → item detail/action surface.
- `decision-queue-detail.html` — decision queue and detail review with risk, evidence, policy trigger, confidence, trace links, and approve/reject/counter/defer/escalate actions.
- `governance-policy-center.html` — policy list, proposed diff, simulation evidence, approval commit, rollback, and trace context.
- `audit-trace-explorer.html` — search/filter plus chronological trace details for actor, tool, data access, policy invocation, and redaction.
- `my-account-settings.html` — profile/context/settings surface with named theme picker, immediate preview semantics, and governed save.
- `agent-admin-catalog-detail.html` — AgentDefinition catalog/detail with prompt/skill/reference manifests, model config, tool boundary, safe test console, and traces.
- `goal-workbench.html` — goal/objective workbench with success criteria, constraints, plan, agent assignment, approval gates, and simulation result.
- `autonomous-agent-task-progress.html` — durable AutonomousAgent task status, dependency, evidence, waiting-for-human, retry/cancel/escalate, and result controls.
- `system-message-states.html` — denial, validation, approval-required, stale/reconnect, no-op, and provider/runtime-blocked system-message surfaces.
- `user-admin-list.html` — user administration list/search surface with access health, audit, AI admin assistance, and capability/auth context.
- `user-admin-edit.html` — detail/edit surface showing designed form controls, approval-required actions, audit timeline, and summary/result panels.

## Canonical attention flow

Most generated workstreams should start from a role-specific dashboard that answers **“what needs my attention?”**. The canonical flow is:

```text
dashboard attention count cards
→ list/search/table surface sorted by urgency, risk, freshness, due time, or business priority
→ item detail/action surface where the human reviews evidence and decides what to do
→ result/system-message/progress surface with trace links
```

Attention cards should be simple, clickable, rectangular count shapes that show the “what” and the count. They are navigation/action affordances into governed surfaces; they are not authorization controls. The list view should make selection efficient. The detail surface is where consequential actions, approvals, denials, validation, traces, and outcomes become explicit.

## Use safely

When generating app UI:

1. Reuse the **anatomy, token roles, hierarchy, and state treatment**.
2. Replace demo names, metrics, users, and logos with target-domain content.
3. Preserve the target app's surface contracts, capability ids, authorization, audit/work-trace behavior, DTOs, routes, and tests.
4. Do not claim these static pages prove the target app works. Real completion requires the intended local Akka/API/UI runtime path.

## Theme doctrine

Theme selection is central to the style. Users choose named themes such as Aurora Light, Obsidian Dark, or Dark Night. The app must not expose a dark/light/system mode selector as the primary preference. A theme may be light-toned or dark-toned for contrast testing, but switching themes changes only color tokens. It must not change layout, component anatomy, density, typography scale, routes, surface contracts, capability mapping, authorization, audit behavior, or the overall AI-first workstream design language.

## Design direction distilled from research references

The research images favor a dark operational command desk: high-density but readable panels, navy/charcoal depth, fine borders, bright status accents, durable decision cards, timeline anchors, and controls that feel designed rather than browser-native. This reference package converts that direction into reusable source assets for the skills pack.

The external frontend-design guidance has been folded into the pack as constrained visual craft doctrine: commit to a clear aesthetic point of view, make one or two details memorable, use typography/color/space/depth/motion intentionally, and avoid generic AI/SaaS cliches. The constraint is important: generated SaaS apps still use the same AI-first workstream anatomy and named-theme token model unless a user supplies a full custom design system.
