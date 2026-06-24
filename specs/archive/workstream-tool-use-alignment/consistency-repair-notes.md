# Tool-use consistency repair notes

Task: `TASK-WTUA-07-001`

## Scope

This pass searched for concrete residual contradictions after WTUA-02 through WTUA-06. It repaired only skills-pack guidance that still implied a surface-only or agent-tool-only model for conversational workstream tool use.

## Searches run

Targeted search families covered:

- stale global prohibition wording: `must not submit`, `Do not make chat`, `future separately-governed`, `direct command authority`, `direct chat`, `no mutation`;
- surface-only or agent-only phrasing: `only through surfaces`, `must use surfaces`, `tools are only`, `agent tools only`, `human-confirmed agent-tool`;
- example drift: `explicit surface action`, `workstream-agent tools are conversational`, `surface action, browser API, workstream-agent tool`, `business functionality and agent tools`, `browser actions, agent tools`.

## Repairs made

- `skills-pack/docs/minimum-ai-first-saas-app.md`
  - Reframed tools as shared governed tools exposed through selected adapters, including browser-tools, confirmed `human_chat_tool_plan`, AI-backed agent-tools, workflows, timers, consumers, APIs, and MCP.
  - Added explicit actor-adapter modeling to the extension sequence.
- `skills-pack/docs/core-ai-first-saas-foundation.md`
  - Updated foundation runtime semantics so capability-first backend contracts precede confirmed human chat tool plans as well as browser and AI-backed agent-tool exposure.
- `skills-pack/docs/examples/requirements-to-workstream-mini-example.md`
  - Replaced stale `human-confirmed agent-tool` wording with `human_chat_tool_plan`.
  - Added shared governed-tool id, confirmation/idempotency, `requestedBy`/`confirmedBy`, partial-failure, and validation expectations for generated tasks.
- `skills-pack/docs/examples/ai-first-saas-core-app-domain/README.md`
  - Split conversational human-backed `human_chat_tool_plan` adapters from AI-backed `agent_tool_call` workstream-agent tools.
  - Clarified side-effecting default paths and shared governed-tool semantics across surface actions, chat plans, and agent calls.
- `skills-pack/docs/examples/ai-first-saas-core-app-domain/*-workstream/README.md`
  - Updated capability-channel paragraphs to include surface/browser, confirmed chat-plan, AI-backed agent-tool, internal, workflow/timer/consumer/API/MCP, view, and internal-method adapters.
  - Repaired User Admin, My Account, Agent Admin, Audit/Trace, and Governance/Policy wording that previously implied conversational side effects required only surface actions or generic workstream-agent tools.

## Residual findings for terminal verification

- Remaining `must not submit` / `no-mutation` hits are scoped to deterministic surface-intent routing and include adjacent reconciliation language for separately modeled confirmed chat tool plans.
- Remaining `explicit surface action` hits in repaired examples are paired with modeled `human_chat_tool_plan` confirmation and backend authorization; they are not known high-confidence contradictions.
- Akka HTTP/gRPC/component skills still often summarize cross-channel consistency as `UI, agent tools, workflows, ...` without naming `human_chat_tool_plan` every time. This pass treats those as lower-confidence omissions rather than contradictions because canonical docs and focused workstream/capability skills already define the chat adapter. Terminal verification can decide whether a broader endpoint-family wording sweep is worthwhile.
- Example runtime code comments using `no direct mutation` remain scoped to read/advisory/autonomous-worker paths and were not changed.

## Result

No known high-confidence contradictions remain in `skills-pack/docs/**` or the touched example skill families from this pass.
