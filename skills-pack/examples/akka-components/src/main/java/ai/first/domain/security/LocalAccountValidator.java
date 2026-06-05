package ai.first.domain.security;

import java.util.ArrayList;
import java.util.List;

/** Deterministic validation for local account commands. */
public final class LocalAccountValidator {

  private LocalAccountValidator() {}

  public static List<String> validate(LocalAccount.Command.Invite command) {
    var errors = new ArrayList<String>();
    if (isBlank(command.email())) {
      errors.add("email must not be blank");
    }
    validateRoles(command.roles(), errors);
    if (command.at() == null) {
      errors.add("at must not be null");
    }
    return List.copyOf(errors);
  }

  public static List<String> validate(LocalAccount.Command.LinkAndActivate command) {
    var errors = new ArrayList<String>();
    if (isBlank(command.workosUserId())) {
      errors.add("workosUserId must not be blank");
    }
    if (command.at() == null) {
      errors.add("at must not be null");
    }
    return List.copyOf(errors);
  }

  public static List<String> validate(LocalAccount.Command.ReplaceRoles command) {
    var errors = new ArrayList<String>();
    validateRoles(command.roles(), errors);
    if (command.at() == null) {
      errors.add("at must not be null");
    }
    return List.copyOf(errors);
  }

  public static List<String> validate(LocalAccount.Command.Disable command) {
    if (command.at() == null) {
      return List.of("at must not be null");
    }
    return List.of();
  }

  public static List<String> validate(LocalAccount.Command.Reactivate command) {
    if (command.at() == null) {
      return List.of("at must not be null");
    }
    return List.of();
  }

  private static void validateRoles(List<RoleAssignment> roles, List<String> errors) {
    if (roles == null) {
      return;
    }
    for (var role : roles) {
      if (role.role() == null) {
        errors.add("role must not be null");
      } else if (role.role() != SecurityRole.APP_ADMIN && isBlank(role.tenantId())) {
        errors.add(role.role() + " tenantId must not be blank");
      } else if ((role.role() == SecurityRole.CUSTOMER_ADMIN || role.role() == SecurityRole.USER)
          && isBlank(role.customerId())) {
        errors.add(role.role() + " customerId must not be blank");
      }
    }
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
