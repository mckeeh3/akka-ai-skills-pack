# Step 6 - Make code generation an explicit downstream phase

## Objective

Clarify throughout the repository that **solution decomposition is not the final output**. It is the input contract for **focused code generation**.

The repository should explicitly communicate that the planning output feeds:
- component generation
- endpoint generation
- web UI generation
- test generation
- documentation/snippet generation when relevant

## Why this step matters

If decomposition is treated as a standalone artifact, future agents may stop after planning or may generate code without a clear handoff.

This step makes the handoff explicit:
- decomposition produces the plan
- the plan drives skill loading
- skill loading drives code generation

## Primary files to consider

Likely targets:
- `skills/akka-solution-decomposition/SKILL.md`
- `skills/README.md`
- `README.md`
- `AGENT-README.md`

Possible supporting target:
- a template doc under `docs/`

## Required changes

Implement the following:

1. Strengthen language that says the component plan is an implementation input.
2. Make skill routing and implementation order feel like a handoff to coding.
3. Where useful, use wording such as:
   - feeds code generation
   - downstream implementation phase
   - implementation contract
4. Clarify that each chosen component should route to corresponding code and test generation skills.

## Desired outcome

A future agent should understand that decomposition has succeeded only when it enables focused implementation work.

## Deliverables

At minimum:
- the planning-to-implementation handoff is explicit in the main routing docs and decomposition skill

Optional:
- a lightweight template doc showing how a solution plan becomes a coding work queue

## Out of scope

Do not redesign every component skill in this step.
This step is primarily about clarifying the handoff and downstream use of planning artifacts.

## Completion criteria

This step is done when:
- the repository clearly treats decomposition output as the input to code generation
- implementation order and skill routing are presented as operational next steps, not informational notes

## Suggested implementation notes

Good concrete language:
- "produce the component plan before coding"
- "use the plan to load only the required implementation skills"
- "generate components and tests in implementation order"
