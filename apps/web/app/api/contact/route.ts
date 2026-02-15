import { NextResponse } from 'next/server';
import { postToBackend } from '@/app/api/_lib/backend-client';
import { toBackendErrorResult, toErrorResponse } from '@/app/api/_lib/error-response';
import { logger } from '@/components/utils/LoggingUtils';

export const runtime = 'nodejs';

const CONTACT_SERVICE = 'contact-api';
type ContactPayload = {
  name: string;
  email: string;
  message: string;
  website?: string;
};

/**
 * Basic email format check.
 */
function looksLikeEmailAddress(value: string): boolean {
  if (!value) return false;

  const atIndex = value.indexOf('@');
  if (atIndex <= 0) return false;

  const domain = value.slice(atIndex + 1);
  if (!domain.includes('.')) return false;

  return domain.indexOf('.') > 0 && domain.split('.').pop()!.length > 0;
}

/**
 * POST /api/contact
 *
 * Receives contact form submissions, validates them, rate-limits them,
 * and forwards the request to the backend (/contact) with X-API-KEY.
 *
 * Security:
 * - Honeypot: blocks bots
 * - Rate limit: limits repeated abuse
 */
export async function POST(request: Request) {
  try {
    const payload = (await request.json()) as Partial<ContactPayload>;

    // Honeypot: if filled, treat as spam and pretend it succeeded.
    if (payload.website && payload.website.trim().length > 0) {
      return NextResponse.json({ ok: true }, { status: 200 });
    }

    const name = (payload.name ?? '').trim();
    const email = (payload.email ?? '').trim();
    const message = (payload.message ?? '').trim();

    if (!name || !email || !message) {
      return NextResponse.json(
        { ok: false, errorCode: 'missing_fields', message: 'Missing fields.' },
        { status: 400 },
      );
    }

    if (!looksLikeEmailAddress(email)) {
      return NextResponse.json(
        { ok: false, errorCode: 'invalid_email', message: 'Invalid email.' },
        { status: 400 },
      );
    }

    if (message.length > 4000) {
      return NextResponse.json(
        { ok: false, errorCode: 'message_too_long', message: 'Message too long.' },
        { status: 400 },
      );
    }

    let backendResponse: Response;
    try {
      backendResponse = await postToBackend({
        path: '/contact',
        body: { name, email, message },
      });
    } catch (error) {
      logger.error({
        service: CONTACT_SERVICE,
        message: 'Backend not configured or unreachable',
        meta: { error },
      });

      return NextResponse.json(
        { ok: false, errorCode: 'server_not_configured', message: 'Server not configured.' },
        { status: 500 },
      );
    }

    if (backendResponse.ok) {
      logger.info({ service: CONTACT_SERVICE, message: 'Contact form submitted successfully' });
      return NextResponse.json({ ok: true }, { status: 200 });
    }

    const backendError = await toBackendErrorResult(backendResponse);

    logger.warn({
      service: CONTACT_SERVICE,
      message: 'Backend rejected request',
      meta: {
        status: backendError.status,
        errorCode: backendError.errorCode,
      },
    });

    return toErrorResponse(backendError);
  } catch (error) {
    logger.error({ service: CONTACT_SERVICE, message: 'Bad request', meta: { error } });
    return NextResponse.json(
      { ok: false, errorCode: 'bad_request', message: 'Bad request.' },
      { status: 400 },
    );
  }
}
