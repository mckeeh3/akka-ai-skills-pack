# Traces: Agent Admin

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence

Agent Admin trace records include actor, tenant/customer/user context, SaaS admin authorization decision, target generated `AgentDefinition`, resolved behavior-profile scope/version, placement/lifecycle/authority summary where allowed, target artifact type (`AgentBehaviorProfile`, `PromptDocument`, `SkillDocument`, `ReferenceDocument`, manifest entry, generated tool assignment, or safe profile metadata), target artifact id/name, version number/checksum where allowed, `AgentSkillManifest`/`AgentReferenceManifest` membership effect when applicable, generated tool assignment effect when applicable, safe `ModelConfigRef` alias, `ToolPermissionBoundary` decision category, proposal id, correlation id, action id, outcome, timestamp, and error/denial category when applicable.

## Edit-session audit

Every edit session is audited. Audit includes:

- actor;
- timestamps;
- target agent and doc/profile;
- base current version;
- all user instructions;
- `AgentBehaviorEditorAgent` structured proposal output;
- proposed diff, summary, rationale, risk classification, authority-expansion flags, and suggested tests/replay evidence;
- Save Draft, Submit for Review, Approve, Reject, Activate, Decision-card routing, or Cancel outcome;
- saved/proposed/activated version content when allowed.

Cancelled edit sessions are not part of user-facing version history but are retained in audit.

## Version traces

Draft/proposed/active document versions record created time, proposed/reviewed/activated actors, lifecycle status, saved content/checksum, risk classification, authority-expansion flags, and the whole editing-session transcript/summary. Agent behavior-profile versions additionally record model config reference, prompt version reference, assigned skill references, allowed generated tool ids, scope, and clone provenance. Restore operations create restore proposals with edit request `Restored from version N`; activation creates the active restore version.

## Runtime profile/skill/reference/tool read traces

All runtime behavior-profile resolution, prompt assembly, `readSkill`/`readReferenceDoc` calls, and generated-tool assignment decisions are traced. Agent Admin shows profile-resolution trace facts, `PromptAssemblyTrace`, `SkillLoadTrace`, reference-load trace facts, model-policy decisions, generated-tool assignment decisions, tool-boundary decisions, and `AgentWorkTrace` metadata directly and may also link to Audit/Trace. Denied trace categories include unauthorized `PromptDocument`, unassigned skill/reference denial, unassigned generated tool denial, disabled/archived-agent denial, cross-scope document/profile, inactive/deleted document, inactive/denied model config, and `ToolPermissionBoundary` denial.

Visible Agent Admin trace fields:

- agent name;
- resolved behavior-profile scope/version;
- prompt/skill/reference doc read;
- manifest assignment status;
- generated tool decision where relevant;
- safe model alias and tool-boundary decision category where relevant;
- timestamp;
- request/session id;
- tenant/customer/user context.

Trace filters: agent, behavior-profile scope/version, skill/reference doc, generated tool, decision, and time range.

Trace rows do not show the full skill/reference content that was read. Authorized SaaS admins can open the current doc from Agent Admin when they need to inspect content.
