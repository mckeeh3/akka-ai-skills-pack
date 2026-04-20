package com.example.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.NotUsed;
import akka.javasdk.DeferredCall;
import akka.javasdk.Metadata;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.client.ComponentMethodRef;
import akka.javasdk.client.TimedActionClient;
import akka.javasdk.client.ViewClient;
import akka.javasdk.client.WorkflowClient;
import akka.javasdk.client.KeyValueEntityClient;
import akka.javasdk.client.EventSourcedEntityClient;
import akka.javasdk.client.AgentClient;
import akka.javasdk.testkit.TimedActionTestkit;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class ApprovalDeadlineTimedActionTest {

  @Test
  void timeoutApprovalReturnsDoneAfterCallingWorkflowTimeoutCommand() {
    var result =
        TimedActionTestkit.of(
                () -> new ApprovalDeadlineTimedAction(componentClientReturning("approval timed out")))
            .method(ApprovalDeadlineTimedAction::timeoutApproval)
            .invoke("approval-1");

    assertTrue(result.isDone());
    assertFalse(result.isError());
  }

  @SuppressWarnings("unchecked")
  private static ComponentClient componentClientReturning(String response) {
    var methodRef =
        (ComponentMethodRef<String>)
            Proxy.newProxyInstance(
                ComponentMethodRef.class.getClassLoader(),
                new Class<?>[] {ComponentMethodRef.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
                      case "invoke" -> response;
                      case "invokeAsync" -> CompletableFuture.completedFuture(response);
                      case "withMetadata", "withRetry" -> proxy;
                      case "deferred" -> deferredCall();
                      case "toString" -> "ApprovalDeadlineWorkflowMethodRefProxy";
                      case "hashCode" -> System.identityHashCode(proxy);
                      case "equals" -> proxy == args[0];
                      default -> throw new UnsupportedOperationException(method.getName());
                    });

    var workflowClient =
        (WorkflowClient)
            Proxy.newProxyInstance(
                WorkflowClient.class.getClassLoader(),
                new Class<?>[] {WorkflowClient.class},
                (proxy, method, args) -> {
                  if (method.getName().equals("method")) {
                    return methodRef;
                  }
                  if (method.getName().equals("toString")) {
                    return "ApprovalDeadlineWorkflowClientProxy";
                  }
                  if (method.getName().equals("hashCode")) {
                    return System.identityHashCode(proxy);
                  }
                  if (method.getName().equals("equals")) {
                    return proxy == args[0];
                  }
                  throw new UnsupportedOperationException(method.getName());
                });

    return (ComponentClient)
        Proxy.newProxyInstance(
            ComponentClient.class.getClassLoader(),
            new Class<?>[] {ComponentClient.class},
            (proxy, method, args) -> {
              if (method.getName().equals("forWorkflow")) {
                return workflowClient;
              }
              if (method.getName().equals("toString")) {
                return "ApprovalDeadlineComponentClientProxy";
              }
              if (method.getName().equals("hashCode")) {
                return System.identityHashCode(proxy);
              }
              if (method.getName().equals("equals")) {
                return proxy == args[0];
              }
              throw new UnsupportedOperationException(method.getName());
            });
  }

  private static DeferredCall<NotUsed, String> deferredCall() {
    return new DeferredCall<>() {
      @Override
      public NotUsed message() {
        return NotUsed.getInstance();
      }

      @Override
      public Metadata metadata() {
        return Metadata.EMPTY;
      }

      @Override
      public DeferredCall<NotUsed, String> withMetadata(Metadata metadata) {
        return this;
      }
    };
  }
}
