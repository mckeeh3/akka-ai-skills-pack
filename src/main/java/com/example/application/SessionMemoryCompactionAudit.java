package com.example.application;

/** Public topic contract for session-memory compaction audit events. */
public record SessionMemoryCompactionAudit(
    String sessionId, String compactedBy, long compactedHistorySizeInBytes, String reason) {}
