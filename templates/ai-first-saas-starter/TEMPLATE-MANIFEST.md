# Template Manifest: AI-First SaaS Starter

## Template identity

- template id: `ai-first-saas-starter`
- source path: `templates/ai-first-saas-starter/`
- installed resource path: `resources/templates/ai-first-saas-starter/`
- scope: minimum-first secure AI-first SaaS starter with five core v0 workstreams and a full-core growth path

## Placeholder contract

| Placeholder | Required rendering |
| --- | --- |
| `{{APP_NAME}}` | Human-readable app name. |
| `{{APP_SLUG}}` | Safe lower-kebab app/project slug. |
| `{{JAVA_BASE_PACKAGE}}` | User-selected Java base package, defaulting to `ai.first` after acceptance/deferral. |
| `{{JAVA_PACKAGE_PATH}}` | Slash path derived from `{{JAVA_BASE_PACKAGE}}`. |
| `{{MAVEN_GROUP_ID}}` | Maven group id, defaulting to `{{JAVA_BASE_PACKAGE}}`. |

## Starter-owned roots

- `backend/` — Akka Java SDK backend template source for the secure SaaS foundation, user administration, governed agent records, workstream API services, backend-owned in-app notification projection/lifecycle/preferences, and static frontend hosting endpoint.
- `frontend/` — React/Vite/TypeScript workstream frontend source, including the My Account in-app notification center surface, built into `src/main/resources/static-resources/`.
- `.env.example` — local environment template for WorkOS/AuthKit, JWT, Resend, bootstrap admin, frontend public AuthKit values, and optional model-provider values.
- `app-description/` — maintained app description seed for scaffold extension; identifies My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy as the initial five core v0 workstream set with `markdown_response` before full-core expansion, plus the workstream graph contract for role-specific dashboard attention, human surface graph nodes/edges, internal workstream agent graph follow-up, workstream expertise, governed-tools, and qualified browser-tool/agent-tool/internal-tool exposures.
- `specs/` — starter planning/checklist seed and scaffold provenance destination; keeps five-core-v0 readiness separate from full-core follow-up work and carries governed-tool/AuthContext/audit/runtime-validation context into implementation queues.

## Isolation rule

Legacy `src/main/java/com/example/**`, `src/test/java/com/example/**`, and `frontend/**` assets are migration references only. Canonical starter code must live under this template path and must render with the selected package.
