# Workstream Expertise Foundation Plan

## Purpose

Make the agent workstream vision concrete: every user-facing functional workstream can be backed by an agent that is an expert in that workstream's processes, policies, references, allowed capabilities, escalation rules, and structured surfaces.

The goal is to close the gap between "this workstream has an agent" and "this workstream agent has governed, testable expertise." The implementation should extend the existing managed-agent foundation rather than inventing an ungoverned prompt-only mechanism.

## Target model

```text
functional workstream
→ workstream expertise contract
→ governed prompt, skills, and reference documents
→ per-agent skill/reference manifests
→ authorized readSkill/readReferenceDoc runtime loading
→ tool/capability boundary enforcement
→ traces and tests proving expertise is assigned and used safely
```

## Execution model

- Execute one task per fresh harness session.
- Use `pending-tasks.md` as the durable queue.
- Select the first `pending` task whose dependencies are satisfied.
- Read this README, the selected sprint, matching backlog, selected task entry, and task brief before editing.
- Each task must update `pending-tasks.md` before completion.
- Each task must run required checks or document why a check could not run.
- Each task must make one git commit before being marked `done`.
- At the completion of every sprint, run a sprint review task that either confirms the next sprint is ready or adds/adjusts follow-up tasks.

## Sprint sequence

1. `sprints/01-doctrine-description-sprint.md` — define workstream expertise doctrine and app-description ownership.
2. `sprints/02-governed-runtime-sprint.md` — extend governed runtime guidance with workstream expert bundles and reference-document loading.
3. `sprints/03-seed-example-sprint.md` — update seed/starter examples so User Admin demonstrates real workstream expertise.
4. `sprints/04-planning-integration-sprint.md` — update PRD/spec/backlog/task generation so new workstreams produce expertise tasks.
5. `sprints/05-review-hardening-sprint.md` — final consistency review, gap closure, and next-sprint decision.

## Done state

This plan is complete when:

- `docs/` defines a canonical workstream expertise model that fits under agent workstream architecture and capability-first backend doctrine.
- App-description guidance has a durable place for per-workstream expertise contracts, skill/reference manifests, capability mappings, authority, traces, and tests.
- Agent governance skills distinguish procedural skills from reference documents, or explicitly define how references are represented until a separate document model exists.
- Runtime invocation guidance covers compact expertise manifests, authorized `readSkill(skillId)`, authorized reference-document loading, denied loads, and trace records.
- The secure AI-first SaaS seed app-description makes `user-admin-agent` a concrete expert with seed skills/reference docs, tool boundary, traces, and tests.
- Starter/template seed resources and/or examples include enough packaged default content to demonstrate first-install workstream expertise.
- PRD/spec/backlog guidance generates self-contained workstream expertise tasks for every new functional agent.
- Final review finds no major path where a functional agent can be declared ready while lacking assigned expertise documents, runtime load controls, boundaries, and tests.

## Open suggestions / decisions for sprint reviews

- Prefer adding a first-class `ReferenceDocument` / `ReferenceVersion` / `AgentReferenceManifest` pattern if the review finds that overloading `SkillDocument` would blur procedural guidance and durable process knowledge.
- Keep the first implementation focused on User Admin as the canonical example; do not try to author expert bundles for every possible domain workstream in one pass.
- Treat skill/reference content as behavior guidance only. Backend authorization, capability contracts, and `ToolPermissionBoundary` remain authoritative.
