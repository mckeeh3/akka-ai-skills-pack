---
name: akka-workflow-testing
description: Write Akka Java SDK Workflow tests using TestKitSupport, componentClient.forWorkflow(...), supporting components, and Awaitility. Use for workflow happy paths, compensation, and pause/resume behavior.
---

# Akka Workflow Testing

Use this skill for executable workflow tests.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. For this skill, require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

## Required reading

Read these first if present:
- `../docs/capability-first-backend-architecture.md`
- `akka-context/sdk/workflows.html.md`
- `akka-context/sdk/agents/testing.html.md`

## Capability-first test role

Workflow tests should verify the governed capability contract, not only step mechanics. Cover authorization/scope, approval gates, supervision states, idempotent retries, compensation, audit/work-trace expectations, and exposure-specific behavior for endpoints, tools, timers, or consumers that start or advance the workflow.

## Test harness rules

Workflow tests should usually:
- extend `TestKitSupport`
- call workflow command handlers through `componentClient.forWorkflow(workflowId)`
- set up supporting entities or components through `componentClient`
- use `Awaitility` when waiting for later workflow steps to finish
- assert current workflow state through a read handler such as `get()`

## Repository patterns

### Straight-through and compensation flows
- a domain-specific workflow integration test
  - creates supporting wallet entities first
  - starts the workflow through `componentClient.forWorkflow(...)`
  - waits for completion with `Awaitility`
  - asserts both successful completion and compensation behavior

### Pause/resume flows
- a domain-specific approval workflow integration test
  - starts the workflow and verifies it is waiting
  - sends a later resume command
  - asserts the workflow ends in the approved state
  - covers invalid resume attempts

### Supporting idempotent components
- `WalletEntityTest`
  - verifies duplicate command ids become no-op replies
  - gives the workflow a retry-safe downstream target

## What to cover

Prefer these categories:
1. successful workflow completion with authorized AuthContext/scope when auth is in scope
2. validation rejection in the start command
3. forbidden, cross-tenant, missing-role/scope, or unauthorized resume/start denial when relevant
4. pause/resume or compensation path when present, including approval-denied and supervision-needed outcomes
5. downstream state after the workflow finishes
6. duplicate-start, retry, or invalid-state command rejection/no-op behavior
7. audit/work-trace creation for approvals, denials, side effects, compensation, and consequential agent/tool activity when relevant

## Anti-patterns

Avoid:
- assuming later steps complete before the start command returns
- testing workflow logic only through mocked incoming messages
- skipping setup of the downstream components the workflow actually calls
- treating UI navigation, prompt text, or tool descriptions as authorization checks
- relying on mutable test sleeps instead of `Awaitility`

## Review checklist

Before finishing, verify:
- tests extend `TestKitSupport`
- workflow calls go through `componentClient.forWorkflow(...)`
- `Awaitility` is used for asynchronous completion when needed
- assertions cover both workflow state and important downstream effects
- consequential capability tests cover approval/denial, idempotency, compensation or supervision, and audit/trace behavior where applicable
