---
name: app-generate-app
description: Realize the current app description as generated code, tests, and runnable outputs while preserving description primacy, making regeneration scope explicit, and reporting what was generated, executed, and left uncertain.
---

# App Generate App

Use this skill when the user explicitly asks to realize the current application description as generated outputs, or when the harness has assessed the description as ready and generation has been accepted.

This skill exists in a **description-first operating model**.
It treats code, tests, and runnable assets as projections from the authoritative app description.
It does not redefine the app.

## Goal

Consume the current app description and generate application outputs that are suitable for implementation evaluation, test execution, and app execution.

The skill should:
- preserve description primacy
- generate code and tests from the current description
- choose full regeneration or localized regeneration deliberately
- make assumptions and generation scope explicit
- support downstream running, testing, and manual evaluation
- report clearly what changed and what remains uncertain

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/ai-first-saas-application-architecture.md`
- `../core-saas-foundation/SKILL.md` for mandatory secure SaaS foundation readiness before generation
- `../../docs/app-description-skills-plan-backlog.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../app-description-intake-router/SKILL.md`
- `../app-description-readiness-assessment/SKILL.md`
- `../ai-first-saas/SKILL.md` when delegated work, agents, decisions, governance, supervision, audit, or outcomes are in scope
- any currently relevant app-description layer artifacts identified by the harness, including `15-operating-model/` when present or required
- `../../docs/web-ui-style-guide.md` and `app-description/55-ui/style-guide.md` for mandatory generated full-stack SaaS web UI

If the user asks to generate and run, read the current build and execution entry points for the target project before acting.

## Use this skill when

The input sounds like:
- "generate the app"
- "generate the code"
- "regenerate from the current description"
- "ok, now generate the code and run the app"
- "realize the current description"

Use it only after the harness has either:
- determined the description is `ready`, or
- determined it is `ready-with-assumptions` and those assumptions are accepted or tolerable for the requested evaluation step

## Core operating rule

Generation is a realization step, not the source-of-truth step.

If generation reveals a semantic gap, the fix belongs in the app description, not in hand-edited generated code.
For every generated SaaS app, never invent missing secure foundation semantics during generation: Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, complete Invitation lifecycle, AuthContext, `/api/me`, backend authorization, audit, support-access, billing boundary, admin surfaces, AI-assisted admin offload, governed runtime agent foundation (`AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, `ToolPermissionBoundary`, first-install/tenant-bootstrap default behavior seed import, deterministic prompt assembly, authorized `readSkill(skillId)`, `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace`), agent governance UI, tenant-isolation, disabled-user, forbidden-access, role/scope-denial, and frontend secret-boundary tests must already be described or generation must stop/mark not-ready.
For AI-first/delegated operations, never invent missing authority, policy, approval, decision, evidence, trace, outcome, or supervision semantics during generation.

Minimum-starter generation is a named narrower scope, not a full-core shortcut. If the user asks for a minimum/starter/basic/chatbot-like generated SaaS app, the generation basis must be labeled `minimum starter`, use `docs/minimum-ai-first-saas-app.md`, realize User Admin workstream v0 with bootstrap authorization, selected AuthContext, bounded UserAdminAgent, durable workstream log, `markdown_response`, backend capability boundary, audit/work trace substrate, markdown sanitization, and starter tests, and emit follow-up tasks for full User Admin, Agent Admin, Audit/Trace UI/search, invitation/onboarding, governed prompt/skill/manifest/tool-boundary management, support access, billing boundary, and full security coverage. Do not describe this output as full-core ready or app-specific ready; app-specific readiness requires full core plus product/domain workstreams, capabilities, surfaces, tests, and operational reviews.

Full-core generation has an additional hard gate: the generation basis must be labeled `full core` and include Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy functional agents; complete Invitation onboarding; full user administration; governed runtime agent records and `readSkill`; workstream UI; and acceptance/security/agent-governance/frontend tests. Workstream UI realization must start from the canonical `frontend/src/workstream/**` reference and User Admin dashboard → list/search → detail/edit vertical, not legacy `frontend/src/screens/**`, page-first route examples, or standalone static-resource examples. If any are absent, stop generation unless the requested output is explicitly labeled `minimum starter`, `Module 1-only / not full core`, or another named narrower scope with the omitted full-core areas listed.

When the target project contains `specs/scaffold-report.md`, treat it as scaffolded from the packaged starter. Prefer localized extension of the scaffolded foundation over fresh full-app regeneration: preserve the recorded Java base package, existing starter components, workstream shell, app-description/spec queue history, and scaffold report. Generate new outputs as vertical capability extensions unless the user explicitly asks to reset or replace the scaffold.

## Generation responsibilities

When generating, this skill must:
- identify the current description baseline
- detect whether `specs/scaffold-report.md` exists and, if so, treat generation as starter extension by default rather than fresh app replacement
- identify the generation scope label (`minimum starter`, `full core`, `Module 1-only / not full core`, or another narrower scope) from the app description/specs/user instruction and report it in the summary
- resolve the Java base package from existing project configuration, `specs/scaffold-report.md`, the app description, or the initial package question: "What Java base package should I use for generated code? Press Enter to use `ai.first`." Default to `ai.first` only when accepted/deferred; never use `com.example` for generated application code unless explicitly requested
- verify readiness did not ignore the secure SaaS foundation required by `core-saas-foundation`
- block full-core generation or return to readiness if User Admin, Agent Admin, complete invitation onboarding, full user administration, governed runtime agents, workstream UI, or required tests are missing without an explicit narrower-scope label
- block or return to readiness if generated SaaS web UI would be realized from `frontend/src/screens/**`, route/page-first tests, or static resource examples instead of the `frontend/src/workstream/**` shell, structured-surface modules, and User Admin reference vertical
- block generation or return to readiness if Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, complete invitation onboarding, `/api/me`, backend authorization, audit, admin/support-access/billing-boundary semantics, AI-assisted admin offload, governed runtime agent foundation, tenant isolation, forbidden access, disabled-user, role/scope denial, or foundation tests are missing
- block generation or return to readiness if `AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, `ToolPermissionBoundary`, first-install/tenant-bootstrap import of implementation-developed default behavior documents, deterministic prompt assembly, authorized `readSkill(skillId)`, `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace`, editing agent proposals, or agent catalog/detail/prompt/skill/manifest/tool-permission UI surfaces are missing for generated AI-first SaaS
- verify readiness did not ignore the required `15-operating-model/` for generated AI-first SaaS apps
- identify whether generation is full or localized
- identify which outputs are in scope
- realize outputs from the description
- keep generated outputs consistent with the current description
- avoid treating generated code as authoritative
- report failures or ambiguities back as description-level issues where appropriate

## Regeneration scope rules

### Full regeneration
Prefer when:
- the app is early-stage or mostly disposable
- the description changed broadly across many layers
- prior outputs are unreliable or obsolete
- a clean realization is cheaper and safer than targeted patching

### Localized regeneration
Prefer when:
- the description change is well-localized
- the affected projections are clear
- preserving stable unaffected outputs reduces churn, cost, or review noise

Localized regeneration is an optimization only.
It must never override description correctness.

## Output categories

As applicable, generation may include:
- secure SaaS foundation outputs first: identity/tenancy/domain types, Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, complete Invitation lifecycle with email delivery/outbox, resend, revoke, expiry, acceptance, delivery status and delivery attempts, AuthContext, `/api/me`, backend authorization service, AdminAuditEvent/audit views, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, support-access, subscription/billing boundary, mandatory governed runtime agent foundation (`AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, `ToolPermissionBoundary`, deterministic prompt assembly, authorized `readSkill(skillId)`, `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace`), behavior editing agent proposals, mandatory AI admin agents (AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, AdminAuditSummaryAgent or one governed `UserAdminAgent` with equivalent skills), agent catalog/detail, prompt governance, skill governance, skill manifest, tool permission, edit-agent proposal, and trace UI surfaces, decision cards for risky admin actions, mandatory admin/context-selection/supervision UI surfaces, and tenant-isolation/security tests for forbidden access, disabled users, role/scope denial, audit, invite lifecycle, membership lifecycle, last-admin protection, admin-agent approval boundaries, disabled-agent denial, unassigned skill denial, unauthorized prompt/skill/tool-boundary change denial, trace creation, audit/search views, UI behavior, and frontend secret boundaries
- source code under the selected Java base package, with Maven/Gradle group id, package declarations, imports, tests, and source paths kept consistent
- generated tests
- configuration or deployment assets
- runtime startup commands or scripts
- Akka-hosted web UI assets and TypeScript frontend modules when the app description includes `55-ui`, applying the selected `55-ui/style-guide.md`; generated SaaS frontend source should reuse/adapt `frontend/src/workstream/**` shell, rail, composer, stream, surfaces, actions, realtime, and User Admin reference vertical contracts, while treating `frontend/src/screens/**` and standalone static-resource examples as legacy/mechanics references only
- AI-first substrate outputs when described: durable goals/plans, policy/approval gates, decision cards, traces, outcome loops, agents/workflows/views/endpoints, and supervision UI surfaces
- documentation or evaluation notes

The exact realization set depends on the current repository and user request, but generated AI-first SaaS is full-stack. Route browser UI realization through `akka-web-ui-apps` and its focused companion skills rather than treating the UI as raw asset delivery or optional polish. Do not invent visual styling during generation; if `55-ui/style-guide.md` or the specs style guide is missing/unselected, stop web UI generation and add or ask the pending style-selection question described in `../../docs/web-ui-style-guide.md`.

For generated SaaS apps, route through `core-saas-foundation` before app-specific generation and stop or surface a blocking gap if the description lacks the mandatory foundation contract. The generation step must not decide that a protected route, agent tool, data access path, workflow action, view query, stream, consumer side effect, timer action, or generated UI action is public or authorized by default. When AI-first behavior is in scope, route through `ai-first-saas` companion skills and the selected Akka substrate skills before generation. Stop or surface a blocking gap if `15-operating-model/` does not define the required delegated work, retained human authority, policies, approval gates, decision evidence, trace obligations, outcome metrics, or AI-first UI surfaces well enough to implement.

## Standard generation output shape

Use this response shape when summarizing generation:

```md
# App Generation Summary

## Generation basis
- description state:
- readiness state:
- generation scope label: minimum starter | full core | Module 1-only / not full core | other narrower scope
- operating-model basis:
- secure SaaS foundation basis:
- assumptions used:
- Java base package:

## Regeneration scope
- full | localized
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
- "What Java base package should I use for generated code? Press Enter to use `ai.first`."
- "Do you want full regeneration or should I localize regeneration to the changed description area if possible?"
- "Should I stop after generation, or also run tests and start the app if available?"
- "Is this generation mainly for early evaluation, or do you want the strictest production-oriented realization possible from the current description?"

## Anti-patterns

Avoid:
- generating code directly from a vague prompt without honoring readiness assessment
- silently fixing generation issues by inventing semantics not present in the description
- treating prior generated code as authoritative over the current description
- performing manual-style code edits as if they were the correct response to semantic gaps
- hiding assumptions used during `ready-with-assumptions` generation
- generating AI-first apps from CRUD-only or chatbot-only descriptions when goals, authority, policies, decisions, traces, outcomes, or supervision surfaces are actually in scope
- copying `com.example` from reference examples into generated application code unless the user explicitly selected it

## Final review checklist

Before finishing, verify:
- the description basis for generation is explicit
- readiness state is explicit
- generation scope label is explicit; minimum-starter outputs are not represented as full-core ready and carry full-core follow-up tasks
- full-core omissions are blocked or labeled as minimum starter, Module 1-only / not full core, or another narrower scope
- secure SaaS foundation basis is explicit and complete enough for generation, including foundation behavior, auth/security, observability, mandatory web UI, and baseline tests
- missing foundation/security semantics block generation instead of becoming assumptions
- operating-model basis is explicit for generated AI-first SaaS
- assumptions are explicit when used
- Java base package is explicit and is not accidentally inherited from `com.example` reference examples
- regeneration scope is explicit
- outputs in scope are listed clearly
- executed steps and results are reported clearly
- semantic gaps discovered during generation are surfaced as description issues, not buried in code changes

## Response style

When answering:
- state the generation basis first
- distinguish generation from description maintenance
- make regeneration scope explicit
- clearly separate generated outputs from executed validation steps
- keep the summary suitable for prompt/response review
