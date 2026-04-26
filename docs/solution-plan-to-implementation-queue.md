# Solution plan to implementation queue

Use this lightweight template after Stage 1 decomposition is accepted.

For durable multi-session execution with task status, use `pending-task-queue.md` and materialize the queue as `specs/pending-tasks.md`.

Purpose:
- turn the solution plan into a downstream implementation work queue
- keep coding focused on one component family at a time
- make code generation and test generation explicit follow-on work

## Rule

A solution plan is not the final output.
It is the implementation contract for downstream work.

That contract should tell the next agent or next phase:
- what to build
- in what order to build it
- which skills to load for each build step
- which tests to generate alongside each component
- whether endpoint, web UI, or documentation/snippet work is also required

## Minimal transformation

Take these sections from the solution plan:
- chosen components
- skill routing
- recommended implementation order
- required tests

Then convert them into a queue like this:

```md
# Implementation Queue

1. Domain model
   - output: domain records, validation helpers, API records
   - skills:
     - <skill>
   - tests:
     - <test type>

2. Stateful core
   - output: <entity or workflow>
   - skills:
     - <skill>
     - <skill>
   - tests:
     - <test type>

3. Read model
   - output: <view>
   - skills:
     - <skill>
   - tests:
     - <test type>

4. Async or timed support
   - output: <consumer or timed action>
   - skills:
     - <skill>
   - tests:
     - <test type>

5. Edge delivery
   - output: <http endpoint / grpc endpoint / mcp endpoint / web ui>
   - skills:
     - <skill>
   - tests:
     - <test type>
```

## Practical use

For each queue item:
1. load only the listed skills
2. generate the code for that component or layer
3. generate its corresponding tests before moving on
4. keep later components out of context until their step begins

For reliable follow-on work across sessions, convert this lightweight queue into `specs/pending-tasks.md` and execute it with `akka-do-next-pending-task` one task at a time.

## What belongs downstream

The downstream implementation phase may include:
- component generation
- endpoint generation
- web UI generation
- test generation
- documentation or snippet generation when the task asks for it

## Quick checklist

Before starting code generation, verify that the solution plan already answers:
- which component is first
- which skills implement it
- which tests belong with it
- which later components depend on it
- whether any open questions still block coding

## Related docs

- `pending-task-queue.md`
- `intent-driven-usage-flow.md`
- `prd-to-akka-flow.md`
- `examples/purchase-request-solution-plan.md`
- `../skills/README.md`
- `../skills/akka-solution-decomposition/SKILL.md`
- `../skills/akka-do-next-pending-task/SKILL.md`
