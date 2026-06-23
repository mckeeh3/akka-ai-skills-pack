# Workstream: Agent Admin

## Purpose

Govern managed-agent definitions, prompts, skills, references, manifests, tool boundaries, model refs, seed imports, behavior proposals, activation, rollback, and runtime traces.

## Functional agent

Owns `agent-admin-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions.

## Capability binding

Primary capability: `../../capabilities/managed-agent-governance.md`. Access is available to SaaS Owner/App Admin selected contexts for platform-level managed agents and to tenant/organization administrator selected contexts (`TENANT_ADMIN` / `tenant-admin`) for tenant/organization-scoped managed agents, always with explicit `agent_admin.*` capabilities. Customer-scoped admins are not Agent Admin operators.

## Attention model

Backend-owned attention includes stable categories `agent_admin.proposal.review_needed`, `agent_admin.activation.approval_required`, `agent_admin.rollback.available`, `agent_admin.seed_import.blocked`, `agent_admin.provider_model.blocked`, `agent_admin.tool_boundary.denied`, `agent_admin.manifest.drift`, `agent_admin.loader.denied`, and `agent_admin.authority_expansion.risk`. Producers are AgentDefinition/behavior proposal state changes, prompt-risk review tasks, seed-import tasks, loader/tool-boundary denials, model/provider readiness checks, and activation/rollback confirmation flows. Each attention item has a backend idempotency key formed from selected scope, managed-agent id, source object/version, category, and current lifecycle status; severity is backend-authored (`info`, `needs_review`, `approval_required`, `blocked`, `risk`) and terminal or resolved source states clear or downgrade the item. Counts feed the left rail and, where the signed-in human is the reviewer/steward or directly assigned actor, My Account aggregation without exposing hidden agents, versions, scopes, prompts, skills, references, model refs, or counts.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation and model/provider fail-closed proof where applicable.


## Confirmed human chat tool-plan exposure

This workstream exposes a bounded `human_chat_tool_plan` adapter for execution-oriented chat prompts after deterministic no-mutation surface routing declines the prompt. The adapter is current-intent only until runtime tasks implement it. It allows `agent-admin-agent` to propose a plan for the representative prompt **start prompt risk review for the Agent Admin prompt proposal**, but it never permits prompt-only mutation, hidden target enumeration, or AI-autonomous authority.

Execution is allowed only when all of the following hold: the proposal was created with `noMutation=true`; the human explicitly confirms the exact plan snapshot; the backend reauthorizes the selected `AuthContext`, actor, capability, tool boundary, lifecycle state, approval policy, tenant/customer ownership, and idempotency on every step; and each step executes through its declared governed surface/action path as a separate transaction boundary.

Representative catalog binding: actions `action-agent-prompt-risk-review-start`; governed tool ids `agent_admin.start_behavior_review_task`; capabilities `agent_admin.start_behavior_review_task`; input contract `schema.agent-admin.prompt-risk-review.start.v1` with visible `agentDefinitionId`, `proposalId`, redacted `artifactDeltas`, reason, and idempotency key; expected result surfaces `surface-agent-admin-prompt-risk-review`. The allowed effect is to start or read a governed prompt-risk review task for a visible proposal; it cannot accept results, approve behavior, activate/rollback/deactivate an agent, or fabricate model-backed review success.
