# Prompt-Risk AutonomousAgent Validation

Date: 2026-06-01
Task: `TASK-AAPR-04-001`

## Scope

Validated the rendered `templates/ai-first-saas-starter` scaffold after the prompt-risk AutonomousAgent runtime, v3 event/attention, and Agent Admin surface wiring tasks.

## Commands and results

### Template-source Maven shortcut

Command:

```bash
cd templates/ai-first-saas-starter/backend && mvn test
```

Result: blocked before compilation because the template source intentionally contains unresolved placeholders:

- `groupId` = `{{MAVEN_GROUP_ID}}`
- `artifactId` = `{{APP_SLUG}}`

This is not a prompt-risk runtime failure; scaffolded validation must run against a rendered target.

### Scaffolded fullstack validation without provider secret

Command:

```bash
env -u OPENAI_API_KEY tools/validate-ai-first-saas-starter-fullstack.sh
```

Result: passed.

Evidence from the scaffolded target `/tmp/ai-first-saas-starter-fullstack.l6F6lv`:

- backend Maven tests: `207` tests, `0` failures, `0` errors, `1` skipped;
- Akka annotation processing detected `2 autonomous-agent` components along with the other starter components;
- prompt-risk backend tests included:
  - `AgentAdminPromptRiskReviewServiceTest`: `4` tests passed;
  - `AgentAdminPromptRiskAutonomousAgentTest`: `2` tests passed;
  - `DurablePromptRiskReviewTaskRepositoryEntityTest`: `1` test passed;
- frontend tests: `132` tests passed;
- frontend typecheck: passed;
- frontend build: passed and wrote static resources under `src/main/resources/static-resources`;
- static asset secret scan: passed;
- optional real-provider smoke: skipped safely because `OPENAI_API_KEY` was unset for this run.

### Optional real-provider smoke with ambient provider configuration

Command:

```bash
tools/validate-ai-first-saas-starter-fullstack.sh
```

Result: blocked during the optional real-provider smoke because the ambient shell had an `OPENAI_API_KEY` value and the targeted `RealModelProviderSmokeTest` failed:

- scaffold target: `/tmp/ai-first-saas-starter-fullstack.9JA7di`;
- backend Maven tests before the optional smoke passed with `207` tests, `0` failures, `0` errors, `1` skipped;
- frontend tests/typecheck/build and static secret scan passed;
- optional smoke failed in `RealModelProviderSmokeTest.workstreamMessageSubmissionUsesRealProviderAndEmitsTraceShape` with `expected: <true> but was: <false>` after a TLS termination warning to `api.openai.com:443`.

This is recorded as an environment/provider smoke blocker for real-provider validation. It does not indicate a deterministic/model-less success path; the scaffolded no-provider path fails closed/skips provider smoke safely, and prompt-risk tests use named TestKit/test-provider infrastructure only.

## Focused evidence scan

A focused scan found prompt-risk runtime/surface markers in starter backend/frontend sources:

```bash
rg "AgentAdminPromptRisk|prompt_risk_review|workflow\.agent_admin\.prompt_risk_review|surface-agent-admin-prompt-risk-review|attention:worker-task|blocked_provider_or_runtime|model-less|fake|deterministic" templates/ai-first-saas-starter/backend/src templates/ai-first-saas-starter/frontend/src
```

Representative evidence:

- prompt-risk capabilities: `agent_admin.prompt_risk_review.start/read/cancel/accept_result/reject_result`;
- surface contract: `surface-agent-admin-prompt-risk-review` and `agent_admin.prompt_risk_review_task.v1`;
- fail-closed marker: `blocked_provider_or_runtime`;
- no-fake-success markers: tests assert the UI/runtime must not fake progress or produce model-less/deterministic fake analysis.

## Conclusion

The scaffolded prompt-risk AutonomousAgent vertical validates successfully for the rendered starter with provider absent/fail-closed behavior. Real provider end-to-end smoke remains dependent on working provider/network configuration and is recorded above as a blocked optional smoke path rather than a prompt-risk-specific implementation failure.
