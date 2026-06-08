# Build Backlog 03: Agent Admin and Hybrid Runtime

## Items

1. Inventory governed-agent executable/reference coverage and confirm remaining code-level gaps.
2. Define production-shaped Akka component contracts for AgentDefinition, PromptDocument/PromptVersion, SkillDocument/SkillVersion, AgentSkillManifest, ToolPermissionBoundary, ModelConfigRef, and traces.
3. Define full Agent Admin APIs and workstream surface contracts.
4. Harden the hybrid Akka runtime implementation contract: AgentRuntimeResolver, prompt assembly, compact manifest, authorized `readSkill`, tool-boundary checks, Java Agent invocation, and trace emission.
5. Add or queue missing governed model config and side-effecting/component/MCP tool-boundary examples.

## Completion signal

A future harness can implement Agent Admin and managed runtime invocation without guessing where static Java Agent code ends and governed tenant behavior begins.
