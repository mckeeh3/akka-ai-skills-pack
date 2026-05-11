---
name: ai-first-saas-permission-enforcement
description: Specify mechanical enforcement of agent tool permissions, data scopes, action authorization, approval gates, tenant isolation, policy evaluators, denied calls, and enforcement audit events for ai-first SaaS systems.
---

# ai-first-saas-permission-enforcement

Use this skill when a coding agent must design or implement the controls that determine what agents may read, call, change, approve, or execute in an ai-first SaaS product.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical contract for agent authority boundaries, approval gates, policies, data access, traces, and audit events.
- `docs/skills-pack-tech-stack.md` defines the target Akka + React/Vite/TypeScript stack: Akka agents, Workflows, Event Sourced Entities, Key Value Entities, Views, Consumers, Timed Actions, HTTP/gRPC/MCP endpoints, and frontend surfaces.

## Core rule: prompts are not a security boundary

Prompts, skills, and policy text instruct model behavior, but they do not enforce security. Treat every model output and requested tool call as untrusted until checked by platform code.

Platform code must enforce:

- which tools an agent can call;
- which tenant, user, domain object, and field data it can read or write;
- which actions it can perform automatically;
- which actions require approval before side effects;
- which policy clauses and thresholds apply;
- which denials, approvals, and executions are audit events.

If a constraint only appears in a prompt, system prompt, skill, or natural-language policy and is not represented in authorization code, it is guidance, not enforcement.

## Enforcement architecture checklist

For each agent, action class, tool, and data scope, define the following before implementation:

```yaml
enforcement_architecture:
  agent_definition:
    agent_id: string
    assigned_role: string
    allowed_tool_ids: string[]
    allowed_data_scopes: string[]
    auto_action_permissions: string[]
    approval_required_actions: string[]
    forbidden_actions: string[]
  compiled_context_snapshot:
    system_prompt_version_id: string
    skill_version_ids: string[]
    policy_version_ids: string[]
    permission_policy_version_id: string
  authorization_services:
    tool_authorizer: required
    data_scope_authorizer: required
    action_authorizer: required
    approval_gate_evaluator: required
    policy_evaluator: required
    tenant_isolation_guard: required
  runtime_guards:
    pre_tool_call_check: required
    pre_data_read_check: required
    pre_data_write_check: required
    pre_side_effect_check: required
    idempotency_check: required_for_side_effects
    post_result_redaction: required_for_sensitive_data
  audit:
    denied_call_events: required
    approved_call_events: required
    data_access_events: required
    policy_evaluation_events: required
    approval_gate_events: required
```

## Stack mapping

| Enforcement concern | Preferred stack component |
|---|---|
| Agent permission and authority definitions | Event Sourced Entity or governed Key Value Entity, with versioned commits |
| Long-running approval and side-effect blocking | Akka Workflow pause/signal/resume pattern |
| Tool authorization before invocation | Workflow step, agent tool wrapper, HTTP/gRPC/MCP gateway middleware |
| Data-scope authorization | Entity command handlers, query endpoints, view filters, repository/service guards |
| Policy and threshold evaluation | Deterministic service/component invoked before action; optionally assisted by Akka agents for classification only |
| Tenant isolation | Every command/query/tool call includes tenant context and is checked server-side |
| UI permission visibility | React views fed by backend-authoritative permission read models |
| Audit and trace projections | Events from entities/workflows consumed into Views and audit trace read models |

## Permission categories

### Tool permissions

Define tools as explicit capabilities, not arbitrary functions the model may request.

```yaml
tool_permission:
  tool_id: string
  display_name: string
  operation_type: read | analyze | draft | mutate_internal | external_side_effect | irreversible_side_effect
  side_effect_class: none | local_state_only | reversible | compensatable | irreversible
  allowed_agent_ids: string[]
  required_data_scopes: string[]
  required_policy_clause_ids: string[]
  approval_gate: none | conditional | always
  max_risk_without_approval: number | null
  max_impact_without_approval: number | null
  tenant_scoped: boolean
  audit_event_names: string[]
```

Rules:

- Expose only approved tools to each agent runtime when possible.
- Also validate requested tool calls at the server/tool-wrapper boundary; do not rely only on hiding tools from the model.
- Separate preview/draft tools from commit/send/delete/payment/write tools.
- Require idempotency keys and authorization records before side-effecting calls.
- Deny unknown tool names, malformed arguments, cross-tenant references, and scope mismatches by default.

### Data scopes

Data scopes constrain what an agent can inspect or mutate.

```yaml
data_scope:
  scope_id: string
  tenant_id_required: boolean
  entity_types: string[]
  field_classes_allowed: public | internal | sensitive | pii | secret
  row_filter_rules: string[]
  purpose: string
  allowed_agent_ids: string[]
  allowed_actions: read | write | summarize | export | delete
  redaction_rules: string[]
  retention_rules: string[]
```

Rules:

- Enforce tenant ID, user/org membership, object ownership, and purpose limitation on every read and write path.
- Apply field-level restrictions for PII, secrets, regulated data, and customer-confidential data.
- Prevent model output from becoming an export bypass; summarize/redact according to the caller's data scope.
- Log meaningful data access events, especially sensitive reads, exports, and cross-object joins.

### Action authorization

Actions are business operations, not just tool calls. A single action may require multiple tool calls and data accesses.

```yaml
action_authorization_rule:
  action_id: string
  description: string
  allowed_agent_ids: string[]
  allowed_roles_on_behalf_of: string[]
  required_data_scopes: string[]
  required_tool_permissions: string[]
  preconditions: string[]
  auto_allowed_when: string[]
  approval_required_when: string[]
  forbidden_when: string[]
  rollback_or_compensation: string | null
  audit_events: string[]
```

Rules:

- Authorize the business action before authorizing its implementation tools.
- Check action preconditions in deterministic code.
- Use risk, confidence, impact, stakes, data sensitivity, reversibility, and policy clauses to route `auto`, `review`, `approval`, or `escalate`.
- Treat destructive, external, financial, legal, security-sensitive, or customer-visible side effects as approval-required unless explicit policy permits automation.

## Approval gate enforcement

Approval gates must block before protected side effects, not after.

```yaml
approval_gate_check:
  gate_id: string
  protected_action_id: string
  requested_by_agent_id: string
  triggering_policy_clause_ids: string[]
  required_approver_roles: string[]
  required_evidence: string[]
  expires_at: datetime | null
  workflow_blocking: boolean
  decision_card_id: string
  resume_signal: string
```

Procedure:

1. Agent proposes or requests an action.
2. Platform builds an authorization context from tenant, user, goal, plan, agent, compiled context, data, tool, action, risk, confidence, impact, and policy versions.
3. Policy evaluator returns `allow`, `review`, `approval_required`, `deny`, or `escalate` with clause citations.
4. If `approval_required`, the workflow persists an `ApprovalRequest`, emits an audit event, and pauses before executing the side effect.
5. Human decision resumes, revises, cancels, or escalates the workflow.
6. The side effect executes only after approval validity, scope, and idempotency are rechecked.

## Policy evaluator contract

Policy evaluators should be deterministic where possible. LLMs may classify, extract, or recommend, but final enforcement must be represented as checkable code or compiled policy rules.

```yaml
policy_evaluation_result:
  id: string
  tenant_id: string
  subject:
    agent_id: string
    human_on_behalf_of: string | null
  action_id: string
  resource_refs: string[]
  tool_id: string | null
  decision: allow | review | approval_required | deny | escalate
  disposition_tag: auto | review | approval | escalate | fyi
  reasons:
    - code: string
      message: string
      policy_document_id: string
      policy_version_id: string
      clause_id: string
  thresholds_applied:
    confidence: number | null
    risk: number | null
    impact: number | null
    stakes: number | null
  approval_requirements:
    roles: string[]
    evidence: string[]
  evaluated_at: datetime
```

## Forbidden tool call handling

When an agent requests a forbidden, malformed, unknown, or out-of-scope tool call:

1. Do not execute the tool.
2. Persist the attempted call with sanitized arguments if safe.
3. Emit a `ToolCallDenied` or equivalent audit event.
4. Return a bounded denial message to the agent, including only safe reason codes and allowed alternatives.
5. If the attempt indicates prompt injection, cross-tenant access, privilege escalation, repeated bypass attempts, or sensitive data exfiltration, escalate to a human/security workflow.
6. Include the denial in the work trace and agent evaluation dataset.

Do not let the model self-approve by rephrasing the call. Re-evaluate any revised request from scratch.

## Least privilege and tenant isolation

- Start each agent with no tools, no sensitive data, and no side-effect authority; add only what the agent needs for its assigned responsibilities.
- Prefer separate specialist agents with narrow scopes over a single omnipotent agent.
- Scope every permission by tenant and, when needed, by organization, workspace, project, goal, entity type, field class, and action.
- Never trust tenant IDs, user IDs, or object IDs supplied by the model without server-side authorization checks.
- Use per-tenant views/projections or query filters that cannot be bypassed by prompt text.
- Avoid giving agents direct database credentials or raw unrestricted APIs. Wrap access behind authorizing services/tools.
- Rotate and isolate external credentials used by tools; do not expose secrets in prompts, traces, client payloads, or model context.

## Enforcement audit events

Minimum event catalog:

```yaml
enforcement_events:
  - name: PermissionPolicyCommitted
    when: permission/action/tool/data-scope rules change
    key_fields: [tenant_id, policy_id, version_id, author_id, changed_rules, approval_id]
  - name: CompiledAgentContextAuthorized
    when: an agent run receives its effective tool/data/action scopes
    key_fields: [tenant_id, agent_run_id, agent_id, compiled_context_id, permission_version_id]
  - name: PolicyEvaluated
    when: an action/tool/data request is checked against policy
    key_fields: [tenant_id, evaluation_id, agent_id, action_id, decision, clause_ids]
  - name: DataAccessAuthorized
    when: sensitive or material data access is allowed
    key_fields: [tenant_id, agent_id, scope_id, resource_refs, purpose, redaction_applied]
  - name: DataAccessDenied
    when: a data request is blocked
    key_fields: [tenant_id, agent_id, scope_id, resource_refs, reason_codes]
  - name: ToolCallAuthorized
    when: a tool call passes authorization
    key_fields: [tenant_id, tool_invocation_id, agent_id, tool_id, side_effect_class]
  - name: ToolCallDenied
    when: a requested tool call is blocked
    key_fields: [tenant_id, agent_id, requested_tool_id, reason_codes, escalation_id]
  - name: ApprovalGateRequired
    when: protected work is paused for approval
    key_fields: [tenant_id, approval_request_id, action_id, policy_clause_ids, workflow_id]
  - name: ApprovalGateSatisfied
    when: approval is granted and revalidated
    key_fields: [tenant_id, approval_request_id, approver_id, action_id, workflow_id]
  - name: SideEffectExecuted
    when: an authorized side effect is performed
    key_fields: [tenant_id, action_id, tool_invocation_id, idempotency_key, authorization_context_id]
```

Audit rules:

- Record denials as well as approvals; blocked attempts are part of accountability.
- Link enforcement events to `Goal`, `ExecutionPlan`, `AgentRun`, `TaskRun`, `ToolInvocation`, `ApprovalRequest`, and `WorkTrace` where applicable.
- Store policy, prompt, skill, permission, and threshold versions used at decision time.
- Redact or tokenize sensitive payloads in audit events while preserving enough metadata for investigation.

## Implementation procedure

1. Inventory agent responsibilities from the agent-team design.
2. Convert responsibilities into action classes, tool permissions, data scopes, and approval-required side effects.
3. Define permission objects and versioning/governance lifecycle.
4. Add authorization checks to every command, query, tool wrapper, workflow step, and external API boundary.
5. Add policy evaluator outputs with stable policy clause citations.
6. Ensure workflows pause before approval-required side effects.
7. Add enforcement audit events and trace links.
8. Build Views for permission inspection, pending approval gates, denied attempts, and sensitive data access.
9. Add React UI affordances that show effective permissions and approval boundaries, but keep backend checks authoritative.
10. Write tests for allowed, denied, approval-required, cross-tenant, prompt-injection, and retry/idempotency paths.

## Output format

When applying this skill, produce:

```yaml
permission_enforcement_plan:
  scope_summary: string
  agents:
    - agent_id: string
      responsibilities: string[]
      allowed_tools: string[]
      allowed_data_scopes: string[]
      auto_actions: string[]
      approval_required_actions: string[]
      forbidden_actions: string[]
  data_scopes:
    - scope_id: string
      entity_types: string[]
      field_classes: string[]
      tenant_isolation_rule: string
      redaction_rules: string[]
  action_rules:
    - action_id: string
      disposition_logic: string
      policy_clause_ids: string[]
      approval_gate: string | null
      side_effect_class: string
  enforcement_components:
    backend_entities: string[]
    workflows: string[]
    tool_wrappers: string[]
    policy_evaluators: string[]
    views: string[]
    frontend_surfaces: string[]
  denied_call_handling: string[]
  audit_events: string[]
  tests:
    - name: string
      scenario: string
      expected_result: allow | review | approval_required | deny | escalate
      expected_audit_events: string[]
```
