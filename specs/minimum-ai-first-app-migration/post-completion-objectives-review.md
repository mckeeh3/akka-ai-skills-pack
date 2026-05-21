# Post-completion Objectives Review

## Review date

2026-05-21

## Review scope

The review checked whether the completed minimum AI-first app migration met the objectives from the original discussion:

- minimum generated SaaS app is **User Admin workstream v0**, not a generic chatbot;
- first renderable surface is `markdown_response`, rendered as sanitized HTML;
- minimum starter still includes bootstrap authorization, selected `AuthContext`, backend role/capability boundary, durable workstream log, and audit/work trace substrate;
- full-core readiness remains stricter and requires complete User Admin, Agent Admin, Audit/Trace, invitations/onboarding, governed runtime agent documents, and full security coverage;
- skills route natural language starter/basic/chatbot-like SaaS prompts to this model;
- packaging/install exposure remains sufficient for installed packs.

## Files reviewed

- `docs/minimum-ai-first-saas-app.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/core-ai-first-saas-foundation.md`
- `skills/README.md`
- `skills/ai-first-saas/SKILL.md`
- `skills/agent-workstream-apps/SKILL.md`
- `skills/core-saas-foundation/SKILL.md`
- `skills/app-description-readiness-assessment/SKILL.md`
- `skills/app-generate-app/SKILL.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`
- `skills/akka-solution-decomposition/SKILL.md`
- `skills/app-description-bootstrap/SKILL.md`
- `skills/app-description-functional-agent-modeling/SKILL.md`
- `skills/app-description-surface-modeling/SKILL.md`
- `docs/examples/ai-first-saas-seed-app-description/README.md`
- `docs/examples/core-ai-first-saas-input/01-core-seed-progression-plan.md`
- `templates/ai-first-saas-starter/specs/README.md`
- `pack/manifest.yaml`
- local installed `.agents` parity for docs/skills exposure

## Commands run

```bash
git status --short
rg -n "minimum-ai-first-saas-app|minimum AI-first|minimum app|markdown_response|User Admin workstream v0|basic chatbot|generic chatbot|simple chatbot|chatbot" docs skills templates specs --glob '!specs/minimum-ai-first-app-migration/**'
rg -n "minimum-ai-first-saas-app" pack/manifest.yaml skills/*/SKILL.md skills/README.md
rg -n "explicitly deferred only|Missing foundation functional-agent|must model these foundation concepts|For every generated SaaS app|mandatory secure SaaS foundation" docs/core-ai-first-saas-foundation.md skills/core-saas-foundation/SKILL.md skills/README.md
test -f .agents/docs/minimum-ai-first-saas-app.md && echo yes || echo no
rg -n "minimum-ai-first-saas-app|User Admin workstream v0|markdown_response" .agents/skills .agents/docs .agents/manifests 2>/dev/null | head -40
```

## Overall assessment

The migration mostly meets the conceptual objectives. The core doctrine and primary routing now correctly identify the minimum generated SaaS app as **bootstrap-authorized User Admin workstream v0** with `markdown_response`, not a generic chatbot. The main docs and routing skills preserve capability-first backend modeling, workstream logs, audit/work traces, and the distinction between minimum starter readiness and full-core readiness.

However, the review found several follow-up gaps that should be fixed before considering the pack fully hardened for installed-pack use and future harness sessions.

## Findings

### Finding 1: Package manifest does not expose the new minimum doctrine as a reference

`docs/minimum-ai-first-saas-app.md` exists and is referenced by skills, but `pack/manifest.yaml` `content.references` does not list it. The build script copies the complete docs tree, so the file may still be physically bundled, but the manifest reference list is the pack's explicit exposure metadata and should include canonical doctrine referenced by top-level routing.

Related observation: the local project `.agents` install is stale and does not contain `.agents/docs/minimum-ai-first-saas-app.md` or the updated minimum-app routing text. `.agents` is not tracked, but the repository should have a task to verify installed-pack parity after manifest/source updates.

Queued task: `TASK-MINAPP-05-001`.

### Finding 2: Core foundation skill still has all-or-nothing wording that conflicts with minimum starter readiness

`skills/core-saas-foundation/SKILL.md` correctly adds Slice 0, but later route-specific/output checklist language still says missing full foundation semantics must block generation and that foundation objects are present or explicitly deferred only for non-SaaS reference work. That conflicts with the new accepted generated-SaaS minimum starter scope, where full-core objects may be explicitly deferred as follow-up gates.

Examples:

- `Route-specific requirements / App-description paths` says missing full foundation semantics must make readiness not-ready or block generation without scoping that to full-core/app-specific targets.
- `Output checklist` says full foundation objects are present or explicitly deferred only for non-SaaS reference work, excluding minimum starter as an accepted narrower generated-SaaS state.

Queued task: `TASK-MINAPP-05-002`.

### Finding 3: `markdown_response` is not in the canonical surface type list and minimum doctrine is missing from some required-read lists

`docs/structured-surface-contracts.md` defines `markdown_response`, and `docs/agent-workstream-application-architecture.md` mentions it in the minimum section. But the canonical surface type list in `docs/agent-workstream-application-architecture.md` does not include `markdown_response`, which can lead future agents to overlook it when they scan the surface taxonomy.

Also, the required-reading lists for `skills/agent-workstream-apps/SKILL.md` and `skills/core-saas-foundation/SKILL.md` do not include `docs/minimum-ai-first-saas-app.md`, even though both skills route minimum/starter/chatbot-like prompts through it. Required reads should make the new doctrine loadable in the exact sessions that need it.

Queued task: `TASK-MINAPP-05-003`.

## Conclusion

No large conceptual redesign is needed. The migration achieved the main model shift. The remaining work is targeted hardening:

1. expose the new doctrine in pack metadata and verify installed-pack parity;
2. remove residual all-or-nothing wording in core foundation guidance;
3. make `markdown_response` and the minimum doctrine more discoverable from surface taxonomy and required-read lists.
