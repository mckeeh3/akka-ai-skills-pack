# Workstream: Governance/Policy

## Purpose

Manage simple SMB-friendly governance policy settings, SaaS-owner defaults, tenant overrides, effective-policy visibility, policy-change history, and runtime policy-decision evidence.

Governance/Policy is a settings-and-controls center, not an enterprise policy engine. It keeps business-governance controls easy to understand and change while preserving non-overridable SaaS platform security controls.

## Worker roster

- Human worker binding: [`workers/governance-policy-human-operators.md`](workers/governance-policy-human-operators.md) covers SaaS owner admins, tenant admins, auditors, and scoped support users with role-specific authority.
- Functional-agent worker binding: [`workers/governance-policy-functional-agent-worker.md`](workers/governance-policy-functional-agent-worker.md) is the model-backed workstream assistant behind `governance-policy-agent`.
- System worker binding: [`workers/governance-policy-system-worker.md`](workers/governance-policy-system-worker.md) deterministically evaluates effective policy, executes authorized writes/checks, and emits history/traces.

## Functional agent

Owns `governance-policy-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions, and the agent worker does not inherit human surface authority.

## Capability binding

Primary capability: `../../capabilities/governance-policy-lifecycle.md`.

## Policy model

SaaS owners manage default policy values. Tenant admins manage tenant-specific overrides. Tenant overrides are active immediately and win over SaaS defaults for business-governance behavior.

Policy values are intentionally simple:

- boolean settings, such as whether an agent/action is allowed immediately or requires governance handling;
- counter/limit settings, such as maximum retry count.

The policy catalog evolves as app-description-defined agents, workstreams, governed tools/actions, roles, and customer/account contexts evolve. Do not introduce complex policy scripting, enterprise rule languages, legal workflow engines, or simulation-only policy machinery for this SMB foundation scope.

Policy scopes may include tenant, agent, workstream, action/tool, role, and customer/account. When more than one policy applies, the finer-grained/more specific setting wins. Tenant admins can reset an override back to the SaaS default. SaaS owner default updates never overwrite existing tenant overrides.

## Attention model

Backend-owned attention focuses on simple policy operations and explainability: recently changed policies, overridden policies, policy history needing review, runtime outcomes affected by policies where practical, and safe denials for hard platform controls. Counts feed the left rail and My Account aggregation only for authorized actors and never expose hidden tenant/customer facts, raw secrets, raw provider/model data, raw prompts, raw tool payloads, or unredacted evidence.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation of effective-policy reads, tenant override writes, reset-to-default, history, runtime trace evidence, auth denials, tenant isolation, and non-overridable platform-security boundaries.

## Confirmed human chat tool-plan exposure

This workstream may expose bounded `human_chat_tool_plan` assistance for policy lookup and simple override preparation after deterministic no-mutation surface routing declines the prompt. Chat may explain effective policy, draft an override payload, or prepare a reset/default-management request, but it must not bypass tenant-admin/SaaS-owner authority, write without explicit confirmation, alter hard platform security controls, or invent complex policy semantics.

Read/open prompts such as policy search and effective-policy explanation should first route to deterministic no-mutation surface-intent openings or bounded agent read tools. Confirmed chat plans are reserved for side-effecting command plans.

Representative command-plan bindings: `action-governance-policy-set-default`, `action-governance-policy-set-override`, and `action-governance-policy-reset-override` using governed tools `governance.policy.set_default`, `governance.policy.set_override`, and `governance.policy.reset_override`. Confirmed execution requires selected `AuthContext`, backend authorization, exact plan snapshot confirmation, required change reason, idempotency, effective-policy recomputation, and trace emission.
