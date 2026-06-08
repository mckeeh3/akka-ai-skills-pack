# Notification Foundation Validation

## Task

`TASK-NPF-04-001: Run notification foundation validation`

## Scope validated

Validated the rendered AI-first SaaS starter notification foundation, including backend-owned in-app notification projection/lifecycle/preferences, My Account notification center surface contracts, and hidden-workstream redaction guardrails.

## Scaffold

Command:

```bash
tmpdir=$(mktemp -d /tmp/npf-scaffold-XXXXXX)
./tools/scaffold-ai-first-saas-starter.sh \
  --target "$tmpdir" \
  --template-dir templates/ai-first-saas-starter \
  --app-name "Notification Validation App" \
  --app-slug "notification-validation-app" \
  --base-package "com.example" \
  --maven-group-id "com.example" \
  --yes
```

Result:

- Scaffold succeeded.
- Rendered path used for validation: `/tmp/npf-scaffold-AZvlj1`.
- Direct `templates/ai-first-saas-starter/backend` Maven execution is intentionally not valid before scaffolding because template placeholders remain in `pom.xml`; rendered scaffold Maven execution is the authoritative check.

## Required checks

### `git diff --check`

Command:

```bash
git diff --check
```

Result: passed.

### Scaffolded backend Maven tests

Command:

```bash
cd /tmp/npf-scaffold-AZvlj1
mvn test
```

Result:

- Status: passed.
- Summary: `Tests run: 235, Failures: 0, Errors: 0, Skipped: 1`.
- Notification-specific evidence:
  - `NotificationServiceTest`: `Tests run: 6, Failures: 0, Errors: 0, Skipped: 0`.
  - `DurableNotificationRepositoryEntityTest`: `Tests run: 1, Failures: 0, Errors: 0, Skipped: 0`.
- Expected local/runtime log noise observed without test failure:
  - real provider smoke test skipped without provider configuration;
  - fail-closed email delivery logs for missing Resend env vars;
  - Akka testkit shutdown/pool termination logs.

### Frontend tests, typecheck, and build

Commands:

```bash
cd /tmp/npf-scaffold-AZvlj1/frontend
npm install
npm test
npm run typecheck
npm run build
```

Result:

- `npm install`: passed, `0 vulnerabilities`.
- `npm test`: passed, `tests 132`, `pass 132`, `fail 0`.
- `npm run typecheck`: passed.
- `npm run build`: passed; Vite emitted static resources under rendered `src/main/resources/static-resources`.

Note: running scaffolded frontend tests before installing dependencies failed on missing `typescript`; rerunning after `npm install` passed. This is not a notification foundation blocker.

### Focused scans

Commands:

```bash
find templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}} \
  templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}} \
  -type f | grep -E 'Notification|FoundationRole|MyAccountService|WorkstreamService' | wc -l

rg -o 'notification\.[a-z_]+|notification:in_app|IN_APP' \
  templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}} \
  templates/ai-first-saas-starter/frontend/src | wc -l

rg -o 'not_found_or_redacted|redaction|SUMMARY_ONLY|hidden workstream|hidden categories|backend-derived|frontend-only|in-app only|email/push' \
  templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/NotificationService.java \
  templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/NotificationServiceTest.java \
  templates/ai-first-saas-starter/frontend/src/workstream/surfaces/NotificationCenterSurface.tsx \
  templates/ai-first-saas-starter/frontend/src/workstream-my-account-vertical.contract.test.mjs | wc -l

rg -n 'not_found_or_redacted|hidden workstream|backend-derived|frontend-only|email/push|in-app only|notification\.list_my_account_center|notification\.mark_read|notification\.dismiss|notification\.archive|notification\.snooze|notification\.update_preferences|notification:in_app' \
  templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/NotificationService.java \
  templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/NotificationServiceTest.java \
  templates/ai-first-saas-starter/frontend/src/workstream/surfaces/NotificationCenterSurface.tsx \
  templates/ai-first-saas-starter/frontend/src/workstream-my-account-vertical.contract.test.mjs
```

Result:

- Backend notification-related source/test files found: `23`.
- Notification capability/channel refs found: `67`.
- Hidden-workstream/redaction/backend-derived/in-app-only refs in focused files found: `16`.
- Focused scan found backend-owned notification tool ids, deterministic `notification:in_app` dedupe key construction, safe `not_found_or_redacted` denials, frontend notification center count source from `notification.list_my_account_center`, and My Account tests asserting backend rather than frontend-only authority.

## Assessment

The notification foundation validation passed for the rendered scaffold. Backend tests cover notification projection/lifecycle/preferences and durable repository behavior. Frontend tests/typecheck/build pass after dependency installation. Focused scans show the implementation remains backend-owned, in-app-only, capability-gated, and redaction-aware, with no evidence that hidden workstream/item existence is exposed through notification center authority.

No follow-up blockers were identified for this validation slice.
