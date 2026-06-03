# Workflow endpoint pattern

Small, agent-oriented reference for HTTP endpoints that start, query, resume, and optionally stream workflow progress.

Primary official semantics:
- `akka-context/sdk/workflows.html.md`
- `akka-context/sdk/http-endpoints.html.md`

Local executable examples:
- `src/main/java/com/example/api/TransferWorkflowEndpoint.java`
- `src/main/java/com/example/api/ApprovalWorkflowEndpoint.java`
- `src/test/java/com/example/application/TransferWorkflowEndpointIntegrationTest.java`
- `src/test/java/com/example/application/ApprovalWorkflowEndpointIntegrationTest.java`

## Use this pattern when

- an HTTP route starts a workflow with `componentClient.forWorkflow(workflowId)`
- an HTTP route reads current workflow state through a `get()` command
- a paused workflow exposes a resume command like `approve(...)`
- workflow notifications should be exposed as SSE

## Minimal structure

```java
@HttpEndpoint("/transfers")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class TransferWorkflowEndpoint {

  private final ComponentClient componentClient;

  public TransferWorkflowEndpoint(ComponentClient componentClient) {
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

## Routing shape

Prefer this route split:
- `POST /{workflowId}` → start workflow
- `GET /{workflowId}` → read current state
- `POST /{workflowId}/{action}` → resume or mutate paused workflow
- `GET /{workflowId}/updates` → SSE notifications when available

Repository examples:
- `TransferWorkflowEndpoint`
  - start + get + updates
- `ApprovalWorkflowEndpoint`
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

Repository examples:
- `TransferWorkflowEndpoint#updates`
- `ApprovalWorkflowEndpoint#updates`

## Testing reminders

Prefer route-level tests with `httpClient`:
- start route returns expected status and body
- get route reflects eventual workflow completion with `Awaitility` when needed
- resume route rejects invalid workflow state with HTTP 400
- missing workflow read maps to HTTP 404
- SSE route uses `testKit.getSelfSseRouteTester()`

Repository examples:
- `TransferWorkflowEndpointIntegrationTest`
- `ApprovalWorkflowEndpointIntegrationTest`
