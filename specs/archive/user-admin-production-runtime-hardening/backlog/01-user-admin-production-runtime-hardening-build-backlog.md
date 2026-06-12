# Build Backlog: User Admin Production Runtime Hardening

## Objective

Move selected User Admin runtime paths from starter/deterministic readiness to production-grade governed behavior for invitation delivery, identity exception recovery, and model-backed access-review automation.

## Work item 1: App-description/spec alignment

Update current intent and realization expectations for the three areas:

- invitation provider/outbox lifecycle, retry, failure recovery, user-safe surfaces, and real-provider smoke expectations;
- identity exception recovery lifecycle, review/approval semantics, workflow/result surfaces, and no-op/idempotency;
- access-review model-backed agent execution, model config, tools, skill/reference behavior, traces, and fail-closed states.

## Work item 2: Provider-backed invitation delivery hardening

Implement or repair:

- Resend-backed provider/outbox execution path;
- retry and failure state transitions;
- resend/revoke interaction with outbox state;
- admin/user-safe delivery history surfaces;
- missing provider config fail-closed surfaces;
- tests for configured fake provider success, missing config, retry/no-op, revocation, audit traces, and token/secret non-exposure;
- optional real-provider smoke test skipped unless credentials are present.

## Work item 3: Production identity exception recovery workflows

Implement or repair:

- durable identity exception/relink request state;
- review/approve/deny/complete commands;
- workflow or entity/service path with idempotency and audit;
- WorkOS/provider-boundary redaction;
- User Admin task/decision/status surfaces;
- tests for request, approval, denial, completion, no-op/replay, hidden target, cross-scope denial, and trace emission.

## Work item 4: Model-backed access-review automation

Implement or repair:

- governed AccessReviewAgent/UserAdminAgent invocation from access-review task path;
- model config/policy checks and missing-provider fail-closed state;
- tool permission boundaries for user/audit evidence access;
- authorized skill/reference loading where applicable;
- durable task progress/result state and human review routing;
- work traces for model, prompt, tool, data, policy, and result usage;
- deterministic test model provider coverage plus missing-config/denial coverage.

## Work item 5: Frontend/workstream integration

Ensure surfaces expose:

- invitation delivery status/retry/recovery;
- identity recovery review/status/approval/denial/completion;
- access-review model-backed task progress/result/fail-closed states;
- no raw provider/model/prompt/secret/token data.

## Work item 6: Verification

Run focused and broader checks, update browser smoke if needed, and append follow-up tasks plus a new terminal verification task when material gaps remain.

## Suggested task breakdown

- `TASK-UAPRH-00-001`: create planning scaffold.
- `TASK-UAPRH-01-001`: align app-description/spec runtime contracts.
- `TASK-UAPRH-02-001`: harden provider-backed invitation delivery.
- `TASK-UAPRH-02-002`: add invitation delivery surfaces/tests/smoke docs.
- `TASK-UAPRH-03-001`: implement durable identity exception recovery backend.
- `TASK-UAPRH-03-002`: wire identity recovery workstream/frontend surfaces and tests.
- `TASK-UAPRH-04-001`: implement model-backed access-review agent runtime path.
- `TASK-UAPRH-04-002`: add access-review agent surfaces, traces, and tests.
- `TASK-UAPRH-05-001`: run integrated User Admin production runtime tests and update smoke coverage.
- `TASK-UAPRH-99-001`: terminal verification.
