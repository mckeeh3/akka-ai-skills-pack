# Workstream: Agent Admin

## Purpose

Allow SaaS Owner/Admin users to improve managed-agent behavior by editing versioned agent documents with AI assistance. Agent docs are prompts, skills, and skill reference docs.

## Description

Agent Admin is the workstream where authorized SaaS admins edit the documents that define managed-agent behavior for all agents. Users should think of the workstream as **improving agent behavior**, not managing internal prompt, skill, reference, model, tool, or lifecycle machinery.

Editing is intentionally not direct text editing. The user gives free-form instructions, and an editing agent interprets the request, reads the current document and related agent context, preserves the existing Markdown structure unless asked otherwise, and drafts the actual document changes. The user reviews the proposed full document, summary, advisory warnings/risks, and optional diff before deciding whether to continue refining, Save, or Cancel.

Each Save immediately creates a new current immutable version and updates the document used by runtime agents. All versions are retained and can be browsed. A requested diff for a historical version compares that version only with its immediate predecessor; for example, version 7 is diffed against version 6. Each version records when it was created, who made the change, and the editing-session transcript/summary that triggered the change. Edit request input is enabled only on the current/latest version. Historical versions are read-only, but users may restore one; restore immediately creates a new current version with edit request `Restored from version N`.

The common journey is: show/filter the agent list, open an agent, open its prompt or one of its skills/reference docs, describe the desired behavior improvement, iterate with the editing agent, then Save or Cancel. The workstream persists previous surfaces and has no forced default surface. Users may open the dashboard on demand or clear the workstream.

The intended outcome is a simple, trusted, SaaS-admin-only editing workspace: admins can improve prompts, skills, and reference docs without hand-editing raw text, while every saved change is versioned, auditable, immediately effective, and recoverable through version history.

## Functional agent

Owns `agent-admin-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions.

## Capability binding

Primary capability: `../../capabilities/agent-doc-administration.md`. Access is limited to SaaS Owner/Admin contexts. Tenant/organization admins, customer admins, tenant employees, customer users, and auditors without SaaS admin authority are not Agent Admin operators.

## Attention model

Agent Admin currently has no required `needs attention` queue. The optional dashboard shows `things you can do`: a clickable total-agent count that opens the agent list and the top five most recently changed agents. Future attention categories may be added only after a concrete user-facing attention need is identified.

## Readiness posture

This node captures current intent only. Runtime readiness requires local Akka/API/UI validation of SaaS-admin authorization, agent/doc browsing, AI-assisted editing, versioning, save/cancel, restore, skill/reference deletion, runtime doc loading, and trace visibility.
