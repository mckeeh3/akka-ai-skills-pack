# Step 4 - Add an explicit end-to-end intent-driven flow section

## Objective

Add a short, high-visibility section to the repository docs that explains the full end-to-end usage flow:

**requirements/PDR/spec -> decomposition -> architectural decisions -> focused skills -> code generation -> tests**

## Why this step matters

Even if the top-level story improves, future agents still benefit from one concrete section that explains the workflow in sequence.

This step should reduce ambiguity by making the expected operating flow visible and repeatable.

## Primary files to consider

Likely targets:
- `README.md`
- `AGENT-README.md`
- `skills/README.md`

Possible supporting target:
- a new doc in `docs/` if a reusable longer version is helpful

## Required changes

Implement the following:

1. Add a concise section titled something like:
   - Intent-driven usage flow
   - Requirements-to-code flow
   - PDR-to-Akka workflow
2. Show the sequence of work clearly.
3. Keep the section short enough to be loaded cheaply by future agents.
4. Make sure the section links or routes to the right files.
5. State explicitly that code generation should happen after decomposition and structural selection.

## Desired outcome

A future agent should be able to read one short section and understand:
- what to do first
- what to do second
- when to start coding
- how decomposition relates to code generation

## Deliverables

At minimum:
- one visible end-to-end flow section in a top-level doc

Optional:
- a dedicated reusable doc in `docs/` for a slightly longer version

## Out of scope

Do not create the full canonical example in this step unless it is needed as a tiny reference.
That belongs more directly to Step 5.

## Completion criteria

This step is done when:
- an agent can read a single short section and understand the end-to-end repository workflow
- the flow from intent to code is explicit, not implied

## Suggested implementation notes

A good compact sequence is:
1. read requirements or spec
2. run decomposition
3. resolve key architecture decisions
4. load focused implementation skills
5. generate code and tests
