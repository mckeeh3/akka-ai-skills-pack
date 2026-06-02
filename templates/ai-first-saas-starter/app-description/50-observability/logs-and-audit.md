# Logs and Audit

- structured logs include correlation id, tenant id where available, actor id/agent id, component, action, and outcome
- audit events are durable for security-relevant and consequential business actions
- audit must cover user/admin actions, agent actions, policy invocations, approvals, tool/data access, timed actions, AgentDefinition lifecycle changes, PromptDocument/PromptVersion changes, SkillDocument/SkillVersion changes, AgentSkillManifest assignments, ToolPermissionBoundary changes, AgentBehaviorEditorAgent proposals, and authorized/denied readSkill requests
- logs must not be the only source of audit truth
- PromptAssemblyTrace, SkillLoadTrace, and AgentWorkTrace are durable evidence for runtime prompt assembly, skill-load decisions, tool/data access, decisions, denials, approvals, and consequential recommendations
