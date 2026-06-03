import assert from 'node:assert/strict';
import { existsSync, readdirSync, readFileSync, statSync } from 'node:fs';
import { join } from 'node:path';
import test from 'node:test';

const root = new URL('.', import.meta.url);
const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const main = read('./main.tsx');
const packageJson = JSON.parse(readFileSync(new URL('../package.json', import.meta.url), 'utf8'));
const tokens = read('./styles/tokens.css');
const base = read('./styles/base.css');
const layout = read('./styles/layout.css');
const components = read('./styles/components.css');
const shell = read('./workstream/shell/WorkstreamShell.tsx');
const panel = read('./workstream/shell/WorkstreamPanel.tsx');
const composer = read('./workstream/composer/WorkstreamComposer.tsx');
const surfaceFrame = read('./workstream/surfaces/SurfaceStateFrame.tsx');
const actionButton = read('./workstream/actions/CapabilityActionButton.tsx');
const realtime = read('./workstream/realtime/workstreamEvents.ts');

const sourceFiles = collectSourceFiles(new URL('.', import.meta.url).pathname);
const legacyScreenFiles = [
  './screens/briefing/BriefingPage.tsx',
  './screens/goals/GoalWorkbenchPage.tsx',
  './screens/decisions/DecisionQueuePage.tsx',
  './screens/governance/GovernancePoliciesPage.tsx',
  './screens/audit/AuditTraceExplorerPage.tsx',
  './screens/admin/AdminUsersPage.tsx',
  './screens/profile/ProfilePreferencesPage.tsx'
];

test('route shell tests have been replaced by workstream deep-link contract coverage', () => {
  assert.match(main, /<WorkstreamShell/);
  assert.match(main, /parseWorkstreamDeepLink/);
  assert.match(main, /serializeWorkstreamDeepLink/);
  assert.match(main, /selectedFunctionalAgentId/);
  assert.match(main, /selectedItemId/);
  assert.match(main, /selectedSurfaceId/);
  assert.match(main, /surfacePlacement: 'inline'/);
  assert.doesNotMatch(main, /function RouteShell|SidebarNav|route === 'briefing'|path: '\/ui\/briefing'/);
});

test('legacy screen source remains quarantined and is not imported by canonical entry', () => {
  for (const path of legacyScreenFiles) {
    assert.ok(existsSync(new URL(path, root)), `${path} remains available only for quarantine/drift comparison`);
  }
  assert.doesNotMatch(main, /from '\.\/screens\//);
  assert.doesNotMatch(main, /<BriefingPage|<GoalWorkbenchPage|<DecisionQueuePage|<GovernancePoliciesPage|<AuditTraceExplorerPage|<AdminUsersPage|<ProfilePreferencesPage/);
});

test('workstream accessibility and quality checklist markers have source evidence', () => {
  assert.match(shell, /Skip to main workstream/);
  assert.match(panel, /<main id="main-content" className="content workstream-panel"/);
  assert.match(panel, /aria-labelledby="workstream-panel-title"/);
  assert.match(composer, /aria-label="Persistent workstream composer"/);
  assert.match(surfaceFrame, /role="alert"/);
  assert.match(surfaceFrame, /role="status"/);
  assert.match(actionButton, /aria-describedby/);
  assert.match(actionButton, /disabled=\{disabled\}/);
  assert.match(base, /:focus-visible/);
  assert.match(base, /prefers-reduced-motion/);
  assert.match(layout + components, /@media \(max-width: 640px\)/);
  assert.match(shell, /data-mobile-rail-open=\{mobileRailOpen \? 'true' : 'false'\}/);
  assert.match(shell, /className=\{`mobile-menu-button/);
  assert.match(shell, /className="nav-backdrop"/);
  assert.match(layout, /\.mobile-menu-button\.mobile-menu-button,[\s\S]*?\.nav-backdrop\.nav-backdrop \{ display: none; \}/);
  assert.match(layout, /\.mobile-menu-button\.mobile-menu-button \{[\s\S]*?display: inline-flex;/);
  assert.match(layout, /\.mobile-menu-button\.mobile-menu-button\.hidden/);
  assert.match(layout, /env\(safe-area-inset-top/);
});

test('workstream composer send tooltip can escape the composer frame', () => {
  assert.match(components, /\.workstream-composer \{[\s\S]*?overflow: visible;/);
  assert.match(components, /\.send-prompt-button \{[\s\S]*?overflow: visible;/);
  assert.match(components, /\.workstream-send-prompt-tooltip \{[\s\S]*?position: absolute;/);
});

test('named themes and tokenized semantic states remain available', () => {
  assert.match(tokens, /\[data-theme="aurora-light"\]/);
  assert.match(tokens, /\[data-theme="cobalt-light"\]/);
  assert.match(tokens, /\[data-theme="obsidian-dark"\]/);
  assert.match(tokens, /\[data-theme="midnight-dark"\]/);
  assert.match(tokens, /\[data-theme="dark-night"\]/);
  assert.match(tokens, /--color-success-soft/);
  assert.match(tokens, /--color-warning-soft/);
  assert.match(tokens, /--color-danger-soft/);
  assert.match(components, /\.status-pill\.success/);
  assert.match(components, /\.status-pill\.warning/);
  assert.match(components, /\.status-pill\.danger/);
  assert.match(surfaceFrame, /surface-frame forbidden/);
  assert.match(components, /\.realtime-banner\.stale/);
});

test('realtime and stale behavior is explicit and safe', () => {
  assert.match(realtime, /selectedTenantId/);
  assert.match(realtime, /Ignored cross-context event/);
  assert.match(realtime, /Ignored out-of-order event/);
  assert.match(realtime, /lastEventId/);
  assert.match(realtime, /realtimeStatusLabel/);
  assert.match(realtime, /Disconnected|Reconnecting|Stale/);
});

test('frontend source avoids unsafe dynamic HTML insertion', () => {
  for (const file of sourceFiles) {
    const text = readFileSync(file, 'utf8');
    assert.doesNotMatch(text, /dangerouslySetInnerHTML|\.innerHTML\s*=/, file);
  }
});

test('frontend scripts document the quality and Akka static handoff commands', () => {
  assert.equal(packageJson.scripts.typecheck, 'tsc --noEmit');
  assert.equal(packageJson.scripts.test, 'node --test src/*.test.mjs');
  assert.equal(packageJson.scripts.prebuild, 'node scripts/clean-static-assets.mjs');
  assert.match(packageJson.scripts.build, /vite build --outDir \.\.\/src\/main\/resources\/static-resources/);
  assert.equal(packageJson.scripts['analyze:bundle'], 'node scripts/report-bundle-size.mjs');
  assert.ok(existsSync(new URL('../scripts/clean-static-assets.mjs', import.meta.url)));
  assert.ok(existsSync(new URL('../scripts/report-bundle-size.mjs', import.meta.url)));
  assert.ok(existsSync(new URL('../README.md', import.meta.url)));
});

function collectSourceFiles(dir) {
  return readdirSync(dir).flatMap((entry) => {
    const path = join(dir, entry);
    const stat = statSync(path);
    if (stat.isDirectory()) return collectSourceFiles(path);
    return /\.(tsx?|css)$/.test(path) ? [path] : [];
  });
}
