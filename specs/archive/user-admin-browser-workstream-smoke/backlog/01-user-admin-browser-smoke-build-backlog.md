# Build Backlog: User Admin Browser Workstream Smoke

## Objective

Create a bounded smoke suite that exercises User Admin structured surfaces through a real local app UI/workstream path, beyond starter-scope contract tests.

## Design notes

- Prefer using existing project tooling and dependencies before introducing new browser automation packages.
- If a browser automation dependency is added, keep it minimal, documented, and compatible with CI/local runs.
- Smoke tests must not rely on external provider credentials by default.
- Test-only seed/config must not weaken production bootstrapping or tenant/customer authorization boundaries.
- Screenshots may be useful as artifacts, but assertions must be machine-checkable.

## Work items

### 1. Smoke scope and tooling survey

Survey current frontend/backend scripts, test dependencies, hosted frontend endpoint, and workstream API behavior. Decide whether to use Playwright, a DOM-driven smoke test, an HTTP + static asset smoke, or a staged approach.

### 2. Deterministic local smoke setup

Define and implement any necessary test-only configuration or seed path so the smoke can load a known authorized User Admin context without external WorkOS/Resend/model provider credentials.

### 3. Browser/workstream smoke implementation

Implement smoke coverage for:

- app shell loads;
- User Admin functional agent opens;
- dashboard renders actionable attention/available-work sections;
- dashboard opens User Directory;
- User Directory row opens read-only User Detail or Invitation Detail;
- dedicated task surfaces open from detail/list/dashboard where deterministic data allows;
- representative denied/blocked path renders typed `system_message`;
- raw secrets/tokens/provider ids are absent.

### 4. Documentation and script integration

Document how to run the smoke suite locally, expected environment variables, generated artifacts, and known skipped external-provider cases. Add package or tool scripts where appropriate.

### 5. Verification

Run or review all required checks, compare against README done state, and append follow-up tasks plus a new terminal verification task when material gaps remain.

## Suggested task breakdown

- `TASK-UABWS-00-001`: create mini-project planning scaffold.
- `TASK-UABWS-01-001`: survey smoke tooling and choose implementation approach.
- `TASK-UABWS-02-001`: implement deterministic local smoke setup.
- `TASK-UABWS-03-001`: implement User Admin browser/workstream smoke tests.
- `TASK-UABWS-04-001`: document and integrate smoke command.
- `TASK-UABWS-99-001`: terminal verification.
