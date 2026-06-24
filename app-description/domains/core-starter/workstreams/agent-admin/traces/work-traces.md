# Traces: Agent Admin

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence

Agent Admin trace records include actor, SaaS admin authorization decision, target agent, target doc type, target doc id/name, version number, correlation id, action id, outcome, timestamp, and error/denial category when applicable.

## Edit-session audit

Every edit session is audited. Audit includes:

- actor;
- timestamps;
- target agent and doc;
- base current version;
- all user instructions;
- editing-agent proposed output;
- summary/warnings/risks;
- Save or Cancel outcome;
- saved version content when Save occurs.

Cancelled edit sessions are not part of user-facing version history but are retained in audit.

## Version traces

Saved versions record created time, actor, saved content, and the whole editing-session transcript/summary. Restore operations create a normal new version with edit request `Restored from version N`.

## Runtime skill/reference read traces

All runtime `readSkill` and `readReferenceDoc` calls are traced. Agent Admin shows trace metadata directly and may also link to Audit/Trace.

Visible Agent Admin trace fields:

- agent name;
- skill/reference doc read;
- timestamp;
- request/session id;
- user/customer context.

Trace filters: agent, skill/reference doc, time range.

Trace rows do not show the full skill/reference content that was read. Authorized SaaS admins can open the current doc from Agent Admin when they need to inspect content.
