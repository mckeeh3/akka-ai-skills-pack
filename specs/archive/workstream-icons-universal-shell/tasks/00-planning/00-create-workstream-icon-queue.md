# TASK-WSI-00-001: Create workstream icon migration queue and doctrine capture

## Goal

Create the workstream icon migration queue and capture the doctrine changes already identified in discussion:

- My Account dashboard is the lightweight “what do I need to do next?” hub.
- Workstream status panels show one large attention count and an icon/open affordance.
- Workstream icons are universal shell metadata.
- Buttons, links, cards, rows, and icons that open surfaces or workstreams are governed surface-request actions.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/examples/ai-first-saas-core-app-domain/my-account-workstream/README.md`

## Expected edits

- Create `specs/workstream-icons-universal-shell/README.md`.
- Create `specs/workstream-icons-universal-shell/conversation-capture.md`.
- Create `specs/workstream-icons-universal-shell/pending-tasks.md`.
- Create self-sufficient follow-up task briefs under `specs/workstream-icons-universal-shell/tasks/`.
- Update `docs/agent-workstream-application-architecture.md` with workstream icon metadata and governed universal shell navigation.
- Update `docs/examples/ai-first-saas-core-app-domain/my-account-workstream/README.md` with dashboard, personal queue, workstream status panels, icon metadata, and tests.

## Required checks

```bash
git diff --check
rg -n "WorkstreamIconDescriptor|workstream icon|personal queue|workstream status|TASK-WSI" \
  docs/agent-workstream-application-architecture.md \
  docs/examples/ai-first-saas-core-app-domain/my-account-workstream/README.md \
  specs/workstream-icons-universal-shell
```

## Done criteria

- The migration queue exists and every follow-up task is self-sufficient for a fresh harness session.
- Universal shell doctrine captures workstream icon metadata, My Account dashboard status panels, and surface-request navigation.
- `specs/workstream-icons-universal-shell/pending-tasks.md` marks this task done with a completion note.
- Commit message: `workstream-icons: add migration queue and doctrine capture`.
