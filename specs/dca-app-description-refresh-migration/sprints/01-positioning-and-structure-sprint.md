# Sprint 1: Positioning and Structure

## Sprint goal

Reposition `docs/examples/ai-first-dca-app-description/` as a domain-rich vertical extension of the canonical secure AI-first SaaS seed example, and bring its top-level app-description control structure closer to the current architecture.

## Scope

- Update README/app-description framing so DCA is not mistaken for the canonical seed template.
- Remove or contextualize stale migration-era wording such as unqualified "Sprint 6" status.
- Add current `00-system` control files: readiness status and generation policy.
- Produce a concise structure gap summary for later sprints.

## Expected outputs

- Refreshed DCA top-level README and app-description README.
- `00-system/readiness-status.md`.
- `00-system/generation-policy.md`.
- Structure gap summary under review/control area.

## Acceptance behavior

A future agent should know:

- the secure SaaS seed example is canonical for baseline structure;
- DCA is a vertical/domain reference;
- DCA is intentionally non-runnable until later realization work;
- remaining gaps are explicit rather than hidden.

## Defer list

- Do not refactor all capabilities in this sprint.
- Do not create executable Akka/React code.
- Do not solve all auth/security details beyond readiness/generation framing.
