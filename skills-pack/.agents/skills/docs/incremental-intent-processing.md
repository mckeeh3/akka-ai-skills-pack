# Incremental Intent Processing

Incremental intent processing turns each user input into a safe current-intent update, realization task, validation step, or pending question.

Incremental input can arrive as a new product idea, revised requirement, bug report, UX correction, policy constraint, implementation discovery, code-level change request, or validation finding. The compiler should preserve the user's meaning while producing clean current-state artifacts.

## Processing loop

1. **Classify** the increment.
   - Is it app objective, domain, workstream, capability, surface, agent, tool, policy, trace, test, realization, or code behavior?
   - Is it new intent, a correction, a deletion, a constraint, a bug, or a validation result?

2. **Locate affected graph nodes.**
   - Identify app/global/domain/workstream files and downstream specs/tasks/code likely affected.
   - Prefer the smallest complete scope that preserves traceability.

3. **Normalize to a delta.**
   - Restate the intended current behavior, not the conversation chronology.
   - Include actor, role, workstream, capability, data/state, authorization, trace, test, and realization implications when known.

4. **Reconcile with current intent.**
   - Merge compatible refinements.
   - Replace superseded statements.
   - Detect conflicts with existing access, policy, state, or runtime obligations.

5. **Decide the route.**
   - Update app-description/current-intent artifacts only.
   - Update specs/backlogs/task queues.
   - Realize in code/tests/runtime validation.
   - Ask or reconcile a pending question before unsafe compilation.

6. **Record traceability.**
   - Link changed intent artifacts to specs, tasks, code surfaces, tests, and runtime validation evidence.

## Delta envelope

Intent-normalization skills should produce or internally reason over a delta with these fields when relevant:

```text
intent_kind: app | domain | workstream | capability | surface | agent | tool | policy | trace | test | realization | code_change | validation
operation: add | refine | replace | remove | reconcile | validate | repair
scope:
  app: <name-or-current-app>
  domain: <domain-or-null>
  workstream: <workstream-or-null>
  global_artifacts: [<artifact refs>]
summary: <current-state change>
affected_artifacts: [<paths or intended paths>]
auth_security: <roles, trust boundaries, denials, approvals>
traces_tests: <audit/work traces, acceptance, regression, negative tests>
realization: <Akka/frontend/API/task implications>
ambiguities: [<questions that block safe compilation>]
```

The exact output format may vary by skill, but these semantics should be preserved.

## Pending questions

Create or update a pending question when a compiler decision would otherwise require guessing about:

- tenant/customer scope;
- workstream ownership or access;
- actor/role authority;
- policy thresholds or approval gates;
- sensitive data, audit, trace, or retention obligations;
- model/tool authority for agents;
- user-visible behavior or acceptance criteria;
- selected app structure or Java/frontend package choices;
- runtime validation path for a feature-bearing task.

A pending question blocks only the unsafe affected scope. Continue unrelated safe updates only when traceability remains clear.

## Current-state examples

Incremental input:

```text
Add invitations. Actually, invitations expire after 7 days. Admins can resend unaccepted invitations. Support can view invitation status but cannot create invitations.
```

Compiled current intent:

```text
Tenant admins may create email invitations.
Invitations expire after 7 days.
Tenant admins may resend unaccepted invitations.
Support users may view invitation status but may not create invitations.
```

Do not preserve the false intermediate state as canonical intent.

See also [Current intent model](current-intent-model.md) and [Intent compiler skill contracts](intent-compiler-skill-contracts.md).
