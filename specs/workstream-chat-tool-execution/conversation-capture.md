# Conversation Capture: Workstream Chat Tool Execution

## Background

The user first identified that the secure AI-first SaaS architecture had two worker types, human and agent, but did not consistently define tools as the shared boundary used by both. Humans use workstreams and surfaces; agents use tools; the missing concept was that surfaces are also human-facing tool interfaces.

The subsequent skills-pack mini-project `specs/workstream-tool-use-alignment/` integrated this doctrine into the skills pack:

```text
Workstream agent
→ bounded purpose/prompt, skills/references, policies, and governed tool catalog
→ human-backed actor adapter: structured surfaces and confirmed chat tool plans
→ AI-backed actor adapter: model-mediated tool planning and agent-tool calls
→ shared capability-backed governed tools
→ backend-enforced authorization, transaction boundary, audit/work trace, and result surfaces
```

That mini-project completed, but it intentionally did not implement root app runtime chat-tool execution.

## User goal

The user wants a root app mini-project to implement confirmed workstream chat tool execution.

Example request:

```text
create org "Org 1", and invite mckee.hugh@gmail.com as an org admin
```

The human user can perform these operations through surfaces today, but should also be able to do work through direct workstream chat requests when the selected workstream agent tool catalog allows it.

## Accepted behavior model

- Access is limited to the selected workstream agent's governed tool catalog.
- A chat request is processed by the workstream agent's AI model for interpretation/planning when the request requires model-backed reasoning.
- The workstream agent responds with a sufficiently detailed plan and asks for permission to proceed.
- The human must explicitly confirm the exact plan before consequential tools execute.
- The human remains limited by their own AuthContext, tenant/customer scope, role/capabilities, membership state, selected workstream, tool boundary, and tool policy.
- Each tool is a transaction boundary.
- Multi-step plans execute one governed tool at a time with per-step authorization, validation, idempotency, traces, and result reporting.
- Failure of one step must not leave the system inconsistent; the result surface must report completed, failed, skipped, and recovery states.
- Surfaces and chat tool plans should share the same governed tool ids and capability contracts rather than duplicating business semantics.

## Relationship to surface intent routing

The earlier mini-project `specs/workstream-surface-intent-routing/` implemented deterministic no-mutation routing to surfaces. That behavior remains correct and should continue to run first for high-confidence surface-open/prefill prompts such as:

```text
create organization "Org 1"
```

This mini-project adds a separately governed path for requests that intentionally ask the workstream agent to plan and, after confirmation, execute tools. The distinction is:

- deterministic surface routing: opens/prepopulates surfaces and never mutates;
- confirmed chat tool execution: proposes a plan, requires human confirmation, then executes governed tools transaction-by-transaction.

## Mini-project structure decision

The user asked whether there should be one mini-project for each of the five foundation workstreams or a single mini-project for all workstreams.

Decision: create a **single mini-project** with shared substrate tasks first and per-workstream expansion tasks later. This avoids duplicating the plan/confirmation/dispatcher/trace substrate five times and gives verification one place to assess cross-workstream consistency.

## Planning defaults for this queue

- Start with User Admin because it contains the motivating example and has existing governed surface/action paths for organizations and invitations.
- Include all five foundation workstreams in the done state, but allow representative first-pass tool-plan paths for the non-User-Admin workstreams.
- If verification finds a workstream undercovered, append focused follow-up tasks rather than closing the queue.
- Do not implement queued tasks in this scaffold session.
