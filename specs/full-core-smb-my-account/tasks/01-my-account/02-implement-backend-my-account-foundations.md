# Task: Implement backend My Account foundations

## Objective

Implement the deterministic backend My Account service boundary for `/api/me`, selected context/authority summary, profile/settings lifecycle, dynamic My Account surfaces, and safe open-workstream authorization.

## Required reads

- AGENTS.md
- specs/full-core-smb-my-account/README.md
- specs/full-core-smb-my-account/conversation-capture.md
- specs/full-core-smb-my-account/my-account-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/my-account-workstream-v0/workstream-contract.md
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/MeService.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/MeResponse.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AuthContextResolver.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java
- templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/MeServiceTest.java
- templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java

## In scope

- Add a focused `MyAccountService` or equivalent deterministic service boundary if source inspection confirms it is cleaner than expanding `WorkstreamService`.
- Extend `/api/me`/`MeResponse` with browser-safe authority-basis/context summary fields needed by My Account.
- Preserve selected-context authorization via `X-Selected-Context-Id`/`X-Selected-Membership-Id`.
- Add dynamic retrieval for `surface-my-account-dashboard`, `surface-my-profile`, and `surface-my-settings`.
- Keep profile/settings updates limited to allowed self-service fields, with validation, idempotency/no-op, audit, and denial tests.
- Make `my_account.open_authorized_workstream` authorize the requested target and return safe `system_message` denials for hidden/forbidden targets.

## Out of scope

- Do not implement personal attention aggregation beyond minimal fields needed by this backend foundation.
- Do not implement MyAccountAgent evidence tools or personal digest workers.
- Do not add My Account to the top rail.
- Do not add identity-provider administration or admin mutation scope.

## Expected outputs

- Updated backend My Account service/source files.
- Updated backend tests for `/api/me`, context, authority, profile/settings update, dynamic surfaces, open-workstream denials, tenant isolation, idempotency/no-op, and audit traces.
- Updated queue status.

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=MeServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest
rg -n "MyAccountService|my_account\.view_context|my_account\.update_profile_settings|my_account\.open_authorized_workstream|surface-my-account-dashboard|surface-my-profile|surface-my-settings|selected context|authority|system_message|tenant|trace" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- Backend My Account account/context/profile/settings/open-workstream behavior is deterministic, capability-checked, trace-linked, and tested.
- My Account surfaces are independently retrievable through the workstream surface endpoint.
- Forbidden/hidden workstream open requests fail closed with safe non-leaking denials.
- Task changes and queue update are committed with `full-core-smb: implement my account backend foundations`.
