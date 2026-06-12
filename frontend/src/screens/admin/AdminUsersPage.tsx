import type { ApiClient } from '../../api';
import { Card } from '../../design-system';

export function AdminUsersPage({ apiClient: _apiClient }: { apiClient: ApiClient }) {
  return (
    <section className="admin-users quarantined-legacy-screen" aria-label="Quarantined legacy Admin Users page">
      <Card
        className="form-card"
        title="Legacy Admin Users page retired"
        subtitle="Normal runtime User Admin work is handled by backend-authored structured workstream surfaces."
      >
        <p>
          This screen is preserved only as a quarantined mechanics reference for drift tests. It is not imported by
          the canonical entry point and must not be used as a normal runtime path for invitations, role changes,
          support access, membership lifecycle work, access reviews, or identity exceptions.
        </p>
        <p>
          Use the User Admin workstream surfaces instead: dashboard, users directory, show-inspection detail,
          dedicated task forms, confirmations, decision cards, workflow status, and typed system messages.
        </p>
      </Card>
    </section>
  );
}
