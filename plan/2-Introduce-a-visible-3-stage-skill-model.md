# Step 2 - Introduce a visible 3-stage skill model

## Objective

Make the repository's routing model explicit as a **3-stage skill hierarchy**:

1. **Intent and architecture**
2. **Structural decisions**
3. **Component implementation**

This model should become visible in the docs instead of remaining implicit.

## Why this step matters

Right now, most content still foregrounds component implementation skills.
A future agent should instead see a clear progression:

- start from intent
- resolve major architecture decisions
- then load implementation skills

Without this model, the repo still feels flat and component-centric.

## Primary files to consider

Likely targets:
- `skills/README.md`
- `README.md`
- `AGENT-README.md`

Possible supporting targets:
- `docs/next-steps.md` if wording needs alignment

## Required changes

Implement the following:

1. Add a concise section that defines the 3 stages.
2. Place `akka-solution-decomposition` in Stage 1.
3. Place `akka-entity-type-selection` in Stage 2.
4. Describe Stage 3 as the family of focused implementation skills:
   - entities
   - workflows
   - views
   - consumers
   - timed actions
   - endpoints
   - agents
5. Make it clear that not every task starts at Stage 3.

## Desired outcome

A future agent should be able to answer:
- Where do I start if all I have is a PDR?
- What do I do if I already know I need state but not which entity type?
- When do I move from planning into code generation?

## Deliverables

At minimum:
- `skills/README.md` contains a visible 3-stage model section
- top-level docs use the same terminology consistently

## Out of scope

Do not create many new selector skills in this step.
This step is about establishing the model, not filling out every possible Stage 2 decision.

## Completion criteria

This step is done when:
- the 3-stage model is clearly documented
- Stage 1, Stage 2, and Stage 3 are easy to distinguish
- the routing model is understandable without reading many files

## Suggested implementation notes

Example Stage labels:
- Stage 1: requirements decomposition and architecture selection
- Stage 2: focused architecture decisions
- Stage 3: focused component implementation
