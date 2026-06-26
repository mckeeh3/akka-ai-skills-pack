# Capability: Agent doc administration

## Purpose

Allow SaaS Owner/Admin users to improve governed managed-agent behavior by inspecting safe agent behavior-profile summaries and reviewing/activating AI-assisted changes to versioned behavior artifacts. Artifacts include each agent's `AgentDefinition`, `PromptDocument`, assigned `SkillDocument` records, governed `ReferenceDocument` records, compact `AgentSkillManifest` and `AgentReferenceManifest` entries, safe `ModelConfigRef` summaries, `ToolPermissionBoundary` summaries, and related runtime loading traces.

## Actors and scope

- SaaS Owner/Admin: may view all agents through the agent catalog, view full prompt/skill/reference docs, inspect safe behavior-profile references, create/update/deprecate/delete skills, create/update/deprecate/delete governed references, edit agent names and purposes, propose restores from historical doc versions, review/activate permitted low-risk drafts, route higher-risk proposals to review/decision cards, and inspect Agent Admin audit/read traces.
- Tenant/organization/customer admins: not Agent Admin operators.
- `AgentBehaviorEditorAgent` (editing-agent): interprets authorized SaaS admin edit requests, reads the relevant current artifact and same-agent context, drafts Markdown-preserving structured `BehaviorChangeProposal` records, summarizes risks/warnings, classifies authority expansion, suggests tests/replay evidence, asks clarifying questions when needed, and returns proposed content for human review. It never directly activates runtime behavior.

Agent Admin applies to all agents in the app, including foundation agents and future business/domain-specific agents. It does not create or delete whole agents.

## Agent document model

- Each governed managed agent is represented by an `AgentDefinition` behavior profile with placement (`functional_context_area` or `internal_worker`), lifecycle status, owner/steward, authority level, active `PromptDocument`/`PromptVersion`, active `AgentSkillManifest`, active `AgentReferenceManifest`, `ModelConfigRef`, policy refs, trace requirements, and `ToolPermissionBoundary` refs.
- Every agent may have zero or more `SkillDocument`/`SkillVersion` records assigned through its `AgentSkillManifest`.
- Every skill may have zero or more governed references associated through `AgentReferenceManifest` entries; the UI may group references under the skill that introduced them.
- Reference docs are governed references with stable ids, titles, short summaries, when-to-consult hints, and optional access/redaction classification; they are not arbitrary filesystem paths or hidden prompt text.
- Agent docs support Markdown, and the `AgentBehaviorEditorAgent` must preserve existing Markdown structure unless the user explicitly asks to reorganize it.
- Agent docs, manifests, `ModelConfigRef` references, and `ToolPermissionBoundary` records are initially created by governed setup from implementation defaults with provenance. SaaS admins may update prompt docs, create/update/deprecate/delete skills, create/update/deprecate/delete references, and update manifest membership only through Agent Admin authorization, proposal, review, activation, and trace checks.

## Versioning

- Each saved prompt, skill, or reference edit creates an immutable draft/proposal version; activation is a separate protected action that makes an approved/reviewed version current.
- Version numbers are simple integers.
- All versions are retained except where a whole skill or reference doc is permanently deleted by configured lifecycle policy.
- Each version records version number, status, created/proposed time, proposed-by actor, reviewer/activator where applicable, saved content/checksum, risk classification, authority-expansion flags, and the whole editing-session transcript/summary including all user instructions.
- Users can browse version history. A history row only needs to show the version number.
- Historical versions are read-only. Edit request input is enabled only on the current/latest editable draft or active document.
- A requested diff for version `N` compares only version `N` with version `N-1`.
- `Restore this version` creates a restore proposal whose content is copied from the selected historical version and whose edit request is `Restored from version N`; activation of that proposal creates the new current active version.

## Editing flow

1. User opens Agent Admin, optionally opens the dashboard, then shows the agent catalog/list.
2. User filters by agent name or workstream/domain and opens an agent detail.
3. User opens the prompt, a skill, or a governed reference associated with the agent/skill manifest.
4. On the current/latest version, user enters free-form instructions for improving behavior.
5. The editing agent may ask clarifying questions or propose a safer alternative for unsafe/out-of-scope requests.
6. The editing agent returns proposed full document content, a diff summary, rationale, risk classification, authority-expansion flags, suggested tests/replay evidence, and advisory warnings/risks.
7. The user may continue providing input to refine the proposal.
8. The session ends with Save draft/proposal, Activate/Commit when allowed, Review/Decision-card routing, or Cancel.
9. Save draft/proposal creates a non-active immutable proposal; activation is a separate backend-authorized action. Cancel discards the proposal and shows the current active version.

Diff viewing is on demand through a `Show diff` style toggle or action. Users may save a draft without opening the diff. Low-risk warnings are advisory for authorized SaaS admins, but authority expansion, model/tool-boundary changes, high-risk behavior changes, secret-like content, or cross-scope requests are blocked or routed to decision-card/review rather than activated directly.

## Skill and reference lifecycle

- Create skill inputs: skill name, editable purpose/description, compact manifest hint, and free-form content request handled by the editing agent.
- Delete/deprecate skill requires confirmation naming the skill, stating permanence/deprecation policy, and listing/counting references and manifest assignments that will be removed, deprecated, or reassigned.
- Permanently deleted skills cannot be restored.
- Create governed reference inputs: title/name, short description and when-to-consult hint used by the model to decide whether to read it, optional access/redaction classification, and free-form content request handled by the editing agent.
- Delete/deprecate reference requires confirmation and must remove manifest access.
- Removing a skill must explicitly remove, deprecate, or reassign references associated with that skill; no hidden loader access remains.

## Runtime document loading

Each time an agent handles a request, it resolves the active `AgentDefinition`, lifecycle status, authority level, current active `PromptDocument`/`PromptVersion`, compact `AgentSkillManifest`, compact `AgentReferenceManifest`, `ModelConfigRef`/model policy, selected `AuthContext`, and `ToolPermissionBoundary`. The model sees only compact assigned skill and reference names/descriptions/hints until it calls `readSkill(skillId)` or `readReferenceDoc(referenceId)`. Agents only know about skills/references listed for themselves; there is no discovery path for other agents' skills or references. Runtime loading emits `PromptAssemblyTrace`, `SkillLoadTrace`, reference-load trace facts, and `AgentWorkTrace`, including safe denials for unauthorized `PromptDocument` access, unassigned skill/reference denial, disabled/archived-agent denial, inactive/deleted docs, inactive/denied model config, and tool-boundary denial.

## Out of scope

- Tenant/organization/customer-scoped Agent Admin.
- Creating or deleting whole agents.
- Provider-secret administration or direct model setting mutation.
- Tool permission administration beyond viewing/explaining `ToolPermissionBoundary` effects and preserving manifest assignments during skill/reference edits.
- Whole-agent lifecycle activation/disable/archive controls beyond safe profile inspection.
- Using prompt/skill/reference text to expand authority, tools, model policy, approval rights, or tenant/customer scope.
