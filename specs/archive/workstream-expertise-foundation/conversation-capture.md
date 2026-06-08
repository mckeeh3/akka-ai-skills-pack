# Conversation Capture: Workstream Expertise Foundation

## User vision

An important feature of the workstream concept is that each workstream is backed by an agent that is an expert on all things about its specific workstream. For example, the User Admin workstream's agent knows all about User Admin processes. Each workstream agent should be provided with a specific set of skills and skill reference documents that make it a workstream expert.

## Gap identified

Existing doctrine already has functional agents, governed prompts, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, `readSkill(skillId)`, `ToolPermissionBoundary`, and traces. The gap is a repeatable workstream-expertise lifecycle that ties these pieces to each functional workstream and proves, through seed content and tests, that a workstream agent is actually expert in its assigned domain.

## Proposed bridge

```text
workstream definition
→ expert bundle
→ seeded governed prompt/skills/reference docs
→ manifest-based runtime loading
→ tool/capability boundaries
→ admin governance UI
→ traceability and tests
```

## Planning request

Create a multi-task plan, similar to existing `specs/` plans, with self-sufficient tasks that can be executed in fresh harness sessions. Each task should be committed when completed. The plan may be organized into sprints, with sprint reviews deciding whether more work is needed.
