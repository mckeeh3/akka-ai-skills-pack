package com.example.application;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import akka.javasdk.keyvalueentity.KeyValueEntityContext;
import com.example.domain.AccountStatus;
import com.example.domain.RoleAssignment;
import com.example.domain.UserAccount;
import com.example.domain.UserProfile;
import java.util.List;

@Component(id = "user-account")
public class UserAccountEntity extends KeyValueEntity<UserAccount> {
  private final String entityId;

  public UserAccountEntity(KeyValueEntityContext context) {
    this.entityId = context.entityId();
  }

  @Override
  public UserAccount emptyState() {
    return UserAccount.empty(entityId);
  }

  public record InviteUser(String email, UserProfile profile, List<RoleAssignment> roles) {}
  public record ActivateUser(String workosUserId) {}

  public ReadOnlyEffect<UserAccount> get() {
    return effects().reply(currentState());
  }

  public Effect<UserAccount> invite(InviteUser command) {
    if (currentState().exists()) {
      return effects().error("User already exists: " + entityId);
    }
    var newState = currentState().invited(command.email(), command.profile(), command.roles() == null ? List.of() : command.roles());
    return effects().updateState(newState).thenReply(newState);
  }

  public Effect<UserAccount> activate(ActivateUser command) {
    if (!currentState().exists()) {
      return effects().error("User does not exist: " + entityId);
    }
    if (currentState().status() == AccountStatus.DISABLED) {
      return effects().error("User is disabled: " + entityId);
    }
    var newState = currentState().activate(command.workosUserId());
    return effects().updateState(newState).thenReply(newState);
  }

  public Effect<UserAccount> updateProfile(UserProfile profile) {
    if (!currentState().exists()) {
      return effects().error("User does not exist: " + entityId);
    }
    var newState = currentState().withProfile(profile);
    return effects().updateState(newState).thenReply(newState);
  }

  public Effect<UserAccount> updateRoles(List<RoleAssignment> roles) {
    if (!currentState().exists()) {
      return effects().error("User does not exist: " + entityId);
    }
    var newState = currentState().withRoles(roles == null ? List.of() : roles);
    return effects().updateState(newState).thenReply(newState);
  }

  public Effect<UserAccount> disable() {
    if (!currentState().exists()) {
      return effects().error("User does not exist: " + entityId);
    }
    var newState = currentState().disabled();
    return effects().updateState(newState).thenReply(newState);
  }

  public Effect<Done> deleteAccount() {
    return effects().deleteEntity().thenReply(Done.getInstance());
  }
}
