# Realization: API contracts for Agent Admin

Capability: `agent-doc-administration`.

## Browser/API intent

Agent Admin APIs should expose SaaS-admin-only all-agent behavior-profile inspection, document browsing, AI-assisted behavior-change proposals, review/activation, immutable versioning, tenant-scoped skill/reference lifecycle, model config reference changes, per-agent skill and generated-tool assignment changes, runtime profile/doc/tool loading, and read trace visibility.

## Required API contract areas

| Contract area | Obligations |
|---|---|
| Agent list | List/filter all agents by agent name, workstream/domain, placement, lifecycle status, steward, authority level, and scope provenance; return agent name, short purpose, placement, lifecycle status, safe model alias summary, resolved profile scope, and last behavior change time. |
| Agent detail | Read generated agent identity/provenance; inspect safe behavior-profile, manifest, model, allowed generated tool, and tool-boundary summaries; list prompt, assigned skills, governed references, profile version summaries, proposal summaries, and trace links. |
| Agent behavior profiles | Read current/historical profile versions; change approved `ModelConfigRef`; assign/unassign independently managed skills; assign/unassign static generated tools; create tenant-scoped profile clone/version on first tenant change; restore historical profile versions through proposal/activation. |
| Prompt docs | Read current/historical prompt versions; start/revise/cancel/save draft AI-assisted prompt proposals; review/approve/reject/activate proposals; create restore proposals from historical versions. |
| Skill docs | Create/read/update/deprecate/remove tenant-scoped skills independent of specific agents; read current/historical skill versions; start/revise/cancel/save draft AI-assisted skill proposals; review/approve/reject/activate proposals; create restore proposals. |
| Governed references | Create/read/update/deprecate/delete references associated with skills/reference manifests; read current/historical reference versions; start/revise/cancel/save draft AI-assisted reference proposals; review/approve/reject/activate proposals; create restore proposals. |
| Edit sessions and proposals | Maintain transcript of all user instructions, editing-agent structured proposal output, proposed diff, summary/rationale, risk classification, authority-expansion flags, suggested tests/replay evidence, base current version, and Save Draft/Review/Approve/Reject/Activate/Cancel outcome. |
| Version history | Store simple integer versions; row listing can show version number only; version detail includes content, created time, actor, edit request/transcript summary. |
| Diffs | Diff selected version `N` only against `N-1`; version 1 returns no-prior-version. Proposed edit diff compares proposal to current base version. |
| Runtime loading | Each agent request resolves tenant-specific active behavior profile when present, otherwise the global active profile, then active prompt, compact skill/reference manifests, model policy, selected `AuthContext`, allowed generated tool list, and `ToolPermissionBoundary`; `readSkill` and `readReferenceDoc` load only authorized assigned active content; generated tool calls must be allowed by the resolved profile and backend boundary. |
| Runtime traces | Trace profile resolution, prompt assembly, model-policy decisions, `readSkill`, `readReferenceDoc`, generated-tool assignment decisions, and tool-boundary decisions; expose Agent Admin trace metadata: agent name, resolved profile scope/version, prompt/skill/reference doc read, generated tool decision, version/checksum where allowed, safe model alias, decision category, timestamp, request/session id, user/customer context. |

## Authorization obligations

All Agent Admin APIs require SaaS Owner/Admin authorization. SaaS app owners operate in the reserved `saas-app-owner` tenant scope for behavior overrides. Authorized SaaS admins can view full doc content for their authorized scope. Trace rows do not include full read content. Provider secrets, hidden platform instructions, generated tool implementation internals beyond safe summaries, and unapproved tool-boundary internals never appear in browser/API payloads.

## Consistency obligations

Edit input and save draft apply only to the current/latest editable draft or active document. Backend consistency checks handle stale current versions and stale proposals. Historical versions are read-only. Restore creates a restore proposal. Runtime behavior changes only after explicit backend activation of an approved/reviewed proposal; low-risk review-and-activate may be a single protected command but must still emit proposal and activation trace facts.

## Out-of-scope API areas

Agent Admin does not own provider-secret administration, raw model setting mutation beyond selecting approved model config references, generated tool code creation/edit/deletion, backend authorization implementation changes, whole-agent lifecycle management, seed import workflows, or whole-agent creation/deletion. It does own behavior-profile and behavior-document proposal/review/activation semantics for model config references, prompts, independently managed skills, skill assignments, generated tool assignments, and governed references.
