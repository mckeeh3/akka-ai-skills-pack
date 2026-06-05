# Persistent Discussion Capture

This document captures additional ideas from the planning discussion that should remain available while the detailed input documents are written.

## Skills for Akka agents

Akka agents do not currently have harness-native skills, but AI-first apps benefit from an equivalent runtime capability.

Recommended pattern:

- store skills as governed documents;
- expose only a compact skill manifest to each agent;
- assign each agent a specific allowlist of skill ids;
- provide a `readSkill(skillId)` tool;
- return skill text as model-visible tool-result context;
- instruct the agent system prompt to call the tool when a request matches an available skill;
- audit every skill load with skill id, version, agent id, session/workflow/goal id, tenant/customer id, and correlation id.

Skill tool results are not true system-priority instructions. The agent prompt must say that returned skills are trusted internal guidance for the current turn, while platform/security policy remains higher authority.

## Runtime-managed agent prompts

Each agent's system prompt should be runtime-managed as a governed document where appropriate.

For each request, the app should retrieve and assemble an effective agent context from deterministic layers such as:

1. platform safety and non-negotiable instructions;
2. tenant/org policies;
3. agent role/persona prompt;
4. task-specific operating instructions;
5. compact available skill manifest;
6. tool-use rules;
7. output format rules;
8. authorized user/session/context data.

The assembly result should be traceable with prompt versions, skill manifest versions, policy versions, model config, tool permission version, timestamp, and checksum.

## Governed behavior documents

Prompts, skills, policies, examples, evaluator rubrics, and tool-use guidance should be treated as behavior-shaping runtime artifacts.

They should be:

- durable;
- versioned;
- assignable to agents;
- auditable;
- permission-controlled;
- reviewable through UI;
- testable through replay/evaluation;
- rolled out through explicit activation/canary/rollback states where needed.

## Two-entity versioning pattern

Use an Event Sourced Entity for the canonical current document and a Key Value Entity for immutable version snapshots.

```text
BehaviorDocumentEntity(docId)
- owns current state
- emits lifecycle/change events

BehaviorDocumentVersionEntity(docId:version)
- stores version snapshot
- supports history and diff UI
- populated by consumer from document events
```

## Closed-loop improvement

Self-improving agents should not directly mutate active behavior.

Use:

```text
production response
→ evaluator analysis
→ improvement proposal
→ replay/simulation/evaluation
→ approval decision
→ activation/canary
→ monitoring
→ rollback if needed
```

Agents may draft proposals. Human governance or explicitly bounded safe automation approves activation.

## Persistent questions to resolve in detailed docs

- Which WorkOS/AuthKit runtime settings and local/dev/test token behavior are required versus production?
- What is the exact minimal first-login/account-linking flow for Module 1?
- Which user-admin features belong in Module 2 versus later hardening?
- What is the smallest useful agent test console for Module 4?
- Which behavior document types are in seed scope versus deferred?
- What auto-approval boundaries, if any, are acceptable for closed-loop improvement?
- What trace payloads must be redacted or summarized by default?
