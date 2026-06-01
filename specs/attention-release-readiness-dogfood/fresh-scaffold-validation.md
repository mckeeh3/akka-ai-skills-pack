# Fresh Scaffold Automated Validation

## Task

- Task ID: `TASK-ARD-01-002`
- Date: 2026-06-01
- Target: `/tmp/attention-dogfood-scaffold-AqgCqZ`
- Template: `templates/ai-first-saas-starter`

## Scaffold command

```bash
tools/scaffold-ai-first-saas-starter.sh \
  --target /tmp/attention-dogfood-scaffold-AqgCqZ \
  --template-dir templates/ai-first-saas-starter \
  --app-name "Attention Dogfood Starter" \
  --app-slug attention-dogfood-starter \
  --base-package ai.first \
  --maven-group-id ai.first \
  --yes
```

Result: passed. The scaffold wrote 353 files plus `specs/scaffold-report.md`.

## Backend validation

```bash
cd /tmp/attention-dogfood-scaffold-AqgCqZ
mvn test -Dtest=AttentionServiceTest,AttentionProducerServiceTest,InvitationAndUserAdminServiceTest,GovernancePolicyServiceTest,UserAdminAccessReviewServiceTest,MeServiceTest,WorkstreamServiceTest,UserAdminAccessReviewWorkerTest
```

Result: passed.

Summary from Maven:

- Tests run: 75
- Failures: 0
- Errors: 0
- Skipped: 0
- Build: success

Coverage included attention backbone, attention producers, invitation/user-admin paths, governance policy paths, access-review worker/task attention, My Account, and workstream attention surfaces.

Observed fail-closed provider evidence during the invitation tests: `ResendEmailService` logged missing `RESEND_API_KEY`, `INVITE_EMAIL_FROM`, and `RESEND_FROM_EMAIL` as blocked production email configuration rather than silently succeeding.

## Frontend validation

Initial frontend command without dependency install:

```bash
cd /tmp/attention-dogfood-scaffold-AqgCqZ/frontend
npm test
npm run typecheck
npm run build
```

Result: failed because dependencies were not installed in the fresh scaffold. This is expected for a raw scaffold before `npm ci`; the build command reported `vite: command not found`.

Dependency install and frontend checks:

```bash
cd /tmp/attention-dogfood-scaffold-AqgCqZ/frontend
npm ci
npm test
npm run typecheck
npm run build
```

Results:

- `npm ci`: passed; 128 packages installed; 0 vulnerabilities.
- `npm test`: failed, 126 passed and 2 failed.
- `npm run typecheck`: passed.
- `npm run build`: passed and wrote Akka static resources under `src/main/resources/static-resources`.

Failing frontend tests:

- `src/workstream-attention-backbone.contract.test.mjs`
- `src/workstream-attention-update-delivery.contract.test.mjs`

Failure reason: both tests read backend source through `../../backend/src/main/java/ai/first/...`, but the scaffolded starter places backend source at the project root under `src/main/java/ai/first/...`. The tests fail with `ENOENT` for:

```text
/tmp/attention-dogfood-scaffold-AqgCqZ/backend/src/main/java/ai/first/application/security/WorkstreamService.java
```

This appears to be a scaffold contract-test path bug, not an attention runtime/backend failure.

## Focused source checks

```bash
cd /tmp/attention-dogfood-scaffold-AqgCqZ
rg -n "AttentionService\.LIST_RAIL_SUMMARIES_TOOL|attention\.list_rail_summaries|attention\.list_workstream_items|functionalAgentsWithBackendAttention|railAttentionState|blocked_provider_or_runtime" src/main/java frontend/src
```

Result: passed as a source-evidence check. It found backend-derived attention markers in `WorkstreamService`, `AttentionService`, `AttentionProducerService`, frontend rail/surface code, and test-only fixtures. It also confirmed `railAttentionState` remains a frontend transient/unseen-response concept while backend markers use `attention.list_rail_summaries` and `attention.list_workstream_items`.

## Release-readiness assessment

Fresh scaffold repeatability is partially proven:

- scaffold generation passes;
- targeted backend attention tests pass;
- frontend typecheck and build pass after `npm ci`;
- frontend attention contract tests currently block a clean `npm test` because they assume a stale `backend/` directory layout.

A bounded blocker task is required before claiming automated scaffold validation is fully green.
