# Sprint 03: Planning, Validation, and Consistency

## Goal

Update planning artifacts, templates, examples, and validation support so the skills-pack does not merely describe the new tool-use model, but also asks future generated-app tasks to carry it through specs, queues, implementation contracts, and review.

## Scope

- Planning and queue skills/task templates should require:
  - workstream agent tool catalog context;
  - governed tool ids and capability ids;
  - actor adapters/exposure channels (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `workflow`, `timer`, `consumer`, `api`, `mcp`);
  - confirmation/approval requirements;
  - idempotency and transaction boundary semantics;
  - audit/work trace requirements;
  - validation paths for direct surface use and confirmed chat-mediated tool use when both are in scope.
- Templates and examples should show shared tool ids between surfaces and agent/chat adapters instead of duplicate semantics.
- Validators or lightweight search checks should be updated only where existing tools can reasonably enforce required vocabulary or structure.
- A consistency repair task should search for conflicting guidance such as global prohibitions on direct chat tool use and replace it with the accepted confirmed-plan model.

## Completion signal

Sprint 03 is complete when new generated-app planning work is likely to produce implementation-ready task briefs that preserve the shared governed tool contract and the human-chat confirmation path.
