# Realization: API contracts for Agent Admin

Capability/foundation scope: managed-agent governance (`agent-doc-administration` legacy mapping retained in source alignment).

## Browser/API intent

Agent Admin APIs expose SaaS-admin-only managed-agent catalog/detail, behavior-profile inspection, governed document browsing, AI-assisted behavior-change proposals, review/approval/activation, immutable versioning, skill/reference lifecycle, manifest assignment, model-policy selection, generated-tool/tool-boundary assignment, safe test-console runs, runtime loader decisions, and trace visibility.

## Required API contract areas

| Contract area | Obligations |
|---|---|
| Dashboard attention | Return behavior-change proposal counts, approval-required counts, provider/config blockers, denied loader/tool-boundary events, actionable agent count, and recently changed agents; counts open filtered surfaces and never activate behavior. |
| Agent catalog | List/filter generated agents by name, workstream/domain, placement, lifecycle, steward, authority, model-policy alias, and scope provenance; return safe summaries and attention badges. |
| Agent detail | Read `AgentDefinition` identity/provenance; inspect safe behavior-profile, manifest, model-policy, generated-tool, tool-boundary, test-console, proposal, and trace summaries. |
| Agent behavior profiles | Read current/historical profile versions; change approved `ModelConfigRef`; assign/unassign skills/references/generated tools/tool-boundary refs; create tenant-scoped clone/version on first tenant change; restore historical profile versions through proposal/activation. |
| Prompt docs | Read current/historical prompt versions; start/revise/cancel/save draft AI-assisted prompt proposals; review/approve/reject/activate proposals; create restore proposals; expose prompt assembly trace links. |
| Skill docs | Create/read/update/deprecate/remove tenant-scoped skills independent of specific agents; manage versions and manifests; authorize `readSkill`; expose `SkillLoadTrace` links. |
| Governed references | Create/read/update/deprecate/delete references; manage versions and reference manifests; authorize `readReferenceDoc`; expose `ReferenceLoadTrace` links. |
| Manifests | Assign/unassign `AgentSkillManifest` and `AgentReferenceManifest` entries via behavior-profile proposals; preview compact manifest context; deny hidden/orphan loader access. |
| Model policy | Select only approved model config aliases; never expose provider secrets; fail closed with provider/config blocker when runtime/provider config is missing/inactive/unauthorized. |
| Tool boundary | Read safe generated-tool and `ToolPermissionBoundary` summaries; assign generated tools/boundary refs through proposal/profile versions; enforce adapter-specific allow/deny/approval-required decisions. |
| Test console | Run authorized test/replay/evaluation preflights using selected profile/doc/manifest/model/boundary state; block side effects by default; return success, provider/config blocker, loader/tool-boundary denial, or partial-failure result with traces. |
| Edit sessions/proposals | Maintain transcript, base version, structured proposal output, diff, rationale, risk, authority flags, suggested tests, expected result surface, Save/Submit/Approve/Reject/Activate/Cancel outcome, idempotency, and trace links. |
| Version history/diffs | Store simple integer versions; list rows; version detail includes content or profile delta, created time, actor, transcript summary; diff selected version `N` only against `N-1`; proposal diff compares proposal to base. |
| Runtime loading | Resolve active profile, prompt, compact manifests, model policy, selected `AuthContext`, provider config, generated-tool list, and `ToolPermissionBoundary`; `readSkill` / `readReferenceDoc` load only assigned active content; generated tool calls require resolved profile plus backend boundary. |
| Runtime traces | Expose profile-resolution, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, model-policy/provider decisions, generated-tool decisions, tool-boundary decisions, test-console facts, and `AgentWorkTrace` metadata with protected redaction. |

## Authorization obligations

All Agent Admin APIs require SaaS Owner/Admin authorization. SaaS app owners operate in the reserved `saas-app-owner` tenant scope. Browser-visible capabilities are not authorization. Backend checks enforce selected AuthContext, tenant scope, current-version consistency, proposal lifecycle, explicit chat confirmation, approval requirements, provider/model availability, loader assignments, and tool-boundary decisions.

Authorized SaaS admins can view full doc content for their authorized scope. Trace rows do not include full loaded content by default. Provider secrets, hidden platform instructions, generated tool implementation internals beyond safe summaries, raw model credentials, arbitrary backend class names, and unapproved tool-boundary internals never appear in browser/API payloads.

## Consistency obligations

Edit input and Save Draft apply only to the current/latest editable draft or active document/profile. Backend consistency checks handle stale current versions and stale proposals. Historical versions are read-only. Restore creates a restore proposal. Runtime behavior changes only after explicit activation of an approved/reviewed proposal. Repeated save/activate/confirm/test requests with the same idempotency key return the existing result/no-op or prior test result rather than duplicate side effects.

## Out-of-scope API areas

Agent Admin does not own provider-secret administration, raw model setting mutation beyond selecting approved model config references, generated tool code creation/edit/deletion, backend authorization implementation changes, whole-agent lifecycle management, seed import workflows, or whole-agent creation/deletion. It owns behavior-profile and behavior-document proposal/review/activation semantics for model config references, prompts, independently managed skills, references, manifests, generated-tool assignments, tool-boundary references, safe test-console runs, and trace reads.
