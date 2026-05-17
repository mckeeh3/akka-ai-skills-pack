# Capability-first example and test coverage review

Task: `TASK-06-003`

## Scope

Reviewed whether the reference examples and tests created or revised during the capability-first backend migration cover the key capability exposure surfaces enough for future agents to follow the pattern.

Primary sources:

- `docs/agent-coverage-matrix.md`
- `docs/capability-first-backend-architecture.md`
- `skills/capability-first-backend/SKILL.md`
- capability-first examples under `src/main/java/com/example/application/`
- capability-first tests under `src/test/java/com/example/application/`
- supply autopilot slice examples under `src/main/java/com/example/application/supplies/`

## Coverage summary

| Capability surface / pattern | Canonical example | Test coverage | Status |
|---|---|---|---|
| Read-only entity/component-tool capability | `ShoppingCartEntity.inspectCartSummary`, `CartInspectorAgent` | `CartInspectorAgentTest` | Covered |
| Same read-only capability reused by browser/API | `ShoppingCartEndpoint` `GET /carts/{cartId}/summary` | `ShoppingCartIntegrationTest.browserApiReusesReadOnlyInspectSummaryCapability` | Covered |
| Remote MCP tool/resource boundary for selected capability | `ShoppingCartToolsMcpEndpoint`, `ShoppingCartMcpEndpoint`, `RemoteShoppingCartAgent` | `RemoteShoppingCartAgentTest`, `ShoppingCartMcpEndpointTest` | Covered |
| Consequential proposal/approval capability | `RefundProposalTools`, `RefundApprovalAgent`, `RefundApprovalWorkflow` | `RefundApprovalCapabilityTest` | Covered |
| Workflow-backed supervised capability | `SupervisedExportWorkflow` | `SupervisedExportWorkflowIntegrationTest` | Covered |
| View-backed read/evidence capability | `SupervisedExportEvidenceView` | `SupervisedExportEvidenceViewIntegrationTest` | Covered |
| Timer-backed capability execution | `SupplyDecisionTimedAction`, `SupplyAutopilotWorkflow` | `SupplyDecisionTimedActionTest`, `SupplyAutopilotWorkflowIntegrationTest`, `SupplySliceAcceptanceIntegrationTest` | Covered by AI-first supply slice, not a minimal standalone capability-first example |
| Event-reactive/consumer capability execution | existing consumer examples and supply slice event flow | consumer integration tests exist, but no minimal capability-first consumer reference was added in this migration | Explicit residual gap |
| Full secure SaaS authorization/tenant tests for capability examples | secure foundation examples and security tests | foundation/security tests exist separately | Intentionally separate; small capability examples remain scoped reference examples |

## Findings

The migration now has concrete, test-backed examples for the most important capability-first exposure choices:

- selected read-only component tool exposure with curated output instead of raw state;
- browser/API reuse of the same capability semantics;
- remote MCP exposure with selected tools rather than broad component authority;
- consequential side effects modeled as proposal/approval workflow capabilities;
- supervised workflow execution with idempotency, trace, approval/denial, and validation tests;
- scoped evidence Views that redact raw workflow state and cross-customer data.

Non-interactive execution is represented in the repository, especially through the supply autopilot slice and existing consumer/timer tests. However, the migration did not add a small standalone capability-first consumer example analogous to the cart/refund/export examples. That gap is now explicit rather than hidden.

## Residual backlog

1. Add a minimal event-reactive capability example when the next examples sprint needs more coverage. Suggested shape: a consumer-backed `capability.trace.enrich` or `notification.dispatch` capability that records provenance/correlation, tenant/customer scope, idempotency/dedupe behavior, denial/no-op semantics, and audit/work trace output.
2. Optional cleanup: add a tiny standalone timer-backed capability example if future agents find the supply autopilot slice too broad for learning timer authority/idempotency patterns.
3. Keep full secure SaaS authorization tests in the foundation/security suites rather than duplicating the whole foundation in every small capability example; individual examples should continue to state when they are scoped reference material.

## Matrix update

Updated `docs/agent-coverage-matrix.md` with a capability-first exposure coverage addendum so future agents can quickly locate the canonical examples and see the residual consumer/timer notes.

## Checks performed

- Reviewed capability-first examples and tests with targeted search for capability ids, approval, idempotency, tenant/customer scope, audit/trace, curated output, MCP, workflow, timer, and consumer language.
- Ran targeted Maven tests covering the canonical capability-first examples.
- Ran `git diff --check`.
