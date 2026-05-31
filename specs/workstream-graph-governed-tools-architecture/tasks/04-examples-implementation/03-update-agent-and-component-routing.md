# TASK-WGGT-04-003: Update agent, internal-agent, and component routing

## Objective

Update agent/internal-agent and horizontal component skills so they consume governed-tool contracts and participate in workstream agent graphs.

## Required reads

- AGENTS.md
- skills/README.md
- specs/workstream-graph-governed-tools-architecture/README.md
- skills/agent-workstream-apps/SKILL.md
- skills/ai-first-saas/SKILL.md
- skills/capability-first-backend/SKILL.md
- skills/akka-agents/SKILL.md
- skills/akka-autonomous-agents/SKILL.md
- representative endpoint/workflow/view/consumer/timed-action skills discovered by search

## In scope

- Internal virtual dashboard agent and worker-agent delegation semantics.
- Agent-tool as governed-tool exposure.
- Internal-tool/workflow-tool/timer-tool/consumer-tool mapping where useful.
- Component skills require governed-tool contracts before coding.

## Checks

- `git diff --check`
- Focused term search over edited skills.

## Done criteria

- Agent and component routing align with governed-tool and workstream agent graph model.
- Queue updated and committed.
