# Workstream Expertise Model

## Status and scope

This is the canonical doctrine for making a user-facing functional agent an expert in its workstream. It sits below `agent-workstream-application-architecture.md` and `capability-first-backend-architecture.md`, and above runtime agent, skill-governance, app-description, seed, and testing guidance.

A functional agent is not ready merely because it has a prompt, a chat surface, or a list of tools. It is workstream-ready only when its expertise is explicit, governed, loadable, bounded, traceable, and tested.

```text
functional workstream
→ role-specific dashboards and surface graph
→ workstream expert bundle
→ governed prompt, model binding, skills, and reference documents
→ compact per-agent expertise manifest
→ authorized readSkill/readReference loading
→ governed-tool and tool-boundary enforcement
→ denials, user help, traces, review surfaces, and tests
```

## Definition: workstream expert bundle

A **workstream expert bundle** is the app-defined, tenant-governed set of behavior, knowledge, capabilities, boundaries, and evidence that makes one functional agent competent for one workstream.

Define one bundle per functional agent/workstream pair unless two workstreams intentionally share the same authority, lifecycle, steward, prompt, skills, references, tools, surfaces, traces, and tests.

A bundle must name:

| Field | Required meaning |
|---|---|
| Bundle id | Stable id such as `user-admin-agent.expertise` tied to a functional agent id. |
| Functional agent | Owning left-rail/workstream agent and whether it is foundation or domain-specific. |
| Scope | Tenant/customer/AuthContext assumptions, supported roles, and workstream responsibilities. |
| Prompt intent | What the agent helps with, what it refuses, when it asks clarifying questions, and when it escalates. |
| Model binding | Explicit `ModelConfigRef` plus `ModelPolicy`, or an explicit inherited governed default model binding; include allowed modes, fallback/no-fallback policy, provider secret boundary, and model-use trace requirements. |
| Skill documents | Procedural guidance the model may load for how to perform workstream tasks. |
| Reference documents | Durable policy/process/product/domain facts the model may cite or consult; not procedural skill instructions by default. |
| Expertise manifest | Compact per-agent manifest entries for available skills and references, assembled into prompt context without full document bodies. |
| Dashboard and surface graph | Role-specific dashboard purpose, attention categories, surface graph nodes/edges, system-message result surfaces, and prompt/surface-request shortcuts the agent should understand. |
| Capability map | Governed backend capabilities the workstream may request, propose, or call. |
| Governed-tool map | Governed-tools inside those capabilities, with exposure as browser-tools, agent-tools, workflow-tools, timer-tools, consumer-tools, MCP-tools, or internal-tools. |
| Tool boundary | ToolPermissionBoundary and model-facing agent-tools allowed for this agent, including `readSkill` and any reference loaders. |
| Authority profile | Read-only, proposal-only, approval-gated, or bounded autonomous authority per capability/tool. |
| Surfaces | Structured surfaces the agent owns or reuses to show evidence, forms, decisions, diffs, traces, and outcomes. |
| Escalation and denial rules | Approval, exception, denial, safe recovery, user-help wording, and handoff behavior. |
| Trace requirements | PromptAssemblyTrace, SkillLoadTrace, reference-load trace, AgentWorkTrace, data-access, decision, and audit events. |
| Tests | Authorization, tenant isolation, assigned/unassigned loads, denied loads, tool-boundary denial, capability behavior, surface rendering, and trace emission. |
| Governance owner | Steward/reviewer role responsible for approving prompt, skill, reference, manifest, and boundary changes. |
| Seed/upgrade policy | Initial default content, provenance/checksums, idempotent import, and customization-preserving upgrade behavior. |

## Distinctions

### Skill document

A skill document is governed procedural guidance for the model: how to reason, classify, draft, review, or decide within the workstream. It is represented by `SkillDocument`/`SkillVersion` and assigned through `AgentSkillManifest`.

Examples for User Admin:

- `access-review-triage` — how to evaluate stale or risky memberships.
- `invitation-drafting` — how to draft safe invite explanations.
- `role-recommendation` — how to recommend roles without granting them.

Skill text is guidance only. It cannot grant data access, tool authority, role permissions, approval rights, or tenant scope.

### Reference document

A reference document is durable workstream knowledge: policies, process manuals, domain rules, checklists, compliance notes, product configuration facts, or customer-specific operating procedures. References may be governed separately from skills because they often contain factual/process knowledge rather than procedural model behavior.

Until a first-class reference-document model is implemented, a project may represent references using governed document records or constrained `SkillDocument` records with `documentKind: reference`, but it must preserve the distinction in manifests, loaders, traces, and tests.

Examples for User Admin:

- current access review policy;
- support-access operating procedure;
- tenant role catalog;
- invitation and offboarding checklist.

Reference content is also guidance/evidence only. Backend authorization and capability contracts remain authoritative.

### Capability

A capability is the backend operation or query contract. It defines actors/callers, AuthContext, schemas, validation, idempotency, side effects, approval, audit, exposure surfaces, and tests. A workstream expert bundle lists which capabilities the agent can request, propose, or call, but the capability itself remains owned by the capability layer.

### Governed-tool and tool boundary

A governed-tool is the semantic executable operation or query inside a capability. It defines actor/caller rules, AuthContext, input/output schemas, side effects, idempotency, approval/policy, audit/work trace, and implementation mapping. A capability groups related governed-tools; exposure channels decide where each governed-tool can be invoked.

Use qualified exposure names:

- `browser-tool` for structured surface actions and browser APIs;
- `agent-tool` for model-facing `@FunctionTool`, component-tool, MCP, or facade exposures;
- `internal-tool`, `workflow-tool`, `timer-tool`, or `consumer-tool` for backend-only execution paths;
- `MCP-tool` for remote LLM-facing boundaries.

`readSkill(skillId)` and `readReferenceDoc(referenceId)` are governed loader agent-tools for loading approved guidance/evidence into a model turn. Other agent-tools may query evidence, draft proposals, start workflows, or request side effects only when the capability, governed-tool, and `ToolPermissionBoundary` allow it.

`ToolPermissionBoundary` is authoritative for what the agent may invoke. Prompt text, skill text, reference text, manifest labels, and UI rail visibility cannot expand governed-tool authority.

### Surface

A surface is a typed workstream artifact for users: dashboard, table, form, decision card, diff, audit timeline, policy card, workflow status, or `markdown_response`. Expert bundles name the surfaces needed to render the agent's work and supervision state, but surface contracts remain under the workstream/surface app-description layer.

## Runtime loading overview

Runtime invocation should follow this shape:

```text
resolve AuthContext and active AgentDefinition
→ resolve approved active PromptVersion
→ resolve explicit ModelConfigRef/ModelPolicy or inherited governed default model binding
→ resolve active workstream expert bundle and compact expertise manifest
→ assemble prompt with compact skill/reference entries only
→ register readSkill/readReferenceDoc and other allowed tools
→ model requests skill/reference ids from the compact manifest
→ loader authorizes tenant, agent, manifest, document status, version, mode, token/redaction limits, and ToolPermissionBoundary
→ allowed loads return full content with checksum and authority note
→ denied loads return safe denial
→ emit load traces and AgentWorkTrace
```

The model must not receive all skill/reference bodies by default. Full content is loaded on demand through authorized tools using stable ids, never filesystem paths or model-supplied resource paths.

## Governance rules

1. Every functional agent with LLM behavior needs an explicit workstream expert bundle or an explicit deferral that prevents readiness from being claimed.
2. Expertise artifacts are tenant-scoped governed behavior/knowledge records, not static prompt-only text.
3. Seeded default prompts, model config refs/policies, skills, references, manifests, and boundaries are imported into governed storage with provenance, checksums, idempotency, audit, and customization-preserving upgrade behavior.
4. Every LLM-backed bundle must name either a specific active `ModelConfigRef`/`ModelPolicy` pair or an inherited governed default model binding; missing or implicit model selection blocks readiness unless explicitly deferred with scope impact.
5. Provider secrets are never bundle content: bundles may reference safe provider aliases only, while deployment/runtime configuration resolves credentials outside prompts, skills, references, traces, browser payloads, and app-description examples.
6. Manifest changes, tool-boundary changes, and model-binding/model-policy changes are governance-impacting and must be reviewed, approved, audited, and tested.
7. Reference documents may use a first-class reference model or a constrained interim governed-document representation, but they must remain distinguishable from procedural skills.
8. A human steward or approved governance workflow owns activation of high-impact expertise changes.
9. No expertise text can override platform policy, backend authorization, tenant isolation, approval requirements, or ToolPermissionBoundary enforcement.

## App-description ownership

In an app-description tree, workstream expertise belongs under `12-workstreams/` because it defines functional-agent application meaning:

```text
app-description/12-workstreams/
  functional-agents.md
  workstream-expertise/
    README.md
    <functional-agent-id>.md
```

Each `<functional-agent-id>.md` should capture the bundle contract: role-specific dashboard semantics, surface graph help, prompt intent, model binding, skills, references, manifests, capability map, governed-tool map, tool boundary, surfaces, denials/user help, traces, governance owner, seed policy, and tests. Cross-layer files should link to it rather than redefining it:

- `10-capabilities/**` owns detailed capability contracts.
- `15-operating-model/**` owns governed runtime agent behavior and lifecycle rules.
- `20-behavior/**` owns business behavior and acceptance semantics.
- `25-auth-security/**` owns roles, scopes, tenant isolation, and authorization policy.
- `30-tests/**` owns test cases and coverage status.
- `40-observability/**` owns audit and trace schemas.
- `55-ui/**` owns browser realization of manifests, surfaces, trace links, and governance screens.
- `70-traceability/**` maps functional agents, expertise artifacts, surfaces, capabilities, tests, and observability.

## Readiness checklist

A functional agent is not expertise-ready until:

- [ ] a workstream expert bundle exists or the missing bundle is explicitly deferred with scope impact;
- [ ] prompt intent, governed prompt refs, model binding, skills, references, and compact manifest entries are listed;
- [ ] model binding names an explicit `ModelConfigRef`/`ModelPolicy` pair or an explicit inherited governed default, including allowed modes, fallback/no-fallback behavior, provider secret boundary, and model-use trace facts;
- [ ] references are distinguished from procedural skills;
- [ ] role-specific dashboard purpose, attention categories, and surface graph help are specified;
- [ ] assigned capabilities, governed-tools, and exposure channels are mapped;
- [ ] `ToolPermissionBoundary` covers loaders and all model-facing agent-tools;
- [ ] denied unassigned skill/reference loads are specified;
- [ ] SkillLoadTrace/reference-load trace and AgentWorkTrace obligations are specified;
- [ ] surfaces expose manifest, evidence, denials, decisions, traces, or governance state where needed;
- [ ] seed/import and customization-preserving upgrade behavior are defined for default content;
- [ ] tests cover assigned loads, unassigned denied loads, tool-boundary denial, capability auth, no authority expansion from text, surface rendering, tenant isolation, and audit/trace emission.

## Test expectations

Workstream expertise tests should verify:

- compact manifest assembly includes only assigned skill/reference ids and hints, not full bodies;
- `readSkill(skillId)` allows assigned active skills and denies unassigned, inactive, cross-tenant, disabled-agent, oversized, or unauthorized mode loads;
- reference loading follows the same authorization, redaction, denial, and trace pattern;
- skill/reference text cannot grant a forbidden governed-tool, capability, role, tenant scope, or approval right;
- side-effecting capabilities remain proposal/approval-gated unless policy grants bounded autonomy;
- surfaces render manifest summaries, decisions, evidence, denials, and trace links safely;
- traces are emitted for prompt assembly, model binding resolution/denial/fallback, allowed loads, denied loads, tool invocations, data access, decisions, and consequential work.

## Routing implications

- Use `app-description-functional-agent-modeling` when adding or revising a functional agent's expert bundle in app-description artifacts.
- Use `akka-agent-model-governance` for governed `ModelConfigRef`, `ModelPolicy`, explicit fallback/no-fallback behavior, provider secret boundaries, and model-use trace facts.
- Use `akka-agent-skill-governance` for governed `SkillDocument`, `SkillVersion`, `AgentSkillManifest`, `readSkill`, and `SkillLoadTrace` implementation guidance.
- Use `akka-agent-reference-governance` for `ReferenceDocument`, `ReferenceVersion`, `AgentReferenceManifest`, `readReferenceDoc(referenceId)`, denied-load semantics, and ReferenceLoadTrace; do not silently collapse references into generic prompts or procedural skills.
- Use `akka-agent-tool-boundaries` for enforcing loader/tool permissions.
- Use `akka-agent-seed-documents` for default prompt/skill/reference/manifest/boundary seed import.
- Use `akka-agent-testing` and `akka-agent-work-trace` for expertise runtime and trace tests.
