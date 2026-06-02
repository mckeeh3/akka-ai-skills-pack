# Runtime Durability Remediation Map

Date: 2026-05-30

## Summary

Initial inventory found that the stronger Akka-component-backed normal-runtime release bar was **not met**: the rendered starter wired multiple non-Akka substitute repositories as normal backend runtime defaults and shipped frontend fixture inspection paths/static assets that could be mistaken for production-like runtime.

Historical status after this remediation pass on 2026-05-30: **passed for its then-documented SMB starter scope**. That conclusion is superseded by `specs/real-akka-runtime-replacement/`, which requires real Akka component-backed normal runtime for all claimed starter workstream/foundation features. Fail-closed behavior remains appropriate for missing external provider/security configuration or unbound pre-runtime setup; it is not a substitute for Akka-backed persistence. Frontend and backend substitutes may remain only in test-only assets.

## Inventory commands used

```bash
rg -n "Substitute|Akka component-backed|mock|Mock|fake|Fake|fixture|Fixture|demo|Demo|canned|model-less|fallback|stub|Stub" templates/ai-first-saas-starter frontend specs/full-core-smb-polish-release-readiness --glob '!**/node_modules/**' --glob '!**/target/**' --glob '!**/dist/**'
find templates/ai-first-saas-starter -path '*/node_modules' -prune -o -path '*/target' -prune -o -path '*/dist' -prune -o -type f \( -iname '*Substitute*' -o -iname '*Fake*' -o -iname '*Fixture*' -o -iname '*Demo*' -o -iname '*Mock*' -o -iname '*Stub*' \) -print | sort
find frontend templates/ai-first-saas-starter/frontend templates/ai-first-saas-starter/src/main/resources/static-resources specs/full-core-smb-polish-release-readiness -path '*/node_modules' -prune -o -path '*/dist' -prune -o -path '*/target' -prune -o -type f \( -iname '*fixture*' -o -iname '*demo*' -o -iname '*mock*' -o -iname '*fake*' -o -iname '*static*' \) -print | sort
find templates/ai-first-saas-starter/src/main/resources/static-resources -maxdepth 3 -type f -print 2>/dev/null | sort | head -200
rg -n "fixtureWorkstream|FixtureWorkstream|fixture|demo|Substitute|fake|model-less|OPENAI_API_KEY|WORKOS_API_KEY" templates/ai-first-saas-starter/src/main/resources/static-resources --glob '!**/*.map'
```

## Backend normal runtime blockers

| Finding | Paths | Classification | Release impact | Required remediation |
|---|---|---|---|---|
| Static service registry wires Akka component-backed identity, invitation, agent behavior, workstream log, audit, and governance stores as normal runtime defaults. | `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/StarterSecurityComponents.java` | historical normal-runtime default requiring Akka replacement | blocker | Superseded requirement: normal runtime must bind Akka durable repositories/components for claimed features. Keep substitute adapters only in test source. |
| Identity/account/membership authorization state is through Akka components and seeded with local demo users. | `SubstituteIdentityRepository.java`, `BootstrapAdminSeeder.java`, `IdentityRepository.java`, `UserAdminService.java`, `InvitationService.java`, `StarterSecurityComponents.java` | normal runtime default requiring replacement | blocker | Add durable identity/account/membership repository or explicit fail-closed bootstrap requirement. `/api/me`, user admin, invitation acceptance, role changes, disabled-user checks, and tenant selection must not depend on volatile default state. |
| Invitation repository has durable Akka seam but normal static registry still defaults to Akka component-backed. | `SubstituteInvitationRepository.java`, `AkkaInvitationRepository.java`, `DurableInvitationRepositoryEntity.java`, `InvitationService.java`, `StarterSecurityComponents.java` | historical normal-runtime default requiring rebinding | blocker | Bind normal invitation runtime to `AkkaInvitationRepository`/`DurableInvitationRepositoryEntity`; keep substitutes test-only. Validate idempotent invite/resend/revoke/outbox paths. |
| Governed agent behavior repository has durable Akka seam but normal static registry still defaults to Akka component-backed. | `SubstituteAgentBehaviorRepository.java`, `AkkaAgentBehaviorRepository.java`, `DurableAgentBehaviorRepositoryEntity.java`, `AgentRuntimeService.java`, `AgentRuntimeToolResolver.java`, `StarterSecurityComponents.java` | normal runtime default requiring rebinding | blocker | Bind normal prompt/skill/reference/manifest/tool-boundary runtime to Akka durable repository. Preserve governed `WorkstreamRuntimeAgent`, loader tools, and provider fail-closed behavior. |
| Agent runtime trace sink defaults to Akka component-backed in `AgentRuntimeService` constructors. | `SubstituteAgentRuntimeTraceSink.java`, `AgentRuntimeService.java`, trace tests | normal runtime default requiring durable trace sink or explicit injection requirement | blocker | Make normal runtime require a durable trace sink or bind one from Akka runtime traces; keep Akka component-backed sink only for unit tests. |
| Workstream log is through Akka components for normal item history. | `SubstituteWorkstreamLogRepository.java`, `WorkstreamService.java`, `StarterSecurityComponents.java` | normal runtime default requiring durable replacement | blocker | Add/bind durable workstream log component. Normal composer submissions, surface responses, and workstream history must survive process restart at the stated scope. |
| Audit trace repository normalizes Akka component-backed runtime/workstream evidence. | `SubstituteAuditTraceRepository.java`, `AuditTraceService.java`, `WorkstreamService.java`, `StarterSecurityComponents.java` | normal runtime default requiring durable replacement | blocker | Add/bind durable audit trace repository/projection over durable runtime/workstream traces. Search/detail/timeline surfaces must not be sourced from volatile data. |
| Governance policy repository is through Akka components. | `SubstituteGovernancePolicyRepository.java`, `GovernancePolicyService.java`, `WorkstreamService.java`, `StarterSecurityComponents.java` | normal runtime default requiring durable replacement or fail-closed gated policy mutation | blocker | Add/bind durable governance policy state or block policy lifecycle actions until durable state exists. Deterministic rollback/proposal paths must remain fail closed where data is unavailable. |
| Access review task repository is through Akka components and constructed inside `WorkstreamService`. | `SubstituteAccessReviewTaskRepository.java`, `UserAdminAccessReviewService.java`, `UserAdminAccessReviewWorkerTest.java`, `WorkstreamService.java` | normal runtime/default worker-task state requiring durable replacement or explicit blocked state | blocker for claimed durable task behavior | Add/bind durable access-review task state or keep start/read surfaces blocked until durable AutonomousAgent/task lifecycle exists. Do not return fake progress. |
| Convenience constructors instantiate fail-closed or non-Akka substitute defaults. | `WorkstreamService.java`, `AgentRuntimeService.java` | API seam requiring safer construction | blocker when used by normal endpoints | Keep test constructors if needed, but production/static registry construction must be explicit and Akka-backed; substitute constructors/adapters belong in test source only. |

## Backend acceptable test-only findings

| Finding | Paths | Classification | Notes |
|---|---|---|---|
| Unit-test fake model provider. | `backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/FakeModelProviderClient.java`, `OpenAiModelProviderClientTest.java`, `AgentRuntimeServiceTest.java`, `WorkstreamServiceTest.java` | test-only acceptable | Clearly test-scoped. Keep if not wired into normal runtime. |
| In-memory repositories in tests. | `backend/src/test/java/**` | test-only acceptable | Acceptable for unit/integration tests if production-like validation also exercises durable/fail-closed bindings. |
| Provider/model fail-closed text and no-fake worker surfaces. | `WorkstreamService.java`, seed markdown, tests | acceptable fail-closed behavior | Preserve; do not replace with deterministic success. |

## Frontend and static asset findings

| Finding | Paths | Classification | Release impact | Required remediation |
|---|---|---|---|---|
| Fixture workstream selection was enabled by query string and bundled into production assets. | `templates/ai-first-saas-starter/frontend/src/main.tsx`, root `frontend/src/main.tsx`, fixture API/realtime client files, static built assets | historical frontend substitute path requiring test quarantine | blocker unless excluded from normal build | Superseded requirement: fixture clients/data must be test-only and excluded from normal runtime imports. Normal starter path must use backend HTTP clients and AuthKit. |
| UI falls back to fixture `meTenantAdmin`, `initialWorkstreamItems`, and `canonicalSurfaceEnvelopes` while bootstrap is loading. | `frontend/src/main.tsx`, template mirror | normal UI behavior requiring correction | blocker | Replace loading fallback with loading/error state only, unless in explicit fixture mode. Do not render fixture data during normal API loading/error. |
| Static resources are committed generated assets containing fixture mode code. | `templates/ai-first-saas-starter/src/main/resources/static-resources/assets/index-DUHG70zt.js`, `index.html`, css/favicon | generated static artifact requiring regeneration/cleanup | blocker until regenerated after fixture gating | Rebuild/copy static resources after frontend gating; scan assets for fixture/demo and backend secret markers. |
| Root `frontend/` mirrors template frontend fixture mode and tests. | `frontend/src/**` | mirrored source requiring synchronization | blocker if template/root diverge | Apply the same frontend changes to root mirror or document why no sync is required. |
| Contract tests asserted fixture clients in normal main entry. | `frontend.contract.test.mjs`, `workstream-composer-message-api.contract.test.mjs`, template mirrors | test expectation requiring update | blocker for frontend remediation | Rewrite tests to assert production path excludes test fixtures and normal runtime imports only real API clients. |

## Documentation findings

| Finding | Paths | Classification | Required remediation |
|---|---|---|---|
| Prior release handoff recommends shipping and says no blockers. | `specs/full-core-smb-polish-release-readiness/release-handoff.md`, `release-readiness-verification.md` | documentation claim requiring correction | Supersede for Akka-component-backed normal-runtime bar. |
| Starter README says shippable and also documents non-Akka substitute fallbacks. | `templates/ai-first-saas-starter/README.md` | documentation claim requiring correction | Mark full-core SMB release status blocked until this remediation passes; keep runtime completion doctrine explicit. |

## Remediation task plan

1. Backend durable/fail-closed foundation wiring for identity, workstream log, audit trace, governance policy, and access-review task state.
2. Backend durable binding for already-started invitation and governed-agent behavior seams plus durable agent runtime trace sink requirement.
3. Frontend fixture gating, root/template synchronization, loading fallback removal, and static asset regeneration/cleanup.
4. Broad validation and release handoff update after source remediation.
5. Terminal verification.

## Validation evidence from TASK-FCSMB-DUR-01-005

- `tools/validate-ai-first-saas-starter-fullstack.sh` passed on rendered target `/tmp/ai-first-saas-starter-fullstack.YQvBuC`, including backend tests, frontend tests/typecheck/build, static resource verification, built asset secret scan, and real provider Akka Agent smoke because `OPENAI_API_KEY` was present.
- Historical broad inventory scan still reported test-only/local-demo/dev-fixture/documentation hits. That classification is superseded for normal runtime: current guidance allows substitutes only in tests or explicitly test-only assets, while normal runtime must use Akka components for claimed features.
- `rg -n "fixtureWorkstream|FixtureWorkstream|fixture|demo|Substitute|fake|model-less|OPENAI_API_KEY|WORKOS_API_KEY" templates/ai-first-saas-starter/src/main/resources/static-resources --glob '!**/*.map'` returned no matches.

## Validation commands for future tasks

- Backend focused rendered-scaffold tests after durable wiring:
  - `tools/validate-ai-first-saas-starter-fullstack.sh --keep`
  - on kept scaffold, targeted `mvn test -Dtest=MeServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest,InvitationAndUserAdminServiceTest,UserAdminAccessReviewServiceTest,GovernancePolicyServiceTest,AgentRuntimeServiceTest,AgentRuntimeTraceSinkTest,DurableAgentBehaviorRepositoryStateTest`
- Frontend fixture gating:
  - `cd templates/ai-first-saas-starter/frontend && npm test -- --run && npm run typecheck && npm run build`
  - repeat or synchronize equivalent root `frontend/` checks if root mirror is edited.
- Static asset scan:
  - `rg -n "fixtureWorkstream|FixtureWorkstream|fixture|demo|Substitute|fake|model-less|OPENAI_API_KEY|WORKOS_API_KEY" templates/ai-first-saas-starter/src/main/resources/static-resources --glob '!**/*.map'`
- Final broad check:
  - `tools/validate-ai-first-saas-starter-fullstack.sh`
