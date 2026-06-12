# Conversation Capture: User Admin Browser Workstream Smoke

## Source discussion

After completing User Admin surface conformance cleanup, the user asked what should happen next. Recommended next options were:

1. close/archive the completed mini-project;
2. run broader confidence checks;
3. start a new scoped mini-project only if going beyond starter scope.

The user requested options 1 and 2, which archived the completed conformance mini-project and ran broader checks. The user then requested option 3: start a new scoped mini-project to go beyond starter scope.

## Chosen scope

The chosen next mini-project is **full browser/workstream smoke automation for User Admin**. This was selected because it is the most direct next maturity step beyond starter-scope backend/frontend contract tests: it validates the now-conformant structured surfaces through the real hosted UI and workstream API path.

## Decisions

- Treat this as root app realization work, not skills-pack maintenance.
- Keep the scope limited to User Admin browser/workstream smoke coverage.
- Do not require external WorkOS, Resend, or model provider credentials for the default smoke suite.
- If deterministic test seed/config is needed, keep it test-only and fail closed outside test mode.
- Prefer automated browser tests if existing project dependencies support them; otherwise create a documented, repeatable local smoke command/manual script as an intermediate step and queue follow-up automation.
- Do not count fixture-only frontend tests as proving normal runtime behavior.

## Prior evidence to preserve

- User Admin navigation tree mini-project completed and archived/verified.
- User Admin surface conformance cleanup completed and archived/verified.
- Broader confidence checks were run after cleanup:
  - `env -u ADMIN_USERS mvn test` passed;
  - `npm --prefix frontend test -- --run` passed;
  - `npm --prefix frontend run typecheck` passed;
  - `npm --prefix frontend run build` passed;
  - `git diff --check` passed.

## Constraints

- Existing shell environment may include `ADMIN_USERS=mckeeh3@gmail.com:TENANT_ADMIN:tenant-starter`; full Maven tests require unsetting it because the app's production bootstrap supports only SaaS Owner admin entries.
- Worktree may contain unrelated `.agents/**`, screenshot, and generated static-asset changes; mini-project tasks should avoid staging unrelated work.
