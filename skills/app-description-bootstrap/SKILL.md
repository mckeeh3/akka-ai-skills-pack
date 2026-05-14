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
- establishes authoritative layers for capabilities, AI-first operating model, behavior, tests, auth/security, observability, and required web UI for generated full-stack AI-first SaaS apps
- records an initial readiness posture
- defines a generation policy
- creates enough cross-linking that later changes can stay localized and traceable

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/ai-first-saas-application-architecture.md`
- `../core-saas-foundation/SKILL.md` for the mandatory secure SaaS foundation every new app-description must seed
- `../../docs/app-description-skills-plan-backlog.md`
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
  15-operating-model/      # required for generated AI-first SaaS apps
    goals-and-objectives.md
    agent-roles-and-authority.md
    policies-and-approval-gates.md
    decisions-exceptions-and-evidence.md
    audit-trace-and-outcomes.md
  20-behavior/
    behavior-index.md
    state-models/
      01-tenant-user-access-model.md
    flows/
      01-secure-foundation-access-flow.md
      02-<primary-flow>.md
    rules/
      01-tenant-authz-rules.md
  30-tests/
    test-index.md
    acceptance/
      01-secure-foundation-acceptance.md
      02-<primary-capability>-acceptance.md
    regression/
      01-tenant-isolation-and-idempotency.md
    negative/
      01-security-denial-baseline.md
  40-auth-security/
    secure-saas-foundation.md
    identity-and-trust.md
    authorization-rules.md
    data-protection.md
    boundary-and-surface-rules.md
  50-observability/
    logs-and-audit.md
    security-and-admin-audit-events.md
  55-ui/                  # required for generated full-stack AI-first SaaS apps
    ui-index.md
    secure-shell-and-context-selection.md
    admin-and-audit-surfaces.md
    screens-and-navigation.md
    style-guide.md        # create when style is supplied; otherwise record unselected or queue a style question
```

Add deeper files only when the user's input already justifies them. For generated AI-first SaaS apps, the browser UI is mandatory: use `app-description-ui` to maintain the `55-ui` layer and `../../docs/web-ui-style-guide.md` for `style-guide.md` structure. If style is not supplied, do not choose one silently; record the style as `unselected` and add or recommend a pending UI style-selection question before web UI generation.

## What this skill must derive from input

From the initial user input, derive as applicable:
- app identity or working name
- top-level goal
- whether the app has AI-first/delegated operating-model semantics
- delegated work versus retained human authority when applicable
- first in-scope capability set, starting with the secure SaaS foundation capability
- likely primary behavior flow, starting with sign-in, `/api/me`, context selection, Account/Profile/Settings maintenance, administration, invitations, support-access, audit viewing, and tenant/customer-scoped access
- first acceptance scenarios, including secure foundation acceptance plus tenant-isolation, forbidden-access, disabled-user, role/scope-denial, `/api/me`, audit, support-access, billing-boundary, and frontend secret-boundary baseline tests
- initial auth/security expectations based on `core-saas-foundation`, including explicit default-deny authorization for every route, agent tool, data access, workflow action, view query, stream, and generated UI action
- initial observability expectations for identity, Membership/role, support-access, admin, audit, policy, data-access, and consequential AI/tool events
- initial policy, approval, exception, audit, trace, and outcome expectations for the AI-first SaaS operating model
- initial frontend/UI expectations for the mandatory browser app, including sign-in state, context selection, `/api/me`, account/profile/settings, tenant/customer admin, Membership/role administration, invitation, support-access, and audit surfaces
- selected web UI style guide/theme when supplied, or an explicit `unselected` style state when not supplied
- initial non-goals
- an initial readiness posture
- a conservative generation policy

Keep all uncertain details explicit as assumptions rather than pretending they are settled.

## Bootstrap workflow

### 1. Establish app identity
Create `00-system/app-manifest.md` with:
- app id or working name
- current status
- top-level goals
- non-goals
- primary generation targets
- major assumptions known so far

### 2. Establish readiness baseline
Create `00-system/readiness-status.md` with an initial state.
For most fresh bootstraps, prefer:
- `not-ready`
- or `ready-with-assumptions` only if the input is already unusually complete

### 3. Establish generation policy
Create `00-system/generation-policy.md` with a conservative policy that preserves description primacy.

### 4. Create the first capability layer
Create a `10-capabilities/` index and a mandatory `01-secure-tenant-user-foundation.md` capability covering SaaS Owner, Tenant, Customer, Account, UserProfile, UserSettings, Membership, Role, Permission/Capability, Invitation, AuthContext, AdminAuditEvent, support-access, subscription/billing boundary, `/api/me`, backend authorization, tenant/customer-scoped commands and queries, and tenant-isolation tests.

Then add the clearest app-specific business capability as the next numbered file when the input supports it.

### 5. Create the AI-first operating-model layer
Create `15-operating-model/` for generated AI-first SaaS apps. The secure foundation itself includes delegated admin assistance, supervision, policy-governed decisions, auditability, and outcome accountability, so this layer is not optional for generated apps.

Seed only the files justified by the input, but prefer the standard operating-model files when the app is clearly agentic:
- `goals-and-objectives.md`
- `agent-roles-and-authority.md`
- `policies-and-approval-gates.md`
- `decisions-exceptions-and-evidence.md`
- `audit-trace-and-outcomes.md`

Capture durable goals, delegated work, retained human authority, agent/team boundaries, policy/approval semantics, decision evidence, trace requirements, and outcome loops as assumptions when not fully settled. Omit this layer only for explicitly non-SaaS reference material or repository-maintenance-only work.

### 6. Create the first behavior layer
Create a `20-behavior/` index plus secure foundation behavior artifacts before app-specific flows:
- `state-models/01-tenant-user-access-model.md` for Account, UserProfile, UserSettings, Tenant, Customer, Membership, Role, Permission/Capability, Invitation, AuthContext, support-access, and billing-boundary state semantics
- `flows/01-secure-foundation-access-flow.md` for sign-in, `/api/me`, context selection, profile/settings, invitation, admin, support-access, audit, and tenant/customer-scoped access
- `rules/01-tenant-authz-rules.md` for default-deny authorization, tenant/customer isolation, disabled-user behavior, role/scope checks, and forbidden access behavior

Then add one primary app-specific flow file when the input supports it. Add deeper app state-model or rules files only if the input clearly contains lifecycle or invariant semantics already.

### 7. Create the first test layer
Create a `30-tests/` index plus mandatory secure foundation test artifacts before app-specific tests:
- `acceptance/01-secure-foundation-acceptance.md` for sign-in seam, `/api/me`, context selection, Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, invitation, support-access, admin, audit, and billing-boundary behavior
- `regression/01-tenant-isolation-and-idempotency.md` for cross-tenant isolation, duplicate invite/acceptance, repeated role changes, repeated support-access revoke/expiry, and idempotent `/api/me` reads
- `negative/01-security-denial-baseline.md` for forbidden access, disabled user, role/scope denial, cross-customer denial, unauthorized stream/query/tool/action attempts, and frontend secret-boundary checks

Then add app-specific acceptance files. Capture only the strongest initial app-specific expectations plus obvious negative or regression expectations if the input already supports them.

### 8. Create initial production-readiness layers
Create:
- `40-auth-security/secure-saas-foundation.md`
- `40-auth-security/identity-and-trust.md`
- `40-auth-security/authorization-rules.md`
- `40-auth-security/data-protection.md`
- `40-auth-security/boundary-and-surface-rules.md`
- `50-observability/logs-and-audit.md`
- `50-observability/security-and-admin-audit-events.md`

These must seed the mandatory secure SaaS foundation from `core-saas-foundation`; provider-specific unknowns may remain explicit open questions, but authorization, tenancy, audit, and tenant isolation are not optional. The bootstrap must state that no route, agent tool, data access, workflow action, view query, stream, or generated UI action is public or authorized by default except deliberately public static assets.

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
