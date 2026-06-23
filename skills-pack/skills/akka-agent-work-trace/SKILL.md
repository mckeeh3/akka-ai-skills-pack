---
name: akka-agent-work-trace
description: Design and implement agent-specific audit/work traces for AgentDefinition, prompt/skill/reference/model/tool/data/policy usage, authorization decisions, redaction, correlation, trace timelines, and investigation UI in AI-first SaaS apps.
---

# Akka Agent Work Trace

Use this skill when agent activity must be explainable, searchable, tenant-scoped, and auditable across prompts, skills, references, models, tools, data access, authorization decisions, approvals, and outcomes.

This is the agent-specific companion to `ai-first-saas-audit-trace`. It does not replace general audit/security, observability, entity, view, endpoint, or web UI skills.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the required capability, AuthContext/scope, DTO, side-effect, trace, and test inputs are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Required reading

Read these first if present:
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/governed-agent-substrate.md`
- `../docs/agent-coverage-matrix.md`
- `../docs/examples/ai-first-saas-core-app-domain/audit-trace-workstream/README.md`
- `../core-saas-foundation/SKILL.md`
- `../ai-first-saas-audit-trace/SKILL.md`
- `../akka-agents/SKILL.md`
- `../akka-agent-behavior-profiles/SKILL.md`
- `../akka-agent-prompt-governance/SKILL.md`
- `../akka-agent-skill-governance/SKILL.md`
- `../akka-agent-tools/SKILL.md`
- `../akka-event-sourced-entities/SKILL.md`
- `../akka-key-value-entities/SKILL.md`
- `../akka-consumers/SKILL.md`
- `../akka-views/SKILL.md`
- `../akka-http-endpoints/SKILL.md`

## Use when the request mentions

- agent audit, work trace, activity trace, timeline, investigation, or explainability
- prompt/skill/reference/model/tool version references in audit
- assigned and unassigned skill/reference loads, including `readSkill` and `readReferenceDoc` denials
- `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `ToolInvocation`, or `DataAccessEvent`
- trace search, redaction, trace export, or sensitive trace fields
- correlation ids across agent tests, workflows, tools, prompt assembly, and skill loads
- why an agent was allowed, denied, escalated, or approved
- replay/evaluation needing prior prompt/skill/model/tool context

## Core trace model

Use two related concepts:

```text
AuditTraceEvent
- immutable normalized event fact
- fine-grained event for actions, denials, prompt assembly, skill load, tool use, data access, decisions
```

```text
WorkTrace
- correlated unit of work or investigation timeline
- groups related AuditTraceEvents by correlation/work id
```

For agent activity, include enough context to answer:

```text
who/what acted?
under which tenant/customer/auth context?
which AgentDefinition and lifecycle state?
which prompt version was assembled?
which skill/reference manifests and versions were available/loaded?
which model config was used?
which governed tool ids and adapter tool ids were used or denied?
was the source a human surface action, confirmed human chat tool plan, AI-backed agent-tool call, workflow, timer, consumer, API, or MCP call?
which permission, policy, confirmation, approval, or guardrail allowed/denied it?
what was the safe input/output/evidence summary?
which workflow/goal/task/session caused it?
what outcome, decision, exception, or rollback followed?
```

## Agent trace event fields

Recommended normalized fields:

```text
AuditTraceEvent
- traceEventId
- tenantId
- customerId optional
- correlationId
- causationId / parentEventId optional
- workTraceId optional
- timestamp
- eventCategory: agent | prompt | skill | tool | data_access | authorization | decision | workflow | evaluation | system
- eventType
- severity: info | warning | risk | error
- actorType: human | agent | workflow | timer | consumer | system
- actorAccountId optional
- agentDefinitionId optional
- targetResourceType / targetResourceId
- actionName
- authorizationDecision: allowed | denied | not_applicable
- authorizationBasisSummary
- agentDefinitionVersion or profile checksum when available
- promptDocumentId / promptVersion when applicable
- skillManifestId / skillManifestVersion when applicable
- skillDocumentId / skillVersion when applicable
- referenceManifestId / referenceManifestVersion when applicable
- referenceDocumentId / referenceVersion when applicable
- modelConfigRef when applicable
- governedToolId / capabilityId when applicable
- toolName / toolCategory / adapterToolId when applicable
- actorAdapterSource: surface_action | human_chat_tool_plan | agent_tool_call | workflow | timer | consumer | api | mcp when applicable
- requestedByAccountId when an AI-backed or model-mediated action originates from a human request
- confirmedByAccountId / confirmationId when a consequential human chat tool plan was confirmed
- dataAccessSummary when applicable
- policyRefs / approvalRefs / guardrailRefs when applicable
- inputSummary / outputSummary when safe
- resultSurfaceRef or systemMessageRef when a tool result is user-visible
- partialFailureSummary when a multi-step plan or tool sequence only partially succeeds
- safeMetadata
- redactionClassification
```

## Required agent trace producers

Plan trace emission for:

- AgentDefinition lifecycle changes and denials.
- Prompt document lifecycle changes.
- Prompt assembly for test/runtime/replay/evaluation.
- Prompt test runs.
- Skill document lifecycle changes.
- Reference document lifecycle changes.
- AgentSkillManifest and AgentReferenceManifest changes.
- `readSkill(skillId)` allowed and denied calls, including unassigned, inactive, cross-tenant, disabled-agent, unauthorized-mode, oversized, and missing-boundary denials.
- `readReferenceDoc(referenceId)` allowed and denied calls, including unassigned, inactive/deprecated/unapproved, cross-tenant, wrong-customer, unauthorized-use, redaction/access, token-limit, secret-like-content, and missing-boundary denials.
- Tool invocation allowed, denied, approval-required, unconfirmed, failed, and partial-failure calls.
- Human-requested confirmed chat tool-plan lifecycle: plan proposed, plan confirmation bound, per-tool execution, denial before confirmation, approval-required result, and result/partial-failure surfaces.
- Data access by agent tools or component tools.
- Workflow steps that call agents.
- Agent response summaries when consequential.
- Approval, escalation, exception, rollback, and evaluation handoffs.

Do not rely on logs alone for these facts.

## Akka component mapping

Route to:
- `akka-event-sourced-entities` for audit-grade append-only trace facts when stateful event history is needed.
- `akka-consumers` to normalize events from agent/profile/prompt/skill/workflow/tool producers into `AuditTraceEvent` facts and to publish trace notifications.
- `akka-views` for trace search, timeline, filters, active work feeds, and investigation queries.
- `akka-key-value-entities` for explicit `WorkTrace` current summaries when timelines need status, title, or outcome state.
- `akka-workflows` when traces are produced by durable multi-step agent execution or approval flows.
- `akka-http-endpoints` for trace search/detail/export/stream APIs.
- `akka-web-ui-apps` for Audit/Work Trace UI, timelines, filters, redaction states, and trace links from agent/prompt/skill surfaces.
- `akka-agent-evaluation` and future closed-loop skills when trace records feed evaluations and improvement proposals.

## Correlation rules

- Generate or propagate a correlation id at the edge endpoint or workflow start.
- Include causation/parent ids when an event follows another event.
- Preserve correlation across prompt assembly, skill load, reference load, human chat plan proposal/confirmation, tool calls, component commands, view reads, workflow steps, consumers, and timers where practical.
- Use stable ids for `AgentDefinition`, prompt versions, skill versions, manifests, model configs, and workflows.
- Do not expose cross-tenant correlation results; query filters must include authorized tenant/customer context.

## Redaction and privacy rules

- Store summaries and references by default, not full sensitive payloads.
- Never store provider secrets, JWTs, API keys, invite secret tokens, raw credentials, or hidden platform secrets.
- Classify fields as safe, sensitive, secret-never-store, or redacted.
- Require explicit capability such as `trace.sensitive.read` for sensitive fields.
- Redact exports by default.
- Trace read access does not imply access to underlying domain records.

## Trace UI surfaces

Provide protected UI for:
- trace landing with counts by category/severity/denial;
- search/list filters for time, actor, category, decision, agent, prompt, skill, tool, correlation id;
- work trace timeline detail;
- event detail with authorization basis and governed artifact references;
- redacted/sensitive field display states;
- links back to AgentDefinition, PromptDocument, SkillDocument, EvaluationRun, and workflows when authorized;
- safe copy/export of trace summaries.

## Test requirements

Plan tests for:
- trace emission for allowed agent actions;
- trace emission for denied agent/prompt/skill/reference/tool/data actions;
- trace emission for confirmed human chat tool plans, including `requestedBy`, `confirmedBy`, confirmation id, source `human_chat_tool_plan`, governed tool ids, denials before confirmation, and partial-failure/result-surface refs;
- assigned skill and assigned reference load traces include manifest version, document/version ids, mode/use, authorization decision, caller, and correlation id;
- unassigned skill and unassigned reference denials return safe model-visible text and emit protected denial trace facts;
- missing `read_skill` and `read_reference` ToolPermissionBoundary grants fail closed and trace the boundary decision;
- prompt, skill, reference, or manifest text that claims expanded authority cannot grant tools, roles, tenant/customer scope, approval authority, or side effects, and the denial/escalation is traceable;
- prompt version, skill version, and reference version references in traces;
- tenant/customer isolation for skill-load and reference-load trace search and detail;
- redaction behavior for normal and sensitive readers;
- disabled/archived agent trace behavior;
- correlation across prompt assembly → skill load/reference load → tool invocation/response/test run;
- export redaction by default.

## Review checklist

Before finishing, verify:
- consequential agent actions emit durable trace facts
- prompt, skill, reference, model, governed tool id, adapter tool id, data, authorization, confirmation, approval, and policy references are represented
- assigned and denied skill/reference loads emit trace facts and link to the agent work trace
- correlation ids link related events into a useful timeline
- sensitive content is summarized, redacted, or not stored
- trace APIs and views are tenant-scoped and capability-protected
- derived views are not the only source of audit truth
- trace records can feed future evaluation/replay/improvement flows
