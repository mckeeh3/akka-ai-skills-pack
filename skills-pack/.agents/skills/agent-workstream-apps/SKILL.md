---
name: agent-workstream-apps
description: Interpret generated full-stack AI-first SaaS apps as role-authorized functional-agent workstream applications, then route to app-description, capability-first backend, web UI, agent, and Akka decomposition skills.
---

# Agent Workstream Apps

Use this skill when a generated or extended SaaS app should be modeled as a set of role-authorized functional/context-area agents with continuous workstreams and structured surfaces, rather than as a page-first CRUD console or generic chatbot. In the Interview phase, this skill defines or reviews description-ready workstream bindings in the living app-description graph. In Build/compile handoffs, preserve those bindings so workers, execution harnesses, actor adapters, governed tools, policies, traces, capabilities, and realization artifacts remain connected.

## Lifecycle classification

- Phase role: Interview-phase workstream modeling with Build/compile handoff constraints.
- Graph layer: workstream, functional-agent worker, structured surface, attention, governed-tool, trace, and realization bindings.
- Canonical chain: `worker → execution harness → actor adapter → governed tool → capability → Akka implementation`.

## Required reading

- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/app-development-lifecycle.md`
- `../docs/app-worker-tool-model.md`
- `../docs/app-description-component-graph.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/workforce-decomposition.md`
- `../docs/workstream-contract.md`
- `../docs/workstream-attention-contracts.md`
- `../docs/workstream-ui-reference-architecture.md`
- `../docs/structured-surface-contracts.md`
- `../docs/workstream-surface-intent-routing.md`
- `../docs/capability-first-backend-architecture.md`
- `../docs/generated-saas-canonical-doctrine.md`
- `../docs/minimum-ai-first-saas-app.md` when foundation scope is involved

## Core model

A workstream app has:

- authenticated shell and selected `AuthContext`
- explicit workstream definition/type, runtime workstream instance/thread/log, browser view/session terminology, and readiness level
- workforce roster covering human workers, the owning functional-agent worker, internal/autonomous/evaluator agent workers, and system workers
- functional-agent rail with role/capability visibility and backend-owned attention state
- continuous stream/composer for work history and requests
- structured surfaces for dashboards, tables, forms, detail cards, decision cards, diffs, audit timelines, evidence bundles, and system messages
- surface graph edges backed by declared actor adapters, governed tools, backend capabilities, authorization, audit/work traces, and result surfaces
- a bounded workstream governed-tool catalog whose ids can be exposed through `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `api_call`, `workflow_step`, `timer_invocation`, `consumer_reaction`, `mcp_tool_call`, or `internal_call` adapters without duplicating business semantics
- deterministic surface intent routing from composer prompts to authorized surfaces with safe editable prefill before model fallback
- optional `human_chat_tool_plan` handling where consequential chat requests produce a detailed plan, require explicit confirmation bound to that plan, execute each governed-tool invocation through backend checks/idempotency, and return result or partial-failure surfaces
- model-backed agents only where a concrete governed Akka Agent runtime path is required and configured

Routes and pages are implementation/deep-link details. Backend capabilities and authorization are authoritative. Keep the canonical chain explicit: `worker → execution harness → actor adapter → governed tool → capability → Akka implementation`.

## Routing

- product/current-intent graph changes: `app-descriptions`, `app-description-functional-agent-modeling`, `app-description-surface-modeling`, `app-description-ui`
- backend capability modeling: `capability-first-backend`
- Akka component decomposition: `akka-solution-decomposition`
- request-based agents/governance/tools/traces: `akka-agents` and focused `akka-agent-*` skills
- durable background model tasks: `akka-autonomous-agents` plus governance/testing companions
- browser app implementation: `akka-web-ui-apps` and focused UI skills
- foundation/admin/security: `core-saas-foundation`, `akka-workos-user-auth`, `akka-basic-user-admin`, `akka-saas-invitation-onboarding`

## Design checklist

For each workstream/functional agent, define:

- current-intent graph paths for the domain/workstream plus any reused global agent/surface/tool/policy/trace definitions
- workstream id, responsibility, exactly-one owning functional agent, required managed-agent definition id, icon metadata with tooltip, instance scope, and readiness level
- worker roster: human workers, owning functional-agent worker, internal/autonomous/evaluator agent workers, and deterministic system workers
- responsibility, non-responsibility, authority, supervision, handoff, failure, and trace boundaries for each worker
- actor roles, scopes, capabilities, hidden/denied states, and default selection
- backend-owned workstream-local attention category ids, canonical category/severity mappings, producers/workers, lifecycle, My Account/rail aggregation, and dashboard variants
- prompt intent and bounded authority, including help/how-to behavior that stays within the selected workstream and does not require confirmation unless it becomes consequential tool execution
- allowed backend capabilities, governed-tool ids, actor adapters/exposure channels, and approval gates
- dashboard/attention model and evidence freshness
- surfaces, payloads, states, actions, system messages, and trace links
- surface intent routing catalog: high-confidence prompt patterns, target surfaces, prefill fields, ambiguity/denial behavior, no-mutation guarantee, and tests
- confirmed human chat tool-plan catalog, when allowed: prompt patterns or scope, proposed-plan detail, confirmation binding, per-tool transaction/idempotency semantics, denials before confirmation, result/partial-failure surfaces, and trace source `human_chat_tool_plan`
- internal agent or AutonomousAgent delegation only when needed
- tests for allowed, denied, stale, failure, tenant/customer isolation, deterministic surface no-mutation, adapter trace source, and confirmed chat execution/partial-failure paths where those adapters are modeled

## Completion standard

A workstream is not complete because static surfaces render or a deterministic placeholder response exists. Build/compile output is only manual-ready when required checks pass and the real worker + actor adapter + governed tool + capability path is known. Runtime-ready work must exercise AuthContext, backend authorization, durable state/projections, governed Akka Agent invocation when model-backed, governed-tool/capability-backed actions, traces/audit, API responses, and frontend rendering. Provider/security failures must fail closed with actionable structured feedback.
