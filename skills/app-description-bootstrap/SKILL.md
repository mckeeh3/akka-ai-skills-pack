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
- establishes authoritative layers for capabilities, behavior, tests, auth/security, observability, and UI when a frontend is in scope
- records an initial readiness posture
- defines a generation policy
- creates enough cross-linking that later changes can stay localized and traceable

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/app-description-skills-plan-backlog.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../app-descriptions/SKILL.md`
- `../../docs/examples/purchase-request-app-description/README.md`

Prefer these example references:
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
    01-<primary-capability>.md
  20-behavior/
    behavior-index.md
    flows/
      01-<primary-flow>.md
  30-tests/
    test-index.md
    acceptance/
      01-<primary-capability>-acceptance.md
  40-auth-security/
    identity-and-trust.md
  50-observability/
    logs-and-audit.md
  55-ui/                  # only when a browser frontend is in scope
    ui-index.md
    screens-and-navigation.md
```

Add deeper files only when the user's input already justifies them. When the app has a meaningful browser UI, use `app-description-ui` to maintain the `55-ui` layer.

## What this skill must derive from input

From the initial user input, derive as applicable:
- app identity or working name
- top-level goal
- first in-scope capability set
- likely primary behavior flow
- first acceptance scenarios
- initial auth/security expectations
- initial observability expectations
- initial frontend/UI expectations when a browser app is in scope
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
Create a `10-capabilities/` index and at least one capability file representing the clearest business capability currently known.

### 5. Create the first behavior layer
Create a `20-behavior/` index and at least one primary flow file.
Add state-model or rules files only if the input clearly contains lifecycle or invariant semantics already.

### 6. Create the first test layer
Create a `30-tests/` index and at least one acceptance file.
Capture only the strongest initial acceptance expectations plus obvious negative or regression expectations if the input already supports them.

### 7. Create initial production-readiness layers
Create:
- `40-auth-security/identity-and-trust.md`
- `50-observability/logs-and-audit.md`

These may begin with baseline expectations and explicit open questions rather than complete policy.

### 8. Create initial cross-links
Cross-link the first capability, behavior, and test artifacts so later maintenance and change-impact work have a stable base.

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
- 20-behavior:
- 30-tests:
- 40-auth-security:
- 50-observability:

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
- biased toward one primary capability and one primary flow first
- expandable without restructuring everything immediately

Do not create a large multi-capability tree from weak input unless the user already supplied a strong PRD or equivalent requirements artifact.

## Handoff rules

After bootstrap, route onward as needed:
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
- inventing a fully detailed security or observability model from thin input
- creating dozens of files from a vague one-paragraph idea
- marking a fresh bootstrap `ready` without substantial supporting detail
- skipping the readiness-status or generation-policy files
- leaving the first capability, behavior, and test artifacts unlinked

## Final review checklist

Before finishing, verify:
- one stable app-description root is used
- the minimum authoritative layers exist
- the initial capability, behavior, and test artifacts are cross-linked
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
