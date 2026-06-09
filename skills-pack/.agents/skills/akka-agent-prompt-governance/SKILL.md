---
name: akka-agent-prompt-governance
description: Implement governed runtime-managed agent system prompts with PromptDocument, PromptVersion, review, activation, diff/history UI, effective prompt assembly, PromptAssemblyTrace, and safe test consoles. Use when prompts need tenant-scoped governance beyond built-in PromptTemplate.
---

# Akka Agent Prompt Governance

Use this skill when agent prompts are behavior-shaping SaaS artifacts that require tenant-scoped lifecycle, review, approval, activation, version history, diff UI, audit, or runtime assembly traces.

This is the focused prompt companion to `akka-agent-governed-documents`. It does not replace `akka-agent-component` for writing the Java `Agent` class or `akka-agent-runtime-state` for simple built-in `PromptTemplate` usage.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the required capability, AuthContext/scope, DTO, side-effect, trace, and test inputs are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Required reading

Read these first if present:
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/governed-agent-substrate.md`
- `../docs/agent-coverage-matrix.md`
- `../docs/examples/ai-first-saas-core-app-domain/agent-admin-workstream/README.md`
- `../core-saas-foundation/SKILL.md`
- `../akka-agents/SKILL.md`
- `../akka-agent-behavior-profiles/SKILL.md`
- `../akka-agent-governed-documents/SKILL.md`
- `../akka-agent-runtime-state/SKILL.md`
- `../ai-first-saas-audit-trace/SKILL.md`
- `../akka-event-sourced-entities/SKILL.md`
- `../akka-key-value-entities/SKILL.md`
- `../akka-consumers/SKILL.md`
- `../akka-views/SKILL.md`
- `../akka-http-endpoints/SKILL.md`

## Use when the request mentions

- runtime-managed system prompts that need governance
- implementation-developed default system prompts that must exist as initial governed prompt versions at install or tenant bootstrap
- prompt editor, prompt review, prompt approval, or prompt activation
- prompt version history, prompt diff, rollback, or deprecation
- active prompt version for an `AgentDefinition`
- effective prompt assembly for each request
- prompt assembly trace, prompt checksum, or prompt version pinning
- prompt test console using active or draft prompt content
- agent-mediated prompt maintenance, an `AgentBehaviorEditorAgent`, proposed prompt diff, or draft prompt version proposal

## PromptTemplate vs governed PromptDocument

Use built-in Akka `PromptTemplate` through `akka-agent-runtime-state` when:
- prompt text only needs simple runtime editing;
- no review/approval workflow is required;
- version history and diff UI are not required;
- tenant-scoped governance and activation semantics are intentionally out of scope.

Use governed `PromptDocument` / `PromptVersion` when:
- prompt changes affect business behavior, authority, compliance, or auditability;
- admins or stewards need draft/review/approved/active lifecycle;
- historical versions, diffs, rollback, or activation evidence are required;
- the app must trace which prompt version shaped an agent response.

## Core model

```text
PromptDocument
- tenantId
- promptDocumentId
- agentDefinitionId
- promptType: system | role | task | output_format
- lifecycleStatus: draft | in_review | approved | active | deprecated | archived
- currentDraftContent
- activeVersion
- latestVersion
- ownerAccountId / stewardRole
- review metadata
- activation metadata
```

```text
PromptVersion
- tenantId
- promptDocumentId
- version
- agentDefinitionId
- contentBody or contentRef
- contentChecksum
- status at snapshot
- changeSummary
- createdBy / createdAt
- approvedBy / approvedAt
- activatedBy / activatedAt
```

```text
PromptAssemblyTrace
- tenantId
- correlationId / assemblyId
- agentDefinitionId
- promptDocumentId / version
- modelConfigRef
- skillManifestRef when present
- policy/tool refs when present
- assembledPromptChecksum
- caller account id or runtime actor
- mode: test | runtime | replay | evaluation
- timestamp
```

## Akka component mapping

Prefer:
- `PromptDocumentEntity` as an Event Sourced Entity for lifecycle, edit, review, approval, activation, rollback, and archive commands.
- `PromptVersionEntity` as an immutable Key Value Entity snapshot populated from prompt document events.
- Consumer to materialize prompt versions, emit audit/work trace events, and update active prompt lookup views.
- Views for prompt overview, active prompt lookup, version history, review queue, and diff inputs.
- HTTP endpoints and web UI for protected prompt editor, review, diff, activation, history, and test console.
- Trace/audit records for prompt edits, approvals, activations, assembly, test runs, denials, and rollback.

## Command and event checklist

Typical commands:
- create prompt document for an agent definition
- edit draft prompt content and metadata
- submit prompt version for review
- approve or reject prompt version
- activate approved prompt version
- deprecate old prompt version
- rollback to prior approved version
- archive prompt document
- assemble prompt for test/runtime/replay

Typical events:
- `PromptDocumentCreated`
- `PromptDraftEdited`
- `PromptVersionSubmitted`
- `PromptVersionApproved`
- `PromptVersionRejected`
- `PromptVersionActivated`
- `PromptVersionDeprecated`
- `PromptRolledBack`
- `PromptDocumentArchived`
- `PromptAssembled`
- `PromptTestRunStarted`

## Effective prompt assembly

Assemble prompts deterministically from explicit layers. Keep the assembly contract small and traceable:

```text
1. platform and app non-negotiable instructions
2. tenant/org policy summary or references
3. active agent role/persona prompt version
4. task-specific operating instructions
5. compact available skill manifest, not full skill text
6. tool-use and data-access rules
7. output format rules
8. authorized caller/session/context data
```

Rules:
- resolve the active `AgentDefinition` first;
- reject disabled, archived, unauthorized, or cross-tenant agents;
- use active approved prompt versions for runtime mode;
- allow draft/unapproved versions only in authorized test/replay modes;
- capture prompt version ids and checksum in `PromptAssemblyTrace`;
- do not include raw secrets, provider tokens, or frontend-only trust claims;
- include full skill text only after explicit `readSkill(skillId)` tool calls, not during initial assembly.

## Initial default prompt versions

When an app ships with implementation-developed default system prompts, create them as governed `PromptDocument`/`PromptVersion` records during first install or tenant bootstrap, or include them in the app-description backlog for explicit implementation.

Rules:
- default prompt content is defined by implementation or app-description artifacts and validated before activation;
- default setup creates the first approved/active prompt version only under an accepted governance policy;
- every default prompt version stores provenance, checksum or content hash, app/content version, actor, timestamp, and correlation id;
- default setup is idempotent and safe to retry when implemented as a bootstrap path;
- app upgrades must not overwrite tenant-customized active prompts; create a proposed draft/diff when tenant content diverges from the prior governed baseline;
- runtime assembly reads active governed `PromptVersion` records, never filesystem defaults directly.

## Agent-mediated prompt maintenance

Generated AI-first SaaS foundations default to prompt maintenance through an `AgentBehaviorEditorAgent` or equivalent editing-agent responsibility.

Normal flow:

```text
human prompt-change request
→ editing agent identifies affected AgentDefinition and PromptDocument records
→ drafts proposed diff, rationale, risk/impact notes, and test prompts
→ creates a draft PromptVersion or proposal, never directly mutating active runtime prompt text
→ routes to prompt review/approval or a decision card when authority, tool use, compliance, or risk changes
→ approved activation emits audit events and PromptAssemblyTrace-visible version references
```

Direct human text editing may be offered only as an explicitly authorized admin surface. It must still create draft versions, show proposed diff/review state, require approval for activation, and preserve audit; it cannot bypass the agent-mediated readiness expectations for generated foundations.

Editing-agent proposals must be denied or escalated when prompt text attempts to expand data access, tool permissions, role capabilities, approval authority, tenant scope, or hidden platform policy. Prompt text remains behavior guidance only.

For implementation details, load `akka-agent-behavior-editing` to define the AgentBehaviorEditorAgent structured prompt-change proposal, proposed diff, risk classification, draft version creation, decision card routing, and authority expansion denial behavior.

## Prompt validation and safety

Validate before review or activation:
- required purpose/responsibility text is present;
- known template variables only;
- no provider secrets, JWTs, API keys, invite tokens, or private credentials;
- no instruction that claims to bypass backend authorization;
- no instruction that expands tool/data authority beyond `AgentDefinition` boundaries;
- token estimate is within configured limits;
- output format instructions do not conflict with downstream structured-response expectations.

## Admin UI surfaces

Provide protected UI for:
- prompt overview from agent detail;
- create prompt document;
- editor with validation, variable hints, and secret-like content warnings;
- review queue and approve/reject actions with rationale;
- side-by-side version diff;
- active version display and rollback controls;
- minimal prompt test console;
- prompt assembly/audit trace links;
- editing-agent proposal review with proposed diff, rationale, risk flags, affected prompt documents, and approval/denial actions.

## Test console rules

A prompt test console should:
- require `prompts.test` or equivalent capability;
- run only against selected tenant-scoped agent definitions;
- refuse disabled or archived agents;
- clearly label draft/test mode;
- trace prompt assembly and test run;
- avoid real external side effects unless explicitly enabled and authorized;
- never expose provider secrets or hidden platform instructions to the browser.

## Review checklist

Before finishing, verify:
- prompt documents are tenant-scoped through their owning `AgentDefinition`
- backend authorization protects every prompt command/query/test action
- prompt versions are immutable after approval/activation
- active runtime prompt lookup returns only approved active versions
- draft prompt use is limited to authorized test/replay modes
- prompt diffs and history are authorization-protected
- prompt assembly is deterministic and traceable
- skill manifests are compact references, not full skill text
- prompt text cannot grant data/tool permissions by itself
- prompt lifecycle, assembly, test actions, and first-install governed default setups emit audit/work trace events
- default prompts exist as governed records before runtime use and upgrades preserve tenant-customized active prompt versions
- AgentBehaviorEditorAgent prompt proposals, draft version creation, review/approval, activation, and rejection paths are tested
- unauthorized authority expansion through prompt edits is denied, audited, and routed to review/approval when appropriate
