# Visual UX Quality Standard

## Purpose

This standard defines the visual and interaction quality bar for the full-core SMB SaaS hardening program. It applies to dashboards, structured surfaces, workstream shell behavior, agent activity, decision/governance/audit experiences, and any root `frontend/` synchronization needed after starter template changes.

The goal is an attractive AI-first workstream application that a real SMB operator would trust for consequential work. It must not regress into page-first CRUD, generic admin dashboards, or chat-only output.

## Required style stance

Use the canonical AI-first style system from `docs/web-ui-style-guide.md` unless a later child mini-project records an accepted custom style brief that preserves the same AI-first anatomy.

Selected default:

- style id: `atlas-ops-supervisory-console`
- visual direction: calm operational supervision interface for delegated agent work
- emphasis: attention, decisions, exceptions, policy boundaries, auditability, and outcomes
- forbidden patterns: generic dashboard/CRM/project-management skins, decorative chat bubbles as the main work model, purple-gradient generic AI chrome, page-first CRUD navigation, letter-only workstream icons, and frontend-only authorization affordances

## Workstream shell standard

The shell must make the workstream/surface architecture obvious and usable.

Required qualities:

- left rail contains role-authorized functional agents with descriptor-backed icons, tooltips, accessible labels, and backend-derived attention state;
- My Account is launched from the lower-left signed-in user tile/email, not duplicated as a top workstream item;
- selected workstream has a clear title, purpose, authority/context indicator, trace affordance, and current attention status;
- main panel reads as a continuous workstream, not a page stack;
- persistent composer remains available for workstream-specific requests but does not visually become the sole source of truth;
- prompt-entered navigation, rail selection, My Account panels, deep links, and surface actions produce honest prompt-like request items with origin metadata and safe denial/system-message behavior;
- narrow screens preserve workstream selection, context/authority indicators, and top attention/action content without hiding critical state.

## Dashboard standard

Every full-core workstream dashboard must answer, at a glance:

1. what is happening in this workstream;
2. what needs my attention now;
3. what is blocked, risky, overdue, failed, stale, or waiting for approval;
4. which humans, request/response agents, internal worker agents, workflows, or timers are participating;
5. what I am authorized to do next;
6. where I can inspect traces and evidence.

Dashboard visual quality requirements:

- strong hierarchy: attention and decisions above FYI metrics;
- compact summary cards that use labels and icons, not color alone;
- empty/first-run states that explain the next useful SMB action;
- backend-derived attention counts and status badges;
- trace/correlation links visible near consequential results;
- attractive card density, spacing, typography, and responsive stacking;
- no fake/demo metric claims unless explicitly labeled as fixture/test data.

## Structured surface standard

All meaningful workstream output must be a typed structured surface or a typed `system_message` surface.

Required surface qualities:

- stable surface identity, type, version, owner functional agent, placement, payload schema, actions, and trace ids;
- loading, empty, ready, submitting, success, warning, error, forbidden, stale/reconnect, approval-needed, and no-op states where relevant;
- visible redaction/permission boundaries when fields or actions are unavailable;
- action bars that distinguish read/surface-request, command, proposal, approval, workflow, governance, and trace actions;
- button labels that describe outcomes, not implementation endpoints;
- backend capability ids and traces available in implementation/test artifacts;
- responsive layouts that keep forms, tables, timelines, diffs, decision cards, and task-progress panels usable on smaller screens;
- accessible headings, labels, keyboard paths, focus rings, live status announcements, and non-color-only status semantics.

`markdown_response` remains acceptable for explanations and summaries, but it must not hide required table, form, dashboard, audit timeline, decision, diff, policy, or task-progress surfaces once those interactions are in scope.

## Agent activity and worker visibility

The UI must make AI work legible without turning the app into an opaque chat transcript.

Request/response workstream agent turns should show:

- producing functional agent;
- selected AuthContext and capability context where relevant;
- generation/loading/provider-failure states;
- trace ids for prompt assembly, skill/reference loads, model invocation, and work results;
- safe system-message surfaces when provider configuration is missing or a tool/capability is denied.

AutonomousAgent/internal worker activity should show:

- task purpose, owner, status, progress, blockers, dependencies, and result/recommendation state;
- last update time and next expected action;
- human review, approval, retry, cancel, or dismiss actions where authorized;
- trace links for task lifecycle, tool use, model calls, denials, and result acceptance/rejection;
- dashboard attention indicators when worker output needs a human.

Deterministic service activity should be visible where useful through clear status and trace surfaces, but the UI should not imply AI made mechanical authorization, lifecycle, or policy-enforcement decisions.

## Workstream icon and identity standard

Each workstream needs professional icon metadata suitable for the shell, dashboards, My Account panels, and status cards.

Required:

- semantic icon from an approved registry or fallback SVG based on actual workstream responsibility;
- stable id and visual hint;
- accessible label and tooltip using full workstream name;
- accent color token aligned with the style guide;
- no text initials as the normal icon fallback;
- no arbitrary emoji or color-only status meaning.

## Core surface family expectations

### My Account

Visual priority:

- cross-workstream personal attention dashboard;
- profile/settings shortcuts;
- authorized context selection;
- personal queue and compact accessible-workstream status panels;
- clear denials for inaccessible contexts/workstreams.

### User Admin

Visual priority:

- user directory dashboard with invitation/access attention;
- invite/resend/revoke/disable/reactivate forms and confirmations;
- access review queue;
- role/capability summaries;
- admin-risk or stale-invite worker summaries;
- audit links near every consequential action.

### Agent Admin

Visual priority:

- agent catalog and runtime readiness;
- behavior version cards for prompts, skills, references, manifests, tool boundaries, and model config;
- proposed diff/review surfaces;
- simulation/replay evidence;
- provider readiness and fail-closed diagnostics without secrets.

### Audit/Trace

Visual priority:

- trace search/filter command surface;
- timeline/detail surfaces with actor, AuthContext, capability, outcome, trace ids, evidence, and redaction;
- investigation summaries;
- export/copy/open-trace actions only when authorized;
- no leakage of hidden tenant/customer or secret data in denials.

### Governance/Policy

Visual priority:

- policy and threshold dashboard;
- approval and exception queues;
- decision cards with evidence, risk, confidence, impact, alternatives, and actions;
- behavior-change proposals and activation/rollback state;
- clear human authority boundary for expanded permissions or behavior changes.

## Accessibility and responsive standard

Every child implementation that touches UI must preserve:

- WCAG-aware contrast for text, controls, borders, status badges, and focus rings;
- keyboard operation for rail selection, composer, surface actions, forms, tables, dialogs, and deep links;
- focus management when surfaces append, refresh, error, or open from shell requests;
- screen-reader labels for icons, status badges, charts, and trace links;
- reduced-motion behavior for surface append/update, agent-working, approval-result, denial, and reconnect transitions;
- readable narrow-screen stacking with attention/decision content before lower-priority reports;
- chart/table alternatives or textual summaries where visualizations appear.

## Runtime and data honesty standard

Visual polish must not create false readiness.

- Fixture surfaces are allowed only in explicitly named fixture/test modes.
- Normal runtime UI must call protected backend APIs and render authorized payloads.
- Missing provider/security configuration must produce actionable blocked/error `system_message` surfaces and traces, not canned successful responses.
- Built frontend assets and browser payloads must not contain backend secrets, provider credentials, hidden prompt text, cross-tenant data, or support-only details.
- UI action visibility is advisory; backend denial must remain correct for manually submitted requests.
- If a visual element implies request/response agent work, AutonomousAgent/internal worker work, traceability, or approval state, the corresponding runtime path and data must exist at that task’s stated scope.

## Test and validation standard

UI tasks in child mini-projects should name or run checks for:

- rendering states for each new surface;
- action-to-capability request/response behavior;
- authorization denial and disabled-user states;
- tenant/customer isolation for rendered payloads;
- audit/work-trace links and correlation ids;
- markdown sanitization where `markdown_response` appears;
- workstream icon rendering and accessibility labels;
- keyboard/focus behavior;
- responsive/narrow layout;
- frontend secret-boundary scans;
- local Akka runtime smoke for API/UI paths when runtime behavior changes.

Broad starter changes should use `tools/validate-ai-first-saas-starter-fullstack.sh`. Focused frontend changes should run the relevant frontend tests, typecheck, and build in the rendered starter or synchronized root frontend as appropriate.

## Review checklist for child mini-projects

Before marking a visual/UI slice done, confirm:

- [ ] The workstream remains the root app unit; no page-first CRUD replacement was introduced.
- [ ] The surface or dashboard has a typed contract and maps actions to governed capabilities.
- [ ] Visual hierarchy prioritizes attention, decisions, risk, blocked work, and authorized next actions.
- [ ] Request/response agent, AutonomousAgent/internal worker, workflow, timer, and deterministic service states are visually distinct when present.
- [ ] Denials, provider failures, validation errors, and deferred work render as typed `system_message` surfaces.
- [ ] Trace/audit affordances are present near consequential results and actions.
- [ ] Accessibility, responsive behavior, and focus paths are acceptable.
- [ ] The UI does not expose secrets or imply unimplemented runtime behavior.
