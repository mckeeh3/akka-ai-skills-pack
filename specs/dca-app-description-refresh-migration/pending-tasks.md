# Pending Tasks

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Update task status before finishing the harness response.
- Each task should make one git commit before being marked `done`; the commit should include only that task's intended changes and this queue-status update.
- Record the task commit hash in that task's notes when practical.
- This queue is for the DCA app-description refresh migration, rooted at `specs/dca-app-description-refresh-migration/`.

## Tasks

### TASK-00-001: Create DCA refresh migration planning scaffold

- status: done
- source: user request to create a multi-sprint migration package for refreshing `docs/examples/ai-first-dca-app-description/`
- task brief: none
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - specs/ai-first-skills-pack-migration/README.md
  - specs/capability-first-backend-migration/README.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/dca-app-description-refresh-migration/README.md
  - specs/dca-app-description-refresh-migration/pending-tasks.md
  - specs/dca-app-description-refresh-migration/sprints/*.md
  - specs/dca-app-description-refresh-migration/backlog/*.md
  - specs/dca-app-description-refresh-migration/tasks/README.md
- required checks:
  - verify the package is planning-only and does not edit the DCA example itself
- done criteria:
  - migration has sprint sequence, backlogs, and initial pending queue
- notes:
  - completed: created planning scaffold for a five-sprint DCA app-description refresh migration

### TASK-01-001: Reposition DCA as vertical extension reference

- status: done
- source: specs/dca-app-description-refresh-migration/backlog/01-positioning-and-structure-build-backlog.md
- task brief: none
- depends on: [TASK-00-001]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - docs/examples/ai-first-saas-seed-app-description/README.md
  - docs/examples/ai-first-dca-app-description/README.md
  - docs/examples/ai-first-dca-app-description/app-description/README.md
  - specs/dca-app-description-refresh-migration/sprints/01-positioning-and-structure-sprint.md
- skills:
  - app-descriptions
  - ai-first-saas
- expected outputs:
  - update DCA README files to state canonical seed vs vertical extension relationship
  - remove or contextualize stale Sprint 6 wording
- required checks:
  - DCA is not presented as the canonical structural template
  - no runnable-app claim is introduced
- done criteria:
  - future agents can route to DCA as a domain-rich vertical example without confusing it with the seed baseline
- notes:
  - completed: repositioned DCA as a domain-rich vertical extension of the canonical secure AI-first SaaS seed; removed stale Sprint 6 framing and preserved non-runnable reference status
  - commit: see git history for this task

### TASK-01-002: Add current system control files

- status: done
- source: specs/dca-app-description-refresh-migration/backlog/01-positioning-and-structure-build-backlog.md
- task brief: none
- depends on: [TASK-01-001]
- required reads:
  - docs/internal-app-description-architecture.md
  - docs/app-description-maintenance-flow.md
  - docs/examples/ai-first-dca-app-description/app-description/00-system/app-manifest.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/00-system/readiness-status.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/00-system/generation-policy.md
- skills:
  - app-descriptions
  - app-description-readiness-assessment
- expected outputs:
  - docs/examples/ai-first-dca-app-description/app-description/00-system/readiness-status.md
  - docs/examples/ai-first-dca-app-description/app-description/00-system/generation-policy.md
  - updated 00-system README if needed
- required checks:
  - readiness blocks code generation until secure foundation, capability contracts, tests, integrations, thresholds, and fixtures are defined
  - generation policy keeps this as non-runnable reference unless realization is explicitly requested
- done criteria:
  - DCA system layer matches current control-file expectations
- notes:
  - completed: added DCA readiness and generation-policy control files; updated the system README to describe current non-runnable reference status and generation blockers
  - commit: see git history for this task

### TASK-01-003: Create structure gap map

- status: done
- source: specs/dca-app-description-refresh-migration/backlog/01-positioning-and-structure-build-backlog.md
- task brief: none
- depends on: [TASK-01-002]
- required reads:
  - docs/internal-app-description-architecture.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/README.md
  - docs/examples/ai-first-dca-app-description/app-description/README.md
- skills:
  - app-descriptions
- expected outputs:
  - docs/examples/ai-first-dca-app-description/app-description/80-review/structure-gap-summary.md or equivalent
- required checks:
  - gap map distinguishes intentional vertical-example omissions from refresh blockers
- done criteria:
  - later tasks have a concise map of remaining DCA alignment gaps
- notes:
  - completed: added a derived DCA structure gap summary that maps current layer shape against seed/current architecture expectations and separates intentional vertical-reference omissions from refresh blockers; updated the review README to route to it
  - commit: see git history for this task

### TASK-02-001: Add secure tenant/user foundation capability

- status: done
- source: specs/dca-app-description-refresh-migration/backlog/02-secure-saas-foundation-alignment-build-backlog.md
- task brief: none
- depends on: [TASK-01-002]
- required reads:
  - docs/core-ai-first-saas-foundation.md
  - docs/core-saas-identity-tenancy-admin.md
  - docs/core-saas-owner-tenant-billing.md
  - docs/capability-first-backend-architecture.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/01-secure-tenant-user-foundation.md
  - docs/examples/ai-first-dca-app-description/app-description/40-auth-security/*.md
- skills:
  - core-saas-foundation
  - capability-first-backend
  - app-description-capability-modeling
- expected outputs:
  - docs/examples/ai-first-dca-app-description/app-description/10-capabilities/01-secure-tenant-user-foundation.md
  - updated capabilities index entry for the foundation capability
- required checks:
  - includes Account, UserProfile, UserSettings, Membership, Role, Permission/Capability, Invitation, AuthContext, `/api/me`, AdminAuditEvent, support access, billing boundary, and tenant/customer isolation
- done criteria:
  - DCA app description has a first-class current secure SaaS foundation capability before DCA-specific capabilities
- notes:
  - completed: added the DCA secure tenant/user foundation capability as CAP-00, updated the capabilities index and README, and verified the required foundation objects and tenant/customer isolation language are present
  - commit: see git history for this task

### TASK-02-002: Align auth/security layer with foundation capability

- status: done
- source: specs/dca-app-description-refresh-migration/backlog/02-secure-saas-foundation-alignment-build-backlog.md
- task brief: none
- depends on: [TASK-02-001]
- required reads:
  - docs/examples/ai-first-dca-app-description/app-description/10-capabilities/01-secure-tenant-user-foundation.md
  - docs/examples/ai-first-dca-app-description/app-description/40-auth-security/identity-and-trust.md
  - docs/examples/ai-first-dca-app-description/app-description/40-auth-security/authorization-rules.md
  - docs/examples/ai-first-dca-app-description/app-description/40-auth-security/agent-permissions.md
  - docs/examples/ai-first-dca-app-description/app-description/40-auth-security/data-protection.md
  - docs/examples/ai-first-dca-app-description/app-description/40-auth-security/boundary-and-surface-rules.md
- skills:
  - app-description-auth-security
  - core-saas-foundation
  - ai-first-saas-audit-trace
- expected outputs:
  - refreshed auth/security files with current foundation terminology and links
- required checks:
  - WorkOS authenticates; Akka-owned local state authorizes
  - frontend navigation is never authorization
  - agent/tool permissions require backend enforcement and audit
- done criteria:
  - auth/security layer is consistent with the foundation capability and current doctrine
- notes:
  - completed: refreshed DCA auth/security README, identity/trust, authorization, agent-permission, data-protection, and boundary/surface rules around the CAP-00 foundation contract; verified WorkOS authenticates while Akka local state authorizes, frontend navigation is not authorization, and agent/tool access requires backend enforcement plus audit/work traces
  - commit: see git history for this task

### TASK-02-003: Add invitation, admin, support-access, and billing-boundary details

- status: done
- source: specs/dca-app-description-refresh-migration/backlog/02-secure-saas-foundation-alignment-build-backlog.md
- task brief: none
- depends on: [TASK-02-002]
- required reads:
  - docs/core-saas-identity-tenancy-admin.md
  - docs/core-saas-owner-tenant-billing.md
  - skills/akka-saas-invitation-onboarding/SKILL.md
  - docs/examples/ai-first-dca-app-description/app-description/40-auth-security/*.md
  - docs/examples/ai-first-dca-app-description/app-description/60-generation/implementation-slices.md
- skills:
  - akka-saas-invitation-onboarding
  - core-saas-foundation
  - app-description-auth-security
- expected outputs:
  - foundation/auth/security updates for Invitation lifecycle, admin audit, support access, and subscription/billing boundary
  - implementation-slice notes adjusted if they still imply older bootstrap-only onboarding
- required checks:
  - no self-registration of privileged users
  - local invite/onboarding behavior is explicit and auditable
- done criteria:
  - DCA foundation covers current mandatory SaaS onboarding/admin/billing boundary expectations
- notes:
  - completed: added explicit invitation/onboarding lifecycle, auditable admin operations, support-access, and SaaS Owner billing-boundary guidance; updated the foundation capability, auth/security routing, and implementation slices to forbid privileged self-registration and avoid bootstrap-only onboarding assumptions
  - commit: see git history for this task

### TASK-03-001: Refactor capability index to current capability-first shape

- status: done
- source: specs/dca-app-description-refresh-migration/backlog/03-capability-first-contracts-build-backlog.md
- task brief: none
- depends on: [TASK-02-001]
- required reads:
  - docs/capability-first-backend-architecture.md
  - docs/internal-app-description-architecture.md
  - docs/examples/ai-first-dca-app-description/app-description/10-capabilities/capabilities-index.md
- skills:
  - capability-first-backend
  - app-description-capability-modeling
- expected outputs:
  - updated capabilities index listing capability id, class, actors/callers, protected scope, and exposure surfaces
- required checks:
  - endpoints, workflows, agents, and entities are not treated as capability roots
  - foundation capability appears before DCA-specific automation
- done criteria:
  - DCA capability index matches current capability-first inventory expectations
- notes:
  - completed: refactored the DCA capability index into the current capability-first inventory shape with capability ids, classes, actors/callers, protected scopes, and exposure surfaces; preserved the secure foundation before DCA-specific automation and clarified that endpoints, workflows, agents, entities, timers, consumers, and UI actions are not capability roots
  - commit: see git history for this task

### TASK-03-002: Add detailed Supplies Autopilot capability contract

- status: done
- source: specs/dca-app-description-refresh-migration/backlog/03-capability-first-contracts-build-backlog.md
- task brief: none
- depends on: [TASK-03-001]
- required reads:
  - docs/capability-first-backend-architecture.md
  - docs/examples/ai-first-dca-app-description/app-description/10-capabilities/capabilities-index.md
  - docs/examples/ai-first-dca-app-description/app-description/20-behavior/flows/01-supplies-autopilot-flow.md
  - docs/examples/ai-first-dca-app-description/app-description/15-operating-model/policies-and-approval-gates.md
  - docs/examples/ai-first-dca-app-description/app-description/15-operating-model/decisions-exceptions-and-evidence.md
- skills:
  - capability-first-backend
  - app-description-capability-modeling
  - ai-first-saas-decision-cards
  - ai-first-saas-policy-governance
- expected outputs:
  - docs/examples/ai-first-dca-app-description/app-description/10-capabilities/03-supplies-autopilot.md
- required checks:
  - contract includes actors/callers, AuthContext, inputs/outputs, data access, side effects, idempotency, approval gates, audit/trace, exposure surfaces, and tests
  - consequential side effects default to policy-gated automation or decision-card approval
- done criteria:
  - first DCA vertical slice has a current governed capability contract
- notes:
  - completed: added the detailed CAP-03 supplies autopilot governed capability contract with actors/callers, AuthContext, schemas, data access, side effects, idempotency, approval gates, audit/trace, exposure surfaces, and tests; updated the capability README and index to route to the new file
  - commit: see git history for this task

### TASK-03-003: Add lightweight contracts for remaining DCA capabilities

- status: done
- source: specs/dca-app-description-refresh-migration/backlog/03-capability-first-contracts-build-backlog.md
- task brief: none
- depends on: [TASK-03-002]
- required reads:
  - docs/capability-first-backend-architecture.md
  - docs/examples/ai-first-dca-app-description/app-description/10-capabilities/capabilities-index.md
  - docs/examples/ai-first-dca-app-description/app-description/20-behavior/flows/02-lifecycle-and-exception-flows.md
  - docs/examples/ai-first-dca-app-description/app-description/20-behavior/state-models/01-lifecycle-foundation.md
- skills:
  - capability-first-backend
  - app-description-capability-modeling
- expected outputs:
  - lightweight capability files or expanded index sections for lifecycle, telemetry, service, billing, onboarding, offboarding, policy governance, owner command center, and audit/outcome review
- required checks:
  - planned capabilities clearly identify missing downstream details instead of inventing thresholds/integration contracts
- done criteria:
  - non-first-slice capabilities have enough shape for routing and future refinement
- notes:
  - completed: added lightweight capability contract files for lifecycle orchestration, telemetry intelligence, service coordination, meter/billing review, onboarding/installation, offboarding/retention, policy governance, owner command center, and audit/outcome review; updated the capability README and index to route to them and preserved future-detail placeholders instead of inventing thresholds or integration contracts
  - commit: see git history for this task

### TASK-03-004: Update traceability maps for capability-first and foundation links

- status: done
- source: specs/dca-app-description-refresh-migration/backlog/03-capability-first-contracts-build-backlog.md
- task brief: none
- depends on: [TASK-03-003]
- required reads:
  - docs/examples/ai-first-dca-app-description/app-description/70-traceability/ai-first-coverage-map.md
  - docs/examples/ai-first-dca-app-description/app-description/10-capabilities/*.md
  - docs/internal-app-description-architecture.md
- skills:
  - app-description-change-impact
  - capability-first-backend
- expected outputs:
  - updated or new traceability maps that connect foundation/capabilities to behavior, tests, auth/security, UI, observability, and generation slices
- required checks:
  - traceability includes secure foundation and Supplies Autopilot detailed contract
- done criteria:
  - future agents can navigate from capability to affected layers without guessing
- notes:
  - completed: refreshed the DCA traceability layer with secure foundation and CAP-03 supplies autopilot coverage; added a capability-to-layer map connecting capabilities to behavior, tests, auth/security, observability, UI, and generation slices
  - commit: see git history for this task

### TASK-04-001: Replace test placeholders with concrete test specs

- status: done
- source: specs/dca-app-description-refresh-migration/backlog/04-tests-ui-observability-readiness-build-backlog.md
- task brief: none
- depends on: [TASK-03-002]
- required reads:
  - docs/examples/ai-first-dca-app-description/app-description/30-tests/README.md
  - docs/examples/ai-first-dca-app-description/app-description/10-capabilities/01-secure-tenant-user-foundation.md
  - docs/examples/ai-first-dca-app-description/app-description/10-capabilities/03-supplies-autopilot.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/30-tests/test-index.md
- skills:
  - app-description-test-specification
  - core-saas-foundation
  - capability-first-backend
- expected outputs:
  - concrete acceptance, negative, regression, and operational DCA test files
  - updated test index/README
- required checks:
  - includes tenant isolation, disabled-user, forbidden role/scope, approval bypass, idempotency, audit/trace, frontend secret-boundary, and outcome checks
- done criteria:
  - DCA tests are no longer placeholders
- notes:
  - completed: replaced DCA test placeholders with concrete description-level acceptance, negative, regression, and operational specifications for the secure SaaS foundation and Supplies Autopilot; added a test index and refreshed readiness/traceability references to reflect remaining fixture gaps rather than placeholder tests
  - commit: see git history for this task

### TASK-04-002: Strengthen observability and trace requirements

- status: pending
- source: specs/dca-app-description-refresh-migration/backlog/04-tests-ui-observability-readiness-build-backlog.md
- task brief: none
- depends on: [TASK-04-001]
- required reads:
  - docs/examples/ai-first-dca-app-description/app-description/50-observability/audit-trace-and-outcomes.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/50-observability/*.md
  - docs/examples/ai-first-dca-app-description/app-description/10-capabilities/*.md
- skills:
  - app-description-observability
  - ai-first-saas-audit-trace
  - ai-first-saas-outcomes-metrics
- expected outputs:
  - updated observability layer, split into current files if useful
- required checks:
  - captures foundation security events and DCA work/decision/policy/tool/data-access/outcome traces
- done criteria:
  - observability requirements are generation-ready enough for future slices
- notes: []

### TASK-04-003: Reconcile UI surfaces and style guide

- status: pending
- source: specs/dca-app-description-refresh-migration/backlog/04-tests-ui-observability-readiness-build-backlog.md
- task brief: none
- depends on: [TASK-03-004]
- required reads:
  - docs/examples/ai-first-dca-app-description/app-description/55-ui/ui-surfaces.md
  - docs/examples/ai-first-dca-app-description/app-description/55-ui/style-guide.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/*.md
  - docs/web-ui-style-guide.md
- skills:
  - app-description-ui
  - ai-first-saas-ui-surfaces
- expected outputs:
  - updated UI docs that remove stale placeholder wording and link surfaces to capabilities/tests/API needs
- required checks:
  - mandatory foundation administration UI surfaces remain present alongside DCA operational surfaces
  - style guide is selected and not contradicted by `ui-surfaces.md`
- done criteria:
  - UI layer is current and internally consistent
- notes: []

### TASK-04-004: Update readiness summary after tests/UI/observability refresh

- status: pending
- source: specs/dca-app-description-refresh-migration/backlog/04-tests-ui-observability-readiness-build-backlog.md
- task brief: none
- depends on: [TASK-04-001, TASK-04-002, TASK-04-003]
- required reads:
  - docs/examples/ai-first-dca-app-description/app-description/00-system/readiness-status.md
  - docs/examples/ai-first-dca-app-description/app-description/80-review/structure-gap-summary.md
  - docs/examples/ai-first-dca-app-description/app-description/30-tests/**/*
  - docs/examples/ai-first-dca-app-description/app-description/55-ui/*
  - docs/examples/ai-first-dca-app-description/app-description/50-observability/*
- skills:
  - app-description-readiness-assessment
  - app-description-readiness-summary
- expected outputs:
  - updated readiness status and latest readiness summary
- required checks:
  - readiness does not claim runnable-code readiness unless remaining integration/evaluation questions are answered
- done criteria:
  - current readiness accurately reflects the refreshed reference state
- notes: []

### TASK-05-001: Final DCA refresh consistency review

- status: pending
- source: specs/dca-app-description-refresh-migration/backlog/05-final-consistency-and-realization-prep-build-backlog.md
- task brief: none
- depends on: [TASK-04-004]
- required reads:
  - specs/dca-app-description-refresh-migration/README.md
  - docs/examples/ai-first-dca-app-description/README.md
  - docs/examples/ai-first-dca-app-description/app-description/**/*
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
- skills:
  - app-descriptions
  - capability-first-backend
  - ai-first-saas
- expected outputs:
  - consistency review notes under specs/dca-app-description-refresh-migration/ or app-description/80-review/
  - small corrective edits if needed
- required checks:
  - no stale Sprint 6 framing remains unless documented as provenance
  - no placeholder test/UI/readiness contradiction remains
  - no guidance presents DCA as canonical seed baseline
- done criteria:
  - refreshed DCA example is coherent as a vertical reference
- notes: []

### TASK-05-002: Update realization prep and future-slice handoff

- status: pending
- source: specs/dca-app-description-refresh-migration/backlog/05-final-consistency-and-realization-prep-build-backlog.md
- task brief: none
- depends on: [TASK-05-001]
- required reads:
  - docs/examples/ai-first-dca-app-description/app-description/60-generation/implementation-slices.md
  - docs/examples/ai-first-dca-app-description/app-description/10-capabilities/*.md
  - docs/examples/ai-first-dca-app-description/app-description/30-tests/**/*
  - specs/ai-first-skills-pack-migration/sprints/08-executable-ai-first-reference-slice-sprint.md
- skills:
  - app-generate-app
  - akka-solution-decomposition
  - capability-first-backend
- expected outputs:
  - updated implementation-slices handoff that points future realization tasks to current foundation, capability, test, UI, and trace files
  - optional future pending-task recommendations for executable reference slices
- required checks:
  - handoff remains planning/reference only and does not start code generation
- done criteria:
  - future executable DCA work has a current, scoped starting point
- notes: []
