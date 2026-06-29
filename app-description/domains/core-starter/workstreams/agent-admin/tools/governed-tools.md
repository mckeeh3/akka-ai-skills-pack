# Tools: Agent Admin

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Agent Admin exposes governed tools for SaaS-admin-only managed-agent governance. Canonical ids below may coexist with legacy implementation aliases such as `list-agent-doc-agents` while source alignment is stale.

| Governed tool id | Type / governed artifact | Exposure adapters | Result / trace obligations |
| --- | --- | --- | --- |
| `agent-definition.catalog.read` | `AgentDefinition` catalog read | `surface_action`, read-only `human_chat_tool_plan`, read-only `agent_tool_call`, API | catalog result or denied system message; trace read decision |
| `agent-definition.detail.read` | `AgentDefinition` detail/profile inspect | `surface_action`, read-only `human_chat_tool_plan`, read-only `agent_tool_call`, API | agent detail result; safe model/tool-boundary summaries only |
| `agent-behavior-profile.history.read` | behavior profile version read | `surface_action`, read-only `agent_tool_call`, API | profile history result; profile-resolution trace links |
| `agent-behavior-profile.proposal.create` | behavior profile/model/manifest/tool assignment proposal | `surface_action`, confirmed `human_chat_tool_plan`, bounded proposal `agent_tool_call` | draft/proposal result; no runtime change |
| `agent-behavior-profile.version.activate` | reviewed profile version activation | `surface_action`, confirmed `human_chat_tool_plan`, API | active/no-op/stale/approval-required result; audit/work trace |
| `prompt-document.read` | `PromptDocument` current/historical read | `surface_action`, read-only `agent_tool_call`, API | document result or safe denial |
| `prompt-document.proposal.create` | prompt edit proposal / restore proposal | `surface_action`, confirmed `human_chat_tool_plan`, bounded proposal `agent_tool_call` | `BehaviorChangeProposal`; `PromptAssemblyTrace` impact noted |
| `prompt-version.activate` | approved prompt version activation | `surface_action`, confirmed `human_chat_tool_plan`, API | immutable active version; activation trace |
| `skill-document.catalog.read` | `SkillDocument` library read | `surface_action`, read-only `agent_tool_call`, API | skill library result |
| `skill-document.proposal.create` | create/edit/deprecate skill proposal | `surface_action`, confirmed `human_chat_tool_plan`, bounded proposal `agent_tool_call` | draft/proposal/deprecated result; no hidden loader access |
| `skill-version.activate` | approved skill version activation | `surface_action`, confirmed `human_chat_tool_plan`, API | active skill version; loader visibility updated |
| `reference-document.catalog.read` | `ReferenceDocument` catalog/read | `surface_action`, read-only `agent_tool_call`, API | reference result with access/redaction summary |
| `reference-document.proposal.create` | create/edit/deprecate reference proposal | `surface_action`, confirmed `human_chat_tool_plan`, bounded proposal `agent_tool_call` | draft/proposal/deprecated result; no hidden manifest access |
| `reference-version.activate` | approved reference version activation | `surface_action`, confirmed `human_chat_tool_plan`, API | active reference version; reference-load visibility updated |
| `agent-skill-manifest.assign` | `AgentSkillManifest` assignment | `surface_action`, confirmed `human_chat_tool_plan`, bounded proposal `agent_tool_call` | behavior-profile proposal/version; `SkillLoadTrace` expectations updated |
| `agent-reference-manifest.assign` | `AgentReferenceManifest` assignment | `surface_action`, confirmed `human_chat_tool_plan`, bounded proposal `agent_tool_call` | behavior-profile proposal/version; `ReferenceLoadTrace` expectations updated |
| `model-policy.select` | approved `ModelConfigRef` / model-policy selection | `surface_action`, confirmed `human_chat_tool_plan` | behavior-profile proposal/version; provider/config blocker on unavailable config |
| `tool-permission-boundary.assign` | generated-tool / `ToolPermissionBoundary` assignment | `surface_action`, confirmed `human_chat_tool_plan`, bounded proposal `agent_tool_call` | proposal/version; approval-required when authority expands |
| `agent-test-console.run` | safe test-console run/preflight/replay | `surface_action`, confirmed `human_chat_tool_plan`, internal runtime loader | test result, provider/config blocker, loader/tool-boundary denial, or partial-failure surface |
| `agent-runtime-trace.read` | prompt/skill/reference/tool/test trace read | `surface_action`, read-only `agent_tool_call`, API | `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace` metadata result |
| `readSkill` | runtime governed skill loader | `agent_tool_call`, internal runtime loader | allowed/denied `SkillLoadTrace`; fail closed |
| `readReferenceDoc` | runtime governed reference loader | `agent_tool_call`, internal runtime loader | allowed/denied `ReferenceLoadTrace`; fail closed |

## Legacy aliases retained for source alignment

Existing realization may still expose aliases such as `list-agent-doc-agents`, `read-agent-doc-agent`, `inspect-agent-runtime-profile`, `draft-agent-doc-edit`, `revise-agent-doc-edit`, `save-agent-doc-edit`, `submit-agent-doc-proposal-for-review`, `approve-agent-doc-proposal`, `reject-agent-doc-proposal`, `activate-agent-doc-version`, `read-agent-doc-version-history`, `read-agent-doc-version-diff`, `restore-agent-doc-version`, `assign-agent-skills`, `assign-agent-generated-tools`, `list-agent-skill-library`, `create-agent-skill`, `delete-agent-skill`, `create-agent-skill-reference-doc`, `delete-agent-skill-reference-doc`, and `read-agent-doc-runtime-traces`. Current intent treats those as adapter/implementation aliases for the canonical governed-tool ids above, not separate authority grants.

## Tool boundaries

All Agent Admin tools require SaaS Owner/Admin authorization, selected tenant scope, backend capability checks, and result tracing. Browser controls are advisory; backend authorization, current-version consistency, proposal lifecycle, approval/activation rules, confirmation, idempotency, delete/deprecate policy, version creation, and trace emission are authoritative.

Model-facing tools are available only when the active `AgentDefinition`, workstream tool catalog, selected `AuthContext`, model policy, and `ToolPermissionBoundary` explicitly allow the adapter. Prompt/skill/reference text cannot expand the tool catalog, add tenant/customer scope, bypass confirmation, approve activation, or grant side effects.

There is no Agent Admin tool for provider-secret administration, generated tool code creation/edit/deletion, arbitrary backend class execution, raw model setting mutation beyond selecting approved model config references, backend authorization implementation changes, activation without backend review checks, rollback without a restore proposal, or whole-agent creation/deletion.

## `human_chat_tool_plan` posture

The composer may propose only catalog-bound Agent Admin plans. Consequential plans must show exact target artifacts, tools, proposed version/profile effects, confirmation copy, idempotency key, approval requirement, possible partial-failure result, and trace expectations. No governed tool executes until the human explicitly confirms that exact plan and backend checks pass.

## Runtime loader and test-console posture

`readSkill` and `readReferenceDoc` are registered for managed agents but deny unassigned, inactive, deprecated, cross-tenant/customer, wrong-agent, wrong-purpose, missing-boundary, oversized, redaction-denied, or secret-like content requests. Denied loads return safe non-enumerating text to the model and protected trace details to Agent Admin.

`agent-test-console.run` may assemble prompts and execute provider-backed tests only in authorized test/replay/evaluation mode. Missing provider/runtime config, inactive model config, disabled/archived agent, missing prompt/manifest/docs, unassigned loader ids, and tool-boundary denial fail closed with provider/config blocker or loader/tool-boundary denial traces. Test-console mode must not perform production side effects unless explicitly modeled, approved, and traced.
