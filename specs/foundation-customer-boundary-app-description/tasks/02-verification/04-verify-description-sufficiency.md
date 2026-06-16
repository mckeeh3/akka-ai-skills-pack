# TASK-FCBAD-01-004: Verify foundation customer boundary app-description sufficiency

## Objective

Verify whether the active `app-description/` graph is sufficiently unambiguous for the foundation customer boundary. If material ambiguity remains, append bounded follow-up tasks and a new terminal verification task.

## Required reads

- `AGENTS.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/intent-to-realization-flow.md`
- `.agents/skills/docs/intent-compiler-skill-contracts.md`
- `app-description/AGENTS.md`
- `specs/foundation-customer-boundary-app-description/README.md`
- `specs/foundation-customer-boundary-app-description/conversation-capture.md`
- `specs/foundation-customer-boundary-app-description/customer-boundary-evidence-and-gap-map.md`
- `specs/foundation-customer-boundary-app-description/backlog/01-foundation-customer-boundary-app-description-backlog.md`
- `specs/foundation-customer-boundary-app-description/pending-tasks.md`
- this task brief
- app-description files edited by prior tasks in this mini-project

## Verification question

Ask and answer explicitly:

> Is the foundation customer boundary description sufficiently unambiguous for future realization and drift repair tasks?

## Sufficiency checklist

Answer yes only if active `app-description/` clearly covers:

- domain ownership and non-goals;
- distinction from business CRM/customer-success/sales/support/billing/customer-intelligence domains;
- organization/tenant vs customer-layer semantics;
- Customer Admin authority and forbidden tenant/sibling-customer actions;
- capabilities and governed tool ids;
- durable state responsibilities and lifecycle/invariants;
- User Admin workstream placement and related workstream references;
- structured surfaces and action edges;
- functional-agent authority limits and model/tool boundary expectations;
- Akka/backend/API/frontend realization mapping;
- authorization, idempotency, denial, redaction, audit/work trace, and test obligations;
- enough detail to decide where a future customer-related requirement belongs.

## Expected outputs

- `specs/foundation-customer-boundary-app-description/verification/foundation-customer-boundary-sufficiency-review.md`
- Updated `pending-tasks.md` status/notes.
- If gaps remain: append new bounded tasks and append a new terminal verification task after them.

## Required checks

- `git diff --check`
- Targeted coverage proof commands over active `app-description/`.

## Done criteria

- Review records a clear yes/no sufficiency answer with evidence.
- If yes: mini-project done state is met and no further tasks are appended.
- If no: queue contains specific follow-up tasks and a new terminal verification task; this task is marked done only for completing the review/queue update, not for the overall mini-project.
- Review remains bounded to the foundation customer boundary app-description scope.

## Vertical workstream contract

- Scope: docs-only verifier/reviewer for foundation customer boundary current intent.
- Attention/non-UI reason: no runtime attention item; app-description sufficiency verification.
- Capability/foundation scope: customer boundary capabilities and User Admin/Customer Admin branch description.
- AuthContext/scope: verify tenant/customer scoping and denial expectations in description.
- Akka substrate: docs-only; verify realization mapping clarity.
- Audit/work trace: verify audit/work trace obligations are described.
- Local validation path: `git diff --check` plus targeted `rg` proof.
