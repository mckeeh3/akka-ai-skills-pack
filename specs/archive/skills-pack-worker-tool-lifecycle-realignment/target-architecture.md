# Target Architecture: Lifecycle, Workers, Tools, and Compile Contracts

## 1. Continuous development lifecycle

The skills-pack should present app evolution as a continuous loop with three named phases.

### Phase 1: Interview / intent reconciliation

Purpose: turn flexible input into current app intent.

Typical inputs:

- business interview notes;
- new feature requests;
- behavior tweaks;
- UI adjustments;
- bug reports;
- manual test observations;
- implementation discoveries;
- changed stakeholder decisions.

Typical outputs:

- normalized intent delta;
- app-description graph edits;
- affected workers, tools, capabilities, surfaces, agents, Akka components, tests, security, and observability;
- open questions or non-blocking assumptions;
- readiness assessment for build/compile.

### Phase 2: Build / compile / implement

Purpose: compile accepted app-description intent into specs, task briefs, code, tests, and validation paths.

Typical outputs:

- impacted graph node list;
- capability/tool contract;
- actor adapters and exposure channels;
- selected Akka substrates;
- frontend/API/runtime changes;
- tests and checks;
- runtime/manual smoke path.

### Phase 3: Manual runtime test / reconciliation

Purpose: verify real behavior through the intended local Akka/API/UI path and feed findings back into intent.

Typical outputs:

- manual test session notes;
- pass/fail/blocked status;
- evidence path and role/AuthContext/tenant setup;
- classification of findings as description gap, implementation gap, test gap, provider/config blocker, seed/demo-data gap, UX/state gap, or expectation change;
- follow-up app-description/spec/task updates.

## 2. Worker model

An app worker is a participant that does app work under explicit authority, evidence, tools, supervision, handoffs, and trace obligations.

Worker types:

- `human`: authenticated person or organizational role using human judgment;
- `functional-agent`: user-facing AI-backed context/workstream agent;
- `internal-agent`: bounded AI-backed specialist invoked by another worker or component;
- `autonomous-agent`: durable background AI-backed worker with task lifecycle;
- `evaluator-agent`: AI-backed reviewer/judge for quality, risk, policy, completeness, or outcome evidence;
- `system`: deterministic workflow, timer, consumer, projection, integration, API, or policy participant.

Human and software workers should be modeled symmetrically enough to preserve common work/tool/trace semantics, but not identically. Human judgment is not controlled like an AI model; surfaces constrain and support human execution but do not replace backend authorization.

## 3. Harnesses and actor adapters

Workers act through harnesses and actor adapters.

Human harnesses:

- structured surfaces;
- browser workstream shell;
- forms, decision cards, dashboards, inspection surfaces;
- confirmed human chat tool plans when explicitly modeled.

Software-agent harnesses:

- Akka Agent;
- Akka AutonomousAgent;
- governed prompts/skills/references/model policy/tool boundaries;
- memory, guardrails, traces, structured responses.

System harnesses:

- workflows;
- timed actions;
- consumers;
- views/projections;
- endpoints;
- integrations;
- internal service methods.

Actor adapters should be explicit. Candidate adapter values:

- `surface_action`
- `human_chat_tool_plan`
- `agent_tool_call`
- `workflow_step`
- `timer_invocation`
- `consumer_reaction`
- `mcp_tool_call`
- `api_call`
- `internal_call`

## 4. Governed tools

A governed tool is a semantic app operation or governed evidence read exposed to one or more actor adapters.

Tools are not:

- generic UI buttons;
- raw endpoints;
- raw Akka component methods;
- unscoped service calls;
- implicit permissions.

A governed tool should define:

- stable tool id;
- purpose and operation type;
- capability id/class it realizes;
- allowed actor adapters;
- worker types allowed to request it;
- authorization and tenant/customer scope;
- input schema and validation;
- confirmation/approval policy;
- idempotency and transaction boundary;
- side effects and read/view effects;
- result and partial-failure surfaces/messages;
- audit/work trace requirements;
- denial semantics;
- tests and validation path.

Tool types may include:

- `read_evidence`
- `search_or_list`
- `draft`
- `recommend`
- `evaluate`
- `propose`
- `command`
- `approval`
- `admin`
- `internal_system`

## 5. Capability and Akka implementation separation

Capabilities remain the backend business contract. Akka components remain implementation substrates.

```text
governed tool
  -> capability contract
    -> one or more Akka components / endpoints / views / frontend adapters
```

A tool may be implemented by multiple Akka components. Do not duplicate business semantics for each exposure path.

Example:

```text
Governed tool: inviteOrganizationMember
Capability: manage organization invitations
Adapters:
  - human Tenant Admin surface_action
  - confirmed human_chat_tool_plan
  - restricted AI admin agent_tool_call with approval gate
Implementation:
  - InvitationEntity
  - InvitationWorkflow
  - EmailOutboxEntity
  - InvitationView
  - AdminAuditConsumer
  - HTTP endpoint
  - invitation surfaces
```

## 6. App-description graph implications

The app-description graph should make these node families explicit:

- app;
- domains;
- workstreams;
- workers;
- surfaces;
- agents;
- tools;
- capabilities;
- policies/security;
- Akka components;
- frontend routes/components;
- observability/audit/work traces;
- tests;
- manual test scenarios.

The graph should preserve links such as:

```text
worker -> harness/surface/agent runtime
worker -> governed tools
surface action -> governed tool
agent tool call -> governed tool
governed tool -> capability
capability -> Akka substrate
governed tool -> result surface / event / trace
test -> worker + adapter + tool + capability path
manual test -> runtime path + reconciliation outcome
```

## 7. Compile contract

A build/compile task should start from a current-intent delta and produce a bounded implementation slice.

Minimum compile chain:

```text
accepted intent delta
  -> affected graph nodes
  -> workers and actor adapters
  -> governed tools
  -> capabilities
  -> selected Akka substrates
  -> frontend/API/agent/runtime adapters
  -> tests and validation path
  -> manual runtime test scenario
```

The compile contract should prevent:

- page-only tasks without worker/tool/capability context;
- component-only tasks without product capability context;
- agent tools that bypass shared governed tools;
- duplicate human and AI implementations for the same operation;
- missing denial, approval, idempotency, trace, or tenant-isolation semantics.
