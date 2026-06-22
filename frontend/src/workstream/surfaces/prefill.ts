import type { BrowserSafeSurfacePrefill, RoutedSurfaceIntentRoute } from '../types';

type PrefillCarrier = {
  prefill?: BrowserSafeSurfacePrefill;
  surfaceIntentRoute?: RoutedSurfaceIntentRoute;
  form?: {
    prefillReviewMessage?: unknown;
    prefillReviewRequired?: unknown;
    prefillSource?: unknown;
    [key: string]: unknown;
  };
  noDirectMutation?: unknown;
};

export const routedPrefillReviewCopy = 'Prefilled from your request. Review or edit these fields, then submit the form; nothing is created automatically.';

export function browserSafePrefillString(data: PrefillCarrier, fieldId: string): string | undefined {
  const directPrefill = normalizeBrowserSafePrefillValue(data.prefill?.[fieldId]);
  if (directPrefill !== undefined) return directPrefill;
  return normalizeBrowserSafePrefillValue(data.surfaceIntentRoute?.prefill?.[fieldId]);
}

export function firstNonEmptyFormValue(...values: unknown[]): string {
  for (const value of values) {
    const normalized = normalizeBrowserSafePrefillValue(value);
    if (normalized !== undefined && normalized.trim().length > 0) return normalized;
  }
  return '';
}

export function hasRoutedPrefill(data: PrefillCarrier): boolean {
  return Boolean(
    data.form?.prefillReviewRequired
      || data.form?.prefillSource === 'surface-intent-route'
      || data.surfaceIntentRoute?.category === 'surface_create_prefill'
      || Object.keys(data.prefill ?? {}).length > 0
  );
}

export function routedPrefillMessage(data: PrefillCarrier): string {
  const formMessage = normalizeBrowserSafePrefillValue(data.form?.prefillReviewMessage);
  return formMessage && formMessage.trim().length > 0 ? `${formMessage} Review or edit these fields before submitting.` : routedPrefillReviewCopy;
}

function normalizeBrowserSafePrefillValue(value: unknown): string | undefined {
  if (typeof value === 'string') return value;
  if (typeof value === 'number' || typeof value === 'boolean') return String(value);
  return undefined;
}
