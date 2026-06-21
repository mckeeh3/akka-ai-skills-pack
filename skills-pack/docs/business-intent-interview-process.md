# Business Intent Interview Process

Use this doctrine for Stage 1 business intake: helping SMB owners, operators, department leads, or representatives express their business processes, goals, constraints, and needs in business language.

Stage 1 is upstream of app-description maintenance. Its output is accepted app-description input, not accepted application design.

```text
Stage 1: business-authored / agent-assisted input
Stage 2: app-description current intent
Stage 3: specs, backlog, tasks, implementation, runtime validation
```

The interview agent is an extrapolating business analyst, not a passive transcriber. It may infer likely process detail from sparse SMB input, but inferred content must remain labeled until confirmed, rejected, or left as a hypothesis/open question.

## Stage 1 Purpose

The purpose is to capture a durable business-intent artifact that later skills can ingest and decompose into domains, workstreams, capabilities, surfaces, agents, policies, traces, specs, tasks, and app implementations.

Stage 1 should help a business representative describe:

- what the business sells or provides;
- who the business serves;
- how work is found, sold, delivered, billed, supported, and improved;
- which people, teams, customers, vendors, tools, and systems participate;
- what decisions, approvals, exceptions, deadlines, and handoffs matter;
- what is painful, risky, slow, manual, inconsistent, opaque, or expensive;
- what outcomes, controls, and measurements would make the business better.

Stage 1 should not ask business representatives to design workstreams, app-description files, Akka components, governed-tools, APIs, tables, or frontend screens.

## Interview Loop

Use an iterative loop:

```text
minimal business input
  -> agent asks focused business questions
  -> agent extrapolates likely process detail and adjacent needs
  -> interviewee confirms, corrects, rejects, or prioritizes
  -> agent refines the capture
  -> repeat until the capture is mutually acceptable
  -> save an app-description input artifact under docs/input/**
```

The loop is not one-and-done. New observations, business changes, customer issues, staff feedback, policy changes, test findings, and implementation discoveries can all create new Stage 1 input artifacts over time.

## Extrapolation Rule

The agent should actively infer likely business processes and adjacent needs from minimal input. For example, a short description of products, services, customer type, sales motion, delivery model, or staffing model may imply candidate needs such as:

- CRM: lead intake, qualification, pipeline, follow-ups, contacts, opportunities, proposals, renewals.
- ERP/accounting: orders, invoices, payments, job costing, revenue recognition, purchasing, vendor bills.
- Operations: scheduling, capacity, assignments, checklists, fulfillment, service delivery, quality control.
- Inventory/assets: stock, equipment, parts, serial numbers, locations, maintenance, replenishment.
- Customer service: onboarding, tickets, escalations, service levels, warranty, communications.
- Compliance/governance: approvals, policy exceptions, audit records, certifications, retention.
- Reporting: margin, throughput, conversion, backlog, aging, risk, exceptions, owner dashboards.

Extrapolation must be presented as a candidate model:

```text
Based on what you described, I suspect these workflows may matter. Please mark what is accurate, missing, not relevant, or lower priority.
```

Never silently promote inferred content to confirmed requirements.

## Capture Categories

Stage 1 artifacts should separate these categories:

- `explicit_input`: what the interviewee said or supplied.
- `agent_inferences`: likely processes, needs, risks, roles, or systems inferred by the agent.
- `confirmed_intent`: statements the interviewee accepted as accurate enough.
- `rejected_or_out_of_scope`: candidate ideas the interviewee rejected, deferred, or narrowed.
- `open_questions`: unresolved questions that matter for later interpretation.
- `candidate_future_needs`: plausible but unconfirmed needs that may be revisited.

This separation lets Stage 2 agents ingest useful business context without treating every guess as product authority.

## Business-Level Question Areas

Ask questions in business language. Select only the areas useful for the current interview.

- Business model: What do you sell, to whom, how often, and how is value delivered?
- Customers: Who are the customer types, buyers, users, approvers, and influencers?
- Sales: How does interest become a quote, order, contract, renewal, or lost deal?
- Delivery: What happens after a sale, who does the work, and how is progress tracked?
- Service/support: What happens when something goes wrong or a customer needs help?
- Money flow: How are prices, estimates, invoices, payments, credits, and costs handled?
- People and roles: Who is responsible, who approves, who needs visibility, and who is accountable?
- Tools and records: Which spreadsheets, software, emails, documents, and manual logs are used today?
- Decisions and rules: What thresholds, approvals, exceptions, policies, and judgment calls matter?
- Timing: What deadlines, reminders, expirations, service levels, and recurring cycles exist?
- Risk and compliance: What must be auditable, restricted, retained, reviewed, or escalated?
- Metrics: How do owners know the process is healthy or failing?
- Pain points: What is slow, manual, duplicated, error-prone, invisible, or frustrating?
- Desired future: What would a better version of this process look like?

## Output Artifact Shape

Use Markdown by default. Prefer a path such as:

```text
docs/input/business-interviews/<yyyy-mm-dd>-<topic>.md
```

Recommended sections:

```text
# Business Intent Interview: <topic>

## Source Context
## Interview Status
## Explicit Input
## Agent-Inferred Business Model
## Confirmed Intent
## Current Process
## Pain Points
## Desired Future State
## Actors and Responsibilities
## Events, Triggers, and Timing
## Decisions, Rules, and Exceptions
## Systems, Documents, and Data
## Candidate CRM / ERP / Operations Needs
## Examples and Scenarios
## Success Measures
## Rejected or Out of Scope
## Open Questions
## Agent Summary for Ingestion
## Interviewee Confirmation
```

Keep raw stakeholder language where it matters. The agent summary may normalize wording, but it must not erase explicit input, rejected ideas, or uncertainty.

## Handoff to Stage 2

When the interviewee accepts the artifact as accurate enough, the next route is usually one of:

- `business-intent-to-app-input` to clean up interview notes or transcripts into a Stage 1 handoff artifact.
- `app-description-input-normalization` to turn accepted Stage 1 input into a current-intent delta.
- `app-description-intake-router` to identify affected app-description nodes.
- `ai-first-saas`, `agent-workstream-apps`, and focused app-description skills when Stage 2 decomposition begins.

Stage 2 agents may use confirmed intent as input. They may use unconfirmed inferences and candidate future needs only as hypotheses, open questions, or later discovery prompts.

## Anti-Patterns

Avoid:

- asking SMB representatives to speak in implementation terms;
- accepting one vague sentence as enough when obvious process areas are unexplored;
- treating extrapolation as confirmed fact;
- hiding rejected ideas or uncertainty;
- turning Stage 1 input directly into app design without Stage 2 normalization;
- implying that the business owner approved technical architecture by approving interview wording;
- requiring a perfect complete PRD before useful input can be saved.
