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
- defines a generation policy that labels scope as `core app baseline`, `full core`, `Module 1-only / not full core`, or another explicit narrower scope
- for core app/basic/minimum/chatbot-like generated SaaS requests, represents the five-core-workstream core app domain — My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy — with `markdown_response`, bootstrap auth/security, durable workstream logs, trace substrate, backend capability boundaries, and explicit follow-up gaps to full core
- creates enough cross-linking that later changes can stay localized and traceable

## Required reading

Read these first if present:
- target project path: AGENTS.md
- `../README.md`
- `../docs/description-first-application-doctrine.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/requirements-to-workstream-development-process.md` for the canonical input → workstreams → attention → dashboards → surfaces/actions → capabilities/APIs → Akka substrate → agent/autonomous task → notifications/projections/traces process
- `../docs/minimum-ai-first-saas-app.md` for minimum/core app/basic/chatbot-like generated SaaS scope: five-core-workstream core app domain with `markdown_response`, not a single-workstream or generic chatbot slice
- `../core-saas-foundation/SKILL.md` for the mandatory secure SaaS foundation every new app-description must establish
- `../docs/internal-app-description-architecture.md`
- `../docs/app-description-maintenance-flow.md`
- `../app-descriptions/SKILL.md`
- `../ai-first-saas/SKILL.md` when the initial app idea includes delegated work, agents, decisions, governance, supervision, audit, or outcomes
- the target project path: app-description/README.md plus `../docs/core-ai-first-saas-foundation.md` for the preferred core app-description shape

Prefer these example references for generated SaaS foundation bootstraps:
- target project path: app-description/00-system/app-manifest.md
- target project path: app-description/10-capabilities/01-secure-tenant-user-foundation.md

Use current target-project app-description files and core app templates for cross-linking mechanics; do not depend on removed historical domain examples.

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

Use `../docs/app-description-skill-output-contracts.md` for the shared bootstrap contract. Create only the smallest truthful `app-description/**` tree for the declared scope, normally covering system metadata/readiness/generation policy, capabilities, workstreams/surfaces, operating model, behavior, tests, auth/security, observability, UI, realization, and traceability.

For generated SaaS, start from target-project `app-description/**` when present or `../templates/ai-first-saas-core-app/app-description/**` for initial structure. Record explicit deferrals instead of inventing full-core details.

## Scope gates

Apply the concise rules in `../docs/app-description-skill-output-contracts.md` plus the focused skill's goal. Preserve mandatory secure SaaS foundation, generated-SaaS runtime completion, tenant/customer scoping, backend authorization, governed agent/tool boundaries, traces, and tests when those concerns are in scope. Ask only blocking questions; otherwise record assumptions and hand off to the next focused skill.

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
- inventing provider-specific security details from thin input while still failing to establish the mandatory secure SaaS foundation
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
- list the created layer roots and initial artifacts
- call out the initial readiness state
- recommend the next focused description skills
