# Full-Core SMB Agent Admin

## Purpose

Make Agent Admin the next SMB full-core workstream in the AI-first SaaS starter.

Agent Admin gives authorized SMB operators governed visibility and lifecycle control for AI behavior artifacts: `AgentDefinition` records, prompts, skills, references, manifests, model refs, `ToolPermissionBoundary` records, default seeds, provider readiness, behavior-change proposals, and safe `AgentAdminAgent` guidance.

This mini-project targets `templates/ai-first-saas-starter/` as the executable baseline and follows the workstream + structured surface architecture. It is not a page-first prompt editor or enterprise model marketplace.

## Background

Completed predecessor full-core User Admin slices now provide practical authority/change evidence and the first durable worker pattern:

- `specs/full-core-smb-user-admin/` — User Admin dashboard and invitation foundation;
- `specs/full-core-smb-user-admin-access-management/` — member status and role/capability management;
- `specs/full-core-smb-user-admin-agent-guidance/` — governed request/response UserAdminAgent guidance;
- `specs/full-core-smb-user-admin-access-review-worker/` — durable access-review worker lifecycle;
- `specs/full-core-smb-baseline-and-ux/` — shared workstream shell, structured surface, runtime validation, and visual quality contracts.

Agent Admin should come before Audit/Trace and Governance/Policy full-core hardening because it creates richer behavior-change, tool-boundary, seed, provider-readiness, prompt assembly, and managed-agent lifecycle traces for those later workstreams.

## Scope

Full-core SMB Agent Admin should cover these vertical slices, appended as bounded tasks after source inspection:

1. **Agent catalog/detail dashboard and governed config reads**: active definitions, authority tier, model/provider readiness, seed status, and core/internal worker inventory.
2. **Prompt/skill/reference/manifest/tool-boundary visibility**: approved versions, redacted previews, provenance, compact manifests, assigned artifacts, tool-boundary allow/deny details, and trace links.
3. **Behavior-change draft/review/activate lifecycle**: deterministic proposal lifecycle for prompt/skill/reference/manifest/model/tool-boundary changes, including diff/risk/authority impact, submit/review/approve/reject/activate/cancel/rollback where in scope.
4. **AgentAdminAgent request/response guidance**: governed Akka `Agent` runtime for explanations, readiness summaries, safe proposal drafting help, tool-boundary denial interpretation, and provider fail-closed surfaces.
5. **Prompt-risk / behavior-review worker candidate**: durable internal worker only after deterministic behavior-change foundations exist and lifecycle semantics justify it.

## Non-goals

- Do not implement arbitrary tenant-managed Java class/tool binding.
- Do not create a marketplace, enterprise model procurement console, or complex multi-provider administration suite.
- Do not expose secrets, hidden prompts, raw provider credentials, or unauthorized cross-tenant behavior artifacts.
- Do not let AI own authorization, lifecycle transitions, activation, rollback, schema validation, seed idempotency, provider readiness, or ToolPermissionBoundary enforcement.
- Do not use deterministic/model-less normal runtime behavior as a substitute for model-backed AgentAdminAgent or worker behavior.
- Do not expand into full Audit/Trace or Governance/Policy hardening except for trace/evidence hooks required by Agent Admin.

## Target source areas

Primary executable baseline:

- `templates/ai-first-saas-starter/`

Likely source families to inspect before editing:

- managed-agent foundations under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/`
- Agent Admin/workstream services and surfaces under backend application/workstream/security packages
- seed material under `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/`
- frontend workstream shell, surfaces, fixtures, actions, and contract tests under `templates/ai-first-saas-starter/frontend/src/`
- broad starter validation script and scaffold path

## Execution model

Execute one task per fresh harness session. Start with vertical contracts and a source-boundary implementation map. The first implementation-map task must append bounded source-edit tasks and task briefs before runtime implementation begins.

## Read order for future task sessions

1. `AGENTS.md`
2. this mini-project `README.md`
3. `conversation-capture.md`
4. selected sprint and backlog files
5. selected task brief
6. predecessor SMB/User Admin/Agent Admin contracts named by the task
7. smallest listed skill files and discovered source files

## Done state

This mini-project is complete when Agent Admin has an SMB-ready full-core vertical at the implemented scope:

- authorized operators can inspect active/default managed-agent configuration through typed surfaces;
- prompt/skill/reference/manifest/model/tool-boundary reads are governed, redacted, trace-linked, and tenant-scoped;
- behavior-changing proposals have deterministic lifecycle, validation, idempotency, review/activation/rollback semantics where implemented, and audit traces;
- AgentAdminAgent uses the governed Akka Agent runtime path and fails closed when provider/model config is absent;
- any internal worker uses durable task semantics and cannot activate or mutate behavior directly without deterministic lifecycle capabilities and human authority;
- frontend surfaces are visually polished, structured, trace-linked, and explicit about provider/config/authorization denials;
- targeted backend/frontend tests and broad starter validation pass, or remaining blockers are captured as bounded follow-up tasks.
