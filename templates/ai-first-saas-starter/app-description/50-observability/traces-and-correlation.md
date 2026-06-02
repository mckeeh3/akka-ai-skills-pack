# Traces and Correlation

- every user request and workflow execution has a correlation id
- workflow, entity command, consumer reaction, timed action, and agent invocation should preserve correlation where feasible
- UI error reports should expose safe request/correlation ids for support
- decision cards and audit traces link back to originating goal/plan/task/workflow ids
- `PromptAssemblyTrace` records active AgentDefinition, PromptDocument/PromptVersion, compact AgentSkillManifest, policy context, checksum, redaction marker, AuthContext, tenant/customer scope, and correlation id
- `SkillLoadTrace` records requested skillId, SkillDocument/SkillVersion, allowed or denied readSkill outcome, AgentSkillManifest reason, ToolPermissionBoundary reason, AuthContext, tenant/customer scope, and correlation id
- `AgentWorkTrace` records consequential recommendations, tool/data access, decisions, denials, approvals, outcome links, prompt assembly trace id, skill load trace ids, redaction marker, and correlation id
