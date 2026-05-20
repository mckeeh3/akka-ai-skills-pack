# TASK-STARTER-07-007: Add durable Akka identity, invitation, and audit slices

## Goal

Start replacing static in-memory security foundation state with durable Akka components behind the existing repository/service ports, without breaking frontend/backend contracts.

## Required reads

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/IdentityRepository.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/InvitationRepository.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/InMemoryIdentityRepository.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/InMemoryInvitationRepository.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/security/**`
- `skills/akka-key-value-entities/SKILL.md`
- `skills/akka-event-sourced-entities/SKILL.md`
- `skills/akka-views/SKILL.md`
- `skills/akka-workflows/SKILL.md`

## Work

1. Select the smallest durable slice that can be completed safely in one session.
2. Prefer preserving existing ports while introducing Akka components for:
   - current-state Account/Profile/Settings/Membership where appropriate;
   - event-sourced Invitation lifecycle and AdminAudit events where history matters;
   - views for directory/invitation/audit queries if practical.
3. Keep in-memory adapters only as local/test fallback if needed, clearly labeled.
4. Add component tests and service tests showing identical auth/idempotency/tenant-isolation behavior.
5. Update documentation to describe current durability coverage and remaining slices.
6. Update the pending queue entry.

## Required checks

- `git diff --check`
- rendered-template Maven tests for changed backend slice
- direct scaffold + `mvn test` if feasible

## Done criteria

- At least one foundation repository path is backed by durable Akka components or a clearly introduced component seam.
- Existing API/frontend contracts remain stable.
- Task status is marked `done` in `pending-tasks.md`.
- One git commit is created.

## Suggested commit message

`Add durable starter identity invitation slice`
