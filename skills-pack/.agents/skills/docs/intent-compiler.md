# Intent Compiler

The Akka AI skills pack is an **intent compiler**. Its source language is incremental human intent: product ideas, requirements, corrections, refinements, bug reports, design feedback, implementation discoveries, and code-level change requests.

The compiler produces two current-state outputs:

1. **Non-code intent artifacts** such as app-description files, requirements, specs, acceptance criteria, task briefs, and pending questions.
2. **Generated functional app behavior** such as Akka components, frontend surfaces, API contracts, tests, configuration, audit/work traces, and runtime validation evidence.

Canonical intent artifacts describe the intended system **now**. They are not a historical ledger. Git history, commits, task queues, completion notes, and release notes carry historical change context.

## Compiler responsibilities

For every user increment, the skills pack should:

1. classify the increment's intent kind and affected scope;
2. normalize it into a current-intent delta;
3. reconcile it with existing app/domain/workstream/global artifacts;
4. identify ambiguity that must become a pending question;
5. update current-state intent artifacts without preserving obsolete phrasing;
6. route downstream realization into specs, backlogs, tasks, code, tests, and validation;
7. preserve traceability from objective to runtime evidence.

## Traceability spine

Forward traceability should follow this chain:

```text
app objective
  -> domain
    -> workstream
      -> actor adapter / surface action / agent tool / governed tool / policy / trace
        -> capability / API / Akka component / frontend route
          -> test
            -> runtime trace / audit / outcome
```

Reverse traceability should make it possible to start from code, state, an event, an endpoint, a test, or a runtime trace and identify the intent artifact that justified it.

When a human surface action and an AI agent tool perform the same consequential operation, the compiler should model them as actor-specific adapters of one governed workstream tool rather than as separate business semantics. The current-intent graph should preserve the shared governed tool id, the human-backed and AI-backed exposure adapters, their approval/denial behavior, and trace source such as `surface_action` or `agent_tool_call`.

## Canonical doc set

Use these documents as the active source of truth for intent-processing skills:

- [Current intent model](current-intent-model.md)
- [Incremental intent processing](incremental-intent-processing.md)
- [Intent to realization flow](intent-to-realization-flow.md)
- [Intent compiler skill contracts](intent-compiler-skill-contracts.md)

Legacy description-first or PRD-first documents may contain historical detail, but active skills should prefer this compiler model unless a focused implementation skill explicitly requires another reference.
