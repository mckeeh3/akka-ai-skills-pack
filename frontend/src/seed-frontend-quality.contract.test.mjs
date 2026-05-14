import assert from 'node:assert/strict';
import { existsSync, readdirSync, readFileSync, statSync } from 'node:fs';
import { join } from 'node:path';
import test from 'node:test';

const root = new URL('.', import.meta.url);
const main = readFileSync(new URL('./main.tsx', import.meta.url), 'utf8');
const packageJson = JSON.parse(readFileSync(new URL('../package.json', import.meta.url), 'utf8'));
const tokens = readFileSync(new URL('./styles/tokens.css', import.meta.url), 'utf8');
const base = readFileSync(new URL('./styles/base.css', import.meta.url), 'utf8');
const layout = readFileSync(new URL('./styles/layout.css', import.meta.url), 'utf8');
const components = readFileSync(new URL('./styles/components.css', import.meta.url), 'utf8');

const screenFiles = {
  briefing: './screens/briefing/BriefingPage.tsx',
  goals: './screens/goals/GoalWorkbenchPage.tsx',
  decisions: './screens/decisions/DecisionQueuePage.tsx',
  governance: './screens/governance/GovernancePoliciesPage.tsx',
  audit: './screens/audit/AuditTraceExplorerPage.tsx',
  admin: './screens/admin/AdminUsersPage.tsx',
  profile: './screens/profile/ProfilePreferencesPage.tsx'
};

const screens = Object.fromEntries(
  Object.entries(screenFiles).map(([id, path]) => [id, readFileSync(new URL(path, root), 'utf8')])
);

const sourceFiles = collectSourceFiles(new URL('.', import.meta.url).pathname);

test('Slice 8 route shell smoke coverage includes every localized seed route', () => {
  const routeExpectations = [
    ['briefing', '/ui/briefing', 'BriefingPage'],
    ['goals', '/ui/goals/new', 'GoalWorkbenchPage'],
    ['decisions', '/ui/decisions', 'DecisionQueuePage'],
    ['governance', '/ui/governance/policies', 'GovernancePoliciesPage'],
    ['audit', '/ui/audit/traces', 'AuditTraceExplorerPage'],
    ['admin', '/ui/admin/users', 'AdminUsersPage'],
    ['profile', '/ui/profile', 'ProfilePreferencesPage']
  ];

  for (const [id, path, component] of routeExpectations) {
    assert.match(main, new RegExp(`id: '${id}'`));
    assert.match(main, new RegExp(`path: '${escapeRegex(path)}'`));
    assert.match(main, new RegExp(`route === '${id}'`));
    assert.match(main, new RegExp(`<${component}`));
  }
});

test('Slice 8 design-specific acceptance markers remain covered by source contracts', () => {
  assert.match(screens.briefing, /CommandStrip/);
  assert.match(screens.briefing, /Mission Control KPI band/);
  assert.match(screens.briefing, /Needs attention/);
  assert.match(screens.briefing, /Trust controls/);
  assert.match(screens.goals, /Create a durable goal/);
  assert.match(screens.goals, /Acknowledge the approval gates before launching/);
  assert.match(screens.decisions, /Evidence/);
  assert.match(screens.decisions, /Risk/);
  assert.match(screens.decisions, /Policy/);
  assert.match(screens.decisions, /Trace links/);
  assert.match(screens.governance, /Authority-change warning/);
  assert.match(screens.audit, /trace-goal/);
  assert.match(screens.audit, /authorization basis/i);
  assert.match(screens.admin, /Invite user/);
  assert.match(screens.admin, /Elevated-role reason/);
  assert.match(screens.profile, /Display mode/);
  assert.match(screens.profile, /updatePreferences/);
});

test('Slice 8 light dark system accessibility and responsive checklist has source evidence', () => {
  assert.match(tokens, /\[data-mode="light"\]/);
  assert.match(tokens, /\[data-mode="dark"\]/);
  assert.match(main, /mode === 'system'/);
  assert.match(base, /:focus-visible/);
  assert.match(base, /prefers-reduced-motion/);
  assert.match(main, /<main id="main-content"/);
  assert.match(main, /Skip to main content/);
  assert.match(components, /Status is always textual, not color-only|status-pill/);
  assert.match(layout + components, /@media \(max-width: 640px\)/);
  assert.match(components, /\.mission-grid/);
  assert.match(components, /\.two-column-flow/);
});

test('Slice 8 source avoids unsafe dynamic HTML insertion', () => {
  for (const file of sourceFiles) {
    const text = readFileSync(file, 'utf8');
    assert.doesNotMatch(text, /dangerouslySetInnerHTML|\.innerHTML\s*=/, file);
  }
});

test('Slice 8 frontend scripts document the quality and Akka static handoff commands', () => {
  assert.equal(packageJson.scripts.typecheck, 'tsc --noEmit');
  assert.equal(packageJson.scripts.test, 'node --test src/*.test.mjs');
  assert.match(packageJson.scripts.build, /vite build --outDir \.\.\/src\/main\/resources\/static-resources/);
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

function escapeRegex(text) {
  return text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}
