# Manual/Runtime Edge Review

## Task

- Task ID: `TASK-ARD-02-001`
- Date: 2026-06-01
- Target: `/tmp/attention-dogfood-runtime-review-q2RB7j`
- Template: `templates/ai-first-saas-starter`

## Starting manual evidence

This review carries forward the user dogfood evidence recorded for this mini-project:

> i tested the app and things have significantly improved. the left rail workstream things needing my attention are working. also, seeing improvements in the dashboards and surfaces.

Interpretation: the visible left-rail attention and dashboard/surface improvements have positive manual evidence. This task focused on runtime-adjacent edge evidence, security/fail-closed checks, and authority-boundary confirmation in a fresh scaffold.

## Fresh scaffold used for review

```bash
tools/scaffold-ai-first-saas-starter.sh \
  --target /tmp/attention-dogfood-runtime-review-q2RB7j \
  --template-dir templates/ai-first-saas-starter \
  --app-name "Attention Dogfood Runtime Review" \
  --app-slug attention-dogfood-runtime-review \
  --base-package ai.first \
  --maven-group-id ai.first \
  --yes
```

Result: passed. The scaffold wrote 353 files plus `specs/scaffold-report.md`.

## Backend/runtime edge validation

```bash
cd /tmp/attention-dogfood-runtime-review-q2RB7j
mvn test -Dtest=AttentionServiceTest,AttentionProducerServiceTest,InvitationAndUserAdminServiceTest,GovernancePolicyServiceTest,UserAdminAccessReviewServiceTest,MeServiceTest,WorkstreamServiceTest,UserAdminAccessReviewWorkerTest
```

Result: passed.

Summary:

- Tests run: 75
- Failures: 0
- Errors: 0
- Skipped: 0
- Build: success

Coverage evidence:

- `AttentionServiceTest` and `WorkstreamServiceTest` cover backend-derived rail summaries, workstream attention items, My Account attention, hidden/denied workstream behavior, system-message/provider-blocked surfaces, and workstream action paths.
- `AttentionProducerServiceTest`, `InvitationAndUserAdminServiceTest`, and `GovernancePolicyServiceTest` cover producer-driven upsert/resolve behavior for invitations, user-admin paths, governance proposals/decisions, and idempotent lifecycle behavior.
- `UserAdminAccessReviewServiceTest` and `UserAdminAccessReviewWorkerTest` cover worker/task attention and provider/runtime blocked states.
- During invitation tests, `ResendEmailService` logged missing `RESEND_API_KEY`, `INVITE_EMAIL_FROM`, and `RESEND_FROM_EMAIL` as blocked production email configuration. This confirms missing provider configuration fails closed instead of silently succeeding.

## Frontend/runtime surface validation

```bash
cd /tmp/attention-dogfood-runtime-review-q2RB7j/frontend
npm ci
npm test
npm run typecheck
npm run build
```

Results:

- `npm ci`: passed; 128 packages installed; 0 vulnerabilities.
- `npm test`: passed; 132 tests passed.
- `npm run typecheck`: passed.
- `npm run build`: passed and wrote static resources under `src/main/resources/static-resources`.

Coverage evidence:

- Frontend tests include explicit attention contracts for backend-derived left-rail actionable badges, backend refresh/update delivery, My Account attention, dashboards/surfaces, denied/empty states, provider-blocked `system_message` surfaces, and separation from transient unseen-response state.
- The successful `npm test` result confirms the prior scaffold contract-test path blocker from `TASK-ARD-01-002` is no longer present in a fresh scaffold.

## Focused source/authority checks

```bash
cd /tmp/attention-dogfood-runtime-review-q2RB7j
rg -n "AttentionService\.LIST_RAIL_SUMMARIES_TOOL|attention\.list_rail_summaries|attention\.list_workstream_items|functionalAgentsWithBackendAttention|blocked_provider_or_runtime" src/main/java frontend/src
rg -n "railAttentionState" frontend/src
rg -n "RESEND_API_KEY|INVITE_EMAIL_FROM|RESEND_FROM_EMAIL|provider secret|raw token|access_token|id_token" frontend/src src/main/java || true
```

Result: source evidence found the expected backend-derived attention and fail-closed markers.

Findings:

- Backend authority: `AttentionService` defines `attention.list_rail_summaries` and `attention.list_workstream_items`; `WorkstreamService` uses `functionalAgentsWithBackendAttention` and backend attention lists for rail and surface payloads.
- Frontend rendering: left-rail items carry `data-attention-kind="backend-actionable"` and `data-attention-source="attention.list_rail_summaries"`; dashboard surfaces carry `attention.list_workstream_items`.
- Frontend-only guardrail: `railAttentionState` appears only as transient unseen-response state; tests assert it does not own `attention.list_rail_summaries` authority.
- Redaction/secret boundary: frontend references to provider secrets/raw tokens are explanatory redaction assertions and browser-safe fixture text; actual Resend environment keys appear only in backend `ResendEmailService`.

## Edge-review matrix

| Area | Evidence | Assessment |
| --- | --- | --- |
| Left-rail backend-derived attention | User dogfood evidence plus backend/frontend tests and source markers for `attention.list_rail_summaries`. | Pass at v1/v2 scope. |
| My Account aggregate attention | `MeServiceTest`, `WorkstreamServiceTest`, My Account frontend contract tests, and dashboard attention source markers. | Pass at v1/v2 scope. |
| Core dashboards/surfaces | User dogfood evidence plus frontend contract tests for User Admin, Agent Admin, Audit/Trace, Governance/Policy, My Account, and generic surface rendering. | Pass at v1/v2 scope. |
| Producer-driven updates | Backend tests for invitation, governance, timed/access-review, and attention producers; frontend update-delivery contract tests. | Pass at v1/v2 scope. |
| Lifecycle resolution/idempotency | Backend producer/service tests cover upsert/resolve/no-op/idempotent attention transitions. | Pass at v1/v2 scope. |
| Hidden/denied workstream redaction | Backend workstream/My Account tests and frontend denied/empty state tests; source carries `not_found_or_redacted` and browser-safe redaction metadata. | Pass at v1/v2 scope. |
| Provider/fail-closed behavior | Missing Resend config logged as blocked; user-admin access review, audit summary, governance analysis, and model-backed workstream surfaces return `blocked_provider_or_runtime` rather than deterministic success. | Pass at v1/v2 scope. |
| No frontend-only authority | Source and contract tests separate backend actionable attention from transient `railAttentionState`. | Pass at v1/v2 scope. |
| Interactive browser rerun | Not launched in this harness session; existing user dogfood evidence is the browser/manual evidence. Fresh scaffold tests/build provide runtime-adjacent repeatability. | Non-blocking limitation; not a release blocker for the current queue scope. |

## Blockers and follow-up

No release blockers were found in this review.

Non-blocking future work remains outside the v1/v2 release-ready claim:

- bounded v3 event backbone status is documented separately in `specs/workstream-event-backbone-v3/event-backbone-v3-handoff.md`;
- broader generated-app event coverage beyond the v3 starter event families;
- SSE/push notification delivery beyond current backend-derived refresh/update hints;
- enterprise notification center/digest infrastructure;
- broader model-backed AutonomousAgent worker enablement for deferred audit/governance analysis tasks.
