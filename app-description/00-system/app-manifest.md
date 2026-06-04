# App Manifest

- app-id: `{{APP_SLUG}}`
- working-name: `{{APP_NAME}}`
- Java base package: `{{JAVA_BASE_PACKAGE}}`
- Maven group id: `{{MAVEN_GROUP_ID}}`
- status: scaffolded-core-app-description
- app-class: secure multi-tenant AI-first SaaS core starter
- source-of-truth: this project-local `app-description/` tree

## Current scaffold scope

The scaffold starts with the five core workstream set:

| Workstream | Core responsibility |
| --- | --- |
| My Account | Current account, selected AuthContext, profile/settings scope, sign-out path, notifications, and safe self-service next steps. |
| User Admin | Bootstrap user administration, invitations, roles/capabilities, access review, and safe admin next steps. |
| Agent Admin | Governed agent definitions, prompts, skills, references, manifests, tool boundaries, proposal review, and traces. |
| Audit/Trace | Audit/work trace substrate, trace links, correlation ids, denial traces, summaries, and investigation readiness. |
| Governance/Policy | Policy/permission concepts, approval/governance boundaries, behavior-change controls, impact analysis, and human review. |

## Generation targets

- Akka Java SDK backend under `{{JAVA_BASE_PACKAGE}}`.
- React + Vite + TypeScript frontend.
- Backend, frontend, integration, runtime smoke, auth/security, audit/trace, and tenant-isolation tests.
- Project-local `specs/` planning and scaffold provenance.

## Non-goals for the initial scaffold

- Domain-specific SMB/CRM/accounting/service-delivery features.
- Public unauthenticated chatbot behavior.
- Frontend-only or prompt-only authorization.
- Deterministic/demo/model-less normal runtime responses for model-backed workstreams.
- Full enterprise IAM, SIEM, e-discovery, marketplace prompt distribution services, arbitrary tenant-managed executable tool binding, provider-backed tool marketplaces, or broad replay/evaluation suites beyond the bounded Governance/Policy simulation-evidence platform unless explicitly added later.

## Architectural assumptions

- All protected state is tenant/customer scoped unless explicitly global.
- Every protected route, component command, query, stream, workflow action, timer, consumer, browser action, and agent tool is backend-authorized.
- Prompt text, loaded skills, route names, hidden UI state, and frontend labels do not grant authority.
- Normal model-backed workstream submission resolves active governed runtime records and invokes a concrete Akka `Agent` component through the governed runtime path.
- Missing external provider/security configuration fails closed with actionable surfaces/traces rather than returning canned success.
- Domain-specific features extend this tree by adding workstreams, surfaces, capabilities, behavior, tests, and traceability entries.

## Primary linked artifacts

- `../10-capabilities/capabilities-index.md`
- `../12-workstreams/functional-agents.md`
- `../12-workstreams/surfaces-index.md`
- `../15-operating-model/governed-runtime-agents.md`
- `../20-behavior/behavior-index.md`
- `../30-tests/test-index.md`
- `../40-auth-security/secure-saas-foundation.md`
- `../55-ui/ui-index.md`
- `../60-generation/realization-scope.md`
