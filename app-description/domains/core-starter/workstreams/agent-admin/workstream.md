# Workstream: Agent Admin

## Purpose

Allow authorized users to manage AI-assisted editing of managed-agent documents: prompts, skills, references, and related agent behavior docs. Edits are interpreted and drafted by an editing agent, reviewed by the user as a proposed document version, and retained as immutable version history with diffs.

## Description

Agent Admin is the workstream where authorized users edit the documents that define managed-agent behavior, especially agent prompts, skills, references, and related agent docs. Its default experience must feel like an AI-assisted document editing workspace, not a compliance console or raw prompt editor.

Editing is intentionally not direct text editing. The user describes the desired change in natural language, and an editing agent interprets the request, identifies the current agent document version, drafts the actual document changes, and presents a proposed new version for review. The user reviews the proposed changes, including a clear summary and diff, before accepting, rejecting, or asking for further revision.

Each accepted agent-doc change creates a new immutable version. The system retains all prior versions. Users can browse or scroll through version history, inspect a specific version, and request the diff for that version against its immediate predecessor; for example, if the user scrolls from version 10 to version 7, the diff shown for version 7 is between version 7 and version 6. Each version records when it was created, who made the change, and the edit request that triggered the change. Edit request input is enabled only on the current/latest version; historical versions are read-only and must clearly indicate that edits cannot be started from that historical view.

The primary journey starts on `surface-agent-admin-dashboard`, opens an agent document catalog or detail view, and then guides the user into one focused AI-assisted edit flow at a time: choose the agent doc, describe the change, review the editing agent's proposed version, inspect the diff, and accept or request revision. Readiness, testing, activation, rollback, and trace review remain supporting flows, but they should not dominate the default editing experience.

`agent-admin-agent` assists by translating the user's intent into understandable proposed document changes: what changed, why it changed, which document/version it affects, and what risk or follow-up review is relevant. It may draft and revise proposed versions within the selected backend `AuthContext`, but it cannot grant authority, reveal protected content to unauthorized users, skip required review, activate behavior without the required lifecycle path, overwrite tenant customizations, or claim provider/model success when configuration is missing.

The intended outcome is an Agent Admin workstream that makes managed-agent behavior editable by authorized humans through AI assistance: users do not need to understand internal governance mechanics to improve prompts and skills, but every accepted change remains versioned, reviewable, auditable, and recoverable.

## Functional agent

Owns `agent-admin-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions.

## Capability binding

Primary capability: `../../capabilities/managed-agent-governance.md`. Access is available to SaaS Owner/App Admin selected contexts for platform-level managed agents and to tenant/organization administrator selected contexts (`TENANT_ADMIN` / `tenant-admin`) for tenant/organization-scoped managed agents, always with explicit `agent_admin.*` capabilities. Customer-scoped admins are not Agent Admin operators.

## Attention model

Backend-owned attention includes stable categories `agent_admin.proposal.review_needed`, `agent_admin.activation.approval_required`, `agent_admin.rollback.available`, `agent_admin.seed_import.blocked`, `agent_admin.provider_model.blocked`, `agent_admin.tool_boundary.denied`, `agent_admin.manifest.drift`, `agent_admin.loader.denied`, and `agent_admin.authority_expansion.risk`. Producers are AgentDefinition/behavior proposal state changes, prompt-risk review tasks, seed-import tasks, loader/tool-boundary denials, model/provider readiness checks, and activation/rollback confirmation flows. Each attention item has a backend idempotency key formed from selected scope, managed-agent id, source object/version, category, and current lifecycle status; severity is backend-authored (`info`, `needs_review`, `approval_required`, `blocked`, `risk`) and terminal or resolved source states clear or downgrade the item. Counts feed the left rail and, where the signed-in human is the reviewer/steward or directly assigned actor, My Account aggregation without exposing hidden agents, versions, scopes, prompts, skills, references, model refs, or counts.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation and model/provider fail-closed proof where applicable.


## Confirmed human chat tool-plan exposure

This workstream exposes a bounded `human_chat_tool_plan` adapter for execution-oriented chat prompts after deterministic no-mutation surface routing declines the prompt. The first-pass runtime path is implemented through backend-owned plan proposal, exact snapshot confirmation, catalog validation, dispatcher reauthorization, idempotency, and trace surfaces. It allows `agent-admin-agent` to propose a plan for the representative prompt **start prompt risk review for the Agent Admin prompt proposal**, but it never permits prompt-only mutation, hidden target enumeration, or AI-autonomous authority.

Execution is allowed only when all of the following hold: the proposal was created with `noMutation=true`; the human explicitly confirms the exact plan snapshot; the backend reauthorizes the selected `AuthContext`, actor, capability, tool boundary, lifecycle state, approval policy, tenant/customer ownership, and idempotency on every step; and each step executes through its declared governed surface/action path as a separate transaction boundary.

Representative catalog binding: actions `action-agent-prompt-risk-review-start`; governed tool ids `agent_admin.start_behavior_review_task`; capabilities `agent_admin.start_behavior_review_task`; input contract `schema.agent-admin.prompt-risk-review.start.v1` with visible `agentDefinitionId`, `proposalId`, redacted `artifactDeltas`, reason, and idempotency key; expected result surfaces `surface-agent-admin-prompt-risk-review`. The allowed effect is to start or read a governed prompt-risk review task for a visible proposal; it cannot accept results, approve behavior, activate/rollback/deactivate an agent, or fabricate model-backed review success.
