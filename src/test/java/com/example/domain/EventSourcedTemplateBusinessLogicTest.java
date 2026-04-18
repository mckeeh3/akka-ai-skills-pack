package com.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class EventSourcedTemplateBusinessLogicTest {

  @Test
  void decideUpdateDetailsNormalizesInputBeforeCreatingEvents() {
    var state = new EventSourcedTemplate.State("template-1", "name", "old description", 1, true);
    var command = new EventSourcedTemplate.Command.UpdateDetails("  new   description  ", -5);

    var decision = EventSourcedTemplateBusinessLogic.decideUpdateDetails(state, command);

    assertEquals(
        List.of(
            new EventSourcedTemplate.Event.DescriptionUpdated("new description"),
            new EventSourcedTemplate.Event.QuantityUpdated(0)),
        decision.events());
  }
}
