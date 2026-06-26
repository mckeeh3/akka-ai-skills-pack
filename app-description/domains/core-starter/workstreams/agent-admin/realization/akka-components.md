# Realization: Akka components for Agent Admin

Capability: `agent-doc-administration`.

This map is docs-only. It describes the intended component responsibilities for current Agent Admin intent: AI-assisted behavior-document proposal, review, activation, and runtime-profile inspection.

## Component responsibilities

| Intent binding | Akka / Java responsibility |
|---|---|
| Agent registry/profile view | Durable/read model for existing agents, names, purposes, placement, lifecycle status, steward, authority level, safe model alias summary, safe tool-boundary summary, workstream/domain grouping, and last behavior change time. Agent Admin can update name/purpose but not create/delete whole agents. |
| Agent prompt docs | Durable versioned document state for each agent's required prompt. Supports current/historical reads, draft/proposal versions, review/activation, restore proposals, and version-to-previous diff. |
| Agent skill docs | Durable skill collection under each agent. Supports create, read, update through editing proposals, delete/deprecate according to lifecycle policy, version history, restore proposals, and manifest membership. |
| Governed references | Durable `ReferenceDocument` records associated with skills/reference manifests. Supports create, read, update through editing proposals, delete/deprecate according to lifecycle policy, version history, restore proposals, and short description/when-to-consult hints for model read selection. |
| Editing agent | Model-backed agent that reads current artifact plus relevant same-agent context, preserves Markdown/structure, asks clarifying questions, drafts proposed full content, structured proposal metadata, risk classification, authority-expansion flags, and suggested tests/replay evidence. |
| Proposal/edit-session state | Durable or request-correlated state for transcript, base version, proposed output, proposed diff, summary/rationale, risk, authority-expansion flags, Save Draft/Review/Approve/Reject/Activate/Cancel outcome, actor, and timestamps. |
| Runtime doc loader | Resolves active AgentDefinition, prompt, compact skill/reference manifests, model policy, selected AuthContext, and ToolPermissionBoundary; provides governed `readSkill` and `readReferenceDoc` tools only for assigned active artifacts. |
| Runtime read trace sink | Records prompt assembly, `readSkill`, `readReferenceDoc`, model-policy, and tool-boundary metadata and exposes it to Agent Admin trace surfaces. |

## Existing implementation caveat

Existing governed prompt/skill/reference entities, runtime loader tools, behavior proposal/review components, and trace entities may provide useful substrate if they are reconciled to this current-intent graph. Existing direct-save, provider-secret/model settings, tool-boundary mutation, seed import, whole-agent activation, and rollback implementation paths should not drive the user-facing workstream unless reintroduced explicitly.

## Validation evidence to update

Backend tests should prove SaaS-admin-only access, safe profile summaries, current/latest edit input, immutable draft/proposal/active versions, restore proposals, version-to-previous diffs, Markdown-preserving structured editing-agent proposals, Save Draft/Review/Approve/Reject/Activate/Cancel, skill/reference deletion or deprecation, runtime active-doc loading, model/tool-boundary fail-closed behavior, and runtime prompt/skill/reference read traces.
