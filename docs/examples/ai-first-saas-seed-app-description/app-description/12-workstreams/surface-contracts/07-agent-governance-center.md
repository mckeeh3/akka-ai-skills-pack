# Surface Contract: Agent Governance Center

- surface-id: `agent-governance-center`
- type/version: catalog+diff+approval/v1
- functional agents: Agent Admin, Governance/Policy
- payload schema:
  - AgentDefinition list/detail, prompt/skill versions, skill manifests, tool boundaries, proposed diffs, approval status, prompt assembly preview, trace references
- allowed actions:
  - create/draft/update AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, ToolPermissionBoundary → `managed-agent-foundation`
  - run prompt/skill/manifest/tool-boundary test → `managed-agent-foundation`
  - approve/activate/rollback behavior version → `managed-agent-foundation`, `governance-decisions-audit`
  - invoke `readSkill(skillId)` and `readReferenceDoc(referenceId)` test consoles → `managed-agent-foundation`
- states:
  - no active agent, disabled agent, draft pending review, approval required, unauthorized authority expansion, skill not assigned, trace unavailable
- auth/security:
  - prompt and skill text cannot grant authority; tool/data access comes only from backend-enforced permission boundary.
- rendering tests:
  - proposed diff review, unassigned skill denial, unassigned reference denial, missing `read_skill` / `read_reference` boundary denial, authority-expansion denial, activation approval, trace-link rendering.
