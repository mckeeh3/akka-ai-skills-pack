# TASK-WCTC-10-001: Update expanded catalog seeds and traceability

## Purpose

Update starter agent seed material and traceability docs for the expanded chat tool catalog.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-catalog-expansion/README.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-coverage-map.md`
- completed per-workstream implementation notes
- `src/main/resources/agent-behavior-seeds/starter-v1/**`
- `src/main/java/ai/first/application/foundation/agent/AgentBehaviorSeedLoader.java`
- `app-description/domains/core-starter/workstreams/**`

## Skills

- `akka-agent-behavior-profiles`
- `akka-agent-prompt-governance`
- `akka-agent-skill-governance`
- `akka-agent-reference-governance`
- `akka-agent-work-trace`

## Expected outputs

- seed prompt/skill/reference updates for expanded catalog guidance
- checksum/import updates if needed
- traceability map/docs updates
- queue update

## Required checks

- `git diff --check`
- targeted seed/import tests if seed resources change
- targeted search proving seed material does not claim unrestricted mutation authority

## Done criteria

- Agents can explain expanded catalog classifications accurately.
- Seed text does not grant authority or bypass confirmation/approval.
- Changes and queue update are committed.
