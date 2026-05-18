# Sprint 08: Executable closed-loop agent improvement

## Goal

Add executable reference coverage for governed closed-loop agent improvement: evaluation findings and trace analysis produce improvement proposals, replay/simulation evidence, human approval decisions, activation, monitoring, and rollback semantics.

## Why this sprint exists

The pack now has executable coverage for governed runtime invocation and agent-mediated behavior editing. The remaining high-value AI-first gap is the improvement loop: how evaluator output or trace analysis safely becomes governed prompt/skill/behavior changes without bypassing human authority.

`docs/agent-coverage-matrix.md` still marks governed closed-loop improvement as a gap.

## Scope

Create the smallest deterministic reference path:

```text
ReferenceEvaluationRun / AgentWorkTrace fixture
→ ReferenceImprovementAnalyzer
→ EvaluationFinding
→ ImprovementProposal linked to behavior-edit proposal
→ ReplaySimulation evidence
→ ImprovementDecision approve/reject/request-changes
→ activation/rollback reference state
→ trace assertions
```

## Non-goals

- Do not build a production evaluator platform.
- Do not implement a real policy engine or external LLM judge.
- Do not mutate production prompt/skill documents directly.
- Do not add frontend UI unless a later sprint requests it.
- Do not broaden into custom model providers.

## Acceptance

- Executable tests prove findings can create governed improvement proposals.
- Replay/simulation evidence is required before activation.
- Human approval gates activation.
- Rollback is explicit and traced.
- Coverage matrix reflects executable closed-loop improvement coverage.

## Related backlog

- `../backlog/08-executable-closed-loop-improvement-build-backlog.md`
