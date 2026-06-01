# Attention Release Smoke Checklist

## Purpose

This checklist turns the recorded dogfood observation into repeatable release-readiness evidence for the implemented attention backbone v1 and bounded producer/update-delivery v2 scope.

## Source dogfood evidence

Manual user observation after the v1/v2 work:

> i tested the app and things have significantly improved. the left rail workstream things needing my attention are working. also, seeing improvements in the dashboards and surfaces.

Interpretation for this checklist:

- the main visible left-rail attention path has positive manual evidence;
- dashboard and structured-surface rendering has positive manual evidence;
- release readiness still requires repeatable backend/frontend, lifecycle, security, fail-closed, and authority-boundary checks.

## Validation modes

- **Automated**: backend Maven, frontend tests/typecheck/build, or focused source checks.
- **Manual**: browser/runtime or local interactive smoke notes.
- **N/A for v1/v2**: intentionally future work, not required for this release-ready claim.

## Smoke checklist

| Area | Check | Mode | Evidence to record in follow-up tasks |
| --- | --- | --- | --- |
| Fresh scaffold | Scaffold a new starter from `templates/ai-first-saas-starter` with a non-example app name, `ai.first` base package, and valid Maven group id. | Automated | Scaffold command, target path, and whether backend/frontend checks run from the scaffold. |
| Backend attention source | Attention items, My Account aggregates, workstream dashboard sections, and rail summaries are read from backend attention services/projections, not hard-coded or browser-only badge state. | Automated | Targeted backend tests plus focused `rg` for `AttentionService`, `attention.list_rail_summaries`, `attention.list_workstream_items`, and separation from `railAttentionState`. |
| Left rail attention | Left rail displays backend-derived actionable counts/items for visible workstreams and updates after a backend refresh path. | Manual + Automated | Browser note or frontend test evidence that rail summaries come from bootstrap/functional-agent API payloads. |
| My Account attention | My Account shows the personal/open attention aggregate and supports opening an attention item through the backend-backed action path. | Manual + Automated | Backend `MyAccountService`/`WorkstreamService` tests and manual note for open-attention/open-workstream behavior. |
| Workstream dashboards/surfaces | User Admin, Agent Admin, Audit/Trace, and Governance/Policy dashboards or surfaces render attention metadata, trace refs, actions, empty states, and denied states consistently at v1/v2 scope. | Manual + Automated | Frontend rendering tests and browser notes for at least the implemented attention-bearing surfaces. |
| Invitation producer updates | User Admin invitation delivery failure/stale/revoke/expire/accept paths upsert or resolve attention idempotently. | Automated | Scaffolded backend tests covering `AttentionProducerService` and invitation/user-admin services. |
| Governance producer updates | Governance policy proposal, decision, activation, and rollback paths upsert or resolve approval attention idempotently. | Automated | Scaffolded backend tests covering `GovernancePolicyService` and producer behavior. |
| Timed attention checks | Implemented timed/stale checks, including invitation delivery timed checks, update attention through the shared backbone without introducing fake success. | Automated | Backend tests and focused source check for timed producer paths. |
| Worker/task attention | User Admin access-review worker/task states create, update, and resolve blocked/review-needed attention while preserving provider/runtime blocked status. | Automated | Backend `UserAdminAccessReviewService` tests and focused `rg` for `blocked_provider_or_runtime`. |
| Lifecycle operations | Open/acknowledge/dismiss/resolve/no-op lifecycle operations behave idempotently and preserve audit/protected-read traces where implemented. | Automated + Manual | Backend attention lifecycle tests and optional browser note for visible lifecycle controls. |
| Denial and hidden-workstream redaction | Hidden, unauthorized, or tenant/customer-mismatched attention items are not exposed; returned denials are safe and traceable. | Automated + Manual | Backend forbidden/tenant-isolation tests and browser note for a denied/hidden surface where feasible. |
| Provider/fail-closed behavior | Missing provider/model/security configuration fails closed with actionable blocked attention or errors; no deterministic/model-less normal runtime success is used to satisfy model-backed work. | Automated + Manual | Backend tests/source checks for blocked provider states and manual/runtime note if provider config is absent. |
| Frontend-only authority guardrail | `railAttentionState` and unseen-response/background indicators remain transient UI state only and are never authoritative for actionable attention. | Automated | Focused `rg` and frontend tests proving actionable counts come from backend summaries/items. |
| Redaction and browser-safe payloads | Browser payloads expose only browser-safe attention metadata, capability ids, surface refs, and trace refs; secrets/provider config remain server-side. | Automated | Frontend secret-boundary/source check and backend API payload review. |
| Docs/handoff accuracy | Release notes distinguish implemented v1 backbone and bounded v2 producers/update delivery from future event backbone, SSE/push, digest, and notification work. | Automated | Focused `rg` over docs/specs for stale “attention missing/not implemented” claims and future-work boundaries. |

## Not required for current release-ready claim

These are useful future improvements but are **N/A for v1/v2 release readiness** unless a later task promotes them into scope:

- generic Workstream Event Backbone v3;
- all possible domain events converted to attention producers;
- enterprise notification center, digest, or subscription infrastructure;
- SSE/push realtime delivery beyond the implemented backend refresh/update path;
- AutonomousAgent task notification integration beyond the bounded worker/task attention currently implemented.

## Minimum evidence bundle for release handoff

Follow-up validation tasks should leave a concise evidence trail containing:

1. scaffold command and target path;
2. backend Maven commands/results for attention services, producers, timed/worker paths, My Account, and workstreams;
3. frontend test/typecheck/build commands/results;
4. focused source checks for backend-derived attention and frontend-only authority separation;
5. manual/runtime notes for left rail, My Account, dashboards/surfaces, lifecycle, denial/redaction, and fail-closed behavior, or a clear blocked reason.
