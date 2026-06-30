# Runtime-validation task authoring

Runtime-validation tasks are first-class generated artifacts. They are generated from app input, app-description graph compilation, and workstream-surface decomposition alongside implementation tasks. In the AI-first SaaS development loop, generated code is one implementation attempt; runtime-validation tasks are the durable executable acceptance contracts that decide whether the implementation is actually runtime-ready.

Use this document when creating `specs/runtime-validation/**` scenarios, seed plans, run records, or `type: runtime-validation` pending-task entries.

## Position in the app-input cascade

Generate runtime-validation tasks from the same cascade that creates implementation tasks:

```text
raw app/business input
  -> normalized current-intent delta
    -> app-description graph updates
      -> workstreams and workers
        -> workstream surfaces or non-UI workstream triggers
          -> surface actions / actor adapters
            -> governed tools and capabilities
              -> expected API/Akka/runtime path
                -> runtime-validation scenarios and tasks
```

If a runtime-validation scenario cannot be written clearly from the current graph, the work is not yet validation-ready. Reconcile the app-description, specs, or pending questions before inventing validation steps.

## Surface-driven validation doctrine

Runtime validation is workstream-surface-driven and worker-centric. A scenario normally starts from the surface a worker uses to perform, supervise, approve, or inspect work:

```text
worker
  -> workstream surface
    -> user action / confirmed plan / non-UI trigger
      -> API/client/actor adapter
        -> governed tool
          -> capability
            -> Akka component/service/substrate
              -> view/result/audit/work trace
```

Component tests, route checks, and frontend rendering can support lower readiness levels, but they do not replace a surface-driven scenario for user-visible runtime readiness.

Non-UI triggers are valid when the workstream promise is driven by a timer, consumer, workflow, autonomous task, integration callback, MCP/API-only operation, or agent background task. Even then, the scenario must identify the owning workstream outcome and the operator/result surface or trace where the effect is observed.

## Local runtime setup model

The default runtime-validation task shape is:

```text
1. Start the local Akka app with empty local persistence.
2. Run the local bootstrap step that creates only the SaaS app owner/base local runtime prerequisite.
3. Run a CLI seed command for the specific scenario.
4. The seed command prepares only the scenario's prerequisite state.
5. A human tester follows the scenario's detailed UI validation script.
6. Evidence is recorded and failures are classified.
7. Implementation, setup, app-description, or task artifacts are repaired and the scenario is rerun.
```

A runtime-validation task should name the app start/reset command, seed command, expected local URL, required provider configuration, and human handoff instructions. Exact CLI names are project-specific, but task briefs should make the command contract explicit enough for a future project to implement commands such as:

```bash
./tools/runtime-validation/start-local.sh --empty
./tools/runtime-validation/seed.sh RV-USER-ADMIN-001
```

or a combined runner:

```bash
./tools/runtime-validation/run-local.sh RV-USER-ADMIN-001 --mode human-manual
```

## Seed plans

A seed plan is a scenario-scoped, idempotent setup procedure that prepares validation prerequisites through the highest-fidelity available application process. Seed plans are not fixture dumps. The act of seeding should exercise real local app capabilities whenever practical.

Preferred seed mechanisms, from highest to lowest fidelity:

1. authenticated browser/admin UI actions;
2. authenticated API calls;
3. governed local/dev setup tools or CLI calls that invoke real app capabilities;
4. Akka component calls through a test harness when the scenario explicitly accepts a lower readiness claim;
5. direct persistence mutation only for fixture-mode tests, never for a `runtime-ready` user-visible claim.

Seed plans may use special local-dev system authority to simplify setup, but this authority is allowed only for local/dev validation setup. It must be unavailable in production, visibly recorded as seed/setup authority, and traceable. Seed authority must not be counted as proof that normal user authorization works.

Do not seed the behavior being validated. If the scenario validates "org admin invites a user," the seed plan may create the organization and org admin, but the invitation action itself must happen through the validation script.

Recommended reusable files:

```text
specs/runtime-validation/
  seed-plans/
    SEED-BOOTSTRAP-OWNER.md
    SEED-ORG-WITH-ADMIN.md
  scenarios/
    RV-USER-ADMIN-001-invite-user.md
  runs/
    2026-06-29-RUN-0001.md
```

Each seed plan should state:

- purpose and scenarios that use it;
- setup mode: `runtime`, `local-seeded`, `provider-missing-fail-closed`, or `fixture`;
- command or tool entrypoint;
- local-dev authority used, if any;
- required providers/configuration;
- personas and identity mapping inputs;
- state created or reused;
- idempotency/reset behavior;
- setup evidence emitted;
- failure classifications and unblock conditions.

## Local-dev passwordless identity pattern

When WorkOS/AuthKit test users are unavailable or unreliable, generated SaaS apps should provide an explicit local-only runtime-validation identity mode instead of weakening protected APIs.

Use a mode such as:

```text
APP_AUTH_MODE=workos      # default/production
APP_AUTH_MODE=local-dev   # local runtime validation only
```

In `local-dev` mode, the app may expose a local-only passwordless endpoint such as `POST /api/dev/auth/sign-in` that accepts a seeded test email and returns a bearer token usable by the normal protected APIs. This mode replaces identity proof only. `/api/me`, local account lookup, membership selection, roles, capabilities, tenant/customer scope, denial behavior, audit/work traces, and browser-safe redaction must remain the same authorization boundary used by WorkOS mode.

Typical seeded local-dev identities:

```text
saas.admin@example.test       -> SaaS/platform admin
org1.admin1@example.test      -> tenant/org admin
org1.user3@example.test       -> tenant/org user
cust1.admin@example.test      -> customer admin
cust1.user2@example.test      -> customer user
disabled.user@example.test    -> disabled account denial
inactive.user@example.test    -> no active membership/recovery denial
```

Seed plans should create or reuse local accounts, tenants, customers, memberships, roles, and settings first, then sign in by email through the local-dev auth endpoint. Unknown emails should be denied by default, or explicitly treated as no-membership recovery personas by the seed policy; they must not self-register privileged access.

Manual browser validation should include a local-only user switcher or sign-in panel when `APP_AUTH_MODE=local-dev` is enabled. Switching users must clear cached auth/bootstrap state, obtain a new local token, call `/api/me`, and reload role/capability-derived UI from the backend. This supports owner/admin/member/viewer/disabled/cross-tenant denial checks without WorkOS.

Safety requirements:

- local-dev auth is disabled unless explicitly configured;
- local-dev endpoints are unavailable in deployed/staging/production environments;
- no anonymous fallback is allowed when WorkOS config is missing in `workos` mode;
- roles and scope come from seeded backend state, never from arbitrary browser input;
- seed/setup traces clearly mark `local-dev-runtime-validation` authority;
- browser assets and run evidence never include reusable bearer tokens, provider secrets, or backend secrets.

Example scenario fields:

```yaml
authMode: local-dev
personas:
  - id: saas-owner
    email: saas.admin@example.test
  - id: org-admin
    email: org1.admin1@example.test
  - id: customer-user
    email: cust1.user2@example.test
```

## WorkOS test-user pattern

For WorkOS/AuthKit applications, runtime validation may rely on known WorkOS test users when they are available. This keeps identity real while allowing seed tooling to create local authorization and membership state.

Typical test identities:

```text
saas.owner@example.com
org.admin@example.com
customer.admin@example.com
customer.user@example.com
support.operator@example.com
unauthorized.user@example.com
```

WorkOS proves identity; the Akka app owns authorization. Seed tooling should map known WorkOS test identities to local accounts, memberships, roles, tenants, organizations, and customer/account scopes. The human tester should still log in through WorkOS/AuthKit so the browser obtains a real JWT and protected API calls use the normal auth path.

Preferred identity mapping options:

1. seed by stable WorkOS subject/user id provided through local environment variables;
2. seed by verified email and link to WorkOS subject on first `/api/me` under an explicit local/test policy;
3. have the seed CLI call the WorkOS Management API to ensure test users exist and fetch stable ids.

The selected project policy must be explicit. Runtime-validation scenarios should not silently rely on anonymous, unsigned-token, fixture-only, or frontend-only auth.

Example scenario fields:

```yaml
authMode: workos-test-users
personas:
  - id: saas-owner
    email: saas.owner@example.com
    workosUserIdEnv: WORKOS_TEST_USER_SAAS_OWNER_ID
  - id: org-admin
    email: org.admin@example.com
    workosUserIdEnv: WORKOS_TEST_USER_ORG_ADMIN_ID
```

## Human UI validation script

For `executionMode: human-manual`, each scenario needs a detailed UI script. It should be precise enough for a human tester who did not write the code.

Include:

- start state produced by the seed command;
- persona and login instructions;
- local URL or deep link to open;
- numbered UI actions;
- expected visible state and result surfaces;
- expected protected API/status behavior when relevant;
- expected trace/audit/work-trace evidence;
- denial or cross-tenant checks when relevant;
- evidence to capture;
- failure classification hints.

## Evidence model

Separate setup evidence from validation evidence.

Setup evidence proves that the local app started from empty state and the seed plan prepared prerequisites:

- app start command, branch/commit, environment, provider state;
- seed command and exit status;
- created/reused tenant, organization, account, membership, invitation, domain object, and trace ids;
- human test personas and URLs handed off by the seed command.

Validation evidence proves that the scenario itself worked:

- tester, persona, role/AuthContext, tenant/organization/customer scope;
- UI observations, screenshots, URLs, network/API statuses where useful;
- result surface state, view/projection ids, audit/work trace ids;
- provider-configured behavior or provider-missing fail-closed message;
- denial/forbidden/hidden/not-found behavior where required;
- final pass/fail/block conclusion and highest justified readiness level.

Seed success alone never passes a scenario.

## Progressive scenario ladder

Runtime-validation scenarios should build from simple bootstrap/auth scenarios toward comprehensive workstream scenarios. Later scenarios may depend on earlier ones:

```text
RV-BOOT-001 app starts empty and bootstraps the local SaaS owner
RV-AUTH-001 SaaS owner can log in through WorkOS/AuthKit and /api/me links locally
RV-ADMIN-001 owner can create or seed an organization and org admin
RV-ADMIN-002 org admin can invite a user
RV-ADMIN-003 invited or seeded user can access the correct organization scope
RV-DOMAIN-001 domain workstream happy path
RV-DOMAIN-002 domain workstream denial/edge path
```

If a prerequisite scenario fails, dependent scenarios are `blocked`, not independently failed as unrelated product defects.

## Runtime-validation task minimum contract

A pending-task entry with `type: runtime-validation` should include or reference:

- scenario id(s) under `specs/runtime-validation/scenarios/`;
- owning workstream and surface or explicit non-UI trigger;
- worker/persona and AuthContext/tenant/customer scope;
- readiness target;
- dependency scenarios;
- local app start/reset command;
- bootstrap expectation;
- seed plan id(s) and seed command;
- auth mode and WorkOS test-user mapping when applicable;
- human UI validation script path or inline script;
- evidence to capture;
- provider/config requirements and fail-closed expectations;
- failure classification and reconciliation path;
- required run-record output under `specs/runtime-validation/runs/`.

Example frontmatter:

```yaml
id: TASK-000142
status: pending
type: runtime-validation
phase: runtime-validation
readinessTarget: runtime-ready
source: RV-USER-ADMIN-001
scenarios:
  - RV-USER-ADMIN-001
executionMode: human-manual
environment: local-empty
startCommand: ./tools/runtime-validation/start-local.sh --empty
seedCommand: ./tools/runtime-validation/seed.sh RV-USER-ADMIN-001
seedPlans:
  - SEED-BOOTSTRAP-OWNER
  - SEED-ORG-WITH-ADMIN
authMode: workos-test-users
dependsOnScenarios:
  - RV-BOOT-001
  - RV-AUTH-001
```

## Failure classification additions

Runtime-validation findings should use the categories in `runtime-validation-reconciliation.md`. Seed-specific failures should be made explicit:

- `bootstrap gap` — empty local startup or owner bootstrap does not establish the required base state;
- `auth/setup gap` — seeded users, WorkOS identities, local account linking, membership, or persona handoff is wrong;
- `seed tooling gap` — CLI/setup tool fails despite the app capability being correctly specified and implemented;
- `provider/config blocker` — WorkOS, Resend, model provider, or other external/local config is missing or unusable;
- `implementation gap` — accepted intent exists but the runtime app cannot create or use the required setup or validation path;
- `app-description gap` — setup or validation expectation is not actually specified;
- `UX/state gap` — the user can technically proceed, but state, copy, loading, errors, recovery, or evidence visibility is inadequate;
- `not a bug / expectation change` — tester expectation conflicts with accepted current intent.

## Authoring checklist

Before marking a runtime-validation task runnable, verify:

- the scenario is workstream-surface-driven or has an explicit non-UI workstream trigger;
- the local-empty start and owner bootstrap assumptions are stated;
- reusable seed plans exist or are created with setup mode, authority, idempotency, and evidence expectations;
- WorkOS test-user/auth mapping is explicit when browser auth is in scope;
- the human UI validation script is detailed enough to execute;
- setup evidence and validation evidence are distinct;
- scenario dependencies form a sensible ladder;
- provider/config blockers and fail-closed expectations are named;
- failures route to reconciliation tasks rather than ad hoc code patching;
- fixture-only setup is not used to justify `runtime-ready` user-visible behavior.
