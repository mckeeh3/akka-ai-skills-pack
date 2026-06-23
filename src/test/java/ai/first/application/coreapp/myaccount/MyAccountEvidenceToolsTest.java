package ai.first.application.coreapp.myaccount;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.foundation.attention.AttentionService;
import ai.first.application.foundation.attention.InMemoryTestAttentionRepository;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.InMemoryTestIdentityRepository;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.AuthContext;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import java.time.Clock;
import java.util.List;
import org.junit.jupiter.api.Test;

class MyAccountEvidenceToolsTest {
  @Test
  void readSupportsSaasOwnerMembershipWithNullTenantId() {
    var identityRepository = new InMemoryTestIdentityRepository();
    identityRepository.saveAccount(new Account("owner@example.test", "workos-owner", "owner@example.test", "owner@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.saveProfile(new UserProfile("owner@example.test", "owner@example.test", "SaaS Owner", "SaaS", "Owner", null));
    identityRepository.saveSettings(new UserSettings("owner@example.test", UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.saveMembership(new Membership("membership-owner", "owner@example.test", ScopeType.SAAS_OWNER, null, null, List.of(FoundationRole.SAAS_OWNER_ADMIN), MembershipStatus.ACTIVE, false, null));

    var authContext = new AuthContext(
        "owner@example.test",
        "workos-owner",
        "membership-owner",
        ScopeType.SAAS_OWNER,
        null,
        null,
        List.of(FoundationRole.SAAS_OWNER_ADMIN),
        List.of(
            "my_account.view_summary",
            "my_account.view_context",
            "my_account.list_personal_attention",
            "my_account.open_authorized_workstream",
            "my_account.view_own_trace_refs"));
    var resolver = new AuthContextResolver(identityRepository);
    var attentionService = new AttentionService(new InMemoryTestAttentionRepository(), resolver, Clock.systemUTC());
    var tools = new MyAccountEvidenceTools(identityRepository, new MyAccountService(resolver, attentionService), authContext, "corr-owner-evidence");

    var evidence = assertDoesNotThrow(() -> tools.read("selected context summary"));

    assertTrue(evidence.contains("tool_id=myAccountEvidence.read"));
    assertTrue(evidence.contains("selectedTenantId=null"));
    assertTrue(evidence.contains("selectedContextId=membership-owner"));
  }
}
