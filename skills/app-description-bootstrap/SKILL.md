---
name: app-description-bootstrap
description: Bootstrap a new internal app-description tree for a new app or early app idea by creating the minimum authoritative layers, cross-links, readiness baseline, and generation policy from flexible user input.
---

# App Description Bootstrap

Use this skill when the user is starting a new app, a new app-description tree does not yet exist, or the existing internal description system is too incomplete to support normal description-maintenance flow.

This skill creates the **initial internal app-description artifact tree** that later description skills will maintain.
It is the description-first equivalent of project scaffolding, but for the authoritative app definition rather than for code.

## Goal

Create a minimum viable internal app-description tree that:
- gives the harness a stable root to maintain
- establishes the mandatory secure SaaS foundation for Account/Profile/Settings/Membership/Tenant/Customer/admin/audit before app-specific features
- establishes authoritative layers for role-authorized functional-agent workstreams, workstream boundary/count decisions, per-workstream attention breakdowns, role-specific dashboard contracts, human surface graphs, governed surface actions, capability-contained governed-tools, AI-first operating model, behavior, tests, auth/security, observability, and required web UI for generated full-stack AI-first SaaS apps
- records internal workstream agent graph candidates, internal worker delegations, autonomous task candidates, notification/projection implications, and trace expectations when durable internal/background agent work may be needed
- records an initial readiness posture
- defines a generation policy that labels scope as `minimum starter / not full core`, `full core`, `Module 1-only / not full core`, or another explicit narrower scope
- for starter/basic/minimum/chatbot-like generated SaaS requests, represents the five core workstream v0 set — My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy — with `markdown_response`, bootstrap auth/security, durable workstream logs, trace substrate, backend capability boundaries, and explicit follow-up gaps to full core
- creates enough cross-linking that later changes can stay localized and traceable

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/ai-first-saas-application-architecture.md`
- `../../docs/requirements-to-workstream-development-process.md` for the canonical input → workstreams → attention → dashboards → surfaces/actions → capabilities/APIs → Akka substrate → agent/autonomous task → notifications/projections/traces process
- `../../docs/minimum-ai-first-saas-app.md` for minimum/starter/basic/chatbot-like generated SaaS scope: five core workstream v0 with `markdown_response`, not a single-workstream or generic chatbot slice
- `../core-saas-foundation/SKILL.md` for the mandatory secure SaaS foundation every new app-description must seed
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../app-descriptions/SKILL.md`
- `../ai-first-saas/SKILL.md` when the initial app idea includes delegated work, agents, decisions, governance, supervision, audit, or outcomes
- `../../docs/examples/ai-first-saas-seed-app-description/README.md` for the preferred secure AI-first SaaS seed shape
- `../../docs/examples/purchase-request-app-description/README.md` for description-layer mechanics only, not target architecture doctrine

Prefer these example references for generated SaaS foundation bootstraps:
- `../../docs/examples/ai-first-saas-seed-app-description/app-description/00-system/app-manifest.md`
- `../../docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/01-secure-tenant-user-foundation.md`

Use purchase-request mechanics references only when the task is specifically about cross-linked description mechanics:
- `../../docs/examples/purchase-request-app-description/app-description/00-system/app-manifest.md`
- `../../docs/examples/purchase-request-app-description/app-description/10-capabilities/01-submit-and-approve-purchase-requests.md`
- `../../docs/examples/purchase-request-app-description/app-description/20-behavior/flows/01-submission-and-approval-flow.md`
- `../../docs/examples/purchase-request-app-description/app-description/30-tests/acceptance/01-purchase-request-acceptance.md`
- `../../docs/examples/purchase-request-app-description/app-description/40-auth-security/identity-and-authorization.md`
- `../../docs/examples/purchase-request-app-description/app-description/50-observability/logs-metrics-traces-and-alerts.md`

## Use this skill when

The task sounds like:
- "start a new app description for this app idea"
- "bootstrap the internal app-description tree"
- "create the initial description artifacts from this PRD"
- "set up the description-first app structure"
- "initialize app-description/ for this project"

Use it before normal description-maintenance work when:
- no `app-description/` root exists yet
- the current description structure is too sparse or inconsistent to maintain safely
- a new app concept needs its first internal description baseline

## Core operating rule

Bootstrap the **minimum authoritative structure**, not a giant speculative encyclopedia.

The result should be small but real:
- enough to maintain
- enough to assess readiness later
- enough to support cross-layer refinement
- not so large that it invents detailed behavior the user did not specify

## Default output root

Prefer a stable root such as:

```text
app-description/
```

If the repository already has a different stable internal root for app descriptions, preserve that instead of introducing a second structure.

## Minimum required outputs

Bootstrap at least these artifacts:

```text
app-description/
  00-system/
    app-manifest.md
    readiness-status.md
    generation-policy.md
  10-capabilities/
    capabilities-index.md
    01-secure-tenant-user-foundation.md
    02-<primary-app-capability>.md
  12-workstreams/          # required for generated full-stack AI-first SaaS apps
    functional-agents.md
    attention-and-dashboards.md
    internal-agents.md      # create when internal/background agent work or autonomous task candidates are in scope
    surfaces-index.md
    surface-contracts/
      00-markdown-response.md      # required for each minimum-starter core workstream v0
      01-access-profile.md
      02-user-admin.md
      03-agent-admin.md
      04-audit-trace.md
      05-governance-policy.md
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
      01-tenant-user-access-model.md
      02-governed-agent-behavior-model.md
    flows/
      01-secure-foundation-access-flow.md
      02-governed-agent-behavior-maintenance-flow.md
      03-<primary-flow>.md
    rules/
      01-tenant-authz-rules.md
      02-agent-prompt-skill-tool-boundary-rules.md
  30-tests/
    test-index.md
    acceptance/
      01-secure-foundation-acceptance.md
      02-governed-agent-foundation-acceptance.md
      03-<primary-capability>-acceptance.md
    regression/
      01-tenant-isolation-and-idempotency.md
      02-agent-prompt-skill-manifest-trace-regression.md
    negative/
      01-security-denial-baseline.md
      02-agent-authority-and-skill-denial-baseline.md
  40-auth-security/
    secure-saas-foundation.md
    identity-and-trust.md
    authorization-rules.md
    data-protection.md
    boundary-and-surface-rules.md
    governed-agent-security.md
  50-observability/
    logs-and-audit.md
    security-and-admin-audit-events.md
    governed-agent-traces.md
  55-ui/                  # required for generated full-stack AI-first SaaS apps
    ui-index.md
    workstream-shell.md
    functional-agent-rail.md
    workstream-panel-and-composer.md
    structured-surface-rendering.md
    routes-and-deep-links.md
    personas-and-journeys.md
    ai-first-surfaces.md
    agent-catalog-and-detail.md
    prompt-and-skill-governance.md
    skill-manifests-and-tool-permissions.md
    edit-agent-proposals-and-traces.md
    interactions-and-forms.md
    frontend-api-contracts.md
    states-and-realtime.md
    accessibility-and-responsive.md
    style-guide.md        # create when style is supplied; otherwise record unselected or queue a style question
```

This `55-ui/` file set is the canonical generated SaaS bootstrap set and matches `docs/internal-app-description-architecture.md`, `app-description-ui`, and the current AI-first SaaS seed reference. Add deeper files only when the user's input already justifies them. For generated AI-first SaaS apps, the browser UI is mandatory: use `app-description-ui` to maintain the `55-ui` layer and `../../docs/web-ui-style-guide.md` for `style-guide.md` structure. If style is not supplied, do not choose one silently; record the style as `unselected` and add or recommend a pending UI style-selection question before web UI generation.

## Scope gates

At bootstrap time, classify the requested generation scope before writing readiness or generation policy:
- `minimum starter / not full core` means the app-description may be small but must describe the real five core workstream v0 set: bootstrap-authorized identity, selected `AuthContext`, role/capability-checked My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy functional agents, durable workstream request/response logs, `markdown_response` structured surface contracts for each core workstream, backend capability boundaries for actions/tools, audit/work trace substrate, bootstrap auth/security rules, and explicit follow-up gaps to full core. This is the canonical interpretation for “minimum app”, “starter app”, “basic app”, or initial chatbot-like generated SaaS requests.
- `full core` means the app-description must include My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy functional agents; complete invitation onboarding; full user administration; governed runtime agent records (`AgentDefinition`, prompts, skills, reference documents, skill/reference manifests, tool boundaries, prompt/skill/reference/work traces, `readSkill`, and `readReferenceDoc`); workstream UI; managed-agent UI files (`agent-catalog-and-detail.md`, `prompt-and-skill-governance.md`, `skill-manifests-and-tool-permissions.md`, reference-governance surfaces, and `edit-agent-proposals-and-traces.md`); and full security/test coverage.
- `Module 1-only / not full core` means only minimal authentication, `/api/me`, selected context, My Account, and an authenticated shell are in scope; it must explicitly defer User Admin, Agent Admin, Audit/Trace, Governance/Policy, invitation lifecycle, governed prompts/skills/references/manifests/tool boundaries, managed-agent UI files, audit/work trace UI, and governance loops. Do not use this label for minimum starter requests that should be the five core workstream v0 set.
- any other narrower scope must be named in `00-system/app-manifest.md`, repeated in `00-system/readiness-status.md`, and enforced by `00-system/generation-policy.md`.

Do not allow a bootstrap to imply full-core readiness while omitting full User Admin, Agent Admin, Invitation onboarding, governed runtime agents, complete workstream UI, or required tests. Missing full-core elements are acceptable only when the generated app is explicitly labeled as `minimum starter / not full core`, Module 1-only, or another accepted narrower scope. Minimum starter readiness must not be reported as full-core readiness.

## What this skill must derive from input

From the initial user input, derive as applicable:
- app identity or working name
- Java base package for generated code; ask "What Java base package should I use for generated code? Press Enter to use `ai.first`." when absent, default to `ai.first` only if accepted/deferred, and never assume `com.example` from reference examples
- top-level goal
- whether the app has AI-first/delegated operating-model semantics
- delegated work versus retained human authority when applicable
- selected scope label: `minimum starter / not full core`, `full core`, `Module 1-only / not full core`, or another explicit narrower scope
- initial workstream inventory with count, boundaries, owner functional agents, tenant/customer/AuthContext scope, and whether each workstream is core-foundation or domain-specific
- per-workstream attention categories answering `what needs my attention?`, including severity/lifecycle, target audience, My Account aggregation, left-rail count behavior, and source/freshness expectations
- role-specific dashboard contract for each workstream: actor-specific summary cards, attention items, blocked/overdue/risky/failed/paused states, participant/task visibility, pending decisions, recent changes, and authorized next actions
- initial human surface graph for each workstream: dashboard trunk, surface nodes, surface actions/edges, system-message surfaces, action outcomes, and links to capability/governed-tool ids
- governed-tool inventory inside capability files and surface/action maps, including qualified browser-tool, agent-tool, and internal-tool exposure candidates rather than ambiguous bare tools
- first in-scope capability set, starting with the secure SaaS foundation capability and, for minimum starter scope, read/explain/deny capability boundaries for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy v0
- internal workstream agent graph candidates: virtual dashboard agent responsibilities, internal worker delegations, escalation points, result/proposal surfaces, and required expertise skill/reference updates
- autonomous task candidates for durable internal/background model-driven work, including start/result/progress/notification surfaces and why Akka `AutonomousAgent` may fit
- event/notification/projection implications for dashboard updates, My Account attention aggregation, left-rail attention summaries, audit/work traces, and stale/reconnect UI states
- likely primary behavior flow, starting with sign-in, `/api/me`, context selection, Account/Profile/Settings maintenance, administration, invitations, support-access, audit viewing, and tenant/customer-scoped access
- first acceptance scenarios, including secure foundation acceptance plus tenant-isolation, forbidden-access, disabled-user, role/scope-denial, `/api/me`, audit, support-access, billing-boundary, and frontend secret-boundary baseline tests
- initial auth/security expectations based on `core-saas-foundation`, including explicit default-deny authorization for every route, agent tool, data access, workflow action, view query, stream, and generated UI action
- initial governed runtime agent expectations: `AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `ReferenceDocument`/`ReferenceVersion`, `AgentSkillManifest`, `AgentReferenceManifest`, `ToolPermissionBoundary`, first-install/tenant-bootstrap loading of implementation-developed default behavior/reference seed documents, deterministic prompt assembly, authorized `readSkill(skillId)`, authorized `readReferenceDoc(referenceId)`, behavior-editing agent proposals, and denial of unauthorized authority expansion
- initial observability expectations for identity, Membership/role, support-access, admin, audit, policy, data-access, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, and consequential AI/tool events
- initial policy, approval, exception, audit, trace, and outcome expectations for the AI-first SaaS operating model
- initial frontend/UI expectations for the mandatory browser app, including sign-in state, context selection, `/api/me`, account/profile/settings, tenant/customer admin, Membership/role administration, invitation, support-access, audit, agent catalog, agent detail, prompt governance, skill governance, skill manifest, tool permission, editing agent proposal, and trace surfaces; for minimum starter scope, record the five core workstream shell, functional-agent rail (with My Account launched from the bottom user tile), composer, workstream logs, `markdown_response` rendering, trace links, and deferred richer surfaces
- selected web UI style guide when supplied, or an explicit `unselected` style state when not supplied
- initial non-goals
- an initial readiness posture
- a conservative generation policy

Keep all uncertain details explicit as assumptions rather than pretending they are settled.

## Bootstrap workflow

### 1. Establish app identity
Create `00-system/app-manifest.md` with:
- app id or working name
- Java base package for generated code, defaulting to `ai.first` only when the user accepts or defers the package question
- current status
- selected scope label and whether the current bootstrap is full core or explicitly not full core
- top-level goals
- non-goals
- primary generation targets
- major assumptions known so far

### 2. Establish readiness baseline
Create `00-system/readiness-status.md` with an initial state.
For most fresh bootstraps, prefer:
- `not-ready`
- or `ready-with-assumptions` only if the input is already unusually complete and remaining assumptions are non-runtime or explicitly outside the named scope

### 3. Establish generation policy
Create `00-system/generation-policy.md` with a conservative policy that preserves description primacy, records the selected Java base package, labels minimum-starter/full-core/narrower generation scope, blocks unlabeled omissions of full User Admin, Agent Admin, Invitation onboarding, governed runtime agents, workstream UI, and security tests, forbids using `com.example` for generated application code unless explicitly requested, and states that generated runtime features are complete only after real local Akka/API/UI validation with no mock/fixture/simulated normal-runtime substitute. For `minimum starter / not full core`, require the five core workstream v0 set and follow-up tasks for richer My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy surfaces/capabilities, invitations/onboarding, governed behavior/reference documents, and security coverage.

### 4. Create the first capability layer
Create a `10-capabilities/` index and a mandatory `01-secure-tenant-user-foundation.md` capability covering SaaS Owner, Tenant, Customer, Account, UserProfile, UserSettings, Membership, Role, Permission/Capability, Invitation, AuthContext, AdminAuditEvent, support-access, subscription/billing boundary, `/api/me`, backend authorization, tenant/customer-scoped commands and queries, and tenant-isolation tests.

Then add the clearest app-specific business capability as the next numbered file when the input supports it. For generated full-stack SaaS, every user-facing capability must link back to a functional-agent workstream, attention/dashboard context, structured surface action, or explicitly state `internal-only`.

### 4a. Create the first workstream, attention, dashboard, and graph layer
Create `12-workstreams/functional-agents.md`, `12-workstreams/attention-and-dashboards.md`, and `12-workstreams/surfaces-index.md` before route/page/UI details. For each initial workstream, record boundary/count rationale, owner functional agent, authorized actors, role-specific dashboard contracts, attention categories, left-rail/My Account summary behavior, participant visibility, human surface graph nodes/actions, system-message surfaces, action-to-capability/governed-tool links, browser-tool/agent-tool/internal-tool exposure candidates, audit/work-trace links, and tests. If the input suggests durable background model-driven work, add or reserve `12-workstreams/internal-agents.md` entries for internal workstream agent graph candidates, worker delegations, expertise needs, lifecycle/result/notification surface expectations, and route later implementation toward Akka `AutonomousAgent` when its typed task semantics fit.

### 5. Create the AI-first operating-model layer
Create `15-operating-model/` for generated AI-first SaaS apps. The secure foundation itself includes delegated admin assistance, supervision, policy-governed decisions, auditability, and outcome accountability, so this layer is not optional for generated apps.

Seed only the files justified by the input, but prefer the standard operating-model files when the app is clearly agentic:
- `goals-and-objectives.md`
- `agent-roles-and-authority.md`
- `governed-runtime-agents.md` for `AgentDefinition`, governed prompt, skill, and reference documents, default behavior/reference seed import, `AgentSkillManifest`, `AgentReferenceManifest`, `ToolPermissionBoundary`, behavior-editing agent responsibilities, `readSkill(skillId)`, `readReferenceDoc(referenceId)`, and prompt/skill/reference load tracing
- `policies-and-approval-gates.md`
- `decisions-exceptions-and-evidence.md`
- `audit-trace-and-outcomes.md`

Capture durable goals, delegated work, retained human authority, agent/team boundaries, policy/approval semantics, decision evidence, trace requirements, and outcome loops as assumptions when not fully settled. Omit this layer only for explicitly non-SaaS reference material or repository-maintenance-only work.

### 6. Create the first behavior layer
Create a `20-behavior/` index plus secure foundation behavior artifacts before app-specific flows:
- `state-models/01-tenant-user-access-model.md` for Account, UserProfile, UserSettings, Tenant, Customer, Membership, Role, Permission/Capability, Invitation, AuthContext, support-access, and billing-boundary state semantics
- `state-models/02-governed-agent-behavior-model.md` for `AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `ReferenceDocument`/`ReferenceVersion`, `AgentSkillManifest`, `AgentReferenceManifest`, `ToolPermissionBoundary`, seed import/provenance, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, and `AgentWorkTrace` lifecycle semantics
- `flows/01-secure-foundation-access-flow.md` for sign-in, `/api/me`, context selection, profile/settings, invitation, admin, support-access, audit, and tenant/customer-scoped access
- `flows/02-governed-agent-behavior-maintenance-flow.md` for editing agent change requests, proposed diffs, draft versions, review/approval, activation, rollback, authorized `readSkill(skillId)`, authorized `readReferenceDoc(referenceId)`, and trace creation
- `rules/01-tenant-authz-rules.md` for default-deny authorization, tenant/customer isolation, disabled-user behavior, role/scope checks, and forbidden access behavior
- `rules/02-agent-prompt-skill-tool-boundary-rules.md` for prompt/skill/reference guidance limits, skill/reference manifest assignment, tool permission boundaries, skill/reference-load authorization, disabled-agent denial, and authority expansion denial

Then add one primary app-specific flow file when the input supports it. Add deeper app state-model or rules files only if the input clearly contains lifecycle or invariant semantics already.

### 7. Create the first test layer
Create a `30-tests/` index plus mandatory secure foundation test artifacts before app-specific tests:
- `acceptance/01-secure-foundation-acceptance.md` for sign-in seam, `/api/me`, context selection, Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, invitation, support-access, admin, audit, and billing-boundary behavior
- `acceptance/02-governed-agent-foundation-acceptance.md` for agent catalog/detail, prompt governance, skill/reference governance, manifest and tool permission management, editing agent proposals, prompt assembly, authorized `readSkill(skillId)`, authorized `readReferenceDoc(referenceId)`, and trace search behavior
- `regression/01-tenant-isolation-and-idempotency.md` for cross-tenant isolation, duplicate invite/acceptance, repeated role changes, repeated support-access revoke/expiry, and idempotent `/api/me` reads
- `regression/02-agent-prompt-skill-manifest-trace-regression.md` for immutable active versions, deterministic prompt assembly, unassigned skill/reference denial, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, and `AgentWorkTrace` creation
- `negative/01-security-denial-baseline.md` for forbidden access, disabled user, role/scope denial, cross-customer denial, unauthorized stream/query/tool/action attempts, and frontend secret-boundary checks
- `negative/02-agent-authority-and-skill-denial-baseline.md` for disabled-agent denial, unauthorized prompt/skill/reference/tool-boundary changes, unauthorized `readSkill(skillId)`, unauthorized `readReferenceDoc(referenceId)`, and approval-required authority expansion

Then add app-specific acceptance files. Capture only the strongest initial app-specific expectations plus obvious negative or regression expectations if the input already supports them.

### 8. Create initial production-readiness layers
Create:
- `40-auth-security/secure-saas-foundation.md`
- `40-auth-security/identity-and-trust.md`
- `40-auth-security/authorization-rules.md`
- `40-auth-security/data-protection.md`
- `40-auth-security/boundary-and-surface-rules.md`
- `40-auth-security/governed-agent-security.md` for `AgentDefinition`, prompt/skill/reference/manifest/tool-boundary authorization, disabled-agent denial, authorized `readSkill(skillId)`, authorized `readReferenceDoc(referenceId)`, and approval-required authority expansion
- `50-observability/logs-and-audit.md`
- `50-observability/security-and-admin-audit-events.md`
- `50-observability/governed-agent-traces.md` for `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, editing agent proposal traces, prompt/skill/reference activation audit, and denied skill/reference/tool attempts

These must seed the mandatory secure SaaS foundation from `core-saas-foundation`; WorkOS/AuthKit is the supported browser authentication provider and Resend (resend.com) is the supported production email service for invitation/account emails and future app email features. WorkOS/Resend setup values may remain explicit open questions, but auth or email provider selection should not. Authorization, tenancy, audit, and tenant isolation are not optional. The bootstrap must state that no route, agent tool, data access, workflow action, view query, stream, or generated UI action is public or authorized by default except deliberately public static assets.

### 9. Create initial cross-links
Cross-link the first capability, operating-model artifacts when present, behavior, and test artifacts so later maintenance and change-impact work have a stable base.

## Standard bootstrap output shape

Use this response shape when summarizing bootstrap work:

```md
# App Description Bootstrap Summary

## Input basis
- ...

## Bootstrapped root
- app-description/

## Created authoritative layers
- 00-system:
- 10-capabilities:
- 15-operating-model: present | omitted because not in scope
- 20-behavior:
- 30-tests:
- 40-auth-security:
- 50-observability:
- secure SaaS foundation artifacts:
  - capability:
  - behavior:
  - tests:
  - auth/security:
  - observability:
  - UI:

## Initial readiness state
- not-ready | ready-with-assumptions | ready

## Major assumptions
- ...

## Recommended next skill or skill sequence
1. ...
2. ...
```

## Sizing rules

Bootstrap should be:
- as small as possible while still structurally useful
- explicit about uncertainty
- biased toward one primary capability, one primary operating-model thread when AI-first is in scope, and one primary flow first
- expandable without restructuring everything immediately

Do not create a large multi-capability tree from weak input unless the user already supplied a strong PRD or equivalent requirements artifact.

## Handoff rules

After bootstrap, route onward as needed:
- to AI-first companion skills for durable object model, agent-team, policy/governance, decisions, audit, UI surfaces, or outcomes for generated AI-first SaaS
- to `app-description-behavior-specification` for deeper behavioral refinement
- to `app-description-test-specification` for richer acceptance, regression, or negative coverage
- to `app-description-auth-security` for tighter access and data-protection definition
- to `app-description-observability` for richer operational visibility requirements
- to `app-description-readiness-assessment` when the user asks whether the bootstrapped description is sufficient to realize

## Clarification policy

Ask only the smallest questions needed to avoid bootstrapping the wrong app identity or wrong primary capability.

Examples:
- "What Java base package should I use for generated code? Press Enter to use `ai.first`."
- "What short working name should I use for the app-description root manifest?"
- "What is the single most important capability to capture first?"
- "Do you want this bootstrap to stay minimal, or should I expand it from the full PRD now?"

## Anti-patterns

Avoid:
- bootstrapping code instead of the app description
- inventing provider-specific security details from thin input while still failing to seed the mandatory secure SaaS foundation
- creating dozens of files from a vague one-paragraph idea
- reducing delegated operational work to CRUD screens or a chatbot without durable goals, authority, policies, decisions, traces, and outcomes
- marking a fresh bootstrap `ready` without substantial supporting detail
- skipping the readiness-status or generation-policy files
- leaving the first capability, operating-model artifacts when present, behavior, and test artifacts unlinked

## Final review checklist

Before finishing, verify:
- one stable app-description root is used
- the minimum authoritative layers exist
- the mandatory secure SaaS foundation capability, AI-first operating model, behavior, auth/security, observability, web UI, and test artifacts exist or the task is explicitly non-SaaS reference material
- the initial capability, operating-model artifacts when present, behavior, and test artifacts are cross-linked
- readiness state is explicit
- generation policy is explicit
- major assumptions are recorded
- the next recommended description-maintenance skill is clear

## Response style

When answering:
- summarize the app idea briefly
- state that the app-description tree was bootstrapped
- list the created layer roots and seed artifacts
- call out the initial readiness state
- recommend the next focused description skills
