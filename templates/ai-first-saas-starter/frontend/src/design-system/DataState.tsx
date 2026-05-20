import type { ReactNode } from 'react';
import type { ApiError } from '../api';
import { Button } from './Button';

export type RemoteData<T> =
  | { status: 'idle' | 'loading' }
  | { status: 'empty' }
  | { status: 'ready'; value: T }
  | { status: 'error'; error: ApiError };

export function DataState<T>({ state, loadingLabel, emptyTitle, emptyDetail, children, onRetry }: { state: RemoteData<T>; loadingLabel: string; emptyTitle: string; emptyDetail: string; children: (value: T) => ReactNode; onRetry?: () => void }) {
  switch (state.status) {
    case 'idle':
    case 'loading':
      return <div className="data-state loading" aria-busy="true">{loadingLabel}</div>;
    case 'empty':
      return <div className="data-state"><h2>{emptyTitle}</h2><p>{emptyDetail}</p></div>;
    case 'error':
      return (
        <div className="data-state error" role="alert">
          <h2>Could not load this information</h2>
          <p>{state.error.message}</p>
          <small>Correlation: {state.error.correlationId}</small>
          {onRetry && <Button tone="secondary" onClick={onRetry}>Retry</Button>}
        </div>
      );
    case 'ready':
      return <>{children(state.value)}</>;
  }
}
