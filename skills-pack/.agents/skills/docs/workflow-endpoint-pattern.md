# Workflow endpoint pattern

Small, agent-oriented reference for HTTP endpoints that start, query, resume, and optionally stream workflow progress.

Primary official semantics:
- `akka-context/sdk/workflows.html.md`
- `akka-context/sdk/http-endpoints.html.md`

Local executable examples: none in the current curated SaaS Foundation App examples. Treat the code below as a minimal domain-specific mechanics pattern, not a class name to look up and not a complete generated-SaaS security contract.

## Use this pattern when

- an HTTP route starts a workflow with `componentClient.forWorkflow(workflowId)`
- an HTTP route reads current workflow state through a `get()` command
- a paused workflow exposes a resume command like `approve(...)`
- workflow notifications should be exposed as SSE

## Minimal structure

```java
@HttpEndpoint("/domain-workflows")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class DomainWorkflowEndpoint {

  private final ComponentClient componentClient;

  public DomainWorkflowEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post("/{transferId}")
  public HttpResponse start(String transferId, StartRequest request) {
    try {
      var state = componentClient
          .forWorkflow(transferId)
          .method(MyWorkflow::start)
          .invoke(toWorkflowRequest(request));
      return HttpResponses.created(toApi(transferId, state));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Get("/{transferId}")
  public HttpResponse get(String transferId) {
    try {
      var state = componentClient.forWorkflow(transferId).method(MyWorkflow::get).invoke();
      return HttpResponses.ok(toApi(transferId, state));
    } catch (CommandException error) {
      return HttpResponses.notFound(error.getMessage());
    }
  }
}
```

## Security and authority boundary

For generated secure AI-first SaaS apps, a workflow endpoint is an exposure adapter for a governed capability; route availability and `@Acl` shape are not authorization. Before starting, reading, resuming, or streaming a workflow with protected data, resolve the signed-in account or service identity, selected `AuthContext`, tenant/customer scope, role/capability grants, idempotency/correlation, and audit/work trace obligations in backend code. Map endpoint actions to the responsible worker, actor adapter (`surface_action`, `api`, `workflow_step`, or another declared adapter), governed-tool id, and capability id. Missing identity, scope, provider/security configuration, or capability authorization should fail closed with safe HTTP/system-message responses and trace evidence.

## Routing shape

Prefer this route split:
- `POST /{workflowId}` → start workflow
- `GET /{workflowId}` → read current state
- `POST /{workflowId}/{action}` → resume or mutate paused workflow
- `GET /{workflowId}/updates` → SSE notifications when available

Repository examples:
- a domain-specific workflow endpoint
  - start + get + updates
- a domain-specific approval workflow endpoint
  - start + approve + get + updates

## Mapping rules

Keep three mappings explicit:
1. endpoint request record -> workflow command record
2. workflow state -> endpoint response record
3. workflow notification -> SSE response record

Do not return workflow-internal state or notification types directly unless that is explicitly intended.

## Error mapping rules

Use HTTP-oriented mapping at the edge:
- workflow start/resume validation failure -> `HttpResponses.badRequest(...)`
- read of missing/not-started workflow -> `HttpResponses.notFound(...)`
- successful start -> `HttpResponses.created(...)`
- successful get/resume -> `HttpResponses.ok(...)`

## SSE pattern

When the workflow exposes `updates()`:

```java
@Get("/{workflowId}/updates")
public HttpResponse updates(String workflowId) {
  var source = componentClient
      .forWorkflow(workflowId)
      .notificationStream(MyWorkflow::updates)
      .source()
      .map(MyEndpoint::toApi);
  return HttpResponses.serverSentEvents(source);
}
```

Pattern examples:
- domain-specific workflow endpoint `updates` method
- domain-specific approval workflow endpoint `updates` method

## Testing reminders

Prefer route-level tests with `httpClient` that prove the endpoint mechanics and the governed capability boundary:
- start route returns expected status and body
- get route reflects eventual workflow completion with `Awaitility` when needed
- resume route rejects invalid workflow state with HTTP 400
- missing workflow read maps to HTTP 404
- SSE route uses `testKit.getSelfSseRouteTester()`
- protected routes reject missing/wrong tenant or capability without starting/resuming the workflow
- side-effecting resume/start routes preserve idempotency, correlation, and audit/work trace expectations

Repository examples:
- a domain-specific workflow endpoint integration test
- a domain-specific approval workflow endpoint integration test
