# Internal App Description Architecture

## Purpose

This document defines the default internal artifact architecture for a **description-first application system**.

Its purpose is to give the harness and app-description skills a concrete target structure to maintain.

The structure is optimized for harness/model use, not for direct human authorship.
Humans interact through prompt/response.
The harness maintains these internal artifacts.

## Core rule

The application description is the source of truth.

Everything in this architecture exists to maintain that truth in a layered, interconnected, regenerable form.
Generated code, tests, and runnable assets are downstream projections.

For AI-first SaaS applications, the description must preserve the secure SaaS foundation and agent workstream model before implementation planning: Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, `/api/me`, backend authorization, audit, tenant isolation, durable goals, delegated work, retained human authority, role-authorized functional agents, governed internal agents, workstreams, structured surfaces, capability contracts, policies, approval gates, decisions, exceptions, traces, and outcomes.

The primary generated-app model is:

```text
functional agents and internal agents
→ workstreams and structured surfaces
→ governed backend capabilities
→ horizontal Akka implementation maps
```

Do not reduce agentic product intent to CRUD objects, page trees, screen hierarchies, or chatbot panels, and do not let generation invent missing security semantics.

## Default root

Use a dedicated root such as:

```text
app-description/
```

The exact root name may vary by project, but the harness should prefer one stable root rather than scattering description artifacts arbitrarily.

Reference example:
- `docs/examples/purchase-request-app-description/app-description/`

## Architecture goals

The internal artifact system should be:
- layered
- explicit
- cross-linked
- small enough for focused updates
- stable under repeated harness maintenance
- suitable for localized regeneration
- suitable for readiness assessment
- suitable for prompt/response review summaries

## Default directory layout

Prefer this baseline structure:

```text
app-description/
  00-system/
    app-manifest.md
    readiness-status.md
    generation-policy.md

  10-capabilities/
    capabilities-index.md
    01-secure-tenant-user-foundation.md
    02-<capability>.md

  12-workstreams/          # required for generated full-stack AI-first SaaS apps
    functional-agents.md
    internal-agents.md
    workstreams-and-retention.md
    surfaces-index.md
    surface-contracts/
      01-<surface>.md

  15-operating-model/      # required for generated AI-first SaaS apps
    goals-and-objectives.md
    agent-roles-and-authority.md
    governed-runtime-agents.md
    policies-and-approval-gates.md
    decisions-exceptions-and-evidence.md
    audit-trace-and-outcomes.md

  20-behavior/
    behavior-index.md
    state-models/
      01-<stateful-area>.md
    flows/
      01-<flow>.md
    rules/
      01-<rule-family>.md

  30-tests/
    test-index.md
    acceptance/
      01-<capability>-acceptance.md
    regression/
      01-<regression-area>.md
    negative/
      01-<negative-area>.md
    operational/
      01-<operational-verification>.md

  40-auth-security/
    secure-saas-foundation.md
    identity-and-trust.md
    authorization-rules.md
    data-protection.md
    boundary-and-surface-rules.md

  50-observability/
    logs-and-audit.md
    metrics.md
    traces-and-correlation.md
    health-and-alerts.md

  55-ui/                  # required for generated full-stack AI-first SaaS apps
    ui-index.md
    workstream-shell.md
    functional-agent-rail.md
    workstream-panel-and-composer.md
    structured-surface-rendering.md
    personas-and-journeys.md
    prompt-and-skill-governance.md
    skill-manifests-and-tool-permissions.md
    edit-agent-proposals-and-traces.md
    routes-and-deep-links.md
    frontend-api-contracts.md
    states-and-realtime.md
    accessibility-and-responsive.md
    style-guide.md

  60-generation/
    realization-scope.md
    regeneration-map.md
    horizontal-implementation-map.md
    output-surfaces.md

  70-traceability/
    functional-agent-to-capability-map.md
    surface-to-capability-map.md
    capability-to-behavior-map.md
    capability-to-horizontal-map.md
    operating-model-to-behavior-map.md  # when 15-operating-model exists
    behavior-to-tests-map.md
    change-impact-map.md

  80-review/
    latest-change-summary.md
    latest-readiness-summary.md
```

This is the default starting architecture, not a rigid final law.
The harness may refine names or split files further as the app grows, but should preserve the same layer responsibilities.

## Layer responsibilities

## `00-system/`
System-level control artifacts.

### `app-manifest.md`
Defines the high-level identity and scope of the described app.
Should include:
- app name or working identity
- Java base package for generated code; ask the initial package question when absent and use `ai.first` only when accepted/deferred
- current status or maturity
- top-level goals
- non-goals
- major architectural assumptions
- primary generation targets

### `readiness-status.md`
Stores the current readiness posture.
Should include:
- current state: `not-ready`, `ready-with-assumptions`, or `ready`
- decisive reasons
- blocking gaps
- accepted assumptions
- last readiness update basis

### `generation-policy.md`
Defines realization policy.
Should include:
- when generation is allowed
- selected Java base package and the rule that `com.example` reference examples are not a generation default
- default full vs localized regeneration preference
- acceptable assumption policy
- required validation after generation

## `10-capabilities/`
Business capability inventory and governed backend capability contracts.

For generated SaaS apps, the first capability must be `01-secure-tenant-user-foundation.md`, covering Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, Invitation, AuthContext, `/api/me`, backend authorization, AdminAuditEvent, support-access, billing boundary, and tenant/customer isolation before app-specific capabilities.

This layer answers:
- what business or user-visible capabilities exist?
- what is in scope?
- what is explicitly out of scope?
- who may invoke each operation or query?
- which authority, scope, side effects, audit, approval, and exposure rules apply before implementation choices are made?

`capabilities-index.md` should list stable capability ids, class (`read/evidence`, `command`, `proposal`, `approval`, `workflow`, `policy/governance`, `trace/audit`, `scheduled`, or `reactive`), primary actors/callers, protected scope, and selected exposure surfaces.

Each capability file should be narrowly focused and should include or link to:
- purpose and in-scope/out-of-scope outcomes;
- actors/callers, AuthContext, tenant/customer scope, roles, permissions, and named capability grants;
- input and output schemas, validation, safe denial/error shape, redaction rules, idempotency key, and correlation id expectations;
- data access boundaries, side effects, policy/approval/escalation rules, and autonomy level;
- audit/work-trace event requirements, retention/redaction expectations, and outcome/evidence links;
- selected exposure surfaces such as UI action, HTTP/gRPC API, agent tool, MCP, workflow step, view/query, timer, consumer, or internal-only method;
- linked operating-model, behavior, tests, auth/security, observability, UI, and traceability artifacts.

Do not treat endpoints, agent tools, workflows, or entities as the capability inventory root. They are downstream realization or exposure choices for these capability contracts.

## `12-workstreams/`
Agent workstream vertical model.

Include this layer for every generated full-stack AI-first SaaS app. It captures the user-facing application structure before browser routes or Akka components are selected.

This layer answers:
- which role-authorized functional agents appear in the authenticated shell?
- which internal agents support workflows, tools, timers, consumers, evaluation, summarization, extraction, routing, or governance without becoming left-rail work areas?
- what durable workstreams exist for each functional agent, and what retention, replay, trace, and follow-up semantics apply?
- which structured surfaces are rendered in workstreams, and which agents may reuse them?
- which surface actions map to governed backend capabilities?
- which denial, approval, escalation, empty, loading, stale, error, and trace-link states are visible?

Default files:
- `functional-agents.md` for user-facing context-area agents, role/capability authorization, tenant/customer scope, default briefing/dashboard surfaces, prompt intent where applicable, available surfaces, callable capabilities, approval/escalation behavior, trace obligations, and tests
- `internal-agents.md` for non-navigation agents with governed definitions, prompt/skill references, tool boundaries, service or AuthContext authority basis, model policy, traces, and tests
- `workstreams-and-retention.md` for timeline semantics, persistence, replay, summarization, correlation ids, attachments, follow-up handling, and retention/redaction rules
- `surfaces-index.md` for stable surface ids, surface type/version, owning or reusable functional agents, payload schema location, allowed actions, linked capabilities, and rendering tests
- `surface-contracts/` for individual structured surface contracts such as dashboards, forms, tables, charts, detail cards, decision/approval/exception cards, diffs, audit timelines, workflow status cards, evidence bundles, prompt/skill version cards, and outcome panels

A page, route, or screen may deep-link to a surface, but it must not replace the functional-agent/workstream/surface model as the primary description root.

## `15-operating-model/`
AI-first operating model and agentic substrate.

Include this layer for every generated AI-first SaaS app. The pack is opinionated toward full-stack AI-first SaaS, so the baseline secure foundation already includes delegated admin assistance, human supervision, policy controls, auditability, and outcome accountability. Omit this layer only for explicitly non-SaaS reference material or repository-maintenance-only work.

This layer answers:
- what durable goals, objectives, success criteria, constraints, and outcome links exist?
- what work is delegated to agents or agent teams, and what authority remains with humans?
- what policies, clauses, guardrails, permissions, thresholds, and approval gates control behavior?
- what recommendations, decisions, exceptions, evidence, risk, confidence, impact, and alternatives must be captured?
- what work traces, decision traces, policy invocations, tool/data-access events, feedback, learning, replay, simulations, and outcome metrics are required?

Default files:
- `goals-and-objectives.md` for durable human objectives, success criteria, constraints, and definitions of done
- `agent-roles-and-authority.md` for agent/team responsibilities, tools, data access, autonomous decisions, escalation rules, and non-responsibilities
- `governed-runtime-agents.md` for `AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, `ToolPermissionBoundary`, deterministic prompt assembly, authorized `readSkill(skillId)`, behavior editing agent proposals, `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace`
- `policies-and-approval-gates.md` for policies, clauses, guardrails, thresholds, approval rules, and governed policy-change semantics
- `decisions-exceptions-and-evidence.md` for decision cards, exception handling, recommendation evidence, risk/confidence/impact, and alternatives
- `audit-trace-and-outcomes.md` for trace requirements, feedback loops, replay/simulation needs, outcome metrics, and links between decisions and results

These files describe operating semantics; Akka component choices remain downstream implementation decisions.

## `20-behavior/`
Behavioral semantics.

This layer answers:
- what must happen?
- what must never happen?
- what state exists?
- what transitions are valid or invalid?
- what no-op or idempotent behavior is required?
- when agentic work starts, pauses, escalates, retries, completes, learns, or requires human review?

Subareas:
- `state-models/` for durable state concepts and lifecycle semantics
- `flows/` for multi-step or temporal behavior
- `rules/` for invariant-heavy or policy-like behavior groupings

## `30-tests/`
Authoritative verification layer.

This layer is part of the app description, not a downstream afterthought.
For generated SaaS apps it must include baseline secure foundation tests for tenant isolation, forbidden access, disabled users, role/scope denial, `/api/me`, audit, support-access, billing boundary, idempotency, and frontend secret boundaries.
It answers:
- what must be provable?
- what acceptance cases define success?
- what regressions must remain fixed?
- what negative or idempotency cases constrain behavior?
- what operational verification is required?

Subareas:
- `acceptance/`
- `regression/`
- `negative/`
- `operational/`

## `40-auth-security/`
Auth/security semantics.

This layer answers:
- how the mandatory secure SaaS foundation is enforced: Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, Invitation, AuthContext, `/api/me`, backend authorization, AdminAuditEvent, support-access, and subscription/billing boundary
- how identity is established
- what authorization rules exist
- what trust boundaries exist
- what access is forbidden
- what data is sensitive
- what masking, redaction, or restricted visibility applies

## `50-observability/`
Operational evidence requirements.

This layer answers:
- what must be logged
- what must be audited, including identity, Membership/role, support-access, billing-boundary, policy, approval, data-access, and consequential AI/tool activity
- what metrics matter
- what trace continuity is required
- what health signals exist
- what should alert operators
- what evidence is needed to diagnose failures

## `55-ui/`
Authoritative frontend and browser interaction layer.

This layer is required for generated full-stack AI-first SaaS apps. It may be omitted only for explicitly non-SaaS reference material or repository-maintenance-only work. Do not treat the browser UI as optional polish or defer it behind backend-only generation.

This layer answers:
- how the agent workstream shell is rendered: functional-agent rail, main workstream panel, persistent composer, context/authority indicators, trace links, and denial/recovery states
- how structured surfaces are rendered and updated in the stream
- which routes or deep links address the shell or individual surfaces as implementation details
- what forms, commands, and frontend validations exist inside surfaces
- what browser API contracts are needed for workstreams, surface payloads, surface actions, and realtime updates
- what loading, empty, error, forbidden, submitting, success, stale, reconnect, and realtime states exist
- what accessibility and responsive behavior is required
- which visual style guide is selected, including light/dark policy, CSS tokens, layout density, component styling, and brand adaptations for generated HTML/CSS/TypeScript

## `60-generation/`
Realization policy and output mapping.

This layer answers:
- what outputs may be generated from the description
- what output surfaces exist
- how regeneration locality is tracked
- what parts of the realized app are affected by which description layers

### `realization-scope.md`
Defines the current intended realization boundary.
Examples:
- code only
- code + tests
- code + tests + run configuration
- code + tests + deploy assets

### `regeneration-map.md`
Maps description areas to generated output areas for localized regeneration decisions.

### `horizontal-implementation-map.md`
Maps governed capabilities to candidate horizontal implementation substrates such as Akka entities, workflows, views, consumers, timed actions, agents, endpoints, MCP, web UI code, auth/security, audit, and tests. This map is derived from capability semantics and is not the primary app model.

### `output-surfaces.md`
Names the output families that can be realized from the description.

## `70-traceability/`
Relationship mapping.

This layer exists to support change impact analysis and localized regeneration.
It should answer:
- which functional agents use which capabilities, surfaces, workstreams, and UI shell regions?
- which structured surfaces expose which actions and therefore which capabilities?
- which capabilities depend on which behavior artifacts?
- which capabilities map to which horizontal Akka implementation candidates?
- which AI-first goals, agents, policies, decisions, traces, and outcomes depend on which capabilities or behavior artifacts?
- which behavior artifacts require which tests?
- which security and observability rules attach to which capabilities, operating-model concerns, workstreams, surfaces, or flows?
- what outputs are likely affected by a given description change?

## `80-review/`
Derived review summaries.

This layer is optional and non-authoritative.
It can be maintained when the harness benefits from persisting its latest summaries, but the truth still lives in the earlier layers.

Use it for:
- latest change summary
- latest readiness summary
- optional human-review snapshots

Do not treat this layer as the source of app meaning.

## Skill-to-layer ownership

Default ownership should be:

- `app-description-bootstrap`
  - creates the initial `00-system/`, secure foundation `10-capabilities/`, foundation `12-workstreams/`, foundation `15-operating-model/`, foundation `20-behavior/`, foundation `30-tests/`, `40-auth-security/`, `50-observability/`, and required `55-ui/` seed artifacts for generated full-stack AI-first SaaS apps
  - establishes the first stable app-description root

- `app-description-input-normalization`
  - produces a structured non-authoritative change envelope for downstream skills
  - may be persisted only as a temporary working artifact when useful

- `app-description-intake-router`
  - identifies candidate impacted layers
  - does not usually own long-lived authoritative artifacts

- `app-description-capability-modeling`
  - primarily owns `10-capabilities/`
  - maintains capability boundaries and links to downstream layers
  - links capabilities to `12-workstreams/` when functional agents, surface actions, agent tools, workstream actions, or internal-agent calls expose them
  - links AI-first capabilities to `15-operating-model/` when they depend on delegated work, goals, policies, decisions, or outcomes

- `app-description-behavior-specification`
  - primarily owns `20-behavior/`
  - may update `15-operating-model/` when behavior changes alter agent authority, approval gates, exception handling, or learning/outcome loops

- `app-description-test-specification`
  - primarily owns `30-tests/`

- `app-description-change-impact`
  - primarily owns `70-traceability/change-impact-map.md`
  - may recommend updates across all authoritative layers and `60-generation/regeneration-map.md`

- `app-description-auth-security`
  - primarily owns `40-auth-security/`
  - links identity, authorization, tenant isolation, data protection, and permission enforcement to AI-first authority boundaries in `15-operating-model/` when present

- `app-description-observability`
  - primarily owns `50-observability/`
  - links logs, audit events, traces, metrics, alerts, and diagnostics to AI-first work traces, decision traces, policy invocations, and outcome loops when present

- `app-description-ui`
  - primarily owns `55-ui/`
  - may update `12-workstreams/` when UI changes add or alter functional agents, internal-agent visibility, workstreams, structured surfaces, or surface actions
  - links shell regions, surface rendering, routes/deep links, interactions, frontend API contracts, accessibility, responsive behavior, and the selected `style-guide.md` back to capabilities, behavior, tests, security, and observability
  - prioritizes the agent workstream shell and structured surfaces over CRUD/page navigation by default for generated AI-first SaaS UI

- `app-description-readiness-assessment`
  - primarily owns `00-system/readiness-status.md`
  - may update `60-generation/realization-scope.md` references when readiness posture changes

- `app-generate-app`
  - reads all authoritative layers
  - may update `60-generation/` and derived generation status notes

- `app-description-change-summary`
  - derives from all impacted authoritative layers
  - may optionally update `80-review/latest-change-summary.md`

- `app-description-readiness-summary`
  - derives from readiness assessment outputs
  - may optionally update `80-review/latest-readiness-summary.md`

## Cross-layer invariants

The harness should maintain these invariants:

1. Every generated SaaS app must include the secure tenant/user foundation capability, agent workstream model, AI-first operating model, behavior, auth/security, observability, web UI, and test artifacts before app-specific generation.
2. Every generated full-stack AI-first SaaS app must model authenticated consequential work as role-authorized functional agents, governed internal agents where needed, durable workstreams, and structured surfaces rather than as a primary page/screen hierarchy.
3. Every surface action, agent tool, workflow step, timer, consumer reaction, API, MCP tool/resource, or internal call must map to a governed capability.
4. Every in-scope capability must record actors/callers, AuthContext/scope, input/output shape, side effects, idempotency, approval/policy, audit/trace, selected exposure surfaces, and tests at the level needed to avoid generation-time invention.
5. Every in-scope capability must link to at least one behavior artifact.
6. Every AI-first capability must link to operating-model artifacts that define goals, delegation, retained human authority, policies, decisions, traces, and outcomes as applicable.
7. Every important behavior change must link to one or more test artifacts.
8. Security-sensitive behavior must link to relevant auth/security artifacts.
9. Operationally important behavior must link to relevant observability artifacts.
10. Agentic authority, policy enforcement, approvals, exceptions, audit traces, and outcome metrics must not be invented only during generation.
11. Horizontal Akka components must be selected from capability semantics and recorded as downstream implementation maps, not used as the app-description root.
12. Readiness must be based on the actual state of workstream model, operating model, capability contracts, behavior, tests, security, observability, mandatory secure foundation, and in-scope UI layers.
13. Generation policy must never override description correctness.
14. Review summaries must be derivable from authoritative layers.

## File sizing rules

Prefer files that are:
- small enough for one focused harness update
- large enough to preserve a coherent semantic topic
- stable under repeated revisions

Good file boundaries:
- one capability
- one functional agent, internal-agent family, workstream, or structured surface contract
- one operating-model concern such as goals, agent authority, policies, decisions, traces, or outcomes
- one flow
- one stateful area
- one rule family
- one security concern area
- one observability concern area
- one verification family

Avoid files that collapse the entire app into one giant narrative.

## Naming rules

Prefer:
- stable numeric prefixes when ordering matters
- explicit topic names
- one topic per file where possible
- directory names that reveal the layer role immediately

Examples:
- `10-capabilities/02-order-submission.md`
- `15-operating-model/agent-roles-and-authority.md`
- `20-behavior/flows/03-approval-escalation.md`
- `30-tests/regression/02-duplicate-submission.md`
- `40-auth-security/authorization-rules.md`

## Artifact update rules

When a change request arrives, the harness should:
1. identify impacted capabilities
2. update `12-workstreams/` when functional agents, internal agents, workstreams, surfaces, or surface actions are affected
3. update AI-first operating-model semantics when delegated work, agents, policies, decisions, traces, or outcomes are affected
4. update behavior semantics
5. update linked test semantics
6. update linked auth/security semantics if needed
7. update linked observability semantics if needed
8. update linked UI semantics, including `55-ui/style-guide.md`, for generated full-stack AI-first SaaS apps
9. update traceability links, including functional-agent/surface/capability/horizontal maps
10. reassess readiness
11. generate outputs only if requested or accepted

## What is authoritative vs derived

### Authoritative
These layers define the app:
- `10-capabilities/`
- `12-workstreams/` for generated full-stack AI-first SaaS apps
- `15-operating-model/` for generated AI-first SaaS apps
- `20-behavior/`
- `30-tests/`
- `40-auth-security/`
- `50-observability/`
- `55-ui/` for generated full-stack AI-first SaaS apps
- relevant control state in `00-system/`

### Derived
These layers are useful but not authoritative:
- `60-generation/` generation status notes
- `70-traceability/` relationship maps
- `80-review/` review summaries

Derived artifacts must be reproducible from authoritative artifacts plus harness analysis.

## Minimum viable internal architecture

For a very small project, the harness may start with only:

```text
app-description/
  00-system/
    app-manifest.md
    readiness-status.md
  10-capabilities/
    capabilities-index.md
    01-secure-tenant-user-foundation.md
  12-workstreams/          # required for generated full-stack AI-first SaaS apps
    functional-agents.md
    surfaces-index.md
  15-operating-model/      # required for generated AI-first SaaS apps
    goals-and-objectives.md
  20-behavior/
    behavior-index.md
    flows/01-secure-foundation-access-flow.md
  30-tests/
    test-index.md
    regression/01-tenant-isolation-and-idempotency.md
    negative/01-security-denial-baseline.md
  40-auth-security/
    secure-saas-foundation.md
    identity-and-trust.md
    authorization-rules.md
  50-observability/
    logs-and-audit.md
  55-ui/
    ui-index.md
    workstream-shell.md
    functional-agent-rail.md
    structured-surface-rendering.md
    prompt-and-skill-governance.md
    style-guide.md
```

Then expand into the full structure as complexity grows. For generated AI-first SaaS, even a very small project is full-stack and must include a browser UI description.

## Operating rule

If there is tension between:
- keeping the internal description minimal but explicit, and
- pushing meaning back down into generated code,

prefer maintaining the internal description.