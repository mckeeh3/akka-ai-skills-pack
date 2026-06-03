package com.example.domain.security;

import java.util.ArrayList;
import java.util.List;

/** Deterministic validation for tenant/customer directory commands. */
public final class TenantDirectoryValidator {

  private TenantDirectoryValidator() {}

  public static List<String> validate(TenantDirectory.Command.UpsertTenant command) {
    var errors = new ArrayList<String>();
    if (isBlank(command.name())) {
      errors.add("tenant name must not be blank");
    }
    if (command.at() == null) {
      errors.add("at must not be null");
    }
    return List.copyOf(errors);
  }

  public static List<String> validate(TenantDirectory.Command.DisableTenant command) {
    if (command.at() == null) {
      return List.of("at must not be null");
    }
    return List.of();
  }

  public static List<String> validate(TenantDirectory.Command.UpsertCustomer command) {
    var errors = new ArrayList<String>();
    if (isBlank(command.customerId())) {
      errors.add("customerId must not be blank");
    }
    if (isBlank(command.name())) {
      errors.add("customer name must not be blank");
    }
    if (command.at() == null) {
      errors.add("at must not be null");
    }
    return List.copyOf(errors);
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
