package com.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EventSourcedTemplateValidatorTest {

  @Test
  void validateCreateCommand() {
    var errors = EventSourcedTemplateValidator.validate(
        new EventSourcedTemplate.Command.Create(" "));

    assertEquals(1, errors.size());
    assertEquals("name must not be blank", errors.getFirst());
  }

  @Test
  void validateUpdateDetailsCommand() {
    var errors = EventSourcedTemplateValidator.validate(
        new EventSourcedTemplate.Command.UpdateDetails("", -1));

    assertEquals(2, errors.size());
    assertEquals("description must not be blank", errors.get(0));
    assertEquals("quantity must be zero or greater", errors.get(1));
  }
}
