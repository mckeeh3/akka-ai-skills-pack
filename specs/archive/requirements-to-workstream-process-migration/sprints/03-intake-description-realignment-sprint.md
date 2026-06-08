# Sprint 03: Intake and Description-First Realignment

## Objective

Update high-level input normalization, app-description intake, bootstrap, workstream modeling, surface modeling, capability modeling, readiness, and generation guidance so they preserve the new process contract from the first interpretation step.

## Scope

Likely affected skills:
- `skills/app-description-input-normalization/SKILL.md`
- `skills/app-description-intake-router/SKILL.md`
- `skills/app-description-bootstrap/SKILL.md`
- `skills/app-description-functional-agent-modeling/SKILL.md`
- `skills/app-description-surface-modeling/SKILL.md`
- `skills/app-description-capability-modeling/SKILL.md`
- `skills/app-description-ui/SKILL.md`
- `skills/app-description-readiness-assessment/SKILL.md`
- `skills/app-generate-app/SKILL.md` if realization routing needs changes
- `skills/ai-first-saas/SKILL.md` and `skills/agent-workstream-apps/SKILL.md` if top-level handoff needs reinforcement

## Work areas

1. Update normalized input envelope to include workstream candidates, attention needs, dashboard candidates, surface expansion, autonomous task candidates, and event/notification/trace implications.
2. Update intake routing so broad app input cannot skip workstream-attention-dashboard pre-processing.
3. Update bootstrap so a new app-description starts with core workstreams, dashboard/attention expectations, and autonomous-task placeholders where justified.
4. Update modeling skills to preserve action-to-capability and autonomous-task links.
5. Update readiness/generation checks so missing attention/dashboard/surface/capability/autonomous-task semantics block or defer correctly.

## Acceptance criteria

- Description-first paths process normal user language into workstreams and attention/dashboard/surface concepts before backend components.
- Internal/background agent work is routed toward Autonomous Agents when task lifecycle semantics fit.
- Readiness checks can detect stale page/CRUD/component-first descriptions.
