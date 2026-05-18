# Backlog 08: Executable closed-loop agent improvement

## Purpose

Add executable reference coverage for closed-loop improvement so generated AI-first SaaS apps can safely turn evaluation findings and trace analysis into governed behavior improvements.

## Delivery goal

Implement compact deterministic reference code and tests for:

- evaluation run and finding records;
- improvement proposal creation from findings/traces;
- linking improvement proposals to behavior-edit proposals;
- replay/simulation evidence;
- approval, rejection, request-changes, activation, monitoring, and rollback reference semantics;
- trace facts for proposal creation, replay, decision, activation, and rollback;
- coverage matrix updates.

## Reference package

Prefer reusing existing governed-agent reference package roots:

```text
src/main/java/com/example/domain/agentfoundation/
src/main/java/com/example/application/agentfoundation/
src/test/java/com/example/application/agentfoundation/
```

## Capability contracts

### `agent-improvement.reference-evaluate`

- inputs: evaluation run fixture, trace/work summary, evaluator result/finding.
- semantics: normalize findings with severity, confidence, affected agent/document/skill, and suggested improvement kind.
- traces: finding creation and source trace linkage.

### `agent-improvement.reference-propose`

- semantics: create `ReferenceImprovementProposal` from one or more findings and produce/link a behavior-edit proposal when prompt/skill/manifest/tool-boundary changes are suggested.
- approval: no activation without replay/simulation evidence and human approval.

### `agent-improvement.reference-replay-simulate`

- semantics: compare current vs proposed behavior using deterministic fixtures.
- outputs: replay/simulation evidence, pass/fail/risk notes, regression warnings.

### `agent-improvement.reference-decide-activate-rollback`

- semantics: approve/reject/request changes; activate only approved proposals with sufficient evidence; rollback to prior approved reference version; trace every transition.

## Suggested harness task breakdown

1. Add closed-loop improvement reference records and fixtures.
2. Add improvement analyzer and proposal creation tests.
3. Add replay/simulation evidence helper and tests.
4. Add improvement decision, activation, rollback, and trace tests.
5. Link improvement proposals to behavior-edit proposals and tests.
6. Update coverage matrix and final audit.

## Done criteria

- Executable tests cover finding → proposal → replay/simulation → approval → activation/rollback.
- No reference path bypasses behavior-editing or approval controls.
- Coverage matrix accurately reports closed-loop improvement coverage and remaining gaps.
