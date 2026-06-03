export interface RequestRow {
  id: string;
  title: string;
  requester: string;
  status: string;
  amount: number;
}

export interface DashboardResponse {
  requests: RequestRow[];
  allowedStatuses: string[];
  streamPath: string;
  submitPath: string;
}

export interface SubmitRequest {
  title: string;
  requester: string;
  amount: number;
}

export interface SubmitResponse {
  request: RequestRow;
  message: string;
}

export type ApiErrorKind = "network" | "validation" | "server" | "malformedResponse";

export interface ApiError {
  kind: ApiErrorKind;
  message: string;
}

export type ApiResult<T> = { ok: true; value: T } | { ok: false; error: ApiError };

export type RemoteData<T> =
  | { status: "idle" }
  | { status: "loading" }
  | { status: "ready"; value: T }
  | { status: "empty" }
  | { status: "error"; message: string };

export interface SubmitState {
  status: "idle" | "submitting" | "success" | "error";
  message?: string;
}

export interface AppState {
  dashboard: RemoteData<DashboardResponse>;
  selectedStatus: string;
  submit: SubmitState;
}

export interface FieldError {
  field: keyof SubmitRequest;
  message: string;
}

export type ValidationResult =
  | { ok: true; input: SubmitRequest }
  | { ok: false; errors: FieldError[] };
