export function renderWorkstreamText(value: unknown): string {
  if (value == null) return '';
  if (typeof value === 'string') return value;
  if (typeof value === 'number' || typeof value === 'boolean' || typeof value === 'bigint') return String(value);
  if (Array.isArray(value)) return value.map(renderWorkstreamText).filter(Boolean).join(' · ');
  if (typeof value === 'object') {
    return Object.entries(value as Record<string, unknown>)
      .map(([key, entry]) => `${key}: ${renderWorkstreamText(entry) || 'n/a'}`)
      .join(' · ');
  }
  return String(value);
}

export function hasWorkstreamText(value: unknown): boolean {
  return renderWorkstreamText(value).trim().length > 0;
}
