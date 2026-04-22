# Step 1 - Reframe the repo's top-level story

## Objective

Change the repository's top-level narrative so it clearly leads with an **intent-driven flow** instead of a **component-family-first** flow.

The repo should explain, early and explicitly, that its main value is helping an AI coding agent go from:

**requirements/specification -> Akka solution decomposition -> focused implementation skills -> code generation**

## Why this step matters

Even with good decomposition skills present, future agents will still take the wrong path if the top-level story says:
- this is mainly a repository of entity, workflow, endpoint, and agent skills

The top-level story must instead say:
- this repository helps agents begin from user intent and derive the correct Akka architecture before coding

## Primary files to consider

Likely targets:
- `README.md`
- `AGENT-README.md`

Possible supporting targets:
- opening paragraphs in `skills/README.md` if needed for consistency

## Required changes

Implement the following kinds of changes:

1. Move intent-driven language higher in the file structure.
2. Explain that the repo supports high-level inputs such as:
   - PDRs
   - requirements docs
   - user stories
   - process descriptions
   - API sketches
   - UI briefs
3. State that the primary workflow is:
   - read high-level input
   - decompose into Akka components
   - route to focused skills
   - generate code and tests
4. Make clear that component skills are downstream implementation assets, not the only front door.

## Desired outcome

A reader should understand within the first section or two that:
- the repo is not only a component reference library
- it is also a requirements-to-architecture-to-code system for AI agents

## Deliverables

At minimum:
- `README.md` reflects the new story near the top
- `AGENT-README.md` reinforces the same direction for repository contributors

## Out of scope

Do not fully redesign all routing docs in this step unless needed for a small consistency edit.
That work belongs more directly to later steps.

## Completion criteria

This step is done when:
- the top-level story is clearly intent-driven
- the decomposition-first usage model is visible near the top of the main docs
- the repository no longer reads primarily as a set of disconnected component-family docs

## Suggested implementation notes

Useful phrasing to introduce or strengthen:
- intent-driven flow
- requirements-first usage
- PDR/spec to code generation
- decomposition before implementation
- focused skills as downstream routing assets
