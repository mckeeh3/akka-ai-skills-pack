# TASK-WCTE-11-001: Update agent seeds and traceability for chat tool execution

## Purpose

Update governed starter agent behavior seed material and traceability docs so foundation agents can explain confirmed chat tool plans accurately.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-execution/README.md`
- `specs/workstream-chat-tool-execution/source-and-design-map.md`
- `src/main/java/ai/first/application/foundation/agent/AgentBehaviorSeedLoader.java`
- `src/main/resources/agent-behavior-seeds/starter-v1/**`
- `app-description/domains/core-starter/workstreams/**`
- completed implementation files

## Skills

- `akka-agent-behavior-profiles`
- `akka-agent-prompt-governance`
- `akka-agent-skill-governance`
- `akka-agent-reference-governance`
- `akka-agent-work-trace`

## Expected outputs

- Seed prompts/skills/references updated for all five workstream agents.
- Seed checksums/import tests updated as needed.
- Traceability docs/specs updated for new surfaces/actions/tool ids.
- Queue update.

## Required checks

- `git diff --check`
- targeted seed/import tests
- targeted search proving seed material does not claim unrestricted mutation authority

## Done criteria

- Agents can explain deterministic surface routing versus confirmed chat tool execution.
- Seed text does not grant authority or bypass confirmation.
- Changes and queue update are committed.
