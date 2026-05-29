# Full-Core SMB User Admin

## Purpose

Make User Admin the first full-core SMB workstream slice for the five-core AI-first SaaS starter.

The goal is practical SMB access administration through the workstream shell: invitations, members, roles/capabilities, disable/reactivate, access review, audit visibility, request/response User Admin Agent guidance, and a justified internal worker path.

## Source

Created from the umbrella Wave 1 plan in `specs/full-core-smb-saas-hardening/`.

## Scope

- User Admin dashboard and structured surfaces.
- Invitation lifecycle and outbox/delivery status visibility.
- Member directory, role/capability preview/change, disable/reactivate guardrails.
- Access review queue and first durable internal-worker candidate planning.
- User Admin Agent request/response behavior through governed Akka Agent runtime.
- Deterministic services for authorization, tenant filtering, validation, idempotency, last-admin guardrails, projections, and trace emission.

## Non-goals

- Enterprise SCIM/SSO lifecycle consoles.
- Complex custom role-builder suites.
- Worker-owned membership/role changes bypassing deterministic governed capabilities.
- Deterministic/demo/model-less normal runtime responses for model-backed agent behavior.

## Execution model

Execute one task per fresh harness session from `pending-tasks.md`.

## Done state

This child mini-project is complete when User Admin has an implementation-ready SMB vertical plan, bounded source-change tasks are queued or completed, runtime/API/UI validation expectations are explicit, and verification confirms no missing User Admin planning gap blocks execution.
