# Full-Core SMB First-Wave Plan

## Purpose

Sequence the first child mini-projects that turn the five-core v0 starter into an SMB full-core baseline without guessing product architecture during implementation sessions.

## Wave 1 child projects

### 1. `specs/full-core-smb-baseline-and-ux/`

Goal: harden shared workstream-shell, structured-surface, visual-quality, runtime-validation, and cross-workstream contracts that every full-core workstream depends on.

Why first:
- prevents each workstream from reinventing dashboards, `system_message` denials, shell request handling, visual standards, and validation expectations;
- preserves the workstream/surface architecture before richer User Admin work adds more actions;
- creates shared proof points for provider fail-closed behavior, trace links, authorization denials, and frontend secret-boundary checks.

First runnable task after scaffold:
- `TASK-FCSMB-BASEUX-01-001`: Define executable shared baseline contracts and validation map.

### 2. `specs/full-core-smb-user-admin/`

Goal: make User Admin the first full-core workstream slice because it provides concrete SMB value and exercises invitations, memberships, role/capability management, access review, audit visibility, request/response agent guidance, and the first durable internal-worker candidate.

Why second:
- it is the highest-leverage workstream for SMB operators;
- it feeds Audit/Trace and Governance/Policy with meaningful access-change evidence;
- it tests deterministic service boundaries versus request/response Akka Agent guidance and AutonomousAgent/internal worker lifecycle semantics.

First runnable task after scaffold:
- `TASK-FCSMB-UA-01-001`: Define User Admin vertical slice contracts and implementation map.

## Deferred child projects

Create later child queues after Wave 1 verification identifies the right boundaries:

- `full-core-smb-my-account` after shared shell/context/attention contracts are confirmed;
- `full-core-smb-agent-admin` after shared baseline and User Admin evidence patterns clarify behavior-governance needs;
- `full-core-smb-audit-trace` after richer User Admin traces exist;
- `full-core-smb-governance-policy` after User Admin and Agent Admin authority-change evidence is available;
- `full-core-smb-polish-release` after the workstream-specific child projects complete.

## Inherited standards for every child project

Every child queue inherits:

- SMB scope and non-goals from `specs/full-core-smb-saas-hardening/smb-full-core-baseline.md`;
- visual and structured-surface quality standards from `specs/full-core-smb-saas-hardening/visual-ux-quality-standard.md`;
- capability/surface/agent/service outlines from `specs/full-core-smb-saas-hardening/workstream-full-core-outline.md`;
- AutonomousAgent/internal-worker selection rules from `specs/full-core-smb-saas-hardening/agent-worker-opportunities.md`;
- runtime-completion doctrine from `AGENTS.md`.

## Wave 1 completion gate

Wave 1 is ready to execute when both child mini-project queues have:

- README, conversation capture, sprint, backlog, task briefs, pending queue, and terminal verification;
- first implementation task that is bounded and runnable in a fresh session;
- required reads that point back to the umbrella baseline/UX/workstream documents;
- required checks that include `git diff --check` plus targeted proof searches or validation maps;
- explicit refusal to mark deterministic/demo/model-less normal runtime behavior as complete.
