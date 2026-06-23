---
name: akka-web-ui-forms-validation
description: Implement form handling for Akka-hosted full web apps, including client validation, submit state, server validation mapping, and accessible errors in standard frontend projects.
---

# Akka Web UI Forms and Validation

Use this skill when the browser UI has forms, commands, mutations, approvals, uploads metadata, filters, or other user input.


## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the required capability, AuthContext/scope, DTO, side-effect, trace, and test inputs are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Required reading

- `../docs/web-ui-frontend-decomposition.md`
- `../docs/web-ui-api-contract-patterns.md`
- `../docs/web-ui-quality-checklist.md`
- `../akka-http-endpoint-component-client/SKILL.md`

## Form rules

1. Use semantic `<form>`, `<label>`, `<input>`, `<select>`, `<textarea>`, and `<button>` elements.
2. Every input needs an accessible label.
3. Structured-surface forms, including `detail-edit` and settings surfaces, must render inputs/selects/textareas with the selected style guide's tokenized control styling; browser-default/native-looking controls are unacceptable.
4. Validate required fields and simple format rules in the browser for fast feedback.
5. Keep authoritative business validation on the backend.
6. Map backend validation errors back to field-level or form-level messages.
7. Disable submit buttons while submitting; show progress.
8. Make success and failure outcomes visible.
9. Do not lose user input after validation failure.
10. Avoid optimistic updates unless the rollback behavior is explicit.
11. For named-theme settings fields, preview the selected named theme immediately on change, but persist only through the governed Save/Confirm action path.
12. For forms opened by deterministic surface routing, show routed prefill as visible, editable, clearable advisory input and do not auto-submit.
13. For confirmed chat tool plans, render the proposed inputs and consequences for review, require explicit confirmation before submit/execution, preserve the plan/confirmation id, and require reconfirmation if the plan or material inputs change.

## Frontend structure

Keep form logic in the frontend source of record: use the project's component, hook, service, or form-module conventions under `frontend/src/**`.

Keep these responsibilities explicit in whichever structure the project uses:
- read or bind form input
- validate client-side input
- render field-level and form-level errors
- apply reusable tokenized structured-surface form control classes/selectors for inputs, selects, textareas, focus, disabled, and validation states
- wire submit behavior and disabled/submitting state

Represent validation errors structurally:

```ts
type FieldError = { field: string; message: string };
type FormError = { message: string; fields: FieldError[] };
```

## Endpoint contract checklist

For each form action, verify:
- command route and HTTP method
- request body shape
- success response and redirect/update behavior
- validation error status/body
- idempotency or duplicate-submit behavior
- authorization failures
- confirmation id and plan-binding behavior for `human_chat_tool_plan` endpoints when allowed
- endpoint integration tests for success, validation failure, denial-before-confirmation, and partial-failure result mapping where applicable

## Accessibility checklist

- errors are associated with fields using `aria-describedby` where appropriate
- the first invalid field receives focus after failed validation
- form-level errors are announced or placed before the form
- controls remain keyboard-operable
- styled structured-surface controls retain visible focus and accessible labels across named themes
