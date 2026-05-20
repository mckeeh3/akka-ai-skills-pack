# TASK-STARTER-04-003: Wire Agent Admin and Governance/Policy UI to real capabilities

## Purpose

Connect Agent Admin and Governance/Policy workstream surfaces to real governed backend capabilities.

## Required reads

- `specs/core-app-full-stack-readiness/agent-admin-component-api-slice.md`
- `specs/core-app-full-stack-readiness/governance-policy-core-module-slice.md`
- `frontend/src/workstream-agent-admin-vertical.contract.test.mjs`
- `frontend/src/workstream/**`
- `skills/akka-web-ui-apps/SKILL.md`

## Expected outputs

- Agent Admin and Governance/Policy API/frontend wiring.
- UI/API tests for proposals, approvals, denials, traces, and governed edits.

## Done criteria

- Agent and policy governance surfaces are backed by starter capabilities.
- UI preserves approval, validation, redaction, trace, and denial semantics.
- Required frontend/backend checks pass, queue status is updated, and changes are committed.
