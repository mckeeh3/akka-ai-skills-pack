# Backlog 07: Starter Fullstack Gap Closure

## Outcome

The starter app should scaffold into a project that can be installed, built, tested, run locally, serve the React/Vite UI through Akka, authenticate through a documented WorkOS/AuthKit path, exercise real backend APIs by default, complete invitation onboarding, send/capture email through the Resend boundary, and persist foundation state through Akka components where feasible.

## Backlog items

1. **Acceptance baseline refresh**
   - Update stale acceptance docs to reflect that `templates/ai-first-saas-starter/frontend/**` is now embedded and scaffolded.
   - Replace the old frontend qualification with the remaining fullstack/runtime/durability gaps.

2. **Fullstack smoke validation**
   - Add a repeatable validation script or documented command sequence that scaffolds the starter, runs backend tests, installs/builds frontend, verifies static output, and runs hygiene checks.
   - Prefer a script under `tools/` that can be used by final acceptance and future release checks.

3. **Production-first frontend polish**
   - Keep fixture mode only behind explicit `?fixtureWorkstream=1` or tests.
   - Remove fixture-era copy from canonical production surfaces and README instructions.
   - Ensure normal startup guides users toward real AuthKit/API mode.

4. **Turnkey local auth/bootstrap**
   - Document and implement the intended local WorkOS/AuthKit configuration path.
   - Make first-admin/bootstrap behavior explicit, safe, and testable.
   - Provide a dev/test identity strategy that does not weaken production auth.

5. **Invitation acceptance end-to-end**
   - Add public invite acceptance API/route shape, token validation, accepted-membership linking, and browser recovery states.
   - Wire workstream User Admin surfaces to expose invite acceptance/resend/revoke lifecycle outcomes.

6. **Resend adapter and local captured outbox**
   - Replace the placeholder production send result with a real adapter boundary that can call Resend when configured.
   - Preserve local/dev/test captured outbox behavior and no-secret frontend/static guarantees.

7. **Durable identity/invitation/audit Akka slices**
   - Incrementally replace static in-memory identity/invitation/audit storage behind existing ports with Akka components.
   - Preserve current service contracts and tests while adding component-level and endpoint-level tests.

8. **Durable governed-agent Akka slices**
   - Incrementally replace in-memory governed-agent behavior storage behind existing ports with Akka components.
   - Preserve deterministic prompt assembly/readSkill/proposal semantics and traces.

9. **Concrete admin/governance/audit APIs and integration tests**
   - Add or strengthen explicit APIs for user directory/search, membership actions, support access, access review, trace search/detail, and governance proposal review.
   - Prove auth, tenant isolation, idempotency, audit, and denial behavior through HTTP/integration tests.

10. **Final fullstack acceptance rerun**
    - Rerun install/scaffold/fullstack smoke/build-pack checks.
    - Publish updated final acceptance and completion summary with any remaining qualified gaps.
