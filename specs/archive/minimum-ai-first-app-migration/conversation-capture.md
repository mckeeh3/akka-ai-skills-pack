# Conversation Capture: Minimum AI-First App Migration

## User premise

The user reconsidered the minimum AI-first app and identified **agent-backed workstreams** as the key abstraction. A generated AI-first SaaS app should be understood as a collection of workstreams. The basic workstream is a flow of user requests and rendered surface responses. The simplest surface is a model response, usually markdown rendered as HTML. Richer SaaS behavior uses typed surfaces such as app screens, screen components, forms, searches, dashboards, user lists, and other structured artifacts.

A workstream is defined like an agent with:

- system prompt;
- skills;
- tools;
- allowed surfaces;
- role-based access.

The shell resembles a web chatbot:

- left rail lists workstreams available to the current user based on roles;
- profile/settings appear at the bottom of the left rail;
- main panel is a vertically scrolling request/response flow;
- composer sits at the bottom.

The user proposed an initial app as a simple chatbot with no normal user accounts or sign-in, but with auth implemented and access limited to bootstrap users. Then the app would add User Admin, Agent Admin, Audit, and future app functionality as additional workstreams.

## Review outcome

The assistant agreed with the workstream abstraction but recommended not making “chatbot” the root abstraction. The root should be:

```text
workstream + surfaces + capabilities
```

Chat is only the simplest UI shell.

The assistant recommended preserving backend capability boundaries and audit/work traces from the first slice.

## User adjustment

The user accepted the review and made one adjustment:

> The initial “chatbot” is the basic User Admin workstream with only the model response, usually markdown, rendered as an HTML surface.

## Accepted target

The minimum app is:

```text
bootstrap-authenticated user
+ role-authorized User Admin workstream
+ UserAdminAgent
+ request/response timeline
+ markdown_response surface rendered as sanitized HTML
+ minimal AuthContext and role/capability model
+ durable workstream log
+ audit/work trace substrate
+ governed capability boundary for backend actions/tools
```

## Required skills pack adjustments identified

1. Add canonical “minimum AI-first app” doctrine.
2. Update `docs/agent-workstream-application-architecture.md` with a minimum initial workstream section.
3. Update `docs/structured-surface-contracts.md` to define `markdown_response`.
4. Update `docs/core-ai-first-saas-foundation.md` to add a Slice 0: bootstrap-only User Admin workstream v0.
5. Update `skills/core-saas-foundation/SKILL.md` to distinguish minimum starter foundation from full-core SaaS foundation.
6. Update `skills/agent-workstream-apps/SKILL.md` so “minimum app,” “starter,” or “chatbot” routes to User Admin workstream v0.
7. Update `skills/README.md` with the minimum app rule.
8. Update app-description bootstrap/functional-agent/surface guidance and starter core app-description examples.
9. Update starter template/scaffold expectations to generate User Admin v0 first.
10. Add readiness language distinguishing minimum starter, full core SaaS, and app-specific readiness.

## Concern to preserve

This migration must not weaken the mandatory secure SaaS foundation. It should introduce a valid first slice for iterative generation, while making clear that full-core readiness still requires complete user admin, agent admin, audit/trace, invitations, governed agent docs, and security tests.
