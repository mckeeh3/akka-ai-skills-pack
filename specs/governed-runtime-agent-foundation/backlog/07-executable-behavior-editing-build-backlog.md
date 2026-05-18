# Backlog 07: Executable behavior-editing proposal reference

## Purpose

Add executable reference coverage for `AgentBehaviorEditorAgent`-style behavior maintenance so future harness runs can implement prompt/skill/manifest/tool-boundary edit proposal flows safely.

## Delivery goal

Implement a compact deterministic reference that demonstrates:

- behavior edit request capture;
- affected governed artifact selection;
- proposed prompt/skill diff creation;
- proposed manifest/tool-boundary change creation;
- risk and authority-expansion classification;
- draft/proposal record creation without active mutation;
- approval helper behavior for approve/reject/request-changes/escalate;
- trace facts for proposals, denials, approvals, and authority-expansion escalation;
- coverage matrix update.

## Reference package

Prefer reusing the existing governed-agent reference package roots:

```text
src/main/java/com/example/domain/agentfoundation/
src/main/java/com/example/application/agentfoundation/
src/test/java/com/example/application/agentfoundation/
```

## Capability contracts

### `agent-behavior.reference-propose-edit`

- callers: authorized steward/admin fixture.
- inputs: behavior change request, tenant id, agent id, target document hints, correlation id.
- semantics: select affected prompt/skill/manifest/tool-boundary records and produce proposed changes only.
- denials: missing permission, cross-tenant target, disabled target when not allowed, unsupported request.
- traces: proposal and denial trace facts.

### `agent-behavior.reference-classify-risk`

- semantics: classify wording-only, skill-guidance, manifest-addition, tool-boundary-expansion, or authority-expansion risk.
- approval: authority/tool/data expansion requires review/decision-card routing.

### `agent-behavior.reference-review-proposal`

- semantics: approve/reject/request-changes/escalate proposal state without bypassing activation controls.
- side effects: trace facts only in this reference slice.

## Suggested harness task breakdown

1. Add behavior-editing reference records and fixtures.
2. Add behavior editor proposal helper and prompt/skill diff tests.
3. Add manifest/tool-boundary proposal risk classification tests.
4. Add proposal review/approval helper and trace tests.
5. Add optional deterministic `AgentBehaviorEditorAgent` wrapper if feasible.
6. Update coverage matrix and run final audit.

## Done criteria

- Executable tests cover safe proposal, authority expansion escalation, cross-tenant denial, no active mutation, review decision state, and trace creation.
- Behavior-editing coverage matrix row is updated accurately.
