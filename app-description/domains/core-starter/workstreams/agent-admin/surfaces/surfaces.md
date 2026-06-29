# Surfaces: Agent Admin

## Workstream placement

Agent Admin is a SaaS-admin-only functional-agent workstream owned by `agent-admin-agent`. It is a managed-agent governance workspace for existing generated agents and their runtime behavior artifacts, not a whole-agent or generated-tool-code creation console.

The workstream persists previous surfaces. There is no forced default surface. A first-time or cleared workstream may be blank with controls to Show dashboard, Show agents, and use the composer.

## Surface inventory

1. `surface-agent-admin-blank` — blank workstream state.
2. `surface-agent-admin-dashboard` — role-specific dashboard with attention and action routers.
3. `surface-agent-admin-agent-catalog` — filterable generated-agent catalog.
4. `surface-agent-admin-agent-detail` — one generated agent's behavior profile, docs, assignments, governance status, and traces.
5. `surface-agent-admin-governance-center` — prompt/skill/reference/manifest/model-policy/tool-boundary governance hub for the selected agent.
6. `surface-agent-admin-agent-profile-history` — versioned agent behavior profile history.
7. `surface-agent-admin-prompt-doc` — `PromptDocument` view/edit/proposal/history.
8. `surface-agent-admin-skill-library` — independently managed tenant-scoped `SkillDocument` catalog.
9. `surface-agent-admin-skill-doc` — `SkillDocument` view/edit/proposal/history.
10. `surface-agent-admin-reference-catalog` — governed `ReferenceDocument` list for an agent/manifest/skill context.
11. `surface-agent-admin-reference-doc` — `ReferenceDocument` view/edit/proposal/history.
12. `surface-agent-admin-manifest-editor` — compact `AgentSkillManifest` / `AgentReferenceManifest` assignment proposal surface.
13. `surface-agent-admin-model-policy-summary` — safe `ModelConfigRef` / model-policy selection proposal surface.
14. `surface-agent-admin-tool-boundary` — safe generated-tool and `ToolPermissionBoundary` assignment proposal surface.
15. `surface-agent-admin-test-console` — authorized safe test-console run/replay/preflight surface.
16. `surface-agent-admin-edit-session` — editing-agent proposal/refinement session.
17. `surface-agent-admin-proposal-review` — behavior-change proposal review/approval/activation/decision-card routing.
18. `surface-agent-admin-version-history` — immutable version list and historical version view.
19. `surface-agent-admin-version-diff` — selected version vs immediate predecessor, or proposal vs base.
20. `surface-agent-admin-create-skill` — skill-library create proposal flow.
21. `surface-agent-admin-delete-skill-confirmation` — deprecation-by-default skill removal confirmation.
22. `surface-agent-admin-create-reference-doc` — create governed reference proposal flow.
23. `surface-agent-admin-delete-reference-doc-confirmation` — permanent/deprecation reference delete confirmation.
24. `surface-agent-admin-runtime-traces` — runtime profile/prompt/skill/reference/tool/test-console traces.
25. `surface-agent-admin-result` — success, no-op, partial-failure, approval-required, or draft-saved result surface.
26. `surface-agent-admin-system-message` — denial, refusal, clarification, unavailable, stale, provider/config blocker, loader/tool-boundary denial, or recovery message.

## Surface graph and action edges

| Source surface | Action edge | Governed tool / capability target | Adapter(s) | Result surface |
| --- | --- | --- | --- | --- |
| Dashboard | open attention category | proposal, provider blocker, loader/tool-boundary trace read | `surface_action`, read-only `agent_tool_call` | proposal review, test console, runtime traces, or system message |
| Agent catalog | open agent detail | `AgentDefinition` read/profile inspect | `surface_action`, `human_chat_tool_plan`, read-only `agent_tool_call` | agent detail or system message |
| Agent detail | open governance center | profile/doc/manifest/model/tool-boundary read | `surface_action`, read-only `agent_tool_call` | governance center |
| Governance center | edit prompt/skill/reference/profile/manifest/model/tool-boundary | behavior proposal draft | `surface_action`, confirmed `human_chat_tool_plan`, bounded proposal `agent_tool_call` | edit session / proposal review / system message |
| Prompt/skill/reference doc | save draft/proposal | versioned non-active draft | `surface_action`, confirmed `human_chat_tool_plan` | draft-saved result or partial-failure |
| Manifest/model/tool-boundary surfaces | propose assignment/selection | behavior-profile proposal | `surface_action`, confirmed `human_chat_tool_plan` | proposal review or approval-required result |
| Proposal review | approve/reject/activate/route | proposal lifecycle/version activation | `surface_action`, confirmed `human_chat_tool_plan` | active/rejected/approval-required/stale result |
| Test console | run preflight/test/replay | test-console governed tool and runtime loader | `surface_action`, confirmed `human_chat_tool_plan`, internal runtime loader | test result, provider/config blocker, loader/tool-boundary denial, or partial failure |
| Runtime traces | filter/open trace evidence | trace read | `surface_action`, read-only `agent_tool_call` | trace detail or redacted/denied system message |

Composer surface-intent routes may open or prepopulate these surfaces, but deterministic routing never submits, approves, activates, runs a provider call, or mutates state. Consequential chat-mediated actions require explicit confirmation bound to the proposed plan.

## Usability directive

Default surfaces must optimize for clear governance and recovery tasks. Use plain language such as Improve behavior, Review proposal, Activate reviewed change, Needs approval, Test safely, Runtime reads, Denied loader event, Provider setup needed, Tool boundary, Version history, Show diff, Save draft, Cancel, and Restore this version. Do not lead with policy ids, seed imports, backend class names, provider secrets, activation plumbing, or tool-boundary internals.

Progressive disclosure order: task summary, current content/profile, primary actions, proposed changes/results, version/history/diff controls, trace/audit diagnostics.

## `surface-agent-admin-dashboard`

Purpose: role-specific dashboard for SaaS-admin Agent Admin work.

Default payload:

- `thingsNeedAttention`: counts/cards for behavior-change proposals, approval-required proposals, provider/config blockers, and denied loader/tool-boundary events; each opens the relevant filtered proposal/review/test-console/trace surface.
- `thingsYouCanDo`: clickable total-agent count opening `surface-agent-admin-agent-catalog`, create skill/reference proposal actions, and runtime trace shortcut.
- `recentlyChangedAgents`: top five agents by last behavior change time; each row opens agent detail.

States: loading, ready, empty, forbidden, stale/reconnect, partial-data, failure.

## `surface-agent-admin-agent-catalog`

Purpose: find an existing generated managed agent to inspect or improve.

Default payload: filters for name, workstream/domain, placement, lifecycle, steward, authority level, safe model-policy alias, and scope provenance; rows show agent name, purpose, placement, lifecycle, safe model alias, scope provenance, last behavior change, and attention badges. Row action opens agent detail.

States: loading, ready, empty-no-agents, empty-no-filter-matches, forbidden, validation-error, failure.

## `surface-agent-admin-agent-detail`

Purpose: inspect one generated agent and choose a governance task.

Default payload: generated agent id/name/provenance, purpose, placement, lifecycle, steward/owner, authority level, scope provenance, safe `ModelConfigRef`/model-policy alias, active prompt link, compact `AgentSkillManifest` / `AgentReferenceManifest` summaries, generated-tool assignment summary, safe `ToolPermissionBoundary` category summary, behavior-profile history, governance center entry point, test-console entry point, proposal review entry point, and trace entry points.

No whole-agent create/delete action appears here.

## `surface-agent-admin-governance-center`

Purpose: role-specific hub for behavior profile, prompt, skill, reference, manifest, model-policy, tool-boundary, proposal, test-console, and trace actions for the selected agent.

Default payload: cards for behavior profile, prompt, skill library/assignments, references, compact manifests, model policy, tool boundary, test console, proposals, and runtime traces. Each card shows current status, attention count, allowed actions, last changed metadata, and safe denial/recovery copy when unavailable.

## Prompt, skill, reference, and manifest surfaces

`surface-agent-admin-prompt-doc`, `surface-agent-admin-skill-doc`, `surface-agent-admin-reference-doc`, and `surface-agent-admin-manifest-editor` render current/latest editable artifacts with version/status metadata, risk/authority flags, runtime trace links, free-form edit input when allowed, and proposal actions. Historical versions are read-only and offer version-to-previous diff plus restore-proposal action.

Skill and reference create/delete confirmation surfaces require explicit confirmation, list affected assignments/manifests/references, default to deprecation when a document has ever been assigned or loaded, and must remove loader access without orphaned hidden manifest entries.

## Model-policy and tool-boundary surfaces

`surface-agent-admin-model-policy-summary` shows approved model config aliases and model-policy status only; it never exposes provider secrets or raw model credentials. Changing the selected model config creates a behavior-profile proposal and may be approval-required when risk or policy expands.

`surface-agent-admin-tool-boundary` shows generated tool categories, adapter exposure (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, internal loader/API), current allowed/denied/approval-required decisions, and safe rationale. Assigning generated tools or changing boundary references creates a behavior-profile proposal. It does not create, edit, or delete generated tool code.

## `surface-agent-admin-test-console`

Purpose: run authorized safe test/replay/preflight scenarios for a selected agent/profile/document version.

Default payload: selected agent/profile/version, selected prompt/skill/reference/manifest/model-policy/tool-boundary context, test input, mode (`test`, `replay`, `evaluation`), side-effect status, expected traces, and result area.

Allowed actions: assemble prompt, validate manifests/loaders, run provider-backed test only when provider/model config is active and authorized, replay historical trace when allowed, and save suggested tests as proposal evidence. Missing provider/runtime config returns a provider/config blocker. Loader or tool-boundary denials return safe denial result plus trace link. Side effects are disabled unless an explicitly modeled safe test tool and approval are present.

States: ready, assembling, running, provider-unavailable, model-policy-denied, loader-denied, tool-boundary-denied, side-effects-blocked, partial-failure, success, forbidden, failure.

## `surface-agent-admin-edit-session`

Purpose: iterative AI-assisted behavior editing session.

Default payload: target agent/artifact/profile, base current version, user instruction transcript, clarifying question when needed, proposed full Markdown or profile delta, structured proposal id, summary/rationale, risk classification, authority-expansion flags, suggested tests/replay evidence, advisory warnings, Show diff, refinement input, Save draft/proposal, Submit for review when required, and Cancel.

States: drafting, clarification-needed, proposed, refining, saving-draft, draft-saved, routed-to-review, cancelled, provider-unavailable, stale-current-version, forbidden, blocked-authority-expansion, partial-failure, failure.

## `surface-agent-admin-proposal-review`

Purpose: review behavior-change proposals before activation or approval routing.

Default payload: proposal id, target agent/artifact/profile, proposed diff/content/delta, summary/rationale, risk classification, authority-expansion flags, model-policy/tool-boundary impact, suggested tests/replay evidence, reviewer status, trace links, and available actions.

Actions: approve, reject with rationale, activate low-risk reviewed draft when authorized, route medium/high-risk proposal to decision-card/review workflow, request changes, run safe test-console evidence, or cancel. Activation re-checks SaaS admin authority, current-version consistency, proposal status, artifact lifecycle, model policy, tool-boundary/authority expansion, provider/runtime availability when relevant, and idempotency key. Rejection leaves active behavior unchanged.

States: draft, in-review, approved, rejected, activation-ready, activating, active, decision-card-required, blocked-authority-expansion, provider-config-blocked, stale-current-version, forbidden, partial-failure, failure.

## Version, result, trace, and system-message surfaces

`surface-agent-admin-version-history` lists immutable versions and opens historical read-only views. `surface-agent-admin-version-diff` compares selected version `N` only with `N-1`; version 1 has no-prior-version state.

`surface-agent-admin-result` returns typed success, no-op, draft-saved, activated, approval-required, denied, or partial-failure results with user-safe summary, next action, and trace link.

`surface-agent-admin-runtime-traces` filters runtime profile resolution, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, model-policy/provider decisions, generated-tool decisions, `ToolPermissionBoundary` decisions, test-console runs, and `AgentWorkTrace` metadata by agent, doc/tool, decision, mode, and time range. Trace rows do not show full loaded skill/reference content.

`surface-agent-admin-system-message` shows denial, clarification, unsupported request, provider unavailable, model-policy denied, stale version, deleted doc, hidden target, or safe alternative copy without hidden enumeration.

## Tests

Surface tests must cover dashboard attention categories, catalog/detail, governance center, behavior profile, prompt/skill/reference/manifest/tool-boundary/model-policy surfaces, test console provider fail-closed behavior, proposal review, result/partial-failure/system-message surfaces, current/latest edit gating, historical read-only views, version-to-previous diff semantics, restore proposals, SaaS-admin-only access, explicit chat confirmation, idempotent repeated actions, provider secret boundary, loader/tool-boundary denial traces, and sufficiency of the surface descriptions for implementation without inventing payload fields, auth behavior, trace links, or visual semantics.
