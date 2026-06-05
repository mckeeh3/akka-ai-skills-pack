package ai.first.application.security;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import akka.javasdk.keyvalueentity.KeyValueEntityContext;
import ai.first.domain.security.LocalAccount;
import ai.first.domain.security.LocalAccountCommandHandler;
import ai.first.domain.security.LocalAccountValidator;

/**
 * Key value entity for local account current state.
 *
 * <p>WorkOS authenticates browser users, but this Akka-owned state is the authorization source for
 * future backend APIs. Idempotent duplicate commands return {@code Done} without replacing state.
 */
@Component(id = "security-local-account")
public class LocalAccountEntity extends KeyValueEntity<LocalAccount.State> {

  private final String userId;

  public LocalAccountEntity(KeyValueEntityContext context) {
    this.userId = context.entityId();
  }

  @Override
  public LocalAccount.State emptyState() {
    return LocalAccount.State.empty(userId);
  }

  public ReadOnlyEffect<LocalAccount.State> get() {
    return effects().reply(currentState());
  }

  public Effect<Done> invite(LocalAccount.Command.Invite command) {
    var errors = LocalAccountValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    try {
      var updatedState = LocalAccountCommandHandler.onCommand(currentState(), command);
      if (updatedState.isEmpty()) {
        return effects().reply(Done.getInstance());
      }
      return effects().updateState(updatedState.get()).thenReply(Done.getInstance());
    } catch (IllegalStateException ex) {
      return effects().error(ex.getMessage());
    }
  }

  public Effect<Done> linkAndActivate(LocalAccount.Command.LinkAndActivate command) {
    var errors = LocalAccountValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    try {
      var updatedState = LocalAccountCommandHandler.onCommand(currentState(), command);
      if (updatedState.isEmpty()) {
        return effects().reply(Done.getInstance());
      }
      return effects().updateState(updatedState.get()).thenReply(Done.getInstance());
    } catch (IllegalStateException ex) {
      return effects().error(ex.getMessage());
    }
  }

  public Effect<Done> replaceRoles(LocalAccount.Command.ReplaceRoles command) {
    var errors = LocalAccountValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    try {
      var updatedState = LocalAccountCommandHandler.onCommand(currentState(), command);
      if (updatedState.isEmpty()) {
        return effects().reply(Done.getInstance());
      }
      return effects().updateState(updatedState.get()).thenReply(Done.getInstance());
    } catch (IllegalStateException ex) {
      return effects().error(ex.getMessage());
    }
  }

  public Effect<Done> disable(LocalAccount.Command.Disable command) {
    var errors = LocalAccountValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    try {
      var updatedState = LocalAccountCommandHandler.onCommand(currentState(), command);
      if (updatedState.isEmpty()) {
        return effects().reply(Done.getInstance());
      }
      return effects().updateState(updatedState.get()).thenReply(Done.getInstance());
    } catch (IllegalStateException ex) {
      return effects().error(ex.getMessage());
    }
  }

  public Effect<Done> reactivate(LocalAccount.Command.Reactivate command) {
    var errors = LocalAccountValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    try {
      var updatedState = LocalAccountCommandHandler.onCommand(currentState(), command);
      if (updatedState.isEmpty()) {
        return effects().reply(Done.getInstance());
      }
      return effects().updateState(updatedState.get()).thenReply(Done.getInstance());
    } catch (IllegalStateException ex) {
      return effects().error(ex.getMessage());
    }
  }
}
