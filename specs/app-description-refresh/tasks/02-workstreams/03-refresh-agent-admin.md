# TASK-ADR-02-003: Refresh Agent Admin workstream app-description

## Summary

Revise the Agent Admin workstream current-intent graph using `workstreams/agent-admin-migration-plan.md` and refreshed shared foundation artifacts.

## Scope

Primary edit scope: `app-description/domains/core-starter/workstreams/agent-admin/**` plus narrow runtime-validation/spec notes when needed.

## Required reads

- `AGENTS.md`
- `app-description/AGENTS.md`
- `specs/app-description-refresh/README.md`
- `specs/app-description-refresh/workstreams/agent-admin-migration-plan.md`
- `specs/app-description-refresh/shared-foundation-audit.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/app-description-component-graph.md`
- `.agents/skills/docs/app-description-source-alignment.md`
- `.agents/skills/docs/runtime-validation.md`
- `.agents/skills/akka-agent-behavior-profiles/SKILL.md`
- `.agents/skills/akka-agent-prompt-governance/SKILL.md`
- `.agents/skills/akka-agent-skill-governance/SKILL.md`
- `.agents/skills/akka-agent-reference-governance/SKILL.md`
- `.agents/skills/akka-agent-tool-boundaries/SKILL.md`
- current Agent Admin app-description files

## Skills

- `app-description-functional-agent-modeling`
- `app-description-surface-modeling`
- `app-description-capability-modeling`
- `app-description-auth-security`
- `app-description-test-specification`
- `app-description-observability`
- `app-description-ui`

## Expected outputs

- Refreshed Agent Admin workstream files.
- Managed-agent governance surfaces/tools/traces are explicit.
- Lifecycle/source-alignment state updated, normally `stale-description-changed` unless proven no-code-impact.

## Required checks

- `git diff --check`
- `rg -n "AgentDefinition|PromptDocument|SkillDocument|ReferenceDocument|ToolPermissionBoundary|PromptAssemblyTrace|SkillLoadTrace|AgentWorkTrace|provider|fail-closed|source-alignment" app-description/domains/core-starter/workstreams/agent-admin`

## Done criteria

- Agent Admin has coherent worker, surface, agent, tool, policy, trace, test, realization, lifecycle, and source-alignment coverage.
- Queue is updated and committed.

## Vertical workstream contract

- Lifecycle / readiness target: description-ready for Agent Admin; implementation alignment updated honestly.
- Workstream / functional agent: `agent-admin` / Agent Admin functional agent.
- Governed-tool id and exposure: managed agent behavior/profile/prompt/skill/reference/manifest/tool-boundary/test-console tools described with adapters.
- Capability id: managed-agent governance and managed-agent behavior state.
- AuthContext / roles / tenant scope: SaaS admin scope, provider secret boundary, tenant isolation, denials.
- Akka substrate: app-description only; map existing substrates in realization files.
- Audit/work trace requirements: prompt assembly, skill/reference loads, tool-boundary denial, behavior edit, provider fail-closed, agent work traces.
- Local validation path: `git diff --check`; runtime-validation scenario references only.
