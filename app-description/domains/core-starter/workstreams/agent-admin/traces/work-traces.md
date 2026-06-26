# Traces: Agent Admin

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence

Agent Admin trace records include actor, tenant/customer/user context, SaaS admin authorization decision, target `AgentDefinition`, placement/lifecycle/authority summary where allowed, target artifact type (`PromptDocument`, `SkillDocument`, `ReferenceDocument`, manifest entry, or safe profile metadata), target artifact id/name, version number/checksum where allowed, `AgentSkillManifest`/`AgentReferenceManifest` membership effect when applicable, safe `ModelConfigRef` alias, `ToolPermissionBoundary` decision category, proposal id, correlation id, action id, outcome, timestamp, and error/denial category when applicable.

## Edit-session audit

Every edit session is audited. Audit includes:

- actor;
- timestamps;
- target agent and doc;
- base current version;
- all user instructions;
- `AgentBehaviorEditorAgent` structured proposal output;
- proposed diff, summary, rationale, risk classification, authority-expansion flags, and suggested tests/replay evidence;
- Save Draft, Submit for Review, Approve, Reject, Activate, Decision-card routing, or Cancel outcome;
- saved/proposed/activated version content when allowed.

Cancelled edit sessions are not part of user-facing version history but are retained in audit.

## Version traces

Draft/proposed/active versions record created time, proposed/reviewed/activated actors, lifecycle status, saved content/checksum, risk classification, authority-expansion flags, and the whole editing-session transcript/summary. Restore operations create restore proposals with edit request `Restored from version N`; activation creates the active restore version.

## Runtime skill/reference read traces

All runtime prompt assembly and `readSkill`/`readReferenceDoc` calls are traced. Agent Admin shows `PromptAssemblyTrace`, `SkillLoadTrace`, reference-load trace facts, model-policy decisions, tool-boundary decisions, and `AgentWorkTrace` metadata directly and may also link to Audit/Trace. Denied trace categories include unauthorized `PromptDocument`, unassigned skill/reference denial, disabled/archived-agent denial, cross-scope document, inactive/deleted document, inactive/denied model config, and `ToolPermissionBoundary` denial.

Visible Agent Admin trace fields:

- agent name;
- prompt/skill/reference doc read;
- manifest assignment status;
- safe model alias and tool-boundary decision category where relevant;
- timestamp;
- request/session id;
- tenant/customer/user context.

Trace filters: agent, skill/reference doc, time range.

Trace rows do not show the full skill/reference content that was read. Authorized SaaS admins can open the current doc from Agent Admin when they need to inspect content.
