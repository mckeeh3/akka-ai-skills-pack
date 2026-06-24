# Realization: API contracts for Agent Admin

Capability: `agent-doc-administration`.

## Browser/API intent

Agent Admin APIs should expose SaaS-admin-only agent document browsing, AI-assisted editing sessions, immutable versioning, skill/reference lifecycle, runtime doc loading, and read trace visibility.

## Required API contract areas

| Contract area | Obligations |
|---|---|
| Agent list | List/filter all agents by agent name and workstream/domain; return agent name, short purpose, last edit time. |
| Agent detail | Read/update agent name and purpose; list prompt, skills, reference docs, version summaries, and trace links. |
| Prompt docs | Read current/historical prompt versions; start/revise/cancel/save AI-assisted prompt edit sessions; restore historical versions. |
| Skill docs | Create/read/update/delete skills; read current/historical skill versions; start/revise/cancel/save AI-assisted skill edit sessions; restore historical versions. |
| Skill reference docs | Create/read/update/delete reference docs under a skill; read current/historical reference versions; start/revise/cancel/save AI-assisted reference edit sessions; restore historical versions. |
| Edit sessions | Maintain transcript of all user instructions, editing-agent proposed output, summary, warnings/risks, base current version, and Save/Cancel outcome. |
| Version history | Store simple integer versions; row listing can show version number only; version detail includes content, created time, actor, edit request/transcript summary. |
| Diffs | Diff selected version `N` only against `N-1`; version 1 returns no-prior-version. Proposed edit diff compares proposal to current base version. |
| Runtime loading | Each agent request loads current prompt and skill names/descriptions; `readSkill` returns skill plus reference doc names/descriptions; `readReferenceDoc` reads a selected reference doc. |
| Runtime traces | Trace every `readSkill` / `readReferenceDoc`; expose Agent Admin trace metadata: agent name, skill/reference doc read, timestamp, request/session id, user/customer context. |

## Authorization obligations

All Agent Admin APIs require SaaS Owner/Admin authorization. Tenant/org/customer-scoped Agent Admin access is removed. Authorized SaaS admins can view full doc content. Trace rows do not include full read content.

## Consistency obligations

Edit input and save apply only to the current/latest version. Backend consistency checks handle stale current versions. Historical versions are read-only. Restore creates a new current version. Save immediately updates runtime behavior; there is no separate activation/publish API.

## Out-of-scope API areas

Agent Admin no longer owns model settings, tool permission administration, tenant/org scoped governance, separate activation/rollback lifecycles, prompt-risk approval blockers, seed import workflows, or whole-agent creation/deletion.
