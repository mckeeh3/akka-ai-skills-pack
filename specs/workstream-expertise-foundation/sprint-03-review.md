# Sprint 03 Review: Seed and Starter User Admin Expertise Example

## Review outcome

Sprint 03 is complete. `user-admin-agent` is now a sufficient canonical workstream expertise example for downstream generated-app guidance and for Sprint 04 planning integration.

The example is sufficient as a seed/starter contract because it shows the complete expertise shape in app-description, starter seed resources, frontend workstream fixtures, and contract tests:

- authoritative app-description expert bundle;
- seeded prompt, procedural skills, reference documents, compact manifests, and tool boundary metadata;
- manifest display without full skill/reference bodies;
- authorized `readSkill(skillId)` and `readReferenceDoc(referenceId)` examples;
- denied unassigned loads and missing-boundary denials;
- trace expectations for prompt assembly, skill loads, reference loads, agent work, and admin audit;
- test/contract checks for the User Admin expertise surface.

Sprint 04 may proceed.

## Evidence reviewed

### Doctrine and readiness fit

- `docs/workstream-expertise-model.md` defines the workstream expert bundle contract, distinguishing `SkillDocument`, `ReferenceDocument`, capability contracts, `ToolPermissionBoundary`, compact expertise manifests, loaders, traces, seed/import policy, and tests.
- `docs/agent-coverage-matrix.md` includes governed workstream reference-document coverage, reference-load trace expectations, and the current cleanup backlog item for eventual executable first-class `ReferenceDocument` / `AgentReferenceManifest` runtime coverage.
- `skills/app-description-readiness-assessment/SKILL.md` blocks functional-agent readiness unless workstream expertise artifacts, manifests, boundaries, traces, and tests exist or are explicitly deferred.
- `skills/akka-agent-testing/SKILL.md` requires assigned/unassigned skill and reference loads, separate `read_skill` / `read_reference` boundary denials, trace emission, and no authority expansion from text.

### App-description evidence

- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md` makes `workstream-expertise/user-admin-agent.md` authoritative for `user-admin-agent` and summarizes its responsibilities, three canonical surfaces, callable capabilities, denial obligations, and trace/test coverage.
- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/workstream-expertise/user-admin-agent.md` concretely defines `user-admin-agent.expertise` with:
  - prompt intent;
  - six procedural `SkillDocument` entries;
  - six governed `ReferenceDocument` entries;
  - compact `AgentSkillManifest` and `AgentReferenceManifest` expectations;
  - `readSkill(skillId)` and `readReferenceDoc(referenceId)` loader semantics;
  - capability/tool-boundary mapping;
  - required denials and safe recovery;
  - surface evidence;
  - `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, and `AdminAuditEvent` requirements;
  - seed/upgrade policy;
  - test obligations.
- `docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/05-managed-agent-foundation.md` owns the managed runtime capability for `AgentDefinition`, prompts, skills, references, skill/reference manifests, boundaries, loaders, traces, seed imports, approval gates, and tests.
- `docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/functional-agent-to-capability-map.md` links User Admin expertise to its surfaces and capability families.
- `docs/examples/ai-first-saas-seed-app-description/app-description/30-tests/test-index.md` requires compact manifests, assigned skill/reference loads, denied loads, boundary denials, no authority expansion, capability authorization, surface rendering, and trace emission.

### Seed and starter evidence

- `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/manifest.properties` declares:
  - `expertBundleId=user-admin-agent.expertise`;
  - `skillManifestId=manifest-user-admin`;
  - `referenceManifestId=reference-manifest-user-admin`;
  - `toolBoundaryId=tool-boundary-user-admin`;
  - six User Admin skill resources;
  - six User Admin reference resources;
  - resource checksums for prompt, skills, and references.
- `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/user-admin-agent-expertise.yaml` declares compact-manifest-only prompt assembly, skills, references, loader grants, required denials, trace types, and tests.
- Starter seed resource files exist for all six skills and all six references under `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/`.
- `src/main/resources/agent-behavior-seeds/reference-v1/manifest.properties` remains the executable reference seed path for the narrower Java example. It does not yet implement first-class reference documents, which is acceptable for this sprint because the starter seed now carries the canonical User Admin expertise content and the remaining first-class runtime reference gap is already recorded in the agent coverage cleanup backlog.

### Frontend fixture and contract-test evidence

- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts` exposes User Admin dashboard/list/detail surfaces, capability-backed actions, scope variants, denials, trace ids, and `expertiseManifest` metadata with `compactManifestOnly: true`.
- The same fixture includes authorized load examples for `readSkill(skillId)` and `readReferenceDoc(referenceId)`, denied unassigned skill/reference loads, missing `read_reference` boundary denial, and the authority rule that `SkillDocument` and `ReferenceDocument` text cannot grant roles, tenant scope, tool access, approval rights, or backend capabilities.
- `templates/ai-first-saas-starter/frontend/src/workstream-user-admin-expertise.contract.test.mjs` verifies:
  - seed manifest and bundle contain skill/reference manifest ids and tool-boundary ids;
  - fixture manifest display omits full bodies;
  - assigned skill/reference loads and traces are represented;
  - unassigned loads and tool-boundary denials are represented;
  - prompt/skill/reference/work/audit traces are visible.

## Readiness judgment

`user-admin-agent` is expertise-ready as the canonical seed/starter example for this skills-pack planning stream.

This does not mean every generated app has executable first-class reference-document persistence by default. It means the pack now has a concrete canonical contract that agents can copy into app descriptions, seed resources, fixtures, and tests without reverting to prompt-only or generic-chatbot behavior.

## Remaining refinement areas

No Sprint 04 blocker was found.

Refinements to keep visible for later hardening:

1. Add executable first-class `ReferenceDocument` / `AgentReferenceManifest` runtime coverage when the repository implements that Java runtime seam; this remains tracked by `docs/agent-coverage-matrix.md` cleanup backlog item 5.
2. Consider extending the starter backend `AgentBehaviorRepositoryState` in a future implementation sprint to persist references separately from skills if/when the starter moves beyond seed/fixture contract coverage.
3. Preserve the distinction between seed/starter contract tests and full runtime tests in future planning guidance, so generated backlogs create implementation tasks for both where in scope.

## Queue adjustments

No new pending tasks are required before Sprint 04. Existing TASK-WEF-04-001 can consume this review and align PRD/spec/backlog generation with explicit workstream expertise tasks.

## Checks

- `git diff --check`: passed.
