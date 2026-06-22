# Build Backlog: Workstream Surface Intent Routing

## Design notes

- Route only to surfaces or surface-open requests. Do not submit side-effecting commands from the agent/composer router.
- Keep backend authorization authoritative. Router matches must still resolve the selected `AuthContext` and only return visible surfaces/actions.
- Prefer high-confidence deterministic patterns. Ambiguous prompts should open a selection/help surface or fall back to model-backed chat.
- Prefill data is advisory browser-safe state. Surface submit handlers must still validate and call existing backend-authorized actions.
- Preserve provider fail-closed behavior: unmatched model-backed chat still invokes the governed runtime path and must not simulate success.

## Suggested implementation order

1. **Planning scaffold**
   - Create this mini-project, queue, sprints, backlog, and task briefs.

2. **Router contract**
   - Add a focused backend surface intent router contract and result model.
   - Define matched route fields: functional agent id, target surface id, source prompt, canonical prompt, prefill, confidence/category, no-mutation marker, trace ids.
   - Integrate before model-backed invocation in the workstream message path.

3. **User Admin proof**
   - Implement high-confidence User Admin routes:
     - `create organization "<name>"` → Organization Create form with prefilled name;
     - `show organizations` / `open organizations` → Organization Directory;
     - `show users` → User Directory;
     - `invite user <email>` → Invitation Create form with prefilled email.
   - Ensure missing capability returns safe unavailable/fallback behavior.

4. **Frontend prefill**
   - Add/standardize surface `prefill` data handling.
   - Wire Organization Create and invitation forms to use prefill while preserving validation and explicit submit.

5. **Routing tests**
   - Backend tests prove matched routes do not invoke model runtime and do not mutate state.
   - Frontend tests prove prefill rendering and normal submit boundaries.

6. **Surface catalog metadata**
   - Add catalog entries for each core workstream with surface id, title, purpose, prompt examples, required capabilities, prefill fields, and forbidden effects.

7. **All-workstream expansion**
   - Add safe high-confidence routes for My Account, Agent Admin, Audit/Trace, and Governance/Policy.
   - Keep risky/destructive prompts as surface opens or fallback, never direct mutation.

8. **Agent familiarity**
   - Update governed agent seed prompt/skill/reference material so each workstream agent can explain available surfaces and recommend structured surface use.

9. **Verification**
   - Verify current sprint and overall mini-project done state through targeted backend/frontend checks and runtime/API/UI evidence where feasible.
   - Append follow-up tasks and a new terminal verification task if gaps remain.

## Acceptance themes

- Faster path: matched prompts render surfaces without waiting for model responses.
- Safer path: routed prompts never submit side effects.
- Training path: UI copy teaches users to review and submit through surfaces.
- Agent familiarity: workstream agents know their own surfaces but cannot claim prompt text grants authority.
