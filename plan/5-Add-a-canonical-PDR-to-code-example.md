# Step 5 - Add a canonical PDR-to-code example

## Objective

Create a canonical example that shows the repository's intended end-to-end workflow from a high-level requirements input to an Akka component plan and then to code-generation routing.

## Why this step matters

Without a concrete example, the repository can claim to support intent-driven decomposition without demonstrating it.

A canonical example gives future agents:
- a reusable prompt pattern
- a model for decomposition output
- a reference for selecting components
- a reference for implementation order

## Primary files to consider

Likely new files:
- `docs/pdr-to-akka-flow.md`
- `docs/examples/<domain>-pdr.md`
- `docs/examples/<domain>-solution-plan.md`

Possible supporting updates:
- `README.md`
- `skills/README.md`
- `skills/akka-solution-decomposition/SKILL.md`

## Required changes

Implement the following:

1. Choose one representative business scenario.
2. Create a concise requirements or PDR input document.
3. Create the corresponding solution plan output document.
4. Show:
   - capability summary
   - chosen Akka components
   - why each component exists
   - skill routing
   - implementation order
   - required tests
5. Link to the example from top-level routing docs if useful.

## Desired outcome

A future agent should be able to copy the pattern of:
- reading a requirement file
- producing a structured component plan
- routing into focused skills

## Deliverables

At minimum:
- one PDR/spec example
- one resulting solution plan example

Recommended:
- one small overview doc that explains how to use the pair

## Out of scope

Do not implement all resulting code in this step unless a tiny snippet is necessary for clarity.
This step is about the planning pattern, not the full application build.

## Completion criteria

This step is done when:
- the repo contains a concrete requirements-to-plan example
- the example is small enough for future agents to load cheaply
- the example clearly demonstrates the new intent-driven usage model

## Suggested implementation notes

Choose a scenario that naturally exercises multiple component families, for example:
- order intake and fulfillment
- approval and reminder flow
- customer support request handling

Prefer a scenario that needs at least:
- one write model
- one query/read model
- one process/orchestration decision
- one API surface
