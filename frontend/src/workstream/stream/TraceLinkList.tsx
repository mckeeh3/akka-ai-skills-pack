import type { TraceLink } from '../types';

type TraceLinkListProps = {
  traceIds?: string[];
  traceLinks?: TraceLink[];
};

export function TraceLinkList({ traceIds = [], traceLinks = [] }: TraceLinkListProps) {
  const derivedLinks = traceIds
    .filter((traceId) => !traceLinks.some((link) => link.traceId === traceId))
    .map((traceId) => ({ traceId, label: traceId, href: `/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(traceId)}` }));
  const links = [...traceLinks, ...derivedLinks];

  if (links.length === 0) {
    return null;
  }

  return (
    <nav className="trace-link-list" aria-label="Trace links">
      <span className="eyebrow">Trace</span>
      <ul>
        {links.map((link) => (
          <li key={`${link.traceId}-${link.href}`}>
            <a href={link.href}>{link.label}</a>
          </li>
        ))}
      </ul>
    </nav>
  );
}
