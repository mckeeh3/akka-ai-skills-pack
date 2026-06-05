# Normalized Input Example

This file shows the kind of structured envelope that `app-description-input-normalization` should produce before routing and maintenance.

## Example raw prompt

> tighten security so only managers can approve purchase requests, add audit visibility for approvals, and once that is updated tell me if the description is ready to generate

## Example normalized envelope

```md
# Normalized App Description Input

## Raw input summary
- tighten approval security, add approval audit visibility, and assess readiness afterward

## Primary intent
- mixed

## Secondary intents
- description-change
- review

## Confirmed deltas
- capabilities:
  - none explicitly changed
- behavior:
  - approval action is restricted to managers
- tests:
  - approval authorization and audit-related verification are implied
- auth/security:
  - only managers may approve purchase requests
- observability:
  - approval actions require audit visibility

## Candidate inferred deltas
- negative verification should deny non-manager approval attempts
- readiness may need reassessment because production constraints changed

## Realization request
- none

## Review request
- readiness

## Constraints and preferences
- perform readiness review after description updates

## Open questions
- should rejection be restricted to managers under the same rule?
- what exact audit fields are required for approval visibility?
```
```