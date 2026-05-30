# Full-Core SMB User Admin Access-Review Worker

## Purpose

Implement the fourth User Admin vertical slice for the AI-first SaaS starter: a durable access-review worker candidate for SMB admins.

This mini-project turns the completed deterministic User Admin foundations and request/response `UserAdminAgent` guidance into a lifecycle-backed access-review investigation flow. The worker must use governed agent runtime semantics only where the task lifecycle justifies it, while deterministic services continue to own authorization, validation, lifecycle, idempotency, audit/trace, evidence shaping, and all access mutations.

## Background

Completed predecessor slices provide the required foundations:

- `specs/full-core-smb-user-admin/` — User Admin dashboard and invitation foundation;
- `specs/full-core-smb-user-admin-access-management/` — member status and role/capability preview/change actions;
- `specs/full-core-smb-user-admin-agent-guidance/` — request/response `UserAdminAgent` guidance, scoped `userAdminEvidence.read`, provider fail-closed behavior, and no-direct-mutation guidance;
- `specs/full-core-smb-baseline-and-ux/` — shared workstream shell, structured surface, system-message, validation, and visual UX contracts.

The next justified step is a durable worker because access review has task lifecycle needs: scoped evidence collection, progress, retry/cancel, provider-blocked handling, model-assisted summarization, human review, and result decision traces.

## Scope

- Deterministic access-review capabilities:
  - `user_admin.access_review.start`
  - `user_admin.access_review.read`
  - `user_admin.access_review.cancel`
  - `user_admin.access_review.accept_result`
  - `user_admin.access_review.reject_result`
- Durable access-review task lifecycle and idempotent start/cancel/read/result-decision behavior.
- Typed `user_admin.access_review_task.v1` surface, including progress, blockers, evidence references, recommendation/result review state, provider failures, and trace links.
- Scoped evidence reads from User Admin directory, invitation, member status, role/capability, recent-change, and trace foundations.
- Governed internal worker / `AutonomousAgent` path when source inspection confirms the starter has the correct runtime foundation.
- Provider missing/config blocked states that fail closed with actionable `system_message` and traces.
- Human accept/reject of worker output without direct access mutation.
- Tests and runtime validation for authorization, tenant isolation, lifecycle, provider fail-closed behavior, trace links, no secret leakage, and no direct access mutations.

## Non-goals

- Do not let the worker directly disable/reactivate users, revoke/resend invitations, or change roles/capabilities.
- Do not bypass deterministic User Admin services for authorization, tenant filtering, status/role policy, idempotency, audit, or trace.
- Do not use a request/response agent turn as a substitute for durable worker lifecycle.
- Do not use deterministic/model-less normal runtime output as a substitute for model-backed worker behavior.
- Do not expand into Agent Admin, Audit/Trace full-core, or Governance/Policy full-core beyond the trace/evidence hooks required for access review.
- Do not implement enterprise access certification, campaign scheduling, segregation-of-duties policy engines, or cross-tenant MSP workflows in this SMB slice.

## Target source areas

Primary executable baseline:

- `templates/ai-first-saas-starter/`

Likely source families to inspect before editing:

- User Admin deterministic services under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/`
- workstream services, runtime agent, and structured surface DTOs under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/workstream/` and related packages
- governed agent/worker runtime foundation under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/`
- seed prompt/skill/reference/tool-boundary material under `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/`
- frontend workstream and surface rendering under `templates/ai-first-saas-starter/frontend/src/`
- targeted backend/frontend tests plus broad starter validation script

## Execution model

Execute one task per fresh harness session. Start with source-boundary inspection and an implementation map. Append bounded backend/frontend/runtime validation tasks from that map before implementing source changes.

## Read order for future task sessions

1. `AGENTS.md`
2. this mini-project `README.md`
3. `conversation-capture.md`
4. selected sprint and backlog files
5. selected task brief
6. predecessor implementation maps and contracts named by the task
7. smallest listed skill files and source files discovered by the task

## Sprint sequence

1. **Access-review worker implementation map** — inspect source boundaries, choose the precise deterministic lifecycle and worker/runtime implementation path, and append bounded source-edit tasks.
2. **Deterministic lifecycle and surfaces** — implement or harden start/read/cancel/result-decision capabilities, task state, typed surfaces, and tests.
3. **Governed worker runtime** — wire internal worker / `AutonomousAgent` behavior, evidence access, provider blocked states, prompt/skill/reference/tool traces, and no-direct-mutation guarantees.
4. **Frontend and validation** — render progress/result/blocked states and run targeted plus fullstack validation.

The initial queue only creates the inspection/map task and terminal verification task. The map task must append bounded source-edit tasks once actual source boundaries are known.

## Done state

This mini-project is complete when:

- access-review capability contracts are implemented at SMB scope;
- the durable access-review task lifecycle works locally for start/read/cancel/accept/reject paths;
- the worker uses governed model-backed runtime behavior where configured and fails closed when provider/model config is absent;
- evidence is authorized, scoped, redacted, trace-linked, and cannot leak cross-tenant data;
- worker output cannot directly mutate invitations, memberships, roles, capabilities, or authorization state;
- typed dashboard/task/system-message surfaces render normal, progress, blocked, canceled, completed, accepted, and rejected states;
- targeted backend/frontend tests and broad starter validation pass, or any blocker is captured as a bounded follow-up task.
