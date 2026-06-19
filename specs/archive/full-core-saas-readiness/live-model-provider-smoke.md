# Live Model-Provider Workstream-Agent Smoke

- task: TASK-FCSR-08-003
- date: 2026-06-04
- result: passed

## Scope

Validated live model-backed workstream agent execution through the governed Akka Agent runtime path using backend-only provider environment variables.

No provider secrets are recorded in this artifact.

## Command

```bash
mvn test -Dtest=RealModelProviderSmokeTest -DrealModelProviderSmoke=true
```

Environment variables were supplied by the local shell:

- `OPENAI_API_KEY` set, value hidden
- `OPENAI_MODEL_ID` set, value hidden
- `OPENAI_API_BASE_URL` set, value hidden

## Result

The smoke passed:

- `Tests run: 1`
- `Failures: 0`
- `Errors: 0`
- `Skipped: 0`
- Maven build result: `BUILD SUCCESS`

Akka TestKit started the local runtime and discovered the expected core components, including:

- workstream HTTP endpoints;
- `/api/me` endpoint;
- the request-based `WorkstreamRuntimeAgent` component;
- key value entities, event sourced entities, views, and autonomous agents used by the core foundation.

## Runtime path proven

`RealModelProviderSmokeTest` submits workstream messages for all five core functional-agent workstreams:

- `agent-my-account`
- `agent-user-admin`
- `agent-agent-admin`
- `agent-audit-trace`
- `agent-governance-policy`

For each workstream, the test validates that normal message submission uses the governed runtime path:

```text
WorkstreamService.submitMessage
→ AgentRuntimeService runtime preparation
→ active AgentDefinition / prompt / manifests / tool boundary / model config-policy resolution
→ WorkstreamRuntimeAgent Akka Agent invocation
→ configured model provider
→ markdown_response surface
→ durable prompt/model/work trace shape
→ persisted workstream item/surface retrieval
```

## Secret-boundary evidence

The test asserts that the backend-only provider secret is not present in:

- `/api/me` response;
- markdown response content;
- workstream response DTOs;
- persisted workstream items;
- persisted workstream surfaces;
- runtime trace records.

## Trace evidence

The smoke asserts runtime traces include:

- `PROMPT_ASSEMBLY`
- `MODEL_INVOCATION`
- `AgentWorkTrace`

The test also asserts trace records do not contain the provider secret.

## Notes

The Akka stream layer logged an expected connection shutdown warning while TestKit stopped after the successful provider calls. The test completed successfully and did not expose provider secrets.
