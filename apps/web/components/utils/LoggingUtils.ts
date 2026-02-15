type LogLevel = 'info' | 'warn' | 'error' | 'debug';

interface LogPayload {
  message: string;
  meta?: Record<string, unknown>;
  service?: string;
}

function serializeError(err: unknown) {
  if (err instanceof Error) {
    return {
      message: err.message,
      name: err.name,
    };
  }
  return { message: String(err) };
}

function log(level: LogLevel, { message, meta = {}, service = 'frontend' }: LogPayload) {
  const entry = {
    level,
    time: new Date().toISOString(),
    service,
    message,
    ...meta,
    ...(meta?.error ? { error: serializeError(meta.error) } : {}),
  };

  const output = JSON.stringify(entry);

  switch (level) {
    case 'error':
      console.error(output);
      break;
    case 'warn':
      console.warn(output);
      break;
    case 'debug':
      console.debug(output);
      break;
    default:
      console.log(output);
  }
}

export const logger = {
  info: (payload: LogPayload) => log('info', payload),
  warn: (payload: LogPayload) => log('warn', payload),
  error: (payload: LogPayload) => log('error', payload),
  debug: (payload: LogPayload) => log('debug', payload),
};
