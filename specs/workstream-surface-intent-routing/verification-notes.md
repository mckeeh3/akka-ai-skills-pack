# Workstream Surface Intent Routing Verification

Date: 2026-06-22
Task: `TASK-SIR-99-001`

## Scope

Terminal verification for the Workstream Surface Intent Routing mini-project done state:

- User Admin prompt `create organization "Org 1"` opens the Organization Create surface with safe prefill and no pre-submit mutation.
- Deterministic routing is attempted before model-backed chat and all five core workstreams have catalog-backed high-confidence routes.
- Frontend composer/API handling accepts backend-routed surfaces and renders prefilled forms without client-side mutation.
- Governed agent seed material describes structured surfaces without granting direct command authority.

## Evidence matrix

| Claim | Runtime path reviewed | Evidence | Result | Gap |
|---|---|---|---|---|
| `create organization "Org 1"` opens Organization Create with `organizationName = Org 1` and no pre-submit mutation | Browser composer source `handleComposerSubmit` -> `HttpWorkstreamApiClient.submitWorkstreamMessage` -> `POST /api/workstream/messages` -> `WorkstreamEndpoint.message` -> `WorkstreamService.submitMessage` -> `DefaultSurfaceIntentRouter` -> `dynamicSurface` / `applyRoutedPrefill` -> `OrganizationAdminSurface` editable submit form | Backend targeted test `submitMessageRoutesUserAdminSurfaceIntentsWithSafePrefillAndNoMutation` passed; frontend contract tests for composer routed response and Organization Admin routed create prefill passed | Achieved at checked backend/API/UI-source path | No live browser smoke was run in this terminal verification |
| Matched routes do not call the model-backed runtime | `WorkstreamService.submitMessage` routes before `workstreamAgentRuntimeInvoker.invokeWorkstreamAgent` | Backend targeted tests assert zero runtime invocations for matched User Admin and representative all-workstream routes | Achieved | None |
| Router does not mutate before submit | Router returns `noMutation=true`, `sideEffect=none`; routed response opens surface only; form submit remains protected action | Backend targeted tests assert tenant/invitation repositories unchanged before submit; frontend tests assert no auto-submit/client-side create path | Achieved | None |
| All five core workstreams have deterministic routes backed by catalog metadata | `app-description/domains/core-starter/workstreams/surface-catalog.md` maps route ids to My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy; `DefaultSurfaceIntentRouter` implements representative routes | Backend targeted test `submitMessageRoutesRepresentativeCoreWorkstreamSurfaceIntentsWithoutModelOrMutation` passed; frontend surface-intent contract test passed | Achieved | None |
| Ambiguous, unauthorized, destructive, or approval-gated prompts remain safe | Router returns empty for ambiguous/high-risk prompts or missing capability, preserving governed model fallback or authorization denial | Backend targeted tests for unauthorized/ambiguous/high-risk fallback and selected-context mismatch passed; frontend contract verifies no special client-side mutation branch for routed responses | Achieved | None |
| Agent familiarity aligns with no-direct-command non-goal | Starter prompt/skill/reference seeds for all five core agents describe structured surfaces, protected backend actions, and no direct mutation/authority grants | Targeted seed-import test `importedCoreAgentSeedsDescribeSurfaceRoutingWithoutGrantingMutationAuthority` passed | Achieved | None |

## Checks run

- `mvn -Dtest='ai.first.application.coreapp.workstream.WorkstreamServiceTest#submitMessageRoutesUserAdminSurfaceIntentsWithSafePrefillAndNoMutation+submitMessageRoutesRepresentativeCoreWorkstreamSurfaceIntentsWithoutModelOrMutation+submitMessageFallsBackSafelyForUnauthorizedAmbiguousOrHighRiskSurfacePrompts+submitMessageLeavesUnmatchedPromptOnGovernedModelFallback+submitMessageRequiresSelectedContextMatch,ai.first.application.foundation.agent.AgentBehaviorSeedLoaderTest#importedCoreAgentSeedsDescribeSurfaceRoutingWithoutGrantingMutationAuthority' test` — passed; 6 tests, 0 failures/errors.
- `npm --prefix frontend test -- --run` — passed; 166 tests, 0 failures.
- `npm --prefix frontend run typecheck` — passed.
- `git diff --check` — passed.

Not run:

- `npm --prefix frontend run build` — production frontend output did not change during verification.
- Full `mvn test` — verification did not materially change shared backend workstream or agent behavior; targeted backend routing and seed tests were run instead.
- Live browser/JWT API smoke — not run in this terminal task; the API/UI path was checked through backend service tests plus endpoint/client/composer/renderer contract evidence.

## Residual risks

- The terminal verification did not start the local Akka service or drive a real authenticated browser session. The checked evidence covers the backend service behavior and source-level API/UI path, but a future manual release smoke can still exercise the same prompt in a browser against a configured local runtime.
- Catalog metadata remains app-description-owned while routing rules are code-owned in `DefaultSurfaceIntentRouter`, as intentionally documented by the catalog. Keep future route additions synchronized.

## Result

The README done state is achieved at the checked scope. No bounded follow-up tasks are required for this mini-project. `TASK-SIR-99-001` can be marked `done`, closing the queue.
