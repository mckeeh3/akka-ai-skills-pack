# Realization: Akka components for Agent Admin

Capability: `agent-doc-administration`.

This map is docs-only. It describes the intended component responsibilities for current Agent Admin intent: AI-assisted all-agent behavior-profile/document proposal, review, activation, runtime-profile inspection, and static generated-agent/generated-tool assignment governance.

## Component responsibilities

| Intent binding | Akka / Java responsibility |
|---|---|
| Agent registry/profile view | Durable/read model for all static app-description-generated agents, purposes, placement, lifecycle status, steward, authority level, safe model alias summary, resolved profile scope, safe generated tool/tool-boundary summary, workstream/domain grouping, and last behavior change time. Agent Admin can manage behavior profiles but not create/delete whole agents. |
| Agent behavior profiles | Durable versioned state for model config reference, prompt version reference, assigned skills, allowed generated tools, scope, and provenance. Supports global app-description defaults, tenant-scoped clone/version on first tenant change, reserved `saas-app-owner` tenant scope, current/historical reads, proposal review/activation, and restore proposals. |
| Agent prompt docs | Durable versioned document state for each agent's required prompt. Supports current/historical reads, draft/proposal versions, review/activation, restore proposals, and version-to-previous diff. |
| Agent skill docs | Durable tenant-scoped skill library independent of specific agents. Supports create, read, update through editing proposals, deprecate by default or hard delete according to lifecycle policy, version history, restore proposals, assigned-agent counts, and manifest membership. |
| Governed references | Durable `ReferenceDocument` records associated with skills/reference manifests. Supports create, read, update through editing proposals, delete/deprecate according to lifecycle policy, version history, restore proposals, and short description/when-to-consult hints for model read selection. |
| Editing agent | Model-backed agent that reads current artifact plus relevant same-agent context, preserves Markdown/structure, asks clarifying questions, drafts proposed full content, structured proposal metadata, risk classification, authority-expansion flags, and suggested tests/replay evidence. |
| Proposal/edit-session state | Durable or request-correlated state for transcript, base version, proposed output, proposed diff, summary/rationale, risk, authority-expansion flags, Save Draft/Review/Approve/Reject/Activate/Cancel outcome, actor, and timestamps. |
| Runtime doc/tool loader | Resolves tenant-specific active behavior profile when present, otherwise global active behavior profile, then prompt, compact skill/reference manifests, model policy, selected AuthContext, allowed generated tool list, and ToolPermissionBoundary; provides governed `readSkill` and `readReferenceDoc` tools only for assigned active artifacts and permits only generated tools allowed by the resolved profile and backend boundary. |
| Runtime read trace sink | Records profile resolution, prompt assembly, `readSkill`, `readReferenceDoc`, generated-tool assignment decisions, model-policy, and tool-boundary metadata and exposes it to Agent Admin trace surfaces. |

## Existing implementation caveat

Existing governed prompt/skill/reference entities, runtime loader tools, behavior profile/proposal/review components, and trace entities may provide useful substrate if they are reconciled to this current-intent graph. Existing direct-save, provider-secret/raw model settings, generated tool code mutation, backend tool-boundary implementation mutation, seed import, whole-agent activation, and rollback implementation paths should not drive the user-facing workstream unless reintroduced explicitly.

## Validation evidence to update

Backend tests should prove SaaS-admin-only access, all-agent registry coverage, safe profile summaries, global-to-tenant clone/version behavior, current/latest edit input, immutable draft/proposal/active versions, restore proposals, version-to-previous diffs, Markdown-preserving structured editing-agent proposals, Save Draft/Review/Approve/Reject/Activate/Cancel, skill/reference deletion or deprecation, runtime active profile/doc/tool loading, model/tool-boundary fail-closed behavior, and runtime profile/prompt/skill/reference/tool read traces.
