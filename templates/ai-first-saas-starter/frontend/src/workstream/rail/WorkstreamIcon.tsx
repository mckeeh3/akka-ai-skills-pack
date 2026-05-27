import type { WorkstreamIconDescriptor } from '../types';

type WorkstreamIconProps = {
  descriptor: WorkstreamIconDescriptor;
};

type IconArtwork = 'account' | 'users' | 'bot' | 'timeline' | 'shield' | 'billing' | 'support' | 'cart' | 'package' | 'invoice' | 'chart' | 'heart' | 'wrench' | 'workstream';

const iconArtworkById: Record<string, IconArtwork> = {
  'my-account': 'account',
  'users-admin': 'users',
  'bot-spark': 'bot',
  'bot-off-denied': 'bot',
  'timeline-search': 'timeline',
  'shield-checklist': 'shield',
  'credit-card-hidden': 'billing',
  'life-ring-disabled': 'support',
  'procurement-cart': 'cart',
  'inventory-package': 'package',
  'finance-invoice': 'invoice',
  'sales-chart': 'chart',
  'customer-success-heart': 'heart',
  'field-service-wrench': 'wrench'
};

const keywordArtwork: Array<[RegExp, IconArtwork]> = [
  [/\b(procurement|purchase|supplier|sourcing|order)\b/i, 'cart'],
  [/\b(inventory|warehouse|stock|supply|package)\b/i, 'package'],
  [/\b(finance|billing|invoice|payment|revenue|accounting)\b/i, 'invoice'],
  [/\b(sales|pipeline|crm|forecast|deal)\b/i, 'chart'],
  [/\b(customer success|success|health|renewal|retention)\b/i, 'heart'],
  [/\b(field service|service|maintenance|dispatch|repair)\b/i, 'wrench'],
  [/\b(user|member|membership|role|access|admin)\b/i, 'users'],
  [/\b(agent|bot|ai|model|prompt|skill)\b/i, 'bot'],
  [/\b(audit|trace|timeline|evidence|log)\b/i, 'timeline'],
  [/\b(governance|policy|guardrail|risk|compliance)\b/i, 'shield'],
  [/\b(profile|account|settings|me)\b/i, 'account'],
  [/\b(support|help|break-glass)\b/i, 'support']
];

export function deriveWorkstreamIconArtwork(descriptor: WorkstreamIconDescriptor): IconArtwork {
  const direct = iconArtworkById[descriptor.iconId];
  if (direct) return direct;
  const semanticText = `${descriptor.iconId} ${descriptor.visualHint} ${descriptor.displayName}`;
  return keywordArtwork.find(([pattern]) => pattern.test(semanticText))?.[1] ?? 'workstream';
}

export function WorkstreamIcon({ descriptor }: WorkstreamIconProps) {
  const artwork = deriveWorkstreamIconArtwork(descriptor);
  return (
    <svg viewBox="0 0 24 24" role="img" focusable="false" aria-label={descriptor.ariaLabel} data-icon-artwork={artwork}>
      <IconPaths artwork={artwork} />
    </svg>
  );
}

function IconPaths({ artwork }: { artwork: IconArtwork }) {
  switch (artwork) {
    case 'account':
      return <><circle cx="12" cy="8" r="3.2" /><path d="M5.5 19.2c1.2-3.4 3.2-5.1 6.5-5.1s5.3 1.7 6.5 5.1" /></>;
    case 'users':
      return <><circle cx="9" cy="8" r="3" /><circle cx="16.5" cy="9.5" r="2.3" /><path d="M3.8 19c1.1-3.6 2.9-5.2 5.2-5.2s4.1 1.6 5.2 5.2" /><path d="M13.6 14.6c2.6.1 4.4 1.5 5.6 4.4" /></>;
    case 'bot':
      return <><rect x="5" y="8" width="14" height="10" rx="3" /><path d="M12 8V4.8" /><circle cx="12" cy="4" r="1.2" /><circle cx="9" cy="13" r="1" /><circle cx="15" cy="13" r="1" /><path d="M9.5 16h5" /><path d="M19.2 5.2l.5 1.4 1.4.5-1.4.5-.5 1.4-.5-1.4-1.4-.5 1.4-.5.5-1.4Z" /></>;
    case 'timeline':
      return <><path d="M6 5v14" /><circle cx="6" cy="7" r="1.5" /><circle cx="6" cy="12" r="1.5" /><circle cx="6" cy="17" r="1.5" /><path d="M9 7h8" /><path d="M9 12h6" /><path d="M9 17h4" /><circle cx="17" cy="16" r="2.5" /><path d="m19 18 2 2" /></>;
    case 'shield':
      return <><path d="M12 3.5 18.5 6v5.5c0 4.2-2.4 7.2-6.5 9-4.1-1.8-6.5-4.8-6.5-9V6L12 3.5Z" /><path d="m8.8 12 2.2 2.2 4.5-4.8" /></>;
    case 'billing':
      return <><rect x="4" y="6" width="16" height="12" rx="2" /><path d="M4 10h16" /><path d="M8 15h3" /><path d="M15 15h2" /></>;
    case 'support':
      return <><circle cx="12" cy="12" r="8" /><circle cx="12" cy="12" r="3" /><path d="m6.5 6.5 3.3 3.3" /><path d="m14.2 14.2 3.3 3.3" /><path d="m17.5 6.5-3.3 3.3" /><path d="m9.8 14.2-3.3 3.3" /></>;
    case 'cart':
      return <><path d="M4 5h2l2.2 9.2a2 2 0 0 0 2 1.5h6.8a2 2 0 0 0 1.9-1.4L20 9H7" /><circle cx="10" cy="19" r="1.4" /><circle cx="17" cy="19" r="1.4" /></>;
    case 'package':
      return <><path d="M4.5 8 12 4l7.5 4v8L12 20l-7.5-4V8Z" /><path d="M4.8 8.2 12 12l7.2-3.8" /><path d="M12 12v8" /><path d="m8.2 6.1 7.3 3.9" /></>;
    case 'invoice':
      return <><path d="M7 3.5h8.5L19 7v13.5H7V3.5Z" /><path d="M15.5 3.8V7H19" /><path d="M10 10h6" /><path d="M10 13h6" /><path d="M10 16h3" /><path d="M5 6v15h11" /></>;
    case 'chart':
      return <><path d="M4 19h16" /><path d="M6 16.5 10 12l3 2.5 5-7" /><path d="M16 7.5h2.5V10" /><circle cx="10" cy="12" r="1" /><circle cx="13" cy="14.5" r="1" /></>;
    case 'heart':
      return <><path d="M12 20s-7-4.4-8.5-9.2C2.6 7.7 4.6 5 7.6 5c1.8 0 3.2 1 4.4 2.6C13.2 6 14.6 5 16.4 5c3 0 5 2.7 4.1 5.8C19 15.6 12 20 12 20Z" /><path d="M7 12h2.4l1-2.2 2.1 4.6 1.2-2.4H17" /></>;
    case 'wrench':
      return <><path d="M14.5 4.2a5 5 0 0 0 5.3 6.6l-8.9 8.9a2.3 2.3 0 0 1-3.2-3.2l8.9-8.9a5 5 0 0 1-2.1-3.4Z" /><path d="M8.2 17.4h.1" /></>;
    case 'workstream':
      return <><rect x="4" y="5" width="16" height="14" rx="3" /><path d="M8 9h8" /><path d="M8 13h5" /><path d="M15 14.5 18 17" /></>;
  }
}
