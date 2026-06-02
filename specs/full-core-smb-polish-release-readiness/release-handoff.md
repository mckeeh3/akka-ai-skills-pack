# Full-Core SMB Release Handoff

Date: 2026-05-30

## Release recommendation

Recommendation: **ship for the documented full-core SMB starter scope, including the stronger Akka-component-backed normal-runtime bar**.

The original release-readiness task group found **no release blockers** across integrated validation, visual UX polish, provider/runtime fail-closed behavior, trace/navigation semantics, evidence-tool authorization, or browser-visible secret boundaries. The follow-up runtime durability remediation then removed or gated normal runtime non-Akka substitute/default fixture dependencies and revalidated the rendered starter. A later AutonomousAgent runtime integration slice added the first durable internal/background worker vertical: User Admin access-review investigation through an Akka `AutonomousAgent` task path with backend lifecycle projection, v3 events, attention, and result-review surfaces.

The stronger durability bar is now met at the documented scope: normal completed runtime paths either bind Akka durable components or fail closed with actionable guidance; explicit local/demo repositories require `AI_FIRST_SAAS_LOCAL_DEMO_REPOSITORIES=true`; frontend fixture mode requires dev/local opt-in; production-like static resources scan clean for fixture/demo/provider-secret markers. The remediation map is `specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md`.

This recommendation remains scoped to `templates/ai-first-saas-starter/` after scaffold rendering. The template source itself contains placeholders and is not intended to run Maven directly before rendering.

## Evidence summary

| Area | Result | Evidence |
|---|---|---|
| Fullstack scaffold validation | Pass | `tools/validate-ai-first-saas-starter-fullstack.sh` rendered the starter, ran backend tests, frontend tests/typecheck/build, verified static resources, scanned built assets, and ran real-provider smoke when `OPENAI_API_KEY` was present. Latest durability-remediation validation passed on 2026-05-30 against `/tmp/ai-first-saas-starter-fullstack.YQvBuC`; later autonomous-agent smoke readiness validated provider-skip full tests and configured real-provider smoke after fixing a stale trace-sink assertion in `RealModelProviderSmokeTest`. |
| Kept rendered scaffold validation | Pass | `tools/validate-ai-first-saas-starter-fullstack.sh --keep` repeated fullstack validation and retained `/tmp/ai-first-saas-starter-fullstack.ASsYXm` for focused backend commands. |
| Workstream icon proof | Pass | `tools/prove-workstream-icons-v0.sh` verified descriptor-backed top-rail icons for User Admin, Agent Admin, Audit/Trace, and Governance/Policy while My Account remains launched from the lower-left signed-in user tile. |
| Provider-missing smoke mode | Expected skip | `env -u OPENAI_API_KEY tools/smoke-ai-first-saas-starter-real-model.sh` skipped loudly with enablement guidance rather than producing model-less success. |
| Focused backend workstream/admin/governance tests | Pass on rendered scaffold | `env -u ADMIN_USERS mvn test -Dtest=MeServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest,InvitationAndUserAdminServiceTest,UserAdminAccessReviewServiceTest,GovernancePolicyServiceTest` passed. |
| Focused backend governed-agent runtime tests | Pass on rendered scaffold | `env -u ADMIN_USERS mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest,AgentRuntimeTraceEntityTest,AgentRuntimeTraceViewTest,AgentRuntimeTraceSinkTest,DurableAgentBehaviorRepositoryStateTest,ManifestBoundaryEntityTest,ManifestBoundaryViewTest` passed. |
| Frontend tests/typecheck/build | Pass | `cd templates/ai-first-saas-starter/frontend && npm test -- --run && npm run typecheck && npm run build` passed; Vite reported a non-blocking chunk-size warning. |
| Visual UX and cross-workstream polish | Pass | Source/test review covered all five workstreams, shell, system messages, provider-blocked states, trace links, responsive/accessibility contracts, and structured surfaces; no visual release blockers found. |
| Provider/trace/secret boundary audit | Pass | Static scans and source review found no deterministic/model-less normal runtime substitute, no browser-visible backend/provider secrets, no hidden prompt exposure, and no trace/navigation/evidence-tool blocker. |
| User Admin access-review AutonomousAgent vertical | Pass | `specs/autonomous-agent-runtime-integration/runtime-validation-evidence.md` records rendered-scaffold backend `mvn -q test`, frontend tests/typecheck/build, concrete `AutonomousAgent` task APIs, `worker.task.*`/`workflow.access_review.*` events, `autonomous_task` refs, worker-task attention, provider fail-closed behavior, and no fake/model-less normal success. |

## Runtime and provider position

- Normal model-backed workstream message submission must use the governed Akka Agent runtime path: active managed configuration resolution, `WorkstreamRuntimeAgent`, governed loader tools, `ToolPermissionBoundary`, `effects().tools(runtimeTools)`, trace emission, and provider-backed response generation.
- Missing or blank provider configuration must fail closed with actionable `system_message`/provider-blocked behavior and trace/correlation references. It must not return deterministic, mock, canned, model-less, or fixture-backed normal success responses.
- Provider smoke has two valid modes:
  - with `OPENAI_API_KEY` set, the fullstack validation can run a targeted real-model smoke through the rendered scaffold, including `ComponentClient`-backed `WorkstreamRuntimeAgent` invocation, provider-backed `markdown_response`, prompt/model/work trace shape, and provider-secret redaction checks;
  - with `OPENAI_API_KEY` unset, the standalone smoke command skips loudly and explains how to enable real provider validation.
- Troubleshoot configured real-provider failures from a kept scaffold with `mvn -DrealModelProviderSmoke=true -Dtest=RealModelProviderSmokeTest test`; assertion failures must be fixed or documented, not reclassified as success, and absent provider configuration must remain fail-closed/no-fake-success.

## Intentional deferrals and non-blocking recommendations

These are not release blockers because the starter does not claim them as completed runtime behavior:

- Enterprise IAM/SCIM/SSO administration, SIEM/legal hold/e-discovery, compliance suites, marketplace prompts, arbitrary tenant-managed tool binding, and policy-as-code authoring remain out of the SMB baseline release scope.
- Optional durable background workers such as personal digest, audit-summary, behavior-review, and policy-impact analysis remain post-release candidates unless a future visible runtime path claims completion. User Admin access-review analysis is no longer only a candidate at the starter/reference scope: it has a bounded Akka `AutonomousAgent` runtime path, while any broader team/delegation/access-remediation worker expansion remains future work.
- Richer full-core structured surfaces can continue to evolve beyond the current v0/workstream response baseline, provided fixture/demo surfaces remain labeled and do not replace governed model-backed runtime paths.
- Manual viewport QA for mobile/off-canvas rail interactions is recommended before a public announcement, but source contracts and tests did not reveal a blocker.
- A final rendered production asset scan is recommended after any future docs/source changes, even though current source/static scans and fullstack validation found no secret leaks.
- Frontend bundle-size optimization can be tracked post-release; the current Vite chunk-size warning is non-blocking.

## Environmental notes for future validators

- Do not run focused Maven commands from `templates/ai-first-saas-starter/backend` before scaffold rendering; placeholder values such as `{{MAVEN_GROUP_ID}}` are intentionally unresolved in template source.
- For focused backend commands on a rendered scaffold that include `AdminEndpointIntegrationTest`, unset or explicitly control `ADMIN_USERS` unless testing custom bootstrap behavior. Inherited harness values can replace deterministic test seeds and cause expected authorization failures.
- Keep backend secrets and provider values out of `frontend/.env*`, `frontend/public`, `frontend/index.html`, Vite config, and built static assets.

## Documentation status

`templates/ai-first-saas-starter/README.md` now records the current release-readiness status, validation commands, provider fail-closed expectations, durability boundaries, explicit local/demo gating, the implemented User Admin access-review AutonomousAgent vertical, and post-release deferrals. It does not claim deterministic/model-less runtime completion for model-backed behavior.

## Next step

For AutonomousAgent runtime integration, proceed to terminal verification: `TASK-AAI-99-001: Verify AutonomousAgent runtime integration`.
