# Runtime Durability Remediation Map

Date: 2026-05-30

## Summary

Initial inventory found that the stronger no-in-memory-normal-runtime release bar was **not met**: the rendered starter wired multiple in-memory repositories as normal backend runtime defaults and shipped frontend fixture inspection paths/static assets that could be mistaken for production-like runtime.

Release status after remediation validation on 2026-05-30: **passed for the documented SMB starter scope**. Normal completed runtime paths now either bind Akka durable components or fail closed with actionable guidance. Explicit local/demo repositories require `AI_FIRST_SAAS_LOCAL_DEMO_REPOSITORIES=true`; frontend fixture mode requires dev/local opt-in; production-like static resources scan clean for fixture/demo/provider-secret markers. The prior full-core SMB release handoff is no longer superseded for this bar after `TASK-FCSMB-DUR-01-005` validation.

## Inventory commands used

```bash
rg -n "InMemory|in-memory|mock|Mock|fake|Fake|fixture|Fixture|demo|Demo|canned|model-less|fallback|stub|Stub" templates/ai-first-saas-starter frontend specs/full-core-smb-polish-release-readiness --glob '!**/node_modules/**' --glob '!**/target/**' --glob '!**/dist/**'
find templates/ai-first-saas-starter -path '*/node_modules' -prune -o -path '*/target' -prune -o -path '*/dist' -prune -o -type f \( -iname '*InMemory*' -o -iname '*Fake*' -o -iname '*Fixture*' -o -iname '*Demo*' -o -iname '*Mock*' -o -iname '*Stub*' \) -print | sort
find frontend templates/ai-first-saas-starter/frontend templates/ai-first-saas-starter/src/main/resources/static-resources specs/full-core-smb-polish-release-readiness -path '*/node_modules' -prune -o -path '*/dist' -prune -o -path '*/target' -prune -o -type f \( -iname '*fixture*' -o -iname '*demo*' -o -iname '*mock*' -o -iname '*fake*' -o -iname '*static*' \) -print | sort
find templates/ai-first-saas-starter/src/main/resources/static-resources -maxdepth 3 -type f -print 2>/dev/null | sort | head -200
rg -n "fixtureWorkstream|FixtureWorkstream|fixture|demo|InMemory|fake|model-less|OPENAI_API_KEY|WORKOS_API_KEY" templates/ai-first-saas-starter/src/main/resources/static-resources --glob '!**/*.map'
```

## Backend normal runtime blockers

| Finding | Paths | Classification | Release impact | Required remediation |
|---|---|---|---|---|
| Static service registry wires in-memory identity, invitation, agent behavior, workstream log, audit, and governance stores as normal runtime defaults. | `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/StarterSecurityComponents.java` | normal runtime default requiring replacement or fail-closed gating | blocker | Replace default normal wiring with Akka durable repositories/components where implemented; fail closed with actionable startup/readiness/API guidance for any normal feature whose durable store is not implemented. Keep any in-memory adapter behind explicit local/demo/test profile only. |
| Identity/account/membership authorization state is in memory and seeded with local demo users. | `InMemoryIdentityRepository.java`, `BootstrapAdminSeeder.java`, `IdentityRepository.java`, `UserAdminService.java`, `InvitationService.java`, `StarterSecurityComponents.java` | normal runtime default requiring replacement | blocker | Add durable identity/account/membership repository or explicit fail-closed bootstrap requirement. `/api/me`, user admin, invitation acceptance, role changes, disabled-user checks, and tenant selection must not depend on volatile default state. |
| Invitation repository has durable Akka seam but normal static registry still defaults to in-memory. | `InMemoryInvitationRepository.java`, `AkkaInvitationRepository.java`, `DurableInvitationRepositoryEntity.java`, `InvitationService.java`, `StarterSecurityComponents.java` | normal runtime default requiring rebinding | blocker | Bind normal invitation runtime to `AkkaInvitationRepository`/`DurableInvitationRepositoryEntity`; keep in-memory invitation adapter test/local-demo-only. Validate idempotent invite/resend/revoke/outbox paths. |
| Governed agent behavior repository has durable Akka seam but normal static registry still defaults to in-memory. | `InMemoryAgentBehaviorRepository.java`, `AkkaAgentBehaviorRepository.java`, `DurableAgentBehaviorRepositoryEntity.java`, `AgentRuntimeService.java`, `AgentRuntimeToolResolver.java`, `StarterSecurityComponents.java` | normal runtime default requiring rebinding | blocker | Bind normal prompt/skill/reference/manifest/tool-boundary runtime to Akka durable repository. Preserve governed `WorkstreamRuntimeAgent`, loader tools, and provider fail-closed behavior. |
| Agent runtime trace sink defaults to in-memory in `AgentRuntimeService` constructors. | `InMemoryAgentRuntimeTraceSink.java`, `AgentRuntimeService.java`, trace tests | normal runtime default requiring durable trace sink or explicit injection requirement | blocker | Make normal runtime require a durable trace sink or bind one from Akka runtime traces; keep in-memory sink only for unit tests. |
| Workstream log is in memory for normal item history. | `InMemoryWorkstreamLogRepository.java`, `WorkstreamService.java`, `StarterSecurityComponents.java` | normal runtime default requiring durable replacement | blocker | Add/bind durable workstream log component. Normal composer submissions, surface responses, and workstream history must survive process restart at the stated scope. |
| Audit trace repository normalizes in-memory runtime/workstream evidence. | `InMemoryAuditTraceRepository.java`, `AuditTraceService.java`, `WorkstreamService.java`, `StarterSecurityComponents.java` | normal runtime default requiring durable replacement | blocker | Add/bind durable audit trace repository/projection over durable runtime/workstream traces. Search/detail/timeline surfaces must not be sourced from volatile data. |
| Governance policy repository is in memory. | `InMemoryGovernancePolicyRepository.java`, `GovernancePolicyService.java`, `WorkstreamService.java`, `StarterSecurityComponents.java` | normal runtime default requiring durable replacement or fail-closed gated policy mutation | blocker | Add/bind durable governance policy state or block policy lifecycle actions until durable state exists. Deterministic rollback/proposal paths must remain fail closed where data is unavailable. |
| Access review task repository is in memory and constructed inside `WorkstreamService`. | `InMemoryAccessReviewTaskRepository.java`, `UserAdminAccessReviewService.java`, `UserAdminAccessReviewWorkerTest.java`, `WorkstreamService.java` | normal runtime/default worker-task state requiring durable replacement or explicit blocked state | blocker for claimed durable task behavior | Add/bind durable access-review task state or keep start/read surfaces blocked until durable AutonomousAgent/task lifecycle exists. Do not return fake progress. |
| Convenience constructors instantiate fail-closed or in-memory defaults. | `WorkstreamService.java`, `AgentRuntimeService.java` | API seam requiring safer construction | blocker when used by normal endpoints | Keep test constructors if needed but make production/static registry construction explicit and durable; name in-memory constructors/adapters as test/local-demo-only. |

## Backend acceptable test-only findings

| Finding | Paths | Classification | Notes |
|---|---|---|---|
| Unit-test fake model provider. | `backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/FakeModelProviderClient.java`, `OpenAiModelProviderClientTest.java`, `AgentRuntimeServiceTest.java`, `WorkstreamServiceTest.java` | test-only acceptable | Clearly test-scoped. Keep if not wired into normal runtime. |
| In-memory repositories in tests. | `backend/src/test/java/**` | test-only acceptable | Acceptable for unit/integration tests if production-like validation also exercises durable/fail-closed bindings. |
| Provider/model fail-closed text and no-fake worker surfaces. | `WorkstreamService.java`, seed markdown, tests | acceptable fail-closed behavior | Preserve; do not replace with deterministic success. |

## Frontend and static asset findings

| Finding | Paths | Classification | Release impact | Required remediation |
|---|---|---|---|---|
| Fixture client mode is enabled by query string and bundled into production assets. | `templates/ai-first-saas-starter/frontend/src/main.tsx`, root `frontend/src/main.tsx`, `FixtureWorkstreamApiClient.ts`, `FixtureWorkstreamRealtimeClient.ts`, static `assets/index-DUHG70zt.js` | explicit local/dev demo adapter requiring stronger gating/copy/build separation | blocker unless clearly excluded from normal build | Gate fixture mode behind explicit dev/local/demo build flag in addition to query string, or exclude it from production-like build. Normal starter path must use backend HTTP clients and AuthKit. |
| UI falls back to fixture `meTenantAdmin`, `initialWorkstreamItems`, and `canonicalSurfaceEnvelopes` while bootstrap is loading. | `frontend/src/main.tsx`, template mirror | normal UI behavior requiring correction | blocker | Replace loading fallback with loading/error state only, unless in explicit fixture mode. Do not render fixture data during normal API loading/error. |
| Static resources are committed generated assets containing fixture mode code. | `templates/ai-first-saas-starter/src/main/resources/static-resources/assets/index-DUHG70zt.js`, `index.html`, css/favicon | generated static artifact requiring regeneration/cleanup | blocker until regenerated after fixture gating | Rebuild/copy static resources after frontend gating; scan assets for fixture/demo and backend secret markers. |
| Root `frontend/` mirrors template frontend fixture mode and tests. | `frontend/src/**` | mirrored source requiring synchronization | blocker if template/root diverge | Apply the same frontend changes to root mirror or document why no sync is required. |
| Contract tests assert fixture clients in normal main entry. | `frontend.contract.test.mjs`, `workstream-composer-message-api.contract.test.mjs`, template mirrors | test expectation requiring update | blocker for frontend remediation | Rewrite tests to assert production path excludes fixture fallback by default and fixture mode is explicit/dev-only. |

## Documentation findings

| Finding | Paths | Classification | Required remediation |
|---|---|---|---|
| Prior release handoff recommends shipping and says no blockers. | `specs/full-core-smb-polish-release-readiness/release-handoff.md`, `release-readiness-verification.md` | documentation claim requiring correction | Supersede for no-in-memory-normal-runtime bar. |
| Starter README says shippable and also documents in-memory fallbacks. | `templates/ai-first-saas-starter/README.md` | documentation claim requiring correction | Mark full-core SMB release status blocked until this remediation passes; keep runtime completion doctrine explicit. |

## Remediation task plan

1. Backend durable/fail-closed foundation wiring for identity, workstream log, audit trace, governance policy, and access-review task state.
2. Backend durable binding for already-started invitation and governed-agent behavior seams plus durable agent runtime trace sink requirement.
3. Frontend fixture gating, root/template synchronization, loading fallback removal, and static asset regeneration/cleanup.
4. Broad validation and release handoff update after source remediation.
5. Terminal verification.

## Validation evidence from TASK-FCSMB-DUR-01-005

- `tools/validate-ai-first-saas-starter-fullstack.sh` passed on rendered target `/tmp/ai-first-saas-starter-fullstack.YQvBuC`, including backend tests, frontend tests/typecheck/build, static resource verification, built asset secret scan, and real provider Akka Agent smoke because `OPENAI_API_KEY` was present.
- Broad inventory scan still reports test-only/local-demo/dev-fixture/documentation hits. These are classified as acceptable because normal runtime uses Akka durable seams or fail-closed ports, local/demo adapters are explicitly named and gated, and frontend fixture mode requires dev/local opt-in.
- `rg -n "fixtureWorkstream|FixtureWorkstream|fixture|demo|InMemory|fake|model-less|OPENAI_API_KEY|WORKOS_API_KEY" templates/ai-first-saas-starter/src/main/resources/static-resources --glob '!**/*.map'` returned no matches.

## Validation commands for future tasks

- Backend focused rendered-scaffold tests after durable wiring:
  - `tools/validate-ai-first-saas-starter-fullstack.sh --keep`
  - on kept scaffold, targeted `mvn test -Dtest=MeServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest,InvitationAndUserAdminServiceTest,UserAdminAccessReviewServiceTest,GovernancePolicyServiceTest,AgentRuntimeServiceTest,AgentRuntimeTraceSinkTest,DurableAgentBehaviorRepositoryStateTest`
- Frontend fixture gating:
  - `cd templates/ai-first-saas-starter/frontend && npm test -- --run && npm run typecheck && npm run build`
  - repeat or synchronize equivalent root `frontend/` checks if root mirror is edited.
- Static asset scan:
  - `rg -n "fixtureWorkstream|FixtureWorkstream|fixture|demo|InMemory|fake|model-less|OPENAI_API_KEY|WORKOS_API_KEY" templates/ai-first-saas-starter/src/main/resources/static-resources --glob '!**/*.map'`
- Final broad check:
  - `tools/validate-ai-first-saas-starter-fullstack.sh`
