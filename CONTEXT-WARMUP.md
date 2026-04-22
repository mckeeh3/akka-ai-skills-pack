# CONTEXT-WARMUP

Use this file as the **startup orientation and context warmup** for AI coding agents working in this repository.

This file is for **session bootstrap**, not for detailed coding rules.
For authoritative Akka implementation constraints, coding rules, and test expectations, read:
- `AGENTS.md`

Also read:
- `skills/README.md` for local routing across the skill library
- `docs/agent-coverage-matrix.md` when the task is agent-related or touches agent coverage/gaps

---

## Core repo principle

**Users should talk to the harness naturally.**
They should not need to know the repository's internal skill taxonomy, stage model, or file layout.

The repository's skills, docs, examples, and routing structure exist primarily to help the harness:
- interpret intent correctly
- choose the right Akka design path
- decompose work before coding
- load only the smallest relevant guidance
- generate consistent code and tests over time

Implications:
- skills are **internal routing assets**, not the user interface
- users may start with a PRD, feature request, change request, rough idea, or iterative follow-up prompt
- if the harness can already infer the right path from normal language, prefer improving internal routing over adding new discoverability-oriented front doors

---

## What this repository is

Treat this repository as a **requirements-first, intent-driven Akka pack for AI coding agents**.

Its job is to help an agent go from:
1. high-level input
2. to Akka solution decomposition
3. to focused skill selection
4. to component-by-component code and test generation

Important distinction:
- `akka-context/` contains official Akka reference material and semantic source-of-truth docs
- this repository repackages that knowledge into **agent-oriented patterns, routing, examples, and tests**

When there is a choice between:
- a human-friendly narrative structure, and
- an agent-friendly structure,

prefer the **agent-friendly structure**.

---

## Default working model

For most tasks, follow this flow:
1. read the user's input completely
2. if the solution shape is not yet clear, decompose first
3. resolve any focused architecture choice that remains open
4. load only the smallest relevant implementation skills
5. generate code and tests component by component
6. repeat that loop as the app evolves over time

A good decomposition result is not a stopping point.
It is an **implementation contract** for downstream coding.

---

## Visible 3-stage skill model

### Stage 1: Intent and architecture
Use when the task starts from requirements, a prompt, a PRD, a specification file, a feature request, or any other high-level input and the Akka solution shape is not yet known.

Start with:
- `akka-solution-decomposition`

### Stage 2: Structural decisions
Use when the broader shape is mostly known, but one architecture choice is still unresolved.

Primary example:
- `akka-entity-type-selection` for Event Sourced Entity vs Key Value Entity

### Stage 3: Focused component implementation
Use only after the architecture is settled enough to write code.

This includes focused implementation skill families for:
- entities
- workflows
- views
- consumers
- timed actions
- HTTP/gRPC/MCP endpoints
- web UI delivery
- agents

Rules of thumb:
- do **not** jump straight into a component family when the broader architecture is still unclear
- do **not** use Stage 2 when Stage 1 decomposition is still needed
- use Stage 3 only when the task is already concrete component work

---

## How to use `akka-context/`

Treat `akka-context/`, especially `akka-context/sdk/`, as the **semantic source of truth** for Akka.

Use it for:
- API confirmation
- component semantics
- edge-case behavior
- official terminology
- coverage gaps in local repo guidance

But do **not** copy its human-oriented style directly.
The goal in this repository is to turn official Akka knowledge into **small, explicit, reusable, low-token guidance for agents**.

In short:
- `akka-context/` explains Akka
- this repository packages Akka knowledge for agent use

---

## How to interpret the main repo areas

### `skills/`
This is the repository's **source-of-truth routing layer**.

A skill should help answer:
- what kind of task is this?
- which files should I read first?
- which pattern applies?
- what should I load next?

Design expectations for skills:
- one broad entry skill per area
- smaller focused companion skills
- explicit “when to use” guidance
- minimal overlap
- low reading cost

### `src/`
This is the **executable example layer**.

Default interpretation:
- `domain` = pure domain logic and immutable models
- `application` = Akka components and component-facing orchestration
- `api` = edge-facing endpoint code
- `test` = verification plus reusable execution patterns

Tests are part of the reference set and should demonstrate:
- success behavior
- validation behavior
- no-op/idempotent behavior
- intended calling patterns
- integration behavior where relevant

### `docs/`
Focused local reference material for recurring patterns, comparisons, and routing aids.

---

## Current strong local coverage

The repository already has strong local patterns across much of the Akka application stack, especially:
- Event Sourced Entities and Key Value Entities
- workflows, including compensation and pause/resume patterns
- views across multiple source types
- consumers and async integration patterns
- timed actions and timer-backed flows
- HTTP endpoints, including static content, SSE, WebSocket, JWT, and web UI delivery
- gRPC endpoints
- MCP endpoints
- agents, including tools, structured responses, memory, orchestration, guardrails, evaluation, and testing
- unit and integration testing patterns across component families

Do not force-fit new work into entity-centric structures when it belongs elsewhere.
If local coverage is missing, create a new focused skill/example set instead of stretching an unrelated family.

---

## Session-start checklist

At the start of a new session:
1. read `AGENTS.md`
2. read this file
3. read `skills/README.md`
4. if the task is agent-related, read `docs/agent-coverage-matrix.md`
5. classify the task before choosing files:
   - requirements decomposition / architecture selection
   - focused structural decision
   - component implementation
   - testing
   - docs/snippet generation
   - cross-cutting topic
   - repo structure / skill design
6. if the task starts from high-level input and the component set is not yet known, start with `akka-solution-decomposition`
7. if the task is narrowed to a stateful core but entity type is still open, use `akka-entity-type-selection`
8. otherwise, load the smallest relevant local skill set first
9. use `akka-context/sdk/...` when you need official semantics or a feature not yet well represented locally
10. prefer local agent-optimized patterns when creating new repo content

---

## Rules for creating new repository content

When adding new skills, docs, or examples, optimize in this order:
1. agent usefulness
2. token efficiency
3. correct Akka semantics
4. consistency with repo conventions
5. human readability

Preferred qualities:
- focused
- reusable
- low-ambiguity
- low-token-cost
- easy to route to by file name alone
- easy for a future agent to extend safely

Preferred shape:
- one topic per file when possible
- exact references instead of broad prose where possible
- examples that isolate one pattern
- tests that clarify intended behavior
- docs that answer:
  - when should I use this?
  - what should I read next?
  - what is the canonical example?

If local coverage is missing:
- consult `akka-context/sdk/...`
- do not stretch unrelated skill families to absorb the task
- create or propose a new focused skill family
- create the smallest useful local example set

---

## Bottom line

The most important objective of this project is:

> turn Akka SDK knowledge into skills, examples, and guidance that are optimized for AI coding agents rather than for human readers

Use `AGENTS.md` for detailed constraints.
Use this file for startup orientation.
Use `skills/README.md` for routing.
Use `akka-context/` for official Akka semantics.
