package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.Done;
import akka.javasdk.NotificationPublisher;
import akka.javasdk.testkit.EventSourcedTestKit;
import com.example.domain.EventSourcedTemplate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Event Sourced Entity using {@link EventSourcedTestKit}.
 *
 * <p>The test kit constructor lambda wires a stub {@link NotificationPublisher} so that published
 * notifications can be inspected in tests.
 */

class EventSourcedEntityTemplateTest {

  /** Collects published notifications for assertion. */
  private final List<EventSourcedTemplate.Event> publishedNotifications = new ArrayList<>();

  private final NotificationPublisher<EventSourcedTemplate.Event> stubPublisher =
      publishedNotifications::add;

  private EventSourcedTestKit<
          EventSourcedTemplate.State,
          EventSourcedTemplate.Event,
          EventSourcedEntityTemplate>
      newTestKit(String entityId) {
    publishedNotifications.clear();
    return EventSourcedTestKit.of(
        entityId, ctx -> new EventSourcedEntityTemplate(ctx, stubPublisher));
  }

  @Test
  void createAndReadState() {
    var testKit = newTestKit("template-1");

    var result = testKit.method(EventSourcedEntityTemplate::create)
        .invoke(new EventSourcedTemplate.Command.Create("initial-name"));

    assertEquals(Done.getInstance(), result.getReply());
    assertEquals(
        new EventSourcedTemplate.Event.Created("template-1", "initial-name"),
        result.getNextEventOfType(EventSourcedTemplate.Event.Created.class));

    var getResult = testKit.method(EventSourcedEntityTemplate::get).invoke();
    assertEquals(
        new EventSourcedTemplate.State("template-1", "initial-name", "", 0, true),
        getResult.getReply());
  }

  @Test
  void duplicateCreateIsANoOp() {
    var testKit = newTestKit("template-1");

    testKit.method(EventSourcedEntityTemplate::create)
        .invoke(new EventSourcedTemplate.Command.Create("initial-name"));

    var duplicate = testKit.method(EventSourcedEntityTemplate::create)
        .invoke(new EventSourcedTemplate.Command.Create("initial-name"));

    assertEquals(Done.getInstance(), duplicate.getReply());
    assertFalse(duplicate.didPersistEvents());
    assertEquals(1, testKit.getAllEvents().size());
  }

  @Test
  void updateDetailsUsesBusinessLogicAndCanPersistMultipleEvents() {
    var testKit = newTestKit("template-1");

    testKit.method(EventSourcedEntityTemplate::create)
        .invoke(new EventSourcedTemplate.Command.Create("initial-name"));

    var result = testKit.method(EventSourcedEntityTemplate::updateDetails)
        .invoke(new EventSourcedTemplate.Command.UpdateDetails("  new-description   ", 42));

    assertEquals(Done.getInstance(), result.getReply());
    assertEquals(
        new EventSourcedTemplate.Event.DescriptionUpdated("new-description"),
        result.getNextEventOfType(EventSourcedTemplate.Event.DescriptionUpdated.class));
    assertEquals(
        new EventSourcedTemplate.Event.QuantityUpdated(42),
        result.getNextEventOfType(EventSourcedTemplate.Event.QuantityUpdated.class));

    assertEquals(
        new EventSourcedTemplate.State("template-1", "initial-name", "new-description", 42, true),
        testKit.getState());
  }

  @Test
  void renamePersistsRenamedEvent() {
    var testKit = newTestKit("template-1");

    testKit.method(EventSourcedEntityTemplate::create)
        .invoke(new EventSourcedTemplate.Command.Create("initial-name"));

    var result = testKit.method(EventSourcedEntityTemplate::rename)
        .invoke(new EventSourcedTemplate.Command.Rename("new-name"));

    assertEquals(Done.getInstance(), result.getReply());
    assertEquals(
        new EventSourcedTemplate.Event.Renamed("new-name"),
        result.getNextEventOfType(EventSourcedTemplate.Event.Renamed.class));

    assertEquals("new-name", testKit.getState().name());
  }

  @Test
  void renameToSameNameIsANoOp() {
    var testKit = newTestKit("template-1");

    testKit.method(EventSourcedEntityTemplate::create)
        .invoke(new EventSourcedTemplate.Command.Create("same-name"));

    var result = testKit.method(EventSourcedEntityTemplate::rename)
        .invoke(new EventSourcedTemplate.Command.Rename("same-name"));

    assertEquals(Done.getInstance(), result.getReply());
    assertFalse(result.didPersistEvents());
  }

  @Test
  void deletePersistsDeletedEventAndMarksEntityDeleted() {
    var testKit = newTestKit("template-1");

    testKit.method(EventSourcedEntityTemplate::create)
        .invoke(new EventSourcedTemplate.Command.Create("initial-name"));

    var result = testKit.method(EventSourcedEntityTemplate::delete).invoke();

    assertEquals(Done.getInstance(), result.getReply());
    assertEquals(
        new EventSourcedTemplate.Event.Deleted(),
        result.getNextEventOfType(EventSourcedTemplate.Event.Deleted.class));
    assertTrue(testKit.isDeleted());
  }

  @Test
  void deleteOnNonExistentEntityIsANoOp() {
    var testKit = newTestKit("template-1");

    var result = testKit.method(EventSourcedEntityTemplate::delete).invoke();

    assertEquals(Done.getInstance(), result.getReply());
    assertFalse(result.didPersistEvents());
  }

  @Test
  void getReturnsCurrentState() {
    var testKit = newTestKit("template-1");

    // Empty state before creation
    var emptyResult = testKit.method(EventSourcedEntityTemplate::get).invoke();
    assertFalse(emptyResult.getReply().exists());

    // After creation
    testKit.method(EventSourcedEntityTemplate::create)
        .invoke(new EventSourcedTemplate.Command.Create("my-entity"));

    var result = testKit.method(EventSourcedEntityTemplate::get).invoke();
    var state = result.getReply();
    assertTrue(state.exists());
    assertEquals("my-entity", state.name());
    assertEquals("", state.description());
    assertEquals(0, state.quantity());
  }

  @Test
  void getConsistentReturnsCurrentState() {
    var testKit = newTestKit("template-1");

    testKit.method(EventSourcedEntityTemplate::create)
        .invoke(new EventSourcedTemplate.Command.Create("consistent-entity"));

    var result = testKit.method(EventSourcedEntityTemplate::getConsistent).invoke();

    assertEquals("consistent-entity", result.getReply().name());
    assertTrue(result.getReply().exists());
  }

  @Test
  void includeRegionRepliesDoneWithoutPersistingEvents() {
    var testKit = newTestKit("template-1");

    var result = testKit.method(EventSourcedEntityTemplate::includeRegion)
        .invoke(new EventSourcedTemplate.Command.IncludeRegion("aws-us-east-2"));

    assertEquals(Done.getInstance(), result.getReply());
    assertFalse(result.didPersistEvents());
  }
}
