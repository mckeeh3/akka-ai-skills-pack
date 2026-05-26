# TASK-MAGENT-02-003: Add manifest and tool-boundary components and views

## Objective

Make skill manifests, reference manifests, and tool permission boundaries first-class core runtime configuration records.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `skills/akka-agent-skill-governance/SKILL.md`
- `skills/akka-agent-reference-governance/SKILL.md`
- `skills/akka-agent-tool-boundaries/SKILL.md`
- `skills/akka-event-sourced-entities/SKILL.md`
- `skills/akka-views/SKILL.md`
- starter `AgentSkillManifest.java`, `AgentReferenceManifest.java`, and `ToolPermissionBoundary.java`

## Expected outputs

- Components for manifest and tool-boundary lifecycle/current state.
- Compact manifest runtime lookup view.
- Tool-boundary grant/detail/search views.
- Tests for assigned/unassigned lookup, missing grant denial, separate read_skill/read_reference grants, tenant isolation, and compact rendering inputs.

## Checks

- `mvn test`
- `git diff --check`
- `rg -n "AgentSkillManifestEntity|AgentReferenceManifestEntity|ToolPermissionBoundaryEntity|ToolBoundary.*View|read_skill|read_reference|authority expansion" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`

## Commit

`managed-agents-core: add manifest and boundary components`
