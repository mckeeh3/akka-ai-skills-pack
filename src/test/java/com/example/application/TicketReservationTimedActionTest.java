package com.example.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.NotUsed;
import akka.javasdk.DeferredCall;
import akka.javasdk.Metadata;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.client.ComponentMethodRef;
import akka.javasdk.client.KeyValueEntityClient;
import akka.javasdk.testkit.TimedActionTestkit;
import com.example.domain.TicketReservation;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class TicketReservationTimedActionTest {

  @Test
  void expireReservationReturnsDoneForNotFoundReplies() {
    var testKit =
        TimedActionTestkit.of(
            () ->
                new TicketReservationTimedAction(
                    componentClientReturning(
                        new TicketReservation.Result(
                            TicketReservation.Outcome.NOT_FOUND, "Reservation not found."))));

    var result = testKit.method(TicketReservationTimedAction::expireReservation).invoke("reservation-1");

    assertTrue(result.isDone());
    assertFalse(result.isError());
  }

  @Test
  void expireReservationReturnsDoneForInvalidReplies() {
    var testKit =
        TimedActionTestkit.of(
            () ->
                new TicketReservationTimedAction(
                    componentClientReturning(
                        new TicketReservation.Result(
                            TicketReservation.Outcome.INVALID,
                            "Confirmed reservations must not be expired."))));

    var result = testKit.method(TicketReservationTimedAction::expireReservation).invoke("reservation-2");

    assertTrue(result.isDone());
    assertFalse(result.isError());
  }

  @SuppressWarnings("unchecked")
  private static ComponentClient componentClientReturning(TicketReservation.Result result) {
    var methodRef =
        (ComponentMethodRef<TicketReservation.Result>)
            Proxy.newProxyInstance(
                ComponentMethodRef.class.getClassLoader(),
                new Class<?>[] {ComponentMethodRef.class},
                (proxy, method, args) -> {
                  return switch (method.getName()) {
                    case "invoke" -> result;
                    case "invokeAsync" -> CompletableFuture.completedFuture(result);
                    case "withMetadata", "withRetry" -> proxy;
                    case "deferred" -> deferredCall();
                    case "toString" -> "ComponentMethodRefProxy";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException(method.getName());
                  };
                });

    var entityClient =
        (KeyValueEntityClient)
            Proxy.newProxyInstance(
                KeyValueEntityClient.class.getClassLoader(),
                new Class<?>[] {KeyValueEntityClient.class},
                (proxy, method, args) -> {
                  if (method.getName().equals("method")) {
                    return methodRef;
                  }
                  if (method.getName().equals("toString")) {
                    return "KeyValueEntityClientProxy";
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
              if (method.getName().equals("toString")) {
                return "ComponentClientProxy";
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

  private static DeferredCall<NotUsed, TicketReservation.Result> deferredCall() {
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
      public DeferredCall<NotUsed, TicketReservation.Result> withMetadata(Metadata metadata) {
        return this;
      }
    };
  }
}
