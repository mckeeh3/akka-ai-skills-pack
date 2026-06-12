# Conversation Capture: Operational Deployment Readiness

## Source discussion

After the User Admin production runtime hardening mini-project completed, the recommended next scope was Operational Deployment Readiness so the hardened runtime has clear environment configuration, smoke commands, health checks, and runbooks for real deployment.

The user asked to do:

1. archive the completed production runtime hardening mini-project;
2. run the release confidence checks;
3. start the recommended operational deployment readiness mini-project.

## Completed immediately before this scaffold

- Archived `specs/user-admin-production-runtime-hardening/` to `specs/archive/user-admin-production-runtime-hardening/`.
- Ran confidence checks:
  - `env -u ADMIN_USERS mvn test` passed;
  - `npm --prefix frontend run smoke:user-admin-workstream` passed;
  - `npm --prefix frontend test -- --run` passed;
  - `npm --prefix frontend run typecheck` passed;
  - `npm --prefix frontend run build` passed;
  - `git diff --check` passed.

## Decisions

- Treat deployment readiness as root app work.
- Do not add secrets to the repository.
- Use documentation plus minimal code/tests/scripts only where they materially improve deployment validation.
- Preserve fail-closed behavior for missing provider/model/email/auth configuration.
- Keep cloud-provider-specific deployment instructions out of scope unless the repository already has provider-specific docs.

## Known caveats

- Local shell may set `ADMIN_USERS` to a Tenant Admin entry; broad Maven checks should be run with `ADMIN_USERS` unset unless explicitly testing production bootstrap rejection.
- `npm --prefix frontend run build` regenerates static frontend assets under `src/main/resources/static-resources/`.
