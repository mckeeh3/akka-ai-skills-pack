# TASK-WSI-01-001: Align skills and app-description guidance for workstream icons

## Goal

Update related routing, app-description, surface, UI, and web UI skills so future generated SaaS apps treat workstream icons as universal shell metadata and capture icons when each workstream is first defined.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/app-description-maintenance-flow.md`
- `skills/agent-workstream-apps/SKILL.md`
- `skills/app-description-functional-agent-modeling/SKILL.md`
- `skills/app-description-surface-modeling/SKILL.md`
- `skills/app-description-ui/SKILL.md`
- `skills/akka-web-ui-apps/SKILL.md`

## Expected edits

Update only focused passages. Do not rewrite whole skills.

Required semantics:

- Workstream definitions include icon metadata: stable icon id, visual hint, accent color token, tooltip, aria label, optional asset reference.
- `12-workstreams/` owns icon meaning as part of workstream metadata.
- `55-ui/` owns rendering and interaction realization, not the semantic assignment.
- Buttons/links/icons/cards/rows that open protected surfaces or workstreams are governed surface-request actions.
- My Account remains launched from the signed-in user tile in the lower-left rail, not duplicated in the top rail.
- Domain workstreams choose/finalize an icon during initial workstream implementation.

## Required checks

```bash
git diff --check
rg -n "workstream icon|WorkstreamIconDescriptor|surface-request|open_workstream|My Account" \
  skills/README.md \
  skills/agent-workstream-apps/SKILL.md \
  skills/app-description-functional-agent-modeling/SKILL.md \
  skills/app-description-surface-modeling/SKILL.md \
  skills/app-description-ui/SKILL.md \
  skills/akka-web-ui-apps/SKILL.md \
  docs/app-description-maintenance-flow.md
```

## Done criteria

- Skills route future generated SaaS work through icon metadata and governed shell navigation.
- Pending task is marked done with a completion note.
- Commit message: `workstream-icons: align skills and app descriptions`.
