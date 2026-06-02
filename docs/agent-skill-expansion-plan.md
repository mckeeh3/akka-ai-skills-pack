# Agent Skill Expansion Plan

## Status

Initial routing/design skill expansion is complete as of this update. The remaining work is executable reference examples, tests, and any later refinements discovered while generating the core AI-first SaaS starter core app.

## Purpose

This plan identifies how the agent-related skills in this repository were revised and expanded using the core AI-first SaaS input documents, especially Modules 3-7 under `docs/examples/core-ai-first-saas-input/`.

The goal is to make the skills pack better at generating applications where agents are governed runtime actors with durable definitions, prompts, skills, traces, evaluations, and improvement loops.

## Primary reference inputs

References used for this expansion:

- `docs/examples/core-ai-first-saas-input/00-document-development-process-context.md`
- `docs/examples/core-ai-first-saas-input/01-core-seed-progression-plan.md`
- `docs/examples/core-ai-first-saas-input/02-persistent-discussion-capture.md`
- `docs/examples/core-ai-first-saas-input/05-module-agent-definition-prd.md`
- `docs/examples/core-ai-first-saas-input/06-module-prompt-governance-prd.md`
- `docs/examples/core-ai-first-saas-input/07-module-skill-governance-prd.md`
- `docs/examples/core-ai-first-saas-input/08-module-audit-work-trace-prd.md`
- `docs/examples/core-ai-first-saas-input/09-module-evaluation-closed-loop-improvement-prd.md`

Related existing guidance:

- `docs/ai-first-saas-application-architecture.md`
- `docs/agent-coverage-matrix.md`
- `skills/akka-agents/SKILL.md`
- `skills/akka-agent-component/SKILL.md`
- `skills/akka-agent-runtime-state/SKILL.md`
- `skills/akka-agent-harness-skills/SKILL.md`
- `skills/akka-agent-evaluation/SKILL.md`
- `skills/ai-first-saas-policy-governance/SKILL.md`
- `skills/ai-first-saas-audit-trace/SKILL.md`

## Core model propagated into skills

Agent support is now represented as a progressive governed runtime model:

```text
AgentDefinition
→ PromptDocument / PromptVersion
→ SkillDocument / SkillVersion / AgentSkillManifest
→ AuditTraceEvent / WorkTrace
→ EvaluationRubric / EvaluationRun / ImprovementProposal
```

Generated AI-first SaaS apps should not treat agents as only Java classes with static prompts. Managed agents should have durable, tenant-scoped behavior profiles composed from:

- identity and authorization context;
- agent definition and lifecycle status;
- prompt document/version;
- compact skill manifest and approved skill read tool;
- model configuration reference;
- tool permission boundary;
- policy/approval boundaries;
- trace and evaluation requirements.

## Completed new skills

### `skills/akka-agent-behavior-profiles/SKILL.md` — complete

Covers durable tenant-scoped `AgentDefinition`, lifecycle, owner/steward, authority level, model config references, tool permission boundaries, admin views, and runtime profile lookup.

### `skills/akka-agent-governed-documents/SKILL.md` — complete

Covers prompts, skills, rubrics, policies, examples, and other behavior-shaping artifacts as governed versioned documents using the two-entity pattern:

```text
BehaviorDocumentEntity(docId)
- Event Sourced Entity owns canonical current state
- emits lifecycle/change events

BehaviorDocumentVersionEntity(docId:version)
- Key Value Entity stores immutable version snapshot
- populated by Consumer from document events
- supports version history and diff UI
```

### `skills/akka-agent-prompt-governance/SKILL.md` — complete

Covers runtime-managed system prompts beyond simple `PromptTemplate`: `PromptDocument`, `PromptVersion`, review, activation, diff/history UI, effective prompt assembly, `PromptAssemblyTrace`, and safe test consoles.

### `skills/akka-agent-skill-governance/SKILL.md` — complete

Covers governed runtime skills: `SkillDocument`, `SkillVersion`, `AgentSkillManifest`, compact skill manifest prompt context, `readSkill(skillId)`, `SkillLoadTrace`, versioning, diff/history UI, and skill-loading test consoles.

### `skills/akka-agent-work-trace/SKILL.md` — complete

Covers agent-specific audit/work traces for `AgentDefinition`, prompt/skill/model/tool/data/policy usage, authorization decisions, redaction, correlation, trace timelines, and investigation UI.

### `skills/akka-agent-closed-loop-improvement/SKILL.md` — complete

Covers governed evaluation and self-improvement loops with `EvaluationRubric`, `EvaluationRun`, `EvaluationFinding`, `ImprovementProposal`, replay/simulation evidence, human approval, activation, monitoring, rollback, and audit.

## Completed revisions to existing skills

### `skills/akka-agent-harness-skills/SKILL.md` — revised

Now distinguishes:

1. static packaged skills for small deploy-time guidance;
2. governed runtime skills for tenant-managed, versioned, audited AI-first SaaS apps.

Routes governed runtime skills to `akka-agent-skill-governance`.

### `skills/akka-agent-runtime-state/SKILL.md` — revised

Now distinguishes:

- built-in `PromptTemplate` for simple runtime-editable prompts;
- governed `PromptDocument` / `PromptVersion` when prompts require review, approval, versioning, diff, trace, and tenant governance.

Routes governed prompt work to `akka-agent-prompt-governance` and `akka-agent-governed-documents`.

### `skills/akka-agent-evaluation/SKILL.md` — revised

Still focuses on Java SDK evaluator-agent mechanics and `EvaluationResult`, but now routes proposal, activation, rollback, and self-improvement scope to `akka-agent-closed-loop-improvement`.

### `skills/akka-agent-tools/SKILL.md` — revised

Now clarifies:

- tool calls must check authorization outside prompts;
- `readSkill(skillId)` is a governed guidance tool with manifest checks;
- skill loading does not grant external tool or data permission.

### `skills/akka-agents/SKILL.md` — revised

Now routes generic agent requests to the focused governed-agent skills:

- durable agent admin/runtime profiles → `akka-agent-behavior-profiles`;
- governed behavior documents → `akka-agent-governed-documents`;
- runtime prompt governance → `akka-agent-prompt-governance`;
- governed runtime skills → `akka-agent-skill-governance`;
- agent explanation/audit → `akka-agent-work-trace`;
- self-improvement/evaluation loops → `akka-agent-closed-loop-improvement`.

### `skills/ai-first-saas-policy-governance/SKILL.md` — revised

Now treats prompts, skills, policies, rubrics, examples, and thresholds as behavior-shaping governed artifacts when they affect runtime behavior. Routes governed behavior documents and closed-loop improvement to the new focused skills.

### `skills/ai-first-saas-audit-trace/SKILL.md` — revised

Now routes detailed agent-specific prompt/skill/model/tool/data traces to `akka-agent-work-trace`.

### `skills/README.md` — revised

Updated agent routing and practical combinations for the new governed-agent skills.

### `docs/agent-coverage-matrix.md` — revised

Added rows for:

- durable `AgentDefinition` and behavior profiles;
- governed behavior document/version pattern;
- governed runtime prompt documents and prompt assembly traces;
- governed runtime skills and manifests;
- agent-specific work traces;
- governed closed-loop improvement.

The matrix intentionally marks these as gaps or partial coverage until executable examples/tests exist.

## Remaining future work: executable examples and tests

The first pass intentionally created routing/design skills only. Executable examples should come next, likely in this order:

1. `AgentDefinitionEntity` + agent admin endpoints/views/tests.
2. `PromptDocumentEntity` + `PromptVersionEntity` snapshot consumer + diff/history endpoint/tests.
3. `SkillDocumentEntity` + `AgentSkillManifestEntity` + `readSkill(skillId)` tool/tests.
4. `AuditTraceEvent` / `WorkTrace` ingestion, views, endpoints, and redaction tests.
5. `EvaluationRun` / `ImprovementProposal` workflow/tests.

## Executable-example planning

The executable-example plan has been created:

- `docs/agent-executable-examples-plan.md`

It decides that the first examples should live in the existing reference app under `src/main/java/com/example/...`, with focused packages for governed-agent examples, and defines five implementation slices:

1. AgentDefinition behavior profile.
2. Governed PromptDocument and version snapshots.
3. SkillDocument, AgentSkillManifest, and `readSkill(skillId)` tool.
4. Agent work trace.
5. Evaluation and ImprovementProposal workflow.

## Review checklist

Before considering this expansion fully complete:

- agent-related tasks route to the smallest relevant skill;
- static harness-like skill loading and governed runtime skill loading are clearly separated;
- prompt governance and skill governance reuse the same governed document/versioning pattern;
- all guidance preserves tenant scoping and backend authorization;
- skills emphasize visible UI/API delivery where module PRDs require it;
- self-improvement is proposal-driven and governed, not direct self-modification;
- coverage matrix reflects new executable-example gaps honestly.
