# TASK-WCTE-04-001: Add governed plan proposal runtime path

## Purpose

Extend the governed workstream agent runtime so model-backed workstream agents can produce structured chat tool plan proposals through the real Akka Agent path.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-execution/README.md`
- `specs/workstream-chat-tool-execution/source-and-design-map.md`
- `src/main/java/ai/first/application/foundation/agent/WorkstreamRuntimeAgent.java`
- `src/main/java/ai/first/application/foundation/agent/DefaultWorkstreamAgentRuntimeInvoker.java`
- `src/main/java/ai/first/application/foundation/agent/AgentRuntimeService.java`
- `src/main/java/ai/first/application/foundation/agent/AgentRuntimeToolResolver.java`
- related agent runtime tests named in the design map

## Skills

- `akka-agent-component`
- `akka-agent-structured-responses`
- `akka-agent-tools`
- `akka-agent-tool-boundaries`
- `akka-agent-testing`

## Expected outputs

- Structured plan proposal method or mode on the governed workstream agent runtime.
- Runtime preparation that resolves active AgentDefinition, prompt assembly, model provider alias, tool boundary, runtime tools, and traces.
- Fail-closed typed system-message/plan-unavailable result when provider/runtime/tool-boundary is missing.
- Deterministic tests using test provider/model support only in tests.
- Queue update.

## Required checks

- `git diff --check`
- targeted agent runtime tests

## Done criteria

- Normal runtime does not fake model-backed planning success without configured provider/runtime.
- Prompt/skill/reference text cannot grant extra tools.
- Changes and queue update are committed.
