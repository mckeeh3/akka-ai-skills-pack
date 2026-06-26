# Realization: Frontend routes and surfaces for Agent Admin

Capability: `agent-doc-administration`.

## Frontend intent

Agent Admin frontend realization should prioritize AI-assisted behavior-document proposal, review, and activation over generic governance-console workflows.

## Surface / route concerns

| Surface / route concern | Expected frontend realization |
|---|---|
| Blank workstream | Persisted workstream surface area can be blank; controls expose Show dashboard, Show agents, Clear workstream, and composer. |
| Optional dashboard | Shows clickable total-agent count, proposal/review counts when implemented, and top five recently changed agents. No default operational needs-attention queue. |
| Agent list | Filter by agent name, workstream/domain, placement, lifecycle status, steward, and authority level; rows show name, short purpose, placement, lifecycle status, safe model alias summary, last behavior change time; row opens agent detail. |
| Agent detail | Editable name/purpose, safe behavior-profile summary, prompt link, skill list, nested governed references, manifest summaries, safe tool-boundary/model summaries, proposal review entry points, create/delete/deprecate skill/reference actions, runtime trace entry points. |
| Prompt doc editor | Current active Markdown content, version/status metadata, version history, edit request input only on current/latest editable artifact, historical read-only view, restore proposal. |
| Skill doc editor | Skill name/purpose, compact manifest hint, Markdown content, governed references, version/history/diff, current/latest edit input, restore proposal. |
| Governed reference doc editor | Name/title, short description and when-to-consult hint, optional access/redaction summary, Markdown content, version/history/diff, current/latest edit input, restore proposal. |
| Editing session | Free-form instructions, editing-agent proposed full document, structured proposal id, summary/rationale, risk classification, authority-expansion flags, suggested tests/replay evidence, Show diff toggle, refinement input, Save Draft, Cancel. |
| Proposal review | Proposed diff/content, risk/authority flags, suggested tests, approve/reject/activate/route-to-decision-card/request-changes actions, stale/forbidden recovery. |
| Create/delete skill | Create uses name, purpose/description, compact manifest hint, editing-agent drafted content, then activation. Delete/deprecate confirmation lists reference/manifest effects. |
| Create/delete reference doc | Create uses name/title, short description/when-to-consult hint, editing-agent drafted content, then activation. Delete/deprecate confirmation lists manifest effects. |
| Runtime traces | Trace metadata filterable by agent, doc, decision, and time range; visible from agent detail, doc pages, and separate trace surface. |

## Removed/de-emphasized frontend concerns

The Agent Admin default UX should not center provider-secret settings, direct tool permission editing, seed import, whole-agent activation/deactivation, rollback plumbing, or tenant/org governance scopes. Behavior proposal review is now authoritative for prompt/skill/reference changes; old direct-save components are not authoritative unless reconciled to the proposal/activation flow.

## Validation evidence to update

Existing frontend contract tests for Agent Admin should be reconciled to cover the new surface inventory, safe profile summaries, current/latest edit input, simple integer version history, version-to-previous diff behavior, restore proposals, editing-session Save Draft/Cancel, proposal review/activation/rejection, skill/reference deletion or deprecation, SaaS-admin-only access, provider-secret boundary, and runtime read trace metadata.
