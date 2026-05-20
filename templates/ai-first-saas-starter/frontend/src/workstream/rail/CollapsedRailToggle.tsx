type CollapsedRailToggleProps = {
  collapsed: boolean;
  onToggle?: (collapsed: boolean) => void;
};

export function CollapsedRailToggle({ collapsed, onToggle }: CollapsedRailToggleProps) {
  return (
    <button
      type="button"
      className="collapse-button workstream-rail-toggle"
      aria-expanded={!collapsed}
      aria-controls="workstream-functional-agent-rail-list"
      onClick={() => onToggle?.(!collapsed)}
    >
      <span aria-hidden="true">{collapsed ? '›' : '‹'}</span>
      <span>{collapsed ? 'Expand agents' : 'Collapse agents'}</span>
    </button>
  );
}
