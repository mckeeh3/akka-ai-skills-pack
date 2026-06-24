# Backlog 02: Editing agent and runtime loading

## Design notes

The editing agent is a model-backed runtime path for proposing Markdown-preserving full document content. It must fail closed when model/provider configuration is unavailable. Tests may use test providers, but normal runtime must not count deterministic/model-less output as feature completion.

Runtime agents use current docs each request: current prompt plus skill names/descriptions appended to prompt context. All agents have `readSkill` and `readReferenceDoc`; agents only know about their own listed skills/reference docs.

## Implementation areas

- `src/main/java/ai/first/application/foundation/agent/WorkstreamRuntimeAgent.java`
- `src/main/java/ai/first/application/foundation/agent/AgentRuntimeLoaderTools.java`
- `src/main/java/ai/first/application/foundation/agent/AgentRuntimeService.java`
- `src/main/java/ai/first/application/foundation/agent/AgentRuntimeTraceSink.java`
- `src/main/java/ai/first/application/coreapp/agentadmin/**`
- tests under matching `src/test/java/**`

## Task breakdown

### AADE-02-001 — Editing-agent draft/revise/save/cancel

Implement the AI-assisted edit session runtime:

- target doc context and base current version;
- initial free-form request;
- optional clarifying question;
- proposed full Markdown document;
- summary of changes;
- advisory warnings/risks;
- additional refinement instructions;
- Save and Cancel outcomes;
- audit fields for all user instructions and proposed output;
- fail-closed provider/runtime behavior.

### AADE-02-002 — Runtime doc loading and read traces

Revise runtime loading and tools:

- current prompt loaded for each agent request;
- skill names/descriptions appended to prompt context;
- `readSkill` returns selected skill content and reference doc names/descriptions;
- `readReferenceDoc` returns selected reference content;
- no cross-agent skill discovery path;
- runtime read traces include agent name, doc read, timestamp, request/session id, and user/customer context.

## Validation

Use focused tests for editing-agent behavior, fail-closed behavior, skill/reference loading, no cross-agent discovery, and trace emission.
