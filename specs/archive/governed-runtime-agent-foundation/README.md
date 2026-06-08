# Governed runtime agent foundation migration

This planning package makes the "fully backed" AI-first agent substrate a mandatory part of the generated core AI-first SaaS foundation.

## Goal

Generated core apps must include a managed runtime agent platform, not just static Akka agent classes or a few hard-coded admin agents. The foundation must define tenant-scoped agents, governed system prompts, governed runtime skills, per-agent skill manifests, tool permission boundaries, dynamic skill loading, behavior-editing agents, traces, and browser UI surfaces for administering and supervising those assets.

## Direction

- Treat `AgentDefinition`, governed prompt documents, governed skill documents, skill manifests, tool boundaries, prompt assembly traces, skill load traces, and agent work traces as core foundation concepts.
- Treat "admin agents" as mandatory responsibilities that can be implemented by one governed `UserAdminAgent` with skills or by multiple specialized agents.
- Make human behavior-document edits agent-mediated: humans request changes, editing agents create proposed diffs/drafts, humans review/approve/activate consequential changes.
- Add core UI expectations for agent catalog, agent detail, prompt governance, skill governance, skill-manifest assignment, tool permissions, edit-agent proposals, and traces.
- Preserve capability-first security: prompt/skill text never grants tool, data, or authorization authority by itself.

## Execution rule

- Execute exactly one task per fresh harness session.
- Do not combine tasks.
- Each completed task must update `pending-tasks.md` and create one git commit before the response.
- Commit message format: `agent-foundation: <short task title>`.

## Files

- `sprints/` — sprint-level migration plan.
- `backlog/` — detailed build backlog per sprint.
- `pending-tasks.md` — executable fresh-session task queue.
