# TASK-UASCC-00-001: Create User Admin surface conformance cleanup planning scaffold

## Intent

Create the mini-project planning scaffold and pending queue from the reviewed User Admin surface conformance findings.

## Required reads

- `AGENTS.md`
- `specs/user-admin-surface-conformance-cleanup/README.md`
- `specs/user-admin-surface-conformance-cleanup/conversation-capture.md`
- `.agents/skills/project-discussed-idea-to-pending-project/SKILL.md` or equivalent provided skill text

## Expected outputs

- mini-project README, conversation capture, sprint, backlog, task briefs, and pending queue.

## Required checks

```bash
git diff --check
```

## Done criteria

- First non-done task is runnable without guessing.
- Queue includes terminal verification task that can append follow-up tasks and a new terminal verification task if gaps remain.
- Planning scaffold is committed when repository state allows a focused commit of mini-project files.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent` concept and `agent-user-admin` runtime alias.
- Attention category or non-attention reason: planning only; no runtime attention item.
- Role-specific dashboard / surface: User Admin dashboard/directory/detail/task surface conformance planning.
- Surface graph node/action edge: docs-only planning for dashboard, directory, detail, task, decision, workflow, and system-message surfaces.
- Governed-tool id and exposure: none; docs/planning only.
- Capability id: planning only; inherits `user_admin.*`, `saas_owner.organization.*`, and admin audit capabilities for later tasks.
- AuthContext / roles / tenant scope: preserve selected AuthContext and role/scope rules in task planning.
- Akka substrate: docs/specs only.
- API / frontend / realtime path: non-runtime planning.
- Audit/work trace requirements: planning records audit/trace expectations for later tasks.
- Local validation path: `git diff --check`.
