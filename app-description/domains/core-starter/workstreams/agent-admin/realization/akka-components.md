# Realization: Akka components for Agent Admin

Capability: `agent-doc-administration`.

This map is docs-only. It describes the intended component responsibilities after the Agent Admin intent shift to AI-assisted agent-document editing.

## Component responsibilities

| Intent binding | Akka / Java responsibility |
|---|---|
| Agent registry | Durable/read model for existing agents, names, purposes, workstream/domain grouping, and last edit time. Agent Admin can update name/purpose but not create/delete whole agents. |
| Agent prompt docs | Durable versioned document state for each agent's required prompt. Supports current/historical reads, save new version, restore historical version, and version-to-previous diff. |
| Agent skill docs | Durable skill collection under each agent. Supports create, read, update through editing sessions, permanent delete, version history, restore, and reference doc containment. |
| Skill reference docs | Durable reference docs under a skill. Supports create, read, update through editing sessions, permanent delete, version history, restore, and short description for model read selection. |
| Editing agent | Model-backed agent that reads current doc plus relevant same-agent context, preserves Markdown/structure, asks clarifying questions, drafts proposed full content, summaries, and advisory warnings/risks. |
| Edit-session state | Durable or request-correlated state for transcript, base version, proposed output, summary, risks, Save/Cancel outcome, actor, and timestamps. |
| Runtime doc loader | Loads current prompt plus skill names/descriptions for every agent request; provides `readSkill` and `readReferenceDoc` tools to all agents. |
| Runtime read trace sink | Records `readSkill` and `readReferenceDoc` metadata and exposes it to Agent Admin trace surfaces. |

## Existing implementation caveat

Existing governed prompt/skill/reference entities, runtime loader tools, and trace entities may provide useful substrate. Existing behavior proposal, prompt-risk review, model settings, tool-boundary, seed import, activation, and rollback implementation paths are no longer the primary Agent Admin intent and should not drive the user-facing workstream unless reintroduced explicitly.

## Validation evidence to update

Backend tests should prove SaaS-admin-only access, current-version-only editing, immutable versions, restore semantics, version-to-previous diffs, Markdown-preserving editing-agent proposals, Save/Cancel, skill/reference permanent deletion, runtime current-doc loading, and runtime skill/reference read traces.
