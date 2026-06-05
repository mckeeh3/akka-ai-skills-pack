# Purchase Request App Description Example

> Mechanics-only reference. This directory demonstrates app-description layer cross-linking for a low-agentic approval workflow; it is not the canonical generated AI-first SaaS target architecture.

This directory is a concrete example of the internal app-description architecture defined in:
- `../../internal-app-description-architecture.md`
- `../../app-description-maintenance-flow.md`

It shows a small but cross-linked **harness-maintained application description** for a purchase-request workflow.

AI-first SaaS / agent workstream reset note: this remains a low-agentic/conventional approval-workflow reference for app-description mechanics only. It is **not** target architecture doctrine and must not be used as the first reference for generated SaaS planning. It intentionally does not teach the mandatory generated-app structure of functional agents, workstreams, structured surfaces, secure SaaS foundation, and horizontal Akka maps. Start new SaaS work from the target project `app-description/README.md` plus `../../docs/core-ai-first-saas-foundation.md`, `../../agent-workstream-design-review-checklist.md`, and the core foundation docs instead. Do not force-fit this example into a full AI-first SaaS operating model without explicit product intent.

Purpose of this example:
- validate the layer structure in practice
- give app-description skills a concrete reference target
- demonstrate how capabilities, behavior, tests, security, observability, readiness, and traceability connect in a compact mechanics-only example
- avoid presenting page/screen hierarchy as the primary generated SaaS architecture

Example root:

```text
docs/examples/purchase-request-app-description/app-description/
```

This example is illustrative.
It is not the only valid internal format.
The important part is the layer responsibility and cross-linking pattern.

Repository/use distinction:
- in this source repository, this directory is a packaged reference example for the skills pack
- in a real development project using a skills-only harness install, the actual maintained `app-description/` tree should live in that project workspace rather than inside `.agents/`, unless the project deliberately chooses another internal location
