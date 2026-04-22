# Step 3 - Make requirements-first the default entry

## Objective

Ensure the repository consistently treats **requirements-first decomposition** as the default starting point for broad tasks.

The default guidance should be:
- if the task begins from high-level requirements, prompts, PDRs, or specification files, start with `akka-solution-decomposition`

The narrower guidance should be:
- if the task already implies a stateful core and only the entity type is unknown, use `akka-entity-type-selection`

## Why this step matters

It is still easy for a future agent to jump directly into:
- entity skills
- workflow skills
- endpoint skills

That is appropriate only when the architecture is already known.
For broad tasks, the repository should default to decomposition first.

## Primary files to consider

Likely targets:
- `skills/README.md`
- `AGENT-README.md`
- `README.md`
- selected skill descriptions that still overstate component-first entry points

## Required changes

Implement the following:

1. Audit wording that implies `akka-entity-type-selection` is the main requirements-first entry.
2. Replace that wording with `akka-solution-decomposition` where appropriate.
3. Preserve `akka-entity-type-selection` as a narrower sub-decision.
4. Make sure startup/session guidance reinforces this ordering.
5. Tighten any descriptions that still imply component selection should happen before decomposition.

## Desired outcome

A future agent should reliably start with the decomposition skill whenever the component set is unknown.

## Deliverables

At minimum:
- references to requirements-first entry are consistent across top-level docs
- `akka-entity-type-selection` is clearly described as a narrower follow-on selector

## Out of scope

Do not exhaustively rewrite every component skill unless the wording directly conflicts with the new default entry model.

## Completion criteria

This step is done when:
- requirements-first tasks consistently route to `akka-solution-decomposition`
- `akka-entity-type-selection` is no longer presented as the general front door for broad requirements
- the docs no longer send mixed signals about where to begin

## Suggested implementation notes

Look especially for phrases like:
- start with entity type selection
- if you have requirements
- start with this skill when the user describes behavior

Those may need to be narrowed or redirected.
