package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.Done;
import akka.javasdk.testkit.EventSourcedTestKit;
import com.example.domain.EventSourcedTemplate;
import org.junit.jupiter.api.Test;

class EventSourcedEntityTemplateTest {

  @Test
  void createAndReadState() {
    var testKit = EventSourcedTestKit.of("template-1", EventSourcedEntityTemplate::new);

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
    var testKit = EventSourcedTestKit.of("template-1", EventSourcedEntityTemplate::new);

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
    var testKit = EventSourcedTestKit.of("template-1", EventSourcedEntityTemplate::new);

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
  void deletePersistsDeletedEventAndMarksEntityDeleted() {
    var testKit = EventSourcedTestKit.of("template-1", EventSourcedEntityTemplate::new);

    testKit.method(EventSourcedEntityTemplate::create)
        .invoke(new EventSourcedTemplate.Command.Create("initial-name"));

    var result = testKit.method(EventSourcedEntityTemplate::delete).invoke();

    assertEquals(Done.getInstance(), result.getReply());
    assertEquals(
        new EventSourcedTemplate.Event.Deleted(),
        result.getNextEventOfType(EventSourcedTemplate.Event.Deleted.class));
    assertTrue(testKit.isDeleted());
  }
}
