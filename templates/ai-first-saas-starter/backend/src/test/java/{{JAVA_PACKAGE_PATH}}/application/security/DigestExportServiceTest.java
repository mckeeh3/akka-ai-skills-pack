package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.DigestExportRequest;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import {{JAVA_BASE_PACKAGE}}.domain.security.Tenant;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
