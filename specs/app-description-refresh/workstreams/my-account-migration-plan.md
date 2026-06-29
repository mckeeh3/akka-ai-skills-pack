# My Account Workstream Migration Plan

## Scope

Refresh `app-description/domains/core-starter/workstreams/my-account/**` to the current skills-pack app-description graph contract.

## Primary intent

The signed-in member can understand their account, organization/tenant context, profile, membership, available workstreams, and relevant audit/work-trace evidence through a governed My Account workstream.

## Required graph coverage

- Workstream purpose and lifecycle/alignment state.
- Signed-in member human worker and My Account functional-agent worker bindings.
- Surfaces for account context, profile/settings, membership/organization context, capability visibility, and system messages.
- Governed tools for account/profile/context reads or updates where applicable.
- Actor adapters: `surface_action`, `human_chat_tool_plan` where allowed, `agent_tool_call` where explicitly permitted, and API/internal reads.
- Capability links to account context/profile and membership state.
- AuthContext, tenant/organization scope, denial behavior, disabled-user behavior, and frontend secret boundaries.
- Trace obligations for profile/context reads, updates, denials, and agent assistance.
- Tests and runtime-validation scenarios for login, `/api/me`, profile/context render, authorization denial, and trace visibility.
- Realization files and source-alignment entries.

## Specific refresh questions for the task

- Which account/profile changes are in scope vs read-only status?
- Is the functional agent allowed to execute any account tool or only explain state and guide the user?
- Which runtime-validation scenario owns `/api/me` evidence: My Account, auth foundation, or both?

## Expected task output

The task should update only My Account workstream files plus narrow shared references if required, then mark lifecycle/source-alignment to reflect description changes and implementation alignment.
