# TASK-003: Capability denials and audit/work traces for User Admin workstream v0

## Purpose

Make the minimum User Admin workstream v0 enforce backend capability checks and record durable audit/work-trace facts for both allowed and denied workstream actions. This task completes the security/observability slice needed before richer User Admin surfaces are treated as usable.

## Reads

Read these before coding:

- `.agents/AGENTS.md`
- `.agents/skills/README.md`
- `specs/scaffold-report.md`
- `specs/pending-tasks.md`
- `app-description/README.md`
- `app-description/10-capabilities/01-secure-tenant-user-foundation.md` if present
- `app-description/12-workstreams/functional-agents.md` if present
- `app-description/12-workstreams/surface-contracts/00-markdown-response.md` if present
- `app-description/20-behavior/rules/01-tenant-authz-rules.md` if present
- `app-description/30-tests/negative/01-forbidden-actions.md` if present
- `app-description/40-auth-security/authorization-rules.md` if present
- `app-description/50-observability/traces-and-correlation.md` if present
- `app-description/55-ui/workstream-panel-and-composer.md` if present
- `backend/pom.xml`
- `frontend/package.json`
- Existing backend AuthContext, capability/authorization, audit, trace, and workstream source files
- Existing frontend User Admin workstream/composer/API client files

Load only these installed skills unless a listed file points to a narrower companion:

- `core-saas-foundation`
- `capability-first-backend`
- `agent-workstream-apps`
- `akka-http-endpoints`
- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-testing`

## Scope

Implement one bounded vertical increment:

1. Backend capability enforcement for User Admin workstream v0 actions.
   - Resolve the caller's selected `AuthContext` from the authenticated account/session or bootstrap-local equivalent already established by the starter.
   - Check the caller has the minimum User Admin workstream capability before invoking the UserAdminAgent or any underlying user-admin operation.
   - Deny missing, disabled, cross-tenant, or insufficient-authority contexts before model/tool/component work executes.
   - Return a stable browser-safe denial response with correlation/trace id and no backend secrets.

2. Durable audit/work-trace recording.
   - Record allowed workstream request facts: actor, selected tenant/customer context, capability id, surface/action, request id/correlation id, outcome, and rendered surface type.
   - Record denied request facts: actor if known, selected or attempted context, capability id, denial reason category, request id/correlation id, and no sensitive token/secret values.
   - Link rendered `markdown_response` payloads to the trace/correlation id.
   - Keep trace records scoped so later full Audit/Trace UI/search can read them without changing this event shape.

3. API and frontend integration.
   - Ensure the User Admin workstream/composer API returns denial payloads in the same typed response family as successful `markdown_response` results, or in the existing typed API error envelope if that is the scaffold convention.
   - Render denials in the workstream timeline as capability-denied system responses with accessible copy and trace/correlation id.
   - Do not expose raw exception messages, JWTs, API keys, provider payloads, prompt internals, or backend stack traces to the browser.

4. Tests.
   - Add or update backend tests for allowed request trace creation and denied request trace creation.
   - Add or update backend tests proving denied requests do not invoke agent/model/tool/component side effects.
   - Add or update API/frontend contract tests for denial envelope rendering and frontend secret boundaries.

## Non-goals

Do not implement these in this task:

- Full UserDirectoryView, MembershipView, InvitationView, AdminAuditView, or AccessReviewQueueView.
- Complete invitation onboarding, Resend delivery, reminder timers, or captured outbox behavior.
- Full Agent Admin, prompt/skill/manifest/tool-boundary governance, or behavior-editing agents.
- Full Audit/Trace Explorer UI/search.
- App-specific domain features.
- A new visual design system or unrelated UI restyle.
- A destructive starter reset or regeneration from `.agents/resources/templates/ai-first-saas-starter/`.

## Akka components involved

Use the existing scaffolded components where present. Expected component boundaries are:

- HTTP endpoint(s) for User Admin workstream requests and current-user/context lookup.
- Existing AuthContext/capability authorization service or equivalent foundation service.
- Existing audit/work-trace entity, key-value record, event-sourced record, view, or repository seam used by the scaffold.
- Existing UserAdminAgent invocation path or deterministic starter substitute.
- Existing React/Vite workstream shell, composer, timeline, and typed API client.

If the scaffold has no durable audit/work-trace component yet, add the smallest durable record/component needed for this task and keep its API internal except for returning trace/correlation ids from workstream responses.

## Skills to load

- `core-saas-foundation`
- `capability-first-backend`
- `agent-workstream-apps`
- `akka-http-endpoints`
- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-testing`

## Expected outputs

Update the local equivalents of these file families as needed:

- Backend authorization/capability check code for User Admin workstream v0.
- Backend audit/work-trace domain records and persistence path if not already present.
- Backend User Admin workstream endpoint response DTOs or typed API envelope.
- Backend tests covering success trace, denial trace, no side effects on denial, and secret-safe denial payloads.
- Frontend API client/types for denial responses if the existing envelope does not already cover them.
- Frontend workstream timeline/composer rendering for capability denials.
- Frontend contract tests covering denial rendering and absence of backend secrets.
- `specs/pending-tasks.md` status/notes for TASK-003.

## Required tests and checks

Run the closest available equivalents in this project:

- Backend unit/component tests for authorization, denial, and audit/work-trace recording.
- Backend API tests for User Admin workstream allowed and denied requests.
- Frontend contract/build tests for typed API handling and denial rendering.
- Frontend secret-boundary check proving rendered payloads do not include backend secret names or raw token/config values.
- `git diff --check`

Suggested commands when available:

```bash
cd backend && mvn test
cd frontend && npm test
cd frontend && npm run build
git diff --check
```

If the project uses root-level commands instead, use those and record the substitution in `specs/pending-tasks.md` notes.

## Local/runtime validation path

After tests pass, validate the intended local surface when feasible:

1. Start the backend/app using the project's documented local command.
2. Use a bootstrap-authorized local user/context and submit a User Admin workstream v0 prompt/action; verify a `markdown_response` appears with a trace/correlation id.
3. Use a missing/unauthorized/disabled/cross-tenant context or test token and submit the same action; verify the UI renders a capability-denied response with trace/correlation id and no secret/raw exception text.
4. Verify a durable audit/work-trace fact exists for both the allowed and denied attempts through the available test seam, log, view, or debug endpoint.

If local runtime validation cannot run because credentials or local services are unavailable, keep TASK-003 blocked unless tests provide an accepted local substitute and the queue notes explicitly record that substitute.

## Done criteria

TASK-003 is done only when:

- User Admin workstream v0 requests are checked against selected `AuthContext` and required capability before agent/tool/component side effects run.
- Unauthorized, disabled, missing-context, or cross-tenant attempts are denied with browser-safe typed responses.
- Allowed and denied attempts produce durable audit/work-trace facts with correlation/trace ids and no secrets.
- The frontend renders denial outcomes in the workstream timeline accessibly and shows the trace/correlation id.
- Backend and frontend tests cover success, denial, no-side-effect-on-denial, audit/work trace creation, and secret-safe rendering/API responses.
- Required checks pass or the queue records an accepted equivalent.
- `specs/pending-tasks.md` is updated for TASK-003 with status, checks run, and next task notes.
