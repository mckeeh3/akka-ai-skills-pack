---
name: app-description-behavior-specification
description: Update authoritative workstream behavior nodes in the app-description current-intent graph from flexible user input by capturing capabilities, rules, invariants, state transitions, edge cases, and forbidden behavior without generating code.
---

# App Description Behavior Specification

Use this skill when the user is defining or revising what the application should do.

This skill maintains `domains/<domain>/workstreams/<workstream>/behavior.md` and related domain/workstream behavior nodes in the current-intent graph.
It does not generate code.
It turns input into authoritative behavioral meaning that downstream generation can later realize.

## Lifecycle classification

- Phase: interview.
- Kind: focused current-intent capture/editor.
- Family: app-description.
- Living-graph contract: behavior nodes are part of the app-description current-intent graph and must preserve the chain from responsible workers through execution harnesses, actor adapters, governed tools, capabilities, traces, tests, and realization links when behavior changes imply them.
- Build/compile handoff: when behavior intent becomes implementation, planning, code, tests, or validation work, hand off through `../docs/app-description-to-code-compile-contract.md` instead of selecting components or endpoints here.

## Goal

Create or update behavior-oriented app-description artifacts that:
- capture capabilities and scope clearly
- express user-visible and system-visible behavior
- preserve AI-first operating semantics when behavior involves delegated work, agent judgment, human governance, or outcome loops
- capture behavior changes to role-specific dashboard attention, human surface graph edges, internal workstream agent graph delegation, and governed-tools instead of hiding them as UI or implementation details
- define state changes, transitions, and invariants
- make forbidden behavior explicit
- separate confirmed facts from assumptions
- identify the linked operating-model, test, security, and observability implications

## Required reading

Read these first if present:
- target project path: AGENTS.md
- `../README.md`
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/app-description-skill-output-contracts.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/capability-first-backend-architecture.md` for preserving capability contracts when behavior changes alter authority, side effects, idempotency, approval, audit, or exposure
- `../app-description-intake-router/SKILL.md`

## Use this skill when

The input sounds like:
- "the app should now support..."
- "change the workflow so that..."
- "fix the behavior where..."
- "after approval, the system must..."
- "users must not be able to..."
- "when X happens, Y should happen"
- "this should be a no-op if..."

Use this skill for:
- new capabilities
- behavior revisions
- bug-fix semantics
- workflow and process rules
- goal, plan, agent-task, approval, exception, decision, or learning-loop semantics
- invariants
- edge-case behavior
- no-op and idempotency semantics
- forbidden states and forbidden transitions

Do **not** use this skill as the primary handler when the user is only asking to generate the app.

## Core operating rule

Prefer **behavioral precision** over implementation speculation.

The job of this skill is to define:
- what must happen
- what may happen
- what must never happen
- what remains unknown

It should avoid prematurely locking in code structure unless the behavior itself requires a structural distinction.

Behavior specs do not own capability boundaries. If a behavior change adds or changes actors/callers, AuthContext, inputs/outputs, side effects, idempotency, approval, audit, exposure surfaces, source functional agents, or source surface actions, update or route to `app-description-capability-modeling` and the affected workstream bindings instead of burying those contract changes only in behavior prose.

## What this skill must capture

For each requested change, identify and describe as applicable:
- linked capability id/class or business purpose
- actors or callers
- delegated work and retained human authority
- triggers, commands, requests, agent actions, approvals, exceptions, scheduled checks, surface actions, browser-tool invocations, agent-tool invocations, or internal-tool calls
- dashboard attention changes, surface graph node/edge changes, and internal workstream agent graph delegation/result/escalation behavior
- stateful concepts and lifecycle states, including goals, plans, tasks, decisions, policy proposals, traces, or outcomes for generated AI-first SaaS
- valid transitions
- invalid transitions
- invariants
- success outcomes
- failure outcomes
- no-op or idempotent behavior
- ordering or timing assumptions
- evidence, risk, confidence, impact, and alternative requirements for decisions when applicable
- learning, feedback, replay, simulation, or outcome-loop behavior when applicable
- whether the behavior changes the capability or governed-tool contract: actors/callers, AuthContext, inputs/outputs, data access, side effects, idempotency, policy/approval, audit/trace, selected exposure surfaces, or browser-tool/agent-tool/internal-tool mappings
- dependencies on global definitions, domain capabilities/data-state, workstream access/surfaces/agents/tools/policies/traces/tests, readiness, or realization maps

## Output contract

The updated behavior specification should make it possible for a later harness step to answer:
- what changed in the app's meaning?
- what behaviors are newly required?
- what behaviors are newly forbidden?
- what tests must exist to make this unambiguous?
- which other app-description graph nodes are now affected?

## Standard behavioral output shape

Use the delta modeling contract in `../docs/app-description-skill-output-contracts.md`. For this behavior skill, report the requested change, affected graph nodes/file targets, in-scope and out-of-scope behavior, authority/scope, DTOs or payloads where relevant, side effects/idempotency/denials/traces/tests, linked graph nodes, assumptions, and next handoff. Avoid repeating the full app-description graph model.

## Behavior modeling rules

Apply the concise rules in `../docs/app-description-skill-output-contracts.md` plus the focused skill's goal. Preserve mandatory secure SaaS foundation, generated-SaaS runtime completion, tenant/customer scoping, backend authorization, governed agent/tool boundaries, traces, and tests when those concerns are in scope. Ask only blocking questions; otherwise record assumptions and hand off to the next focused skill.

## Clarification policy

Ask only the smallest questions needed to resolve material ambiguity in behavior.

Good questions include:
- "Should repeated requests be treated as idempotent success or as an error?"
- "After approval, is this field immutable or still editable?"
- "If the downstream action fails, should the request remain pending, fail permanently, or retry later?"
- "Is this rule user-visible behavior or only an internal operational expectation?"
- "May the agent take this action autonomously, or only recommend it for approval?"
- "What evidence or confidence threshold is required before the action can proceed?"

## Handoff to other skills

When the behavior update is established, route onward as needed:
- to AI-first companion skills when the behavior changes goals, agent authority, policies, decision cards, audit traces, or outcome loops
- to `app-description-capability-modeling` when the behavior implies a changed capability contract, new operation/query, caller, AuthContext, schema, side effect, idempotency rule, approval gate, audit obligation, or exposure surface
- to `app-description-test-specification` when the behavior needs concrete acceptance, regression, evaluation, or edge-case tests
- to `app-description-auth-security` when the change affects authentication, authorization, trust boundaries, sensitive-data rules, or enforceable agent/human permissions
- to `app-description-ui` when changed behavior appears in browser journeys, forms/actions, supervision, decisions, governance, digests, or audit surfaces
- to `app-description-observability` when the change affects logs, metrics, traces, audit events, work traces, decision traces, policy invocations, or diagnosability
- to `app-description-readiness-assessment` when the user asks whether the description is ready or when missing behavior/capability semantics would otherwise be guessed during generation

## Anti-patterns

Avoid:
- paraphrasing the user request without turning it into actual behavioral rules
- silently choosing implementation structure as if it were behavioral truth
- omitting forbidden behavior and failure behavior
- forgetting no-op or idempotency semantics for retries and repeated requests
- treating delegated work as ordinary CRUD plus generated text
- allowing agents to act without explicit authority, approval, exception, and trace semantics
- treating a bug fix as only a patch instead of a corrected behavioral rule
- mixing security or observability policy into behavior without identifying the cross-node impact

## Final review checklist

Before finishing, verify:
- the requested behavior change is restated accurately
- new or changed rules are explicit
- invariants and forbidden behavior are included when relevant
- no-op or idempotent behavior is included when relevant
- assumptions are separated from confirmed facts
- operational delegation and human governance are represented for generated AI-first SaaS
- linked operating-model, test, security, and observability impacts are called out
- no code-generation step was assumed

## Response style

When answering:
- summarize the behavior change first
- list rules in concise declarative form
- keep implementation speculation minimal
- call out open questions separately from confirmed behavior
- make the downstream test implications explicit
- state delegated work, retained authority, and approval/exception/trace impacts explicitly for generated AI-first SaaS
