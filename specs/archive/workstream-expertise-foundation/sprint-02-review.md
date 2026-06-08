# Sprint 02 Review: Governed Runtime Expertise Loading

## Verdict

Sprint 02 is complete enough for Sprint 03 seed/example work to proceed.

The runtime guidance now treats a workstream expert bundle as an enforceable runtime input: prompt assembly renders only compact skill/reference entries, full bodies load through authorized tools, `ToolPermissionBoundary` remains authoritative, and allowed/denied loads are traceable. No blocking runtime-guidance gaps remain for modeling the User Admin workstream expertise example in Sprint 03.

## Completed Sprint 02 outputs

- `specs/workstream-expertise-foundation/runtime-expertise-gap-matrix.md` identified missing runtime support for reference documents, compact expertise manifests, loader authorization, seed import, boundary enforcement, traces, and tests.
- `skills/akka-agent-reference-governance/SKILL.md` now defines first-class governed reference-document guidance with `ReferenceDocument`, `ReferenceVersion`, `AgentReferenceManifest`, `readReferenceDoc(referenceId)`, denied-load semantics, `ReferenceLoadTrace`, governance surfaces, and tests.
- `docs/agent-runtime-invocation-pattern.md` now resolves an active workstream expert bundle, renders a compact expertise manifest with separate skill/reference sections, registers `readSkill` and `readReferenceDoc`, enforces `read_skill` and `read_reference` grants, and emits prompt/load/work traces.
- `skills/akka-agent-skill-governance/SKILL.md` now positions `AgentSkillManifest` as the procedural-skill section of the broader expertise manifest and explicitly separates skills from references.
- `skills/akka-agent-seed-documents/SKILL.md` now includes reference documents, reference manifests, workstream expert bundles, seed provenance/checksums, idempotent import, customization-preserving upgrades, and runtime use of governed records only.
- `skills/akka-agent-tool-boundaries/SKILL.md` now distinguishes `read_skill` and `read_reference` grants and denies authority expansion through prompt, skill, reference, or manifest text.
- `docs/agent-coverage-matrix.md` and `skills/akka-agent-testing/SKILL.md` now require compact reference-manifest prompt entries, assigned/unassigned reference load tests, missing `read_reference` boundary denials, `ReferenceLoadTrace`, and text-cannot-grant-authority regressions.

## Governance decisions

- Reference documents are now modeled as first-class governed runtime artifacts for guidance purposes: `ReferenceDocument`, `ReferenceVersion`, `AgentReferenceManifest`, `readReferenceDoc(referenceId)`, and `ReferenceLoadTrace` are the preferred pattern.
- A constrained interim implementation may still store references in a generalized governed-document table, but it must preserve `documentKind=reference`, separate manifests, separate loader authorization, separate traces, and separate `read_reference` boundary grants.
- `readSkill(skillId)` remains the procedural guidance loader. It does not imply reference access.
- `readReferenceDoc(referenceId)` is not a filesystem/classpath/URL read or broad search. It is a governed loader for assigned, approved, scoped reference ids only.
- Skill/reference/prompt text and manifest labels are never authority sources; backend capabilities, AuthContext, policies, approval rules, and `ToolPermissionBoundary` remain authoritative.

## Readiness for Sprint 03

Sprint 03 can proceed with the User Admin seed/app-description example using the following runtime contract:

1. Define a concrete `user-admin-agent` workstream expert bundle under the starter core app-description.
2. List separate User Admin procedural skills and factual/process references.
3. Include compact manifest entries only: ids, titles/summaries, when-to-use or when-to-consult hints, version policy, and authority notes.
4. Map assigned capabilities and surfaces without granting authority through the expertise text.
5. Require explicit `read_skill` and `read_reference` tool-boundary grants.
6. Specify allowed and denied loads, including unassigned, inactive, cross-tenant, wrong-customer, redaction/token-limit, and missing-boundary denial cases.
7. Link expected `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, and audit events.
8. Add or reference tests for manifest display, authorized loads, denied loads, tool-boundary denial, and no authority expansion from text.

## Remaining refinement areas

- Sprint 03 should provide the first canonical User Admin expertise artifact and seed resources; Sprint 02 intentionally stopped at runtime guidance.
- Executable first-class `ReferenceDocument` / `AgentReferenceManifest` Java examples remain future implementation coverage; Sprint 03's User Admin example is the planned canonical path.
- UI/governance surfaces for reference catalogs, denied-load history, evidence display, and trace links are specified as requirements but still need concrete seed/example realization.
- Later planning integration should ensure new domain-specific LLM-enabled workstreams automatically get tasks for expert bundles, references, manifests, boundaries, loaders, surfaces, and tests.

## Queue adjustments

No task reordering is required.

`TASK-WEF-03-001` is unblocked by Sprint 02 completion and should model the User Admin workstream expertise bundle in the starter core app-description before seed resources or tests are added.

## Review checks

Required check:

```text
git diff --check
```

Text-search evidence for Sprint 02 runtime readiness should cover compact expertise manifest assembly, `readReferenceDoc`, denied reference loads, `ReferenceLoadTrace`, separate `read_reference` boundary grants, seed import of reference artifacts, and no authority expansion from expertise text.
