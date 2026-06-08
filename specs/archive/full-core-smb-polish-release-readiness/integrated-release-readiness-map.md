# Integrated Release-Readiness Map

## Purpose

This map converts the completed full-core SMB workstream implementation maps into bounded release-readiness tasks for `templates/ai-first-saas-starter/`. It is an inspection and planning artifact; it does not claim release readiness until the appended validation, polish, audit, documentation, and verification tasks pass.

## Discovery commands used

```bash
find specs/full-core-smb-polish-release-readiness -maxdepth 3 -type f -print | sort
find templates/ai-first-saas-starter -path '*/node_modules' -prune -o -type f -print | sort | rg -n "(validate|package|README|frontend|backend|WorkstreamService|test|api|surface|secret|provider|trace|release|fullstack|smoke)" | head -200
find tools -maxdepth 2 -type f -print | sort | rg -n "(validate|smoke|prove|secret|starter|fullstack|workstream|frontend)"
rg -n "fullstack|visual|provider|fail-closed|system_message|trace|secret|hidden prompt|User Admin|Agent Admin|Audit/Trace|Governance/Policy|My Account|release" specs/full-core-smb-polish-release-readiness templates/ai-first-saas-starter/README.md tools templates/ai-first-saas-starter/frontend/src templates/ai-first-saas-starter/backend/src/test --glob '!**/node_modules/**' | head -250
rg -n '"(test|typecheck|build|lint)"' templates/ai-first-saas-starter/frontend/package.json
find templates/ai-first-saas-starter/frontend/src -maxdepth 1 -type f -name '*test.mjs' -print | sort
```

## Source and validation boundaries

### Canonical starter source

- Starter root: `templates/ai-first-saas-starter/`
- Backend source/tests: `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/`, `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/`
- Frontend source/tests: `templates/ai-first-saas-starter/frontend/src/`
- Frontend package commands: `npm test -- --run`, `npm run typecheck`, `npm run build`
- Scaffold/runtime validation scripts: `tools/validate-ai-first-saas-starter-fullstack.sh`, `tools/smoke-ai-first-saas-starter-real-model.sh`, `tools/prove-workstream-icons-v0.sh`
- Release packaging scripts: `tools/build-pack.sh`, `tools/release.sh`, `tools/check-version-consistency.sh`

### Workstream implementation-map inputs

- User Admin: `specs/full-core-smb-user-admin/user-admin-vertical-contracts.md`
- Agent Admin: `specs/full-core-smb-agent-admin/agent-admin-implementation-map.md`
- Audit/Trace: `specs/full-core-smb-audit-trace/audit-trace-implementation-map.md`
- Governance/Policy: `specs/full-core-smb-governance-policy/governance-policy-implementation-map.md`
- My Account: `specs/full-core-smb-my-account/my-account-implementation-map.md`
- Shared contracts: `specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md`
- Visual standard: `specs/full-core-smb-saas-hardening/visual-ux-quality-standard.md`

## Current release-readiness interpretation

The starter now contains concrete full-core assets that earlier implementation maps marked as missing or planned, including focused services and evidence tools for Agent Admin, Audit/Trace, Governance/Policy, My Account, and User Admin access review. The release-readiness task group should therefore validate the integrated product rather than append another workstream feature queue.

Release blockers are limited to issues that prevent the SMB full-core baseline from being truthfully shipped:

- broad scaffold validation fails without a bounded, documented cause;
- normal user-facing model-backed paths do not invoke the governed Akka Agent runtime or fail closed when provider config is absent;
- browser-visible assets or DTOs leak provider credentials, backend secrets, hidden prompt text, cross-tenant data, or unauthorized evidence;
- shell, action, trace, or surface contracts diverge across workstreams enough to confuse or misrepresent authority;
- visual UX regresses to page-first CRUD, generic dashboards, hidden critical state, inaccessible controls, or fixture-only claims;
- starter docs overclaim full-core readiness, provider behavior, worker behavior, or release status.

Intentional deferrals are non-blocking when they are explicitly documented and no runtime UI claims they are complete:

- enterprise IAM/SCIM/SSO administration, SIEM/legal hold/e-discovery, compliance suites, marketplace prompts, arbitrary tenant-managed tool binding, and policy-as-code authoring;
- optional durable background workers such as personal digest, audit-summary, behavior-review, and policy-impact analysis unless a visible runtime path claims success;
- provider-backed smoke when `OPENAI_API_KEY` is unavailable, provided the skip state is loud and provider-missing paths fail closed.

## Appended task plan

1. `TASK-FCSMB-REL-01-002` runs broad scaffold/fullstack validation plus focused workstream test suites and records exact results or blockers in `validation-results.md`.
2. `TASK-FCSMB-REL-01-003` reviews visual UX and shell/surface consistency against the shared visual standard and records bounded blocker/follow-up findings in `visual-ux-polish-review.md`.
3. `TASK-FCSMB-REL-01-004` audits provider fail-closed behavior, trace/action/navigation consistency, and no-secret/no-hidden-prompt boundaries, then records findings in `provider-trace-secret-audit.md`.
4. `TASK-FCSMB-REL-01-005` updates starter/release documentation as needed and writes `release-handoff.md` with validation evidence, intentional deferrals, known blockers, and ship/no-ship recommendation.
5. `TASK-FCSMB-REL-99-001` verifies the task group and appends bounded follow-up tasks before a new terminal verification task if any blocker remains.

## Validation command set for future tasks

Use the smallest command set that matches the task, but the release-readiness group should cover these before verification:

```bash
tools/validate-ai-first-saas-starter-fullstack.sh

tools/prove-workstream-icons-v0.sh

env -u OPENAI_API_KEY tools/smoke-ai-first-saas-starter-real-model.sh

cd templates/ai-first-saas-starter/backend && mvn test -Dtest=MeServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest,InvitationAndUserAdminServiceTest,UserAdminAccessReviewServiceTest,GovernancePolicyServiceTest

cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest,AgentRuntimeTraceEntityTest,AgentRuntimeTraceViewTest,AgentRuntimeTraceSinkTest,DurableAgentBehaviorRepositoryStateTest,ManifestBoundaryEntityTest,ManifestBoundaryViewTest

cd templates/ai-first-saas-starter/frontend && npm test -- --run
cd templates/ai-first-saas-starter/frontend && npm run typecheck
cd templates/ai-first-saas-starter/frontend && npm run build

rg -n "My Account|User Admin|Agent Admin|Audit/Trace|Governance/Policy|system_message|blocked_provider_or_runtime|fail-closed|ToolPermissionBoundary|readSkill|readReferenceDoc|AgentWorkTrace|PromptAssemblyTrace|trace|tenant|provider|secret|hidden prompt|open_authorized_workstream|EvidenceTools" templates/ai-first-saas-starter --glob '!**/node_modules/**'

git diff --check
```

## Release handoff candidates

- `templates/ai-first-saas-starter/README.md` may need updates if validation reveals current docs underclaim or overclaim full-core readiness.
- `specs/full-core-smb-polish-release-readiness/validation-results.md` should capture command, date, result, provider-env mode, and retained logs/artifacts when available.
- `specs/full-core-smb-polish-release-readiness/visual-ux-polish-review.md` should classify visual findings as blocker, non-blocking polish, or post-release recommendation.
- `specs/full-core-smb-polish-release-readiness/provider-trace-secret-audit.md` should record static scans and provider/trace fail-closed findings.
- `specs/full-core-smb-polish-release-readiness/release-handoff.md` should be the final user-facing release-readiness summary for this mini-project.

## Initial recommendation

Proceed with validation before changing product code. The current source tree appears to contain the expected focused service/evidence-tool boundaries for all five workstreams, so release readiness should be proven or blocked by integrated validation, visual review, provider/trace/secret audit, and documentation accuracy rather than by adding new feature scope.
