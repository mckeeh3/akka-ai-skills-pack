# App-description source alignment

App-description source alignment is the file-level contract that records which implementation artifacts realize each feature-bearing current-intent graph slice. It complements workstream lifecycle state: lifecycle records the readiness/alignment decision, while source alignment records the concrete app-description files and source/test/frontend files used to make that decision.

## Purpose

Use source alignment to make stale implementation detectable without rereading the whole app-description:

- a changed app-description file can identify the source, frontend, API, test, and validation artifacts likely affected;
- a changed source file can identify the app-description nodes that should be reconciled;
- workstream lifecycle state can move to `stale-description-changed`, `stale-code-changed`, `partially-aligned`, `aligned`, or `unknown` with file-level evidence;
- generation and maintenance tasks can update alignment evidence as part of completion.

Source alignment is a traceability artifact. It does not replace the product semantics in surfaces, governed tools, capabilities, policies, traces, or tests, and it does not authorize runtime behavior.

## Required location

New current-intent app-description workstreams should include a source-alignment realization artifact:

```text
app-description/domains/<domain>/workstreams/<workstream>/realization/source-alignment.md
```

Machine-readable projects may also maintain a sibling JSON file:

```text
app-description/domains/<domain>/workstreams/<workstream>/realization/source-alignment.json
```

Use Markdown as the durable human-maintained minimum. Use JSON only when a checker or generator will consume it.

## Minimum Markdown contract

Each `source-alignment.md` should contain:

```markdown
# <Workstream> source alignment

Lifecycle: ../lifecycle.md
Last reviewed: <date-or-unknown>
Alignment state: aligned | stale-description-changed | stale-code-changed | partially-aligned | unknown | not-started | blocked

## Alignment entries

| Entry id | App-description files | Implementation files | Test / validation files | Last aligned evidence | Notes |
| --- | --- | --- | --- | --- | --- |
| <stable-id> | `<paths>` | `<src/frontend/resources paths>` | `<test/spec/runtime-validation paths>` | <commit/check/date/digest> | <scope, gaps, no-code-impact note> |

## Unmapped current-intent files

- `<app-description path>` — reason: not implemented yet / description-only / blocked / unknown.

## Unmapped implementation files

- `<source path>` — reason: shared platform / generated build output / needs description reconciliation / unknown.
```

## Optional JSON shape

When tooling is desired, use this shape or a strict subset:

```json
{
  "workstreamId": "user_admin",
  "alignmentState": "aligned",
  "lastReviewedAt": "2026-06-25T00:00:00Z",
  "entries": [
    {
      "id": "invitation-create",
      "descriptionFiles": [
        "app-description/domains/core-starter/workstreams/user-admin/tools/create-invitation.md",
        "app-description/domains/core-starter/capabilities/user-and-access-administration.md"
      ],
      "implementationFiles": [
        "src/main/java/ai/first/api/coreapp/admin/UserAdminEndpoint.java",
        "src/main/java/ai/first/application/coreapp/admin/invitation/InvitationWorkflow.java",
        "frontend/src/extensions/core-starter/user-admin/InviteUserForm.tsx"
      ],
      "testFiles": [
        "src/test/java/ai/first/coreapp/admin/UserAdminEndpointTest.java",
        "frontend/src/extensions/core-starter/user-admin/InviteUserForm.test.tsx"
      ],
      "lastAlignedEvidence": {
        "commit": "unknown",
        "checks": ["mvn test", "npm --prefix frontend test -- --run"],
        "runtimeValidationScenario": "open invite form, submit authorized invite, verify result surface and audit trace"
      },
      "notes": "Human surface action and confirmed chat plan share governed tool id useradmin.invitation.create."
    }
  ],
  "unmappedDescriptionFiles": [],
  "unmappedImplementationFiles": []
}
```

## Staleness rule

A file-level timestamp, Git commit, or digest comparison is a signal, not the final authority:

- if any mapped app-description file is newer than its mapped implementation or test files, default the owning workstream to `stale-description-changed` unless an explicit alignment review records `description-only` or `no-code-impact`;
- if any mapped implementation file changed after the mapped app-description files without a matching description reconciliation, default to `stale-code-changed` or `partially-aligned`;
- if mappings are missing for a feature-bearing node, use `unknown` or `not-started` rather than `aligned`;
- generated static build output may be excluded or mapped as derived output when the source file is already mapped.

Do not treat mtimes alone as proof that behavior is runtime-ready. The runtime-ready claim still requires the compile contract, automated checks, and the real API/UI/agent runtime-validation path where applicable.

## Required update points

Update source alignment when:

- bootstrapping a new feature-bearing workstream, even if entries start as `not-started` or `unknown`;
- adding or changing surfaces, governed tools, capabilities, agents, policies, traces, tests, or realization files;
- implementing code, frontend, endpoints, agents, Akka components, tests, or validation paths for a graph slice;
- reconciling runtime-validation failures or stale implementation discoveries;
- recording a no-code-impact alignment review.

## Relationship to lifecycle

`source-alignment.md` provides file-level evidence for the owning `lifecycle.md` record. Keep them consistent:

- lifecycle `implementationAlignment` summarizes the workstream state;
- source alignment entries identify the files and evidence behind that summary;
- lifecycle `last alignment review`, `last compile`, and `last runtime-validation run` should point to the relevant alignment entries or evidence.

See also [Current intent model](current-intent-model.md), [App-description component graph](app-description-component-graph.md), and [App-description to code compile contract](app-description-to-code-compile-contract.md).
