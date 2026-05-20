# Conversation Capture

## User request

The user observed that the repository now has a consistent web UI based on functional/context-area agents and asked to review design-related content to identify cleanup and revisions.

Follow-up request: create a task-based migration plan similar to `specs/workstream-ui-implementation-migration/`, with tasks that can be independently completed in new harness sessions, and git commit the planning changes when the planning task is complete.

## Review findings captured for migration

The design is mostly consistent but needs tightening in these areas:

1. Standardize `55-ui/` file sets across app-description bootstrap, UI skill, internal architecture docs, and examples.
2. Make `12-workstreams/` clearly primary for the application model and `55-ui/` clearly browser rendering/interaction realization.
3. Normalize terminology around **functional/context-area agent** on first mention and **functional agent** thereafter.
4. Update `ai-first-saas-ui-surfaces` so every selected surface is placed inside owning/reusable functional-agent workstreams.
5. Refresh or explicitly label the DCA app-description UI example so it does not look like the current canonical split structure if it remains consolidated.
6. Quarantine legacy page/screen/static UI references more aggressively and route generated SaaS UI to `frontend/src/workstream/**`.
7. Add a compact design review checklist for functional-agent workstream design.
8. Ensure generation guidance treats the workstream reference as canonical.

## Planning output created

This migration plan under `specs/agent-workstream-design-content-migration/` captures those findings as independent tasks suitable for fresh harness sessions.
