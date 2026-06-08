---
name: akka-workflow-notifications
description: Add live notification streams to Akka Java SDK Workflows using NotificationPublisher and NotificationStream, and expose them through SSE-friendly endpoint mappings. Use when clients must track workflow progress without polling.
---

# Akka Workflow Notifications

Use this skill when a workflow should push progress updates to subscribers.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the required capability, AuthContext/scope, DTO, side-effect, trace, and test inputs are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Required reading

Read these first if present:
- `../docs/capability-first-backend-architecture.md`
- `akka-context/sdk/workflows.html.md`

## Capability-first notification role

Use notifications as a selected exposure surface for workflow capability progress. Keep notifications scoped, redacted, and subscriber-safe; they may support supervision, approval, audit, or UI progress but must not expose raw internal state or bypass authorization on the API/SSE route.

## Core pattern

1. Inject `NotificationPublisher<Notification>` into the workflow.
2. Define a compact notification record or sealed interface for subscriber-facing progress updates.
3. Call `publish(...)` at meaningful milestones such as step completion, rejection, compensation, or unexpected failure.
4. Expose a public `NotificationStream<Notification>` method.
5. Map workflow notifications to API-facing SSE records in the endpoint.

## Repository example

- a domain-specific workflow
  - publishes progress notifications for withdraw success, completion, rejection, compensation, and unexpected failure
  - exposes `updates()` for subscribers
- a domain-specific approval workflow
  - publishes notifications when approval starts waiting and when approval is applied
  - exposes `updates()` for subscribers
- a domain-specific workflow endpoint
  - adapts workflow notifications into API records
  - returns them as SSE over HTTP
- a domain-specific approval workflow endpoint
  - adapts paused-workflow notifications into API records
  - returns them as SSE over HTTP

## Design note

Notifications are for user experience, supervision, and observability, not business correctness or authorization. They do not replace the authoritative workflow state from `get()`, audit/work-trace records, or backend checks on the subscriber route.

## Generated SaaS checks

For generated SaaS workflows, preserve the accepted capability contract:
- compensation, notification, approval, and escalation steps carry `AuthContext` or system-principal authority basis;
- downstream side effects are idempotent and retry safe;
- structured-surface/workstream events include surface id/version, event id, correlation id, trace ids, and stale/progress semantics;
- audit/work traces cover approval, denial, compensation, retry exhaustion, and side effects;
- tests cover authorized success, forbidden/cross-tenant, idempotency/no-op, surface/realtime updates, and audit/trace emission where exposed.


## Review checklist

Before finishing, verify:
- the workflow injects `NotificationPublisher<...>`
- notifications are small, progress-oriented, scoped, and redacted for the intended subscriber
- the workflow exposes a `NotificationStream<...>` method
- public APIs map internal notifications to API-facing records
- tests verify that subscribers receive workflow progress after the stream is opened
