# Build Backlog 04: Agent Governance and Runtime Foundation

## Items

1. Implement governed AgentDefinition, PromptDocument/PromptVersion, SkillDocument/SkillVersion, AgentSkillManifest, ToolPermissionBoundary, PromptAssemblyTrace, SkillLoadTrace, and AgentWorkTrace components/views.
2. Implement first-install/tenant-bootstrap seed import for default agent behavior documents with checksums, provenance, idempotency, and upgrade semantics.
3. Implement deterministic prompt assembly and authorized `readSkill(skillId)` capability.
4. Implement behavior editing proposal/review/activation flow and bounded admin agents or equivalent deterministic stubs where model access is not required.
5. Wire Agent Admin and Governance/Policy workstream surfaces to real backend capabilities.
6. Add tests for disabled agents, unauthorized prompt/skill/tool-boundary change, unassigned skill denial, authority expansion denial, trace creation, and seed idempotency.

## Completion signal

The starter app demonstrates governed runtime agent behavior as executable foundation code rather than doctrine-only guidance.
