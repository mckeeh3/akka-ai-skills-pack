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
