# Workstream: Agent Admin

## Purpose

Allow SaaS Owner/Admin users to govern managed-agent behavior safely across the core app by inspecting existing `AgentDefinition` records, behavior profiles, governed prompt/skill/reference documents, manifests, model-policy choices, tool boundaries, safe test-console runs, proposals, approvals, and runtime trace evidence. Agents and generated tools are static/code-generated from the app-description; Agent Admin changes runtime-managed behavior state and governance documents, not generated agent identity or tool code.

## Description

Agent Admin is the SaaS-admin functional-agent workstream for the managed-agent governance foundation scope. Authorized admins inspect generated managed-agent behavior profiles and govern the `PromptDocument`, `SkillDocument`, `ReferenceDocument`, `AgentSkillManifest`, `AgentReferenceManifest`, `ModelConfigRef`/model policy, and `ToolPermissionBoundary` references that shape managed-agent behavior.

The workstream covers functional/context-area agents, internal worker agents, evaluator agents, autonomous/background agents, system/foundation agents, and future generated business agents. Users should think of it as **improving agent behavior safely**. The UI may show placement, lifecycle status, steward, authority level, safe model alias, manifest summaries, generated tool categories, tool-boundary decision categories, test-console status, proposal status, and trace links. Provider secrets, backend authorization internals, generated tool implementation details, hidden platform prompts, and unapproved tool-boundary internals remain outside browser/API payloads.

Editing is intentionally proposal-first. A SaaS admin may request behavior changes through browser surface actions or confirmed chat plans. The `AgentBehaviorEditorAgent` reads the current artifact and same-agent context through governed loader/tool boundaries, preserves existing Markdown structure unless asked otherwise, and drafts a structured behavior-change proposal. The internal editing agent never directly mutates active runtime behavior, activates its own proposal, expands authority, or bypasses backend capability checks.

The common journey is: open the Agent Admin dashboard or catalog, inspect an agent detail/profile, open prompt/skill/reference/manifest/tool-boundary/model-policy governance, run a safe test-console preflight when allowed, request or refine a behavior edit, review the proposed full content/diff/risk/tests, then Save draft/proposal, Submit for review, Activate a low-risk reviewed change, route authority-expanding work to approval/decision-card handling, or Cancel. Runtime behavior changes only after protected backend activation of an approved/reviewed proposal.

## Functional agent and workers

Owns `agent-admin-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions.

Worker bindings live under `workers/` and distinguish:

- `saas-admin-human` using browser `surface_action` and explicit `human_chat_tool_plan` confirmation;
- `agent-admin-functional-agent-worker` routing surfaces, explaining governance state, and preparing bounded proposals;
- `agent-behavior-editor-internal-agent-worker` drafting structured behavior-change proposals;
- `agent-runtime-system-worker` resolving active behavior profiles, model policy, manifests, loader tools, generated-tool decisions, and traces.

Workers use separate actor adapters over shared governed tool ids. Human surface availability, chat text, prompt text, skill/reference content, or model output never grants backend authority.

## Capability binding

Primary foundation capability scope: managed-agent governance and managed-agent behavior state. Current workstream files retain source-alignment references to the existing `agent-doc-administration` capability artifact until the shared capability node is renamed or consolidated. Access is limited to SaaS Owner/Admin contexts with explicit `agent_admin.*` or equivalent managed-agent-governance capability. SaaS app owners operate in the reserved `saas-app-owner` tenant scope for scoped overrides. Tenant/organization admins, customer admins, tenant employees, customer users, and auditors without SaaS admin authority are not Agent Admin operators.

## Attention model

Agent Admin now has first-class attention categories for SaaS admins:

- `behavior-change-proposal`: saved drafts, restore proposals, or proposed prompt/skill/reference/profile changes awaiting review;
- `approval-required`: medium/high-risk, authority-expanding, model-policy, tool-boundary, approval-boundary, or tenant-scope changes that cannot be directly activated;
- `provider-config-blocker`: safe test-console or editing-agent requests blocked because active provider/model configuration is missing, inactive, unauthorized, or secret-boundary-invalid;
- `loader-tool-boundary-denied`: denied `readSkill`, `readReferenceDoc`, generated-tool, or `ToolPermissionBoundary` event requiring admin inspection.

The dashboard orders these as **things that need my attention** before **things I can do**. Attention counts open the proposal review, governance/test-console, or trace surfaces; they never auto-activate behavior.

## Readiness posture

This node captures refreshed current intent only. Runtime readiness is not claimed by this docs-only refresh. Required runtime-validation scenarios include SaaS-admin authorization, catalog/detail/profile inspection, proposal-first prompt/skill/reference/profile edits, approval-required authority expansion, safe test-console provider fail-closed behavior, loader/tool-boundary denial traces, runtime `PromptAssemblyTrace`/`SkillLoadTrace`/`ReferenceLoadTrace`/`AgentWorkTrace` visibility, and frontend provider-secret boundaries.
