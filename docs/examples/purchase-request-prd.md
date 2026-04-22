# Purchase request approval PRD

## Summary
Build an internal service for employees to submit purchase requests for software or equipment and track them through approval and fulfillment.

## Business goals
- keep a durable record of each request and its approval history
- support manager and finance approvals for larger purchases
- prevent requests from getting stuck without follow-up
- give operations a searchable queue view
- expose a simple HTTP API for the internal portal

## Actors
- employee submitting a request
- manager approving or rejecting a request
- finance approver for larger requests
- procurement operations staff monitoring approved work
- internal portal calling the service over HTTP

## Functional requirements

### 1. Submit purchase request
An employee can submit a request with:
- employee id
- department
- items
- total amount
- business justification

The service returns a request id and current status.

### 2. Keep full lifecycle history
The business wants an audit trail of important lifecycle changes, including:
- request submitted
- manager approval requested
- manager approved or rejected
- finance approval requested when required
- finance approved or rejected
- request expired due to inactivity
- request released for procurement fulfillment

### 3. Approval rules
- requests up to 5,000 USD need manager approval only
- requests above 5,000 USD also need finance approval
- a rejection at any approval stage ends the request
- once all required approvals are complete, the request should move to fulfillment preparation

### 4. Reminder and expiry behavior
- if the current approver has not acted within 24 hours, send a reminder
- if no approval decision is made within 72 hours of the current approval step starting, expire the request
- reminder or expiry processing must be safe across restarts and retries

### 5. Procurement handoff
When a request reaches approved-for-fulfillment status, publish a message for downstream procurement processing.
This handoff should be asynchronous so the approval flow is not blocked by downstream availability.

### 6. Query needs
Operations needs to query:
- requests by status
- requests by department
- requests waiting on a given approver
- requests older than a chosen age threshold

Single request status by id is also required.

### 7. API surface
Expose HTTP endpoints for:
- submit request
- approve request
- reject request
- get request by id
- list/search requests for the operations queue

## Non-functional constraints
- the service must survive restarts without losing approval progress
- retries must not create duplicate downstream procurement handoffs
- latest status is important, but lifecycle history must also be preserved for audit
- no LLM or agent behavior is needed for this feature
- internal authenticated usage is sufficient; public internet exposure is not required
