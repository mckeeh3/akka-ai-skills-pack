# Capability: Agent doc administration

## Purpose

Allow SaaS Owner/Admin users to improve governed managed-agent behavior for all app agents by inspecting safe generated-agent behavior-profile summaries and reviewing/activating AI-assisted changes to versioned behavior artifacts. Agents and tools are static/code-generated from app-description. Artifacts include each agent's global or tenant-scoped `AgentDefinition` behavior profile, `PromptDocument`, independently managed tenant-scoped `SkillDocument` records, governed `ReferenceDocument` records, compact `AgentSkillManifest` and `AgentReferenceManifest` entries, safe `ModelConfigRef` summaries, allowed generated tool lists, `ToolPermissionBoundary` summaries, and related runtime loading traces.

## Canonical capability posture

This file retains the legacy capability artifact id `agent-doc-administration` for source-alignment compatibility. Current shared intent treats the capability scope as **managed-agent governance**: Agent Admin governs existing generated managed-agent behavior profiles and governed documents, not whole-agent creation/deletion or generated tool code.

Canonical governed-tool ids are the artifact-scoped ids used by `../workstreams/agent-admin/tools/governed-tools.md`, including `agent-definition.catalog.read`, `agent-definition.detail.read`, `agent-behavior-profile.history.read`, `agent-behavior-profile.proposal.create`, `agent-behavior-profile.version.activate`, `prompt-document.read`, `prompt-document.proposal.create`, `prompt-version.activate`, `skill-document.catalog.read`, `skill-document.proposal.create`, `skill-version.activate`, `reference-document.catalog.read`, `reference-document.proposal.create`, `reference-version.activate`, `agent-skill-manifest.assign`, `agent-reference-manifest.assign`, `model-policy.select`, `tool-permission-boundary.assign`, `agent-test-console.run`, `agent-runtime-trace.read`, `readSkill`, and `readReferenceDoc`.

Legacy `*-agent-doc-*` ids such as `list-agent-doc-agents`, `read-agent-doc-agent`, `inspect-agent-runtime-profile`, `draft-agent-doc-edit`, `save-agent-doc-edit`, `activate-agent-doc-version`, `assign-agent-skills`, `assign-agent-generated-tools`, and `read-agent-doc-runtime-traces` are implementation/source-alignment aliases for those canonical tools. They must not be interpreted as duplicate governed-tool authority, direct-save authority, whole-agent lifecycle authority, provider-secret authority, generated-tool-code edit authority, or backend authorization bypass.

## Actors and scope

- SaaS Owner/Admin: may view all agents through the agent catalog, view full prompt/skill/reference docs, inspect safe behavior-profile references, update model config references, create/update/deprecate/remove skills, create/update/deprecate/delete governed references, assign/unassign independently managed skills, assign/unassign static generated tools, propose restores from historical doc/profile versions, review/activate permitted low-risk drafts, route higher-risk proposals to review/decision cards, and inspect Agent Admin audit/read traces. SaaS app owners operate in the reserved `saas-app-owner` tenant scope.
- Tenant/organization/customer admins: not Agent Admin operators.
- `AgentBehaviorEditorAgent` (editing-agent): interprets authorized SaaS admin edit requests, reads the relevant current artifact and same-agent context, drafts Markdown-preserving structured `BehaviorChangeProposal` records, summarizes risks/warnings, classifies authority expansion, suggests tests/replay evidence, asks clarifying questions when needed, and returns proposed content for human review. It never directly activates runtime behavior.

Agent Admin applies to all agents in the app, including functional/context-area agents, internal worker agents, evaluator agents, autonomous/background agents, system/foundation agents, and future business/domain-specific agents. It does not create or delete whole agents or create/edit/delete generated tool code.

## Agent document model

- Each governed managed agent is represented by an `AgentDefinition` behavior profile with placement (`functional_context_area`, `internal_worker`, evaluator, autonomous/background, system/foundation, or future generated placement), lifecycle status, owner/steward, authority level, active `PromptDocument`/`PromptVersion`, active `AgentSkillManifest`, active `AgentReferenceManifest`, `ModelConfigRef`, allowed generated tool list, policy refs, trace requirements, and `ToolPermissionBoundary` refs.
- Every agent may have zero or more independently managed `SkillDocument`/`SkillVersion` records assigned through its `AgentSkillManifest`. Skills are tenant-scoped library artifacts and are not owned by a specific agent.
- Every skill may have zero or more governed references associated through `AgentReferenceManifest` entries; the UI may group references under the skill that introduced them.
- Reference docs are governed references with stable ids, titles, short summaries, when-to-consult hints, and optional access/redaction classification; they are not arbitrary filesystem paths or hidden prompt text.
- Agent docs support Markdown, and the `AgentBehaviorEditorAgent` must preserve existing Markdown structure unless the user explicitly asks to reorganize it.
- Agent profiles, docs, manifests, `ModelConfigRef` references, allowed generated tool lists, and `ToolPermissionBoundary` records are initially created by governed setup from app-description/code-generated defaults with provenance. Agents initially have a global behavior profile. A tenant change to model config reference, prompt, skill assignment list, or generated tool assignment list clones/versions the global profile into that tenant scope; SaaS app owners use the reserved `saas-app-owner` tenant scope. SaaS admins may update prompt docs, create/update/deprecate/remove skills, create/update/deprecate/delete references, update skill assignments, and update generated tool assignments only through Agent Admin authorization, proposal, review, activation, and trace checks.

## Versioning

- Each saved prompt, skill, reference, or agent behavior-profile edit creates an immutable draft/proposal version; activation is a separate protected action that makes an approved/reviewed version current.
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

- Create skill inputs: skill name, editable purpose/description, compact manifest hint, and free-form content request handled by the editing agent. The new skill is created in the tenant-scoped skill library, not under a specific agent.
- Assigning or unassigning a skill creates a new version of the target agent behavior profile and does not change the skill document version.
- Delete/deprecate skill requires confirmation naming the skill, stating lifecycle policy, and listing/counting agent assignments, references, and manifest entries that will be removed, deprecated, or reassigned.
- Skill removal defaults to deprecation, especially when a skill has ever been assigned or used in traces. Permanently deleted skills cannot be restored.
- Create governed reference inputs: title/name, short description and when-to-consult hint used by the model to decide whether to read it, optional access/redaction classification, and free-form content request handled by the editing agent.
- Delete/deprecate reference requires confirmation and must remove manifest access.
- Removing a skill must explicitly remove, deprecate, or reassign references associated with that skill; no hidden loader access remains.

## Runtime document loading

Each time an agent handles a request, it resolves the tenant-specific active behavior profile when present, otherwise the global active profile, then resolves lifecycle status, authority level, current active `PromptDocument`/`PromptVersion`, compact `AgentSkillManifest`, compact `AgentReferenceManifest`, `ModelConfigRef`/model policy, selected `AuthContext`, allowed generated tool list, and `ToolPermissionBoundary`. The model sees only compact assigned skill and reference names/descriptions/hints until it calls `readSkill(skillId)` or `readReferenceDoc(referenceId)`, and it may only invoke generated tools allowed by the resolved profile and backend tool boundary. Agents only know about skills/references listed for themselves; there is no discovery path for other agents' skills or references. Runtime loading emits profile-resolution traces, `PromptAssemblyTrace`, `SkillLoadTrace`, reference-load trace facts, generated-tool assignment trace facts, and `AgentWorkTrace`, including safe denials for unauthorized `PromptDocument` access, unassigned skill/reference denial, unassigned generated tool denial, disabled/archived-agent denial, inactive/deleted docs, inactive/denied model config, and tool-boundary denial.

## Out of scope

- Creating or deleting whole agents.
- Creating, editing, or deleting generated tool code; tools are static/code-generated from app-description.
- Provider-secret administration or raw model setting mutation beyond selecting approved `ModelConfigRef` values for an agent behavior profile.
- Backend authorization changes or `ToolPermissionBoundary` implementation changes; Agent Admin may change the per-agent allowed generated tool list but cannot make tool code bypass backend checks.
- Whole-agent lifecycle activation/disable/archive controls beyond safe profile inspection unless later scoped.
- Using prompt/skill/reference text to expand authority, model policy, approval rights, or tenant/customer scope.

## Linked graph nodes

- Global workers: `../../../global/workers/foundation-workers.md`
- Global agents: `../../../global/agents/foundation-functional-agents.md`
- Global tools: `../../../global/tools/foundation-governed-tools.md`
- Data/state: `../data-state/managed-agent-behavior-state.md`
- Workstream: `../workstreams/agent-admin/workstream.md`
- Workers: `../workstreams/agent-admin/workers/`
- Agent binding: `../workstreams/agent-admin/agents/functional-agent.md`
- Tools: `../workstreams/agent-admin/tools/governed-tools.md`
- Surfaces: `../workstreams/agent-admin/surfaces/surfaces.md`
- Tests: `../workstreams/agent-admin/tests/coverage.md`
- Traces: `../workstreams/agent-admin/traces/work-traces.md`
