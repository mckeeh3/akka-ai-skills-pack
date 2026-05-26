# Sprint 01: Runtime Tooling

Goal: make the starter's normal workstream agent runtime assemble a governed tool list from active managed configuration and pass it to the real Akka Agent with `effects().tools(runtimeTools)`.

Tasks:

1. Add runtime tool registry and resolver.
2. Add `@FunctionTool` loader tools for `readSkill` and `readReferenceDoc`.
3. Wire `WorkstreamRuntimeAgent` to use resolved runtime tools.
4. Add tests proving real Akka tool calls through the model path.

Acceptance:

- Normal workstream agent invocation cannot be marked complete unless active managed config determines both prompt and tool list.
- Tool availability comes from stable tool ids in `ToolPermissionBoundary`, not arbitrary class names.
- `readSkill` and `readReferenceDoc` are available only when both manifest assignment and tool boundary grants allow them.
