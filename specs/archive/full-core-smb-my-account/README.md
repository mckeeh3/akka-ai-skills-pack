# Full-Core SMB My Account

## Purpose

Make My Account the signed-in user's trusted control center in the AI-first SaaS starter.

My Account should show profile/settings, selected context, authority basis, personal attention, safe workstream navigation, own trace references, and governed `MyAccountAgent` guidance from the lower-left user tile flow.

This mini-project targets `templates/ai-first-saas-starter/` as the executable baseline and preserves the workstream + structured surface architecture. My Account is not a top-rail CRUD page.

## Background

Completed full-core workstreams now produce the signals My Account should aggregate:

- User Admin produces membership, role, invitation, member-status, access-review, and user-admin agent evidence.
- Agent Admin produces managed-agent, behavior-change, tool-boundary, seed, provider, and agent guidance evidence.
- Audit/Trace exposes redacted trace search/detail/timeline evidence.
- Governance/Policy exposes policy posture, proposals, simulations, decisions, and blocked worker-readiness signals.
- Shared baseline/UX work defines shell request, system-message, validation, and visual standards.

My Account should now become the personal command center that explains “who am I here, what can I do, what needs my attention, and where can I safely go next?”

## Scope

Full-core SMB My Account should cover these vertical slices, appended as bounded tasks after source inspection:

1. **`/api/me`, selected context, authority summary, and lower-left user tile launch**: account/profile/context/membership/capability summary and no duplicate top-rail My Account launcher.
2. **Profile/settings surfaces**: self-service profile/settings reads and updates with validation, no-op/idempotency, audit, and denials.
3. **Personal attention aggregation**: authorized attention items from sibling workstreams without leaking hidden workstreams or resources.
4. **Own trace refs and safe cross-workstream navigation**: trace links and workstream open requests routed through backend authority checks.
5. **MyAccountAgent request/response guidance**: governed Akka `Agent` runtime for authority explanations, next-step summaries, context/profile guidance, and provider fail-closed surfaces.
6. **Personal digest worker candidate**: durable internal worker only if lifecycle semantics justify it after deterministic attention foundations exist.

## Non-goals

- Do not duplicate My Account in the top workstream rail.
- Do not add administrative user, tenant, policy, prompt, tool-boundary, or behavior mutations to My Account.
- Do not expose hidden workstream names, cross-tenant attention counts, secrets, provider credentials, hidden prompts, or unauthorized trace evidence.
- Do not let AI own context selection authorization, profile validation, attention filtering, workstream launch authority, or trace redaction.
- Do not use deterministic/model-less normal runtime behavior as a substitute for model-backed MyAccountAgent or worker behavior.

## Target source areas

Primary executable baseline:

- `templates/ai-first-saas-starter/`

Likely source families to inspect before editing:

- backend `/api/me`, identity, context, membership, workstream launch, and My Account services;
- backend workstream services and structured surface DTOs;
- agentfoundation managed-agent runtime, tools, tool-boundary, seed, and trace services;
- sibling workstream attention/trace producers already implemented;
- seed material under `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/`;
- frontend user tile, shell, context indicator, My Account surfaces, fixtures, API clients, actions, and contract tests;
- broad starter validation script and scaffold path.

## Execution model

Execute one task per fresh harness session. Start with vertical contracts and a source-boundary implementation map. The first implementation-map task must append bounded source-edit tasks and task briefs before runtime implementation begins.

## Read order for future task sessions

1. `AGENTS.md`
2. this mini-project `README.md`
3. `conversation-capture.md`
4. selected sprint and backlog files
5. selected task brief
6. predecessor SMB workstream contracts named by the task
7. smallest listed skill files and discovered source files

## Done state

This mini-project is complete when My Account has an SMB-ready full-core vertical at the implemented scope:

- the signed-in user can open My Account only from the lower-left user tile/email;
- `/api/me` and My Account surfaces show browser-safe account, profile/settings, selected context, membership, capability/authority basis, and denials;
- profile/settings updates are deterministic, validated, idempotent/no-op aware, audited, and trace-linked;
- personal attention aggregates authorized sibling-workstream items without leaking hidden workstreams/resources;
- own trace refs and workstream navigation use backend authority checks and safe system-message denials;
- MyAccountAgent uses the governed Akka Agent runtime path and fails closed when provider/model config is absent;
- any personal digest worker uses durable task semantics and cannot bypass deterministic attention authorization/redaction;
- frontend surfaces are visually polished, structured, accessible, and explicit about provider/config/authorization denials;
- targeted backend/frontend tests and broad starter validation pass, or remaining blockers are captured as bounded follow-up tasks.
