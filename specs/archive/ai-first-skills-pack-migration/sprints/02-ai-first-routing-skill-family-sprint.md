# Sprint 2: AI-First Routing Skill Family

## Sprint goal

Create the first AI-first SaaS skill family so high-level product inputs route through an agentic operating model before Akka component decomposition.

## Dependencies

- Sprint 1 canonical doctrine and routing update complete.

## Scope

- Add `skills/ai-first-saas/SKILL.md` as the top-level entry skill.
- Add focused companion skills only where they route real work.
- Keep companion skills concise and orchestration-oriented; do not duplicate Akka implementation skills.

## Candidate skill set

Initial creation target:

- `ai-first-saas`
- `ai-first-saas-object-model`
- `ai-first-saas-agent-team-design`
- `ai-first-saas-policy-governance`
- `ai-first-saas-ui-surfaces`
- `ai-first-saas-decision-cards`
- `ai-first-saas-audit-trace`
- `ai-first-saas-outcomes-metrics`

Potential later skills, only if justified:

- `ai-first-saas-runtime-orchestration`
- `ai-first-saas-permission-enforcement`
- `ai-first-saas-replay-simulation`
- `ai-first-saas-security-privacy`
- `ai-first-saas-testing-evaluation`
- `ai-first-saas-risk-confidence-calibration`
- `ai-first-saas-curation-digest`
- `ai-first-saas-conversation-to-durable-objects`

## Acceptance behavior

- A prompt like “build an AI-first SaaS app where agents do operational work and humans approve exceptions” routes to `ai-first-saas` before `akka-solution-decomposition`.
- The AI-first skills map concepts such as goals, plans, policies, decisions, traces, approvals, exceptions, and outcomes onto existing Akka and web UI skill families.
- The skill index avoids broken links to missing companion skills.

## Done criteria

- Skill files exist for the approved initial set.
- `skills/README.md` documents when to start with `ai-first-saas`.
- Existing agent and web UI routing references know when AI-first companion skills should be loaded.
