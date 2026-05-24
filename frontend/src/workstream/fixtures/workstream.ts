import type { WorkstreamItem } from '../types';

const createdAt = '2026-05-19T12:00:00.000Z';
// The real API bootstrap and fixture bootstrap both start with exactly five core v0 starter markdown_response items.

export const initialWorkstreamItems: WorkstreamItem[] = [
  {
    itemId: 'item-v0-my-account-markdown',
    functionalAgentId: 'agent-my-account',
    kind: 'markdown_response',
    createdAt,
    correlationId: 'corr-v0-my-account-markdown',
    traceIds: ['trace-surface-v0-my-account-markdown'],
    surfaceId: 'surface-v0-my-account-markdown',
    title: 'My Account v0 response',
    body: 'Five core v0 starter surface for account, profile, settings, selected context, and authority basis.',
    status: 'ready'
  },
  {
    itemId: 'item-v0-user-admin-markdown',
    functionalAgentId: 'agent-user-admin',
    kind: 'markdown_response',
    createdAt,
    correlationId: 'corr-v0-user-admin-markdown',
    traceIds: ['trace-surface-v0-user-admin-markdown'],
    surfaceId: 'surface-v0-user-admin-markdown',
    title: 'User Admin v0 response',
    body: 'Five core v0 starter surface for invitations, memberships, roles, and access review questions.',
    status: 'ready'
  },
  {
    itemId: 'item-v0-agent-admin-markdown',
    functionalAgentId: 'agent-agent-admin',
    kind: 'markdown_response',
    createdAt,
    correlationId: 'corr-v0-agent-admin-markdown',
    traceIds: ['trace-surface-v0-agent-admin-markdown'],
    surfaceId: 'surface-v0-agent-admin-markdown',
    title: 'Agent Admin v0 response',
    body: 'Five core v0 starter surface for governed agent definitions, prompts, skills, tool boundaries, models, and traces.',
    status: 'ready'
  },
  {
    itemId: 'item-v0-audit-trace-markdown',
    functionalAgentId: 'agent-audit-trace',
    kind: 'markdown_response',
    createdAt,
    correlationId: 'corr-v0-audit-trace-markdown',
    traceIds: ['trace-surface-v0-audit-trace-markdown'],
    surfaceId: 'surface-v0-audit-trace-markdown',
    title: 'Audit/Trace v0 response',
    body: 'Five core v0 starter surface for browser-safe audit and trace summaries.',
    status: 'ready',
    traceLinks: [{ traceId: 'trace-surface-v0-audit-trace-markdown', label: 'Audit/Trace fixture trace', href: '/ui?surfaceId=surface-v0-audit-trace-markdown' }]
  },
  {
    itemId: 'item-v0-governance-policy-markdown',
    functionalAgentId: 'agent-governance-policy',
    kind: 'markdown_response',
    createdAt,
    correlationId: 'corr-v0-governance-policy-markdown',
    traceIds: ['trace-surface-v0-governance-policy-markdown'],
    surfaceId: 'surface-v0-governance-policy-markdown',
    title: 'Governance/Policy v0 response',
    body: 'Five core v0 starter surface for policy guardrails, approval boundaries, and deferred full-core behavior.',
    status: 'ready'
  }
];
