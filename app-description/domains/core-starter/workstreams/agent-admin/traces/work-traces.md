# Traces: Agent Admin

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence

Agent Admin trace records include actor, tenant/customer/user context, selected `AuthContext`, SaaS admin authorization decision, actor adapter/source (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API, or internal runtime loader), target generated `AgentDefinition`, resolved behavior-profile scope/version, placement/lifecycle/authority summary where allowed, target artifact type (`AgentBehaviorProfile`, `PromptDocument`, `SkillDocument`, `ReferenceDocument`, `AgentSkillManifest`, `AgentReferenceManifest`, `ModelConfigRef`/model policy, `ToolPermissionBoundary`, generated tool assignment, test-console run, or safe profile metadata), target artifact id/name, version number/checksum where allowed, manifest membership effect when applicable, generated tool assignment effect when applicable, safe model alias, provider/config decision, tool-boundary decision category, proposal id, confirmation id when applicable, correlation id, action id, outcome, result/partial-failure/system-message surface id, timestamp, and error/denial category when applicable.

## Edit-session audit

Every edit session is audited. Audit includes:

- actor and selected AuthContext;
- adapter/source and confirmation id when the action came from a confirmed chat plan;
- timestamps;
- target agent and doc/profile/manifest/model/tool-boundary artifact;
- base current version;
- all user instructions;
- `AgentBehaviorEditorAgent` structured proposal output;
- proposed diff, summary, rationale, risk classification, authority-expansion flags, model-policy/tool-boundary impact, and suggested tests/replay evidence;
- Save Draft, Submit for Review, Approve, Reject, Activate, Decision-card routing, Test Console, or Cancel outcome;
- saved/proposed/activated version content or profile delta when allowed;
- denial or partial-failure details in protected trace fields.

Cancelled edit sessions are not part of user-facing version history but are retained in audit.

## Version traces

Draft/proposed/active document versions record created time, proposed/reviewed/activated actors, lifecycle status, saved content/checksum, risk classification, authority-expansion flags, and the whole editing-session transcript/summary. Agent behavior-profile versions additionally record model config reference, prompt version reference, assigned skill references, assigned reference manifest entries, allowed generated tool ids, tool-boundary reference, scope, clone provenance, approval basis, and idempotency key.

Restore operations create restore proposals with edit request `Restored from version N`; activation creates the active restore version. Repeated activation/no-op operations emit no-op result traces rather than duplicate version traces.

## Runtime profile/skill/reference/tool/test traces

All runtime behavior-profile resolution, prompt assembly, `readSkill`/`readReferenceDoc` calls, generated-tool assignment decisions, test-console runs, provider/model-policy checks, and tool-boundary decisions are traced. Agent Admin shows profile-resolution trace facts, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, model-policy/provider decisions, generated-tool assignment decisions, `ToolPermissionBoundary` decisions, test-console run facts, and `AgentWorkTrace` metadata directly and may also link to Audit/Trace.

Denied trace categories include unauthorized `AgentDefinition`/`PromptDocument`, unassigned skill/reference, unassigned generated tool, disabled/archived agent, cross-scope document/profile, inactive/deleted document, inactive/denied model config, missing provider/runtime configuration, unauthorized test-console mode, side-effects-blocked test mode, and `ToolPermissionBoundary` denial.

Visible Agent Admin trace fields:

- agent name;
- resolved behavior-profile scope/version;
- prompt/skill/reference doc read;
- manifest assignment status;
- generated tool decision where relevant;
- safe model alias and provider/model-policy decision category where relevant;
- tool-boundary decision category;
- test-console mode/result when relevant;
- timestamp;
- request/session/correlation id;
- tenant/customer/user context.

Trace filters: agent, behavior-profile scope/version, skill/reference doc, generated tool, decision, runtime/test mode, provider/config blocker, denial category, and time range.

Trace rows do not show the full skill/reference content that was read. Authorized SaaS admins can open the current/historical doc from Agent Admin when they need to inspect content. Denied-load traces use safe non-enumerating model-visible messages and more specific protected admin categories.
