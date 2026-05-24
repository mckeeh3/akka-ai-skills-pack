# Conversation Capture: Five Core Workstream v0 Starter

The user has been testing the installed skills pack in empty projects by following the README getting-started prompts. The current scaffold is close to producing a working initial workstream shell.

Initial idea: implement a functioning workstream shell where a bootstrap user signs in, sees a basic My Account/User Admin style workstream, sends a prompt through a workstream agent configured with a model, and receives a sanitized `markdown_response` surface.

Refinement: instead of showing only one initial workstream, show all five core app workstreams in the left rail from the beginning:

- My Account
- User Admin
- Agent Admin
- Audit/Trace
- Governance/Policy

Each workstream should initially be limited to the single `markdown_response` surface. Once this basic shell is functional, each workstream can be fully implemented one by one. The same pattern should apply beyond the core domain: provide a simple PRD for a new workstream, add/refine the workstream's surfaces, skills, capabilities, and implementation incrementally.

Key architecture decision: treat the first runnable starter as five core v0 functional-agent workstreams, not as a generic chatbot and not as full-core readiness. The starter remains secure, workstream-first, capability-first, and traceable.
