# Domain workstream PRD structure

Use this structure when capturing PRDs or reference descriptions for AI-first SaaS domains. It applies to the core SaaS app domain and to later domain-specific app domains. Use `./workstream-contract.md` for compact workstream fields/readiness levels and `./workstream-attention-contracts.md` for attention item, producer, lifecycle, aggregation, and test contracts.

The goal is to decompose product intent into fully functional workstreams before implementation:

```text
domain intent
→ workstreams as root app units
→ exactly one backing functional/context-area agent per workstream
→ workstream-agent expertise for user assistance
→ role-specific dashboard surfaces and attention items
→ human surface graph and system-message surfaces
→ surface actions and surface requests
→ governed backend capabilities containing governed-tools
→ internal workstream agent graph where delegated work is needed
→ Akka/backend/frontend realization and tests
```

## Directory shape

A domain is a directory with a `README.md`. It contains one directory per workstream.

```text
<domain-name>/
  README.md

  <workstream-name>-workstream/
    README.md
    capabilities.md
    tests.md

    workstream-agent/
      prompt.md
      skills/
        <skill-name>/
          SKILL.md

    surfaces/
      dashboard.md
      <surface-name>.md
      system-messages.md
```

Example:

```text
ai-first-saas-core-app-domain/
  README.md

  user-admin-workstream/
    README.md
    capabilities.md
    tests.md

    workstream-agent/
      prompt.md
      skills/
        user-admin-overview/
          SKILL.md
        invitation-guidance/
          SKILL.md
        role-governance/
          SKILL.md

    surfaces/
      dashboard.md
      access-review-queue.md
      invitation-flow.md
      member-detail-card.md
      system-messages.md
```

The example uses User Admin because it is one required foundation workstream. Its surfaces are workstream contracts with capability-backed actions, not a page/resource decomposition to copy for unrelated domains.

## Domain `README.md`

The domain README is the high-level domain PRD. It should define:

- domain purpose and scope;
- domain vocabulary and shared object names;
- included workstreams;
- shared actors, roles, permissions/capabilities, and tenant/customer scope;
- shared policies, approval rules, authority limits, and exception rules;
- shared audit/work-trace expectations;
- shared backend objects or Akka component candidates when already known;
- domain-level readiness and not-ready conditions.

For the core SaaS app domain, the domain includes the required foundation workstreams such as My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy.

For an app-specific domain, the domain describes the user's business area and then decomposes it into domain-specific workstreams using the same structure.

## Workstream `README.md`

The workstream README is the workstream-level PRD and the primary generation contract for that vertical.

It must state:

- workstream id, display name, type-vs-runtime-instance semantics, purpose, and business responsibility;
- backing functional/context-area agent; every workstream has exactly one;
- authorized roles/capabilities and tenant/customer scope;
- workstream icon metadata;
- default dashboard, attention, or briefing surface;
- role-specific dashboards and the attention items each dashboard answers;
- required surface graph nodes, edges, result surfaces, and system-message surfaces;
- user intents the workstream agent must understand;
- surface actions and surface-request actions;
- capability inventory, governed-tool inventory, and exposure-channel summary;
- internal workstream agent graph when background/delegated worker agents are part of the workstream;
- audit/work-trace behavior;
- escalation, approval, denial, and exception behavior;
- readiness level (`identified`, `described`, `surface-ready`, `capability-ready`, `expertise-ready`, `runtime-ready`, or `production-ready`) and not-ready conditions.

Required invariant text:

```text
This workstream is backed by exactly one functional/context-area agent.
Surfaces are the only renderable workstream artifacts.
System messages are typed surfaces.
Every surface action, including read/query and surface-request actions, maps to a governed backend capability and governed-tool contract.
The workstream agent may request surfaces and guide users, but backend capabilities enforce authority.
```

## `workstream-agent/`

The `workstream-agent/` directory describes the workstream's user-facing assistant. The workstream remains the root abstraction; the agent backs the workstream and helps users operate within it.

### `prompt.md`

`prompt.md` contains the workstream agent's system-prompt intent, for example: "You are the User Admin workstream assistant..." It should define:

- agent role and responsibility;
- workstream scope and boundaries;
- supported user intents;
- how to answer “how do I...” questions;
- shorthand surface requests the agent should recognize, such as “dashboard”, “show users”, or “find Alex”;
- how to request or refresh surfaces;
- how to explain denials, validation errors, deferred capabilities, and next steps;
- tool/capability boundaries and authority reminders;
- when to escalate, request approval, or emit a system-message surface.

### `workstream-agent/skills/`

These are generated-app runtime/workstream-agent skills, not this repository's harness routing skills.

Use them to capture user-assistance knowledge for the workstream agent, such as:

- how to use the workstream;
- how each surface works;
- how to interpret fields and statuses;
- how to guide common tasks;
- how to explain denials and recovery;
- examples of supported user requests.

Each `SKILL.md` should be scoped to one user-facing assistance topic. For example, User Admin may have skills for general user administration, invitation guidance, role/capability guidance, access review, denial recovery, or surface-specific help.

If a generated app also stores runtime governed skills as `SkillDocument`/`SkillVersion` records, these files are default/reference content that can be represented in governed storage with provenance, review, activation, and tenant-customization rules.

## `surfaces/`

Each surface file is a surface-level PRD. It is a behavioral contract, not a static visual mockup.

A surface file should define:

- surface id, display name, type, and version;
- surface graph role: dashboard trunk, attention node, detail node, form node, decision node, progress node, trace node, or system-message/result node;
- owning workstream and reusable workstreams if any;
- purpose and when it appears;
- payload fields and redaction rules;
- producing read/evidence capability;
- available actions;
- graph edges: next surfaces, updates, dashboard attention changes, internal-agent work starts/results, or typed system-message surfaces produced by actions;
- authority requirements and denial behavior;
- loading, empty, ready, submitting, success, approval-needed, forbidden, error, conflict, stale/reconnect, and no-op states where relevant;
- audit/work-trace fields and visible trace links;
- rendering, action, authorization, tenant-isolation, audit/trace, accessibility, responsive, and realtime tests.

Surface actions include:

- read/query or surface-request actions, such as show dashboard, open an attention item, search scoped members, open an evidence card, open audit timeline, and refresh;
- command actions, such as invite, revoke, disable, save, resend, and update settings;
- proposal/approval/workflow actions, such as draft change, request approval, approve/reject, start review, and show workflow or AutonomousAgent progress/result surfaces;
- decision-card actions, such as inspect evidence, compare alternatives, approve, reject, counter, defer, or escalate;
- governance/trace actions, such as open diff, simulate policy impact, commit governed changes, open trace, and view audit timeline.

Every action maps to a governed backend capability. In browser realization, actions usually invoke backend APIs that return a new surface, updated surface, workstream item, workflow/progress surface, or typed `system_message` surface.

### `system-messages.md`

Each workstream should define its common system-message surfaces, including:

- success confirmations;
- forbidden/denied messages;
- validation failures;
- approval-required notices;
- deferred-capability notices;
- stale/reconnect notices;
- background-work-started notices;
- no-op results;
- safe recovery guidance.

System messages must not leak secrets, hidden privileged facts, prompt content, provider details, or cross-tenant data.

## `capabilities.md`

Capabilities are governed backend contracts. A capability is the product ability or grouping; governed-tools are the executable operations/queries inside that capability. APIs, browser-tools, agent-tools, workflow-tools, timer-tools, consumer-tools, MCP-tools, internal-tools, views, and component methods are exposure or realization forms over those governed-tool contracts. Capabilities should not exist only as notes inside surface files.

The workstream `capabilities.md` should define every operation/query exposed by the workstream and its exposure channels:

- stable capability id and purpose;
- capability grouping semantics: which governed-tools belong together and why;
- governed-tool ids and classes: read/evidence, command, proposal, approval, workflow, autonomous task, governance, trace/audit, scheduled, reactive, or internal;
- actors/callers: humans, workstream agent, internal agents, workflows, services, timers, consumers, support roles;
- AuthContext, tenant/customer scope, role/capability requirements, and denial behavior;
- input/output schemas, validation, redaction, and idempotency;
- data access and side effects;
- policy/approval gates and escalation rules;
- audit/work-trace fields;
- exposure channels: surface action, browser API/browser-tool, workstream-agent agent-tool, internal-agent agent-tool, workflow-tool, timer-tool, consumer-tool, MCP-tool/resource, view/query, or internal-tool;
- tests.

Surface files and workstream-agent skills reference capability ids and governed-tool ids from this file. The capability grouping remains authoritative for product meaning; each governed-tool remains authoritative for backend behavior, security, side effects, idempotency, approval, audit, denial shape, exposure mapping, and tests.

Exposure-channel rules:

- Browser APIs are frontend exposures of capabilities.
- Workstream-agent tools are conversational exposures of capabilities.
- Internal-agent tools are backend AI-worker exposures of capabilities.
- Surface actions reference capabilities and usually invoke browser APIs.
- Agent tools may invoke the same capabilities as surface actions when authority, required inputs, confirmation, approval, audit, and result surfaces are preserved.
- Side-effecting tools default to draft/proposal/approval flows unless a bounded autonomous policy explicitly allows execution.

## `tests.md`

`tests.md` summarizes workstream-level acceptance and regression expectations:

- authorized success flows through intended surfaces;
- user-intent handling by the workstream agent;
- surface request flow, including dashboards, scoped search results, detail/evidence cards, decision cards, and trace/governance surfaces;
- form submission and result-surface behavior;
- forbidden, disabled-user, missing-role, and tenant-isolation cases;
- idempotent/no-op behavior;
- audit/work-trace creation;
- approval/escalation behavior;
- system-message rendering and redaction;
- accessibility, responsive, stale/reconnect, and realtime behavior;
- local smoke/manual validation expectations.

A workstream is not ready if required surfaces are static mockups, actions do not invoke governed backend capabilities, authorization is frontend-only, protected data is hardcoded, audit/work traces are missing, or tests do not cover forbidden and tenant-isolation paths.

## How domain workstreams are introduced

When consuming domain-specific intent, first identify candidate workstreams before selecting Akka components. A domain workstream should represent a durable user-facing work area with a coherent assistant, surfaces, actions, authority model, and capability set.

For each candidate workstream, define:

1. business responsibility and outcome;
2. primary users and roles;
3. backing functional/context-area agent;
4. default surface;
5. required surfaces;
6. supported user intents;
7. surface actions and requested next surfaces;
8. governed backend capabilities and exposure channels;
9. Akka component realization candidates;
10. audit/work traces and tests.

Domain workstreams use the same pattern as core SaaS workstreams:

```text
workstream
→ backing workstream agent
→ role-specific dashboard and attention items
→ user intents
→ surface graph nodes and edges
→ surface actions / surface requests / system-message results
→ capabilities and governed-tools
→ browser-tools / agent-tools / internal-tools / workflows / views / entities / APIs
→ tests and traces
```

Do not decompose a domain first into pages, CRUD screens, resource APIs, entities, or agent tools. Use the workstream and surface model to preserve user intent and then map the required behavior to backend capabilities and Akka components. Conventional routes, route-backed regions, forms, and dense-data widgets are implementation/deep-link mechanics after the workstream and surface contracts are known.
