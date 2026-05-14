import assert from 'node:assert/strict';
import { readFileSync, existsSync } from 'node:fs';
import test from 'node:test';

const main = readFileSync(new URL('./main.tsx', import.meta.url), 'utf8');
const governance = readFileSync(new URL('./screens/governance/GovernancePoliciesPage.tsx', import.meta.url), 'utf8');
const audit = readFileSync(new URL('./screens/audit/AuditTraceExplorerPage.tsx', import.meta.url), 'utf8');
const admin = readFileSync(new URL('./screens/admin/AdminUsersPage.tsx', import.meta.url), 'utf8');
const profile = readFileSync(new URL('./screens/profile/ProfilePreferencesPage.tsx', import.meta.url), 'utf8');
const fixture = readFileSync(new URL('./api/FixtureApiClient.ts', import.meta.url), 'utf8');
const components = readFileSync(new URL('./styles/components.css', import.meta.url), 'utf8');

test('Slice 7 wires governance, audit, admin, and profile screens to route shells', () => {
  assert.ok(existsSync(new URL('./screens/governance/GovernancePoliciesPage.tsx', import.meta.url)));
  assert.ok(existsSync(new URL('./screens/audit/AuditTraceExplorerPage.tsx', import.meta.url)));
  assert.ok(existsSync(new URL('./screens/admin/AdminUsersPage.tsx', import.meta.url)));
  assert.ok(existsSync(new URL('./screens/profile/ProfilePreferencesPage.tsx', import.meta.url)));
  assert.match(main, /import \{ GovernancePoliciesPage \}/);
  assert.match(main, /import \{ AuditTraceExplorerPage \}/);
  assert.match(main, /import \{ AdminUsersPage \}/);
  assert.match(main, /import \{ ProfilePreferencesPage \}/);
  assert.match(main, /route === 'governance'/);
  assert.match(main, /route === 'audit'/);
  assert.match(main, /route === 'admin'/);
  assert.match(main, /route === 'profile'/);
});

test('Governance Center implements proposal, simulation, commit warning, and audit links', () => {
  assert.match(governance, /Governance Center/);
  assert.match(governance, /Policy proposal panel/);
  assert.match(governance, /Simulation and commit state/);
  assert.match(governance, /Authority-change warning/);
  assert.match(governance, /Confirm authorized policy commit/);
  assert.match(governance, /Open policy invocation traces/);
  assert.match(fixture, /governance = \{/);
  assert.match(fixture, /createPolicyProposal/);
  assert.match(fixture, /simulatePolicyProposal/);
});

test('Audit Trace Explorer covers required filters, details, no-results, and forbidden export state', () => {
  assert.match(audit, /Trace filters/);
  assert.match(audit, /id="trace-goal"/);
  assert.match(audit, /id="trace-agent"/);
  assert.match(audit, /id="trace-decision"/);
  assert.match(audit, /id="trace-policy"/);
  assert.match(audit, /id="trace-tool"/);
  assert.match(audit, /id="trace-actor"/);
  assert.match(audit, /id="trace-time"/);
  assert.match(audit, /No trace results/);
  assert.match(audit, /Forbidden export/);
  assert.match(audit, /authorization basis/i);
  assert.match(audit, /correlation/i);
});

test('Admin Users implements invite, duplicate handling, role validation, and UX-only auth copy', () => {
  assert.match(admin, /Admin Users and Invitations/);
  assert.match(admin, /Invite user/);
  assert.match(admin, /Duplicate invitation handled/);
  assert.match(admin, /Elevated-role reason/);
  assert.match(admin, /Confirm the elevated authority change/);
  assert.match(admin, /UX-only visibility/);
  assert.match(fixture, /inviteUser/);
  assert.match(fixture, /status: 'resent'/);
  assert.match(fixture, /updateRoles/);
  assert.match(fixture, /Reason is required for elevated role grants/);
});

test('Profile Preferences persists mode through session client seam and includes failure state', () => {
  assert.match(profile, /Profile Preferences/);
  assert.match(profile, /Display mode/);
  assert.match(profile, /updatePreferences/);
  assert.match(profile, /Simulate API failure on save/);
  assert.match(profile, /Preferences saved/);
  assert.match(profile, /Notification preferences placeholder/);
  assert.match(fixture, /updatePreferences/);
});

test('Slice 7 styles support trace rows, authority warnings, and responsive layouts', () => {
  assert.match(components, /\.trace-row/);
  assert.match(components, /\.commit-warning/);
  assert.match(components, /\.preference-mode-group/);
  assert.match(components, /@media \(max-width: 640px\)[\s\S]*\.trace-row,[\s\S]*\.decision-facts \{ grid-template-columns: 1fr; \}/);
});
