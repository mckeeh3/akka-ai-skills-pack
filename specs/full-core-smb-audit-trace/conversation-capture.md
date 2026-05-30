# Conversation Capture

## Source discussion

After all pending `full-core-smb-*` tasks were completed, the assistant inspected the full-core sequence and recommended Audit/Trace as the next workstream. The user agreed and asked to proceed.

## Accepted decisions

- Create `specs/full-core-smb-audit-trace/` as the next mini-project.
- Sequence Audit/Trace after User Admin and Agent Admin because those workstreams now produce meaningful access, worker, managed-agent, behavior-change, provider, and tool-boundary traces.
- Sequence Audit/Trace before Governance/Policy because Governance/Policy needs practical trace evidence for proposals, simulations, exceptions, approvals, and decisions.
- Preserve SMB scope; avoid SIEM, legal hold, e-discovery, and enterprise compliance-suite scope creep.
- Keep deterministic services responsible for trace ingestion/query, authorization, tenant filtering, redaction, correlation, projection, retention-safe DTOs, and export/copy guards.
- Use governed request/response Akka Agent behavior for AuditTraceAgent explanations, with provider fail-closed behavior.
- Introduce scheduled audit-summary/anomaly worker only after deterministic search/detail/timeline foundations exist.

## Constraints

- Target is `templates/ai-first-saas-starter/` as the executable baseline.
- Workstream + structured surface UX remains the only product architecture.
- Missing provider/model config must fail closed with actionable surfaces/traces.
- No deterministic/model-less successful normal responses for model-backed AuditTraceAgent guidance or workers.
- Visual quality and runtime validation are first-class acceptance criteria.
- One task per fresh harness context.

## Risks

- Audit/Trace can become too broad. Keep the first implementation map focused on SMB investigation workflows and source realities.
- Trace detail can leak sensitive prompts, secrets, or cross-tenant payloads. Require redaction and browser-safe DTOs.
- Cross-workstream trace links can leak existence of hidden workstreams or resources. Backend trace reads remain authoritative.
- AuditTraceAgent guidance can accidentally reveal unauthorized evidence. It may explain only already-authorized evidence loaded through governed tools.

## Unresolved questions

No blocking product question is required to start. The implementation-map task must determine the starter's current trace substrate and whether trace records are persisted enough for search/detail/timeline or need a deterministic trace repository foundation first.
