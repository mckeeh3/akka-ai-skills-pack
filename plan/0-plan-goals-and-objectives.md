# Plan Goals and Objectives

## Purpose

This plan restructures the repository from a primarily **Akka-component-first** resource library into an **intent-driven flow** that supports:

1. starting from high-level product/design requirements, prompts, or specification files
2. decomposing those inputs into the right Akka architecture
3. routing to focused implementation skills
4. generating code and tests component by component

The target usage model is:

**PDR/spec/input -> solution decomposition -> architectural decisions -> focused skill loading -> code generation -> tests**

## Why this plan exists

The repository already has strong local coverage for:
- Event Sourced Entities
- Key Value Entities
- Workflows
- Views
- Consumers
- Timed Actions
- HTTP/gRPC/MCP endpoints
- Agents
- Web UI patterns
- testing

However, most of that coverage is still presented in a way that assumes the agent already knows which component family to use.

What is missing is a stronger top-level experience for:
- reading a requirements document
- deciding which Akka components are needed
- sequencing implementation work
- reducing the need for broad context loading

## High-level outcomes

When this plan is complete, a future AI coding agent should be able to:
- start from a high-level requirement or spec file
- identify capabilities, stateful cores, read models, workflows, timers, integrations, APIs, UI, and AI features
- select the minimal Akka component set
- route to the correct focused skills
- generate code with a smaller and more targeted context window

## Operating principles for every step

Each step in this plan should:
- keep files small and explicit
- improve routing clarity
- reduce ambiguity for future agents
- preserve Akka correctness
- avoid broad narrative prose when a concise routing structure is enough
- produce reusable outputs that can be referenced in later steps

## How to work this plan

Work the plan **one step at a time**.

For each session:
1. read this file first
2. read exactly one step file
3. implement only that step unless the step explicitly requires a small supporting change elsewhere
4. keep the session focused on the stated deliverables
5. avoid scope creep into later steps

## Session prompt pattern

Use prompts like:

- `read plan/0-plan-goals-and-objectives.md, then read and implement plan/1-Reframe-the-repo-s-top-level-story.md`
- `read plan/0-plan-goals-and-objectives.md, then read and implement plan/2-Introduce-a-visible-3-stage-skill-model.md`

## Repository-level design direction

The repository should increasingly communicate this hierarchy:

### Stage 1: Intent and architecture
Start from requirements, prompts, PDRs, or spec files.

### Stage 2: Structural decisions
Resolve key choices such as entity type, interfaces, orchestration style, read models, and delivery model.

### Stage 3: Component implementation
Load only the focused skills needed to generate concrete Akka code and tests.

## Expected deliverables across the full plan

The overall plan is expected to produce:
- stronger top-level messaging in `README.md`
- clearer startup guidance in `AGENT-README.md`
- a more explicit routing model in `skills/README.md`
- one or more intent-driven docs in `docs/`
- one or more canonical requirements-to-solution-plan examples
- reduced perception that the repository is primarily entity-centric
- a clearer bridge from decomposition to code generation

## Constraints

While implementing each step:
- preserve existing component-specific value
- do not remove focused skills that are already useful
- do not weaken Akka-specific implementation guidance
- avoid introducing vague architecture language without concrete routing or examples
- prefer edits that future agents can follow mechanically

## Definition of success

This plan is successful when a future coding agent can begin with a prompt like:
- "read this PDR and build the Akka service"
- "read this requirements file and produce the implementation plan"
- "derive the Akka component set from this spec and then generate code"

and the repository naturally guides that agent through a low-ambiguity path from intent to implementation.
