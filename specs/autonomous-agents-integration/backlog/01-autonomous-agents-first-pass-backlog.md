# Backlog 01: Autonomous Agents First-Pass Migration

## Goal

Integrate Akka Autonomous Agents across docs, skills, routing, coverage tracking, and initial executable examples/tests.

## Harness task breakdown

1. **Research notes** — deeply read official docs and samples; create local research notes with API facts, routing heuristics, governance implications, and example targets.
2. **Doctrine and routing** — update core architecture docs and `skills/README.md` so Autonomous Agents are first-class for durable internal/background agents.
3. **Skill family** — add installable Autonomous Agent skills and update package manifest/routing.
4. **Guidance consistency** — update existing agent/workflow/testing/governance docs and coverage matrix for consistency.
5. **Single-agent executable example** — add minimal Autonomous Agent task/result/client/test reference under `src/`.
6. **Coordination executable example** — add focused coordination example/test, preferably delegation first, with handoff/external input queued if too broad.
7. **Starter/internal-background alignment** — update starter template guidance or implementation references where internal/background agents are represented, without replacing user-facing workstream request/response agents.
8. **Verification loop** — review all changes, append follow-up tasks for gaps, and add another verification task when follow-ups are appended.
9. **Additional examples planning** — once the verification loop is clear, define any remaining executable example/test tasks.

## Dependencies

- Research notes precede doctrine, skills, examples, and starter updates.
- Doctrine/routing precedes skill consistency updates.
- Skill family should exist before examples rely on local skill instructions.
- Verification runs after the first-pass tasks.

## Required checks by task type

- Docs/skills: `git diff --check` plus targeted `rg` checks.
- Skills/package updates: include manifest/export checks used by existing skill tasks if present.
- Java examples/tests: `mvn test` or the narrowest valid Maven test command for the touched examples.
- Verification: `rg`-based coverage checks plus queue review.

## Acceptance criteria

- Future harness sessions can route natural-language background/internal agent requests to Autonomous Agent guidance.
- Future harness sessions can still route workstream request/response requests to request-based Agent guidance.
- Official Akka docs remain the semantic source of truth and local guidance is agent-optimized rather than copied wholesale.
- Examples prove actual Akka SDK usage with deterministic test model providers, not mocked normal runtime guidance.
