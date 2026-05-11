---
name: ai-first-saas-security-privacy
description: Design security, privacy, and abuse-resistance for ai-first SaaS systems, including tenant isolation, sensitive data handling, redaction, secrets, prompt-injection defense, provider data controls, audit privacy, retention, approvals, and security tests.
---

# ai-first-saas-security-privacy

Use this skill when a coding agent must plan, implement, or test security and privacy controls for an ai-first SaaS product.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical contract for agent authority, policies, traces, approvals, audit, and sensitive data handling.
- `docs/skills-pack-tech-stack.md` defines the target Akka + React/Vite/TypeScript stack: Akka agents, Workflows, Event Sourced Entities, Key Value Entities, Views, Consumers, Timed Actions, HTTP/gRPC/MCP endpoints, and frontend surfaces.

## Core principle

Treat every model input, model output, tool result, retrieved document, user-provided instruction, webhook payload, and external API response as untrusted until validated by platform code.

AI-first systems have conventional SaaS security risks plus agent-specific risks:

- agents may read, summarize, combine, or export sensitive data at machine speed;
- prompts and retrieved content can try to override instructions;
- tool outputs can contain malicious instructions or poisoned evidence;
- audit traces can accidentally preserve PII, secrets, or regulated data;
- model-provider calls can become unintended data disclosures;
- autonomous side effects can amplify abuse if approval gates and rate limits fail.

Security and privacy controls must therefore be implemented as backend-enforced policy, not only prompt guidance.

## Security and privacy design procedure

1. **Classify data and actions**: identify PII, secrets, regulated data, confidential customer data, internal-only data, and side-effect classes.
2. **Define trust boundaries**: users, tenants, agents, model providers, MCP/tools, external APIs, browser clients, webhooks, event streams, audit stores, and analytics exports.
3. **Model threats**: apply the threat-model prompts below to each goal, agent, tool, workflow, UI surface, and data flow.
4. **Specify controls**: tenant isolation, authorization, redaction, minimization, approval gates, rate limits, secrets handling, retention, and incident detection.
5. **Map to stack components**: enforce controls in Akka entities, workflows, tool wrappers, endpoint middleware, views/projections, and React UI contracts.
6. **Add audit-safe telemetry**: log meaningful security/privacy events without storing raw secrets or unnecessary PII.
7. **Write tests and evals**: include cross-tenant, prompt-injection, data-exfiltration, approval-bypass, redaction, deletion/export, and malicious tool-output scenarios.

## Threat-model prompts

For each feature or workflow, answer:

```yaml
threat_model:
  scope:
    feature_or_workflow: string
    tenant_context: string
    human_roles: string[]
    agents_involved: string[]
    tools_involved: string[]
    external_systems: string[]
  assets:
    sensitive_data_types: string[]
    secrets_or_credentials: string[]
    high_impact_actions: string[]
    audit_or_trace_records: string[]
  trust_boundaries:
    user_to_frontend: string
    frontend_to_backend: string
    backend_to_model_provider: string
    backend_to_tools: string
    tenant_to_tenant: string
    agent_to_agent: string
  threats:
    - name: cross_tenant_access | pii_leakage | prompt_injection | malicious_tool_output | secrets_exposure | approval_bypass | audit_privacy_leak | retention_violation | model_provider_disclosure | abuse_or_spam | data_poisoning
      scenario: string
      likely_actor: external_user | tenant_user | compromised_account | malicious_document | compromised_tool | buggy_agent | insider | integration_partner
      impact: low | medium | high | critical
      likelihood: low | medium | high
      existing_controls: string[]
      missing_controls: string[]
  required_mitigations:
    - control: string
      enforced_by: backend_code | workflow | policy_evaluator | tool_wrapper | view_filter | frontend_ui | ops_process
      audit_event: string | null
      test_name: string
```

## Control checklist

### Tenant isolation

- Require tenant context on every command, query, workflow signal, tool invocation, event, view, and model request.
- Enforce tenant membership server-side; never trust tenant IDs supplied by a model or browser alone.
- Scope Event Sourced Entities, Key Value Entities, Views, audit records, files, embeddings, vector indexes, and analytics read models by tenant.
- Prevent cross-tenant joins in tools, projections, exports, and prompt context assembly unless an explicit administrative control authorizes them.
- Include cross-tenant denial events in audit traces with sanitized resource references.

### PII and sensitive data handling

Define data classes before implementation:

```yaml
data_classification:
  class_id: string
  label: public | internal | confidential | pii | sensitive_pii | regulated | secret
  examples: string[]
  allowed_agent_uses: string[]
  allowed_model_provider_uses: none | redacted | full_with_contract | full_for_approved_tenants_only
  redaction_required_in:
    prompts: boolean
    traces: boolean
    audit_events: boolean
    ui: boolean
    exports: boolean
  retention_rule: string
  deletion_rule: string
  approval_required_for: string[]
```

Rules:

- Minimize model context: send only fields needed for the current task.
- Prefer references, summaries, tokens, hashes, or redacted snippets over raw sensitive payloads.
- Apply field-level and purpose-based access checks before retrieval and again before prompt construction.
- Mask sensitive values in React UI unless the user role and purpose justify reveal.
- Ensure generated summaries do not re-identify users or leak hidden fields.

### Redaction and audit log privacy

- Redact secrets, credentials, access tokens, payment data, unnecessary PII, and sensitive free text before storing traces or audit events.
- Preserve enough metadata for accountability: data class, resource ID, field class, purpose, policy version, redaction method, and actor.
- Separate raw execution artifacts from audit-safe records when raw retention is justified.
- Avoid storing hidden model chain-of-thought; store structured rationale, evidence references, alternatives, policy citations, and decisions.
- Provide role-restricted audit views and export paths.

### Secrets management

- Never place secrets in prompts, skill text, policy documents, frontend bundles, audit logs, traces, or model-visible tool descriptions.
- Store credentials in a managed secret store or deployment secret mechanism.
- Tools should receive short-lived credentials or server-side capability tokens where possible.
- Rotate secrets, isolate credentials per tenant/integration where feasible, and audit use.
- Treat secret exposure to a model provider or trace as an incident requiring revocation/rotation.

### Prompt injection defense

Prompt injection is expected, not exceptional. Defend by design:

- Separate trusted system/developer instructions from untrusted user, document, email, web, ticket, or tool content.
- Label untrusted content explicitly in prompts and tool outputs.
- Do not allow retrieved content to change policies, permissions, tool schemas, approval gates, or system prompts.
- Validate every requested tool call, data access, and side effect against backend policy.
- Strip or neutralize executable instructions from untrusted documents when the task only needs facts.
- Add injection detectors as advisory signals, but do not rely on detectors as the only defense.
- Escalate repeated or high-impact injection attempts to a security review workflow.

### Untrusted tool output handling

Tool results, MCP responses, external API data, scraped web pages, uploaded files, and webhooks can be malicious or poisoned.

Controls:

- Schema-validate and size-limit tool outputs before they enter agent context.
- Mark provenance and trust level for each evidence item.
- Reject or quarantine unexpected content types, embedded instructions, scripts, and oversized payloads.
- Require independent confirmation for high-stakes facts when the source is untrusted.
- Prevent tool output from directly selecting tools, modifying policies, approving decisions, or authorizing side effects.

### Data sent to model providers

For every model call, define:

```yaml
model_data_policy:
  model_provider: string
  model_name: string
  tenant_id: string
  data_classes_allowed: string[]
  redaction_strategy: none | mask | tokenize | summarize | retrieve_by_reference
  retention_contract: string
  training_use_allowed: boolean
  region_or_residency_requirement: string | null
  human_approval_required_for_sensitive_context: boolean
  audit_event: ModelContextPrepared
```

Rules:

- Respect tenant-specific provider, region, retention, training-use, and data-processing agreements.
- Do not send secrets or unnecessary sensitive data to a model provider.
- Record prompt/context data classes and policy versions in audit events without storing full sensitive prompt payloads unless explicitly required and protected.
- Support provider changes through governed configuration, not ad hoc code edits.

### Human approval for sensitive side effects

Require approval gates for actions that are destructive, external, irreversible, financial, legal, security-sensitive, customer-visible, privacy-impacting, or high-volume.

Examples:

- sending customer communications containing sensitive data;
- exporting, deleting, merging, or bulk-updating records;
- changing permissions, policies, prompts, skills, thresholds, or guardrails;
- initiating payments, refunds, collections, account suspension, or legal/compliance actions;
- sharing data with third parties or enabling integrations.

Approval cards must show sensitivity, policy clauses, data classes involved, reversibility, blast radius, and expected audit events.

### Retention, deletion, and export

- Define retention for domain records, events, traces, model prompts, tool payloads, audit logs, embeddings, files, exports, and analytics views.
- Support deletion or anonymization where legally required, while preserving legally necessary audit records in minimized form.
- Ensure derived data such as summaries, embeddings, digests, replay fixtures, and evaluation datasets follow the same privacy obligations as source data.
- Log export events and require role-based authorization for exports.
- Make deletion/export workflows testable and traceable.

## Stack mapping

| Concern | Preferred stack component |
|---|---|
| Data classification and privacy policy versions | Event Sourced Entity or governed Key Value Entity |
| Tenant-scoped domain state | Entity IDs, command handlers, and query filters with tenant context |
| Long-running approvals, deletion/export, incident response | Akka Workflows with pause/resume and compensation |
| Prompt/model context preparation | Backend service/tool wrapper with redaction and policy evaluation |
| External tools and MCP integrations | Authorizing wrappers, schema validation, output sanitization |
| Security/privacy event processing | Consumers into audit and security Views |
| Retention/deletion timers | Timed Actions and workflow deadlines |
| UI privacy controls | React views from backend-authoritative read models |
| Real-time security notifications | HTTP/SSE/WebSocket streams from security/audit views |

## Security/privacy event catalog

Minimum events to consider:

```yaml
security_privacy_events:
  - name: SensitiveDataAccessed
    when: an agent, user, or tool reads sensitive data
    key_fields: [tenant_id, actor_type, actor_id, data_class, resource_refs, purpose, redaction_applied]
  - name: ModelContextPrepared
    when: data is prepared for a model call
    key_fields: [tenant_id, agent_run_id, provider, model, data_classes, redaction_strategy, policy_version_id]
  - name: PromptInjectionDetected
    when: untrusted content appears to instruct policy/tool/permission bypass
    key_fields: [tenant_id, source_type, source_ref, detector_version, severity, action_taken]
  - name: ToolOutputQuarantined
    when: tool output fails trust, schema, content, or size checks
    key_fields: [tenant_id, tool_id, invocation_id, reason_codes]
  - name: SensitiveSideEffectApprovalRequired
    when: a privacy/security-sensitive action is blocked for approval
    key_fields: [tenant_id, approval_request_id, action_id, data_classes, policy_clause_ids]
  - name: DataExported
    when: user, agent, or integration exports data
    key_fields: [tenant_id, actor_id, data_classes, record_count, destination, approval_id]
  - name: DataDeletionOrAnonymizationCompleted
    when: retention or deletion workflows modify data
    key_fields: [tenant_id, request_id, resource_refs, method, preserved_audit_refs]
  - name: SecretExposureSuspected
    when: a credential may have entered prompt, output, trace, log, or audit storage
    key_fields: [tenant_id, source_ref, secret_type, containment_action, rotation_required]
```

## Acceptance criteria for implementation plans

A security/privacy plan is acceptable only if it includes:

- tenant isolation controls for every command, query, view, workflow, tool, model call, audit record, and export path;
- data classification for PII, secrets, regulated data, confidential data, traces, and derived data;
- redaction/minimization rules for prompts, model outputs, traces, audit logs, UI, and exports;
- secrets handling that keeps credentials out of prompts, clients, traces, and logs;
- prompt-injection and malicious tool-output defenses with backend-enforced authorization;
- a model-provider data policy covering retention, training use, residency, and sensitive-context handling;
- approval gates for sensitive side effects;
- retention, deletion, anonymization, and export requirements;
- security/privacy audit events with safe payloads;
- tests for cross-tenant access, redaction, injection, approval bypass, provider data policy, retention/deletion/export, and abuse/rate limits.

## Security testing checklist

Include tests or evals for:

- cross-tenant reads, writes, joins, exports, traces, views, and tool calls are denied;
- field-level redaction appears in model context, UI, audit events, exports, and traces;
- secrets never appear in generated prompts, client bundles, audit payloads, traces, logs, or model outputs;
- prompt-injection documents cannot override system prompts, policies, approval gates, or tool permissions;
- malicious tool output cannot authorize actions or modify governance objects;
- model-provider policy blocks disallowed data classes or applies required redaction;
- sensitive side effects pause for human approval before execution;
- deletion/export workflows affect source and derived data correctly;
- replay/evaluation fixtures do not contain unredacted production sensitive data unless explicitly authorized;
- rate limits and abuse controls prevent high-volume agent misuse.

## Output format

When applying this skill, produce:

```yaml
security_privacy_plan:
  scope_summary: string
  data_classification:
    - class_id: string
      label: public | internal | confidential | pii | sensitive_pii | regulated | secret
      examples: string[]
      allowed_agent_uses: string[]
      redaction_rules: string[]
      retention_rule: string
  threat_model:
    top_threats:
      - name: string
        scenario: string
        impact: low | medium | high | critical
        mitigations: string[]
        tests: string[]
  tenant_isolation:
    enforcement_points: string[]
    cross_tenant_denial_events: string[]
  model_provider_controls:
    providers_allowed: string[]
    data_classes_allowed: string[]
    redaction_strategy: string
    retention_training_residency_notes: string
  prompt_injection_controls:
    untrusted_sources: string[]
    isolation_rules: string[]
    detection_and_escalation: string[]
  tool_output_controls:
    validation_rules: string[]
    quarantine_rules: string[]
    provenance_rules: string[]
  sensitive_side_effects:
    - action_id: string
      why_sensitive: string
      approval_gate: string
      audit_events: string[]
  retention_deletion_export:
    retention_rules: string[]
    deletion_or_anonymization_rules: string[]
    export_controls: string[]
  stack_components:
    backend_entities: string[]
    workflows: string[]
    consumers: string[]
    views: string[]
    tool_wrappers: string[]
    frontend_surfaces: string[]
  audit_events: string[]
  acceptance_criteria: string[]
  security_tests:
    - name: string
      scenario: string
      expected_control: string
      expected_audit_events: string[]
```
