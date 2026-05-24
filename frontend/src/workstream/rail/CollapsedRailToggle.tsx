type CollapsedRailToggleProps = {
  collapsed: boolean;
  onToggle?: (collapsed: boolean) => void;
};

export function CollapsedRailToggle({ collapsed, onToggle }: CollapsedRailToggleProps) {
  const label = collapsed ? 'Expand sidebar' : 'Collapse sidebar';

  return (
    <button
      type="button"
      className="collapse-button workstream-rail-toggle"
      aria-expanded={!collapsed}
      aria-controls="workstream-functional-agent-rail-list"
      aria-label={label}
      onClick={() => onToggle?.(!collapsed)}
    >
      <span className="workstream-rail-toggle-icon" aria-hidden="true">{collapsed ? '›' : '‹'}</span>
      <span className="workstream-rail-toggle-tooltip" role="tooltip">{label}</span>
      <span className="sr-only">{label}</span>
    </button>
  );
}
