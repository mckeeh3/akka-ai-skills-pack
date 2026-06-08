---
name: project-discussed-idea-to-pending-project
description: Create a self-contained specs/<initiative>/ mini-project with supporting docs and a pending-tasks.md queue when an idea, feature, concept, migration, remediation, or follow-up for the current project has already been discussed and is ready to implement one task per fresh harness session.
---

# Project Discussed Idea to Pending Project

Use this skill when an idea, feature, concept, migration, remediation, review finding, or follow-up initiative for the current project has already been discussed enough to implement and should be captured as a durable mini-project under `specs/`.

The target may be a downstream generated app, a fork of the secure SaaS Foundation App, or the Akka AI skills-pack source repository itself. Preserve the target project's own source-of-truth app-description, specs, repository guidance, and runtime completion rules.

The output is planning and queue materialization only. Treat the discussed idea as current intent for the mini-project, then materialize bounded tasks from that current-state model. Do not implement the queued tasks in the same run unless the user explicitly asks to execute one task after the queue exists.

## Use this skill when

Use when the prompt sounds like:
- "we discussed this; create pending tasks"
- "turn this concept into a task queue"
- "make a specs mini-project for this repo"
- "create a pending-tasks directory for this work"
- "this is ready to implement; break it into fresh-session tasks"
- "capture our discussion as a plan and queue"

## Project scope guardrail

Create planning artifacts for the **current target project**, not for `.agents/` itself unless the user explicitly asks for skills-pack maintenance. In downstream app projects, the mini-project usually concerns app-facing assets such as:
- `app-description/`
- `specs/`
- backend and frontend source
- docs, tests, tools, examples, or repository guidance

In the Akka AI skills-pack source repository, pack-maintenance mini-projects may concern source assets such as `skills-pack/skills/`, `skills-pack/docs/`, `skills-pack/pack/`, examples, tests, packaging, install scripts, or repository guidance.

Do not treat an installed `.agents/skills` support library as writable application source unless the user explicitly asks for skills maintenance, harness-skill customization, or a destructive reset.

## Required reading

Read only the smallest useful context:
- `AGENTS.md` or equivalent repository guidance
- `.agents/skills/README.md` or `skills/README.md` when present
- pack `../docs/intent-compiler.md`, `../docs/current-intent-model.md`, `../docs/intent-to-realization-flow.md`, and `../docs/intent-compiler-skill-contracts.md` when present
- pack `../docs/pending-task-queue.md` (installed skill) or project `docs/pending-task-queue.md` for queue mechanics when present
- pack `../docs/pending-question-queue.md` (installed skill) or project `docs/pending-question-queue.md` when unresolved decisions may block tasks
- pack `../docs/ai-first-saas-application-architecture.md` / `../docs/capability-first-backend-architecture.md` (installed skill) or project-local equivalents only when the initiative affects generated-app doctrine, agent workstreams, governed capabilities, security, UI, agents, audit, or runtime completion semantics
- the conversation notes, issue, prompt, draft spec, review finding, or source files named by the user
- related existing `specs/*/README.md` and `specs/*/pending-tasks.md` as pattern references

Prefer source artifacts that capture the actual discussion. Do not reread the whole repository unless the initiative is a whole-pack migration or review.

## Output directory shape

Use `../docs/intent-compiler-skill-contracts.md` and `../docs/intent-to-realization-flow.md` for the detailed mini-project, backlog, and queue output contract. Preserve generated-SaaS/SaaS Foundation App context when in scope, including invitation lifecycle, email delivery, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, AI admin/AdminRiskAgent/AccessReviewAgent, decision cards for risky admin, AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, readSkill, PromptAssemblyTrace, SkillLoadTrace, behavior editing, agent catalog, and agent detail coverage across the generated specs/backlog/task sequence.

## Verification loop

The task queue must implement a repeatable task-group loop:

1. plan bounded tasks;
2. execute queued work one task per fresh harness context;
3. run a verification work task at the end of the current task group;
4. verify both the current task group/sprint goals and the overall mini-project done state;
5. when gaps remain, append more bounded tasks and then append a new terminal verification task;
6. repeat until the mini-project is complete.

A verification task must:
- assess whether the current task group or sprint goals are complete;
- assess whether the overall mini-project done state in `README.md` has been achieved;
- compare completed work against sprint/backlog goals, task done criteria, conversation-capture decisions, unresolved questions, and blockers;
- run or require appropriate validation checks for the stated scope;
- append new bounded tasks when material gaps remain;
- append a new terminal verification task after any newly added work;
- record completion only when no material gaps remain.

Keep verification bounded to the mini-project's stated scope and done state. Do not turn the verification task into a whole-repository review unless the mini-project is explicitly a whole-repository review. If unrelated issues are discovered, record them as recommendations or propose a separate mini-project rather than silently expanding scope.

## Task sizing rules

Each task must be executable in one fresh harness context.

Good task boundaries:
- one doctrine/routing update plus focused consistency checks;
- one skill family update plus tests/search proof;
- one reference slice or example update;
- one backend component family plus direct tests;
- one frontend surface/component family plus typecheck/tests/build;
- one review/audit task that produces findings and follow-up queue edits.

Split or block tasks that are too broad, such as "fix all skills", "make SaaS Foundation App production ready", "migrate all UI", or "implement the whole feature".

Generated-app/reference-runtime tasks must preserve the target project runtime completion doctrine: do not mark feature work complete unless the real local runtime/API/UI path works at the stated scope, or the task is explicitly non-runtime/docs-only.

## Status and commit rules

Future task sessions must:
1. mark exactly one selected task `in-progress` before implementation edits;
2. execute only that task;
3. run required checks or block with a clear reason;
4. mark `done` only when checks and done criteria pass;
5. create one focused git commit for completed work;
6. record the commit message or hash in task notes;
7. report the next runnable task.

Do not mark a task `done` without a commit unless the queue explicitly says the task is non-mutating review-only and the user accepts no commit. For this repository's normal mini-projects, prefer committing review outputs too.

## Planning workflow

1. Name the initiative with a stable kebab-case directory and short task prefix.
2. Capture the discussion and decisions in `conversation-capture.md`.
3. Define current intent, done state, and non-goals in `README.md`.
4. Split work into ordered sprints only as much as needed.
5. Write backlog files with suggested harness task breakdowns.
6. Write task briefs for non-trivial queue items, including the terminal verification task brief.
7. Create `pending-tasks.md` with the scaffold task, implementation/review tasks, and a final verification task.
8. Validate that the first non-done task is runnable without guessing.
9. Validate that the final verification task can determine whether task-group and mini-project goals are complete, and can append follow-up tasks plus a new terminal verification task when needed.
10. Run `git diff --check`.
11. If committing the scaffold now, commit only the mini-project files and queue-status update.

## Final response

Report:
- created mini-project path;
- supporting docs created;
- number of tasks, including the terminal verification task, and first runnable task;
- whether the planning scaffold was committed;
- questions, concerns, or recommendations.

End with a fresh-context handoff prompt for the next task, for example:

```text
Use the project-discussed-idea-to-pending-project queue at specs/<initiative>/pending-tasks.md.
Execute only the next runnable task, load only its required reads and listed skills, update the queue, commit the task changes, and report the next runnable task.
```
