# Global Traces: Foundation trace patterns

Reusable trace patterns for the core starter.

- `admin-audit-event`: identity, membership, role, support access, invitation, policy, approval, data access, denial, and export events.
- `workstream-log-trace`: request, response, surface action, capability result, system message, attention update, and correlation links.
- `prompt-assembly-trace`: managed agent definition, prompt version, manifest, model config, compact expertise manifest, and provider boundary decisions.
- `skill-reference-load-trace`: authorized/denied skill and reference loads, redaction, manifest checks, and tool-boundary checks.
- `agent-work-trace`: tool calls, data access, model calls, recommendations, denials, failures, and final structured results.
- `policy-decision-trace`: proposal, simulation, evidence, decision, activation, rollback, override, and outcome links.

Trace payloads must preserve diagnosis and audit value while respecting tenant/customer scope, redaction, and frontend secret boundaries.
