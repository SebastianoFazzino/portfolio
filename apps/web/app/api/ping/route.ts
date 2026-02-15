import { NextResponse } from 'next/server';
import { postToBackend } from '@/app/api/_lib/backend-client';
import { logger } from '@/components/utils/LoggingUtils';

export const runtime = 'nodejs';

export async function POST() {
  try {
    const backendResponse = await postToBackend({
      path: '/ping',
      body: {},
    });

    if (backendResponse.ok) {
      return NextResponse.json({ ok: true }, { status: 200 });
    }

    return NextResponse.json(
      { ok: false, status: backendResponse.status },
      { status: backendResponse.status },
    );
  } catch (error) {
    logger.error({
      service: 'ping-api',
      message: 'Backend unreachable',
      meta: { error },
    });
    return NextResponse.json({ ok: false }, { status: 502 });
  }
}
