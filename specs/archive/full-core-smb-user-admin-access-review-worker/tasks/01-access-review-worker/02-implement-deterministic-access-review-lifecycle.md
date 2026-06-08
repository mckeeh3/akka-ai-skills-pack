# Task: Implement deterministic access-review lifecycle and backend surfaces

## Objective

Add the deterministic SMB User Admin access-review task record, repository/service lifecycle, backend action handling, and `user_admin.access_review_task.v1` surface shaping.

## Required reads

Use the required reads listed on `TASK-FCSMB-UARW-01-002` in `pending-tasks.md`.

## In scope

- Add task state for start/read/cancel/accept result/reject result.
- Map capabilities to `user_admin.access_review.start`, `user_admin.access_review.read`, `user_admin.access_review.cancel`, `user_admin.access_review.accept_result`, and `user_admin.access_review.reject_result`.
- Keep deterministic code responsible for authorization, selected AuthContext scope, disabled actor denial, tenant/customer filtering, idempotency, lifecycle transitions, trace ids, redaction, and no direct access mutation.
- Return typed `user_admin.access_review_task.v1` data through the workstream API/action path.
- Preserve provider/runtime blocked behavior until the governed worker task is implemented.

## Out of scope

- Do not implement model-backed worker completion in this task.
- Do not mutate invitations, memberships, roles, capabilities, or authorization from access-review output.
- Do not expand into enterprise certification campaigns or scheduling.

## Expected outputs

- New/updated backend domain/application files under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/security/` and `.../application/security/`.
- Updated `WorkstreamService.java` action and surface handling.
- Backend tests for lifecycle, idempotency, authorization, tenant isolation, provider blocked state, and no direct mutation.

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=UserAdminAccessReviewServiceTest,WorkstreamServiceTest,InvitationAndUserAdminServiceTest
rg -n "user_admin\.access_review\.(start|read|cancel|accept_result|reject_result)|user_admin\.access_review_task\.v1|AccessReviewTask|UserAdminAccessReview|system_message|provider|no direct mutation|blocked_provider_or_runtime" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- Deterministic lifecycle tests pass.
- Runtime action paths produce typed access-review task surfaces and safe blocked/provider states.
- Access-review task results cannot directly mutate User Admin access state.

## Commit message

- `full-core-smb: implement access review lifecycle`
