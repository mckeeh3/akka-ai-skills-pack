# Workstream: Agent Admin

## Purpose

Allow SaaS Owner/Admin users to improve governed managed-agent behavior by reviewing and activating AI-assisted changes to versioned agent behavior artifacts. Agent behavior records include `AgentDefinition` behavior profiles, `PromptDocument` prompts, `SkillDocument` skills, governed `ReferenceDocument` records, compact `AgentSkillManifest` and `AgentReferenceManifest` entries, `ModelConfigRef` references, and `ToolPermissionBoundary` references assigned to an `AgentDefinition`.

## Description

Agent Admin is the workstream where authorized SaaS admins inspect managed-agent behavior profiles and govern the documents that shape managed-agent behavior for all agents. Users should think of the workstream as **improving agent behavior safely**, not managing internal prompt, skill, reference, model, tool, or lifecycle machinery. The UI may show profile references such as placement, lifecycle status, steward, authority level, model alias, tool-boundary summary, and trace links, but provider secrets and backend authorization internals remain hidden.

Editing is intentionally not direct text editing. The user gives free-form instructions, and `AgentBehaviorEditorAgent` (the editing-agent) interprets the request, reads the current document and related agent context, preserves the existing Markdown structure unless asked otherwise, and drafts the actual document changes. The user reviews the proposed full document, summary, advisory warnings/risks, and optional diff before deciding whether to continue refining, Save, or Cancel.

The editing agent never directly mutates active runtime behavior. It returns a structured behavior-change proposal with proposed full content, diff, rationale, risk classification, authority-expansion flags, suggested tests, and recommended next action. An explicit SaaS-admin **Save draft/proposal** creates an immutable draft or proposal version. Activation is a separate protected backend action. For low-risk copy/clarity changes the same authorized SaaS admin may immediately review and activate the draft as the documented foundation simplification; medium/high-risk, authority-expanding, model-policy, tool-boundary, approval-boundary, or tenant-scope changes require denial or decision-card/review routing instead of direct activation. All versions are retained and can be browsed. A requested diff for a historical version compares that version only with its immediate predecessor; for example, version 7 is diffed against version 6. Each version records when it was created, who proposed/reviewed/activated it, and the editing-session transcript/summary that triggered the proposal. Edit request input is enabled only on the current/latest editable draft or active document. Historical versions are read-only, but users may propose a restore; activating that restore creates a new current active version with edit request `Restored from version N`.

The common journey is: show/filter the agent catalog, open an agent detail, inspect the runtime profile summary, open its prompt, one of its skills, or one of its governed references, describe the desired behavior improvement, iterate with the editing agent, review the structured proposal/diff/risk, then Save draft/proposal, Activate/Commit when allowed, route to review/decision card, or Cancel. The workstream persists previous surfaces and has no forced default surface. Users may open the dashboard on demand or clear the workstream.

The intended outcome is a simple, trusted, SaaS-admin-only governance workspace: admins can improve prompts, skills, references, and manifest membership without hand-editing raw text, while every proposed, reviewed, activated, rejected, or cancelled change is versioned, auditable, and recoverable through version history. Prompt/skill/reference content remains behavior guidance only; backend authorization, tenant/customer scope, model policy, approval policy, and `ToolPermissionBoundary` enforcement remain authoritative.

## Functional agent and workers

Owns `agent-admin-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions.

Worker bindings live under `workers/` and distinguish the SaaS admin human worker, the user-facing functional-agent worker, the internal `AgentBehaviorEditorAgent` worker, and the runtime resolver/loader system worker. These workers use separate actor adapters (`surface_action`, confirmed `human_chat_tool_plan`, `agent_tool_call`, and `internal_call`) over shared governed tool ids; authority does not transfer between adapters.

## Capability binding

Primary capability: `../../capabilities/agent-doc-administration.md`. Access is limited to SaaS Owner/Admin contexts. Tenant/organization admins, customer admins, tenant employees, customer users, and auditors without SaaS admin authority are not Agent Admin operators.

## Attention model

Agent Admin currently has no mandatory operational `needs attention` queue. The optional dashboard shows `things you can do`: a clickable total-agent count that opens the agent list, draft/review proposal counts when proposal lifecycle is implemented, and the top five most recently changed agents. Draft/review counts open the proposal/review surface and must not imply auto-activation. Future attention categories may be added only after a concrete user-facing attention need is identified.

## Readiness posture

This node captures current intent only. Runtime readiness requires local Akka/API/UI validation of SaaS-admin authorization, agent/doc browsing, AI-assisted editing, versioning, save/cancel, restore, skill/reference deletion, runtime doc loading, and trace visibility.
