# Planning skill output contracts

Use this shared reference to keep planning and queue skills concise.

## Backlog-to-task contract

Every implementation task must be runnable by one focused harness session and include:

- task id, title, status, source backlog/spec reference, and dependency/blocker status;
- exact files or package zones expected to change;
- capability/workstream/surface/agent context when generated SaaS behavior is in scope;
- AuthContext/scope, authorization, traces, idempotency, and tests required for the slice;
- acceptance checks and commands;
- explicit out-of-scope items.

For secure full-core planning, preserve invitation lifecycle, email delivery, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, AI admin/AdminRiskAgent/AccessReviewAgent, decision cards for risky admin, AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, readSkill, PromptAssemblyTrace, SkillLoadTrace, behavior editing, agent catalog, and agent detail coverage in the relevant task sequence.

## Pending queue contract

`specs/pending-tasks.md` is the durable work queue. Keep existing ids/status history. Use statuses consistently: `pending`, `in-progress`, `blocked`, `done`, `superseded`.

Queue entries should be small enough to execute independently, ordered by dependency, and linked to task briefs when briefs exist. Do not mark done until implementation and checks pass. Do not delete completed work; add follow-up/supersession entries when requirements change.

## Maintenance contract

Queue maintenance may repair stale, duplicate, blocked, superseded, vague, answered-but-unreconciled, or inconsistent tasks/questions without implementing application code. Report:

- queue summary;
- findings;
- edits made;
- remaining risks/questions;
- next runnable task.

## Reconciliation contract

When a revised PRD/change request appears, preserve existing app-description/spec/backlog/task ids and implementation history. Categorize deltas as added, changed, removed, clarified, or conflict. Supersede obsolete non-done tasks; add follow-up tasks for completed work that must change; do not regenerate a parallel fresh app unless explicitly requested.

## Execution contract

`akka-do-next-pending-task` selects one runnable pending task, marks it in-progress, reads its brief and linked specs, implements only that scope, runs the smallest proving checks, updates the queue, and reports changed files/checks/blockers/next task.

`akka-do-next-pending-question` selects one actionable question, either asks it or reconciles an answered item into app-description/spec/backlog/task updates, then updates the question queue without implementing unrelated application code.
