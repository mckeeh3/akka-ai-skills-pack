# Behavior: Agent Admin

## Current-state behavior

Agent Admin supports SaaS-admin governance of runtime-managed behavior artifacts for all generated managed agents. Whole agents, generated tool code, provider secrets, backend authorization implementation, and hidden platform policy are not created or edited here. Agent Admin manages behavior-profile state and governed documents: `AgentDefinition` summaries, active prompt references, tenant-scoped `PromptDocument` / `PromptVersion`, `SkillDocument` / `SkillVersion`, `ReferenceDocument` / `ReferenceVersion`, `AgentSkillManifest`, `AgentReferenceManifest`, approved `ModelConfigRef` / model-policy selection, generated-tool assignment, `ToolPermissionBoundary` selection, safe test-console runs, and proposal/review/activation state.

## Entry and attention behavior

The workstream persists previous surfaces and has no forced default surface. A first-time or cleared workstream may be blank with Show dashboard, Show agents, Clear workstream, and composer affordances.

The dashboard is an action router. It first shows attention categories for behavior-change proposals, approval-required changes, provider/config blockers, and denied loader/tool-boundary events. Each count opens a filtered proposal, governance, test-console, or trace surface. Then it shows things an admin can do, such as open the agent catalog, browse recently changed agents, create skill/reference proposals, or inspect runtime traces.

## Agent catalog and detail behavior

The catalog lists only existing generated agents. It may be filtered by agent name, workstream/domain, placement (`functional_context_area`, `internal_worker`, evaluator, autonomous/background, system/foundation, or future generated placements), lifecycle status, steward, authority level, model-policy alias, and scope provenance. Rows show safe summaries and open agent detail.

Agent detail shows static app-description provenance plus the resolved behavior profile: lifecycle, steward/owner, authority level, scope (`global`, reserved `saas-app-owner`, or tenant-specific), safe model alias/model-policy summary, active prompt, compact skill/reference manifests, generated-tool assignment summary, tool-boundary decision categories, test-console entry point, proposal/review entry points, and trace links. Whole-agent create/delete and generated-tool implementation edits are absent.

## Governance surface behavior

Prompt, skill, reference, manifest, model-policy, tool-boundary, and test-console surfaces use the same governed graph:

- browser `surface_action` can read, draft, review, approve, activate, reject, cancel, restore, assign, unassign, run safe tests, and inspect traces when backend authorization allows;
- `human_chat_tool_plan` can propose catalog-bound plans but executes consequential actions only after the human confirms the exact plan and backend checks pass;
- `agent_tool_call` exposure is read/proposal/test-preflight only unless the active `ToolPermissionBoundary`, capability contract, and approval policy explicitly permit the adapter;
- `internal_call` covers runtime profile resolution, prompt assembly, loader authorization, generated-tool decision checks, provider/model-policy checks, and trace emission.

## Editing and proposal behavior

Editing is mediated by `AgentBehaviorEditorAgent` with doc-type-specific skills for prompt editing, skill editing, reference editing, manifest/profile edits, tool-boundary/model-policy impact explanation, and behavior-change risk classification. Users provide free-form instructions. The editing agent reads only authorized same-agent context through governed loader/tool boundaries and returns a structured `BehaviorChangeProposal` containing proposed full content or profile delta, proposed diff, summary, rationale, risk classification, authority-expansion flags, suggested tests/replay evidence, expected result surface, and recommended next action.

The editing agent may ask clarifying questions before proposing changes. Unsafe, unsupported, authority-expanding, tenant-scope-expanding, model-policy-expanding, tool-boundary-expanding, or provider-secret-seeking requests are refused, blocked, or routed to approval/decision-card handling. Prompt/skill/reference text remains behavior guidance only; it cannot grant backend capabilities, generated tool use, provider access, tenant/customer scope, approval authority, or autonomous side effects.

Save draft/proposal creates an immutable non-active proposal or version. Activate/Commit is a separate protected backend action. Low-risk copy/clarity changes may be reviewed and activated by the same authorized SaaS admin as the foundation simplification. Medium/high-risk, authority-expanding, model-policy, tool-boundary, approval-boundary, provider, or tenant-scope changes require approval/decision-card routing or denial. Cancel discards the proposal and leaves active runtime behavior unchanged.

## Version, idempotency, and transaction behavior

Versions use simple integer numbers. Prompt/skill/reference/profile draft/proposed/approved/active versions are immutable and retained. Historical versions are read-only. Restore creates a restore proposal copied from historical content and records `Restored from version N`; activation creates a new current active version.

Idempotency and transaction rules:

- repeated Save Draft with the same proposal/content and idempotency key returns the existing proposal result;
- repeated activation of an already-active version is a no-op result surface with trace evidence;
- stale base-version saves or activations fail with a stale-current-version system message;
- multi-step confirmed chat plans execute per governed tool and return a partial-failure result surface when a later step fails;
- assignment changes activate as behavior-profile versions and never mutate skill/reference document versions or generated tool code;
- deleted/deprecated skill/reference docs remove manifest/loader access and preserve trace readability.

## Test-console behavior

The test console is safe by default. It may assemble prompts, compact manifests, selected draft/active docs, model-policy summaries, and allowed test inputs for an authorized tenant-scoped `AgentDefinition`. It may invoke a real provider only when an approved `ModelConfigRef`, active provider runtime configuration, test-console capability, selected AuthContext, and `ToolPermissionBoundary` allow test mode. Missing provider/runtime configuration, inactive model config, disabled/archived agent, unavailable prompt/skill/reference docs, unassigned loader ids, or missing tool-boundary grants fail closed with an actionable provider/config blocker or loader/tool-boundary denial surface and trace. Test-console mode must not perform external side effects unless an explicitly modeled safe test tool and approval are present.

## Runtime behavior

Only activated versions update runtime behavior. Each agent request resolves the tenant-specific active behavior profile when present, otherwise the global active profile, then validates `AgentDefinition`, lifecycle status, authority level, active `PromptDocument`/`PromptVersion`, compact `AgentSkillManifest`, compact `AgentReferenceManifest`, `ModelConfigRef`/model policy, selected `AuthContext`, generated-tool assignment list, and `ToolPermissionBoundary`. Prompt assembly includes current prompt and compact assigned skill/reference names, descriptions, and when-to-use hints. Full skill/reference bodies load only through authorized `readSkill(skillId)` and `readReferenceDoc(referenceId)` calls. Runtime profile resolution, prompt assembly, skill loads, reference loads, provider/model-policy decisions, generated-tool decisions, tool-boundary denials, test-console runs, partial failures, and work results are traced.
