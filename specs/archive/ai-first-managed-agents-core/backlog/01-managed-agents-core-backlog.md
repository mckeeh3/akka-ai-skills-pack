# Backlog: AI-first Managed Agents Core

## Runtime tooling

- Add backend-owned tool catalog entries with stable tool ids, capability ids, categories, side-effect classification, and implementation bindings.
- Add `AgentRuntimeToolResolver` that converts active `ToolPermissionBoundary` grants into a `List<Object>` acceptable to `effects().tools(...)`.
- Add request-scoped `AgentRuntimeTools` with `@FunctionTool` methods for `readSkill(skillId)` and `readReferenceDoc(referenceId)`.
- Update `WorkstreamRuntimeAgent` so it resolves and registers runtime tools before model invocation.
- Add tests proving model-invoked `readSkill`/`readReferenceDoc` works through the real Akka Agent tool loop.

## Durable core state

- Add first-class `AgentDefinitionEntity` lifecycle/profile component and views.
- Add prompt, skill, and reference document/version components or snapshots.
- Add manifest components for `AgentSkillManifest` and `AgentReferenceManifest`.
- Add `ToolPermissionBoundaryEntity` and tool catalog views.
- Add durable trace storage and trace search/detail views.

## Core seeded agents

- Seed managed runtime profiles for My Account, User Admin, Audit/Trace, Governance/Policy, and Agent Admin.
- Ensure each seeded agent has a distinct prompt and manifest, not a shared generic profile.
- Ensure Agent Admin is itself configuration-driven before it manages other agents.

## Docs and gates

- Update starter README and validation scripts.
- Update core doctrine and routing skills to name AI-first managed agents as a primary core app feature alongside workstreams and surfaces.
- Add completion gates preventing ad hoc static-prompt agents in generated AI-first SaaS apps.
