# Governed Agent Executable Examples Plan

## Purpose

This plan defines how to add executable reference examples and tests for the governed-agent skills added in `docs/agent-skill-expansion-plan.md`.

The examples should prove the new guidance with small, focused Akka components rather than attempting to implement the entire core AI-first SaaS starter core app. They are source assets for this skills pack and should remain easy for future harness runs to load, inspect, and extend.

## Source references

Read before implementing examples:

- `docs/agent-skill-expansion-plan.md`
- `docs/examples/core-ai-first-saas-input/05-module-agent-definition-prd.md`
- `docs/examples/core-ai-first-saas-input/06-module-prompt-governance-prd.md`
- `docs/examples/core-ai-first-saas-input/07-module-skill-governance-prd.md`
- `docs/examples/core-ai-first-saas-input/08-module-audit-work-trace-prd.md`
- `docs/examples/core-ai-first-saas-input/09-module-evaluation-closed-loop-improvement-prd.md`
- `skills/akka-agent-behavior-profiles/SKILL.md`
- `skills/akka-agent-governed-documents/SKILL.md`
- `skills/akka-agent-prompt-governance/SKILL.md`
- `skills/akka-agent-skill-governance/SKILL.md`
- `skills/akka-agent-work-trace/SKILL.md`
- `skills/akka-agent-closed-loop-improvement/SKILL.md`

## Placement decision

Place the first executable examples in the existing reference app under:

```text
src/main/java/com/example/domain/agents/
src/main/java/com/example/application/agents/
src/main/java/com/example/api/agents/
src/test/java/com/example/application/agents/
```

Rationale:

- the current repository already uses `src/` as the executable example layer;
- existing tests demonstrate component patterns in the same app;
- examples can reuse existing security/domain conventions where useful;
- focused packages prevent governed-agent examples from being confused with simpler agent examples.

Do not create a separate exported example tree for the first pass. Consider exporting later after the examples stabilize.

## Scope boundary

The examples should demonstrate a minimal governed-agent substrate, not the full core starter core app.

Include:

- tenant-scoped commands and views;
- stable ids and version references;
- backend validation and denial behavior;
- audit/trace records as durable facts or queryable test records;
- enough endpoint/API surface to demonstrate intended usage;
- deterministic tests.

Defer:

- full browser frontend implementation;
- WorkOS/AuthKit integration;
- complete SaaS user-admin flows;
- production-ready diff algorithms;
- large evaluation datasets;
- canary rollout sophistication;
- cross-tenant learning or global skill sharing.

UI requirements remain specified in the PRD input docs. Executable examples should expose API/endpoints and state/query patterns that a UI would consume.

## Minimal end-to-end demonstration path

The reference path should show this sequence:

```text
1. create an AgentDefinition
2. create and activate a PromptDocument version for that agent
3. create and activate a SkillDocument version
4. assign the skill to the agent's AgentSkillManifest
5. assemble effective prompt context with compact skill manifest
6. read an allowed skill through readSkill(skillId)
7. deny an unassigned skill read
8. emit/search trace events for prompt assembly and skill loads
9. run an evaluator against a test output
10. create an ImprovementProposal from the finding
```

This path touches all new skills while staying small enough for incremental implementation.

## Example implementation sequence

### Slice 1: AgentDefinition behavior profile

Goal: establish durable managed-agent identity and lifecycle.

Add:

```text
src/main/java/com/example/domain/agents/AgentDefinition.java
src/main/java/com/example/domain/agents/AgentDefinitionCommandHandler.java
src/main/java/com/example/domain/agents/AgentDefinitionValidator.java
src/main/java/com/example/application/agents/AgentDefinitionEntity.java
src/main/java/com/example/application/agents/AgentDefinitionByTenantView.java
src/main/java/com/example/api/agents/AgentDefinitionEndpoint.java
```

Minimum behavior:

- create draft agent definition;
- update draft metadata;
- activate valid draft;
- disable active agent;
- reject archived/disabled runtime lookup;
- reject secret-like model config values;
- tenant id required on all commands/queries;
- emit simple trace/audit events or return event metadata suitable for later trace ingestion.

Tests:

```text
src/test/java/com/example/application/agents/AgentDefinitionEntityTest.java
src/test/java/com/example/application/agents/AgentDefinitionEndpointIntegrationTest.java
src/test/java/com/example/application/agents/AgentDefinitionByTenantViewIntegrationTest.java
```

Cover:

- create/activate/disable success;
- missing purpose/tool boundary validation;
- secret-like model reference rejection;
- disabled agent cannot resolve runtime profile;
- tenant-filtered list/detail;
- forbidden/cross-tenant behavior if endpoint auth fixture is available.

### Slice 2: Governed PromptDocument and version snapshots

Goal: demonstrate governed prompt lifecycle and immutable versions.

Add:

```text
src/main/java/com/example/domain/agents/PromptDocument.java
src/main/java/com/example/application/agents/PromptDocumentEntity.java
src/main/java/com/example/application/agents/PromptVersionEntity.java
src/main/java/com/example/application/agents/PromptVersionSnapshotConsumer.java
src/main/java/com/example/application/agents/PromptHistoryView.java
src/main/java/com/example/api/agents/PromptGovernanceEndpoint.java
```

Minimum behavior:

- create prompt document for active/draft agent;
- edit draft content;
- submit for review;
- approve version;
- activate approved version;
- reject activation of unapproved version;
- create immutable version snapshot on submit/approve/activate boundary;
- expose history and simple line/string diff input.

Tests:

```text
PromptDocumentEntityTest.java
PromptVersionSnapshotConsumerIntegrationTest.java
PromptHistoryViewIntegrationTest.java
PromptGovernanceEndpointIntegrationTest.java
```

Cover:

- lifecycle success path;
- activation denial for unapproved version;
- version snapshot immutability expectation;
- history query by prompt/agent/tenant;
- secret-like prompt content rejection;
- prompt belongs to same tenant as AgentDefinition.

### Slice 3: SkillDocument, AgentSkillManifest, and readSkill tool

Goal: demonstrate governed runtime skills and manifest-authorized loading.

Add:

```text
src/main/java/com/example/domain/agents/SkillDocument.java
src/main/java/com/example/domain/agents/AgentSkillManifest.java
src/main/java/com/example/application/agents/SkillDocumentEntity.java
src/main/java/com/example/application/agents/SkillVersionEntity.java
src/main/java/com/example/application/agents/SkillVersionSnapshotConsumer.java
src/main/java/com/example/application/agents/AgentSkillManifestEntity.java
src/main/java/com/example/application/agents/GovernedAgentSkillTools.java
src/main/java/com/example/application/agents/GovernedSkillDemoAgent.java
src/main/java/com/example/api/agents/SkillGovernanceEndpoint.java
```

Minimum behavior:

- create/edit/approve/activate skill version;
- assign active skill version to an agent manifest;
- assemble compact manifest string;
- `readSkill(skillId)` returns only manifest-allowed active skill content;
- unassigned, inactive, disabled-agent, and cross-tenant reads are denied safely;
- skill load emits trace event.

Tests:

```text
SkillDocumentEntityTest.java
SkillVersionSnapshotConsumerIntegrationTest.java
AgentSkillManifestEntityTest.java
GovernedAgentSkillToolsTest.java
GovernedSkillDemoAgentTest.java
SkillGovernanceEndpointIntegrationTest.java
```

Cover:

- allowed skill read;
- unassigned skill denial;
- inactive/deprecated skill denial;
- disabled agent denial;
- compact manifest excludes full skill text;
- tool result includes version/checksum/authority note;
- skill text cannot grant tool permission.

### Slice 4: Agent work trace

Goal: demonstrate normalized trace facts and queryable timelines.

Add:

```text
src/main/java/com/example/domain/agents/AuditTraceEvent.java
src/main/java/com/example/domain/agents/WorkTrace.java
src/main/java/com/example/application/agents/AuditTraceEventEntity.java
src/main/java/com/example/application/agents/AgentWorkTraceView.java
src/main/java/com/example/application/agents/AgentTraceIngestionConsumer.java
src/main/java/com/example/api/agents/AgentTraceEndpoint.java
```

Minimum behavior:

- record trace facts for prompt assembly, skill load allowed, skill load denied, agent test run, and tool/data denial;
- include agentDefinitionId, prompt version, skill manifest/version, skill version, model ref, authorization basis, redaction classification, and correlation id when available;
- query trace events by tenant, agent, category, decision, and correlation id;
- expose redacted detail response by default.

Tests:

```text
AuditTraceEventEntityTest.java
AgentWorkTraceViewIntegrationTest.java
AgentTraceEndpointIntegrationTest.java
```

Cover:

- trace emission for allowed and denied skill loads;
- correlation across prompt assembly → skill load → agent test run;
- tenant-filtered trace search;
- sensitive fields redacted by default;
- trace read does not expose full prompt/skill content unless explicitly allowed.

### Slice 5: Evaluation and ImprovementProposal workflow

Goal: demonstrate governed improvement loop without full autonomous self-modification.

Add:

```text
src/main/java/com/example/domain/agents/EvaluationRun.java
src/main/java/com/example/domain/agents/ImprovementProposal.java
src/main/java/com/example/application/agents/GovernedOutputEvaluatorAgent.java
src/main/java/com/example/application/agents/EvaluationRunWorkflow.java
src/main/java/com/example/application/agents/ImprovementProposalEntity.java
src/main/java/com/example/application/agents/EvaluationQueueView.java
src/main/java/com/example/api/agents/EvaluationImprovementEndpoint.java
```

Minimum behavior:

- run deterministic or `TestModelProvider`-backed evaluator against a test output or trace;
- create `EvaluationFinding` result;
- create `ImprovementProposal` targeting a prompt or skill version;
- approve or reject proposal;
- activation is represented as a governed handoff, not direct mutation, unless implementing a narrow activation call is small;
- rollback target is captured.

Tests:

```text
GovernedOutputEvaluatorAgentTest.java
EvaluationRunWorkflowIntegrationTest.java
ImprovementProposalEntityTest.java
EvaluationImprovementEndpointIntegrationTest.java
```

Cover:

- evaluator result mapping and label validation;
- finding creation;
- proposal creation from finding;
- unauthorized activation/review denial if auth fixture is available;
- approval/rejection lifecycle;
- rollback target capture;
- audit/trace emission.

## Cross-slice implementation rules

- Keep each slice independently useful and testable.
- Prefer narrow domain records and component APIs over large generic frameworks.
- Use tenant ids in commands, state, events, views, and endpoints.
- Prefer deterministic tests with `TestModelProvider` or pure domain logic.
- Avoid adding real provider credentials, secrets, or environment requirements.
- Keep generated examples small enough for skills to reference cheaply.
- Do not build the full UI in `src/`; endpoint/API and HTML-lite reference surfaces are enough unless a later task explicitly targets UI examples.
- Add audit/trace fields early even if the full trace UI arrives later.

## Skills to load per slice

### Slice 1

- `akka-agent-behavior-profiles`
- `akka-event-sourced-entities`
- `akka-views`
- `akka-http-endpoints`
- `akka-agent-testing`

### Slice 2

- `akka-agent-governed-documents`
- `akka-agent-prompt-governance`
- `akka-event-sourced-entities`
- `akka-key-value-entities`
- `akka-consumers`
- `akka-views`
- `akka-http-endpoints`

### Slice 3

- `akka-agent-governed-documents`
- `akka-agent-skill-governance`
- `akka-agent-tools`
- `akka-agent-component`
- `akka-agent-testing`
- `akka-event-sourced-entities`
- `akka-key-value-entities`
- `akka-consumers`
- `akka-views`
- `akka-http-endpoints`

### Slice 4

- `akka-agent-work-trace`
- `ai-first-saas-audit-trace`
- `akka-event-sourced-entities`
- `akka-key-value-entities`
- `akka-consumers`
- `akka-views`
- `akka-http-endpoints`

### Slice 5

- `akka-agent-closed-loop-improvement`
- `akka-agent-evaluation`
- `akka-agent-work-trace`
- `akka-agent-governed-documents`
- `akka-workflows`
- `akka-workflow-pausing`
- `akka-event-sourced-entities`
- `akka-views`
- `akka-http-endpoints`

## Documentation updates after each slice

After each executable slice, update:

- `docs/agent-coverage-matrix.md` status and canonical examples;
- the relevant new skill's repository examples section if added;
- `skills/README.md` example references if the example becomes canonical;
- `docs/agent-skill-expansion-plan.md` remaining-work list if scope changes.

## Acceptance criteria for the example set

The example set is sufficient when a future harness can read the new skills and find at least one executable reference for each governed-agent concept:

- durable AgentDefinition behavior profile;
- governed document/version snapshot pattern;
- governed prompt activation and prompt assembly trace;
- governed skill manifest and `readSkill(skillId)` authorization;
- agent work trace and redaction;
- evaluation-to-improvement proposal loop.

## Open implementation questions

Resolve during Slice 1 planning:

1. Should these source-repo reference examples reuse existing `com.example.domain.security` fixture records directly or use lightweight fixture auth records in `com.example.domain.agents`? (`com.example` remains reference-only and is not generated-application package guidance.)
2. Should trace facts be written directly by components in early slices or introduced only through Slice 4 ingestion?
3. Should endpoint integration tests require real auth headers, or use simplified tenant/capability request records to keep examples focused?
4. Should version snapshots be separate Key Value Entities from the first prompt/skill slice, or mocked by views until the snapshot consumer slice is implemented?
5. What is the smallest useful diff representation: raw before/after strings, line arrays, or simple changed-line records?
