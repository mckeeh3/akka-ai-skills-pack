# TASK-WTUA-04-001: Align agent, tool-boundary, and trace skills

## Purpose

Update agent/tool implementation skills so they treat Akka function tools, component tools, and MCP tools as exposure adapters for governed workstream tools, not as the root architecture.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/workstream-tool-use-alignment/README.md`
- `specs/workstream-tool-use-alignment/conversation-capture.md`
- `specs/workstream-tool-use-alignment/tool-use-source-map.md`
- `specs/workstream-tool-use-alignment/sprints/02-skill-family-alignment.md`
- canonical docs updated by `TASK-WTUA-02-001`
- `skills-pack/skills/akka-agents/SKILL.md`
- `skills-pack/skills/akka-agent-tools/SKILL.md`
- `skills-pack/skills/akka-agent-component-tools/SKILL.md`
- `skills-pack/skills/akka-agent-mcp-tools/SKILL.md`
- `skills-pack/skills/akka-agent-tool-boundaries/SKILL.md`
- `skills-pack/skills/akka-agent-work-trace/SKILL.md`
- additional agent governance/testing skills named by the source map

## Expected outputs

- Agent skills updated to explain:
  - governed tool id versus Akka `@FunctionTool`/component/MCP exposure;
  - bounded workstream agent tool catalog;
  - human-requested, model-planned, human-confirmed tool execution path;
  - intersection of human authority, workstream tool catalog, agent tool boundary, and tool policy;
  - tool invocation traces with `requestedBy`, actor adapter/source, plan confirmation, denials, partial failure, and result surfaces.
- Queue update.

## Required checks

- `git diff --check`
- targeted search over edited agent skills proving tool-boundary and trace guidance covers human chat-mediated and AI-backed tool calls

## Done criteria

- Skills do not imply prompt/skill text can grant tool authority.
- Skills preserve governed runtime assembly and provider fail-closed behavior for model-backed workstream agents.
- Changes and queue update are committed.
