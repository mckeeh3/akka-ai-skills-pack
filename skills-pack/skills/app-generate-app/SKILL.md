---
name: app-generate-app
description: Realize the current app description as maintained code, tests, and runnable outputs while preserving description primacy, making implementation/generation scope explicit, and reporting what changed, executed, and remains uncertain.
---

# App Generate App

Use this skill when the user explicitly asks to realize the current application description in runnable outputs, or when the harness has assessed the description as ready and implementation/generation has been accepted.

This skill exists in a **description-first operating model**.
It treats code, tests, and runnable assets as maintained realizations of the authoritative app description.
It does not redefine the app, and it does not replace an existing core app unless a destructive reset is explicit.

## Goal

Consume the current app description and realize application outputs that are suitable for implementation evaluation, test execution, and app execution.

The skill should:
- preserve description primacy
- extend or repair code and tests from the current description
- choose localized extension/repair by default for existing repositories and use broad regeneration only when explicitly scoped
- make assumptions and implementation/generation scope explicit
- support downstream running, testing, and manual evaluation
- make clear whether the generated app was actually run locally and what visible/API/workstream behavior was validated
- report clearly what changed and what remains uncertain

## Required reading

Read these first if present:
- `../../../AGENTS.md`
- `../README.md`
- `../docs/description-first-application-doctrine.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../core-saas-foundation/SKILL.md` for mandatory secure SaaS foundation readiness before generation
- `../docs/internal-app-description-architecture.md`
- `../docs/app-description-maintenance-flow.md`
- `../app-description-intake-router/SKILL.md`
- `../app-description-readiness-assessment/SKILL.md`
- `../ai-first-saas/SKILL.md` when delegated work, agents, decisions, governance, supervision, audit, or outcomes are in scope
- any currently relevant app-description layer artifacts identified by the harness, including `15-operating-model/` when present or required
- `../docs/web-ui-style-guide.md` and `app-description/55-ui/style-guide.md` for mandatory generated full-stack SaaS web UI

If the user asks to generate and run, read the current build and execution entry points for the target project before acting.

## Use this skill when

The input sounds like:
- "generate the app"
- "generate the code"
- "regenerate from the current description"
- "extend the app from the current description"
- "repair the implementation to match the description"
- "ok, now generate the code and run the app"
- "realize the current description"

Use it only after the harness has either:
- determined the description is `ready`, or
- determined it is `ready-with-assumptions` for a named narrower scope whose assumptions are accepted, non-runtime, and do not affect backend behavior, API contracts, auth/security, tenant isolation, agent/provider binding, UI action wiring, audit/work traces, tests, or local validation

## Core operating rule

Generation or implementation is a realization step, not the source-of-truth step.

If realization reveals a semantic gap, the fix belongs in the app description. If it reveals stale or incomplete implementation, repair the maintained runnable code and tests without inventing semantics.
For every generated SaaS app, never invent missing secure foundation semantics during generation: Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, complete Invitation lifecycle, AuthContext, `/api/me`, backend authorization, audit, support-access, billing boundary, admin surfaces, AI-assisted admin offload, governed runtime agent foundation (`AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `ReferenceDocument`/`ReferenceVersion`, `AgentSkillManifest`, `AgentReferenceManifest`, `ToolPermissionBoundary`, first-install/tenant-bootstrap governed default behavior/reference setup, deterministic prompt assembly, authorized `readSkill(skillId)`, authorized `readReferenceDoc(referenceId)`, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`), agent governance UI, tenant-isolation, disabled-user, forbidden-access, role/scope-denial, and frontend secret-boundary tests must already be described or generation must stop/mark not-ready.
For AI-first/delegated operations, never invent missing authority, policy, approval, decision, evidence, trace, outcome, or supervision semantics during generation. Also never invent role-specific dashboard attention, human surface graph edges, internal workstream agent graph delegation, workstream expertise, governed-tool contracts, or browser-tool/agent-tool/internal-tool exposure mappings during generation; return to description maintenance or narrow the scope instead.

Minimum-core app generation is a named narrower scope, not a full-core shortcut. If the user asks for a minimum/core app/basic/chatbot-like generated SaaS app, the generation basis must be labeled `core app baseline`, use `docs/minimum-ai-first-saas-app.md`, and realize the five core workstream core app domain — My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy — with bootstrap authorization, selected AuthContext, bounded functional agents, durable workstream logs, `markdown_response` surfaces, backend capability boundaries, governed managed-agent/runtime boundary with explicit model/provider fail-closed behavior, audit/work trace substrate, markdown sanitization, and core app tests. Emit follow-up tasks for full User Admin, Agent Admin, Audit/Trace UI/search, Governance/Policy depth, invitation/onboarding, governed prompt/skill/reference/manifest/tool-boundary management, support access, billing boundary, and full security coverage. Do not describe this output as full-core ready or app-specific ready; app-specific readiness requires full core plus product/domain workstreams, capabilities, surfaces, tests, and operational reviews.

Full-core generation has an additional hard gate: the generation basis must be labeled `full core` and include My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy functional agents; complete Invitation onboarding; full user administration; governed runtime agent records, reference documents, `readSkill`, and `readReferenceDoc`; workstream UI; and acceptance/security/agent-governance/frontend tests. Workstream UI realization must start from the canonical `frontend/src/workstream/**` reference and User Admin dashboard → list/search → detail/edit vertical, not legacy `frontend/src/screens/**`, page-first route examples, or removed standalone static UI fixtures. The User Admin vertical must realize `user-admin-dashboard`, `user-admin-user-list`, and `user-admin-user-account` as fullstack surfaces backed by scoped backend APIs/capabilities/views/components and tests; fixture-only, API-only, or UI-only User Admin output is not full-core functional completion. If any are absent, stop generation unless the requested output is explicitly labeled `core app baseline`, `Module 1-only / not full core`, or another named narrower scope with the omitted full-core areas listed.

When the target project already contains implementation artifacts or a legacy `specs/scaffold-report.md`, prefer localized extension of that core app foundation over fresh full-app regeneration: preserve the fixed Java base package `ai.first`, existing foundation components, workstream shell, and app-description/spec queue history. Generate new outputs as vertical capability extensions unless the user explicitly asks to reset or replace the app.

## Generation responsibilities

When generating, this skill must:
- identify the current description baseline
- detect existing implementation artifacts or legacy `specs/scaffold-report.md` and treat generation as existing-app extension by default rather than fresh app replacement
- identify the generation scope label (`core app baseline`, `full core`, `Module 1-only / not full core`, or another narrower scope) from the app description/specs/user instruction and report it in the summary
- use the fixed Java base package `ai.first` for generated code; package selection is out of scope
- verify readiness did not ignore the secure SaaS foundation required by `core-saas-foundation`
- block full-core generation or return to readiness if User Admin, Agent Admin, complete invitation onboarding, full user administration, governed runtime agents, workstream UI, or required tests are missing without an explicit narrower-scope label
- block full-core generation or return to readiness if User Admin cannot be proven through the fullstack `user-admin-dashboard` → `user-admin-user-list` search/filter → `user-admin-user-account` detail/action path, including scoped APIs, backend capability enforcement, UserAdminAgent behavior, audit/trace output, and negative checks for disabled actor, cross-tenant access, Customer Admin Tenant-level denial, SaaS Owner without support access, role escalation, and last-admin loss
- block or return to readiness if generated SaaS web UI would be realized from `frontend/src/screens/**`, route/page-first tests, or removed static UI fixtures instead of the `frontend/src/workstream/**` shell, structured-surface modules, and User Admin reference vertical
- block or return to readiness when role-specific dashboard attention, human surface graph nodes/edges, system-message denial surfaces, internal workstream agent graph delegation, governed-tools, or browser-tool/agent-tool/internal-tool exposure mappings are missing or not traceable to capabilities, surfaces, tests, audit, and runtime validation paths
- block generation or return to readiness if Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, complete invitation onboarding, `/api/me`, backend authorization, audit, admin/support-access/billing-boundary semantics, AI-assisted admin offload, governed runtime agent foundation, tenant isolation, forbidden access, disabled-user, role/scope denial, or foundation tests are missing
- block generation or return to readiness if `AgentDefinition`, governed model binding (`ModelConfigRef`/`ModelPolicy` or explicit inherited default), fallback/no-fallback policy, provider secret boundary, model-use trace facts, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `ReferenceDocument`/`ReferenceVersion`, `AgentSkillManifest`, `AgentReferenceManifest`, `ToolPermissionBoundary`, first-install/tenant-bootstrap governed setup of implementation-developed default behavior/reference documents, deterministic prompt assembly, authorized `readSkill(skillId)`, authorized `readReferenceDoc(referenceId)`, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, editing agent proposals, or agent catalog/detail/model/prompt/skill/reference/manifest/tool-permission UI surfaces are missing for generated AI-first SaaS
- verify readiness did not ignore the required `15-operating-model/` for generated AI-first SaaS apps
- identify whether realization is localized extension/repair or explicitly scoped broad regeneration/replacement
- identify which outputs are in scope
- identify the role-specific dashboard, human surface graph, internal workstream agent graph, governed-tool, browser-tool, agent-tool, and internal-tool contracts that generation must realize or explicitly exclude from the named scope
- identify the local run, endpoint smoke, browser/workstream smoke, or manual-test path expected to prove the generated scope works
- realize the generated scope as working runtime code with Akka component-backed normal runtime state for claimed workstream/foundation features, not as a mock/demo/simulated substitute; provider/security gaps must fail closed with actionable configuration errors, while missing internal Akka persistence must block or narrow the completion claim
- realize outputs from the description
- keep implementation and generated/derived outputs consistent with the current description
- avoid treating code-only drift as authoritative over the description
- report failures or ambiguities back as description-level issues where appropriate

## Realization scope rules

### Localized extension/repair
Prefer when:
- the target repository already has implementation artifacts
- the work extends this core-app-first baseline or a downstream fork
- the description change is well-localized
- the affected implementation, test, UI, spec, or generated/derived areas are clear
- preserving stable unaffected outputs reduces churn, cost, or review noise

### Broad regeneration/replacement
Use only when:
- the user explicitly asks for destructive reset, fresh realization, or broad regeneration
- the scope label is named and the files safe to replace are clear
- prior outputs are unreliable or obsolete enough that targeted repair is riskier
- replacement will not discard the fixed Java package `ai.first`, foundation behavior, queue history, or user-owned domain work without explicit approval

Localized realization must never override description correctness.

## Output categories

As applicable, generation may include:
- secure SaaS foundation outputs first: identity/tenancy/domain types, Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, complete Invitation lifecycle with email delivery/outbox, resend, revoke, expiry, acceptance, delivery status and delivery attempts, AuthContext, `/api/me`, backend authorization service, AdminAuditEvent/audit views, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, support-access, subscription/billing boundary, mandatory governed runtime agent foundation (`AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `ReferenceDocument`/`ReferenceVersion`, `AgentSkillManifest`, `AgentReferenceManifest`, `ToolPermissionBoundary`, deterministic prompt assembly, authorized `readSkill(skillId)`, authorized `readReferenceDoc(referenceId)`, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`), behavior editing agent proposals, mandatory AI admin agents (AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, AdminAuditSummaryAgent or one governed `UserAdminAgent` with equivalent skills and references), role-specific dashboard projections, human surface graph rendering/action plumbing, internal workstream agent graph runtime paths, governed-tool implementations and browser-tool/agent-tool/internal-tool mappings, agent catalog/detail, prompt governance, skill governance, reference governance, skill/reference manifest, tool permission, edit-agent proposal, and trace UI surfaces, User Admin `user-admin-dashboard`, `user-admin-user-list`, and `user-admin-user-account` fullstack surfaces with safe mutation or decision-card-producing actions, decision cards for risky admin actions, mandatory admin/context-selection/supervision UI surfaces, and tenant-isolation/security tests for forbidden access, disabled users, cross-tenant access, Customer Admin Tenant-level denial, SaaS Owner no-support-access denial, role/scope denial, role escalation, audit, invite lifecycle, membership lifecycle, last-admin protection, admin-agent approval boundaries, disabled-agent denial, unassigned skill/reference denial, unauthorized prompt/skill/reference/tool-boundary change denial, trace creation, audit/search views, surface graph UI behavior, browser-tool denial, and frontend secret boundaries
- source code under the fixed Java base package `ai.first`, with Maven/Gradle group id, package declarations, imports, tests, and source paths kept consistent
- generated tests
- configuration or deployment assets
- runtime startup commands or scripts
- Akka-hosted web UI assets and TypeScript frontend modules when the app description includes `55-ui`, applying the selected `55-ui/style-guide.md`; generated SaaS frontend source should reuse/adapt `frontend/src/workstream/**` shell, rail, composer, stream, surfaces, actions, realtime, and User Admin reference vertical contracts, without restoring removed standalone static UI fixtures
- AI-first substrate outputs when described: durable goals/plans, governed model bindings for LLM-backed workstream agents, policy/approval gates, decision cards, traces, outcome loops, agents/workflows/views/endpoints, and supervision UI surfaces
- documentation or evaluation notes

The exact realization set depends on the current repository and user request, but generated AI-first SaaS is full-stack. Route browser UI realization through `akka-web-ui-apps` and its focused companion skills rather than treating the UI as raw asset delivery or optional polish. Do not invent visual styling during generation; if `55-ui/style-guide.md` or the specs style guide is missing/unselected, stop web UI generation and add or ask the pending style-selection question described in `../docs/web-ui-style-guide.md`.

For generated SaaS apps, route through `core-saas-foundation` before app-specific generation and stop or surface a blocking gap if the description lacks the mandatory foundation contract. The generation step must not decide that a protected route, agent-tool, data access path, workflow action, view query, stream, consumer side effect, timer action, or generated UI action is public or authorized by default. When AI-first behavior is in scope, route through `ai-first-saas` companion skills and the selected Akka substrate skills before generation. Stop or surface a blocking gap if `15-operating-model/` does not define the required delegated work, retained human authority, policies, approval gates, decision evidence, trace obligations, outcome metrics, or AI-first UI surfaces well enough to implement.

## Standard generation output shape

Use this response shape when summarizing generation:

```md
# App Generation Summary

## Generation basis
- description state:
- readiness state:
- generation scope label: core app baseline | full core | Module 1-only / not full core | other narrower scope
- operating-model basis:
- secure SaaS foundation basis:
- assumptions used:
- Java base package:

## Realization scope
- localized extension/repair | explicitly scoped broad regeneration/replacement
- affected output areas:

## Generated or updated outputs
- ...

## Executed steps
- generation:
- tests:
- app run:

## Results
- passed:
- failed:
- not run:

## Remaining uncertainties
- ...

## Recommended next step
- ...
```

## Handoff behavior

After generation, route onward as needed:
- to `ai-first-saas` or focused AI-first companion skills if generation exposed missing operating-model semantics
- to `app-description-change-summary` if the user asks what changed
- to `app-description-readiness-summary` if the user asks why generation was considered acceptable
- back to description-maintenance skills if generation exposed missing semantics or unacceptable assumptions

## Clarification policy

Ask only the smallest questions needed to avoid an obviously wrong realization step.

Examples:
- "Should I localize the implementation change to the affected area, or are you explicitly asking for a broader regeneration/replacement?"
- "Should I also run the available tests and start/smoke the local app path needed to prove this generated scope works?"
- "Which named narrower scope should I generate now, and which missing runtime features should remain blocked outside that completion claim?"

## Anti-patterns

Avoid:
- generating code directly from a vague prompt without honoring readiness assessment
- silently fixing generation issues by inventing semantics not present in the description
- treating prior code as authoritative over the current description when semantics differ
- using code-only edits to hide semantic gaps that belong in the app description
- hiding assumptions used during `ready-with-assumptions` generation
- generating AI-first apps from CRUD-only or chatbot-only descriptions when goals, authority, policies, decisions, traces, outcomes, or supervision surfaces are actually in scope
- changing generated application code away from the fixed `ai.first` package

## Final review checklist

Before finishing, verify:
- the description basis for generation is explicit
- readiness state is explicit
- generation scope label is explicit; core-baseline outputs are not represented as full-core ready and carry full-core follow-up tasks
- full-core omissions are blocked or labeled as core app baseline, Module 1-only / not full core, or another narrower scope
- secure SaaS foundation basis is explicit and complete enough for generation, including foundation behavior, auth/security, observability, mandatory web UI, and baseline tests
- missing foundation/security semantics block generation instead of becoming assumptions
- operating-model basis is explicit for generated AI-first SaaS
- assumptions are explicit when used
- Java base package is explicit and fixed to `ai.first`
- realization scope is explicit
- outputs in scope are listed clearly
- executed steps and results are reported clearly, including whether the app was run locally and what visible/API/workstream paths were validated
- semantic gaps discovered during generation are surfaced as description issues, not buried in code changes
- no LLM-backed generated workstream agent is declared ready unless its expert bundle names a specific approved `ModelConfigRef`/`ModelPolicy` or explicit inherited governed default and the generated outputs preserve model denial/fallback traces without provider secrets

## Response style

When answering:
- state the generation basis first
- distinguish realization from description maintenance
- make implementation/generation scope explicit
- clearly separate changed outputs from executed validation steps
- keep the summary suitable for prompt/response review
