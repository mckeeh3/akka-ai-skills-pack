# Workstream Surface Intent Routing

## Status and scope

This document defines the default generated-app guidance for routing natural-language workstream composer requests to structured surfaces before model-backed chat. Use it with `./agent-workstream-application-architecture.md`, `./structured-surface-contracts.md`, `./workstream-contract.md`, `./workstream-expertise-model.md`, and `./web-ui-api-contract-patterns.md` whenever a new workstream, surface graph, functional agent, or browser composer is planned or implemented.

Surface intent routing is a fast, deterministic, no-mutation request path. It lets users type requests such as `create customer "Acme"`, `invite user jane@example.com`, `show policies`, or `open audit trace abc` and receive the relevant authorized surface, often prefilled, without waiting for a model response and without granting the workstream agent direct command authority.

## Core rule

Every new generated workstream with a persistent composer must define deterministic surface intent routing before falling back to model-backed workstream chat:

```text
composer prompt
→ selected AuthContext and functional-agent workstream
→ deterministic high-confidence surface intent router
→ authorized surface request or prefilled surface
→ user reviews and submits through existing surface action
→ fallback to governed model-backed chat only when no safe route matches
```

The router may open, refresh, or prepopulate surfaces. It must not submit side-effecting commands, approve decisions, change policy, mutate records, send emails, activate behavior, or bypass confirmation. Consequential operations still occur only through backend-authorized surface actions, APIs, workflows, or future separately-governed agent tools.

## Why this is mandatory

Surface routing is the safest first response to operational language. It:

- gives users fast feedback without a provider/model round trip;
- teaches the workstream's structured surfaces and action vocabulary;
- avoids treating prompt text as authorization;
- keeps the human supervisor in the review/submit loop;
- reduces prompt-injection and ambiguity risk for side-effecting operations;
- lets unmatched prompts still use governed model-backed chat with provider fail-closed behavior.

## Required workstream contract additions

For each workstream, add a **surface intent routing catalog** alongside the surface graph and expertise bundle. Each catalog entry should include:

| Field | Required content |
|---|---|
| `intentId` | Stable semantic id such as `customer.create.open`, `policy.search.open`, or `audit.trace.open`. |
| Prompt patterns | High-confidence examples and aliases; avoid broad patterns that could capture unrelated requests. |
| Target surface | `targetSurfaceId`, owner functional agent, result placement, and request/result append behavior. |
| Prefill mapping | Browser-safe fields the router may infer, their source phrases, validation notes, and editable/clearable behavior. |
| Required capability | Capability/governed-tool ids used to authorize opening the surface or reading target data. |
| Forbidden effects | Explicit statement that the route does not submit, approve, activate, revoke, invite, archive, send, or otherwise mutate. |
| Ambiguity behavior | Ask/open selection/help surface or fall back to model-backed chat; do not guess hidden targets. |
| Denial/system message | Safe unavailable shape for missing capability, hidden target, stale deep link, or unsupported selected context. |
| Trace | Correlation id, route decision trace, source prompt, selected AuthContext, target surface, and no-mutation evidence. |
| Tests | Matched route, fallback route, denied route, prefill rendering, no command submission, and no model invocation for matched routes. |

At `surface-ready`, the workstream should know which surfaces can be opened from composer prompts. At `capability-ready`, protected routes must map to backend capabilities. At `expertise-ready`, the functional agent's prompt/skills/references should explain these surfaces and recommend them without claiming command authority. At `runtime-ready`, the local API/UI path must prove matched prompts open the expected surfaces.

## Router behavior

Implement the router as deterministic backend-owned application behavior, not as a frontend-only shortcut and not as a model prompt convention. The backend should:

1. resolve the signed-in identity and selected `AuthContext`;
2. restrict matching to the selected functional-agent workstream unless an explicit authorized cross-workstream request is supported;
3. match only high-confidence patterns from the workstream's routing catalog;
4. authorize target surface visibility or read capability server-side;
5. produce a normal workstream request item and one primary result surface or typed `system_message`;
6. attach browser-safe prefill data under the surface payload or a documented `prefill` envelope field;
7. record route decision traces and no-mutation evidence;
8. fall back to the governed model-backed workstream agent when no deterministic route matches.

Do not rely on the frontend to enforce hidden/denied states. The frontend may provide local convenience, but the protected `/api/...` route that handles composer submissions must re-run authorization and target resolution.

## Prefill rules

Prefill is advisory input state, not committed data.

- Prefilled fields must remain visible, editable, and clearable.
- Forms must still run normal validation before submission.
- Idempotency keys for eventual submit actions must still be generated/validated by the surface/action path, not by a model-only inference.
- Prefill must not include secrets, hidden ids, privileged policy facts, raw traces, provider/model data, prompt internals, or cross-tenant/customer data.
- If a target object is referenced by name and multiple visible matches exist, open a selection/list surface or ask for clarification rather than guessing.
- If the router cannot safely infer a required field, open the blank/create surface with a helper message instead of inventing a value.

Example route:

```text
User prompt: create organization "Org 1"
Route: user-admin.organization.create.open
Result: Organization Create surface
Prefill: { organizationName: "Org 1", reasonHint: "Requested from User Admin composer" }
Forbidden effects: no organization is created until the human submits the Create Organization action
```

## Agent familiarity guidance

Every LLM-backed functional agent should know its workstream's surface catalog. Prompt, skill, or reference material should teach the agent to:

- answer how to use the workstream's surfaces;
- recommend specific surfaces and actions by user-facing name;
- explain that the router may open/prepopulate surfaces for review;
- avoid claiming that it directly mutated state unless a separately-governed agent tool actually performed a backend-authorized operation;
- preserve selected AuthContext, capability, approval, idempotency, and audit boundaries in explanations;
- direct users to structured surfaces for consequential work.

This familiarity does not grant tool access. Human surface availability does not imply AI tool availability. If later work exposes direct agent tools, model those tools separately through `ToolPermissionBoundary`, schemas, approvals, idempotency, and traces.

## Frontend implementation guidance

The browser should render routed surfaces as ordinary workstream results:

- append a prompt-like request item with the user's original text;
- append/open the target surface as the primary result;
- show concise helper copy such as “Opened from your request. Review the prefilled fields before submitting.”;
- preserve focus management, keyboard operation, loading/empty/error/forbidden states, and responsive behavior;
- keep submit buttons and confirmation surfaces explicit;
- do not auto-click or auto-submit actions after routing.

## Testing requirements

For each new workstream route family, include tests that prove:

- matched deterministic routes do not invoke the model-backed runtime;
- matched create/edit/task routes do not mutate before the user submits a surface action;
- prefill fields render and remain editable;
- unauthorized or hidden targets return safe `system_message` or fallback behavior without enumeration;
- ambiguous prompts do not guess hidden targets;
- unmatched prompts still use the governed model-backed workstream path and provider/runtime fail-closed behavior;
- trace/correlation/no-mutation evidence is recorded.

## Planning checklist

When generating a new workstream or adding a substantial surface family, create bounded tasks for:

1. surface routing catalog definition;
2. backend deterministic router and authorization path;
3. prefilled surface rendering;
4. no-model/no-mutation tests;
5. workstream agent familiarity updates;
6. local API/UI/browser smoke at the claimed readiness level.

Do not close a workstream as `runtime-ready` until the intended local route from composer prompt to authorized surface rendering works at the selected scope, with denial and trace evidence.
