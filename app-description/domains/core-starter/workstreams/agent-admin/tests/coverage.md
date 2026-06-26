# Tests: Agent Admin

## Acceptance

- Given a SaaS Owner/Admin, when they open Agent Admin with no persisted surface, then a blank workstream renders with Show dashboard, Show agents, and composer affordances.
- Given a SaaS Owner/Admin, when they open the dashboard, then it shows a clickable total-agent count and top five most recently changed agents, with no required needs-attention section.
- Given a SaaS Owner/Admin, when they filter the all-agent catalog by agent name, workstream/domain, placement, or scope provenance, then rows show generated agent name, short purpose, resolved profile scope, and last edit time and open agent detail.
- Given an agent detail, then it shows generated agent identity/provenance, purpose, placement, lifecycle status, steward, authority level, safe model alias summary, compact skill/reference manifest summaries, allowed generated tool list, safe tool-boundary summary, prompt link, skill list, nested governed references, skill-library entry point, skill assignment action, generated tool assignment action, create/delete/deprecate skill/reference actions, behavior-profile history, proposal review entry points, and trace entry points; it does not expose whole-agent create/delete, generated tool code editing, provider secrets, raw model-setting mutation, or direct tool-boundary implementation mutation.
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
- Given create skill, then skill name, purpose/description, compact manifest hint, and editing-agent-drafted content create a non-active tenant-scoped skill-library proposal; activation creates the skill and first active version independent of any specific agent.
- Given assign/unassign skill, then activation creates a new target agent behavior-profile version and does not change the skill document version.
- Given assign/unassign generated tool, then activation creates a new target agent behavior-profile version, does not create/edit/delete tool code, and runtime still enforces backend authorization and tool boundaries.
- Given delete/deprecate skill, then confirmation names the skill, defaults to deprecation unless lifecycle policy permits hard deletion, lists/counts assigned agents, references, and manifest assignments, and confirm performs the configured lifecycle action with no hidden loader access.
- Given create reference doc, then name, short description/when-to-consult hint, optional access/redaction summary, and editing-agent-drafted content create a non-active reference proposal; activation creates the reference and first active version.
- Given delete/deprecate reference doc, then confirmation performs the configured lifecycle action with no hidden manifest access.

## Security and negative

- Non-SaaS-admin callers, tenant/org admins, customer admins, tenant employees, customer users, disabled users, inactive users, and unauthenticated callers are denied.
- Unauthorized `PromptDocument` reads, unassigned skill denial, disabled-agent denial, cross-tenant/customer access, inactive/deleted document access, and `ToolPermissionBoundary` denial return safe system-message recovery without hidden document enumeration.
- Agent Admin does not create/delete whole agents, create/edit/delete generated tool code, manage provider secrets, mutate raw model settings beyond approved model config reference selection, bypass backend authorization, or activate changes without protected review/activation checks.
- Unsafe or out-of-scope edit requests, including authority expansion attempts through prompt/skill/reference text, produce an explanation and safer alternative or review/decision-card route; authority-expanding proposals cannot be activated directly from the editor.
- Historical versions cannot be directly edited.
- Stale current-version saves or activations are rejected or recovered by backend consistency checks.

## Runtime and observability

- Each agent request resolves the tenant-specific active behavior profile when present, otherwise the global active behavior profile, then lifecycle status, authority level, current active `PromptDocument`/`PromptVersion`, compact `AgentSkillManifest`, compact `AgentReferenceManifest`, `ModelConfigRef`/model policy, selected `AuthContext`, allowed generated tool list, and `ToolPermissionBoundary`, then loads only the current prompt plus assigned skill/reference names/descriptions/hints.
- Agents can call `readSkill(skillId)` and `readReferenceDoc(referenceId)` only for authorized assigned documents and can call only generated tools allowed by the resolved profile and backend boundary; runtime profile resolution and prompt assembly emit trace facts, skill loads emit `SkillLoadTrace`, reference loads emit reference-load trace facts, model policy decisions emit safe trace facts, generated-tool assignment decisions emit trace facts, and model/tool work emits `AgentWorkTrace`.
- Agent Admin trace surfaces show agent name, resolved profile scope/version, prompt/skill/reference doc read, generated tool decision where applicable, version/checksum where allowed, safe model alias, tool-boundary decision category, timestamp, request/session id, and tenant/customer/user context, filterable by agent, doc/tool, decision, and time range.
- Trace rows do not show full skill/reference content.
- Every edit session audit includes user input, editing-agent structured proposal output, saved/proposed/activated version content where allowed, Save Draft/Review/Approve/Reject/Activate/Cancel outcome, timestamps, actors, risk classification, authority-expansion flags, and decision-card links when applicable.

## UX and formatting

- Prompt, skill, and reference doc content supports Markdown.
- `AgentBehaviorEditorAgent` preserves existing Markdown and structure unless the user requests reorganization.
- Users may Save without opening the diff.
- No live-update behavior is required for concurrent edit sessions; backend consistency is authoritative.
- Provider-secret boundary tests prove model/provider keys, WorkOS/Resend secrets, hidden platform instructions, generated tool implementation internals beyond safe summaries, and unapproved tool-boundary internals never appear in browser payloads, prompts, skills, references, or trace views.
