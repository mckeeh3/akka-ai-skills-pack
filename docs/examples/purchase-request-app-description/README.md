# Purchase Request App Description Example

This directory is a concrete example of the internal app-description architecture defined in:
- `../../internal-app-description-architecture.md`
- `../../app-description-maintenance-flow.md`

It shows a small but cross-linked **harness-maintained application description** for a purchase-request workflow.

Purpose of this example:
- validate the layer structure in practice
- give app-description skills a concrete reference target
- demonstrate how capabilities, behavior, tests, security, observability, readiness, and traceability connect

Example root:

```text
docs/examples/purchase-request-app-description/app-description/
```

This example is illustrative.
It is not the only valid internal format.
The important part is the layer responsibility and cross-linking pattern.

Repository/use distinction:
- in this source repository, this directory is a packaged reference example for the skills pack
- in a real development project using the installed pack, the actual maintained `app-description/` tree should live in that project workspace rather than inside `.agents/`, unless the project deliberately chooses another internal location
