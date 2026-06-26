# Business Intent Input: Audit Trace Workstream

## Source Context

This artifact captures a live interview about the audit trace workstream for the secure AI-first SMB SaaS core app.

The stakeholder described the desired business intent for tenant-admin audit trace visibility, focusing on a searchable activity log that answers who did what across human and agent workers.

## Input Status

accepted-for-stage-1-input

The stakeholder confirmed the captured intent during the interview and asked for it to be turned into a clean app-description input artifact.

## Explicit Input

- Primary users are tenant admins.
- The core question the workstream should answer is: "Who did what?"
- The trace scope is worker request/response for human and agent workers.
- The workstream must include user-facing request/response activity and tool calls.
- Human worker identity should include email, role, and org.
- Agent worker identity should include:
  - agent name;
  - agent role/workstream;
  - model used;
  - prompt/skill/version used;
  - session/conversation id;
  - requested-by human/user where applicable.
- Tool call detail should include:
  - tool name;
  - tool purpose;
  - input payload;
  - output payload;
  - authorization result;
  - duration;
  - status/error;
  - linked parent request/response.
- Traces may be associated with a customer/account or may be system-level/no-customer actions.
- Tenant admins can view full payloads.
- The v1 surface should be a searchable activity log.
- Required filters are:
  - date/time range;
  - worker type;
  - actor/user/agent;
  - action type;
  - customer/account;
  - success/failure/denied.
- Keyword search should apply only to metadata/summary fields, not full payloads.
- Denied actions should show the reason/policy that denied them.
- Tenant admins should be able to search, view, and configure retention for now.
- Export is not part of v1.
- Retention defaults to 90 days.
- Tenant admins can configure retention between 30 and 365 days.
- Audit records should be immutable and removed only by retention expiry.
- Retention configuration changes are logged like any other interaction.
- The UI should show a warning/badge for full payload access, such as: "Sensitive full payload — tenant admin access only."
- Searchable summaries should be deterministic app-generated summaries only.
- The default activity log list should show:
  - time;
  - worker type;
  - actor/agent;
  - action type;
  - customer/account;
  - status;
  - summary;
  - correlation/session id.

## Agent-Inferred Business Model

The audit trace workstream is intended to serve as a tenant-admin accountability and review surface for AI-first SaaS operations.

Confirmed by the interview:

- The business need is accountability rather than broad compliance export in v1.
- Tenant admins need to see both human and agent activity in one searchable activity log.
- Tool calls are considered consequential enough to audit alongside worker request/response activity.
- Full payload visibility is acceptable for tenant admins, but it should be clearly marked as sensitive.
- Search should avoid indexing full payload text and should rely on deterministic metadata/summary fields.
- Retention is tenant-controlled within a bounded 30–365 day range.

Unconfirmed but likely adjacent needs for later consideration:

- Compliance export or evidence bundles may become useful later.
- Suspicious activity review, acknowledgements, and investigation notes may become useful later.
- Agent-generated summaries may be useful later but are intentionally not part of v1 searchable audit indexing.

## Confirmed Intent

The audit trace workstream should provide tenant admins with a searchable, immutable activity log that answers "who did what" across human workers, agent workers, and tool calls.

The log should make it easy to filter activity by time, worker type, actor/user/agent, action type, customer/account, and status. Tenant admins should be able to open details and view full request/response/tool payloads with a clear sensitive-access warning.

Each trace should be tenant-scoped and may optionally link to a customer/account. Some traces may represent system-level actions where no customer/account applies.

Denied actions must include the denial reason and policy reference. Retention settings are tenant-admin configurable from 30 to 365 days, defaulting to 90 days, and changes to retention settings must themselves be audit-traced.

## Current Process

No existing current process was described in the interview.

The implied current gap is that tenant admins need a trustworthy way to inspect human and agent activity without relying on raw logs, hidden system internals, or unsearchable conversation history.

## Pain Points

Confirmed or directly implied pain points:

- Tenant admins need a clear answer to "who did what?"
- Human and agent actions need to be visible in a common audit experience.
- Tool calls need to be traceable, including authorization results and failures.
- Denied actions must be explainable with reason and policy, not merely marked as denied.
- Full payload access is needed for review, but it carries sensitivity risk and should be visibly controlled.
- Audit records must not be mutable by ordinary users or tenant admins.

## Desired Future State

Tenant admins can open a searchable activity log and quickly find relevant human, agent, and tool-call activity by metadata filters.

From the list view, they can see the time, worker type, actor/agent, action type, customer/account, status, summary, and correlation/session id. They can then open a trace detail view to inspect full request, response, and tool payloads where needed.

The audit log is immutable and automatically expires entries according to tenant retention settings. The default retention is 90 days, and tenant admins may configure any value from 30 to 365 days. Retention-setting changes appear in the same audit trail as other interactions.

## Actors and Responsibilities

- Tenant admin:
  - searches activity logs;
  - views trace details and full payloads;
  - configures retention within the allowed range;
  - can see sensitive full payload data with an explicit warning.

- Human worker:
  - performs user-facing actions that produce auditable request/response traces;
  - is identified by email, role, and org.

- Agent worker:
  - performs user-facing agent interactions that produce auditable request/response traces;
  - may invoke tools that also produce auditable trace details;
  - is identified by agent name, role/workstream, model, prompt/skill/version, session/conversation id, and requested-by human/user where applicable.

## Events, Triggers, and Timing

Events that should create audit traces:

- Human user-facing request/response interaction.
- Agent user-facing request/response interaction.
- Agent or worker tool call.
- Denied action.
- Failed action.
- Retention configuration change.

Timing and retention:

- Traces should include time of occurrence.
- Default retention is 90 days.
- Tenant admins can configure retention from 30 to 365 days.
- Records expire only according to the retention policy.

## Decisions, Rules, and Exceptions

Confirmed rules:

- Every trace is tenant-scoped.
- A trace may be linked to a customer/account, but customer/account linkage is optional.
- System-level or no-customer traces are allowed.
- Tenant admins can view full payloads.
- Full payloads should not be keyword-searched in v1.
- Search should apply to deterministic app-generated metadata/summary fields only.
- Denied actions must show the denial reason and policy.
- Audit records are immutable except for retention expiry.
- Retention configuration is tenant-admin controlled only.
- Retention values must be between 30 and 365 days.
- Export is not included in v1.

## Systems, Documents, and Data

Data that must be captured or displayed includes:

- Tenant scope.
- Optional customer/account scope.
- Time.
- Worker type.
- Human actor identity: email, role, org.
- Agent identity: agent name, role/workstream, model, prompt/skill/version, session/conversation id, requested-by human/user where applicable.
- Action type.
- Status: success, failure, or denied.
- Deterministic app-generated summary for search/list display.
- Correlation/session id.
- Full request payload.
- Full response payload.
- Tool call details:
  - tool name;
  - tool purpose;
  - input payload;
  - output payload;
  - authorization result;
  - duration;
  - status/error;
  - linked parent request/response.
- Denial reason and policy for denied actions.
- Retention configuration change details.

## Candidate CRM / ERP / Operations Needs

This interview did not identify CRM, ERP, billing, inventory, scheduling, or customer-service process needs for this workstream.

Operations/governance needs that may matter:

- Tenant-admin operational accountability for human and AI-assisted work.
- Activity review for customer/account-related actions.
- Sensitive payload handling for tenant-admin-only access.
- Configurable retention to meet tenant operating preferences.

## Examples and Scenarios

Confirmed example scenarios:

- A tenant admin searches activity from a specific date range to determine which human or agent performed an action.
- A tenant admin filters activity by worker type to see agent actions separately from human actions.
- A tenant admin opens an agent trace and sees the agent name, model, prompt/skill/version, session/conversation id, requested-by user, request payload, response payload, and related tool calls.
- A tenant admin opens a tool-call trace and sees the tool name, purpose, inputs, outputs, authorization result, duration, status/error, and parent request/response link.
- A tenant admin reviews a denied action and sees the denial reason and policy.
- A tenant admin changes audit retention from the default 90 days to another value between 30 and 365 days; that configuration change appears in the activity log.

## Success Measures

A successful v1 audit trace workstream lets tenant admins:

- answer "who did what" for human and agent workers;
- find relevant traces using the required filters;
- distinguish success, failure, and denial outcomes;
- inspect full payload details when needed;
- understand why a denied action was denied;
- see tool calls linked to their parent worker request/response;
- configure retention within the allowed range;
- trust that audit records are immutable except for retention expiry.

## Rejected or Out of Scope

Out of scope for v1:

- Export or compliance bundle generation.
- Investigation notes.
- Suspicious activity acknowledgement or review workflow.
- Agent-generated audit summaries.
- Keyword search across full payloads.
- Payload visibility restrictions beyond tenant-admin-only access.

## Open Questions

No blocking open questions remain from this interview for Stage 1 input.

Possible future questions if the scope expands:

- Should exports be added for compliance or customer evidence bundles?
- Should suspicious activity review, acknowledgement, or investigation notes become part of the workflow?
- Should there be a stronger role than tenant admin for some future sensitive payload access scenarios?
- Should retention policy have platform-level maximums or plan-based limits beyond the current 30–365 day tenant-admin range?

## Agent Summary for Ingestion

Create or update the audit trace workstream intent so tenant admins can search and view immutable tenant-scoped audit traces answering "who did what" across human workers, agent workers, and tool calls.

V1 must provide a searchable activity log with filters for date/time range, worker type, actor/user/agent, action type, customer/account, and status. List rows should display time, worker type, actor/agent, action type, customer/account, status, deterministic app-generated summary, and correlation/session id.

Trace detail should expose full request/response/tool payloads to tenant admins, with a visible sensitive-payload warning. Tool details should include name, purpose, input/output payload, authorization result, duration, status/error, and linked parent request/response. Denied actions must include denial reason and policy.

Retention defaults to 90 days and is tenant-admin configurable from 30 to 365 days. Audit records are immutable except for retention expiry. Retention configuration changes must be logged as audit interactions. Export, investigation notes, acknowledgement workflows, agent-generated summaries, and full-payload keyword search are out of scope for v1.

## Confirmation Notes

The stakeholder confirmed the captured v1 intent during the interview and requested this clean app-description input artifact.
