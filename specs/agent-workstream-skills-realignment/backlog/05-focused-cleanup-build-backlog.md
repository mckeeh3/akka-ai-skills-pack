# Backlog 05: Focused Realignment Cleanup

## Outcome

The skills pack should be safe to dogfood for starter implementation without stale installed-pack behavior or common mechanics-first escape hatches.

## Backlog items

1. **Refresh installed pack and validate parity**
   - Reinstall the source pack into `.agents/`.
   - Restore source `AGENTS.md` if installer replaced it.
   - Spot-check installed skills for source realignment terms.
   - Document parity status; do not commit `.agents/`.

2. **Make core SaaS foundation workstream-first**
   - Add `agent-workstream-apps` and structured-surface docs to required reads.
   - Add foundation functional-agent/surface handoff: Access/Profile, User Admin, Agent Admin, Audit/Trace, Governance/Policy, Support Access, Billing where in scope.
   - Ensure first-slice implementation order routes foundation UI/admin work through surfaces/actions/capabilities before components.

3. **Add missing input-contract gates to focused skills**
   - Add the standard generated SaaS input contract to remaining high-use focused implementation skills that can still be loaded directly for mechanics-first work.
   - Keep edits compact and skill-specific.

4. **Normalize structured-surface vs exposure-channel terminology**
   - Audit and update top-level ambiguous language where “surface” could mean both workstream surface and capability exposure surface.
   - Prefer `structured surface` for UI/workstream artifacts and `exposure channel/path` for HTTP/gRPC/MCP/tools/workflow/timer/consumer/API realizations.

5. **Align AI-first companion handoffs**
   - Update policy, decision, audit, and admin-agent companion skills to output functional-agent/surface/action/capability handoffs.

6. **Validate and repair source skill path references**
   - Create a validation report for source skill required-reading paths.
   - Fix highest-impact broken source references or queue follow-up tasks if many remain.

7. **Update starter acceptance consistency**
   - Update final acceptance/migration summary docs to acknowledge Sprint 08 workstream-first follow-up queue added after the earlier final acceptance.

8. **Sprint review**
   - Re-run focused checks.
   - Decide whether realignment is closed or add a targeted Sprint 06.
