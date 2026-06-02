import React from 'react';
import type { ApiClient, ApiError, MeResponse, ThemePreference } from '../../api';
import { Button, Card, DataState } from '../../design-system';

type RemoteData<T> =
  | { status: 'loading' }
  | { status: 'empty' }
  | { status: 'ready'; value: T }
  | { status: 'error'; error: ApiError };

export function ProfilePreferencesPage({ apiClient, themeId, onThemeChange }: { apiClient: ApiClient; themeId: ThemePreference; onThemeChange: (themeId: ThemePreference) => void }) {
  const [profileState, setProfileState] = React.useState<RemoteData<MeResponse>>({ status: 'loading' });
  const [draftThemeId, setDraftThemeId] = React.useState<ThemePreference>(themeId);
  const [emailDigest, setEmailDigest] = React.useState(true);
  const [decisionAlerts, setDecisionAlerts] = React.useState(true);
  const [saving, setSaving] = React.useState(false);
  const [message, setMessage] = React.useState<string>();
  const [simulateFailure, setSimulateFailure] = React.useState(false);

  const loadProfile = React.useCallback(async () => {
    setProfileState({ status: 'loading' });
    const result = await apiClient.session.getMe();
    if (!result.ok) {
      setProfileState({ status: 'error', error: result.error });
      return;
    }
    setProfileState({ status: 'ready', value: result.value });
    setDraftThemeId(result.value.preferences.themeId);
    onThemeChange(result.value.preferences.themeId);
  }, [apiClient, onThemeChange]);

  React.useEffect(() => {
    void loadProfile();
  }, [loadProfile]);

  async function savePreferences(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setMessage(undefined);
    setSaving(true);
    if (simulateFailure) {
      await new Promise((resolve) => window.setTimeout(resolve, 40));
      setSaving(false);
      setMessage('API failure: preferences were not saved. Keep your current selections and try again.');
      return;
    }
    const result = await apiClient.session.updatePreferences({ themeId: draftThemeId });
    setSaving(false);
    if (!result.ok) {
      setMessage(result.error.message);
      return;
    }
    onThemeChange(result.value.preferences.themeId);
    setMessage(`Preferences saved. Theme is ${result.value.preferences.themeId}; correlation ${result.value.correlationId}.`);
    await loadProfile();
  }

  return (
    <section className="profile-preferences" aria-label="Profile Preferences">
      <div className="slice-intro">
        <p className="eyebrow">Slice 7</p>
        <h2>Profile Preferences</h2>
        <p>Review profile context, choose an available named theme, and persist preferences through the session client seam.</p>
      </div>
      <div className="two-column-flow profile-layout">
        <DataState
          state={profileState.status === 'loading' ? { status: 'loading' } : profileState.status === 'empty' ? { status: 'empty' } : profileState.status === 'error' ? { status: 'error', error: profileState.error } : { status: 'ready', value: profileState.value }}
          loadingLabel="Loading profile preferences…"
          emptyTitle="Profile unavailable"
          emptyDetail="No session profile is available from the fixture client."
          onRetry={loadProfile}
        >
          {(profile) => <ProfileSummary profile={profile} />}
        </DataState>
        <Card className="form-card" title="Preferences" subtitle="Theme changes update the root data-theme token attribute after save; notification controls preserve future API seams.">
          <form className="stacked-form" onSubmit={savePreferences}>
            <fieldset className="preference-theme-group">
              <legend>Theme</legend>
              {(['aurora-light', 'cobalt-light', 'obsidian-dark', 'midnight-dark'] as const).map((option) => (
                <label key={option} className={draftThemeId === option ? 'theme-choice selected' : 'theme-choice'}>
                  <input type="radio" name="profile-theme" value={option} checked={draftThemeId === option} onChange={() => setDraftThemeId(option)} />
                  <span>{option}</span>
                </label>
              ))}
            </fieldset>
            <section className="notification-preferences" aria-labelledby="notification-preferences-heading">
              <h3 id="notification-preferences-heading">Notification preferences placeholder</h3>
              <label className="checkbox-row"><input type="checkbox" checked={emailDigest} onChange={(event) => setEmailDigest(event.target.checked)} /><span>Include routine work in async digest when real notification APIs are connected.</span></label>
              <label className="checkbox-row"><input type="checkbox" checked={decisionAlerts} onChange={(event) => setDecisionAlerts(event.target.checked)} /><span>Notify me about high-impact decision cards and approval exceptions.</span></label>
              <label className="checkbox-row subtle"><input type="checkbox" checked={simulateFailure} onChange={(event) => setSimulateFailure(event.target.checked)} /><span>Simulate API failure on save.</span></label>
            </section>
            <Button type="submit" disabled={saving}>{saving ? 'Saving preferences…' : 'Save preferences'}</Button>
          </form>
          {message && <p className={message.startsWith('API failure') ? 'form-status conflict' : 'success-note'} role="status" aria-live="polite">{message}</p>}
        </Card>
      </div>
    </section>
  );
}

function ProfileSummary({ profile }: { profile: MeResponse }) {
  const activeMembership = profile.memberships.find((membership) => membership.tenantId === profile.activeTenantId) ?? profile.memberships[0];
  return (
    <Card title="Profile summary" subtitle="Fixture session data is read through SessionClient.getMe.">
      <div className="detail-heading">
        <div>
          <h3>{profile.user.displayName}</h3>
          <p>{profile.user.email}</p>
        </div>
        <span className="version-chip">{activeMembership?.status ?? 'unknown'}</span>
      </div>
      <div className="decision-facts">
        <div className="fact"><span>Tenant</span><strong>{activeMembership?.tenantName ?? 'No tenant'}</strong></div>
        <div className="fact"><span>Roles</span><strong>{activeMembership?.roles.join(', ') ?? 'none'}</strong></div>
        <div className="fact"><span>Saved theme</span><strong>{profile.preferences.themeId}</strong></div>
        <div className="fact"><span>Security note</span><strong>UX only</strong></div>
      </div>
    </Card>
  );
}
