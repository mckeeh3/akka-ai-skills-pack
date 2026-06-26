# Realization: Frontend routes and surfaces for Agent Admin

Capability: `agent-doc-administration`.

## Frontend intent

Agent Admin frontend realization should prioritize AI-assisted all-agent behavior-profile and behavior-document proposal, review, and activation over generic governance-console workflows. Agents and tools are static/code-generated from app-description; the frontend manages runtime behavior profiles, not generated code.

## Surface / route concerns

| Surface / route concern | Expected frontend realization |
|---|---|
| Blank workstream | Persisted workstream surface area can be blank; controls expose Show dashboard, Show agents, Clear workstream, and composer. |
| Optional dashboard | Shows clickable total-agent count, proposal/review counts when implemented, and top five recently changed agents. No default operational needs-attention queue. |
| Agent list | Filter all generated agents by agent name, workstream/domain, placement, lifecycle status, steward, authority level, and scope provenance; rows show name, short purpose, placement, lifecycle status, safe model alias summary, resolved profile scope, last behavior change time; row opens agent detail. |
| Agent detail | Generated agent identity/provenance, purpose, safe behavior-profile summary, resolved scope, prompt link, skill list, generated tool list, nested governed references, manifest summaries, safe tool-boundary/model summaries, profile history, proposal review entry points, create/delete/deprecate skill/reference actions, skill assignment action, generated tool assignment action, runtime trace entry points. |
| Agent behavior profile | Versioned model config reference, prompt version, skill assignments, generated tool assignments, scope/provenance, clone-from-global behavior, restore proposal. |
| Prompt doc editor | Current active Markdown content, version/status metadata, version history, edit request input only on current/latest editable artifact, historical read-only view, restore proposal. |
| Skill library | Tenant-scoped skill catalog independent of any single agent, with create/deprecate/remove actions and assigned-agent counts. |
| Skill doc editor | Skill name/purpose, compact manifest hint, Markdown content, governed references, version/history/diff, current/latest edit input, restore proposal. |
| Skill assignment | Assign/unassign active skills for a generated agent; activation creates a behavior-profile version and does not alter skill document versions. |
| Generated tool assignment | Assign/unassign app-description/code-generated tools for a generated agent; activation creates a behavior-profile version and does not create/edit/delete tool code. |
| Governed reference doc editor | Name/title, short description and when-to-consult hint, optional access/redaction summary, Markdown content, version/history/diff, current/latest edit input, restore proposal. |
| Editing session | Free-form instructions, editing-agent proposed full document, structured proposal id, summary/rationale, risk classification, authority-expansion flags, suggested tests/replay evidence, Show diff toggle, refinement input, Save Draft, Cancel. |
| Proposal review | Proposed diff/content, risk/authority flags, suggested tests, approve/reject/activate/route-to-decision-card/request-changes actions, stale/forbidden recovery. |
| Create/delete skill | Create uses name, purpose/description, compact manifest hint, editing-agent drafted content, then activation in the tenant-scoped skill library. Delete/deprecate defaults to deprecation and confirmation lists assigned-agent/reference/manifest effects. |
| Create/delete reference doc | Create uses name/title, short description/when-to-consult hint, editing-agent drafted content, then activation. Delete/deprecate confirmation lists manifest effects. |
| Runtime traces | Trace metadata filterable by agent, profile scope/version, doc/tool, decision, and time range; visible from agent detail, doc pages, and separate trace surface. |

## Removed/de-emphasized frontend concerns

The Agent Admin default UX should not center provider-secret settings, generated tool code editing, backend tool-boundary implementation editing, seed import, whole-agent activation/deactivation, or rollback plumbing. Behavior proposal review is now authoritative for model config reference, prompt, skill, skill assignment, generated tool assignment, and reference changes; old direct-save components are not authoritative unless reconciled to the proposal/activation flow.

## Validation evidence to update

Existing frontend contract tests for Agent Admin should be reconciled to cover the new surface inventory, all-agent catalog, safe profile summaries, global-to-tenant scope provenance, current/latest edit input, simple integer version history, behavior-profile versions for model config/prompt/skill/tool assignment changes, version-to-previous diff behavior, restore proposals, editing-session Save Draft/Cancel, proposal review/activation/rejection, skill/reference deletion or deprecation, SaaS-admin-only access, provider-secret/tool-code boundary, and runtime profile/read/tool trace metadata.
