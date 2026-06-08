# Notification Foundation Verification

## Task

`TASK-NPF-99-001: Verify notification foundation`

## Assessment

The notification platform foundation mini-project is complete for the first governed **in-app notification foundation** slice. No bounded follow-up tasks are required from this verification pass.

Verified scope remains limited to backend-owned in-app notification projection, lifecycle, preferences, and the My Account notification center surface. Email, SMS, mobile push, webhook, Slack/Teams, external delivery, subscription fan-out, delivery analytics, and broad enterprise notification-platform features remain future governed delivery-channel work.

## Fresh scaffold verification

Command:

```bash
validation_dir=$(mktemp -d /tmp/npf-verify-XXXXXX)
./tools/scaffold-ai-first-saas-starter.sh \
  --target "$validation_dir" \
  --template-dir templates/ai-first-saas-starter \
  --app-name "Notification Verification App" \
  --app-slug "notification-verification-app" \
  --base-package "com.example" \
  --maven-group-id "com.example" \
  --yes
```

Result: scaffold succeeded.

Rendered path used for verification: `/tmp/npf-verify-sWf53o`.

## Required checks

### `git diff --check`

Result: passed.

### Targeted backend tests for projection/lifecycle/preferences/redaction

Command:

```bash
cd /tmp/npf-verify-sWf53o
mvn test
```

Result: passed.

Summary:

- `Tests run: 235, Failures: 0, Errors: 0, Skipped: 1`.
- `NotificationServiceTest`: `Tests run: 6, Failures: 0, Errors: 0, Skipped: 0`.
- `DurableNotificationRepositoryEntityTest`: `Tests run: 1, Failures: 0, Errors: 0, Skipped: 0`.

Expected non-blocking local/runtime log noise was observed without test failure: real-provider smoke skipped without provider config, fail-closed Resend env logs, and Akka testkit shutdown/pool termination logs.

### Frontend tests/typecheck/build

Commands:

```bash
cd /tmp/npf-verify-sWf53o/frontend
npm install
npm test
npm run typecheck
npm run build
```

Results:

- `npm install`: passed, `0 vulnerabilities`.
- `npm test`: passed, `tests 132`, `pass 132`, `fail 0`.
- `npm run typecheck`: passed.
- `npm run build`: passed.

### Focused scans

Commands covered:

```bash
rg -n 'in-app|NotificationPreference|NotificationProjectionInput|AuthContext|redaction|Lifecycle|surface-my-account-notification-center|email/push' \
  specs/notification-platform-foundation/notification-foundation-contract.md

rg -n 'projectFromSource|notification:in_app|markRead|dismiss|archive|snooze|updatePreferences|not_found_or_redacted|redaction|tenant|AuthContext' \
  templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/NotificationService.java \
  templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/NotificationServiceTest.java

rg -n 'surface-my-account-notification-center|notification_center|backend-derived|in-app only|email/push|notification\.list_my_account_center|notification\.mark_read|notification\.dismiss|notification\.archive|notification\.snooze|notification\.update_preferences' \
  templates/ai-first-saas-starter/frontend/src/workstream/surfaces/NotificationCenterSurface.tsx \
  templates/ai-first-saas-starter/frontend/src/workstream-my-account-vertical.contract.test.mjs \
  specs/notification-platform-foundation/notification-foundation-handoff.md \
  specs/notification-platform-foundation/README.md
```

Result: passed.

Scan evidence confirmed:

- contract coverage for in-app notifications, preferences, projection inputs, AuthContext redaction, lifecycle, My Account notification center, and future email/push boundary;
- backend projection and lifecycle implementation with deterministic `notification:in_app` dedupe keys;
- lifecycle/preference/redaction tests and safe `not_found_or_redacted` denials;
- My Account notification center surface id and backend-derived count source;
- docs/handoff preserving the in-app-only implemented scope and future email/push boundary.

## Follow-up decision

No follow-up tasks were appended. The terminal verification task can be marked done.
