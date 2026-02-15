import { logger } from '@/components/utils/LoggingUtils';

function getBackendBaseUrl(): string {
  const backendBaseUrl = process.env.BACKEND_BASE_URL;
  if (!backendBaseUrl) {
    throw new Error('Missing BACKEND_BASE_URL');
  }
  return backendBaseUrl.replace(/\/+$/, '');
}

function getBackendApiKey(): string {
  const backendApiKey = process.env.API_KEY;
  if (!backendApiKey) {
    throw new Error('Missing API_KEY');
  }
  return backendApiKey;
}

export async function postToBackend(args: { path: string; body: unknown }): Promise<Response> {
  const backendUrl = `${getBackendBaseUrl()}${args.path.startsWith('/') ? '' : '/'}${args.path}`;
  const backendApiKey = getBackendApiKey();

  logger.info({
    service: 'backend-client',
    message: 'Posting to backend',
    meta: { backendUrl, body: args.body },
  });

  return fetch(backendUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-API-KEY': backendApiKey,
    },
    body: JSON.stringify(args.body),
  });
}

export async function getStreamFromBackend(args: { pathWithQuery: string }): Promise<Response> {
  const backendUrl = `${getBackendBaseUrl()}${args.pathWithQuery.startsWith('/') ? '' : '/'}${args.pathWithQuery}`;
  const backendApiKey = getBackendApiKey();

  logger.info({
    service: 'backend-client',
    message: 'Posting stream to backend',
    meta: { backendUrl, body: args.pathWithQuery },
  });

  return fetch(backendUrl, {
    method: 'GET',
    headers: {
      'X-API-KEY': backendApiKey,
      Accept: 'text/event-stream',
      'Cache-Control': 'no-cache',
    },
  });
}
