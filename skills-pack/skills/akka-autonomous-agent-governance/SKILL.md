---
name: akka-autonomous-agent-governance
description: "Apply generated-app governance to Akka Autonomous Agents: capability contracts, model policy, tool boundaries, approvals, tenant scope, traces, notifications, and managed AgentDefinition terminology."
---

# Akka Autonomous Agent Governance

Use this skill when Autonomous Agents are part of a generated secure AI-first SaaS app or any runtime where tasks, tools, models, approvals, traces, or notification streams need backend governance.

## Required reading

Read before generated-app worker task implementation or review:
- `../docs/autonomous-agent-worker-runtime-pattern.md`

Read when API details are needed:
- `../docs/agent-component-selection-guide.md`
- `../docs/capability-first-backend-architecture.md`
- `../docs/agent-runtime-invocation-pattern.md`
- `../../../specs/autonomous-agents-integration/research-notes.md`
- `../akka-agent-tool-boundaries/SKILL.md`
- `../akka-agent-model-governance/SKILL.md` when model policies/aliases are in scope
- `../akka-agent-work-trace/SKILL.md` when trace storage/search is in scope

## Governance model

Autonomous Agents add durable task machinery. They do not relax the pack's managed-agent requirements. Model-driven task loops still need backend-owned capability contracts, authorization, model policy, tool boundaries, approval rules, and durable traces. Generated-app worker tasks must additionally follow `../docs/autonomous-agent-worker-runtime-pattern.md`: explicit task contract, governed capabilities, typed `worker.task.*` workstream events, attention rules, structured surfaces, provider fail-closed behavior, and no fake success.

Qualify terminology:
- **Akka autonomous `AgentDefinition`**: SDK definition returned by `AutonomousAgent.definition()` / `define()` or dynamic `AgentSetup`.
- **Governed managed-agent `AgentDefinition`**: app domain record for tenant-scoped lifecycle, owner/steward, authority, model ref, prompt/skill/reference manifests, and tool boundary.

## Capability inventory

Define capabilities for:
- start `runSingleTask`;
- create/assign task;
- query task snapshot/result;
- external complete/fail/cancel;
- suspend/resume/terminate agent instance;
- dynamic setup changes;
- task notification stream and agent notification stream;
- typed `worker.task.*` event publication, attention upsert/resolve, and structured worker progress/result surface reads/actions when visible;
- delegation, handoff, team, and moderation operations;
- every local/function/component/MCP/readSkill/readReferenceDoc tool;
- every side effect performed from the autonomous loop.

Each capability needs actor/caller, `AuthContext`, tenant/customer scope, input/output schema, idempotency, side effects, approval policy, audit/work trace, selected exposure surface, and tests.

## Runtime enforcement

Before task start or assignment:
1. resolve caller `AuthContext`, tenant/customer scope, and membership/capability;
2. resolve governed managed-agent profile if used;
3. validate lifecycle status, model policy, provider config, dynamic setup bounds, and task type;
4. resolve active prompt/skill/reference manifests and `ToolPermissionBoundary` for tool registration;
5. deny missing provider/security/model/boundary config with actionable errors;
6. emit task-start or denial trace.

For worker tasks, missing `ComponentClient`, provider/model configuration, governed profile, tool grants, evidence access, or runtime binding must fail closed with blocked/denied state, trace refs, typed workstream events/attention when appropriate, and actionable recovery text. Do not fabricate successful findings or use deterministic/demo/model-less runtime substitutes.

For tool calls, use `akka-agent-tool-boundaries`: prompt text, task instructions, skill/reference text, and tool descriptions never grant authority.

For side effects:
- default to proposal or approval-required;
- require idempotency keys for retriable operations;
- reauthorize before final commit, especially after long-running task loops;
- record approval, denial, and side-effect traces.

## Surfaces and notifications

Expose protected UI/API surfaces for:
- task list/detail and status;
- task result/failure reason;
- agent instance state and lifecycle controls;
- notification streams with tenant/customer filters;
- trace links for prompts, tools, tasks, delegation/handoff/team/moderation, denials, and approvals.

Notification streams are progress surfaces, not business source of truth. Use task snapshots/component state for decisions.

## Review checklist

- AutonomousAgent use is justified over request-based Agent and Workflow;
- governed managed-agent `AgentDefinition` and Akka autonomous `AgentDefinition` are not conflated;
- every task/lifecycle/notification/tool operation maps to a capability;
- tenant/customer scope is present in ids, instructions, attachments, queries, notifications, and traces;
- model policy and provider-secret boundaries are enforced before model invocation;
- tool registration follows active `ToolPermissionBoundary` and registry entries;
- approval gates exist for authority expansion, high-impact side effects, and higher-authority handoff;
- tests cover allowed, denied, cross-tenant, approval-required, trace, and fail-closed paths.
