# TASK-STARTER-07-005: Implement invitation acceptance end-to-end

## Goal

Complete the invitation onboarding path so the starter supports browser/API acceptance, token validation, membership activation/linking, safe recovery states, audit, and idempotency.

## Required reads

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/InvitationService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/InvitationRepository.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/security/Invitation.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/api/workstream/WorkstreamEndpoint.java`
- `templates/ai-first-saas-starter/frontend/src/workstream/**`
- `specs/core-app-full-stack-readiness/invitation-onboarding-reference-slice.md`
- `skills/akka-saas-invitation-onboarding/SKILL.md`

## Work

1. Add an explicit invite acceptance API/route contract in the starter backend.
2. Implement token validation/accepted membership linking through existing invitation service ports.
3. Add browser-safe accepted/expired/revoked/already-accepted recovery DTOs.
4. Wire frontend route/state for invitation acceptance or a workstream surface that handles acceptance outcomes.
5. Add tests for accept, duplicate accept, expired/revoked token, wrong account, audit, and no raw token leakage.
6. Update the pending queue entry for this task.

## Required checks

- `git diff --check`
- rendered-template Maven tests for invitation slice
- frontend tests/typecheck/build if frontend changed

## Done criteria

- Invitation acceptance can be exercised from API/browser paths in a scaffolded starter.
- Denials and duplicate/idempotent cases are safe and audited.
- Task status is marked `done` in `pending-tasks.md`.
- One git commit is created.

## Suggested commit message

`Implement starter invitation acceptance flow`
