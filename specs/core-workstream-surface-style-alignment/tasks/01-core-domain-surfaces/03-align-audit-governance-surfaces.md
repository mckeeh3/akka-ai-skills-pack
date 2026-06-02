# Task Brief: Align Audit/Trace and Governance/Policy Surface Style

## Objective

Update Audit/Trace and Governance/Policy core workstream README files so their required surfaces include concise replacement-style appearance expectations.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/core-workstream-surface-style-alignment/README.md`
- `specs/core-workstream-surface-style-alignment/conversation-capture.md`
- `specs/core-workstream-surface-style-alignment/pending-tasks.md`
- `specs/core-workstream-surface-style-alignment/sprints/01-core-domain-surfaces-sprint.md`
- `specs/core-workstream-surface-style-alignment/backlog/01-core-domain-surfaces-build-backlog.md`
- `specs/core-workstream-surface-style-alignment/tasks/01-core-domain-surfaces/03-align-audit-governance-surfaces.md`
- `docs/web-ui-style-guide.md`
- `docs/examples/ai-first-saas-core-app-domain/audit-trace-workstream/README.md`
- `docs/examples/ai-first-saas-core-app-domain/governance-policy-workstream/README.md`

## In scope

- Add surface style notes for Audit/Trace dashboards, trace search, record detail, timelines, agent work traces, prompt/skill traces, data access surfaces, export requests, and system-message surfaces.
- Add surface style notes for Governance/Policy dashboards, policy lists/details/diffs, proposal queues, decision cards, simulation results, exception reviews, learning center, and system-message surfaces.
- Preserve redaction, evidence, decision, and capability semantics.

## Out of scope

- Editing My Account/User Admin/Agent Admin docs.
- Frontend/backend implementation.

## Expected outputs

- Updated Audit/Trace and Governance/Policy workstream README files.
- Updated queue status.

## Skills

- `app-description-ui`
- `ai-first-saas-ui-surfaces`
- `ai-first-saas-audit-trace`
- `ai-first-saas-policy-governance`

## Required checks

- `git diff --check`
- focused stale style/theme search over touched files

## Done criteria

- Audit/Trace and Governance/Policy surfaces include enterprise workstream style expectations for evidence, trace, decision, and governance surfaces.
- Touched docs contain no stale mode-first or old default style contradictions.
- Changes and queue update are committed.

## Commit message convention

- `ui-theme: align audit governance surface style`
