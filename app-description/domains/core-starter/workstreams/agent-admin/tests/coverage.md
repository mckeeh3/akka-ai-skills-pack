# Tests: Agent Admin

## Acceptance

- Given a SaaS Owner/Admin, when they open Agent Admin with no persisted surface, then a blank workstream renders with Show dashboard, Show agents, and composer affordances.
- Given a SaaS Owner/Admin, when they open the dashboard, then it shows a clickable total-agent count and top five most recently changed agents, with no required needs-attention section.
- Given a SaaS Owner/Admin, when they filter the agent catalog by agent name or workstream/domain, then rows show agent name, short purpose, and last edit time and open agent detail.
- Given an agent detail, then it shows editable agent name/purpose, placement, lifecycle status, steward, authority level, safe model alias summary, compact skill/reference manifest summaries, safe tool-boundary summary, prompt link, skill list, nested governed references, create/delete/deprecate skill/reference actions, proposal review entry points, and trace entry points; it does not expose whole-agent create/delete, provider secrets, direct model-setting mutation, or direct tool-boundary mutation.
- Given a `PromptDocument`, `SkillDocument`, or `ReferenceDocument` current editable version, then edit input is enabled and accepts free-form instructions.
- Given a historical version, then content, metadata, edit request/transcript summary, optional diff, and restore action are visible, edit input is disabled, and a read-only banner is shown.
- Given version `N`, when Show diff is requested, then the diff compares only `N` to `N-1`; version 1 has no-prior-version behavior.
- Given a free-form edit request, when the editing agent proposes a change, then the surface shows full proposed Markdown content, structured proposal id, summary/rationale, risk classification, authority-expansion flags, suggested tests/replay evidence, advisory warnings/risks, additional input, Save Draft, Cancel, and Show diff.
- Given further user instructions, when the editing agent revises the proposal, then the transcript retains all user instructions.
- Given Save Draft, then a non-active immutable proposal version is created and runtime reads remain on the active version.
- Given an approved low-risk proposal and an authorized SaaS admin, when Activate is confirmed, then a new current active immutable version is created and runtime reads use it.
- Given a medium/high-risk or authority-expanding proposal, when activation is attempted directly, then the system routes to review/decision card or denies activation and leaves active behavior unchanged.
- Given Reject, then active behavior remains unchanged and rejection rationale is audited.
- Given Cancel, then no active version is created and the current active version is shown.
- Given Restore this version, then a restore proposal is created with content copied from the historical version and edit request `Restored from version N`; activation creates the active restore version.
- Given create skill, then skill name, purpose/description, compact manifest hint, and editing-agent-drafted content create a non-active skill proposal; activation creates the skill and first active version.
- Given delete/deprecate skill, then confirmation names the skill, states permanence/deprecation policy, lists/counts references and manifest assignments, and confirm performs the configured lifecycle action with no hidden loader access.
- Given create reference doc, then name, short description/when-to-consult hint, optional access/redaction summary, and editing-agent-drafted content create a non-active reference proposal; activation creates the reference and first active version.
- Given delete/deprecate reference doc, then confirmation performs the configured lifecycle action with no hidden manifest access.

## Security and negative

- Non-SaaS-admin callers, tenant/org admins, customer admins, tenant employees, customer users, disabled users, inactive users, and unauthenticated callers are denied.
- Unauthorized `PromptDocument` reads, unassigned skill denial, disabled-agent denial, cross-tenant/customer access, inactive/deleted document access, and `ToolPermissionBoundary` denial return safe system-message recovery without hidden document enumeration.
- Agent Admin does not create/delete whole agents, manage provider secrets, directly mutate model settings, grant tool permissions, bypass backend authorization, or activate changes without protected review/activation checks.
- Unsafe or out-of-scope edit requests, including authority expansion attempts through prompt/skill/reference text, produce an explanation and safer alternative or review/decision-card route; authority-expanding proposals cannot be activated directly from the editor.
- Historical versions cannot be directly edited.
- Stale current-version saves or activations are rejected or recovered by backend consistency checks.

## Runtime and observability

- Each agent request resolves the active `AgentDefinition`, lifecycle status, authority level, current active `PromptDocument`/`PromptVersion`, compact `AgentSkillManifest`, compact `AgentReferenceManifest`, `ModelConfigRef`/model policy, selected `AuthContext`, and `ToolPermissionBoundary`, then loads only the current prompt plus assigned skill/reference names/descriptions/hints.
- Agents can call `readSkill(skillId)` and `readReferenceDoc(referenceId)` only for authorized assigned documents; runtime prompt assembly emits `PromptAssemblyTrace`, skill loads emit `SkillLoadTrace`, reference loads emit reference-load trace facts, model policy decisions emit safe trace facts, and model/tool work emits `AgentWorkTrace`.
- Agent Admin trace surfaces show agent name, prompt/skill/reference doc read, version/checksum where allowed, safe model alias, tool-boundary decision category, timestamp, request/session id, and tenant/customer/user context, filterable by agent, doc, decision, and time range.
- Trace rows do not show full skill/reference content.
- Every edit session audit includes user input, editing-agent structured proposal output, saved/proposed/activated version content where allowed, Save Draft/Review/Approve/Reject/Activate/Cancel outcome, timestamps, actors, risk classification, authority-expansion flags, and decision-card links when applicable.

## UX and formatting

- Prompt, skill, and reference doc content supports Markdown.
- `AgentBehaviorEditorAgent` preserves existing Markdown and structure unless the user requests reorganization.
- Users may Save without opening the diff.
- No live-update behavior is required for concurrent edit sessions; backend consistency is authoritative.
- Provider-secret boundary tests prove model/provider keys, WorkOS/Resend secrets, hidden platform instructions, and unapproved tool-boundary internals never appear in browser payloads, prompts, skills, references, or trace views.
