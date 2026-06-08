# Sprint 07: Executable behavior-editing proposal reference

## Goal

Add executable reference coverage for agent-mediated behavior editing: natural-language behavior change requests become proposed diffs, draft document changes, risk classifications, decision-card/approval routing, and traceable denials instead of direct active prompt/skill/manifest/tool-boundary mutation.

## Why this sprint exists

The pack now has executable coverage for governed runtime agent invocation, prompt assembly, skill loading, tool-boundary denials, and traces. The main remaining AI-first agent foundation gap is the distinctive maintenance loop: humans prompt an editing agent, the editing agent proposes changes, and humans approve or reject activation.

`docs/agent-coverage-matrix.md` still marks `AgentBehaviorEditorAgent` executable coverage as a gap.

## Scope

Create the smallest deterministic reference path:

```text
BehaviorChangeRequest fixture
→ ReferenceAgentBehaviorEditor
→ affected PromptDocument / SkillDocument / AgentSkillManifest / ToolPermissionBoundary selection
→ ProposedDocumentDiff / proposed manifest or tool-boundary change
→ risk and authority-expansion classification
→ draft/proposal record
→ approval helper decision
→ BehaviorEditTrace / AgentWorkTrace assertions
```

## Non-goals

- Do not implement a complete production document editor.
- Do not implement a full policy engine or final activation workflow.
- Do not add frontend UI unless a later sprint explicitly requests it.
- Do not require a real model; deterministic helper or TestModelProvider-backed behavior is enough.
- Do not mutate active runtime prompt/skill/manifest/tool-boundary records directly.

## Acceptance

- Reference code and tests prove safe behavior edit proposal creation, authority-expansion escalation, cross-tenant/unauthorized denial, and approval-gated activation semantics.
- Coverage matrix is updated from gap to partial/executable coverage for behavior-editing proposal flow.
- Each task is self-contained and committed independently.

## Related backlog

- `../backlog/07-executable-behavior-editing-build-backlog.md`
