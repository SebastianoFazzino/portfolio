'use client';

import React, { useEffect, useRef, useState } from 'react';

export function Chat() {
  const [question, setQuestion] = useState('');
  const [answer, setAnswer] = useState<string | null>(null);
  const [status, setStatus] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [dotCount, setDotCount] = useState(0);
  const [hasStreamStarted, setHasStreamStarted] = useState(false);

  const esRef = useRef<EventSource | null>(null);
  const streamStartedRef = useRef(false);

  useEffect(() => {
    return () => {
      esRef.current?.close();
      esRef.current = null;
    };
  }, []);

  useEffect(() => {
    if (!loading) return;
    const interval = setInterval(() => {
      setDotCount((prev) => (prev + 1) % 4);
    }, 400);

    return () => clearInterval(interval);
  }, [loading]);

  function clearQuestion() {
    if (loading) return;
    setQuestion('');
  }

  function cancel() {
    esRef.current?.close();
    esRef.current = null;
    setLoading(false);
    setStatus('Cancelled');
  }

  function ask() {
    const q = question.trim();
    if (!q || loading) return;

    esRef.current?.close();
    esRef.current = null;

    streamStartedRef.current = false;
    setHasStreamStarted(false);
    setAnswer(null);

    setStatus('Connecting…');
    setLoading(true);

    const es = new EventSource(`/api/knowledge?question=${encodeURIComponent(q)}`);
    esRef.current = es;

    es.addEventListener('status', (e) => {
      setStatus((e as MessageEvent).data);
    });

    es.addEventListener('backend_error', (e) => {
      const data = JSON.parse((e as MessageEvent).data) as { message: string };
      es.close();
      esRef.current = null;
      setLoading(false);
      setStatus(data.message);
    });

    es.addEventListener('token', (e) => {
      const raw = (e as MessageEvent).data as string;
      const chunk = (JSON.parse(raw) as { text: string }).text;

      if (!streamStartedRef.current) {
        streamStartedRef.current = true;
        setHasStreamStarted(true);
      }

      setAnswer((prev) => (prev ?? '') + chunk);
    });

    es.addEventListener('done', () => {
      es.close();
      esRef.current = null;
      setLoading(false);
      setStatus(null);
    });

    es.addEventListener('error', () => {
      es.close();
      esRef.current = null;
      setLoading(false);
      setStatus('Something went wrong');
    });
  }

  function onQuestionKeyDown(e: React.KeyboardEvent<HTMLTextAreaElement>) {
    if (e.key !== 'Enter') return;
    if (e.shiftKey) return;
    e.preventDefault();
    ask();
  }

  return (
    <div className="border border-white/10 rounded-lg p-4 sm:p-6 bg-black/40">
      <div className="mt-2 max-w-sm">
        <div className="flex items-start gap-2">
          <p className="chat text-xs text-white/50 leading-relaxed">
            This chat uses my personal knowledge base to answer questions about my experience,
            projects, and how I work.
          </p>

          <div className="relative group shrink-0">
            <button
              type="button"
              className="text-white/40 hover:text-white/70 focus:outline-none"
              aria-label="Chat disclaimer"
            >
              ⓘ
            </button>
            <div
              className="
                                absolute right-0 bottom-6 z-50
                                hidden w-72
                                rounded-md border border-white/10
                                bg-black/90 p-3
                                text-xs text-white/70
                                shadow-lg
                                group-hover:block
                            "
            >
              This assistant responds using my curated personal knowledge base.
              <br />
              Runs on my Raspberry Pi — response time can vary.
              <br />
              It does not collect, store, or retain personal information.
              <br />
              For direct communication, please use the &#34;connect&#34; form.
            </div>
          </div>
        </div>
      </div>

      <div className="mt-6 space-y-4">
        <textarea
          value={question}
          readOnly={loading}
          onChange={(e) => setQuestion(e.target.value)}
          onKeyDown={onQuestionKeyDown}
          placeholder="Ask me anything..."
          className={`
                        w-full min-h-20
                        rounded-md
                        resize-none
                        bg-black/60
                        border border-white/10
                        p-3
                        text-sm
                        text-white
                        placeholder-white/40
                        focus:outline-none
                        focus:ring-1
                        focus:ring-(--accent)
                        ${loading ? 'opacity-70' : ''}
                    `}
        />

        <div className="flex items-center gap-3">
          <button
            onClick={ask}
            disabled={!question.trim() || loading}
            className="
                            inline-flex items-center justify-center
                            rounded-md
                            bg-(--accent)
                            px-4 py-2
                            text-sm font-medium
                            text-black
                            disabled:opacity-50
                        "
          >
            Ask
          </button>

          {!!question && !loading && (
            <button
              onClick={clearQuestion}
              className="
                                inline-flex items-center justify-center
                                rounded-md
                                border border-white/10
                                bg-black/60
                                px-4 py-2
                                text-sm font-medium
                                text-white/80
                                hover:border-white/30 hover:text-white
                            "
            >
              Clear
            </button>
          )}

          {loading && (
            <button
              onClick={cancel}
              className="
                                inline-flex items-center justify-center
                                rounded-md
                                border border-white/10
                                bg-black/60
                                px-4 py-2
                                text-sm font-medium
                                text-white/80
                                hover:border-rose-500 hover:text-rose-500
                            "
            >
              Cancel
            </button>
          )}

          {status && (
            <span className="text-xs text-white/50">
              {status}
              {loading && status.toLowerCase().includes('generating') ? '.'.repeat(dotCount) : null}
            </span>
          )}
        </div>

        {hasStreamStarted && answer !== null && (
          <div className="mt-4 rounded-md border border-white/10 bg-black/60 p-4">
            <p className="text-sm text-white/80 whitespace-pre-wrap">{answer}</p>
          </div>
        )}
      </div>
    </div>
  );
}
