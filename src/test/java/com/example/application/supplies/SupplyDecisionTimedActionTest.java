package com.example.application.supplies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.DeferredCall;
import akka.javasdk.Metadata;
import akka.javasdk.client.AgentClient;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.client.ComponentMethodRef1;
import akka.javasdk.client.WorkflowClient;
import akka.javasdk.testkit.TimedActionTestkit;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class SupplyDecisionTimedActionTest {

  @Test
  void staleDecisionTimerDelegatesToWorkflowWithStableIdempotencyKey() {
    var captured = new AtomicReference<SupplyAutopilotWorkflow.EscalateStaleDecision>();
    var result =
        TimedActionTestkit.of(() -> new SupplyDecisionTimedAction(componentClientCapturing(captured)))
            .method(SupplyDecisionTimedAction::escalateStaleDecision)
            .invoke("timer-workflow-1");

    assertTrue(result.isDone());
    assertFalse(result.isError());
    assertEquals("idem-stale-timer-workflow-1", captured.get().idempotencyKey());
    assertEquals("supply-decision-timer", captured.get().actor());
    assertEquals("approval SLA elapsed", captured.get().rationale());
    assertEquals("supply-decision-stale-timer-workflow-1", SupplyDecisionTimedAction.timerName("timer-workflow-1"));
  }

  @SuppressWarnings("unchecked")
  private static ComponentClient componentClientCapturing(
      AtomicReference<SupplyAutopilotWorkflow.EscalateStaleDecision> captured) {
    var methodRef =
        (ComponentMethodRef1<SupplyAutopilotWorkflow.EscalateStaleDecision, SupplyAutopilotWorkflow.State>)
            Proxy.newProxyInstance(
                ComponentMethodRef1.class.getClassLoader(),
                new Class<?>[] {ComponentMethodRef1.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
                      case "invoke" -> {
                        captured.set((SupplyAutopilotWorkflow.EscalateStaleDecision) args[0]);
                        yield null;
                      }
                      case "invokeAsync" -> CompletableFuture.completedFuture(null);
                      case "withMetadata", "withRetry" -> proxy;
                      case "deferred" -> deferredCall((SupplyAutopilotWorkflow.EscalateStaleDecision) args[0]);
                      case "toString" -> "SupplyAutopilotWorkflowMethodRefProxy";
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
                    return "SupplyAutopilotWorkflowClientProxy";
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
                return "SupplyDecisionTimedActionComponentClientProxy";
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

  private static DeferredCall<SupplyAutopilotWorkflow.EscalateStaleDecision, SupplyAutopilotWorkflow.State>
      deferredCall(SupplyAutopilotWorkflow.EscalateStaleDecision command) {
    return new DeferredCall<>() {
      @Override
      public SupplyAutopilotWorkflow.EscalateStaleDecision message() {
        return command;
      }

      @Override
      public Metadata metadata() {
        return Metadata.EMPTY;
      }

      @Override
      public DeferredCall<SupplyAutopilotWorkflow.EscalateStaleDecision, SupplyAutopilotWorkflow.State> withMetadata(
          Metadata metadata) {
        return this;
      }
    };
  }
}
