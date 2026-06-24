# Workstream Tool Use Alignment Build Backlog

## Backlog goal

Align `skills-pack/` around governed tools as the shared workstream boundary for human-backed and AI-backed workers.

## Bounded implementation order

### WTUA-01: Audit current doctrine and create source map

- Produce `specs/workstream-tool-use-alignment/tool-use-source-map.md`.
- Identify canonical docs, skills, templates, examples, and validation tools that mention tools, surfaces, workstream agents, direct chat, surface routing, tool boundaries, capabilities, confirmation, approval, and traces.
- Classify each finding as aligned, needs refinement, potentially conflicting, or out of scope.
- Do not rewrite pack guidance in this task except for queue/status updates.

### WTUA-02: Update canonical doctrine docs

- Use the source map to update canonical docs first.
- Add or refine a compact canonical tool-use flow if needed.
- Reconcile no-mutation surface routing guidance with confirmed chat-driven tool execution.
- Preserve capability-first backend, workstream, surface, managed-agent, and runtime completion doctrine.

### WTUA-03: Align app-description and intent compiler skills

- Update capture/modeling/readiness skills so current-intent graph artifacts can represent workstream tool catalogs and actor adapters.
- Require shared governed tool ids across surface actions, human chat tool plans, agent tools, APIs, and internal exposure channels.
- Add confirmation, transaction boundary, trace, and test expectations.

### WTUA-04: Align agent/tool/trace implementation skills

- Update agent skills to distinguish governed workstream tools from Akka function-tool exposure.
- Add guidance for human-requested, agent-planned, human-confirmed tool execution.
- Preserve tool permission boundaries, runtime assembly, traces, and deterministic enforcement.

### WTUA-05: Align workstream/UI/SaaS skills

- Update workstream and UI-oriented skills so surfaces are human tool adapters and chat is a natural-language tool-plan adapter.
- Ensure surface routing remains supported but does not globally forbid confirmed chat tool execution.
- Preserve UX requirements for detailed confirmation, review, result surfaces, partial failure reporting, accessibility, and denial recovery.

### WTUA-06: Align planning, templates, examples, and validators

- Update planning/queue skills and any templates/examples to carry governed tool ids, actor adapters, confirmation/approval, transaction/idempotency, trace, and validation fields.
- Update or add lightweight validation/search checks only where appropriate.

### WTUA-07: Consistency repair pass

- Search the pack for contradictions or old wording that treats tools as agent-only or surfaces as unrelated screens.
- Repair scoped wording in the smallest safe set of files.
- Document residual out-of-scope findings in verification notes or append tasks.

### WTUA-99: Verify alignment and append follow-up tasks if needed

- Run pack checks.
- Compare final state against README done state.
- Append new bounded tasks and a new terminal verification task if gaps remain.

## Design notes

- Treat this as a pack-maintenance documentation/skill alignment project, not generated app runtime implementation.
- Keep edits small and source-of-truth-oriented. Avoid rewriting every skill when a focused cross-reference to canonical doctrine is enough.
- Use qualified terms consistently:
  - `governed tool` or `governed workstream tool` for the semantic operation;
  - `surface action` / `browser-tool` for human structured surface exposure;
  - `human_chat_tool_plan` or equivalent for human chat-mediated confirmed plans;
  - `agent-tool` / `agent_tool_call` for model-facing exposure;
  - `workflow-tool`, `timer-tool`, `consumer-tool`, `MCP-tool`, `internal-tool`, and `API` where those exposure channels matter.
- Confirmation is required for consequential human-chat tool execution. Approval may still be required by policy after confirmation.
- The AI model may propose a plan, but deterministic runtime checks enforce authority.
