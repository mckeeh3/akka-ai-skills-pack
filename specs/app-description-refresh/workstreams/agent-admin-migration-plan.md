# Agent Admin Workstream Migration Plan

## Scope

Refresh `app-description/domains/core-starter/workstreams/agent-admin/**` to the current skills-pack app-description graph contract.

## Primary intent

SaaS admins govern runtime-managed agent behavior through AgentDefinition, behavior profiles, PromptDocument, SkillDocument, ReferenceDocument, manifests, model policy, tool boundaries, behavior-editing proposals, safe test consoles, and work traces.

## Required graph coverage

- Workstream purpose and lifecycle/alignment state.
- SaaS admin human worker, Agent Admin functional agent, behavior-editor internal agent, agent-runtime system worker.
- Surfaces for agent catalog, agent detail, behavior profile, prompt/skill/reference governance, manifests, tool boundaries, model config summary, test console, behavior-edit proposal, approval/denial, and trace evidence.
- Governed tools for AgentDefinition lifecycle/profile, prompt governance, skill governance, reference governance, manifest assignment, model policy selection, tool-boundary changes, behavior-edit proposals, test console, and trace reads.
- Actor adapters: surface actions, confirmed human chat plans, agent tool calls where bounded, internal runtime/load calls, API calls.
- Capability links to managed-agent governance and managed-agent behavior state.
- Authorization, tenant scope, approval-required authority expansion, disabled-agent denial, provider missing fail-closed behavior, loader denial semantics, and frontend secret boundaries.
- Trace obligations: PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, behavior-edit audit, denied-load traces.
- Tests and runtime-validation scenarios for agent catalog/detail, safe behavior edit, manifest denial, tool-boundary denial, provider fail-closed, and trace visibility.
- Realization files and source-alignment entries.

## Specific refresh questions for the task

- Which governed document types are fully in current foundation scope vs deferred?
- Which test-console paths may invoke real providers vs fail-closed provider-missing behavior?
- How are behavior-editing proposals routed to approval/decision surfaces?

## Expected task output

The task should update only Agent Admin workstream files plus narrow shared references if required, then mark lifecycle/source-alignment to reflect description changes and implementation alignment.
