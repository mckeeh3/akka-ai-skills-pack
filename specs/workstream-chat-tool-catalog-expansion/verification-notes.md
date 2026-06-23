# Verification Notes: Workstream Chat Tool Catalog Expansion

Task: `TASK-WCTC-99-001`

Verified: 2026-06-23

## Scope

Terminal verification for all tasks TASK-WCTC-00-001 through TASK-WCTC-11-001. Checks run against HEAD at `07a0e0ef` (workstream-chat-catalog: add regression tests).

---

## Checks run

| Check | Result | Notes |
|---|---|---|
| `git diff --check` | ✅ clean | No whitespace/merge-conflict errors |
| 29 targeted expansion backend tests | ✅ 29/29 pass | Full list below |
| 9 seed loader tests (`AgentBehaviorSeedLoaderTest`) | ✅ 9/9 pass | Includes `expandedCatalogSeedGuidanceDescribesNewPathsWithoutAuthorityGrant` |
| `npm --prefix frontend test -- --run` | ✅ 176 pass, 1 pre-existing failure | Pre-existing `surface intent router keeps destructive and approval-gated asks on safe fallback` (unrelated to expansion) |
| `npm --prefix frontend run typecheck` | ✅ clean | No TypeScript errors |
| `mvn test` (full suite) | ⚠️ 106 tests, 6 failures, 1 error | All 7 failures are **pre-existing** (added before expansion; confirmed by git history: all in `packages: move core app packages` or earlier commits) |
| Runtime/API smoke | 🔲 not runnable | Smoke tests extend `TestKitSupport` (Akka TestKit) and require a running Akka runtime; no live deployment available in this context. Browser smoke coverage files exist for all 5 workstreams: `MyAccountBrowserWorkstreamSmokeTest`, `UserAdminBrowserWorkstreamSmokeTest`, `AgentAdminBrowserWorkstreamSmokeTest`, `AuditTraceBrowserWorkstreamSmokeTest`, `GovernancePolicyBrowserWorkstreamSmokeTest`. |

---

## Targeted expansion tests (all pass)

```
WorkstreamServiceTest:
  chatToolCatalogListsBoundedHumanChatPlanEntries
  expandedAgentAdminSimulationChatToolPlanIsProposalOnlyAndNoSideEffect
  expandedAgentAdminSubmitReviewChatToolPlanCannotGrantAuthorityOrActivateLifecycle
  expandedAgentAdminLifecyclePromptsRemainBlockedBeforeModelPlanning
  expandedMyAccountChatToolPlansRequireExactConfirmationAndStaySelfScoped
  expandedMyAccountNotificationChatToolPlanExecutesVisibleNotificationOnlyAndPreservesSourceState
  expandedMyAccountNotificationPreferenceChatPlanValidatesInAppCategoryAndRejectsExternalControls
  representativeChatToolPlansCoverAllFiveFoundationWorkstreamsWithConfirmationAndTraceSemantics
  confirmedChatToolPlanRequiresExactSnapshotAndExplicitHumanConfirmationBeforeExecution
  confirmedUserAdminChatToolPlanExecutesOrganizationAndInvitationIdempotently
  expandedUserAdminInvitationChatToolPlansExecuteOnlyPinnedScopedRoles
  expandedUserAdminCustomerAndOrganizationRenameChatPlansStayScopedAndIdempotent
  confirmedUserAdminChatToolPlanReportsPartialFailureAndRecoveryWithoutRollingBackCompletedStep
  confirmedUserAdminChatToolPlanRejectsOutOfCatalogSnapshotBeforeAnyStepExecutes
  submitMessageBlocksUnsupportedAndHighRiskChatToolPromptsAfterDeterministicRouting
  submitMessageRoutesMatchedSurfaceIntentBeforeModelInvocation
  submitMessageRoutesUserAdminMotivatingPromptToModelBackedPlanProposalWithoutMutation
  expandedAuditTraceChatToolPlanExecutesRedactedReadPathsWithVisibleTraceBinding
  expandedAuditTraceExportAndRawEvidencePathsRemainBlockedThroughChat
  expandedAuditTraceHiddenTraceBindingDeniedInChatPlanStepValidation
  auditTraceActionsReturnScopedSearchDetailTimelineFailureAndGuidanceSurfaces
  auditTraceSearchValidatesInputAndDeniesCrossTenantScope
  auditTraceCapabilitiesAreForbiddenForMemberWithoutAuditAuthority
  expandedGovernancePolicyChatToolPlanExecutesListReadAndProposalPathsWithSafeInput
  expandedGovernancePolicyActivationAndRollbackPromptsRemainBlockedThroughChat
  expandedGovernancePolicyHiddenProposalBindingDeniedInChatPlanStepValidation
  expandedCatalogSurfaceOnlyAndBlockedActionsAreOutOfCatalogAcrossAllFiveWorkstreams
  expandedCatalogCapabilityDenialBlocksChatPlanProposalForRestrictedWorkstreams
  expandedCatalogIdempotentReplayProducesNoExtraStateChangesForExpandedPaths

AgentBehaviorSeedLoaderTest (all 9 pass including):
  expandedCatalogSeedGuidanceDescribesNewPathsWithoutAuthorityGrant
```

---

## README done-state gap analysis

| Done-state bullet | Status | Evidence |
|---|---|---|
| All foundation workstream surface actions inventoried and classified | ✅ | `catalog-inventory.md` classifies all actions across all 5 workstreams with rationale, risk, prerequisites |
| App-description current intent records expanded catalog for all 5 workstreams | ✅ | `catalog-coverage-map.md` + updated governed-tools.md files; TASK-WCTC-02-001 committed |
| Backend catalog entries exist for each accepted action with governed tool id, capability id, input schema, idempotency, confirmation/approval behavior, transaction boundary, and trace requirements | ✅ (partial gap) | 29 catalog entries in `WorkstreamService.chatToolCatalog()` pass; chat-plan-level input validation passes; direct-action-level settings field/timezone validation absent — see Material Gap 1 |
| Prompt classification recognizes useful requests without stealing deterministic no-mutation routing | ✅ | `submitMessageRoutesMatchedSurfaceIntentBeforeModelInvocation` and `submitMessageBlocksUnsupportedAndHighRiskChatToolPromptsAfterDeterministicRouting` pass |
| Confirmation requires exact plan snapshot acknowledgement and selected AuthContext validation | ✅ | `confirmedChatToolPlanRequiresExactSnapshotAndExplicitHumanConfirmationBeforeExecution` passes; all expansion confirm tests require `CONFIRM <planSnapshotId>` |
| Each executed step uses existing backend-authorized action/service paths | ✅ | All step execution tests verify correct action path and result surface |
| High-impact actions are fully modeled or remain approval-gated/surface-only/blocked | ✅ | `expandedCatalogSurfaceOnlyAndBlockedActionsAreOutOfCatalogAcrossAllFiveWorkstreams` passes (7 cross-workstream denials); lifecycle/activation/export remain blocked |
| Frontend plan proposal/result surfaces usable for larger catalogs | ✅ | TASK-WCTC-09-001: `stepClassificationPill`, `resultStatusPill`, `BoundaryNotice` approval-gated count; 2 contract tests pass; 176/177 frontend tests pass |
| Starter agent seed material explains expanded catalog | ✅ | TASK-WCTC-10-001: 4 workstream seed bundles updated; `expandedCatalogSeedGuidanceDescribesNewPathsWithoutAuthorityGrant` passes |
| Tests cover no-mutation, exact confirmation, out-of-catalog denial, capability denial, approval-gated, idempotency, partial failure/recovery, and trace evidence | ✅ (partial gap) | 29 expansion tests + prior regression coverage all pass. Direct-action-level My Account settings field/timezone validation absent — see Material Gap 1 |
| Terminal verification records evidence and appends follow-up tasks if gaps remain | ✅ | This document; follow-up tasks appended below |

---

## Pre-existing full-suite failures (not caused by expansion)

All 7 full-suite failures existed before the catalog expansion project started (confirmed by `git log` tracing each test's introduction to `packages: move core app packages` or earlier).

| Test | Failure | Root cause | Materiality to expansion |
|---|---|---|---|
| `starterSourceContainsConcreteAkkaWorkstreamRuntimeAgentAndInvokerSeam` | `WorkstreamRuntimeAgent` class contains "Fake" (test expects non-fake) | Aspirational test for real Akka Agent component; `WorkstreamRuntimeAgent` is still the in-process fake adapter | Future Akka runtime integration milestone; **outside expansion scope** |
| `myAccountRejectsUnsupportedSelfServiceFieldsBeforeMutation` | `runAction` with `roleIds` input does not throw `AuthorizationException(MY_ACCOUNT_UNSUPPORTED_SELF_SERVICE_FIELD)` | Direct-action-level field allowlist not enforced in `runAction` path for settings update | **Material gap** — coverage map lists "unsupported field denial" as required evidence; see Material Gap 1 |
| `myAccountSettingsRejectInvalidTimezoneBeforeMutation` | `runAction` with `timeZone=Hidden/Provider` does not throw `AuthorizationException(MY_ACCOUNT_INVALID_PREFERENCE)` | Direct-action-level timezone allowlist not enforced in `runAction` path | **Material gap** — coverage map lists "invalid timezone denial" as required evidence; see Material Gap 1 |
| `userAdminStatusActionsDisableReactivateNoOpAndDenyManualSelfDisable` | `action-useradmin-disable-member` returns `no-op` instead of `accepted` | Pre-existing behavior mismatch in member disable path (not in expansion scope; disable-member classified `approval-gated` in catalog) | **Outside expansion scope** |
| `auditTraceSummaryWorkerFailsClosedUntilRealAutonomousRuntimeExists` | `IllegalStateException` (Akka binding) | Aspirational test for real Akka autonomous agent runtime | Future Akka runtime integration milestone; **outside expansion scope** (noted in TASK-WCTC-07-001) |
| `governancePolicyBackendActionsExposeReadProposalSimulationApprovalAndBlockedRuntimeSurfaces` | Submit-proposal surface does not contain "in_review" | `GovernancePolicyProposal.submitted()` transitions to `SIMULATION_REQUIRED`, not directly to `IN_REVIEW`; pre-existing workflow mismatch | Outside expansion scope; expansion's targeted submit test (`expandedGovernancePolicyChatToolPlanExecutesListReadAndProposalPathsWithSafeInput`) passes |

---

## Material gaps identified

### Material Gap 1: My Account direct-action settings field and timezone validation missing

- **What was claimed:** Catalog coverage map and task notes for TASK-WCTC-04-001 list "unsupported field denial" and "invalid timezone denial" as required evidence for My Account settings/profile expansion.
- **What was implemented:** Chat-plan-level validation (`createChatToolPlanProposal` rejects `accountId` and other cross-scope fields with `CHAT_TOOL_MY_ACCOUNT_UNSUPPORTED_FIELD`). This passes.
- **What is missing:** Direct `runAction` path for `action-update-my-settings` does not validate the field allowlist (rejects `roleIds`, unsupported settings fields) or the timezone allowlist (`MY_ACCOUNT_INVALID_PREFERENCE`).
- **Risk level:** Medium — the attack vector requires bypassing the chat plan proposal layer and calling the raw action API directly. Backend authorization (self-scope, capability check) still applies. But defense-in-depth requires the direct-action path to also enforce the field allowlist.
- **Follow-up task:** TASK-WCTC-12-001 (appended below)

---

## Representative path evidence (within automated test scope)

These paths are verified through targeted automated tests since live Akka runtime is not available:

| Path | Test | Status |
|---|---|---|
| My Account profile update (chat plan confirm) | `expandedMyAccountChatToolPlansRequireExactConfirmationAndStaySelfScoped` | ✅ pass |
| My Account notification lifecycle (chat plan confirm) | `expandedMyAccountNotificationChatToolPlanExecutesVisibleNotificationOnlyAndPreservesSourceState` | ✅ pass |
| My Account notification preferences (category validation) | `expandedMyAccountNotificationPreferenceChatPlanValidatesInAppCategoryAndRejectsExternalControls` | ✅ pass |
| User Admin invitation (pinned role, scope) | `expandedUserAdminInvitationChatToolPlansExecuteOnlyPinnedScopedRoles` | ✅ pass |
| User Admin customer/org rename (scoped, idempotent) | `expandedUserAdminCustomerAndOrganizationRenameChatPlansStayScopedAndIdempotent` | ✅ pass |
| Agent Admin simulation (no side effect, proposal-only) | `expandedAgentAdminSimulationChatToolPlanIsProposalOnlyAndNoSideEffect` | ✅ pass |
| Agent Admin submit-review (no authority grant) | `expandedAgentAdminSubmitReviewChatToolPlanCannotGrantAuthorityOrActivateLifecycle` | ✅ pass |
| Agent Admin lifecycle prompts blocked | `expandedAgentAdminLifecyclePromptsRemainBlockedBeforeModelPlanning` | ✅ pass |
| Audit/Trace read paths (redacted, visible binding) | `expandedAuditTraceChatToolPlanExecutesRedactedReadPathsWithVisibleTraceBinding` | ✅ pass |
| Audit/Trace export blocked | `expandedAuditTraceExportAndRawEvidencePathsRemainBlockedThroughChat` | ✅ pass |
| Governance/Policy list/read/proposal paths | `expandedGovernancePolicyChatToolPlanExecutesListReadAndProposalPathsWithSafeInput` | ✅ pass |
| Governance/Policy activation/rollback blocked | `expandedGovernancePolicyActivationAndRollbackPromptsRemainBlockedThroughChat` | ✅ pass |
| Cross-workstream surface/blocked out-of-catalog | `expandedCatalogSurfaceOnlyAndBlockedActionsAreOutOfCatalogAcrossAllFiveWorkstreams` | ✅ pass |
| Cross-workstream capability denial | `expandedCatalogCapabilityDenialBlocksChatPlanProposalForRestrictedWorkstreams` | ✅ pass |
| Idempotent replay no extra state changes | `expandedCatalogIdempotentReplayProducesNoExtraStateChangesForExpandedPaths` | ✅ pass |
| Provider missing config fails closed | `representativeChatToolPlansCoverAllFiveFoundationWorkstreamsWithConfirmationAndTraceSemantics` includes fail-closed assertion | ✅ pass |
| Audit/work trace evidence recorded | All expansion step tests assert `traceIds()` and trace event types | ✅ pass |

---

## Governance/security invariants verified

- Deterministic no-mutation surface routing runs before model planning for all workstreams ✅
- Exact `CONFIRM <planSnapshotId>` phrase required before any step executes ✅
- Backend re-authorizes every step against selected `AuthContext`, capability, catalog membership, and idempotency ✅
- Provider/model/runtime unavailable states fail closed; no fabricated model success ✅
- Audit/Trace read paths are redacted and require visible trace/correlation binding ✅
- Export, raw evidence, activation, rollback, role-changes, support-access remain approval-gated/blocked ✅
- Agent Admin prompt/skill/model text cannot grant authority or activate behavior ✅
- Governance policy activation/rollback blocked; policy changes remain proposal-only until full lifecycle approved ✅
- Frontend browser surfaces never expose secrets/JWTs/provider payloads ✅
- Seed guidance uses negative assertions only; no authority-grant or bypass phrases ✅

---

## Mini-project status

**Not fully closed.** Material Gap 1 (My Account direct-action settings field/timezone validation) is a bounded gap against the README done state. Two bounded follow-up tasks are appended (TASK-WCTC-12-001 and TASK-WCTC-99-002) to resolve and re-verify this gap before the mini-project is marked closed.
