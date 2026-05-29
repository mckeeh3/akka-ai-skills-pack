# Full-Core SMB UserAdminAgent Guidance

## Purpose

Make `UserAdminAgent` genuinely useful for SMB administrators after deterministic User Admin foundations are in place.

This mini-project implements or plans the request/response AI guidance slice for the User Admin workstream: scoped explanations, summaries, safe next-action guidance, invite/member/role-change drafting help, access-risk summaries, provider fail-closed behavior, and trace-linked structured responses through the governed Akka Agent runtime.

## Background

Predecessor mini-projects completed the deterministic foundations this agent must rely on:

- `specs/full-core-smb-user-admin/` — dashboard and invitation foundation;
- `specs/full-core-smb-user-admin-access-management/` — member status and role/capability preview/change foundations;
- `specs/full-core-smb-baseline-and-ux/` — shared workstream/surface/system-message/runtime-validation contracts.

`UserAdminAgent` must not own authorization or mutations. It uses scoped deterministic evidence and guides humans toward governed capabilities.

## Scope

- Request/response Akka Agent behavior for the User Admin workstream.
- Scoped evidence reads from deterministic User Admin dashboard, invitation, member, status, role/capability, trace, and readiness capabilities.
- Updated governed seed prompt/skill/reference/manifest/tool-boundary material for User Admin guidance when needed.
- Structured response or `markdown_response`/`system_message` behavior for explanations, summaries, blocked states, provider failures, and safe next steps.
- Tests for governed runtime assembly, assigned skill/reference/tool availability, denied evidence loads, provider fail-closed behavior, no direct mutation, and trace links.
- Frontend surface behavior only as needed to render agent guidance and denials clearly.

## Non-goals

- Do not implement access-review AutonomousAgent/internal worker in this mini-project.
- Do not allow the request/response agent to directly mutate invitations, memberships, roles, capabilities, or authorization state.
- Do not bypass deterministic services for tenant filtering, last-admin checks, idempotency, or audit.
- Do not use deterministic/model-less normal responses as a substitute for model-backed agent guidance.
- Do not expand into Agent Admin or Governance/Policy behavior editing beyond User Admin seed/runtime material needed for this agent.

## Target source areas

Primary executable baseline:

- `templates/ai-first-saas-starter/`

Likely source families:

- User Admin seed prompt/skill/reference files under `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/`
- governed agent runtime services/tools under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/`
- User Admin deterministic evidence services under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/`
- workstream message/action runtime under `WorkstreamService`/`WorkstreamRuntimeAgent`
- frontend composer/surface rendering only if agent guidance surfaces need UI changes
- tests under backend agentfoundation/security and frontend workstream contract tests

## Execution model

Execute one task per fresh harness session. Start with source-boundary inspection and guidance contract refinement, then append bounded backend/frontend/source-edit tasks.

## Done state

This mini-project is complete when:

- UserAdminAgent has a bounded SMB guidance contract;
- implementation tasks have made or queued the necessary source edits;
- request/response guidance uses the governed Akka Agent runtime path with active AgentDefinition/prompt/skill/reference/tool-boundary/model resolution;
- missing provider config fails closed with actionable system-message/traces;
- agent guidance cannot mutate access state directly and cannot leak unauthorized evidence;
- runtime/API/UI validation passes for the implemented scope or bounded blocker tasks are appended.
