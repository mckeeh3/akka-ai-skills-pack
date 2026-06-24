# TASK-017: Repair app-description governed-agent verification coverage

## Scope

Repair the terminal verification blocker recorded in TASK-016: the maintainer pack verification script fails because root `app-description/**` lacks required governed runtime agent foundation coverage terms.

This is a documentation/app-description reconciliation task. Do not edit root runtime code.

## Required reads

- `specs/skills-pack-worker-tool-lifecycle-realignment/verification-notes.md`
- `skills-pack/pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh`
- The smallest affected `app-description/**` files needed to reconcile the missing governed runtime agent foundation terms.
- Relevant skills-pack doctrine only as needed:
  - `skills-pack/docs/core-ai-first-saas-foundation.md`
  - `skills-pack/docs/governed-agent-substrate.md`
  - `skills-pack/docs/workstream-expertise-model.md`

## Expected outputs

- Focused `app-description/**` updates that document the governed runtime agent foundation concepts required by the maintainer verification script without changing runtime code.
- Updated verification notes or task notes summarizing which maintainer-script patterns were repaired.

## Done criteria

- The app-description includes coherent coverage for the missing governed runtime agent foundation concepts reported by TASK-016, including `PromptDocument`, `SkillDocument`, `AgentSkillManifest`, `AgentBehaviorEditorAgent` or `editing-agent`, and `agent catalog` where appropriate.
- The added text preserves existing app-description intent, tenant/customer scope, tool-boundary, prompt/skill/reference governance, trace, denial, and behavior-editing semantics.
- No root runtime code is edited.
- If the maintainer script reveals a different unrelated blocker after this repair, record it for TASK-018 instead of broadening this task.

## Required checks

- `git diff --check`
- `bash skills-pack/pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh`
