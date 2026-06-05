package ai.first.application.foundation.agent;

import java.util.ArrayList;
import java.util.List;

/** Browser-safe/import-safe seed import summary with idempotency and customization-preservation notes. */
public final class SeedImportResult {
  private final String tenantId;
  private final String seedBundleId;
  private final String contentVersion;
  private final String correlationId;
  private final List<RecordResult> records = new ArrayList<>();

  public SeedImportResult(String tenantId, String seedBundleId, String contentVersion, String correlationId) {
    this.tenantId = tenantId;
    this.seedBundleId = seedBundleId;
    this.contentVersion = contentVersion;
    this.correlationId = correlationId;
  }

  public void created(String type, String id) { records.add(new RecordResult(type, id, "created-active", null)); }
  public void skippedUnchanged(String type, String id) { records.add(new RecordResult(type, id, "skipped-unchanged", null)); }
  public void proposedDraft(String type, String id, String reason) { records.add(new RecordResult(type, id, "draft-proposed", reason)); }

  public String tenantId() { return tenantId; }
  public String seedBundleId() { return seedBundleId; }
  public String contentVersion() { return contentVersion; }
  public String correlationId() { return correlationId; }
  public List<RecordResult> records() { return List.copyOf(records); }

  public long createdCount() { return records.stream().filter(record -> record.status().equals("created-active")).count(); }
  public long skippedCount() { return records.stream().filter(record -> record.status().equals("skipped-unchanged")).count(); }
  public long proposedDraftCount() { return records.stream().filter(record -> record.status().equals("draft-proposed")).count(); }

  public record RecordResult(String recordType, String recordId, String status, String reason) {}
}
