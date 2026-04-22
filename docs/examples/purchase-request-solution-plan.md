# Akka Solution Plan

## Inputs
- source:
  - `docs/examples/purchase-request-pdr.md`
- assumptions:
  - reminder delivery itself can be delegated to downstream infrastructure after the service emits the reminder trigger
  - procurement handoff can be modeled as publishing to a topic rather than making a synchronous external API call
  - authentication details are handled at the HTTP edge and do not change the component set

## Capability summary
- durable purchase request lifecycle with auditable history
- multi-step approval process with conditional finance approval
- reminder and expiry behavior for stalled approval steps
- queue-style operational queries across many requests
- asynchronous procurement handoff after final approval
- internal HTTP API for command and query access

## Chosen components
- `EventSourcedEntity`: `PurchaseRequestEntity` — durable request state plus lifecycle history
- `Workflow`: `PurchaseRequestApprovalWorkflow` — durable orchestration for manager approval, optional finance approval, and fulfillment release
- `TimedAction`: `PurchaseRequestApprovalTimedAction` — executes reminder and expiry callbacks safely
- `View`: `PurchaseRequestsByStatusView` — queue/search projection for status, department, approver, and age filters
- `Consumer`: `ApprovedPurchaseRequestConsumer` — reacts after final approval and publishes the procurement handoff asynchronously
- `HTTP endpoint`: `PurchaseRequestEndpoint` — internal REST surface for submit, approve, reject, get, and list operations

## Why each component exists
- `PurchaseRequestEntity`: the PDR explicitly requires full lifecycle history and auditability, which makes event sourcing the best fit over latest-state-only storage
- `PurchaseRequestApprovalWorkflow`: the approval path is a durable multi-step process with branching, waits, and terminal rejection/expiry behavior
- `PurchaseRequestApprovalTimedAction`: reminder and 72-hour expiry requirements need scheduled callbacks that survive restarts and can safely no-op on stale timers
- `PurchaseRequestsByStatusView`: operations queries are list/search oriented and should not be served directly from the write model
- `ApprovedPurchaseRequestConsumer`: downstream procurement handoff should be asynchronous and isolated from core approval commands
- `PurchaseRequestEndpoint`: the PDR explicitly asks for an HTTP API for both command and queue/query use cases

## Skill routing
- `akka-solution-decomposition`
- `akka-event-sourced-entities`
- `akka-ese-domain-modeling`
- `akka-ese-application-entity`
- `akka-workflows`
- `akka-workflow-component`
- `akka-workflow-pausing`
- `akka-workflow-testing`
- `akka-timed-actions`
- `akka-timed-action-component`
- `akka-timers-scheduling`
- `akka-timed-action-testing`
- `akka-views`
- `akka-view-from-event-sourced-entity`
- `akka-view-query-patterns`
- `akka-view-testing`
- `akka-consumers`
- `akka-consumer-from-workflow`
- `akka-consumer-producing`
- `akka-consumer-testing`
- `akka-http-endpoints`
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-acl-internal`
- `akka-http-endpoint-testing`

## Open questions and assumptions
- question: should reminder delivery be email, chat, or another internal channel?
- question: does the procurement handoff need an outbox-style deduplication key beyond Akka delivery guarantees?
- question: should expired requests be restartable by submitting a new approval command, or are they terminal?
- assumption: approval comments are part of the entity event history
- assumption: the workflow id can be aligned with the purchase request id for simpler tracing
- assumption: the operations queue does not require live SSE updates in the first version

## Recommended implementation order
1. define domain records, validation rules, lifecycle statuses, and API request/response models
2. implement `PurchaseRequestEntity` with auditable lifecycle events and command validation
3. implement `PurchaseRequestApprovalWorkflow` to drive manager approval, optional finance approval, and terminal outcomes
4. implement `PurchaseRequestApprovalTimedAction` for reminder and expiry callbacks into the workflow or entity
5. implement `PurchaseRequestsByStatusView` for operational queue queries
6. implement `ApprovedPurchaseRequestConsumer` to publish approved requests to the procurement topic
7. implement `PurchaseRequestEndpoint` for internal portal access
8. add component-family tests and one end-to-end happy-path integration scenario

## Required tests
- entity unit tests — `PurchaseRequestEntity` validation, event emission, and replayed lifecycle state
- workflow integration tests — `PurchaseRequestApprovalWorkflow` manager-only path, manager-plus-finance path, rejection path, and retry behavior
- timed action tests — `PurchaseRequestApprovalTimedAction` reminder scheduling, expiry execution, and stale-timer idempotency
- view integration tests — `PurchaseRequestsByStatusView` projections for status, department, approver, and age-based queries
- consumer integration tests — `ApprovedPurchaseRequestConsumer` publishes exactly one procurement handoff with correct metadata
- HTTP endpoint integration tests — `PurchaseRequestEndpoint` submit, approve, reject, get-by-id, and list/search flows
- end-to-end scenario test — submit request, approve through required stages, verify final status and procurement publication
