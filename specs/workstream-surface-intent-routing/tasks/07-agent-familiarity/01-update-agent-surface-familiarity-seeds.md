# Task: Update agent surface familiarity seeds

## Objective

Update governed workstream agent prompt/skill/reference seed material so agents accurately understand and recommend their workstream surfaces without claiming direct mutation authority.

## Required reads

- `AGENTS.md`
- `specs/workstream-surface-intent-routing/README.md`
- `specs/workstream-surface-intent-routing/sprints/04-agent-familiarity-verification.md`
- `src/main/java/ai/first/application/foundation/agent/AgentBehaviorSeedLoader.java`
- `src/main/resources/agent-behavior-seeds/starter-v1/**`
- surface catalog metadata from previous tasks
- agent behavior repository/seed tests

## Skills

- akka-agent-behavior-profiles
- akka-agent-prompt-governance
- akka-agent-reference-governance
- akka-agent-tool-boundaries

## Expected outputs

- Seed prompt/skill/reference updates describing surface routing and structured surface use.
- Guidance remains explicit that agents may recommend/open surfaces but may not submit side-effecting commands in this mini-project.
- Tests or checksum/seed import adjustments as needed.
- Queue update.

## Required checks

- `git diff --check`
- targeted seed/import tests
- broader `mvn test` if shared agent behavior seed loading changes materially

## Done criteria

- Each core workstream agent has enough familiarity to explain key surfaces and safe next steps.
- Prompt/skill/reference text cannot be interpreted as granting backend capability or direct mutation authority.
- Changes and queue update are committed.
