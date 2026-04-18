package com.example.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Optional validation helper for edge-facing callers.
 *
 * <p>Flow-internal consumers typically send already-normalized commands and may not need this class.
 * Endpoints or other external ingress points can use it before calling the entity.
 */
public final class EventSourcedTemplateValidator {

  private EventSourcedTemplateValidator() {}

  public static List<String> validate(EventSourcedTemplate.Command.Create command) {
    var errors = new ArrayList<String>();

    if (isBlank(command.name())) {
      errors.add("name must not be blank");
    }

    return List.copyOf(errors);
  }

  public static List<String> validate(EventSourcedTemplate.Command.Rename command) {
    var errors = new ArrayList<String>();

    if (isBlank(command.name())) {
      errors.add("name must not be blank");
    }

    return List.copyOf(errors);
  }

  public static List<String> validate(EventSourcedTemplate.Command.UpdateDetails command) {
    var errors = new ArrayList<String>();

    if (isBlank(command.description())) {
      errors.add("description must not be blank");
    }

    if (command.quantity() < 0) {
      errors.add("quantity must be zero or greater");
    }

    return List.copyOf(errors);
  }

  public static List<String> validate(EventSourcedTemplate.Command.Delete command) {
    return List.of();
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
