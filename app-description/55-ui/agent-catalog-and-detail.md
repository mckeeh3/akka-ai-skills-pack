# Agent Catalog and Detail UI

## Agent Catalog

- route: `/ui/agents`
- user goal: find and govern tenant/customer-scoped `AgentDefinition` records before they perform work
- required data:
  - agent id/name, status (`draft`, `active`, `disabled`, `archived`), owner/steward, tenant/customer scope, authority level, model reference, active `PromptDocument`/`PromptVersion`, `AgentSkillManifest`, `ToolPermissionBoundary`, and recent trace summary
- actions:
  - create draft agent definition
  - filter by status, scope, steward, authority level, manifest, prompt, skill, disabled state, and recent denied `readSkill` events
  - open agent detail
- access rules:
  - list/search is tenant/customer scoped
  - auditors may read metadata and traces according to audit permissions
  - hidden/cross-tenant agents use not-found or forbidden-safe responses

## Agent Detail

- route: `/ui/agents/:agentId`
- user goal: understand and safely manage one agent's effective behavior and authority
- required regions:
  - lifecycle status and disabled-agent denial state
  - effective `AgentDefinition` metadata and version
  - active prompt reference and prompt assembly preview link
  - active skill manifest with compact skill hints and unassigned skill denial indicators
  - active tool permission boundary with scoped tool/data grants and policy citations
  - authority summary showing what the agent may draft, recommend, read, or execute
  - recent `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace` links
  - decision cards and approval state for pending behavior or authority changes
- actions:
  - disable/reactivate agent when permitted
  - request behavior edit through `AgentBehaviorEditorAgent`
  - propose manifest or tool-boundary change
  - open trace explorer filtered to the agent
- denial behavior:
  - disabled agents cannot be invoked or load skills
  - activation/reactivation is blocked when prompt, skill, manifest, or tool-boundary references are unapproved, stale, or missing
