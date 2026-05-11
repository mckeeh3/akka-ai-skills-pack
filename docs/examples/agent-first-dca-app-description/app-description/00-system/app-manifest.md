# App Manifest

## Working identity

Agent-First DCA and Office Device Lifecycle Platform.

## Reference-asset status

This app description is a worked reference example for the `akka-ai-skills-pack`. It is not the business application of this repository and does not define runnable Akka code.

## Product thesis

Small office-device dealers should not operate separate record-centric CRM, ERP, and DCA tools when routine work crosses all three domains. The platform is an AI-first operating system for customer-device-contract-service-billing lifecycle management, starting from DCA telemetry as the operational signal source.

The app should convert device telemetry, contract context, service history, inventory state, and customer lifecycle status into durable goals, plans, delegated agent work, policy-bound recommendations, human decisions, audit traces, and outcome feedback.

## Initial scope

The reference example covers:

- customer, device, and DCA collector lifecycle orchestration;
- onboarding from acquired customer to operational fleet;
- telemetry-driven fleet health, supplies, meter, billing, service, and offboarding work;
- supplies fulfillment as the first implementation slice;
- bounded agent work with explicit policies, approvals, exceptions, and traces;
- supervision, decision, governance, audit, and outcome surfaces for humans.

## Non-goals

- replacing every CRM, ERP, or DCA feature at once;
- modeling the product as a chatbot attached to static records;
- allowing agents to expand their own authority without human-governed policy changes;
- treating lifecycle state changes, billing-impacting records, customer-sensitive communications, data deletion, or unusual costs as invisible background automation;
- generating runnable application code from this scaffold before readiness gaps are closed.

## Primary generation targets when realized

A downstream realization would target:

- Akka backend components for durable lifecycle state, agent orchestration, workflows, views, policies, audit traces, and integrations;
- React + Vite + TypeScript frontend surfaces for command center, decision cards, governance, audit, and outcome review;
- tests derived from lifecycle gates, approval boundaries, negative cases, operational traces, and outcome metrics.

## Current readiness

`reference-ready`: this Sprint 6 example is complete enough to guide future agents through AI-first app-description, planning, UI, trace, outcome, and implementation-slice shape. It remains intentionally non-runnable and still requires downstream project-specific answers for external integrations, numeric thresholds, tenant/user roles, style guide, retention periods, and concrete evaluation fixtures before code generation.
