# Workstream Expertise Bundles

Each LLM-backed functional agent needs a workstream expert bundle. The bundle makes the agent competent for one workstream through governed prompt intent, model binding, skills, references, manifests, loader tools, tool boundaries, traces, and tests. Expertise guides behavior; it does not grant authority.

Within this legacy numbered template, map reusable agent definitions into current-intent `app-description/global/agents/**` nodes and workstream-specific expertise/bindings into `app-description/domains/<domain>/workstreams/<workstream>/agents/**` before treating them as active app intent.

## Required bundle fields

- bundle id and owning functional agent;
- scope, roles, tenant/customer/AuthContext assumptions;
- prompt intent, refusal rules, clarification/escalation rules;
- explicit `ModelConfigRef`/`ModelPolicy` or inherited governed default model binding;
- assigned procedural `SkillDocument`/`SkillVersion` entries through `AgentSkillManifest`;
- assigned factual/process `ReferenceDocument`/`ReferenceVersion` entries through `AgentReferenceManifest`;
- compact manifest entries included in prompt assembly, never full bodies by default;
- authorized `readSkill(skillId)` and `readReferenceDoc(referenceId)` loader tools;
- capability/governed-tool map and `ToolPermissionBoundary`;
- surfaces, dashboard help, denial/recovery examples, and trace links;
- PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace requirements;
- governance owner, default-content policy, customization-preserving upgrades;
- tests for assigned loads, denied loads, tenant isolation, tool-boundary denial, no authority expansion from text, traces, and surface rendering.

## Foundation bundle files

- `my-account-agent.md`
- `user-admin-agent.md`
- `agent-admin-agent.md`
- `audit-trace-agent.md`
- `governance-policy-agent.md`

Use the minimal template in each file, then expand it in the target app as managed-agent runtime scope becomes implemented.
