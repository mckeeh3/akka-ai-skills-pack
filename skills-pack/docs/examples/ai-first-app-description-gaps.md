# AI-First App-Description Example Coverage

## Purpose

This note records current app-description example coverage and distinguishes canonical generated-SaaS references from mechanics-only examples.

It prevents existing examples from being silently reinterpreted as AI-first when their intent is simpler.

## Current examples

### target project `app-description/`

Status: **preferred current generated-SaaS app-description reference**.

This tree defines the core app-description baseline maintained in the target/core app workspace. It is the canonical app-description starting point for secure multi-tenant SaaS foundations, functional/context-area agents, `12-workstreams/` application model, split `55-ui/` browser realization, frontend/backend integration patterns, and coherent Akka component coverage.

Use it when the task is about:
- bootstrapping a generated AI-first SaaS app;
- defining shared human-user access, tenant, membership, role, and permission foundations;
- mapping a reusable AI-first operating model to implementation phases;
- planning the runnable starter core app that future skills and examples can reference.

### `purchase-request-app-description/`

Status: **low-agentic / conventional approval-workflow reference**.

Keep this example focused on the existing purchase request workflow unless a later task intentionally evolves it. It demonstrates the baseline app-description layer structure for capabilities, behavior, tests, auth/security, observability, generation, traceability, and review.

Do **not** force-fit this example into a full AI-first SaaS operating model. It lacks durable delegated goals, bounded agent/team execution, governed policy learning, decision-card evidence, work traces, and outcome loops by design.

## Removed examples

The former DCA app-description vertical was removed because it no longer provided unique reusable skills-pack value after the starter core app-description, core app baseline, workstream UI reference, and focused Akka implementation examples matured.

## Remaining gaps

Remaining gaps are executable reference implementation and test gaps, not app-description baseline gaps. The target project `app-description/` tree is the planning/reference description for closing runnable core-app gaps. Track broader executable gaps in `../ai-first-examples-and-tests-gap-list.md`.

## Non-goals for this note

- Do not retrofit the purchase-request example into a complete AI-first example without explicit product intent.
- Do not create new domain-specific vertical examples unless they add reusable skills-pack value that the core app path cannot provide.
