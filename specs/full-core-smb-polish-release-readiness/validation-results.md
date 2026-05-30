# Full-Core SMB Integrated Validation Results

Date: 2026-05-30

## Summary

Integrated validation passed for the rendered AI-first SaaS starter. No release-blocking validation failures were found in this task.

Provider modes covered:

- ambient real-provider mode: `OPENAI_API_KEY` was present in the harness environment, so fullstack validation ran the real Akka Agent provider smoke path inside the rendered scaffold.
- provider-missing mode: `env -u OPENAI_API_KEY tools/smoke-ai-first-saas-starter-real-model.sh` skipped loudly with actionable enablement guidance, as expected for no-provider CI.

Notes:

- The starter source under `templates/ai-first-saas-starter/backend` is template source and cannot run Maven directly before scaffold rendering because `{{MAVEN_GROUP_ID}}` and `{{APP_SLUG}}` placeholders are intentionally unresolved.
- A targeted rendered backend command failed when inherited harness `ADMIN_USERS=mckeeh3@gmail.com:TENANT_ADMIN:tenant-starter` replaced the deterministic `admin@example.test` test seed expected by `AdminEndpointIntegrationTest`. Re-running the same rendered command with `env -u ADMIN_USERS` passed. This is classified as an environmental command hygiene issue, not a release blocker.

## Commands and results

### Fullstack scaffold validation

```bash
tools/validate-ai-first-saas-starter-fullstack.sh
```

Result: pass.

Evidence summary:

- scaffolded starter into `/tmp/ai-first-saas-starter-fullstack.QDRd7y`;
- verified rendered backend, frontend, and planning paths;
- verified `WorkstreamRuntimeAgent` runtime tool registration gates;
- ran rendered backend Maven tests: 170 tests, 0 failures, 0 errors, 1 skipped provider smoke test in the unit-test phase;
- ran frontend `npm install`, `npm test -- --run`, `npm run typecheck`, and `npm run build`;
- frontend tests passed: 121 tests, 0 failures;
- built Akka static resources and scanned built static assets for backend secret leaks;
- because `OPENAI_API_KEY` was present, optional real model smoke ran through backend workstream message submission and passed without provider-secret leaks in smoke logs, frontend env, or static assets.

### Fullstack scaffold validation with kept rendered target

```bash
tools/validate-ai-first-saas-starter-fullstack.sh --keep
```

Result: pass.

Evidence summary:

- scaffolded starter into `/tmp/ai-first-saas-starter-fullstack.ASsYXm`;
- repeated fullstack backend/frontend validation successfully;
- optional real model smoke ran and passed;
- kept the rendered target for focused follow-up commands.

### Workstream icon proof

```bash
tools/prove-workstream-icons-v0.sh
```

Result: pass.

Evidence summary:

- scaffolded starter into `/tmp/workstream-icons-v0-proof.NnJLMj`;
- verified descriptor-backed left-rail icon affordances for User Admin, Agent Admin, Audit/Trace, and Governance/Policy;
- verified My Account remains available only through the lower-left signed-in user tile, not the top rail.

### Provider-missing smoke mode

```bash
env -u OPENAI_API_KEY tools/smoke-ai-first-saas-starter-real-model.sh
```

Result: expected skip, not a failure.

Evidence summary:

- smoke reported `Akka Agent smoke skipped: OPENAI_API_KEY is not set or is blank`;
- smoke reported how to enable real provider validation by exporting `OPENAI_API_KEY` and optional provider variables.

Classification: environmental skip for no-provider mode; not a release blocker because the fullstack validation in this task also covered real-provider mode with ambient provider configuration.

### Focused backend workstream/admin/governance tests on rendered scaffold

Initial source-template command attempted from `templates/ai-first-saas-starter/backend`:

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=MeServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest,InvitationAndUserAdminServiceTest,UserAdminAccessReviewServiceTest,GovernancePolicyServiceTest
```

Result: expected template-source failure before rendering.

Failure summary:

- Maven rejected placeholder `{{MAVEN_GROUP_ID}}` and `{{APP_SLUG}}` values.

Classification: command target hygiene; not a release blocker. Backend Maven commands must run against a rendered scaffold target.

Rendered scaffold with inherited harness `ADMIN_USERS`:

```bash
cd /tmp/ai-first-saas-starter-fullstack.ASsYXm && mvn test -Dtest=MeServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest,InvitationAndUserAdminServiceTest,UserAdminAccessReviewServiceTest,GovernancePolicyServiceTest
```

Result: environmental failure.

Failure summary:

- 66 tests ran; `AdminEndpointIntegrationTest` had 3 errors;
- protected admin endpoint requests for `admin@example.test` received `403 Forbidden: no-local-account-or-invitation`;
- the harness environment had `ADMIN_USERS=mckeeh3@gmail.com:TENANT_ADMIN:tenant-starter`, so the deterministic test admin account was not seeded.

Classification: environmental command hygiene; not a release blocker.

Rendered scaffold with local admin bootstrap unset:

```bash
cd /tmp/ai-first-saas-starter-fullstack.ASsYXm && env -u ADMIN_USERS mvn test -Dtest=MeServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest,InvitationAndUserAdminServiceTest,UserAdminAccessReviewServiceTest,GovernancePolicyServiceTest
```

Result: pass.

Evidence summary:

- 66 tests ran, 0 failures, 0 errors, 0 skipped;
- invitation email production-delivery missing-env errors were logged intentionally by tests and did not fail, preserving fail-closed behavior for missing Resend configuration.

### Focused backend governed-agent runtime tests on rendered scaffold

```bash
cd /tmp/ai-first-saas-starter-fullstack.ASsYXm && env -u ADMIN_USERS mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest,AgentRuntimeTraceEntityTest,AgentRuntimeTraceViewTest,AgentRuntimeTraceSinkTest,DurableAgentBehaviorRepositoryStateTest,ManifestBoundaryEntityTest,ManifestBoundaryViewTest
```

Result: pass.

Evidence summary:

- 75 tests ran, 0 failures, 0 errors, 0 skipped;
- expected `runtime-tool-tenant-mismatch` denial was logged by `WorkstreamRuntimeAgentTest` and covered by passing tests.

### Focused frontend tests, typecheck, and build

```bash
cd templates/ai-first-saas-starter/frontend && npm test -- --run && npm run typecheck && npm run build
```

Result: pass.

Evidence summary:

- 121 frontend tests passed, 0 failures;
- TypeScript typecheck passed;
- Vite build passed and wrote Akka static resources;
- Vite reported a non-blocking chunk-size warning for a chunk larger than 500 kB.

Classification: pass with non-blocking bundle-size recommendation only.

## Blocker classification

No release blockers were found by this validation task.

Environmental/non-blocking findings:

1. Direct Maven commands cannot run from unrendered template source; use the rendered scaffold target from `tools/validate-ai-first-saas-starter-fullstack.sh --keep` for focused backend commands.
2. Focused rendered backend commands that include `AdminEndpointIntegrationTest` should unset or control `ADMIN_USERS` unless intentionally validating a custom admin bootstrap, because the test expects deterministic `admin@example.test` seed data.
3. Frontend production build reports a chunk-size warning; this is a post-release optimization recommendation, not a release blocker for the SMB baseline.

## Queue impact

No bounded blocker tasks were appended. Continue with the planned visual UX and cross-workstream polish review.
