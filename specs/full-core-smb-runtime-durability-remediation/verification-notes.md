# Runtime Durability Remediation Verification Notes

Date: 2026-05-30

## Result

The mini-project is historically complete for its original stated scope. Its gated/fail-closed compromise is superseded by `specs/real-akka-runtime-replacement/`, which raises the current bar to real Akka component-backed normal runtime for all claimed starter workstream/foundation features.

## Done-state comparison

| Done-state item | Verification |
|---|---|
| All non-Akka substitute/mock/fixture/demo paths inventoried and classified | Complete historically. `runtime-durability-remediation-map.md` records the inventory, classifications, remediation plan, and post-remediation validation evidence; current guidance supersedes any classification that allowed non-Akka normal runtime substitutes for claimed features. |
| Normal generated runtime no longer silently depends on non-Akka substitute stores for claimed-complete features, or fails closed | Superseded. The current bar requires Akka component-backed normal runtime for claimed starter features. Fail-closed behavior is for missing external provider/security configuration or unbound pre-runtime setup, not for substituting internal persistence. |
| Tests may use fakes/fixtures only when clearly test scoped | Current. Remaining substitutes are acceptable only as test-only assets or adapters. |
| Frontend fixture paths are quarantined from production-like runtime | Superseded by stricter guidance. Fixture clients/data must be test-only and normal UI must use real HTTP/realtime clients. |
| Static generated assets are clean/regenerated | Complete. Fullstack validation rebuilt static resources and the final static scan returned no matches. |
| Release-readiness docs are corrected and explicit | Complete. `templates/ai-first-saas-starter/README.md`, `specs/full-core-smb-polish-release-readiness/release-handoff.md`, and `specs/full-core-smb-polish-release-readiness/release-readiness-verification.md` recommend shipping for the documented scope including the stronger durability bar, while preserving explicit boundaries and deferrals. |
| Broad starter validation passes after remediation or blockers are bounded | Complete. `tools/validate-ai-first-saas-starter-fullstack.sh` passed on rendered target `/tmp/ai-first-saas-starter-fullstack.9nYyN9`, including backend tests, frontend tests/typecheck/build, static resource checks, and real provider Akka Agent smoke because provider configuration was present. |

## Final validation commands

```bash
git diff --check
rg -n "new Substitute(Identity|WorkstreamLog|AuditTrace|GovernancePolicy|AccessReviewTask|Invitation|AgentBehavior|AgentRuntimeTrace)|Substitute(Identity|WorkstreamLog|AuditTrace|GovernancePolicy|AccessReviewTask|Invitation|AgentBehavior)Repository|SubstituteAgentRuntimeTraceSink" templates/ai-first-saas-starter/backend/src/main/java
rg -n "fixtureWorkstream|FixtureWorkstream|fixture|demo|Substitute|fake|model-less|OPENAI_API_KEY|WORKOS_API_KEY" templates/ai-first-saas-starter/src/main/resources/static-resources --glob '!**/*.map'
tools/validate-ai-first-saas-starter-fullstack.sh
rg -n "Substitute|Akka component-backed|mock|Mock|fake|Fake|fixture|Fixture|demo|Demo|canned|model-less|fallback|stub|Stub" templates/ai-first-saas-starter frontend specs/full-core-smb-polish-release-readiness --glob '!**/node_modules/**' --glob '!**/target/**' --glob '!**/dist/**'
```

## Notes

- The targeted backend normal-runtime Akka component-backed scan returned no matches.
- The static resource fixture/demo/provider-secret scan returned no matches.
- Any broad inventory scan hits must now be interpreted under the stricter real-Akka-runtime bar: substitutes are acceptable only in tests or explicitly test-only assets, never as normal runtime for claimed starter features.
