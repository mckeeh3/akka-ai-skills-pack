package {{JAVA_BASE_PACKAGE}}.domain.agentfoundation;

import java.time.Instant;

/** Provenance attached to behavior records created from implementation-packaged seed content. */
public record SeedProvenance(
    String seedBundleId,
    String contentVersion,
    String resourceId,
    String checksum,
    Instant importedAt,
    String importerActor,
    String correlationId,
    boolean tenantCustomized) {}
