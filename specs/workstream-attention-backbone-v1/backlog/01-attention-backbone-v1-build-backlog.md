# Backlog: Attention Backbone v1

## Goal

Turn the discussed shared attention backbone into bounded fresh-session implementation tasks for the starter template.

## Suggested task breakdown

1. **Contract and routing update**
   - Define the `AttentionItem` and summary contract.
   - Record capability/governed-tool/API/surface expectations.
   - Update starter docs or local contract tests so later tasks have an implementation target.

2. **Backend attention foundation**
   - Add domain records/enums for attention item status, category, severity, source refs, and scoped target rules.
   - Add a repository/service path suitable for starter runtime use, preferring durable Akka-backed state where feasible.
   - Add protected read and lifecycle methods with audit/trace hooks.

3. **Core workstream producers and My Account integration**
   - Replace hard-coded `MyAccountService.personalAttention` with attention service reads.
   - Derive or seed initial attention from existing core workstream states: invitation delivery, Agent Admin provider readiness, Governance proposal/approval state, Audit/Trace failure evidence.
   - Wire `open_attention_item` to resolve through authorized attention refs where feasible.

4. **Frontend/API integration**
   - Surface backend attention summaries to the left rail and My Account dashboard.
   - Render attention items with actions/trace links and safe empty/denied states.
   - Keep unseen-response rail state separate from backend actionable attention.

5. **Verification**
   - Run targeted backend/frontend checks.
   - Compare completed work to mini-project done state.
   - Append follow-up tasks if gaps remain.

## Dependencies

- Contract task before backend and frontend implementation.
- Backend foundation before integration tasks.
- Frontend/API integration after backend read contracts exist.

## Required checks

Use targeted checks per task, typically:

- `git diff --check`
- backend Maven tests under `templates/ai-first-saas-starter/backend` when Java changes are made
- frontend contract/type/build tests under `templates/ai-first-saas-starter/frontend` when TypeScript/React changes are made
- focused `rg` checks proving frontend-only attention is not used as the authoritative count source

## Acceptance criteria

The queue is successful when the starter has a real shared backend-owned attention path powering My Account/workstream/rail attention at v1 scope, with tests and no misleading runtime completion claims.
