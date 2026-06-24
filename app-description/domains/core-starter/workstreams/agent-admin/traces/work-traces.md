# Traces: Agent Admin

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence

Agent Admin trace records include actor, tenant/customer/user context, SaaS admin authorization decision, target `AgentDefinition`, target doc type (`PromptDocument`, `SkillDocument`, or reference doc), target doc id/name, version number/checksum where allowed, `AgentSkillManifest` membership effect when applicable, correlation id, action id, outcome, timestamp, and error/denial category when applicable.

## Edit-session audit

Every edit session is audited. Audit includes:

- actor;
- timestamps;
- target agent and doc;
- base current version;
- all user instructions;
- `AgentBehaviorEditorAgent` proposed output;
- summary/warnings/risks;
- Save or Cancel outcome;
- saved version content when Save occurs.

Cancelled edit sessions are not part of user-facing version history but are retained in audit.

## Version traces

Saved versions record created time, actor, saved content, and the whole editing-session transcript/summary. Restore operations create a normal new version with edit request `Restored from version N`.

## Runtime skill/reference read traces

All runtime prompt assembly and `readSkill`/`readReferenceDoc` calls are traced. Agent Admin shows `PromptAssemblyTrace`, `SkillLoadTrace`, reference-load trace facts, and `AgentWorkTrace` metadata directly and may also link to Audit/Trace. Denied trace categories include unauthorized `PromptDocument`, unassigned skill denial, disabled-agent denial, cross-scope document, inactive/deleted document, and `ToolPermissionBoundary` denial.

Visible Agent Admin trace fields:

- agent name;
- skill/reference doc read;
- manifest assignment status;
- timestamp;
- request/session id;
- tenant/customer/user context.

Trace filters: agent, skill/reference doc, time range.

Trace rows do not show the full skill/reference content that was read. Authorized SaaS admins can open the current doc from Agent Admin when they need to inspect content.
