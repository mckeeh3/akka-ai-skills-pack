package ai.first.application.coreapp.myaccount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.coreapp.myaccount.DigestExportRequest;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import ai.first.domain.foundation.identity.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.LocalDemoIdentityRepository;
import ai.first.application.foundation.notification.LocalDemoNotificationRepository;

class DigestExportServiceTest {
  private LocalDemoIdentityRepository identityRepository;
  private LocalDemoNotificationRepository repository;
  private AuthContextResolver resolver;
  private DigestExportService service;

  @BeforeEach
  void setUp() {
    identityRepository = new LocalDemoIdentityRepository();
    repository = new LocalDemoNotificationRepository();
    resolver = new AuthContextResolver(identityRepository);
    service = new DigestExportService(repository, resolver, Clock.fixed(Instant.parse("2026-05-26T10:00:00Z"), ZoneOffset.UTC));
    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    identityRepository.putTenant(new Tenant("tenant-2", "Tenant Two", true));
    addAccount("admin@example.test", "membership-admin", "tenant-1", List.of(FoundationRole.TENANT_ADMIN));
    addAccount("member@example.test", "membership-member", "tenant-1", List.of());
    addAccount("other@example.test", "membership-other", "tenant-2", List.of(FoundationRole.TENANT_ADMIN));
  }

  @Test
  void manualDigestIsIdempotentRedactedAuditedAndDoesNotMutateSources() {
    var actor = actor("admin@example.test", "membership-admin", "corr-digest");
    var first = service.startManualDigest(actor, new DigestExportService.DigestCommand("idem-digest", null, "strict", "attention token=secret"), "corr-digest");
    var duplicate = service.startManualDigest(actor, new DigestExportService.DigestCommand("idem-digest", null, "strict", "ignored"), "corr-digest-dup");

    assertEquals(first.requestId(), duplicate.requestId());
    assertEquals(DigestExportRequest.Status.READY, first.status());
    assertEquals(DigestExportRequest.RedactionProfile.STRICT, first.redactionProfile());
    assertFalse(first.toString().contains("token=secret"));
    assertTrue(first.safeSummary().contains("source attention and audit records are unchanged"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals(DigestExportService.START_MANUAL_DIGEST_CAPABILITY) && event.reasonCode().contains("manual-digest-ready")));
  }

  @Test
  void scheduledDigestRunsOnlyWhenDueThroughTimerSafeBackendCapability() {
    var actor = actor("admin@example.test", "membership-admin", "corr-schedule");
    var scheduled = service.scheduleDigest(actor, new DigestExportService.DigestCommand("idem-scheduled", Instant.parse("2026-05-26T11:00:00Z"), "audit_safe", "audit"), "corr-schedule");

    assertTrue(service.runDueScheduledDigests("tenant-1", Instant.parse("2026-05-26T10:30:00Z"), "corr-timer-early").isEmpty());
    var due = service.runDueScheduledDigests("tenant-1", Instant.parse("2026-05-26T11:00:00Z"), "corr-timer-due");

    assertEquals(1, due.size());
    assertEquals(scheduled.requestId(), due.get(0).requestId());
    assertEquals(DigestExportRequest.Status.READY, due.get(0).status());
    assertTrue(due.get(0).resultUri().startsWith("local://digest/"));
  }

  @Test
  void sensitiveExportPausesForHumanApprovalBeforeResultRelease() {
    var actor = actor("admin@example.test", "membership-admin", "corr-export");
    var requested = service.requestExport(actor, new DigestExportService.ExportCommand("idem-export", "standard", "json", true, "audit api_key=secret"), "corr-export");

    assertEquals(DigestExportRequest.Status.PENDING_APPROVAL, requested.status());
    assertEquals(null, requested.resultUri());
    assertFalse(requested.toString().contains("api_key=secret"));

    var approved = service.approveExport(actor, requested.requestId(), "Approved for tenant admin review", "corr-approve");

    assertEquals(DigestExportRequest.Status.READY, approved.status());
    assertTrue(approved.resultUri().startsWith("local://export/"));
    assertTrue(approved.safeSummary().contains("compliance-suite/legal-hold behavior is not implied"));
  }

  @Test
  void strictExportCanBeReadyLocallyAndCrossTenantReadsAreDeniedWithoutLeaks() {
    var admin = actor("admin@example.test", "membership-admin", "corr-export");
    var other = actor("other@example.test", "membership-other", "corr-other");
    var request = service.requestExport(admin, new DigestExportService.ExportCommand("idem-strict", "strict", "csv", false, "notifications"), "corr-export");

    assertEquals(DigestExportRequest.Status.READY, request.status());
    assertEquals(DigestExportRequest.ExportFormat.CSV, request.exportFormat());
    assertThrows(AuthorizationException.class, () -> service.read(other, request.requestId(), "corr-cross"));
  }

  @Test
  void legalHoldRequiresApprovalAndStoresOnlyRedactedScopedMarker() {
    var actor = actor("admin@example.test", "membership-admin", "corr-legal-hold");
    var hold = service.requestLegalHold(actor, new DigestExportService.LegalHoldCommand("idem-legal", Instant.parse("2026-06-26T10:00:00Z"), "trace token=secret", "pending litigation"), "corr-legal-hold");

    assertEquals(DigestExportRequest.RequestType.LEGAL_HOLD, hold.requestType());
    assertEquals(DigestExportRequest.Status.PENDING_APPROVAL, hold.status());
    assertEquals(null, hold.resultUri());
    assertFalse(hold.toString().contains("token=secret"));

    var approved = service.approveExport(actor, hold.requestId(), "Approved legal hold", "corr-legal-approve");

    assertEquals(DigestExportRequest.Status.READY, approved.status());
    assertTrue(approved.resultUri().startsWith("local://legal-hold/"));
    assertTrue(approved.safeSummary().contains("without deleting, mutating, or widening retention"));
  }

  @Test
  void ediscoverySiemAndComplianceFoundationsDoNotClaimVendorCertificationOrProviderDelivery() {
    var actor = actor("admin@example.test", "membership-admin", "corr-enterprise-export");
    var ediscovery = service.requestEdiscoveryExport(actor, new DigestExportService.EnterpriseExportCommand("idem-ediscovery", "audit_safe", "json", "audit secret=value", "case review"), "corr-ediscovery");
    var siem = service.requestSiemExport(actor, new DigestExportService.SiemExportCommand("idem-siem", "audit", true), "corr-siem");
    var compliance = service.requestComplianceReport(actor, new DigestExportService.EnterpriseExportCommand("idem-compliance", "audit_safe", "markdown", "audit", "SOC review"), "corr-compliance");

    assertEquals(DigestExportRequest.Status.PENDING_APPROVAL, ediscovery.status());
    assertFalse(ediscovery.toString().contains("secret=value"));
    assertEquals(DigestExportRequest.Status.BLOCKED_PROVIDER_OR_RUNTIME, siem.status());
    assertEquals("siem-provider-not-configured", siem.blockerCode());
    assertEquals(null, siem.resultUri());
    assertTrue(siem.safeSummary().contains("failed closed"));
    assertEquals(DigestExportRequest.Status.READY, compliance.status());
    assertTrue(compliance.safeSummary().contains("not a compliance-suite certification"));
  }

  @Test
  void enterpriseAuditExportsAreTenantScopedIdempotentAndCapabilityProtected() {
    var admin = actor("admin@example.test", "membership-admin", "corr-enterprise");
    var other = actor("other@example.test", "membership-other", "corr-other");
    var member = actor("member@example.test", "membership-member", "corr-member");
    var first = service.requestComplianceReport(admin, new DigestExportService.EnterpriseExportCommand("idem-compliance-idem", "strict", "json", "audit", "review"), "corr-enterprise");
    var duplicate = service.requestComplianceReport(admin, new DigestExportService.EnterpriseExportCommand("idem-compliance-idem", "strict", "json", "ignored", "review"), "corr-enterprise-dup");

    assertEquals(first.requestId(), duplicate.requestId());
    assertThrows(AuthorizationException.class, () -> service.read(other, first.requestId(), "corr-cross-enterprise"));
    assertThrows(AuthorizationException.class, () -> service.requestLegalHold(member, new DigestExportService.LegalHoldCommand("idem-denied-legal", Instant.parse("2026-06-26T10:00:00Z"), "audit", "denied"), "corr-member"));
  }

  @Test
  void missingCapabilityCannotStartDigestOrExport() {
    var member = actor("member@example.test", "membership-member", "corr-member");
    assertThrows(AuthorizationException.class, () -> service.startManualDigest(member, new DigestExportService.DigestCommand("idem-denied", null, "strict", "attention"), "corr-member"));
  }

  private AuthContextResolver.ResolvedMe actor(String email, String membershipId, String correlationId) {
    return resolver.resolveMe(new WorkosIdentity("workos-" + email, email, email), membershipId, correlationId);
  }

  private void addAccount(String email, String membershipId, String tenantId, List<FoundationRole> roles) {
    identityRepository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile(email, email, email, null, null, null));
    identityRepository.putSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership(membershipId, email, ScopeType.TENANT, tenantId, null, roles, MembershipStatus.ACTIVE, false, null));
  }
}
