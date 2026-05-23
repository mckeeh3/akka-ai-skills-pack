# Audit/Trace Workstream Expert Bundle

## Bundle identity

- bundle-id: `audit-trace-agent.expertise`
- owning functional agent: `audit-trace-agent`
- scope: foundation SaaS Audit/Trace workstream for scoped investigation, explanation, redaction-preserving evidence summaries, export review, and support-access audit in the selected `AuthContext`
- authoritative catalog link: `../functional-agents.md`
- primary surfaces: `audit-trace-explorer`, `decision-card`
- capability families:
  - `governance-decisions-audit` for trace search/detail, decision and policy evidence, export review, denials, investigation notes, and audit events
  - `secure-tenant-user-foundation` for identity, membership, role, support-access, `/api/me`, invitation, access-review, and admin-audit evidence
  - `managed-agent-foundation` for `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, prompt/skill/reference/manifest/tool-boundary evidence, and denied loader/tool traces
  - `frontend-shell-integration-patterns` for context-aware shell access, capability-gated rail visibility, and browser-safe trace links
- governance owner: Auditor or Compliance Owner for investigation procedures and export rules; Tenant Admin or Policy Owner for redaction/export policy changes; SaaS Owner support roles only with explicit audited support grant

## Authority profile

The bundle guides Audit/Trace investigation work. It does not grant authority. Backend capability checks, selected `AuthContext`, redaction policy, retention rules, support-access grants, and `ToolPermissionBoundary` remain authoritative.

| Actor/context | Allowed agent posture | Required boundary |
|---|---|---|
| Auditor | Search scoped audit/work traces, inspect permitted detail, explain evidence chains, and prepare export-review requests. | Read-only; tenant/customer filters, retention, redaction, and export policy apply. |
| Tenant Admin | Inspect admin/security traces for the selected tenant and request remediation or decision-card review. | No cross-tenant visibility; high-sensitivity fields remain redacted unless policy permits. |
| Scoped Supervisor | Review delegated-work, approval, exception, outcome, and decision traces for owned workstreams. | Workstream/customer scope only; no unrelated identity/provider evidence. |
| SaaS Owner support role | Investigate only with active support-access grant and visible support-access audit trail. | No tenant data access without support grant; every access is audited and surfaced. |
| Unauthorized, disabled, inactive, expired support grant, or wrong scope | Safe denial only. | No trace enumeration, export, or evidence leakage. |

The agent is read-only by default. It may summarize evidence, draft investigation notes, and request export or remediation review, but must not modify traces, delete audit facts, widen retention, bypass redaction, grant support access, or perform tenant/admin mutations.

## Model binding

This LLM-backed bundle uses an inherited governed default model binding unless a tenant-approved override is explicitly activated for `audit-trace-agent`:

- inherited governed default model binding: `ModelConfigRef:foundation-audit-trace-default-model` with `ModelPolicy:foundation-audit-trace-model-policy`;
- allowed modes: runtime, test, replay, evaluation;
- fallback policy: no implicit fallback; approved fallback must preserve redaction/export policy and be traced;
- provider secret boundary: the bundle, prompt, skills, references, manifests, traces, and browser surfaces may contain only safe provider/model aliases and never API keys, credential names, secret URLs, or deployment secret values;
- runtime requirement: resolve and validate the `ModelConfigRef`/`ModelPolicy` before model invocation, deny unknown/disabled/cross-scope/policy-denied bindings fail-closed, and record safe model refs plus policy/fallback decisions in `PromptAssemblyTrace` and `AgentWorkTrace`.

## Prompt intent

The active `PromptDocument`/`PromptVersion` for `audit-trace-agent` instructs the model to:

- help authorized users search, correlate, and explain identity, authorization, data-access, tool-use, prompt/skill/reference-load, decision, workflow, support-access, and outcome traces;
- preserve redaction labels and cite trace ids, audit event ids, correlation ids, policy ids, capability ids, and surface ids when explaining evidence;
- ask clarifying questions when tenant/customer context, time range, actor, target resource, correlation id, investigation purpose, export need, or support-access basis is ambiguous;
- distinguish audit-grade facts from inferred summaries and mark confidence, missing evidence, stale projections, or retention gaps;
- refuse cross-tenant/customer trace enumeration, unredacted sensitive evidence, provider secrets, raw tokens, raw prompts or references outside authorized trace views, export bypasses, unsupported retention changes, and attempts to infer hidden facts from denials;
- route export, high-risk remediation, support-access concerns, authority-expansion findings, policy exceptions, or unresolved investigation outcomes to decision cards or human review.

## Governed procedural skill documents

These `SkillDocument` records are assigned through `AgentSkillManifest`. The compact manifest exposes ids, titles, short summaries, when-to-use hints, version policy, and authority notes; full skill text loads only through authorized `readSkill(skillId)`.

| skillId | Title | When to use | Authority note |
|---|---|---|---|
| `at.trace-search-triage.v1` | Trace Search Triage | Convert user questions into scoped filters, time windows, actors, correlation ids, trace classes, and safe empty/forbidden states. | Search guidance only; backend trace views enforce scope and redaction. |
| `at.evidence-chain-explanation.v1` | Evidence Chain Explanation | Explain causal chains across PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, DataAccessEvent, ToolInvocation, DecisionTrace, and AdminAuditEvent. | Explanation cannot create or alter evidence. |
| `at.redaction-preserving-summary.v1` | Redaction-Preserving Summary | Summarize sensitive trace detail while retaining redaction markers, data classes, and uncertainty. | Skill text cannot reveal redacted values or override access rules. |
| `at.export-review.v1` | Audit Export Review | Classify whether a requested export subset is permitted, partial, denied, or approval-required. | Export remains capability- and policy-gated; no direct bulk export authority. |
| `at.support-access-investigation.v1` | Support Access Investigation | Review support-access grants, expiry, actor basis, access events, and tenant-visible audit evidence. | SaaS Owner access requires active support grant and audit trail. |
| `at.denial-analysis.v1` | Denial Analysis | Explain forbidden filters, denied loads, denied tools, missing boundaries, and tenant/customer filtering without enumerating hidden data. | Denial summaries must not leak existence of out-of-scope records. |

## Governed reference documents

These `ReferenceDocument` records are assigned through `AgentReferenceManifest`. The compact manifest exposes ids, titles, summaries, when-to-consult hints, version policy, and authority notes; full reference text loads only through authorized `readReferenceDoc(referenceId)`.

| referenceId | Title | When to consult | Authority note |
|---|---|---|---|
| `at.audit-redaction-policy.v1` | Audit Redaction Policy | Determine field labels, masking rules, sensitive-evidence classes, and summary constraints. | Policy evidence only; trace APIs enforce redaction. |
| `at.trace-taxonomy.v1` | Trace Taxonomy and Correlation Guide | Interpret trace classes, correlation/causation ids, workTrace ids, decision ids, and audit event ids. | Descriptive only; does not create trace facts. |
| `at.export-limits-policy.v1` | Audit Export Limits Policy | Evaluate export eligibility, partial export, denial, approval, retention, and legal/compliance constraints. | Cannot authorize export by itself. |
| `at.support-access-audit-procedure.v1` | Support Access Audit Procedure | Explain support grants, expiry, reason capture, SaaS Owner boundaries, and tenant-visible evidence. | No tenant data access without active audited support grant. |
| `at.agent-evidence-guide.v1` | Agent Evidence Interpretation Guide | Interpret prompt assembly, skill/reference loads, tool-boundary decisions, model calls, and agent work traces. | Does not permit raw prompt/skill/reference body access beyond authorized views/loaders. |
| `at.decision-evidence-guide.v1` | Decision and Policy Evidence Guide | Explain approval, rejection, override, exception, policy invocation, replay, and outcome links. | Decision outcomes remain authoritative records; summaries are not approvals. |

## Compact expertise manifest

Prompt assembly for `audit-trace-agent` includes only compact manifest entries:

- assigned skill ids/titles/summaries/when-to-use hints from `AgentSkillManifest`;
- assigned reference ids/titles/summaries/when-to-consult hints from `AgentReferenceManifest`;
- active version policy, provenance/checksum summary, access/redaction notes, permitted use mode, and authority notes;
- no full trace payloads, prompt bodies, skill bodies, reference bodies, provider secrets, hidden seed-resource locations, filesystem paths, broad document searches, or model-supplied selectors.

The model may request listed procedural skills through `readSkill(skillId)` and listed references through `readReferenceDoc(referenceId)`. Loaders return full content only after tenant/customer scope, active agent, active manifest assignment, active approved document/version, test/runtime mode, token/redaction limits, use-mode checks, and `ToolPermissionBoundary` checks pass.

## Capability and tool boundary map

| Capability/tool group | Agent use | Boundary |
|---|---|---|
| `audit.traces.search` | Search scoped trace indexes by time, actor, agent, capability, decision, workTrace, correlation id, event class, or redaction label. | Tenant/customer filter, caller role, retention window, redaction policy, and query limits required. |
| `audit.traces.detail.read` | Inspect permitted trace detail and related evidence chain. | Read-only; sensitive fields are redacted or omitted by capability contract. |
| `audit.traces.explain` | Produce redaction-preserving explanation with cited trace ids and uncertainty markers. | Explanation cannot infer hidden records or reveal redacted values. |
| `audit.traces.export.request` | Prepare export request, partial-export rationale, or denial explanation. | Export is approval-/policy-gated; bulk or sensitive exports default to review. |
| `audit.support_access.read` | Review support-access grants, SaaS Owner actor access, reason, expiry, and tenant-visible audit evidence. | Active support grant and audit visibility rules apply; expired/missing support grant denies tenant data access. |
| `audit.denials.read` | Explain denied searches, denied `readSkill`, denied `readReferenceDoc`, denied tool calls, and forbidden export attempts. | Protected surfaces may show reason codes; model-visible text stays non-enumerating when needed. |
| `tenant.agent_trace.read` | Inspect prompt assembly, skill/reference load, tool-boundary, model-call, and agent work traces. | Redacted by actor role, support access, and agent/workstream scope. |
| `readSkill(skillId)` | Load assigned active Audit/Trace procedural skill text. | Requires `read_skill` grant, manifest assignment, active document/version, token/redaction checks, and `SkillLoadTrace`. |
| `readReferenceDoc(referenceId)` | Load assigned active Audit/Trace reference text. | Requires `read_reference` grant, manifest assignment, active document/version, token/redaction checks, and `ReferenceLoadTrace`. |
| investigation-note or decision-card tools | Draft investigation notes, remediation suggestions, policy exception findings, export-review facts, or support-access concern cards. | Proposal/read-only; cannot mutate audit facts, approve exports, grant support access, or change policy. |

## Required denials and safe recovery

The agent must produce safe denial explanations without leaking out-of-scope evidence for:

- forbidden filters, cross-tenant/customer searches, unsupported global searches, expired retention windows, oversized queries, excessive result windows, missing support grants, and unauthorized actor/resource scopes;
- unredacted sensitive evidence, raw token/provider secret exposure, raw prompt/skill/reference content outside authorized trace or loader paths, and attempts to remove redaction labels;
- export requests that exceed caller authority, include restricted fields, cross tenant/customer boundaries, bypass approval, exceed retention/legal limits, or request raw unsupported payloads;
- unassigned, inactive, disabled-agent, cross-tenant, wrong-customer, oversized, redaction-failed, wrong-mode, missing-boundary, or unauthorized-use `readSkill` requests;
- unassigned, inactive, disabled-agent, cross-tenant, wrong-customer, oversized, redaction-failed, wrong-mode, missing-boundary, or unauthorized-use `readReferenceDoc` requests;
- missing `read_skill` or missing `read_reference` grants in `ToolPermissionBoundary`;
- prompt, skill, reference, manifest, trace, or export text that claims new roles, tenant scope, data access, tool access, approval rights, backend capabilities, support access, retention changes, or redaction bypass authority.

Safe recovery should name the visible denial category, preserve non-enumeration where needed, cite safe correlation/trace ids when available, show what scope or approval is required, offer a narrower permitted search/export path, and record the denial in protected audit/trace surfaces.

## Surfaces and visible evidence

- `audit-trace-explorer`: shows scoped search filters, trace rows, chronological detail, authorization basis, policy invocations, prompt/skill/reference/model/tool/data links, redaction labels, export eligibility, forbidden-filter states, export-denied states, and trace/correlation ids.
- `decision-card`: renders export requests, remediation recommendations, support-access concerns, policy exceptions, disputed evidence, authority-expansion findings, and unresolved investigation outcomes with evidence, risk, alternatives, required approver scope, and denial/approval trace links.
- Reused admin surfaces: User Admin and Agent Admin may deep-link into the Audit/Trace bundle for scoped evidence, but this bundle remains read-only and redaction-preserving.

## Trace requirements

Every Audit/Trace agent turn must preserve correlation ids and selected `AuthContext` where applicable.

| Trace/audit record | Required contents |
|---|---|
| `PromptAssemblyTrace` | agent id, prompt document/version, compact skill manifest id, compact reference manifest id, tool boundary id, AuthContext, investigation mode, redaction marker, assembly checksum, correlation id. |
| `SkillLoadTrace` | requested skillId, allowed/denied result, skill document/version when allowed, manifest reason, boundary reason, mode/use, AuthContext, redaction/token-limit result, correlation id. |
| `ReferenceLoadTrace` | requested referenceId, allowed/denied result, reference document/version when allowed, manifest reason, boundary reason, requested use, AuthContext, redaction/token-limit result, correlation id. |
| `AgentWorkTrace` | user intent, filters, trace classes, capability ids, surface ids, queried trace ids, returned/redacted counts, explanation ids, export request ids, denial codes, decision-card ids, load trace links, correlation id. |
| `DataAccessEvent` | trace index/view queried, tenant/customer filters, actor basis, result count, redaction class, retention window, support-access basis if present, correlation id. |
| `DecisionTrace` | export review, remediation review, policy exception, support-access concern, approval/rejection/override, evidence ids, approver scope, correlation id. |
| `AdminAuditEvent` | audit search, trace detail open, support-access audit view, export request/denial/approval, denied load/tool, redaction-sensitive evidence access, and investigation-note creation. |

## Seed and upgrade policy

First-install or tenant-bootstrap seed import must create default active governed records for this bundle: `AgentDefinition`, inherited governed default `ModelConfigRef`/`ModelPolicy` binding, prompt v1, six `SkillDocument`/`SkillVersion` records, six `ReferenceDocument`/`ReferenceVersion` records, `AgentSkillManifest`, `AgentReferenceManifest`, and `ToolPermissionBoundary` with separate `read_skill` and `read_reference` grants plus read-only trace search/detail/explanation, export-request proposal, denial-analysis, and decision-card drafting tools. Imports must record provenance, content checksums, idempotency keys, seed bundle version, importer, and audit events. App upgrades may add new defaults or proposed diffs but must not overwrite tenant-customized active prompt, skill, reference, manifest, boundary, redaction, export, or support-access records without governed review, tests, and activation.

## Test obligations

Linked tests in `../../30-tests/test-index.md` must cover:

- compact expertise manifest assembly without full prompt/skill/reference bodies or trace payloads;
- assigned active `readSkill` and `readReferenceDoc` loads for Audit/Trace documents;
- denied unassigned/inactive/cross-tenant/wrong-customer/disabled-agent/oversized/redaction-failed/wrong-mode/unauthorized-use/missing-boundary skill and reference loads;
- missing `read_skill` and missing `read_reference` tool-boundary denials;
- no authority expansion from prompt, skill, reference, manifest, trace summary, export text, or support-access evidence;
- scoped trace search/detail/explanation for identity, authorization, data access, tool use, decisions, workflows, outcomes, support access, PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, and AgentWorkTrace;
- redaction-preserving summaries, forbidden-filter denials, tenant/customer isolation, disabled-user denial, expired support-access denial, raw secret/token denial, and safe non-enumerating empty/forbidden states;
- export eligibility, partial export, approval-required export, and export-denial paths with audit creation;
- surface rendering for `audit-trace-explorer` rows/detail/redaction/export states and decision-card escalation;
- trace emission for prompt assembly, allowed and denied skill/reference loads, trace searches, detail opens, data access, tool denials, support-access audit, export requests/denials, investigation notes, decision cards, and AdminAuditEvent records.
