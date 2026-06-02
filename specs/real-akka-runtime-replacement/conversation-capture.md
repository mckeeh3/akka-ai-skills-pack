# Conversation Capture: Real Akka Runtime Replacement

## User goals and decisions

- The user asked to review workstream-related skills/template guidance for non-Akka substitute mockups.
- The review found remaining Akka component-backed/local-demo/default guidance and starter runtime wiring.
- The user then clarified the stronger requirement: **replace these non-Akka substitute/defaults; they all must be replaced with real Akka components.**
- The only allowed use of non-Akka substitute mockups, fakes, mocks, fixtures, canned responses, or deterministic substitute runtime is testing.

## Accepted constraints

- Real local Akka execution is the production-like validation path for generated apps.
- Normal workstream implementations must aggressively build toward production-ready code.
- Gating local/demo adapters is not enough when a generated feature is claimed as implemented.
- Fail-closed is acceptable for missing external provider/security configuration, but not as a replacement for missing Akka persistence/components when the feature is in scope.
- Test source may keep test doubles if clearly test-only.

## Rejected alternatives

- Do not keep `LocalDemo*` repositories as explicit local/demo runtime adapters for generated applications.
- Do not keep query-string/runtime fixture switches for normal UI behavior.
- Do not mark a feature complete because static fixtures, local Akka component-backed state, or fail-closed placeholder repositories exist.
- Do not merely rename Akka component-backed classes without changing runtime behavior.

## Risks

- Existing tests may depend heavily on local-demo repositories and fixture clients; replacement tasks must move those dependencies to test-only fixtures or adapt tests to Akka TestKit/runtime components.
- Some current services have constructors that silently instantiate local-demo repositories. These must be removed or made test-only.
- Existing Akka component seams may need views/endpoints wired through `ComponentClient`, not just entity classes.
- Frontend fixture data is used as reference contract material; production runtime must not import or select it, but tests may continue reading fixture files if those files are test-only assets.

## Unresolved questions

No blocking product decision is required. The user chose the strict policy: replace all normal-runtime defaults with real Akka components; keep substitutes only for tests.
