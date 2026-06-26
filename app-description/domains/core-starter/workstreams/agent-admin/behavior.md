# Behavior: Agent Admin

## Current-state behavior

Agent Admin supports AI-assisted governance of versioned agent behavior artifacts for all agents. Behavior artifacts include each agent's `AgentDefinition` profile summary, required prompt, zero or more skills, zero or more governed references, compact skill/reference manifests, model-config references, and tool-boundary summaries. Agent Admin edits behavior documents and safe profile metadata; it does not grant runtime authority through document text.

## Entry behavior

The workstream persists previous surfaces. There is no forced default surface. A first-time or cleared workstream may be blank. Users can use explicit controls such as Show dashboard, Show agents, and Clear workstream.

## Agent list and detail behavior

The agent list may be filtered by agent name, workstream/domain, placement (`functional_context_area` or `internal_worker`), lifecycle status, steward, and authority level when those fields are implemented. Each row shows agent name, short purpose, placement, lifecycle status, safe model alias summary, and last behavior change time. Opening an agent shows agent name, editable purpose, steward/owner summary, lifecycle/authority summary, safe `ModelConfigRef` and `ToolPermissionBoundary` summaries, prompt link, clickable skill list, reference manifest entries, and trace entry points. Each skill shows its name/description and clickable reference docs or governed references.

Agent names and purposes are editable. Whole agents cannot be created or deleted in Agent Admin. Lifecycle, model-config, and tool-boundary changes are visible for inspection/explanation only unless a later scoped Governance/Policy or Agent Admin task defines protected change flows and approval gates.

## Editing behavior

Editing is mediated by one editing agent with doc-type-specific skills for prompt editing, skill editing, governed reference editing, and behavior-change risk classification. Users provide free-form instructions. The editing agent reads the current artifact and relevant context from the same agent, preserves Markdown and existing structure unless asked to reorganize, and returns a structured behavior-change proposal containing proposed full document content, proposed diff, summary, rationale, risk classification, authority-expansion flags, and suggested tests/replay evidence.

The editing agent may ask clarifying questions before proposing changes. If a request is unsafe, authority-expanding, or outside scope, it must explain the refusal or review requirement and propose a safer alternative. Prompt/skill/reference text that claims new tools, broader tenant/customer scope, approval authority, model authority, or side-effect permission is treated as blocked or high-risk proposal material; backend authorization and `ToolPermissionBoundary` remain authoritative.

The user may continue giving instructions to refine the proposed content. The editing session ends with Save draft/proposal, Activate/Commit, Review/Decision-card routing, or Cancel. Save draft/proposal creates an immutable non-active draft/proposal record. Activate/Commit is a separate protected backend action; for low-risk copy/clarity changes, the same authorized SaaS admin may review and activate immediately as the foundation simplification. Medium/high-risk or authority-expanding changes cannot be activated directly from the editor. Cancel discards the proposal and returns to the current version. Cancelled edit sessions are audited but are not retained in user-facing version history.

## Version behavior

Versions use simple integer numbers. Each draft/proposed/active version records created time, proposed-by, reviewed-by/activated-by when applicable, content checksum/body reference, risk classification, authority-expansion flags, and the whole editing-session transcript/summary including all user instructions. Version history rows show version number and lifecycle status. Historical version views show content, metadata, edit request/transcript summary, optional diff to the immediate predecessor, and a read-only banner.

Edit input is enabled only on the current/latest editable draft or active document. Historical versions are read-only. Restore this version creates a restore proposal copied from the historical content and records `Restored from version N` as the edit request; activation of that restore proposal creates a new current active version. Restore-created versions appear in history.

## Skill/reference lifecycle

SaaS admins may create, update, deprecate, and delete skills through governed proposal flows. Create skill captures skill name, editable purpose/description, compact manifest hint, and a free-form request for initial content drafted by the editing agent. Delete skill requires confirmation naming the skill, stating deletion is permanent or deprecated according to configured lifecycle policy, and listing/counting references and manifest assignments affected. Deleted skills cannot be restored unless the implementation chooses deprecation over physical deletion.

SaaS admins may create, update, deprecate, and delete governed references associated with an agent/skill reference manifest. Create reference captures title/name, short description/when-to-consult hint used by models for read-selection, access/redaction classification when applicable, and free-form content request handled by the editing agent. Delete reference is permanent or deprecated according to configured lifecycle policy and requires confirmation. Removing a skill must explicitly remove or reassign its references and manifest entries; it must not leave hidden orphaned loader access.

## Runtime behavior

Only activated versions update the current artifact used at runtime. Each agent request resolves the active `AgentDefinition`, lifecycle status, authority level, active `PromptDocument`/`PromptVersion`, compact `AgentSkillManifest`, compact `AgentReferenceManifest`, `ModelConfigRef`/model policy, selected `AuthContext`, and `ToolPermissionBoundary`. Prompt assembly includes the current prompt and compact assigned skill/reference names, descriptions, and when-to-use hints. Full skill/reference bodies load only through authorized `readSkill(skillId)` and `readReferenceDoc(referenceId)` calls. Agents only know about skills and references listed for themselves. Runtime prompt assembly, skill loads, reference loads, tool-boundary denials, model-policy denials, and work results are traced.
