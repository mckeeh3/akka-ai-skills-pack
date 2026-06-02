---
name: akka-agent-tool-boundaries
description: Implement governed ToolPermissionBoundary enforcement for Akka agents across local function tools, Akka component tools, remote MCP tools, readSkill, readReferenceDoc, data access, side effects, approval-required expansion, runtime denials, and traces.
---

# Akka Agent Tool Boundaries

Use this skill when an AI-first SaaS app must decide, store, enforce, review, or test which tools and data resources a managed agent may use.

A `ToolPermissionBoundary` is backend-enforced authority for agent tool and data access. Prompt text, skill text, tool descriptions, and compact manifests may explain allowed behavior to the model, but they never grant tool/data permission, tenant/customer scope, approval authority, or autonomous side-effect authority.

## Generated SaaS input contract

For generated full-stack AI-first SaaS tool-boundary work, implement only after the task, app-description, spec, or backlog supplies or explicitly defers:
- owning functional/internal agent, workstream placement when user-facing, governance surface id/action, and affected tool/action surfaces;
- capability ids/classes for each tool grant, data grant, side effect, approval request, and boundary-management action;
- `AuthContext`, tenant/customer scope, caller roles/capabilities, agent authority, tool categories, data classifications, and denial behavior;
- idempotency, policy/approval/escalation, audit/work trace fields, redaction, simulation/diff requirements, and required tests.

If these are absent for generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of inventing tool authority.

## Required reading

Read these first if present:
- `../../docs/capability-first-backend-architecture.md`
- `../../docs/agent-runtime-invocation-pattern.md`
- `../akka-agent-behavior-profiles/SKILL.md`
- `../akka-agent-tools/SKILL.md`
- `../akka-agent-component-tools/SKILL.md`
- `../akka-agent-mcp-tools/SKILL.md`
- `../akka-agent-skill-governance/SKILL.md`
- `../akka-agent-work-trace/SKILL.md`
- `../../templates/ai-first-saas-starter/app-description/55-ui/skill-manifests-and-tool-permissions.md`

## Use when the request mentions

- `ToolPermissionBoundary`, tool boundary, tool permissions, tool governance, tool registry, or tool catalog
- allowed tool ids, categories, scopes, data resources, side effects, or external integrations
- read-only vs side-effecting agent tools
- component tools or MCP tools under managed agent profiles
- tool denial, unauthorized tool use, cross-scope access, denied skill/reference loads, or denied data access traces
- approval-required expansion of an agent's tools, data scope, tenant/customer scope, or autonomy
- tool permission UI, proposed boundary diffs, simulations, or rollback

## Core model

```text
ToolPermissionBoundary
- tenantId
- boundaryId
- boundaryVersion
- status: draft | in_review | active | deprecated | archived
- agentDefinitionId or reusableBoundaryRef
- stewardRole / ownerAccountId
- allowedToolGrants[]
- allowedDataGrants[]
- sideEffectPolicy
- approvalRules
- policyRefs
- createdBy / reviewedBy / activatedBy / timestamps
- checksum / changeSummary
```

```text
ToolGrant
- toolId: stable id, not method display text alone
- toolCategory: local_function | component | mcp | read_skill | read_reference | data_lookup | external_side_effect
- capabilityId
- surface: agent_tool | component_tool | mcp_tool | skill_tool
- allowedOperations: read | propose | request_approval | execute
- tenantScope: same_tenant | selected_customer | explicit_customer_set | support_context
- requiredAgentAuthorityLevel
- requiredCallerCapability optional
- dataClassificationLimit
- sideEffectLevel: none | internal_state | notification | external_call | billing | security | irreversible
- autonomy: never | proposal_only | approval_required | bounded_autonomous
- idempotencyRequired: true/false
- traceLevel: denial_only | access | full_work_trace
```

```text
ToolInvocationTrace / AuditTraceEvent fields
- tenantId / customerId
- correlationId / workTraceId
- agentDefinitionId
- boundaryId / boundaryVersion
- toolId / toolCategory / capabilityId
- requested operation and input summary
- authorizationDecision: allowed | denied | approval_required
- denialReason or approvalRuleRef
- dataAccessSummary / sideEffectSummary
- idempotencyKey when applicable
- policyRefs / approvalRefs
- timestamp
```

## Tool registry/catalog

Create a backend-owned tool registry or catalog before assigning grants. Registry entries should include:

- stable `toolId` and display name;
- capability id and purpose;
- tool category (`local_function`, `component`, `mcp`, `read_skill`, `read_reference`, etc.);
- implementation binding: class/method, component method, MCP tool name/server, or governed resource lookup;
- input/output schema summary and redaction rules;
- read-only or side-effecting classification;
- tenant/customer scope behavior;
- required caller capability and agent authority level;
- approval/idempotency/audit requirements;
- owner/steward and lifecycle status.

Do not allow free-form model-supplied tool names, URLs, component ids, resource paths, or MCP server names to bypass the registry.

## Runtime enforcement sequence

Before model invocation, `AgentRuntimeResolver` should resolve the active `AgentDefinition`, active prompt, compact expertise manifest (`AgentSkillManifest` plus `AgentReferenceManifest` when references are assigned), and active `ToolPermissionBoundary` for the current `AuthContext` and mode.

For every tool call:

1. Resolve the stable tool id from the registered local/function/component/MCP/readSkill/readReferenceDoc binding.
2. Load the active `ToolPermissionBoundary` for the `AgentDefinition` and tenant.
3. Verify the tool id/category/capability is granted for the current mode (`runtime`, `test`, `replay`, `evaluation`).
4. Verify caller `AuthContext`, tenant/customer scope, active membership, required capability, agent lifecycle, and authority level.
5. Verify input validation, data classification, side-effect class, idempotency key, approval rule, and policy refs.
6. If approval is required, return a safe `approval_required` result or create an approval/decision-card request; do not execute the side effect.
7. If denied, return a safe denial shape and emit a trace with the denial reason.
8. If allowed, execute the tool through the selected implementation binding and emit access/side-effect traces.
9. Preserve the same capability contract when the operation is also exposed by UI, HTTP/gRPC, workflow, timer, consumer, or MCP paths.

## Read-only vs side-effecting defaults

- Read-only evidence tools are preferred for agent autonomy when outputs are scoped, redacted, and traced.
- `readSkill(skillId)` is a `read_skill` tool grant that loads procedural guidance only; it cannot expand other tool or data permissions.
- `readReferenceDoc(referenceId)` is a separate `read_reference` tool grant that loads governed factual/process references only when the active expert bundle and reference manifest assign the reference; it cannot expand tool, data, role, tenant/customer, approval, or side-effect authority.
- Data-export, billing, security, role/membership, cross-tenant, external-message, email-send, delete, irreversible, or high-impact tools default to `approval_required`. Email-send tools must route through `akka-resend-email-service`; Resend is the supported production email service and local/dev/test must use captured outbox behavior.
- Side-effecting tools must define idempotency, duplicate handling, denial shape, trace fields, rollback or compensation expectations, and approval/autonomy policy.
- Autonomous side effects require an explicit accepted policy boundary and should be narrow, reversible, and observable.

## Governed loader tools

For `readSkill(skillId)` and `readReferenceDoc(referenceId)`:

- assign stable tool ids such as `agent.read_skill` and `agent.read_reference` plus capability ids;
- require explicit `read_skill` and `read_reference` grants; one does not imply the other;
- authorize against tenant, AuthContext, active `AgentDefinition`, active workstream expert bundle, active manifest assignment, document/version status, mode, redaction/token limits, and customer scope when applicable;
- return safe non-enumerating denials to the model for unassigned, inactive, cross-tenant, wrong-customer, missing-boundary, oversized, or unauthorized loads;
- emit `SkillLoadTrace` or `ReferenceLoadTrace` for both allowed and denied loads, linked to the same `AgentWorkTrace` and boundary version;
- treat loaded text as guidance/evidence only, subordinate to backend capability, policy, and boundary enforcement.

## Local function tools

For `@FunctionTool` methods and external tool classes:

- assign each exposed method a stable tool id and capability id;
- register only methods included in the tool registry and active boundary;
- inject or resolve AuthContext and correlation id outside model arguments when possible;
- enforce boundary checks before protected reads or side effects;
- return safe denial, validation, approval-required, or success results;
- trace allowed and denied invocations.

## Non-component ComponentClient-backed tool facades

For local/external `@FunctionTool` classes that use `ComponentClient` internally:

- treat the facade method as the model-facing tool and assign it one stable tool id plus capability id;
- register the facade binding separately from any component methods it calls;
- list underlying component calls, data resources, tenant/customer scopes, and side-effect classes in the tool registry entry;
- enforce boundary checks before the facade reads protected data or performs side effects, not only inside downstream components;
- resolve AuthContext, correlation id, customer/tenant scope, and idempotency keys outside model-supplied arguments when possible;
- return scoped, redacted, computed DTOs rather than leaking raw component state or internal component layout;
- trace the facade invocation with safe summaries of underlying component/data access and side effects;
- do not require the underlying component methods to be annotated with `@FunctionTool` unless they are also intentionally exposed directly as model-selectable component tools.

## Akka component tools

For `.tools(ComponentClass.class)`:

- annotate only component methods that are selected capability surfaces;
- map generated tool names and `uniqueId` to a stable tool id plus tenant/customer-scoped aggregate/workflow id rules;
- enforce authorization inside the component command/query or at the calling capability boundary;
- prefer View-backed evidence tools for read-only lookup;
- route state-changing entity/workflow tools through proposal or approval flows unless bounded autonomy is explicitly granted.

## Remote MCP tools

For `.mcpTools(...)` and `RemoteMcpTools`:

- allow only registry-approved tool names via `.withAllowedToolNames(...)` or `.withToolNameFilter(...)`;
- bind each remote tool to a stable tool id and capability id;
- propagate service identity, AuthContext, tenant/customer scope, correlation id, and policy context as required;
- require the MCP endpoint to enforce its own ACL/JWT/service identity, scope validation, redaction, and audit;
- treat remote side effects as high-risk by default and require approval unless an accepted policy grants bounded autonomy.

## Approval-required expansion

Any change that broadens tools, data resources, tenant/customer scope, side-effect class, external integration authority, model autonomy, or approval bypass is authority expansion.

Default maintenance flow:

```text
human or AgentBehaviorEditorAgent request
→ proposed ToolPermissionBoundary diff
→ affected capabilities/tools/data/scope/risk/tests listed
→ simulation/replay evidence attached when consequential
→ decision-card review by authorized steward/approver
→ approved activation emits audit/work trace
→ denied expansion is recorded and leaves active boundary unchanged
```

Skill or prompt text that asks for additional tools must become a boundary proposal. It must not be honored at runtime until the backend boundary is approved and activated.

## UI surfaces

Provide protected UI for:

- tool registry/catalog by category, capability, side-effect level, owner, and lifecycle;
- `ToolPermissionBoundary` list/detail with linked agents, grants, scopes, approval rules, and checksum;
- proposed boundary diff with rationale, risk/impact, affected capabilities, tests/replays, and approval actions;
- denial history for tool/data/cross-scope attempts;
- trace links for tool invocations and data access;
- activation, rollback, and deprecation controls.

## Testing expectations

Plan tests for:

- allowed read-only tool invocation with trace emission;
- denied ungranted tool id/category;
- denied side-effecting tool when boundary only grants read/propose;
- cross-tenant/customer denial;
- disabled/archived agent denial;
- `readSkill(skillId)` allowed only when both `AgentSkillManifest` and `ToolPermissionBoundary` permit it;
- `readReferenceDoc(referenceId)` allowed only when both `AgentReferenceManifest` and `ToolPermissionBoundary` permit it, with redaction/customer-scope checks;
- missing `read_skill` or `read_reference` grants deny loader calls and emit the corresponding load trace;
- MCP allowed-tool filtering and remote denial propagation;
- component-tool `uniqueId` scope validation;
- facade-tool boundary checks for underlying component/data access, redaction, and trace summaries;
- approval-required response for authority expansion or high-impact side effects;
- idempotent duplicate side-effecting tool calls;
- traces include `ToolPermissionBoundary`, tool id, capability id, authorization decision, policy/approval refs, and safe summaries.

## Review checklist

Before finishing, verify:

- every agent tool maps to a named capability and stable tool registry entry
- active `ToolPermissionBoundary` is resolved before model invocation and checked before each tool execution
- read-only, side-effecting, external, MCP, component, `readSkill`, and `readReferenceDoc` tools have explicit grant semantics
- prompt/skill/reference text, compact manifests, and tool descriptions do not serve as authorization controls
- denied tool/data access fails closed and emits trace records
- approval-required expansion cannot be activated by prompt/skill text alone
- side-effecting tools define idempotency, audit, approval, and rollback/compensation expectations
- UI/test plans cover boundary detail, proposed diffs, denials, traces, and activation/rollback
