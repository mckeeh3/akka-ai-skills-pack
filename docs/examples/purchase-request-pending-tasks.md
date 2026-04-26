# Pending Tasks

Example durable implementation queue for `purchase-request-prd.md` and `purchase-request-solution-plan.md`.

This is a reference example for the queue contract in `../pending-task-queue.md`.
In a real target project, this file would live at:

```text
specs/pending-tasks.md
```

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Update task status before finishing the harness response.
- Do not implement adjacent tasks unless they are explicitly in scope for the selected task.

## Tasks

### TASK-001: Define purchase request domain and API records

- status: pending
- source: specs/backlog/01-purchase-request-core-build-backlog.md
- task brief: none
- depends on: []
- required reads:
  - specs/akka-solution-plan.md
  - specs/cross-cutting/00-common-domain-and-conventions.md
  - specs/backlog/01-purchase-request-core-build-backlog.md
- skills:
  - akka-event-sourced-entities
  - akka-ese-domain-modeling
- expected outputs:
  - purchase request domain records
  - lifecycle status model
  - command/request/response records shared by entity and endpoint
  - validation helper methods or value-object constructors
  - unit tests for domain validation
- required checks:
  - mvn test
- done criteria:
  - domain and API records compile
  - validation tests cover valid request, missing required fields, empty items, and invalid amount
  - no Akka component implementation is started
- notes:
  - keep this task limited to shared model and validation foundations

### TASK-002: Implement PurchaseRequestEntity and entity tests

- status: pending
- source: specs/backlog/01-purchase-request-core-build-backlog.md
- task brief: none
- depends on: [TASK-001]
- required reads:
  - specs/akka-solution-plan.md
  - specs/cross-cutting/00-common-domain-and-conventions.md
  - specs/backlog/01-purchase-request-core-build-backlog.md
- skills:
  - akka-event-sourced-entities
  - akka-ese-application-entity
  - akka-ese-unit-testing
- expected outputs:
  - PurchaseRequestEntity
  - entity command and event records
  - entity unit tests for submit, approve, reject, invalid transitions, and replayed state
- required checks:
  - mvn test
- done criteria:
  - entity command handlers persist auditable lifecycle events
  - replayed state reconstructs current request status and approval history
  - invalid commands fail or no-op according to the slice spec
- notes:
  - do not implement workflow orchestration in this task

### TASK-003: Implement approval workflow and workflow tests

- status: pending
- source: specs/backlog/02-approval-orchestration-build-backlog.md
- task brief: none
- depends on: [TASK-002]
- required reads:
  - specs/akka-solution-plan.md
  - specs/backlog/02-approval-orchestration-build-backlog.md
- skills:
  - akka-workflows
  - akka-workflow-component
  - akka-workflow-pausing
  - akka-workflow-testing
- expected outputs:
  - PurchaseRequestApprovalWorkflow
  - workflow state and command records
  - tests for manager-only path, manager-plus-finance path, rejection path, and retry behavior
- required checks:
  - mvn test
- done criteria:
  - workflow survives waits for manager and finance decisions
  - workflow branches correctly at the approval threshold
  - workflow reaches terminal approved, rejected, or expired states as specified
- notes:
  - keep reminder and expiry scheduling details for the timed-action task

### TASK-004: Implement reminder and expiry timed action

- status: pending
- source: specs/backlog/03-reminders-and-expiry-build-backlog.md
- task brief: none
- depends on: [TASK-003]
- required reads:
  - specs/akka-solution-plan.md
  - specs/backlog/03-reminders-and-expiry-build-backlog.md
- skills:
  - akka-timed-actions
  - akka-timed-action-component
  - akka-timers-scheduling
  - akka-timed-action-testing
- expected outputs:
  - PurchaseRequestApprovalTimedAction
  - timer scheduling integration from workflow or entity as planned
  - tests for reminder scheduling, expiry execution, and stale timer idempotency
- required checks:
  - mvn test
- done criteria:
  - 24-hour reminder and 72-hour expiry callbacks are represented
  - stale timer callbacks are safe no-ops
  - tests prove retry/idempotency behavior
- notes:
  - reminder delivery channel remains abstract unless specified by the project

### TASK-005: Implement operational queue view

- status: pending
- source: specs/backlog/04-operational-queries-build-backlog.md
- task brief: none
- depends on: [TASK-002]
- required reads:
  - specs/akka-solution-plan.md
  - specs/backlog/04-operational-queries-build-backlog.md
- skills:
  - akka-views
  - akka-view-from-event-sourced-entity
  - akka-view-query-patterns
  - akka-view-testing
- expected outputs:
  - PurchaseRequestsByStatusView
  - query models for status, department, approver, and age filters
  - view projection/query tests
- required checks:
  - mvn test
- done criteria:
  - view supports required operations queue filters
  - tests verify projection from purchase request lifecycle events
- notes:
  - do not add HTTP endpoints in this task

### TASK-006: Implement approved-request procurement consumer

- status: pending
- source: specs/backlog/05-procurement-handoff-build-backlog.md
- task brief: none
- depends on: [TASK-003]
- required reads:
  - specs/akka-solution-plan.md
  - specs/backlog/05-procurement-handoff-build-backlog.md
- skills:
  - akka-consumers
  - akka-consumer-from-workflow
  - akka-consumer-producing
  - akka-consumer-testing
- expected outputs:
  - ApprovedPurchaseRequestConsumer
  - procurement handoff message records
  - consumer tests for exactly-one logical handoff and metadata correctness
- required checks:
  - mvn test
- done criteria:
  - consumer reacts only after final approval
  - handoff message includes request id, requester, approved items, amount, and trace metadata
  - duplicate or retried events do not create duplicate logical handoffs
- notes:
  - use the topic/publishing pattern selected by the project plan

### TASK-007: Implement internal HTTP endpoint and endpoint tests

- status: pending
- source: specs/backlog/06-http-api-build-backlog.md
- task brief: none
- depends on: [TASK-002, TASK-003, TASK-005]
- required reads:
  - specs/akka-solution-plan.md
  - specs/backlog/06-http-api-build-backlog.md
- skills:
  - akka-http-endpoints
  - akka-http-endpoint-component-client
  - akka-http-endpoint-acl-internal
  - akka-http-endpoint-testing
- expected outputs:
  - PurchaseRequestEndpoint
  - submit, approve, reject, get-by-id, and list/search routes
  - endpoint integration tests
- required checks:
  - mvn test
- done criteria:
  - endpoint delegates commands to entity/workflow components as planned
  - endpoint serves operational queries through the view
  - internal ACL/auth assumptions are represented consistently with the plan
- notes:
  - do not add public internet exposure

### TASK-008: Add end-to-end approval scenario

- status: pending
- source: specs/backlog/07-end-to-end-validation-build-backlog.md
- task brief: none
- depends on: [TASK-004, TASK-006, TASK-007]
- required reads:
  - specs/akka-solution-plan.md
  - specs/backlog/07-end-to-end-validation-build-backlog.md
- skills:
  - akka-ese-integration-testing
  - akka-workflow-testing
  - akka-http-endpoint-testing
  - akka-consumer-testing
- expected outputs:
  - end-to-end happy-path integration test
  - regression test for rejection or expiry path if included in the slice backlog
- required checks:
  - mvn test
- done criteria:
  - test submits a request, approves through required stages, verifies final status, and verifies procurement publication
  - test documents any external dependency stubs or test-kit assumptions
- notes:
  - this is validation glue, not a place to add new production behavior
