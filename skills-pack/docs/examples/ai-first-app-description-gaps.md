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

### Historical domain-specific app-description example

Status: **removed**.

The old low-agentic approval-workflow app-description example has been removed so current guidance points at the core app-description baseline, starter templates, and focused Akka component examples. Do not reintroduce it as a generic mechanics reference.

## Remaining gaps

Remaining gaps are executable reference implementation and test gaps, not app-description baseline gaps. The target project `app-description/` tree is the planning/reference description for closing runnable core-app gaps. Track broader executable gaps in `../ai-first-examples-and-tests-gap-list.md`.

## Non-goals for this note

- Do not reintroduce historical domain-specific app-description examples as generic generated-SaaS guidance without explicit product intent.
- Do not create new domain-specific vertical examples unless they add reusable skills-pack value that the core app path cannot provide.
