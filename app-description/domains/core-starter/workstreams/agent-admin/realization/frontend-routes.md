# Realization: Frontend routes and surfaces for Agent Admin

Capability/foundation scope: managed-agent governance (`agent-doc-administration` legacy mapping retained in source alignment).

## Frontend intent

Agent Admin frontend realization prioritizes SaaS-admin managed-agent governance: dashboard attention, catalog/detail, behavior-profile/document governance, test-console preflight, proposal review/approval/activation, result/partial-failure/system-message surfaces, and trace evidence. Agents and tools are static/code-generated from app-description; the frontend manages runtime behavior profiles and governed documents, not generated code.

## Surface / route concerns

| Surface / route concern | Expected frontend realization |
|---|---|
| Blank workstream | Persisted workstream surface can be blank; controls expose Show dashboard, Show agents, Clear workstream, and composer. |
| Dashboard | Shows things that need attention: behavior-change proposals, approval-required changes, provider/config blockers, and denied loader/tool-boundary events. Then shows actionable agent count, create skill/reference actions, recently changed agents, test console, and traces. |
| Agent catalog | Filter generated agents by name, workstream/domain, placement, lifecycle, steward, authority, model-policy alias, and scope; rows show safe summaries and open detail. |
| Agent detail | Generated identity/provenance, purpose, safe behavior-profile summary, resolved scope, prompt link, skill/reference manifests, generated tool list, safe tool-boundary/model summaries, governance center, profile history, proposal review, test console, and runtime trace entry points. |
| Governance center | Cards/routes for behavior profile, prompt, skill library, skill assignments, references, manifests, model policy, tool boundary, proposals, test console, and runtime traces. |
| Agent behavior profile | Versioned model config reference, prompt version, skill/reference manifest assignments, generated tool/tool-boundary assignments, scope/provenance, clone-from-global behavior, restore proposal, approval status. |
| Prompt doc editor | Current active Markdown, version/status metadata, version history, edit request input only on current/latest editable artifact, historical read-only view, restore proposal, `PromptAssemblyTrace` links. |
| Skill library/doc | Tenant-scoped skill catalog independent of any agent, create/deprecate/remove actions, assigned-agent counts, current/latest editor, version/history/diff, manifest usage, `SkillLoadTrace` links. |
| Reference catalog/doc | Reference list and editor with title, summary, when-to-consult hint, redaction summary, version/history/diff, manifest usage, `ReferenceLoadTrace` links. |
| Manifest editor | Assign/unassign compact `AgentSkillManifest` and `AgentReferenceManifest` entries; preview compact manifest context; activation creates profile version. |
| Model-policy summary | Select approved model config aliases only; never display provider secrets; provider/config blocker when active runtime config is missing. |
| Tool-boundary surface | Display safe generated-tool categories, adapter exposure, allow/deny/approval-required categories, proposed changes, and trace impact; no generated tool code editing. |
| Test console | Authorized test/replay/evaluation mode; assemble prompt/profile/manifests; provider-backed run only with active config; side effects disabled by default; returns success, provider/config blocker, loader/tool-boundary denial, or partial-failure result. |
| Editing session | Free-form instructions, editing-agent proposed content/delta, proposal id, summary/rationale, risk, authority flags, suggested tests, Show diff, refinement input, Save Draft, Submit for Review, Cancel. |
| Proposal review | Proposed diff/content/delta, risk/authority/model/tool-boundary impact, suggested tests, approve/reject/activate/route/request-changes/test actions, stale/forbidden/provider-blocked recovery. |
| Result/system-message | Typed success, no-op, draft-saved, activated, approval-required, denial, provider/config blocker, loader/tool-boundary denied, partial failure, stale, unsupported, or clarification states. |
| Runtime traces | Metadata filterable by agent, profile scope/version, doc/tool, decision, mode, and time range; visible from agent detail, governance center, doc pages, test console, and trace surface. |

## Composer and chat-plan behavior

Composer routes may open or prepopulate Agent Admin surfaces without mutation. Confirmed chat plans may execute catalog-bound actions only after the UI shows exact target, governed tool ids/user-facing action labels, affected versions/profile fields, confirmation text, approval requirement, idempotency/retry behavior, possible partial-failure result, and trace links. The frontend must not treat model output as approval or authority.

## Removed/de-emphasized frontend concerns

The Agent Admin default UX should not center provider-secret settings, generated tool code editing, backend tool-boundary implementation editing, seed import, whole-agent activation/deactivation, or rollback plumbing. Behavior proposal review is authoritative for model config reference, prompt, skill, reference, manifest, generated-tool assignment, tool-boundary reference, and test-console evidence. Old direct-save components are not authoritative unless reconciled to proposal/activation flow.

## Validation evidence to update

Frontend contract/runtime-validation should cover dashboard attention categories, catalog/detail/governance center, safe profile summaries, global-to-tenant provenance, current/latest edit input, simple integer version history, profile versions for model/prompt/manifest/tool-boundary changes, version-to-previous diff, restore proposals, editing-session Save/Submit/Cancel, proposal review/activation/rejection, skill/reference deprecation, test-console provider fail-closed behavior, SaaS-admin-only access, explicit chat confirmation, provider-secret/tool-code boundary, result/partial-failure/system-message surfaces, and runtime profile/read/tool trace metadata.
