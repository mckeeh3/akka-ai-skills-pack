# Realization: API contracts for Agent Admin

Capability: `agent-doc-administration`.

## Browser/API intent

Agent Admin APIs should expose SaaS-admin-only agent behavior-profile inspection, document browsing, AI-assisted behavior-change proposals, review/activation, immutable versioning, skill/reference lifecycle, runtime doc loading, and read trace visibility.

## Required API contract areas

| Contract area | Obligations |
|---|---|
| Agent list | List/filter all agents by agent name, workstream/domain, placement, lifecycle status, steward, and authority level; return agent name, short purpose, placement, lifecycle status, safe model alias summary, and last behavior change time. |
| Agent detail | Read/update agent name and purpose; inspect safe behavior-profile, manifest, model, and tool-boundary summaries; list prompt, skills, governed references, version summaries, proposal summaries, and trace links. |
| Prompt docs | Read current/historical prompt versions; start/revise/cancel/save draft AI-assisted prompt proposals; review/approve/reject/activate proposals; create restore proposals from historical versions. |
| Skill docs | Create/read/update/deprecate/delete skills; read current/historical skill versions; start/revise/cancel/save draft AI-assisted skill proposals; review/approve/reject/activate proposals; create restore proposals. |
| Governed references | Create/read/update/deprecate/delete references associated with skills/reference manifests; read current/historical reference versions; start/revise/cancel/save draft AI-assisted reference proposals; review/approve/reject/activate proposals; create restore proposals. |
| Edit sessions and proposals | Maintain transcript of all user instructions, editing-agent structured proposal output, proposed diff, summary/rationale, risk classification, authority-expansion flags, suggested tests/replay evidence, base current version, and Save Draft/Review/Approve/Reject/Activate/Cancel outcome. |
| Version history | Store simple integer versions; row listing can show version number only; version detail includes content, created time, actor, edit request/transcript summary. |
| Diffs | Diff selected version `N` only against `N-1`; version 1 returns no-prior-version. Proposed edit diff compares proposal to current base version. |
| Runtime loading | Each agent request resolves active `AgentDefinition`, active prompt, compact skill/reference manifests, model policy, selected `AuthContext`, and `ToolPermissionBoundary`; `readSkill` and `readReferenceDoc` load only authorized assigned active content. |
| Runtime traces | Trace prompt assembly, model-policy decisions, `readSkill`, `readReferenceDoc`, and tool-boundary decisions; expose Agent Admin trace metadata: agent name, prompt/skill/reference doc read, version/checksum where allowed, safe model alias, decision category, timestamp, request/session id, user/customer context. |

## Authorization obligations

All Agent Admin APIs require SaaS Owner/Admin authorization. Tenant/org/customer-scoped Agent Admin access is out of scope. Authorized SaaS admins can view full doc content. Trace rows do not include full read content. Provider secrets, hidden platform instructions, and unapproved tool-boundary internals never appear in browser/API payloads.

## Consistency obligations

Edit input and save draft apply only to the current/latest editable draft or active document. Backend consistency checks handle stale current versions and stale proposals. Historical versions are read-only. Restore creates a restore proposal. Runtime behavior changes only after explicit backend activation of an approved/reviewed proposal; low-risk review-and-activate may be a single protected command but must still emit proposal and activation trace facts.

## Out-of-scope API areas

Agent Admin does not own provider-secret administration, direct model setting mutation, direct tool permission administration, tenant/org scoped governance, whole-agent lifecycle management, seed import workflows, or whole-agent creation/deletion. It does own behavior-document proposal/review/activation semantics for prompts, skills, and governed references.
