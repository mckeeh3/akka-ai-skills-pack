package com.example.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.NotUsed;
import akka.javasdk.DeferredCall;
import akka.javasdk.Metadata;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.client.ComponentDeferredMethodRef1;
import akka.javasdk.client.ComponentMethodRef;
import akka.javasdk.client.KeyValueEntityClient;
import akka.javasdk.client.TimedActionClient;
import akka.javasdk.testkit.TimedActionTestkit;
import com.example.domain.ReminderJob;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class ReminderJobTimedActionTest {

  @Test
  void sendReminderReturnsDoneForCompletedJobs() {
    var result =
        TimedActionTestkit.of(
                () ->
                    new ReminderJobTimedAction(
                        componentClientReturning(
                            new ReminderJob.Result(
                                ReminderJob.Outcome.INVALID,
                                "Reminder job is already completed.",
                                2,
                                false,
                                1))))
            .method(ReminderJobTimedAction::sendReminder)
            .invoke("job-1");

    assertTrue(result.isDone());
    assertFalse(result.isError());
  }

  @Test
  void sendReminderReturnsDoneForMissingJobs() {
    var result =
        TimedActionTestkit.of(
                () ->
                    new ReminderJobTimedAction(
                        componentClientReturning(
                            new ReminderJob.Result(
                                ReminderJob.Outcome.NOT_FOUND,
                                "Reminder job not found.",
                                0,
                                false,
                                0))))
            .method(ReminderJobTimedAction::sendReminder)
            .invoke("job-2");

    assertTrue(result.isDone());
    assertFalse(result.isError());
  }

  @SuppressWarnings("unchecked")
  private static ComponentClient componentClientReturning(ReminderJob.Result result) {
    var entityMethodRef =
        (ComponentMethodRef<ReminderJob.Result>)
            Proxy.newProxyInstance(
                ComponentMethodRef.class.getClassLoader(),
                new Class<?>[] {ComponentMethodRef.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
                      case "invoke" -> result;
                      case "invokeAsync" -> CompletableFuture.completedFuture(result);
                      case "withMetadata", "withRetry" -> proxy;
                      case "deferred" -> deferredCall();
                      case "toString" -> "ReminderJobEntityMethodRefProxy";
                      case "hashCode" -> System.identityHashCode(proxy);
                      case "equals" -> proxy == args[0];
                      default -> throw new UnsupportedOperationException(method.getName());
                    });

    var timedActionMethodRef =
        (ComponentDeferredMethodRef1<String, Object>)
            Proxy.newProxyInstance(
                ComponentDeferredMethodRef1.class.getClassLoader(),
                new Class<?>[] {ComponentDeferredMethodRef1.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
                      case "deferred" -> deferredCall((String) args[0]);
                      case "withMetadata" -> proxy;
                      case "toString" -> "ReminderJobTimedActionDeferredProxy";
                      case "hashCode" -> System.identityHashCode(proxy);
                      case "equals" -> proxy == args[0];
                      default -> throw new UnsupportedOperationException(method.getName());
                    });

    var entityClient =
        (KeyValueEntityClient)
            Proxy.newProxyInstance(
                KeyValueEntityClient.class.getClassLoader(),
                new Class<?>[] {KeyValueEntityClient.class},
                (proxy, method, args) -> {
                  if (method.getName().equals("method")) {
                    return entityMethodRef;
                  }
                  if (method.getName().equals("toString")) {
                    return "ReminderJobEntityClientProxy";
                  }
                  if (method.getName().equals("hashCode")) {
                    return System.identityHashCode(proxy);
                  }
                  if (method.getName().equals("equals")) {
                    return proxy == args[0];
                  }
                  throw new UnsupportedOperationException(method.getName());
                });

    var timedActionClient =
        (TimedActionClient)
            Proxy.newProxyInstance(
                TimedActionClient.class.getClassLoader(),
                new Class<?>[] {TimedActionClient.class},
                (proxy, method, args) -> {
                  if (method.getName().equals("method")) {
                    return timedActionMethodRef;
                  }
                  if (method.getName().equals("toString")) {
                    return "ReminderJobTimedActionClientProxy";
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
              if (method.getName().equals("forKeyValueEntity")) {
                return entityClient;
              }
              if (method.getName().equals("forTimedAction")) {
                return timedActionClient;
              }
              if (method.getName().equals("toString")) {
                return "ReminderJobComponentClientProxy";
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

  private static DeferredCall<NotUsed, ReminderJob.Result> deferredCall() {
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
      public DeferredCall<NotUsed, ReminderJob.Result> withMetadata(Metadata metadata) {
        return this;
      }
    };
  }

  private static DeferredCall<String, Object> deferredCall(String jobId) {
    return new DeferredCall<>() {
      @Override
      public String message() {
        return jobId;
      }

      @Override
      public Metadata metadata() {
        return Metadata.EMPTY;
      }

      @Override
      public DeferredCall<String, Object> withMetadata(Metadata metadata) {
        return this;
      }
    };
  }
}
