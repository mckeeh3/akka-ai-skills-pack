import userAdminSurfaces from '../../../app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md?raw';
import agentAdminSurfaces from '../../../app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md?raw';
import auditTraceSurfaces from '../../../app-description/domains/core-starter/workstreams/audit-trace/surfaces/surfaces.md?raw';
import governancePolicySurfaces from '../../../app-description/domains/core-starter/workstreams/governance-policy/surfaces/surfaces.md?raw';
import myAccountSurfaces from '../../../app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md?raw';

export type WorkstreamDescription = {
  workstreamId: string;
  label: string;
  sourcePath: string;
  markdown: string;
};

export const workstreamDescriptions: Record<string, WorkstreamDescription> = {
  'user-admin': {
    workstreamId: 'user-admin',
    label: 'User Admin',
    sourcePath: 'app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md',
    markdown: userAdminSurfaces
  },
  'agent-admin': {
    workstreamId: 'agent-admin',
    label: 'Agent Admin',
    sourcePath: 'app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md',
    markdown: agentAdminSurfaces
  },
  'audit-trace': {
    workstreamId: 'audit-trace',
    label: 'Audit Trace',
    sourcePath: 'app-description/domains/core-starter/workstreams/audit-trace/surfaces/surfaces.md',
    markdown: auditTraceSurfaces
  },
  'governance-policy': {
    workstreamId: 'governance-policy',
    label: 'Governance / Policy',
    sourcePath: 'app-description/domains/core-starter/workstreams/governance-policy/surfaces/surfaces.md',
    markdown: governancePolicySurfaces
  },
  'my-account': {
    workstreamId: 'my-account',
    label: 'My Account',
    sourcePath: 'app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md',
    markdown: myAccountSurfaces
  }
};

export function surfaceDescriptionExcerpt(markdown: string, surfaceId: string): string {
  const lines = markdown.split('\n');
  const headingIndex = lines.findIndex((line) => line.startsWith('##') && line.includes(surfaceId));
  if (headingIndex >= 0) {
    const nextHeadingIndex = lines.findIndex((line, index) => index > headingIndex && line.startsWith('## '));
    return lines.slice(headingIndex, nextHeadingIndex > headingIndex ? nextHeadingIndex : undefined).join('\n').trim();
  }

  const rowIndex = lines.findIndex((line) => line.includes(`\`${surfaceId}\``) || line.includes(surfaceId));
  if (rowIndex >= 0) {
    const sectionStart = Math.max(0, findPreviousHeading(lines, rowIndex));
    const tableStart = Math.max(sectionStart, rowIndex - 2);
    const tableEnd = Math.min(lines.length, rowIndex + 8);
    return [
      `## ${surfaceId}`,
      '',
      `Source excerpt from the surface description inventory.`,
      '',
      ...lines.slice(tableStart, tableEnd)
    ].join('\n').trim();
  }

  return [
    `## ${surfaceId}`,
    '',
    'No exact surface-specific section was found in this workstream surface description. The full source file is still linked and should be reviewed for missing or stale surface coverage.'
  ].join('\n');
}

function findPreviousHeading(lines: string[], fromIndex: number): number {
  for (let index = fromIndex; index >= 0; index -= 1) {
    if (lines[index].startsWith('## ')) return index;
  }
  return 0;
}
