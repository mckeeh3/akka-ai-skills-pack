# Realization: Akka components for Agent Admin

Capability/foundation scope: managed-agent governance (`agent-doc-administration` remains the legacy source-alignment capability artifact until shared capability naming is reconciled).

This map is docs-only. It describes intended component responsibilities for current Agent Admin intent: SaaS-admin-only managed-agent catalog/detail, behavior-profile/document proposal, review, approval, activation, runtime-profile inspection, model-policy/tool-boundary governance, safe test-console execution, and runtime trace visibility.

## Component responsibilities

| Intent binding | Akka / Java responsibility |
|---|---|
| Agent registry/profile view | Durable/read model for static app-description-generated `AgentDefinition` records, purposes, placement, lifecycle, steward, authority, safe model alias, resolved profile scope, generated-tool/tool-boundary summaries, attention badges, workstream/domain grouping, and last behavior change time. Agent Admin can manage behavior profiles but not create/delete whole agents. |
| Agent behavior profiles | Durable versioned state for model config reference, prompt version reference, skill/reference manifest entries, generated-tool assignment, tool-boundary reference, scope, provenance, approval basis, and idempotency keys. Supports global defaults, tenant-scoped clone/version on first tenant change, reserved `saas-app-owner` scope, current/historical reads, proposal review/activation, no-op repeat activation, and restore proposals. |
| Agent prompt docs | Durable versioned `PromptDocument` / `PromptVersion` state for each managed agent. Supports current/historical reads, draft/proposal versions, review/approval/activation, restore proposals, version-to-previous diff, prompt assembly lookup, and `PromptAssemblyTrace`. |
| Agent skill docs | Durable tenant-scoped `SkillDocument` / `SkillVersion` library independent of specific agents. Supports create/read/update through editing proposals, deprecate/hard delete policy, version history, restore proposals, assigned-agent counts, manifest membership, `readSkill` authorization, and `SkillLoadTrace`. |
| Governed references | Durable `ReferenceDocument` / `ReferenceVersion` records associated with reference manifests. Supports create/read/update through editing proposals, delete/deprecate policy, version history, restore proposals, when-to-consult hints, `readReferenceDoc` authorization, and `ReferenceLoadTrace`. |
| Manifest/profile governance | Durable or event-backed `AgentSkillManifest` / `AgentReferenceManifest` assignment changes as behavior-profile proposals/versions, separate from document version changes. |
| Model policy governance | Approved `ModelConfigRef` / model-policy selection and safe alias summary. Provider secrets remain server-side. Missing/inactive provider config fails closed as provider/config blocker. |
| Tool-boundary governance | Generated-tool assignment and `ToolPermissionBoundary` reference/version changes through behavior-profile proposals. Runtime tool exposure validates governed tool id, adapter/source, AuthContext, and scope before use. |
| Editing agent | Model-backed `AgentBehaviorEditorAgent` that resolves its own active profile and drafts structured `BehaviorChangeProposal` output with proposed content/delta, diff, rationale, risk, authority flags, suggested tests, and result surface. Fails closed when provider/runtime config is unavailable. |
| Proposal/edit-session state | Durable or request-correlated state for transcript, base version, proposed output/delta, diff, risk, authority flags, suggested tests, Save/Submit/Approve/Reject/Activate/Cancel/Test outcome, actor, confirmation id, and timestamps. |
| Test-console runtime | Safe test/replay/evaluation runner that assembles selected profile/prompt/manifest/model/tool-boundary state, invokes provider only when authorized and configured, blocks production side effects by default, and emits test-console and runtime-loader traces. |
| Runtime doc/tool loader | Resolves tenant-specific active behavior profile when present, otherwise global active profile, then prompt, compact manifests, model policy, selected AuthContext, provider availability, generated-tool assignment, and `ToolPermissionBoundary`; provides `readSkill` and `readReferenceDoc` only for assigned active artifacts and permits generated tools only when the resolved profile and backend boundary allow. |
| Runtime read trace sink | Records profile resolution, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, generated-tool assignment decisions, provider/model-policy fail-closed decisions, `ToolPermissionBoundary` decisions, test-console run facts, and `AgentWorkTrace` metadata for Agent Admin trace surfaces. |

## Existing implementation caveat

Existing governed prompt/skill/reference entities, runtime loader tools, behavior profile/proposal/review components, and trace entities may provide useful substrate if reconciled to this current-intent graph. Existing direct-save, provider-secret/raw model settings, generated tool code mutation, backend tool-boundary implementation mutation, seed import, whole-agent activation, and rollback implementation paths should not drive the user-facing workstream unless reintroduced explicitly.

## Validation evidence to update

Backend runtime-validation should prove SaaS-admin-only access, all-agent registry coverage, safe profile summaries, global-to-tenant clone/version behavior, current/latest edit input, immutable drafts/proposals/active versions, restore proposals, version-to-previous diffs, Markdown-preserving structured editing-agent proposals, Save/Submit/Review/Approve/Reject/Activate/Cancel, skill/reference deletion or deprecation, manifest assignment, model-policy selection, tool-boundary assignment, test-console provider fail-closed behavior, runtime active profile/doc/tool loading, loader/tool-boundary denials, partial-failure result surfaces, and runtime `PromptAssemblyTrace`/`SkillLoadTrace`/`ReferenceLoadTrace`/`AgentWorkTrace` visibility.
