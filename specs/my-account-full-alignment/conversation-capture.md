# Conversation Capture: My Account Full Automated Alignment

## User request

The user stated that the My Account workstream is ready to compile and that existing code is stale because it was implemented with an older skills-pack while `app-description/` has changed significantly.

A focused compile pass reconciled a bounded command-center/dashboard slice and reported that full alignment still requires automated runtime/API/UI tests, trace/audit evidence, digest closure, chat-plan runtime proof, and source-alignment updates.

The user then asked whether all non-manual items can be completed with a mini-project and approved creation of that mini-project.

## Decisions captured

- Target is root app realization, not skills-pack maintenance.
- The mini-project should cover all non-manual My Account full-alignment work.
- Manual/browser acceptance remains outside the done state, but the terminal verification task must record exactly what remains manual.
- Real provider-backed digest success cannot be claimed unless concrete provider/test runtime configuration is available and exercised. Fail-closed behavior can be automated and aligned.
- Work must preserve tenant/customer scoping, backend authorization, frontend secret boundaries, provider fail-closed behavior, and durable audit/work trace semantics.
- Queue execution must be sequential: one fresh-context subagent, one task, one commit, then parent review before the next task.

## Prior compile evidence

The bounded dashboard compile changed:

- `src/main/java/ai/first/application/coreapp/myaccount/MyAccountService.java`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `frontend/src/workstream/surfaces/DashboardSurface.tsx`
- `frontend/src/workstream/types/surfaces.ts`
- `frontend/src/workstream-my-account-vertical.contract.test.mjs`
- `app-description/domains/core-starter/workstreams/my-account/lifecycle.md`
- `app-description/domains/core-starter/workstreams/my-account/realization/source-alignment.md`

Validation reported:

```bash
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
mvn -Dtest='WorkstreamServiceTest,MyAccountPersonalAttentionDigestServiceTest' test
git diff --check
```

## Remaining automated alignment areas

- Split source alignment into actionable entries.
- Backend protected API/action path tests and repairs.
- Durable trace/audit verification.
- Notification center lifecycle and frontend rendering contracts.
- Bounded `human_chat_tool_plan` proposal/confirmation/denial/idempotency proof.
- Digest fail-closed/provider-runtime classification and tests.
- Frontend automated surface/action/secret-boundary coverage.
- Terminal verification and readiness updates.
