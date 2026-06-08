# Conversation Capture: Agent Workstream Architecture

This file captures the product/architecture discussion that triggered the migration. It is source material for migration tasks, not installed-pack doctrine by itself.

## Problem with current getting-started behavior

Testing with the getting-started prompt produced an initial app, but it failed the main objective: AI-first user administration and agent administration were not implemented. The prompt started scaffolding but did not bind generation to a hard core-app product contract.

Conclusion: a core app should be driven by a predefined core app PRD and readiness gates, not by a soft getting-started prompt alone.

## Vertical/horizontal model

The app should be described from user and agent perspectives:

- user perspective: the interactive work areas and surfaces humans use;
- agent perspective: system prompt, skills, tools, authority, data access, and renderable outputs;
- verticals: functional agents and their workstream surfaces;
- horizontals: internal app/Akka components that support those verticals.

The skills pack should accept vertical product input and derive the horizontal Akka implementation.

## Chat-shaped workstream UI

Use the familiar AI chat layout as the base authenticated app shell:

- left rail: role-authorized functional/context-area agents instead of chat sessions;
- main panel: continuous vertical stream of requests, responses, results, dashboards, forms, cards, charts, links, and audit traces;
- bottom: persistent input composer.

Responses are not just text. They are typed structured surfaces: dashboards, forms, graphs, tables, decision cards, diffs, audit timelines, details, approvals, workflow status, and links.

Selecting a functional agent should usually render an initial dashboard/attention surface. User clicks/actions append follow-up surfaces in the same workstream rather than navigating through a conventional page tree.

## Functional agents

Functional agents are user-facing role-authorized work areas. Examples:

Foundation/platform agents:

- Access/Profile Agent
- User Admin Agent
- Agent Admin Agent
- Audit/Trace Agent
- Governance/Policy Agent
- Support Access/Tenant Admin Agent
- Billing/Subscription Agent where relevant

CRM agents:

- Sales Pipeline Agent
- Account Management Agent
- Support Agent
- Marketing Campaign Agent
- Revenue Operations Agent

ERP agents:

- Finance Agent
- Procurement Agent
- Inventory/Supply Chain Agent
- Order Management Agent
- HR/Workforce Agent
- Operations Control Agent

Cross-functional agents:

- Executive Briefing Agent
- Risk & Exceptions Agent
- Approval Queue Agent
- Outcome Metrics Agent
- Process Improvement Agent

## User Admin and Agent Admin meaning

User Admin is a foundation functional agent for human access and tenancy: invitations, users, memberships, roles/capabilities, disabled access, access review, and admin audit.

Agent Admin is a foundation functional agent for AI behavior and authority: agent definitions, prompts, skills, tool boundaries, lifecycle, proposals, approvals, and traces.

## Surfaces

Surfaces are associated with agents but not necessarily owned exclusively by one agent. A customer detail, audit timeline, or approval card may appear in multiple workstreams with different actions based on role and context.

## Capabilities remain the backend contract

Tools and UI actions should map to governed backend capabilities. Capabilities preserve auth context, tenant/customer scope, validation, idempotency, side effects, approval, audit/trace, exposure surfaces, and tests.

## Incremental development model

Apps grow by adding role-based functional areas:

```text
add functional agent
+ default dashboard surface
+ one or two useful actions
+ backing capabilities
+ Akka horizontals
+ tests
```

Functional agents gain skills, tools, and surfaces over time. Internal agents can be introduced separately for bounded backend work.

## Migration implication

This architecture is not one option among several. It should become the opinionated generated-app model for the skills pack. Legacy page-first, CRUD-first, static admin-console, and chatbot-bolt-on guidance should be removed or rewritten.
