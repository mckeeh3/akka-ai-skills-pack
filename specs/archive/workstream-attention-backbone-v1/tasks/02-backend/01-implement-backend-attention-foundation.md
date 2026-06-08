# TASK-WAB-02-001: Implement backend attention foundation

## Objective

Add the starter backend foundation for shared attention items, summaries, authorization, lifecycle operations, and tests.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-attention-backbone-v1/README.md`
- `specs/workstream-attention-backbone-v1/conversation-capture.md`
- `specs/workstream-attention-backbone-v1/sprints/01-attention-backbone-v1-sprint.md`
- `specs/workstream-attention-backbone-v1/backlog/01-attention-backbone-v1-build-backlog.md`
- `specs/workstream-attention-backbone-v1/tasks/02-backend/01-implement-backend-attention-foundation.md`
- contract artifact from `TASK-WAB-01-001`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AuthContextResolver.java`
- current starter service/repository patterns under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/`

## Skills

- `akka-entity-type-selection` if durable Akka state shape is unclear
- focused entity/view/testing skills only if needed after reading existing starter patterns

## In scope

- Add starter backend attention domain records/enums for item, category, severity, status, target/scope, and source refs.
- Add a shared attention repository/service with scoped reads for:
  - workstream attention items;
  - My Account personal attention;
  - left-rail/workstream summary counts.
- Add lifecycle operations for v1 status changes, at least acknowledge/dismiss/resolve where contract-approved.
- Enforce AuthContext, tenant/customer scope, visible workstream/capability filtering, and safe redaction.
- Emit audit/protected-read/denial traces through existing starter trace facilities.
- Add backend tests for success, forbidden/hidden workstream, tenant isolation/redaction, lifecycle idempotency/no-op, and audit/trace.

## Out of scope

- Rewiring every existing workstream surface.
- Frontend rendering.
- Full event consumer/timer/autonomous-agent notification system.

## Expected outputs

- Backend attention model/service/repository files in the starter template.
- Backend tests under the starter template.
- Updated queue status/notes.

## Required checks

- `git diff --check`
- targeted backend test command from `templates/ai-first-saas-starter/backend` covering new tests

## Done criteria

- Backend has one shared attention foundation, not per-workstream duplicate queues.
- Normal starter runtime path can read scoped attention from backend service/repository.
- Tests prove auth, redaction, lifecycle, and audit/trace behavior.
- Task changes and queue update are committed.

## Commit message

`attention-backbone: implement backend foundation`
