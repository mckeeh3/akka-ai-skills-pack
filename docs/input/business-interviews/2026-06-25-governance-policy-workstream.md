# Business Intent Interview: Governance/Policy Workstream

## Source Context

- Date: 2026-06-25
- Topic: Governance/Policy workstream
- Interview mode: Stage 1 business-intent capture
- Interviewee request: "interview me about the governance-policy workstream"

## Interview Status

Accepted as a Stage 1 capture for later app-description/current-intent reconciliation.

## Explicit Input

- The workstream description should already cover the broad meaning and purpose of Governance/Policy.
- SaaS owner sets the defaults for all tenants.
- Tenants should be able to customize governance policies to fit their specific needs.
- In some cases one agent is allowed to send an email while another agent needs approval.
- Tenants can override anything in the business-governance policy layer.
- Policy applicability could depend on agent, tenant policy, recipient/customer sensitivity, email content/category, requester role, risk score, and possibly more.
- Tenant policy changes should become active immediately.
- If a tenant policy conflicts with SaaS owner defaults, the tenant decides.
- There are no non-overridable business governance rules, but hard SaaS platform/security controls remain non-overridable.
- Tenant admins can change tenant policies.
- Policy-change history should be kept.
- For the email example, when the tenant policy allows immediate sending, the runtime outcome should be allow immediately.
- Policies cannot get too complicated; ideally they are booleans or counters, such as `3 retries`.
- This is an SMB SaaS app, not an enterprise app.
- Tenant admins should see the effective policy.
- No policy simulation/testing workflow is needed.
- Policy changes should notify nobody by default.
- The initial policy catalog is not known and should evolve as app-descriptions evolve.
- Policies may apply at tenant, agent, workstream, action/tool, role, and customer/account levels.
- Tenant admins should enter a reason when changing a policy.
- Policy history is useful for tenant admins, auditors, and SaaS owner support.
- If multiple policy scopes apply, the finer-grained/more specific policy wins.
- Tenants can reset one policy back to the SaaS default.
- SaaS owner can update defaults later without overwriting tenant overrides.
- Runtime traces should explain the effective policy decision and source.
- Governance/Policy should show all policies and support search/filter.
- Policy changes can happen from the Governance/Policy workstream and from relevant agent/workstream settings or customer/account pages.
- SaaS owner should use the same workstream with SaaS-owner/defaults context selected.
- No extra confirmation is needed for policy changes beyond the required reason.
- Important filters: policy name, workstream, agent, tool/action, and role.
- Policy history should show both direct changes and runtime outcomes influenced by the policy, if possible.
- There should be an overridden indicator.
- Keep complex policy scripting, simulations, legal compliance workflow, approval workflows, notifications, and enterprise role delegation out of scope.

## Agent-Inferred Business Model

- Governance/Policy should behave as a simple effective-policy settings center, not a formal enterprise policy engine.
- SaaS defaults are starter/baseline values for business governance, not hard tenant constraints.
- Tenant overrides are tenant-owned operating choices and should be easy for tenant admins to change.
- The policy catalog should be extensible so future app-description-defined capabilities, agents, tools, roles, workstreams, and customer/account contexts can introduce additional simple policy settings.
- Runtime policy enforcement should be explainable through audit/work traces so support, auditors, and tenant admins can understand why an agent action was allowed, blocked, or routed differently.

## Confirmed Intent

The Governance/Policy workstream should provide SMB-friendly policy controls:

- SaaS owner manages default policy values.
- Tenant admins manage tenant-specific overrides.
- Tenant overrides become active immediately and win over SaaS defaults for business-governance behavior.
- Hard platform security remains non-overridable, including tenant isolation, secret protection, backend authorization, redaction boundaries, and platform integrity controls.
- Policies should remain simple: primarily booleans and counters/limits.
- More specific/finer-grained policy overrides win when more than one policy could apply.
- Policy changes require a human-entered reason, are history/audit recorded, and do not notify anyone by default.
- Tenants can reset policies back to SaaS defaults.
- SaaS owner default changes do not overwrite tenant overrides.
- Governance/Policy is the central visibility, search/filter, effective-policy, override-status, and history view.
- Related pages may provide convenient policy-edit entry points, but Governance/Policy remains authoritative for visibility and history.
- Runtime traces should identify the effective policy source and explain the decision.

## Current Process

No current manual process was described beyond the intended SaaS-owner default and tenant-admin override model.

## Pain Points

- Existing or expected policy governance could become too complex if modeled like an enterprise policy engine.
- Tenants need flexibility to operate according to their own business needs without waiting for SaaS-owner approval.
- Tenant admins, auditors, and SaaS owner support need traceability when policies change or influence runtime outcomes.

## Desired Future State

- Tenant admins can find and adjust simple policy values without navigating complex policy languages or enterprise approval workflows.
- SaaS owners can manage defaults in the same workstream by selecting a SaaS-owner/defaults context.
- Tenants can see which policies are overridden and what effective value is currently in force.
- Runtime actions can explain which policy value applied and why.
- The policy catalog grows naturally as app descriptions add new agents, tools, roles, workstreams, and customer/account behavior.

## Actors and Responsibilities

- SaaS owner:
  - Sets and updates default policy values.
  - Uses the same Governance/Policy workstream with defaults context selected.
  - Default updates must not overwrite existing tenant overrides.
- Tenant admin:
  - Changes tenant policy overrides.
  - Provides a required reason for each policy change.
  - Can reset a policy back to the SaaS default.
- Auditor:
  - Uses policy history and runtime outcome evidence for review.
- SaaS owner support:
  - Uses policy history and effective-policy traces for tenant support/troubleshooting.
- Agents:
  - Are governed at runtime by effective policy values.
  - May have different behavior for the same action depending on configured policy scope.

## Events, Triggers, and Timing

- SaaS owner creates or updates a default policy value.
- Tenant admin creates, updates, or resets a tenant override.
- Tenant override changes become active immediately.
- Runtime agent/tool/action attempts evaluate effective policy.
- Runtime decisions create trace evidence showing effective policy source and decision explanation.
- Policy history records direct changes and, if practical, aggregated or linked runtime outcomes influenced by policies.

## Decisions, Rules, and Exceptions

- Tenant overrides win over SaaS defaults for business-governance policy decisions.
- More specific/finer-grained overrides win when multiple scopes apply.
- Policy values should be simple booleans or counters/limits unless future app descriptions introduce a justified simple extension.
- Tenant policy edits require a reason.
- No extra confirmation is required for policy edits.
- No default notification is sent for policy edits.
- No simulation/testing workflow is required.
- Hard platform security controls are not tenant-overridable.

## Systems, Documents, and Data

Policy records should support:

- policy id/name;
- type, such as boolean or counter/limit;
- SaaS default value;
- tenant override value, when present;
- effective value;
- scope, such as tenant, agent, workstream, action/tool, role, or customer/account;
- override indicator;
- changed by;
- change reason;
- changed/active timestamp;
- affected agents/capabilities/workstreams/tools/roles/customers/accounts where applicable;
- runtime trace references and outcome links where practical.

## Candidate CRM / ERP / Operations Needs

No CRM/ERP-specific needs were identified in this interview. Operational needs include:

- searchable/filterable policy inventory;
- tenant override management;
- SaaS default management;
- effective-policy calculation;
- policy-change history;
- runtime policy-decision traceability;
- optional outcome history/aggregation influenced by policy.

## Examples and Scenarios

- One agent is allowed to send an email immediately, while another agent may require approval or be governed differently.
- A tenant admin enables immediate send behavior for a particular agent/action context.
- A tenant admin changes a retry limit to `3` and enters a reason.
- A tenant resets a policy back to the SaaS default.
- SaaS owner changes a default value later; tenants with overrides keep their override values.
- Runtime trace explains: allowed because a tenant override at a specific agent/action scope was changed by a tenant admin on a given date with a recorded reason.

## Success Measures

- Tenant admins can find policies by policy name, workstream, agent, tool/action, or role.
- Tenant admins can clearly see overridden versus default policies.
- Tenant admins can change simple policies quickly without complex workflow.
- Policy changes are active immediately and reliably reflected in runtime behavior.
- Policy history is useful for tenant admins, auditors, and SaaS owner support.
- Runtime traces clearly explain effective policy decisions.
- The workstream remains simple and SMB-appropriate.

## Rejected or Out of Scope

- Complex policy scripting.
- Policy simulations/testing workflows.
- Legal compliance workflow.
- Approval workflows for policy edits.
- Notifications by default.
- Enterprise role delegation.
- Treating SaaS business-governance defaults as hard non-overridable tenant constraints.
- Allowing tenant overrides to bypass hard SaaS platform/security controls.

## Open Questions

- What exact first policy catalog entries should exist? The interviewee expects this to evolve as app descriptions evolve.
- How should runtime outcomes influenced by a policy be summarized in history without making the feature too complex?
- Are any non-boolean/non-counter simple types needed later, such as a small enum, or should all initial policies remain boolean/counter only?

## Agent Summary for Ingestion

Update Governance/Policy current intent from a proposal-heavy governance model toward an SMB-friendly effective-policy settings model. SaaS owner manages defaults. Tenant admins can immediately override business-governance policy values at multiple scopes. Finer-grained overrides win. Policies should be simple booleans/counters and evolve with app-description-defined agents, tools, roles, workstreams, and customer/account contexts. Policy edits require a reason, create history, do not require approval/confirmation beyond the reason, and notify nobody by default. Governance/Policy should provide search/filter, effective-policy display, overridden indicators, and policy history. Runtime traces should explain effective policy source and decision. Hard platform security remains non-overridable.

## Interviewee Confirmation

Interviewee answered "yes" when asked whether to save the summarized Stage 1 interview artifact.
