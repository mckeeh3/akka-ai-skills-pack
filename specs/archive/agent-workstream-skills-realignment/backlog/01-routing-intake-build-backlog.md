# Backlog 01: Routing and Intake Alignment

## Outcome

Broad product input, PRDs, app-description requests, and direct Akka decomposition requests should consistently enter through the workstream-first model before backend capability and Akka component selection.

## Backlog items

1. **Audit top-level routing**
   - Review `skills/README.md`, installed `.agents/skills/README.md`, and top-level entry skills.
   - Identify where intent can bypass functional agents and structured surfaces.
   - Produce a concise gap matrix.

2. **Align AI-first SaaS and agent-workstream entry skills**
   - Update `ai-first-saas` and `agent-workstream-apps` so their relationship is explicit.
   - Ensure high-level intent always hands off functional-agent/surface context downstream.

3. **Align capability-first and Akka decomposition skills**
   - Update capability-first guidance so backend capabilities are explicitly derived from workstream/surface/action semantics.
   - Update solution decomposition output requirements to include functional agents, surfaces, surface actions, and capability mappings before components.

4. **Sprint review and next-task generation**
   - Re-run the routing audit.
   - Record remaining issues.
   - Add Sprint 02 tasks if the review confirms routing is aligned enough to proceed to app-description/PRD planning alignment.
