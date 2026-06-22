# Sprint 01: Router Contract and User Admin Proof

## Goal

Introduce the deterministic surface intent routing contract and prove it with User Admin Organization Admin routing before model-backed chat.

## Required outcomes

- A backend router contract exists for matching `(functionalAgentId, prompt, selected AuthContext)` to a target surface and safe prefill payload.
- `WorkstreamService.submitMessage(...)` or an adjacent backend path attempts routing before invoking the model-backed workstream agent.
- `create organization "Org 1"` opens the Organization Create surface with prefilled `organizationName` and no mutation.
- Simple User Admin aliases such as `show organizations`, `show users`, and `invite user alice@example.com` route to appropriate surfaces/forms where feasible.
- Ambiguous or unsupported prompts still fall back to governed model-backed chat.

## Validation focus

- no model/runtime invocation for matched deterministic routes;
- backend authorization is still selected-context based;
- route result surfaces are browser-safe and carry traces/correlation ids;
- no side-effecting command is submitted by the router.
