# Sprint 02: Governed Runtime Expertise Loading

## Objective

Extend governed agent runtime guidance so a workstream expert's knowledge is loaded through approved manifests and audited tools, not by broad static prompt stuffing or ungoverned file reads.

## Scope

Likely source files:

- `docs/ai-first-saas-application-architecture.md`
- `docs/agent-runtime-invocation-pattern.md`
- `docs/agent-coverage-matrix.md`
- `skills/akka-agent-skill-governance/SKILL.md`
- `skills/akka-agent-harness-skills/SKILL.md`
- `skills/akka-agent-seed-documents/SKILL.md`
- `skills/akka-agent-tool-boundaries/SKILL.md`
- possibly a new skill such as `skills/akka-agent-reference-governance/SKILL.md` if separate reference-document governance is chosen

## Deliverables

- Runtime sequence includes compact workstream expertise manifest assembly.
- `readSkill(skillId)` remains the governed loader for procedural skill guidance.
- Reference-document loading is explicitly modeled, either as a separate governed document pattern or as a constrained subset of skill governance with a migration note.
- Denied skill/reference loads and allowed loads emit trace records.
- Tool boundaries and capabilities remain authoritative; prompt, skill, and reference text cannot grant authority.

## Checks

- `git diff --check`
- Text search proving runtime guidance mentions compact manifest, authorized loading, denied loading, trace records, and authority boundaries.
