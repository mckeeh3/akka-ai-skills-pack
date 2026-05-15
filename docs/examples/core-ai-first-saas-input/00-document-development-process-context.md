# Core AI-First SaaS Input Document Development Process Context

## Purpose

Use this document as the persistent process context when creating the PRD/spec input documents under `docs/examples/core-ai-first-saas-input/`.

The input document set is a source asset for the skills pack. It is intended to:

1. define the canonical progressive core AI-first SaaS seed app;
2. provide realistic example input documents for skills pack users;
3. feed the skills pack planning flow to produce `specs/`, module/sprint plans, build backlogs, and tasks;
4. support incremental implementation of visible, demonstrable full-stack functionality.

Do not treat this document tree as the business source of truth for this repository itself. It is reference input material for generated applications.

## Working principle

Work on one input document at a time. Each document should be complete enough to feed into the skills pack independently or together with earlier documents.

The process is:

```text
core input docs
→ PRD/spec decomposition
→ module specs
→ sprint specs
→ task backlogs
→ one demonstrable full-stack sprint at a time
→ repeat
```

Each implementation sprint must produce visible behavior through UI and/or APIs, with tests. Avoid backend-only foundation work unless it directly supports a demonstrable full-stack increment.

## Core idea captured from planning discussion

The core seed app is built progressively:

1. start with minimal user authentication and app access;
2. add user administration;
3. add agent definition/governance foundations;
4. add runtime-managed prompts;
5. add shared skills and per-agent skill manifests;
6. add audit/work trace and explainability;
7. add evaluation and closed-loop improvement.

This progression continues beyond the seed app as skills pack users add real application modules.

## MVP definition

MVP does not mean a minimal backend skeleton.

For this skills pack, MVP means:

> the smallest full-stack, demonstrable, secure foundation that future modules can extend safely.

The first module must include a functional browser UI, authenticated app shell, `/api/me`, authorization boundary, tenant/membership context, and security tests. It may defer advanced administration, agent governance, prompt editing, skill management, and closed-loop improvement.

## Expected document set

Create the input documents under this directory, using names close to:

```text
README.md
00-document-development-process-context.md
01-core-seed-progression-plan.md
02-product-vision-and-progressive-delivery.md
03-module-auth-app-access-prd.md
04-module-user-admin-prd.md
05-module-agent-definition-prd.md
06-module-prompt-governance-prd.md
07-module-skill-governance-prd.md
08-module-audit-work-trace-prd.md
09-module-evaluation-closed-loop-improvement-prd.md
10-ui-ux-design-system-and-implementation-requirements.md
11-security-threat-model-and-testing-requirements.md
12-acceptance-scenarios-and-demo-flows.md
```

Adjust numbering only if later repository work establishes a better sequence.

## Document contract

Each module PRD should include:

- module purpose and user-visible outcome;
- actors and authorization expectations;
- core capabilities;
- durable objects and state ownership;
- UI surfaces and navigation expectations;
- API surfaces and integration expectations;
- audit/security requirements;
- acceptance scenarios;
- implementation notes for Akka decomposition;
- explicit defer list.

Each document should be specific enough that `akka-prd-to-specs-backlog` can break it into module specs, sprint specs, backlog items, and tasks.

## UI and UX expectations

The generated app is full-stack from the start. Each module document should describe specific UI requirements, not just backend capabilities.

Include where relevant:

- page inventory;
- navigation placement;
- list/detail/form behavior;
- empty, loading, error, forbidden, and disabled states;
- decision card layout;
- diff viewer behavior;
- audit timeline behavior;
- approval queue behavior;
- responsive and accessibility requirements;
- API/client state expectations.

## Agent runtime governance ideas to preserve

Prompts, skills, policies, examples, evaluator rubrics, and tool-use guidance are behavior-shaping runtime artifacts. They should be treated as governed documents where appropriate.

Important concepts:

- system prompts are stored as versioned runtime state;
- prompts are assembled per agent request from deterministic layers;
- skills are shared documents but exposed to each agent through an allowed manifest;
- agents load skill text with an approved `readSkill(skillId)` tool;
- full skill content should not be placed in the initial system prompt; use a compact manifest;
- every agent response should trace prompt versions, loaded skill versions, policies, tools, model config, and authorization basis;
- agents may propose improvements, but should not directly activate consequential behavior changes without a defined governance rule.

## Versioning pattern to preserve

Use two durable representations for governed documents:

1. an Event Sourced Entity that owns the canonical current document state and emits lifecycle events;
2. an immutable or append-only Key Value Entity snapshot per document version, populated from those events, for version history and diff views.

This pattern applies to prompts, skills, policies, evaluator rubrics, examples, and other behavior-shaping documents where version history matters.

## Closed-loop improvement pattern to preserve

Closed-loop agent improvement should follow a governed path:

```text
production agent response
→ evaluator agent analysis
→ issue/failure classification
→ improvement proposal
→ replay/simulation/evaluation
→ human approval or bounded auto-approval policy
→ activation/canary
→ monitoring
→ rollback if needed
```

Agents can draft proposals. Human governance or explicit safe automation decides activation.

## Process rules for future harness work

When working on this document set:

1. read this process context first;
2. read `01-core-seed-progression-plan.md` if it exists;
3. work on one document per task unless the user asks for a broader pass;
4. keep each document usable as input to planning/decomposition skills;
5. preserve progressive delivery and visible full-stack sprint outcomes;
6. mark deferrals explicitly instead of silently omitting hard topics;
7. do not overbuild later modules into earlier MVP documents;
8. prefer concrete acceptance scenarios and UI requirements over abstract architecture prose.
