# AutonomousAgent Runtime Validation Evidence

## Task

`TASK-AAI-04-001: Run AutonomousAgent runtime validation`

## Validation target

Fresh scaffold rendered from `templates/ai-first-saas-starter` into `/tmp/aai-runtime-validation` with:

```bash
./tools/scaffold-ai-first-saas-starter.sh \
  --target /tmp/aai-runtime-validation \
  --template-dir templates/ai-first-saas-starter \
  --app-name 'AAI Runtime Validation' \
  --app-slug aai-runtime-validation \
  --base-package ai.first \
  --maven-group-id ai.first \
  --yes
```

Scaffold completed successfully and wrote 371 files plus `specs/scaffold-report.md`.

## Backend evidence

Command:

```bash
cd /tmp/aai-runtime-validation && mvn -q test
```

Result: passed.

Observed runtime/test coverage includes:

- Akka Runtime/TestKit startup with starter components discovered.
- `UserAdminAccessReviewAutonomousAgentTest` exercising Akka `AutonomousAgent` task completion/failure test infrastructure.
- `UserAdminAccessReviewServiceTest` covering lifecycle, idempotency, fail-closed, and projection behavior.
- `WorkstreamEventBackboneServiceTest` covering access-review `workflow.access_review.*`, `worker.task.*`, `autonomous_task` refs, and attention linkage.
- Provider/configuration failures remain fail-closed; Resend/model provider missing configuration logs are actionable and do not produce model-less success.

## Frontend evidence

Initial `npm test` before dependency installation failed because the fresh scaffold had no local `node_modules` and `typescript` was not installed. This is expected for a clean scaffold checkout and was resolved with the normal dependency installation step.

Commands:

```bash
cd /tmp/aai-runtime-validation/frontend
npm ci
npm test
npm run typecheck
npm run build
```

Result: passed.

Observed frontend evidence:

- `npm ci`: added dependencies from lockfile with 0 vulnerabilities.
- `npm test`: 132 tests passed.
- `npm run typecheck`: passed.
- `npm run build`: Vite build passed and emitted Akka static resources under `../src/main/resources/static-resources`.

## Focused guardrail checks

Command group:

```bash
cd /tmp/aai-runtime-validation
rg "AutonomousAgent|TaskAcceptance|runSingleTask|forTask\(|completeTask|failTask" src
rg "blocked_provider_or_runtime|fail closed|no deterministic|model-less|fake" src frontend/src
rg "workflow.access_review|worker.task|autonomous_task|attention:worker-task|surface-user-admin-access-review" .
```

Result: passed.

Evidence found:

- Concrete `AutonomousAgent` task APIs, including `runSingleTask`, task snapshots, and `TestModelProvider.AutonomousAgentTools.completeTask` / `failTask` in tests.
- Fail-closed guardrails for provider/runtime absence and no deterministic/model-less/fake normal success.
- Access-review lifecycle events, worker-task events, `autonomous_task` source refs, worker-task attention ids, and `surface-user-admin-access-review` surface/action wiring.

## Manual/local smoke notes

A live provider-backed model smoke was not run because the validation environment does not have production provider secrets/configuration. That is the intended fail-closed boundary for this starter: missing provider/runtime configuration must produce blocked/provider status and actionable trace/surface evidence rather than successful recommendations.

Local scaffold smoke was covered through production-like Akka TestKit/runtime startup, backend lifecycle/event/attention tests, frontend API/surface tests, typecheck, and static build. No release blocker was found for the scoped User Admin Access Review AutonomousAgent runtime path.

## Outcome

`TASK-AAI-04-001` validation passed. No additional blocker tasks are required from this validation run.
