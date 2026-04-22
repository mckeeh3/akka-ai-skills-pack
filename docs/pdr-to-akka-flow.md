# PDR-to-Akka flow

Use this mini-example set when you want a concrete pattern for the repository's intended workflow:

1. read a small requirements artifact
2. produce an Akka solution plan before coding
3. route to the minimal focused skills
4. implement components in the recommended order

## Canonical example pair

Requirements input:
- `examples/purchase-request-pdr.md`

Resulting solution plan:
- `examples/purchase-request-solution-plan.md`

## How to use the pair

### As a learning example
Read the PDR first, then compare it with the solution plan.

### As a reusable prompt pattern
Use a prompt like:

```text
read docs/examples/purchase-request-pdr.md,
then produce an Akka solution plan with:
- capability summary
- chosen components
- why each component exists
- skill routing
- implementation order
- required tests
```

### As a coding handoff
Once the solution plan is accepted, treat it as the implementation contract.
Load only the Stage 3 skills named in the plan and generate code component by component, with corresponding tests for each component family.
For a lightweight queue template, see `solution-plan-to-implementation-queue.md`.

## What the example demonstrates

The example intentionally includes enough scope to justify multiple component families:
- one write model
- one durable orchestration flow
- one read model
- one timed behavior
- one async integration bridge
- one HTTP API surface

It is still small enough for future agents to load cheaply.

## Related docs

- `intent-driven-usage-flow.md`
- `../skills/README.md`
- `../skills/akka-solution-decomposition/SKILL.md`
