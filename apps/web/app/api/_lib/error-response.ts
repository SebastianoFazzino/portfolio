import { NextResponse } from 'next/server';

export type BackendValidationError = { field?: string; message?: string };

export type NormalizedBackendError = {
  status: number;
  errorCode: string;
  message: string;
  validationErrors: BackendValidationError[] | null;
};

type ErrorResponse = {
  httpStatus?: number;
  errorCode?: string;
  message?: string;
  validationErrors?: BackendValidationError[];
};

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}

async function readJsonSafely(response: Response): Promise<unknown | null> {
  const contentType = response.headers.get('content-type') ?? '';
  if (!contentType.includes('application/json')) return null;
  try {
    return await response.json();
  } catch {
    return null;
  }
}

function normalizeBackendError(body: unknown): ErrorResponse {
  if (!isRecord(body)) return {};

  const httpStatus = typeof body.httpStatus === 'number' ? body.httpStatus : undefined;
  const errorCode = typeof body.errorCode === 'string' ? body.errorCode : undefined;
  const message = typeof body.message === 'string' ? body.message : undefined;

  const validationErrors = Array.isArray(body.validationErrors)
    ? (body.validationErrors as BackendValidationError[])
    : undefined;

  return { httpStatus, errorCode, message, validationErrors };
}

function mapErrorCodeToClientMessage(errorCode: string | undefined): string {
  if (errorCode === 'too_many_requests') return 'Too many requests';
  if (errorCode === 'contact_rejected') return 'Message rejected';
  if (errorCode === 'email_send_failed') return 'Failed to send message';
  return 'Something went wrong';
}

export async function toBackendErrorResult(
  backendResponse: Response,
): Promise<NormalizedBackendError> {
  const backendBodyRaw = await readJsonSafely(backendResponse);
  const backendBody = normalizeBackendError(backendBodyRaw);

  const errorCode = backendBody.errorCode ?? 'unknown_error';
  const message = mapErrorCodeToClientMessage(backendBody.errorCode);

  return {
    status: backendResponse.status,
    errorCode,
    message,
    validationErrors: backendBody.validationErrors ?? null,
  };
}

export function toErrorResponse(error: NormalizedBackendError) {
  return NextResponse.json(
    {
      ok: false,
      errorCode: error.errorCode,
      message: error.message,
      validationErrors: error.validationErrors,
    },
    { status: error.status },
  );
}
