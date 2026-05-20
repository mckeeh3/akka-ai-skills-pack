# TASK-STARTER-07-006: Implement Resend adapter boundary and captured outbox checks

## Goal

Replace the current production send stub with a real Resend adapter boundary while preserving safe local/dev/test captured outbox behavior.

## Required reads

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/ResendEmailService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/security/EmailOutboxMessage.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/security/EmailDeliveryStatus.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/InvitationAndUserAdminServiceTest.java`
- `templates/ai-first-saas-starter/.env.example`
- `skills/akka-resend-email-service/SKILL.md`

## Work

1. Define an email delivery port/adapter split if needed.
2. Implement production Resend HTTP/SDK call behavior behind configuration.
3. Preserve local/test captured outbox mode and failure visibility.
4. Ensure no Resend API key, raw invitation token, or backend secret can appear in frontend DTOs, traces, fixtures, or static assets.
5. Add tests for missing config, local capture, successful adapter request construction, failure mapping, and audit/delivery status.
6. Update docs/env examples and the pending queue entry.

## Required checks

- `git diff --check`
- rendered-template Maven tests for email/invitation slice
- no-secret scan over template frontend/static assets if static assets exist

## Done criteria

- Production mode has a real Resend adapter boundary instead of a hardcoded success stub.
- Local/dev/test captured outbox remains explicit and deterministic.
- Task status is marked `done` in `pending-tasks.md`.
- One git commit is created.

## Suggested commit message

`Implement starter Resend email adapter`
