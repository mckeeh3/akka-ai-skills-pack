# Rules: Agent Authority

- agents operate only within a tenant and goal/plan/task context
- every runtime agent invocation must resolve an active `AgentDefinition`, approved `PromptDocument`/`PromptVersion`, approved `AgentSkillManifest`, and approved `ToolPermissionBoundary`
- prompt and skill content are behavior guidance only; they cannot grant role, tenant, data, tool, or approval authority
- full skill text loads require authorized `readSkill(skillId)` and produce `SkillLoadTrace`; unassigned skills are denied
- agents cannot bypass role, permission, policy, or approval gates
- external side effects require an explicit permitted tool and policy clearance
- low-confidence or high-risk outputs must create or update a decision card
- agent-generated policy changes remain proposals until human approval
- all material agent work records trace events
- prompt assembly records `PromptAssemblyTrace`, and consequential recommendations, tool/data access, denials, decisions, approvals, and outcomes record `AgentWorkTrace`
- disabled agents cannot assemble prompts, load skills, call tools, or perform work
- `AgentBehaviorEditorAgent` may draft prompt/skill/manifest/tool-boundary proposed diffs, but activation and authority expansion require review/approval
