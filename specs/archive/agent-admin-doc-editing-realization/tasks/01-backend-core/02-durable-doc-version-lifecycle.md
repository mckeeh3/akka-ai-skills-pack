# Task AADE-01-002: Durable document version and lifecycle behavior

## Scope

Implement durable backend behavior for prompt, skill, and skill reference doc versions and lifecycle. Do not implement editing-agent model calls or frontend surfaces in this task.

## Required reads

- `specs/agent-admin-doc-editing-realization/README.md`
- `specs/agent-admin-doc-editing-realization/conversation-capture.md`
- `specs/agent-admin-doc-editing-realization/sprints/01-backend-doc-admin-sprint.md`
- `specs/agent-admin-doc-editing-realization/backlog/01-backend-doc-admin-build-backlog.md`
- `app-description/domains/core-starter/capabilities/agent-doc-administration.md`
- `app-description/domains/core-starter/data-state/managed-agent-behavior-state.md`
- current prompt/skill/reference document entities and tests

## Skills

- `akka-key-value-entities` or `akka-event-sourced-entities` if entity shape changes are needed
- `akka-kve-unit-testing` or `akka-ese-unit-testing` as appropriate

## Implementation guidance

Implement or adapt durable state for:

- simple integer versions;
- current/latest version tracking;
- version metadata: created time, actor, saved content, edit-session transcript/summary;
- current-version-only Save behavior with stale-version recovery/denial;
- historical read-only behavior;
- adjacent-version diff (`N` vs `N-1` only);
- restore creating a new current version with edit request `Restored from version N`;
- skill create/update/delete;
- reference doc create/update/delete;
- permanent skill deletion cascading reference doc deletion;
- no restore for deleted skills/reference docs.

## Required checks

```bash
mvn -Dtest='*GovernedDocument*Test,*Agent*Doc*Test,*AgentAdmin*Service*Test' test
git diff --check
```

Adjust test names to actual classes added/changed.

## Done criteria

- Durable state and tests prove versioning, diff, restore, skill/reference lifecycle, and stale save semantics.
- Existing old governance semantics do not override the new current app-description behavior.
- Queue is updated and changes are committed.
