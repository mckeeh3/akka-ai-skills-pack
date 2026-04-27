---
name: akka-web-ui-forms-validation
description: Implement lightweight TypeScript form handling for Akka-hosted web UIs, including client validation, submit state, server validation mapping, and accessible errors.
---

# Akka Web UI Forms and Validation

Use this skill when the browser UI has forms, commands, mutations, approvals, uploads metadata, filters, or other user input.

## Required reading

- `../../../docs/web-ui-frontend-decomposition.md`
- `../../../docs/web-ui-api-contract-patterns.md`
- `../../../docs/web-ui-quality-checklist.md`
- `../akka-http-endpoint-component-client/SKILL.md`

## Form rules

1. Use semantic `<form>`, `<label>`, `<input>`, `<select>`, `<textarea>`, and `<button>` elements.
2. Every input needs an accessible label.
3. Validate required fields and simple format rules in the browser for fast feedback.
4. Keep authoritative business validation on the backend.
5. Map backend validation errors back to field-level or form-level messages.
6. Disable submit buttons while submitting; show progress.
7. Make success and failure outcomes visible.
8. Do not lose user input after validation failure.
9. Avoid optimistic updates unless the rollback behavior is explicit.

## TypeScript structure

Prefer `forms.ts` with:
- `read<FormName>Form(form: HTMLFormElement)`
- `validate<FormName>Input(input)`
- `render<FormName>Errors(errors)`
- `wire<FormName>Form(elements, callbacks)`

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
- endpoint integration tests for success and validation failure

## Accessibility checklist

- errors are associated with fields using `aria-describedby` where appropriate
- the first invalid field receives focus after failed validation
- form-level errors are announced or placed before the form
- controls remain keyboard-operable
