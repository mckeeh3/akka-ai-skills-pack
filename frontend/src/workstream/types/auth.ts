export type AccountStatus = 'active' | 'disabled' | 'pending';
export type ThemePreference = 'aurora-light' | 'cobalt-light' | 'obsidian-dark' | 'midnight-dark' | 'dark-night';

export type AccountSummary = {
  accountId: string;
  email: string;
  displayName: string;
  status: AccountStatus;
};

export type UserProfile = {
  displayName: string;
  locale?: string;
  timeZone?: string;
};

export type UserSettings = {
  preferredThemeId?: ThemePreference;
};

export type MembershipSummary = {
  membershipId: string;
  tenantId: string;
  tenantName: string;
  customerId?: string;
  customerName?: string;
  status: 'active' | 'pending' | 'disabled' | 'suspended';
  roleIds: string[];
  capabilityIds: string[];
};

export type SupportAccess = {
  active: boolean;
  reason?: string;
  expiresAt?: string;
};

export type AuthContext = {
  selectedContextId: string;
  tenantId: string;
  tenantName: string;
  customerId?: string;
  customerName?: string;
  membershipId: string;
  roleIds: string[];
  capabilityIds: string[];
  supportAccess?: SupportAccess;
};

export type MeResponse = {
  account: AccountSummary;
  profile: UserProfile;
  settings: UserSettings;
  memberships: MembershipSummary[];
  selectedAuthContext: AuthContext;
  availableAuthContexts: AuthContext[];
  visibleCapabilityIds: string[];
  functionalAgents: import('./agents').FunctionalAgentSummary[];
};

export type MeFixtureState = 'admin' | 'member' | 'auditorSupport' | 'disabled' | 'forbiddenNoMembership';
