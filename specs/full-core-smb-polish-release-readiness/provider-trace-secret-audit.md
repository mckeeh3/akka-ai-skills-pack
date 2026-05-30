# Provider, Trace, Navigation, and Secret Boundary Audit

## Scope

Audit task: `TASK-FCSMB-REL-01-004`.

Reviewed the AI-first SaaS starter for provider fail-closed behavior, no deterministic/model-less normal-runtime substitution, trace links, workstream navigation, evidence tools, browser-visible static assets, frontend environment files, and denial/provider-blocked copy.

## Commands run

```bash
rg -n "My Account|User Admin|Agent Admin|Audit/Trace|Governance/Policy|system_message|blocked_provider_or_runtime|fail-closed|ToolPermissionBoundary|readSkill|readReferenceDoc|AgentWorkTrace|PromptAssemblyTrace|trace|tenant|provider|secret|hidden prompt|open_authorized_workstream|EvidenceTools" templates/ai-first-saas-starter --glob '!**/node_modules/**' | head -250

rg -n "deterministic|fallback|model-less|mock|fixture|demo|canned|OPENAI_API_KEY|api[_-]?key|secret|password|token|Authorization|Bearer|prompt" templates/ai-first-saas-starter --glob '!**/node_modules/**' --glob '!backend/target/**' --glob '!frontend/dist/**' | head -250

find templates/ai-first-saas-starter/frontend -maxdepth 3 \( -path '*/node_modules' -o -path '*/dist' \) -prune -o -type f \( -name '.env*' -o -name '*.ts' -o -name '*.tsx' -o -name '*.js' -o -name '*.mjs' -o -name '*.html' \) -print | sort | head -200

rg -n "EvidenceTools|evidence|open_authorized_workstream|system_message|traceId|correlationId|provider-blocked|blocked_provider_or_runtime|ToolPermissionBoundary|readSkill|readReferenceDoc" templates/ai-first-saas-starter/backend/src templates/ai-first-saas-starter/frontend/src --glob '!**/node_modules/**' | head -250

rg -n "blocked_provider_or_runtime|Provider|provider|model|OPENAI_API_KEY|Required backend environment|fail closed|deterministic|model-less|fallback|WorkstreamRuntimeAgent|effects\(\)\.tools|DefaultWorkstreamAgentRuntimeInvoker|AgentRuntimeService|ModelProvider" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java templates/ai-first-saas-starter/backend/src/main/resources --glob '!**/target/**'

rg -n "OPENAI_API_KEY|WORKOS_API_KEY|RESEND_API_KEY|JWT|Bearer|Authorization|secret|hidden prompt|api[_-]?key|providerCredential|invitation token|raw token|password" templates/ai-first-saas-starter/frontend/src templates/ai-first-saas-starter/frontend/public templates/ai-first-saas-starter/frontend/*.env* templates/ai-first-saas-starter/frontend/index.html

rg -n "My Account|User Admin|Agent Admin|Audit/Trace|Governance/Policy|open_authorized_workstream|launchMyAccount|functionalAgentId|selectedFunctionalAgent|workstreamId|/api/workstream|/ui\?traceId|traceLinks" templates/ai-first-saas-starter/frontend/src templates/ai-first-saas-starter/backend/src/main/java --glob '!**/node_modules/**' | head -220

rg -n "class .*EvidenceTools|EvidenceTools|read-only|cannot|hidden prompts|secrets|tenant/customer|AuthorizationException|requireRequestedScope|ToolPermissionBoundary|readSkill|readReferenceDoc|deniedToolIds|allowedToolGrants" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java --glob '!**/target/**' | head -220

env -u OPENAI_API_KEY tools/smoke-ai-first-saas-starter-real-model.sh

find templates/ai-first-saas-starter/frontend -maxdepth 2 \( -path '*/node_modules' -o -path '*/dist' \) -prune -o -type f -name '.env*' -print -exec sh -c 'for f; do printf "%s\n" "--- $f ---"; grep -nE "OPENAI|WORKOS|RESEND|SECRET|API_KEY|TOKEN|JWT|VITE_" "$f" || true; done' sh {} +

find templates/ai-first-saas-starter/frontend -maxdepth 3 \( -path '*/node_modules' -o -path '*/dist' \) -prune -o -type f \( -path '*/public/*' -o -name 'index.html' -o -name 'vite.config.ts' \) -print -exec sh -c 'for f; do printf "%s\n" "--- $f ---"; grep -nEi "OPENAI|WORKOS_API|RESEND|SECRET|API[_-]?KEY|TOKEN|JWT|Bearer|hidden prompt|providerCredential" "$f" || true; done' sh {} +
```

## Results summary

- Release blockers: none found.
- Environmental skips: real provider smoke skipped because `OPENAI_API_KEY` was intentionally unset for the fail-closed/no-secret audit path.
- Non-blocking recommendations: keep manual release QA focused on a rendered production build asset scan after final docs/handoff are complete.
- Intentional deferrals: optional durable background workers and real-provider smoke without credentials remain non-blocking when documented as provider/env-gated.

## Provider fail-closed and no model-less runtime substitution

Evidence found:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/DefaultWorkstreamAgentRuntimeInvoker.java` returns provider/runtime blocked decisions rather than model-less success when runtime preparation blocks invocation.
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java`, `WorkstreamRuntimeAgent.java`, and related tests preserve the governed Akka Agent path, trace ids, `PromptAssemblyTrace`, `AgentWorkTrace`, loader-tool traces, and provider-failure metadata.
- `WorkstreamRuntimeAgentTest`, `AgentRuntimeServiceTest`, `AgentRuntimeToolResolverTest`, and `AgentRuntimeTraceSinkTest` cover provider-secret redaction, loader-tool authorization, denied tool traces, and runtime trace behavior.
- Frontend contract tests cover `system_message` rendering for provider/runtime blocked turns and assert that provider credentials/API-key markers are not rendered in blocked responses.

The smoke command result was the expected environmental skip:

```text
[starter-real-model-smoke] Akka Agent smoke skipped: OPENAI_API_KEY is not set or is blank.
[starter-real-model-smoke] To enable real provider validation: export OPENAI_API_KEY, optionally OPENAI_MODEL_ID/OPENAI_API_BASE_URL/OPENAI_REQUEST_TIMEOUT_SECONDS, then rerun this command.
```

This is not a release blocker because this task intentionally audited the provider-missing mode and the prior validation task recorded real-provider smoke as environment-gated.

## Trace, action, and navigation semantics

Evidence found:

- Workstream API and frontend types consistently carry `correlationId`, `traceId`, `traceIds`, and `traceLinks` near protected reads, composer submissions, action results, and audit detail routes.
- Composer responses map backend trace ids into trace links under `/ui?traceId=...`.
- My Account remains represented as a lower-left signed-in user tile/workstream launch path in the frontend contract set, while User Admin, Agent Admin, Audit/Trace, and Governance/Policy remain top-rail workstreams.
- `open_authorized_workstream` appears in My Account surface fixtures/tests and is treated as a governed navigation action rather than a frontend-only bypass.
- Backend `WorkstreamEndpoint` routes all protected workstream bootstrap, item, surface, action, message, and event calls through authorized request handling with selected context and correlation id.

No navigation/trace blocker was found.

## Evidence tools and authorization boundaries

Evidence found:

- `ToolRegistry` defines read-only governed loader/evidence tools for `readSkill`, `readReferenceDoc`, `userAdminEvidence.read`, `myAccountEvidence.read`, `agentAdminEvidence.read`, `auditTraceEvidence.read`, and `governancePolicyEvidence.read` with capability metadata.
- `AgentRuntimeToolResolverTest` verifies expected tool grants for all five workstreams, denied unknown tools, wrong capability/category/mode grants, disabled/mismatched boundaries, capability denial, and cross-tenant denial for evidence tools.
- Evidence tool implementations describe read-only, scoped, redacted behavior and throw authorization errors for cross-tenant/customer evidence requests.
- Seed prompts instruct agents not to expose hidden prompts, provider credentials, JWTs, invitation tokens, or unauthorized tenant/customer data, and not to replace provider/runtime failures with deterministic/model-less success.

No evidence-tool release blocker was found.

## Browser-visible secret and hidden-prompt boundary

Evidence found:

- `templates/ai-first-saas-starter/frontend/.env.example` contains only public `VITE_WORKOS_*` values and explicit comments warning not to add backend secrets such as `WORKOS_API_KEY`, `ADMIN_USERS`, `RESEND_API_KEY`, invite sender credentials, or `OPENAI_API_KEY`.
- `frontend/public`, `frontend/index.html`, and `frontend/vite.config.ts` contained no matches for backend API keys, provider credentials, bearer tokens, JWTs, hidden prompt markers, or provider credential markers in the targeted scan.
- Frontend contract tests assert that provider-blocked/system-message rendering does not expose `providerCredential`, `api_key`, hidden prompts, raw JWTs, invitation tokens, or unauthorized tenant/customer evidence.
- Hidden prompt/source seed files are backend resources under `backend/src/main/resources/agent-behavior-seeds/...`, not browser-public assets.

No browser-visible secret or hidden-prompt blocker was found in source/static candidates.

## Denial and provider-blocked copy

Evidence found:

- Typed `system_message` surfaces and fixtures/tests cover provider/runtime blocked states, forbidden/denied action states, no-op results, redaction indicators, recovery guidance, and trace links.
- Browser-facing blocked/denial copy uses safe reason/recovery language and references correlation/trace ids without exposing provider secrets or hidden prompt bodies.

No denial-copy blocker was found.

## Finding classification

| Finding | Classification | Action |
|---|---|---|
| Real provider smoke skipped with `OPENAI_API_KEY` unset | Environmental skip | Keep documented provider-gated smoke command; not a release blocker. |
| Optional rendered production asset scan after final handoff/docs | Non-blocking recommendation | Mention in release handoff/manual QA; no bounded blocker task needed. |
| Optional durable background workers not implemented | Intentional deferral | Keep as post-release candidate unless visible runtime claims completion. |

## Release-readiness impact

This audit found no release blockers and did not append fix tasks. The next queued release-readiness step can proceed to release docs and handoff.
