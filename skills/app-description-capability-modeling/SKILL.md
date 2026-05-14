---
name: app-description-capability-modeling
description: Update the authoritative capability layer of the app description by defining business capabilities, scope boundaries, actors, outcomes, and links to behavior, tests, security, and observability artifacts.
---

# App Description Capability Modeling

Use this skill when the harness needs to define or revise the **capability layer** of the app description.

This skill maintains `10-capabilities/` as the inventory of what the app is for, what user-visible outcomes it supports, and what is in or out of scope.
It does not generate code.

## Goal

Create or update capability-oriented app-description artifacts that:
- define the app's business or user-visible capabilities clearly
- separate in-scope from out-of-scope outcomes
- identify primary actors or callers
- preserve AI-first operating semantics when a capability delegates work, shapes human authority, or requires outcome accountability
- make capability boundaries stable enough for downstream operating-model, behavior, tests, security, and observability work
- link each capability to the artifacts that realize and verify it

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../../docs/ai-first-saas-application-architecture.md`
- `../../docs/app-description-skills-plan-backlog.md`
- `../app-descriptions/SKILL.md`
- `../app-description-input-normalization/SKILL.md`
- `../app-description-bootstrap/SKILL.md`
- `../app-description-behavior-specification/SKILL.md`

Prefer these example references when present:
- `../../docs/examples/purchase-request-app-description/app-description/10-capabilities/capabilities-index.md`
- `../../docs/examples/purchase-request-app-description/app-description/10-capabilities/01-submit-and-approve-purchase-requests.md`
- `../../docs/examples/purchase-request-app-description/app-description/70-traceability/capability-to-behavior-map.md`

## Use this skill when

The task sounds like:
- "what capabilities does this app need?"
- "add a new business capability"
- "split this into capability areas"
- "what is in scope vs out of scope?"
- "model the capability layer before behavior details"

Use it for:
- new capability definition
- scope boundary clarification
- actor and outcome identification
- delegated-work, supervision, decision, governance, trace, or outcome-loop capability boundaries
- capability splitting or consolidation
- maintaining the `10-capabilities/` layer and its links

## Core operating rule

Capabilities describe **what the app must enable**, not how it is implemented.

A capability should be:
- business-meaningful
- stable enough to survive internal implementation changes
- narrow enough to link clearly to behavior and verification
- explicit about what it excludes

## What this skill must capture

For each capability, identify and describe as applicable:
- capability name
- business goal
- primary actors or caller classes
- delegated operational work, if any
- retained human authority, approvals, exceptions, or supervision needs, if any
- policy, permission, evidence, trace, learning, or outcome-accountability needs, if any
- in-scope outcomes
- out-of-scope outcomes
- major constraints or assumptions
- linked operating-model artifacts under the required `15-operating-model/` for generated AI-first SaaS semantics
- linked behavior artifacts
- linked test artifacts
- linked auth/security artifacts
- linked observability artifacts

## Standard capability output shape

Use this response shape when updating or summarizing capability work:

```md
# Capability Modeling Update

## Requested change
- ...

## Capability definition
- name:
- business goal:
- actors:

## AI-first operating semantics
- delegated work:
- retained human authority:
- governance / policy boundary:
- decision, exception, or supervision needs:
- trace / learning / outcome needs:

## In-scope outcomes
- ...

## Out-of-scope outcomes
- ...

## Major assumptions or constraints
- ...

## Linked layers
- operating model:
- behavior:
- tests:
- auth/security:
- observability:
```

## Capability modeling rules

### 1. Model capability before implementation structure
Use business outcomes and user-visible purpose, not classes, services, or endpoints, as the primary definition.

### 2. Keep scope boundaries explicit
Every important capability should make clear what it does not include.
This reduces later ambiguity and uncontrolled scope creep.

### 3. Keep capabilities stable but bounded
Do not create one giant capability for the whole app.
Do not create tiny pseudo-capabilities that are really just implementation details.

### 4. Link forward deliberately
A capability is incomplete if it cannot be linked to behavior and verification artifacts.

### 5. Separate capability change from behavior detail
A new capability may require later behavior work, but capability modeling should first establish the business boundary and intended outcomes.

### 6. Preserve AI-first semantics before component or CRUD framing
When the capability involves delegated operational work, autonomous or semi-autonomous judgment, human approval, policy controls, exceptions, auditability, or outcome accountability, define those as capability semantics.
Do not flatten them into CRUD records, dashboards, or a chatbot feature.
Link the capability to `15-operating-model/` artifacts for goals, agent authority, policies, decisions, traces, and outcomes as applicable.

### 7. Keep human governance visible
If automation can affect consequential state, money, commitments, permissions, customer communication, compliance, or operational outcomes, record what humans delegate and what humans retain.
If the boundary is unknown, ask or flag it before downstream behavior or generation work.

### 8. Preserve user language where useful
When the user names a business concept clearly, preserve that concept in the capability name or description rather than replacing it with framework language.

## Handoff rules

Route onward as needed:
- to `ai-first-saas-object-model`, `ai-first-saas-agent-team-design`, `ai-first-saas-policy-governance`, `ai-first-saas-decision-cards`, `ai-first-saas-audit-trace`, or `ai-first-saas-outcomes-metrics` when the capability needs focused operating-model semantics
- to `app-description-behavior-specification` when the capability needs concrete flows, rules, states, or invariants
- to `app-description-test-specification` when acceptance, evaluation, or scope-verification scenarios need to be defined
- to `app-description-auth-security` when the capability introduces differentiated actors, ownership, protected actions, or enforceable agent/human permissions
- to `app-description-observability` when the capability introduces auditable, measurable, traceable, or diagnostically important flows
- to `app-description-change-impact` when a capability change likely alters existing linked layers or realization scope

## Clarification policy

Ask only the smallest questions needed to avoid creating the wrong capability boundary.

Examples:
- "Is this a new top-level capability, or a refinement of an existing one?"
- "What outcome is the user trying to achieve through this capability?"
- "What should explicitly remain out of scope for this capability right now?"
- "Does this capability apply to all users, or only a specific actor type?"
- "What work is delegated to the system or agents, and what authority remains with a human?"
- "Which decisions require approval, exception handling, evidence, or audit trace?"

## Anti-patterns

Avoid:
- naming capabilities after technical components
- collapsing multiple unrelated outcomes into one vague capability
- omitting out-of-scope boundaries
- treating implementation tasks as capabilities
- treating delegated operational work as generic CRUD plus a chatbot
- hiding approval, policy, exception, or audit needs inside later implementation
- leaving capability files unlinked to downstream behavior and tests

## Final review checklist

Before finishing, verify:
- the capability is named clearly
- business goal and actors are explicit
- in-scope and out-of-scope outcomes are explicit
- major assumptions are recorded when relevant
- delegated work and retained human governance are explicit for generated AI-first SaaS semantics
- linked downstream layers are called out
- the result strengthens `10-capabilities/` rather than bypassing it

## Response style

When answering:
- summarize the capability change first
- state the capability boundary clearly
- list in-scope and out-of-scope outcomes explicitly
- avoid implementation terminology unless needed for clarification
- call out the next linked description layers to update
- mention operating-model links before behavior/test/security/observability links for generated AI-first SaaS semantics
